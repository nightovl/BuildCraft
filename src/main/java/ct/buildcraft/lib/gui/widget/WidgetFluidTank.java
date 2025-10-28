/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.lib.gui.widget;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.parse.ANTLRParser.finallyClause_return;

import com.mojang.blaze3d.vertex.PoseStack;

import ct.buildcraft.lib.fluid.Tank;
import ct.buildcraft.lib.gui.BuildCraftGui;
import ct.buildcraft.lib.gui.GuiElementSimple;
import ct.buildcraft.lib.gui.GuiIcon;
import ct.buildcraft.lib.gui.IGuiElement;
import ct.buildcraft.lib.gui.IInteractionElement;
import ct.buildcraft.lib.gui.MenuBC_Neptune;
import ct.buildcraft.lib.gui.TankContainerData;
import ct.buildcraft.lib.gui.Widget_Neptune;
import ct.buildcraft.lib.gui.elem.ToolTip;
import ct.buildcraft.lib.gui.help.ElementHelpInfo.HelpPosition;
import ct.buildcraft.lib.gui.pos.IGuiArea;
import ct.buildcraft.lib.misc.GuiUtil;
import ct.buildcraft.lib.net.PacketBufferBC;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.ContainerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public class WidgetFluidTank extends Widget_Neptune<MenuBC_Neptune> {
    private static final byte NET_CLICK = 0;

    private final Tank tank;
    private final ContainerData tankData;
    private int dataPoint = 0;
    public final boolean isClientSide;

    public WidgetFluidTank(MenuBC_Neptune container, Tank tank) {
        super(container);
        this.tank = tank;
        tankData = null;
        isClientSide = false;
    }
    
    public WidgetFluidTank(MenuBC_Neptune container, ContainerData data, int point) {
        super(container);
        this.tank = null;
        tankData = data;
        isClientSide = true;
        dataPoint = point;
    }
    

    @Override
    public void handleWidgetDataServer(NetworkEvent.Context ctx, FriendlyByteBuf buffer) throws IOException {
        byte id = buffer.readByte();
        if (id == NET_CLICK) {
            tank.onGuiClicked(container);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public IGuiElement createGuiElement(BuildCraftGui gui, IGuiArea area, GuiIcon overlay) {
        return new GuiElementFluidTank(gui, area, overlay);
    }

    private final class GuiElementFluidTank extends GuiElementSimple implements IInteractionElement {
        private final GuiIcon overlay;

        public GuiElementFluidTank(BuildCraftGui gui, IGuiArea area, GuiIcon overlay) {
            super(gui, area);
            this.overlay = overlay;
        }

        @Override
        public void drawBackground(PoseStack pose, float partialTicks) {
            GuiUtil.drawFluid(pose, this, TankContainerData.getRenderFluidStack(tankData, dataPoint), TankContainerData.getTankCapacity(tankData, dataPoint));
            if (overlay != null) {
                overlay.drawCutInside(this);
            }
        }

        @Override
        public void onMouseClicked(int button) {
            if (contains(gui.mouse)) {
                WidgetFluidTank.this.sendWidgetData(buffer -> buffer.writeByte(NET_CLICK));
            }
        }

        @Override
        public void addToolTips(List<ToolTip> tooltips) {
            if (contains(gui.mouse)) {
                ToolTip tooltip = TankContainerData.getTankToopTip(tankData, dataPoint);
                tooltip.refresh();
                tooltips.add(tooltip);
            }
        }

        @Override
        public void addHelpElements(List<HelpPosition> elements) {
//            elements.add(tank.helpInfo.target(this.expand(4)));
        }
    }
}
