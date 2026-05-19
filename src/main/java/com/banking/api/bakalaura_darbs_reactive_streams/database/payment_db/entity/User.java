package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity;

import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column("id")
    private Long id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("email")
    private String email;

    @Column("is_user_under_18")
    private Boolean isUserUnder18;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("attachment_id")
    private Long attachmentId;
}
