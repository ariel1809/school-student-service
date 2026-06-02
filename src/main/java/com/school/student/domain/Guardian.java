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
@Table(name = "guardians", indexes = {
        @Index(name = "idx_guardians_email", columnList = "email"),
        @Index(name = "idx_guardians_phone", columnList = "phone")
})
public class Guardian extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "profession", length = 200)
    private String profession;

    @Column(name = "employer", length = 200)
    private String employer;

    @Column(name = "national_id", length = 50)
    private String nationalId;
}
