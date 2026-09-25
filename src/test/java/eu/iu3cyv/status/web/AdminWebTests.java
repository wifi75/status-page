package eu.iu3cyv.status.web;

import eu.iu3cyv.status.monitor.MonitorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class AdminWebTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MonitorRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void createdMonitorAppearsOnPublicPageWithoutTarget() throws Exception {
        mvc.perform(post("/admin/monitors").with(csrf())
                        .param("name", "Sito di prova")
                        .param("type", "HTTP")
                        .param("target", "https://interno.esempio.it/segreto")
                        .param("intervalSeconds", "60")
                        .param("enabled", "true")
                        .param("displayOrder", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        assertThat(repository.findAll()).hasSize(1);

        mvc.perform(get("/"))
                .andExpect(content().string(containsString("Sito di prova")))
                .andExpect(content().string(not(containsString("interno.esempio.it"))));
    }

    @Test
    void invalidTargetIsRejectedWithMessage() throws Exception {
        mvc.perform(post("/admin/monitors").with(csrf())
                        .param("name", "Porta")
                        .param("type", "TCP")
                        .param("target", "senza-porta")
                        .param("intervalSeconds", "60")
                        .param("displayOrder", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Formato atteso host:porta")));

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void postWithoutCsrfIsRefused() throws Exception {
        mvc.perform(post("/admin/monitors").param("name", "x"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unknownMonitorGives404() throws Exception {
        mvc.perform(get("/admin/monitors/999")).andExpect(status().isNotFound());
    }
}
