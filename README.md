# UIUC BookReaders Group Project for Jenkins IRC Plugin
## Team Members
Jung-chen (Anna), Ryan, Hongjae, Tao, Austin, Scott, Zehao, Jinian, Yuhang
## Project Proposal
We intend to work with the IRC plugin for Jenkins (here: https://wiki.jenkins-ci.org/display/JENKINS/IRC+Plugin) to allow for communication from within Jenkins. We will attempt to build more features into the IRC chat such as syntax highlighting, history, and various other important (programmer-oriented) features.
## Weekly Meetings
8-9 pm Thursday. Meet by Einstein Bros on the main floor of Siebel.

### Iteration 1 (Oct 13 - Oct 20)
|actual|estimated|spike|description|
|------|---------|-----|-----------|
|3 units|3 units||User Stories|
|2 units|3 units||Get familiar with code base & install plugins|
|3 units|4 units||Modify code base|

### Iteration 2 (Oct 21 - Nov 4)
|Name|Description|Story Points|Actual Time Taken (units)|Pair (Name 1, Name 2)|
|----|-----------|------------|-------------------------|---------------------|
|Query Build History|As a user I want to be able to see the past builds for a Jenkins project by issuing a command to the Jenkins bot. Define the structure of a simple query format. Does not need to be intricate. Modify Bot Class in Instant Messaging Plugin to create new command for requesting status for past builds. Create function to filter the returned status listing and push to the IRC channel.|4|10|Austin, Ryan|
|Show Build Overview|As a user I want to be able to issue a command to the Jenkins bot and receive an overview of information about projects. (build number, current status, url, last commit users and messages, etc.) Add new command class that extends AbstractMultipleJobCommand and include @Extension annotation Create function to grab current build info (number, status), health status, commit users and messages and return it to the bot to send.|4|11|Scott, Jung-chen (Anna)|
|Quick Access to Jenkins|As a user I want to issue a command to Jenkins to receive a URL to specific sub-pages of Jenkins (ex: Configurations, Home, Code etc.) Modify Bot Class in Instant Messaging Plugin to create new command Create function to grab URL from Jenkins Create function to read flags from IRC to Bot |2	|10	|Tao, Zehao, Jinian|
|Build User History	|As a user I want to be able to see recent builds issued by a user.We need a command passed to pass in a user name. We also need a function created to query the recent builds by the username.|3|8|Yuhang, Hongjae|
|Generic Build	|As a user I want to prevent the generic build from happening unless intended. i.e.) "!jenkins build -all" (or a similar command) to perform the same way that "!jenkins build" used to. Update BuildCommand class to reply no when "build" command is received Also update BuildCommand class to receive a "build -all" command which will build all jobs|2|5	|Zehao, Tao |

### Iteration 3 (Nov 5 - Nov 18)
|Name|Description|Story Points|Actual Time Taken (units)|Pair (Name 1, Name 2)|
|----|-----------|------------|-------------------------|---------------------|
|Customize Message Style	|As a user I want to be able to customize a message reply's colors from the Jenkins bot. Update IRC plugin UI to let user choose and save custom colors for messages Update the colorize function to format messages according to the user's configured colors|4|7	|Austin, Hongjae|Tao|
|Scope of Notifications	|As a user I want to be able to add nicks following a command to have Jenkins reply to multiple users with a private message instead of just the user that issued the command Create function to read our flag from IRC to Bot Create function that intercepts that message with flag and sends private replies to all the recipients passed to it|4|6	|Scott, Ryan|
|Repository interaction|As a user I want to be able to issue Jenkins a command and get the repository information of builds where the code is under version control. Modify Bot Class in Instant Messaging Plugin to create new command. Display a set of paths in the workspace that was affected by the changes, the repository version number and build number, the user who made this change and commit message. User can decide which version control number to be displayed. User can decide how many version control number to be displayed. Default is 5.|3|8	|Jinian, Jung-chen (Anna)|
|Workspace Overview I	|As a user I want to be able to issue a command similar to "ls" to traverse the workspaces. Modify Bot Class in Instant Messaging Plugin to create new command "open" "open <Project Name>" => The content of root directory, eg: [trunk] [tags] readme.md "open <Project Name> <Path To Directory>" => The content of sub directory, eg: "open BookReader trunk" => "[src] [target] pom.xml" "open <Project Name> <Path To File>" => The URL of long file and content of short file, eg: "open BookReader trunk/pom.xml" => content or link to pom.xml" open file or directory with incomplete name"=>URL,content of the desired file or list of files containing the same prefix, eg:"open BookReader trunk/po" => content or link to pom.xml Output files and directories are displayed in the alphabetic order.|4|7|Zehao, Yuhang|

