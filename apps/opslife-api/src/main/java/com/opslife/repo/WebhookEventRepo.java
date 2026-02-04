package com.opslife.repo;

import com.opslife.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebhookEventRepo extends JpaRepository<WebhookEvent, Long> {
  List<WebhookEvent> findTop50ByOrderByReceivedAtDesc();
}
