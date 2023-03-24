package com.mysite.sbb.user;

import com.mysite.sbb.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final MailService mailService;
    private final JwtProvider jwtProvider;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordIncorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFaoled", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/reset")
    public String receiveResetRequest(ResetForm resetForm) {
        return "reset_form";
    }

    @PostMapping("/reset")
    public String receiveResetRequest(@Valid ResetForm resetForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "reset_form";
        }
        SiteUser user = userService.getUser(resetForm.getUsername());
        if (resetForm.getEmail().equals(user.getEmail())) {
            String token = jwtProvider.generateToken(user);
            mailService.sendMailTo(user.getName(), token);
            return "email_sent";
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "사용자의 이메일이 일치하지 않습니다.");
    }

    @GetMapping("/reset-password")
    public String resetPassword(PasswordForm passwordForm, @RequestParam("token") String token) {
        Claims decode = jwtProvider.decode(token);
        Date expiration = decode.getExpiration();
        if (expiration.before(new Date())) {
            throw new JwtException("토큰의 유효기간이 만기되었습니다.");
        }
        passwordForm.setToken(token);
        return "new_password_form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid PasswordForm passwordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "new_password_form";
        }
        String token = passwordForm.getToken();
        Claims decode = jwtProvider.decode(token);
        Date expiration = decode.getExpiration();
        if (expiration.before(new Date())) {
            throw new JwtException("토큰의 유효기간이 만기되었습니다.");
        }

        String username = decode.getSubject();
        SiteUser user = userService.getUser(username);
        LocalDateTime tokenCreatedTime = LocalDateTime.ofInstant(decode.getIssuedAt().toInstant(), ZoneId.systemDefault());
        if (tokenCreatedTime.isBefore(user.getLastModifiedAt())) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "이미 비밀번호를 바꾸셨습니다.");
        }
        userService.resetPassword(user, passwordForm.getPassword());

        return "reset_complete";
    }
}
// 사용자 마지막 수정 -> 토큰 발급 -> 사용자 수정 ->