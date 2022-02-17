package fr.farmvivi.discordbot.module;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.farmvivi.discordbot.Bot;
import fr.farmvivi.discordbot.module.commands.CommandsModule;
import fr.farmvivi.discordbot.module.music.MusicModule;
import fr.farmvivi.discordbot.module.test.TestModule;

public class ModulesManager {
    public static final Logger logger = LoggerFactory.getLogger(ModulesManager.class);

    private final Bot bot;

    private final Map<Modules, Module> modules = new HashMap<Modules, Module>();

    public ModulesManager(Bot bot) {
        this.bot = bot;
    }

    public void loadModules() {
        logger.info("Loading modules...");

        for (String moduleName : bot.getConfiguration().features) {
            if (moduleName.length() == 0)
                continue;

            Modules module;
            try {
                module = Modules.valueOf(moduleName);
            } catch (IllegalArgumentException e) {
                logger.warn("Module " + moduleName + " not found");
                continue;
            }

            loadModule(module);
        }

        logger.info("Modules loaded");
    }

    public void loadModule(Modules moduleType) {
        for (Modules requiredModule : moduleType.getRequiredModules())
            loadModule(requiredModule);

        if (modules.containsKey(moduleType))
            return;

        Module module;

        switch (moduleType) {
            case COMMANDS:
                module = new CommandsModule(moduleType, bot);
                break;
            case MUSIC:
                module = new MusicModule(moduleType, bot);
                break;
            case TEST:
                module = new TestModule(moduleType, bot);
                break;
            default:
                return;
        }

        logger.info("Loading " + moduleType.getName() + " module...");
        module.enable();
        module.setEnabled(true);

        modules.put(moduleType, module);
    }

    public void unloadModules() {
        logger.info("Unloading modules...");

        while (!modules.isEmpty()) {
            for (Modules module : modules.keySet().toArray(new Modules[modules.size()])) {
                try {
                    unloadModule(module);
                } catch (UnloadModuleException e) {
                }
            }
        }

        logger.info("Modules unloaded");
    }

    public void unloadModule(Modules moduleType) throws UnloadModuleException {
        for (Modules mod : modules.keySet().toArray(new Modules[modules.size()])) {
            if (!mod.equals(moduleType))
                for (Modules dependency : mod.getRequiredModules())
                    if (dependency.equals(moduleType))
                        throw new UnloadModuleException(
                                "Cannot unload module because module " + mod + " depend on it...");
        }

        logger.info("Unloading " + moduleType.getName() + " module...");
        modules.remove(moduleType).disable();
    }

    public Module getModule(Modules moduleType) {
        return modules.get(moduleType);
    }
}
