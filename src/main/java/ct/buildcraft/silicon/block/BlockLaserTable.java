/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.silicon.block;

import buildcraft.silicon.BCSiliconGuis;
import buildcraft.silicon.tile.TileAdvancedCraftingTable;
import buildcraft.silicon.tile.TileAssemblyTable;
import buildcraft.silicon.tile.TileChargingTable;
import buildcraft.silicon.tile.TileIntegrationTable;
import buildcraft.silicon.tile.TileProgrammingTable_Neptune;
import ct.buildcraft.api.enums.EnumLaserTableType;
import ct.buildcraft.api.mj.ILaserTargetBlock;
import ct.buildcraft.lib.block.BlockBCTile_Neptune;
import ct.buildcraft.lib.tile.TileBC_Neptune;
import net.minecraft.block.state.IBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.World;
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
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }*/

    @Override
    public TileBC_Neptune newBlockEntity(BlockPos pos, BlockState state) {
        switch(type) {
            case ASSEMBLY_TABLE:
                return new TileAssemblyTable();
            case ADVANCED_CRAFTING_TABLE:
                return new TileAdvancedCraftingTable();
            case INTEGRATION_TABLE:
                return new TileIntegrationTable();
            case CHARGING_TABLE:
                return new TileChargingTable();
            case PROGRAMMING_TABLE:
                return new TileProgrammingTable_Neptune();
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
    	switch(type) {
        case ASSEMBLY_TABLE:
            if (!world.isClientSide) {
                //BCSiliconGuis.ASSEMBLY_TABLE.openGUI(player, pos);
            }
            return true;
        case ADVANCED_CRAFTING_TABLE:
            if (!world.isClientSide) {
                //BCSiliconGuis.ADVANCED_CRAFTING_TABLE.openGUI(player, pos);
            }
            return true;
        case INTEGRATION_TABLE:
            if (!world.isClientSide) {
               // BCSiliconGuis.INTEGRATION_TABLE.openGUI(player, pos);
            }
            return true;
        case CHARGING_TABLE:
        case PROGRAMMING_TABLE:
    }
    return InteractionResult.CONSUME;
	}

}
