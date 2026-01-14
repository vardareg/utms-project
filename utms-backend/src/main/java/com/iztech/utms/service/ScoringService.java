package com.iztech.utms.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final ConfigurationService configurationService;

    private static final BigDecimal MAX_GPA_4 = new BigDecimal("4.00");
    private static final BigDecimal MIN_GPA_4 = new BigDecimal("2.00");

    // Formula constants for (GPA - 2.0) * (46.67 / 2) + 53.33
    // 46.67 / 2 = 23.335
    private static final BigDecimal FACTOR = new BigDecimal("23.335");
    private static final BigDecimal BASE_100 = new BigDecimal("53.33");
    private static final BigDecimal PERFECT_100 = new BigDecimal("100.00");

    /**
     * Converts a 4.0 scale GPA to 100 scale using the standard YÖK approximation:
     * (GPA - 2) * ( (100 - 53.33) / 2 ) + 53.33
     * 
     * @param gpa4 The GPA on a 4.00 scale.
     * @return The GPA on a 100 scale.
     */
    public BigDecimal convertGpaTo100(BigDecimal gpa4) {
        if (gpa4 == null) {
            return BigDecimal.ZERO;
        }

        // Clamp logic: if > 4.00 return 100, if < 2.00 return 0 or handle logic.
        // Usually, < 2.00 is not eligible, but for conversion, we can act strictly.
        if (gpa4.compareTo(MAX_GPA_4) >= 0) {
            return PERFECT_100;
        }
        if (gpa4.compareTo(MIN_GPA_4) <= 0) {
            // Below 2.00 maps to lower than 53.33 linearly or just return base?
            // Strict YÖK table usually starts at 2.00 = 53.33.
            // Let's use the formula even if it goes below 53.33, or clamp to 0.
            // For safety in this strict system, if it's < 2.00, it's failed anyway,
            // but let's return the formula result or 0.
            if (gpa4.compareTo(BigDecimal.ZERO) < 0)
                return BigDecimal.ZERO;
        }

        // Formula: (GPA - 2.0) * 23.335 + 53.33
        BigDecimal diff = gpa4.subtract(MIN_GPA_4);
        if (diff.compareTo(BigDecimal.ZERO) < 0) {
            // Handle 0.0 - 1.99 logic if needed, but for now we trust the formula
            // or simply return a proportional value.
            // Simple linear 4.0->100, 0->0 is (GPA * 25), but YÖK is non-linear/offset.
            // We will stick to the formula only for >= 2.0. If < 2.0, use (GPA/4.0)*100?
            // NO, let's stick to the PR-07 requirement of "Valid YÖK Conversion".
            // Valid YÖK conversion implies valid GPA (>= 2.0 usually).
            // We will clamp minimum at 2.0 logic equivalent (53.33) or let it slide?
            // Let's return the calculated value, but ensure it doesn't break.
            // Actually, simplest safe fallback for < 2.0 is just 0 since they aren't
            // eligible.
            return BigDecimal.ZERO;
        }

        BigDecimal scaled = diff.multiply(FACTOR);
        BigDecimal finalScore = scaled.add(BASE_100);

        return finalScore.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Composite Score (PR-07)
     * Score = (GPA_100 * WEIGHT_GPA) + (YKS * WEIGHT_YKS)
     */
    public BigDecimal calculateCompositeScore(BigDecimal gpa100, BigDecimal yks) {
        if (gpa100 == null)
            gpa100 = BigDecimal.ZERO;
        if (yks == null)
            yks = BigDecimal.ZERO;

        BigDecimal weightGpa = configurationService.getGpaWeight();
        BigDecimal weightYks = configurationService.getYksWeight();

        BigDecimal gpaPart = gpa100.multiply(weightGpa);
        BigDecimal yksPart = yks.multiply(weightYks);

        return gpaPart.add(yksPart).setScale(3, RoundingMode.HALF_UP);
    }
}
