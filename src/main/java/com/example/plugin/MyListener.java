package com.example.plugin;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.function.Consumer;

import static com.example.plugin.AdminModule.*;
import static com.example.plugin.MarketApi.*;

/**
 * @author pt
 * @email plexpt@gmail.com
 * @date 2020-04-30 0:26
 */
public class MyListener implements Consumer<GroupMessageEvent> {

    public static MiraiLogger log = null;

    public static final String[] CMD = {
            ".jita", ".id", "id" ,".help", ".set", ".del", ".get"
    };


    @Override
    public void accept(GroupMessageEvent event) {

        Group group = event.getGroup();
        String message = event.getMessage().get(1).toString();
        long groupId = group.getId();

        long qq = event.getSender().getId();
        log = event.getBot().getLogger();
        log.info("message"+message);

        stepCmd(group, groupId, qq, message);
    }

    private void stepCmd(Group api, long groupId, long qq, String message) {
        Member member = api.getOrNull(qq);
        if ( member == null) {
            System.out.println("该成员已不在QQ群");
            return;
        }
       if (message.startsWith(CMD[0])){
           // .jita
           String keyword = message.replace(CMD[0] + " ","");
           String reply = "关键字:'"+ keyword +"'没有返回匹配项。白面鸮这样说道。";
           try {
               reply = callSearchKeyWordApi(keyword);
           } catch( Exception e) {
               System.out.println(e.getMessage());
           }
           api.sendMessage(new At(member).plus(reply));
       } else if (message.startsWith(CMD[1]) || message.startsWith(CMD[2])) {
           // .id
           String id = message.startsWith(CMD[1]) ?
                   message.replace(CMD[1] + " ","")
                   : message.replace(CMD[2] + " ","");
           String reply = "关键字:'"+ id +"'没有返回匹配项。白面鸮这样说道。";
           try {
               reply = callDetailItemApi(id);
           } catch( Exception e) {
               System.out.println(e.getMessage());
           }
           api.sendMessage(new At(member).plus(reply));
       } else if (message.startsWith(CMD[3])) {
           // .help
           String reply = getHelpMessage();
           api.sendMessage(new At(member).plus(reply));
       } else if (message.startsWith(CMD[4])) {
           // .set
           String input = message.replace(CMD[4] + " ","");
           String reply = "项目和内容请用空格键分开。白面鸮这样说道。";
           if (!input.contains(" ")) {
               api.sendMessage(new At(member).plus(reply));
           } else {
               String key = input.substring(0, input.indexOf(' '));
               String value = input.substring(input.indexOf(' ') + 1);
               reply = setProperties(key, value);
               api.sendMessage(new At(member).plus(reply));
           }
       } else if (message.startsWith(CMD[5])) {
           // .del
           String key = message.replace(CMD[5] + " ","");
           String reply = deletePropertyByKey(key);
           api.sendMessage(new At(member).plus(reply));
       } else if (message.startsWith(CMD[6])) {
           // .del
           String key = message.replace(CMD[6] + " ","");
           if (key.equals("列表")) {
               api.sendMessage(new At(member).plus(getPropertiesFullList()));
           } else {
               api.sendMessage(new At(member).plus(getPropertyByKey(key)));
           }
       }

    }

}
