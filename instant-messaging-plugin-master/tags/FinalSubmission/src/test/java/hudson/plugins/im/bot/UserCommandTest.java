package hudson.plugins.im.bot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import hudson.model.Item;
import hudson.model.ParameterValue;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BooleanParameterValue;
import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.BooleanParameterDefinition;
import hudson.model.RunParameterDefinition;
import hudson.model.RunParameterValue;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.model.User;
import hudson.plugins.im.Sender;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class UserCommandTest {

	@Rule
	public JenkinsRule j = new JenkinsRule();

	private Bot bot;

	public String getFinalResult(String[] s) {
		User user = User.get("superman");
		User[] users = { user, User.get("spideman"), User.get("batman") };

		AbstractProject project = mock(AbstractProject.class);
		AbstractBuild<?, ?> build = mock(AbstractBuild.class);
		when(build.hasParticipant(User.get("batman"))).thenReturn(true);
		when(project.getLastBuild()).thenReturn(build);
		when(build.getUrl()).thenReturn("one");

		AbstractBuild<?, ?> prevBuild = build;
		for (int i = 0; i < 50; i++) {
			AbstractBuild tempBuild = (AbstractBuild) mock(AbstractBuild.class);

			when(tempBuild.hasParticipant(users[i % 3])).thenReturn(true);
			when(tempBuild.getUrl()).thenReturn(String.valueOf(i));
			doReturn(tempBuild).when(prevBuild).getPreviousBuild();
			prevBuild = tempBuild;
		}

		Sender sender = new Sender("tester");
		String[] args = { "userHistory", "superman" };

		JobProvider jobProvider = mock(JobProvider.class);
		UserCommand cmd = new UserCommand();
		cmd.setJobProvider(jobProvider);
		ArrayList<AbstractProject<?, ?>> list = new ArrayList();
		list.add(project);
		UserCommand command = new UserCommand();
		String result = command.getMessageForJob(list, s).toString();
		return result;
	}

	@Before
	public void setUp() {
		bot = mock(Bot.class);
	}

	@Test
	public void testNoUser() {
		Sender sender = new Sender("tester");
		String[] args = { "userHistory" };

		UserCommand cmd = new UserCommand();
		String reply = cmd.getReply(bot, sender, args);
		assertEquals("tester: syntax is: 'userHistory <username>'", reply);
	}

	@Test
	public void testFakeUser() {
		Sender sender = new Sender("tester");
		String[] args = { "userHistory", "foo" };

		UserCommand cmd = new UserCommand();
		String reply = cmd.getReply(bot, sender, args);
		assertEquals("tester: don't know a user named foo", reply);
	}

	@Test
	public void testUsersWithOneBuildCase() {
		User user = User.get("superman");
		User[] users = { user, User.get("spideman"), User.get("batman") };

		AbstractProject project = mock(AbstractProject.class);
		AbstractBuild<?, ?> build = mock(AbstractBuild.class);
		when(build.hasParticipant(user)).thenReturn(true);
		when(project.getLastBuild()).thenReturn(build);
		when(build.getUrl()).thenReturn("one");

		String[] args = { "userHistory", "superman" };

		UserCommand cmd = new UserCommand();
		ArrayList<AbstractProject<?, ?>> list = new ArrayList();
		list.add(project);
		CharSequence reply = cmd.getMessageForJob(list, args);
		String replyStr = reply.toString();
		String expectedStr = "null (null ago): null: ";
		expectedStr += String.valueOf(Hudson.getInstance().getRootUrl()) + "one\n";
		assertEquals(expectedStr, replyStr);
	}

	@Test
	public void testNumberSuccess() throws Exception {
		String[] s = new String[] { "userHistory", "superman", "1" };
		String result = getFinalResult(s);
		boolean test = result.contains("null");
		assertEquals(test, true);
	}

	@Test
	public void testWithoutNumberArgument() throws Exception {
		String[] s = new String[] { "userHistory", "superman" };
		String result = getFinalResult(s);
		int len = result.split("\r\n|\r|\n").length;
		assertEquals(len, 5);
	}

	@Test
	public void testWithNumberArgument() throws Exception {
		String[] s = new String[] { "userHistory", "superman", "Default", "3" };
		String result = getFinalResult(s);
		int len = result.split("\r\n|\r|\n").length;
		assertEquals(len, 3);
	}
}
