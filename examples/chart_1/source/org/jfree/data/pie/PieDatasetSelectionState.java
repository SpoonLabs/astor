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
 * -----------------------------
 * PieDatasetSelectionState.java
 * -----------------------------
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 01-Jul-2009 : Version 1 (DG);
 *
 */

package org.jfree.data.pie;

import org.jfree.data.general.*;
import java.io.Serializable;
import org.jfree.chart.util.PublicCloneable;

/**
 * Returns information about the selection state of items in an
 * {@link PieDataset}.  Classes that implement this interface must also
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
public interface PieDatasetSelectionState extends DatasetSelectionState {

    /**
     * Returns the number of items in the dataset.
     *
     * @return The number of items.
     */
    public int getItemCount();

    /**
     * Returns <code>true</code> if the specified item is selected, and
     * <code>false</code> otherwise.
     *
     * @param key  the item key.
     *
     * @return A boolean.
     */
    public boolean isSelected(Comparable key);

    /**
     * Sets the selection state for an item in the dataset.
     *
     * @param key  the item key.
     * @param selected  the selection state.
     */
    public void setSelected(Comparable key, boolean selected);

    /**
     * Sets the selection state for the specified item and, if requested,
     * fires a change event.
     *
     * @param key  the item key.
     * @param selected  the selection state.
     * @param notify  notify listeners?
     */
    public void setSelected(Comparable key, boolean selected, boolean notify);

    /**
     * Clears all selected items.
     */
    public void clearSelection();

    /**
     * Send an event to registered listeners to indicate that the selection
     * has changed.
     */
    public void fireSelectionEvent();

}
