package com.iztech.utms.service;

import com.iztech.utms.entity.SystemConfig;
import com.iztech.utms.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class ConfigurationService {

    private final SystemConfigRepository repository;

    public static final String KEY_MIN_GPA = "MIN_GPA_THRESHOLD";
    public static final String KEY_MIN_YKS = "MIN_YKS_THRESHOLD"; // New
    public static final String KEY_WEIGHT_GPA = "WEIGHT_GPA";
    public static final String KEY_WEIGHT_YKS = "WEIGHT_YKS";

    // Defaults
    private static final String DEFAULT_MIN_GPA = "2.00";
    private static final String DEFAULT_MIN_YKS = "150.00"; // New
    private static final String DEFAULT_WEIGHT_GPA = "0.5";
    private static final String DEFAULT_WEIGHT_YKS = "0.5";

    @PostConstruct
    public void init() {
        // Initialize defaults if not present
        if (!repository.existsById(KEY_MIN_GPA)) {
            log.info("Initializing default config: {} = {}", KEY_MIN_GPA, DEFAULT_MIN_GPA);
            repository.save(new SystemConfig(KEY_MIN_GPA, DEFAULT_MIN_GPA));
        }
        if (!repository.existsById(KEY_MIN_YKS)) { // New
            log.info("Initializing default config: {} = {}", KEY_MIN_YKS, DEFAULT_MIN_YKS);
            repository.save(new SystemConfig(KEY_MIN_YKS, DEFAULT_MIN_YKS));
        }
        if (!repository.existsById(KEY_WEIGHT_GPA)) {
            log.info("Initializing default config: {} = {}", KEY_WEIGHT_GPA, DEFAULT_WEIGHT_GPA);
            repository.save(new SystemConfig(KEY_WEIGHT_GPA, DEFAULT_WEIGHT_GPA));
        }
        if (!repository.existsById(KEY_WEIGHT_YKS)) {
            log.info("Initializing default config: {} = {}", KEY_WEIGHT_YKS, DEFAULT_WEIGHT_YKS);
            repository.save(new SystemConfig(KEY_WEIGHT_YKS, DEFAULT_WEIGHT_YKS));
        }
    }

    public BigDecimal getMinGpaThreshold() {
        String val = repository.findById(KEY_MIN_GPA)
                .map(SystemConfig::getConfigValue)
                .orElse(DEFAULT_MIN_GPA);
        log.debug("Fetched MIN_GPA_THRESHOLD: {}", val);
        return new BigDecimal(val);
    }

    public BigDecimal getMinYksThreshold() { // New
        String val = repository.findById(KEY_MIN_YKS)
                .map(SystemConfig::getConfigValue)
                .orElse(DEFAULT_MIN_YKS);
        log.debug("Fetched MIN_YKS_THRESHOLD: {}", val);
        return new BigDecimal(val);
    }

    public BigDecimal getGpaWeight() {
        return new BigDecimal(repository.findById(KEY_WEIGHT_GPA)
                .map(SystemConfig::getConfigValue)
                .orElse(DEFAULT_WEIGHT_GPA));
    }

    public BigDecimal getYksWeight() {
        return new BigDecimal(repository.findById(KEY_WEIGHT_YKS)
                .map(SystemConfig::getConfigValue)
                .orElse(DEFAULT_WEIGHT_YKS));
    }

    public Map<String, String> getAllConfigs() {
        List<SystemConfig> configs = repository.findAll();
        Map<String, String> map = new HashMap<>();
        for (SystemConfig c : configs) {
            map.put(c.getConfigKey(), c.getConfigValue());
        }
        return map;
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateConfigs(Map<String, String> updates) {
        log.info("Updating configs: {}", updates);
        updates.forEach((k, v) -> {
            SystemConfig config = repository.findById(k).orElse(new SystemConfig(k, v));
            config.setConfigValue(v);
            repository.save(config);
            log.info("Saved config: {} = {}", k, v);
        });
    }
}
