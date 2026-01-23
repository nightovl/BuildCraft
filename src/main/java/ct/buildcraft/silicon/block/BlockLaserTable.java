/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.silicon.block;

import ct.buildcraft.api.enums.EnumLaserTableType;
import ct.buildcraft.api.mj.ILaserTargetBlock;
import ct.buildcraft.lib.block.BlockBCTile_Neptune;
import ct.buildcraft.lib.tile.TileBC_Neptune;
import ct.buildcraft.silicon.tile.TileAdvancedCraftingTable;
import ct.buildcraft.silicon.tile.TileAssemblyTable;
import ct.buildcraft.silicon.tile.TileChargingTable;
import ct.buildcraft.silicon.tile.TileIntegrationTable;
import ct.buildcraft.silicon.tile.TileProgrammingTable_Neptune;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockLaserTable extends BlockBCTile_Neptune implements ILaserTargetBlock {
    private final EnumLaserTableType type;
    
    private static final VoxelShape BOUNDING_BOX = Shapes.box(0 / 16D, 0 / 16D, 0 / 16D, 16 / 16D, 9 / 16D, 16 / 16D);

    public BlockLaserTable(EnumLaserTableType type) {
        super();
        this.type = type;
    }

    @Override
	public boolean isCollisionShapeFullBlock(BlockState p_181242_, BlockGetter p_181243_, BlockPos p_181244_) {
		return false;
	}
    
	@Override
	public boolean isOcclusionShapeFullBlock(BlockState p_222959_, BlockGetter p_222960_, BlockPos p_222961_) {
		return false;
	}

 /*   @Override
    public RenderType getBlockLayer() {
        return RenderType.CUTOUT;
    }*/

    @Override
    public TileBC_Neptune newBlockEntity(BlockPos pos, BlockState state) {
        switch(type) {
            case ASSEMBLY_TABLE:
                return new TileAssemblyTable(pos, state);
            case ADVANCED_CRAFTING_TABLE:
                return new TileAdvancedCraftingTable(pos, state);
            case INTEGRATION_TABLE:
                return new TileIntegrationTable(pos, state);
            case CHARGING_TABLE:
                return new TileChargingTable(pos, state);
            case PROGRAMMING_TABLE:
                return new TileProgrammingTable_Neptune(pos, state);
        }
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext p_51174_) {
        return BOUNDING_BOX;
    }

    @Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
    	if(world.getBlockEntity(pos) instanceof TileBC_Neptune tile) {
    		tile.onActivated(player, hand, hit);
    	}
    	return InteractionResult.SUCCESS;
	}

}
