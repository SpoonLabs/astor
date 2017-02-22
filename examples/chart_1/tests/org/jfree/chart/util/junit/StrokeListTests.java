/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
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
 * --------------------
 * StrokeListTests.java
 * --------------------
 * (C) Copyright 2007, 2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 04-Dec-2007 : Version 1, based on PaintListTests (DG);
 *
 */

package org.jfree.chart.util.junit;

import java.awt.BasicStroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.util.StrokeList;

/**
 * Some tests for the {@link StrokeList} class.
 */
public class StrokeListTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(StrokeListTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public StrokeListTests(String name) {
        super(name);
    }

    /**
     * Tests the equals() method.
     */
    public void testEquals() {
        StrokeList l1 = new StrokeList();
        StrokeList l2 = new StrokeList();
        assertEquals(l1, l2);

        l1.setStroke(0, new BasicStroke(1.0f));
        assertFalse(l1.equals(l2));
        l2.setStroke(0, new BasicStroke(1.0f));
        assertTrue(l1.equals(l2));

        l1.setStroke(1, new BasicStroke(1.5f));
        assertFalse(l1.equals(l2));
        l2.setStroke(1, new BasicStroke(1.5f));
        assertTrue(l1.equals(l2));

        l1.setStroke(1, null);
        assertFalse(l1.equals(l2));
        l2.setStroke(1, null);
        assertTrue(l1.equals(l2));
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {

        StrokeList l1 = new StrokeList();
        l1.setStroke(0, new BasicStroke(1.0f));
        l1.setStroke(1, new BasicStroke(1.5f));
        l1.setStroke(2, null);

        StrokeList l2 = null;
        try {
            l2 = (StrokeList) l1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(l1 != l2);
        assertTrue(l1.getClass() == l2.getClass());
        assertTrue(l1.equals(l2));

        l2.setStroke(0, new BasicStroke(0.5f));
        assertFalse(l1.equals(l2));

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        StrokeList l1 = new StrokeList();
        l1.setStroke(0, new BasicStroke(1.0f));
        l1.setStroke(1, new BasicStroke(1.5f));
        l1.setStroke(2, null);

        StrokeList l2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(l1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            l2 = (StrokeList) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(l1, l2);

    }

    /**
     * Some checks for the testHashCode() method.
     */
    public void testHashCode() {
        StrokeList p1 = new StrokeList();
        StrokeList p2 = new StrokeList();
        assertTrue(p1.hashCode() == p2.hashCode());

        p1.setStroke(0, new BasicStroke(0.5f));
        assertFalse(p1.hashCode() == p2.hashCode());
        p2.setStroke(0, new BasicStroke(0.5f));
        assertTrue(p1.hashCode() == p2.hashCode());

        p1.setStroke(1,null);
        assertFalse(p1.hashCode() == p2.hashCode());
        p2.setStroke(1,null);
        assertTrue(p1.hashCode() == p2.hashCode());
    }

}
