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
 * ----------------------
 * RefineryUtilities.java
 * ----------------------
 * (C) Copyright 2000-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Jon Iles;
 *
 * Changes (from 26-Oct-2001)
 * --------------------------
 * 26-Oct-2001 : Changed package to com.jrefinery.ui.*;
 * 26-Nov-2001 : Changed name to SwingRefinery.java to make it obvious that
 *               this is not part of the Java APIs (DG);
 * 10-Dec-2001 : Changed name (again) to JRefineryUtilities.java (DG);
 * 28-Feb-2002 : Moved system properties classes into
 *               com.jrefinery.ui.about (DG);
 * 19-Apr-2002 : Renamed JRefineryUtilities-->RefineryUtilities.
 *               Added drawRotatedString() method (DG);
 * 21-May-2002 : Changed frame positioning methods to accept Window parameters,
 *               as suggested by Laurence Vanhelsuwe (DG);
 * 27-May-2002 : Added getPointInRectangle method (DG);
 * 26-Jun-2002 : Removed unnecessary imports (DG);
 * 12-Jul-2002 : Added workaround for rotated text (JI);
 * 14-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-May-2003 : Added a new drawRotatedString() method (DG);
 * 09-May-2003 : Added a drawRotatedShape() method (DG);
 * 10-Jun-2003 : Updated aligned and rotated string methods (DG);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 07-Nov-2003 : Added rotateShape() method (DG);
 * 16-Mar-2004 : Moved rotateShape() method to ShapeUtils.java (DG);
 * 07-Apr-2004 : Modified text bounds calculation with
 *               TextUtilities.getTextBounds() (DG);
 * 21-May-2004 : Fixed bug 951870 - precision in drawAlignedString()
 *               method (DG);
 * 30-Sep-2004 : Deprecated and moved a number of methods to the TextUtilities
 *               class (DG);
 * 04-Oct-2004 : Renamed ShapeUtils --> ShapeUtilities (DG);
 * 11-Jan-2005 : Removed deprecated code in preparation for the 1.0.0
 *               release (DG);
 * 21-Jun-2007 : Copied from JCommon (DG);
 *
 */

package org.jfree.chart.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * A collection of utility methods relating to user interfaces.
 */
public class RefineryUtilities {

    private RefineryUtilities() {
    }

    /**
     * Computes the center point of the current screen device. If this method
     * is called on JDK 1.4, Xinerama-aware results are returned. (See
     * Sun-Bug-ID 4463949 for details).
     *
     * @return the center point of the current screen.
     */
    public static Point getCenterPoint ()
    {
      GraphicsEnvironment localGraphicsEnvironment
              = GraphicsEnvironment.getLocalGraphicsEnvironment();
      try {
          Method method = GraphicsEnvironment.class.getMethod("getCenterPoint",
                  (Class[]) null);
          return (Point) method.invoke(localGraphicsEnvironment,
                  (Object[]) null);
      }
      catch (Exception e) {
        // ignore ... will fail if this is not a JDK 1.4 ..
      }

      Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
      return new Point (s.width / 2, s.height / 2);
    }

  /**
   * Computes the maximum bounds of the current screen device. If this method
   * is called on JDK 1.4, Xinerama-aware results are returned.
   * (See Sun-Bug-ID 4463949 for details).
   *
   * @return the maximum bounds of the current screen.
   */
    public static Rectangle getMaximumWindowBounds() {
      GraphicsEnvironment localGraphicsEnvironment
              = GraphicsEnvironment.getLocalGraphicsEnvironment();
      try {
          Method method = GraphicsEnvironment.class.getMethod(
                  "getMaximumWindowBounds", (Class[]) null);
          return (Rectangle) method.invoke(localGraphicsEnvironment,
                  (Object[]) null);
      }
      catch (Exception e) {
          // ignore ... will fail if this is not a JDK 1.4 ..
      }

      Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
      return new Rectangle (0, 0, s.width, s.height);
    }

    /**
     * Positions the specified frame in the middle of the screen.
     *
     * @param frame  the frame to be centered on the screen.
     */
    public static void centerFrameOnScreen(final Window frame) {
        positionFrameOnScreen(frame, 0.5, 0.5);
    }

