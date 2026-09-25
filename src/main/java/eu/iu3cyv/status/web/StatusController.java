package eu.iu3cyv.status.web;

import eu.iu3cyv.status.monitor.MonitorService;
import eu.iu3cyv.status.monitor.MonitorStatus;
import eu.iu3cyv.status.monitor.MonitorView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StatusController {

    private final MonitorService monitorService;

    public StatusController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping("/")
    public String index(Model model) {
        var monitors = monitorService.publicMonitors();
        model.addAttribute("monitors", monitors);
        model.addAttribute("summary", summary(monitors));
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** Stato complessivo: il peggiore tra i servizi già controllati. */
    static Summary summary(List<MonitorView> monitors) {
        if (monitors.isEmpty()) {
            return new Summary("is-idle", "Nessun servizio configurato.");
        }
        long down = monitors.stream().filter(m -> m.status() == MonitorStatus.DOWN).count();
        if (down > 0) {
            return new Summary("is-down", down == 1 ? "Un servizio non è raggiungibile" : down + " servizi non sono raggiungibili");
        }
        if (monitors.stream().anyMatch(m -> m.status() == MonitorStatus.DEGRADED)) {
            return new Summary("is-warn", "Alcuni servizi hanno problemi");
        }
        if (monitors.stream().allMatch(m -> m.status() == MonitorStatus.PENDING)) {
            return new Summary("is-idle", "In attesa dei primi controlli.");
        }
        return new Summary("is-ok", "Tutti i servizi sono operativi");
    }

    public record Summary(String cssClass, String text) {
    }
}
