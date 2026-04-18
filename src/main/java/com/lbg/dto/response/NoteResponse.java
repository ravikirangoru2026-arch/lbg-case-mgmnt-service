package com.lbg.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoteResponse {
    private Long id;
    private String author;
    private LocalDateTime noteTimestamp;
    private String noteText;
}
