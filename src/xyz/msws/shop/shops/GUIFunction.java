package xyz.msws.shop.shops;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.msws.shop.utils.MSG;

public interface GUIFunction {
	enum Type {
		BUY(BuyFunction.class), SELL(null), COMMAND(null), OPEN_GUI(null), CLOSE(null), PLAYSOUND(SoundFunction.class),
		GOTO(GotoFunction.class);

		private Class<? extends GUIFunction> fClass;

		Type(Class<? extends GUIFunction> fClass) {
			this.fClass = fClass;
		}

		public Class<? extends GUIFunction> getFunctionClass() {
			return fClass;
		}

		@SuppressWarnings("unchecked")
		public <T extends GUIFunction> T createFunction(Object... value) {
			try {
				Class<?> clazz = null;
				Constructor<?> constructor = null;
				Map<Integer, Class<?>> casts = new HashMap<Integer, Class<?>>();
				int p = 0;
				constructor = getFunctionClass().getConstructors()[0];
				for (Class<?> t : constructor.getParameterTypes()) {
					clazz = (Class<?>) t;
					casts.put(p, clazz);
					p++;
				}
				Object[] params = new Object[casts.size()];
				List<CItem> items = new ArrayList<>();

				for (Entry<Integer, Class<?>> entry : casts.entrySet()) {
					Class<?> c = entry.getValue();
					Object v = value[entry.getKey()];
					if (c.equals(Number.class)) {
						try {
							params[entry.getKey()] = NumberFormat.getInstance().parse(value[entry.getKey()].toString());
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (c.equals(Sound.class)) {
						params[entry.getKey()] = Sound.valueOf((String) v);
					} else if (c.equals(CItem.class)) {
						params[entry.getKey()] = new CItem((String) v);
					} else if (c.equals(CItem[].class)) {
						for (int i = 1; i < value.length; i++) {
							items.add(new CItem((String) value[i]));
						}
					} else {
						params[entry.getKey()] = entry.getValue().cast(v);
					}
				}
				if (items.isEmpty())
					return (T) constructor.newInstance(params);
				MSG.log("Item array size: " + items.size());
				params[1] = items.toArray(new CItem[(items.size())]);
				return (T) constructor.newInstance(params);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	void execute(InventoryClickEvent event);

}
