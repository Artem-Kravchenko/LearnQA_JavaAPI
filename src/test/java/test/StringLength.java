package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringLength {
    String name = "ABC";

    @Test
    public void testStringLength(){
        assertTrue(name.length() > 15, "String length less than 15 symbols");
        }

}

