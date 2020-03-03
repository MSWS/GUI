package xyz.msws.gui.functions.items;

import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.msws.gui.guis.CItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface ItemFunction {
    void execute(InventoryClickEvent event);

    enum Type {
        BUY(BuyFunction.class), SELL(SellFunction.class), COMMAND(CommandFunction.class), OPEN_GUI(null), CLOSE(null), PLAYSOUND(SoundFunction.class),
        GOTO(GotoFunction.class);

        private Class<? extends ItemFunction> fClass;

        Type(Class<? extends ItemFunction> fClass) {
            this.fClass = fClass;
        }

        public Class<? extends ItemFunction> getFunctionClass() {
            return fClass;
        }

        @SuppressWarnings("unchecked")
        public <T extends ItemFunction> T createFunction(Object... value) {
            try {
                Class<?> clazz;
                Constructor<?> constructor;
                Map<Integer, Class<?>> casts = new HashMap<>();
                int p = 0;
                constructor = getFunctionClass().getConstructors()[0];
                for (Class<?> t : constructor.getParameterTypes()) {
                    clazz = t;
                    casts.put(p, clazz);
                    p++;
                }
                Object[] params = new Object[casts.size()];
                List<CItem> items = new ArrayList<>();
                List<String> strings = new ArrayList<>();

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
                    } else if (c.equals(CommandFunction.CommandType.class)) {
                        params[entry.getKey()] = CommandFunction.CommandType.valueOf((String) v);
                    } else if (c.equals(String[].class)) {
                        for (int i = 1; i < value.length; i++) {
                            strings.add((String) value[i]);
                        }
                    } else {
                        params[entry.getKey()] = entry.getValue().cast(v);
                    }
                }
                if (params.length >= 2 && params[1] == null) {
                    if (!items.isEmpty())
                        params[1] = items.toArray(new CItem[(items.size())]);
                    if (!strings.isEmpty())
                        params[1] = strings.toArray(new String[strings.size()]);
                }

                if (items.isEmpty())
                    return (T) constructor.newInstance(params);
                return (T) constructor.newInstance(params);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | SecurityException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
