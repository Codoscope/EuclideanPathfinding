package test.java.io.github.General;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import main.java.io.github.General.Distance;
import main.java.io.github.General.Itineraire;
import main.java.io.github.General.Plan;
import main.java.io.github.General.Position;

public class ItineraireTest {

    private Itineraire sut;

    private final int ySize = 14, xSize = 9;

    private Plan<Boolean> map;
    private Plan<Distance> expectedDistanceMap;
    private final Position departure = new Position(3, 1), arrival = new Position(4, 7);

    private void addObstacle(int y, int x) {
        map.modifierElement(y, x, true);
    }

    @Before
    public void setUp() {
        map = new Plan<>(new Boolean[ySize][xSize], false);
        expectedDistanceMap = new Plan<>(new Distance[ySize][xSize], Distance.INFINI);

        addObstacle(3, 1);
        addObstacle(3, 2);
        addObstacle(3, 3);
        addObstacle(3, 4);
        addObstacle(3, 5);

        addObstacle(3, 7);

        addObstacle(12, 5);
        addObstacle(12, 6);
        addObstacle(12, 7);

        sut = new Itineraire(map, departure, arrival);
    }

    // These UTs check a snapshot of verified results

    private void setExpectedDistanceMapShortestPath() {
        expectedDistanceMap.modifierElement(1, 3, new Distance(0));
        expectedDistanceMap.modifierElement(2, 3, new Distance(1));
        expectedDistanceMap.modifierElement(2, 4, new Distance(2));
        expectedDistanceMap.modifierElement(2, 5, new Distance(3));
        expectedDistanceMap.modifierElement(2, 6, new Distance(4));
        expectedDistanceMap.modifierElement(3, 6, new Distance(5));
        expectedDistanceMap.modifierElement(4, 6, new Distance(6));
        expectedDistanceMap.modifierElement(5, 6, new Distance(7));
        expectedDistanceMap.modifierElement(6, 6, new Distance(8));
        expectedDistanceMap.modifierElement(7, 6, new Distance(9));
        expectedDistanceMap.modifierElement(7, 5, new Distance(10));
        expectedDistanceMap.modifierElement(7, 4, new Distance(11));
    }

    private void setExpectedDistanceMapAllPaths() {
        expectedDistanceMap.modifierElement(1, 3, new Distance(0));
        expectedDistanceMap.modifierElement(1, 4, new Distance(1));
        expectedDistanceMap.modifierElement(1, 5, new Distance(2));
        expectedDistanceMap.modifierElement(1, 6, new Distance(3));
        expectedDistanceMap.modifierElement(2, 6, new Distance(4));
        expectedDistanceMap.modifierElement(3, 6, new Distance(5));
        expectedDistanceMap.modifierElement(4, 6, new Distance(6));
        expectedDistanceMap.modifierElement(5, 6, new Distance(7));
        expectedDistanceMap.modifierElement(6, 6, new Distance(8));
        expectedDistanceMap.modifierElement(7, 6, new Distance(9));
        expectedDistanceMap.modifierElement(7, 5, new Distance(10));
        expectedDistanceMap.modifierElement(7, 4, new Distance(11));

        expectedDistanceMap.modifierElement(1, 7, new Distance(4));
        expectedDistanceMap.modifierElement(1, 8, new Distance(5));
        expectedDistanceMap.modifierElement(2, 8, new Distance(6));
        expectedDistanceMap.modifierElement(3, 8, new Distance(7));
        expectedDistanceMap.modifierElement(4, 8, new Distance(8));
        expectedDistanceMap.modifierElement(5, 8, new Distance(9));
        expectedDistanceMap.modifierElement(5, 7, new Distance(8));


        expectedDistanceMap.modifierElement(1, 2, new Distance(1));
        expectedDistanceMap.modifierElement(1, 1, new Distance(2));
        expectedDistanceMap.modifierElement(2, 1, new Distance(3));
        expectedDistanceMap.modifierElement(2, 0, new Distance(4));
        expectedDistanceMap.modifierElement(3, 0, new Distance(5));
        expectedDistanceMap.modifierElement(4, 0, new Distance(6));
        expectedDistanceMap.modifierElement(4, 1, new Distance(7));
        expectedDistanceMap.modifierElement(5, 1, new Distance(8));
        expectedDistanceMap.modifierElement(5, 2, new Distance(9));
        expectedDistanceMap.modifierElement(6, 2, new Distance(10));
        expectedDistanceMap.modifierElement(6, 3, new Distance(11));
        expectedDistanceMap.modifierElement(6, 4, new Distance(10));
    }

    @Test
    public void testSimplePathfinding() {
        Plan<Distance> shortestPath = sut.calculerPlusCourtCheminOrthogonal();

        setExpectedDistanceMapShortestPath();
        assertEquals(expectedDistanceMap, shortestPath);
    }

    @Test
    public void testGraphOfPossiblePaths() {
        Plan<Distance> allShortestPaths = sut.calculerZoneTopologique();

        setExpectedDistanceMapAllPaths();
        assertEquals(expectedDistanceMap, allShortestPaths);

        final int expectedNumberOfPossiblePaths = 3;
        assertEquals(expectedNumberOfPossiblePaths, sut.calculerGraphe());
    }
}
