package ct.buildcraft.energy.fluid;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

public class BCFluidType extends FluidType{

	private final ResourceLocation stillTexture;BCFluidType
	private final ResourceLocation flowTexture;
	private final int tintColor;
	
	public BCFluidType(Properties properties, int nameid, int tempture, int tintColor) {
		super(properties);
		this.stillTexture = nameid;
		this.flowTexture = flowingTexture;
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

	@Override
	public int getTemperature() {
		// TODO Auto-generated method stub
		return super.getTemperature();
	}

	@Override
	public int getTemperature(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		// TODO Auto-generated method stub
		return super.getTemperature(state, getter, pos);
	}

	@Override
	public int getTemperature(FluidStack stack) {
		// TODO Auto-generated method stub
		return super.getTemperature(stack);
	}
	
	
	
	
}
