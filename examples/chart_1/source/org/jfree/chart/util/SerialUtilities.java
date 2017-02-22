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
 * --------------------
 * SerialUtilities.java
 * --------------------
 * (C) Copyright 2000-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Arik Levin;
 *
 * Changes
 * -------
 * 25-Mar-2003 : Version 1 (DG);
 * 18-Sep-2003 : Added capability to serialize GradientPaint (DG);
 * 26-Apr-2004 : Added read/writePoint2D() methods (DG);
 * 22-Feb-2005 : Added support for Arc2D - see patch 1147035 by Arik Levin (DG);
 * 29-Jul-2005 : Added support for AttributedString (DG);
 * 21-Jun-2007 : Copied from JCommon (DG);
 * 02-Jun-2008 : Fixed bug 1977609 in readShape() for GeneralPath (DG);
 * 10-Sep-2008 : Added readImage() and writeImage() methods (DG);
 *
 */

package org.jfree.chart.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;

/**
 * A class containing useful utility methods relating to serialization.
 */
public class SerialUtilities {

    /**
     * Private constructor prevents object creation.
     */
    private SerialUtilities() {
    }

    /**
     * Returns <code>true</code> if a class implements <code>Serializable</code>
     * and <code>false</code> otherwise.
     *
     * @param c  the class.
     *
     * @return A boolean.
     */
    public static boolean isSerializable(Class c) {
        return Serializable.class.isAssignableFrom(c);
    }

    /**
     * Reads a <code>Paint</code> object that has been serialised by the
     * {@link SerialUtilities#writePaint(Paint, ObjectOutputStream)} method.
     *
     * @param stream  the input stream (<code>null</code> not permitted).
     *
     * @return The paint object (possibly <code>null</code>).
     *
     * @throws IOException  if there is an I/O problem.
     * @throws ClassNotFoundException  if there is a problem loading a class.
     */
    public static Paint readPaint(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        Paint result = null;
        boolean isNull = stream.readBoolean();
        if (!isNull) {
            Class c = (Class) stream.readObject();
            if (isSerializable(c)) {
                result = (Paint) stream.readObject();
            }
            else if (c.equals(GradientPaint.class)) {
                float x1 = stream.readFloat();
                float y1 = stream.readFloat();
                Color c1 = (Color) stream.readObject();
                float x2 = stream.readFloat();
                float y2 = stream.readFloat();
                Color c2 = (Color) stream.readObject();
                boolean isCyclic = stream.readBoolean();
                result = new GradientPaint(x1, y1, c1, x2, y2, c2, isCyclic);
            }
        }
        return result;

    }

