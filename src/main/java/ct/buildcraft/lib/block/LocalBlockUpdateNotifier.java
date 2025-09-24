package ct.buildcraft.lib.block;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import buildcraft.lib.world.WorldEventListenerAdapter;
import ct.buildcraft.core.BCCoreConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;


/**
 * Listens for BlockUpdates in a given world and notifies all registered IBlockUpdateSubscribers of the update provided
 * it was within the update range of the ILocalBlockUpdateSubscriber
 */
public class LocalBlockUpdateNotifier {

    private static final Map<Level, LocalBlockUpdateNotifier> instanceMap = new WeakHashMap<>();
    private final Set<ILocalBlockUpdateSubscriber> subscriberSet = new HashSet<>();


    private LocalBlockUpdateNotifier(Level world, BlockPos pos) {

    	GameEventListener worldEventListener = new GameEventListener() {
        	@Override
        	public boolean handleEventsImmediately() {
        		return true;
        	}
        	@Override
        	public PositionSource getListenerSource() {
        		return blockPosSource;
        	}
        	@Override
        	public int getListenerRadius() {
        		int limt = BCCoreConfig.miningMaxDepth;
        		int high = worldPosition.getY()+64;
        		return high < limt ? high: limt;
        	}
            @Override
            public void notifyBlockUpdate(@Nonnull Level world, @Nonnull BlockPos eventPos, @Nonnull IBlockState oldState,
                                          @Nonnull BlockState newState, int flags) {
                notifySubscribersInRange(world, eventPos, oldState, newState, flags);
            }
        };
        GameEventDispatcher gameeventdispatcher = world.getChunk(pos).getEventDispatcher(SectionPos.blockToSectionCoord(pos.getY()));//(worldEventListener);
        gameeventdispatcher.register(gameeventlistener);
    }

    /**
     * Gets the LocalBlockUpdateNotifier for the given world
     *
     * @param world the World where BlockUpdate events will be listened for
     * @return the instance of LocalBlockUpdateNotifier for the given world
     */
    public static LocalBlockUpdateNotifier instance(Level world, BlockPos pos) {
        if (!instanceMap.containsKey(world)) {
            instanceMap.put(world, new LocalBlockUpdateNotifier(world, pos));
        }
        return instanceMap.get(world);
    }

    /**
     * Register an @{ILocalBlockUpdateSubscriber} to receive notifications about block updates
     *
     * @param subscriber the subscriber to receive notifications about local block updates
     */
    public void registerSubscriberForUpdateNotifications(ILocalBlockUpdateSubscriber subscriber) {
        subscriberSet.add(subscriber);
    }

    /**
     * Stop an @{ILocalBlockUpdateSubscriber} from receiving notifications about block updates
     *
     * @param subscriber the subscriber to no longer receive notifications about local block update
     */
    public void removeSubscriberFromUpdateNotifications(ILocalBlockUpdateSubscriber subscriber) {
        subscriberSet.remove(subscriber);
    }

    /**
     * Notifies all subscribers near the given position that a world update took place. The distance used to determine
     * if a subscriber is close enough to notify is determined by a call to the subscriber's implementation of
     * getUpdateRange
     *
     * @param world    from the Block Update
     * @param eventPos from the Block Update
     * @param oldState from the Block Update
     * @param newState from the Block Update
     * @param flags    from the Block Update
     */
    private void notifySubscribersInRange(Level world, BlockPos eventPos, BlockState oldState, IBlockState newState,
                                          int flags) {
        for (ILocalBlockUpdateSubscriber subscriber : subscriberSet) {
            BlockPos keyPos = subscriber.getSubscriberPos();
            int updateRange = subscriber.getUpdateRange();
            if (Math.abs(keyPos.getX() - eventPos.getX()) <= updateRange &&
                    Math.abs(keyPos.getY() - eventPos.getY()) <= updateRange &&
                    Math.abs(keyPos.getZ() - eventPos.getZ()) <= updateRange) {
                subscriber.setWorldUpdated(world, eventPos, oldState, newState, flags);
            }
        }
    }

}
