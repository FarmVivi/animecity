package fr.farmvivi.discordbot.module.commands.command;

import fr.farmvivi.discordbot.module.commands.Command;
import fr.farmvivi.discordbot.module.commands.CommandCategory;
import fr.farmvivi.discordbot.module.commands.CommandReceivedEvent;

public class ShutdownCommand extends Command {
    public ShutdownCommand() {
        super("shutdown", CommandCategory.OTHER, "Éteint le bot");

        this.guildOnly = false;
        this.adminOnly = true;
    }

    @Override
    public boolean execute(CommandReceivedEvent event, String content) {
        if (!super.execute(event, content))
            return false;

        System.exit(0);

        return true;
    }
}
