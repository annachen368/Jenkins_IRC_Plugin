package hudson.plugins.im.bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.im.Sender;
import hudson.FilePath;
import hudson.scm.ChangeLogSet;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SCM;
import hudson.scm.ChangeLogSet.Entry;
import jenkins.model.Jenkins;

@Extension
public class RepoCommand extends AbstractSourceQueryCommand {

	private static final String SYNTAX = " [show <int> | reponumber <int> | all]";
	private static final String HELP = SYNTAX + " - show the repository details of build history";
	public final String REPO = "repo";
	HashMap<String, Integer> cmd_list;

	@Override
	public Collection<String> getCommandNames() {
		return Collections.singleton("repo");
	}
	
	@Override
	protected CharSequence getMessageForJob(Collection<AbstractProject<?, ?>> projects, String[] args) {
		
		int number = 0;
		int counter = 5;
	    int flag = 1;
	    boolean lock = false;
	    
		isNull(); //Initialize

		if (args.length == 1) {
			counter = 5;
			flag = 1;
			lock = false;
			number = 0;
		} else if (args.length >= 2) {
			int cmd_idx = cmd_list.get(args[1]) == null ? 0 : cmd_list.get(args[1]);
			switch (cmd_idx) {
			case 1:
				if (args.length == 2){
					counter=5;
					flag=1;
					lock=false;
					number = 0;
				}
				else{
					counter = Integer.parseInt(args[2]);
					flag=1;
					lock=false;
					number=0;
				}
				break;
			case 2:
				number = Integer.parseInt(args[2]);
				flag=0;
				lock=true;
				counter=5;
				break;
			case 3:
				counter=5;
				flag=0;
				lock=false;
				number=0;
				break;
			default:
				return getAvailableCommand();
			}
		} 

		Collection<AbstractBuild<?,?>> builds = new ArrayList<AbstractBuild<?,?>>();
		
		
		//get all the builds
        for (AbstractProject<?, ?> abProj: projects) {
        	        	
            AbstractBuild<?,?> tempbuild = abProj.getLastBuild();
            while(tempbuild != null){
                builds.add(tempbuild);
                tempbuild = tempbuild.getPreviousBuild();
            }
        }
		
        StringBuilder msg = new StringBuilder(builds.size());

        for (AbstractBuild<?, ?> abBuild: builds) {
        	
        	if (counter<=0 && flag==1){
            	break;
            }
        	counter--;
        	if(lock==false){
        		msg.append("Building #: ").append(abBuild.getNumber()); 
        	}
             
            for (Entry entry : abBuild.getChangeSet()) {
            	if((Integer.parseInt(entry.getCommitId()) == number) || (lock==false)) {
            		if(lock){
            			msg.append("Building #: ").append(abBuild.getNumber()); 
            		}
            		msg.append("\nRevision: "+entry.getCommitId()); 
            		msg.append(entry.getAffectedPaths());           	
            		msg.append("\n* " + entry.getAuthor()).append(": ").append(entry.getMsg());
            		if(lock) {
            			break;
            		}
            	}
    		}
            msg.append("\n");
        }
        
        if(!msg.toString().contains("Building")) {
        	msg.append("Please try again!");
        }
        return msg;
	}

	private void isNull() {
		if (cmd_list == null) {
			cmd_list = new HashMap<String, Integer>();

			cmd_list.put("show", 1);

			cmd_list.put("reponumber", 2);

			cmd_list.put("all", 3);
		}
	}

	@Override
	protected String getCommandShortName() {
		return REPO;
	}

	@Override
	public String getHelp() {
		return HELP;
	}
	
	private String getAvailableCommand() {
		StringBuilder buf = new StringBuilder();
		buf.append("Available Command: ");
		buf.append("show [how many last builds wanted], ");
		buf.append("reponumber [the repository number for search], ");
		buf.append("all(be careful to choose, it will take a long time to run) ");
		return buf.substring(0, buf.length() - 1);
	}
}
