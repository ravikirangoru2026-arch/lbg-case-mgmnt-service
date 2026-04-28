package com.lbg.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload to add an immutable note to a case")
public class NoteRequestDTO {

    @NotBlank(message = "Note text must not be blank")
    @Size(min = 10, max = 2000, message = "Note text must be between 10 and 2000 characters")
    @Schema(description = "Note content", minLength = 10, maxLength = 2000)
    private String text;

    @NotBlank(message = "Author must not be blank")
    @Schema(example = "t.bergmann")
    private String author;
}