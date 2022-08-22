package com.ll.zzandi.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.ll.zzandi.config.security.UserContext;
import com.ll.zzandi.domain.Interest;
import com.ll.zzandi.domain.User;
import com.ll.zzandi.dto.UserDto;
import com.ll.zzandi.exception.ErrorCode;
import com.ll.zzandi.exception.UserApplicationException;
import com.ll.zzandi.repository.InterestRepository;
import com.ll.zzandi.repository.UserRepository;
import com.ll.zzandi.util.aws.ImageUploadService;
import com.ll.zzandi.util.mail.EmailMessage;
import com.ll.zzandi.util.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final InterestRepository interestRepository;

    private final AmazonS3Client amazonS3Client;
    private final ImageUploadService imageUploadService;

    @Transactional
    public User join(final UserDto.RegisterRequest registerRequest) {
            registerRequest.encodePassword(passwordEncoder);
            User newUser=userRepository.save(User.of(registerRequest));
            newUser.generateEmailCheckToken();
            sendSignUpConfirmEmail(newUser);
            for(int i=0;i<registerRequest.getInterests().size();i++){
                Interest interest=new Interest();
                interest.setInterest(registerRequest.getInterests().get(i));
                interest.setUser(newUser);
                interestRepository.save(interest);
            }
            System.out.println("----------로그인 전------------");
            login(newUser);
            System.out.println("----------로그인 후------------");
            return newUser;
    }

    public void sendSignUpConfirmEmail(User user) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + user.getEmailCheckToken() +
                "&email=" + user.getUserEmail());
        context.setVariable("nickname", user.getUserNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "ZZANDI 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", "http://localhost:8080/user");
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getUserEmail())
                .subject("ZZANDI, 회원 가입 인증")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }
    @Transactional
    public void completeSignUp(User user) {
        user.completeSignUp();
        userRepository.save(user);
        login(user);
    }

    public void login(User account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserContext(account,List.of(new SimpleGrantedAuthority("ROLE_USER"))).getUser(),
                account.getUserPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
        System.out.println("TOKEN 생성완료");
    }

    @Transactional
    public void updateProfile(MultipartFile multipartFile, String userId) throws IOException {
        User user1=userRepository.findByUserId(userId).orElseThrow(RuntimeException::new);
        String originalName=multipartFile.getOriginalFilename();
        String[] name=originalName.split("\\\\");
        final String ext = name[2].substring(name[2].lastIndexOf('.'));
        final String saveFileName = getUuid() + ext;
        String uploadUrl=imageUploadService.upload(saveFileName,multipartFile);
        user1.setUserprofileUrl(uploadUrl);
    }
    private static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}


