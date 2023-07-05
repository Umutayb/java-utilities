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

    /**
     * Creates a new instance of EmailUtilities with the specified host.
     *
     * @param host the hostname of the SMTP server for sending emails
     */
    public EmailUtilities(String host){setHost(host);}

    private static final Printer log = new Printer(EmailUtilities.class);
    private String host;

    /**
     * Sends an email message with an optional attachment to the specified recipient.
     *
     * @param subject the subject of the email
     * @param content the content of the email
     * @param receiver the email address of the recipient
     * @param ID the username for authenticating with the SMTP server
     * @param Password the password for authenticating with the SMTP server
     * @param attachment the optional multipart attachment to include in the email
     * @return true if the email was sent successfully, false otherwise
     */
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

            log.info("Sending...");
            Transport.send(message);// Send message
            log.success("Sent message successfully!");
            return true;
        }
        catch (MessagingException mex) {log.error(mex.getMessage(), mex);}
        return false;
    }

    /**
     * Sets the hostname of the SMTP server used for sending emails.
     *
     * @param host the hostname of the SMTP server
     */
    private void setHost(String host){this.host = host;}

    public static class Inbox {
        private final Printer log = new Printer(Inbox.class);
        private final String host;
        private final String port;
        private final String userName;
        private final String password;
        private final String secureCon;

        /**
         * List of email messages represented as a list of maps where each map contains email fields as keys
         * and their corresponding values as values.
         */
        public List<Map<EmailField,Object>> messages = new ArrayList<>();

        /**
         * Enumeration of email fields used as keys in the map representation of email messages.
         */
        public enum EmailField {SUBJECT, SENDER, CONTENT, INDEX, DATE, ATTACHMENTS}

        /**
         * Constructs a new Inbox object with the specified configuration settings.
         *
         * @param host the hostname of the email server
         * @param port the port number of the email server
         * @param userName the username for authenticating with the email server
         * @param password the password for authenticating with the email server
         * @param secureCon the type of secure connection to use (e.g. "ssl", "tls", "starttls")
         * @param print a boolean indicating whether to print the inbox contents to the console
         * @param save a boolean indicating whether to save the inbox contents to a file
         * @param saveAttachments a boolean indicating whether to save email attachments to files
         */
        public Inbox(String host,
                     String port,
                     String userName,
                     String password,
                     String secureCon,
                     Boolean print,
                     Boolean save,
                     Boolean saveAttachments
        ) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.secureCon = secureCon;
            loadInbox(null, null, print, save, saveAttachments);
        }

        /**
         * Constructs a new Inbox object with the specified configuration settings and inbox filter.
         *
         * @param host the hostname of the email server
         * @param port the port number of the email server
         * @param userName the username for authenticating with the email server
         * @param password the password for authenticating with the email server
         * @param secureCon the type of secure connection to use (e.g. "ssl", "tls", "starttls")
         * @param filterType the type of inbox filter to apply (e.g. "FROM", "TO", "SUBJECT", "BODY")
         * @param filterKey the keyword to use for the inbox filter
         * @param print a boolean indicating whether to print the inbox contents to the console
         * @param save a boolean indicating whether to save the inbox contents to a file
         * @param saveAttachments a boolean indicating whether to save email attachments to files
         */
        public Inbox(
                String host,
                String port,
                String userName,
                String password,
                String secureCon,
                EmailField filterType,
                String filterKey,
                Boolean print,
                Boolean save,
                Boolean saveAttachments
        ) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.secureCon = secureCon;
            loadInbox(filterType, filterKey, print, save, saveAttachments);
        }

        /**
         * Saves an email message body to a file with the given filename in the 'inbox' directory.
         *
         * @param filename the name of the file to be created and saved as.
         * @param messageContent the content of the email message body to be saved.
         *
         * @throws RuntimeException if there is an IOException during the file write operation.
         */
        public void saveMessage(String filename, String messageContent){
            log.info("Saving email body...");
            try (FileWriter file = new FileWriter("inbox/" + filename + ".html")){
                file.write(String.valueOf(messageContent));
                log.info("Saved as \"" + filename + ".html\"");
            }
            catch (IOException e) {throw new RuntimeException(e);}
        }

        /**
         * Loads email messages from the mailbox, filters them based on the provided criteria, and performs specified actions.
         *
         * @param filterType the type of email field to use as a filter. Can be null to skip filtering.
         * @param filterKey the filter criteria to apply to the selected email field.
         * @param print whether to print the contents of the filtered messages.
         * @param save whether to save the contents of the filtered messages.
         * @param saveAttachments whether to save the attachments of the filtered messages.
         *
         * @throws RuntimeException if there is a MessagingException during the process.
         */
        private void loadInbox(EmailField filterType, String filterKey, Boolean print, Boolean save, Boolean saveAttachments){
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
                log.info("Connecting please wait....");
                Store store = session.getStore("pop3");
                store.connect(userName, password);
                Folder folderInbox = store.getFolder("INBOX");
                folderInbox.open(Folder.READ_ONLY);
                log.info("Connected to mail via "+host);
                // opens the inbox folder
                log.info("Getting inbox..");

                // fetches new messages from server
                List<Message> messages = List.of(folderInbox.getMessages());

                for (Message message:messages) {
                    String selector;

                    if (filterType != null){
                        selector = switch (filterType) {
                            case SUBJECT -> message.getSubject();
                            case SENDER -> message.getFrom()[0].toString();
                            case CONTENT -> getContent(message);
                            case INDEX -> String.valueOf(messages.indexOf(message));
                            case DATE -> String.valueOf(message.getSentDate());
                            default -> throw new EnumConstantNotPresentException(EmailField.class, filterKey);
                        };

                        if ((selector.contains(filterKey) || selector.equalsIgnoreCase(filterKey)))
                            resolveMessage(message, messages.indexOf(message), print, save, saveAttachments);
                    }

                    else resolveMessage(message, messages.indexOf(message), print, save, saveAttachments);
                }
                log.info("You have "+this.messages.size()+" new mails in your inbox");
                // disconnect
                folderInbox.close(false);
                store.close();
            }
            catch (MessagingException exception) {log.error(exception.getLocalizedMessage(),exception);}
        }

        /**
         * Resolves the content and attachments of a provided email message, and adds the message data to the instance's messages list.
         *
         * @param message the email message to resolve.
         * @param index the index of the email message in the mailbox.
         * @param print whether or not to print the resolved message content and attachments.
         * @param save whether or not to save the resolved message content.
         * @param saveAttachments whether or not to save the resolved message attachments.
         *
         * @throws Error if there is a MessagingException during the process.
         */
        private void resolveMessage(Message message, Integer index, Boolean print, Boolean save, Boolean saveAttachments){
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
                    log.info("Message #" + index);
                    log.info("From: " + from);
                    log.info("Subject: " + subject);
                    log.info("Sent Date: " + sentDate);
                    log.info("Message: " + messageContent);
                    if (attachments.length()>0) log.info("Attachments: " + attachments);
                }

                if (save) saveMessage("message#" + index, messageContent);
            }
            catch (MessagingException exception){log.error("Could not connect to the message store", exception);}
        }

        /**
         * Retrieves the current status of the connection.
         *
         * @return A string indicating the status of the connection.
         *         Possible values are:
         *         - "connected_to_pop3" if the connection to the POP3 server was successful.
         *         - An error message if there was a problem connecting to the server.
         */
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
                log.info("Connecting please wait....");
                Store store = session.getStore("pop3");
                store.connect(userName, password);
                isconnected = "connected_to_pop3";
                log.info("Is Connected: "+isconnected);
                log.info("Connected to mail via "+host);
            }
            catch (NoSuchProviderException ex) {
                String ex1 ="No provider for pop3.";
                log.warning(ex1);
                return ex1;
            }
            catch (MessagingException ex) {
                String ex2 = "Could not connect to the message store";
                log.warning("Could not connect to the message store");
                return ex2;
            }
            return isconnected;
        }

        /**
         * Retrieves the content of the given email message.
         *
         * @param message the email message from which to retrieve content
         * @return the message content, as a String
         * @throws RuntimeException if there is a problem retrieving the message content
         */
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
                log.error(e.fillInStackTrace().getLocalizedMessage(), e);
                throw new RuntimeException(e);
            }
        }

        /**
         * Retrieves the attachments from the given email message and optionally saves them to the "inbox/attachments" directory.
         *
         * @param message the email message from which to retrieve attachments
         * @param saveAttachments true if attachments should be saved, false otherwise
         * @return a comma-separated string of attachment filenames
         * @throws RuntimeException if there is a problem retrieving or saving attachments
         */
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
                                part.saveFile("inbox/attachments" + File.separator + fileName);
                            }
                        }
                    }
                    if (attachments.length() > 1)
                        attachments = new StringBuilder(attachments.substring(0, attachments.length() - 2));
                }
                return attachments.toString();
            }
            catch (MessagingException | IOException e) {
                log.error(e.fillInStackTrace().getLocalizedMessage(), e);
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
