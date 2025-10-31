package cc.ranmc.game.card.client.scene;

import cc.ranmc.game.card.client.Main;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.regex.Pattern;

import static cc.ranmc.game.card.common.constant.GameInfo.NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.SAVE_FILE_NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.VERSION;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class MainMenuScene extends Scene {

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

        Text helpText = getUIFactoryService().newText("点击开始游戏。", Color.WHITE, 22);
        helpText.setTranslateX(12);
        helpText.setTranslateY(460);
        FXGL.getGameScene().addUINode(helpText);

        String name = Main.getSave().get(NAME);
        if (name == null || name.isEmpty()) name = "无名氏";
        Text help2Text = getUIFactoryService().newText("来玩吧，" + name + "！", Color.WHITE, 30);
        help2Text.setTranslateX(12);
        help2Text.setTranslateY(495);
        FXGL.getGameScene().addUINode(help2Text);

        Button logoutBtn = new Button("退出登陆");
        logoutBtn.setTranslateX(434);
        logoutBtn.setTranslateY(325);
        logoutBtn.getStyleClass().add("mode-button");
        FXGL.getGameScene().addUINode(logoutBtn);
        logoutBtn.setOnAction(_ -> Main.changeScene(new LoginScene()));

        Text versionText = getUIFactoryService().newText("当前游戏版本：" + VERSION, Color.WHITE, 13);
        versionText.setTranslateX(12);
        versionText.setTranslateY(520);
        FXGL.getGameScene().addUINode(versionText);
    }
}
