package hudson.plugins.im.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.plugins.im.Sender;
import jenkins.model.Jenkins;

/**
 * Open the file or directory in specific project workspace.
 * 
 * @author Zehao Song, Yuhang Wang
 *
 */

@Extension
public class OpenCommand extends AbstractTextSendingCommand {
	/**
	 * A inner nested helper class for OpenCommand.
	 * The class is simply used for transfer data:
	 * 		Project Name
	 * 		Parent Path
	 * 		All Valid FilePath objects
	 * 		
	 * 		The reason why parse failed.
	 * 
	 * 
	 * 
	 * The parseMessage() method is used to convert the original message into a MsgWrapper instance.
	 * The handler() method will use MsgWrapper to generate the final reply string.
	 * 
	 * @author Zehao
	 *
	 */
	private class MsgWrapper {
		private String projectName;
		private String parentPath;
		private List<FilePath> destinations;
		private Boolean isValid;
		private String reason;

		MsgWrapper(String projectName, String parentPath) {
			this.projectName = projectName;
			this.parentPath = parentPath;
			destinations = new ArrayList<FilePath>();
			this.isValid = true;
		}

		MsgWrapper(String reason) {
			this.isValid = false;
			this.reason = reason;
		}

		Boolean isValid() {
			return isValid;
		}

		String getReason() {
			return reason;
		}

		String getProjectName() {
			return projectName;
		}

		String getParentPath() {
			return parentPath;
		}

		List<FilePath> getDestinations() {
			return destinations;
		}

		void addDestination(FilePath destination) {
			destinations.add(destination);
		}

		void addDestination(List<FilePath> dests) {
			destinations.addAll(dests);
		}

		int getDestNum() {
			return destinations.size();
		}
	}

	private static final String SYNTAX = "<ProjectName> <Path to Directory or File>";
	private static final String HELP = SYNTAX + " - show the content of directory or the url of file";

	@Override
	public Collection<String> getCommandNames() {
		return Collections.singleton("open");
	}

	/**
	 * Parse the original message into a MsgWrapper object. 
	 * 
	 * @param args  The original message
	 * @return 
	 */
	private MsgWrapper parseMessage(String[] args) {
		// Find the proper project
		AbstractProject<?, ?> proj = getJobProvider().getJobByNameOrDisplayName(args[1]);
		if (proj != null) {

			// Find the proper workspace
			FilePath rootDir = getWorkSpace(proj);

			// Workspace is not available
			if (rootDir == null)
				return new MsgWrapper("Cannot Get WorkSpace!");

			// Find the directory or file
			String[] path = grabPath((args.length < 3) ? "" : args[2]);
			FilePath parent = new FilePath(rootDir, path[0]);
			String name = path[1];
			List<FilePath> dests = new ArrayList<>();
			try {
				// Find the destination file
				if (name.equals("")) {
					dests.add(parent);
				} else if (parent.isDirectory()) {
					for (FilePath temp : parent.list()) {
						if (temp.getName().startsWith(name)) {
							dests.add(temp);
						}
					}
				}

				if (dests.size() == 0) {
					return new MsgWrapper("Directory/File Not Found!!");
				} else {
					MsgWrapper msg = new MsgWrapper(args[1], path[0]);
					msg.addDestination(dests);
					return msg;
				}

			} catch (Exception e) {
				return new MsgWrapper("Directory/File Not Found!!");
			}

		} else {
			return new MsgWrapper("Unknown job '" + args[1] + "'");
		}
	}

