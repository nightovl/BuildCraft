package ct.buildcraft.energy.fluid;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;

public class BCLiquidBlock extends LiquidBlock{
	
	public final boolean isStick;

	public BCLiquidBlock(Supplier<? extends FlowingFluid> source, Properties propertes, boolean isStick) {
		super(source, propertes);
		this.isStick = isStick;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if(isStick)
			entity.makeStuckInBlock(state	, new Vec3(0.25D, (double)0.05F, 0.25D));
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		// TODO Auto-generated method stub
		return super.getPickupSound();
	}

	
	
}
