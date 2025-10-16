package com.api.erpdistribuidora.service.storage;

import com.api.erpdistribuidora.dto.UploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final String apiKey;
    private final String bucket;
    private final boolean publicBucket;
    private final int signedUrlExpSeconds;

    public SupabaseStorageService(
            @Value("${supabase.url}") String baseUrl,
            @Value("${supabase.key}") String apiKey,
            @Value("${supabase.bucket}") String bucket,
            @Value("${supabase.public-bucket:true}") boolean publicBucket,
            @Value("${supabase.signed-url-exp-seconds:3600}") int signedUrlExpSeconds
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.bucket = bucket;
        this.publicBucket = publicBucket;
        this.signedUrlExpSeconds = signedUrlExpSeconds;
    }

    public UploadResponse uploadImage(MultipartFile file) {
        validateImage(file);

        String ext = getExtension(file.getOriginalFilename());
        String objectPath = "images/%d/%02d/%s.%s".formatted(
                Instant.now().atZone(java.time.ZoneId.of("UTC")).getYear(),
                Instant.now().atZone(java.time.ZoneId.of("UTC")).getMonthValue(),
                UUID.randomUUID(),
                ext
        );

        putObject(objectPath, file);

        String url = publicBucket ? publicUrl(objectPath) : signUrl(objectPath, signedUrlExpSeconds);
        return new UploadResponse(url, objectPath);
    }

    /* =================== helpers =================== */

    private void putObject(String objectPath, MultipartFile file) {
        String encodedPath = encodePath(objectPath);
        String url = baseUrl + "/storage/v1/object/" + bucket + "/" + encodedPath;

        HttpHeaders headers = commonHeaders();
        headers.set("x-upsert", "true");
        headers.setContentType(detectMediaType(file));

        try {
            byte[] bytes = file.getBytes();
            HttpEntity<byte[]> entity = new HttpEntity<>(bytes, headers);
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Falha no upload: " + resp.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro enviando arquivo ao Supabase", e);
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
        String body = "{\"expiresIn\":" + expiresInSeconds + "}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null || !resp.getBody().containsKey("signedURL")) {
            throw new RuntimeException("Falha ao gerar URL assinada");
        }
        String pathWithToken = (String) resp.getBody().get("signedURL"); // retorna "/object/sign/..."
        return baseUrl + "/storage/v1" + pathWithToken;
    }

    private HttpHeaders commonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", apiKey);
        headers.setBearerAuth(apiKey);
        return headers;
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Arquivo vazio.");

        String ct = file.getContentType();
        if (!StringUtils.hasText(ct)) throw new IllegalArgumentException("Content-Type ausente.");
        String ctL = ct.toLowerCase();

        boolean ok =
                ctL.equals("image/jpeg") ||
                        ctL.equals("image/jpg")  ||
                        ctL.equals("image/png")  ||
                        ctL.equals("image/gif")  ||
                        ctL.equals("image/webp");

        if (!ok) throw new IllegalArgumentException("Tipo de imagem não suportado: " + ct);

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Imagem maior que 10MB.");
        }
    }

    private MediaType detectMediaType(MultipartFile file) {
        try { return MediaType.parseMediaType(file.getContentType()); }
        catch (Exception e) { return MediaType.APPLICATION_OCTET_STREAM; }
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) return "bin";
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return "jpeg".equals(ext) ? "jpg" : ext;
    }

    private String encodePath(String path) {
        // preserva '/' e escapa caracteres especiais por segmento
        String[] parts = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append('/');
            sb.append(URLEncoder.encode(parts[i], StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
}
