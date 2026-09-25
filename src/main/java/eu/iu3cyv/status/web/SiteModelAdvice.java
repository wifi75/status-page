package eu.iu3cyv.status.web;

import eu.iu3cyv.status.config.StatusProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Dati comuni a tutte le pagine. */
@ControllerAdvice
public class SiteModelAdvice {

    private final StatusProperties properties;

    public SiteModelAdvice(StatusProperties properties) {
        this.properties = properties;
    }

    @ModelAttribute("siteTitle")
    String siteTitle() {
        return properties.siteTitle();
    }
}
