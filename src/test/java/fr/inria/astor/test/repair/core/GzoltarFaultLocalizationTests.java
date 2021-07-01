package fr.inria.astor.test.repair.core;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class GzoltarFaultLocalizationTests {

    @Test
    public void testParseOchiaiLine_LineIsHeader_shouldReturnNull(){
        GZoltarFaultLocalization testObject = new GZoltarFaultLocalization();

        String testLine = "name;suspiciousness_value";

        SuspiciousCode result = testObject.parseLineOchiaiLine(testLine);

        assertNull(result);
    }

    @Test
    public void testParseOchiaiLine_LineIsNormal_shouldReturnSuspCode(){
        GZoltarFaultLocalization testObject = new GZoltarFaultLocalization();

        String testLine = "nl.tudelft.mutated_rers$Problem1_ESTest#test002():39;1.0";

        SuspiciousCode result = testObject.parseLineOchiaiLine(testLine);

        assertNotNull(result);

        assertEquals(1.0,result.getSuspiciousValue(),0.000001);
        assertEquals("test002()",result.getMethodName());
    }

    @Tag("Regression")
    @Test
    public void testParseOchiaiLine_LineHasMultipleHashtags_shouldReturnSuspCode(){
        GZoltarFaultLocalization testObject = new GZoltarFaultLocalization();

        String testLine = "org.apache.commons.math.complex$ComplexTest#OuterMethod(double)#TestComplex(double,double):962;0.0";

        SuspiciousCode result = testObject.parseLineOchiaiLine(testLine);

        assertNotNull(result);
        assertEquals(0.0,result.getSuspiciousValue(),0.000001);
        assertEquals("OuterMethod(double)",result.getMethodName());
        assertEquals("ComplexTest",result.getClassName());
    }

    @Tag("Regression")
    @Test
    public void testParseOchiaiLine_LineHasMultipleDollars_shouldReturnSuspCode(){
        GZoltarFaultLocalization testObject = new GZoltarFaultLocalization();

        String testLine = "org.apache.commons.math.complex$ComplexTest$TestComplex#ComplexTest(double,double):962;0.0";

        SuspiciousCode result = testObject.parseLineOchiaiLine(testLine);

        assertNotNull(result);
        assertEquals(0.0,result.getSuspiciousValue(),0.000001);
        assertEquals("ComplexTest(double,double)",result.getMethodName());
        assertEquals("ComplexTest",result.getClassName());
    }

    @Tag("Regression")
    @Test
    public void testParseOchiaiLine_LineHasMultipleDollarsAndMultipleHashTags_shouldReturnSuspCode(){
        GZoltarFaultLocalization testObject = new GZoltarFaultLocalization();

        String testLine = "org.apache.commons.math.complex$ComplexTest$TestComplex#OuterMethod(double)#TestComplex(double,double):962;0.0";

        SuspiciousCode result = testObject.parseLineOchiaiLine(testLine);

        assertNotNull(result);
        assertEquals(0.0,result.getSuspiciousValue(),0.000001);
        assertEquals("OuterMethod(double)",result.getMethodName());
        assertEquals("ComplexTest",result.getClassName());
    }

    @Tag("Regression")
    @Test
    public void testParseOchiaiLine_LineHasMultipleHashTagsAndDollarsAfterEachOther_shouldReturnSuspCode(){
        GZoltarFaultLocalization testObject = new GZoltarFaultLocalization();

        String testLine = "org.apache.commons.math.complex$ComplexTest$TestComplex#ComplexTest$TestComplex(double,double):962;0.0";

        SuspiciousCode result = testObject.parseLineOchiaiLine(testLine);

        assertNotNull(result);
        assertEquals(0.0,result.getSuspiciousValue(),0.000001);
        assertEquals("ComplexTest",result.getMethodName());
        assertEquals("ComplexTest",result.getClassName());
    }

    @Test
    public void testParseOchiaiLine_LineHasDifferentFormat_shouldReturnNull(){
        GZoltarFaultLocalization testObject = new GZoltarFaultLocalization();

        String testLine = "org.apache.commons.math.complex$a:962#2;0.0;4";

        SuspiciousCode result = testObject.parseLineOchiaiLine(testLine);

        assertNull(result);
    }

    @Test
    public void testFirstAfter_nullString_returnsNull(){
        String input = null;
        String markup = "$";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertNull(result);
    }

    @Test
    public void testFirstAfter_emptyString_returnsNull(){
        String input = "";
        String markup = "$";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertNull(result);
    }

    @Test
    public void testFirstAfter_nullParameters_returnsNull(){
        String input = "a$Something#string()";
        String markup = null;

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertNull(result);
    }

    @Test
    public void testFirstAfter_findClassName_ReturnsClassName(){
        String input = "a$Something#string()";
        String markup = "$";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertEquals("Something",result);
    }

    @Test
    public void testFirstAfter_findClassName_TwoClassNamesInInput_ReturnsFirst(){
        String input = "a$Something$SomethingElse#string()";
        String markup = "$";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertEquals("Something",result);
    }

    @Test
    public void testFirstAfter_findClassName_MultipleClassNamesInInput_ReturnsFirst(){
        String input = "package$Something$SomethingElse$More$EvenMore#string()";
        String markup = "$";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertEquals("Something",result);
    }

    @Test
    public void testFirstAfter_findMethodName_TwoMethodNamesInInput_ReturnsFirst(){
        String input = "package$Something#a()#b()";
        String markup = "#";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertEquals("a()",result);
    }

    @Test
    public void testFirstAfter_findMethodName_MultipleMethodNamesInInput_ReturnsFirst(){
        String input = "package$Something#a()#b()#c()#d()";
        String markup = "#";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertEquals("a()",result);
    }

    @Test
    public void testFirstAfter_ClassesInMethodsInClasses_LookForClasses_ReturnsFirst(){
        String input = "package$A#a()$B#b()$C#c()$D#d()";
        String markup = "#";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertEquals("a()",result);
    }

    @Test
    public void testFirstAfter_ClassesInMethodsInClasses2_LookForClasses_ReturnsFirst(){
        String input = "package$A#a()$B#b()$C#c()$D#d()";
        String markup = "$";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertEquals("A",result);
    }

    @Test
    public void testFirstAfter_ClassesInMethodsInClasses3_LookForClasses_ReturnsFirst(){
        String input = "package$A$A2#a()$B#b()$C#c()$D#d()";
        String markup = "$";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertEquals("A",result);
    }

    @Test
    public void testFirstAfter_ClassesInMethodsInClasses4_LookForClasses_ReturnsFirst(){
        String input = "package$A$A2#a()#a2()#a3()$B#b()$C#c()$D#d()";
        String markup = "#";

        String result = GZoltarFaultLocalization.firstAfter(input,markup);

        assertEquals("a()",result);
    }
}
