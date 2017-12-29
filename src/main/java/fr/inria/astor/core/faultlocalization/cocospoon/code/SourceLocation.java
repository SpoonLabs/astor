/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.astor.core.faultlocalization.cocospoon.code;


import java.io.File;
import java.io.Serializable;

/**
 * @author Favio D. DeMarco
 */
public final class SourceLocation implements Serializable {

    private static final long serialVersionUID = -4580346333924562425L;
    private final String containingClassName;

    private final int lineNumber;

    private int beginSource;
    private int endSource;

    public int getBeginSource() {
        return beginSource;
    }

    public void setSourceStart(int beginSource) {
        this.beginSource = beginSource;
    }

    public int getEndSource() {
        return endSource;
    }

    public void setSourceEnd(int endSource) {
        this.endSource = endSource;
    }



    /**
     * @param containingClassName
     * @param lineNumber
     */
    public SourceLocation(final String containingClassName, final int lineNumber) {
        this.containingClassName = containingClassName;
        this.lineNumber = lineNumber;
    }

    /**
     * @return the containingClassName
     */
    public String getContainingClassName() {
        return containingClassName;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    public String getRootClassName() {
        int inertTypeIndex = containingClassName.indexOf('$');
        if (inertTypeIndex > 0) {
            return containingClassName.substring(0, inertTypeIndex);
        }
        return containingClassName;
    }

    public File getSourceFile(final File sourceFolder) {
        if (sourceFolder.isFile()) {
            return sourceFolder;
        }
        String pathToJavaFile = getRootClassName().replace('.', File.separatorChar);
        return new File(sourceFolder.getAbsolutePath() + '/' + pathToJavaFile + ".java");
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("SourceLocation %s:%d", containingClassName, lineNumber);
    }

    @Override
    public int hashCode() {
        int result = containingClassName != null ? containingClassName.hashCode() : 0;
        result = 31 * result + lineNumber;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceLocation that = (SourceLocation) o;

        if (lineNumber != that.lineNumber) return false;
        return !(containingClassName != null ? !containingClassName.equals(that.containingClassName) : that.containingClassName != null);
    }
}
