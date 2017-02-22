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
 * ----------------
 * ZoomHandler.java
 * ----------------
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes:
 * --------
 * 11-Jun-2009 : Version 1 (DG);
 *
 */

package org.jfree.chart.panel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.ShapeUtilities;

/**
 * A mouse handler than performs a zooming operation on a ChartPanel.
 */
public class ZoomHandler extends AbstractMouseHandler {

    private Point2D zoomPoint;

    private Rectangle2D zoomRectangle;

    /**
     * Creates a new instance.
     */
    public ZoomHandler() {
        super();
    }

    public void mousePressed(MouseEvent e) {
        ChartPanel chartPanel = (ChartPanel) e.getSource();
        Rectangle2D screenDataArea = chartPanel.getScreenDataArea(e.getX(),
                e.getY());
        if (screenDataArea != null) {
            this.zoomPoint = ShapeUtilities.getPointInRectangle(e.getX(),
                    e.getY(), screenDataArea);
        }
        else {
            this.zoomPoint = null;
        }
    }

    public void mouseDragged(MouseEvent e) {

        // if no initial zoom point was set, ignore dragging...
        if (this.zoomPoint == null) {
            return;
        }
        ChartPanel panel = (ChartPanel) e.getSource();
        Graphics2D g2 = (Graphics2D) panel.getGraphics();

        // erase the previous zoom rectangle (if any).  We only need to do
        // this is we are using XOR mode, which we do when we're not using
        // the buffer (if there is a buffer, then at the end of this method we
        // just trigger a repaint)
        if (!panel.getUseBuffer()) {
            drawZoomRectangle(panel, g2, true);
        }

        boolean hZoom = false;
        boolean vZoom = false;
        if (panel.getOrientation() == PlotOrientation.HORIZONTAL) {
            hZoom = panel.isRangeZoomable();
            vZoom = panel.isDomainZoomable();
        }
        else {
            hZoom = panel.isDomainZoomable();
            vZoom = panel.isRangeZoomable();
        }
        Rectangle2D scaledDataArea = panel.getScreenDataArea(
                (int) this.zoomPoint.getX(), (int) this.zoomPoint.getY());
        if (hZoom && vZoom) {
            // selected rectangle shouldn't extend outside the data area...
            double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
            double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
            this.zoomRectangle = new Rectangle2D.Double(
                    this.zoomPoint.getX(), this.zoomPoint.getY(),
                    xmax - this.zoomPoint.getX(), ymax - this.zoomPoint.getY());
        }
        else if (hZoom) {
            double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
            this.zoomRectangle = new Rectangle2D.Double(
                    this.zoomPoint.getX(), scaledDataArea.getMinY(),
                    xmax - this.zoomPoint.getX(), scaledDataArea.getHeight());
        }
        else if (vZoom) {
            double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
            this.zoomRectangle = new Rectangle2D.Double(
                    scaledDataArea.getMinX(), this.zoomPoint.getY(),
                    scaledDataArea.getWidth(), ymax - this.zoomPoint.getY());
        }
        panel.setZoomRectangle(this.zoomRectangle);
        // Draw the new zoom rectangle...
        if (panel.getUseBuffer()) {
            panel.repaint();
        }
        else {
            // with no buffer, we use XOR to draw the rectangle "over" the
            // chart...
            drawZoomRectangle(panel, g2, true);
        }
        g2.dispose();
    }

    public void mouseReleased(MouseEvent e) {

        if (this.zoomRectangle == null) {
            return;
        }
        ChartPanel panel = (ChartPanel) e.getSource();
        boolean hZoom = false;
        boolean vZoom = false;
        if (panel.getOrientation() == PlotOrientation.HORIZONTAL) {
            hZoom = panel.isRangeZoomable();
            vZoom = panel.isDomainZoomable();
        }
        else {
            hZoom = panel.isDomainZoomable();
            vZoom = panel.isRangeZoomable();
        }

        boolean zoomTrigger1 = hZoom && Math.abs(e.getX()
                - this.zoomPoint.getX()) >= panel.getZoomTriggerDistance();
        boolean zoomTrigger2 = vZoom && Math.abs(e.getY()
                - this.zoomPoint.getY()) >= panel.getZoomTriggerDistance();
        if (zoomTrigger1 || zoomTrigger2) {
            if ((hZoom && (e.getX() < this.zoomPoint.getX()))
                    || (vZoom && (e.getY() < this.zoomPoint.getY()))) {
                panel.restoreAutoBounds();
            }
            else {
                double x, y, w, h;
                Rectangle2D screenDataArea = panel.getScreenDataArea(
                        (int) this.zoomPoint.getX(),
                        (int) this.zoomPoint.getY());
                double maxX = screenDataArea.getMaxX();
                double maxY = screenDataArea.getMaxY();
                // for mouseReleased event, (horizontalZoom || verticalZoom)
                // will be true, so we can just test for either being false;
                // otherwise both are true
                if (!vZoom) {
                    x = this.zoomPoint.getX();
                    y = screenDataArea.getMinY();
                    w = Math.min(this.zoomRectangle.getWidth(),
                            maxX - this.zoomPoint.getX());
                    h = screenDataArea.getHeight();
                }
                else if (!hZoom) {
                    x = screenDataArea.getMinX();
                    y = this.zoomPoint.getY();
                    w = screenDataArea.getWidth();
                    h = Math.min(this.zoomRectangle.getHeight(),
                            maxY - this.zoomPoint.getY());
                }
                else {
                    x = this.zoomPoint.getX();
                    y = this.zoomPoint.getY();
                    w = Math.min(this.zoomRectangle.getWidth(),
                            maxX - this.zoomPoint.getX());
                    h = Math.min(this.zoomRectangle.getHeight(),
                            maxY - this.zoomPoint.getY());
                }
                Rectangle2D zoomArea = new Rectangle2D.Double(x, y, w, h);
                panel.zoom(zoomArea);
            }
            this.zoomPoint = null;
            this.zoomRectangle = null;
            panel.setZoomRectangle(null);
            panel.clearLiveMouseHandler();
        }
        else {
            // erase the zoom rectangle
            Graphics2D g2 = (Graphics2D) panel.getGraphics();
            if (panel.getUseBuffer()) {
                panel.repaint();
            }
            else {
                drawZoomRectangle(panel, g2, true);
            }
            g2.dispose();
            this.zoomPoint = null;
            this.zoomRectangle = null;
            panel.setZoomRectangle(null);
            panel.clearLiveMouseHandler();
        }
    }

    /**
     * Draws zoom rectangle (if present).
     * The drawing is performed in XOR mode, therefore
     * when this method is called twice in a row,
     * the second call will completely restore the state
     * of the canvas.
     *
     * @param g2 the graphics device.
     * @param xor  use XOR for drawing?
     */
    private void drawZoomRectangle(ChartPanel panel, Graphics2D g2,
            boolean xor) {
        if (this.zoomRectangle != null) {
            if (xor) {
                 // Set XOR mode to draw the zoom rectangle
                g2.setXORMode(Color.gray);
            }
            if (panel.getFillZoomRectangle()) {
                g2.setPaint(panel.getZoomFillPaint());
                g2.fill(this.zoomRectangle);
            }
            else {
                g2.setPaint(panel.getZoomOutlinePaint());
                g2.draw(this.zoomRectangle);
            }
            if (xor) {
                // Reset to the default 'overwrite' mode
                g2.setPaintMode();
            }
        }
    }

}

