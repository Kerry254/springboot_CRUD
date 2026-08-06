package com.example.springboot_backend.controller;

import com.example.springboot_backend.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    void getEmployees_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEmployees_withUserRole_returns200() throws Exception {
        when(employeeRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createEmployee_withUserRole_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/employees")
                        .contentType("application/json")
                        .content("""
                                {"firstName":"Jane","lastName":"Doe","emailId":"jane@example.com"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEmployeeById_withNonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/employees/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_asAdmin_withMissingId_returns404() throws Exception {
        when(employeeRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(delete("/api/v1/employees/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
