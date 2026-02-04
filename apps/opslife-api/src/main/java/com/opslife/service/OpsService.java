package com.opslife.service;

import com.opslife.model.*;
import com.opslife.repo.*;
import com.opslife.sse.SseHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class OpsService {
  private final HabitRepo habitRepo;
  private final CheckInRepo checkInRepo;
  private final InterviewRepo interviewRepo;
  private final WebhookEventRepo webhookRepo;
  private final SseHub sseHub;

  public OpsService(HabitRepo habitRepo, CheckInRepo checkInRepo, InterviewRepo interviewRepo,
                    WebhookEventRepo webhookRepo, SseHub sseHub) {
    this.habitRepo = habitRepo;
    this.checkInRepo = checkInRepo;
    this.interviewRepo = interviewRepo;
    this.webhookRepo = webhookRepo;
    this.sseHub = sseHub;
  }

  public List<Habit> listHabits() {
    return habitRepo.findAll().stream()
      .sorted(Comparator.comparing(h -> h.getName().toLowerCase()))
      .toList();
  }

  public long activeHabitsCount() {
    return habitRepo.countByActiveTrue();
  }

  @Transactional
  public Habit createHabit(String name) {
    String n = (name == null) ? "" : name.trim();
    if (n.isEmpty()) throw new IllegalArgumentException("name is required");
    Habit h = habitRepo.save(new Habit(n));
    sseHub.broadcast(Map.of("type","habit.created","name",h.getName(), "habitId", h.getId()));
    return h;
  }

  @Transactional
  public Habit setHabitActive(Long id, boolean active) {
    Habit h = habitRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("habit not found"));
    h.setActive(active);
    sseHub.broadcast(Map.of("type","habit.updated","habitId",h.getId(),"active",active));
    return h;
  }

  @Transactional
  public CheckIn upsertCheckIn(Long habitId, LocalDate date, CheckInStatus status, String note) {
    if (habitId == null) throw new IllegalArgumentException("habitId is required");
    if (status == null) throw new IllegalArgumentException("status is required");
    LocalDate d = (date == null) ? LocalDate.now() : date;

    Habit habit = habitRepo.findById(habitId).orElseThrow(() -> new IllegalArgumentException("habit not found"));

    CheckIn existing = checkInRepo.findByHabitIdAndCheckinDate(habitId, d).orElse(null);
    CheckIn saved;
    if (existing == null) {
      saved = checkInRepo.save(new CheckIn(habit, d, status, note));
    } else {
      existing.setStatus(status);
      existing.setNote(note);
      saved = existing;
    }

    sseHub.broadcast(Map.of(
      "type","checkin.upserted",
      "habitId", habitId,
      "date", d.toString(),
      "status", status.toString()
    ));

    return saved;
  }

  public List<CheckIn> checkInsFor(LocalDate date) {
    LocalDate d = (date == null) ? LocalDate.now() : date;
    return checkInRepo.findByCheckinDate(d);
  }

  public Map<String, Object> summaryToday() {
    LocalDate today = LocalDate.now();
    long totalActive = habitRepo.countByActiveTrue();
    long done = checkInRepo.countByCheckinDateAndStatus(today, CheckInStatus.DONE);

    // "Streak" = consecutive days (ending today) where doneCount == totalActiveHabits
    // v1 assumption: habits don't change daily; good enough to start.
    int streak = 0;
    for (int i = 0; i < 365; i++) {
      LocalDate d = today.minusDays(i);
      long doneThatDay = checkInRepo.countByCheckinDateAndStatus(d, CheckInStatus.DONE);
      if (doneThatDay == totalActive && totalActive > 0) streak++;
      else break;
    }

    return Map.of(
      "date", today.toString(),
      "doneCount", done,
      "totalActiveHabits", totalActive,
      "streakDays", streak
    );
  }

  @Transactional
  public InterviewEntry addInterviewEntry(String topic, String question, String answer, int rating, String tags) {
    String t = (topic == null) ? "general" : topic.trim().toLowerCase();
    if (question == null || question.trim().isEmpty()) throw new IllegalArgumentException("question is required");
    if (answer == null || answer.trim().isEmpty()) throw new IllegalArgumentException("answer is required");
    if (rating < 1 || rating > 5) throw new IllegalArgumentException("rating must be 1-5");

    InterviewEntry e = interviewRepo.save(new InterviewEntry(t, question.trim(), answer.trim(), rating, tags));
    sseHub.broadcast(Map.of("type","interview.created","topic",t,"rating",rating));
    return e;
  }

  public List<InterviewEntry> recentInterviewEntries(String topic) {
    if (topic == null || topic.trim().isEmpty()) return interviewRepo.findTop50ByOrderByCreatedAtDesc();
    return interviewRepo.findTop50ByTopicOrderByCreatedAtDesc(topic.trim().toLowerCase());
  }

  @Transactional
  public WebhookEvent recordWebhook(String source, String type, String payload) {
    WebhookEvent e = webhookRepo.save(new WebhookEvent(source, type, payload));
    sseHub.broadcast(Map.of("type", type, "source", source));
    return e;
  }

  public List<WebhookEvent> recentWebhookEvents() {
    return webhookRepo.findTop50ByOrderByReceivedAtDesc();
  }
}
