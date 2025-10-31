package cc.ranmc.game.card.server.util;

import cc.ranmc.game.card.server.Main;
import com.alibaba.fastjson2.JSONObject;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    public static JSONObject CONFIG = new JSONObject();
    public static void load() {
        try {
            File file = new File(System.getProperty("user.dir") + "/config.json");
            CONFIG = JSONObject.parseObject(FileUtils.fileRead(file, "utf8"));
        } catch (IOException e) {
            Main.getLogger().error(e.getMessage());
        }
    }
}

