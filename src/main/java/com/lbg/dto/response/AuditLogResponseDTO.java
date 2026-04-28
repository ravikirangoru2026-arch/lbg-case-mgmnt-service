package com.lbg.dto.response;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
public class AuditLogResponseDTO {
    private String eventType;
    private String analyst;
    private String detail;
    private Instant changedAt;
}