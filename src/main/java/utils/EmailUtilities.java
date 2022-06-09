package utils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;

public class EmailUtilities {

    private static final Printer log = new Printer(EmailUtilities.class);
    public static String host;

    public static Boolean sendEmail(String subject, String content, String receiver, String ID, String Password, Multipart attachment) {

        // Assuming you are sending email from through gmail's smtp
        host = "smtp-relay.sendinblue.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ID, Password);
            }
        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(ID));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(content+"\n");
            if (attachment!=null)
                message.setContent(attachment);

            log.new Info("Sending...");
            Transport.send(message);// Send message
            log.new Success("Sent message successfully!");
            return true;
        }
        catch (MessagingException mex) {log.new Error(mex.getMessage());}
        return false;
    }

}
