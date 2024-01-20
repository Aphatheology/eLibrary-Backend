package com.aphatheology.elibrarybackend.repository;

import com.aphatheology.elibrarybackend.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Books, Long> {
    Optional<Books> findBySlug(String slug);
}
