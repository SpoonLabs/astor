***********************************
*  JFREECHART: Version 1.2.0-ea1  *
***********************************

30 June 2009

(C)opyright 2000-2009, by Object Refinery Limited and Contributors.

-----------------
1.  INTRODUCTION
-----------------

*** PLEASE NOTE : THIS IS A PREVIEW RELEASE OF JFREECHART 1.2.0.  THE 
*** LATEST STABLE VERSION OF JFREECHART IS 1.0.13.  YOU SHOULD USE THE
*** STABLE VERSION FOR PRODUCTION SYSTEMS.  THE PREVIEW RELEASES WILL BE 
*** SUBJECT TO API CHANGES UNTIL THE OFFICIAL 1.2.0 RELEASE IS MADE LATER IN 
*** 2009.

JFreeChart is a free chart library for the Java(tm) platform.  It runs 
on the Java 2 Platform (JDK 1.4.2 or later) and uses the Java 2D API for 
drawing.

JFreeChart is licensed under the terms of the GNU Lesser General
Public Licence (LGPL).  A copy of the licence is included in the
distribution.

Please note that JFreeChart is distributed WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  Please refer to the licence for details.

-------------------
2.  LATEST VERSION
-------------------
The latest version of this class library can be obtained from:

    http://www.jfree.org/jfreechart/

If you have an comments, suggestions or bugs to report, please post a
message in the JFreeChart forum.

-----------------
3.  DOCUMENTATION
-----------------
THERE IS NOT YET ANY OFFICIAL DOCUMENTATION FOR THE PREVIEW RELEASE.  Please
use the JFreeChart 1.0.x versions if you require documentation.

-----------------
4.  DEPENDENCIES
-----------------
JFreeChart has the following dependencies:

(a)  JDK 1.4.2 or higher - if you are using JFreeChart to create applets, 
this means that you cannot rely on the JVM integrated with Microsoft's 
Internet Explorer - your users will need to have the Java 2 plug-in 
installed.  Most other browsers (e.g. Firefox, Mozilla, Netscape, Konqueror) 
support JRE 1.4, 1.5 or 1.6.

(b)  servlet.jar - classes in the org.jfree.chart.servlet
package require this file.  The JFreeChart distribution includes the 
servlet.jar file distributed with Tomcat 4.1.31.  Applicable license 
terms are published at:  

    http://java.sun.com/products/servlet/LICENSE   

(c)  JUnit - a unit testing framework (the junit.jar runtime file is
included in the distribution).  JUnit is licensed under the terms
of the IBM Common Public License.  You can find out more about JUnit
and/or download the latest version from:

    http://www.junit.org

The JUnit tests included with JFreeChart have been created using JUnit
4.3.1.

-----------
5.  SUPPORT
-----------
Support questions can be posted in the free support forum at

    http://www.jfree.org/phpBB2/viewforum.php?f=3
    
We read all questions posted in the forum, and respond to as many as we can
in the time available.  Unfortunately, there are too many questions to answer
them all.


--------------------
6.  ANT BUILD SCRIPT
--------------------
An Ant build script (build.xml) is included in the distribution.  This
is the same script that is used to create the JFreeChart distribution.

For more information about Ant:

    http://ant.apache.org/

Please note that you will need to obtain the servlet.jar file (see the
DEPENDENCIES section above) before running the Ant script.

------------------------
7.  THE DEMO APPLICATION
------------------------
A demo application that shows a selection of the charts that can be
generated is included in the JFreeChart distribution.   To run the
demo (using JDK 1.4.2 or later), use the following command:

    java -jar jfreechart-1.2.0-ea1-demo.jar

The complete source code for the demo application is available for
download when you purchase the JFreeChart Developer Guide.

The demo application uses iText, a library for creating PDF documents.
iText is licensed under the terms of the GNU Lesser General Public Licence
and is available to download from:

    http://www.lowagie.com/iText/

---------------
8.  LIMITATIONS
---------------
JFreeChart has some known limitations that will hopefully be addressed in 
the future:

    - with the XYPlot class, when the bounds of the range axis are calculated
      automatically, all of the data is used, not just the subset that is 
      visible on the chart;
    - some renderers do not respect the series visibility flags yet;
    - the chart property editors (accessible by right-clicking on the chart
      panel) are horribly out of date and probably shouldn't be used;
    - item labels (if displayed) are not taken into account for the 
      automatically calculated axis range.  As a workaround, you can increase
      the axis margins;
    - tick labels on a DateAxis that uses a SegmentedTimeline can be 
      problematic;
    
If there are other items that you think should be listed here,
please post a bug report.

----------------
10.  CONTRIBUTORS
----------------
JFreeChart wouldn't be half the library that it is today without the
contributions (large and small) that have been made by the developers listed 
below:

    - Eric Alexander
    - Richard Atkinson
    - David Basten
    - David Berry
    - Chris Boek
    - Zoheb Borbora
    - Anthony Boulestreau
    - Jeremy Bowman
    - Nicolas Brodu
    - Jody Brownell
    - David Browning
    - Soren Caspersen
    - Chuanhao Chiu
    - Brian Cole
    - Pascal Collet
    - Martin Cordova
    - Paolo Cova
    - Mike Duffy
    - Don Elliott
    - Rune Fauske
    - Jonathan Gabbai
    - Serge V. Grachov
    - Daniel Gredler
    - Joao Guilherme Del Valle
    - Hans-Jurgen Greiner
    - Aiman Han
    - Cameron Hayne
    - Jon Iles
    - Wolfgang Irler
    - Sergei Ivanov
    - Adriaan Joubert
    - Darren Jung
    - Xun Kang
    - Bill Kelemen
    - Norbert Kiesel
    - Gideon Krause
    - Pierre-Marie Le Biot
    - Arnaud Lelievre
    - Wolfgang Lenhard
    - David Li
    - Yan Liu
    - Tin Luu
    - Craig MacFarlane
    - Achilleus Mantzios
    - Thomas Meier
    - Jim Moore
    - Jonathan Nash
    - Barak Naveh
    - David M. O'Donnell
    - Krzysztof Paz
    - Eric Penfold
    - Tomer Peretz
    - Xavier Poinsard
    - Andrzej Porebski
    - Viktor Rajewski
    - Eduardo Ramalho
    - Michael Rauch
    - Cameron Riley
    - Klaus Rheinwald
    - Dan Rivett
    - Scott Sams
    - Michel Santos
    - Thierry Saura
    - Andreas Schneider
    - Jean-Luc SCHWAB
    - Bryan Scott
    - Tobias Selb
    - Mofeed Shahin
    - Pady Srinivasan
    - Greg Steckman
    - Roger Studner
    - Irv Thomae
    - Eric Thomas
    - Jess Thrysoee
    - Rich Unger
    - Daniel van Enckevort
    - Laurence Vanhelsuwe
    - Sylvain Vieujot
    - Jelai Wang
    - Mark Watson
    - Alex Weber
    - Richard West
    - Matthew Wright
    - Benoit Xhenseval
    - Christian W. Zuckschwerdt
    - Hari 
    - Sam (oldman)

It is possible that I have missed someone on this list, if that
applies to you, please e-mail me. 

Dave Gilbert (david.gilbert@object-refinery.com)
JFreeChart Project Leader
