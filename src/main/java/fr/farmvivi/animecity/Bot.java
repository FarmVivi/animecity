package fr.farmvivi.animecity;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.farmvivi.animecity.command.CommandsManager;
import fr.farmvivi.animecity.jda.JDAManager;
import fr.farmvivi.animecity.music.MusicManager;
import fr.farmvivi.animecity.music.MusicPlayer;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Bot {
    private static Bot instance;

    public static final String version = "1.3.0.0";
    public static final String name = "AnimeCity";
    public static final boolean production = false;

    public static final Logger logger = LoggerFactory.getLogger(name);

    private final Configuration configuration;
    private final CommandsManager commandsManager;
    private final MusicManager musicManager;

    public Bot(String[] args) {
        logger.info("Démarrage de " + name + " v" + version + ") (Prod: " + production + ") en cours...");

        logger.info("System.getProperty('os.name') == '" + System.getProperty("os.name") + "'");
        logger.info("System.getProperty('os.version') == '" + System.getProperty("os.version") + "'");
        logger.info("System.getProperty('os.arch') == '" + System.getProperty("os.arch") + "'");
        logger.info("System.getProperty('java.version') == '" + System.getProperty("java.version") + "'");
        logger.info("System.getProperty('java.vendor') == '" + System.getProperty("java.vendor") + "'");
        logger.info("System.getProperty('sun.arch.data.model') == '" + System.getProperty("sun.arch.data.model") + "'");

        instance = this;

        configuration = new Configuration();
        JDAManager.getShardManager();
        commandsManager = new CommandsManager();
        JDAManager.getShardManager().addEventListener(commandsManager);
        musicManager = new MusicManager();
        setDefaultActivity();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown asked!");
            JDAManager.getShardManager().removeEventListener(commandsManager);
            musicManager.getAudioPlayerManager().shutdown();
            JDAManager.getShardManager().shutdown();
            logger.info("Bye!");
        }));

        if (configuration.radioEnabled) {
            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                logger.error("wait failed", e);
            }
            setupRadio();
        }
    }

    public static Bot getInstance() {
        return instance;
    }

    public static void setInstance(Bot instance) {
        Bot.instance = instance;
    }

    public void setupRadio() {
        final Guild guild = JDAManager.getShardManager().getGuildById(configuration.radioGuildID);
        if (guild == null) {
            logger.error("Guild not found !");
            System.exit(1);
            return;
        }

        final VoiceChannel voiceChannel = guild.getVoiceChannelById(configuration.radioChannelID);
        if (voiceChannel == null) {
            logger.error("Channel not found !");
            System.exit(1);
            return;
        }

        Bot.logger.info("Join channel " + voiceChannel.getName() + "...");
        guild.getAudioManager().openAudioConnection(voiceChannel);
        guild.getAudioManager().setAutoReconnect(true);

        final MusicPlayer musicPlayer = musicManager.getPlayer(guild);
        musicPlayer.getAudioPlayer().setVolume(MusicPlayer.DEFAULT_RADIO_VOLUME);
        musicPlayer.setLoopQueueMode(true);
        musicPlayer.setShuffleMode(true);

        musicManager.loadTrack(guild, configuration.radioPlaylistURL);
    }

    public void setDefaultActivity() {
        if (production)
            JDAManager.getShardManager()
                    .setActivity(Activity.playing("v" + version + " | Prefix: " + configuration.cmdPrefix));
        else
            JDAManager.getShardManager()
                    .setActivity(Activity.playing("Dev - v" + version + " | Prefix: " + configuration.cmdPrefix));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }
}
