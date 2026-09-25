package eu.iu3cyv.status.web;

import eu.iu3cyv.status.monitor.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;

@Controller
public class StatusController {

    private final DashboardService dashboardService;

    public StatusController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dashboard", dashboardService.build(Instant.now()));
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
