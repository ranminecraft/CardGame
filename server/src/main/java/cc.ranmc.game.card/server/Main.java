package cc.ranmc.game.card.server;

import cc.ranmc.game.card.common.bean.Player;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Server;
import com.alibaba.fastjson2.JSONArray;
import com.almasb.fxgl.net.tcp.TCPServer;

import java.util.HashMap;
import java.util.Map;

import static cc.ranmc.game.card.common.constant.BundleKey.ID;
import static cc.ranmc.game.card.common.constant.BundleKey.MOVE;
import static cc.ranmc.game.card.common.constant.BundleKey.PLAYERS;

public class Main {

    private static final int PORT = 2261;
    private static Server<Bundle> server;
    private static final Map<String, Player> playerMap = new HashMap<>();
    private static int id = 0;

    static void main() {
        server = new TCPServer<>(PORT, Bundle.class);
        server.setOnConnected(connection -> {

            id++;
            playerMap.put(connection.toString(), new Player(id));
            Bundle bundle = new Bundle(ID);
            bundle.put(ID, id);
            connection.send(bundle);
            updatePlayerList();
            System.out.println("客户端连接 " + connection + " id" + id);

            connection.addMessageHandler((conn, message) -> {
                if (message.getName().equals(MOVE)) {
                    message.put(ID, playerMap.get(connection.toString()).getId());
                    server.broadcast(message);
                }
            });
        });
        server.setOnDisconnected(connection -> {
            playerMap.remove(connection.toString());
            updatePlayerList();
        });
        server.startAsync();
        System.out.println("服务器已启动，监听端口 " + PORT);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private static void updatePlayerList() {
        Bundle bundle = new Bundle(PLAYERS);
        JSONArray array = new JSONArray();
        for (String key : playerMap.keySet()) {
            array.add(playerMap.get(key));
        }
        bundle.put(PLAYERS, array.toString());
        server.broadcast(bundle);
    }
}
