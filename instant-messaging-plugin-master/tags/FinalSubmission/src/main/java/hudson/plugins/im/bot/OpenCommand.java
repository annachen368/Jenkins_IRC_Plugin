package hudson.plugins.im.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.plugins.im.IMException;
import hudson.plugins.im.Sender;
import jenkins.model.Jenkins;

/**
 * Open the file or directory in specific project workspace. This command is a
 * combination of "ls" and "find". Users could traverse the workspace level by
 * level or just use the file name to find the path.
 * 
 * @author Zehao, Yuhang, Scott
 *
 */

@Extension
public class OpenCommand extends AbstractTextSendingCommand {

	private static final String SYNTAX = "<ProjectName> <Path to Directory or File>";
	private static final String HELP = SYNTAX + " - show the content of directory or the url of file";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getCommandNames() {
		return Collections.singleton("open");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getReply(Bot bot, Sender sender, String[] args) {
		/*
		 * "open"
		 * 
		 * Prompt all available Project Name
		 * 
		 */
		if (args.length == 1) {
			StringBuilder res = new StringBuilder();
			res.append("Syntax: open <Project Name> <Path To Dir/File>\n");
			res.append("Available Project Name:\n");
			List<AbstractProject<?, ?>> projs = getJobProvider().getTopLevelJobs();
			for (AbstractProject<?, ?> proj : projs) {
				res.append(proj.getFullDisplayName() + " ");
			}
			return res.toString();

		}
		/*
		 * "open <Project Name> <Path to Destination>
		 * 
		 * Parse the message and generate the output
		 * 
		 */
		else if (args.length < 4) {
			MsgWrapper msg;
			try {
				msg = parseMessage(args);
			} catch (IMException e) {
				return e.getMessage();
			}
			return handler(msg);

		} else {
			// Invalid Command
			return giveSyntax(sender.getNickname(), "open");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHelp() {
		return HELP;
	}

	private String giveSyntax(String sender, String cmd) {
		return sender + ": syntax is: '" + cmd + SYNTAX + "'";
	}

	/**
	 * Parse the original message into a MsgWrapper object.
	 * 
	 * @param args
	 *            The original message
	 * @return
	 */
	private MsgWrapper parseMessage(String[] args) throws IMException {
		// Find the proper project
		AbstractProject<?, ?> proj = getJobProvider().getJobByNameOrDisplayName(args[1]);
		if (proj == null)
			throw new IMException("Unknown job '" + args[1] + "'");

		// Find the proper workspace
		FilePath rootDir = getWorkSpace(proj);
		if (rootDir == null)
			throw new IMException("Cannot Get WorkSpace!");

		// Find Command
		if (args.length > 2 && args[2].indexOf("/") == -1) {
			Map<FilePath, String> destPaths = traverseFilePath(rootDir, args[2], "");
			if (destPaths.size() == 0) {
				throw new IMException("Directory/File Not Found!!");
			} else {
				return new MsgWrapper(args[1], destPaths, true);
			}
		}

		// LS Command
		String[] path = grabPath((args.length < 3) ? "" : args[2]);
		FilePath parent = new FilePath(rootDir, path[0]);
		String name = path[1];
		Map<FilePath, String> destPaths = new HashMap<>();
		try {
			// First check all child FilePath
			if (name.equals("")) {
				destPaths.put(parent, path[0]);
			} else if (parent.isDirectory()) {
				for (FilePath temp : parent.list()) {
					if (temp.getName().startsWith(name)) {
						destPaths.put(temp, path[0] + "/" + temp.getName());
					}
				}
			}
			if (destPaths.size() > 0) {
				MsgWrapper msg = new MsgWrapper(args[1], destPaths, false);
				return msg;
			}

			// Second Traverse all sub directories if no FilePath is found in
			// the previous process
			destPaths = traverseFilePath(parent, name, "/" + path[0].replaceAll("/" + parent.getName() + "$", ""));
			if (destPaths.size() == 0) {
				throw new IMException("Directory/File Not Found!!");
			} else {
				return new MsgWrapper(args[1], destPaths, true);
			}

		} catch (Exception e) {
			throw new IMException("Directory/File Not Found!!");
		}
	}

	/**
	 * Generate the reply string based on the message.
	 * 
	 * Find Command: Return the URLs of all find FilePath 
	 * LS Command: 
	 * Multiple Files/Directories - return the name of each FilePath 
	 * Single Directory - list the files/directories in it Single File - Content of short file - URL of long file
	 * 
	 * @param msg
	 *            keeps all information to generate the reply string
	 * @return
	 */
	private String handler(MsgWrapper msg) {

		Map<FilePath, String> destPaths = msg.getDestPaths();
		StringBuilder res = new StringBuilder();

		// Find Command
		if (msg.isFindFile()) {
			res.append("Files Found:\n");

			for (Entry<FilePath, String> entry : destPaths.entrySet()) {
				res.append(getBaseURL() + "job/" + msg.getProjectName() + "/ws"
						+ entry.getValue().replaceAll("^/workspace", "") + "\n");
			}
			return res.toString();
		}
		// List Command
		try {
			if (destPaths.size() > 1) {
				// Multiple files/directories available
				res.append("Available Directory/File Name:\n");
				for (Entry<FilePath, String> entry : destPaths.entrySet()) {
					res.append(file2Str(entry.getKey()));
				}
				return res.toString();
			} else {
				// Only One File/Directory
				FilePath destination = null;
				for (FilePath temp : destPaths.keySet()) {
					destination = temp;
					break;
				}

				// Directory
				if (destination.isDirectory()) {
					List<FilePath> subDirs = destination.list();
					List<String> dirsName = new ArrayList<>();
					for (FilePath file : subDirs) {
						dirsName.add(file2Str(file));
					}
					Collections.sort(dirsName);
					for (String name : dirsName) {
						res.append(name);
					}
					return res.toString();
				}
				// File
				else {
					// Return the content of the short file
					if (destination.length() < 500) {
						BufferedReader in = new BufferedReader(new InputStreamReader(destination.read()));
						String line = null;

						while ((line = in.readLine()) != null) {
							res.append(line + "\n");
						}
						return res.toString();
					}
					// Return the URL of the long file
					else {
						String temp = "job/" + msg.getProjectName() + "/ws/" + destPaths.get(destination) + "\n";
						return getBaseURL() + temp.replaceAll("//", "/");
					}
				}
			}
		} catch (Exception e) {
			return "Cannot Open Directory/File !!";
		}

	}

	/*
	 * The following two method will be hijacked by spy() method in the test
	 * Don't change the scope of these two method! getBaseURL(), getWorkSpace()
	 * 
	 */
	/**
	 * Get the root URL of Jenkins
	 * 
	 * @return "http://domain:port/"
	 */
	String getBaseURL() {
		return Jenkins.getInstance().getRootUrl();
	}

	/**
	 * Get the Workspace of a specific project
	 * 
	 * @param proj
	 *            The project, whose workspace is retrieved.
	 * @return The FilePath represents the Root Directories
	 */
	FilePath getWorkSpace(AbstractProject<?, ?> proj) {
		return proj.getLastBuild().getWorkspace();
	}

	/*
	 * Utility Methods
	 * 
	 */
	/**
	 * Within a given root directory, find all files whose name are started with
	 * "name".
	 * 
	 * @param root
	 *            Object represents the root directory
	 * @param name
	 *            File to search
	 * @param parentPath
	 *            The path to the root directory
	 * @return A set of all found files and their absolute paths
	 */
	private Map<FilePath, String> traverseFilePath(FilePath root, String name, String parentPath) {
		Map<FilePath, String> destPaths = new HashMap<>();
		traverseFilePath(root, name, parentPath, destPaths);
		return destPaths;
	}

	private void traverseFilePath(FilePath root, String name, String parentPath, Map<FilePath, String> destPaths) {
		try {

			if (root.isDirectory() && !root.getName().startsWith(".")) {
				for (FilePath f : root.list())
					traverseFilePath(f, name, parentPath + "/" + root.getName(), destPaths);
			} else {
				if (root.getName().startsWith(name)) {
					destPaths.put(root, parentPath + "/" + root.getName());
				}
			}
		} catch (IOException | InterruptedException e) {
			return;
		}
	}

	/**
	 * Separate parent path and destination name. This method is particular
	 * designed for incomplete path name. The parent path, which is accurate and
	 * complete, is used to find the parent FilePath. The file name, which might
	 * be incomplete, is used for searching FilePath in the list of parent
	 * sub directories/files.
	 * 
	 * @param path
	 *            The original path string, e.g. "path/to/file"
	 * 
	 * @return A string array consist of two string, ["path/to","file"]
	 */
	private String[] grabPath(String path) {
		Pattern pattern = Pattern.compile("^(/)?(.*)/([^/]+)/?$");
		Matcher m = pattern.matcher(path);
		if (m.find()) {
			return new String[] { m.group(2), m.group(3) };
		}
		return new String[] { "", path.replaceAll("/", "") };
	}

	/**
	 * Convert the file name to String. Different Format for directory and file
	 * 
	 * @param f
	 *            The FilePath object to convert
	 * @return "" for hidden file/directories, 
	 * 		   "[name] " for directories,
	 *         "name " for files
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String file2Str(FilePath f) throws IOException, InterruptedException {
		String name = f.getName();
		if (name.startsWith("."))
			return ""; // Hide the hidden file
		if (f.isDirectory()) {
			return "[" + name + "] ";
		} else {
			return name + " ";
		}
	}

	/**
	 * A inner nested helper class for OpenCommand. The class is simply used for
	 * transfer data: Project Name All Valid FilePath objects A flag indicating
	 * whether the command is "ls" or "find"
	 * 
	 * 
	 * The parseMessage() method is used to convert the original message into a
	 * MsgWrapper instance. The handler() method will use MsgWrapper to generate
	 * the final reply string.
	 * 
	 * @author Zehao
	 *
	 */
	private class MsgWrapper {
		private String projectName;
		private boolean isFindFile;
		private Map<FilePath, String> destPaths;

		MsgWrapper(String projectName, Map<FilePath, String> destPaths, boolean isFindFile) {
			this.projectName = projectName;
			this.destPaths = destPaths;
			this.isFindFile = isFindFile;
		}

		boolean isFindFile() {
			return isFindFile;
		}

		Map<FilePath, String> getDestPaths() {
			return destPaths;
		}

		String getProjectName() {
			return projectName;
		}
	}
}
