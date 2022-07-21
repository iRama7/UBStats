package rama.ubstats.timingshandler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import rama.ubstats.BotHandler.TPSWriter;
import rama.ubstats.UBStats;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Bukkit.getServer;

public class TimingsMain {

    private UBStats plugin = UBStats.getPlugin(UBStats.class);
    private double serverTps = 20;
    private long lastTimeStamp = System.currentTimeMillis();
    private double millisPerTick = 1000 / 20;
    private double averageTickPerMinute = 20;
    private List<Double> tickSamples = new ArrayList<>();

    private int timingsTaskID;

    public void startTimingsReport(){
        Bukkit.getLogger().info("Iniciando reporte de tiempos");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timings on");
        timingsTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timings paste");
                Bukkit.getLogger().info("Pegando reporte de tiempos");
            }
        },300*20);
        
    }

    public String getLatestTimingsURL() throws IOException {
        String mainPath = plugin.getServer().getWorldContainer().getAbsolutePath();
        String TimingURL = "No hay un reporte de timings válido.";
        File latestLog = new File(mainPath + "/logs/latest.log");
        BufferedReader read = new BufferedReader(new FileReader(latestLog));
        List<String> logs = new ArrayList<>();
        String timingLine = null;
        String line;

        while ((line = read.readLine()) != null) {
            logs.add(line);
        }
        for(String logLine : logs){
            if(logLine.contains("View Timings Report: ")) {
                timingLine = logLine;
            }
        }
        //"[13:00:46] [Timings paste thread/INFO]: View Timings Report: https://timings.aikar.co/?id=def54314993641f9ad5a0a66c24b67e0"
        if(timingLine != null) {

            String regex = "https:\\/\\/timings\\.aikar\\.co\\/\\?id=[0-9a-f]+";

            String[] parts = timingLine.split("View Timings Report: ");
            TimingURL = parts[0];
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(timingLine);

            while(matcher.find()){
                TimingURL = matcher.group(0);
            }

        }
        return TimingURL;
    }

    public void startTPSChecker(){
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    updateTPS();
                } catch (LoginException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskTimer(plugin, 0, 20);

    }

    private void updateTPS() throws LoginException, InterruptedException {

        Double latestTPS;
        Double latestAvgTPS;

        long currentTimeStamp = System.currentTimeMillis();
        long timeDiff = currentTimeStamp - lastTimeStamp;
        double ticksLost = (double) timeDiff / millisPerTick;
        this.serverTps = 40 - ticksLost;
        if (serverTps > 20) {
            serverTps = 20;
        }
        this.tickSamples.add(serverTps);
        if (tickSamples.size() >= 60) {
            double totalTick = 0;
            for (Double buffer : tickSamples) {
                totalTick += buffer;
            }

            this.averageTickPerMinute = totalTick / tickSamples.size();
            tickSamples.clear();
        }
        this.lastTimeStamp = currentTimeStamp;
        if(serverTps <= 18 && plugin.getUptime() > 60){
            BukkitScheduler scheduler = getServer().getScheduler();
            if(!scheduler.isQueued(timingsTaskID)){
                startTimingsReport();
                latestTPS = serverTps;
                latestAvgTPS = averageTickPerMinute;
                scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        TPSWriter tw = null;
                        try {
                            tw = new TPSWriter();
                        } catch (LoginException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            tw.sendTPSReport(latestTPS, latestAvgTPS, getLatestTimingsURL());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, 303*20);
            }else{
                Bukkit.getLogger().warning("[UBStats] Se detectó un bajón de TPS pero no se registrará reporte de tiempos porque ya hay uno en curso!");
            }
        }
    }

}
