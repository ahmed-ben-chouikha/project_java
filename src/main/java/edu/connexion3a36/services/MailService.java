package edu.connexion3a36.services;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public final class MailService {

    private static final String DEFAULT_SMTP_HOST = "smtp.gmail.com";
    private static final String DEFAULT_SMTP_PORT = "587";

    private MailService() {
    }

    public static void sendEmail(String toEmail, String subject, String plainTextContent) throws MessagingException {
        String username = resolveRequired("GMAIL_SMTP_USERNAME");
        String appPassword = resolveRequired("GMAIL_SMTP_APP_PASSWORD");
        String fromEmail = firstNonBlank(System.getenv("GMAIL_SMTP_FROM"), username);
        String host = firstNonBlank(System.getenv("GMAIL_SMTP_HOST"), DEFAULT_SMTP_HOST);
        String port = firstNonBlank(System.getenv("GMAIL_SMTP_PORT"), DEFAULT_SMTP_PORT);

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject(subject == null ? "" : subject);
        message.setText(plainTextContent == null ? "" : plainTextContent, "UTF-8");

        Transport.send(message);
    }

    private static String resolveRequired(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " is not set.");
        }
        return value.trim();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }
}
