package eu.iu3cyv.status.web;

import eu.iu3cyv.status.config.StatusProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StatusProperties properties;

    public AdminController(StatusProperties properties) {
        this.properties = properties;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("siteTitle", properties.siteTitle());
        return "admin/dashboard";
    }
}
