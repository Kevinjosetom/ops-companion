package com.opslife.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "webhook_events")
public class WebhookEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 30)
  private String source; // "jenkins" / "alerts"

  @Column(nullable = false, length = 30)
  private String type;   // "deployment.recorded" / "alert.received"

  // IMPORTANT:
  // - Do NOT use @Lob here (it makes Hibernate expect CLOB)
  // - columnDefinition="TEXT" aligns with your Flyway migration
  @Column(nullable = false, columnDefinition = "TEXT")
  private String payload;

  @Column(nullable = false)
  private Instant receivedAt = Instant.now();

  public WebhookEvent() {}

  public WebhookEvent(String source, String type, String payload) {
    this.source = source;
    this.type = type;
    this.payload = payload;
  }

  public Long getId() { return id; }
  public String getSource() { return source; }
  public String getType() { return type; }
  public String getPayload() { return payload; }
  public Instant getReceivedAt() { return receivedAt; }
}
