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
 * ---------------
 * PanHandler.java
 * ---------------
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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;

/**
 * Handles panning operations in a {@link ChartPanel} if the plot supports
 * panning (see the {@link Pannable} interface).
 *
 * @since 1.2.0
 */
public class PanHandler extends AbstractMouseHandler {

    /**
     * Temporary storage for the width and height of the chart
     * drawing area during panning.
     */
    private double panW, panH;

    /** The last mouse position during panning. */
    private Point panLast;

    /**
     * Creates a new instance.
     */
    public PanHandler() {
        super();
        this.panLast = null;
    }

    /**
     * Handles a mouse pressed event.
     *
     * @param e  the event.
     */
    public void mousePressed(MouseEvent e) {
        ChartPanel panel = (ChartPanel) e.getSource();
        Plot plot = panel.getChart().getPlot();
        if (!(plot instanceof Pannable)) {
            return;  // there's nothing for us to do
        }
        Pannable pannable = (Pannable) plot;
        if (pannable.isDomainPannable() || pannable.isRangePannable()) {
            Rectangle2D screenDataArea = panel.getScreenDataArea(e.getX(),
                    e.getY());
            if (screenDataArea != null && screenDataArea.contains(
                    e.getPoint())) {
                this.panW = screenDataArea.getWidth();
                this.panH = screenDataArea.getHeight();
                this.panLast = e.getPoint();
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }
        // the actual panning occurs later in the mouseDragged() method
    }

    /**
     * Handles a mouse dragged event.  This is where the panning occurs.
     *
     * @param e  the event.
     */
    public void mouseDragged(MouseEvent e) {
        // handle panning if we have a start point
        if (this.panLast == null) {
            return;
        }
        ChartPanel panel = (ChartPanel) e.getSource();
        JFreeChart chart = panel.getChart();
        double dx = e.getX() - this.panLast.getX();
        double dy = e.getY() - this.panLast.getY();
        if (dx == 0.0 && dy == 0.0) {
            return;
        }
        double wPercent = -dx / this.panW;
        double hPercent = dy / this.panH;
        boolean old = chart.getPlot().isNotify();
        chart.getPlot().setNotify(false);
        Pannable p = (Pannable) chart.getPlot();
        PlotRenderingInfo info = panel.getChartRenderingInfo().getPlotInfo();
        if (p.getOrientation() == PlotOrientation.VERTICAL) {
            p.panDomainAxes(wPercent, info, this.panLast);
            p.panRangeAxes(hPercent, info, this.panLast);
        }
        else {
            p.panDomainAxes(hPercent, info, this.panLast);
            p.panRangeAxes(wPercent, info, this.panLast);
        }
        this.panLast = e.getPoint();
        chart.getPlot().setNotify(old);
        return;
    }

    /**
     * Handles a mouse released event.
     *
     * @param e  the event.
     */
    public void mouseReleased(MouseEvent e) {
        // if we've been panning, we need to reset now that the mouse is
        // released...
        if (this.panLast != null) {
            ChartPanel panel = (ChartPanel) e.getSource();
            this.panLast = null;
            panel.setCursor(Cursor.getDefaultCursor());
            panel.clearLiveMouseHandler();
        }
    }

}

