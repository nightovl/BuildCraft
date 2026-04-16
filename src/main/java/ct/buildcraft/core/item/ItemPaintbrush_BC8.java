/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ct.buildcraft.core.item;

import ct.buildcraft.api.blocks.CustomPaintHelper;
import ct.buildcraft.core.BCCoreItems;
import ct.buildcraft.lib.item.ItemByEnum;
import ct.buildcraft.lib.misc.SoundUtil;
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
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ItemPaintbrush_BC8 extends ItemByEnum<DyeColor> {

    public ItemPaintbrush_BC8(Properties pro, DyeColor color) {
        super(pro, color);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        InteractionHand hand = ctx.getHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = world.getBlockState(pos);

        if ((state.getBlock() instanceof BedBlock || state.getBlock() instanceof ShulkerBoxBlock) && !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        Vec3 hitPos = ctx.getClickLocation();
        if (CustomPaintHelper.INSTANCE.attemptPaintBlock(world, pos, state, hitPos, ctx.getClickedFace(), type)
            == InteractionResult.SUCCESS) {
            CompoundTag tag = stack.getTag();
            stack.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(hand));
            if (stack.isEmpty()) {
                ItemStack cleanBrush = new ItemStack(BCCoreItems.PAINT_BRUSH.get(), 1);
                cleanBrush.setTag(tag);
                player.setItemInHand(hand, cleanBrush);
            }
            player.inventoryMenu.broadcastChanges();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, damage);
    }

    public boolean tryBrush(ItemStack stack, Level world, BlockPos pos, BlockState state, Vec3 hitPos, Direction side,
        Player player) {
        if (type != null && stack.getDamageValue() > 64) {
            return false;
        }

        InteractionResult result = CustomPaintHelper.INSTANCE.attemptPaintBlock(world, pos, state, hitPos, side, type);

        if (result == InteractionResult.SUCCESS) {
            SoundUtil.playChangeColour(world, pos, type);
            return true;
        }
        return false;
    }
}
