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
 * -------------------
 * G2TextMeasurer.java
 * -------------------
 * (C) Copyright 2004-2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 07-Jan-2004 : Version 1 (DG);
 * 20-Jun-2007 : Copied from JCommon (DG);
 *
 */

package org.jfree.chart.text;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * A {@link TextMeasurer} based on a {@link Graphics2D}.
 */
public class G2TextMeasurer implements TextMeasurer {

    /** The graphics device. */
    private Graphics2D g2;

    /**
     * Creates a new text measurer.
     *
     * @param g2  the graphics device.
     */
    public G2TextMeasurer(Graphics2D g2) {
        this.g2 = g2;
    }

    /**
     * Returns the string width.
     *
     * @param text  the text.
     * @param start  the index of the first character to measure.
     * @param end  the index of the last character to measure.
     *
     * @return The string width.
     */
    public float getStringWidth(String text, int start, int end) {
        FontMetrics fm = this.g2.getFontMetrics();
        Rectangle2D bounds = TextUtilities.getTextBounds(text.substring(start,
                end), this.g2, fm);
        float result = (float) bounds.getWidth();
        return result;
    }

}

