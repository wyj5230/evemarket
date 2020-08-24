package com.example.plugin;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.function.Consumer;

import static com.example.plugin.MarketApi.*;

/**
 * @author pt
 * @email plexpt@gmail.com
 * @date 2020-04-30 0:26
 */
public class MyListener implements Consumer<GroupMessageEvent> {

    public static MiraiLogger log = null;

    public static final String[] CMD = {
            ".jita", ".id", "id" ,".help", ".eng"
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

       if (message.startsWith(CMD[0])){
           String keyword = message.replace(CMD[0] + " ","");
           String reply = "关键字:'"+ keyword +"'没有返回匹配项。白面鸮这样说道。";
           try {
               reply = callSearchKeyWordApi(keyword);
           } catch( Exception e) {
               System.out.println(e.getMessage());
           }
           api.sendMessage(new At(api.getOrNull(qq))
                   .plus(reply));
       } else if (message.startsWith(CMD[1])) {
           String id = message.replace(CMD[1] + " ","");
           String reply = "关键字:'"+ id +"'没有返回匹配项。白面鸮这样说道。";
           try {
               reply = callDetailItemApi(id);
           } catch( Exception e) {
               System.out.println(e.getMessage());
           }
           api.sendMessage(new At(api.getOrNull(qq))
                   .plus(reply));
       } else if (message.startsWith(CMD[2])) {
           String id = message.replace(CMD[2] + " ","");
           String reply = "关键字:'"+ id +"'没有返回匹配项。白面鸮这样说道。";
           try {
               reply = callDetailItemApi(id);
           } catch( Exception e) {
               System.out.println(e.getMessage());
           }
           api.sendMessage(new At(api.getOrNull(qq))
                   .plus(reply));
       } else if (message.startsWith(CMD[3])) {
           String reply = getHelpMessage();
           api.sendMessage(new At(api.getOrNull(qq))
                   .plus(reply));
       }

    }

}
