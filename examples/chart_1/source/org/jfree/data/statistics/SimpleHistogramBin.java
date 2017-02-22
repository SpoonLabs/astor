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
 * -----------------------
 * SimpleHistogramBin.java
 * -----------------------
 * (C) Copyright 2005-2009, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 10-Jan-2005 : Version 1 (DG);
 * 21-Jun-2007 : Removed JCommon dependencies (DG);
 * 19-Jun-2009 : Added selection state (DG);
 *
 */

package org.jfree.data.statistics;

import java.io.Serializable;

import org.jfree.chart.util.PublicCloneable;

/**
 * A bin for the {@link SimpleHistogramDataset}.
 */
public class SimpleHistogramBin implements Comparable,
        Cloneable, PublicCloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 3480862537505941742L;

    /** The lower bound for the bin. */
    private double lowerBound;

    /** The upper bound for the bin. */
    private double upperBound;

    /**
     * A flag that controls whether the lower bound is included in the bin
     * range.
     */
    private boolean includeLowerBound;

    /**
     * A flag that controls whether the upper bound is included in the bin
     * range.
     */
    private boolean includeUpperBound;

    /** The item count. */
    private int itemCount;

    /**
     * A flag that indicates whether or not the bin is selected.
     *
     * @since 1.2.0
     */
    private boolean selected;

    /**
     * Creates a new bin.
     *
     * @param lowerBound  the lower bound (inclusive).
     * @param upperBound  the upper bound (inclusive);
     */
    public SimpleHistogramBin(double lowerBound, double upperBound) {
        this(lowerBound, upperBound, true, true);
    }

    /**
     * Creates a new bin.
     *
     * @param lowerBound  the lower bound.
     * @param upperBound  the upper bound.
     * @param includeLowerBound  include the lower bound?
     * @param includeUpperBound  include the upper bound?
     */
    public SimpleHistogramBin(double lowerBound, double upperBound,
                              boolean includeLowerBound,
                              boolean includeUpperBound) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Invalid bounds; " + lowerBound
                    + " to " + upperBound);
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.includeLowerBound = includeLowerBound;
        this.includeUpperBound = includeUpperBound;
        this.itemCount = 0;
        this.selected = false;
    }

    /**
     * Returns the lower bound.
     *
     * @return The lower bound.
     *
     * @see #getUpperBound()
     */
    public double getLowerBound() {
        return this.lowerBound;
    }

    /**
     * Return the upper bound.
     *
     * @return The upper bound.
     *
     * @see #getLowerBound()
     */
    public double getUpperBound() {
        return this.upperBound;
    }

    /**
     * Returns the item count.
     *
     * @return The item count.
     *
     * @see #setItemCount(int)
     */
    public int getItemCount() {
        return this.itemCount;
    }

    /**
     * Sets the item count.  No event notification occurs when calling this
     * method - if the bin is contained within a
     * {@link SimpleHistogramDataset}, you should not be calling this method
     * directly.  Instead, update the bin using methods such as
     * {@link SimpleHistogramDataset#addObservations(double[])}.
     *
     * @param count  the item count.
     *
     * @see #getItemCount()
     */
    public void setItemCount(int count) {
        this.itemCount = count;
    }

    /**
     * Returns a flag indicating whether or not the bin is selected.
     *
     * @return A boolean.
     *
     * @see #setSelected(boolean)
     *
     * @since 1.2.0
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Sets the flag that indicates whether or not the bin is selected.
     *
     * @param selected  the new flag value.
     *
     * @see #isSelected()
     *
     * @since 1.2.0
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Returns <code>true</code> if the specified value belongs in the bin,
     * and <code>false</code> otherwise.
     *
     * @param value  the value.
     *
     * @return A boolean.
     */
    public boolean accepts(double value) {
        if (Double.isNaN(value)) {
            return false;
        }
        if (value < this.lowerBound) {
            return false;
        }
        if (value > this.upperBound) {
            return false;
        }
        if (value == this.lowerBound) {
            return this.includeLowerBound;
        }
        if (value == this.upperBound) {
            return this.includeUpperBound;
        }
        return true;
    }

    /**
     * Returns <code>true</code> if this bin overlaps with the specified bin,
     * and <code>false</code> otherwise.
     *
     * @param bin  the other bin (<code>null</code> not permitted).
     *
     * @return A boolean.
     */
    public boolean overlapsWith(SimpleHistogramBin bin) {
        if (this.upperBound < bin.lowerBound) {
            return false;
        }
        if (this.lowerBound > bin.upperBound) {
            return false;
        }
        if (this.upperBound == bin.lowerBound) {
            return this.includeUpperBound && bin.includeLowerBound;
        }
        if (this.lowerBound == bin.upperBound) {
            return this.includeLowerBound && bin.includeUpperBound;
        }
        return true;
    }

    /**
     * Compares the bin to an arbitrary object and returns the relative
     * ordering.
     *
     * @param obj  the object.
     *
     * @return An integer indicating the relative ordering of the this bin and
     *         the given object.
     */
    public int compareTo(Object obj) {
        if (!(obj instanceof SimpleHistogramBin)) {
            return 0;
        }
        SimpleHistogramBin bin = (SimpleHistogramBin) obj;
        if (this.lowerBound < bin.lowerBound) {
            return -1;
        }
        if (this.lowerBound > bin.lowerBound) {
            return 1;
        }
        // lower bounds are the same
        if (this.upperBound < bin.upperBound) {
            return -1;
        }
        if (this.upperBound > bin.upperBound) {
            return 1;
        }
        return 0;
    }

    /**
     * Tests this bin for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SimpleHistogramBin)) {
            return false;
        }
        SimpleHistogramBin that = (SimpleHistogramBin) obj;
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (this.includeLowerBound != that.includeLowerBound) {
            return false;
        }
        if (this.includeUpperBound != that.includeUpperBound) {
            return false;
        }
        if (this.itemCount != that.itemCount) {
            return false;
        }
        if (this.selected != that.selected) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of the bin.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException not thrown by this class.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
