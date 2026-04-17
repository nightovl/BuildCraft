package ct.buildcraft.api.blocks;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import ct.buildcraft.api.core.BCDebugging;
import ct.buildcraft.api.core.BCLog;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/** Provides a simple way to paint a single block, iterating through all {@link ICustomPaintHandler}'s that are
 * registered for the block. */
public enum CustomPaintHelper {
    INSTANCE;

    /* If you want to test your class-based rotation registration then add the system property
     * "-Dbuildcraft.api.rotation.debug.class=true" to your launch. */
    private static final boolean DEBUG = BCDebugging.shouldDebugLog("api.painting");

    private final java.util.Map<Class<? extends Block>, List<ICustomPaintHandler>> handlers = Maps.newIdentityHashMap();
    private final List<ICustomPaintHandler> allHandlers = Lists.newArrayList();

    private enum ColourFamily {
        STAINED_GLASS("_stained_glass"),
        STAINED_GLASS_PANE("_stained_glass_pane"),
        TERRACOTTA("_terracotta"),
        GLAZED_TERRACOTTA("_glazed_terracotta"),
        WOOL("_wool"),
        CARPET("_carpet"),
        CONCRETE("_concrete"),
        CONCRETE_POWDER("_concrete_powder"),
        CANDLE("_candle"),
        CANDLE_CAKE("_candle_cake"),
        BANNER("_banner"),
        WALL_BANNER("_wall_banner"),
        SHULKER_BOX("_shulker_box"),
        BED("_bed");

        final String suffix;

        ColourFamily(String suffix) {
            this.suffix = suffix;
        }
    }

    /** Registers a handler that will be called LAST for ALL blocks, if all other paint handlers have returned PASS or
     * none are registered for that block. */
    public void registerHandlerForAll(ICustomPaintHandler handler) {
        if (DEBUG) {
            BCLog.logger.info("[api.painting] Adding a paint handler for ALL blocks (" + handler.getClass() + ")");
        }
        allHandlers.add(handler);
    }

    public void registerHandler(Class<? extends Block> block, ICustomPaintHandler handler) {
        if (registerHandlerInternal(block, handler)) {
            if (DEBUG) {
                BCLog.logger.info("[api.painting] Setting a paint handler for block " + block.getClass().getSimpleName() + "(" + handler.getClass() + ")");
            }
        } else if (DEBUG) {
            BCLog.logger.info("[api.painting] Adding another paint handler for block " + block.getClass().getSimpleName() + "(" + handler.getClass() + ")");
        }
    }

    private boolean registerHandlerInternal(Class<? extends Block> block, ICustomPaintHandler handler) {
        if (!handlers.containsKey(block)) {
            List<ICustomPaintHandler> forBlock = Lists.newArrayList();
            forBlock.add(handler);
            handlers.put(block, forBlock);
            return true;
        } else {
            handlers.get(block).add(handler);
            return false;
        }
    }

