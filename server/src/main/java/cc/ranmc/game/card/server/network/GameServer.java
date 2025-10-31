package cc.ranmc.game.card.server.network;

import cc.ranmc.game.card.common.bean.Player;
import cc.ranmc.game.card.server.constant.JsonKey;
import cc.ranmc.game.card.server.Main;
import com.alibaba.fastjson2.JSONArray;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.net.tcp.TCPServer;

import java.util.HashMap;
import java.util.Map;

import static cc.ranmc.game.card.common.constant.BundleKey.ID;
import static cc.ranmc.game.card.common.constant.BundleKey.MOVE;
import static cc.ranmc.game.card.common.constant.BundleKey.PLAYERS;
import static cc.ranmc.game.card.common.constant.BundleKey.PLAYER_NAME;
import static cc.ranmc.game.card.server.util.ConfigUtil.CONFIG;

public class GameServer {
    private static int id = 0;
    private static Server<Bundle> server;
    private static final Map<String, Player> playerMap = new HashMap<>();

    public static void start() {
        int port = CONFIG.getIntValue(JsonKey.TCP_PORT, 2261);
        server = new TCPServer<>(port, Bundle.class);
        server.setOnConnected(connection -> {

            id++;
            playerMap.put(connection.toString(), new Player(id));
            Bundle bundle = new Bundle(ID);
            bundle.put(ID, id);
            connection.send(bundle);
            Main.getLogger().info("客户端连接{} id{}", connection, id);

            connection.addMessageHandler((_, message) ->
                    handleMessage(connection.toString(), message));
        });
        server.setOnDisconnected(connection -> {
            Main.getLogger().info("客户端断开{} id{}", connection, playerMap.get(connection.toString()).getId());
            playerMap.remove(connection.toString());
            updatePlayerList();
        });
        server.startAsync();
        Main.getLogger().info("TCP已成功运行在端口 {}", port);
    }

    private static void handleMessage(String connectionKey,Bundle message) {
        if (message.getName().equals(MOVE)) {
            message.put(ID, playerMap.get(connectionKey).getId());
            server.broadcast(message);
        } else if (message.getName().equals(PLAYER_NAME)) {
            playerMap.get(connectionKey).setPlayerName(message.get(PLAYER_NAME));
            updatePlayerList();
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
