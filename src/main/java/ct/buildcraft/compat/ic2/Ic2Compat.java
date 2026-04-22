package ct.buildcraft.compat.ic2;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import ct.buildcraft.compat.CompatCapTransfromer;
import ct.buildcraft.energy.BCEnergy;
import ct.buildcraft.energy.BCEnergyFluids;
import ct.buildcraft.lib.fluid.BCFluid;
import ic2.api.recipe.Recipes;
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
    
	private static void registerDefaultHandle(Class clazz) {
		FIELD_CACHE.put(clazz, findIc2FluidTankField(clazz));
		CompatCapTransfromer.INSTANCE.registryFluidCapTransform(clazz,
				(tile, d) -> new Ic2TankHandler(getFirstIc2FluidTankField(clazz.cast(tile)), d));
	}

	public static void preInit() {
		registerDefaultHandle(TileEntityGeoGenerator.class);
		registerDefaultHandle(TileEntityOreWashing.class);
		registerDefaultHandle(TileEntitySemifluidGenerator.class);
		registerDefaultHandle(TileEntityFluidHeatGenerator.class);

	}
	
	public static void init() {
		addSemiGenerator(BCEnergyFluids.oilResidue, 3000);
		addSemiGenerator(BCEnergyFluids.crudeOil, 16000);
		addSemiGenerator(BCEnergyFluids.oilDistilled, 10000);
		addSemiGenerator(BCEnergyFluids.oilHeavy, 10000);
		addSemiGenerator(BCEnergyFluids.oilDense, 10000);
		addSemiGenerator(BCEnergyFluids.fuelGaseous, 44992);
		addSemiGenerator(BCEnergyFluids.fuelDense, 128000);
		addSemiGenerator(BCEnergyFluids.fuelMixedLight, 128000);
		addSemiGenerator(BCEnergyFluids.fuelMixedHeavy, 128000);
		addSemiGenerator(BCEnergyFluids.fuelLight, 128000);
		
		addHeatGenerator(BCEnergyFluids.oilResidue, 6);
		addHeatGenerator(BCEnergyFluids.crudeOil, 32);
		addHeatGenerator(BCEnergyFluids.oilDistilled, 32);
		addHeatGenerator(BCEnergyFluids.oilHeavy, 32);
		addHeatGenerator(BCEnergyFluids.oilDense, 32);
		addHeatGenerator(BCEnergyFluids.fuelGaseous, 90);
		addHeatGenerator(BCEnergyFluids.fuelDense, 768);
		addHeatGenerator(BCEnergyFluids.fuelMixedLight, 768);
		addHeatGenerator(BCEnergyFluids.fuelMixedHeavy, 768);
		addHeatGenerator(BCEnergyFluids.fuelLight, 768);
		
		//Recipes.liquidHeatupManager.add
		

	}
	
	private final static void addSemiGenerator(BCFluid[] fluids, int power) {
		Recipes.semiFluidGenerator.addFluid(fluids[0], 1, power/1000f);
		Recipes.semiFluidGenerator.addFluid(fluids[1], 1, power/1000f);
		Recipes.semiFluidGenerator.addFluid(fluids[2], 1, power/1000f);
	}
	
	private final static void addHeatGenerator(BCFluid[] fluids, int power) {
		Recipes.fluidHeatGenerator.addFluid(fluids[0], 10, power);
		Recipes.fluidHeatGenerator.addFluid(fluids[1], 10, power);
		Recipes.fluidHeatGenerator.addFluid(fluids[2], 10, power);
	}
	
	

}
