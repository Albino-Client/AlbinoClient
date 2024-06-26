package ir.albino.client.features.modules;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.stream.JsonReader;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import ir.albino.client.AlbinoClient;
import ir.albino.client.features.modules.configuration.ModuleTheme;
import ir.albino.client.features.modules.settings.ModuleSetting;
import ir.albino.client.features.modules.settings.Setting;
import ir.albino.client.utils.Common;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModuleManager {

    private ModuleTheme currentTheme;


    public void initModules() {
        final LinkedList<Module> modules = new LinkedList<>();
        if (!Common.getModulesPath().exists()) Common.getModulesPath().mkdirs();
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
            for (ClassInfo clz : scanResult.getClassesWithAnnotation(ModuleInfo.class)) {

                val loadClass = clz.loadClass();
                val annotation = loadClass.getAnnotation(ModuleInfo.class);

                final Module module;
                module = (Module) loadClass.getConstructor().newInstance();
                module.setSettings(Arrays.stream(loadClass.getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(Setting.class)).map(f -> {
                            try {
                                return (ModuleSetting<?>) f.get(module);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }).collect(Collectors.toList()));

                module.setName(annotation.module());
                module.setDescription(annotation.description());
                module.setVersion(annotation.version());
                module.setDraggable(annotation.draggable());
                module.onInit();

                if (AlbinoClient.instance.debug)
                    AlbinoClient.instance.getLogger().info(String.format("Module Loaded %s - %s", annotation.module(), annotation.version()));

                modules.add(module);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        //Sorting the Modules as Length -> Higher to lower
        modules.sort(Comparator.comparing(module -> Minecraft.getMinecraft().fontRendererObj.getStringWidth(module.getName())));
        Collections.reverse(modules);
        AlbinoClient.instance.modules.addAll(modules);
    }

    public Module getModuleByName(String name) {
        val module = AlbinoClient.instance.modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
        return module.orElse(null);
    }

    public List<Module> getEnabledModules() {
        return AlbinoClient.instance.modules.stream().filter(Module::isEnabled).collect(Collectors.toList());
    }

    public ModuleTheme getModuleTheme() {
        if (currentTheme == null) {
            currentTheme = new ModuleTheme();
        }
        return currentTheme;
    }

}
