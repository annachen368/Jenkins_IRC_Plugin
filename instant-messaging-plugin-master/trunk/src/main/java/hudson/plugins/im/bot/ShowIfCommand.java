/*
 * Created on Apr 22, 2007
 */
package hudson.plugins.im.bot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

/**
 * 
 * ShowIfCommand collects all the builds in Jenkins and apply a set of filters
 * to remove unnecessary builds. A string representation of the remaining builds
 * was returned and send back to Chat Room
 * 
 * Sample Input : !jenkins showIf " user <user> | date < < | = | > >
 * <YYYY-MM-DD-HH-mm> | project <project> | build <build number> | jobs < < | =
 * | > > <job number>
 * 
 * @author Austin, Ryan
 * @author Zehao ( Add HashMap mapping query_type to filter objects to avoid
 *         large number of conditions )
 * 
 */
@Extension
public class ShowIfCommand extends AbstractSourceQueryCommand {
	private static final Logger LOGGER = Logger.getLogger(ShowIfCommand.class.getName());

	// Map query_type name to the corresponding filter instance
	private static Map<String, AbstractBuildsFilter> filters = getFilters();

	// The static method to generate the filters map
	private static Map<String, AbstractBuildsFilter> getFilters() {
		Map<String, AbstractBuildsFilter> map = new HashMap<>();
		map.put("user", new UserFilter());
		map.put("date", new DateFilter());
		map.put("project", new ProjectFilter());
		map.put("build", new BuildFilter());
		map.put("jobs", new JobsFilter());
		return map;
	}

	/**
	 * Override the getCommandeNames()
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getCommandNames() {
		return Arrays.asList("showIf", "si");
	}

	// Apply All Filters to builds collections & Generate the reply string
	// list : List containing all parsed input arguments
	// args : Original input arguments
	// builds : A collection of all builds in Jenkins
	// returns The string which is ready to sent back to Chat room
	private CharSequence distroToFilter(ArrayList<ArrayList<String>> list, String[] args,
			Collection<AbstractBuild<?, ?>> builds) {

		StringBuilder msg = new StringBuilder();

		/* Add the default build limit */
		boolean isGoverned = false;
		if (!Arrays.asList(args).contains("build")) {
			isGoverned = true;
			ArrayList<String> temp = new ArrayList<>();
			temp.add("build");
			temp.add("10");
			list.add(temp);
		}

		/* Apply Filters To Builds Collections */
		for (ArrayList<String> query : list) {
			String query_type = query.get(0);

			// apply showif filters here
			if (!checkQuery(query, query_type)) {
				msg.append("Malformed " + query_type + " Command!\n");
				LOGGER.warning("Malformed " + query_type + " Command!\n");
				continue;
			}

			AbstractBuildsFilter filter = filters.get(query_type);
			if (filter != null) {
				filter.applyFilter(builds, query);
			}
			// builds = this.runQuery(query, query_type , builds);

			if (builds.isEmpty()) {
				StringBuilder temp = new StringBuilder(32);
				temp.append("No Builds Found! \n");
				return temp;
			}
		}

		/* Generate the output string */
		for (AbstractBuild<?, ?> abBuild : builds) {
			msg.append(String.format("last build: %s (%s ago): %s\n", abBuild.getNumber(), abBuild.getTimestampString(),
					abBuild.getResult()));
		}

		if (isGoverned) {
			msg.append("\nOUTPUT IS GOVERNED TO 10 ITEMS!\n");
		}

		return msg;
	}

	/**
	 * Check whether the input arguments are valid
	 * 
	 * @param query
	 * @param query_type
	 * @return
	 */
	protected boolean checkQuery(ArrayList<String> query, String query_type) {
		if ("user project build".indexOf(query_type) != -1) {
			return query.size() == 2;
		}
		if ("date jobs".indexOf(query_type) != -1) {
			return query.size() == 3;
		}
		return false;
	}

	/**
	 * 
	 * Parse the arguments & collect all builds Call distroToFilter() method to
	 * apply filters and generate reply string
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected CharSequence getMessageForJob(Collection<AbstractProject<?, ?>> projects, String[] args) {
		int arg_len = args.length;

		LOGGER.warning("there are " + Integer.toString(arg_len) + " args items\n");

		// convert projects Collection to ArrayList
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

		// Parse Query
		for (int i = 0; i < arg_len; i++) {
			if ("showIf si !jenkins |".indexOf(args[i]) == -1) {
				ArrayList<String> temp = new ArrayList<String>();
				for (; i < arg_len && "showIf si |".indexOf(args[i]) == -1; ++i) {
					temp.add(args[i].trim());
				}
				list.add(temp);
			}
		}

		// get all builds
		// if this command is slow may need to put limiter on while loop
		Collection<AbstractBuild<?, ?>> builds = new ArrayList<AbstractBuild<?, ?>>();
		for (AbstractProject<?, ?> abProj : projects) {
			AbstractBuild<?, ?> tempbuild = abProj.getLastBuild();
			while (tempbuild != null) {
				builds.add(tempbuild);
				tempbuild = tempbuild.getPreviousBuild();
			}
		}

		return distroToFilter(list, args, builds);
	}

	/**
	 * returns short name for ShowIf Command
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected String getCommandShortName() {
		return "detailed history";
	}
}

/**
 * 
 * Date Filter Remove all builds that don't match the date
 * 
 * args : date < < | = | > > <YYYY-MM-DD-HH-mm> eg. date < 2000-12-31-01-00
 * Containing operator "<" and date in format of <YYYY-MM-DD-HH-mm>
 * 
 * @author Austin, Ryan, Zehao
 *
 */
