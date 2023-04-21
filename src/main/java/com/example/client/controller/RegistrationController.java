package com.example.client.controller;

import com.example.client.entity.UserEntity;
import com.example.client.entity.VerificationToken;
import com.example.client.event.RegistrationCompleteEvent;
import com.example.client.model.PasswordModel;
import com.example.client.model.UserModel;
import com.example.client.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {

  @Autowired private UserService userService;

  @Autowired private ApplicationEventPublisher publisher;

  @PostMapping("/register")
  public String registerUser(
      @RequestBody UserModel userModel, final HttpServletRequest httpServletRequest) {
    UserEntity user = userService.registerUser(userModel);
    publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(httpServletRequest)));
    return "Success";
  }

  private String applicationUrl(HttpServletRequest httpServletRequest) {
    return "http://"
        + httpServletRequest.getServerName()
        + ":"
        + httpServletRequest.getServerPort()
        + httpServletRequest.getContextPath();
  }

  @GetMapping("/verifyRegistration")
  public String verifyRegistration(@RequestParam("token") String token) {
    String result = userService.validateVerificationToken(token);
    if (result.equalsIgnoreCase("valid")) {
      return "User Verifies Successfully";
    } else return "Bad User";
  }

  @GetMapping("/resendVerifyToken")
  public String resendVerificationToken(
      @RequestParam("token") String oldToken, HttpServletRequest request) {
    VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
    UserEntity user = verificationToken.getUser();
    resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
    return "Verification Link Sent";
  }

  @PostMapping("/resetPassword")
  public String resetPassword(
      @RequestBody PasswordModel passwordModel, HttpServletRequest httpServletRequest) {
    UserEntity user = userService.findUserByEmail(passwordModel.getEmail());
    String url = "";
    if (user != null) {
      String token = UUID.randomUUID().toString();
      userService.createPasswordResetTokenForUser(user, token);
      url = passwordResetTokenMail(user, applicationUrl(httpServletRequest), token);
    }

    return url;
  }

  @PostMapping("/savePassword")
  public String savePassword(
      @RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {
    String result = userService.validatePasswordResetToken(token);
    if (!result.equalsIgnoreCase("valid")) {
      return "Invalid Token";
    }
    Optional<UserEntity> user= userService.getUserByPasswordResetToken(token);
    if(user.isPresent()){
      return "Password Reset Successfully";
    }else {
      return "Invalid Token";
    }
  }

  private String passwordResetTokenMail(UserEntity user, String applicationUrl, String token) {
    String url = applicationUrl + "/savePassword?token=" + token;
    log.info("Click the link to reset your password: ", url);
    return url;
  }

  private void resendVerificationTokenMail(
      UserEntity user, String s, VerificationToken verificationToken) {
    //        Send mail to user
    String url = s + "/verifyRegistration?token=" + verificationToken.getToken();

    log.info("Click the link to verify your account: {}", url);
  }
}
