/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.model;

import org.junit.Test;
import static org.junit.Assert.*;
import pacandroid.model.PathFinder.Node;
import pacandroid.model.PathFinder.Path;

/**
 *
 * @author lachlan
 */
public class PathFinderTest {

    @Test
    public void testTrivial() {
        Grid grid = new Grid(5, 1);
        Node start = new Node(2, 0);
        Node end = new Node(2, 4);

        PathFinder pathFinder = new PathFinder(grid);
        Path path = pathFinder.getPath(start, end);

        assertEquals(start, path.get(0));
        assertEquals(end, path.get(1));
    }

    @Test
    public void testBasic() {
        Grid grid = new Grid(5, 1);
        grid.set(2, 2, Grid.GRID_WALL);
        grid.set(3, 2, Grid.GRID_WALL);
        grid.set(4, 2, Grid.GRID_WALL);

        Node start = new Node(2, 0);
        Node end = new Node(2, 4);
        Path expected = new Path(
                start,
                new Node(2, 1), new Node(1, 1),
                new Node(1, 4),
                end);

        PathFinder pathFinder = new PathFinder(grid);
        Path path = pathFinder.getPath(start, end);

        assertEquals(expected, path);
    }

    @Test
    public void testBiggerBasic() {
        Grid grid = new Grid(9, 1);
        grid.set(2, 4, Grid.GRID_WALL);
        grid.set(3, 4, Grid.GRID_WALL);
        grid.set(4, 4, Grid.GRID_WALL);

        Node start = new Node(2, 0);
        Node end = new Node(2, 8);
        Path expected = new Path(
                start,
                new Node(2, 3), new Node(1, 3),
                new Node(1, 8),
                end);

        PathFinder pathFinder = new PathFinder(grid);
        Path path = pathFinder.getPath(start, end);

        assertEquals(expected, path);
    }
}