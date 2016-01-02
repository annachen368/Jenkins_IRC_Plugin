package hudson.plugins.im.bot;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.plugins.im.IMCause;
import hudson.plugins.im.Sender;

public class OpenCommandTest {

	private Pattern pattern = Pattern.compile("https?://[^/]*?/(([^/]*/)*)");

	private String getPath(String url) {
		Matcher m = pattern.matcher(url);
		if (m.find()) {
			return m.group(1);
		} else {
			return "Not A valid URL!!";
		}
	}

	private String shortFile = OpenCommandTest.createString(5);
	private String longFile = OpenCommandTest.createString(200);

	private Sender sender = new Sender("tester");
	private OpenCommand cmd;
	private OpenCommand spy;
	private FilePath root, dir1, dir2, dir3, file1, file2, file3, file4;
	private JobProvider jobProvider = mock(JobProvider.class);
	private AbstractProject<?, ?> proj1 = mockProject(jobProvider, "proj1");

	@Before
	public void initialize() {
		// Create The work space
		root = new FilePath(new File("root"));
		dir1 = new FilePath(root, "test1");
		dir2 = new FilePath(dir1, "test2");
		dir3 = new FilePath(dir2, "fileTest");
		try {
			file1 = root.createTextTempFile("fileInLevel1", null, "");
			file2 = dir1.createTextTempFile("fileInLevel2", "txt", shortFile);
			file3 = dir2.createTextTempFile("fileInLevel3", "txt", longFile);
			file4 = dir2.createTextTempFile("fileTest", "txt", shortFile);
			dir3.mkdirs();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create The Projects
		cmd = new OpenCommand();
		cmd.setJobProvider(jobProvider);

		spy = spy(cmd);

		doReturn("http://domain:port/").when(spy).getBaseURL();
		doReturn(root).when(spy).getWorkSpace(any(AbstractProject.class));
	}

	@After
	public void cleanUp() {
		// Clean up the workspace
		try {
			root.deleteRecursive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testIncompleCommandArguments() {
		AbstractProject<?, ?> proj2 = mockProject(jobProvider, "proj2");
		List<AbstractProject<?, ?>> projects = new LinkedList<AbstractProject<?, ?>>();
		projects.add(proj1);
		projects.add(proj2);
		doReturn(projects).when(jobProvider).getTopLevelJobs();

		String[] args = { "open" };

		String reply = spy.getReply(null, sender, args);

		assertTrue(reply.endsWith("proj1 proj2 "));
	}

	@Test
	public void testWithRightProjectName() {
		String[] args = { "open", "proj1" };
		String reply = spy.getReply(null, sender, args);
		String[] res = reply.split("[\\s]+");
		for (String file : res) {
			if (file.charAt(0) == '[') {
				assertEquals(file, "[test1]");
			} else {
				assertTrue(file.startsWith("fileInLevel1"));
			}
		}
	}

	@Test
	public void testWithWrongProjectName() {
		String[] args = { "open", "proj3" };
		String reply = spy.getReply(null, sender, args);
		assertEquals(reply, "Unknown job 'proj3'");
	}

	@Test
	public void testPathToDir() {
		// right path
		String[] args1 = { "open", "proj1", "test1" };
		String reply1 = spy.getReply(null, sender, args1);
		String[] res = reply1.split("[\\s]+");
		for (String file : res) {
			if (file.charAt(0) == '[') {
				assertEquals(file, "[test2]");
			} else {
				assertTrue(file.startsWith("fileInLevel2"));
			}
		}

		// wrong path
		String[] args2 = { "open", "proj1", "test3" };
		String reply2 = spy.getReply(null, sender, args2);
		assertEquals(reply2, "Directory/File Not Found!!");
	}

	@Test
	public void testPathToFile() {
		// right path to Short File
		String[] args1 = { "open", "proj1", "test1/fileInLevel2" };
		String reply1 = spy.getReply(null, sender, args1);
		assertEquals(reply1,shortFile);
		
		// Right Path to Long File
		String[] args2 = { "open", "proj1", "test1/test2/fileInLevel3" };
		String reply2 = spy.getReply(null, sender, args2);
		assertTrue( reply2.startsWith("http://domain:port/job/proj1/ws/test1/test2/fileInLevel3") );
		
		// Wrong Path
		String[] args3 = { "open", "proj1", "test1/test2/fileInLevel5" };
		String reply3 = spy.getReply(null, sender, args3);
		assertEquals(reply3, "Directory/File Not Found!!");
	}

	@Test
	public void testMultiFileFound() {
		String[] args1 = { "open", "proj1", "test1/test2/file" };
		String reply1 = spy.getReply(null, sender, args1);
		assertTrue( reply1.startsWith("Available Directory/File Name:\n") );
		assertTrue( reply1.indexOf("fileInLevel3") >= 0 );
		assertTrue( reply1.indexOf("[fileTest]") >= 0 );
		assertTrue( reply1.indexOf("fileTest") >= 0 );
	}
	
	
	@Test
	public void testSortFileName() {
		String[] args1 = { "open", "proj1", "test1/test2" };
		String reply1 = spy.getReply(null, sender, args1);
		String[] res = reply1.split("[\\s]+");
		assertEquals(res[0],"[fileTest]");
		assertTrue(res[1].startsWith("fileInLevel3"));
		assertTrue(res[2].startsWith("fileTest"));
	}
	// Utilities Methods

	private static String createString(int n) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < n; i++) {
			res.append(i + " : This is the test line for file whatever!!!!!--------------------------\n");
		}
		return res.toString();
	}

	@SuppressWarnings("unchecked")
	private AbstractProject<?, ?> mockProject(JobProvider jobProvider, String name) {
		@SuppressWarnings("rawtypes")
		AbstractProject project = mock(FreeStyleProject.class);
		when(jobProvider.getJobByNameOrDisplayName(name)).thenReturn(project);
		when(project.hasPermission(Item.BUILD)).thenReturn(Boolean.TRUE);
		when(project.isBuildable()).thenReturn(true);

		@SuppressWarnings("rawtypes")
		ItemGroup parent = mock(ItemGroup.class);
		when(parent.getFullDisplayName()).thenReturn("");
		when(project.getParent()).thenReturn(parent);
		when(project.getFullDisplayName()).thenReturn(name);

		when(project.scheduleBuild(anyInt(), any(IMCause.class))).thenReturn(true);
		return project;
	}

	@SuppressWarnings("rawtypes")
	private AbstractBuild<?, ?> mockBuild(AbstractProject proj) {
		AbstractBuild<?, ?> res = mock(AbstractBuild.class);
		doReturn(res).when(proj).getLastBuild();
		return res;
	}
}
