package com.school.student.domain;

import com.school.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "student_documents")
public class StudentDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private DocumentType type;

    @Column(name = "label", length = 200)
    private String label;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    public enum DocumentType {
        BIRTH_CERTIFICATE,
        PHOTO,
        PREVIOUS_REPORT_CARD,
        PREVIOUS_DIPLOMA,
        MEDICAL_CERTIFICATE,
        ID_CARD,
        TRANSFER_CERTIFICATE,
        OTHER
    }
}
