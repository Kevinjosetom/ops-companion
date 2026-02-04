package com.opslife.repo;

import com.opslife.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepo extends JpaRepository<Habit, Long> {
  List<Habit> findByActiveTrueOrderByNameAsc();
  long countByActiveTrue();
}
