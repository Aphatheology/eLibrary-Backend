package com.aphatheology.elibrarybackend.repository;

import com.aphatheology.elibrarybackend.entity.Feedbacks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedbacks, Long> {
}
