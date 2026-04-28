package com.lbg.dto.response;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PagedResponseDTO<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <T> PagedResponseDTO<T> from(Page<T> pageResult) {
        return PagedResponseDTO.<T>builder()
                .content(pageResult.getContent())
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }
}