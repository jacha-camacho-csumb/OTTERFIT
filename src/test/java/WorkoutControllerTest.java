import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ui.workout.WorkoutController;

/**
 * Tests basic functionality of the WorkoutController class
 *
 * @author Nanorta Amwar
 * @version 0.1.0
 * @since 4/14/2026
 */


public class WorkoutControllerTest {
    /**
     * Tests that the WorkoutController object is created successfully
     *
     * @return void
     */
    @Test
    void testWorkoutControllerExists() {
            WorkoutController controller = new WorkoutController();
            assertNotNull(controller);

    }
}
