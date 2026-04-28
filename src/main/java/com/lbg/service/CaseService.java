package com.lbg.service;

import com.lbg.dto.request.*;
import com.lbg.dto.response.*;

public interface CaseService {
    CaseDetailDTO openCase(CaseRequestDTO dto);
    PagedResponseDTO<CaseSummaryDTO> getCases(CaseFilterCriteria filter,
                                              int page, int size);
    CaseDetailDTO  getCaseById(String caseRef);
    NoteResponseDTO addNote(String caseRef, NoteRequestDTO dto);
    CaseDetailDTO  fileSar(String caseRef, SarRequestDTO dto);
}