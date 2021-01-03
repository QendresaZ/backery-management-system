package dev.bms.bakerymngsystem.backend.repository;

import dev.bms.bakerymngsystem.backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
