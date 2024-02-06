package com.aphatheology.elibrarybackend.repository;

import com.aphatheology.elibrarybackend.entity.FilesData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FilesData, String> {
}