    /**
     * Positions the specified frame at a relative position in the screen,
     * where 50% is considered to be the center of the screen.
     *
     * @param frame  the frame.
     * @param horizontalPercent  the relative horizontal position of the frame
     *     (0.0 to 1.0, where 0.5 is the center of the screen).
     * @param verticalPercent  the relative vertical position of the frame
     *     (0.0 to 1.0, where 0.5 is the center of the screen).
     */
    public static void positionFrameOnScreen(Window frame,
                                             double horizontalPercent,
                                             double verticalPercent) {

        Rectangle s = getMaximumWindowBounds();
        Dimension f = frame.getSize();
        int w = Math.max(s.width - f.width, 0);
        int h = Math.max(s.height - f.height, 0);
        int x = (int) (horizontalPercent * w) + s.x;
        int y = (int) (verticalPercent * h) + s.y;
        frame.setBounds(x, y, f.width, f.height);

    }

    /**
     * Positions the specified frame at a random location on the screen while ensuring that the
     * entire frame is visible (provided that the frame is smaller than the screen).
     *
     * @param frame  the frame.
     */
    public static void positionFrameRandomly(Window frame) {
        positionFrameOnScreen(frame, Math.random(), Math.random());
    }

    /**
     * Positions the specified dialog within its parent.
     *
     * @param dialog  the dialog to be positioned on the screen.
     */
    public static void centerDialogInParent(Dialog dialog) {
        positionDialogRelativeToParent(dialog, 0.5, 0.5);
    }

    /**
     * Positions the specified dialog at a position relative to its parent.
     *
     * @param dialog  the dialog to be positioned.
     * @param horizontalPercent  the relative location.
     * @param verticalPercent  the relative location.
     */
    public static void positionDialogRelativeToParent(Dialog dialog,
                                                      double horizontalPercent,
                                                      double verticalPercent) {
        Dimension d = dialog.getSize();
        Container parent = dialog.getParent();
        Dimension p = parent.getSize();

        int baseX = parent.getX() - d.width;
        int baseY = parent.getY() - d.height;
        int w = d.width + p.width;
        int h = d.height + p.height;
        int x = baseX + (int) (horizontalPercent * w);
        int y = baseY + (int) (verticalPercent * h);

        // make sure the dialog fits completely on the screen...
        Rectangle s = getMaximumWindowBounds();
        x = Math.min(x, (s.width - d.width));
        x = Math.max(x, 0);
        y = Math.min(y, (s.height - d.height));
        y = Math.max(y, 0);

        dialog.setBounds(x + s.x, y + s.y, d.width, d.height);

    }

    /**
     * Creates a panel that contains a table based on the specified table model.
     *
     * @param model  the table model to use when constructing the table.
     *
     * @return The panel.
     */
    public static JPanel createTablePanel(TableModel model) {

        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(model);
        for (int columnIndex = 0; columnIndex < model.getColumnCount();
                columnIndex++) {
            TableColumn column = table.getColumnModel().getColumn(columnIndex);
            Class c = model.getColumnClass(columnIndex);
            if (c.equals(Number.class)) {
                column.setCellRenderer(new NumberCellRenderer());
            }
        }
        panel.add(new JScrollPane(table));
        return panel;

    }

    /**
     * Creates a label with a specific font.
     *
     * @param text  the text for the label.
     * @param font  the font.
     *
     * @return The label.
     */
    public static JLabel createJLabel(String text, Font font) {

        JLabel result = new JLabel(text);
        result.setFont(font);
        return result;

    }

    /**
     * Creates a label with a specific font and color.
     *
     * @param text  the text for the label.
     * @param font  the font.
     * @param color  the color.
     *
     * @return The label.
     */
    public static JLabel createJLabel(String text, Font font, Color color) {

        JLabel result = new JLabel(text);
        result.setFont(font);
        result.setForeground(color);
        return result;

    }

    /**
     * Creates a {@link JButton}.
     *
     * @param label  the label.
     * @param font  the font.
     *
     * @return The button.
     */
    public static JButton createJButton(String label, Font font) {

        JButton result = new JButton(label);
        result.setFont(font);
        return result;

    }

}


