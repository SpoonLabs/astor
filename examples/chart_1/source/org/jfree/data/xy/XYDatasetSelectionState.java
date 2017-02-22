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
 * ----------------------------
 * XYDatasetSelectionState.java
 * ----------------------------
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 29-Jun-2009 : Version 1 (DG);
 *
 */

package org.jfree.data.xy;

import java.io.Serializable;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.general.DatasetSelectionState;

/**
 * Returns information about the selection state of items in an
 * {@link XYDataset}.  Classes that implement this interface must also
 * implement {@link PublicCloneable} to ensure that charts and datasets can be
 * correctly cloned.  Likewise, classes implementing this interface must also
 * implement {@link Serializable}.
 * <br><br>
 * The selection state might be part of a dataset implementation, or it could
 * be maintained in parallel with a dataset implementation that doesn't
 * directly support selection state.
 *
 * @since 1.2.0
 */
public interface XYDatasetSelectionState extends DatasetSelectionState {

    /**
     * Returns the number of series.
     *
     * @return The number of series.
     */
    public int getSeriesCount();

    /**
     * Returns the number of items within the specified series.
     *
     * @param series  the series.
     *
     * @return The number of items in the series.
     */
    public int getItemCount(int series);

    /**
     * Returns <code>true</code> if the specified item is selected, and
     * <code>false</code> otherwise.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return A boolean.
     */
    public boolean isSelected(int series, int item);

    /**
     * Sets the selection state for an item in the dataset.
     *
     * @param series  the series index.
     * @param item  the item index.
     * @param selected  the selection state.
     */
    public void setSelected(int series, int item, boolean selected);

    /**
     * Sets the selection state for the specified item and, if requested,
     * fires a change event.
     *
     * @param series  the series index.
     * @param item  the item index.
     * @param selected  the selection state.
     * @param notify  notify listeners?
     */
    public void setSelected(int series, int item, boolean selected,
            boolean notify);

    /**
     * Send an event to registered listeners to indicate that the selection
     * has changed.
     */
    public void fireSelectionEvent();
    
    /**
     * Clears all selected items.
     */
    public void clearSelection();

}
