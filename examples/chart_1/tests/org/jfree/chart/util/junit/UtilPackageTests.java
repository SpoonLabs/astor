/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2009, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ---------------------
 * UtilPackageTests.java
 * ---------------------
 * (C) Copyright 2006-2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes:
 * --------
 * 23-Nov-2006 : Version 1 (DG);
 * 04-Dec-2007 : Added BooleanListTests, HashUtilitiesTests, PaintListTests and
 *               StrokeListTests (DG);
 * 08-Apr-2008 : Added LogFormatTests (DG);
 * 02-Jun-2008 : Added ShapeUtilitiesTests and SerialUtilitiesTests (DG);
 * 17-Jun-2008 : Added ShapeListTests (DG);
 * 29-Jun-2009 : Moved PaintMap and StrokeMap to org.jfree.chart.util.* (DG);
 *
 */

package org.jfree.chart.util.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the org.jfree.chart.util package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 */
public class UtilPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return The test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.util");
        suite.addTestSuite(BooleanListTests.class);
        suite.addTestSuite(HashUtilitiesTests.class);
        suite.addTestSuite(LineUtilitiesTests.class);
        suite.addTestSuite(LogFormatTests.class);
        suite.addTestSuite(PaintListTests.class);
        suite.addTestSuite(PaintMapTests.class);
        suite.addTestSuite(RelativeDateFormatTests.class);
        suite.addTestSuite(SerialUtilitiesTests.class);
        suite.addTestSuite(ShapeListTests.class);
        suite.addTestSuite(ShapeUtilitiesTests.class);
        suite.addTestSuite(StrokeListTests.class);
        suite.addTestSuite(StrokeMapTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the suite name.
     */
    public UtilPackageTests(String name) {
        super(name);
    }

    /**
     * Runs the test suite using JUnit's text-based runner.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
