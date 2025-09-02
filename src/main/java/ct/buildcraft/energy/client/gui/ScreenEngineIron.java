package ct.buildcraft.energy.client.gui;

import java.util.List;

import ct.buildcraft.energy.BCEnergySprites;
import ct.buildcraft.lib.gui.ContainerScreenBase;
import ct.buildcraft.lib.gui.component.TankComponent;
import ct.buildcraft.lib.misc.LocaleUtil;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.material.Fluid;

public class ScreenEngineIron extends ContainerScreenBase<MenuEngineIron_BC8>{

	private static final ResourceLocation TEXTURE_BASE = BCEnergySprites.ENGINE_IRON_GUI;

	protected static final TankComponent fuelTank = new TankComponent(26, 18, 16, 60, 10000, 176, 0);
	protected static final TankComponent coolantTank = new TankComponent(80, 18, 16, 60, 10000, 176, 0);
	protected static final TankComponent residueTank = new TankComponent(134, 18, 16, 60, 10000, 176, 0);
	
	protected final ContainerData data;
	
	public ScreenEngineIron(MenuEngineIron_BC8 be, Inventory p_97742_, Component p_97743_) {
		super(be, p_97742_, p_97743_, 3, TEXTURE_BASE);
		data = be.data;
		inventoryLabelY += 12;
		imageHeight += 10;
		this.add(fuelTank, true);
		this.add(coolantTank, true);
		this.add(residueTank, true);
		setup(data);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
	    super.render(pose, mouseX, mouseY, partialTick);
/*	    RenderSystem.setShaderTexture(0, TEXTURE_BASE);
	    this.blit(pose, this.leftPos+26, this.topPos+18, 176, 0, 16, 60);
	    this.blit(pose, this.leftPos+80, this.topPos+18, 176, 0, 16, 60);
	    this.blit(pose, this.leftPos+134, this.topPos+18, 176, 0, 16, 60);*/
	    renderTooltip(pose, mouseX, mouseY);
	}
	
	@Override
	protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
		super.renderBg(pose, partialTick, mouseX, mouseY);
	}

    protected List<Component> getToolTip(Fluid fluid,int amount) {
        List<Component> toolTip = Lists.newArrayList();
        if (amount > 0) 
        	toolTip.add(fluid.getFluidType().getDescription());
        toolTip.add(Component.translatable(LocaleUtil.localizeFluidStaticAmount(amount, 10000)).withStyle(ChatFormatting.GRAY));//TODO
        return toolTip ;
    }



}
