package fr.farmvivi.discordbot.module.music.command.effects;

import com.github.natanbc.lavadsp.distortion.DistortionPcmAudioFilter;
import fr.farmvivi.discordbot.module.commands.CommandCategory;
import fr.farmvivi.discordbot.module.commands.CommandMessageBuilder;
import fr.farmvivi.discordbot.module.commands.CommandReceivedEvent;
import fr.farmvivi.discordbot.module.music.MusicModule;
import fr.farmvivi.discordbot.module.music.MusicPlayer;
import fr.farmvivi.discordbot.module.music.command.MusicCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Collections;
import java.util.Map;

public class DistortionCommand extends MusicCommand {
    public DistortionCommand(MusicModule musicModule) {
        super(musicModule, "distortion", CommandCategory.MUSIC, "Active l'effet de distortion");
    }

    @Override
    public boolean execute(CommandReceivedEvent event, Map<String, OptionMapping> args, CommandMessageBuilder reply) {
        if (!super.execute(event, args, reply))
            return false;

        Guild guild = event.getGuild();

        if (musicModule.getPlayer(guild).getAudioPlayer().getPlayingTrack() == null) {
            reply.addContent("Aucune musique en cours de lecture.");
            return false;
        }

        MusicPlayer musicPlayer = musicModule.getPlayer(guild);
        musicPlayer.getAudioPlayer().setFilterFactory((track, format, output) -> {
            DistortionPcmAudioFilter distortionPcmAudioFilter = new DistortionPcmAudioFilter(output, format.channelCount);
            return Collections.singletonList(distortionPcmAudioFilter);
        });
        reply.addContent("**Effet de distortion** activé.");

        return true;
    }
}
