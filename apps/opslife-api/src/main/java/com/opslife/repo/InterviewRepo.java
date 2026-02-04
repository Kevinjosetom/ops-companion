package com.opslife.repo;

import com.opslife.model.InterviewEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepo extends JpaRepository<InterviewEntry, Long> {
  List<InterviewEntry> findTop50ByOrderByCreatedAtDesc();
  List<InterviewEntry> findTop50ByTopicOrderByCreatedAtDesc(String topic);
}
