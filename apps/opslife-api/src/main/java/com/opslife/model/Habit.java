package com.opslife.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "habits")
public class Habit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 120)
  private String name;

  @Column(nullable = false)
  private boolean active = true;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public Habit() {}
  public Habit(String name) { this.name = name; }

  public Long getId() { return id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public Instant getCreatedAt() { return createdAt; }
}
