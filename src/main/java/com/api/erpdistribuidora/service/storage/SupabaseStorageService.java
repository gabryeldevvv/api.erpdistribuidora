package com.api.erpdistribuidora.service.storage;

import com.api.erpdistribuidora.config.SupabaseProps;
import com.api.erpdistribuidora.dto.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private static final Logger log = LoggerFactory.getLogger(SupabaseStorageService.class);

    private final RestTemplate restTemplate;
    private final SupabaseProps props;

    public SupabaseStorageService(SupabaseProps props) {
        this.props = props;

        // RestTemplate com timeouts para não travar requisições
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(8_000);
        rf.setReadTimeout(20_000);
        this.restTemplate = new RestTemplate(rf);

        // Log leve (sem expor a key) – não derruba o app
        log.info("Supabase cfg: url={}, bucket={}, publicBucket={}, exp={}",
                safe(props.getUrl()), props.getBucket(), props.isPublicBucket(), props.getSignedUrlExpSeconds());
    }

    public UploadResponse uploadImage(MultipartFile file) {
        // validação de configuração só aqui (na chamada), não no boot
        assertConfigured();

        validateImage(file);
        String ext = getExtension(file.getOriginalFilename());
        String objectPath = buildObjectPath(ext);

        putObject(objectPath, file);

        String url = props.isPublicBucket() ? publicUrl(objectPath) : signUrl(objectPath, props.getSignedUrlExpSeconds());
        return new UploadResponse(url, objectPath);
    }

    /* =================== helpers =================== */

    private void assertConfigured() {
        if (!StringUtils.hasText(props.getUrl()) || !props.getUrl().startsWith("http")) {
            throw new IllegalStateException("Configuração inválida: supabase.url (esperado: https://<PROJECT_ID>.supabase.co)");
        }
        if (!StringUtils.hasText(props.getKey())) {
            throw new IllegalStateException("Configuração inválida: supabase.key (defina SUPABASE_SERVICE_ROLE_KEY ou --supabase.key=...)");
        }
        if (!StringUtils.hasText(props.getBucket())) {
            throw new IllegalStateException("Configuração inválida: supabase.bucket");
        }
    }

    private String buildObjectPath(String ext) {
        int y = Instant.now().atZone(java.time.ZoneId.of("UTC")).getYear();
        int m = Instant.now().atZone(java.time.ZoneId.of("UTC")).getMonthValue();
        if (!StringUtils.hasText(ext)) ext = "bin";
        String name = UUID.randomUUID().toString();
        return "images/%d/%02d/%s.%s".formatted(y, m, name, ext);
    }

    private void putObject(String objectPath, MultipartFile file) {
        String encodedPath = encodePath(objectPath);
        String url = normalizedBaseUrl() + "/storage/v1/object/" + props.getBucket() + "/" + encodedPath;

        HttpHeaders headers = commonHeaders();
        headers.set("x-upsert", "true");
        headers.setContentType(detectMediaType(file));
        if (StringUtils.hasText(props.getCacheControl())) {
            headers.set("Cache-Control", props.getCacheControl());
        }

        try {
            byte[] bytes = file.getBytes();
            HttpEntity<byte[]> entity = new HttpEntity<>(bytes, headers);
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Falha no upload: " + resp.getStatusCode() + " body=" + resp.getBody());
            }
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Upload falhou: HTTP %d, body=%s"
                    .formatted(e.getRawStatusCode(), e.getResponseBodyAsString()), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro enviando arquivo ao Supabase", e);
        }
    }

    private String publicUrl(String objectPath) {
        String encodedPath = encodePath(objectPath);
        return normalizedBaseUrl() + "/storage/v1/object/public/" + props.getBucket() + "/" + encodedPath;
    }

    @SuppressWarnings("unchecked")
    private String signUrl(String objectPath, int expiresInSeconds) {
        String encodedPath = encodePath(objectPath);
        String url = normalizedBaseUrl() + "/storage/v1/object/sign/" + props.getBucket() + "/" + encodedPath;

        HttpHeaders headers = commonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"expiresIn\":" + Math.max(1, expiresInSeconds) + "}";

        try {
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null || !resp.getBody().containsKey("signedURL")) {
                throw new RuntimeException("Falha ao gerar URL assinada: status=" + resp.getStatusCode() + " body=" + resp.getBody());
            }
            String pathWithToken = String.valueOf(resp.getBody().get("signedURL")); // "/object/sign/..."
            return normalizedBaseUrl() + "/storage/v1" + pathWithToken;
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Sign falhou: HTTP %d, body=%s"
                    .formatted(e.getRawStatusCode(), e.getResponseBodyAsString()), e);
        }
    }

    private HttpHeaders commonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", props.getKey());
        headers.setBearerAuth(props.getKey());
        return headers;
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Arquivo vazio.");
        String ct = (file.getContentType() != null) ? file.getContentType().toLowerCase() : "";

        boolean allowed =
                "image/jpeg".equals(ct) || "image/jpg".equals(ct) ||
                        "image/png".equals(ct)  || "image/gif".equals(ct) ||
                        "image/webp".equals(ct);

        if (!allowed && StringUtils.hasText(ct)) {
            allowed = ct.startsWith("image/");
        }
        if (!allowed) throw new IllegalArgumentException("Tipo de imagem não suportado: " + ct);

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Imagem maior que 10MB.");
        }
    }

    private MediaType detectMediaType(MultipartFile file) {
        try {
            String ct = file.getContentType();
            if (StringUtils.hasText(ct)) return MediaType.parseMediaType(ct);
        } catch (Exception ignored) {}
        String ext = getExtension(file.getOriginalFilename());
        return switch (ext) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png"        -> MediaType.IMAGE_PNG;
            case "gif"        -> MediaType.IMAGE_GIF;
            case "webp"       -> MediaType.parseMediaType("image/webp");
            default           -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) return "";
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return "jpeg".equals(ext) ? "jpg" : ext;
    }

    private String encodePath(String path) {
        String[] parts = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append('/');
            sb.append(URLEncoder.encode(parts[i], StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private String normalizedBaseUrl() {
        String url = props.getUrl();
        return (url == null) ? "" : url.replaceAll("/+$", "");
    }

    private String safe(String v) {
        return (v == null || v.isBlank()) ? "<vazio>" : v;
    }
}
