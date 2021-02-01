package test.java.io.github.General;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import main.java.io.github.General.AngleUtils;
import main.java.io.github.General.Position;

public class AngleUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        final double delta = 0.00001;
        assertEquals(-90, AngleUtils.angle(new Position(1, 3), new Position(1, 1), new Position(4, 3)), delta);
        assertEquals(71.56505, AngleUtils.angle(new Position(5, 4), new Position(3, 2), new Position(6, 2)), delta);
        assertEquals(0, AngleUtils.angle(new Position(4, 2), new Position(2, 2), new Position(3,2)), delta);
        assertEquals(180, AngleUtils.angle(new Position(4, 6), new Position(2, 3), new Position(8, 12)), delta);
    }

}
