package cc.ranmc.game.card.server.network;

import cc.ranmc.game.card.common.bean.Player;
import cc.ranmc.game.card.common.constant.BundleKey;
import cc.ranmc.game.card.common.constant.JsonKey;
import cc.ranmc.game.card.server.Main;
import cc.ranmc.game.card.server.constant.SQLKey;
import cc.ranmc.game.card.server.sql.SQLFilter;
import cc.ranmc.game.card.server.sql.SQLRow;
import cc.ranmc.game.card.server.util.JwtTokenUtil;
import com.alibaba.fastjson2.JSONArray;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.net.tcp.TCPServer;

import java.util.HashMap;
import java.util.Map;

import static cc.ranmc.game.card.common.constant.BundleKey.ID;
import static cc.ranmc.game.card.common.constant.BundleKey.PLAYERS;
import static cc.ranmc.game.card.server.util.ConfigUtil.CONFIG;

public class GameServer {
    private static Server<Bundle> server;
    private static final Map<Connection<Bundle>, Player> playerMap = new HashMap<>();

    public static void start() {
        int port = CONFIG.getIntValue(JsonKey.TCP_PORT, 2261);
        server = new TCPServer<>(port, Bundle.class);
        server.setOnConnected(connection -> {
            Main.getLogger().info("客户端连接{}", connection);
            connection.addMessageHandler((_, message) ->
                    handleMessage(connection, message));
        });
        server.setOnDisconnected(connection -> {
            Main.getLogger().info("客户端断开{}", connection);
            playerMap.remove(connection);
            updatePlayerList();
        });
        server.startAsync();
        Main.getLogger().info("TCP已成功运行在端口 {}", port);
    }

    private static void handleMessage(Connection<Bundle> connection, Bundle message) {
        if (message.getName().equals(BundleKey.MOVE)) {
            message.put(BundleKey.ID, playerMap.get(connection).getId());
            server.broadcast(message);
        } else if (message.getName().equals(BundleKey.PING)) {
            connection.send(new Bundle(BundleKey.PONG));
        } else if (message.getName().equals(BundleKey.CHAT)) {
            Player player = playerMap.get(connection);
            message.put(BundleKey.ID, player.getId());
            Main.getLogger().info("id{} 玩家名{} 说 {}",
                    player.getId(), player.getPlayerName(), message.get(BundleKey.CHAT));
            server.broadcast(message);
        } else if (message.getName().equals(BundleKey.TOKEN)) {
            String token = message.get(BundleKey.TOKEN);
            if (!JwtTokenUtil.validate(token)) {
                connection.terminate();
                Main.getLogger().warn("连接断开 Token 过期");
                return;
            }
            String email;
            try {
                email = JwtTokenUtil.getEmail(token);
            } catch (Exception e) {
                connection.terminate();
                Main.getLogger().warn("连接断开 无法获取邮箱");
                return;
            }
            SQLRow sqlRow = Main.getData().selectRow(SQLKey.PLAYER,
                    new SQLFilter().where(SQLKey.EMAIL, email));
            for (Connection<Bundle> c : playerMap.keySet()) {
                if (playerMap.get(c).getPlayerName().equals(sqlRow.getString(SQLKey.NAME))) {
                    c.terminate();
                    Main.getLogger().warn("连接断开 玩家在别处连接");
                    break;
                }
            }
            Player player = new Player();
            player.setId(sqlRow.getInt(SQLKey.ID));
            player.setPlayerName(sqlRow.getString(SQLKey.NAME));
            playerMap.put(connection, player);
            Bundle bundle = new Bundle(ID);
            bundle.put(ID, sqlRow.getInt(SQLKey.ID));
            connection.send(bundle);
            updatePlayerList();
        }
    }

    private static void updatePlayerList() {
        Bundle bundle = new Bundle(PLAYERS);
        JSONArray array = new JSONArray();
        array.addAll(playerMap.values());
        bundle.put(PLAYERS, array.toString());
        server.broadcast(bundle);
    }
}
