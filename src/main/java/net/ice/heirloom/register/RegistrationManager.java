package net.ice.heirloom.register;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.ice.heirloom.register.autoregister.AutoRegister;
import net.ice.heirloom.register.autoregister.Registerable;
import org.tinylog.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationManager {

    private final Map<Class<?>, Registry<?>> registries = new HashMap<>();

    public <T extends Registerable<T, R>, R extends Registry<T>> void openRegistry(Class<T> type, R registry) {
        registries.put(type, registry);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void register(String root) {
        try(ScanResult scanResult = new ClassGraph().enableClassInfo().enableAnnotationInfo().acceptPackages(root).scan()) {
            for(ClassInfo classInfo : scanResult.getClassesWithAnnotation(AutoRegister.class)) {
                Class<?> loadedClass = classInfo.loadClass();
                try {
                    Registerable registerable = (Registerable) loadedClass.getDeclaredConstructor().newInstance();

                    Class<?> targetInterface = findRegisterableInterface(loadedClass);

                    if(targetInterface == null) {
                        Logger.error("Class [{}] annotated by @AutoRegister but does not implement known Registerable subinterface.", loadedClass.getCanonicalName());
                    }

                    Registry registry = registries.get(targetInterface);
                    if (registry == null) {
                        Logger.error("No active registry found for [{}]", targetInterface.getName());
                    }

                    registerable.register(registry);
                } catch (ClassCastException e) {
                    Logger.error("Class [{}] annotated by @AutoRegister but does not implement known Registerable subinterface.", loadedClass.getCanonicalName());
                }
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> findRegisterableInterface(Class<?> clazz) {
        for(Class<?> iface : clazz.getInterfaces()) {
            if (Registerable.class.isAssignableFrom(iface) && iface != Registerable.class) {
                //iFace - the new product from apple
                //ugly as fuck?
                //no job?
                //no bitches?
                //fucking loser?
                //parents disappointed in you?
                //the iFace will NOT fix any of that
                //starting at $4,000,000
                return iface;
            }
        }
        if(clazz.getSuperclass() != null) {
            return findRegisterableInterface(clazz.getSuperclass());
        }
        return null;
    }

}
