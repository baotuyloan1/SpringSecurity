package com.example.client.service;

import com.example.client.entity.UserEntity;
import com.example.client.entity.VerificationToken;
import com.example.client.model.UserModel;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserService {

    UserEntity registerUser(UserModel user);

    void saveVerificationTokenForUser(String token, UserEntity user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    UserEntity findUserByEmail(String email);

    void createPasswordResetTokenForUser(UserEntity user, String token);

    String validatePasswordResetToken(String token);

    Optional<UserEntity> getUserByPasswordResetToken(String token);

    void changePassword(UserEntity userEntity, String newPassword);

    boolean checkIfValidOldPassword(UserEntity user, String oldPassword);
}
