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
 * ----------------------
 * VerticalAlignment.java
 * ----------------------
 * (C) Copyright 2004-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes:
 * --------
 * 08-Jan-2004 : Version 1 (DG);
 * 20-Jun-2007 : Copied from JCommon (DG);
 *
 */

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * An enumeration of the vertical alignment types (<code>TOP</code>,
 * <code>BOTTOM</code> and <code>CENTER</code>).
 */
public final class VerticalAlignment implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 7272397034325429853L;

    /** Top alignment. */
    public static final VerticalAlignment TOP
            = new VerticalAlignment("VerticalAlignment.TOP");

    /** Bottom alignment. */
    public static final VerticalAlignment BOTTOM
            = new VerticalAlignment("VerticalAlignment.BOTTOM");

    /** Center alignment. */
    public static final VerticalAlignment CENTER
            = new VerticalAlignment("VerticalAlignment.CENTER");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private VerticalAlignment(String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the object.
     *
     * @return the string.
     */
    public String toString() {
        return this.name;
    }

    /**
     * Returns <code>true</code> if this object is equal to the specified
     * object, and <code>false</code> otherwise.
     *
     * @param obj  the other object.
     *
     * @return a boolean.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VerticalAlignment)) {
            return false;
        }

        VerticalAlignment alignment = (VerticalAlignment) obj;
        if (!this.name.equals(alignment.name)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return the hashcode
     */
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Ensures that serialization returns the unique instances.
     *
     * @return The object.
     *
     * @throws ObjectStreamException if there is a problem.
     */
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(VerticalAlignment.TOP)) {
            return VerticalAlignment.TOP;
        }
        else if (this.equals(VerticalAlignment.BOTTOM)) {
            return VerticalAlignment.BOTTOM;
        }
        else if (this.equals(VerticalAlignment.CENTER)) {
            return VerticalAlignment.CENTER;
        }
        else {
            return null;  // this should never happen
        }
    }

}
