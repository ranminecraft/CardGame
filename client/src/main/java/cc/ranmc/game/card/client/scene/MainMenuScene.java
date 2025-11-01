package cc.ranmc.game.card.client.scene;

import cc.ranmc.game.card.client.Main;
import cc.ranmc.game.card.client.util.ApiUtil;
import cc.ranmc.game.card.client.util.DialogUtil;
import cc.ranmc.game.card.client.util.HttpUtil;
import cc.ranmc.game.card.client.util.InputUtil;
import cc.ranmc.game.card.common.constant.BundleKey;
import cc.ranmc.game.card.common.constant.GameInfo;
import cc.ranmc.game.card.common.constant.HttpResponse;
import cc.ranmc.game.card.common.constant.JsonKey;
import com.alibaba.fastjson2.JSONObject;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.texture.Texture;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.awt.*;
import java.net.URI;

import static cc.ranmc.game.card.common.constant.GameInfo.SAVE_FILE_NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.VERSION;
import static cc.ranmc.game.card.common.constant.HttpPath.INFO_PATH;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class MainMenuScene extends Scene {

    private int money;
    private String name;

    @Override
    public void onCreate() {
        getGameScene().setCursor(Cursor.DEFAULT);

        Texture background = FXGL.getAssetLoader().loadTexture("lobby.png");
        background.setFitWidth(960);
        background.setFitHeight(540);
        background.setTranslateX(0);
        background.setTranslateY(0);
        FXGL.getGameScene().addUINode(background);

        Texture author = FXGL.getAssetLoader().loadTexture("author.png");
        author.setFitWidth(100);
        author.setFitHeight(130);
        author.setTranslateX(840);
        author.setTranslateY(400);
        FXGL.getGameScene().addUINode(author);

        Button playBtn = new Button("开始游戏");
        playBtn.getStyleClass().add("play-button");
        playBtn.setOnAction(_ -> Main.changeScene(new GameScene()));
        FXGL.getGameScene().addUINode(playBtn);

        Button logoutBtn = new Button("退出登陆");
        logoutBtn.getStyleClass().add("small-button");
        logoutBtn.setOnAction(_ -> {
            Main.getSave().put(BundleKey.TOKEN, "");
            FXGL.getSaveLoadService().saveAndWriteTask(SAVE_FILE_NAME).run();
            Main.changeScene(new LoginScene());
        });

        Button fullscreenBtn = new Button("切换全屏");
        fullscreenBtn.setOnAction(e -> {
            boolean isFullScreen = FXGL.getPrimaryStage().isFullScreen();
            FXGL.getPrimaryStage().setFullScreen(!isFullScreen);
        });
        fullscreenBtn.getStyleClass().add("small-button");

        VBox btnBox = new VBox(10, playBtn, fullscreenBtn, logoutBtn);
        btnBox.setAlignment(Pos.CENTER);
        javafx.application.Platform.runLater(() -> {
            btnBox.setTranslateX((GameInfo.WIDTH - btnBox.getWidth()) / 2);
            btnBox.setTranslateY((GameInfo.HEIGHT - btnBox.getHeight()) / 2);
        });
        FXGL.getGameScene().addUINode(btnBox);

        Text moneyText = getUIFactoryService().newText("", Color.GOLD, 20);
        Text nameText = getUIFactoryService().newText("", Color.WHITE, 30);
        Text versionText = getUIFactoryService().newText("当前游戏版本 " + VERSION, Color.WHITE, 14);
        Text groupText = getUIFactoryService().newText("企鹅群 207302647", Color.WHITE, 14);
        groupText.setOnMouseClicked(_ -> {
            try {
                Desktop.getDesktop().browse(new URI("https://qm.qq.com/q/xF5cjEck2Q"));
            } catch (Exception e) {
                DialogUtil.show("打开链接失败");
            }
        });
        groupText.setOnMouseEntered(_ -> groupText.setUnderline(true));
        groupText.setOnMouseExited(_ -> groupText.setUnderline(false));

        VBox textBox = new VBox(0, moneyText, nameText, versionText, groupText);
        textBox.setTranslateX(10);
        Platform.runLater(() -> textBox.setTranslateY(GameInfo.HEIGHT - textBox.getHeight() - 20));
        FXGL.getGameScene().addUINode(textBox);

        InputUtil.addEnd(()-> {
            GameInfo.TCP_PORT = 2261;
            GameInfo.HTTP_PORT = 2262;
            GameInfo.ADDRESS = "localhost";
            DialogUtil.show("已开启调试模式");
        }, KeyCode.F12, this.getClass().toString());

        JSONObject json = new JSONObject();
        json.put(JsonKey.TOKEN, Main.getSave().get(BundleKey.TOKEN));
        HttpUtil.post(ApiUtil.get(INFO_PATH), json.toString(), body -> {
            if (body.isEmpty()) {
                DialogUtil.show("连接服务器失败");
                return;
            }
            JSONObject bodyJson = JSONObject.parseObject(body);
            int code = bodyJson.getIntValue(JsonKey.CODE, 0);
            if (code == HttpResponse.SC_OK) {
                money = bodyJson.getIntValue(JsonKey.MONEY, 0);
                name = bodyJson.getString(JsonKey.NAME);
                moneyText.setText("金币 " + money);
                nameText.setText("来玩吧，" + name + "！");
            } else if (code == HttpResponse.SC_UNAUTHORIZED) {
                DialogUtil.show("登陆已过期");
                Main.changeScene(new LoginScene());
            } else {
                DialogUtil.show(bodyJson.getString(JsonKey.MSG));
            }
        });
    }

}
