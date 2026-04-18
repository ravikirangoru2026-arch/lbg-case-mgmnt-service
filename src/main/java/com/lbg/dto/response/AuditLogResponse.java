package com.lbg.dto.response;

import com.lbg.enums.AuditEventType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {
    private Long id;
    private AuditEventType eventType;
    private LocalDateTime eventTimestamp;
    private String analyst;
    private String detail;
}
