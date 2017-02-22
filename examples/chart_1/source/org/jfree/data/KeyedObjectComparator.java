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
 * --------------------------
 * KeyedObjectComparator.java
 * --------------------------
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes:
 * --------
 * 01-Jul-2009 : Version 1 (DG);
 *
 */

package org.jfree.data;

import java.util.Comparator;

import org.jfree.chart.util.SortOrder;

/**
 * A utility class that can compare and order two {@link KeyedObject} instances
 * and sort them into ascending or descending order by key or by object.
 */
public class KeyedObjectComparator implements Comparator {

    /** The comparator type. */
    private KeyedObjectComparatorType type;

    /** The sort order. */
    private SortOrder order;

    /**
     * Creates a new comparator.
     *
     * @param type  the type (<code>BY_KEY</code> or <code>BY_OBJECT</code>,
     *              <code>null</code> not permitted).
     * @param order  the order (<code>null</code> not permitted).
     */
    public KeyedObjectComparator(KeyedObjectComparatorType type,
                                 SortOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        this.type = type;
        this.order = order;
    }

    /**
     * Returns the type.
     *
     * @return The type (never <code>null</code>).
     */
    public KeyedObjectComparatorType getType() {
        return this.type;
    }

    /**
     * Returns the sort order.
     *
     * @return The sort order (never <code>null</code>).
     */
    public SortOrder getOrder() {
        return this.order;
    }

    /**
     * Compares two {@link KeyedValue} instances and returns an
     * <code>int</code> that indicates the relative order of the two objects.
     *
     * @param o1  object 1.
     * @param o2  object 2.
     *
     * @return An int indicating the relative order of the objects.
     */
    public int compare(Object o1, Object o2) {

        if (o2 == null) {
            return -1;
        }
        if (o1 == null) {
            return 1;
        }

        KeyedObject ko1 = (KeyedObject) o1;
        KeyedObject ko2 = (KeyedObject) o2;

        if (this.type == KeyedObjectComparatorType.BY_KEY) {
            if (this.order.equals(SortOrder.ASCENDING)) {
                return ko1.getKey().compareTo(ko2.getKey());
            }
            else if (this.order.equals(SortOrder.DESCENDING)) {
                return ko2.getKey().compareTo(ko1.getKey());
            }
            else {
                throw new IllegalArgumentException("Unrecognised sort order.");
            }
        }
        else if (this.type == KeyedObjectComparatorType.BY_VALUE) {
            Object n1 = ko1.getObject();
            Object n2 = ko2.getObject();
            Comparable c1 = "FALLBACK";
            if (n1 instanceof Comparable) {
                c1 = (Comparable) n1;
            }
            Comparable c2 = "FALLBACK";
            if (n2 instanceof Comparable) {
                c2 = (Comparable) n2;
            }
            if (n2 == null) {
                return -1;
            }
            if (n1 == null) {
                return 1;
            }
            if (this.order.equals(SortOrder.ASCENDING)) {
                return c1.compareTo(c2);
            }
            else if (this.order.equals(SortOrder.DESCENDING)) {
                return c2.compareTo(c1);
            }
            else {
                throw new IllegalArgumentException("Unrecognised sort order.");
            }
        }
        else {
            throw new IllegalArgumentException("Unrecognised type.");
        }
    }

}
