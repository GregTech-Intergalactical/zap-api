package tesseract.newgraph;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public interface INode<T> {
    T getHandler();
    Direction getSide();
    BlockEntity getBlockEntity();
    List<INode<T>> getConsumers();
    int getNodeValue();
    int getHighestNodeValue();
    int geCreationTime();
}
