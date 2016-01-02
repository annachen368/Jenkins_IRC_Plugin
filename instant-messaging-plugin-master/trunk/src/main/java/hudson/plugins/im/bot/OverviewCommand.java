/*
 * Created on Oct 25, 2015
 */
package hudson.plugins.im.bot;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.HealthReport;
import hudson.plugins.im.tools.MessageHelper;
import hudson.scm.ChangeLogSet.Entry;

/**
 * Displays the health for one or several jobs.
 * 
 * @author Anna, Scott, Zehao, Yuhang
 */
@Extension
public class OverviewCommand extends AbstractMultipleJobCommand {

	private final String OVERVIEW = "overview";
	private final String LINE_BREAK = "\n";

	private static final String SYNTAX = " [o | overview]";
	private static final String HELP = SYNTAX + " - show the overview of a specific job";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getCommandNames() {
		return Arrays.asList(OVERVIEW, "o");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CharSequence getMessageForJob(AbstractProject<?, ?> project) {
		StringBuilder msg = new StringBuilder(32);
		String projectName = project.getFullDisplayName();
		msg.append(projectName);
		String spaces = getSpaces(projectName);
		String newline = LINE_BREAK + spaces;

		if (project.isDisabled()) {
			msg.append("(disabled)");
		} else if (project.isBuilding()) {
			msg.append(String.format("(BUILDING: %s)", project.getLastBuild().getDurationString()));
		} else if (project.isInQueue()) {
			msg.append("(in queue)");
		}
		msg.append(": ");

		AbstractBuild<?, ?> lastBuild = project.getLastBuild();
		while ((lastBuild != null) && lastBuild.isBuilding()) {
			lastBuild = lastBuild.getPreviousBuild();
		}
		if (lastBuild != null) {
			List<HealthReport> reports = project.getBuildHealthReports();
			if (reports.isEmpty()) {
				reports = Collections.singletonList(project.getBuildHealth());
			}
			// display health information
			int i = 1;
			for (HealthReport health : reports) {

				msg.append(String.format("%s(%s%%)", health.getDescription(), health.getScore()));
				if (i < reports.size()) {
					msg.append(newline);
				}
				i++;
			}
			// status information
			msg.append(String.format("%sLast Build: %s (%s ago): %s", newline, lastBuild.getNumber(),
					lastBuild.getTimestampString(), lastBuild.getResult()));

			// build url
			msg.append(newline + MessageHelper.getBuildURL(lastBuild));

			// commit authors and messages
			for (Entry entry : lastBuild.getChangeSet()) {
				msg.append(String.format("%s* %s: %s", newline, entry.getAuthor(), entry.getMsg()));
			}
		} else {
			msg.append("no finished build yet");
		}

		return msg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getCommandShortName() {
		return OVERVIEW;
	}

	private String getSpaces(String projectName) {
		int spacesNum = projectName.length();
		StringBuilder spaces = new StringBuilder("  ");
		for (int i = 0; i < spacesNum; i++) {
			spaces.append(" ");
		}
		return spaces.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHelp() {
		return HELP;
	}
}
