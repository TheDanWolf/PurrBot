package net.andre601.listeners;

import net.andre601.commands.server.CmdPrefix;
import net.andre601.commands.server.CmdWelcome;
import net.andre601.core.Main;
import net.andre601.util.MessageUtil;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter{
    private static String setBotGame(){
        return (Main.file.getItem("config", "beta").equalsIgnoreCase("true") ?
        "some Beta-Stuff on %s Guilds" : "some Nekos OwO | On %s Guilds");
    }

    public static String getBotGame(){
        return setBotGame();
    }

    public void onReady(ReadyEvent e){


        String botID = e.getJDA().getSelfUser().getId();
        int servers = e.getJDA().getGuilds().size();

        e.getJDA().getPresence().setGame(Game.watching(String.format(
                getBotGame(),
                e.getJDA().getGuilds().toArray().length
        )));

        CmdPrefix.load(e.getJDA());
        CmdWelcome.load(e.getJDA());

        System.out.println(String.format(
                "[INFO] Enabled Bot-User %s (%s)\n" +
                "  > Version: %s\n" +
                "  > JDA: %s\n" +
                "  > Discords loaded: %s",
                MessageUtil.getTag(e.getJDA().getSelfUser()),
                botID,
                Main.getVersion(),
                JDAInfo.VERSION,
                e.getJDA().getGuilds().size()
        ));

        //  Sending update if Bot isn't beta
        if(Main.file.getItem("config", "beta").equalsIgnoreCase("false"))
            Main.getAPI().setStats(botID, servers);

    }
}
