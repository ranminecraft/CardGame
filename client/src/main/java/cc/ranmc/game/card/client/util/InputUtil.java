package cc.ranmc.game.card.client.util;

import cc.ranmc.game.card.client.Main;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputUtil {

    private static final Map<KeyCode, Map<String, Runnable>> keyRunnableMap = new HashMap<>();
    private static final Map<KeyCode, Map<String, Runnable>> keyEndRunnableMap = new HashMap<>();
    private static final List<KeyCode> keyList = new ArrayList<>();

    public static void add(Runnable runnable, KeyCode keyCode, String sceneName) {
        initKey(keyCode);
        Map<String, Runnable> runnableMap = getRunnableMap(keyCode);
        runnableMap.put(sceneName, runnable);
        keyRunnableMap.put(keyCode, runnableMap);
    }

    public static void addEnd(Runnable runnable, KeyCode keyCode, String sceneName) {
        initKey(keyCode);
        Map<String, Runnable> runnableMap = getEndRunnableMap(keyCode);
        runnableMap.put(sceneName, runnable);
        keyEndRunnableMap.put(keyCode, runnableMap);
    }

    private static void initKey(KeyCode keyCode) {
        if (keyList.contains(keyCode)) return;
        FXGL.getInput().addAction(new UserAction(keyCode.toString()) {
            @Override
            protected void onAction() {
                Runnable runnable = getRunnableMap(keyCode).get(Main.getScene().getClass().toString());
                if (runnable != null) runnable.run();
            }

            @Override
            protected void onActionEnd() {
                Runnable runnable = getEndRunnableMap(keyCode).get(Main.getScene().getClass().toString());
                if (runnable != null) runnable.run();
            }
        }, keyCode);
        keyList.add(keyCode);
    }

    private static Map<String, Runnable> getRunnableMap(KeyCode keyCode) {
        return keyRunnableMap.getOrDefault(keyCode, new HashMap<>());
    }

    private static Map<String, Runnable> getEndRunnableMap(KeyCode keyCode) {
        return keyEndRunnableMap.getOrDefault(keyCode, new HashMap<>());
    }

}
