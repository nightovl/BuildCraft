package ct.buildcraft.silicon;

import java.util.EnumMap;

import ct.buildcraft.silicon.plug.PluggablePulsar;
import ct.buildcraft.api.enums.EnumRedstoneChipset;
import ct.buildcraft.builders.BCBuilders;
import ct.buildcraft.lib.item.ItemPluggableSimple;
import ct.buildcraft.silicon.item.ItemPluggableFacade;
import ct.buildcraft.silicon.item.ItemPluggableGate;
import ct.buildcraft.silicon.item.ItemPluggableLens;
import ct.buildcraft.silicon.item.ItemRedstoneChipset;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BCSiliconItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BCBuilders.MODID);

    public static final EnumMap<EnumRedstoneChipset, ItemRedstoneChipset> REDSTONE_CHIPSET_ITEMS = new EnumMap<>(EnumRedstoneChipset.class);
    public static final RegistryObject<ItemPluggableGate> PLUG_GATE_ITEM = ITEMS.register("plug/gate", ItemPluggableGate::new); 
    public static final RegistryObject<ItemPluggableFacade> PLUG_FACADE_ITEM = ITEMS.register("plug/facade", ItemPluggableFacade::new); 
    public static final RegistryObject<ItemPluggableLens> PLUG_LENS_ITEM = ITEMS.register("plug/lens", ItemPluggableLens::new); 
    public static final RegistryObject<ItemPluggableSimple> PLUG_LIGHT_SENSOR_ITEM = ITEMS.register("plug/light_sensor", () -> new ItemPluggableSimple(BCSiliconPlugs.lightSensor, new Item.Properties())); 
    public static final RegistryObject<ItemPluggableSimple> PLUG_PULSAR_ITEM = ITEMS.register("plug/pulsar", () -> new ItemPluggableSimple(BCSiliconPlugs.pulsar, PluggablePulsar::new, ItemPluggableSimple.PIPE_BEHAVIOUR_ACCEPTS_RS_POWER, new Item.Properties())); 

    
    public static void registry(IEventBus b) {
    	ITEMS.register(b);
    	for(EnumRedstoneChipset type : EnumRedstoneChipset.values()) {
    		createChipset(type);
    	}
    }
    
    private static void createChipset(EnumRedstoneChipset type) {
    	ITEMS.register("redstone_chipset/"+type.getSerializedName(), () -> new ItemRedstoneChipset(type));

    }
}
