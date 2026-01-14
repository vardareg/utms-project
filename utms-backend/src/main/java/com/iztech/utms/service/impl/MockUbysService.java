package com.iztech.utms.service.impl;

import com.iztech.utms.dto.TranscriptDto;
import com.iztech.utms.service.UbysService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class MockUbysService implements UbysService {

    @Override
    public List<TranscriptDto> getStudentTranscripts(String tckn) {
        List<TranscriptDto> transcripts = new ArrayList<>();

        // Mock logic based on TCKN last digit
        char lastDigit = tckn.length() > 0 ? tckn.charAt(tckn.length() - 1) : '9';

        if (lastDigit == '0') {
            // Good student (High GPA ~3.5)
            transcripts
                    .add(new TranscriptDto("CENG101", "Intro to Programming", new BigDecimal("4.0"), 5, "Fall", 2023));
            transcripts.add(new TranscriptDto("MATH101", "Calculus I", new BigDecimal("3.5"), 5, "Fall", 2023));
            transcripts.add(new TranscriptDto("PHYS101", "Physics I", new BigDecimal("3.0"), 4, "Fall", 2023));
        } else if (lastDigit == '1') {
            // Struggling student (Low GPA ~1.8)
            transcripts
                    .add(new TranscriptDto("CENG101", "Intro to Programming", new BigDecimal("2.0"), 5, "Fall", 2023));
            transcripts.add(new TranscriptDto("MATH101", "Calculus I", new BigDecimal("1.5"), 5, "Fall", 2023));
            transcripts.add(new TranscriptDto("PHYS101", "Physics I", new BigDecimal("1.0"), 4, "Fall", 2023));
        } else {
            // Average student (GPA ~2.5)
            transcripts
                    .add(new TranscriptDto("CENG101", "Intro to Programming", new BigDecimal("3.0"), 5, "Fall", 2023));
            transcripts.add(new TranscriptDto("MATH101", "Calculus I", new BigDecimal("2.5"), 5, "Fall", 2023));
            transcripts.add(new TranscriptDto("PHYS101", "Physics I", new BigDecimal("2.0"), 4, "Fall", 2023));
        }

        return transcripts;
    }
}
