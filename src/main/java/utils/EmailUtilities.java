package utils;

import static utils.EmailUtilities.Inbox.EmailField.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import javax.mail.*;
import java.util.*;

@SuppressWarnings("unused")
public class EmailUtilities {

    public EmailUtilities(String host){setHost(host);}

    private static final Printer log = new Printer(EmailUtilities.class);
    private String host;

    public Boolean sendEmail(String subject, String content, String receiver, String ID, String Password, Multipart attachment) {

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
        catch (MessagingException mex) {log.new Error(mex.getMessage(), mex);}
        return false;
    }

    private void setHost(String host){this.host = host;}

    public static class Inbox {
        private final Printer log = new Printer(Inbox.class);
        private final String host;
        private final String port;
        private final String userName;
        private final String password;
        private final String secureCon;

        private Boolean save = false;

        public List<Map<EmailField,Object>> messages = new ArrayList<>();

        public enum EmailField {SUBJECT, SENDER, CONTENT, INDEX, DATE, ATTACHMENTS}

        public Inbox(String host,
                     String port,
                     String userName,
                     String password,
                     String secureCon,
                     Boolean print,
                     Boolean saveAttachments) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.secureCon = secureCon;
            loadInbox(null, null, print, saveAttachments);
        }

        public Inbox(String host,
                     String port,
                     String userName,
                     String password,
                     String secureCon,
                     Boolean print,
                     Boolean saveAttachments,
                     Boolean save) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.secureCon = secureCon;
            loadInbox(null, null, print, saveAttachments);
            this.save = save;
        }

        public Inbox(
                String host,
                String port,
                String userName,
                String password,
                String secureCon,
                EmailField filterType,
                String filterKey,
                Boolean print,
                Boolean saveAttachments) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.secureCon = secureCon;
            loadInbox(filterType, filterKey, print, saveAttachments);
        }

        public Inbox(
                String host,
                String port,
                String userName,
                String password,
                String secureCon,
                EmailField filterType,
                String filterKey,
                Boolean print,
                Boolean saveAttachments,
                Boolean save) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.secureCon = secureCon;
            loadInbox(filterType, filterKey, print, saveAttachments);
            this.save = save;
        }

        public void saveMessage(String filename, String messageContent){
            log.new Info("Saving email body...");
            try (FileWriter file = new FileWriter("email/" + filename + ".html")){
                file.write(String.valueOf(messageContent));
                log.new Info("Saved as \"" + filename + ".html\"");
            }
            catch (IOException e) {throw new RuntimeException(e);}
        }

        private void loadInbox(EmailField filterType, String filterKey, Boolean print, Boolean saveAttachments){
            Properties properties = new Properties();

            //---------- Server Setting---------------
            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", port);
            if(secureCon.equalsIgnoreCase("ssl")){properties.put("mail.smtp.ssl.enable", "true");}
            else{properties.put("mail.smtp.ssl.enable", "false");}
            //---------- SSL setting------------------
            properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.pop3.socketFactory.fallback", "false");
            properties.setProperty("mail.pop3.socketFactory.port", String.valueOf(port));
            Session session = Session.getDefaultInstance(properties);
            //----------------------------------------

            try {
                log.new Info("Connecting please wait....");
                Store store = session.getStore("pop3");
                store.connect(userName, password);
                Folder folderInbox = store.getFolder("INBOX");
                folderInbox.open(Folder.READ_ONLY);
                log.new Info("Connected to mail via "+host);
                // opens the inbox folder
                log.new Info("Getting inbox..");

                // fetches new messages from server
                List<Message> messages = List.of(folderInbox.getMessages());

                for (Message message:messages) {
                    String selector;

                    if (filterType != null){
                        switch (filterType){
                            case SUBJECT:
                                selector = message.getSubject();
                                break;

                            case SENDER:
                                selector = message.getFrom()[0].toString();
                                break;

                            case CONTENT:
                                selector = getContent(message);
                                break;

                            case INDEX:
                                selector = String.valueOf(messages.indexOf(message));
                                break;

                            case DATE:
                                selector = String.valueOf(message.getSentDate());
                                break;

                            default:
                                throw new EnumConstantNotPresentException(EmailField.class,filterKey);
                        }

                        if ((selector.contains(filterKey) || selector.equalsIgnoreCase(filterKey)))
                            resolveMessage(message, messages.indexOf(message), print, saveAttachments);
                    }

                    else resolveMessage(message, messages.indexOf(message), print, saveAttachments);
                }
                log.new Info("You have "+this.messages.size()+" new mails in your inbox");
                // disconnect
                folderInbox.close(false);
                store.close();
            }
            catch (MessagingException exception) {log.new Error(exception.getCause().getMessage(),exception);}
        }

        private void resolveMessage(Message message, Integer index, Boolean print, Boolean saveAttachments){
            try {
                String from = message.getFrom()[0].toString();
                Date sentDate = message.getSentDate();
                String subject = message.getSubject();
                String messageContent = getContent(message);
                String attachments = getAttachments(message,saveAttachments);

                Map<EmailField,Object> messageMap = new HashMap<>();

                messageMap.put(INDEX, index);
                messageMap.put(SENDER, from);
                messageMap.put(SUBJECT, subject);
                messageMap.put(DATE, sentDate);
                messageMap.put(CONTENT, messageContent);

                if (attachments.length()>0) messageMap.put(ATTACHMENTS, attachments);

                this.messages.add(messageMap);

                if (print){
                    log.new Info("Message #" + index);
                    log.new Info("From: " + from);
                    log.new Info("Subject: " + subject);
                    log.new Info("Sent Date: " + sentDate);
                    log.new Info("Message: " + messageContent);
                    if (attachments.length()>0) log.new Info("Attachments: " + attachments);
                }

                if (save) saveMessage("message#" + index, messageContent);
            }
            catch (MessagingException exception){log.new Error("Could not connect to the message store", exception);}
        }

        public String getConnectionStatus(){
            Properties properties = new Properties();

            //---------- Server Setting---------------
            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", port);
            properties.put("mail.smtp.ssl.enable", "true");
            //---------- SSL setting------------------
            properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.pop3.socketFactory.fallback", "false");
            properties.setProperty("mail.pop3.socketFactory.port", String.valueOf(port));
            Session session = Session.getDefaultInstance(properties);
            //----------------------------------------
            String isconnected="";
            try {
                // connects to the message store
                log.new Info("Connecting please wait....");
                Store store = session.getStore("pop3");
                store.connect(userName, password);
                isconnected = "connected_to_pop3";
                log.new Info("Is Connected: "+isconnected);
                log.new Info("Connected to mail via "+host);
            }
            catch (NoSuchProviderException ex) {
                String ex1 ="No provider for pop3.";
                log.new Warning(ex1);
                return ex1;
            }
            catch (MessagingException ex) {
                String ex2 = "Could not connect to the message store";
                log.new Warning("Could not connect to the message store");
                return ex2;
            }
            return isconnected;
        }

        private String getContent(Message message){
            try {
                String messageContent = "";
                String contentType = message.getContentType();
                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) messageContent = getText(message);
                        else messageContent = part.getContent().toString();
                    }
                }
                else if ((contentType.contains("text/plain") || contentType.contains("text/html") && message.getContent() != null))
                    messageContent = message.getContent().toString();
                return messageContent;
            }
            catch (MessagingException | IOException e) {
                log.new Error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        }

        private String getAttachments(Message message, Boolean saveAttachments){
            StringBuilder attachments = new StringBuilder();
            try {
                String contentType = message.getContentType();
                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            if (saveAttachments){ // Attachment
                                String fileName = part.getFileName();
                                attachments.append(fileName).append(", ");
                                part.saveFile("email/attachments" + File.separator + fileName);
                            }
                        }
                    }
                    if (attachments.length() > 1)
                        attachments = new StringBuilder(attachments.substring(0, attachments.length() - 2));
                }
                return attachments.toString();
            }
            catch (MessagingException | IOException e) {
                log.new Error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        }

        /**
         *  This method is used to handle MIME message.
         *  a message with an attachment is represented in MIME as a multipart message.
         *  In the simple case, the results of the Message object's getContent method will be a MimeMultipart object.
         *  The first body part of the multipart object wil be the main text of the message.
         *  The other body parts will be attachments.
         * @param part is the body
         * @return returns the body of the email
         */
        private String getText(Part part) throws MessagingException, IOException {

            if (part.isMimeType("text/*")) {return (String) part.getContent();}

            if (part.isMimeType("multipart/alternative")) {
                // prefer html text over plain text
                Multipart multipart = (Multipart) part.getContent();
                String text = null;
                for (int i = 0; i < multipart.getCount(); i++) {
                    Part bodyPart = multipart.getBodyPart(i);
                    String content = getText(bodyPart);
                    if (bodyPart.isMimeType("text/plain") && text == null) text = content;
                    else if (bodyPart.isMimeType("text/html") && content != null) return content;
                    else return content;
                }
                return text;
            }
            else if (part.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    String s = getText(mp.getBodyPart(i));
                    if (s != null) return s;
                }
            }
            return null;
        }
    }
}
