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
 * DatasetChangeEvent.java
 * -----------------------
 * (C) Copyright 2000-2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Move to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 11-Jun-2002 : Separated the event source from the dataset to cover the case
 *               where the dataset is changed to null in the Plot class.
 *               Updated Javadocs (DG);
 * 04-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 05-Oct-2004 : Minor Javadoc updates (DG);
 * 01-Jul-2009 : Added dataset change info field (DG);
 *
 */

package org.jfree.data.event;

import org.jfree.data.general.*;
import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.plot.Plot;

/**
 * A change event that encapsulates information about a change to a dataset.
 */
public class DatasetChangeEvent extends java.util.EventObject {

    /**
     * The dataset that generated the change event.
     */
    private Dataset dataset;

    /**
     * Some details of the change.
     * 
     * @since 1.2.0
     */
    private DatasetChangeInfo info;

    /**
     * Constructs a new event.  The source is either the dataset or the
     * {@link Plot} class.  The dataset can be <code>null</code> (in this case
     * the source will be the {@link Plot} class).
     *
     * @param source  the source of the event.
     * @param dataset  the dataset that generated the event (<code>null</code>
     *                 permitted).
     * @param info  information about the change (<code>null</code> not
     *         permitted).
     *
     * @since 1.2.0
     */
    public DatasetChangeEvent(Object source, Dataset dataset,
            DatasetChangeInfo info) {
        super(source);
        if (info == null) {
            throw new IllegalArgumentException("Null 'info' argument.");
        }
        this.dataset = dataset;
        this.info = info;
    }

    /**
     * Returns the dataset that generated the event.  Note that the dataset
     * may be <code>null</code> since adding a <code>null</code> dataset to a
     * plot will generated a change event.
     *
     * @return The dataset (possibly <code>null</code>).
     */
    public Dataset getDataset() {
        return this.dataset;
    }

    /**
     * Returns the dataset change info.
     *
     * @return The dataset change info.
     *
     * @since 1.2.0
     */
    public DatasetChangeInfo getInfo() {
        return this.info;
    }

}
