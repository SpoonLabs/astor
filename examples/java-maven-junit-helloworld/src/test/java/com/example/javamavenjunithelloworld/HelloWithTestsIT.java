package com.example.javamavenjunithelloworld;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration test for the HelloApp program.
 * <p>
 * An integration test verifies the workings of a complete program, a module, or a set of dependant classes.
 */
public class HelloWithTestsIT {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void before() {
        // By putting our own PrintStream in the place of the normal System.out,
        // the output produced by the application can be verified.
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    public void cleanUp() {
        // Restore the original System.out to prevent weirdness in any following tests.
        System.setOut(originalOut);
    }

    @Test
    public void doesItSayHelloTest() {
        String[] args = {"1"};
        HelloApp.main(args);

        assertThat(out.toString(), is(String.format("%s%s", Hello.HELLO, System.lineSeparator())));
    }

    @Test
    public void doesItSayHelloTest3() {
        String[] args = {"3"};
        HelloApp.main(args);

        // Hello
        // Hello
        // Hello
        String thrice = String.format("%1$s%2$s%1$s%2$s%1$s%2$s", Hello.HELLO, System.lineSeparator());
        assertThat(out.toString(), is(thrice));
    }
}
