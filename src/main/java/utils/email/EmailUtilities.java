package utils.email;

import collections.Pair;
import context.ContextStore;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import utils.DateUtilities;
import utils.Printer;
import utils.StringUtilities;
import utils.email.mapping.EmailFlag;
import utils.reflection.ReflectionUtilities;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static utils.StringUtilities.markup;
import static utils.arrays.lambda.Collectors.toSingleton;

/**
 * Utility class for sending and managing emails.
 * <p>
 * This class provides functionality to send emails via SMTP and includes an inner {@link Inbox} class
 * for retrieving, filtering, and managing emails using IMAP or POP3 protocols.
 * </p>
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class EmailUtilities {

    /**
     * Creates a new instance of EmailUtilities with the specified SMTP host.
     *
     * @param host the hostname of the SMTP server for sending emails
     */
    public EmailUtilities(String host) {
        setHost(host);
    }

    private static final Printer log = new Printer(EmailUtilities.class);
    private final boolean keepLogs = Boolean.parseBoolean(ContextStore.get("keep-email-logs", "true"));
    private String host;

    /**
     * Sends an email message with an optional attachment to the specified recipient using default text/plain content type.
     *
     * @param subject    the subject of the email
     * @param content    the body content of the email
     * @param receiver   the email address of the recipient
     * @param ID         the username (sender email) for authenticating with the SMTP server
     * @param password   the password (or application password) for authenticating with the SMTP server
     * @param attachment the optional multipart attachment to include in the email; can be null
     * @return true if the email was sent successfully, false otherwise
     */
    public Boolean sendEmail(String subject, String content, String receiver, String ID, String password, Multipart attachment) {
        return this.sendEmail(
                subject,
                content,
                "text/plain; charset=" + MimeUtility.quote("us-ascii", HeaderTokenizer.MIME),
                receiver,
                ID,
                password,
                attachment
        );
    }

    /**
     * Sends an email message with an optional attachment to the specified recipient with a custom content type.
     * <p>
     * This method configures the SMTP session using port 587 with STARTTLS enabled.
     * </p>
     *
     * @param subject     the subject of the email
     * @param content     the body content of the email
     * @param contentType the MIME type of the content (e.g., "text/html; charset=utf-8")
     * @param receiver    the email address of the recipient
     * @param ID          the username (sender email) for authenticating with the SMTP server
     * @param password    the password (or application password) for authenticating with the SMTP server
     * @param attachment  the optional multipart attachment to include in the email; can be null
     * @return true if the email was sent successfully, false otherwise
     */
    public Boolean sendEmail(String subject, String content, String contentType, String receiver, String ID, String password, Multipart attachment) {
        Properties properties = new Properties();
        properties.putAll(System.getProperties());

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ID, password);
            }
        });

        session.setDebug(keepLogs);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ID));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            message.setSubject(subject);
            message.setContent(content, contentType);
            if (attachment != null)
                message.setContent(attachment);

            if (keepLogs) log.info("Sending...");
            Transport.send(message);
            if (keepLogs) log.success("Sent message successfully!");
            return true;
        } catch (MessagingException mex) {
            log.error(mex.getMessage(), mex);
        }
        return false;
    }

    /**
     * Sets the hostname of the SMTP server used for sending emails.
     *
     * @param host the hostname of the SMTP server
     */
    private void setHost(String host) {
        this.host = host;
    }

    /**
     * Represents an email inbox and provides methods to retrieve, filter, and delete messages.
     * <p>
     * Supports both IMAP and POP3 protocols via the {@link EmailProtocol} enum.
     * </p>
     */
    public static class Inbox {
        private final Printer log = new Printer(Inbox.class);
        private final String host;
        private final String port;
        private final String userName;
        private final String password;
        private final String secureCon;
        private final EmailProtocol protocol;

        /**
         * List of email messages retrieved from the server.
         */
        public static List<EmailMessage> messages = new ArrayList<>();

        /**
         * Enumeration of supported email protocols.
         */
        public enum EmailProtocol {
            IMAP("imap"),
            POP3("pop3");

            private final String value;

            EmailProtocol(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }

        /**
         * Enumeration of email fields used for filtering and mapping.
         */
        public enum EmailField {SUBJECT, SENDER, CONTENT, @Deprecated(since = "1.6.2", forRemoval = true) INDEX, DATE, ATTACHMENTS}

        /**
         * Represents a simplified email message containing common fields.
         */
        public static class EmailMessage {
            String from;
            String sentDate;
            String subject;
            String messageContent;
            String attachments;
            String fileName;

            /**
             * Constructs an EmailMessage object from a jakarta.mail.Message.
             *
             * @param message The jakarta.mail.Message object to construct from.
             */
            public EmailMessage(Message message) {
                try {
                    this.from = message.getFrom()[0].toString();
                    this.subject = message.getSubject();
                    this.messageContent = getContent(message);
                    this.sentDate = String.valueOf(message.getSentDate());
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }

            /**
             * Factory method to create an EmailMessage object from a jakarta.mail.Message.
             *
             * @param message The jakarta.mail.Message object.
             * @return The created EmailMessage object.
             */
            public static EmailMessage from(Message message) {
                return new EmailMessage(message);
            }

            public String getSentDate() {
                return sentDate;
            }

            public void setSentDate(String sentDate) {
                this.sentDate = sentDate;
            }

            public String getSubject() {
                return subject;
            }

            public void setSubject(String subject) {
                this.subject = subject;
            }

            public String getMessageContent() {
                return messageContent;
            }

            public void setMessageContent(String messageContent) {
                this.messageContent = messageContent;
            }

            public String getAttachments() {
                return attachments;
            }

            public void setAttachments(String attachments) {
                this.attachments = attachments;
            }

            public String getFileName() {
                return fileName;
            }

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName + ".html";
            }
        }

        /**
         * Retrieves a single email message matching the provided list of filter criteria.
         *
         * @param filterPairs a list of pairs consisting of email fields and corresponding filter strings
         * @return the unique email message matching the filter criteria
         * @throws RuntimeException if zero or more than one message matches the criteria
         */
        public EmailMessage getMessageBy(List<Pair<EmailField, String>> filterPairs) {
            return messages.stream()
                    .filter(message -> emailMatch(message, filterPairs))
                    .collect(toSingleton());
        }

        /**
         * Retrieves a single email message based on a specific filter type and value.
         *
         * @param filterType  The type of filter to apply (e.g., SUBJECT).
         * @param filterValue The value to filter by.
         * @return The email message matching the specified filter.
         */
        public EmailMessage getMessageBy(EmailField filterType, String filterValue) {
            return getMessageBy(List.of(Pair.of(filterType, filterValue)));
        }

        /**
         * Constructs a new Inbox object using the default IMAP protocol.
         *
         * @param host      the hostname of the email server
         * @param port      the port number of the email server
         * @param userName  the username for authenticating with the email server
         * @param password  the password for authenticating with the email server
         * @param secureCon the type of secure connection (e.g. "ssl")
         */
        public Inbox(String host, String port, String userName, String password, String secureCon) {
            this(host, port, userName, password, secureCon, EmailProtocol.IMAP);
        }

        /**
         * Constructs a new Inbox object with a specified protocol.
         *
         * @param host      the hostname of the email server
         * @param port      the port number of the email server
         * @param userName  the username for authenticating with the email server
         * @param password  the password for authenticating with the email server
         * @param secureCon the type of secure connection (e.g. "ssl")
         * @param protocol  the protocol to use (IMAP or POP3)
         */
        public Inbox(String host, String port, String userName, String password, String secureCon, EmailProtocol protocol) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.secureCon = secureCon;
            this.protocol = protocol;
            messages = new ArrayList<>();
        }

        /**
         * Static convenience method to load emails and retrieve a specific message.
         * <p>
         * This method waits for the expected number of messages to appear before filtering.
         * </p>
         *
         * @param inbox                the Inbox instance to use
         * @param timeout              timeout in seconds to wait for messages
         * @param expectedMessageCount the number of messages expected in the inbox
         * @param print                whether to print message details to the log
         * @param save                 whether to save the message body to a file
         * @param saveAttachments      whether to download attachments
         * @param filterPairs          filters to identify the specific message to return
         * @return the matching EmailMessage
         */
        public static EmailMessage getEmail(Inbox inbox, int timeout, int expectedMessageCount, boolean print, boolean save, boolean saveAttachments, List<Pair<EmailField, String>> filterPairs) {
            load(inbox, timeout, expectedMessageCount, print, save, saveAttachments, filterPairs);
            return inbox.getMessageBy(filterPairs);
        }

        /**
         * Loads emails and returns a message matching the filters immediately.
         *
         * @param print           whether to print message details to the log
         * @param save            whether to save the message body to a file
         * @param saveAttachments whether to download attachments
         * @param filterPairs     filters to identify the specific message to return
         * @return the matching EmailMessage
         */
        public EmailMessage getEmail(boolean print, boolean save, boolean saveAttachments, List<Pair<EmailField, String>> filterPairs) {
            load(print, save, saveAttachments, filterPairs);
            return this.getMessageBy(filterPairs);
        }

        /**
         * Saves an email message body to a file in the 'inbox' directory.
         *
         * @param filename       the name of the file to be created.
         * @param messageContent the content of the email message body.
         * @throws RuntimeException if there is an IOException.
         */
        public void saveMessage(String filename, String messageContent) {
            log.info("Saving email body...");
            try (FileWriter file = new FileWriter("inbox/" + filename)) {
                file.write(String.valueOf(messageContent));
                log.info("Saved as \"" + filename);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Instance method to load emails with a wait condition.
         *
         * @param timeout              maximum time in seconds to wait
         * @param expectedMessageCount minimum number of messages expected
         * @param print                whether to print message details
         * @param save                 whether to save message content
         * @param saveAttachments      whether to save attachments
         * @param filterPairs          filters to apply (affects which messages are processed/saved)
         */
        public void load(int timeout, int expectedMessageCount, boolean print, boolean save, boolean saveAttachments, List<Pair<EmailField, String>> filterPairs) {
            EmailUtilities.Inbox.load(this, timeout, expectedMessageCount, print, save, saveAttachments, filterPairs);
        }

        /**
         * Static method that iteratively attempts to load emails until the expected count is reached or timeout occurs.
         *
         * @param inbox                the inbox instance
         * @param timeout              maximum time in seconds to wait
         * @param expectedMessageCount minimum number of messages expected
         * @param print                whether to print message details
         * @param save                 whether to save message content
         * @param saveAttachments      whether to save attachments
         * @param filterPairs          filters to apply
         */
        public static void load(Inbox inbox, int timeout, int expectedMessageCount, boolean print, boolean save, boolean saveAttachments, List<Pair<EmailField, String>> filterPairs) {
            ReflectionUtilities.iterativeConditionalInvocation(
                    timeout,
                    () -> {
                        inbox.load(print, save, saveAttachments, filterPairs);
                        return messages.size() >= expectedMessageCount;
                    }
            );
        }

        /**
         * Loads emails matching a single filter criterion.
         *
         * @param filterType      the field to filter by
         * @param filterKey       the value to match
         * @param print           whether to print message details
         * @param save            whether to save message content
         * @param saveAttachments whether to save attachments
         */
        public void load(EmailField filterType, String filterKey, boolean print, boolean save, boolean saveAttachments) {
            load(print, save, saveAttachments, List.of(Pair.of(filterType, filterKey)));
        }

        /**
         * Connects to the mail server, retrieves messages, applies filters, and populates the local messages list.
         * <p>
         * Note: This opens the Inbox folder in READ_ONLY mode.
         * </p>
         *
         * @param print           whether to print message details
         * @param save            whether to save message content
         * @param saveAttachments whether to save attachments
         * @param filterPairs     filters to apply
         */
        public void load(boolean print, boolean save, boolean saveAttachments, List<Pair<EmailField, String>> filterPairs) {
            try {
                Store store = createStoreConnection();
                Folder folderInbox = store.getFolder("INBOX");

                folderInbox.open(Folder.READ_ONLY);

                log.info("Connected to mail via " + host + " (" + protocol.name() + ")");
                log.info("Getting inbox..");

                List<Message> messages = new ArrayList<>(List.of(folderInbox.getMessages()));
                Collections.reverse(messages);

                for (Message message : messages) {
                    if (emailMatch(EmailMessage.from(message), filterPairs))
                        resolveMessage(message, messages.indexOf(message), print, save, saveAttachments);
                }
                log.info("You have " + Inbox.messages.size() + " new mails in your inbox");
                folderInbox.close(false);
                store.close();
            } catch (MessagingException exception) {
                log.error(exception.getLocalizedMessage(), exception);
            }
        }

        /**
         * Generates the connection properties based on the selected protocol (IMAP or POP3).
         *
         * @return a Properties object containing host, port, and SSL settings.
         */
        Properties getConnectionProperties() {
            Properties properties = new Properties();
            String proto = protocol.getValue();

            properties.put("mail." + proto + ".host", host);
            properties.put("mail." + proto + ".port", port);

            if (secureCon.equalsIgnoreCase("ssl")) {
                properties.put("mail." + proto + ".ssl.enable", "true");
                properties.setProperty("mail." + proto + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.setProperty("mail." + proto + ".socketFactory.fallback", "false");
                properties.setProperty("mail." + proto + ".socketFactory.port", String.valueOf(port));
            } else {
                properties.put("mail." + proto + ".ssl.enable", "false");
            }

            return properties;
        }

        /**
         * Checks if an email message matches a list of filter criteria.
         *
         * @param emailMessage The email message to check.
         * @param filterPairs  A list of criteria where the message must match the given value for the given field.
         * @return True if the message matches ALL filters, false otherwise.
         */
        public static boolean emailMatch(EmailMessage emailMessage, List<Pair<EmailField, String>> filterPairs) {
            for (Pair<EmailField, String> filterPair : filterPairs) {
                String selector;
                EmailField filterType = filterPair.alpha();
                String filterValue = filterPair.beta();

                if (filterType != null) {
                    selector = switch (filterType) {
                        case SUBJECT -> emailMessage.getSubject();
                        case SENDER -> emailMessage.getFrom();
                        case CONTENT -> emailMessage.getMessageContent();
                        case DATE -> emailMessage.getSentDate();
                        default -> throw new EnumConstantNotPresentException(EmailField.class, filterValue);
                    };
                    if (!(selector.contains(filterValue) || selector.equalsIgnoreCase(filterValue)))
                        return false;
                }
            }
            return true;
        }

        /**
         * Evaluates if an email message satisfies a list of filtering criteria within a specific date range.
         * <p>
         * This method parses the email's sent date (expected format: "EEE MMM dd HH:mm:ss zzz yyyy", English Locale)
         * and ensures it falls inclusively within the provided ZonedDateTime range before applying standard content filters.
         * </p>
         *
         * @param emailMessage the {@link EmailMessage} object to be validated.
         * @param filterPairs  a {@link List} of {@link Pair} objects for content filtering.
         * @param start        the inclusive start of the date range.
         * @param end          the inclusive end of the date range.
         * @return {@code true} if the message is within the date range and matches all filters; {@code false} otherwise.
         */
        public static boolean emailMatch(
                EmailMessage emailMessage,
                List<Pair<EmailField, String>> filterPairs,
                ZonedDateTime start,
                ZonedDateTime end
        ) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            ZonedDateTime sentDate = ZonedDateTime.parse(emailMessage.getSentDate(), formatter);

            boolean isWithinRange = !sentDate.isBefore(start) && !sentDate.isAfter(end);

            if (!isWithinRange) {
                return false;
            }

            return emailMatch(emailMessage, filterPairs);
        }

        /**
         * Helper method to process a raw Jakarta Message into an internal EmailMessage and handle I/O (printing/saving).
         *
         * @param message         The raw message.
         * @param index           The index of the message in the list.
         * @param print           Whether to log details.
         * @param save            Whether to save body to disk.
         * @param saveAttachments Whether to save attachments to disk.
         */
        private void resolveMessage(Message message, Integer index, Boolean print, Boolean save, Boolean saveAttachments) {
            try {
                String from = message.getFrom()[0].toString();
                String sentDate = String.valueOf(message.getSentDate());
                String subject = message.getSubject();
                String messageContent = getContent(message);
                String attachments = getAttachments(message, saveAttachments);

                EmailMessage emailMessage = EmailMessage.from(message);
                emailMessage.setFileName(String.format("message#%s", DateUtilities.getDate().getTimeInMillis()));

                messages.add(emailMessage);

                if (print) {
                    log.info("Message #" + index);
                    log.info("From: " + from);
                    log.info("Subject: " + subject);
                    log.info("Sent Date: " + sentDate);
                    log.info("Message: " + messageContent);
                    if (!attachments.isEmpty()) log.info("Attachments: " + attachments);
                }

                if (save) saveMessage(emailMessage.getFileName(), messageContent);
            } catch (MessagingException exception) {
                log.error("Could not connect to the message store", exception);
            }
        }

        /**
         * Tests the connection to the email server.
         *
         * @return A status string ("connected_to_imap" or "connected_to_pop3") or an error message.
         */
        public String getConnectionStatus() {
            String status = "";
            try {
                Store store = createStoreConnection();
                status = "connected_to_" + protocol.getValue();
                log.info("Is Connected: " + status);
                log.info("Connected to mail via " + host);
                store.close();
            } catch (NoSuchProviderException ex) {
                String ex1 = "No provider for " + protocol.getValue() + ".";
                log.warning(ex1);
                return ex1;
            } catch (MessagingException ex) {
                String ex2 = "Could not connect to the message store";
                log.warning(ex2);
                return ex2;
            }
            return status;
        }

        /**
         * Extracts the text content from a message, handling Multipart/Alternative structures.
         *
         * @param message the email message
         * @return the content string (plain text or HTML)
         * @throws RuntimeException if extraction fails
         */
        public static String getContent(Message message) {
            try {
                String messageContent = "";
                String contentType = message.getContentType();
                if (contentType.contains("multipart")) {
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) messageContent = getText(message);
                        else messageContent = part.getContent().toString();
                    }
                } else if ((contentType.contains("text/plain") || contentType.contains("text/html") && message.getContent() != null))
                    messageContent = message.getContent().toString();
                return messageContent;
            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        /**
         * Extracts attachments from a message and optionally saves them to 'inbox/attachments'.
         *
         * @param message         the email message
         * @param saveAttachments true to save files to disk
         * @return a comma-separated string of attachment filenames
         */
        private String getAttachments(Message message, Boolean saveAttachments) {
            StringBuilder attachments = new StringBuilder();
            try {
                String contentType = message.getContentType();
                if (contentType.contains("multipart")) {
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            if (saveAttachments) {
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
            } catch (MessagingException | IOException e) {
                log.error(e.fillInStackTrace().getLocalizedMessage(), e);
                throw new RuntimeException(e);
            }
        }

        /**
         * recursively retrieves the best text candidate (Html/Plain) from a Part.
         *
         * @param part the MIME part
         * @return the text content or null
         * @throws MessagingException if email parsing fails
         * @throws IOException        if stream reading fails
         */
        private static String getText(Part part) throws MessagingException, IOException {
            if (part.isMimeType("text/*")) {
                return (String) part.getContent();
            }
            if (part.isMimeType("multipart/alternative")) {
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
            } else if (part.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    String s = getText(mp.getBodyPart(i));
                    if (s != null) return s;
                }
            }
            return null;
        }

        /**
         * Clears all messages from the inbox for the given credentials using the default IMAP protocol.
         *
         * @param email                    the email address
         * @param emailApplicationPassword the password
         * @param host                     the host
         * @param port                     the port
         * @param secureCon                security connection type
         */
        public static void clearInbox(String email, String emailApplicationPassword, String host, String port, String secureCon) {
            new Printer(Inbox.class).info("Flushing email inbox...");
            Inbox inbox = new EmailUtilities.Inbox(host, port, email, emailApplicationPassword, secureCon, EmailProtocol.IMAP);
            inbox.clearInbox();
        }

        /**
         * Creates a store connection using the configured protocol.
         *
         * @return The connected Store object.
         * @throws MessagingException if connection fails.
         */
        private Store createStoreConnection() throws MessagingException {
            Properties properties = getConnectionProperties();
            Session session = Session.getInstance(properties);
            log.info("Connecting please wait...");
            Store store = session.getStore(protocol.getValue());
            store.connect(userName, password);
            return store;
        }

        /**
         * Marks messages as DELETED if they match the provided filters.
         * <p>
         * Requires a READ_WRITE folder connection.
         * </p>
         *
         * @param filterPairs filters to identify messages to delete
         */
        public void clearInbox(List<Pair<EmailField, String>> filterPairs) {
            try {
                Store store = createStoreConnection();
                Folder folderInbox = store.getFolder("INBOX");
                folderInbox.open(Folder.READ_WRITE);

                log.info("Getting inbox..");
                List<Message> messages = List.of(folderInbox.getMessages());

                log.info("Deleting messages..");
                for (Message message : messages)
                    if (emailMatch(EmailMessage.from(message), filterPairs))
                        message.setFlag(Flags.Flag.DELETED, true);

                folderInbox.close(true);
                store.close();
                log.info(messages.size() + " messages have been successfully deleted!");
            } catch (MessagingException exception) {
                log.error(exception.getLocalizedMessage(), exception);
            }
        }

        /**
         * Applies a specific EmailFlag (e.g., DELETED, SEEN) to messages matching the provided filters.
         *
         * @param flag        the flag to apply
         * @param filterPairs variable arguments of filters
         */
        @SafeVarargs
        public final void clearInbox(EmailFlag flag, Pair<EmailField, String>... filterPairs) {
            try {
                Store store = createStoreConnection();
                Folder folderInbox = store.getFolder("INBOX");
                folderInbox.open(Folder.READ_WRITE);

                log.info("Getting inbox..");
                List<Message> messages = List.of(folderInbox.getMessages());

                log.info("Marking messages as " + markup(StringUtilities.Color.BLUE, flag.name()) + "...");
                int markedMessageCounter = 0;
                for (Message message : messages)
                    if (emailMatch(EmailMessage.from(message), List.of(filterPairs))) {
                        message.setFlag(flag.getFlag(), true);
                        markedMessageCounter += 1;
                    }

                folderInbox.close(true);
                store.close();
                log.info(markedMessageCounter + " messages have been marked as " + flag.name() + "!");
            } catch (MessagingException exception) {
                log.error(exception.getLocalizedMessage(), exception);
            }
        }

        /**
         * Deletes all messages in the inbox.
         */
        public void clearInbox() {
            try {
                Store store = createStoreConnection();
                Folder folderInbox = store.getFolder("INBOX");
                folderInbox.open(Folder.READ_WRITE);

                log.info("Getting inbox..");
                List<Message> messages = List.of(folderInbox.getMessages());

                log.info("Deleting messages..");
                for (Message message : messages) {
                    message.setFlag(Flags.Flag.DELETED, true);
                }

                folderInbox.close(true);
                store.close();
                log.info(messages.size() + " messages have been successfully deleted!");
            } catch (MessagingException exception) {
                log.error(exception.getLocalizedMessage(), exception);
            }
        }

        /**
         * Deletes messages in the inbox in batches to handle large volumes.
         *
         * @param batchSize the maximum number of messages to delete in this operation
         */
        public void clearInboxInBatches(int batchSize) {
            try {
                Store store = createStoreConnection();
                Folder folderInbox = store.getFolder("INBOX");
                folderInbox.open(Folder.READ_WRITE);

                log.info("Getting inbox..");
                List<Message> messages = List.of(folderInbox.getMessages());

                if (messages.isEmpty()) {
                    log.info("Inbox is empty.");
                    folderInbox.close(false);
                    store.close();
                    return;
                }

                int safeBatchSize = Math.min(batchSize, messages.size());
                List<Message> batchToDelete = messages.subList(0, safeBatchSize);

                log.info("Deleting messages..");
                for (Message message : batchToDelete) {
                    message.setFlag(Flags.Flag.DELETED, true);
                }

                folderInbox.close(true);
                store.close();
                log.info(batchToDelete.size() + " messages out of " + messages.size() + " have been successfully deleted!");
            } catch (MessagingException exception) {
                log.error(exception.getLocalizedMessage(), exception);
            }
        }
    }
}