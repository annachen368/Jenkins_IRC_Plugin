// CHECKSTYLE:OFF

package hudson.plugins.ircbot;

import org.jvnet.localizer.Localizable;
import org.jvnet.localizer.ResourceBundleHolder;

@SuppressWarnings({
    "",
    "PMD"
})
public class Messages {

    private final static ResourceBundleHolder holder = ResourceBundleHolder.get(Messages.class);

    /**
     * Started by IRC message from {0}
     * 
     */
    public static String IrcCause_ShortDescription(Object arg1) {
        return holder.format("IrcCause.ShortDescription", arg1);
    }

    /**
     * Started by IRC message from {0}
     * 
     */
    public static Localizable _IrcCause_ShortDescription(Object arg1) {
        return new Localizable(holder, "IrcCause.ShortDescription", arg1);
    }

}
