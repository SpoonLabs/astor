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
 * --------------
 * PaintList.java
 * --------------
 * (C) Copyright 2003-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 13-Aug-2003 : Version 1 (DG);
 * 27-Jun-2005 : Fixed equals() method to handle GradientPaint (DG);
 * 21-Jun-2007 : Copied from JCommon (DG);
 *
 */

package org.jfree.chart.util;

import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A table of {@link Paint} objects.
 */
public class PaintList extends AbstractObjectList {

    /**
     * Creates a new list.
     */
    public PaintList() {
        super();
    }

    /**
     * Returns a {@link Paint} object from the list.
     *
     * @param index the index (zero-based).
     *
     * @return The object.
     */
    public Paint getPaint(int index) {
        return (Paint) get(index);
    }

    /**
     * Sets the {@link Paint} for an item in the list.  The list is expanded
     * if necessary.
     *
     * @param index  the index (zero-based).
     * @param paint  the {@link Paint}.
     */
    public void setPaint(int index, Paint paint) {
        set(index, paint);
    }

    /**
     * Tests the list for equality with another object (typically also a list).
     *
     * @param obj  the other object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof PaintList) {
            PaintList that = (PaintList) obj;
            int listSize = size();
            for (int i = 0; i < listSize; i++) {
               if (!PaintUtilities.equal(getPaint(i), that.getPaint(i))) {
                   return false;
               }
            }
        }
        return true;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return The hashcode.
     */
    public int hashCode() {
        int result = 127;
        int size = size();
        result = HashUtilities.hashCode(result, size());
        // for efficiency, we just use the first, last and middle items to
        // compute a hashCode...
        if (size > 0) {
            result = HashUtilities.hashCode(result, getPaint(0));
            if (size > 1) {
                result = HashUtilities.hashCode(result, getPaint(size - 1));
                if (size > 2) {
                    result = HashUtilities.hashCode(result, getPaint(size / 2));
                }
            }
        }
        return result;
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {

        stream.defaultWriteObject();
        int count = size();
        stream.writeInt(count);
        for (int i = 0; i < count; i++) {
            Paint paint = getPaint(i);
            if (paint != null) {
                stream.writeInt(i);
                SerialUtilities.writePaint(paint, stream);
            }
            else {
                stream.writeInt(-1);
            }
        }

    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException,
            ClassNotFoundException {

        stream.defaultReadObject();
        int count = stream.readInt();
        for (int i = 0; i < count; i++) {
            final int index = stream.readInt();
            if (index != -1) {
                setPaint(index, SerialUtilities.readPaint(stream));
            }
        }

    }

}
