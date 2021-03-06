package com.epicBot.main.messageProcessing;

import com.epicBot.main.Main;
import com.epicBot.main.assets.ASCIIart;

import com.epicBot.main.setup.Configs;
import com.epicBot.main.utils.ArrayUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.atomic.AtomicLong;

public class PrivateMessageProcessor {

    public void process(MessageReceivedEvent event){

        Message message = event.getMessage();
        TextChannel outChannel = getOutChannelWithHistory(event.getAuthor());
        String displayName = event.getAuthor().getName();

        outChannel.sendMessage(displayName+": "+message.getContentDisplay()).queue();
    }

    public static TextChannel getOutChannel(User user){
        String userName = user.getName().toLowerCase().replace(" ","-");

        Category botCat = Main.jda.getCategoryById(Configs.botCategoryId);

        AtomicLong id = new AtomicLong(-1L);
        botCat.getChannels().forEach(guildChannel -> {
            if (guildChannel.getName().equals("pm-"+userName)){
                id.set(guildChannel.getIdLong());
            }
        });
        if (id.get()!=-1L) {
            return Main.jda.getTextChannelById(id.get());
        } else {
            TextChannel o = Main.jda.getTextChannelById(botCat.createTextChannel("pm-"+userName).complete().getIdLong());
            o.createPermissionOverride(Main.jda.getRoleById(Configs.everyoneID)).setDeny(Permission.MESSAGE_WRITE).queue();
            return o;
        }
    }


    public static TextChannel getOutChannelWithHistory(User user){
        String userName = user.getName().toLowerCase().replace(" ","-");

        Category botCat = Main.jda.getCategoryById(Configs.botCategoryId);

        AtomicLong id = new AtomicLong(-1L);
        botCat.getChannels().forEach(guildChannel -> {
            if (guildChannel.getName().equals("pm-"+userName)){
                id.set(guildChannel.getIdLong());
            }
        });
        if (id.get()!=-1L) {
            return Main.jda.getTextChannelById(id.get());
        } else {
            System.out.println("Generating");
            TextChannel o = Main.jda.getTextChannelById(botCat.createTextChannel("pm-"+userName).complete().getIdLong());
            o.createPermissionOverride(Main.jda.getRoleById(Configs.everyoneID)).setDeny(Permission.MESSAGE_WRITE).queue();
            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.getHistory().retrievePast(30).queue(list -> {
                    ArrayUtils.reverse(list).forEach(msg -> o.sendMessage(msg.getAuthor().getName()+": "+msg.getContentDisplay()).queue());
                });
            });
            return o;
        }
    }
}
