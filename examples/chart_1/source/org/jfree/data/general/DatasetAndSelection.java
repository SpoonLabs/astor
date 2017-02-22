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
 * ------------------------
 * DatasetAndSelection.java
 * ------------------------
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

package org.jfree.data.general;

/**
 * A utility class for pairing up a dataset and a dataset selection state.
 * Once paired, the items can be stored in a collection and retrieved on the
 * basis of the dataset as the key.
 *
 * // if cloning and serialization of a ChartPanel is supported, then this
 * // class will have to support it too.
 *
 * @since 1.2.0
 */
public class DatasetAndSelection {

    /** The dataset. */
    private Dataset dataset;

    /** The dataset selection state. */
    private DatasetSelectionState selection;

    /**
     * Creates a new instance.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param selection  the selection (<code>null</code> not permitted).
     */
    public DatasetAndSelection(Dataset dataset, DatasetSelectionState selection) {
        this.dataset = dataset;
        this.selection = selection;
    }

    /**
     * Returns the dataset.
     *
     * @return The dataset (never <code>null</code>).
     */
    public Dataset getDataset() {
        return this.dataset;
    }

    /**
     * Returns the selection state.
     *
     * @return The selection state (never <code>null</code>).
     */
    public DatasetSelectionState getSelection() {
        return this.selection;
    }

}