### Iteration 4 (Nov 18 - Dec 2)
|Name|Description|Story Points|Actual Time Taken (units)|Pair (Name 1, Name 2)|
|----|-----------|------------|-------------------------|---------------------|
|Improve ShowIf Command|Improve running time and readability: especially related to the parsing of the commands and query language add governors to block flooding|3|3|Austin, Jinian|
|Workspace Overview II|The user wish to issue a command similar to "find" to grab the URL of the desired file. "open <Project Name> <File Name>" => Find the position of file and provide the url |3|3	|Zehao,Scott|
|Improve User Command	|Improve user command by adding new features add default number to show certain number of builds. able to change default number to certain number.|3|3	|Jung-chen (Anna), Tao, Yuhang|
|Improve Colors Directive|Improve the colors directives in IRC to be settable within a private chat with jenkins. also add preconfigured default colors (themes) and directives to support this. The "CLEAN" example is a good existing example.|3|3	|Hongjae, Ryan|

### Refactoring, Documentation, and Javadoc Tasks
|Refactoring and Javadoc Tasks|Pair (Name 1, Name 2)|
|---------------------------------------------|---------------------|
|Generic Build, Customize Message Style, Improve Colors Directive|Hongjae, Yuhang|
|GetUserHistory.java,Scope of Notifications, OverviewCommand.java, UserCommand.java|Scott, Jung-chen (Anna)|
|RepoCommand.java, Query Build History, URLCommand.java, OpenCommand.java| Jinian, Zehao|

|Documentation|Pair (Name 1, Name 2)|
|-------------|---------------------|
|PDF Document|	Ryan, Austin, Tao|

## Meeting Notes
Minutes of Meetings
Minutes of Meeting held on Oct 14, 2015
Venue: SC 1112
Members Present (with roles): All
Agenda:
Go over wiki, sign contract, decide on project
Meeting notes:
If you haven't signed the contract. SIGN THE CONTRACT. It is due the 15th in class to Darko.
We have chosen project #11.
Wiki is okay needs updating from template.
Deliverables by next meeting:
Read & go over requirements for iteration #1 so that we can plan accordingly for the meeting on the 15th.
Minutes of Meeting held on Oct 14, 2015
Venue: SC Foyer
Members Present (with roles): All
Agenda: We discussed basic role responsibilities for tackling the first iteration.
We chose to use the IRC Plugin to build off of. One group will be investigating the code base. Once group will come up with a framework for users stories.
We will meet again on Sunday at 1PM.
Go over requirements and distribution of roles for Iteration 1 and how we will move going forward.
Deliverables by next meeting:
We should have the basics of user stories and and understanding of the codebase. Formal user stories will be developed on Sunday as a group.
Minutes of Meeting held on Oct 18, 2015
Venue: SC 1112 -> moved to third floor conference room
Members Present (with roles): All
Agenda:
We are finalizing user stories and we discussed the codebase in detail.
We discussed the IRC Plugin implementation and its dependency on the instant messaging plugin Bot class.


Start & finish spike test.
Meeting notes:

Deliverables by next meeting:

Minutes of Meeting held on Oct 19, 2015
Venue: SC 1314
Members Present (with roles): All
Agenda:
Go over & finalize user stories.
Go over codebase.
Modified spike test to provide demo in first meeting.
Meeting notes:
We need to provide Agenda ready for our first meeting with TA.
We need divide our team in small group of 2-3 people.
Deliverables by next meeting:
Presentation
Working demo
User stories

Minutes of Meeting held on Oct 21, 2015
Venue: SC 3124
Members Present (with roles): All
Agenda:
Iteration 1 Meeting with TA

Meeting notes:
Moderator: Tao
Scribe: Austin
Tao shared entire screen and Scott called in.
Tao moderated.
 
               Tao - 
