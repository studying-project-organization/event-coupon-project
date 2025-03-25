package com.plusproject.domain.user.entity;

import com.plusproject.common.entity.BaseEntity;
import com.plusproject.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private String nickname;
    private String address;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String email, String password,String nickname, String address, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.address = address;
        this.userRole = userRole;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
