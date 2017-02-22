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
 * -------------------------------
 * GradientPaintTransformType.java
 * -------------------------------
 * (C) Copyright 2003-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes:
 * --------
 * 21-Oct-2003 : Version 1 (DG);
 * 20-Jun-2007 : Copied from JCommon (DG);
 *
 */

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Represents a type of transform for a <code>GradientPaint</code>.
 */
public final class GradientPaintTransformType implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 8331561784933982450L;

    /** Vertical. */
    public static final GradientPaintTransformType VERTICAL
        = new GradientPaintTransformType("GradientPaintTransformType.VERTICAL");

    /** Horizontal. */
    public static final GradientPaintTransformType HORIZONTAL
        = new GradientPaintTransformType(
                "GradientPaintTransformType.HORIZONTAL");

    /** Center/vertical. */
    public static final GradientPaintTransformType CENTER_VERTICAL
        = new GradientPaintTransformType(
                "GradientPaintTransformType.CENTER_VERTICAL");

    /** Center/horizontal. */
    public static final GradientPaintTransformType CENTER_HORIZONTAL
        = new GradientPaintTransformType(
                "GradientPaintTransformType.CENTER_HORIZONTAL");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private GradientPaintTransformType(String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the object.
     *
     * @return The string.
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
     * @return A boolean.
     */
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GradientPaintTransformType)) {
            return false;
        }

        GradientPaintTransformType t = (GradientPaintTransformType) obj;
        if (!this.name.equals(t.name)) {
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
        GradientPaintTransformType result = null;
        if (this.equals(GradientPaintTransformType.HORIZONTAL)) {
            result = GradientPaintTransformType.HORIZONTAL;
        }
        else if (this.equals(GradientPaintTransformType.VERTICAL)) {
            result = GradientPaintTransformType.VERTICAL;
        }
        else if (this.equals(GradientPaintTransformType.CENTER_HORIZONTAL)) {
            result = GradientPaintTransformType.CENTER_HORIZONTAL;
        }
        else if (this.equals(GradientPaintTransformType.CENTER_VERTICAL)) {
            result = GradientPaintTransformType.CENTER_VERTICAL;
        }
        return result;
    }

}