class DateFilter extends AbstractBuildsFilter {
	/**
	 * Applies filter to builds with args
	 * 
	 * @param builds
	 * @param args
	 *            ArrayList of data and dateformat
	 */
	@Override
	public void applyFilter(Collection<AbstractBuild<?, ?>> builds, ArrayList<String> args) {
		if (args.size() != 3)
			return;
		String op = args.get(1);

		// Check whether or not the operator is valid
		if ("<>=".indexOf(op) == -1) {
			return;
		}

		// Get the date time
		DateFormat format = new SimpleDateFormat("YYYY-MM-dd-HH-mm");
		long date = 0;
		try {
			date = (format.parse(args.get(2))).getTime();
		} catch (ParseException e) {
			return;
		}

		// Filter
		Iterator<AbstractBuild<?, ?>> it = builds.iterator();
		while (it.hasNext()) {
			AbstractBuild<?, ?> item = it.next();
			long temp = (item.getTimestamp()).getTimeInMillis();
			switch (op) {
			case "<":
				if (!(temp < date)) {
					it.remove();
				}
				break;
			case ">":
				if (!(temp > date)) {
					it.remove();
				}
				break;
			case "=":
				if (!(temp + 60000 > date && temp - 60000 < date)) {
					it.remove();
				}
				break;
			default:
			}
		}
	}
}

/**
 * Jobs Filter Remove all builds that don't match the specific build numbers
 * 
 * args : jobs < < | = | > > <job number> For example: "jobs < 13" will remove
 * all builds with a build number not less than 13
 * 
 * @author Austin, Ryan, Zehao
 *
 */
class JobsFilter extends AbstractBuildsFilter {
	/**
	 * Applies filter to builds with args
	 * 
	 * @param builds
	 * @param args
	 *            ArrayList of jobs and jobformat
	 */
	@Override
	public void applyFilter(Collection<AbstractBuild<?, ?>> builds, ArrayList<String> args) {
		if (args.size() != 3)
			return;
		String op = args.get(1);
		int job_num = Math.abs(Integer.parseInt(args.get(2)));

		// Check whether or not the operator is valid
		if ("<>=".indexOf(op) == -1) {
			return;
		}
		// Filter
		Iterator<AbstractBuild<?, ?>> it = builds.iterator();
		while (it.hasNext()) {
			AbstractBuild<?, ?> item = it.next();
			int temp = item.getNumber();
			switch (op) {
			case "<":
				if (!(temp < job_num)) {
					it.remove();
				}
				break;
			case ">":
				if (!(temp > job_num)) {
					it.remove();
				}
				break;
			case "=":
				if (!(temp == job_num)) {
					it.remove();
				}
				break;
			default:
			}
		}
	}
}

/**
 * Project Filter Remove all builds that doesn't belong the specific project
 * 
 * args : project < project >
 * 
 * @author Austin, Ryan, Zehao
 *
 */
class ProjectFilter extends AbstractBuildsFilter {
	/**
	 * Applies filter to builds with args
	 * 
	 * @param builds
	 * @param args
	 *            ArrayList of project and projectformat
	 */
	@Override
	public void applyFilter(Collection<AbstractBuild<?, ?>> builds, ArrayList<String> args) {
		if (args.size() != 2)
			return;
		String projectName = args.get(1);

		Iterator<AbstractBuild<?, ?>> it = builds.iterator(); // get an
																// interator
																// from the
																// collection
		while (it.hasNext()) {
			AbstractBuild<?, ?> item = it.next();
			if (!(item.getProject().getName().equals(projectName))) {
				it.remove();
			}
		}
	}
}

/**
 * Build Filter Determines the maximum number of builds in the collection
 * 
 * args : build < build number >
 * 
 * @author Austin, Ryan, Zehao
 *
 */
class BuildFilter extends AbstractBuildsFilter {
	/**
	 * Applies filter to builds with args
	 * 
	 * @param builds
	 * @param args
	 *            ArrayList of build and buildformat
	 */
	@Override
	public void applyFilter(Collection<AbstractBuild<?, ?>> builds, ArrayList<String> args) {
		if (args.size() != 2)
			return;
		// parse the number to determine how many builds to return
		int number_of_recent_builds = Math.abs(Integer.parseInt(args.get(1)));
		Iterator<AbstractBuild<?, ?>> it = builds.iterator(); // get an iterator from the collection
		while (it.hasNext()) {
			it.next();
			if (number_of_recent_builds <= 0) { // get the first n builds
				it.remove(); // remove all the rest
			}
			number_of_recent_builds--; // decrement of builds
		}
	}

}

/**
 * User Filter Remove all builds that doesn't belong to specific user.
 * 
 * args : user < user name >
 * 
 * This class encapsulate the GetUserHistory class. ( Adapter )
 * 
 * @author Zehao
 *
 */
class UserFilter extends AbstractBuildsFilter {
	/**
	 * Applies filter to builds with args
	 * 
	 * @param builds
	 * @param args
	 *            ArrayList of user and userformat
	 */
	@Override
	public void applyFilter(Collection<AbstractBuild<?, ?>> builds, ArrayList<String> args) {
		if (args.size() != 2)
			return;
		Collection<AbstractBuild<?, ?>> newbuilds = new GetUserHistory(builds, args.get(1));
		builds.retainAll(newbuilds);
	}

}
