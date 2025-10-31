package cc.ranmc.game.card.client.scene;

import cc.ranmc.game.card.client.Main;
import cc.ranmc.game.card.client.util.ApiUtil;
import cc.ranmc.game.card.common.constant.BundleKey;
import cc.ranmc.game.card.common.constant.HttpResponse;
import cc.ranmc.game.card.common.constant.JsonKey;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static cc.ranmc.game.card.common.constant.GameInfo.SAVE_FILE_NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.VERSION;
import static cc.ranmc.game.card.common.constant.HttpPath.INFO_PATH;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
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
        author.setFitHeight(129);
        author.setTranslateX(840);
        author.setTranslateY(401);
        FXGL.getGameScene().addUINode(author);

        Button playBtn = new Button("开始游戏");
        playBtn.setTranslateX(425);
        playBtn.setTranslateY(210);
        playBtn.getStyleClass().add("play-button");
        playBtn.setOnAction(_ -> Main.changeScene(new GameScene()));
        FXGL.getGameScene().addUINode(playBtn);

        Text moneyText = getUIFactoryService().newText("", Color.GOLD, 22);
        moneyText.setTranslateX(12);
        moneyText.setTranslateY(460);
        FXGL.getGameScene().addUINode(moneyText);

        Text nameText = getUIFactoryService().newText("", Color.WHITE, 30);
        nameText.setTranslateX(12);
        nameText.setTranslateY(495);
        FXGL.getGameScene().addUINode(nameText);

        Button logoutBtn = new Button("退出登陆");
        logoutBtn.setTranslateX(434);
        logoutBtn.setTranslateY(325);
        logoutBtn.getStyleClass().add("mode-button");
        FXGL.getGameScene().addUINode(logoutBtn);
        logoutBtn.setOnAction(_ -> {
            Main.getSave().put(BundleKey.TOKEN, "");
            FXGL.getSaveLoadService().saveAndWriteTask(SAVE_FILE_NAME).run();
            Main.changeScene(new LoginScene());
        });

        Text versionText = getUIFactoryService().newText("当前游戏版本：" + VERSION, Color.WHITE, 13);
        versionText.setTranslateX(12);
        versionText.setTranslateY(520);
        FXGL.getGameScene().addUINode(versionText);

        JSONObject json = new JSONObject();
        json.put(JsonKey.TOKEN, Main.getSave().get(BundleKey.TOKEN));
        try {
            String body = HttpUtil.post(ApiUtil.get(INFO_PATH), json.toString());
            json = JSONObject.parseObject(body);
            int code = json.getIntValue(JsonKey.CODE, 0);
            if (code == HttpResponse.SC_OK) {
                money = json.getIntValue(JsonKey.MONEY, 0);
                name = json.getString(JsonKey.NAME);
                moneyText.setText("金币 " + money);
                nameText.setText("来玩吧，" + name + "！");
            } else if (code == HttpResponse.SC_UNAUTHORIZED) {
                getDialogService().showMessageBox("登陆已过期");
                Main.changeScene(new LoginScene());
            } else {
                getDialogService().showMessageBox(json.getString(JsonKey.MSG));
            }
        } catch (Exception e) {
            getDialogService().showMessageBox("获取账号数据失败\n" + e.getMessage());
        }
    }

}
