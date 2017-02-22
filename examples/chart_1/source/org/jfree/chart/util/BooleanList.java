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
 * ----------------
 * BooleanList.java
 * ----------------
 * (C) Copyright 2003-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 11-Jun-2003 : Version 1 (DG);
 * 23-Jul-2003 : Renamed BooleanTable --> BooleanList and now extends
 *               ObjectList (DG);
 * 13-Aug-2003 : Now extends new class AbstractObjectList (DG);
 * 21-Oct-2004 : Removed duplicate implementation of Cloneable and Serializable,
 *               AbstractObjectList already implements that.
 * 21-Jun-2007 : Removed JCommon dependencies (DG);
 *
 */

package org.jfree.chart.util;

/**
 * A list of <code>Boolean</code> objects.
 */
public class BooleanList extends AbstractObjectList {

    /** For serialization. */
    private static final long serialVersionUID = -8543170333219422042L;

    /**
     * Creates a new list.
     */
    public BooleanList() {
    }

    /**
     * Returns a {@link Boolean} from the list.
     *
     * @param index the index (zero-based).
     *
     * @return a {@link Boolean} from the list.
     */
    public Boolean getBoolean(int index) {
        return (Boolean) get(index);
    }

    /**
     * Sets the value for an item in the list.  The list is expanded if
     * necessary.
     *
     * @param index  the index (zero-based).
     * @param b  the boolean.
     */
    public void setBoolean(int index, Boolean b) {
        set(index, b);
    }

    /**
     * Tests the list for equality with another object (typically also a list).
     *
     * @param obj  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj instanceof BooleanList) {
            return super.equals(obj);
        }
        return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return The hashcode.
     */
    public int hashCode() {
        return super.hashCode();
    }
}
