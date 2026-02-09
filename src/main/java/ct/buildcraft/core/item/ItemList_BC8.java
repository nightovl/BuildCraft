/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ct.buildcraft.core.item;

import java.util.List;

import javax.annotation.Nonnull;

import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import buildcraft.core.BCCoreGuis;
import buildcraft.lib.list.ListHandler;
import ct.buildcraft.api.items.IList;
import ct.buildcraft.lib.misc.AdvancementUtil;
import ct.buildcraft.lib.misc.NBTUtilBC;
import ct.buildcraft.lib.misc.StackUtil;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.network.NetworkHooks;

public class ItemList_BC8 extends Item implements IList, MenuProvider {
    private static final ResourceLocation ADVANCEMENT = new ResourceLocation("buildcraftcore:list");
    public ItemList_BC8(Item.Properties prop) {//stack to 1
        super(prop);
    }
    
    

    @Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        AdvancementUtil.unlockAdvancement(player, ADVANCEMENT);
        if(player instanceof ServerPlayer sPlayer)
        	NetworkHooks.openScreen(sPlayer, this);
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}

    @Override
    @SideOnly(Side.CLIENT)
    public void addModelVariants(TIntObjectHashMap<ModelResourceLocation> variants) {
        addVariant(variants, 0, "clean");
        addVariant(variants, 1, "used");
    }

/*    @Override
    public int getMetadata(ItemStack stack) {
        return ListHandler.hasItems(StackUtil.asNonNull(stack)) ? 1 : 0;
    }*/
    
    

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, Level world, List<String> tooltip, ITooltipFlag flag) {
        String name = getName(StackUtil.asNonNull(stack));
        if (StringUtil.isNullOrEmpty(name)) return;
        tooltip.add(TextFormatting.ITALIC + name);
    }

    // IList

    @Override
    @OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
		String name = getLabelName(stack);
        if (StringUtil.isNullOrEmpty(name)) return;
        tooltip.add(Component.literal(name).setStyle(Style.EMPTY.withColor(ChatFormatting.ITALIC)));
	}



    //TODO ItemStack:getHoverName
	@Override
    public String getLabelName(@Nonnull ItemStack stack) {
        return NBTUtilBC.getItemData(stack).getString("label");
    }

    @Override
    public boolean setLabelName(@Nonnull ItemStack stack, String name) {
        NBTUtilBC.getItemData(stack).putString("label", name);
        return true;
    }

    @Override
    public boolean matches(@Nonnull ItemStack stackList, @Nonnull ItemStack item) {
        return ListHandler.matches(stackList, item);
    }



	@Override
	public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Component getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}
}
