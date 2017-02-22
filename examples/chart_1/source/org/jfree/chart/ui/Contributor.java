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
 * ----------------
 * Contributor.java
 * ----------------
 * (C) Copyright 2001-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 10-Dec-2001 : Version 1 (DG);
 * 28-Feb-2002 : Moved into package com.jrefinery.ui.about (DG);
 * 08-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 21-Jun-2007 : Removed JCommon dependencies (DG);
 *
 */

package org.jfree.chart.ui;

/**
 * A simple class representing a contributor to a software project.
 */
public class Contributor {

    /** The name. */
    private String name;

    /** The e-mail address. */
    private String email;

    /**
     * Creates a new contributor.
     *
     * @param name  the name.
     * @param email  the e-mail address.
     */
    public Contributor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Returns the contributor's name.
     *
     * @return the contributor's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the contributor's e-mail address.
     *
     * @return the contributor's e-mail address.
     */
    public String getEmail() {
        return this.email;
    }

}
