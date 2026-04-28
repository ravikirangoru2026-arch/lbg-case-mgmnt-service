package com.lbg.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseFilterCriteria {
    private String status;
    private String priority;
}