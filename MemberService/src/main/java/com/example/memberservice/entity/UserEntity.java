package com.example.memberservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String userName;

    public UserEntity(String loginId, String userName) {
        this.loginId = loginId;
        this.userName = userName;
    }

    public void modifyUserName(String userName) {
        this.userName = userName;
    }

}
