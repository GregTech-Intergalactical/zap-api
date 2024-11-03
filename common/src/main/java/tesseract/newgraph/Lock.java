package tesseract.newgraph;


import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;

public class Lock {

    protected ArrayList<BlockEntity> tiles = new ArrayList<>();

    public void addTileEntity(BlockEntity tileEntity) {
        int i = contains(tileEntity);
        if (i == -1) {
            tiles.add(tileEntity);
        }
    }

    public void removeTileEntity(BlockEntity tileEntity) {
        int i = contains(tileEntity);
        if (i > -1) {
            tiles.remove(i);
        }
    }

    public boolean isLocked() {
        return !tiles.isEmpty();
    }

    // i want to check for the exact object not equals
    protected int contains(BlockEntity tileEntity) {
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i) == tileEntity) {
                return i;
            }
        }
        return -1;
    }
}
