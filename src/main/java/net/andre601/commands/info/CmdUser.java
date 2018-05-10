package net.andre601.commands.info;

import net.andre601.commands.Command;
import net.andre601.util.EmbedUtil;
import net.andre601.util.MessageUtil;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CmdUser implements Command {

    public void getUser(TextChannel tc, Message msg){
        tc.sendTyping().queue();
        List<Member> mentionedMember = msg.getMentionedMembers();
        for(Member member : mentionedMember){
            if(member == null){
                tc.sendMessage(String.format(
                        "%s This user isn't on this Discord.",
                        msg.getAuthor().getAsMention()
                )).queue();
                return;
            }

            String roles = member.getRoles().stream().map(Role::getName).collect(Collectors.joining(", "));
            EmbedBuilder uInfo = EmbedUtil.getEmbed(msg.getAuthor())
                    .setAuthor("Userinfo")
                    .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                    .addField("User:", String.format(
                            "**Name**: %s\n" +
                            "**ID**: %s\n" +
                            "**Status**: %s",
                            MessageUtil.getUsername(member),
                            member.getUser().getId(),
                            MessageUtil.getStatus(member, msg)
                    ), false)
                    .addField("Avatar:",
                            (member.getUser().getEffectiveAvatarUrl() != null ?
                            String.format(
                                    "[`Current Avatar`](%s)\n" +
                                    "[`Default Avatar`](%s)",
                                    member.getUser().getEffectiveAvatarUrl(),
                                    member.getUser().getDefaultAvatarUrl()
                            ) : String.format(
                                    "[`Default Avatar`](%s)",
                                    member.getUser().getDefaultAvatarUrl()
                            )), true)
                    .addField("Is Bot:", MessageUtil.isBot(member.getUser()), false)
                    .addField("Roles:", (member == null ?
                            "This member is not here!" : (roles.length() > 1000 ? "*Way to many!*" :
                                    roles)),
                            true)
                    .addField("Dates:", String.format(
                            "**Account created**: %s\n" +
                            "**Joined**: %s",
                            MessageUtil.formatTime(LocalDateTime.from(
                                    member.getUser().getCreationTime()
                            )),
                            (member == null ? "`Not on this Discord!`" :
                            MessageUtil.formatTime(LocalDateTime.from(
                                    member.getJoinDate()
                            )))
                    ), false);

            tc.sendMessage(uInfo.build()).queue();
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        if (!PermUtil.canWrite(msg))
            return;

        if(!PermUtil.canSendEmbed(e.getMessage())){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(args.length == 0){
            tc.sendTyping().queue();
            String roles = msg.getMember().getRoles().stream().map(Role::getName).collect(Collectors.joining(", "));
            EmbedBuilder uInfo = EmbedUtil.getEmbed(msg.getAuthor())
                    .setAuthor("Userinfo")
                    .setThumbnail(msg.getAuthor().getEffectiveAvatarUrl())
                    .addField("User:", String.format(
                            "**Name**: %s\n" +
                            "**ID**: %s\n" +
                            "**Status**: %s",
                            MessageUtil.getUsername(e.getMember()),
                            msg.getAuthor().getId(),
                            MessageUtil.getStatus(msg.getMember(), msg)
                    ), false)
                    .addField("Avatar:",
                            (msg.getAuthor().getEffectiveAvatarUrl() != null ? String.format(
                                    "[`Current Avatar`](%s)\n" +
                                    "[`Default Avatar`](%s)",
                                    msg.getAuthor().getEffectiveAvatarUrl(),
                                    msg.getAuthor().getDefaultAvatarUrl()
                            ) : String.format(
                                    "[`Default Avatar`](%s)",
                                    msg.getAuthor().getDefaultAvatarUrl()
                            )), true)
                    .addField("Is Bot:", MessageUtil.isBot(msg.getAuthor()), true)
                    .addField("Roles:", (roles.length() > 1000 ? "*Way to many!*" : roles), false)
                    .addField("Dates:", String.format(
                            "**Account created**: %s\n" +
                            "**Joined**: %s",
                            MessageUtil.formatTime(LocalDateTime.from(
                                    msg.getAuthor().getCreationTime()
                            )),
                            (msg.getAuthor() == null ? "`Not on this Discord!`" :
                                    MessageUtil.formatTime(LocalDateTime.from(
                                            msg.getMember().getJoinDate()
                                    )))
                    ), false);
            tc.sendMessage(uInfo.build()).queue();
            return;
        }

        getUser(tc, msg);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}