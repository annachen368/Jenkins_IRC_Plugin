package hudson.plugins.im.bot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.maven.AbstractMavenProject;
import hudson.model.BallColor;
import hudson.model.FreeStyleBuild;
import hudson.model.HealthReport;
import hudson.model.ItemGroup;
import hudson.model.Result;
import hudson.model.User;
import hudson.plugins.im.Sender;
import hudson.plugins.im.tools.MessageHelper;
import hudson.scm.ChangeLogSet;

import org.junit.Before;
import org.junit.Test;

public class OverviewCommandTest {
	
	private final Pattern percentagePattern = Pattern.compile("\\D(\\d+)[%]"); 
	
	private FreeStyleBuild build;
	private HealthReport healthMock;
	private AbstractMavenProject job;
	private ItemGroup parent;
	private User user;
	private ChangeLogSet.Entry item;
	private ChangeLogSet changeSet;
	private Iterator<ChangeLogSet.Entry> iterator;
	private Result result;
	
	@Before
	public void setUp() {
		build = mock(FreeStyleBuild.class);
		when(build.getUrl()).thenReturn("http://www.fakeurl.com");
		
		healthMock = mock(HealthReport.class);
		when(healthMock.getDescription()).thenReturn("Fine");
		when(healthMock.getScore()).thenReturn(100);
		
		job = mock(AbstractMavenProject.class);
		
		parent = mock(ItemGroup.class);
		when(parent.getFullDisplayName()).thenReturn("");
		when(job.getParent()).thenReturn(parent);
        when(job.getFullDisplayName()).thenReturn("fsProject");
        when(job.getLastBuild()).thenReturn(build);
        
        user = mock(User.class);
		when(user.toString()).thenReturn("Batman");
        item = mock(ChangeLogSet.Entry.class);
		Object[] items = new Object[1];
		items[0] = item;
		when(item.getAuthor()).thenReturn(user);
		when(item.getMsg()).thenReturn("Batman to the rescue!");
		changeSet = mock(ChangeLogSet.class);
		
		iterator = mock(Iterator.class);
        when(iterator.hasNext()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(iterator.next()).thenReturn(item);
        
        when(changeSet.isEmptySet()).thenReturn(false);
        when(changeSet.iterator()).thenReturn(iterator);        
		when(build.getChangeSet()).thenReturn(changeSet);				
        when(build.getNumber()).thenReturn(9);
        when(build.getTimestampString()).thenReturn("10 min");
        
        result = Result.SUCCESS;
        when(build.getResult()).thenReturn(result);
        
        when(job.getBuildHealth()).thenReturn(healthMock);	
	}
	
	@Test
	public void testNoJobFound() {
	    JobProvider jobProvider = mock(JobProvider.class);
		OverviewCommand cmd = new OverviewCommand();
		cmd.setJobProvider(jobProvider);
		
		Sender sender = new Sender("tester");
		String[] args = {"overview"};
		String reply = cmd.getReply(null, sender, args);
		
		assertEquals(sender + ": no job found", reply);
	}
	
	@Test
	public void testOverview() throws Exception {
				
		OverviewCommand cmd = new OverviewCommand();
		String reply = cmd.getMessageForJob(job).toString();
		
		assertFalse(reply.contains(AbstractMultipleJobCommand.UNKNOWN_JOB_STR));
		assertTrue(reply.contains("fsProject"));
		Matcher m = percentagePattern.matcher(reply);
		assertTrue(m.find());
		String match = m.group(1);
		assertEquals("100", match);
		assertEquals("fsProject: Fine(100%)\n"+
		           "           Last Build: 9 (10 min ago): SUCCESS\n"+
		           "           nullhttp://www.fakeurl.com\n"+
		           "           * Batman: Batman to the rescue!",reply);
	}
	
	@Test
	public void testOverviewDisabled() throws Exception {		
		when(job.isDisabled()).thenReturn(true);		
		OverviewCommand cmd = new OverviewCommand();
		String reply = cmd.getMessageForJob(job).toString();
        assertTrue(reply.contains("(disabled"));
	}
	
	@Test
	public void testOverviewBuilding() throws Exception {		
		when(job.isBuilding()).thenReturn(true);		
		OverviewCommand cmd = new OverviewCommand();
		String reply = cmd.getMessageForJob(job).toString();
        assertTrue(reply.contains("(BUILDING:"));
	}
	
	@Test
	public void testOverviewInQueue() throws Exception {		
		when(job.isInQueue()).thenReturn(true);		
		OverviewCommand cmd = new OverviewCommand();
		String reply = cmd.getMessageForJob(job).toString();
        assertTrue(reply.contains("(in queue)"));
	}
	
	@Test
	public void testOverviewLastBuildNull() throws Exception {		
		when(job.getLastBuild()).thenReturn(null);		
		OverviewCommand cmd = new OverviewCommand();
		String reply = cmd.getMessageForJob(job).toString();
        assertTrue(reply.contains("no finished build yet"));
	}
	
}
