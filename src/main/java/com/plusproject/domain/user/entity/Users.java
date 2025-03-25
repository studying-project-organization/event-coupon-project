package com.plusproject.domain.user.entity;

import com.plusproject.common.entity.BaseEntity;
import com.plusproject.domain.user.dto.request.CreateUserRequest;
import com.plusproject.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    private String email;

    private String nickname;

    private String address;

    private String password;

    private UserRole role = UserRole.USER;

    public Users(CreateUserRequest req) {
        this.address = req.getAddress();
        this.password = req.getPassword();
        this.email = req.getEmail();
        this.nickname = req.getNickname();
    }

    public void updateRole() {
        this.role = UserRole.ADMIN;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
