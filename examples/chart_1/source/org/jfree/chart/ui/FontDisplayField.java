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
 * ---------------------
 * FontDisplayField.java
 * ---------------------
 * (C) Copyright 2000-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Arnaud Lelievre;
 *
 * Changes (from 26-Oct-2001)
 * --------------------------
 * 26-Oct-2001 : Changed package to com.jrefinery.ui.*;
 * 14-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Sep-2003 : Added internationalization via use of properties resourceBundle (RFE 690236) (AL);
 * 21-Jun-2007 : Copied from JCommon (DG);
 *
 */

package org.jfree.chart.ui;

import java.awt.Font;
import java.util.ResourceBundle;

import javax.swing.JTextField;

import org.jfree.chart.util.ResourceBundleWrapper;

/**
 * A field for displaying a font selection.  The display field itself is
 * read-only, to the developer must provide another mechanism to allow the
 * user to change the font.
 */
public class FontDisplayField extends JTextField {

    /** The current font. */
    private Font displayFont;

    /** The resourceBundle for the localization. */
    protected static final ResourceBundle localizationResources =
            ResourceBundleWrapper.getBundle(
                    "org.jfree.chart.ui.LocalizationBundle");

    /**
     * Standard constructor - builds a FontDescriptionField initialised with
     * the specified font.
     *
     * @param font  the font.
     */
    public FontDisplayField(Font font) {
        super("");
        setDisplayFont(font);
        setEnabled(false);
    }

    /**
     * Returns the current font.
     *
     * @return the font.
     */
    public Font getDisplayFont() {
        return this.displayFont;
    }

    /**
     * Sets the font.
     *
     * @param font  the font.
     */
    public void setDisplayFont(Font font) {
        this.displayFont = font;
        setText(fontToString(this.displayFont));
    }

    /**
     * Returns a string representation of the specified font.
     *
     * @param font  the font.
     *
     * @return a string describing the font.
     */
    private String fontToString(Font font) {
        if (font != null) {
            return font.getFontName() + ", " + font.getSize();
        }
        else {
            return localizationResources.getString("No_Font_Selected");
        }
    }

}
