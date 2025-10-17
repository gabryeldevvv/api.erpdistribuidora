package com.api.erpdistribuidora.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "supabase")
public class SupabaseProps {

    /**
     * Ex.: https://<PROJECT_ID>.supabase.co
     */
    private String url;

    /**
     * Service role key (NÃO commitar).
     * Ideal: vir de env -> SUPABASE_SERVICE_ROLE_KEY
     */
    private String serviceRoleKey;

    /**
     * Nome do bucket de storage (ex.: "imagens")
     */
    private String bucket;

    /**
     * Se true, retorna URL pública; se false, gera URL assinada
     */
    private boolean publicBucket = true;

    /**
     * Expiração da URL assinada (em segundos)
     */
    private int signedUrlExpSeconds = 3600;

    /**
     * Cabeçalho Cache-Control para objetos (opcional)
     * Ex.: "public, max-age=31536000, immutable"
     */
    private String cacheControl = "public, max-age=31536000, immutable";

    // ===== getters/setters =====
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getServiceRoleKey() { return serviceRoleKey; }
    public void setServiceRoleKey(String serviceRoleKey) { this.serviceRoleKey = serviceRoleKey; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public boolean isPublicBucket() { return publicBucket; }
    public void setPublicBucket(boolean publicBucket) { this.publicBucket = publicBucket; }

    public int getSignedUrlExpSeconds() { return signedUrlExpSeconds; }
    public void setSignedUrlExpSeconds(int signedUrlExpSeconds) { this.signedUrlExpSeconds = signedUrlExpSeconds; }

    public String getCacheControl() { return cacheControl; }
    public void setCacheControl(String cacheControl) { this.cacheControl = cacheControl; }

    // ===== Compatibilidade opcional com "supabase.key" =====
    /**
     * @deprecated use {@link #getServiceRoleKey()}
     */
    @Deprecated
    public String getKey() { return serviceRoleKey; }

    /**
     * @deprecated use {@link #setServiceRoleKey(String)}
     */
    @Deprecated
    public void setKey(String key) { this.serviceRoleKey = key; }
}
