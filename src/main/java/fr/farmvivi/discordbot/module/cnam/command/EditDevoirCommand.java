package fr.farmvivi.discordbot.module.cnam.command;

import fr.farmvivi.discordbot.module.cnam.CnamModule;
import fr.farmvivi.discordbot.module.cnam.DevoirEventHandler;
import fr.farmvivi.discordbot.module.cnam.form.devoir.edit.EditDevoirForm;
import fr.farmvivi.discordbot.module.commands.Command;
import fr.farmvivi.discordbot.module.commands.CommandCategory;
import fr.farmvivi.discordbot.module.commands.CommandMessageBuilder;
import fr.farmvivi.discordbot.module.commands.CommandReceivedEvent;
import fr.farmvivi.discordbot.module.forms.Form;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Map;

public class EditDevoirCommand extends Command {
    private final CnamModule module;
    private final DevoirEventHandler devoirEventHandler;

    public EditDevoirCommand(CnamModule module, DevoirEventHandler devoirEventHandler) {
        super("devoir_edit", CommandCategory.CNAM, "Modifier un devoir");

        this.setGuildOnly(true);

        this.module = module;
        this.devoirEventHandler = devoirEventHandler;
    }

    @Override
    public boolean execute(CommandReceivedEvent event, Map<String, OptionMapping> args, CommandMessageBuilder reply) {
        if (!super.execute(event, args, reply))
            return false;

        if (event.getOriginalEvent() instanceof IReplyCallback replyCallback) {
            Form form = new EditDevoirForm(module, devoirEventHandler);
            form.start(replyCallback);
        } else {
            reply.addContent("Une erreur est survenue lors de l'exécution de la commande.");
            return false;
        }

        return true;
    }
}