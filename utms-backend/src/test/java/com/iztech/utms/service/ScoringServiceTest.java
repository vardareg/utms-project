package com.iztech.utms.service;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@org.junit.jupiter.api.extension.ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class ScoringServiceTest {

    @org.mockito.Mock
    private ConfigurationService configurationService;

    @org.mockito.InjectMocks
    private ScoringService scoringService;

    @Test
    void testConvertGpaTo100_PerfectScore() {
        BigDecimal gpa = new BigDecimal("4.00");
        BigDecimal result = scoringService.convertGpaTo100(gpa);
        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void testConvertGpaTo100_MinimumPassing() {
        // 2.00 should be 53.33
        BigDecimal gpa = new BigDecimal("2.00");
        BigDecimal result = scoringService.convertGpaTo100(gpa);
        assertEquals(new BigDecimal("53.33"), result);
    }

    @Test
    void testConvertGpaTo100_MidValue() {
        // 3.00 Calculation roughly:
        // (3.0 - 2.0) * 23.335 + 53.33 = 1 * 23.335 + 53.33 = 76.665 -> 76.67 (Half Up)
        BigDecimal gpa = new BigDecimal("3.00");
        BigDecimal result = scoringService.convertGpaTo100(gpa);
        assertEquals(new BigDecimal("76.67"), result);
    }

    @Test
    void testCalculateCompositeScore() {
        // Mock weights
        org.mockito.Mockito.when(configurationService.getGpaWeight()).thenReturn(new BigDecimal("0.5"));
        org.mockito.Mockito.when(configurationService.getYksWeight()).thenReturn(new BigDecimal("0.5"));

        // PR-07: (GPA_100 * 0.5) + (YKS * 0.5)
        BigDecimal gpa100 = new BigDecimal("80.00");
        BigDecimal yks = new BigDecimal("400.00");

        // Expected: (40) + (200) = 240.000
        BigDecimal result = scoringService.calculateCompositeScore(gpa100, yks);
        assertEquals(new BigDecimal("240.000"), result);
    }

    @Test
    void testCalculateCompositeScore_RealExample() {
        // Mock weights
        org.mockito.Mockito.when(configurationService.getGpaWeight()).thenReturn(new BigDecimal("0.5"));
        org.mockito.Mockito.when(configurationService.getYksWeight()).thenReturn(new BigDecimal("0.5"));

        // Student with 3.50 GPA (approx 88.33) and 450 YKS
        // 3.50 -> (1.5 * 23.335) + 53.33 = 35.0025 + 53.33 = 88.3325 -> 88.33
        BigDecimal gpa = new BigDecimal("3.50");
        BigDecimal gpa100 = scoringService.convertGpaTo100(gpa); // 88.33

        BigDecimal yks = new BigDecimal("450.00");

        // (88.33 * 0.5) + (450 * 0.5)
        // 44.165 + 225.00 = 269.165
        BigDecimal result = scoringService.calculateCompositeScore(gpa100, yks);

        assertEquals(new BigDecimal("269.165"), result);
    }
}
