package com.aphatheology.elibrarybackend.repository;

import com.aphatheology.elibrarybackend.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Books, Long> {
}
