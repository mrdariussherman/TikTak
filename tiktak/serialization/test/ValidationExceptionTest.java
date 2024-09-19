/************************************************
 *
 * Author: Michael Prescott and Darius Sherman
 * Assignment: Program 1 Test
 * Class: CSI 4321
 *
 ************************************************/
package tiktak.serialization.test;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import tiktak.serialization.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ValidationExceptionTest {

    @Test
    @DisplayName("[Valid] Test Validation Exception " +
            "Constructor with Cause: Check Attributes")
    public void testValidationExceptionConstructorCause() {
        Exception e = new Exception("test");
        ValidationException v = new ValidationException("test1", e, "test2");
        assertEquals("test1", v.getMessage());
        assertEquals(e, v.getCause());
        assertEquals("test2", v.getBadToken());
    }

    @Test
    @DisplayName("[Valid] Test Validation Exception " +
            "Constructor without Cause: Check Attributes")
    public void testValidationExceptionConstructorNoCause() {
        ValidationException v = new ValidationException("test1", "test2");
        assertEquals("test1", v.getMessage());
        assertEquals("test2", v.getBadToken());
    }
}
