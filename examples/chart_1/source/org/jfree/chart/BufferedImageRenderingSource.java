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
 * ---------------------------------
 * BufferedImageRenderingSource.java
 * ---------------------------------
 * (C) Copyright 2009, by David Gilbert (for Object Refinery Limited).
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 06-Jul-2009 : Version 1 (DG);
 *
 */

package org.jfree.chart;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetAndSelection;
import org.jfree.data.general.DatasetSelectionState;

/**
 * The rendering source for drawing to a buffered image.
 *
 * @since 1.2.0
 */
public class BufferedImageRenderingSource implements RenderingSource {

    /** The buffered image. */
    private BufferedImage image;

    /**
     * A list of {@link DatasetAndSelection} objects.
     */
    private List selectionStates = new java.util.ArrayList();

    /**
     * Creates a new rendering source.
     *
     * @param image  the buffered image (<code>null</code> not permitted).
     */
    public BufferedImageRenderingSource(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        this.image = image;
    }

    /**
     * Returns a graphics context that a renderer can use to calculate
     * selection bounds.
     *
     * @return A graphics context.
     */
    public Graphics2D createGraphics2D() {
        return this.image.createGraphics();
    }

    /**
     * Returns the selection state, if any, that this source is maintaining
     * for the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The selection state (possibly <code>null</code>).
     */
    public DatasetSelectionState getSelectionState(Dataset dataset) {
        Iterator iterator = this.selectionStates.iterator();
        while (iterator.hasNext()) {
            DatasetAndSelection das = (DatasetAndSelection) iterator.next();
            if (das.getDataset() == dataset) {
                return das.getSelection();
            }
        }
        // we didn't find a selection state for the dataset...
        return null;
    }

    /**
     * Stores the selection state that is associated with the specified
     * dataset for this rendering source.  If two rendering sources are
     * displaying the same dataset, ideally they should have separate selection
     * states.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param state  the state (<code>null</code> permitted).
     */
    public void putSelectionState(Dataset dataset,
            DatasetSelectionState state) {
        this.selectionStates.add(new DatasetAndSelection(dataset, state));
    }

}
