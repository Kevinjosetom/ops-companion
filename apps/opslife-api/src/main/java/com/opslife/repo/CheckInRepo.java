package com.opslife.repo;

import com.opslife.model.CheckIn;
import com.opslife.model.CheckInStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckInRepo extends JpaRepository<CheckIn, Long> {
  Optional<CheckIn> findByHabitIdAndCheckinDate(Long habitId, LocalDate date);
  List<CheckIn> findByCheckinDate(LocalDate date);
  long countByCheckinDateAndStatus(LocalDate date, CheckInStatus status);
}
