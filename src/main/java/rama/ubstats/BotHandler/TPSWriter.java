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
import java.io.IOException;

public class TPSWriter {

    private Plugin plugin = UBStats.getPlugin(UBStats.class);
    String token = plugin.getConfig().getString("token");
    public JDA jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES).build().awaitReady();

    public TPSWriter() throws LoginException, InterruptedException {
    }

    public void sendTPSReport(Double TPS, Double avgTPS, String URL){
        String server_name = plugin.getConfig().getString("server_name");
                if(server_name.equals("minas")){
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Nuevo bajón de TPS ("+server_name+")");
                    eb.setDescription("Fecha: "+TimeFormat.RELATIVE.now());
                    eb.addField("TPS Actuales", String.valueOf(TPS), true);
                    eb.addField("Media de TPS", String.valueOf(avgTPS), true);
                    eb.addField("Timings", URL, true);
                    TextChannel textChannel = jda.getGuildById("312552375804100609").getTextChannelById("992443631858372678");
                    textChannel.sendMessageEmbeds(eb.build()).queue();

                }else if(server_name.equals("villa")){
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Nuevo bajón de TPS ("+server_name+")");
                    eb.setDescription("Fecha: "+TimeFormat.RELATIVE.now());
                    eb.addField("TPS Actuales", String.valueOf(TPS), true);
                    eb.addField("Media de TPS", String.valueOf(avgTPS), true);
                    eb.addField("Timings", URL, true);
                    TextChannel textChannel = jda.getGuildById("312552375804100609").getTextChannelById("992443631858372678");
                    textChannel.sendMessageEmbeds(eb.build()).queue();
                }
            }
}
