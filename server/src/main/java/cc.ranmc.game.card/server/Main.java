package cc.ranmc.game.card.server;

import cc.ranmc.game.card.common.bean.Player;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Server;
import com.alibaba.fastjson2.JSONArray;
import com.almasb.fxgl.net.tcp.TCPServer;
import io.github.biezhi.ome.OhMyEmail;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static cc.ranmc.game.card.common.constant.BundleKey.ID;
import static cc.ranmc.game.card.common.constant.BundleKey.MOVE;
import static cc.ranmc.game.card.common.constant.BundleKey.PLAYERS;
import static cc.ranmc.game.card.common.constant.BundleKey.PLAYER_NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.AUTHOR;
import static cc.ranmc.game.card.common.constant.GameInfo.VERSION;
import static io.github.biezhi.ome.OhMyEmail.defaultConfig;

public class Main {

    private static final int PORT = 2261;
    private static Server<Bundle> server;
    private static final Map<String, Player> playerMap = new HashMap<>();
    private static int id = 0;
    @Getter
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    static void main() {
        System.out.println("-----------------------");
        System.out.println(NAME + " By " + AUTHOR);
        System.out.println("Version: " + VERSION);
        System.out.println("-----------------------");

        // 初始化邮件
        Properties props = defaultConfig(false);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.qcloudmail.com");
        props.put("mail.smtp.port", "465");
        OhMyEmail.config(props, "bot@ranmc.cc", "");

        server = new TCPServer<>(PORT, Bundle.class);
        server.setOnConnected(connection -> {

            id++;
            playerMap.put(connection.toString(), new Player(id));
            Bundle bundle = new Bundle(ID);
            bundle.put(ID, id);
            connection.send(bundle);
            System.out.println("客户端连接 " + connection + " id" + id);

            connection.addMessageHandler((_, message) ->
                    handleMessage(connection.toString(), message));
        });
        server.setOnDisconnected(connection -> {
            System.out.println("客户端断开 " + connection + " id" + playerMap.get(connection.toString()).getId());
            playerMap.remove(connection.toString());
            updatePlayerList();
        });
        server.startAsync();
        System.out.println("已成功运行在端口 " + PORT);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
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
