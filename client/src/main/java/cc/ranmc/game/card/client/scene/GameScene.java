package cc.ranmc.game.card.client.scene;

import cc.ranmc.game.card.client.Main;
import cc.ranmc.game.card.client.util.InputUtil;
import cc.ranmc.game.card.common.constant.BundleKey;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
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

import static cc.ranmc.game.card.common.constant.BundleKey.ID;
import static cc.ranmc.game.card.common.constant.BundleKey.MOVE;
import static cc.ranmc.game.card.common.constant.BundleKey.PLAYERS;
import static cc.ranmc.game.card.common.constant.BundleKey.PLAYER_NAME;
import static cc.ranmc.game.card.common.constant.BundleKey.TOKEN;
import static cc.ranmc.game.card.common.constant.BundleKey.X;
import static cc.ranmc.game.card.common.constant.BundleKey.Y;
import static cc.ranmc.game.card.common.constant.GameInfo.ADDRESS;
import static cc.ranmc.game.card.common.constant.GameInfo.NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.TCP_PORT;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;

public class GameScene extends Scene {

    private int id = 0;
    private final Map<Integer, Entity> playerMap = new HashMap<>();
    private final double MOVE_SPEED = 2.0;
    private final double PLAYER_SIZE = 40;
    private static Connection<Bundle> clientConnection;
    private static Client<Bundle> client;
    private static Text helpText;

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

        InputUtil.add(()-> {
            if (!playerMap.containsKey(id)) return;
            double newY = playerMap.get(id).getY() - MOVE_SPEED;
            if (newY >= 0) {
                playerMap.get(id).setY(newY);
            }
        }, KeyCode.W, this.getClass().toString());

        InputUtil.add(()-> {
            if (!playerMap.containsKey(id)) return;
            double newY = playerMap.get(id).getY() + MOVE_SPEED;
            if (newY + PLAYER_SIZE <= FXGL.getAppHeight()) {
                playerMap.get(id).setY(newY);
            }
        }, KeyCode.S, this.getClass().toString());

        InputUtil.add(()-> {
            if (!playerMap.containsKey(id)) return;
            double newX = playerMap.get(id).getX() - MOVE_SPEED;
            if (newX >= 0) {
                playerMap.get(id).setX(newX);
            }
        }, KeyCode.A, this.getClass().toString());

        InputUtil.add(()-> {
            if (!playerMap.containsKey(id)) return;
            double newX = playerMap.get(id).getX() + MOVE_SPEED;
            if (newX + PLAYER_SIZE <= FXGL.getAppWidth()) {
                playerMap.get(id).setX(newX);
            }
        }, KeyCode.D, this.getClass().toString());

        InputUtil.add(()-> {
            id = 0;
            playerMap.clear();
            client.disconnect();
            Main.changeScene(new MainMenuScene());
        }, KeyCode.ESCAPE, this.getClass().toString());
    }

    protected void updateData() {
        if (playerMap.containsKey(id) &&
                clientConnection != null &&
                clientConnection.isConnected()) {
            Bundle bundle = new Bundle(MOVE);
            bundle.put(X, playerMap.get(id).getX());
            bundle.put(Y, playerMap.get(id).getY());
            clientConnection.send(bundle);
        }
        if (clientConnection != null && !clientConnection.isConnected()) {
            Main.changeScene(new MainMenuScene());
            getDialogService().showMessageBox("与服务器断开连接");
        }
    }

    protected void connect() {
        client = FXGL.getNetService().newTCPClient(ADDRESS, TCP_PORT);
        client.setOnConnected(connection -> {
            clientConnection = connection;
            helpText.setText("WSAD 移动  Esc 返回主菜单");
            Bundle bundle = new Bundle(BundleKey.TOKEN);

            String token = Main.getSave().get(BundleKey.TOKEN);
            if (token == null || token.isEmpty()) {
                client.disconnect();
                return;
            }
            bundle.put(BundleKey.TOKEN, token);
            clientConnection.send(bundle);

            FXGL.getGameTimer().runAtInterval(this::updateData, Duration.millis(10));

            connection.addMessageHandlerFX((_, message) ->
                    handleMessage(message));
        });
        client.connectTask()
                .onFailure(error -> {
                    FXGL.getDialogService().showMessageBox("无法连接服务器\n" + error.getMessage());
                    Main.changeScene(new MainMenuScene());
                }).run();
    }

    private void handleMessage(Bundle message) {
        if (message.getName().equals(MOVE)) {
            int pid = message.get(ID);
            if (pid != id && playerMap.containsKey(pid)) {
                playerMap.get(pid).setX(message.get(X));
                playerMap.get(pid).setY(message.get(Y));
            }
        } else if (message.getName().equals(ID)) {
            id = message.get(ID);
        } else if (message.getName().equals(PLAYERS)) {
            List<Integer> list = new ArrayList<>();
            JSONArray.parse(message.get(PLAYERS)).forEach(obj -> {
                JSONObject json = (JSONObject) obj;
                int pid = json.getInteger(ID);
                list.add(pid);
                if (!playerMap.containsKey(pid)) {
                    Text nameText = new Text(json.getString(PLAYER_NAME));
                    nameText.setFill(Color.BLUE);
                    nameText.setFont(Font.font(15));
                    nameText.setTranslateY(-10);
                    playerMap.put(pid,
                            FXGL.entityBuilder()
                                    .at(150, 150)
                                    .view("player.png")
                                    .view(nameText)
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
