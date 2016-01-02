package hudson.plugins.im.bot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import hudson.Extension;
import hudson.plugins.im.Sender;
import jenkins.model.Jenkins;

@Extension
public class URLCommand extends AbstractTextSendingCommand {

	private static final String SYNTAX = "<page>";
	private static final String HELP = SYNTAX + " - retrieve the spesific URL for Jenkins";

	HashMap<String, Integer> cmd_list;
	
	public URLCommand() {
		createCommendList();
	}

	@Override
	public Collection<String> getCommandNames() {
		return Collections.singleton("geturl");
	}
	
	

	@Override
	protected String getReply(Bot bot, Sender sender, String[] args) {
		if (cmd_list == null) {
			createCommendList();          
		}

		if (args.length == 1) {
			return getBaseURL();
		} else if (args.length == 2) {
			int cmd_idx = cmd_list.get(args[1]) == null ? 0 : cmd_list.get(args[1]);
			return executeCommand(cmd_idx);
		} else {
			return giveSyntax(sender.getNickname(), args[0]);
		}
	}

	@Override
	public String getHelp() {
		return HELP;
	}

	private String giveSyntax(String sender, String cmd) {
		return sender + ": syntax is: '" + cmd + SYNTAX + "'";
	}

	String getBaseURL() {
		return Jenkins.getInstance().getRootUrl();
	}

	private String getGlobalConfigureURL() {
		return getBaseURL() + "configure/";
	}

	private String getGlobalSystemLogURL() {
		return getBaseURL() + "log/";
	}

	private String getPluginManager() {
		return getBaseURL() + "pluginManager/";
	}
    
    
    
    //extend
    private String getSecurity() {
        return getBaseURL() + "configureSecurity/";
    }
    
    private String getLoadstatistics() {
        return getBaseURL() + "load-statistics/";
    }
    
    private String getScriptConsole() {
        return getBaseURL() + "script/";
    }
    
    private String getNodes() {
        return getBaseURL() + "computer/";
    }
    
    private String getUsers() {
        return getBaseURL() + "securityRealm/";
    }
    
    //
	private String getAvailableCommand() {
		StringBuilder buf = new StringBuilder();
		buf.append("Available Command: ");
		for (String key : cmd_list.keySet()) {
			buf.append(key);
			buf.append(", ");
		}
		return buf.substring(0, buf.length() - 2);
	}
	
	private void insertData(int seq, String name1, String name2) {
		if(name1.length() > 0) {
			cmd_list.put(name1, seq);
		}
		if(name2.length() > 0) {
			cmd_list.put(name2, seq);
		}
	}
	
	private String executeCommand(int cmd_idx) {
		switch (cmd_idx) {
		case 1:
			return getBaseURL();
		case 2:
			return getGlobalConfigureURL();
		case 3:
			return getGlobalSystemLogURL();
		case 4:
			return getPluginManager();
        case 5:
            return getSecurity();
        case 6:
            return getLoadstatistics();
        case 7:
            return getScriptConsole();
        case 8:
            return getNodes();
        case 9:
            return getUsers();
		default:
			return getAvailableCommand();
		}
	}
	
	private void createCommendList() {
		cmd_list = new HashMap<String, Integer>();
		
		insertData(1, "base", "root");
		insertData(2, "configure", "conf");
		insertData(3, "log", "");
		insertData(4, "plugin", "plugins");
		insertData(5, "security", "sec");
		insertData(6, "statistic", "stat");
		insertData(7, "script", "scripts");
		insertData(8, "nodes", "node");
		insertData(9, "user", "users");   
	}
}

