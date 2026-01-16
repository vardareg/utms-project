package com.iztech.utms.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemMonitorService {

    private final MeterRegistry meterRegistry;
    private final HealthEndpoint healthEndpoint;

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. System Uptime
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        stats.put("uptime", uptime);
        stats.put("uptimeFormatted", formatUptime(uptime));

        // 2. Active Sessions
        double activeRequests = 0;
        try {
            activeRequests = meterRegistry.get("http.server.requests").timer().count();
        } catch (Exception e) {
            // Metric not found or not a timer, ignore
        }
        stats.put("activeRequests", (int) activeRequests);

        // 3. Database Health
        HealthComponent health = healthEndpoint.health();
        Status dbStatus = health.getStatus();
        stats.put("systemHealth", dbStatus.getCode());

        // Detailed DB status if available
        if (health instanceof CompositeHealth) {
            CompositeHealth composite = (CompositeHealth) health;
            if (composite.getComponents().containsKey("db")) {
                stats.put("dbDetails", composite.getComponents().get("db").getStatus().getCode());
            } else {
                stats.put("dbDetails", "Unknown");
            }
        } else if (health instanceof Health) {
            Health h = (Health) health;
            if (h.getDetails().containsKey("db")) {
                stats.put("dbDetails", h.getDetails().get("db"));
            } else {
                stats.put("dbDetails", "Unknown");
            }
        } else {
            stats.put("dbDetails", "Unknown");
        }

        // 4. Memory Usage
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        stats.put("usedMemoryInfo", formatMemory(usedMemory));

        return stats;
    }

    private String formatUptime(long uptimeMillis) {
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return String.format("%dd %02dh %02dm", days, hours % 24, minutes % 60);
    }

    private String formatMemory(long bytes) {
        long mb = bytes / (1024 * 1024);
        return mb + " MB";
    }
}
