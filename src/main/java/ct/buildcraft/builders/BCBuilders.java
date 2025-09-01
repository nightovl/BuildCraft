/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ct.buildcraft.builders;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import ct.buildcraft.builders.client.render.RenderArchitectTable;
import ct.buildcraft.builders.client.render.RenderBuilder;
import ct.buildcraft.builders.client.render.RenderFiller;
import ct.buildcraft.builders.client.render.RenderQuarry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

//@formatter:off
@Mod(BCBuilders.MODID)
//@formatter:on
public class BCBuilders {
    public static final String MODID = "buildcraftbuilders";
    static final Logger LOGGER = LogUtils.getLogger();

    public BCBuilders() {
    	IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    	modEventBus.addListener(BCBuilders::commonSetup);
//    	modEventBus.addListener(this::gatherData);//DataGenerator
    	
    	BCBuildersBlocks.registry(modEventBus);
    	BCBuildersItems.registry(modEventBus);
    	BCBuildersSchematics.preInit();
    	BCBuildersConfig.preInit();
    	BCBuildersRegistries.preInit();
    	BCBuildersGuis.preInit(modEventBus);
    	ModLoadingContext.get().registerConfig(Type.COMMON, BCBuildersConfig.config);
    	
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(BCBuildersConfig.class);

    }

    public void gatherData(GatherDataEvent event) {
    }

    public static void commonSetup(final FMLCommonSetupEvent event) {
    	BCBuildersConfig.reloadConfig(MODID);
    	BCBuildersRegistries.init();
    }



    public static void serverStarting(/*FMLServerStartingEvent event*/) {
    }
    
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){
        	BCBuildersSprites.init();
        	BCBuildersGuis.clientInit(event);
        }
        
        @SubscribeEvent
        public static void registryRender(EntityRenderersEvent.RegisterRenderers e) {

        	e.registerBlockEntityRenderer(BCBuildersBlocks.QUARRY_TILE_BC8.get(), RenderQuarry::new);
        	e.registerBlockEntityRenderer(BCBuildersBlocks.ARCHITECT_TILE_BC8.get(), RenderArchitectTable::new);
        	e.registerBlockEntityRenderer(BCBuildersBlocks.FILLER_TILE_BC8.get(), RenderFiller::new);
        	e.registerBlockEntityRenderer(BCBuildersBlocks.BUILDER_TILE_BC8.get(), RenderBuilder::new);
        }
        
        @SubscribeEvent
        public static void onModelBakePre(RegisterAdditional event) {
        	BCBuildersItems.registerItemProperties();
        }
        
        @SubscribeEvent
        public static void registryTexture(TextureStitchEvent.Pre e){ 
        	BCBuildersSprites.onTextureStitchPre(e);
//        	PipeWireRenderer.clearWireCache();
        }
        	
    }
}
