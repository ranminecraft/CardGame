package cc.ranmc.game.card.server.network;

import cc.ranmc.game.card.server.constant.ConfigKey;
import cc.ranmc.game.card.server.Main;
import cc.ranmc.game.card.server.network.handler.BaseHandler;
import io.javalin.Javalin;

import static cc.ranmc.game.card.server.util.ConfigUtil.CONFIG;

public class HttpServer {
    public static void start() {
        int port = CONFIG.getIntValue(ConfigKey.HTTP_PORT, 2262);
        Javalin.create()
                .get("/", BaseHandler::handle)
                .start(port);
        Main.getLogger().info("HTTP已成功运行在端口 {}", port);
    }
}
