package ui.workout.notifications;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests that NotificationManager follows the singleton pattern correctly.
 *
 * @author Nanorta Amwar
 * @version 0.1.0
 * @since 4/22/2026
 */

public class NotificationManagerTest {

    // Verifies that getInstance() always returns the same object
    @Test
    void getInstanceReturnsSameSingleton() {
        // Get two instances of the manager
        NotificationManager first = NotificationManager.getInstance();
        NotificationManager second = NotificationManager.getInstance();

        // Ensure both references point to the same object
        assertNotNull(first);
        assertSame(first, second);
    }
}
