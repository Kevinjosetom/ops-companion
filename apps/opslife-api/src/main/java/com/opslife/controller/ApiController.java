package com.opslife.controller;

import com.opslife.model.*;
import com.opslife.service.OpsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
  private final OpsService ops;

  public ApiController(OpsService ops) {
    this.ops = ops;
  }

  // Habits
  @GetMapping("/habits")
  public List<Habit> habits() { return ops.listHabits(); }

  @PostMapping("/habits")
  public Habit createHabit(@RequestBody Map<String, Object> body) {
    return ops.createHabit((String) body.get("name"));
  }

  @PatchMapping("/habits/{id}")
  public Habit setHabitActive(@PathVariable Long id, @RequestBody Map<String, Object> body) {
    boolean active = Boolean.parseBoolean(String.valueOf(body.getOrDefault("active", true)));
    return ops.setHabitActive(id, active);
  }

  // Checkins
  @PostMapping("/checkins")
  public CheckIn upsertCheckIn(@RequestBody Map<String, Object> body) {
    Long habitId = body.get("habitId") == null ? null : Long.valueOf(String.valueOf(body.get("habitId")));
    String dateStr = (String) body.get("date");
    LocalDate date = (dateStr == null || dateStr.isBlank()) ? null : LocalDate.parse(dateStr);
    CheckInStatus status = body.get("status") == null ? null : CheckInStatus.valueOf(String.valueOf(body.get("status")));
    String note = (String) body.getOrDefault("note", null);
    return ops.upsertCheckIn(habitId, date, status, note);
  }

  @GetMapping("/checkins")
  public List<CheckIn> checkins(@RequestParam(required = false) String date) {
    LocalDate d = (date == null || date.isBlank()) ? null : LocalDate.parse(date);
    return ops.checkInsFor(d);
  }

  @GetMapping("/summary/today")
  public Map<String, Object> summaryToday() { return ops.summaryToday(); }

  // Interview
  @PostMapping("/interview/entries")
  public InterviewEntry addInterview(@RequestBody Map<String, Object> body) {
    String topic = (String) body.getOrDefault("topic", "general");
    String question = (String) body.get("question");
    String answer = (String) body.get("answer");
    int rating = Integer.parseInt(String.valueOf(body.getOrDefault("rating", 3)));
    String tags = (String) body.getOrDefault("tags", "");
    return ops.addInterviewEntry(topic, question, answer, rating, tags);
  }

  @GetMapping("/interview/entries")
  public List<InterviewEntry> listInterview(@RequestParam(required = false) String topic) {
    return ops.recentInterviewEntries(topic);
  }
}
