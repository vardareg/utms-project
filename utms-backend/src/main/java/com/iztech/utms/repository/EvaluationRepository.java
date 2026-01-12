package main.java.com.iztech.utms.repository;

import com.iztech.utms.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    // Check if an application has already been evaluated
    Optional<Evaluation> findByApplicationId(Long applicationId);
}