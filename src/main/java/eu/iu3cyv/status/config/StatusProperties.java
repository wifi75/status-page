package eu.iu3cyv.status.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "status")
public record StatusProperties(String siteTitle, String badge, Admin admin) {

    public record Admin(String username, String passwordHash) {
    }
}
