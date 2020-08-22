package com.example.plugin;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.function.Consumer;

import static com.example.plugin.MarketApi.callDetailItemApi;
import static com.example.plugin.MarketApi.callSearchKeyWordApi;

/**
 * @author pt
 * @email plexpt@gmail.com
 * @date 2020-04-30 0:26
 */
public class MyListener implements Consumer<GroupMessageEvent> {

    public static MiraiLogger log = null;

    public static final String[] CMD = {
            ".jita", ".id"
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
           String reply = callSearchKeyWordApi(keyword);
           api.sendMessage(new At(api.getOrNull(qq))
                   .plus(reply));
       } else if (message.startsWith(CMD[1])) {
           String id = message.replace(CMD[1] + " ","");
           String reply = callDetailItemApi(id);
           api.sendMessage(new At(api.getOrNull(qq))
                   .plus(reply));
       }

    }

}
