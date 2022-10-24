package rama.ubstats.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rama.ubstats.BotHandler.LogsWriter;
import rama.ubstats.BotHandler.TPSWriter;
import rama.ubstats.LoggingHandler.LoggingMain;
import rama.ubstats.UBStats;
import rama.ubstats.timingshandler.TimingsMain;

import java.io.IOException;


public class MainCommand implements CommandExecutor {

    private UBStats plugin = UBStats.getPlugin(UBStats.class);
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            if(args[0].equals("write")){
                LoggingMain lm = new LoggingMain();
                try {
                    lm.writeLogs();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if(args[0].equals("reconnect")){
                try {
                    Bukkit.getConsoleSender().sendMessage(LogsWriter.paste(plugin.getErrorLogFile()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }else if(args[0].equals("getTimings")){
                TimingsMain tm = new TimingsMain();
                String timingsURL;
                try {
                    timingsURL = tm.getLatestTimingsURL();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Bukkit.getConsoleSender().sendMessage(timingsURL);
            }
        }
        return false;
    }
}
