package org.example.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> stats(Long shopId, String range);
}
