package hudson.plugins.im.bot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.DescriptorExtensionList;
import hudson.FilePath;
import hudson.maven.AbstractMavenProject;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BallColor;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.HealthReport;
import hudson.model.ItemGroup;
import hudson.model.ParameterValue;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.User;
import hudson.plugins.im.Sender;
import hudson.plugins.im.tools.MessageHelper;
import hudson.scm.ChangeLogSet;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SCM;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class RepoCommandTest {

	private FreeStyleBuild build;
	private AbstractMavenProject job;
	private ItemGroup parent;
	private User user;
	private ChangeLogSet.Entry item;
	private ChangeLogSet changeSet;
	private Iterator<ChangeLogSet.Entry> iterator;
	private Result result;
	private Run run;
	private File f;

	@Before
	public void setUp() {
		build = mock(FreeStyleBuild.class);
		job = mock(AbstractMavenProject.class);
		parent = mock(ItemGroup.class);
		user = mock(User.class);
		item = mock(ChangeLogSet.Entry.class);
		changeSet = mock(ChangeLogSet.class);
		iterator = mock(Iterator.class);
		f = mock(File.class);
		run = mock(Run.class);
		when(build.getUrl()).thenReturn("http://www.fakeurl.com");
		when(parent.getFullDisplayName()).thenReturn("");
		when(job.getParent()).thenReturn(parent);
		when(job.getFullDisplayName()).thenReturn("fsProject");
		when(job.getLastBuild()).thenReturn(build);	
		when(user.toString()).thenReturn("Batman");
		Object[] items = new Object[1];
		items[0] = item;
		when(item.getAuthor()).thenReturn(user);
		when(item.getMsg()).thenReturn("Batman to the rescue!");
		when(item.getCommitId()).thenReturn("19888");
		when(iterator.hasNext()).thenReturn(Boolean.TRUE, Boolean.FALSE);
		when(iterator.next()).thenReturn(item);
		when(changeSet.isEmptySet()).thenReturn(false);
		when(changeSet.iterator()).thenReturn(iterator);
		when(build.getChangeSet()).thenReturn(changeSet);
		when(build.getNumber()).thenReturn(9);
		when(item.getParent()).thenReturn(changeSet);
		when(item.getParent().getRun()).thenReturn(run);
		when(run.getUrl()).thenReturn("www.123.com");
		when(run.getBuildStatusUrl()).thenReturn("www.789.com");
		when(f.getName()).thenReturn("filename");
		when(run.getRootDir()).thenReturn(f);
	}
 
	public String getFinalResult(String[] s) {
		AbstractProject project = job;
		ArrayList<AbstractProject<?, ?>> list = new ArrayList<AbstractProject<?, ?>>();
		list.add(project);
		RepoCommand command = new RepoCommand();
		String result = command.getMessageForJob(list, s).toString();
		return result;
	}
	
	@Test
	public void testRepoCommand1() throws Exception {
		String[] s = new String[] {"repo"};
		String result = getFinalResult(s);
		boolean test = result.contains("Building");
		assertEquals(test, true);
	}

	@Test
	public void testNumberSuccess() throws Exception {
		String[] s = new String[] {"repo","reponumber","19888"};
		String result = getFinalResult(s);
		boolean test = result.contains("Building");
		assertEquals(test, true);
	}
	
	@Test
	public void testNumberFailed() throws Exception {
		String[] s = new String[] {"repo","reponumber","196"};
		String result = getFinalResult(s);
		boolean test = result.contains("Please");
		assertEquals(test, true);
	}
	
	@Test
	public void testRepoCommand3() throws Exception {
		String[] s = new String[] {"repo","show","3"};
		String result = getFinalResult(s);
		boolean test = result.contains("Building");
		assertEquals(test, true);
	}
	
	@Test
	public void testRepoCommand4() throws Exception {
		String[] s = new String[] {"repo","all"};
		String result = getFinalResult(s);
		boolean test = result.contains("Building");
		assertEquals(test, true);
	}
	
	@Test
	public void testFail() throws Exception {
		String[] s = new String[] {"repo","fail"};
		String result = getFinalResult(s);
		boolean test = result.contains("Building");
		assertEquals(test, false);
	}
}
