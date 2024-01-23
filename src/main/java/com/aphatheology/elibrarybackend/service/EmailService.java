package com.aphatheology.elibrarybackend.service;

import com.aphatheology.elibrarybackend.dto.EmailPayloadDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    @Value("${app.url.frontend}")
    private String FRONTEND_URL;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(EmailPayloadDto emailPayloadDto) {
        Context context = new Context();
        if("email-verification".equals(emailPayloadDto.getTemplateName())) {
            context.setVariable("content", emailPayloadDto.getContent());
            context.setVariable("contentHeader", emailPayloadDto.getContentHeader());
            context.setVariable("receiverName", emailPayloadDto.getReceiverName());
            context.setVariable("appUrl", FRONTEND_URL);
            context.setVariable("token", emailPayloadDto.getToken());
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            helper.setTo(emailPayloadDto.getReceiverEmail());
            helper.setSubject(emailPayloadDto.getSubject());
            helper.setFrom("support@campipal.com", "eLibrary Campipal");
            String htmlContent = templateEngine.process(emailPayloadDto.getTemplateName(), context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.info("Failed to send email. Error: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.info("UnsupportedEncoding. Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
