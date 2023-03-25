package com.mysite.sbb.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class UserSecurityServiceTest {
    @Autowired
    private MailService mailService;

    @Test
    void 메일_발송_테스트() {
        mailService.sendMailTo("user1");
    }
}