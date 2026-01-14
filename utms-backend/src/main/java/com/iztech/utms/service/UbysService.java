package com.iztech.utms.service;

import com.iztech.utms.dto.TranscriptDto;
import java.util.List;

public interface UbysService {
    List<TranscriptDto> getStudentTranscripts(String tckn);
}
