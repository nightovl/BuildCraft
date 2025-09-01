/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ct.buildcraft.core.item;

import java.util.EnumMap;

import ct.buildcraft.api.blocks.CustomPaintHelper;
import ct.buildcraft.core.BCCoreItems;
import ct.buildcraft.lib.item.ItemByEnum;
import ct.buildcraft.lib.misc.SoundUtil;
import ct.buildcraft.lib.misc.StackUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ItemPaintbrush_BC8 extends ItemByEnum<DyeColor> {
    
    protected final DyeColor color;

    public ItemPaintbrush_BC8(DyeColor color, Properties pro, EnumMap<DyeColor, ItemPaintbrush_BC8> map) {
		super(pro, color, null);
		if(color != null)
			map.put(color, this);
		this.color = color;
	}
    
    @Override
	public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        InteractionHand hand = ctx.getHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
    	ItemStack stack = StackUtil.asNonNull(player.getItemInHand(hand));
        Vec3 hitPos = ctx.getClickLocation();
        if (CustomPaintHelper.INSTANCE.attemptPaintBlock(world, pos, world.getBlockState(pos), hitPos, ctx.getClickedFace(), color) == InteractionResult.SUCCESS) {
            CompoundTag tag = stack.getTag();
//            BCLog.logger.debug("" + tag.getAsString());
            if (player != null) {
                stack.hurtAndBreak(1, player, (p_186374_) -> {
                   p_186374_.broadcastBreakEvent(hand);
                });
             }
            stack.setDamageValue(63);
            if (stack.isEmpty()) {
            	ItemStack stack1 = new ItemStack(BCCoreItems.PAINT_BRUSH.get(), 1);
            	stack1.setTag(tag);
                player.setItemInHand(hand, stack1);
            }
            // We just changed the damage NBT value
            player.inventoryMenu.broadcastChanges();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
	}

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, damage);
    }

    public boolean tryBrush(ItemStack stack, Level world, BlockPos pos, BlockState state, Vec3 hitPos, Direction side, Player player) {
        if (color != null && stack.getDamageValue() > 64) {
            return false;
        }

        InteractionResult result = CustomPaintHelper.INSTANCE.attemptPaintBlock(world, pos, state, hitPos, side, color);

        if (result == InteractionResult.SUCCESS) {
//            ParticleUtil.showChangeColour(world, hitPos, colour);
            SoundUtil.playChangeColour(world, pos, color);
            return true;
        }
        return false;
    }

}
