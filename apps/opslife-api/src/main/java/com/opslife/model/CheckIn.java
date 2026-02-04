package com.opslife.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
  name = "checkins",
  uniqueConstraints = @UniqueConstraint(name = "uk_habit_date", columnNames = {"habit_id","checkin_date"})
)
public class CheckIn {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "habit_id", nullable = false)
  private Habit habit;



  @Column(name = "checkin_date", nullable = false)
  private LocalDate checkinDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CheckInStatus status;

  @Column(length = 500)
  private String note;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public CheckIn() {}

  public CheckIn(Habit habit, LocalDate date, CheckInStatus status, String note) {
    this.habit = habit;
    this.checkinDate = date;
    this.status = status;
    this.note = note;
  }

  public Long getId() { return id; }
  public Habit getHabit() { return habit; }
  public LocalDate getCheckinDate() { return checkinDate; }
  public CheckInStatus getStatus() { return status; }
  public void setStatus(CheckInStatus status) { this.status = status; }
  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }
  public Instant getCreatedAt() { return createdAt; }
}
