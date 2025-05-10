package net.ice.relic.engine.opengl.shader;

import java.util.*;

public class ShaderAssembler {
    private final Map<String, ShaderModule> modules = new HashMap<>();
    private final Set<String> includedModules = new LinkedHashSet<>();

    public void registerModule(ShaderModule module) {
        modules.put(module.getName(), module);
    }

    public void include(String moduleName) {
        if (!modules.containsKey(moduleName)) {
            throw new IllegalArgumentException("Module not found: " + moduleName);
        }
        includedModules.add(moduleName);
    }

    public String assemble(String baseSource) {
        StringBuilder assembled = new StringBuilder();

        // Inject #define flags for included modules
        for (String module : includedModules) {
            assembled.append("#define ").append(module.toUpperCase()).append("\n");
        }
        assembled.append("\n");

        // Add module sources
        for (String module : includedModules) {
            assembled.append("// --- Module: ").append(module).append(" ---\n");
            assembled.append(modules.get(module).getCode()).append("\n\n");
        }

        // Finally, add the base shader source
        assembled.append("// --- Base Shader ---\n");
        assembled.append(baseSource);

        return assembled.toString();
    }
}