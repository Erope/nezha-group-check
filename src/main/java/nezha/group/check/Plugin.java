package nezha.group.check;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.event.events.*;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();
    public Listener listener;

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("nezha.group.check.plugin", "1.0-SNAPSHOT")
                .name("哪吒入群验证")
                .author("ShinNET/zPonds")
                .build());
    }

    @Override
    public void onEnable() {
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinRequestEvent.class, event -> {
            if (event.getGroupId() != 872069346)
                return;
            String[] Messages = event.getMessage().split("\n");
            if (Messages.length != 2) {
                event.reject(false, "异常错误");
                return;
            }
            else {
                String Answer = Messages[1].substring(3);
                if (Answer.isEmpty())
                    event.reject(false, "监控链接不能为空！");
                getLogger().info(Answer);
                // 简单处理链接
                String Host = Answer.replace("https://", "").replace("http://", "").replace("/", "").toLowerCase();
                SQData sq = new SQData();
                if (sq.isExist(Host)) {
                    event.reject(false, "此链接已被使用过！");
                    return;
                }
                if (!Answer.startsWith("https://") && !Answer.startsWith("http://"))
                    if (Answer.startsWith("http")) {
                        event.reject(false, "链接格式错误！");
                        return;
                    } else {
                        Answer = "http://" + Answer;
                    }
                if (!Answer.endsWith("/")) {
                    Answer = Answer + "/";
                }
                // 验证首页
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Answer)
                        .build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    if (response.code() != 200) {
                        event.reject(false, "无法访问，请检查是否关闭防火墙。");
                        return;
                    }
                    String OkBody = Objects.requireNonNull(response.body()).string();
                    if (OkBody.contains("验证查看密码")) {
                        event.reject(false, "请关闭密码验证。");
                        return;
                    }
                    if (!OkBody.contains("naiba")) {
                        event.reject(false, "无法确认哪吒面板，请不要删除版权信息或开启防火墙");
                        return;
                    }
                } catch (IOException e) {
                    event.reject(false, "无法访问链接，请检查拼写。");
                    return;
                }
                // 验证WebSocket
                Answer = Answer.replace("http://", "ws://").replace("https://", "wss://") + "ws";
                int ws_check = -1;
                try {
                    ws_check = new WebSocketCheck(Answer).check();
                    if (ws_check == -1 && Answer.startsWith("ws://")) {
                        Answer = Answer.replace("ws://", "wss://");
                        ws_check = new WebSocketCheck(Answer).check();
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                if (ws_check == -1) {
                    event.reject(false, "无法建立WebSocket连接");
                    return;
                } else if (ws_check < 2) {
                    event.reject(false, "无2台及以上有效的服务器");
                    return;
                } else {
                    getLogger().info(String.valueOf(ws_check));
                    sq.InSert(event.getFromId(), Host);
                    event.accept();
                }
            }
            getLogger().info(event.getMessage());
        });

        if (new SQData().isExist("ops.naibahq.com"))
            getLogger().info("SQLite正常");
        else getLogger().error("SQLite错误");
        getLogger().info("Plugin loaded!");
    }

    @Override
    public void onDisable() {
        listener.complete();
        getLogger().info("Plugin disabled!");
    }
}