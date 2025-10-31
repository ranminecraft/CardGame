package cc.ranmc.game.card.client.util;

import cc.ranmc.game.card.common.constant.GameInfo;

public class ApiUtil {

    public static String get(String path) {
        if (!path.startsWith("/")) path = "/" + path;
        return GameInfo.ADDRESS + ":" + GameInfo.HTTP_PORT  + path;
    }

}
