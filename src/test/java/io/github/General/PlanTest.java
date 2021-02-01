package test.java.io.github.General;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import main.java.io.github.General.Plan;
import main.java.io.github.General.Position;

public class PlanTest {
    
    private Plan<Boolean> sut;
    private final int ySize = 30, xSize = 25;
    private final boolean defaultValue = false;
    private final boolean otherValue = true;
    
    private void assertListsContainSamePositions(List<Position> list1, List<Position> list2) {
        assertEquals(list1.size(), list2.size());
        assertTrue(list1.containsAll(list2));
        assertTrue(list2.containsAll(list1));
    }
    
    @Before
    public void setUp() {
        sut = new Plan<>(ySize, xSize, defaultValue);
    }

    @Test
    public void test() {
        assertFalse(sut.contient(new Position(-1, 0)));
        assertFalse(sut.contient(new Position(0, -1)));
        assertFalse(sut.contient(new Position(0, ySize)));
        assertFalse(sut.contient(new Position(xSize, 0)));
        assertTrue(sut.contient(new Position(0, 0)));
        assertTrue(sut.contient(new Position(3, 4)));
        assertTrue(sut.contient(new Position(xSize - 1, ySize - 1)));
        
        assertEquals(sut.recevoirElement(5, 3), defaultValue);
        sut.modifierElement(5, 3, otherValue);
        assertEquals(sut.recevoirElement(5, 3), otherValue);
        assertEquals(sut.recevoirElement(2, 4), defaultValue);
        
        assertEquals(defaultValue, sut.recevoirElement(-1, 4));
        sut.modifierValeurParDefaut(otherValue);
        assertEquals(otherValue, sut.recevoirElement(-1, 4));

        assertListsContainSamePositions(Arrays.asList(sut.voisinsDe(new Position(0, 0))),
                Arrays.asList(new Position(0, 0), new Position(0, 1), new Position(1, 0))); // TODO Remove Position(0, 0)
        
        assertListsContainSamePositions(Arrays.asList(sut.voisinsDe(new Position(0, 1))),
                Arrays.asList(new Position(0, 1), new Position(0, 0), new Position(1, 1), new Position(0, 2)));
        
        assertListsContainSamePositions(Arrays.asList(sut.voisinsDe(new Position(2, 3))),
                Arrays.asList(new Position(2, 3), new Position(1, 3), new Position(2, 2),
                        new Position(3, 3), new Position(2, 4)));
    }

}
