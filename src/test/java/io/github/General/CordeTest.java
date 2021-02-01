package test.java.io.github.General;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.Test;

import main.java.io.github.General.Corde;
import main.java.io.github.General.Plan;
import main.java.io.github.General.Position;
import main.java.io.github.General.Segment;

public class CordeTest {
    
    private Corde sut;
    private Plan<Boolean> carte;

    @Before
    public void setUp() {
        carte = new Plan<Boolean>(10, 10, false);
    }
    
    private void setObstacles() {
        carte.modifierElement(0, 4, true);
        carte.modifierElement(1, 4, true);
        carte.modifierElement(2, 4, true);
    }
    
    @Test
    public void testAngleWithoutObstacles() {
        sut = new Corde(carte, new Position[] {new Position(1, 1), new Position(2, 5), new Position(7, 0)});
        assertArrayEquals(new Segment[] {new Segment(1, 1, 7, 0, carte)}, sut.lissage());
    }
    
    @Test
    public void testStickToObstacles() {
        setObstacles();
        
        final Position[] inputPath = new Position[] {
                new Position(3, 0), new Position(3, 1), new Position(3, 2),
                new Position(3, 3), new Position(4, 3),
                new Position(5, 3), new Position(5, 2), new Position(5, 1),
                new Position(5, 0)
        };
        
        sut = new Corde(carte, inputPath);
        
        final Segment[] expectedSimplePath = new Segment[] {
                new Segment(3, 0, 3, 3, carte),
                new Segment(3, 3, 5, 2, carte),
                new Segment(5, 2, 5, 0, carte)
                };
        
        assertArrayEquals(expectedSimplePath, sut.premiereApproche());
        
        final Segment[] expectedSmoothedPath = new Segment[] {
                new Segment(3, 0, 3, 1, carte),
                new Segment(3, 1, 4, 3, carte),
                new Segment(4, 3, 5, 2, carte),
                new Segment(5, 2, 5, 0, carte)
                };
        
        assertArrayEquals(expectedSmoothedPath, sut.lissage());
    }

    @Test
    public void testQuiteCloseToObstacles() {
        setObstacles();
        
        final Position[] inputPath = new Position[] {
                new Position(3, 0), new Position(3, 1), new Position(2, 2),
                new Position(2, 3), new Position(3, 3), new Position(4, 3), new Position(5, 4),
                new Position(6, 4), new Position(6, 3), new Position(5, 2), new Position(5, 1),
                new Position(5, 0)
        };
        
        sut = new Corde(carte, inputPath);
        
        final Segment[] expectedSimplePath = new Segment[] {
                new Segment(3, 0, 3, 3, carte),
                new Segment(3, 3, 5, 2, carte),
                new Segment(5, 2, 5, 0, carte)
                };
        
        assertArrayEquals(expectedSimplePath, sut.premiereApproche());
        
        final Segment[] expectedSmoothedPath = new Segment[] {
                new Segment(3, 0, 3, 1, carte),
                new Segment(3, 1, 4, 3, carte),
                new Segment(4, 3, 5, 2, carte),
                new Segment(5, 2, 5, 0, carte)
                };
        
        assertArrayEquals(expectedSmoothedPath, sut.lissage());
    }

    @Test
    public void testNeedsSmooth() {
        setObstacles();
        
        final Position[] inputPath = new Position[] {
                new Position(0, 3), new Position(1, 3), new Position(2, 3), new Position(3, 3),
                new Position(4, 3), new Position(5, 3), new Position(6, 3), new Position(7, 3),
                new Position(7, 2), new Position(6, 1), new Position(5, 0)
        };
        
        sut = new Corde(carte, inputPath);
        
        final Segment[] expectedSimplePath = new Segment[] {
                new Segment(0, 3, 7, 3, carte),
                new Segment(7, 3, 5, 0, carte)
        };
        
        assertArrayEquals(expectedSimplePath, sut.premiereApproche());
        
        final Segment[] expectedSmoothedPath = new Segment[] {
                new Segment(0, 3, 3, 3, carte),
                new Segment(3, 3, 5, 2, carte),
                new Segment(5, 2, 5, 0, carte)
        };
        
        assertArrayEquals(expectedSmoothedPath, sut.lissage());
    }
}
