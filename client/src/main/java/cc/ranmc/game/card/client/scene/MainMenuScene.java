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

        Text help2Text = getUIFactoryService().newText("来玩吧，" + Main.getPlayerName() + "！", Color.WHITE, 30);
        help2Text.setTranslateX(12);
        help2Text.setTranslateY(495);
        FXGL.getGameScene().addUINode(help2Text);

        Button nameBtn = new Button("更改名字");
        nameBtn.setTranslateX(434);
        nameBtn.setTranslateY(325);
        nameBtn.getStyleClass().add("mode-button");
        FXGL.getGameScene().addUINode(nameBtn);
        nameBtn.setOnAction(_ -> {
            getDialogService().showInputBox("输入您的游戏名称", answer -> {
                if (answer == null ||
                        !Pattern.compile("^[a-zA-Z一-龥0-9-_.()（）~]{1,6}$")
                                .matcher(answer).matches()) {
                    getDialogService().showMessageBox("名称过长或不规范");
                } else {
                    Main.setPlayerName(answer);
                    help2Text.setText("来玩吧，" + answer + "！");
                    FXGL.getSaveLoadService().saveAndWriteTask(SAVE_FILE_NAME).run();
                    if (answer.equals("阿然")) {
                        getDialogService().showMessageBox("嗯？另一个阿然！");
                    } else {
                        getDialogService().showMessageBox("修改名称成功");
                    }
                }
            });
        });

        Text versionText = getUIFactoryService().newText("当前游戏版本：" + VERSION, Color.WHITE, 13);
        versionText.setTranslateX(12);
        versionText.setTranslateY(520);
        FXGL.getGameScene().addUINode(versionText);
    }
}
