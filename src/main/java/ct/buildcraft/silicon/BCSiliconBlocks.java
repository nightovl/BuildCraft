package ct.buildcraft.silicon;

import ct.buildcraft.api.enums.EnumLaserTableType;
import ct.buildcraft.factory.block.BlockPump;
import ct.buildcraft.silicon.block.BlockLaser;
import ct.buildcraft.silicon.block.BlockLaserTable;
import ct.buildcraft.silicon.tile.TileAssemblyTable;
import ct.buildcraft.silicon.tile.TileLaser;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BCSiliconBlocks {
	
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BCSilicon.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITYS = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BCSilicon.MODID);
    public static final RegistryObject<Block> LASER_BLOCK = BLOCKS.register("laser", BlockLaser::new);
    public static final RegistryObject<Block> ASSEMBLY_TABLE_BLOCK = BLOCKS.register("assembly_table", () -> new BlockLaserTable(EnumLaserTableType.ASSEMBLY_TABLE));
    
    public static final RegistryObject<BlockEntityType<TileLaser>> LASER_TILE = BLOCK_ENTITYS.register("entity_laser", 
    		() -> BlockEntityType.Builder.of(TileLaser::new,LASER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileAssemblyTable>> ASSEMBLY_TABLE_TILE = BLOCK_ENTITYS.register("entity_assembly_table", 
    		() -> BlockEntityType.Builder.of(TileAssemblyTable::new,ASSEMBLY_TABLE_BLOCK.get()).build(null));
    
    public static void registry(IEventBus b) {
    	BLOCKS.register(b);
    	BLOCK_ENTITYS.register(b);
    }
}
