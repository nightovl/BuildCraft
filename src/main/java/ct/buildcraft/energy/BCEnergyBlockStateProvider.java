package ct.buildcraft.energy;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BCEnergyBlockStateProvider extends BlockStateProvider{

	public BCEnergyBlockStateProvider(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, BCEnergy.MODID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		
		
	}


	
}
