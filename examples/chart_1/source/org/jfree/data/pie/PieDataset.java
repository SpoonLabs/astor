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
 * ---------------
 * PieDataset.java
 * ---------------
 * (C) Copyright 2001-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Sam (oldman);
 *
 * Changes
 * -------
 * 17-Nov-2001 : Version 1 (DG);
 * 22-Jan-2002 : Removed the getCategoryCount() method, updated Javadoc
 *               comments (DG);
 * 18-Apr-2002 : getCategories() now returns List instead of Set (oldman);
 * 23-Oct-2002 : Reorganised the code: PieDataset now extends KeyedValues
 *               interface (DG);
 * 04-Mar-2003 : Now just replicates the KeyedValuesDataset interface (DG);
 * 01-Jul-2009 : Added optional selection state that can be associated with
 *               the dataset (DG);
 *
 */

package org.jfree.data.pie;

import org.jfree.chart.plot.PiePlot;
import org.jfree.data.KeyedValues;
import org.jfree.data.general.Dataset;

/**
 * A general purpose dataset where values are associated with keys.  As the
 * name suggests, you can use this dataset to supply data for pie charts (refer
 * to the {@link PiePlot} class).
 */
public interface PieDataset extends KeyedValues, Dataset {

    /**
     * Returns the selection state for this dataset, if any.
     *
     * @return The selection state (possibly <code>null</code>).
     *
     * @since 1.2.0
     */
    public PieDatasetSelectionState getSelectionState();

}
