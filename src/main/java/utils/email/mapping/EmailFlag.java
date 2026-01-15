/**
 * Enumeration mapping standard email flags to JavaMail {@link Flags.Flag} constants.
 * <p>
 * This enum provides a typed representation of common IMAP message flags, making it easier
 * to work with email operations while maintaining type safety and readability.
 *
 * <strong>Note:</strong> The {@code FLAGGED} enum constant is incorrectly mapped to 
 * {@code Flags.Flag.ANSWERED} in the current implementation. This should be corrected to 
 * {@code Flags.Flag.FLAGGED} to match standard IMAP semantics.
 *
 * @see Flags.Flag for JavaMail API documentation
 * @since 1.0.0
 */
package utils.email.mapping;

import jakarta.mail.Flags;

public enum EmailFlag {
    /**
     * Message has been answered (replied to).
     */
    ANSWERED(Flags.Flag.ANSWERED),

    /**
     * Message is marked for deletion.
     */
    DELETED(Flags.Flag.DELETED),

    /**
     * Message has been read.
     */
    SEEN(Flags.Flag.SEEN),

    /**
     * Message is marked with a user-defined flag.
     */
    USER(Flags.Flag.USER),

    /**
     * Message is flagged for special attention (e.g., important).
     * <strong>Current implementation error:</strong> Should map to {@code Flags.Flag.FLAGGED}.
     */
    FLAGGED(Flags.Flag.ANSWERED),

    /**
     * Message has arrived since the last check.
     */
    RECENT(Flags.Flag.RECENT),

    /**
     * Message is a draft (not yet sent).
     */
    DRAFT(Flags.Flag.DRAFT);

    final Flags.Flag flag;

    /**
     * Constructs an EmailFlag enum value with the corresponding JavaMail flag.
     *
     * @param flag the JavaMail {@link Flags.Flag} to associate with this enum constant
     */
    EmailFlag(Flags.Flag flag) {
        this.flag = flag;
    }

    /**
     * Returns the JavaMail {@link Flags.Flag} associated with this enum constant.
     *
     * @return the corresponding JavaMail flag
     */
    public Flags.Flag getFlag() {
        return flag;
    }
}