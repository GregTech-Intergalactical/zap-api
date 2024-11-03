package tesseract.api;

import net.minecraft.core.Direction;
import tesseract.newgraph.NodePath;

/**
 * A simple interface for representing connectable objects.
 */
public interface IConnectable {

    /**
     * @param direction The direction vector.
     * @return True if connect to the direction, false otherwise.
     */
    boolean connects(Direction direction);

    boolean validate(Direction dir);


    <T extends IConnectable> void setNodePath(NodePath<T> tNodePath);

    void reloadLocks();
}
