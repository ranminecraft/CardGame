package cc.ranmc.game.card.client.scene;

import cc.ranmc.game.card.client.Main;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.FadeTransition;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;

public class PreLoadingScene extends Scene {

    @Override
    public void onCreate() {
        getGameScene().setCursor(Cursor.NONE);
        Texture background = FXGL.getAssetLoader().loadTexture("background.png");
        background.setFitWidth(960);
        background.setFitHeight(540);
        background.setTranslateX(0);
        background.setTranslateY(0);
        FXGL.getGameScene().addUINode(background);

        Texture logo = FXGL.getAssetLoader().loadTexture("logo.png");
        logo.setFitWidth(360);
        logo.setFitHeight(257);
        logo.setTranslateX(300);
        logo.setTranslateY(140);
        logo.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(2000), logo);
        fadeIn.setToValue(1);
        fadeIn.play();
        FXGL.getGameScene().addUINode(logo);

        /*Input input = FXGL.getGameScene().getInput();
        input.addAction(new UserAction("EscSkip") {
            @Override
            protected void onAction() {
                Main.changeScene(new MainMenuScene());
            }
        }, KeyCode.ESCAPE);
        input.addAction(new UserAction("SpaceSkip") {
            @Override
            protected void onAction() {
                Main.changeScene(new MainMenuScene());
            }
        }, KeyCode.SPACE);
        input.addAction(new UserAction("EnterSkip") {
            @Override
            protected void onAction() {
                Main.changeScene(new MainMenuScene());
            }
        }, KeyCode.ENTER);*/

        getGameTimer().runOnceAfter(() ->
                Main.changeScene(new MainMenuScene()), Duration.millis(6000));
    }

}