    /** Attempts to paint a block at the given position. Basically iterates through all registered paint handlers. */
    public InteractionResult attemptPaintBlock(Level world, BlockPos pos, BlockState state, Vec3 hitPos,
        @Nullable Direction hitSide, @Nullable DyeColor paint) {
        Block block = state.getBlock();
        Class<? extends Block> blockClass = block.getClass();
        if (block instanceof ICustomPaintHandler) {
            return ((ICustomPaintHandler) block).attemptPaint(world, pos, state, hitPos, hitSide, paint);
        }
        List<ICustomPaintHandler> custom = handlers.get(blockClass);
        if (custom == null || custom.isEmpty()) {
            return defaultAttemptPaint(world, pos, state, hitPos, hitSide, paint);
        }
        for (ICustomPaintHandler handler : custom) {
            InteractionResult result = handler.attemptPaint(world, pos, state, hitPos, hitSide, paint);
            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        return defaultAttemptPaint(world, pos, state, hitPos, hitSide, paint);
    }

    private InteractionResult defaultAttemptPaint(Level world, BlockPos pos, BlockState state, Vec3 hitPos,
        @Nullable Direction hitSide, @Nullable DyeColor paint) {
        for (ICustomPaintHandler handler : allHandlers) {
            InteractionResult result = handler.attemptPaint(world, pos, state, hitPos, hitSide, paint);
            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        if (paint == null) {
            return InteractionResult.FAIL;
        }
        return recolorBlock(world, pos, state, paint) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    public static boolean recolorBlock(Level world, BlockPos pos, DyeColor paint) {
        return recolorBlock(world, pos, world.getBlockState(pos), paint);
    }

    public static boolean recolorBlock(Level world, BlockPos pos, BlockState state, DyeColor paint) {
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (key == null) {
            return false;
        }

        ColourFamily family = detectFamily(state, key.getPath());
        if (family == null) {
            return false;
        }

        Block targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key.getNamespace(), paint.getName() + family.suffix));
        if (targetBlock == null) {
            return false;
        }

        if (family == ColourFamily.BED) {
            return replaceBed(world, pos, targetBlock);
        }
        return replaceSingleBlock(world, pos, state, targetBlock);
    }

    private static @Nullable ColourFamily detectFamily(BlockState state, String path) {
        if (state.getBlock() instanceof BedBlock && path.endsWith("_bed")) {
            return ColourFamily.BED;
        }
        if (path.equals("glass") || path.endsWith("_stained_glass")) {
            return ColourFamily.STAINED_GLASS;
        }
        if (path.equals("glass_pane") || path.endsWith("_stained_glass_pane")) {
            return ColourFamily.STAINED_GLASS_PANE;
        }
        if (path.endsWith("_glazed_terracotta")) {
            return ColourFamily.GLAZED_TERRACOTTA;
        }
        if (path.equals("terracotta") || path.endsWith("_terracotta")) {
            return ColourFamily.TERRACOTTA;
        }
        if (path.equals("wool") || path.endsWith("_wool")) {
            return ColourFamily.WOOL;
        }
        if (path.equals("carpet") || path.endsWith("_carpet")) {
            return ColourFamily.CARPET;
        }
        if (path.equals("concrete_powder") || path.endsWith("_concrete_powder")) {
            return ColourFamily.CONCRETE_POWDER;
        }
        if (path.equals("concrete") || path.endsWith("_concrete")) {
            return ColourFamily.CONCRETE;
        }
        if (path.equals("candle_cake") || path.endsWith("_candle_cake")) {
            return ColourFamily.CANDLE_CAKE;
        }
        if (path.equals("candle") || path.endsWith("_candle")) {
            return ColourFamily.CANDLE;
        }
        if (path.equals("wall_banner") || path.endsWith("_wall_banner")) {
            return ColourFamily.WALL_BANNER;
        }
        if (path.equals("banner") || path.endsWith("_banner")) {
            return ColourFamily.BANNER;
        }
        if (path.equals("shulker_box") || path.endsWith("_shulker_box")) {
            return ColourFamily.SHULKER_BOX;
        }
        return null;
    }

    private static boolean replaceSingleBlock(Level world, BlockPos pos, BlockState oldState, Block targetBlock) {
        BlockState newState = copySharedProperties(oldState, targetBlock.defaultBlockState());
        if (oldState.getBlock() == newState.getBlock()) {
            return false;
        }

        CompoundTag blockEntityTag = getBlockEntityTag(world, pos, oldState);
        if (!world.setBlock(pos, newState, 3)) {
            return false;
        }
        restoreBlockEntity(world, pos, newState, blockEntityTag);
        world.sendBlockUpdated(pos, oldState, newState, 3);
        world.blockUpdated(pos, newState.getBlock());
        world.updateNeighborsAt(pos, newState.getBlock());
        return true;
    }

    private static boolean replaceBed(Level world, BlockPos clickedPos, Block targetBlock) {
        if (!(targetBlock instanceof BedBlock)) {
            return false;
        }

        BlockState clickedState = world.getBlockState(clickedPos);
        if (!(clickedState.getBlock() instanceof BedBlock)) {
            return false;
        }

        Direction facing = clickedState.getValue(BedBlock.FACING);
        BedPart clickedPart = clickedState.getValue(BedBlock.PART);
        BlockPos footPos = clickedPart == BedPart.FOOT ? clickedPos : clickedPos.relative(facing.getOpposite());
        BlockPos headPos = clickedPart == BedPart.HEAD ? clickedPos : clickedPos.relative(facing);

        BlockState footState = world.getBlockState(footPos);
        BlockState headState = world.getBlockState(headPos);
        if (!(footState.getBlock() instanceof BedBlock) || !(headState.getBlock() instanceof BedBlock)) {
            return false;
        }

        BlockState newFootState = copySharedProperties(footState, targetBlock.defaultBlockState());
        BlockState newHeadState = copySharedProperties(headState, targetBlock.defaultBlockState());

        if (footState.getBlock() == newFootState.getBlock() && headState.getBlock() == newHeadState.getBlock()) {
            return false;
        }

        if (!world.setBlock(footPos, newFootState, 0)) {
            return false;
        }
        if (!world.setBlock(headPos, newHeadState, 0)) {
            world.setBlock(footPos, footState, 0);
            return false;
        }

        world.sendBlockUpdated(footPos, footState, newFootState, 3);
        world.sendBlockUpdated(headPos, headState, newHeadState, 3);
        world.blockUpdated(footPos, newFootState.getBlock());
        world.blockUpdated(headPos, newHeadState.getBlock());
        world.updateNeighborsAt(footPos, newFootState.getBlock());
        world.updateNeighborsAt(headPos, newHeadState.getBlock());
        return true;
    }

    private static BlockState copySharedProperties(BlockState source, BlockState target) {
        BlockState result = target;
        for (Property<?> property : source.getProperties()) {
            if (result.getProperties().contains(property)) {
                result = copyPropertyUnchecked(source, result, property);
            }
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static BlockState copyPropertyUnchecked(BlockState source, BlockState target, Property property) {
        return target.setValue(property, (Comparable) source.getValue(property));
    }

    private static @Nullable CompoundTag getBlockEntityTag(Level world, BlockPos pos, BlockState state) {
        if (!state.hasBlockEntity()) {
            return null;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity == null ? null : blockEntity.saveWithFullMetadata();
    }

    private static void restoreBlockEntity(Level world, BlockPos pos, BlockState state, @Nullable CompoundTag tag) {
        if (tag == null || !state.hasBlockEntity()) {
            return;
        }
        CompoundTag blockEntityTag = tag.copy();
        blockEntityTag.putInt("x", pos.getX());
        blockEntityTag.putInt("y", pos.getY());
        blockEntityTag.putInt("z", pos.getZ());
        BlockEntity blockEntity = BlockEntity.loadStatic(pos, state, blockEntityTag);
        if (blockEntity != null) {
            blockEntity.setLevel(world);
            world.setBlockEntity(blockEntity);
            BlockEntity placed = world.getBlockEntity(pos);
            if (placed != null) {
                placed.setChanged();
            }
        }
    }
}
