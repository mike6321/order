package com.example.memberservice.service;

import com.example.memberservice.entity.UserEntity;
import com.example.memberservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserEntity registerUser(String loginId, String userName) {
        var user = new UserEntity(loginId, userName);
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity modifyUser(Long userId, String userName) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow();
        userEntity.modifyUserName(userName);
        return userEntity;
    }

    public UserEntity getUser(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow();
    }

    

}
