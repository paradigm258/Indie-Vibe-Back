package com.swp493.ivb.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.swp493.ivb.common.user.EntityUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailUtils {

    public static final String CONFIRM_EMAIL_ENDPOINT = "/activation";

    @Autowired
    JavaMailSender emailSender;

    @Autowired
    ConfirmTokenUtils confirmUtils;

    @Value("${MAIL_USERNAME}")
    private String serverEmail;

    @Value("${HOST:http://localhost:5000}")
    private String host;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendConfirmEmail(EntityUser user) throws MessagingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        final Context ctx = new Context();
        final MimeMessage mimeMessage = this.emailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        String path =  host + CONFIRM_EMAIL_ENDPOINT;
        UriComponentsBuilder uBuilder = 
            UriComponentsBuilder.fromUriString(path)  
                                .queryParam("id", user.getId())
                                .queryParam("activateToken", confirmUtils.generateConfirmToken(user));
        String url = uBuilder.toUriString();
        ctx.setVariable("url", url);
        ctx.setVariable("host", host);
        ctx.setVariable("logo", "logo");
        String htmlText = templateEngine.process("ConfirmEmailTemplate.html", ctx);
        message.setFrom(serverEmail);
        message.setTo(user.getEmail());
        message.setSubject("Email Confirmation");
        message.setText(htmlText,true);
        ClassPathResource imageSource = new ClassPathResource("static/logo192.png");
        message.addInline("logo", imageSource,"image/png");
        this.emailSender.send(mimeMessage);
        templateEngine.clearTemplateCache();
    }

    public void sendExpireWarning(EntityUser user){

    }

    public void sendResetPassword(EntityUser user, String password) throws MessagingException {
        final Context ctx = new Context();
        final MimeMessage mimeMessage = this.emailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        ctx.setVariable("logo", "logo");
        ctx.setVariable("password", password);
        String htmlText = templateEngine.process("ResetPasswordTemplate.html", ctx);
        message.setFrom(serverEmail);
        message.setTo(user.getEmail());
        message.setSubject("Reset password");
        message.setText(htmlText,true);
        // ClassPathResource imageSource = new ClassPathResource("static/logo192.png");
        // message.addInline("logo", imageSource,"image/png");
        this.emailSender.send(mimeMessage);
        templateEngine.clearTemplateCache();
    }
}