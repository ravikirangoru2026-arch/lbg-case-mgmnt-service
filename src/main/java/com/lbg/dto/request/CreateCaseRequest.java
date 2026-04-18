package com.lbg.dto.request;

import com.lbg.enums.CasePriority;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateCaseRequest {

    @NotBlank(message = "customerId is required")
    @Size(max = 20, message = "customerId must not exceed 20 characters")
    private String customerId;

    @NotNull(message = "priority is required")
    private CasePriority priority;

    @NotBlank(message = "assignedAnalyst is required")
    @Size(max = 50, message = "assignedAnalyst must not exceed 50 characters")
    private String assignedAnalyst;

    @NotEmpty(message = "At least one linkedAlertId is required")
    @Size(min = 1, max = 50, message = "linkedAlertIds must contain between 1 and 50 entries")
    private List<@NotBlank(message = "Alert ID must not be blank") String> linkedAlertIds;
}
