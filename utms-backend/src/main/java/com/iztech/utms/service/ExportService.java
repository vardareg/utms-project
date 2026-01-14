package com.iztech.utms.service;

import com.iztech.utms.service.EvaluationService.RankingResponse;
import java.io.ByteArrayInputStream;

public interface ExportService {
    ByteArrayInputStream export(RankingResponse data);
}
