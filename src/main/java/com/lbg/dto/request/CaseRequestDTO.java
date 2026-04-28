package com.lbg.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload to open a new investigation case")
public class CaseRequestDTO {

    @NotEmpty(message = "At least one alert ID must be linked")
    @Schema(description = "Alert references to link", example = "[\"ALT-00001\",\"ALT-00020\"]")
    private List<String> linkedAlertIds;

    @NotBlank(message = "Customer ID must not be blank")
    @Schema(example = "CUST-1042")
    private String customerId;

    @NotNull(message = "Priority must not be null")
    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "Priority must be HIGH, MEDIUM, or LOW")
    @Schema(example = "HIGH")
    private String priority;

    @NotBlank(message = "Assigned analyst must not be blank")
    @Schema(example = "j.rahman")
    private String assignedAnalyst;
}