package hudson.plugins.im.bot;

import java.util.Arrays;
import java.util.Collection;

import hudson.Extension;
import hudson.Util;
import hudson.model.Computer;
import hudson.model.Executor;
import hudson.model.Item;
import hudson.model.Queue.Executable;
import hudson.model.queue.SubTask;
import hudson.plugins.im.IMChat;
import hudson.plugins.im.IMException;
import hudson.plugins.im.IMMessage;
import hudson.plugins.im.Sender;
import jenkins.model.Jenkins;

/**
 * CurrentlyBuilding command for instant messaging plugin.
 * 
 * Generates a list of jobs in progress.
 * 
 * @author Bjoern Kasteleiner
 */
@Extension
public class CurrentlyBuildingCommand extends BotCommand {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getCommandNames() {
		return Arrays.asList("currentlyBuilding", "cb");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeCommand(Bot bot, IMChat chat, IMMessage message, Sender sender, String[] args)
			throws IMException {
		StringBuffer msg = new StringBuffer();
		msg.append("Currently building:");
		boolean currentlyJobsInProgess = false;
		for (Computer computer : Jenkins.getInstance().getComputers()) {
			for (Executor executor : computer.getExecutors()) {
				Executable currentExecutable = executor.getCurrentExecutable();
				if (currentExecutable != null) {
					currentlyJobsInProgess = true;
					msg.append(buildstatusreply(computer, executor, currentExecutable));
				}
			}
		}

		if (!currentlyJobsInProgess) {
			msg.append("\n- No jobs are running. Try again later!");
		}

		chat.sendMessage(msg.toString());
	}

	private String buildstatusreply(Computer computer, Executor executor, Executable executable) {
		String returnStr;

		SubTask task = executable.getParent();
		Item item = null;
		if (task instanceof Item) {
			item = (Item) task;
		}
		returnStr = "\n- " + computer.getDisplayName() + "#" + executor.getNumber() + ": ";
		returnStr += (item != null ? item.getFullDisplayName() : task.getDisplayName()) + " (Elapsed time: ";
		returnStr += Util.getTimeSpanString(executor.getElapsedTime()) + ", Estimated remaining time: ";
		returnStr += executor.getEstimatedRemainingTime() + ")";
		return returnStr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHelp() {
		return " - list jobs which are currently in progress";
	}

}
