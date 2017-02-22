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
 * --------------------
 * SelectableValue.java
 * --------------------
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 03-Jul-2009 : Version 1 (DG);
 *
 */

package org.jfree.data;

import java.io.Serializable;

/**
 * A data structure for a numerical value along with selection status.
 * 
 * @since 1.2.0
 */
public class SelectableValue implements Value, Cloneable, Serializable {

    /** The value (<code>null</code> is permitted). */
    private Number value;

    /** Is the item selected? */
    private boolean selected;

    /**
     * Creates a new instance with the specified value and the selection
     * state set to <code>false</code>.
     *
     * @param value  the value (<code>null</code> permitted).
     */
    public SelectableValue(Number value) {
        this(value, false);
    }

    /**
     * Creates a new instance with the specified value and selection state.
     *
     * @param value  the value (<code>null</code> permitted).
     * @param selected  the selection state.
     */
    public SelectableValue(Number value, boolean selected) {
        this.value = value;
        this.selected = selected;
    }

    /**
     * Returns the value for this data item.
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getValue() {
        return this.value;
    }

    /**
     * Returns the selection state.
     *
     * @return The selection state.
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Sets the selection state.
     *
     * @param selected  selected?
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
