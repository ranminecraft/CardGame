package cc.ranmc.game.card.client;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.almasb.fxgl.core.serialization.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.ranmc.game.card.common.constant.BundleKey.ID;
import static cc.ranmc.game.card.common.constant.BundleKey.MOVE;
import static cc.ranmc.game.card.common.constant.BundleKey.PLAYERS;
import static cc.ranmc.game.card.common.constant.BundleKey.X;
import static cc.ranmc.game.card.common.constant.BundleKey.Y;
import static cc.ranmc.game.card.common.constant.GameInfo.VERSION;
import static cc.ranmc.game.card.common.constant.GameInfo.ADDRESS;
import static cc.ranmc.game.card.common.constant.GameInfo.PORT;

public class Main extends GameApplication {

    private int id = 0;
    private final Map<Integer, Entity> playerMap = new HashMap<>();

    private final double MOVE_SPEED = 5.0;
    private final double PLAYER_SIZE = 40;
    private static Connection<Bundle> clientConnection;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(960);
        settings.setHeight(540);
        settings.setGameMenuEnabled(false);
        //settings.setDefaultLanguage(Language.CHINESE);
        settings.setVersion(VERSION);
        settings.setTitle("卡牌游戏");
    }

    @Override
    protected void initInput() {
        Input input = FXGL.getInput();
        // W键 - 向上移动
        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                if (!playerMap.containsKey(id)) return;
                double newY = playerMap.get(id).getY() - MOVE_SPEED;
                if (newY >= 0) {
                    playerMap.get(id).setY(newY);
                }
            }
        }, KeyCode.W);

        // S键 - 向下移动
        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                if (!playerMap.containsKey(id)) return;
                double newY = playerMap.get(id).getY() + MOVE_SPEED;
                if (newY + PLAYER_SIZE <= FXGL.getAppHeight()) {
                    playerMap.get(id).setY(newY);
                }
            }
        }, KeyCode.S);

        // A键 - 向左移动
        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                if (!playerMap.containsKey(id)) return;
                double newX = playerMap.get(id).getX() - MOVE_SPEED;
                if (newX >= 0) {
                    playerMap.get(id).setX(newX);
                }
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                if (!playerMap.containsKey(id)) return;
                double newX = playerMap.get(id).getX() + MOVE_SPEED;
                if (newX + PLAYER_SIZE <= FXGL.getAppWidth()) {
                    playerMap.get(id).setX(newX);
                }
            }
        }, KeyCode.D);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (playerMap.containsKey(id) &&
                clientConnection != null &&
                clientConnection.isConnected()) {
            Bundle bundle = new Bundle(MOVE);
            bundle.put(X, playerMap.get(id).getX());
            bundle.put(Y, playerMap.get(id).getY());
            clientConnection.send(bundle);
        }
    }

    private void handleMessage(Bundle message) {
        if (message.getName().equals(MOVE)) {
            double x = message.get(X);
            double y = message.get(Y);
            int pid = message.get(ID);
            if (pid != id && playerMap.containsKey(pid)) {
                playerMap.get(pid).setX(x);
                playerMap.get(pid).setY(y);
            }
        } else if (message.getName().equals(ID)) {
            id = message.get(ID);
        } else if (message.getName().equals(PLAYERS)) {
            List<Integer> list = new ArrayList<>();
            JSONArray.parse(message.get(PLAYERS)).forEach(obj -> {
                int pid = ((JSONObject) obj).getInteger(ID);
                list.add(pid);
                if (!playerMap.containsKey(pid)) {
                    playerMap.put(pid,
                            FXGL.entityBuilder()
                                    .at(150, 150)
                                    .view(new Rectangle(PLAYER_SIZE, PLAYER_SIZE, Color.BLUE))
                                    .buildAndAttach());
                }
            });
            playerMap.keySet().removeIf(i -> {
                if (!list.contains(i)) {
                    playerMap.get(i).removeFromWorld();
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    protected void initGame() {
        connect();
    }

    protected void connect() {
        Client<Bundle> client = FXGL.getNetService().newTCPClient(ADDRESS, PORT);
        client.setOnConnected(connection -> {
            clientConnection = connection;
            connection.addMessageHandlerFX((_, message) -> {
                handleMessage(message);
            });
        });
        client.connectAsync();
    }

    static void main(String[] args) {
        launch(args);
    }
}