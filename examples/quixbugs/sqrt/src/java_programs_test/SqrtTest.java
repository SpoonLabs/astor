package java_programs_test;


public class SqrtTest {
    @org.junit.Test(timeout = 2000)
    public void test_0() throws java.lang.Exception {
        float result = java_programs.SQRT.sqrt((float)2,(float)0.01);
        org.junit.Assert.assertEquals( (float) 1.4166666666666665, result);
    }

    @org.junit.Test(timeout = 2000)
    public void test_1() throws java.lang.Exception {
        float result = java_programs.SQRT.sqrt((float)2,(float)0.5);
        org.junit.Assert.assertEquals( (float) 1.5, result);
    }

    @org.junit.Test(timeout = 2000)
    public void test_2() throws java.lang.Exception {
        float result = java_programs.SQRT.sqrt((float)2,(float)0.3);
        org.junit.Assert.assertEquals( (float) 1.5, result);
    }

    @org.junit.Test(timeout = 2000)
    public void test_3() throws java.lang.Exception {
        float result = java_programs.SQRT.sqrt((float)4,(float)0.2);
        org.junit.Assert.assertEquals( (float) 2, result);
    }

    @org.junit.Test(timeout = 2000)
    public void test_4() throws java.lang.Exception {
        float result = java_programs.SQRT.sqrt((float)27,(float)0.01);
        org.junit.Assert.assertEquals( (float) 5.196164639727311, result);
    }

    @org.junit.Test(timeout = 2000)
    public void test_5() throws java.lang.Exception {
        float result = java_programs.SQRT.sqrt((float)33,(float)0.05);
        org.junit.Assert.assertEquals( (float) 5.744627526262464, result);
    }

    @org.junit.Test(timeout = 2000)
    public void test_6() throws java.lang.Exception {
        float result = java_programs.SQRT.sqrt((float)170,(float)0.03);
        org.junit.Assert.assertEquals( (float) 13.038404876679632, result);
    }
}

