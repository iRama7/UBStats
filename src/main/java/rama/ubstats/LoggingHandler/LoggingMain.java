package rama.ubstats.LoggingHandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import rama.ubstats.UBStats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoggingMain {

    Boolean isLogging = false;

    private UBStats plugin = UBStats.getPlugin(UBStats.class);


    public void writeLogs() throws IOException {
        String mainPath = plugin.getServer().getWorldContainer().getAbsolutePath();
        File latestLog = new File(mainPath + "/logs/latest.log");
        BufferedReader read = new BufferedReader(new FileReader(latestLog));
        List<String> logs = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<String> warns = new ArrayList<>();
        List<String> updates = new ArrayList<>();
        try {
            String line;
            while ((line = read.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String line : logs) {
            if (line.contains("Server thread/ERROR")) {
                errors.add(line);
            } else if (line.contains("Server thread/WARN")) {
                warns.add(line);
            } else if(line.contains("update") || line.contains("available")){
                updates.add(line);
            }
        }
        if(!isLogging) {
            logToFile(warns, errors, updates);
        }else{
            Bukkit.getLogger().severe("Plugin is still logging");
        }
    }

    public void logToFile(List<String> wrn, List<String> err, List<String> upd) throws IOException {
        isLogging = true;
        for(int i = 0; i < wrn.size(); i++){
            FileConfiguration logFile = plugin.getLogConfig();
            logFile.set("AVISOS."+i, wrn.get(i));
            logFile.save(plugin.getErrorLogFile());
        }
        for(int i = 0; i < err.size(); i++){
            FileConfiguration logFile = plugin.getLogConfig();
            logFile.set("ERRORES."+i, err.get(i));
            logFile.save(plugin.getErrorLogFile());
        }
        for(int i = 0; i < upd.size(); i++){
            FileConfiguration logFile = plugin.getLogConfig();
            logFile.set("POSIBLES ACTUALIZACIONES."+i, upd.get(i));
            logFile.save(plugin.getErrorLogFile());
        }
        isLogging = false;
    }
}
