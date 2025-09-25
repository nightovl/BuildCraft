/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.silicon.item;

import javax.annotation.Nonnull;

import ct.buildcraft.api.transport.IItemPluggable;
import ct.buildcraft.api.transport.pipe.IFlowItems;
import ct.buildcraft.api.transport.pipe.IPipe;
import ct.buildcraft.api.transport.pipe.IPipeHolder;
import ct.buildcraft.api.transport.pluggable.PipePluggable;
import ct.buildcraft.api.transport.pluggable.PluggableDefinition;
import ct.buildcraft.lib.misc.ColourUtil;
import ct.buildcraft.lib.misc.LocaleUtil;
import ct.buildcraft.lib.misc.SoundUtil;
import ct.buildcraft.silicon.BCSiliconPlugs;
import ct.buildcraft.silicon.plug.PluggableLens;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ItemPluggableLens extends Item implements IItemPluggable {
    public ItemPluggableLens() {
        super(new Item.Properties().durability(0));
      //  setMaxDamage(0);
        //setHasSubtypes(true);
    }

    public static LensData getData(ItemStack stack) {
        return new LensData(stack);
    }

    @Nonnull
    public ItemStack getStack(DyeColor colour, boolean isFilter) {
        return getStack(new LensData(colour, isFilter));
    }

    @Nonnull
    public ItemStack getStack(LensData variant) {
        ItemStack stack = new ItemStack(this);
        variant.writeToStack(stack);
        return stack;
    }

    @Override
    public PipePluggable onPlace(@Nonnull ItemStack stack, IPipeHolder holder, Direction side, Player player,
        InteractionHand hand) {
        IPipe pipe = holder.getPipe();
        if (pipe == null || !(pipe.getFlow() instanceof IFlowItems)) {
            return null;
        }
        LensData data = getData(stack);
        SoundUtil.playBlockPlace(holder.getPipeWorld(), holder.getPipePos(), Blocks.STONE.defaultBlockState());
        PluggableDefinition def = BCSiliconPlugs.lens;
        return new PluggableLens(def, holder, side, data.colour, data.isFilter);
    }

    @Override
	public String getDescriptionId(ItemStack stack) {
        LensData data = getData(stack);
        String colour = data.colour == null ? LocaleUtil.localize("color.clear")
            : ColourUtil.getTextFullTooltipSpecial(data.colour);
        String first = LocaleUtil.localize(data.isFilter ? "item.Filter.name" : "item.Lens.name");
        return colour + " " + first;
	}

/*	@Override
    @SideOnly(Side.CLIENT)
    public FontRenderer getFontRenderer(ItemStack stack) {
        return SpecialColourFontRenderer.INSTANCE;
    }*///TODO

    @Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> subItems) {
        for (int i = 0; i < 34; i++) {
        	ItemStack item = new ItemStack(this, 1);
        	item.setDamageValue(i);
            subItems.add(item);
        }
    }

/*    @Override
    @SideOnly(Side.CLIENT)
    public void addModelVariants(TIntObjectHashMap<ModelResourceLocation> variants) {
        for (int i = 0; i < 34; i++) {
            variants.put(i, new ModelResourceLocation("buildcraftsilicon:lens_item#inventory"));
        }
    }*/

	public static class LensData {
        public final DyeColor colour;
        public final boolean isFilter;

        public LensData(DyeColor colour, boolean isFilter) {
            this.colour = colour;
            this.isFilter = isFilter;
        }

        public LensData(ItemStack stack) {
            this(stack.getDamageValue());
        }

        public LensData(int damage) {
            if (damage >= 32) {
                colour = null;
                isFilter = damage == 33;
            } else {
                colour = DyeColor.byId(damage & 15);
                isFilter = damage >= 16;
            }
        }

        public int getItemDamage() {
            if (colour == null) {
                return isFilter ? 33 : 32;
            } else {
                return colour.getId() + (isFilter ? 16 : 0);
            }
        }

        public ItemStack writeToStack(ItemStack stack) {
            stack.setDamageValue(getItemDamage());
            return stack;
        }
    }
}
