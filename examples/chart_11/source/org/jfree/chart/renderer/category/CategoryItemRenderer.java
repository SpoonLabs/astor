/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
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
 * -------------------------
 * CategoryItemRenderer.java
 * -------------------------
 *
 * (C) Copyright 2001-2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *
 * Changes
 * -------
 * 23-Oct-2001 : Version 1 (DG);
 * 16-Jan-2002 : Renamed HorizontalCategoryItemRenderer.java 
 *               --> CategoryItemRenderer.java (DG);
 * 05-Feb-2002 : Changed return type of the drawCategoryItem method from void 
 *               to Shape, as part of the tooltips implementation (DG)        
 *
 *               NOTE (30-May-2002) : this has subsequently been changed back 
 *               to void, tooltips are now collected along with entities in 
 *               ChartRenderingInfo (DG);
 *
 * 14-Mar-2002 : Added the initialise method, and changed all bar plots to use 
 *               this renderer (DG);
 * 23-May-2002 : Added ChartRenderingInfo to the initialise method (DG);
 * 29-May-2002 : Added the getAxisArea(Rectangle2D) method (DG);
 * 06-Jun-2002 : Updated Javadoc comments (DG);
 * 26-Jun-2002 : Added range axis to the initialise method (DG);
 * 24-Sep-2002 : Added getLegendItem() method (DG);
 * 23-Oct-2002 : Added methods to get/setToolTipGenerator (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 06-Nov-2002 : Added the domain axis to the drawCategoryItem method.  Renamed
 *               drawCategoryItem() --> drawItem() (DG);
 * 20-Nov-2002 : Changed signature of drawItem() method to reflect use of 
 *               TableDataset (DG);
 * 26-Nov-2002 : Replaced the isStacked() method with the getRangeType() 
 *               method (DG);
 * 08-Jan-2003 : Changed getSeriesCount() --> getRowCount() and
 *               getCategoryCount() --> getColumnCount() (DG);
 * 09-Jan-2003 : Changed name of grid-line methods (DG);
 * 21-Jan-2003 : Merged TableDataset with CategoryDataset (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in 
 *               drawItem() method (DG);
 * 29-Apr-2003 : Eliminated Renderer interface (DG);
 * 02-Sep-2003 : Fix for bug 790407 (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 20-Oct-2003 : Added setOutlinePaint() method (DG);
 * 06-Feb-2004 : Added missing methods, and moved deprecated methods (DG);
 * 19-Feb-2004 : Added extra setXXXLabelsVisible() methods (DG);
 * 29-Apr-2004 : Changed Integer --> int in initialise() method (DG);
 * 18-May-2004 : Added methods for item label paint (DG);
 * 05-Nov-2004 : Added getPassCount() method and 'pass' parameter to drawItem() 
 *               method (DG);
 * 07-Jan-2005 : Renamed getRangeExtent() --> findRangeBounds (DG);
 * 11-Jan-2005 : Removed deprecated code in preparation for 1.0.0 release (DG);
 * 23-Feb-2005 : Now extends LegendItemSource (DG);
 * 20-Apr-2005 : Renamed CategoryLabelGenerator 
 *               --> CategoryItemLabelGenerator (DG);
 * 20-May-2005 : Added drawDomainMarker() method (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 20-Feb-2007 : Updated API docs (DG);
 * 19-Apr-2007 : Deprecated seriesVisible and seriesVisibleInLegend flags (DG);
 * 20-Apr-2007 : Deprecated paint, fillPaint, outlinePaint, stroke, 
 *               outlineStroke, shape, itemLabelsVisible, itemLabelFont, 
 *               itemLabelPaint, positiveItemLabelPosition, 
 *               negativeItemLabelPosition and createEntities override 
 *               fields (DG);
 * 20-Jun-2007 : Removed deprecated code (DG);
 * 26-Jun-2007 : Added a number of methods already implemented in the
 *               AbstractCategoryItemRenderer class but missing from this
 *               interface (DG);
 * 27-Jun-2007 : Changed get/setBaseItemLabelsVisible from Boolean to 
 *               boolean (DG);
 * 06-Jul-2007 : Added annotation support (DG);
 *
 */

package org.jfree.chart.renderer.category;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.util.Layer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;

/**
 * A plug-in object that is used by the {@link CategoryPlot} class to display 
 * individual data items from a {@link CategoryDataset}.
 * <p>
 * This interface defines the methods that must be provided by all renderers.  
 * If you are implementing a custom renderer, you should consider extending the
 * {@link AbstractCategoryItemRenderer} class.
 * <p>
 * Most renderer attributes are defined using a "per-series" approach with
 * a base (default) value to cover cases where no per-series value is
 * defined.
 */
public interface CategoryItemRenderer extends LegendItemSource {

    /**
     * Returns the plot that the renderer has been assigned to (where 
     * <code>null</code> indicates that the renderer is not currently assigned 
     * to a plot).
     *
     * @return The plot (possibly <code>null</code>).
     * 
     * @see #setPlot(CategoryPlot)
     */
    public CategoryPlot getPlot();

    /**
     * Sets the plot that the renderer has been assigned to.  This method is 
     * usually called by the {@link CategoryPlot}, in normal usage you 
     * shouldn't need to call this method directly.
     *
     * @param plot  the plot (<code>null</code> not permitted).
     * 
     * @see #getPlot()
     */
    public void setPlot(CategoryPlot plot);

    /**
     * Returns the number of passes through the dataset required by the 
     * renderer.  Usually this will be one, but some renderers may use
     * a second or third pass to overlay items on top of things that were
     * drawn in an earlier pass.
     * 
     * @return The pass count.
     */
    public int getPassCount();

    /**
     * Returns the range of values the renderer requires to display all the 
     * items from the specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     * 
     * @return The range (or <code>null</code> if the dataset is 
     *         <code>null</code> or empty).
     */
    public Range findRangeBounds(CategoryDataset dataset);
    
    /**
     * Adds a change listener.
     * 
     * @param listener  the listener.
     * 
     * @see #removeChangeListener(RendererChangeListener)
     */
    public void addChangeListener(RendererChangeListener listener);
    
    /**
     * Removes a change listener.
     * 
     * @param listener  the listener.
     * 
     * @see #addChangeListener(RendererChangeListener)
     */
    public void removeChangeListener(RendererChangeListener listener);

    //// VISIBILITY ///////////////////////////////////////////////////////////
    
    /**
     * Returns a boolean that indicates whether or not the specified item 
     * should be drawn (this is typically used to hide an entire series).
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return A boolean.
     */
    public boolean getItemVisible(int series, int item);
    
    /**
     * Returns a boolean that indicates whether or not the specified series 
     * should be drawn (this is typically used to hide an entire series).
     * 
     * @param series  the series index.
     * 
     * @return A boolean.
     */
    public boolean isSeriesVisible(int series);
    
    /**
     * Returns the flag that controls whether a series is visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly <code>null</code>).
     * 
     * @see #setSeriesVisible(int, Boolean)
     */
    public Boolean getSeriesVisible(int series);
    
    /**
     * Sets the flag that controls whether a series is visible and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag (<code>null</code> permitted).
     * 
     * @see #getSeriesVisible(int)
     */
    public void setSeriesVisible(int series, Boolean visible);
    
    /**
     * Sets the flag that controls whether a series is visible and, if 
     * requested, sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param series  the series index.
     * @param visible  the flag (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @see #getSeriesVisible(int)
     */
    public void setSeriesVisible(int series, Boolean visible, boolean notify);

    /**
     * Returns the base visibility for all series.
     *
     * @return The base visibility.
     * 
     * @see #setBaseSeriesVisible(boolean)
     */
    public boolean getBaseSeriesVisible();

    /**
     * Sets the base visibility and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param visible  the flag.
     * 
     * @see #getBaseSeriesVisible()
     */
    public void setBaseSeriesVisible(boolean visible);
    
    /**
     * Sets the base visibility and, if requested, sends 
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  the visibility.
     * @param notify  notify listeners?
     * 
     * @see #getBaseSeriesVisible()
     */
    public void setBaseSeriesVisible(boolean visible, boolean notify);


    // SERIES VISIBLE IN LEGEND (not yet respected by all renderers)
    
    /**
     * Returns <code>true</code> if the series should be shown in the legend,
     * and <code>false</code> otherwise.
     * 
     * @param series  the series index.
     * 
     * @return A boolean.
     */
    public boolean isSeriesVisibleInLegend(int series);
    
    /**
     * Returns the flag that controls whether a series is visible in the 
     * legend.  This method returns only the "per series" settings - to 
     * do a lookup that falls back to the default value, you need to use the 
     * {@link #isSeriesVisibleInLegend(int)} method.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly <code>null</code>).
     * 
     * @see #setSeriesVisibleInLegend(int, Boolean)
     */
    public Boolean getSeriesVisibleInLegend(int series);
    
    /**
     * Sets the flag that controls whether a series is visible in the legend 
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag (<code>null</code> permitted).
     * 
     * @see #getSeriesVisibleInLegend(int)
     */
    public void setSeriesVisibleInLegend(int series, Boolean visible);
    
    /**
     * Sets the flag that controls whether a series is visible in the legend
     * and, if requested, sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param series  the series index.
     * @param visible  the flag (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @see #getSeriesVisibleInLegend(int)
     */
    public void setSeriesVisibleInLegend(int series, Boolean visible, 
                                         boolean notify);

    /**
     * Returns the base visibility in the legend for all series.
     *
     * @return The base visibility.
     * 
     * @see #setBaseSeriesVisibleInLegend(boolean)
     */
    public boolean getBaseSeriesVisibleInLegend();

    /**
     * Sets the base visibility in the legend and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     * 
     * @see #getBaseSeriesVisibleInLegend()
     */
    public void setBaseSeriesVisibleInLegend(boolean visible);
    
    /**
     * Sets the base visibility in the legend and, if requested, sends 
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  the visibility.
     * @param notify  notify listeners?
     * 
     * @see #getBaseSeriesVisibleInLegend()
     */
    public void setBaseSeriesVisibleInLegend(boolean visible, boolean notify);


    //// PAINT ////////////////////////////////////////////////////////////////
    
    /**
     * Returns the paint used to fill data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getItemPaint(int row, int column);
    
    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series index (zero-based).
     *
     * @return The paint (possibly <code>null</code>).
     * 
     * @see #setSeriesPaint(int, Paint)
     */
    public Paint getSeriesPaint(int series);

    /**
     * Sets the paint used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     * 
     * @see #getSeriesPaint(int)
     */
    public void setSeriesPaint(int series, Paint paint);
    
    /**
     * Sets the paint used for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index.
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     *
     * @since 1.2.0
     */
    public void setSeriesPaint(int series, Paint paint, boolean notify);
    
    /**
     * Returns the base paint.
     *
     * @return The base paint (never <code>null</code>).
     * 
     * @see #setBasePaint(Paint)
     */
    public Paint getBasePaint();

    /**
     * Sets the base paint and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     * 
     * @see #getBasePaint()
     */
    public void setBasePaint(Paint paint);
    
    /**
     * Sets the base paint and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * @param notify  notify listeners?
     *
     * @since 1.2.0
     */
    public void setBasePaint(Paint paint, boolean notify);
    

    //// FILL PAINT /////////////////////////////////////////////////////////
    
    /**
     * Returns the paint used to fill data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never <code>null</code>).
     *
     * @since 1.2.0
     */
    public Paint getItemFillPaint(int row, int column);
    
    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (possibly <code>null</code>).
     * 
     * @see #setSeriesFillPaint(int, Paint)
     *
     * @since 1.2.0
     */
    public Paint getSeriesFillPaint(int series);

    /**
     * Sets the fill paint for the specified series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     * 
     * @see #getSeriesFillPaint(int)
     *
     * @since 1.2.0
     */
    public void setSeriesFillPaint(int series, Paint paint);
    
    /**
     * Sets the fill paint for the specified series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @see #getSeriesFillPaint(int)
     *
     * @since 1.2.0
     */
    public void setSeriesFillPaint(int series, Paint paint, boolean notify);

    /**
     * Returns the base fill paint.
     *
     * @return The paint (never <code>null</code>).
     * 
     * @see #setBaseFillPaint(Paint)
     *
     * @since 1.2.0
     */
    public Paint getBaseFillPaint();

    /**
     * Sets the default fill paint and sends a {@link RendererChangeEvent} to 
     * all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     * 
     * @see #getBaseFillPaint()
     *
     * @since 1.2.0
     */
    public void setBaseFillPaint(Paint paint);

    /**
     * Sets the default fill paint and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     * @param notify  notify listeners?
     * 
     * @see #getBaseFillPaint()
     *
     * @since 1.2.0
     */
    public void setBaseFillPaint(Paint paint, boolean notify);
    

    //// OUTLINE PAINT /////////////////////////////////////////////////////////
    
    /**
     * Returns the paint used to outline data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getItemOutlinePaint(int row, int column);
    
    /**
     * Returns the paint used to outline an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (possibly <code>null</code>).
     * 
     * @see #setSeriesOutlinePaint(int, Paint)
     */
    public Paint getSeriesOutlinePaint(int series);

    /**
     * Sets the paint used for a series outline and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     * 
     * @see #getSeriesOutlinePaint(int)
     */
    public void setSeriesOutlinePaint(int series, Paint paint);

    /**
     * Sets the paint used for a series outline and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @see #getSeriesOutlinePaint(int)
     *
     * @since 1.2.0
     */
    public void setSeriesOutlinePaint(int series, Paint paint, boolean notify);

    /**
     * Returns the base outline paint.
     *
     * @return The paint (never <code>null</code>).
     * 
     * @see #setBaseOutlinePaint(Paint)
     */
    public Paint getBaseOutlinePaint();

    /**
     * Sets the default outline paint and sends a {@link RendererChangeEvent} 
     * to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     * 
     * @see #getBaseOutlinePaint()
     */
    public void setBaseOutlinePaint(Paint paint);

    /**
     * Sets the default outline paint and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     * @param notify  notify listeners?
     * 
     * @see #getBaseOutlinePaint()
     *
     * @since 1.2.0
     */
    public void setBaseOutlinePaint(Paint paint, boolean notify);
    

    //// STROKE ////////////////////////////////////////////////////////////////
    
    /**
     * Returns the stroke used to draw data items.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getItemStroke(int row, int column);

    /**
     * Returns the stroke used to draw the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (never <code>null</code>).
     * 
     * @see #setSeriesStroke(int, Stroke)
     */
    public Stroke getSeriesStroke(int series);
    
    /**
     * Sets the stroke used for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     * 
     * @see #getSeriesStroke(int)
     */
    public void setSeriesStroke(int series, Stroke stroke);

    /**
     * Sets the stroke used for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @see #getSeriesStroke(int)
     *
     * @since 1.2.0
     */
    public void setSeriesStroke(int series, Stroke stroke, boolean notify);
    
    /**
     * Returns the default stroke.
     *
     * @return The default stroke (never <code>null</code>).
     * 
     * @see #setBaseStroke(Stroke)
     */
    public Stroke getBaseStroke();

    /**
     * Sets the default stroke and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     * 
     * @see #getBaseStroke()
     */
    public void setBaseStroke(Stroke stroke);
    
    /**
     * Sets the default stroke and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     * @param notify  notify listeners?
     * 
     * @see #getBaseStroke()
     *
     * @since 1.2.0
     */
    public void setBaseStroke(Stroke stroke, boolean notify);
    
    
    //// OUTLINE STROKE ////////////////////////////////////////////////////////
    
    /**
     * Returns the stroke used to outline data items.
     * <p>
     * The default implementation passes control to the 
     * lookupSeriesOutlineStroke method.  You can override this method if you 
     * require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getItemOutlineStroke(int row, int column);
    
    /**
     * Returns the stroke used to outline the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (possibly <code>null</code>).
     * 
     * @see #setSeriesOutlineStroke(int, Stroke)
     */
    public Stroke getSeriesOutlineStroke(int series);

    /**
     * Sets the outline stroke used for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     * 
     * @see #getSeriesOutlineStroke(int)
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke);
    
    /**
     * Sets the outline stroke for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param stroke  the stroke (<code>null</code> permitted)
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getSeriesOutlineStroke(int)
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke, 
            boolean notify);
    
    /**
     * Returns the base outline stroke.
     *
     * @return The stroke (never <code>null</code>).
     * 
     * @see #setBaseOutlineStroke(Stroke)
     */
    public Stroke getBaseOutlineStroke();

    /**
     * Sets the default outline stroke and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     * 
     * @see #getBaseOutlineStroke()
     */
    public void setBaseOutlineStroke(Stroke stroke);
    
    /**
     * Sets the default outline stroke and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param stroke  the stroke (<code>null</code> not permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getBaseOutlineStroke()
     */
    public void setBaseOutlineStroke(Stroke stroke, boolean notify);
    

    //// SHAPE /////////////////////////////////////////////////////////////////
    
    /**
     * Returns a shape used to represent a data item.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The shape (never <code>null</code>).
     */
    public Shape getItemShape(int row, int column);
    
    /**
     * Returns a shape used to represent the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The shape (possibly <code>null</code>).
     * 
     * @see #setSeriesShape(int, Shape)
     */
    public Shape getSeriesShape(int series);

    /**
     * Sets the shape used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape (<code>null</code> permitted).
     * 
     * @see #getSeriesShape(int)
     */
    public void setSeriesShape(int series, Shape shape);
    
    /**
     * Sets the shape for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param shape  the shape (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getSeriesShape(int)
     */
    public void setSeriesShape(int series, Shape shape, boolean notify);
    
    /**
     * Returns the base shape.
     *
     * @return The shape (never <code>null</code>).
     * 
     * @see #setBaseShape(Shape)
     */
    public Shape getBaseShape();

    /**
     * Sets the default shape for the renderer and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param shape  the shape (<code>null</code> not permitted).
     * 
     * @see #getBaseShape()
     */
    public void setBaseShape(Shape shape);

    /**
     * Sets the default shape for the renderer and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param shape  the shape (<code>null</code> not permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getBaseShape()
     */
    public void setBaseShape(Shape shape, boolean notify);

    
    //// LEGEND ITEMS /////////////////////////////////////////////////////////
    
    /**
     * Returns a legend item for a series.  This method can return 
     * <code>null</code>, in which case the series will have no entry in the
     * legend.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series (zero-based index).
     *
     * @return The legend item (possibly <code>null</code>).
     */
    public LegendItem getLegendItem(int datasetIndex, int series);

    /**
     * Returns the legend item label generator.
     *
     * @return The label generator (never <code>null</code>).
     *
     * @see #setLegendItemLabelGenerator(CategorySeriesLabelGenerator)
     * 
     * @since 1.2.0
     */
    public CategorySeriesLabelGenerator getLegendItemLabelGenerator();

    /**
     * Sets the legend item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> not permitted).
     *
     * @see #getLegendItemLabelGenerator()
     * 
     * @since 1.2.0
     */
    public void setLegendItemLabelGenerator(
            CategorySeriesLabelGenerator generator);
    
    /**
     * Returns the legend item tool tip generator.
     *
     * @return The tool tip generator (possibly <code>null</code>).
     *
     * @see #setLegendItemToolTipGenerator(CategorySeriesLabelGenerator)
     * 
     * @since 1.2.0
     */
    public CategorySeriesLabelGenerator getLegendItemToolTipGenerator();

    /**
     * Sets the legend item tool tip generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     *
     * @see #setLegendItemToolTipGenerator(CategorySeriesLabelGenerator)
     * 
     * @since 1.2.0
     */
    public void setLegendItemToolTipGenerator(
            CategorySeriesLabelGenerator generator);
    
    /**
     * Returns the legend item URL generator.
     *
     * @return The URL generator (possibly <code>null</code>).
     *
     * @see #setLegendItemURLGenerator(CategorySeriesLabelGenerator)
     * 
     * @since 1.2.0
     */
    public CategorySeriesLabelGenerator getLegendItemURLGenerator();

    /**
     * Sets the legend item URL generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     *
     * @see #getLegendItemURLGenerator()
     * 
     * @since 1.2.0
     */
    public void setLegendItemURLGenerator(
            CategorySeriesLabelGenerator generator);
    
    
    // TOOL TIP GENERATOR
    
    /**
     * Returns the tool tip generator that should be used for the specified 
     * item.  This method looks up the generator using the "three-layer" 
     * approach outlined in the general description of this interface.  
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The generator (possibly <code>null</code>).
     */
    public CategoryToolTipGenerator getToolTipGenerator(int row, int column);
    
    /**
     * Returns the tool tip generator for the specified series (a "layer 1" 
     * generator).
     *
     * @param series  the series index (zero-based).
     *
     * @return The tool tip generator (possibly <code>null</code>).
     * 
     * @see #setSeriesToolTipGenerator(int, CategoryToolTipGenerator)
     */
    public CategoryToolTipGenerator getSeriesToolTipGenerator(int series);

    /**
     * Sets the tool tip generator for a series and sends a 
     * {@link org.jfree.chart.event.RendererChangeEvent} to all registered 
     * listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator (<code>null</code> permitted).
     * 
     * @see #getSeriesToolTipGenerator(int)
     */
    public void setSeriesToolTipGenerator(int series, 
                                          CategoryToolTipGenerator generator);
    
    /**
     * Sets the tool tip generator for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param generator  the generator (<code>null</code> permitted)
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getSeriesToolTipGenerator(int)
     */
    public void setSeriesToolTipGenerator(int series, 
            CategoryToolTipGenerator generator, boolean notify);

    /**
     * Returns the base tool tip generator (the "layer 2" generator).
     *
     * @return The tool tip generator (possibly <code>null</code>).
     * 
     * @see #setBaseToolTipGenerator(CategoryToolTipGenerator)
     */
    public CategoryToolTipGenerator getBaseToolTipGenerator();

    /**
     * Sets the base tool tip generator and sends a 
     * {@link org.jfree.chart.event.RendererChangeEvent} to all registered 
     * listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     * 
     * @see #getBaseToolTipGenerator()
     */
    public void setBaseToolTipGenerator(CategoryToolTipGenerator generator);
    
    /**
     * Sets the default tool tip generator and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param generator  the generator (<code>null</code> permitted).
     * @param notify  notify listeners?
     *
     * @since 1.2.0
     * 
     * @see #getBaseToolTipGenerator()
     */
    public void setBaseToolTipGenerator(CategoryToolTipGenerator generator, 
            boolean notify);

    
    // ITEM URL GENERATOR
    
    /**
     * Returns the URL generator for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The item URL generator.
     */
    public CategoryURLGenerator getURLGenerator(int series, int item);
    
    /**
     * Returns the URL generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The URL generator.
     * 
     * @see #setSeriesURLGenerator(int, CategoryURLGenerator)
     */
    public CategoryURLGenerator getSeriesURLGenerator(int series);

    /**
     * Sets the URL generator for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     * 
     * @see #getSeriesURLGenerator(int)
     */
    public void setSeriesURLGenerator(int series, 
            CategoryURLGenerator generator);

    /**
     * Sets the URL generator for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param generator  the generator (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     */
    public void setSeriesURLGenerator(int series, 
            CategoryURLGenerator generator, boolean notify);
    
    /**
     * Returns the base URL generator.
     *
     * @return The URL generator (possibly <code>null</code>).
     * 
     * @see #setBaseURLGenerator(CategoryURLGenerator)
     */
    public CategoryURLGenerator getBaseURLGenerator();

    /**
     * Sets the base URL generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the URL generator (<code>null</code> permitted).
     * 
     * @see #getBaseURLGenerator()
     */
    public void setBaseURLGenerator(CategoryURLGenerator generator);

    /**
     * Sets the default URL generator and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param generator  the generator (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     */
    public void setBaseURLGenerator(CategoryURLGenerator generator, 
            boolean notify);
        

    // ITEM LABELS VISIBLE 
    
    /**
     * Returns <code>true</code> if an item label is visible, and 
     * <code>false</code> otherwise.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return A boolean.
     */
    public boolean isItemLabelVisible(int row, int column);

    /**
     * Returns <code>true</code> if the item labels for a series are visible, 
     * and <code>false</code> otherwise.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return A boolean.
     * 
     * @see #setSeriesItemLabelsVisible(int, Boolean)
     */    
    public boolean isSeriesItemLabelsVisible(int series);
    
    /**
     * Returns a flag that controls whether or not item labels are displayed
     * for the data items in the specified series.
     * 
     * @param series  the series index.
     * 
     * @return The flag (possibly <code>null</code>).
     * 
     * @since 1.2.0
     * 
     * @see #isSeriesItemLabelsVisible(int)
     */
    public Boolean getSeriesItemLabelsVisible(int series);
    
    /**
     * Sets a flag that controls the visibility of the item labels for a series.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     * 
     * @see #isSeriesItemLabelsVisible(int)
     */
    public void setSeriesItemLabelsVisible(int series, boolean visible);
    
    /**
     * Sets a flag that controls the visibility of the item labels for a series.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the flag (<code>null</code> permitted).
     * 
     * @see #isSeriesItemLabelsVisible(int)
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible);
    
    /**
     * Sets the visibility of item labels for a series and, if requested, sends 
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the visible flag.
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     *                
     * @see #isSeriesItemLabelsVisible(int)
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible, 
                                           boolean notify);
    
    /**
     * Returns the base setting for item label visibility.
     * 
     * @return A flag (possibly <code>null</code>).
     * 
     * @see #setBaseItemLabelsVisible(boolean)
     */
    public boolean getBaseItemLabelsVisible();
    
    /**
     * Sets the base flag that controls whether or not item labels are visible.
     * 
     * @param visible  the flag.
     * 
     * @see #getBaseItemLabelsVisible()
     */
    public void setBaseItemLabelsVisible(boolean visible);
    
    /**
     * Sets the base visibility for item labels and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  the visibility flag.
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     *                
     * @see #getBaseItemLabelsVisible()
     */
    public void setBaseItemLabelsVisible(boolean visible, boolean notify);
    

    // ITEM LABEL GENERATOR
    
    /**
     * Returns the item label generator for the specified data item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The generator (possibly <code>null</code>).
     */
    public CategoryItemLabelGenerator getItemLabelGenerator(int series, 
            int item);
    
    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The label generator (possibly <code>null</code>).
     * 
     * @see #setSeriesItemLabelGenerator(int, CategoryItemLabelGenerator)
     */
    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series);

    /**
     * Sets the item label generator for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     * 
     * @see #getSeriesItemLabelGenerator(int)
     */
    public void setSeriesItemLabelGenerator(int series, 
            CategoryItemLabelGenerator generator);
    
    /**
     * Sets the item label generator for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param generator  the generator (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getSeriesItemLabelGenerator(int)
     */
    public void setSeriesItemLabelGenerator(int series, 
            CategoryItemLabelGenerator generator, boolean notify);

    /**
     * Returns the base item label generator.
     *
     * @return The generator (possibly <code>null</code>).
     * 
     * @see #setBaseItemLabelGenerator(CategoryItemLabelGenerator)
     */
    public CategoryItemLabelGenerator getBaseItemLabelGenerator();

    /**
     * Sets the base item label generator and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     * 
     * @see #getBaseItemLabelGenerator()
     */
    public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator);
    
    /**
     * Sets the default item label generator and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param generator  the generator (<code>null</code> not permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     */
    public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator, 
            boolean notify);

    
    //// ITEM LABEL FONT  //////////////////////////////////////////////////////
    
    /**
     * Returns the font for an item label.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return The font (never <code>null</code>).
     */
    public Font getItemLabelFont(int row, int column);

    /**
     * Returns the font for all the item labels in a series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The font (possibly <code>null</code>).
     * 
     * @see #setSeriesItemLabelFont(int, Font)
     */
    public Font getSeriesItemLabelFont(int series);

    /**
     * Sets the item label font for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  
     * 
     * @param series  the series index (zero-based).
     * @param font  the font (<code>null</code> permitted).
     * 
     * @see #getSeriesItemLabelFont(int)
     */
    public void setSeriesItemLabelFont(int series, Font font);
    
    /**
     * Sets the item label font for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param font  the font (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getSeriesItemLabelFont(int)
     */
    public void setSeriesItemLabelFont(int series, Font font, boolean notify);

    /**
     * Returns the base item label font (this is used when no other font 
     * setting is available).
     * 
     * @return The font (<code>never</code> null).
     * 
     * @see #setBaseItemLabelFont(Font)
     */
    public Font getBaseItemLabelFont();

    /**
     * Sets the base item label font and sends a {@link RendererChangeEvent} 
     * to all registered listeners.  
     * 
     * @param font  the font (<code>null</code> not permitted).
     * 
     * @see #getBaseItemLabelFont()
     */
    public void setBaseItemLabelFont(Font font);
    
    /**
     * Sets the default item label font and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param font  the font (<code>null</code> not permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getBaseItemLabelFont()
     */
    public void setBaseItemLabelFont(Font font, boolean notify);
    
    
    //// ITEM LABEL PAINT  /////////////////////////////////////////////////////

    /**
     * Returns the paint used to draw an item label.
     * 
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getItemLabelPaint(int row, int column);
    
    /**
     * Returns the paint used to draw the item labels for a series.
     * 
     * @param series  the series index (zero based).
     * 
     * @return The paint (possibly <code>null<code>).
     * 
     * @see #setSeriesItemLabelPaint(int, Paint)
     */
    public Paint getSeriesItemLabelPaint(int series);

    /**
     * Sets the item label paint for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series (zero based index).
     * @param paint  the paint (<code>null</code> permitted).
     * 
     * @see #getSeriesItemLabelPaint(int)
     */
    public void setSeriesItemLabelPaint(int series, Paint paint);
    
    /**
     * Sets the item label paint for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getSeriesItemLabelPaint(int)
     */
    public void setSeriesItemLabelPaint(int series, Paint paint, 
            boolean notify);
    
    /**
     * Returns the base item label paint.
     * 
     * @return The paint (never <code>null<code>).
     * 
     * @see #setBaseItemLabelPaint(Paint)
     */
    public Paint getBaseItemLabelPaint();

    /**
     * Sets the base item label paint and sends a {@link RendererChangeEvent} 
     * to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * 
     * @see #getBaseItemLabelPaint()
     */
    public void setBaseItemLabelPaint(Paint paint);
    
    /**
     * Sets the default item label paint and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     * 
     * @see #getBaseItemLabelPaint()
     */
    public void setBaseItemLabelPaint(Paint paint, boolean notify);


    // POSITIVE ITEM LABEL POSITION...

    /**
     * Returns the item label position for positive values.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return The item label position (never <code>null</code>).
     */
    public ItemLabelPosition getPositiveItemLabelPosition(int row, int column);

    /**
     * Returns the item label position for all positive values in a series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The item label position.
     * 
     * @see #setSeriesPositiveItemLabelPosition(int, ItemLabelPosition)
     */
    public ItemLabelPosition getSeriesPositiveItemLabelPosition(int series);
    
    /**
     * Sets the item label position for all positive values in a series and 
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     * 
     * @see #getSeriesPositiveItemLabelPosition(int)
     */
    public void setSeriesPositiveItemLabelPosition(int series, 
                                                   ItemLabelPosition position);

    /**
     * Sets the item label position for all positive values in a series and (if
     * requested) sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     * 
     * @see #getSeriesPositiveItemLabelPosition(int)
     */
    public void setSeriesPositiveItemLabelPosition(int series, 
            ItemLabelPosition position, boolean notify);

    /**
     * Returns the base positive item label position.
     * 
     * @return The position.
     * 
     * @see #setBasePositiveItemLabelPosition(ItemLabelPosition)
     */
    public ItemLabelPosition getBasePositiveItemLabelPosition();

    /**
     * Sets the base positive item label position.
     * 
     * @param position  the position.
     * 
     * @see #getBasePositiveItemLabelPosition()
     */
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position);
    
    /**
     * Sets the base positive item label position and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position.
     * @param notify  notify registered listeners?
     * 
     * @see #getBasePositiveItemLabelPosition()
     */
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position, 
                                                 boolean notify);
    
    
    // NEGATIVE ITEM LABEL POSITION...

    /**
     * Returns the item label position for negative values.  This method can be
     * overridden to provide customisation of the item label position for 
     * individual data items.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column (zero-based).
     * 
     * @return The item label position.
     */
    public ItemLabelPosition getNegativeItemLabelPosition(int row, int column);

    /**
     * Returns the item label position for all negative values in a series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The item label position.
     * 
     * @see #setSeriesNegativeItemLabelPosition(int, ItemLabelPosition)
     */
    public ItemLabelPosition getSeriesNegativeItemLabelPosition(int series);

    /**
     * Sets the item label position for negative values in a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     * 
     * @see #getSeriesNegativeItemLabelPosition(int)
     */
    public void setSeriesNegativeItemLabelPosition(int series, 
                                                   ItemLabelPosition position);

    /**
     * Sets the item label position for negative values in a series and (if 
     * requested) sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     * 
     * @see #getSeriesNegativeItemLabelPosition(int)
     */
    public void setSeriesNegativeItemLabelPosition(int series, 
                                                   ItemLabelPosition position, 
                                                   boolean notify);

    /**
     * Returns the base item label position for negative values.
     * 
     * @return The position.
     * 
     * @see #setBaseNegativeItemLabelPosition(ItemLabelPosition)
     */
    public ItemLabelPosition getBaseNegativeItemLabelPosition();

    /**
     * Sets the base item label position for negative values and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position.
     * 
     * @see #getBaseNegativeItemLabelPosition()
     */
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position);
    
    /**
     * Sets the base negative item label position and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position.
     * @param notify  notify registered listeners?
     * 
     * @see #getBaseNegativeItemLabelPosition()
     */
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position, 
                                                 boolean notify);
    
    
    // CREATE ENTITIES
    
    /**
     * Returns <code>true</code> if the renderer should create an item entity
     * for the specified data item, and <code>false</code> otherwise.
     * 
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return A boolean.
     * 
     * @since 1.2.0
     */
    public boolean getItemCreateEntity(int series, int item);
    
    /**
     * Returns a flag that controls whether or not chart entities are 
     * generated for the data items in the specified series.
     * 
     * @param series  the series index.
     * 
     * @return A flag (possibly <code>null</code>).
     * 
     * @since 1.2.0
     */
    public Boolean getSeriesCreateEntities(int series);
    
    /**
     * Sets the flag that controls whether or not chart entities are created
     * for the data items in the specified series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param create  the new flag value (<code>null</code> permitted).
     * 
     * @since 1.2.0
     */
    public void setSeriesCreateEntities(int series, Boolean create);
    
    /**
     * Sets the flag that controls whether or not chart entities are created
     * for the data items in the specified series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param create  the new flag value (<code>null</code> permitted).
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     */
    public void setSeriesCreateEntities(int series, Boolean create, 
            boolean notify);
    
    /**
     * Returns the default flag that controls whether or not chart entities
     * are generated for the data items drawn by this renderer.
     * 
     * @return A boolean.
     * 
     * @since 1.2.0
     */
    public boolean getBaseCreateEntities();
    
    /**
     * Sets the default flag that controls whether or not chart entities are
     * generated for the data items drawn by this renderer.
     * 
     * @param create  the flag default.
     * 
     * @since 1.2.0
     */
    public void setBaseCreateEntities(boolean create);
    
    /**
     * Sets the default flag that controls whether or not chart entities are
     * generated for the data items drawn by this renderer and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param create  the flag default.
     * @param notify  notify listeners?
     * 
     * @since 1.2.0
     */
    public void setBaseCreateEntities(boolean create, boolean notify);

    
    //// ANNOTATIONS //////////////////////////////////////////////////////////
    
    /**
     * Adds an annotation and sends a {@link RendererChangeEvent} to all 
     * registered listeners.  The annotation is added to the foreground
     * layer.
     * 
     * @param annotation  the annotation (<code>null</code> not permitted).
     * 
     * @since 1.2.0
     */
    public void addAnnotation(CategoryAnnotation annotation);

    /**
     * Adds an annotation to the specified layer.
     * 
     * @param annotation  the annotation (<code>null</code> not permitted).
     * @param layer  the layer (<code>null</code> not permitted).
     * 
     * @since 1.2.0
     */
    public void addAnnotation(CategoryAnnotation annotation, Layer layer);

    /**
     * Removes the specified annotation and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     * 
     * @param annotation  the annotation to remove (<code>null</code> not 
     *                    permitted).
     * 
     * @return A boolean to indicate whether or not the annotation was 
     *         successfully removed.
     * 
     * @since 1.2.0
     */
    public boolean removeAnnotation(CategoryAnnotation annotation);
    
    /**
     * Removes all annotations and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     * 
     * @since 1.2.0
     */
    public void removeAnnotations();
    
    /**
     * Draws all the annotations for the specified layer.
     * 
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param layer  the layer.
     * @param info  the plot rendering info.
     * 
     * @since 1.2.0
     */
    public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, 
            CategoryAxis domainAxis, ValueAxis rangeAxis, Layer layer, 
            PlotRenderingInfo info);
    
    
    //// DRAWING //////////////////////////////////////////////////////////////
    
    /**
     * Initialises the renderer.  This method will be called before the first 
     * item is rendered, giving the renderer an opportunity to initialise any 
     * state information it wants to maintain. The renderer can do nothing if 
     * it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param rendererIndex  the renderer index.
     * @param info  collects chart rendering information for return to caller.
     * 
     * @return A state object (maintains state information relevant to one 
     *         chart drawing).
     */
    public CategoryItemRendererState initialise(Graphics2D g2,
            Rectangle2D dataArea, CategoryPlot plot, int rendererIndex,
            PlotRenderingInfo info);

    /**
     * Draws a single data item.
     *
     * @param g2  the graphics device.
     * @param state  state information for one chart.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
            int pass);

    /**
     * Draws a background for the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawBackground(Graphics2D g2, CategoryPlot plot, 
            Rectangle2D dataArea);

    /**
     * Draws an outline for the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawOutline(Graphics2D g2, CategoryPlot plot, 
            Rectangle2D dataArea);

    /**
     * Draws a grid line against the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data (not yet adjusted for any 
     *                  3D effect).
     * @param value  the value.
     * 
     * @see #drawRangeGridline(Graphics2D, CategoryPlot, ValueAxis, 
     *     Rectangle2D, double)
     */
    public void drawDomainGridline(Graphics2D g2, CategoryPlot plot,
            Rectangle2D dataArea, double value);

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 
     *                  3D effect).
     * @param value  the value.
     * 
     * @see #drawDomainGridline(Graphics2D, CategoryPlot, Rectangle2D, double)
     */
    public void drawRangeGridline(Graphics2D g2, CategoryPlot plot,
            ValueAxis axis, Rectangle2D dataArea, double value);

    /**
     * Draws a line (or some other marker) to indicate a particular category on 
     * the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the category axis.
     * @param marker  the marker.
     * @param dataArea  the area for plotting data (not including 3D effect).
     * 
     * @see #drawRangeMarker(Graphics2D, CategoryPlot, ValueAxis, Marker, 
     *     Rectangle2D)
     */
    public void drawDomainMarker(Graphics2D g2, CategoryPlot plot, 
            CategoryAxis axis, CategoryMarker marker, Rectangle2D dataArea);

    /**
     * Draws a line (or some other marker) to indicate a particular value on 
     * the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker.
     * @param dataArea  the area for plotting data (not including 3D effect).
     * 
     * @see #drawDomainMarker(Graphics2D, CategoryPlot, CategoryAxis, 
     *     CategoryMarker, Rectangle2D)
     */
    public void drawRangeMarker(Graphics2D g2, CategoryPlot plot, 
            ValueAxis axis, Marker marker, Rectangle2D dataArea);
                            
}
