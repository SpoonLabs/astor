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
 * ---------------
 * Selectable.java
 * ---------------
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limtied);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 18-Jun-2009 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.RenderingSource;

/**
 * An interface that should be implemented by a plot that supports mouse
 * selection of data items.
 *
 * @since 1.2.0
 */
public interface Selectable {

    /**
     * Returns <code>true</code> if this plot supports selection by a point,
     * and <code>false</code> otherwise.
     *
     * @return A boolean.
     */
    public boolean canSelectByPoint();

    /**
     * Returns <code>true</code> if this plot supports selection by a region,
     * and <code>false</code> otherwise.
     *
     * @return A boolean.
     */
    public boolean canSelectByRegion();

    /**
     * Set the selection state for the data item(s) at the specified (x, y)
     * coordinates in Java2D space.
     *
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param dataArea  the data area.
     * @param source  the selection source (usually a chart panel, possibly
     *         <code>null</code>).
     */
    public void select(double x, double y, Rectangle2D dataArea,
            RenderingSource source);

    /**
     * Set the selection state for the data item(s) within the specified region
     * in Java2D space.
     *
     * @param region  the region.
     * @param dataArea  the data area.
     * @param source  the selection source (usually a chart panel, possibly
     *         <code>null</code>).
     */
    public void select(GeneralPath region, Rectangle2D dataArea,
            RenderingSource source);

    /**
     * Deselects all items.
     */
    public void clearSelection();

}

