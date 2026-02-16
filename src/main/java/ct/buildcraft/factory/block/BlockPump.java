/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.factory.block;

import ct.buildcraft.factory.BCFactoryBlocks;
import ct.buildcraft.factory.tile.TilePump;
import ct.buildcraft.lib.block.BlockBCTile_Neptune;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class BlockPump extends BlockBCTile_Neptune implements EntityBlock {

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TilePump(pos,state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> bet) {
		return bet == BCFactoryBlocks.ENTITYBLOCKPUMP.get() ? ($0, pos, $1, tile) -> {
			if(tile instanceof TilePump be)
				be.update();
		} : null;
	}

	
	
	
	
	
}
