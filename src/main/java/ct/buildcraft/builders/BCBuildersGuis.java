/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ct.buildcraft.builders;

import ct.buildcraft.builders.gui.MenuArchitectTable;
import ct.buildcraft.builders.gui.MenuBuilder;
import ct.buildcraft.builders.gui.MenuElectronicLibrary;
import ct.buildcraft.builders.gui.MenuFiller;
import ct.buildcraft.builders.gui.MenuReplacer;
import ct.buildcraft.builders.gui.ScreenArchitectTable;
import ct.buildcraft.builders.gui.ScreenBuilder;
import ct.buildcraft.builders.gui.ScreenElectronicLibrary;
import ct.buildcraft.builders.gui.ScreenFiller;
import ct.buildcraft.builders.gui.ScreenReplacer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BCBuildersGuis {
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BCBuilders.MODID);
    public static final RegistryObject<MenuType<MenuArchitectTable>> MENU_ARCHITECT_TABLE = MENUS.register("architect_menu", () -> new MenuType<>(MenuArchitectTable::new));
    public static final RegistryObject<MenuType<MenuBuilder>> MENU_BUILDER = MENUS.register("builder_menu", () -> new MenuType<>(MenuBuilder::new));
    public static final RegistryObject<MenuType<MenuElectronicLibrary>> MENU_ELIBRARY = MENUS.register("elibrary_menu", () -> new MenuType<>(MenuElectronicLibrary::new));
    public static final RegistryObject<MenuType<MenuFiller>> MENU_FILLER = MENUS.register("filler_menu", () -> new MenuType<>(MenuFiller::new));
    public static final RegistryObject<MenuType<MenuReplacer>> MENU_REPLACER = MENUS.register("replacer_menu", () -> new MenuType<>(MenuReplacer::new));


    public static void clientInit(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> {
                	MenuScreens.register(MENU_ARCHITECT_TABLE.get(), ScreenArchitectTable::new);
                	MenuScreens.register(MENU_BUILDER.get(), ScreenBuilder::new);
                	MenuScreens.register(MENU_ELIBRARY.get(), ScreenElectronicLibrary::new);
                	MenuScreens.register(MENU_FILLER.get(), ScreenFiller::new);
                	MenuScreens.register(MENU_REPLACER.get(), ScreenReplacer::new);
                }
        );
    }
    

    public void openGui(Player player) {
        openGui(player, 0, -1, 0);
    }

    public void openGui(Player player, BlockPos pos) {
        openGui(player, pos.getX(), pos.getY(), pos.getZ());
    }

    public void openGui(Player player, int x, int y, int z) {
//    	player.openMenu(null);
//        player.openGui(BCTransport, ordinal(), player.getLevel(), x, y, z);
    }
    static void preInit(IEventBus modEventBus) {
    	MENUS.register(modEventBus);
    }
}
