package xyz.msws.shop.shops;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface GUIFunction {
	enum Type {
		BUY(BuyFunction.class), SELL(null), COMMAND(null), OPEN_GUI(null), CLOSE(null), PLAYSOUND(null);

		private Class<? extends GUIFunction> fClass;

		Type(Class<? extends GUIFunction> fClass) {
			this.fClass = fClass;
		}

		public Class<? extends GUIFunction> getFunctionClass() {
			return fClass;
		}

		@SuppressWarnings("unchecked")
		public <T extends GUIFunction> T createFunction(Object value) {
			try {
				Class<?> clazz = null;
				Constructor<?> constructor = null;
				for (Constructor<?> c : getFunctionClass().getConstructors()) {
					for (Class<?> t : c.getParameterTypes()) {
						clazz = (Class<?>) t;
					}
					constructor = (Constructor<?>) c;
				}
				return (T) constructor.newInstance(clazz.cast(value));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	void execute(InventoryClickEvent event);

}
