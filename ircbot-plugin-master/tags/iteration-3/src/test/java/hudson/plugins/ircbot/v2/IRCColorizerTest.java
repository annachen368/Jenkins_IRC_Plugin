package hudson.plugins.ircbot.v2;

import static org.junit.Assert.assertEquals;

import org.fusesource.jansi.Ansi.Color;
import org.junit.Test;
import org.jvnet.hudson.test.Bug;
import org.pircbotx.Colors;

public class IRCColorizerTest {
	
	

	@Test
	@Bug(22360)
	public void shouldColorizeKeywords() {
		String message = "Build job123 is STILL FAILING: https://server.com/build/42";
		String colorizedMessage = IRCColorizer.colorize(message);
		 
		assertEquals("Build job123 is " + Colors.BOLD + Colors.RED + "STILL FAILING" + Colors.NORMAL + ": https://server.com/build/42", colorizedMessage);
	}
	
	@Test
	//Check wether setter function could add new user
	public void setterTest1()
	{
		String nickname = "neverused";
		IRCColorizer.cleanUserPattern(nickname);
		int oldSize = IRCColorizer.getSize();
		String pattern = "Build";
		String color = "RED";
		IRCColorizer.setter(nickname, pattern, color);
		int curSize = IRCColorizer.getSize();
		assertEquals(oldSize + 1, curSize);
	}
	
	@Test
	//Check wether setter function could add new pattern->color to a specific user
	public void setterTest2()
	{
		String nickname = "neverused";
		IRCColorizer.cleanUserPattern(nickname);
		
		String pattern1 = "Build";
		String color1 = "RED";
		String pattern2 = "[0-9]{3}";
		String color2 = "BLUE";
		
		IRCColorizer.setter(nickname, pattern1, color1);
		int oldPatternSize = IRCColorizer.getSizeByNickName(nickname);

		IRCColorizer.setter(nickname, pattern2, color2);
		int curPatternSize = IRCColorizer.getSizeByNickName(nickname);
		assertEquals(oldPatternSize + 1, curPatternSize);
	}
	
	@Test
	//Check wether setter function could add an existing pattern->color to a specific user
	public void setterTest3()
	{
		String nickname = "neverused";
		IRCColorizer.cleanUserPattern(nickname);
		
		String pattern1 = "Build";
		String color1 = "RED";
		String pattern2 = "Build";
		String color2 = "BLUE";
		
		IRCColorizer.setter(nickname, pattern1, color1);
		int oldPatternSize = IRCColorizer.getSizeByNickName(nickname);

		IRCColorizer.setter(nickname, pattern2, color2);
		int curPatternSize = IRCColorizer.getSizeByNickName(nickname);
		assertEquals(oldPatternSize, curPatternSize);
	}
	
	
	
	@Test
	public void basicColorizeTest() {
		String nickName = "foo";
		String pattern = "Build";
		String color = "RED";
		
		String message = "Build job123 is STILL FAILING: https://server.com/build/42";
		
		IRCColorizer.setter(nickName, pattern, color);
		
		String colorizedMessage = IRCColorizer.colorize(nickName, message);
		System.out.println(colorizedMessage);
		assertEquals(Colors.RED + "Build" + Colors.NORMAL + " job123 is STILL FAILING: https://server.com/build/42", colorizedMessage);
	}
	
	@Test
	public void duplicateColorizeTest() {
		String nickName = "foo";
		String pattern = "Build";
		String color = "RED";
		
		String message = "Build job123 is STILL FAILING: https://server.com/Build/42";
		
		IRCColorizer.setter(nickName, pattern, color);
		
		String colorizedMessage = IRCColorizer.colorize(nickName, message);
//		System.out.println(colorizedMessage);
		assertEquals(Colors.RED + "Build" + Colors.NORMAL + " job123 is STILL FAILING: https://server.com/"+
				Colors.RED + "Build" + Colors.NORMAL + "/42", colorizedMessage);
	}
	
