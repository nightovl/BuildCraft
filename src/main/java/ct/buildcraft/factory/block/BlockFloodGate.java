package ct.buildcraft.factory.block;

import java.util.HashMap;
import java.util.Map;

import ct.buildcraft.api.properties.BuildCraftProperties;
import ct.buildcraft.factory.BCFactoryBlocks;
import ct.buildcraft.factory.tile.EntityBlockFloodGate;
import ct.buildcraft.factory.tile.TileFloodGate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;

public class BlockFloodGate extends Block implements EntityBlock{
    public static final Map<Direction, BooleanProperty> CONNECTED_MAP;

    static {
        CONNECTED_MAP = new HashMap<>(BuildCraftProperties.CONNECTED_MAP);
        CONNECTED_MAP.remove(Direction.UP);
    }
//    public static final BooleanProperty connected_up = BooleanProperty.create("connected_up");
//    public static final BooleanProperty connected_down = BooleanProperty.create("connected_down");
//    public static final BooleanProperty connected_east = BooleanProperty.create("connected_east");
//    public static final BooleanProperty connected_west = BooleanProperty.create("connected_west");
//    public static final BooleanProperty connected_south = BooleanProperty.create("connected_south");
//    public static final BooleanProperty connected_north = BooleanProperty.create("connected_north");
	public BlockFloodGate() {
		super(BlockBehaviour.Properties.of(Material.METAL).strength(25.0f).explosionResistance(10.0f));
		// TODO Auto-generated constructor stub
//		this.registerDefaultState(
//				  this.stateDefinition.any()
//				    .setValue(connected_up, false)
//				    .setValue(connected_down, false)
//				    .setValue(connected_east, false)
//				    .setValue(connected_west, false)
//				    .setValue(connected_south, false)
//				    .setValue(connected_north, false)
//				);
	}
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> bs) {
//		super.createBlockStateDefinition(bs);
//		bs.add(connected_up);
//		bs.add(connected_down);
//		bs.add(connected_east);
//		bs.add(connected_west);
//		bs.add(connected_south);
//		bs.add(connected_north);
	}
	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		// TODO Auto-generated method stub
		return BCFactoryBlocks.ENTITYBLOCKFLOODGATE.get().create(p_153215_, p_153216_);
	}
	@Override
	public InteractionResult use(BlockState bs, Level lev, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		if(!FluidUtil.interactWithFluidHandler(player, hand, lev, pos, hit.getDirection())) {
    		return InteractionResult.PASS;
		}
		else return InteractionResult.SUCCESS;
	}
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lev, BlockState bs,
			BlockEntityType<T> bet) {
		return bet == BCFactoryBlocks.ENTITYBLOCKFLOODGATE.get() ? ($0,pos,$1,BlockEntity) -> {
			if(BlockEntity instanceof TileFloodGate) {
				((TileFloodGate) BlockEntity).update();
			}
		} : null;

	}

	
	
	

}
