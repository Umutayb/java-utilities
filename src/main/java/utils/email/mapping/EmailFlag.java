package utils.email.mapping;

import jakarta.mail.Flags;

public enum EmailFlag {
    ANSWERED(Flags.Flag.ANSWERED),
    DELETED(Flags.Flag.DELETED),
    SEEN(Flags.Flag.SEEN),
    USER(Flags.Flag.USER),
    FLAGGED(Flags.Flag.ANSWERED),
    RECENT(Flags.Flag.RECENT),
    DRAFT(Flags.Flag.DRAFT);

    final Flags.Flag flag;

    EmailFlag(Flags.Flag flag){
        this.flag = flag;
    }

    public Flags.Flag getFlag() {
        return flag;
    }
}
