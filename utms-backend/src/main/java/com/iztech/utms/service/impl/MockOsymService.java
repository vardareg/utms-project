package com.iztech.utms.service.impl;

import com.iztech.utms.dto.YksValidationResponse;
import com.iztech.utms.service.OsymService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Primary
public class MockOsymService implements OsymService {

    @Override
    public YksValidationResponse validateYksScore(String tckn, BigDecimal submittedScore) {
        // Simulate network delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Determine "Official" Score based on TCKN
        BigDecimal officialScore;
        char lastDigit = tckn != null && tckn.length() > 0 ? tckn.charAt(tckn.length() - 1) : '9';

        if (lastDigit == '0') {
            officialScore = new BigDecimal("450.00");
        } else if (lastDigit == '1') {
            officialScore = new BigDecimal("300.00");
        } else {
            officialScore = new BigDecimal("350.00");
        }

        // Validation Logic: Match within expected range or exact/close match
        // For simplicity, let's say "Valid" if submittedScore equals officialScore
        // OR let's be more lenient for demo: Valid if submittedScore is NOT null and >
        // 0,
        // BUT we want to flag mismatches.
        // Revised Logic:
        // - Returns official score.
        // - Valid = true if submittedScore matches officialScore.
        // - Message explains result.

        boolean isValid = submittedScore != null && submittedScore.compareTo(officialScore) == 0;

        String message;
        if (isValid) {
            message = "YKS Score verified successfully.";
        } else {
            message = "Score Mismatch: Official record is " + officialScore + ".";
        }

        return YksValidationResponse.builder()
                .valid(isValid)
                .officialScore(officialScore)
                .examYear(2023) // Mock year
                .message(message)
                .build();
    }

    @Override
    public BigDecimal getYksScore(String tckn) {
        // Simulate network delay
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        char lastDigit = tckn != null && tckn.length() > 0 ? tckn.charAt(tckn.length() - 1) : '9';

        if (lastDigit == '0') {
            return new BigDecimal("450.00");
        } else if (lastDigit == '1') {
            return new BigDecimal("300.00");
        } else {
            return new BigDecimal("350.00");
        }
    }
}
