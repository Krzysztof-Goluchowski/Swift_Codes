package krzysztof.goluchowski.swiftcodes.controller;

import krzysztof.goluchowski.swiftcodes.dto.HeadquarterWithBranchesDto;
import krzysztof.goluchowski.swiftcodes.service.SwiftCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SwiftCodeController.class)
class SwiftCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SwiftCodeService service;

    @Test
    void shouldReturnHeadquarterWithBranches() throws Exception {
        // Given
        String swiftCode = "TESTBANKXXX";
        HeadquarterWithBranchesDto dto = new HeadquarterWithBranchesDto(
                "Address, Town, Something", "Bank HQ", "PL", "Poland", false, swiftCode, List.of());

        when(service.getHeadquarterWithBranches(swiftCode)).thenReturn(Optional.of(dto));

        // When & Then
        mockMvc.perform(get("/v1/swift-codes/{swiftCode}", swiftCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value(swiftCode));
    }

    @Test
    void shouldReturnNotFoundForUnknownSwiftCode() throws Exception {
        // Given
        String swiftCode = "UNKNOWNXXX";
        when(service.getHeadquarterWithBranches(swiftCode)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/v1/swift-codes/{swiftCode}", swiftCode))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("SWIFT code not found"));
    }

    @Test
    void shouldAddSwiftCodeSuccessfully() throws Exception {
        // Given
        String requestJson = """
                {
                    "address": "Some address",
                    "bankName": "Bank",
                    "countryISO2": "PL",
                    "countryName": "Poland",
                    "isHeadquarter": true,
                    "swiftCode": "TESTBANK123"
                }
                """;

        when(service.addSwiftCode(any())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("SWIFT code added successfully"));
    }

    @Test
    void shouldReturnConflictWhenSwiftCodeAlreadyExists() throws Exception {
        // Given
        String requestJson = """
                {
                    "address": "Some address",
                    "bankName": "Bank",
                    "countryISO2": "PL",
                    "countryName": "Poland",
                    "isHeadquarter": true,
                    "swiftCode": "TESTBANK123"
                }
                """;

        when(service.addSwiftCode(any())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("SWIFT code already exists"));
    }

    @Test
    void shouldDeleteSwiftCodeSuccessfully() throws Exception {
        // Given
        String swiftCode = "TESTBANK123";
        when(service.deleteSwiftCode(swiftCode)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/v1/swift-codes/{swiftCode}", swiftCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SWIFT code deleted successfully"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownSwiftCode() throws Exception {
        // Given
        String swiftCode = "UNKNOWN";
        when(service.deleteSwiftCode(swiftCode)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/v1/swift-codes/{swiftCode}", swiftCode))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("SWIFT code not found"));
    }
}
