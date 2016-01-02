package hudson.plugins.im.bot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import java.util.Arrays;
import java.util.Collection;
import java.util.*;

import hudson.model.Item;
import hudson.model.ParameterValue;
import hudson.model.AbstractProject;
import hudson.model.AbstractBuild;
import hudson.model.BooleanParameterValue;
import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.model.FreeStyleBuild;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.BooleanParameterDefinition;
import hudson.model.RunParameterDefinition;
import hudson.model.RunParameterValue;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.plugins.im.Sender;
import hudson.plugins.im.tools.MessageHelper;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.util.Calendar;

/**
 * Test case for the 'showif' command with username
 * 
 * @author rtfreed2, avdavis3
 */
public class ShowIfCommandTest {

	@Rule public JenkinsRule j = new JenkinsRule();
	
    private FreeStyleProject project;
    private List<ParameterValue> parsedParameters;

    private AbstractProject<?,?> createProject(Calendar date, int number, String name) {
        AbstractProject project = mock(AbstractProject.class);
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        when(project.getLastBuild()).thenReturn(build);
        
        doReturn(null).when(build).getPreviousBuild();
        doReturn(number).when(build).getNumber();
        doReturn(date).when(build).getTimestamp();
        doReturn(project).when(build).getProject();
        when(project.getName()).thenReturn(name);
        return project;
    }

    private AbstractProject<?,?> createProjectWithBuilds(Calendar date, String name, int n, Calendar date_final) {
        AbstractProject project = mock(AbstractProject.class);
        ArrayList<AbstractBuild<?, ?>> builds = new ArrayList<AbstractBuild<?,?>>();
        for (int i = 0; i < n; i++) {
            AbstractBuild<?, ?> build = mock(AbstractBuild.class);
            if (i == 0) {
                doReturn(null).when(build).getPreviousBuild();
            } else {
                doReturn(builds.get(i-1)).when(build).getPreviousBuild();
            }
            doReturn(project).when(build).getProject();
            doReturn(i).when(build).getNumber();
            if(i != 0) {
                doReturn(date).when(build).getTimestamp();
            } else {
                doReturn(date_final).when(build).getTimestamp();
            }
            builds.add(build);
        }
        when(project.getLastBuild()).thenReturn(builds.get(0));
        when(project.getName()).thenReturn(name);
        return project;
    }

    @Test
    public void testMultiBuild() {
        @SuppressWarnings({ "rawtypes" })
        Calendar date = Calendar.getInstance();
        date.set(2015,1,1,1,0,0);
        Calendar date2 = Calendar.getInstance();
        date2.set(2012,1,1,1,0,0);
        AbstractProject project = createProjectWithBuilds(date,"test",15,date2);
        AbstractProject project2 = createProjectWithBuilds(date2,"test2",11,date);
        ArrayList<AbstractProject<?,?>> list = new ArrayList<AbstractProject<?,?>>();
        list.add(project);
        list.add(project2);
        ShowIfCommand command = new ShowIfCommand();
        String result = command.getMessageForJob(list,
                new String[] { "jobs", "<","1"}).toString();
        boolean test = result.startsWith("last build:");
        assertEquals(test, true);
    }

    @Test
    public void testNothing() {
     	@SuppressWarnings({ "rawtypes" })
        Calendar date = Calendar.getInstance();
        date.set(2015,1,1,1,0,0);
        AbstractProject project = createProject(date,0,"test");
        ArrayList<AbstractProject<?,?>> list = new ArrayList<AbstractProject<?,?>>();
        list.add(project);
        ShowIfCommand command = new ShowIfCommand();
        String result = command.getMessageForJob(list,
                new String[] { "user", "rtfreed2"}).toString();
        assertEquals("No Builds Found! \n", result);
    }

