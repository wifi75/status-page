package eu.iu3cyv.status.web;

import eu.iu3cyv.status.monitor.MonitorForm;
import eu.iu3cyv.status.monitor.MonitorService;
import eu.iu3cyv.status.monitor.MonitorType;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MonitorService monitorService;

    public AdminController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @ModelAttribute("types")
    MonitorType[] types() {
        return MonitorType.values();
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("monitors", monitorService.allMonitors());
        return "admin/dashboard";
    }

    @GetMapping("/monitors/new")
    public String newForm(Model model) {
        model.addAttribute("form", MonitorForm.empty());
        model.addAttribute("monitorId", null);
        return "admin/monitor-form";
    }

    @PostMapping("/monitors")
    public String create(@Valid @ModelAttribute("form") MonitorForm form, BindingResult errors,
                         Model model, RedirectAttributes redirect) {
        validateTarget(form, errors);
        if (errors.hasErrors()) {
            model.addAttribute("monitorId", null);
            return "admin/monitor-form";
        }
        monitorService.create(form);
        redirect.addFlashAttribute("notice", "Servizio \"" + form.name().strip() + "\" aggiunto");
        return "redirect:/admin";
    }

    @GetMapping("/monitors/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", MonitorForm.of(monitorService.get(id)));
        model.addAttribute("monitorId", id);
        return "admin/monitor-form";
    }

    @PostMapping("/monitors/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") MonitorForm form,
                         BindingResult errors, Model model, RedirectAttributes redirect) {
        validateTarget(form, errors);
        if (errors.hasErrors()) {
            model.addAttribute("monitorId", id);
            return "admin/monitor-form";
        }
        monitorService.update(id, form);
        redirect.addFlashAttribute("notice", "Servizio \"" + form.name().strip() + "\" aggiornato");
        return "redirect:/admin";
    }

    @PostMapping("/monitors/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        var name = monitorService.get(id).getName();
        monitorService.delete(id);
        redirect.addFlashAttribute("notice", "Servizio \"" + name + "\" eliminato");
        return "redirect:/admin";
    }

    /** Il formato del target dipende dal tipo: controllo incrociato che Bean Validation da solo non fa. */
    private static void validateTarget(MonitorForm form, BindingResult errors) {
        if (form.type() == null || form.target() == null || errors.hasFieldErrors("target")) {
            return;
        }
        var error = form.type().validateTarget(form.target().strip());
        if (error != null) {
            errors.rejectValue("target", "invalid", error);
        }
    }
}
