package com.lbg.dto.request;

import com.lbg.enums.SarDecision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileSarRequest {

    @NotNull(message = "sarDecision is required (FILE or NO_ACTION)")
    private SarDecision sarDecision;

    @NotBlank(message = "sarRationale is mandatory")
    private String sarRationale;

    @NotBlank(message = "analyst is required")
    private String analyst;
}
