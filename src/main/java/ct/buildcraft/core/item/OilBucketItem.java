package ct.buildcraft.core.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fluids.FluidStack;

public class OilBucketItem extends BucketItem {

	public OilBucketItem(Properties builder) {
		super(() -> Fluids.EMPTY, builder);Items.WATER_BUCKET
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.isEmpty()) {
            return InteractionResultHolder.pass(itemstack);
        }
        CompoundTag t = itemstack.getTag();
        CompoundTag fluidNbt = t == null? null : t.getCompound("fluid");
        if (fluidNbt == null) {
            return InteractionResultHolder.pass(itemstack);
        }
        Fluid fluid =  FluidStack.loadFluidStackFromNBT(fluidNbt).getFluid();
        
		BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player,
				fluid == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
		InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, level,
				itemstack, blockhitresult);
		if (ret != null)
			return ret;
		if (blockhitresult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemstack);
		} else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(itemstack);
		} else {
			BlockPos blockpos = blockhitresult.getBlockPos();
			Direction direction = blockhitresult.getDirection();
			BlockPos blockpos1 = blockpos.relative(direction);
			if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction, itemstack)) {
				if (fluid == Fluids.EMPTY) {
					BlockState blockstate1 = level.getBlockState(blockpos);
					if (blockstate1.getBlock() instanceof BucketPickup) {
						BucketPickup bucketpickup = (BucketPickup) blockstate1.getBlock();
						ItemStack itemstack1 = bucketpickup.pickupBlock(level, blockpos, blockstate1);
						if (!itemstack1.isEmpty()) {
							player.awardStat(Stats.ITEM_USED.get(this));
							bucketpickup.getPickupSound(blockstate1).ifPresent((p_150709_) -> {
								player.playSound(p_150709_, 1.0F, 1.0F);
							});
							level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);
							ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, player, itemstack1);
							if (!level.isClientSide) {
								CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemstack1);
							}

							return InteractionResultHolder.sidedSuccess(itemstack2, level.isClientSide());
						}
					}

					return InteractionResultHolder.fail(itemstack);
				} else {
					BlockState blockstate = level.getBlockState(blockpos);
					BlockPos blockpos2 = blockstate.getBlock() instanceof LiquidBlockContainer blockContainer && blockContainer.canPlaceLiquid(level, blockpos, blockstate, fluid)
							? blockpos : blockpos1;
					if (this.emptyContents(player, level, blockpos2, blockhitresult)) {
						this.checkExtraContent(player, level, itemstack, blockpos2);
						if (player instanceof ServerPlayer) {
							CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos2, itemstack);
						}

						player.awardStat(Stats.ITEM_USED.get(this));
						return InteractionResultHolder.sidedSuccess(getEmptySuccessItem(itemstack, player),
								level.isClientSide());
					} else {
						return InteractionResultHolder.fail(itemstack);
					}
				}
			} else {
				return InteractionResultHolder.fail(itemstack);
			}
		}
	}

}
