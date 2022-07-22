package rama.ubstats;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rama.ubstats.BotHandler.LogsWriter;
import rama.ubstats.Commands.MainCommand;
import rama.ubstats.LoggingHandler.LoggingMain;
import rama.ubstats.timingshandler.TimingsMain;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public final class UBStats extends JavaPlugin {


    private File errorLogFile;
    private FileConfiguration errorLogConfig;
    public long serverStart;
    String token = this.getConfig().getString("token");
    public JDA jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES).build().awaitReady();

    public UBStats() throws LoginException, InterruptedException {
    }

    @Override
    public void onEnable() {
        serverStart = System.currentTimeMillis();
        createLogFile();
        try {
            setupLogging();
        } catch (IOException | LoginException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.saveDefaultConfig();
        registerCommands();
        TimingsMain tm = new TimingsMain();
        tm.startTPSChecker();
    }

    @Override
    public void onDisable() {
        try {
            flushLogFile(errorLogFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        jda.shutdown();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&eUBStats&6] &fDesactivando JDA..."));
    }

    public int getUptime(){
        long now = System.currentTimeMillis();
        long diff = now - serverStart;

        int Seconds = (int) (diff / 1000);
        return Seconds;
    }

    public void registerCommands(){
        Bukkit.getPluginCommand("ubstats").setExecutor(new MainCommand());
    }

    public FileConfiguration getLogConfig(){
        return this.errorLogConfig;
    }

    public File getErrorLogFile(){
        return this.errorLogFile;
    }

    private void createLogFile(){
        errorLogFile = new File(getDataFolder(), "logs.yml");
        if(!errorLogFile.exists()){
            errorLogFile.getParentFile().mkdirs();
            saveResource("logs.yml", false);
        }

        errorLogConfig = new YamlConfiguration();
        try{
            errorLogConfig.load(errorLogFile);
        } catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&eUBStats&6] &fCreando archivo de registro..."));

    }

    public void setupLogging() throws IOException, LoginException, InterruptedException {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                LoggingMain lm = new LoggingMain();
                try {
                    lm.writeLogs();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },0, 60*20);
        new BukkitRunnable() {
            @Override
            public void run() {
                LogsWriter lw = null;
                try {
                    lw = new LogsWriter();
                } catch (LoginException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lw.startLogsWriter(getPlugin(UBStats.class).getErrorLogFile());
            }
        }.runTaskLater(this, 1L);
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&eUBStats&6] &fIniciando registro automático..."));
    }

    public void flushLogFile(File f) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(f);
        writer.println("");
        writer.close();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&eUBStats&6] &fVaciando archivo de registro..."));
    }


}