    @Test
    public void jobsTest() {
     	@SuppressWarnings({ "rawtypes" })
        Calendar date = Calendar.getInstance();
        date.set(2015,1,1,1,0,0);
        AbstractProject project = createProject(date,0,"test");
        ArrayList<AbstractProject<?,?>> list = new ArrayList<AbstractProject<?,?>>();
        list.add(project);
        ShowIfCommand command = new ShowIfCommand();
        String result = command.getMessageForJob(list,
                new String[] { "jobs","<","1"}).toString();

        // does string start with last build? If so then we know it has returned a build
        boolean test = result.startsWith("last build:");
        assertEquals(test, true);
    }

    @Test
    public void dateTest() {
     	@SuppressWarnings({ "rawtypes" })
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2015);
        date.set(Calendar.MONTH, 1);
        date.set(Calendar.DAY_OF_MONTH, 1);
        
        AbstractProject project = createProject(date,0,"test");
        ArrayList<AbstractProject<?,?>> list = new ArrayList<AbstractProject<?,?>>();
        list.add(project);
        ShowIfCommand command = new ShowIfCommand();
        String result = command.getMessageForJob(list,
                new String[] { "date",">","2013-01-01"}).toString();

        // does string start with last build? If so then we know it has returned a build
        boolean test = result.startsWith("last build:");
        assertEquals(test, true);
    }

    @Test
    public void projectTest() {
     	@SuppressWarnings({ "rawtypes" })
        Calendar date = Calendar.getInstance();
        date.set(2015,1,1,1,0,0);
        AbstractProject project = createProject(date,0,"test");
        ArrayList<AbstractProject<?,?>> list = new ArrayList<AbstractProject<?,?>>();
        list.add(project);
        ShowIfCommand command = new ShowIfCommand();
        String result = command.getMessageForJob(list,
                new String[] { "project","test"}).toString();

        // does string start with last build? If so then we know it has returned a build
        boolean test = result.startsWith("last build:");
        assertEquals(test, true);
    }

    @Test
    public void buildTest() {
     	@SuppressWarnings({ "rawtypes" })
        Calendar date = Calendar.getInstance();
        date.set(2015,1,1,1,0,0);
        AbstractProject project = createProject(date,0,"test");
        ArrayList<AbstractProject<?,?>> list = new ArrayList<AbstractProject<?,?>>();
        list.add(project);
        ShowIfCommand command = new ShowIfCommand();
        String result = command.getMessageForJob(list,
                new String[] { "build","1"}).toString();

        // does string start with last build? If so then we know it has returned a build
        boolean test = result.startsWith("last build:");
        assertEquals(test, true);
    }

    @Test
    public void combo_project_build_Test() {
     	@SuppressWarnings({ "rawtypes" })
        Calendar date = Calendar.getInstance();
        date.set(2015,1,1,1,0,0);
        AbstractProject project = createProject(date,0,"test");
        ArrayList<AbstractProject<?,?>> list = new ArrayList<AbstractProject<?,?>>();
        list.add(project);
        ShowIfCommand command = new ShowIfCommand();
        String result = command.getMessageForJob(list,
                new String[] { "build","1","|","project","test"}).toString();

        // does string start with last build? If so then we know it has returned a build
        boolean test = result.startsWith("last build:");
        assertEquals(test, true);
    }

    @Test
    public void combo_date_project_build_Test() {
     	@SuppressWarnings({ "rawtypes" })
        Calendar date = Calendar.getInstance();
        date.set(2015,1,1,0,0,0);
        AbstractProject project = createProject(date,0,"test");
        ArrayList<AbstractProject<?,?>> list = new ArrayList<AbstractProject<?,?>>();
        list.add(project);
        ShowIfCommand command = new ShowIfCommand();
        String result = command.getMessageForJob(list,
                new String[] { "build","1","|","project","test","|","date",">","2013-01-01"}).toString();

        // does string start with last build? If so then we know it has returned a build
        boolean test = result.startsWith("last build:");
        assertEquals(test, true);
    }
}
