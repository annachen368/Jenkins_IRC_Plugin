/*
 * Created on Apr 22, 2007
 * @author Hongjeon Yuhang
 */
package hudson.plugins.im.bot;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.cigame.UserScoreProperty;
import hudson.plugins.im.Sender;
import hudson.plugins.im.tools.MessageHelper;
import hudson.tasks.Mailer;

import java.util.*;


/**
 * @author BookReaders CS 427 Group UIUC
 */


@Extension
public class UserCommand extends AbstractSourceQueryCommand {
	private static final String SYNTAX = " <username>";
	private static final String HELP = SYNTAX + " - prints builds processed by a Jenkins user";
	
    @Override
    public Collection<String> getCommandNames() {
    	return Collections.singleton("userHistory");
    }
    
    @Override
	protected String getReply(Bot bot, Sender sender, String[] args) {
    	if (args.length < 2) {
		    return giveSyntax(sender.getNickname(), args[0]);
		}
		String userName = args[1];
		User user = User.get(userName, false, Collections.emptyMap());
		if (user != null) {
		    
		    String checkPermission = checkPermission(user, sender);
		    if (checkPermission != null) {
		        return checkPermission;
		    }
		    
			Collection<AbstractProject<?, ?>> projects = new ArrayList<AbstractProject<?, ?>>();
	        try {
	            getProjects(sender, args, projects);
	        } catch (CommandException e) {
	            return getErrorReply(sender, e);
	        }

	        StringBuilder buf = new StringBuilder();
			buf.append(userName).append(":\n");
			
	        if (!projects.isEmpty()) {
	        	buf.append(getCommandShortName()).append(" of all projects:\n");
	        	buf.append(getMessageForJob(projects, args));
	            return buf.toString();
	        } else {
	            return sender + ": no job found";
	        }
	        
		} else {
			return sender.getNickname() + ": don't know a user named " + userName;
		}
    }
    
    @Override
    protected CharSequence getMessageForJob(Collection<AbstractProject<?, ?>> projects, String[] args) {

    	// convert projects Collection to ArrayList
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        
        // get all builds
        // if this command is slow may need to put limiter on while loop
        Collection<AbstractBuild<?,?>> builds = new ArrayList<AbstractBuild<?,?>>();
        for (AbstractProject<?, ?> abProj: projects) {
            AbstractBuild<?,?> tempbuild = abProj.getLastBuild();
            while(tempbuild != null){
                builds.add(tempbuild);
                tempbuild = tempbuild.getPreviousBuild();
            }
        }
        
        builds = new GetUserHistory(builds, args[1]);
        if (builds.isEmpty()) {
            StringBuilder temp = new StringBuilder(32);
            temp.append("No Builds Found! \n");
            return temp;
        }
        StringBuilder msg = new StringBuilder(builds.size());
        for (AbstractBuild<?, ?> abBuild: builds) {
            msg.append(abBuild.getFullDisplayName()).append(" : ").append(abBuild.getNumber()).append(" (")
                .append(abBuild.getTimestampString()).append(" ago): ").append(abBuild.getResult()).append(": ")
                .append(MessageHelper.getBuildURL(abBuild)).append("\n");
        }
        return msg;
    }



    @Override
    protected String getCommandShortName() {
        return "detailed history";
    }

    private String checkPermission(User user, Sender sender) {
        if (!user.hasPermission(Hudson.READ)) {
            return sender.getNickname() + ": you may not read that user!"; 
        }
        return null;
    }
    
    private String giveSyntax(String sender, String cmd) {
		return sender + ": syntax is: '" + cmd +  SYNTAX + "'";
	}
    
    @Override
	public String getHelp() {
		return HELP;
    }
}