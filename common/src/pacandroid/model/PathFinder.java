/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.model;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author lachlan
 */
public class PathFinder {

    private final Grid grid;

    public PathFinder(Grid grid) {
        this.grid = grid;
    }

    public Path getPath(Node start, Node end) {
        Set<Node> open = new HashSet<Node>();
        Set<Node> closed = new HashSet<Node>();

        Node[][] nodes = setupNodes();

        nodes[end.x][end.y] = end;

        open.add(start);

        while (true) {
            Node curr = getNext(open, end);
            if (curr == null)
                break;
            if (curr.equals(end))
                break;

            open.remove(curr);
            closed.add(curr);

            for (Node neighbour : getNeighbours(nodes, curr)) {
                if (closed.contains(neighbour) && curr.g < neighbour.g) {
                    neighbour.g = curr.g + 1;
                    neighbour.parent = curr;
                } else if (open.contains(neighbour) && curr.g < neighbour.g) {
                    neighbour.g = curr.g + 1;
                    neighbour.parent = curr;
                } else if (!closed.contains(neighbour) && !open.contains(neighbour)) {
                    open.add(neighbour);
                    neighbour.g = curr.g + 1;
                    neighbour.parent = curr;
                }
            }
        }

        Path path = new Path();
        Node node = end;
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        path.reverse();

        return path.optimise();
    }

    private Node[][] setupNodes() {
        Node[][] nodes = new Node[grid.getWidth()][grid.getHeight()];

        for (int x = 0; x < nodes.length; x++) {
            for (int y = 0; y < nodes[x].length; y++) {
                if (!Grid.isWall(grid.get(x, y)))
                    nodes[x][y] = new Node(x, y);
            }
        }

        return nodes;
    }

    private Node getNext(Set<Node> open, Node end) {
        int minCost = Integer.MAX_VALUE;
        Node best = null;

        for (Node node : open) {
            int h = node.dist(end);
            int f = node.f + h;

            if (f > minCost)
                continue;
            else {
                best = node;
                minCost = f;
            }
        }

        return best;
    }

    private List<Node> getNeighbours(Node[][] nodes, Node centre) {
        List<Node> neighbours = new ArrayList<Node>();

        if (centre.x > 0)
            neighbours.add(nodes[centre.x - 1][centre.y]);
        if (centre.x < nodes.length - 1)
            neighbours.add(nodes[centre.x + 1][centre.y]);

        if (centre.y > 0)
            neighbours.add(nodes[centre.x][centre.y - 1]);
        if (centre.y < nodes[0].length - 1)
            neighbours.add(nodes[centre.x][centre.y + 1]);

        while (neighbours.remove(null));

        return neighbours;
    }

    public Path getPath(int startX, int startY, int endX, int endY) {
        return getPath(new Node(startX, startY), new Node(endX, endY));
    }

    public static class Node {

        public final int x, y;
        public int g, h, f;
        public Node parent;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Node(Node node) {
            this(node.x, node.y);
        }

        public int dist(Node other) {
            return (int) Math.round(Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)));
        }

        @Override
        public String toString() {
            return "[" + x + "," + y + "]";
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + this.x;
            hash = 29 * hash + this.y;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Node other = (Node) obj;
            if (this.x != other.x || this.y != other.y)
                return false;
            return true;
        }

        public Vector2 toVector2(Grid grid) {
            return new Vector2((x - .5f) * grid.getUnitSize(), (y - .5f) * grid.getUnitSize());
        }
    }

    public static class Path implements Iterable<Node> {

        private final List<Node> nodes;

        public Path() {
            nodes = new ArrayList<Node>();
        }

        public Path(Node... initial) {
            nodes = new ArrayList<Node>(initial.length);
            nodes.addAll(Arrays.asList(initial));
        }

        public void add(Node node) {
            if (nodes.contains(node))
                throw new IllegalStateException("Path cannot have cycles");
            nodes.add(node);
        }

        public void reverse() {
            Collections.reverse(nodes);
        }

        public Node get(int index) {
            return nodes.get(index);
        }

        public int getCost() {
            return nodes.size();
        }

        public int size() {
            return nodes.size();
        }

        @Override
        public Iterator<Node> iterator() {
            return nodes.iterator();
        }

        @Override
        public int hashCode() {
            return nodes.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Path other = (Path) obj;
            return nodes.equals(other.nodes);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            builder.append(nodes).append("\n");

            Node max = getMax();
            for (int y = 0; y < max.y + 1; y++) {
                for (int x = 0; x < max.x + 1; x++) {
                    Node curr = new Node(x, y);
                    if (nodes.contains(curr)) {
                        int ind = nodes.indexOf(curr);
                        if (ind == nodes.size() - 1)
                            builder.append('0');
                        else if (ind == 0)
                            builder.append('+');
                        else
                            builder.append('#');
                    } else
                        builder.append(' ');
                }
                builder.append('\n');
            }

            builder.append("]");

            return builder.toString();
        }

        private Node getMax() {
            int x = 0;
            int y = 0;

            for (Node n : nodes) {
                x = Math.max(n.x, x);
                y = Math.max(n.y, y);
            }

            return new Node(x, y);
        }

        public Path optimise() {
            if (nodes.size() < 3)
                return this;

            Path optimised = new Path(nodes.get(0));

            Node p, c, n;
            for (int i = 1; i < nodes.size() - 1; i++) {
                p = nodes.get(i - 1);
                c = nodes.get(i);
                n = nodes.get(i + 1);

                // if straight line on x axis
                if (p.x == c.x && c.x == n.x)
                    continue;
                if (p.y == c.y && c.y == n.y)
                    continue;

                optimised.add(c);
            }

            optimised.add(nodes.get(nodes.size() - 1));

            return optimised;
        }
    }
}
