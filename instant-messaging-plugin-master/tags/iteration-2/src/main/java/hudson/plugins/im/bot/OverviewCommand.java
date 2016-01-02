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
 * @author Anna, Scott
 */
@Extension
public class OverviewCommand extends AbstractMultipleJobCommand {
	
	public final String OVERVIEW = "overview";
	public final String LINE_BREAK = "\n";

	@Override
	public Collection<String> getCommandNames() {
		return Arrays.asList(OVERVIEW, "o");
	}
	
	@Override
	protected CharSequence getMessageForJob(AbstractProject<?, ?> project) {
		StringBuilder msg = new StringBuilder(32);
		String projectName = project.getFullDisplayName();
        msg.append(projectName);
        int spacesNum = projectName.length();
        StringBuilder spaces = new StringBuilder("  ");
        for (int i=0; i < spacesNum; i++) {
        	spaces.append(" ");
        }
        
        if (project.isDisabled()) {
            msg.append("(disabled)");
        } else if (project.isBuilding()) {
            msg.append("(BUILDING: ").append(project.getLastBuild().getDurationString()).append(")");
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
        	if (reports.isEmpty() ) {
        		reports = Collections.singletonList(project.getBuildHealth());
        	}
        	// display health information
        	int i = 1;
        	for (HealthReport health : reports) {
        		msg.append(health.getDescription())
        			.append("(").append(health.getScore()).append("%)");
        		if (i<reports.size()) {
        			msg.append(LINE_BREAK);
        			msg.append(spaces.toString());
        		}
        		i++;
        	}
        	// status information
        	msg.append(LINE_BREAK);
			msg.append(spaces.toString());
        	msg.append("Last Build: ").append(lastBuild.getNumber()).append(" (")
        	.append(lastBuild.getTimestampString()).append(" ago): ").append(lastBuild.getResult());
			
			// build url
			msg.append(LINE_BREAK);
        	msg.append(spaces.toString());
			msg.append(MessageHelper.getBuildURL(lastBuild));
			
			// commit authors and messages
			for (Entry entry : lastBuild.getChangeSet()) {
            	msg.append(LINE_BREAK);
            	msg.append(spaces.toString());
            	msg.append("* ");
                msg.append(entry.getAuthor()).append(": ").append(entry.getMsg());
            }
        } else {
            msg.append("no finished build yet");
        }
        
        return msg;
	}

	@Override
	protected String getCommandShortName() {
		return OVERVIEW;
	}

}
