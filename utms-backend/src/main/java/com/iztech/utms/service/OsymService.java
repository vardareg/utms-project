package com.iztech.utms.service;

import com.iztech.utms.dto.YksValidationResponse;
import java.math.BigDecimal;

public interface OsymService {
    YksValidationResponse validateYksScore(String tckn, BigDecimal score);

    BigDecimal getYksScore(String tckn);
}
