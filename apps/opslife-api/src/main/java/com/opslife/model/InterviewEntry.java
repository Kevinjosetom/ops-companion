package com.opslife.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "interview_entries")
public class InterviewEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String topic; // e.g. "kubernetes", "aws"

  @Column(nullable = false, length = 500)
  private String question;

  @Column(nullable = false, length = 4000)
  private String answer;

  @Column(nullable = false)
  private int rating; // 1-5

  @Column(length = 500)
  private String tags; // comma-separated for v1

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public InterviewEntry() {}

  public InterviewEntry(String topic, String question, String answer, int rating, String tags) {
    this.topic = topic;
    this.question = question;
    this.answer = answer;
    this.rating = rating;
    this.tags = tags;
  }

  public Long getId() { return id; }
  public String getTopic() { return topic; }
  public String getQuestion() { return question; }
  public String getAnswer() { return answer; }
  public int getRating() { return rating; }
  public String getTags() { return tags; }
  public Instant getCreatedAt() { return createdAt; }
}
