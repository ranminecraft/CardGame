package cc.ranmc.game.card.client.util;

import cc.ranmc.game.card.client.Main;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class InputUtil {

    private static final Map<KeyCode, Map<String, Runnable>> keyRunnableMap = new HashMap<>();

    public static void add(Runnable runnable, KeyCode keyCode, String sceneName) {
        if (!keyRunnableMap.containsKey(keyCode)) {
            FXGL.getInput().addAction(new UserAction(keyCode.toString()) {
                @Override
                protected void onAction() {
                    Runnable runnable = getRunnableMap(keyCode).get(Main.scene.getClass().toString());
                    if (runnable != null) runnable.run();
                }
            }, keyCode);
        }
        Map<String, Runnable> runnableMap = getRunnableMap(keyCode);
        runnableMap.put(sceneName, runnable);
        keyRunnableMap.put(keyCode, runnableMap);
    }

    private static Map<String, Runnable> getRunnableMap(KeyCode keyCode) {
        return keyRunnableMap.getOrDefault(keyCode, new HashMap<>());
    }

}
