package cc.ranmc.game.card.client;

import cc.ranmc.game.card.client.scene.PreLoadingScene;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.core.serialization.Bundle;
import org.jetbrains.annotations.NotNull;

import static cc.ranmc.game.card.common.constant.BundleKey.PLAYER_NAME;
import static cc.ranmc.game.card.common.constant.BundleKey.SAVE;
import static cc.ranmc.game.card.common.constant.GameInfo.FONT;
import static cc.ranmc.game.card.common.constant.GameInfo.HEIGHT;
import static cc.ranmc.game.card.common.constant.GameInfo.NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.SAVE_FILE_NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.VERSION;
import static cc.ranmc.game.card.common.constant.GameInfo.WIDTH;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getSaveLoadService;

public class Main extends GameApplication {

    private static Scene scene = new PreLoadingScene();
    public static String playerName = "无名氏";

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
    }

    @Override
    protected void onPreInit() {
        getSaveLoadService().addHandler(new SaveLoadHandler() {
            @Override
            public void onSave(@NotNull DataFile data) {
                Bundle bundle = new Bundle(SAVE);
                bundle.put(PLAYER_NAME, playerName);
                data.putBundle(bundle);
            }

            @Override
            public void onLoad(@NotNull DataFile data) {
                Bundle bundle = data.getBundle(SAVE);
                playerName = bundle.get(PLAYER_NAME);
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
        FXGL.getInput().clearAll();
        FXGL.getGameScene().getInput().clearAll();
        getGameTimer().clear();
        scene = newScene;
        scene.onCreate();
    }

}