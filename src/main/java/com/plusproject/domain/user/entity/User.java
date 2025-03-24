package com.plusproject.domain.user.entity;

import com.plusproject.common.entity.BaseEntity;
import com.plusproject.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 60, nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Builder
    private User(String email, String password, String nickname, String address, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.address = address;
        this.userRole = userRole;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

}
