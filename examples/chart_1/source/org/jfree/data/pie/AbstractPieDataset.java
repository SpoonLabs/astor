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
 * AbstractPieDataset.java
 * -----------------------
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
import org.jfree.chart.event.DatasetChangeInfo;

/**
 * A base class that is convenient for implementing the {@link PieDataset}
 * interface.
 */
public class AbstractPieDataset extends AbstractDataset 
        implements SelectablePieDataset{

    /**
     * The dataset selection state (possibly <code>null</code>).
     *
     * @since 1.2.0
     */
    private PieDatasetSelectionState selectionState;

    /**
     * Default constructor.
     */
    public AbstractPieDataset() {
        super();
    }

    /**
     * Returns the selection state for this dataset, if any.  The default
     * value is <code>null</code>.
     *
     * @return The selection state (possibly <code>null</code>).
     *
     * @since 1.2.0
     */
    public PieDatasetSelectionState getSelectionState() {
        return this.selectionState;
    }

    /**
     * Sets the selection state for this dataset.
     *
     * @param state  the selection state (<code>null</code> permitted).
     *
     * @since 1.2.0
     */
    public void setSelectionState(PieDatasetSelectionState state) {
        this.selectionState = state;
        fireDatasetChanged(new DatasetChangeInfo());
        // TODO:  fill in real change details
    }

    // TODO: should we override equals() and check the selection state?  I
    // think yes...and we need cloning and serialization too...

}