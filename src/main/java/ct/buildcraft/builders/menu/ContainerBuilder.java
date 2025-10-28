/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.builders.menu;

import java.util.ArrayList;
import java.util.List;

import ct.buildcraft.builders.BCBuildersGuis;
import ct.buildcraft.builders.tile.TileBuilder;
import ct.buildcraft.lib.gui.ContainerBCTile;
import ct.buildcraft.lib.gui.ItemProvider;
import ct.buildcraft.lib.gui.TankContainerData;
import ct.buildcraft.lib.gui.slot.SlotBase;
import ct.buildcraft.lib.gui.slot.SlotDisplay;
import ct.buildcraft.lib.gui.widget.WidgetFluidTank;
import ct.buildcraft.lib.tile.item.IItemHandlerAdv;
import ct.buildcraft.lib.tile.item.ItemHandlerSimple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.IItemHandler;

public class ContainerBuilder extends ContainerBCTile<TileBuilder> {
    public final List<WidgetFluidTank> widgetTanks = new ArrayList<WidgetFluidTank>(4);
//    public final ContainerData remainingDisplayRequiredData;
    
	public ContainerBuilder(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new ItemHandlerSimple(1), new ItemHandlerSimple(27),
				new SimpleContainerData(4 * TankContainerData.LEN), new ItemHandlerSimple(24), ContainerLevelAccess.NULL);
	}


    public ContainerBuilder(int containerId, Inventory playerInventory, IItemHandlerAdv invSnapshot, IItemHandlerAdv invResources, 
    		ContainerData tanks, IItemHandler invRequire, /*ContainerData remainingDisplayRequiredData,*/ ContainerLevelAccess access) {
    	super(BCBuildersGuis.MENU_BUILDER.get(), playerInventory, containerId, access);

 //   	this.remainingDisplayRequiredData = remainingDisplayRequiredData;
        addFullPlayerInventory(140);

        addSlot(new SlotBase(invSnapshot, 0, 80, 27));

        for (int sy = 0; sy < 3; sy++) {
            for (int sx = 0; sx < 9; sx++) {
            	addSlot(new SlotBase(invResources, sx + sy * 9, 8 + sx * 18, 72 + sy * 18));
            }
        }
        
        addDataSlots(tanks);
        for(int point =0;point * TankContainerData.LEN < tanks.getCount();point++) {
        	WidgetFluidTank widgetFluidTank = new WidgetFluidTank(this, tanks, point);
        	widgetTanks.add(widgetFluidTank);
        	addWidget(widgetFluidTank);
        }
        

        for(int y = 0; y < 6; y++) {
            for(int x = 0; x < 4; x++) {
            	addSlot(new SlotDisplay(invRequire, x + y * 4, 179 + x * 18, 18 + y * 18));
            }
        }
    }


 /*   private ItemStack getDisplay(int index) {
        return blueprintData.get(0) == EnumSnapshotType.BLUEPRINT.ordinal() &&
                index < remainingDisplayRequiredData.getCount()/2
                ? new ItemStack(((ForgeRegistry<Item>)ForgeRegistries.ITEMS).getValue(remainingDisplayRequiredData.get(index <<1 + 1)),remainingDisplayRequiredData.get(index <<1))
                : ItemStack.EMPTY;
    }*/
}
