package com.techblog.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "xss")
@Data
public class XssProperties {
    private volatile String mode = "vuln";
    
    public boolean isVuln() {
        return "vuln".equalsIgnoreCase(mode);
    }
    
    public boolean isSecure() {
        return "secure".equalsIgnoreCase(mode);
    }
    
    public void setMode(String mode) {
        if ("vuln".equalsIgnoreCase(mode) || "secure".equalsIgnoreCase(mode)) {
            this.mode = mode.toLowerCase();
        }
    }
}
