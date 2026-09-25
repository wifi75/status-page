package eu.iu3cyv.status.monitor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dati del modulo admin per creare o modificare un servizio. */
public record MonitorForm(
        @NotBlank(message = "Il nome è obbligatorio")
        @Size(max = 100, message = "Massimo 100 caratteri")
        String name,

        @NotNull(message = "Scegli il tipo di controllo")
        MonitorType type,

        @NotBlank(message = "L'indirizzo è obbligatorio")
        @Size(max = 500, message = "Massimo 500 caratteri")
        String target,

        @NotNull(message = "Indica ogni quanti secondi controllare")
        @Min(value = 30, message = "Minimo 30 secondi")
        @Max(value = 3600, message = "Massimo 3600 secondi")
        Integer intervalSeconds,

        // checkbox non spuntata = campo assente = null
        Boolean enabled,

        @NotNull(message = "Indica l'ordine")
        @Min(value = 0, message = "Non può essere negativo")
        Integer displayOrder
) {

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    public static MonitorForm empty() {
        return new MonitorForm("", MonitorType.HTTP, "", 60, true, 0);
    }

    public static MonitorForm of(Monitor m) {
        return new MonitorForm(m.getName(), m.getType(), m.getTarget(), m.getIntervalSeconds(), m.isEnabled(), m.getDisplayOrder());
    }
}
