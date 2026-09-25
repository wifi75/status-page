package eu.iu3cyv.status.web;

import eu.iu3cyv.status.config.StatusProperties;
import eu.iu3cyv.status.monitor.MonitorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatusController {

    private final MonitorService monitorService;
    private final StatusProperties properties;

    public StatusController(MonitorService monitorService, StatusProperties properties) {
        this.monitorService = monitorService;
        this.properties = properties;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("siteTitle", properties.siteTitle());
        model.addAttribute("monitors", monitorService.publicMonitors());
        return "index";
    }
}
