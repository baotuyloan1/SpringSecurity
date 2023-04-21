package com.example.client.service;

import com.example.client.entity.PasswordResetToken;
import com.example.client.entity.UserEntity;
import com.example.client.entity.VerificationToken;
import com.example.client.model.UserModel;
import com.example.client.repository.PasswordResetTokenRepository;
import com.example.client.repository.UserRepository;
import com.example.client.repository.VerificationTokenRepository;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;

@Service
public class UserServiceImpl implements UserService {
  @Autowired private UserRepository userRepository;

  @Autowired private VerificationTokenRepository verificationTokenRepository;

  @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @Override
  public UserEntity registerUser(UserModel userModel) {
    UserEntity user = new UserEntity();
    user.setEmail(userModel.getEmail());
    user.setFirstName(userModel.getFirstName());
    user.setLastName(userModel.getLastName());
    user.setRole("USER");
    user.setPassword(passwordEncoder.encode(userModel.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public void saveVerificationTokenForUser(String token, UserEntity user) {
    VerificationToken verificationToken = new VerificationToken(user, token);

    verificationTokenRepository.save(verificationToken);
  }

  @Override
  public String validateVerificationToken(String token) {
    VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
    if (verificationToken == null) {
      return "invalid";
    }

    UserEntity user = verificationToken.getUser();
    Calendar calendar = Calendar.getInstance();

    if ((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
      verificationTokenRepository.delete(verificationToken);
      return "expired";
    }
    user.setEnabled(true);
    userRepository.save(user);
    return "valid";
  }

  @Override
  public VerificationToken generateNewVerificationToken(String oldToken) {
    VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
    verificationToken.setToken(UUID.randomUUID().toString());
    verificationTokenRepository.save(verificationToken);
    return verificationToken;
  }

  @Override
  public UserEntity findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public void createPasswordResetTokenForUser(UserEntity user, String token) {
    PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
    passwordResetTokenRepository.save(passwordResetToken);
  }

  @Override
  public String validatePasswordResetToken(String token) {
    PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
    if (passwordResetToken == null) {
      return "invalid";
    }

    UserEntity user = passwordResetToken.getUser();
    Calendar calendar = Calendar.getInstance();

    if ((passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
      passwordResetTokenRepository.delete(passwordResetToken);
      return "expired";
    }
    return "valid";
  }

  @Override
  public Optional<UserEntity> getUserByPasswordResetToken(String token) {
    return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
  }


}
