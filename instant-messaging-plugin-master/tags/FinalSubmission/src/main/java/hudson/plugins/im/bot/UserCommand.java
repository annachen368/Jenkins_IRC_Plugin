/*
 * Created on Apr 22, 2007
 * @author Hongjeon Yuhang
 */
package hudson.plugins.im.bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.im.Sender;
import hudson.plugins.im.tools.MessageHelper;

/**
 * Display the recent builds issued by a user
 * 
 * @author BookReaders CS 427 Group UIUC
 */
@Extension
public class UserCommand extends AbstractSourceQueryCommand {
	private static final String SYNTAX = " <username>";
	private static final String HELP = SYNTAX + " - prints builds processed by a Jenkins user";
	private static int defaultNumber = 5;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getCommandNames() {
		return Collections.singleton("userHistory");
	}

	private void setDefaultNumber(int newDefualtNumber) {
		defaultNumber = newDefualtNumber;
	}

	private int getDefaultNumber() {
		return defaultNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getReply(Bot bot, Sender sender, String[] args) {
		if (args.length < 2) {
			return giveSyntax(sender.getNickname(), args[0], 1);
		} else if (args.length > 4) {
			return giveSyntax(sender.getNickname(), args[0], 2);
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

	private int getCounter(String[] args) {
		int counter = 0;
		if (args.length == 2) {
			counter = getDefaultNumber();
		} else if (args.length == 3) {
			counter = Integer.parseInt(args[2]);
		} else if (args.length == 4) {
			if (args[2].equals("default") || args[2].equals("Default")) {
				setDefaultNumber(Integer.parseInt(args[3]));
				counter = getDefaultNumber();
			}
		}

		return counter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CharSequence getMessageForJob(Collection<AbstractProject<?, ?>> projects, String[] args) {
		int counter = getCounter(args);
		// ArrayList<ArrayList<String>> list = new
		// ArrayList<ArrayList<String>>();

		Collection<AbstractBuild<?, ?>> builds = new ArrayList<AbstractBuild<?, ?>>();
		for (AbstractProject<?, ?> abProj : projects) {
			AbstractBuild<?, ?> tempbuild = abProj.getLastBuild();
			while (tempbuild != null) {
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
		for (AbstractBuild<?, ?> abBuild : builds) {
			if (counter <= 0)
				break;
			message(msg, abBuild);
			counter--;
		}

		return msg;
	}

	private void message(StringBuilder msg, AbstractBuild<?, ?> abBuild) {
		msg.append(abBuild.getFullDisplayName()).append(" (");
		msg.append(abBuild.getTimestampString()).append(" ago): ");
		msg.append(abBuild.getResult()).append(": ");
		msg.append(MessageHelper.getBuildURL(abBuild)).append("\n");
	}

	/**
	 * {@inheritDoc}
	 */
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

	private String giveSyntax(String sender, String cmd, int status) {
		String retString = "";
		if (status == 1)
			retString = sender + ": syntax is: '" + cmd + SYNTAX + "'";
		else if (status == 2)
			retString = sender + ": syntax is: '" + cmd + SYNTAX + "<Number>" + "' or '" + cmd + SYNTAX
					+ "default <Number>" + "'";
		return retString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHelp() {
		return HELP;
	}
}