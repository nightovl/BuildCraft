package ct.buildcraft.compat.ic2;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import ct.buildcraft.compat.CompatCapTransfromer;
import ic2.core.block.comp.Fluids;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import ic2.core.block.machine.tileentity.TileEntityOreWashing;

public class Ic2Compat {

    private static final ConcurrentHashMap<Class<?>, Field> FIELD_CACHE = new ConcurrentHashMap<>();
    
    public static Fluids getFirstIc2FluidTankField(Object obj) {
        Class<?> clazz = obj.getClass();
        Field field = FIELD_CACHE.get(clazz);
        try {
            return (Fluids) field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static Field findIc2FluidTankField(Class<?> clazz) {
        Class<?> current = clazz;
        
        for (Field field : current.getDeclaredFields()) {
        	if (Fluids.class.isAssignableFrom(field.getType())) {
        		field.setAccessible(true);
        		return field;
            }
        }
        throw new NoSuchFieldError("Can't find Ic2FluidTank field in "+ clazz.getSimpleName());
    }

	public static void init() {
		registerDefaultHandle(TileEntityGeoGenerator.class);
		registerDefaultHandle(TileEntityOreWashing.class);
		registerDefaultHandle(TileEntitySemifluidGenerator.class);
		registerDefaultHandle(TileEntityFluidHeatGenerator.class);

		
	}
	
	private static void registerDefaultHandle(Class clazz) {
		FIELD_CACHE.put(clazz, findIc2FluidTankField(clazz));
		CompatCapTransfromer.INSTANCE.registryFluidCapTransform(clazz,
				(tile, d) -> new Ic2TankHandler(getFirstIc2FluidTankField(clazz.cast(tile)), d));
	}
}
