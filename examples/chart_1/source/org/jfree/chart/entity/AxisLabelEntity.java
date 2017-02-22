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
 * AxisLabelEntity.java
 * --------------------
 * (C) Copyright 2007, 2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 02-Jul-2007 : Version 1 (DG);
 * 06-Jul-2007 : Assign axis in constructor, override toString() (DG);
 *
 */

package org.jfree.chart.entity;

import java.awt.Shape;

import org.jfree.chart.axis.Axis;

/**
 * A chart entity that represents the label for an axis.
 *
 * @since 1.2.0
 */
public class AxisLabelEntity extends ChartEntity {

    /** The axis. */
    private Axis axis;

    /**
     * Creates a new entity representing the label on an axis.
     *
     * @param axis  the axis.
     * @param hotspot  the hotspot.
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param url  the url (<code>null</code> permitted).
     */
    public AxisLabelEntity(Axis axis, Shape hotspot, String toolTipText,
            String url) {
        super(hotspot, toolTipText, url);
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }
        this.axis = axis;
    }

    /**
     * Returns the axis for this entity.
     *
     * @return The axis.
     */
    public Axis getAxis() {
        return this.axis;
    }

    /**
     * Returns a string representation of the chart entity, useful for
     * debugging.
     *
     * @return A string.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("AxisLabelEntity: ");
        buf.append("label = ");
        buf.append(this.axis.getLabel());
        return buf.toString();
    }


}
