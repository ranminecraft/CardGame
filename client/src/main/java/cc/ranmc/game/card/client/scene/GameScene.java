package cc.ranmc.game.card.client.scene;

import cc.ranmc.game.card.client.Main;
import cc.ranmc.game.card.client.component.PlayerComponent;
import cc.ranmc.game.card.client.util.DialogUtil;
import cc.ranmc.game.card.client.util.InputUtil;
import cc.ranmc.game.card.common.constant.BundleKey;
import cc.ranmc.game.card.common.constant.GameInfo;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.ranmc.game.card.common.constant.BundleKey.CHAT;
import static cc.ranmc.game.card.common.constant.BundleKey.MOVE;
import static cc.ranmc.game.card.common.constant.GameInfo.ADDRESS;
import static cc.ranmc.game.card.common.constant.GameInfo.TCP_PORT;

public class GameScene extends Scene {

    private int id = 0;
    private final Map<Integer, Entity> playerMap = new HashMap<>();
    private static final double MOVE_SPEED = 1;
    private static Connection<Bundle> clientConnection;
    private static Client<Bundle> client;
    private static Text helpText;
    private static int fps;
    private static long latency;
    private static long lastPingTime;
    private double lastX = 0;
    private double lastY = 0;
    private static final int player_width = 32;
    private static final int player_height = 48;

    @Override
    public void onDestroy() {
        if (client != null) client.disconnect();
    }

    @Override
    public void onCreate() {
        Texture background = FXGL.getAssetLoader().loadTexture("game.png");
        background.setFitWidth(960);
        background.setFitHeight(540);
        background.setTranslateX(0);
        background.setTranslateY(0);
        FXGL.entityBuilder()
                .at(0, 0)
                .view(background)
                .zIndex(-1000)
                .buildAndAttach();

        helpText = new Text("请稍后，正在连接服务器中...");
        helpText.setFont(Font.font(18));
        FXGL.addUINode(helpText, 10, 20);

        connect();

        Text statusText = new Text("FPS: 0 | Ping: 0ms");
        statusText.setFont(Font.font(16));
        statusText.setFill(Color.WHITE);
        FXGL.addUINode(statusText, FXGL.getAppWidth() - 180, 20);
        statusText.setVisible(false);
        FXGL.getGameTimer().runAtInterval(() -> {
            statusText.setText("FPS: " + fps + " | Ping: " + latency + "ms");
        }, Duration.seconds(1));

        FXGL.getGameTimer().runAtInterval(() -> {
            if (clientConnection != null && clientConnection.isConnected()) {
                clientConnection.send(new Bundle(BundleKey.PING));
                lastPingTime = System.currentTimeMillis();
            }
        }, Duration.seconds(10));

        InputUtil.addEnd(()-> statusText.setVisible(!statusText.isVisible()),
                KeyCode.F3, this.getClass().toString());

        InputUtil.add(()-> {
            if (!playerMap.containsKey(id)) return;
            double newY = playerMap.get(id).getY() - MOVE_SPEED;
            if (newY >= 0) {
                playerMap.get(id).setY(newY);
                playerMap.get(id).getComponent(PlayerComponent.class).moveUp();
            }
        }, KeyCode.W, this.getClass().toString());

        InputUtil.add(()-> {
            if (!playerMap.containsKey(id)) return;
            double newY = playerMap.get(id).getY() + MOVE_SPEED;
            if (newY + player_height <= FXGL.getAppHeight()) {
                playerMap.get(id).setY(newY);
                playerMap.get(id).getComponent(PlayerComponent.class).moveDown();
            }
        }, KeyCode.S, this.getClass().toString());

        InputUtil.add(()-> {
            if (!playerMap.containsKey(id)) return;
            double newX = playerMap.get(id).getX() - MOVE_SPEED;
            if (newX >= 0) {
                playerMap.get(id).setX(newX);
                playerMap.get(id).getComponent(PlayerComponent.class).moveLeft();
            }
        }, KeyCode.A, this.getClass().toString());

        InputUtil.add(()-> {
            if (!playerMap.containsKey(id)) return;
            double newX = playerMap.get(id).getX() + MOVE_SPEED;
            if (newX + player_width <= FXGL.getAppWidth()) {
                playerMap.get(id).setX(newX);
                playerMap.get(id).getComponent(PlayerComponent.class).moveRight();
            }
        }, KeyCode.D, this.getClass().toString());

        Runnable stopComponent = ()-> {
            if (!playerMap.containsKey(id)) return;
            playerMap.get(id).getComponent(PlayerComponent.class).stop();
        };
        InputUtil.addEnd(stopComponent, KeyCode.W, this.getClass().toString());
        InputUtil.addEnd(stopComponent, KeyCode.S, this.getClass().toString());
        InputUtil.addEnd(stopComponent, KeyCode.A, this.getClass().toString());
        InputUtil.addEnd(stopComponent, KeyCode.D, this.getClass().toString());

        InputUtil.addEnd(()-> {
            DialogUtil.input("请输入聊天内容", msg -> {
                if (msg.length() > 20) {
                    DialogUtil.show("聊天内容过长");
                    return;
                }
                if (msg.isEmpty()) return;
                Bundle bundle = new Bundle(CHAT);
                bundle.put(CHAT, msg);
                clientConnection.send(bundle);
            });
        }, KeyCode.ENTER, this.getClass().toString());

        InputUtil.addEnd(()-> {
            id = 0;
            playerMap.clear();
            client.disconnect();
            Main.changeScene(new MainMenuScene());
        }, KeyCode.ESCAPE, this.getClass().toString());
    }

