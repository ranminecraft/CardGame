package cc.ranmc.game.card.server.network.handler;

import cc.ranmc.game.card.server.Main;
import cc.ranmc.game.card.server.constant.JsonKey;
import cc.ranmc.game.card.server.constant.SQLKey;
import cc.ranmc.game.card.server.sql.SQLFilter;
import cc.ranmc.game.card.server.util.JwtTokenUtil;
import com.alibaba.fastjson2.JSONObject;
import io.javalin.http.ContentType;
import io.javalin.http.Context;

import static cc.ranmc.game.card.server.constant.JsonKey.CODE;
import static cc.ranmc.game.card.server.constant.JsonKey.MSG;
import static cc.ranmc.game.card.server.constant.JsonKey.TOKEN;
import static cc.ranmc.game.card.server.network.handler.PreForgetHandler.PRE_FORGET_MAP;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED;

public class ForgetHandler {
    public static void handle(Context context) {
        context.contentType(ContentType.APPLICATION_JSON);
        JSONObject json = new JSONObject();
        JSONObject parms = JSONObject.parseObject(context.body());

        if (!parms.containsKey(JsonKey.KEY)) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "参数错误");
            context.result(json.toString());
            return;
        }
        String key = parms.getString(JsonKey.KEY);
        if (PRE_FORGET_MAP.containsKey(key)) {
            JSONObject playerParms = PRE_FORGET_MAP.get(key);
            Main.getData().update(SQLKey.PLAYER, new SQLFilter()
                            .set(SQLKey.PASSWORD, playerParms.getString(JsonKey.PASSWORD))
                            .andSet(SQLKey.LAST_LOGIN, System.currentTimeMillis())
                            .where(SQLKey.NAME, playerParms.getString(JsonKey.NAME)));
            PRE_FORGET_MAP.remove(key);
            json.put(CODE, SC_OK);
            json.put(TOKEN, JwtTokenUtil.generatorToken(playerParms.getString(JsonKey.EMAIL)));
            json.put(MSG, "成功");
            Main.getLogger().info("重置密码 玩家名：{}", playerParms.get(JsonKey.NAME));
        } else {
            json.put(CODE, SC_PAYMENT_REQUIRED);
            json.put(MSG, "验证过期或不存在");
        }
        context.result(json.toString());
    }
}
