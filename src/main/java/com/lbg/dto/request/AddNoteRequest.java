package com.lbg.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddNoteRequest {

    @NotBlank(message = "author is required")
    @Size(max = 50, message = "author must not exceed 50 characters")
    private String author;

    @NotBlank(message = "noteText is required")
    private String noteText;
}