we chose the chatroom topic
we chose Topic 11 because it is underused and allows the control of jenkins
We have met 5 times so far.
Tao reviewed our work to date at an introductory level.
Anna -
reviewed agenda and the basics of how the plugin systems works with jenkins and the irc chat.
reviewed why we chose the irc plugin.
discussed why we did not chose slack plugin
        Zehao - 
powerful plugin bot of instant messaging plugin is being used
overviewed bot class structure
       Scott discussed user stories
       Yuhang -discussed our specific user stories
       Jinan -plans moving forward
       Austin -reviewed jenkins install of irc plugin
       Ryan - demonstrated how IRC works with the jenkins bot
       Tao - 
Demonstrated how our spike solution works.
Deliverables by next meeting:
               Semih(TA) feedback-
 Each pair should be aware of the progress/solution of other pairs, in iteration 2 meeting, Semih may assign a pair randomly to review other pair's code.
 Pairs should identify the approach of programming, e.g. pair programming or other.
 Reminder our group to tag a working version properly for iteration 2 meeting.
 
Minutes of Meeting held on Oct 22, 2015
Venue: Google Hangouts
Members Present (with roles): All (except Anna went to interview)
Agenda:
Setup a better meeting time for this week for Sunday at 1PM.
Discuss strategy for Iteration 2.
Meeting notes:
We will meet on Sunday as a group and do pair programming for a few hours.
Deliverables by next meeting:
Everybody needs to read their selected User Story and think about implementations and read the Iteration 2 wiki.
Minutes of Meeting held on Oct 25, 2015
Venue:  SC 3102
Members Present(with roles): All
Agenda:
talk about strategy about iteration2
work on code implementation of the user stories in the iteration2 in pairs
Meeting notes:
Deliverables by next meeting:

Minutes of Meeting held on Oct 28, 2015
Venue:  SC 4124
Members Present(with roles): Ryan & Austin
Agenda:
Finish query language
Meeting notes:
Query language was completed along with dispatching to private functions. Tests not implemented yet
Deliverables by next meeting:
write private functions & think about tests
Minutes of Meeting held on Oct 28, 2015
Venue:  SC 1314
Members Present(with roles): Austin, Anna, Hongjae, Ryan & Scott
Agenda:
Progress meeting with TA
Meeting notes:
Added tag in our svn
Each pair discussed about their user stories
visible changes in jenkins
code review to the TA and team members
Need to have automated test for our development
Deliverables by next meeting:
Iteration 2 Meeting with TA
 
Minutes of Meeting held on Oct 28, 2015
Venue:  SC 1314
Members Present(with roles): Hongjae & Yuhang
Agenda:
Finish username filter
Meeting notes:
GetUserHistory receives Collection of builds data and returns Collection of builds data with applied filter user.
Modified tests
Deliverables by next meeting:
write about tests
Minutes of Meeting held on Oct 30, 2015
Venue:  SC 4407
Members Present(with roles): Hongjae & Yuhang
Agenda:
work on getuserhistory test
Meeting notes:
Modifiy tests
Deliverables by next meeting:
write about tests and set up jenkins IRC plugin on Yuhang's VM
Minutes of Meeting held on Nov 1, 2015
Venue:  SC 3124
Members Present(with roles): All
Agenda:
Discussed problems met so far
Each pair report current progress/status
Each pair continue work on implementing user stories
Meeting notes:
Assign roles for Iteration2:
Moderator: Yuhang Wang
Scribe: Jinian Xiao
Deliverables by next meeting:
Create iteration2 page 
Prepare for presentation, prepare slides for iteration 2
Arrange meeting with TA on Monday 11/02 if there is still issues

  Minutes of Meeting held on Nov,1st  2015
Venue:  SC 3124
Members Present(with roles): Hongjae & Yuhang
Agenda:
Complete getuserhistory test 
Meeting notes:
Assigning work on PPT presentation 
Deliverables by next meeting:
 PPT of the implementation of the work
Minutes of Meeting held on Nov 2, 2015
Venue:  Google Hangout
Members Present(with roles): Scott & Jung-chen (Anna)
Agenda:
Finish test cases
Meeting notes:
Test refactoring and adding more test cases for code coverage. Additionally, we created slide for iteration 2 meeting.
 
Minutes of Meeting held on Nov 2, 2015
 
