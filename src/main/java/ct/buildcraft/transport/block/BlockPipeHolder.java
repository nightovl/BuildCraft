/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.transport.block;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import ct.buildcraft.api.blocks.ICustomPaintHandler;
import ct.buildcraft.api.core.BCLog;
import ct.buildcraft.api.core.EnumPipePart;
import ct.buildcraft.api.transport.EnumWirePart;
import ct.buildcraft.api.transport.IItemPluggable;
import ct.buildcraft.api.transport.WireNode;
import ct.buildcraft.api.transport.pipe.IPipeHolder;
import ct.buildcraft.api.transport.pipe.PipeApi;
import ct.buildcraft.api.transport.pipe.PipeDefinition;
import ct.buildcraft.api.transport.pluggable.PipePluggable;
import ct.buildcraft.api.transport.pluggable.PluggableModelKey;
import ct.buildcraft.lib.block.BlockBCTile_Neptune;
import ct.buildcraft.lib.client.sprite.SingleSpriteSet;
import ct.buildcraft.lib.misc.AdvancementUtil;
import ct.buildcraft.lib.misc.BoundingBoxUtil;
import ct.buildcraft.lib.misc.InventoryUtil;
import ct.buildcraft.lib.misc.SpriteUtil;
import ct.buildcraft.lib.misc.VecUtil;
import ct.buildcraft.transport.BCTransportBlocks;
import ct.buildcraft.transport.BCTransportItems;
import ct.buildcraft.transport.client.model.PipeModelCacheBase;
import ct.buildcraft.transport.client.model.PipeModelCachePluggable;
import ct.buildcraft.transport.client.render.PipeWireRenderer;
import ct.buildcraft.transport.item.ItemWire;
import ct.buildcraft.transport.pipe.Pipe;
import ct.buildcraft.transport.tile.TilePipeHolder;
import ct.buildcraft.transport.wire.EnumWireBetween;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

public class BlockPipeHolder extends BlockBCTile_Neptune implements ICustomPaintHandler, EntityBlock, IClientBlockExtensions {

	public static final VoxelShape BOX_CENTER = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
	public static final VoxelShape BOX_DOWN = Shapes.box(0.25D, 0, 0.25D, 0.75D, 0.25D, 0.75D);
	public static final VoxelShape BOX_UP = Shapes.box(0.25D, 0.75D, 0.25, 0.75D, 1D, 0.75D);
	public static final VoxelShape BOX_NORTH = Shapes.box(0.25D, 0.25D, 0, 0.75D, 0.75D, 0.25D);
	public static final VoxelShape BOX_SOUTH = Shapes.box(0.25D, 0.25D, 0.75D, 0.75D, 0.75D, 1D);
	public static final VoxelShape BOX_WEST = Shapes.box(0, 0.25D, 0.25D, 0.25D, 0.75D, 0.75D);
	public static final VoxelShape BOX_EAST = Shapes.box(0.75D, 0.25D, 0.25D, 1D, 0.75D, 0.75D);
	public static final VoxelShape[] BOX_FACES = { BOX_DOWN, BOX_UP, BOX_NORTH, BOX_SOUTH, BOX_WEST, BOX_EAST };
	
    private static final VoxelShape[] PIPE_SHAPE_CACHE = new VoxelShape[64];

	
	private static final SingleSpriteSet spriteSet = new SingleSpriteSet(null);

	private static final ResourceLocation ADVANCEMENT_LOGIC_TRANSPORTATION = new ResourceLocation(
			"buildcrafttransport:logic_transportation");

