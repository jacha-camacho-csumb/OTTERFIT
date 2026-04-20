package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UserTest
 * <p>
 * Description:
 * Tests basic construction and validation logic for the User model.
 * @author rcwav
 * @since 4/18/2026
 */
class UserTest {
    @Test
    void constructorSetsFieldsCorrectly() {
        User user = new User(1, "otter", "otter@csumb.edu");

        assertEquals(1, user.userId);
        assertEquals("otter", user.username);
        assertEquals("otter@csumb.edu", user.email);
    }

    @Test
    void isValidReturnsTrueForValidInput() {
        boolean result = User.isValid("otter", "otter@csumb.edu", "password");

        assertTrue(result);
    }

    @Test
    void isValidFailsForBlankUsername() {
        boolean result = User.isValid("   ", "test@test.com", "password");

        assertFalse(result);
    }

    @Test
    void isValidFailsForNullUsername() {
        boolean result = User.isValid(null, "test@test.com", "password");

        assertFalse(result);
    }

    @Test
    void isValidFailsForBlankPassword() {
        boolean result = User.isValid("user", "test@test.com", "   ");

        assertFalse(result);
    }

    @Test
    void isValidFailsForNullPassword() {
        boolean result = User.isValid("user", "test@test.com", null);

        assertFalse(result);
    }

    @Test
    void isValidFailsForInvalidEmail() {
        boolean result = User.isValid("user", "invalid-email", "password");

        assertFalse(result);
    }

    @Test
    void isValidFailsForNullEmail() {
        boolean result = User.isValid("user", null, "password");

        assertFalse(result);
    }
}