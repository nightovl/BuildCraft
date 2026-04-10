/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.factory;

import ct.buildcraft.factory.client.render.RenderDistiller;
import ct.buildcraft.factory.client.render.RenderHeatExchange;
import ct.buildcraft.factory.client.render.RenderMiningWell;
import ct.buildcraft.factory.client.render.RenderPump;
import ct.buildcraft.factory.client.render.RenderTank;
import ct.buildcraft.factory.tile.TileDistiller_BC8;
import ct.buildcraft.lib.client.model.ModelHolderVariable;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;

public class BCFactoryModels {
    public static final ModelHolderVariable DISTILLER = new ModelHolderVariable(
        "buildcraftfactory:models/tiles/distiller.json",
        TileDistiller_BC8.MODEL_FUNC_CTX
    );
/*    public static final ModelHolderVariable HEAT_EXCHANGE_STATIC = new ModelHolderVariable(
        "buildcraftfactory:models/tiles/heat_exchange_static.json",
        ModelHeatExchange.FUNCTION_CONTEXT
    );*/

/*    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onModelRegistry(ModelRegistryEvent event) {
        if (BCFactoryBlocks.heatExchange != null) {
            ModelLoader.setCustomStateMapper(
                BCFactoryBlocks.heatExchange,
                new StateMapperBase() {
                    @Nonnull
                    @Override
                    protected ModelResourceLocation getModelResourceLocation(@Nonnull BlockState state) {
                        return new ModelResourceLocation("buildcraftfactory:heat_exchange#normal");
                    }
                }
            );
        }
    }*/

	public static void registryRender(RegisterRenderers e) {
    	e.registerBlockEntityRenderer(BCFactoryBlocks.ENTITYBLOCKTANK.get(), RenderTank::new);
    	e.registerBlockEntityRenderer(BCFactoryBlocks.ENTITYBLOCKPUMP.get(), RenderPump::new);
    	e.registerBlockEntityRenderer(BCFactoryBlocks.ENTITYBLOCKMININGWELL.get(), RenderMiningWell::new);
    	e.registerBlockEntityRenderer(BCFactoryBlocks.ENTITYBLOCKDISTILLER.get(), RenderDistiller::new);
    	e.registerBlockEntityRenderer(BCFactoryBlocks.ENTITYBLOCKHEATEXCHANGE.get(), RenderHeatExchange::new);
	}

    public static void onModelBake(BakingCompleted event) {
/*        event.getModelRegistry().putObject(
            new ModelResourceLocation("buildcraftfactory:heat_exchange#normal"),
            new ModelHeatExchange()
        );*/
 /*       event.getModels().put(
            new ModelResourceLocation("buildcraftfactory:heat_exchange#inventory"),
            new ModelItemSimple(
                Arrays.stream(BCFactoryModels.HEAT_EXCHANGE_STATIC.getCutoutQuads())
                    .map(MutableQuad::multShade)
                    .map(MutableQuad::toBakedItem)
                    .collect(Collectors.toList()),
                ModelItemSimple.TRANSFORM_BLOCK,
                true
            )
        );*/
    }
}