Venue:  Google Hangout
Members Present(with roles): Austin & Ryan
Agenda:
Finish test cases
Meeting notes:
worked on finishing our testing strategy. made sure all tests passed.
 
Minutes of Meeting held on Nov 4, 2015
Venue:  SC 3124
Members Present(with roles): All (Except Tao has exam at the same time)
Agenda:
Iteration 2 Meeting with TA
Meeting notes:
Scribe: Jinian
Zehao
explain the user story—Query Build History.
Austin and Ryan
Austin gives the demo of Query Build History.
with”!jenkins ShowIf user rtfreed2 | jobs<5”
“!jenkins ShowIf user rtfreed2 | build 5”
And shows the information of the builds.
Show that they have enough tests for their job. 
Show their modification in codes.
Hongjae
Show and explain the change in the codes.
Because their user stories have some similar attributes with Austin’s.
Austin
explain the user story——Show Build Overview
Anna
show the demo of their job.
by”!jenkins overview”
And the bot automatically show the last build of every project in jenkins.
show the test file.
show the code modification in the OverviewCommand.java
Austin
Explain the code base.
Ryan
explain the user story——Build User History
Yuhang
show the demo of—Build User History.
using”!jenkins userHistory ywang515”
bot returns every build done by ywang515
Hongjae
show and explain the test cases.
show the changes in code base.
Zehao
explain the code base.
Scott
explain the user story—Quick Access to Jenkins.
Zehao
show the demo.
By”!jenkins geturl help”
show all the commands of geturl.
and try several of them.
and”!jenkins build -all”
show and explain the test file.
show the changes in code base.
Semih(TA) feedback-
Point out some question about the story points.
The work time in the iteration 3 should be balanced.
Minutes of Meeting held on Nov 6, 2015
Venue: SC sofa area at 1F
Members Present (with roles): Jinian & Jung-chen (Anna)
Meeting time length: 1.5 hours
Agenda: Discuss and decide the layout of the output
Meeting notes:
To display the list with the version control number, we figure out some possible ways to write the function
Next meeting: Nov 8, 2015
Minutes of Meeting held on Nov 8, 2015
Venue: SC 3124
Members Present (with roles): Everyone
Agenda: Go over preliminary tasks of Iteration 3 requirements
Meeting notes:
Reminder to go over and read Iteration 3 page
General teamwork discussion
Get time for in-progress meeting
Go through user stories
Minutes of Meeting held on Nov 8, 2015
Venue: SC 3124
Members Present (with roles):  Zehao&Yuhang
Meeting time length: 7 hours
Agenda: Discuss the layout of the output and implement the code for the user story.
Meeting notes:
Modifying the code to complete the function of opencommand and write the test for opencommand
Refactoring the geturl code
Minutes of Meeting held on Nov 8, 2015
Venue: SC 3124
Members Present (with roles):  Jinian & Jung-chen(Anna)
Meeting time length: 3 hours
Agenda: Design the implement the code for the user story.
Meeting notes:
Read database.
Read ChangeLogSet part of http://javadoc.jenkins-ci.org
Minutes of Meeting held on Nov 9, 2015
Venue: SC 4102
Members Present (with roles):  Austin, Hongjae & Tao
Meeting time length: 2 hours
Agenda: Discuss how we are going to implement the code for the user story.
Meeting notes:
Discussed about how we are going to implement the code for the user story and discussed about classes we 
we will modify for our user stories.
Next meeting:
Nov 13, 2015

Minutes of Meeting held on Nov 10, 2015
Venue: Skype
Members Present:  Ryan & Scott
Meeting time length: 2.5 hours
Agenda: Work on user story.
Meeting notes:
Implementation strategy discussion. Wrote a bunch of code. Discussed listener operations within IRC.
Next meeting:
Nov 11, 2015
Minutes of Meeting held on Nov 11, 2015
Venue: Skype
Members Present:  Ryan & Scott
Meeting time length: 2.5 hours
Agenda: Work on implementation and tests.
Meeting notes:
Tested our previous work, debugged why it wasn't working in irc, wrote tests, made changes to pass tests and then refactored method, feature is now working!
Next meeting:
          Nov 15, 2015
