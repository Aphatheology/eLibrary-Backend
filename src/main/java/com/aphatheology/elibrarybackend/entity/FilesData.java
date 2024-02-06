package com.aphatheology.elibrarybackend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FilesData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String type;

    @Lob
    @Column(nullable = false)
    private byte[] bytes;

    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public FilesData(String fileName, byte[] fileByte){
        this.name = fileName;
        this.bytes = fileByte;
    }

    public FilesData(String fileName, String fileType, byte[] fileByte) {
        this(fileName, fileByte);
        this.type = fileType;
    }
}
