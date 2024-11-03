package tesseract.newgraph;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public interface INode<T> {
    T getHandler();
    Direction getSide();
    BlockEntity getBlockEntity();
    List<INode<T>> getConsumers();
    INode<T> getNeighborNode(Direction direction);
    void setNeighborNode(Direction direction, INode<T> neighbor);
    Lock getLock(Direction direction);
    void setLock(Direction direction, Lock lock);
    int getNodeValue();
    int getHighestNodeValue();
    int geCreationTime();
}