    @Override
    protected void onUpdate(double tpf) {
        fps = (int) (1 / tpf);
    }

    protected void updateData() {
        if (playerMap.containsKey(id) &&
                clientConnection != null &&
                clientConnection.isConnected()) {
            double x = playerMap.get(id).getX();
            double y = playerMap.get(id).getY();
            if (x != lastX || y != lastY) {
                lastX = x;
                lastY = y;
                Bundle bundle = new Bundle(MOVE);
                bundle.put(BundleKey.X, x);
                bundle.put(BundleKey.Y, y);
                bundle.put(BundleKey.DIRECTION, playerMap.get(id).getComponent(PlayerComponent.class).getDirection());
                clientConnection.send(bundle);
            }
        }
        if (clientConnection != null && !clientConnection.isConnected()) {
            Main.changeScene(new MainMenuScene());
            DialogUtil.show("与服务器断开连接");
        }
    }

    protected void connect() {
        client = FXGL.getNetService().newTCPClient(ADDRESS, TCP_PORT);
        client.setOnConnected(connection -> {
            clientConnection = connection;
            helpText.setText("WSAD 移动  Esc 返回主菜单  Enter 聊天  F3 帧率显示");
            Bundle bundle = new Bundle(BundleKey.JOIN);

            String token = Main.getSave().get(BundleKey.TOKEN);
            if (token == null || token.isEmpty()) {
                client.disconnect();
                return;
            }
            bundle.put(BundleKey.TOKEN, token);
            bundle.put(BundleKey.VERSION, GameInfo.VERSION);
            clientConnection.send(bundle);

            FXGL.getGameTimer().runAtInterval(this::updateData, Duration.millis(10));

            connection.addMessageHandlerFX((_, message) ->
                    handleMessage(message));
        });
        client.connectTask()
                .onFailure(error -> {
                    Main.changeScene(new MainMenuScene());
                    DialogUtil.show("无法连接服务器\n" + error.getMessage());
                }).run();
    }

    private void handleMessage(Bundle message) {
        if (message.getName().equals(BundleKey.MOVE)) {
            int pid = message.get(BundleKey.ID);
            if (pid != id && playerMap.containsKey(pid)) {
                PlayerComponent playerComponent = playerMap.get(pid).getComponent(PlayerComponent.class);
                playerComponent.setDirection(message.get(BundleKey.DIRECTION));
                playerComponent.setDestination(message.get(BundleKey.X), message.get(BundleKey.Y));
            }
        } else if (message.getName().equals(BundleKey.ID)) {
            id = message.get(BundleKey.ID);
        } else if (message.getName().equals(BundleKey.PONG)) {
            latency = System.currentTimeMillis() - lastPingTime;
        } else if (message.getName().equals(BundleKey.DISCONNECT)) {
            client.disconnect();
            Main.changeScene(new MainMenuScene());
            DialogUtil.show(message.get(BundleKey.DISCONNECT));
        } else if (message.getName().equals(BundleKey.CHAT)) {
            System.out.println(message);
            int pid = message.get(BundleKey.ID);
            if (playerMap.containsKey(pid)) {
                Text chatText = (Text) playerMap.get(pid).getViewComponent().getChildren().get(2);
                chatText.setText("：" + message.get(BundleKey.CHAT));
                FXGL.runOnce(() -> chatText.setText(""), Duration.seconds(5));
            }
        } else if (message.getName().equals(BundleKey.PLAYERS)) {
            lastX = 0;
            lastY = 0;
            List<Integer> list = new ArrayList<>();
            JSONArray.parse(message.get(BundleKey.PLAYERS)).forEach(obj -> {
                JSONObject json = (JSONObject) obj;
                int pid = json.getInteger(BundleKey.ID);
                list.add(pid);
                if (!playerMap.containsKey(pid)) {
                    Text nameText = new Text(json.getString(BundleKey.PLAYER_NAME));
                    nameText.setFill(Color.BLUE);
                    nameText.setFont(Font.font(16));
                    nameText.setTranslateY(-10);
                    nameText.setTranslateX((double) player_width / 2 - nameText.getBoundsInLocal().getWidth() / 2);
                    Text chatText = new Text();
                    chatText.setFill(Color.GREEN);
                    chatText.setFont(Font.font(18));
                    chatText.setTranslateY(25);
                    chatText.setTranslateX(player_width);
                    playerMap.put(pid,
                            FXGL.entityBuilder()
                                    .at(150, 150)
                                    .with(new PlayerComponent(id == pid ? "p.png" : "p2.png"))
                                    .view(nameText)
                                    .view(chatText)
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
}
