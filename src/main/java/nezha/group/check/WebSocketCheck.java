package nezha.group.check;

import java.net.URI;
import java.net.URISyntaxException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocketCheck extends WebSocketClient {
    public int status = 0;
    public int count = 0;

    public WebSocketCheck(String url) throws URISyntaxException {
        super(new URI(url));
    }

    @Override
    public void onOpen(ServerHandshake shake) {
        status = 1;
    }

    @Override
    public void onMessage(String paramString) {
        // JSON 解析
        JSONObject data = JSONObject.parseObject(paramString);
        JSONArray servers = data.getJSONArray("servers");
        int server_count = 0;
        for (int i = 0; i < servers.size(); i++) {
            String LA = (String) servers.getJSONObject(i).get("LastActive");
            if (!LA.contains("0001-01-01"))
                server_count += 1;
        }
        count = server_count;
        status = 2;
    }

    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {

    }

    @Override
    public void onError(Exception e) {

    }

    public int check() {
        try {
            this.connect();
            int try_time = 0;
            while (try_time < 1000) {
                Thread.sleep(10);
                try_time += 1;
                if (status == 2)
                    break;
            }
            this.close();
            if (status != 2)
                return -1;
            else
                return count;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
