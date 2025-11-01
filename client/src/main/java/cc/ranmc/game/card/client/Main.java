package cc.ranmc.game.card.client;

import cc.ranmc.game.card.client.scene.PreLoadingScene;
import cc.ranmc.game.card.client.util.DialogUtil;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.scene.Scene;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static cc.ranmc.game.card.common.constant.BundleKey.SAVE;
import static cc.ranmc.game.card.common.constant.GameInfo.FONT;
import static cc.ranmc.game.card.common.constant.GameInfo.HEIGHT;
import static cc.ranmc.game.card.common.constant.GameInfo.NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.SAVE_FILE_NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.VERSION;
import static cc.ranmc.game.card.common.constant.GameInfo.WIDTH;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getSaveLoadService;

public class Main extends GameApplication {

    @Getter
    private static Scene scene = new PreLoadingScene();
    @Getter
    private static Bundle save = new Bundle(SAVE);

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setMainMenuEnabled(false);
        settings.setGameMenuEnabled(false);
        settings.setVersion(VERSION);
        settings.setTitle(NAME);
        settings.setAppIcon("icon.png");
        settings.getCSSList().add("style.css");
        settings.setFontGame(FONT);
        settings.setFontMono(FONT);
        settings.setFontUI(FONT);
        settings.setFontText(FONT);
        settings.setScaleAffectedOnResize(true);
        settings.setPreserveResizeRatio(true);
        settings.setManualResizeEnabled(true);

    }

    @Override
    protected void onPreInit() {
        getSaveLoadService().addHandler(new SaveLoadHandler() {
            @Override
            public void onSave(@NotNull DataFile data) {
                data.putBundle(save);
            }

            @Override
            public void onLoad(@NotNull DataFile data) {
                save = data.getBundle(SAVE);
            }
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        scene.update(tpf);
    }

    @Override
    protected void initGame() {
        FXGL.getSaveLoadService().readAndLoadTask(SAVE_FILE_NAME).run();
        scene.onCreate();
    }

    static void main(String[] args) {
        launch(args);
    }

    public static void changeScene(Scene newScene) {
        scene.onDestroy();
        FXGL.getGameScene().clearGameViews();
        FXGL.getGameScene().clearUINodes();
        FXGL.getGameTimer().clear();
        scene = newScene;
        scene.onCreate();
    }

}