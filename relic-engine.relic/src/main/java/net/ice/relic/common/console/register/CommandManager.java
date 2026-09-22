package net.ice.relic.common.console.register;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.ice.heirloom.register.autoregister.AutoRegister;
import net.ice.relic.RelicApplication;

import java.lang.reflect.InvocationTargetException;

public class CommandManager {

    public CommandManager(RelicApplication application) {
        registerInternal();

        String rootPackage = application.getClass().getPackageName();
        try(ScanResult scanResult = new ClassGraph().enableClassInfo().enableAnnotationInfo().acceptPackages(rootPackage).scan()) {
            for(ClassInfo classInfo : scanResult.getClassesWithAnnotation(AutoRegister.class)) {
                Command command = (Command) classInfo.loadClass().getDeclaredConstructor().newInstance();

                command.register(new CommandRegistry());
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerInternal() {
        try(ScanResult scanResult = new ClassGraph().enableClassInfo().enableAnnotationInfo().acceptPackages("net.ice.relic").scan()) {
            for(ClassInfo classInfo : scanResult.getClassesWithAnnotation(AutoRegister.class)) {
                Command command = (Command) classInfo.loadClass().getDeclaredConstructor().newInstance();

                command.register(new CommandRegistry());
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

}
