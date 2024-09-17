package org.example.sber;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    public static void main(String[] args) {
        final String username = "your_email@example.com"; // Замените на ваш адрес электронной почты
        final String password = "your_password"; // Замените на ваш пароль электронной почты

        // Установка свойств для подключения к серверу SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.example.com"); // Замените на адрес SMTP-сервера вашего почтового провайдера
        props.put("mail.smtp.port", "587"); // Порт SMTP-сервера

        // Создание сессии с аутентификацией
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Создание сообщения
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("recipient@example.com")); // Замените на адрес получателя
            message.setSubject("Привет, это проверка кода");
            message.setText("Привет, это проверка кода");

            // Отправка сообщения
            Transport.send(message);

            System.out.println("Письмо успешно отправлено.");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Ошибка при отправке письма: " + e.getMessage());
        }
    }
}