    /**
     * Serialises a <code>Paint</code> object.
     *
     * @param paint  the paint object (<code>null</code> permitted).
     * @param stream  the output stream (<code>null</code> not permitted).
     *
     * @throws IOException if there is an I/O error.
     */
    public static void writePaint(Paint paint, ObjectOutputStream stream)
            throws IOException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (paint != null) {
            stream.writeBoolean(false);
            stream.writeObject(paint.getClass());
            if (paint instanceof Serializable) {
                stream.writeObject(paint);
            }
            else if (paint instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) paint;
                stream.writeFloat((float) gp.getPoint1().getX());
                stream.writeFloat((float) gp.getPoint1().getY());
                stream.writeObject(gp.getColor1());
                stream.writeFloat((float) gp.getPoint2().getX());
                stream.writeFloat((float) gp.getPoint2().getY());
                stream.writeObject(gp.getColor2());
                stream.writeBoolean(gp.isCyclic());
            }
        }
        else {
            stream.writeBoolean(true);
        }

    }

    /**
     * Reads a <code>Stroke</code> object that has been serialised by the
     * {@link SerialUtilities#writeStroke(Stroke, ObjectOutputStream)} method.
     *
     * @param stream  the input stream (<code>null</code> not permitted).
     *
     * @return The stroke object (possibly <code>null</code>).
     *
     * @throws IOException  if there is an I/O problem.
     * @throws ClassNotFoundException  if there is a problem loading a class.
     */
    public static Stroke readStroke(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        Stroke result = null;
        boolean isNull = stream.readBoolean();
        if (!isNull) {
            Class c = (Class) stream.readObject();
            if (c.equals(BasicStroke.class)) {
                float width = stream.readFloat();
                int cap = stream.readInt();
                int join = stream.readInt();
                float miterLimit = stream.readFloat();
                float[] dash = (float[]) stream.readObject();
                float dashPhase = stream.readFloat();
                result = new BasicStroke(width, cap, join, miterLimit, dash,
                        dashPhase);
            }
            else {
                result = (Stroke) stream.readObject();
            }
        }
        return result;

    }

    /**
     * Serialises a <code>Stroke</code> object.  This code handles the
     * <code>BasicStroke</code> class which is the only <code>Stroke</code>
     * implementation provided by the JDK (and isn't directly
     * <code>Serializable</code>).
     *
     * @param stroke  the stroke object (<code>null</code> permitted).
     * @param stream  the output stream (<code>null</code> not permitted).
     *
     * @throws IOException if there is an I/O error.
     */
    public static void writeStroke(Stroke stroke, ObjectOutputStream stream)
            throws IOException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (stroke != null) {
            stream.writeBoolean(false);
            if (stroke instanceof BasicStroke) {
                BasicStroke s = (BasicStroke) stroke;
                stream.writeObject(BasicStroke.class);
                stream.writeFloat(s.getLineWidth());
                stream.writeInt(s.getEndCap());
                stream.writeInt(s.getLineJoin());
                stream.writeFloat(s.getMiterLimit());
                stream.writeObject(s.getDashArray());
                stream.writeFloat(s.getDashPhase());
            }
            else {
                stream.writeObject(stroke.getClass());
                stream.writeObject(stroke);
            }
        }
        else {
            stream.writeBoolean(true);
        }
    }

    /**
     * Reads a <code>Shape</code> object that has been serialised by the
     * {@link #writeShape(Shape, ObjectOutputStream)} method.
     *
     * @param stream  the input stream (<code>null</code> not permitted).
     *
     * @return The shape object (possibly <code>null</code>).
     *
     * @throws IOException  if there is an I/O problem.
     * @throws ClassNotFoundException  if there is a problem loading a class.
     */
    public static Shape readShape(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        Shape result = null;
        boolean isNull = stream.readBoolean();
        if (!isNull) {
            Class c = (Class) stream.readObject();
            if (c.equals(Line2D.class)) {
                double x1 = stream.readDouble();
                double y1 = stream.readDouble();
                double x2 = stream.readDouble();
                double y2 = stream.readDouble();
                result = new Line2D.Double(x1, y1, x2, y2);
            }
            else if (c.equals(Rectangle2D.class)) {
                double x = stream.readDouble();
                double y = stream.readDouble();
                double w = stream.readDouble();
                double h = stream.readDouble();
                result = new Rectangle2D.Double(x, y, w, h);
            }
            else if (c.equals(Ellipse2D.class)) {
                double x = stream.readDouble();
                double y = stream.readDouble();
                double w = stream.readDouble();
                double h = stream.readDouble();
                result = new Ellipse2D.Double(x, y, w, h);
            }
            else if (c.equals(Arc2D.class)) {
                double x = stream.readDouble();
                double y = stream.readDouble();
                double w = stream.readDouble();
                double h = stream.readDouble();
                double as = stream.readDouble(); // Angle Start
                double ae = stream.readDouble(); // Angle Extent
                int at = stream.readInt();       // Arc type
                result = new Arc2D.Double(x, y, w, h, as, ae, at);
            }
            else if (c.equals(GeneralPath.class)) {
                GeneralPath gp = new GeneralPath();
                float[] args = new float[6];
                boolean hasNext = stream.readBoolean();
                while (!hasNext) {
                    int type = stream.readInt();
                    for (int i = 0; i < 6; i++) {
                        args[i] = stream.readFloat();
                    }
                    switch (type) {
                        case PathIterator.SEG_MOVETO :
                            gp.moveTo(args[0], args[1]);
                            break;
                        case PathIterator.SEG_LINETO :
                            gp.lineTo(args[0], args[1]);
                            break;
                        case PathIterator.SEG_CUBICTO :
                            gp.curveTo(args[0], args[1], args[2],
                                    args[3], args[4], args[5]);
                            break;
                        case PathIterator.SEG_QUADTO :
                            gp.quadTo(args[0], args[1], args[2], args[3]);
                            break;
                        case PathIterator.SEG_CLOSE :
                            gp.closePath();
                            break;
                        default :
                            throw new RuntimeException(
                                    "JFreeChart - No path exists");
                    }
                    gp.setWindingRule(stream.readInt());
                    hasNext = stream.readBoolean();
                }
                result = gp;
            }
            else {
                result = (Shape) stream.readObject();
            }
        }
        return result;

    }

    /**
     * Serialises a <code>Shape</code> object.
     *
     * @param shape  the shape object (<code>null</code> permitted).
     * @param stream  the output stream (<code>null</code> not permitted).
     *
     * @throws IOException if there is an I/O error.
     */
    public static void writeShape(Shape shape, ObjectOutputStream stream)
            throws IOException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (shape != null) {
            stream.writeBoolean(false);
            if (shape instanceof Line2D) {
                Line2D line = (Line2D) shape;
                stream.writeObject(Line2D.class);
                stream.writeDouble(line.getX1());
                stream.writeDouble(line.getY1());
                stream.writeDouble(line.getX2());
                stream.writeDouble(line.getY2());
            }
            else if (shape instanceof Rectangle2D) {
                Rectangle2D rectangle = (Rectangle2D) shape;
                stream.writeObject(Rectangle2D.class);
                stream.writeDouble(rectangle.getX());
                stream.writeDouble(rectangle.getY());
                stream.writeDouble(rectangle.getWidth());
                stream.writeDouble(rectangle.getHeight());
            }
            else if (shape instanceof Ellipse2D) {
                Ellipse2D ellipse = (Ellipse2D) shape;
                stream.writeObject(Ellipse2D.class);
                stream.writeDouble(ellipse.getX());
                stream.writeDouble(ellipse.getY());
                stream.writeDouble(ellipse.getWidth());
                stream.writeDouble(ellipse.getHeight());
            }
            else if (shape instanceof Arc2D) {
                Arc2D arc = (Arc2D) shape;
                stream.writeObject(Arc2D.class);
                stream.writeDouble(arc.getX());
                stream.writeDouble(arc.getY());
                stream.writeDouble(arc.getWidth());
                stream.writeDouble(arc.getHeight());
                stream.writeDouble(arc.getAngleStart());
                stream.writeDouble(arc.getAngleExtent());
                stream.writeInt(arc.getArcType());
            }
            else if (shape instanceof GeneralPath) {
                stream.writeObject(GeneralPath.class);
                PathIterator pi = shape.getPathIterator(null);
                float[] args = new float[6];
                stream.writeBoolean(pi.isDone());
                while (!pi.isDone()) {
                    int type = pi.currentSegment(args);
                    stream.writeInt(type);
                    // TODO: could write this to only stream the values
                    // required for the segment type
                    for (int i = 0; i < 6; i++) {
                        stream.writeFloat(args[i]);
                    }
                    stream.writeInt(pi.getWindingRule());
                    pi.next();
                    stream.writeBoolean(pi.isDone());
                }
            }
            else {
                stream.writeObject(shape.getClass());
                stream.writeObject(shape);
            }
        }
        else {
            stream.writeBoolean(true);
        }
    }

    /**
     * Reads a <code>Point2D</code> object that has been serialised by the
     * {@link #writePoint2D(Point2D, ObjectOutputStream)} method.
     *
     * @param stream  the input stream (<code>null</code> not permitted).
     *
     * @return The point object (possibly <code>null</code>).
     *
     * @throws IOException  if there is an I/O problem.
     */
    public static Point2D readPoint2D(ObjectInputStream stream)
        throws IOException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        Point2D result = null;
        boolean isNull = stream.readBoolean();
        if (!isNull) {
            double x = stream.readDouble();
            double y = stream.readDouble();
            result = new Point2D.Double(x, y);
        }
        return result;

    }

    /**
     * Serialises a <code>Point2D</code> object.
     *
     * @param p  the point object (<code>null</code> permitted).
     * @param stream  the output stream (<code>null</code> not permitted).
     *
     * @throws IOException if there is an I/O error.
     */
    public static void writePoint2D(Point2D p, ObjectOutputStream stream)
            throws IOException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (p != null) {
            stream.writeBoolean(false);
            stream.writeDouble(p.getX());
            stream.writeDouble(p.getY());
        }
        else {
            stream.writeBoolean(true);
        }
    }

    /**
     * Reads a <code>AttributedString</code> object that has been serialised by
     * the {@link SerialUtilities#writeAttributedString(AttributedString,
     * ObjectOutputStream)} method.
     *
     * @param stream  the input stream (<code>null</code> not permitted).
     *
     * @return The attributed string object (possibly <code>null</code>).
     *
     * @throws IOException  if there is an I/O problem.
     * @throws ClassNotFoundException  if there is a problem loading a class.
     */
    public static AttributedString readAttributedString(
            ObjectInputStream stream) throws IOException,
            ClassNotFoundException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        AttributedString result = null;
        boolean isNull = stream.readBoolean();
        if (!isNull) {
            // read string and attributes then create result
            String plainStr = (String) stream.readObject();
            result = new AttributedString(plainStr);
            char c = stream.readChar();
            int start = 0;
            while (c != CharacterIterator.DONE) {
                int limit = stream.readInt();
                Map atts = (Map) stream.readObject();
                result.addAttributes(atts, start, limit);
                start = limit;
                c = stream.readChar();
            }
        }
        return result;
    }

    /**
     * Serialises an <code>AttributedString</code> object.
     *
     * @param as  the attributed string object (<code>null</code> permitted).
     * @param stream  the output stream (<code>null</code> not permitted).
     *
     * @throws IOException if there is an I/O error.
     */
    public static void writeAttributedString(AttributedString as,
            ObjectOutputStream stream) throws IOException {

        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (as != null) {
            stream.writeBoolean(false);
            AttributedCharacterIterator aci = as.getIterator();
            // build a plain string from aci
            // then write the string
            StringBuffer plainStr = new StringBuffer();
            char current = aci.first();
            while (current != CharacterIterator.DONE) {
                plainStr = plainStr.append(current);
                current = aci.next();
            }
            stream.writeObject(plainStr.toString());

            // then write the attributes and limits for each run
            current = aci.first();
            int begin = aci.getBeginIndex();
            while (current != CharacterIterator.DONE) {
                // write the current character - when the reader sees that this
                // is not CharacterIterator.DONE, it will know to read the
                // run limits and attributes
                stream.writeChar(current);

                // now write the limit, adjusted as if beginIndex is zero
                int limit = aci.getRunLimit();
                stream.writeInt(limit - begin);

                // now write the attribute set
                Map atts = new HashMap(aci.getAttributes());
                stream.writeObject(atts);
                current = aci.setIndex(limit);
            }
            // write a character that signals to the reader that all runs
            // are done...
            stream.writeChar(CharacterIterator.DONE);
        }
        else {
            // write a flag that indicates a null
            stream.writeBoolean(true);
        }

    }

    /**
     * Reads a boolean from the specified stream and, if the boolean is
     * <code>false</code>, reads a PNG byte stream, then returns  the
     * corresponding image.  If the boolean is <code>true</code>, this
     * signifies that the original streamed image was <code>null</code>, and
     * so the method returns <code>null</code>.
     *
     * @param stream  the input stream (<code>null</code> not permitted).
     *
     * @return An image, or <code>null</code>.
     *
     * @throws IOException if there is an input/output problem.
     *
     * @since 1.2.0
     */
    public static Image readImage(ObjectInputStream stream)
            throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        BufferedImage result = null;
        boolean isNull = stream.readBoolean();
        if (!isNull) {
            result = ImageIO.read(stream);
        }
        return result;
    }

    /**
     * Writes an image to the stream in PNG format.
     *
     * @param image  the image.
     * @param stream  the output stream.
     *
     * @throws IOException if there is an input/output error.
     *
     * @since 1.2.0
     */
    public static void writeImage(Image image, ObjectOutputStream stream)
            throws IOException {
        BufferedImage bi = null;
        if (image instanceof BufferedImage) {
            bi = (BufferedImage) image;
        }
        else {
            bi = new BufferedImage(image.getWidth(null), image.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi.createGraphics();
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
        }
        EncoderUtil.writeBufferedImage(bi, ImageFormat.PNG, stream);
    }

}

