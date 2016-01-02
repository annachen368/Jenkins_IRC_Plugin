package hudson.plugins.im.bot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import hudson.Extension;
import hudson.plugins.im.Sender;
import jenkins.model.Jenkins;

/**
 * URLCommand collects URL of some useful Jenkins' pages, users can get fast
 * access to the pages when using the URL returned by BOT. URL of the demanded
 * page was returned and send back to Chat Room
 * 
 * Sample Input : !jenkins geturl "<pagename>"
 * 
 * @author Zehao, Tao
 *
 */
@Extension
public class URLCommand extends AbstractTextSendingCommand {

	private static final String SYNTAX = "<page>";
	private static final String HELP = SYNTAX + " - retrieve the spesific URL for Jenkins";

	/** Mapping the command arguments to path */
	private HashMap<String, String> cmd_list;
	/** All available command names */
	private String availableCommand;

	/**
	 * Constructor
	 */
	public URLCommand() {
		super();
		createCommandList();
		getAvailableCommand();
	}

	/**
	 * returns name for URL Command
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getCommandNames() {
		return Collections.singleton("geturl");
	}

	/**
	 * Analyze the command from input
	 * 
	 * @param bot
	 * @param sender
	 * @param args
	 *            String list of Command
	 * @return String {@inheritDoc}
	 *
	 */
	@Override
	protected String getReply(Bot bot, Sender sender, String[] args) {
		// "geturl"
		if (args.length == 1) {
			return getBaseURL();
		}
		// "geturl name"
		else if (args.length == 2) {
			String path = cmd_list.get(args[1]);
			return path != null ? (getBaseURL() + path) : getAvailableCommand();
		}
		// Invalid Arguments
		else {
			return giveSyntax(sender.getNickname(), args[0]);
		}
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
	 * @param sender
	 *            Name or ID of Sender (Chat room)
	 * @param cmd
	 *            Command Name "geturl <cmd>"
	 * @return
	 */
	private String giveSyntax(String sender, String cmd) {
		return sender + ": syntax is: '" + cmd + SYNTAX + "'";
	}

	/**
	 * Get the server URL of Jenkins
	 * 
	 * @return "http://domain:port/"
	 */
	String getBaseURL() {
		return Jenkins.getInstance().getRootUrl();
	}

	/**
	 * 
	 * @return The string sent back to Chat Room
	 */
	private String getAvailableCommand() {
		if (availableCommand == null) {
			StringBuilder buf = new StringBuilder();
			buf.append("Available Command: ");
			for (String key : cmd_list.keySet()) {
				buf.append(key);
				buf.append(", ");
			}
			availableCommand = buf.substring(0, buf.length() - 2);
		}
		return availableCommand;
	}

	/**
	 * Create a hash table which maps the command arguments to URL path.
	 */
	private void createCommandList() {
		cmd_list = new HashMap<String, String>();

		createHashMapEntries("", "base", "root");
		createHashMapEntries("configure/", "configure", "conf");
		createHashMapEntries("log/", "log");
		createHashMapEntries("pluginManager/", "plugin", "plugins");
		createHashMapEntries("configureSecurity/", "security", "sec");
		createHashMapEntries("load-statistics/", "statistic", "stat");
		createHashMapEntries("script/", "script", "scripts");
		createHashMapEntries("computer/", "nodes", "node");
		createHashMapEntries("securityRealm/", "user", "users");
	}

	/**
	 * Helper method for createCommandList() to put <key,value> pairs into hash
	 * map.
	 * 
	 * @param value
	 *            URL path
	 * @param keys
	 *            All keys that points to the same path
	 */
	private void createHashMapEntries(String value, String... keys) {
		for (String key : keys) {
			cmd_list.put(key, value);
		}
	}
}
