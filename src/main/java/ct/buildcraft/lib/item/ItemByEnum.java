package ct.buildcraft.lib.item;

import java.util.EnumMap;
import javax.annotation.Nullable;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

public class ItemByEnum<E extends Enum<E> & StringRepresentable> extends Item{

	protected final E type;
	protected final EnumMap<E, ItemByEnum<E>> map;
	
	public ItemByEnum(Properties p_40566_, E type, @Nullable EnumMap<E, ItemByEnum<E>> map) {
		super(p_40566_);
		this.type = type;
		this.map = map;
		if(map != null&&type!=null)
			map.put(type, this);
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if(this.allowedIn(tab)) {
			list.add(new ItemStack(this));}
	}

	@Override
	public Component getName(ItemStack p_41458_) {
		return Component.translatable(getDescriptionId()/*+"_" + type.getSerializedName()*/);
	}
	
	public E getType() {
		return type;
	}
	
	public @Nullable EnumMap<E, ItemByEnum<E>> getMap(){
		return map;
	}
	
	public static <E extends Enum<E> & StringRepresentable, P extends ItemByEnum<?>> EnumMap<E, P> creatItems(IEunmItemCreator<E, P> creator, Properties p, E[] types, Class<E> clazz, String id, DeferredRegister<Item> register) {
		EnumMap<E, P> items = new EnumMap<>(clazz);
		for(final E e : types) {
			register.register(id+"/"+e.getSerializedName(),()->{
				P i = creator.create(p, e, items);
				items.put(e, i);
				return i;
			});
		}
		//items.forEach(register);
		return items;
	}
	
}
