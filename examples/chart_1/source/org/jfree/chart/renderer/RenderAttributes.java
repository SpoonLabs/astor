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
 * ---------------------
 * RenderAttributes.java
 * ---------------------
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes:
 * --------
 * 29-Jun-2009 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.Serializable;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.util.BooleanList;
import org.jfree.chart.util.ObjectList;
import org.jfree.chart.util.PaintList;
import org.jfree.chart.util.ShapeList;
import org.jfree.chart.util.StrokeList;

/**
 * A set of attributes that a renderer can use for rendering a data item.  The
 * list is not exhaustive, but includes the most common attributes used by
 * renderer implementations.
 *
 * @since 1.2.0
 */
public class RenderAttributes implements Cloneable, Serializable {

    private boolean allowNull;

    private PaintList paintList;

    private Paint defaultPaint;

    private StrokeList strokeList;

    private Stroke defaultStroke;

    private ShapeList shapeList;

    private Shape defaultShape;

    private PaintList fillPaintList;

    private Paint defaultFillPaint;

    private PaintList outlinePaintList;

    private Paint defaultOutlinePaint;

    private StrokeList outlineStrokeList;

    private Stroke defaultOutlineStroke;

    private BooleanList labelsVisibleList;

    private Boolean defaultLabelVisible;

    private ObjectList labelFontList;

    private Font defaultLabelFont;

    private PaintList labelPaintList;

    private Paint defaultLabelPaint;

    private ObjectList positionItemLabelPositionList;

    private ItemLabelPosition defaultPositiveItemLabelPosition;

    private ObjectList negativeItemLabelPositionList;

    private ItemLabelPosition defaultNegativeItemLabelPosition;

    private BooleanList createEntityList;

    private Boolean defaultCreateEntity;

    /**
     * Creates a new instance.
     */
    public RenderAttributes() {
        this(true);
    }

    public RenderAttributes(boolean allowNull) {
        this.paintList = new PaintList();
        this.defaultPaint = allowNull ? null : Color.BLACK;
        this.strokeList = new StrokeList();
        this.defaultStroke = allowNull ? null : new BasicStroke(1.0f);
        this.fillPaintList = new PaintList();
        this.defaultFillPaint = allowNull ? null : Color.BLACK;
        this.outlinePaintList = new PaintList();
        this.defaultOutlinePaint = allowNull ? null : Color.BLACK;
        this.shapeList = new ShapeList();
    }

    public boolean getAllowNull() {
        return this.allowNull;
    }

    // PAINT

    public Paint getItemPaint(int series, int item) {
        return lookupSeriesPaint(series);
    }

    protected Paint lookupSeriesPaint(int series) {
        Paint result = this.paintList.getPaint(series);
        if (result == null) {
            result = this.defaultPaint;
        }
        return result;
    }

    public Paint getSeriesPaint(int series) {
        return this.paintList.getPaint(series);
    }

    public void setSeriesPaint(int series, Paint paint) {
        this.paintList.setPaint(series, paint);
    }

    public Paint getDefaultPaint() {
        return this.defaultPaint;
    }

