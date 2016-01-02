// CHECKSTYLE:OFF

package hudson.plugins.im.build_notify;

import org.jvnet.localizer.Localizable;
import org.jvnet.localizer.ResourceBundleHolder;

@SuppressWarnings({
    "",
    "PMD"
})
public class Messages {

    private final static ResourceBundleHolder holder = ResourceBundleHolder.get(Messages.class);

    /**
     * Yippee, build fixed!
     * 
     * 
     */
    public static String SummaryOnlyBuildToChatNotifier_BuildIsFixed() {
        return holder.format("SummaryOnlyBuildToChatNotifier.BuildIsFixed");
    }

    /**
     * Yippee, build fixed!
     * 
     * 
     */
    public static Localizable _SummaryOnlyBuildToChatNotifier_BuildIsFixed() {
        return new Localizable(holder, "SummaryOnlyBuildToChatNotifier.BuildIsFixed");
    }

    /**
     * Starting build {0} for job {1}
     * 
     */
    public static String SummaryOnlyBuildToChatNotifier_StartMessage(Object arg1, Object arg2) {
        return holder.format("SummaryOnlyBuildToChatNotifier.StartMessage", arg1, arg2);
    }

    /**
     * Starting build {0} for job {1}
     * 
     */
    public static Localizable _SummaryOnlyBuildToChatNotifier_StartMessage(Object arg1, Object arg2) {
        return new Localizable(holder, "SummaryOnlyBuildToChatNotifier.StartMessage", arg1, arg2);
    }

    /**
     * Project {0} build {1}: {2} in {3}: {4}
     * 
     */
    public static String SummaryOnlyBuildToChatNotifier_Summary(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return holder.format("SummaryOnlyBuildToChatNotifier.Summary", arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * Project {0} build {1}: {2} in {3}: {4}
     * 
     */
    public static Localizable _SummaryOnlyBuildToChatNotifier_Summary(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return new Localizable(holder, "SummaryOnlyBuildToChatNotifier.Summary", arg1, arg2, arg3, arg4, arg5);
    }

}
