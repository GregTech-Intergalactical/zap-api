package tesseract.graph;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import tesseract.util.Node;
import tesseract.util.Pos;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * The Path is a class that should work with paths for grids.
 */
public class Path<C extends IConnectable> {

    private Pos origin;
    private Pos target;
    private ObjectList<C> full;
    private ObjectList<C> cross;

    /**
     * Creates a path instance.
     *
     * @param connectors The connectors array.
     * @param path The path queue.
     */
    public Path(Long2ObjectMap<Connectivity.Cache<C>> connectors, ArrayDeque<Node> path) {
        origin = path.pollLast();
        target = path.pollFirst();

        full = new ObjectArrayList<>();
        cross = new ObjectArrayList<>();

        Iterator<Node> iterator = path.descendingIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            C cable = connectors.get(node.get()).value();
            full.add(cable);
            if (node.isCrossroad()) {
                cross.add(cable);
            }
        }
    }

    /**
     * @return Gets the origin position.
     */
    public Pos origin() {
        return origin;
    }

    /**
     * @return Gets the target position.
     */
    public Pos target() {
        return target;
    }

    /**
     * @return Gets the full connectors path.
     */
    public ObjectList<C> getFull() {
        return full;
    }

    /**
     * @return Gets the crossroad connectors path.
     */
    public ObjectList<C> getCross() {
        return cross;
    }

    /**
     * @return Checks that the path is empty.
     */
    public boolean isEmpty() {
        return origin == null || target == null;
    }
}