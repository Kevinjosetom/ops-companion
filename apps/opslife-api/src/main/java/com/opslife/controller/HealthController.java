package com.opslife.controller;

import com.opslife.repo.HabitRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
  private final HabitRepo habitRepo;

  public HealthController(HabitRepo habitRepo) { this.habitRepo = habitRepo; }

  @GetMapping("/api/v1/health/live")
  public Map<String, Object> live() {
    return Map.of("status","UP");
  }

  @GetMapping("/api/v1/health/ready")
  public Map<String, Object> ready() {
    // DB check (simple query)
    habitRepo.count();
    return Map.of("status","UP","db","UP","kafka","SKIPPED_IN_V1");
  }
}