    public void setDefaultPaint(Paint paint) {
        if (paint == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.defaultPaint = paint;
    }

    // STROKE

    public Stroke getItemStroke(int series, int item) {
        return lookupSeriesStroke(series);
    }

    protected Stroke lookupSeriesStroke(int series) {
        Stroke result = this.strokeList.getStroke(series);
        if (result == null) {
            result = this.defaultStroke;
        }
        return result;
    }

    public Stroke getSeriesStroke(int series) {
        return this.strokeList.getStroke(series);
    }

    public void setSeriesStroke(int series, Stroke stroke) {
        this.strokeList.setStroke(series, stroke);
    }

    public Stroke getDefaultStroke() {
        return this.defaultStroke;
    }

    public void setDefaultStroke(Stroke stroke) {
        if (stroke == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.defaultStroke = stroke;
    }

    // SHAPE

    public Shape getItemShape(int series, int item) {
        return lookupSeriesShape(series);
    }

    protected Shape lookupSeriesShape(int series) {
        Shape result = this.shapeList.getShape(series);
        if (result == null) {
            result = this.defaultShape;
        }
        return result;
    }

    public Shape getSeriesShape(int series) {
        return this.shapeList.getShape(series);
    }

    public void setSeriesShape(int series, Shape shape) {
        this.shapeList.setShape(series, shape);
    }

    public Shape getDefaultShape() {
        return this.defaultShape;
    }

    public void setDefaultShape(Shape shape) {
        if (shape == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.defaultShape = shape;
    }

    // FILL PAINT

    public Paint getItemFillPaint(int series, int item) {
        return lookupSeriesFillPaint(series);
    }

    protected Paint lookupSeriesFillPaint(int series) {
        Paint result = this.fillPaintList.getPaint(series);
        if (result == null) {
            result = this.defaultFillPaint;
        }
        return result;
    }

    public Paint getSeriesFillPaint(int series) {
        return this.fillPaintList.getPaint(series);
    }

    public void setSeriesFillPaint(int series, Paint paint) {
        this.fillPaintList.setPaint(series, paint);
    }

    public Paint getDefaultFillPaint() {
        return this.defaultFillPaint;
    }

    public void setDefaultFillPaint(Paint paint) {
        if (paint == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.defaultFillPaint = paint;
    }

    // OUTLINE PAINT

    public Paint getItemOutlinePaint(int series, int item) {
        return lookupSeriesOutlinePaint(series);
    }

    protected Paint lookupSeriesOutlinePaint(int series) {
        Paint result = this.outlinePaintList.getPaint(series);
        if (result == null) {
            result = this.defaultOutlinePaint;
        }
        return result;
    }

    public Paint getSeriesOutlinePaint(int series) {
        return this.outlinePaintList.getPaint(series);
    }

    public void setSeriesOutlinePaint(int series, Paint paint) {
        this.outlinePaintList.setPaint(series, paint);
    }

    public Paint getDefaultOutlinePaint() {
        return this.defaultOutlinePaint;
    }

    public void setDefaultOutlinePaint(Paint paint) {
        if (paint == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.defaultOutlinePaint = paint;
    }

    // OUTLINE STROKE

    public Stroke getItemOutlineStroke(int series, int item) {
        return lookupSeriesOutlineStroke(series);
    }

    protected Stroke lookupSeriesOutlineStroke(int series) {
        Stroke result = this.outlineStrokeList.getStroke(series);
        if (result == null) {
            result = this.defaultOutlineStroke;
        }
        return result;
    }

    public Stroke getSeriesOutlineStroke(int series) {
        return this.outlineStrokeList.getStroke(series);
    }

    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        this.outlineStrokeList.setStroke(series, stroke);
    }

    public Stroke getDefaultOutlineStroke() {
        return this.defaultOutlineStroke;
    }

    public void setDefaultOutlineStroke(Stroke stroke) {
        if (stroke == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.defaultOutlineStroke = stroke;
    }

    // LABEL VISIBLE

    public Boolean isLabelVisible(int series, int item) {
        return lookupSeriesLabelVisible(series);
    }

    protected Boolean lookupSeriesLabelVisible(int series) {
        Boolean result = this.labelsVisibleList.getBoolean(series);
        if (result == null) {
            result = this.defaultLabelVisible;
        }
        return result;
    }

    public Boolean getSeriesLabelVisible(int series) {
        return this.labelsVisibleList.getBoolean(series);
    }

    public void setSeriesLabelVisible(int series, Boolean visible) {
        this.labelsVisibleList.setBoolean(series, visible);
    }

    public Boolean getDefaultLabelVisible() {
        return this.defaultLabelVisible;
    }

    public void setDefaultLabelVisible(Boolean visible) {
        if (visible == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'visible' argument.");
        }
        this.defaultLabelVisible = visible;
    }

    // LABEL FONT

    public Font getItemLabelFont(int series, int item) {
        return lookupSeriesLabelFont(series);
    }

    protected Font lookupSeriesLabelFont(int series) {
        Font result = (Font) this.labelFontList.get(series);
        if (result == null) {
            result = this.defaultLabelFont;
        }
        return result;
    }

    public Font getSeriesLabelFont(int series) {
        return (Font) this.labelFontList.get(series);
    }

    public void setSeriesLabelFont(int series, Font font) {
        this.labelFontList.set(series, font);
    }

    public Font getDefaultLabelFont() {
        return this.defaultLabelFont;
    }

    public void setDefaultLabelFont(Font font) {
        if (font == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.defaultLabelFont = font;
    }

    // LABEL PAINT

    public Paint getItemLabelPaint(int series, int item) {
        return lookupSeriesLabelPaint(series);
    }

    protected Paint lookupSeriesLabelPaint(int series) {
        Paint result = this.labelPaintList.getPaint(series);
        if (result == null) {
            result = this.defaultLabelPaint;
        }
        return result;
    }

    public Paint getSeriesLabelPaint(int series) {
        return this.labelPaintList.getPaint(series);
    }

    public void setSeriesLabelPaint(int series, Paint paint) {
        this.labelPaintList.setPaint(series, paint);
    }

    public Paint getDefaultLabelPaint() {
        return this.defaultLabelPaint;
    }

    public void setDefaultLabelPaint(Paint paint) {
        if (paint == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.defaultLabelPaint = paint;
    }

    // POSITIVE ITEM LABEL POSITION

    // NEGATIVE ITEM LABEL POSITION

    // CREATE ENTITY - you'd probably use this rarely, but for example you
    // might want a chart where the selected items have entities created, but
    // not any other items.

    public Boolean getCreateEntity(int series, int item) {
        return lookupSeriesCreateEntity(series);
    }

    protected Boolean lookupSeriesCreateEntity(int series) {
        Boolean result = this.createEntityList.getBoolean(series);
        if (result == null) {
            result = this.defaultCreateEntity;
        }
        return result;
    }

    public Boolean getSeriesCreateEntity(int series) {
        return this.createEntityList.getBoolean(series);
    }

    public void setSeriesCreateEntity(int series, Boolean visible) {
        this.createEntityList.setBoolean(series, visible);
    }

    public Boolean getDefaultCreateEntity() {
        return this.defaultCreateEntity;
    }

    public void setDefaultCreateEntity(Boolean create) {
        if (create == null && !this.allowNull) {
            throw new IllegalArgumentException("Null 'create' argument.");
        }
        this.defaultCreateEntity = create;
    }

}