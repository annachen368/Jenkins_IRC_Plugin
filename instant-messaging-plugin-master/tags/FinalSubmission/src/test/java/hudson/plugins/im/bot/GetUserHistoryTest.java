package hudson.plugins.im.bot;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import hudson.maven.AbstractMavenProject;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleBuild;
import hudson.model.HealthReport;
import hudson.model.ItemGroup;
import hudson.model.User;
import hudson.plugins.im.Sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Test case for the 'showif' command with username
 * 
 * @author ywang515
 */
public class GetUserHistoryTest {

	private final Pattern percentagePattern = Pattern.compile("\\D(\\d+)[%]"); 
	
	@Test
	public void testNoJobFound() {
	    JobProvider jobProvider = mock(JobProvider.class);
		HealthCommand cmd = new HealthCommand();
		cmd.setJobProvider(jobProvider);
		
		Sender sender = new Sender("tester");
		String[] args = {"user"};
		String reply = cmd.getReply(null, sender, args);
		
		assertEquals(sender + ": no job found", reply);
	}
	
	@SuppressWarnings("rawtypes")
    @Test
	public void testHealth() throws Exception {
		
		FreeStyleBuild build = mock(FreeStyleBuild.class);
		when(build.getUrl()).thenReturn("job/foo/32/");
		
		HealthReport healthMock = mock(HealthReport.class);
		when(healthMock.getDescription()).thenReturn("Fine");
		when(healthMock.getScore()).thenReturn(100);
		
		AbstractMavenProject job = mock(AbstractMavenProject.class);
		ItemGroup parent = mock(ItemGroup.class);
		when(parent.getFullDisplayName()).thenReturn("");
		when(job.getParent()).thenReturn(parent);
        when(job.getFullDisplayName()).thenReturn("fsProject");
        when(job.getLastBuild()).thenReturn(build);
        when(job.getBuildHealth()).thenReturn(healthMock);
		
		HealthCommand cmd = new HealthCommand();
		String reply = cmd.getMessageForJob(job).toString();
		
		assertFalse(reply.contains(AbstractMultipleJobCommand.UNKNOWN_JOB_STR));
		assertTrue(reply.contains("fsProject"));
		Matcher m = percentagePattern.matcher(reply);
		assertTrue(m.find());
		String match = m.group(1);
		assertEquals("100", match);
	}
	
	@SuppressWarnings("rawtypes")
    @Test
	public void testFailure() throws Exception {
        FreeStyleBuild build = mock(FreeStyleBuild.class);
        when(build.getUrl()).thenReturn("job/foo/32/");

        HealthReport healthMock = mock(HealthReport.class);
        when(healthMock.getDescription()).thenReturn("Cloudy");
        when(healthMock.getScore()).thenReturn(0);

        AbstractMavenProject job = mock(AbstractMavenProject.class);
		ItemGroup parent = mock(ItemGroup.class);
		when(parent.getFullDisplayName()).thenReturn("");
		when(job.getParent()).thenReturn(parent);
        when(job.getFullDisplayName()).thenReturn("fsProject");
        when(job.getLastBuild()).thenReturn(build);
        when(job.getBuildHealth()).thenReturn(healthMock);
	    
		HealthCommand cmd = new HealthCommand();
		{
			String reply = cmd.getMessageForJob(job).toString();
		
			assertFalse(reply.contains(AbstractMultipleJobCommand.UNKNOWN_JOB_STR));
			assertTrue(reply.contains("fsProject"));
			Matcher m = percentagePattern.matcher(reply);
			assertTrue(m.find());
			String match = m.group(1);
			assertEquals("0", match);
		}
	}
	
	@Test
	public void testUsername() {

		String username= "ywang515";
		ShowIfCommand sic = new ShowIfCommand();
		AbstractProject<?, ?> project = mock(AbstractProject.class);
		ArrayList<AbstractProject<?, ?>> projects = new ArrayList<>();
		projects.add(project);

		String args[] = {"user " + username};
//		CharSequence result=sic.getMessageForJob(projects, args);
//		System.out.println(String.valueOf(result));
//		assertEquals("'" + username + "' has no builds.", String.valueOf(result));
	}
	
	@Test
	public void testUsername2() {
		String username="whom";
		ShowIfCommand sic = new ShowIfCommand();
		AbstractProject<?, ?> project = mock(AbstractProject.class);
		ArrayList<AbstractProject<?, ?>> projects = new ArrayList<>();
		projects.add(project);

		String args[] = {"user " + username};
//		CharSequence result=sic.getMessageForJob(projects, args);
//		System.out.println(String.valueOf(result));
//		assertEquals("There is no user name with '" + username + "'", String.valueOf(result));

	}
	
//	@Test
//	public void testUsernameWithBuilds() {
//
//		String username= "ywang515";
//		ShowIfCommand sic = new ShowIfCommand();
//		AbstractProject<?, ?> project = mock(AbstractProject.class);
//		ArrayList<AbstractProject<?, ?>> projects = new ArrayList<>();
//		
//		
//		projects.add(project);
//
//		String args[] = {"user " + username};
//		CharSequence result=sic.getMessageForJob(projects, args);
//		System.out.println(String.valueOf(result));
//		assertEquals("'" + username + "' has no builds.", String.valueOf(result));
//	}
}