	@Test
	public void duplicateNewLineColorizeTest() {
		String nickName = "foo";
		String pattern = "Build";
		String color = "RED\n";
		
		String message = "Build job123 is STILL FAILING: https://server.com/Build/42";
		
		IRCColorizer.setter(nickName, pattern, color);
		
		String colorizedMessage = IRCColorizer.colorize(nickName, message);
//		System.out.println(colorizedMessage);
		assertEquals(Colors.RED + "Build" + Colors.NORMAL + " job123 is STILL FAILING: https://server.com/"+
				Colors.RED + "Build" + Colors.NORMAL + "/42", colorizedMessage);
	}
	
	@Test
	public void regexSimpleMatch1()
	{

		String nickName = "foo2";
		String pattern = "B[a-z]*d";
		String color = "RED\n";
		String message = "Build job123 is STILL FAILING: https://server.com/Bird/Build/42";
		IRCColorizer.setter(nickName, pattern, color);
		String colorizedMessage = IRCColorizer.colorize(nickName, message);
		assertEquals(Colors.RED + "Build" + Colors.NORMAL + " job123 is STILL FAILING: https://server.com/"+
		Colors.RED + "Bird" + Colors.NORMAL + "/" + Colors.RED + "Build" + Colors.NORMAL + "/42", colorizedMessage);

	}
	
	@Test
	public void regexMultipleColorsTest()
	{

		String nickName = "foo6";
		String pattern1 = "B[a-z]*d";
		String color1 = "RED\n";
		
		String pattern2 = "[0-9]{3}";
		String color2 = "BLUE\n";
		
		String message = "Build job123 is STILL FAILING: https://server.com/Bird/Build/42";
		IRCColorizer.setter(nickName, pattern1, color1);
		IRCColorizer.setter(nickName, pattern2, color2);
		String colorizedMessage = IRCColorizer.colorize(nickName, message);
		//assertEquals(Colors.RED + "Build" + Colors.NORMAL + " job123 is STILL FAILING: https://server.com/"+
		//		Colors.RED + "Bird" + Colors.NORMAL + "/" + Colors.RED + "Build" + Colors.NORMAL + "/42", colorizedMessage);

		
		assertEquals(Colors.RED + "Build" + Colors.NORMAL + " job" + Colors.BLUE + "123" + Colors.NORMAL + " is STILL FAILING: https://server.com/"+
		Colors.RED + "Bird" + Colors.NORMAL + "/" + Colors.RED + "Build" + Colors.NORMAL + "/42", colorizedMessage);

	}
	
	@Test
	public void regexMultipleUserDifferentColorTest()
	{

		String nickName1 = "A";
		String pattern1 = "B[a-z]*d";
		String color1 = "RED\n";
		
		String nickName2 = "B";
		String pattern2 = "B[a-z]*d";
		String color2 = "BLUE\n";
		
		String message = "Build job123 is STILL FAILING: https://server.com/Bird/Build/42";
		
		IRCColorizer.setter(nickName1, pattern1, color1);
		IRCColorizer.setter(nickName2, pattern2, color2);
		
		String colorizedMessage1 = IRCColorizer.colorize(nickName1, message);
		String colorizedMessage2 = IRCColorizer.colorize(nickName2, message);
		
		assertEquals(Colors.RED + "Build" + Colors.NORMAL + " job123 is STILL FAILING: https://server.com/"+
				Colors.RED + "Bird" + Colors.NORMAL + "/" + Colors.RED + "Build" + Colors.NORMAL + "/42", colorizedMessage1);
		assertEquals(Colors.BLUE + "Build" + Colors.NORMAL + " job123 is STILL FAILING: https://server.com/"+
				Colors.BLUE + "Bird" + Colors.NORMAL + "/" + Colors.BLUE + "Build" + Colors.NORMAL + "/42", colorizedMessage2);

	}
	
}
