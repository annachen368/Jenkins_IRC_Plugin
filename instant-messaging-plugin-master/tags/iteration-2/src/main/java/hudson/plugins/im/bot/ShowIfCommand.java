/*
 * Created on Apr 22, 2007
 */
package hudson.plugins.im.bot;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.im.tools.MessageHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import hudson.plugins.im.tools.ExceptionHelper;
import java.text.DateFormat;
import java.text.ParseException;

import java.lang.Integer;
/**
 * @author BookReaders CS 427 Group UIUC
 */


@Extension
public class ShowIfCommand extends AbstractSourceQueryCommand {
    private static final Logger LOGGER  = Logger.getLogger(ShowIfCommand.class.getName());
    @Override
    public Collection<String> getCommandNames() {
        System.out.println("[CHECK] getCommandNames() was reached in ShowIf\n");
        return Arrays.asList("ShowIf","si");
    }

    @Override
    protected CharSequence getMessageForJob(Collection<AbstractProject<?, ?>> projects, String[] args) {
        // add a logger for args
        String log_msg = args.toString();
        LOGGER.warning(log_msg);

        String spacer = "|";
        int arg_len = args.length;
        
        LOGGER.warning("there are " + Integer.toString(arg_len) + " args items\n");

        // convert projects Collection to ArrayList
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        
        // Parse Query
        for (int i = 0; i < arg_len; i++) {
            if(!args[i].equals("ShowIf") && !args[i].equals("!jenkins") && !args[i].equals(spacer)){
                ArrayList<String> temp = new ArrayList<String>();
                for (; i < arg_len && !args[i].equals(spacer); i++) {
                    temp.add(args[i]);
                }
                list.add(temp);
            }
        }

        // get all builds
        // if this command is slow may need to put limiter on while loop
        Collection<AbstractBuild<?,?>> builds = new ArrayList<AbstractBuild<?,?>>();
        for (AbstractProject<?, ?> abProj: projects) {
            AbstractBuild<?,?> tempbuild = abProj.getLastBuild();
            while(tempbuild != null){
                builds.add(tempbuild);
                tempbuild = tempbuild.getPreviousBuild();
            }
        }
        
        // iterate over the query chunks
        for (int i = 0; i < list.size(); i++) {
            switch ((list.get(i)).get(0)) {
                case "user":
                    if (list.get(i).size() <= 1) {
                        return "Malformed size filter.";
                    }
                    builds = userFilter(builds,(list.get(i)).get(1));
                    break;
                case "date":
                    if (list.get(i).size() <= 2) {
                        return "Malformed date filter.";
                    }
                    builds = dateFilter(builds,(list.get(i)).get(2),(list.get(i)).get(1));
                    break;
                case "project":
                    if (list.get(i).size() <= 1) {
                        return "Malformed project filter.";
                    }
                    builds = projectFilter(builds,(list.get(i)).get(1));
                    break;
                case "build":
                    if (list.get(i).size() <= 1) {
                        return "Malformed build filter.";
                    }
                    builds = buildFilter(builds,(list.get(i)).get(1));
                    break;
                case "jobs":
                    if (list.get(i).size() <= 2) {
                        return "Malformed job filter.";
                    }
                    builds = jobsFilter(builds,(list.get(i)).get(2),(list.get(i)).get(1));
                    break;
                default: // add evil message
                    return "Malformed command.";
            }
            if (builds.isEmpty()) {
                StringBuilder temp = new StringBuilder(32);
                temp.append("No Builds Found! \n");
                return temp;
            }
        }
        StringBuilder msg = new StringBuilder(builds.size());
        for (AbstractBuild<?, ?> abBuild: builds) {
            msg.append("last build: ").append(abBuild.getNumber()).append(" (")
                .append(abBuild.getTimestampString()).append(" ago): ").append(abBuild.getResult())
                //.append(": ").append(MessageHelper.getBuildURL(abBuild))
                .append(System.getProperty("line.separator"));
        }
        return msg;
    }



    @Override
    protected String getCommandShortName() {
        return "detailed history";
    }

    // sub query priavate funcitons
    private Collection<AbstractBuild<?, ?>> userFilter(Collection<AbstractBuild<?, ?>> builds, String username) {
        return new GetUserHistory(builds, username);
    }
    
    private Collection<AbstractBuild<?, ?>> dateFilter(Collection<AbstractBuild<?,?>> builds, String dt, String op) {
         // *.getTime() might be a thing
        Iterator<AbstractBuild<?,?>> it = builds.iterator();
        DateFormat format = new SimpleDateFormat("YYYY-MM-dd-HH-mm");
        long date = 0;
        try{
            date = (format.parse(dt)).getTime();
        }
        catch(ParseException e) {
            LOGGER.warning(ExceptionHelper.dump(e));
        }
        while(it.hasNext()) {
            AbstractBuild<?,?> item = it.next();
            long temp = (item.getTimestamp()).getTimeInMillis();
            switch(op) {
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
                    LOGGER.warning("WARNING: Bad operator passed into date filter for ShowIfCommand");
                    break;
            }
        }
        return builds;
    }
    
    private Collection<AbstractBuild<?, ?>> jobsFilter(Collection<AbstractBuild<?,?>> builds, String job, String op) {
        Iterator<AbstractBuild<?,?>> it = builds.iterator();
        int job_num = Integer.parseInt(job);
        while(it.hasNext()) {
            AbstractBuild<?,?> item = it.next();
            int temp = item.getNumber();
            switch(op) {
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
                    LOGGER.warning("WARNING: Bad operator passed into job filter for ShowIfCommand");
                    break;
            }
        }
        return builds;
    }

    private Collection<AbstractBuild<?, ?>> projectFilter(Collection<AbstractBuild<?,?>> builds, String projectName) {
        Iterator<AbstractBuild<?,?>> it = builds.iterator(); // get an interator from the collection
        while(it.hasNext()) {
            AbstractBuild<?,?> item = it.next();
            if (!(item.getProject().getName().equals(projectName))) {
                it.remove();
            }
        }
        return builds;
    }
    
    private Collection<AbstractBuild<?, ?>> buildFilter(Collection<AbstractBuild<?,?>> builds, String n) {
        // parse the number to determine how many builds to return
        int number_of_recent_builds = (Integer.parseInt(n) < 0) ? -Integer.parseInt(n) : Integer.parseInt(n); // make sure the number is non-negative
        Iterator<AbstractBuild<?,?>> it = builds.iterator(); // get an interator from the collection
        while(it.hasNext()) {
            AbstractBuild<?,?> item = it.next();
            if (number_of_recent_builds <= 0) {  // get the first n builds
                try {
                    it.remove();  // remove all the rest
                } catch (IllegalStateException e) {
                    LOGGER.warning(ExceptionHelper.dump(e));
                };
            };
            number_of_recent_builds--; // decrementer of builds
        }
        // return truncated builds
        return builds;
    }
}