	/**
	 * Generate the reply string based on the message.
	 * 
	 * @param msg
	 * @return
	 */
	private String handler(MsgWrapper msg) {
		// Parent File Path Wrong
		if (!msg.isValid()) {
			return msg.getReason();
		}
		int destNum = msg.getDestNum();
		try {
			// No Destination
			if (destNum < 1) {
				return "Directory/File Not Found!!";
			} else if (destNum > 1) { 
				// Multiple files/directories available
				StringBuilder res = new StringBuilder("Available Directory/File Name:\n");
				for (FilePath dest : msg.getDestinations()) {
					res.append(file2Str(dest));
				}
				return res.toString();
			} else { // Only One File/Directory
				FilePath destination = msg.getDestinations().get(0);
				StringBuilder res = new StringBuilder();
				if (destination.isDirectory()) {
					// List the directory and file of workspace
					List<FilePath> subDirs = destination.list();
					List<String> dirsName = new ArrayList<>();
					for (FilePath file : subDirs) {
						dirsName.add(file2Str(file));
					}
					Collections.sort(dirsName);
					for ( String name : dirsName ) {
						res.append(name);
					}
					return res.toString();
				} else {
					// Grab and return the file content or URL
					if (destination.length() < 500) { // return file content
						BufferedReader in = new BufferedReader(new InputStreamReader(destination.read()));
						String line = null;

						while ((line = in.readLine()) != null) {
							res.append(line + "\n");
						}
						return res.toString();
					} else { // return the URL to the file
						return getBaseURL() + "job/" + msg.getProjectName() + "/ws/" + msg.getParentPath() + "/"
								+ destination.getName();
					}
				}
			}
		} catch (Exception e) {
			return "Cannot Open Directory/File !!";
		}

	}

	@Override
	protected String getReply(Bot bot, Sender sender, String[] args) {
		if (args.length == 1) {
			// Prompt all available Project Name
			StringBuilder res = new StringBuilder();
			res.append("Syntax: open <Project Name> <Path To Dir/File>\n");
			res.append("Available Project Name:\n");
			List<AbstractProject<?, ?>> projs = getJobProvider().getTopLevelJobs();
			for (AbstractProject<?, ?> proj : projs) {
				res.append(proj.getFullDisplayName() + " ");
			}
			return res.toString();

		} else if (args.length < 4) {
			// Show content of directory or URL of file
			MsgWrapper msg = parseMessage(args);
			return handler(msg);

		} else {
			// Invalid Command
			return giveSyntax(sender.getNickname(), "open");
		}
	}

	@Override
	public String getHelp() {
		return HELP;
	}

	private String giveSyntax(String sender, String cmd) {
		return sender + ": syntax is: '" + cmd + SYNTAX + "'";
	}

	/*
	 *  The following two method will be hijacked by spy() method in the test
	 *  Don't change the scope of these two method!
	 *  getBaseURL(), getWorkSpace()
	 *  
	 */
	/** Get the root URL of Jenkins
	 * 
	 * @return  "http://domain:port/"
	 */
	String getBaseURL() {
		return Jenkins.getInstance().getRootUrl();
	}

	/** Get the Workspace of a specific project
	 * 
	 * @param proj   The project, whose workspace is retrieved.
	 * @return  The FilePath represents the Root Directories 
	 */
	FilePath getWorkSpace(AbstractProject<?, ?> proj) {
		return proj.getLastBuild().getWorkspace();
	}

	/* 
	 * Utility Methods
	 * 
	 */
	/**
	 * Separate parent path and destination name.
	 * This method is particular designed for incomplete path name. 
	 * The parent path, which is accurate and complete, is used to find the parent FilePath.
	 * The file name, which might be incomplete, is used for searching FilePath in the list of parent subdirs/files.
	 * 
	 * @param path  The original path string, e.g. "path/to/file"
	 * 
	 * @return  A string array consist of two string, ["path/to","file"]
	 */
	private String[] grabPath(String path) {
		Pattern pattern = Pattern.compile("^(/)?(.*)/([^/]+)/?$");
		Matcher m = pattern.matcher(path);
		if (m.find()) {
			return new String[] { m.group(2), m.group(3) };
		}
		return new String[] { "", path.replaceAll("/", "") };
	}

	/** Convert the file name to String. Different Format for directory and file
	 * 
	 * @param f  The FilePath object to convert
	 * @return   "" for hidden file/directories, "[name] " for directories, "name " for files 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String file2Str(FilePath f) throws IOException, InterruptedException {
		String name = f.getName();
		if ( name.startsWith(".") ) return ""; // Hide the hidden file
		if (f.isDirectory()) {
			return "[" + name + "] ";
		} else {
			return name + " ";
		}
	}
}
