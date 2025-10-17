package com.api.erpdistribuidora.service.storage;

import com.api.erpdistribuidora.config.SupabaseProps;
import com.api.erpdistribuidora.dto.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private static final Logger log = LoggerFactory.getLogger(SupabaseStorageService.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;                 // https://<PROJECT_ID>.supabase.co
    private final String apiKey;                  // service-role key (sanitizada e validada)
    private final String bucket;                  // p.ex. "imagens"
    private final boolean publicBucket;           // true => URL pública; false => assinada
    private final int signedUrlExpSeconds;        // validade URL assinada
    private final String cacheControl;            // ex.: "public, max-age=31536000, immutable"

    public SupabaseStorageService(SupabaseProps props) {
        // validações claras
        if (!StringUtils.hasText(props.getUrl()) || !props.getUrl().startsWith("http")) {
            throw new IllegalStateException("supabase.url inválida (ex.: https://<PROJECT_ID>.supabase.co)");
        }
        if (!StringUtils.hasText(props.getServiceRoleKey())) {
            throw new IllegalStateException("supabase.serviceRoleKey ausente (defina SUPABASE_SERVICE_ROLE_KEY ou --supabase.serviceRoleKey=...)");
        }
        if (!StringUtils.hasText(props.getBucket())) {
            throw new IllegalStateException("supabase.bucket ausente.");
        }

        this.baseUrl = props.getUrl().replaceAll("/+$", "");
        this.apiKey = ensureServiceRole(sanitizeAndValidateKey(props.getServiceRoleKey())); // valida que é service_role
        this.bucket = props.getBucket();
        this.publicBucket = props.isPublicBucket();
        this.signedUrlExpSeconds = Math.max(1, props.getSignedUrlExpSeconds());
        this.cacheControl = props.getCacheControl();

        // RestTemplate com timeouts sensatos
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(12_000); // +tolerância
        rf.setReadTimeout(30_000);
        this.restTemplate = new RestTemplate(rf);

        // Log seguro (não vaza segredo)
        log.info("SupabaseStorageService inicializado: baseUrl={}, bucket={}, jwtParts={}",
                this.baseUrl, this.bucket, (this.apiKey.split("\\.").length));
    }

    public UploadResponse uploadImage(MultipartFile file) {
        validateImage(file);
        String ext = getExtension(file.getOriginalFilename());
        String objectPath = buildObjectPath(ext);

        putObject(objectPath, file);

        String url = publicBucket ? publicUrl(objectPath) : signUrl(objectPath, signedUrlExpSeconds);
        return new UploadResponse(url, objectPath);
    }

    /* =================== helpers =================== */

    private String sanitizeAndValidateKey(String raw) {
        String k = raw.trim().replace("\r", "").replace("\n", "");
        // Alguns gerenciadores de segredo inserem aspas acidentais
        if (k.startsWith("\"") && k.endsWith("\"") && k.length() > 1) {
            k = k.substring(1, k.length() - 1);
        }
        if (k.startsWith("'") && k.endsWith("'") && k.length() > 1) {
            k = k.substring(1, k.length() - 1);
        }
        // JWT/JWS "compact" deve ter 3 partes
        if (k.split("\\.").length != 3) {
            throw new IllegalStateException("supabase.serviceRoleKey com formato inválido (esperado JWS em 3 partes).");
        }
        return k;
    }

    private String ensureServiceRole(String key) {
        String[] parts = key.split("\\.");
        if (parts.length != 3) {
            throw new IllegalStateException("JWT inválido (esperado 3 partes).");
        }
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        // service role tem claim: "role":"service_role"
        if (!payloadJson.contains("\"role\":\"service_role\"")) {
            throw new IllegalStateException(
                    "A chave fornecida NÃO é service_role. Payload detectado: " + payloadJson
            );
        }
        return key;
    }

    private String buildObjectPath(String ext) {
        int y = Instant.now().atZone(java.time.ZoneId.of("UTC")).getYear();
        int m = Instant.now().atZone(java.time.ZoneId.of("UTC")).getMonthValue();
        if (!StringUtils.hasText(ext)) ext = "bin";
        String name = UUID.randomUUID().toString();
        return "images/%d/%02d/%s.%s".formatted(y, m, name, ext);
    }

    private void putObject(String objectPath, MultipartFile file) {
        if (!StringUtils.hasText(objectPath)) throw new IllegalArgumentException("objectPath vazio.");
        String encodedPath = encodePath(objectPath);
        String url = baseUrl + "/storage/v1/object/" + bucket + "/" + encodedPath;

        HttpHeaders headers = commonHeaders();
        headers.set("x-upsert", "true");
        headers.setContentType(detectMediaType(file));
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        if (StringUtils.hasText(cacheControl)) {
            headers.set("Cache-Control", cacheControl);
        }

        try {
            byte[] bytes = file.getBytes();
            HttpEntity<byte[]> entity = new HttpEntity<>(bytes, headers);
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Falha no upload: " + resp.getStatusCode() + " body=" + resp.getBody());
            }
        } catch (RestClientResponseException e) {
            // Respostas HTTP do Supabase (>=400) – mantém corpo para debug
            throw new RuntimeException(
                    "Upload falhou: HTTP %d, body=%s".formatted(e.getRawStatusCode(), e.getResponseBodyAsString()), e);
        } catch (ResourceAccessException e) {
            // Problemas de rede/SSL/DNS/timeout
            throw new RuntimeException("Falha de conexão ao Supabase: " + e.getMessage(), e);
        } catch (RestClientException e) {
            // Outras falhas do cliente REST
            throw new RuntimeException("Erro REST ao chamar Supabase: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro enviando arquivo ao Supabase: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    private String publicUrl(String objectPath) {
        String encodedPath = encodePath(objectPath);
        return baseUrl + "/storage/v1/object/public/" + bucket + "/" + encodedPath;
    }

    @SuppressWarnings("unchecked")
    private String signUrl(String objectPath, int expiresInSeconds) {
        String encodedPath = encodePath(objectPath);
        String url = baseUrl + "/storage/v1/object/sign/" + bucket + "/" + encodedPath;

        HttpHeaders headers = commonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        String body = "{\"expiresIn\":" + expiresInSeconds + "}";

        try {
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null || !resp.getBody().containsKey("signedURL")) {
                throw new RuntimeException("Falha ao gerar URL assinada: status=" + resp.getStatusCode() + " body=" + resp.getBody());
            }
            String pathWithToken = String.valueOf(resp.getBody().get("signedURL")); // "/object/sign/..."
            return baseUrl + "/storage/v1" + pathWithToken;
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Sign falhou: HTTP %d, body=%s"
                    .formatted(e.getRawStatusCode(), e.getResponseBodyAsString()), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Falha de conexão ao Supabase (sign): " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Erro REST ao chamar Supabase (sign): " + e.getMessage(), e);
        }
    }

    private HttpHeaders commonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", apiKey);
        headers.setBearerAuth(apiKey); // Authorization: Bearer <service-role>
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
            allowed = ct.startsWith("image/"); // fallback permissivo para variantes
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

    public boolean deleteImage(String path) {
        try {
            // Ajuste se sua classe já tiver helpers para montar URL/headers
            String endpoint = props.getUrl() + "/storage/v1/object/"
                    + URLEncoder.encode(props.getBucket(), StandardCharsets.UTF_8) + "/"
                    + URLEncoder.encode(path, StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + props.getServiceRoleKey());
            headers.set("apikey", props.getServiceRoleKey());

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Void> resp = restTemplate.exchange(endpoint, HttpMethod.DELETE, entity, Void.class);
            return resp.getStatusCode().is2xxSuccessful();
        } catch (RestClientResponseException e) {
            log.error("Delete falhou ({}): {}", path, e.getResponseBodyAsString(), e);
            return false;
        } catch (RestClientException e) {
            log.error("Delete falhou (erro genérico) para {}", path, e);
            return false;
        }
    }
}
