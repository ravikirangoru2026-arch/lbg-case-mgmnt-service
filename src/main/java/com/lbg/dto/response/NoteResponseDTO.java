package com.lbg.dto.response;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
public class NoteResponseDTO {
    private Long id;
    private String author;
    private String text;
    private Instant createdAt;
}