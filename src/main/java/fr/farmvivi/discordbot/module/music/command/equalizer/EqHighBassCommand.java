package fr.farmvivi.discordbot.module.music.command.equalizer;

import fr.farmvivi.discordbot.module.commands.Command;
import fr.farmvivi.discordbot.module.commands.CommandCategory;
import fr.farmvivi.discordbot.module.commands.CommandMessageBuilder;
import fr.farmvivi.discordbot.module.commands.CommandReceivedEvent;
import fr.farmvivi.discordbot.module.music.MusicModule;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class EqHighBassCommand extends Command {
    private static final float[] BASS_BOOST = {0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f};

    private final MusicModule musicModule;

    public EqHighBassCommand(MusicModule musicModule) {
        super("eqhighbass", CommandCategory.MUSIC, "Ajuste le niveau de basses du modificateur audio", new OptionData[]{
                new OptionData(OptionType.NUMBER, "niveau", "Niveau de basses")});

        this.musicModule = musicModule;
    }

    @Override
    public boolean execute(CommandReceivedEvent event, String content, CommandMessageBuilder reply) {
        if (!super.execute(event, content, reply))
            return false;

        Guild guild = event.getGuild();

        if (musicModule.getPlayer(guild).getAudioPlayer().getPlayingTrack() == null) {
            reply.append("Aucune musique en cours de lecture.");
            return false;
        }

        try {
            float diff = Float.parseFloat(content);
            for (int i = 0; i < BASS_BOOST.length; i++)
                musicModule.getPlayer(guild).getEqualizer().setGain(i, BASS_BOOST[i] + diff);

            reply.append("**Equalizer - High Bass** de ").append(String.valueOf(diff)).append(" activé.");

            return true;
        } catch (NumberFormatException err) {
            reply.append("Valeur non valide.");
            return false;
        }
    }
}
