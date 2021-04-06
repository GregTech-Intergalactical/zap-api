package tesseract.graph;

import net.minecraft.util.LazyValue;
import tesseract.api.IConnectable;
import tesseract.util.Dir;

import java.util.function.Supplier;

/**
 * The Cache is a class that should work with connections.
 */
public class Cache<T extends IConnectable> {

    /** Byte value associated with cache, e.g. connectivity or ref count. **/
    private byte associated;
    private final LazyValue<T> value;

    /**
     * Creates a cache instance.
     */
    public Cache(Supplier<T> value) {
        this.value = new LazyValue<>(value);
        this.associated = 1;
    }

    public Cache(T value) {
        this.value = new LazyValue<>(() -> value);
        this.associated = Connectivity.of(this.value.getValue());
    }

    /**
     * Creates a cache instance from a delegate.
     */
    /*public Cache(T value, IConnectable delegate) {
        this.value = value;
        this.connectivity = Connectivity.of(delegate);
    }*/

    /**
     * @param direction The direction index.
     * @return True when connect, false otherwise.
     */
    public boolean connects(Dir direction) {
        return Connectivity.has(associated, direction.getIndex());
    }

    /**
     * @return Gets the connection state.
     */
    public byte associated() {
        return associated;
    }

    /**
     * @return Gets the cache.
     */
    public T value() {
        return value.getValue();
    }

    public void increaseCount() {
        this.associated++;
    }

    public boolean decreaseCount() {
        this.associated--;
        return associated == 0;
    }
}