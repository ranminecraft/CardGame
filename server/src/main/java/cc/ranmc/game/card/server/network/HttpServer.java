package cc.ranmc.game.card.server.network;

import cc.ranmc.game.card.common.constant.JsonKey;
import cc.ranmc.game.card.server.Main;
import cc.ranmc.game.card.server.network.handler.BaseHandler;
import cc.ranmc.game.card.server.network.handler.ForgetHandler;
import cc.ranmc.game.card.server.network.handler.InfoHandler;
import cc.ranmc.game.card.server.network.handler.LoginHandler;
import cc.ranmc.game.card.server.network.handler.PreForgetHandler;
import cc.ranmc.game.card.server.network.handler.PreRegisterHandler;
import cc.ranmc.game.card.server.network.handler.RegisterHandler;
import io.javalin.Javalin;

import static cc.ranmc.game.card.common.constant.HttpPath.BASE_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.FORGET_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.INFO_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.LOGIN_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.PRE_FORGET_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.PRE_REGISTER_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.REGISTER_PATH;
import static cc.ranmc.game.card.server.util.ConfigUtil.CONFIG;

public class HttpServer {
    public static void start() {
        int port = CONFIG.getIntValue(JsonKey.HTTP_PORT, 2262);
        Javalin.create()
                .get(BASE_PATH, BaseHandler::handle)
                .post(PRE_REGISTER_PATH, PreRegisterHandler::handle)
                .post(REGISTER_PATH, RegisterHandler::handle)
                .post(PRE_FORGET_PATH, PreForgetHandler::handle)
                .post(FORGET_PATH, ForgetHandler::handle)
                .post(LOGIN_PATH, LoginHandler::handle)
                .post(INFO_PATH, InfoHandler::handle)
                .start(port);
        Main.getLogger().info("HTTP已成功运行在端口 {}", port);
    }
}