Minutes of Meeting held on Nov 8, 2015
Venue: SC 1404
Members Present (with roles):  Zehao&Yuhang
Meeting time length: 2.5 hours
Agenda: Implement test for the code.
Meeting notes:
Refactoring the code of OpenCommand.java
Minutes of Meeting held on Nov 12, 2015
Venue: SC Basement
Members Present (with roles):  Jinian & Jung-chen(Anna)
Meeting time length: 5 hours
Agenda: fix the problems in the former code.
Meeting notes:
Modify the codebase and try to get correct Repository URL.
Minutes of Meeting held on Nov 12, 2015
Venue: SC 4124
Members Present (with roles):  Austin, Hongjae & Tao
Meeting time length: 3 hours
Agenda: Updated changes in the code
Meeting notes:
Updated the codebase to according user story and try to test in IRC chatting plugin.
Minutes of Meeting held on Nov 13, 2015
Venue: SC 4102
Members Present (with roles):  Austin, Hongjae & Tao
Meeting time length: 5 hours
Agenda: Updated changes in the code, try to find appropriate way of using regular expression, HashMap, and the color code encode/decode
Meeting notes:
We might need to re-check the code in irc to clarify how the original logic of colorizing texts
Minutes of Meeting held on Nov 15, 2015
Venue: SC 4102
Members Present (with roles):  Austin, Hongjae & Tao
Meeting time length: 5 hours
Agenda: finish tests.  meet in full group meeting.
Meeting notes:
We are done! – now we need to prepare for the meeting.

Minutes of Meeting held on Nov 15, 2015
Venue: SC 4102
Members Present (with roles): Yuhang& Zehao
Meeting time length: 4 hours
Agenda: Refactor the OpenCommand to be more extendable and add new feature "obscure search" to OpenCommand, followed by refactoring the code using extract method.
Meeting notes:
We should prepare the representation in iteration-3 meeting.
Minutes of Meeting held on Nov 15, 2015
Venue: SC 4102
Members Present (with roles): Jinian& Jung-chen(Anna)
Meeting time length: 6 hours
Agenda: Add test cases and subcommand ----"show, reponumber, all".
Meeting notes:
We need more test cases.
Minutes of Meeting held on Nov 15, 2015
Venue: SC 4102 & Google Hangout
Members Present (with roles): Everyone
Meeting time length: 1 hour
Agenda: Introduce each function and decide MARS roles for iteration 3
Meeting notes: 
Zehao will be the moderator and Jung-chen will be scribe
Reminder to go over and read Iteration 3 page
Minutes of Meeting held on Nov, 18  2015
Venue: SC 3124
Members Present (with roles): All (Except Ryan is sick and Yuhang has class)
Meeting time length: 1 hour
Agenda: Iteration 3 Meeting with TA Semih
Meeting notes:
Moderator: Zehao
Scribe: Jung-chen (Anna)
 
Zehao:
-Explain user story--Customize Message Style, saying that a user can customize a message reply's colors from the Jenkins bot.
-Review “Customize Message Style” code
-Demo the user story -- “Workspace Overview”
-Show the refactoring of OpenCommand.java, OpenCommandTest.java , and Overviewcommand.java
 
Austin:
-Demo how to color a specific word by issuing command !jenkins set color foo RED and term foo will become red in the chat room.
-Explain user story -- Scope of Notifications
-Review code of user story -- “Repository interaction”: RepoCommand.java, RepoCommandTest.java
 
Tao:
-Explain the test of “Customize Message Style”.
-Explain user story -- “Repository interaction”
  
Scott:
-Demo the user story -- Scope of Notifications
-Show the refactoring of ShowIfCommand.java. They did that by extracting methods.
-Explain the user story -- Workspace Overview
 
Hongjae:
-Review code of "Scope of Notifications"
-Demonstrated refactoring in UrlCommand.java
 
Jinian:
-Demo user story -- “Repository interaction” via command: !jenkins repo, !jenkins repo reponumber <version number>, !jenkins show <number>
-Show the refactoring of UserCommand.java, RepoCommandTest.java
-Review code of OpenCommand.java and OpenCommandTest.java
 
Semih’s suggestions:
For Customize Message Style:
-Could you make the word match?
-You should do some refactoring.
 
For Scope of Notifications:
-Do the refract in next iteration. For example, Event.getBot() was called twice, you can assign a variable for that. There are too many functions call (event.getBot().getUserChannelDao().getUser()…) and if one of the function return null, it will be a problem.
-You could remove duplicate from the set. Line 120-123.
 
