package com.github.Lexya06.startrestapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="server.api")
public class ApiProperties {
    @Getter
    @Setter
    private BasePath basePath;
    public static class BasePath{
        @Getter
        final String v1;

        @Getter
        final String v2;
        public BasePath(String v1, String v2) {
            this.v1 = v1;
            this.v2 = v2;
        }
    }

}
