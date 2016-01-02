package hudson.plugins.im.bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.scm.ChangeLogSet.Entry;

/**
 * Repository Interaction Command for instant messaging bot
 * 
 * @author Jung-chen, Jinian
 *
 */
@Extension
public class RepoCommand extends AbstractSourceQueryCommand {

	private static final String SYNTAX = " [show <int> | reponumber <int> | all]";
	private static final String HELP = SYNTAX + " - show the repository details of build history";
	public final String REPO = "repo";

	/** All available command names */
	private String availableCommand;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getCommandNames() {
		return Collections.singleton("repo");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CharSequence getMessageForJob(Collection<AbstractProject<?, ?>> projects, String[] args) {

		int number = 0;
		int counter = 5;
		int flag = 1;
		boolean lock = false;
		// Set the lock and counter for different sub-command
		if (args.length >= 2) {
			switch (args[1]) {
			case "show":
				if (args.length > 2)
					counter = Integer.parseInt(args[2]);
				break;
			case "reponumber":
				number = Integer.parseInt(args[2]);
				flag = 0;
				lock = true;
				break;
			case "all":
				flag = 0;
				break;
			default:
				return getAvailableCommand();
			}
		}

		// Initialaze the Collection of Abstractbuild
		Collection<AbstractBuild<?, ?>> builds = new ArrayList<AbstractBuild<?, ?>>();

		// get all the builds
		for (AbstractProject<?, ?> abProj : projects) {

			AbstractBuild<?, ?> tempbuild = abProj.getLastBuild();
			while (tempbuild != null) {
				builds.add(tempbuild);
				tempbuild = tempbuild.getPreviousBuild();
			}
		}

		StringBuilder msg = new StringBuilder(builds.size());
		// analyze the builds get, and form demanded output
		for (AbstractBuild<?, ?> abBuild : builds) {

			if (counter <= 0 && flag == 1) {
				break;
			}
			counter--;
			if (lock == false) {
				msg.append(String.format("Building #: %s", abBuild.getNumber()));
			}

			for (Entry entry : abBuild.getChangeSet()) {
				if ((Integer.parseInt(entry.getCommitId()) == number) || (lock == false)) {
					if (lock) {
						msg.append(String.format("Building #: %s", abBuild.getNumber()));
					}
					msg.append(String.format("\nRevision: %s%s\n* %s: %s", entry.getCommitId(),
							entry.getAffectedPaths(), entry.getAuthor(), entry.getMsg()));
					if (lock) {
						break;
					}
				}
			}
			msg.append("\n");
		}

		if (!msg.toString().contains("Building")) {
			msg.append("Please try again!");
		}
		return msg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getCommandShortName() {
		return REPO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHelp() {
		return HELP;
	}

	/**
	 * 
	 * @return The string sent back to Chat Room
	 */
	private String getAvailableCommand() {
		if (availableCommand == null) {
			StringBuilder buf = new StringBuilder();
			buf.append("Available Command: ");
			buf.append("show [how many last builds wanted], ");
			buf.append("reponumber [the repository number for search], ");
			buf.append("all(be careful to choose, it will take a long time to run) ");
			availableCommand = buf.substring(0, buf.length() - 1);
		}
		return availableCommand;
	}
}
