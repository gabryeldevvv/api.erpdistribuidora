package com.api.erpdistribuidora.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "supabase")
public class SupabaseProps {
    @NotBlank
    private String url;          // ex.: https://<PROJECT_ID>.supabase.co
    @NotBlank
    private String key;          // service role key (somente backend)
    @NotBlank
    private String bucket;       // ex.: imagens

    private boolean publicBucket = true;
    private int signedUrlExpSeconds = 3600;
    private String cacheControl = "";  // ex.: "public, max-age=31536000, immutable"

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public boolean isPublicBucket() { return publicBucket; }
    public void setPublicBucket(boolean publicBucket) { this.publicBucket = publicBucket; }

    public int getSignedUrlExpSeconds() { return signedUrlExpSeconds; }
    public void setSignedUrlExpSeconds(int signedUrlExpSeconds) { this.signedUrlExpSeconds = signedUrlExpSeconds; }

    public String getCacheControl() { return cacheControl; }
    public void setCacheControl(String cacheControl) { this.cacheControl = cacheControl; }
}
