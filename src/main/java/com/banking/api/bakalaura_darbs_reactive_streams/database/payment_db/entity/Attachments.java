package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity;

import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "attachments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachments {

    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("document_type")
    private String documentType;

    @Column("file_name")
    private String fileName;

    @Column("file_size")
    private Long fileSize;

    @Column("mime_type")
    private String mimeType;

    @Column("file_path")
    private String filePath;

    @Column("uploaded_at")
    private LocalDateTime uploadedAt;

    @Column("verification_status")
    private String verificationStatus;

    @Column("verified_at")
    private LocalDateTime verifiedAt;

    @Column("verified_by")
    private Long verifiedBy;

    @Column("notes")
    private String notes;
}