	public BlockPipeHolder() {
		super(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.25f)
				.explosionResistance(3.0f));
	}

	// ticks
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lev, BlockState bs, BlockEntityType<T> bet) {
		return bet == BCTransportBlocks.PIPE_HOLDER_BE.get() ? ($0, pos, $1, BlockEntity) -> {
			if (BlockEntity instanceof TilePipeHolder) {
				((TilePipeHolder) BlockEntity).update();
			}
		} : null;
	}

	// basics

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TilePipeHolder(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState p_60550_) {
		return RenderShape.MODEL;
	}

	public VoxelShape getVisualShape(BlockState p_48735_, BlockGetter p_48736_, BlockPos p_48737_,
			CollisionContext p_48738_) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
		return Shapes.empty();
	}

	@Override
	public boolean propagatesSkylightDown(BlockState p_48740_, BlockGetter p_48741_, BlockPos p_48742_) {
		return true;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext p_60575_) {
//		return getShape(state, level, pos, CollisionContext.empty());
		return getInteractionShape(state, level, pos);
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState p_181242_, BlockGetter p_181243_, BlockPos p_181244_) {
		return false;
	}

	@Override
	public boolean isOcclusionShapeFullBlock(BlockState p_222959_, BlockGetter p_222960_, BlockPos p_222961_) {
		return false;
	}

	@Override
	public boolean hasDynamicShape() {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext p_60558_) {
		TilePipeHolder tile = getPipe(world, pos, false);
		if (tile == null) {
			return Shapes.block();
		}
		Minecraft mc = Minecraft.getInstance();
		HitResult h = mc.hitResult;
		if (!(h instanceof BlockHitResult))
			return Shapes.block();
		Vec3 carmpos = mc.cameraEntity.getEyePosition();
		Vec3 location = ((BlockHitResult) h).getLocation();
		BCBlockHitResult trace = rayTrace(world, pos, carmpos, location.add(location.subtract(carmpos).normalize()));
		int part = trace.subhit;
//        BCLog.logger.debug(""+part);
//        BCLog.logger.debug(trace.getLocation().toString());
		/*
		 * BlockHitResult trace = (BlockHitResult)h; int part =
		 * computHitFacing(trace.getLocation().subtract(pos.getX(), pos.getY(),
		 * pos.getZ()));
		 */

		if (part < 0 /* || !pos.equals(((BlockHitResult)h).getBlockPos()) */) {
			// Perhaps we aren't the object the mouse is over
			return Shapes.block();
		}
		VoxelShape aabb = Shapes.block();
		if (part == 0) {
			aabb = BOX_CENTER;
		} else if (part < 1 + 6) {
			aabb = BOX_FACES[part - 1];
			Pipe pipe = tile.getPipe();
			if (pipe != Pipe.EMPTY) {
				Direction face = Direction.values()[part - 1];
				float conSize = pipe.getConnectedDist(face);
				if (conSize > 0 && conSize != 0.25f) {
					Vec3 center = VecUtil.offset(new Vec3(0.5, 0.5, 0.5), face, 0.25 + (conSize / 2));
					Vec3 radius = new Vec3(0.25, 0.25, 0.25);
					radius = VecUtil.replaceValue(radius, face.getAxis(), conSize / 2);
					Vec3 min = center.subtract(radius);
					Vec3 max = center.add(radius);
					aabb = Shapes.create(BoundingBoxUtil.makeFrom(min, max));
				}
			}
		} else if (part < 1 + 6 + 6) {
			Direction side = Direction.values()[part - 1 - 6];
			PipePluggable pluggable = tile.getPluggable(side);
			if (pluggable != PipePluggable.EMPTY) {
				aabb = pluggable.getBoundingBox();
			}
		} else if (part < 1 + 6 + 6 + 8) {
			EnumWirePart wirePart = EnumWirePart.VALUES[part - 1 - 6 - 6];
			aabb = wirePart.boundingBox;
		} else if (part < 1 + 6 + 6 + 6 + 8 + 36) {
			EnumWireBetween wireBetween = EnumWireBetween.VALUES[part - 1 - 6 - 6 - 8];
			aabb = wireBetween.boundingBox;
		}
		if (part >= 1 + 6 + 6) {
			return Shapes.empty();
		} else {
			return (aabb == Shapes.block() ? aabb : Shapes.create(aabb.bounds().inflate(1 / 32.0)));
		}

	}

	@Nullable
	public BCBlockHitResult rayTrace(BlockGetter world, BlockPos pos, Player player) {
		Vec3 start = player.getEyePosition();// .add(0, player.getEyeHeight(), 0);
		// .getPositionVector().addVector(0, player.getEyeHeight(), 0);
		double reachDistance = 5;
		if (player instanceof ServerPlayer) {
			reachDistance = ((ServerPlayer) player).getReachDistance();
		}
		Vec3 end = start.add(player.getLookAngle().normalize().scale(reachDistance));
		/*
		 * BCLog.logger.debug(start.toString()); BCLog.logger.debug(end.toString());
		 * BCLog.logger.debug(pos.toShortString()); BCLog.logger.debug("" +
		 * player.getEyeHeight());
		 */
		return rayTrace(world, pos, start, end);
	}

	@Nullable
	public BCBlockHitResult rayTrace(BlockGetter world, BlockPos pos, Vec3 start, Vec3 end) {
		TilePipeHolder tile = getPipe(world, pos, false);
		if (tile == null) {
			return new BCBlockHitResult(Shapes.block().clip(start, end, pos), 400);
		}

		BlockHitResult result = getInteractionShape(world.getBlockState(pos), world, pos).clip(start, end, pos);
		if (result == null) {
//        	BCLog.logger.error("A error fired in BlockPipeHolder#rayTrace : the clip result was null!");
			return new BCBlockHitResult(Shapes.block().clip(start, end, pos), 400);
		}
		int subHit = computSubhit(tile, result.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()));
		if (subHit == 0) {
//        	BCLog.logger.error("A error fired in BlockPipeHolder#rayTrace : the clip result was null!");
			return new BCBlockHitResult(Shapes.block().clip(start, end, pos), 0);
		}
		return new BCBlockHitResult(result, subHit);/**
													 * This is the original method adapted into 1.19.2(Have not been
													 * test!) A quick method has replaced it
													 */
		/*
		 * TilePipeHolder tile = getPipe(world, pos, false); if (tile == null) { return
		 * new BCBlockHitResult(Shapes.block().clip(end, start, pos), 400); }
		 * BCBlockHitResult best = null; Pipe pipe = tile.getPipe(); boolean computed =
		 * false; if (pipe != null) { computed = true; best = computeTageTrace(best,
		 * pos, start, end, BOX_CENTER, 0); for (Direction face : Direction.values()) {
		 * float conSize = pipe.getConnectedDist(face); if (conSize > 0) { VoxelShape
		 * aabb = BOX_FACES[face.get3DDataValue()]; if (conSize != 0.25f) { Vec3 center
		 * = VecUtil.offset(new Vec3(0.5, 0.5, 0.5), face, 0.25 + (conSize / 2)); Vec3
		 * radius = new Vec3(0.25, 0.25, 0.25); radius = VecUtil.replaceValue(radius,
		 * face.getAxis(), conSize / 2); Vec3 min = center.subtract(radius); Vec3 max =
		 * center.add(radius); aabb = Shapes.create(BoundingBoxUtil.makeFrom(min, max));
		 * } best = computeTageTrace(best, pos, start, end, aabb, face.ordinal() + 1); }
		 * } } for (Direction face : Direction.values()) { PipePluggable pluggable =
		 * tile.getPluggable(face); if (pluggable != null) { VoxelShape bb =
		 * pluggable.getBoundingBox(); best = computeTageTrace(best, pos, start, end,
		 * bb, face.ordinal() + 1 + 6); computed = true; } } for (EnumWirePart part :
		 * tile.getWireManager().parts.keySet()) { best = computeTageTrace(best, pos,
		 * start, end, part.boundingBox, part.ordinal() + 1 + 6 + 6); computed = true; }
		 * for (EnumWireBetween between : tile.getWireManager().betweens.keySet()) {
		 * best = computeTageTrace(best, pos, start, end, between.boundingBox,
		 * between.ordinal() + 1 + 6 + 6 + 8); computed = true; } if (!computed) {
		 * return computeTageTrace(null, pos, start, end, Shapes.block(), 400); }
		 * if(best == null) { BCLog.logger.
		 * error("A error fired in BlockPipeHolder#rayTrace : the clip result was null!"
		 * ); return new BCBlockHitResult(Shapes.block().clip(start, end, pos), 400); }
		 * return best;
		 */
	}

	@Nullable
	public static EnumWirePart rayTraceWire(BlockPos pos, Vec3 start, Vec3 end) {
		Vec3 realStart = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		Vec3 realEnd = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		EnumWirePart best = null;
		double dist = 1000;
		for (EnumWirePart part : EnumWirePart.VALUES) {
			// to Debug
			BlockHitResult trace = part.boundingBoxPossible.clip(realStart, realEnd, BlockPos.ZERO);
			if (trace != null) {
				if (best == null) {
					best = part;
					dist = trace.getLocation().distanceToSqr(realStart);
				} else {
					double nextDist = trace.getLocation().distanceToSqr(realStart);
					if (dist > nextDist) {
						best = part;
						dist = nextDist;
					}
				}
			}
		}
		return best;
	}

	/*
	 * private BCBlockHitResult computeTageTrace(BCBlockHitResult lastBest, BlockPos
	 * pos, Vec3 start, Vec3 end, AABB aabb, int part) { Vec3 v = aabb.clip(start,
	 * end).get(); if (v == null) { return lastBest; } BlockHitResult next = new
	 * BlockHitResult(v, null, pos, false); BCBlockHitResult result = new
	 * BCBlockHitResult(next, part); if (lastBest == null) { return result; } double
	 * distLast = lastBest.result.getLocation().x - (start.x); double distNext = v.x
	 * - (start.x); return distLast > distNext ? result : lastBest; }
	 */

	private static int computHitFacing(Vec3 clip) {
		double x = clip.x;
		double y = clip.y;
		double z = clip.z;
//		BCLog.logger.debug(clip.toString());
		if (x > 0.75 + 1.0E-7D) {// East
//			BCLog.logger.debug("East");
			return 6;
		}
		if (x < 0.25 - 1.0E-7D) {// West
//			BCLog.logger.debug("West");
			return 5;
		}
		if (z > 0.75 + 1.0E-7D) {// South
//			BCLog.logger.debug("South");
			return 4;
		}
		if (z < 0.25 - 1.0E-7D) {// North
//			BCLog.logger.debug("North");
			return 3;
		}
		if (y > 0.75 + 1.0E-7D) {// Up
//			BCLog.logger.debug("Up");
			return 2;
		}
		if (y < 0.25 - 1.0E-7D) {// down
//			BCLog.logger.debug("Down");
			return 1;
		}
//		BCLog.logger.debug("Center");
		return 0;
	}

	private static int computSubhit(TilePipeHolder tile, Vec3 clip) {
		int hitFacing = computHitFacing(clip);
		if (hitFacing == 0)
			return 0;
		Direction face = Direction.from3DDataValue(hitFacing - 1);
//		int subHit = hitFacing;
		PipePluggable pluggable = tile.getPluggable(face);
		if (pluggable != PipePluggable.EMPTY) {
//			VoxelShape bb = pluggable.getBoundingBox();
			return hitFacing += 6;
		}
		if (!tile.getWireManager().parts.keySet().isEmpty()/*EnumWirePart part : tile.getWireManager().parts.keySet()*/) {
			return hitFacing += 12;
		}
		if (!tile.getWireManager().betweens.keySet().isEmpty()) {
			return hitFacing += 20;
		}
		return hitFacing;
	}

	@Nullable
	public static Direction getPartSideHit(Direction facing, int part) {
		if (part <= 0) {
			return facing;
		}
		if (part <= 6) {
			return Direction.values()[part - 1];
		}
		if (part <= 6 + 6) {
			return Direction.values()[part - 1 - 6];
		}
		return null;
	}

	@Nullable
	public static EnumWirePart getWirePartHit(int part) {
		if (part <= 6 + 6) {
			return null;
		} else if (part <= 6 + 6 + 8) {
			return EnumWirePart.VALUES[part - 1 - 6 - 6];
		} else {
			return null;
		}
	}

	@Nullable
	public static EnumWireBetween getWireBetweenHit(int part) {
		if (part <= 6 + 6 + 8) {
			return null;
		} else if (part <= 6 + 6 + 8 + EnumWireBetween.VALUES.length) {
			return EnumWireBetween.VALUES[part - 1 - 6 - 6 - 8];
		} else {
			return null;
		}
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
		TilePipeHolder tile = getPipe(world, pos, false);
		if (tile == null) {
			return BOX_CENTER;
		}
		Pipe pipe = tile.getPipe();
		VoxelShape shape = BOX_CENTER;
		boolean computed = false;
		if (pipe != Pipe.EMPTY) {
			
			computed = true;
			boolean canUseCache = true;
			Direction[] direTogen = new Direction[6];
			float[] conSizes = new float[6];
			int len = 0;
			
			
			for(int i=0;i<6;i++) {
				Direction d = Direction.values()[i];
				conSizes[len] = pipe.getConnectedDist(Direction.values()[i]);
				if(conSizes[len]>0) {
					canUseCache &= conSizes[len] == 0.25f;
					direTogen[len++] = d;
				}
			}
			if(canUseCache)
				shape = getCachedPipeShape(direTogen, len);
			else
				for (int i = 0;i < len; i++) {
					Direction face = direTogen[i];
					if (conSizes[i] > 0) {
						VoxelShape aabb = BOX_FACES[face.get3DDataValue()];
						if (conSizes[i] != 0.25f) {
							Vec3 center = VecUtil.offset(new Vec3(0.5, 0.5, 0.5), face, 0.25 + (conSizes[i] / 2));
							Vec3 radius = new Vec3(0.25, 0.25, 0.25);
							radius = VecUtil.replaceValue(radius, face.getAxis(), conSizes[i] / 2);
							Vec3 min = center.subtract(radius);
							Vec3 max = center.add(radius);
							aabb = Shapes.create(BoundingBoxUtil.makeFrom(min, max));
						}
						shape = Shapes.or(shape, aabb);
					}
				}
		}
		for (Direction face : Direction.values()) {
			PipePluggable pluggable = tile.getPluggable(face);
			if (pluggable != PipePluggable.EMPTY) {
				VoxelShape bb = pluggable.getBoundingBox();
				shape = Shapes.or(shape, bb);
				computed = true;
			}
		}
		for (EnumWirePart part : tile.getWireManager().parts.keySet()) {
			shape = Shapes.or(shape, part.boundingBox);
			computed = true;
		}
		for (EnumWireBetween between : tile.getWireManager().betweens.keySet()) {
			shape = Shapes.or(shape, between.boundingBox);
			computed = true;
		}
		if (computed)
			return shape;
		return BOX_CENTER;
	}

	// @Override
	public ItemStack getPickBlock(BlockState state, BlockHitResult target, Level world, BlockPos pos, Player player) {
		TilePipeHolder tile = getPipe(world, pos, false);
		if (tile == null || target == null) {
			return ItemStack.EMPTY;
		}
		int subHit = computSubhit(tile, target.getLocation());
		if (subHit <= 6) {
			Pipe pipe = tile.getPipe();
			if (pipe != Pipe.EMPTY) {
				PipeDefinition def = pipe.getDefinition();
				Item item = (Item) PipeApi.pipeRegistry.getItemForPipe(def);
				if (item != null) {
					CompoundTag tag = new CompoundTag();
					tag.putInt("color", pipe.getColour() == null ? 0 : pipe.getColour().getId() + 1);
					ItemStack stack = new ItemStack(item, 1);
					stack.setTag(tag);
					return stack;
				}
			}
		} else if (subHit <= 12) {
			int pluggableHit = subHit - 7;
			Direction face = Direction.values()[pluggableHit];
			PipePluggable plug = tile.getPluggable(face);
			if (plug != PipePluggable.EMPTY) {
				return plug.getPickStack();
			}
		} else {
			EnumWirePart part = null;
			EnumWireBetween between = null;

			if (subHit > 6) {
				part = getWirePartHit(subHit);
				between = getWireBetweenHit(subHit);
			}
			if (part != null && tile.wireManager.getColorOfPart(part) != null) {
				return new ItemStack(BCTransportItems.wires.get(tile.wireManager.getColorOfPart(part)), 1);
			} else if (between != null && tile.wireManager.getColorOfPart(between.parts[0]) != null) {
				return new ItemStack(BCTransportItems.wires.get(tile.wireManager.getColorOfPart(between.parts[0])), 1);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult re) {
		TilePipeHolder tile = getPipe(world, pos, false);
		if (tile == null)
			return InteractionResult.PASS;
		Vec3 carmpos = player.getEyePosition();
		Vec3 location = re.getLocation();
		BCBlockHitResult trace = rayTrace(world, pos, carmpos, location.add(location.subtract(carmpos).normalize()));
		int subHit = trace.subhit;
		Direction realSide = subHit == 0 ? re.getDirection() : getPartSideHit(re.getDirection(), subHit);
		if (realSide == null)
			realSide = re.getDirection();
		BCLog.logger.debug(realSide.getName());
		if (subHit > 6 && subHit <= 12) {
			PipePluggable existing = tile.getPluggable(realSide);
			if (existing != PipePluggable.EMPTY)
				return existing.onPluggableActivate(player, re, world);
		}

		EnumPipePart part = subHit == 0 ? EnumPipePart.CENTER : EnumPipePart.fromFacing(realSide);

		ItemStack held = player.getItemInHand(hand);
		Item item = held.isEmpty() ? null : held.getItem();
		PipePluggable existing = tile.getPluggable(realSide);
		if (item instanceof IItemPluggable && existing == PipePluggable.EMPTY) {
			IItemPluggable itemPlug = (IItemPluggable) item;
			PipePluggable plug = itemPlug.onPlace(held, tile, realSide, player, hand);
			if (plug == PipePluggable.EMPTY) {
				return InteractionResult.PASS;
			} else {
				tile.replacePluggable(realSide, plug);
				plug.onPlacedBy(player);
				if (!player.isCreative()) {
					held.shrink(1);
				}
				return InteractionResult.SUCCESS;
			}
		}
		if (item instanceof ItemWire wire) {
			EnumWirePart wirePartHit = getWirePartHit(subHit);
			EnumWirePart wirePart;
			TilePipeHolder attachTile = tile;
			if (wirePartHit != null) {
				WireNode node = new WireNode(pos, wirePartHit);
				node = node.offset(re.getDirection());
				wirePart = node.part;
				if (!node.pos.equals(pos)) {
					attachTile = getPipe(world, node.pos, false);
				}
			} else {
				wirePart = EnumWirePart.get((re.getLocation().x + 1) % 1 > 0.5, (re.getLocation().y % 1 + 1) % 1 > 0.5,
						(re.getLocation().z % 1 + 1) % 1 > 0.5);
			}
			if (wirePart != null && attachTile != null) {
				DyeColor colour = wire.getType();
				boolean attached = attachTile.getWireManager().addPart(wirePart, colour);
				attachTile.scheduleNetworkUpdate(IPipeHolder.PipeMessageReceiver.WIRES);
				if (attached) {
					WireNode from = new WireNode(attachTile.getPipePos(), wirePart);

					boolean isNowConnected = false;
					for (Direction dir : Direction.values()) {
						WireNode to = from.offset(dir);
						if (to.pos == attachTile.getPipePos()) {
							if (attachTile.getWireManager().getColorOfPart(to.part) == colour) {
								isNowConnected = true;
								break;
							}
						} else {
							BlockEntity localTile = attachTile.getLocalTile(to.pos);
							if (localTile instanceof TilePipeHolder) {
								if (((TilePipeHolder) localTile).getWireManager().getColorOfPart(to.part) == colour) {
									isNowConnected = true;
									break;
								}
							}
						}
					}
					if (isNowConnected) {
						AdvancementUtil.unlockAdvancement(player, ADVANCEMENT_LOGIC_TRANSPORTATION);
					}

					if (!player.isCreative()) {
						held.shrink(1);
					}
				}
				if (attached) {
					return InteractionResult.SUCCESS;
				}
			}
		}
		Pipe pipe = tile.getPipe();
		if (pipe == Pipe.EMPTY) {
			return InteractionResult.PASS;
		}
		if (pipe.behaviour.onPipeActivate(player, re, world, part)) {
			return InteractionResult.SUCCESS;
		}
		if (pipe.flow.onFlowActivate(player, re, world, part)) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest,
			FluidState fluid) {
		if (world.isClientSide()) {
			// return super.onDestroyedByPlayer(state, world, pos, player, willHarvest,
			// fluid);
			this.spawnDestroyParticles(world, player, pos, state);
			world.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
			return false;
		}

		TilePipeHolder tile = getPipe(world, pos, false);
		if (tile == null) {
			return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
		}

		NonNullList<ItemStack> toDrop = NonNullList.create();
		BCBlockHitResult t = rayTrace(world, pos, player);
		int subHit = t.subhit;
		BlockHitResult trace = t.result;
		Direction side = null;
		EnumWirePart part = null;
		EnumWireBetween between = null;

		if (trace != null && subHit > 6) {
			side = getPartSideHit(trace.getDirection(), subHit);
			part = getWirePartHit(subHit);
			between = getWireBetweenHit(subHit);
		}

		if (side != null) {
			removePluggable(side, tile, toDrop);
			if (!player.isCreative()) {
				InventoryUtil.dropAll(world, pos, toDrop);
			}
			return false;
		} else if (part != null) {
			ItemStack stack = new ItemStack(BCTransportItems.wires.get(tile.wireManager.getColorOfPart(part)), 1);
			toDrop.add(stack);
			tile.wireManager.removePart(part);
			if (!player.isCreative()) {
				InventoryUtil.dropAll(world, pos, toDrop);
			}
			tile.scheduleNetworkUpdate(IPipeHolder.PipeMessageReceiver.WIRES);
			return false;
		} else if (between != null) {
			ItemStack stack = new ItemStack(BCTransportItems.wires.get(tile.wireManager.getColorOfPart(between.parts[0])), between.to == null ? 2 : 1);
			toDrop.add(stack);
			if (between.to == null) {
				tile.wireManager.removeParts(Arrays.asList(between.parts));
			} else {
				tile.wireManager.removePart(between.parts[0]);
			}
			if (!player.getAbilities().instabuild) {
				InventoryUtil.dropAll(world, pos, toDrop);
			}
			tile.scheduleNetworkUpdate(IPipeHolder.PipeMessageReceiver.WIRES);
			return false;
		} else {
//  		toDrop.addAll(getDrops(state, (ServerLevel) world, pos, null));
            for (Direction face : Direction.values()) {
                removePluggable(face, tile, toDrop);
            }
		}
		if (!player.isCreative()) {
			InventoryUtil.dropAll(world, pos, toDrop);
		}
//	        LogUtils.getLogger().debug("destorybyp "+world.isClientSide);
		return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, Builder builder) {
		NonNullList<ItemStack> toDrop = NonNullList.create();
		TilePipeHolder tile = getPipe(builder.getLevel(),
				new BlockPos(builder.getOptionalParameter(LootContextParams.ORIGIN)), false);
		if (tile == null) {
			BCLog.logger.debug(getDescriptionId() + ": the BlockEntity in "
					+ builder.getOptionalParameter(LootContextParams.ORIGIN) + " can not be null!");
			return toDrop;
		}
		for (Direction face : Direction.values()) {
			PipePluggable pluggable = tile.getPluggable(face);
			if (pluggable != null) {
				pluggable.addDrops(toDrop, 1);// 1 is meaningless
			}
		}
		for (DyeColor color : tile.wireManager.parts.values()) {
			ItemStack stack = new ItemStack(BCTransportItems.wires.get(color), 1);
			toDrop.add(stack);
		}
		Pipe pipe = tile.getPipe();
		if (pipe != null) {
			pipe.addDrops(toDrop, 1);// 1 is meaningless
		}
		return toDrop;
	}

	@Override
	public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
		Entity exploder = explosion.getExploder();
		if (exploder != null) {
			Vec3 subtract = exploder.position().subtract(Vec3.atLowerCornerOf(pos).add(VecUtil.VEC_HALF)).normalize();
			Direction side = Arrays.stream(Direction.values())
					.min(Comparator.comparing(facing -> Vec3.atLowerCornerOf(facing.getNormal()).distanceTo(subtract)))
					.orElseThrow(IllegalArgumentException::new);
			TilePipeHolder tile = getPipe(level, pos, true);
			if (tile != null) {
				PipePluggable pluggable = tile.getPluggable(side);
				if (pluggable != PipePluggable.EMPTY) {
					float explosionResistance = pluggable.getExplosionResistance(exploder, explosion);
					if (explosionResistance > 0) {
						return explosionResistance;
					}
				}
			}
		}
		return super.getExplosionResistance(state, level, pos, explosion);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		TilePipeHolder tile = getPipe(world, pos, false);
		if (tile == null) {
			return;
		}
		Pipe pipe = tile.getPipe();
		if (pipe != Pipe.EMPTY) {
			pipe.getBehaviour().onEntityCollide(entity);
		}
	}

	@Override
	public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity be,
			ItemStack stack) {
		player.awardStat(Stats.BLOCK_MINED.get(this));
		player.causeFoodExhaustion(0.005F);
	}

	public boolean canBeConnectedTo(BlockGetter world, BlockPos pos, Direction facing) {
		TilePipeHolder tile = getPipe(world, pos, false);
		if (tile == null) {
			return false;
		}
		PipePluggable pluggable = tile.getPluggable(facing);
		return pluggable != null && pluggable.canBeConnected();
	}

	private static void removePluggable(Direction side, TilePipeHolder tile, NonNullList<ItemStack> toDrop) {
		PipePluggable removed = tile.replacePluggable(side, PipePluggable.EMPTY);
		if (removed != PipePluggable.EMPTY) {
			removed.onRemove();
			removed.addDrops(toDrop, 0);
		}
	}

	public static TilePipeHolder getPipe(BlockGetter access, BlockPos pos, boolean requireServer) {
		if (access instanceof Level) {
			return getPipe((Level) access, pos, requireServer);
		}
		if (requireServer) {
			return null;
		}
		BlockEntity tile = access.getBlockEntity(pos);
		if (tile instanceof TilePipeHolder) {
			return (TilePipeHolder) tile;
		}
		return null;
	}

	public static TilePipeHolder getPipe(Level world, BlockPos pos, boolean requireServer) {
		if (requireServer && world.isClientSide()) {
			return null;
		}
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof TilePipeHolder) {
			return (TilePipeHolder) tile;
		}
		return null;
	}

	// Block overrides

	@Override
	public boolean addLandingEffects(BlockState state, ServerLevel worldObj, BlockPos blockPosition,
			BlockState BlockState, LivingEntity entity, int numberOfParticles) {
		BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, BlockState).setPos(blockPosition);
		worldObj.sendParticles(particle, entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, (double)0.15F);
		return true;
	}
	
	
	
	
	/*
	 * private static HitSpriteInfo getHitSpriteInfo(BlockHitResult target,
	 * TilePipeHolder pipeHolder) { int p = computSubhit(pipeHolder,
	 * target.getLocation()); VoxelShape aabb = null; TextureAtlasSprite sprite =
	 * SpriteUtil.missingSprite(); if (0 <= p && p <= 6) { aabb = p == 0 ?
	 * BOX_CENTER : BOX_FACES[p - 1]; PipeDefinition def =
	 * pipeHolder.getPipe().definition; TextureAtlasSprite[] sprites =
	 * PipeModelCacheBase.generator.getItemSprites(def); sprite = sprites.length ==
	 * 0 ? SpriteUtil.missingSprite() : sprites[0]; } else if (6 + 1 <= p && p < 6 +
	 * 6 + 1) { PipePluggable plug = pipeHolder.getPluggable(Direction.values()[p -
	 * 6 - 1]); if (plug == PipePluggable.EMPTY) { return null; } aabb =
	 * plug.getBoundingBox(); if (aabb == null) { return null; } PluggableModelKey
	 * keyC = plug.getModelRenderKey(RenderType.cutout()); PluggableModelKey keyT =
	 * plug.getModelRenderKey(RenderType.translucent()); if (keyC == null && keyT ==
	 * null) { return null; } List<BakedQuad> quads = null; if (keyC != null) quads
	 * = PipeModelCachePluggable.cacheCutoutSingle.bake(keyC); if (quads == null ||
	 * quads.isEmpty()) { if (keyT == null) { return null; } quads =
	 * PipeModelCachePluggable.cacheTranslucentSingle.bake(keyT); if (quads == null
	 * || quads.isEmpty()) { return null; } } sprite = quads.get(0).getSprite(); }
	 * else if (6 + 6 + 1 <= p && p < 1 + 6 + 6 + 8) { EnumWirePart wirePart =
	 * EnumWirePart.values()[p - 6 - 6 - 1]; aabb = wirePart.boundingBox; DyeColor
	 * colour = pipeHolder.getWireManager().getColorOfPart(wirePart); if (colour ==
	 * null) { return null; } sprite =
	 * PipeWireRenderer.getWireSprite(colour).getSprite(); } else if (6 + 6 + 1 + 8
	 * < p && p <= 6 + 6 + 1 + 8 + 36) { EnumWireBetween wireBetween =
	 * EnumWireBetween.values()[p - 6 - 6 - 1 - 8]; aabb = wireBetween.boundingBox;
	 * DyeColor colour = pipeHolder.getWireManager().betweens.get(wireBetween); if
	 * (colour == null) { return null; } sprite =
	 * PipeWireRenderer.getWireSprite(colour).getSprite(); } else { return null; }
	 * if (aabb == null) { throw new IllegalStateException("Null aabb for index " +
	 * p + " (and sprite " + sprite + ")"); } return new HitSpriteInfo(aabb,
	 * sprite); }
	 */

	@Override
	public void stepOn(Level p_152431_, BlockPos p_152432_, BlockState p_152433_, Entity p_152434_) {
		super.stepOn(p_152431_, p_152432_, p_152433_, p_152434_);
	}

	@Override
	public void attack(BlockState p_60499_, Level p_60500_, BlockPos p_60501_, Player p_60502_) {
		super.attack(p_60499_, p_60500_, p_60501_, p_60502_);
	}
	
    private static HitSpriteInfo getHitSpriteInfo(BlockHitResult target, TilePipeHolder pipeHolder) {
    	BlockPos pos = pipeHolder.getBlockPos();
        int p = computSubhit(pipeHolder, target.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()));
        VoxelShape aabb = null;
        TextureAtlasSprite sprite = SpriteUtil.missingSprite();
        if (0 <= p && p <= 6) {
            aabb = p == 0 ? BOX_CENTER : BOX_FACES[p - 1];
            PipeDefinition def = pipeHolder.getPipe().definition;
            TextureAtlasSprite[] sprites = PipeModelCacheBase.generator.getItemSprites(def);
            sprite = sprites.length == 0 ? SpriteUtil.missingSprite() : sprites[0];
        } else if (6 + 1 <= p && p < 6 + 6 + 1) {
            PipePluggable plug = pipeHolder.getPluggable(Direction.values()[p - 6 - 1]);
            if (plug == null) {
                return null;
            }
            aabb = plug.getBoundingBox();
            if (aabb == null) {
                return null;
            }
            PluggableModelKey keyC = plug.getModelRenderKey(RenderType.cutout());
            PluggableModelKey keyT = plug.getModelRenderKey(RenderType.translucent());
            if (keyC == null && keyT == null) {
                return null;
            }
            List<BakedQuad> quads = null;
            if (keyC != null) quads = PipeModelCachePluggable.cacheCutoutSingle.bake(keyC);
            if (quads == null || quads.isEmpty()) {
                if (keyT == null) {
                    return null;
                }
                quads = PipeModelCachePluggable.cacheTranslucentSingle.bake(keyT);
                if (quads == null || quads.isEmpty()) {
                    return null;
                }
            }
            sprite = quads.get(0).getSprite();
        } else if (6 + 6 + 1 <= p && p < 1 + 6 + 6 + 8) {
            EnumWirePart wirePart = EnumWirePart.values()[p - 6 - 6 - 1];
            aabb = wirePart.boundingBox;
            DyeColor colour = pipeHolder.getWireManager().getColorOfPart(wirePart);
            if (colour == null) {
                return null;
            }
            sprite = PipeWireRenderer.getWireSprite(colour).getSprite();
        } else if (6 + 6 + 1 + 8 < p && p <= 6 + 6 + 1 + 8 + 36) {
            EnumWireBetween wireBetween = EnumWireBetween.values()[p - 6 - 6 - 1 - 8];
            aabb = wireBetween.boundingBox;
            DyeColor colour = pipeHolder.getWireManager().betweens.get(wireBetween);
            if (colour == null) {
                return null;
            }
            sprite = PipeWireRenderer.getWireSprite(colour).getSprite();
        } else {
            return null;
        }
        if (aabb == null) {
            throw new IllegalStateException("Null aabb for index " + p + " (and sprite " + sprite + ")");
        }
        return new HitSpriteInfo(aabb, sprite);
    }


	@Override
	public boolean addHitEffects(BlockState state, Level world, HitResult hit, ParticleEngine manager) {
		if(!(hit instanceof BlockHitResult target)) 
			return false;
		BlockPos pos = target.getBlockPos();
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof TilePipeHolder) {
			TilePipeHolder pipeHolder = ((TilePipeHolder) te);
			HitSpriteInfo info = getHitSpriteInfo(target, pipeHolder);
			
	            if (info == null) {
	                return false;
	            }

	            double x = Math.random() * (info.aabb.max(Axis.X) - info.aabb.min(Axis.X)) + info.aabb.min(Axis.X);
	            double y = Math.random() * (info.aabb.max(Axis.Y) - info.aabb.min(Axis.Y)) + info.aabb.min(Axis.Y);
	            double z = Math.random() * (info.aabb.max(Axis.Z) - info.aabb.min(Axis.Z)) + info.aabb.min(Axis.Z);
	            
	            int hitface = computHitFacing(target.getLocation());
	            Direction dir = hitface == 0 ? target.getDirection() : Direction.from3DDataValue(hitface - 1);
	            switch (dir) {
	                case DOWN:
	                    y = info.aabb.min(Axis.Y) - 0.1;
	                    break;
	                case UP:
	                    y = info.aabb.max(Axis.Y) + 0.1;
	                    break;
	                case NORTH:
	                    z = info.aabb.min(Axis.Z) - 0.1;
	                    break;
	                case SOUTH:
	                    z = info.aabb.max(Axis.Z) + 0.1;
	                    break;
	                case WEST:
	                    x = info.aabb.min(Axis.X) - 0.1;
	                    break;
	                default:
	                    x = info.aabb.max(Axis.X) + 0.1;
	                    break;
	            }

	            x += pos.getX();
	            y += pos.getY();
	            z += pos.getZ();

	            TerrainParticle particle = new TerrainParticle((ClientLevel) world, x, y, z, 0, 0, 0, state, pos);
	            spriteSet.texture = info.sprite;
	            particle.pickSprite(spriteSet);
	            particle.setPower(0.2f);
	            particle.scale(0.6f);
	            manager.add(particle);
	            return true;
	        }
		return false;

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean addDestroyEffects(BlockState state, Level world, BlockPos pos, ParticleEngine manager) {
		Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;
//        hit = null;
        if (hit == null || !(hit instanceof BlockHitResult hitResult)|| !pos.equals(hitResult.getBlockPos())) {
            return false;
        }
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TilePipeHolder pipeHolder) {
            HitSpriteInfo info = getHitSpriteInfo(hitResult, pipeHolder);
            if (info == null) {
                return false;
            }

            double sizeX = info.aabb.max(Axis.X) - info.aabb.min(Axis.X);
            double sizeY = info.aabb.max(Axis.Y) - info.aabb.min(Axis.Y);
            double sizeZ = info.aabb.max(Axis.Z) - info.aabb.min(Axis.Z);

            int countX = (int) Math.max(2, 4 * sizeX);
            int countY = (int) Math.max(2, 4 * sizeY);
            int countZ = (int) Math.max(2, 4 * sizeZ);

            for (int x = 0; x < countX; x++) {
                for (int y = 0; y < countY; y++) {
                    for (int z = 0; z < countZ; z++) {
                    	
                    	double d4 = ((double)x + 0.5D) / (double)countX;
                        double d5 = ((double)y + 0.5D) / (double)countY;
                        double d6 = ((double)z + 0.5D) / (double)countX;

                        double _x = pos.getX() + info.aabb.min(Axis.X) + (x + 0.5) * sizeX / countX;
                        double _y = pos.getY() + info.aabb.min(Axis.Y) + (y + 0.5) * sizeY / countY;
                        double _z = pos.getZ() + info.aabb.min(Axis.Z) + (z + 0.5) * sizeZ / countZ;

                        TerrainParticle particle = new TerrainParticle((ClientLevel) world, _x, _y, _z, d4 - 0.5, d5 - 0.5, d6 - 0.5, state, pos);
                        spriteSet.texture = info.sprite;
                        particle.pickSprite(spriteSet);
//                        world.addParticle(ParticleTypes.GLOW, sizeX, sizeY, sizeZ, countX, countY, countZ);
                        manager.add(particle);
                    }
                }
            }
            return true;
        }
        return false;
	
	}



	@OnlyIn(Dist.CLIENT)
	private static final class HitSpriteInfo {
		final VoxelShape aabb;
		final TextureAtlasSprite sprite;

		HitSpriteInfo(VoxelShape aabb, TextureAtlasSprite sprite) {
			this.aabb = aabb;
			this.sprite = sprite;
		}
	}

	// paint

	@Override
	public InteractionResult attemptPaint(Level world, BlockPos pos, BlockState state, Vec3 hitPos, Direction hitSide,
			DyeColor paintColour) {
		TilePipeHolder tile = getPipe(world, pos, true);
		if (tile == null) {
			return InteractionResult.PASS;
		}

		Pipe pipe = tile.getPipe();
		if (pipe == Pipe.EMPTY) {
			return InteractionResult.FAIL;
		}
		if (pipe.getColour() == paintColour || !pipe.definition.canBeColoured) {
			return InteractionResult.FAIL;
		} else {
			pipe.setColour(paintColour);
			pipe.getHolder().getPipeTile().requestModelDataUpdate();
			return InteractionResult.SUCCESS;
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return new ItemStack(
				BCTransportItems.PIPE_MAP.get(((TilePipeHolder) level.getBlockEntity(pos)).getPipe().definition).get());
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
		if (side == null)
			return false;
		TilePipeHolder tile = getPipe(world, pos, false);
		if (tile != null) {
			PipePluggable pluggable = tile.getPluggable(side.getOpposite());
			return pluggable != PipePluggable.EMPTY && pluggable.canConnectToRedstone(side);
		}
		return false;
	}

	@Override
	public boolean isSignalSource(BlockState p_60571_) {
		return true;
	}

	@Override
	public int getSignal(BlockState p_60483_, BlockGetter p_60484_, BlockPos p_60485_, Direction p_60486_) {
		return getDirectSignal(p_60483_, p_60484_, p_60485_, p_60486_);
	}

	@Override
	public int getDirectSignal(BlockState p_60559_, BlockGetter blockAccess, BlockPos pos, Direction side) {
		if (side == null) {
			return 0;
		}
		TilePipeHolder tile = getPipe(blockAccess, pos, false);
		if (tile != null) {
			return tile.getRedstoneOutput(side.getOpposite());
		}
		return 0;
	}

	@Override
	public Object getRenderPropertiesInternal() {
		return this;
	}

	/** a wrapper of {@link BlockHitResult} */
	protected static class BCBlockHitResult {
		int subhit;// as RayTraceResult in 1.12.2
		public BlockHitResult result;

		BCBlockHitResult(BlockHitResult result, int subHit) {
			this.result = result;
			this.subhit = subHit;
		}

	}
	
    public static final VoxelShape getCachedPipeShape(Direction[] ds, int len) {
    	int index = 0;
    	for(int i = 0;i<len;i++) {
    		if(ds != null)
    		index+= 1<<ds[i].ordinal();
    	}
    	VoxelShape shape =  PIPE_SHAPE_CACHE[index];
    	if(shape == null) {
    		shape = BOX_CENTER;
    		for(int i = 0;i<len;i++)
    			shape = Shapes.or(shape, BOX_FACES[ds[i].get3DDataValue()]);
    		 PIPE_SHAPE_CACHE[index] = shape;
    	}
    	return shape;
    	
    }
}
