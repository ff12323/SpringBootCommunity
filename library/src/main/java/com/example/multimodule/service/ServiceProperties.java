package com.example.multimodule.service;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("service")
public class ServiceProperties {

    private String message;

    private String zyhInfo;

    public String getZyhInfo() {
        return zyhInfo;
    }

    public void setZyhInfo(String zyhInfo) {
        this.zyhInfo = zyhInfo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
