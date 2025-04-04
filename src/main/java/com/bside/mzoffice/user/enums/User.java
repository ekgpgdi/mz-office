package com.bside.mzoffice.user.enums;

import com.bside.mzoffice.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx1_username", columnList = "username"),
                @Index(name = "idx2_email", columnList = "email"),
                @Index(name = "idx3_sns_id", columnList = "sns_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String username;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String snsId;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private SnsType snsType;

    @Column(length = 255)
    private String accessToken;

    @Column(length = 255)
    private String refreshToken;

    private LocalDateTime tokenExpiry;
}

