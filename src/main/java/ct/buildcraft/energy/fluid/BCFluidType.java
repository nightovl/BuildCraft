package ct.buildcraft.energy.fluid;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

public class BCFluidType extends FluidType{

	//store fluid type, but no temperature
	private final ResourceLocation stillTexture;
	private final ResourceLocation flowTexture;
	private final int tintColor;
	
	public BCFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowTexture, int tintColor) {
		super(properties);
		this.stillTexture = stillTexture;
		this.flowTexture = flowTexture;
		this.tintColor = tintColor;
	}

	@Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
    {
		consumer.accept(new IClientFluidTypeExtensions() {
           ResourceLocation 
           UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png"),
           WATER_OVERLAY = new ResourceLocation("block/water_overlay");
            
			@Override
			public ResourceLocation getStillTexture() {
				return stillTexture;
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return flowTexture;
			}
			
			@Override
			public @Nullable ResourceLocation getOverlayTexture() {
				return WATER_OVERLAY;//TODO
			}
			
            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc)
            {
                return UNDERWATER_LOCATION;//TODO
            }

			@Override
			public int getTintColor() {
				return tintColor;
			}
			

		});
    }
	
	
}
