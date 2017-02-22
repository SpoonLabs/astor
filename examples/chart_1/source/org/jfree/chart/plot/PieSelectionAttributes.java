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
 * ---------------------------
 * PieSelectionAttributes.java
 * ---------------------------
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes:
 * --------
 * 03-Jul-2009 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;
import org.jfree.chart.util.BooleanList;
import org.jfree.chart.util.ObjectList;
import org.jfree.chart.util.PaintList;
import org.jfree.chart.util.PaintMap;
import org.jfree.chart.util.StrokeMap;

/**
 * A set of attributes that a {@link PiePlot} can use for rendering a selected
 * data item.
 *
 * @since 1.2.0
 */
public class PieSelectionAttributes implements Cloneable, Serializable {

    private boolean allowNull;

    private PaintMap sectionPaint;

    private Paint defaultPaint;

    private PaintMap sectionOutlinePaint;

    private Paint defaultOutlinePaint;

    private StrokeMap sectionOutlineStroke;

    private Stroke defaultOutlineStroke;

    private BooleanList labelsVisibleList;

    private Boolean defaultLabelVisible;

    private ObjectList labelFontList;

    private Font defaultLabelFont;

    private PaintList labelPaintList;

    private Paint defaultLabelPaint;

    private BooleanList createEntityList;

    private Boolean defaultCreateEntity;

    /**
     * Creates a new instance.
     */
    public PieSelectionAttributes() {
        this(true);
    }

    public PieSelectionAttributes(boolean allowNull) {
        this.sectionPaint = new PaintMap();
        this.defaultPaint = allowNull ? null : Color.BLACK;
        this.sectionOutlinePaint = new PaintMap();
        this.defaultOutlinePaint = allowNull ? null : Color.BLACK;
        this.sectionOutlineStroke = new StrokeMap();
        this.defaultOutlineStroke = allowNull ? null : new BasicStroke(2.0f);
    }

    public boolean getAllowNull() {
        return this.allowNull;
    }

    // PAINT

    protected Paint lookupSectionPaint(Comparable key) {
        Paint result = this.sectionPaint.getPaint(key);
        if (result == null) {
            result = this.defaultPaint;
        }
        return result;
    }

    public Paint getSectionPaint(Comparable key) {
        return this.sectionPaint.getPaint(key);
    }

    public void setSeriesPaint(Comparable key, Paint paint) {
        this.sectionPaint.put(key, paint);
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

    // OUTLINE PAINT

    protected Paint lookupSectionOutlinePaint(Comparable key) {
        Paint result = this.sectionOutlinePaint.getPaint(key);
        if (result == null) {
            result = this.defaultOutlinePaint;
        }
        return result;
    }

    public Paint getSectionOutlinePaint(Comparable key) {
        return this.sectionOutlinePaint.getPaint(key);
    }

    public void setSectionOutlinePaint(Comparable key, Paint paint) {
        this.sectionOutlinePaint.put(key, paint);
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

    protected Stroke lookupSectionOutlineStroke(Comparable key) {
        Stroke result = this.sectionOutlineStroke.getStroke(key);
        if (result == null) {
            result = this.defaultOutlineStroke;
        }
        return result;
    }

    public Stroke getSectionOutlineStroke(Comparable key) {
        return this.sectionOutlineStroke.getStroke(key);
    }

    public void setSectionOutlineStroke(Comparable key, Stroke stroke) {
        this.sectionOutlineStroke.put(key, stroke);
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