For Repository interaction:
-getMessageForJob is a little bit long. They could do the refractor later on.
-It is hard to read the code. You may need to change it.
 
For Workspace Overview:
-Could you terminate the message?
 
[Note]
You will have to spend some time for documentation. Maybe you can use JAVA doc.
 
Minutes of Meeting held on Nov 29, 2015
Venue: SC 3124 & Google Hangout
Members Present (with roles): Everyone except Ryan
Meeting time length: 1 hour
Agenda: Discussed the logistic for final iteration
Meeting notes: Try to finish the user story by Tuesday Dec 2 then start work on documentation and setup the preliminary meeting time and final iteration meeting with TA.

Minutes of Meeting held on Nov 29, 2015
Venue: SC 3124
Members Present (with roles): Yuyang Wang, Jung-chen Chen,  Tao Feng
Meeting time length: 3 hour
Agenda: Work on the final part of user story and add test cases
Meeting notes: Will need to test all the new functions added are working properly 
 
Minutes of Meeting held on Dec 1, 2015
Venue: SC 3124
Members Present (with roles): Ryan Freedman and Hongjae Jeon
Meeting time length: 2 hour
Agenda: Start & finish themes user story
Meeting notes: We started... and seem to have finished the user story. However, we are running into technical issues with the jenkins bot.
Minutes of Meeting held on Dec 1, 2015
Venue: SC 3124
Members Present (with roles): Zehao and Scott
Meeting time length: 5 hour
Agenda: Start & finish workspace II user stories
Meeting notes: Everything works quite well.
Minutes of Meeting held on Dec 3, 2015
Venue: Hangout
Members Present (with roles): Scott and Jung-chen(Anna)
Meeting time length: 2 hours
Agenda: refactoring code and write Javadoc 
Meeting notes: we finish all the tasks. Need to discuss with other groups to make sure Javadoc is ok

Minutes of Meeting held on Dec 3, 2015
Venue: SC basement
Members Present (with roles): Hongjae and Yuhang
Meeting time length: 2 hours
Agenda: refactoring code and write Javadoc 
Meeting notes: we finish all the tasks in next meeting


Minutes of Meeting held on Dec 6, 2015
Venue: SC 1214
Members Present (with roles): Hongjae and Yuhang
Meeting time length: 3 hours
Agenda: refactoring code and write Javadoc 
Meeting notes: we finish all the tasks. Need to discuss with other groups to make sure Javadoc is ok
 
Minutes of Meeting held on Dec 9, 2015 (Final Meeting):
Scribe: Ryan Freedman
Moderator: Hongjae Jeon
Introduction: Austin
Hongjae goes over agenda
Added series of improvements. Detail categories of improvements:
user customization
provide mechanism to interrogate jenkins
general usability improvements
syntax/query language
details of how jenkins IRC-side interaction works
pre-existing architecture of IRC/IM plugins and the interaction with jenkins
basic communcation model with message flow to/from jenkins
UML diagram before/after
Major User Stories Demonstrations.
User history demonstrated by Hongjae:
!jenkins userHistory <ID> <num_respond>
num_respond default is 5
Tao presents custom message style & themes:
Setting user specified color/theme
!jenkins set color <theme/clear/keyword> <key?color>
clear goes back to default state
Yuhang presents generic build:
schedules a build for all
Scott presents notification command:
only dispatches to users independently
Austin demos showif command:
showif allows to filter build results
Zehao demos urlcommand:
geturl command returns url of various pages from within jenkins for easy access
Jinian demos repo command:
this shows the most recently scheduled builds, revision number, and changes in revision.
can be governed by appending number to the end of the command
can also specify repo number by appending reponumber
Anna demonstrates overview command:
displays overview of recent builds and tests
can also specify project by appending project name to the command
Zehao presents open command:
Gives an overview of workspace
lists files and directories in project
Honjae and Ryan gave an overview of thier command and test improvements.
added themes
demonstrated the code
ryan looked at something on Semih's computer and explained clear to semih
there were 5 tests
Semih want's to see all of the refactoring commits
Austin and Jinian demonstrate showif improvements
Yuhang, Tao, and Anna demonstrate refactoring of user command:
added tests listed and improvements
Zehao and Scott Demonstrate:
Open command and tests

 
