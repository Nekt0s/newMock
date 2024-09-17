package org.example.mail;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    public static void sendEmail(String recipient, String subject, String content) {
        // Настройки SMTP сервера
        String host = "smtp.gmail.com";
        final String user = "tokarev.60@yandex.ru"; // Измените на ваш Gmail адрес
        final String password = "qtxwavueqqfwacqm"; // Измените на ваш пароль или пароль для приложений

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Получение сессии
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            // Создание объекта MimeMessage
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(content);

            // Отправка сообщения
            Transport.send(message);

            System.out.println("Сообщение успешно отправлено...");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Пример отправки
        String to = "NAlTokarev@sberbank.ru"; // адрес получателя
        String subject = "Тестовое сообщение";
        String content = "Привет! Это тестовое сообщение.";

        sendEmail(to, subject, content);
    }
}
