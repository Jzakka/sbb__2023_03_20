package com.mysite.sbb.user;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class MailService {
    private final UserRepository userRepository;
    private final String API_KEY;

    public MailService(@Autowired UserRepository userRepository, @Value("${spring.sendgrid.api-key}") String apiKey) {
        this.userRepository = userRepository;
        this.API_KEY = apiKey;
    }

    public void sendMailTo(String username, String token) {
        Optional<SiteUser> user = userRepository.findByName(username);
        if (user.isEmpty()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }
        Mail mail = buildMail(user.get().getEmail(), token);

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("메일을 보내는 도중 문제가 발생했습니다.");
        }
    }

    private Mail buildMail(String userEmail, String token) {
        Email from = new Email("mouse4786@gmail.com");
        String subject = "비밀번호 초기화를 위한 메일입니다.";
        Email to = new Email(userEmail);
        Content content = new Content("text/html",
                "<h1>비밀번호 초기화를 위한 링크입니다.</h1>\n" +
                "<a href=\"http://localhost:8080/user/reset-password?token=%s\">링크</a>".formatted(token));
        Mail mail = new Mail(from, subject, to, content);
        return mail;
    }
}
