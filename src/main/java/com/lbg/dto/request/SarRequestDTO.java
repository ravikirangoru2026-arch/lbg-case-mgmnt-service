package com.lbg.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SAR filing decision — only valid from PENDING_REVIEW status")
public class SarRequestDTO {

    @NotNull(message = "Decision must not be null")
    @Pattern(regexp = "FILE|NO_ACTION", message = "Decision must be FILE or NO_ACTION")
    @Schema(description = "SAR decision", allowableValues = {"FILE", "NO_ACTION"})
    private String decision;

    @NotBlank(message = "Rationale must not be blank")
    @Size(min = 20, message = "Rationale must be at least 20 characters")
    @Schema(description = "Mandatory rationale for the SAR decision", minLength = 20)
    private String rationale;
}