package cc.ranmc.game.card.server.network.handler;

import io.javalin.http.ContentType;
import io.javalin.http.Context;

public class BaseHandler {
    public static void handle(Context context) {
        context.contentType(ContentType.TEXT_HTML);
        context.result("<html><head><meta http-equiv=\"refresh\" content=\"0;url=https://www.ranmc.cc\"></head><body></body></html>");
    }
}
