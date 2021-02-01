package test.java.io.github.General;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import main.java.io.github.General.Plan;
import main.java.io.github.General.Position;
import main.java.io.github.General.Segment;

public class SegmentTest {
    
    private Segment sut;
    Plan<Boolean> carte;

    @Before
    public void setUp() throws Exception {
        carte = new Plan<Boolean>(10, 10, false);
        carte.modifierElement(3, 4, true);
    }

    @Test
    public void test() {
        final double delta = 0.00001;
        
        sut = new Segment(new Position(0, 0), new Position(5, 0), carte);
        assertEquals(5, sut.taille(), delta);
        assertFalse(sut.toucheUnObstacle());
        
        sut = new Segment(new Position(1, 0), new Position(5, 6), carte);
        assertEquals(7.21110, sut.taille(), delta);
        assertFalse(sut.toucheUnObstacle());
        
        assertFalse(new Segment(new Position(4, 5), new Position(5, 3), carte).toucheUnObstacle());
        
        assertTrue(new Segment(new Position(2, 3), new Position(6, 3), carte).toucheUnObstacle());
        
        assertTrue(new Segment(new Position(3, 5), new Position(5, 1), carte).toucheUnObstacle());
        assertTrue(new Segment(new Position(5, 1), new Position(3, 5), carte).toucheUnObstacle());
    }
    
    // TODO add tests for corner cases to force algorithm to adopt a thicker line
}
