package tesseract.mixin.fabric;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.fabric.fluid.storage.FabricBlockFluidContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FabricBlockFluidContainer.class, remap = false)
public interface FabricBlockFluidContainerAccessor {

    @Accessor
    FluidContainer getContainer();
}
