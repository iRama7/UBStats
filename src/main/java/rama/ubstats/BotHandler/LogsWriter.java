package rama.ubstats.BotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import rama.ubstats.UBStats;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


public class LogsWriter {

    private Plugin plugin = UBStats.getPlugin(UBStats.class);
    String token = plugin.getConfig().getString("token");
    public JDA jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES).build().awaitReady();

    public LogsWriter() throws LoginException, InterruptedException {
    }

    public void startLogsWriter(File file) {
        String server_name = plugin.getConfig().getString("server_name");
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    if(server_name.equals("minas")){
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Registro de errores ("+server_name+")");
                        eb.setDescription("Actualizado: "+TimeFormat.RELATIVE.now());
                        try {
                            eb.addField("URL", paste(file), true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        TextChannel textChannel = jda.getGuildById("312552375804100609").getTextChannelById("994869391239958548");
                        textChannel.sendMessageEmbeds(eb.build()).queue();

                    }else if(server_name.equals("villa")){
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Registro de errores ("+server_name+")");
                        eb.setDescription("Actualizado: "+TimeFormat.RELATIVE.now());
                        try {
                            eb.addField("URL", paste(file), true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        TextChannel textChannel = jda.getGuildById("312552375804100609").getTextChannelById("992441585159643206");
                        textChannel.sendMessageEmbeds(eb.build()).queue();
                    }
                }
            }, 0, ((30*30*20)*20)); //5 horas

        }
    public static String paste(File file) throws MalformedURLException, IOException {

        String URL = null;

        List<String> logsLines = new ArrayList<>();
        BufferedReader read = new BufferedReader(new FileReader(file));
        String line;
        while ((line = read.readLine()) != null) {
            logsLines.add(line);
        }
            URL url = new URL("https://pastebin.com/api/api_post.php");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            Map<String,String> arguments = new HashMap<>();
            arguments.put("api_dev_key", "McfSbOHElkPV-IZWu1U-6jddIXoxOjwg");
            arguments.put("api_option","paste");
            StringBuilder strbul=new StringBuilder();
            for(String str : logsLines)
            {
                strbul.append(str);
                strbul.append("\n");
            }
            String str=strbul.toString();
            arguments.put("api_paste_code", str);
            StringJoiner sj = new StringJoiner("&");
            for(Map.Entry<String,String> entry : arguments.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(out);
            InputStream is = http.getInputStream();
            String text = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            URL = text;
        return URL;
    }
}
