package com.jobtracker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.api.model.ApplicationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class JobApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createApplication(String company, String role, ApplicationStatus status) throws Exception {
        Map<String, Object> body = Map.of(
                "company", company,
                "role", role,
                "status", status.name(),
                "dateApplied", "2024-01-15"
        );
        MvcResult result = mockMvc.perform(post("/api/applications")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void createApplication_returnsCreated() throws Exception {
        Map<String, Object> body = Map.of(
                "company", "Acme Corp",
                "role", "Software Engineer",
                "status", "APPLIED",
                "dateApplied", "2024-01-15",
                "location", "Remote"
        );
        mockMvc.perform(post("/api/applications")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.company").value("Acme Corp"))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void filterByStatus_returnsMatchingApplications() throws Exception {
        createApplication("Company A", "Engineer", ApplicationStatus.APPLIED);
        createApplication("Company B", "Manager", ApplicationStatus.INTERVIEW);

        mockMvc.perform(get("/api/applications?status=APPLIED")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].status", everyItem(is("APPLIED"))));
    }

    @Test
    void validStatusTransition_succeeds() throws Exception {
        String id = createApplication("TechCo", "Developer", ApplicationStatus.APPLIED);

        mockMvc.perform(patch("/api/applications/" + id + "/status")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SCREENING\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SCREENING"));
    }

    @Test
    void invalidStatusTransition_returns409() throws Exception {
        String id = createApplication("TechCo", "Developer", ApplicationStatus.REJECTED);

        mockMvc.perform(patch("/api/applications/" + id + "/status")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPLIED\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void search_returnsMatchingResults() throws Exception {
        createApplication("Google", "Backend Engineer", ApplicationStatus.APPLIED);
        createApplication("Facebook", "Frontend Developer", ApplicationStatus.APPLIED);

        mockMvc.perform(get("/api/applications/search?query=Google")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].company").value("Google"));
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteApplication_returns204() throws Exception {
        String id = createApplication("DeleteMe Inc", "QA Engineer", ApplicationStatus.APPLIED);

        mockMvc.perform(delete("/api/applications/" + id)
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/applications/" + id)
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSummary_returnsAllStatuses() throws Exception {
        mockMvc.perform(get("/api/applications/summary")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.APPLIED").exists())
                .andExpect(jsonPath("$.REJECTED").exists())
                .andExpect(jsonPath("$.OFFER").exists());
    }
}
