package utils.email;

import collections.Pair;
import context.ContextStore;
import utils.Printer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;
import static utils.StringUtilities.highlighted;

/**
 * The EmailAcquisition class facilitates the acquisition of emails from an inbox
 * based on specified filters and options.
 */
@SuppressWarnings("unused")
public class EmailAcquisition {

    /**
     * The inbox from which emails are acquired.
     */
    EmailUtilities.Inbox inbox;

    /**
     * Used for logging purposes.
     */
    Printer log = new Printer(EmailAcquisition.class);

    /**
     * The timeout value for email acquisition.
     */
    public int emailAcquisitionTimeout = Integer.parseInt(ContextStore.get("email-acquisition-timeout", "45000"));

    /**
     * Constructs an EmailAcquisition object with the provided email inbox.
     *
     * @param emailInbox The inbox from which emails will be acquired.
     */
    public EmailAcquisition(EmailUtilities.Inbox emailInbox) {
        this.inbox = emailInbox;
    }

    /**
     * Acquires an email based on the specified filter type and key,
     * using default timeout and other options.
     *
     * @param filterType The type of filter to apply.
     * @param filterKey  The value to filter by.
     * @return The absolute path of the saved email.
     */
    public String acquireEmail(EmailUtilities.Inbox.EmailField filterType, String filterKey) {
        return acquireEmail(emailAcquisitionTimeout, false, true, true, Pair.of(filterType, filterKey));
    }

    /**
     * Acquires an email based on the specified filter type, key, and timeout,
     * using default options.
     *
     * @param filterType The type of filter to apply.
     * @param filterKey  The value to filter by.
     * @param timeout    The timeout value for email acquisition.
     * @return The absolute path of the saved email.
     */
    public String acquireEmail(EmailUtilities.Inbox.EmailField filterType, String filterKey, int timeout) {
        return acquireEmail(timeout,false, true, true, Pair.of(filterType, filterKey));
    }

    /**
     * Acquires emails based on the specified parameters and filter pairs.
     *
     * @param timeout              The timeout value for email acquisition.
     * @param print                Specifies whether to print the retrieved messages.
     * @param save                 Specifies whether to save the retrieved messages.
     * @param saveAttachments      Specifies whether to save attachments.
     * @param filterPairs          An array of filter pairs containing the filter type and value.
     * @return The absolute path of the saved email.
     */
    public String acquireEmail(
            int timeout,
            boolean print,
            boolean save,
            boolean saveAttachments,
            Pair<EmailUtilities.Inbox.EmailField, String>... filterPairs
    ) {
        return acquireEmail(timeout, 1, false, true, true, List.of(filterPairs));
    }

    /**
     * Acquires an email and saves it based on the specified settings and filters.
     *
     * @param timeout              the maximum time to wait for the expected message count to be reached, in seconds
     * @param expectedMessageCount the expected number of messages to be loaded
     * @param print                boolean flag indicating whether to print the emails
     * @param save                 boolean flag indicating whether to save the emails
     * @param saveAttachments      boolean flag indicating whether to save email attachments
     * @param filterPairs          a list of pairs consisting of email fields and corresponding filter strings
     * @return the absolute path of the acquired email
     */
    public String acquireEmail(
            int timeout,
            int expectedMessageCount,
            boolean print,
            boolean save,
            boolean saveAttachments,
            List<Pair<EmailUtilities.Inbox.EmailField, String>> filterPairs
    ) {
        for (Pair<EmailUtilities.Inbox.EmailField, String> filter : filterPairs) {
            log.info("Acquiring & saving email(s) by " +
                    highlighted(BLUE, filter.alpha().name()) +
                    highlighted(GRAY, " -> ") +
                    highlighted(BLUE, filter.beta())
            );
        }
        EmailUtilities.Inbox.EmailMessage message =  EmailUtilities.Inbox.getEmail(this.inbox, timeout, expectedMessageCount, print, save, saveAttachments, filterPairs);
        File dir = new File("inbox");
        String absolutePath = null;
        for (File email : Objects.requireNonNull(dir.listFiles()))
            try {
                boolean nullCheck = Files.probeContentType(email.toPath()) != null;
                if (nullCheck && Files.probeContentType(email.toPath()).equals("text/html")) {
                    absolutePath = "file://" + email.getAbsolutePath().replaceAll("#", "%23");
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return absolutePath;
    }
}
