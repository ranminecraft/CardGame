package cc.ranmc.game.card.server.network.handler;

import cc.ranmc.game.card.server.Main;
import cc.ranmc.game.card.common.constant.JsonKey;
import cc.ranmc.game.card.server.constant.SQLKey;
import cc.ranmc.game.card.server.sql.SQLRow;
import cc.ranmc.game.card.server.util.JwtTokenUtil;
import com.alibaba.fastjson2.JSONObject;
import io.javalin.http.ContentType;
import io.javalin.http.Context;

import static cc.ranmc.game.card.common.constant.JsonKey.CODE;
import static cc.ranmc.game.card.common.constant.JsonKey.MSG;
import static cc.ranmc.game.card.common.constant.JsonKey.TOKEN;
import static cc.ranmc.game.card.server.network.handler.PreRegisterHandler.PRE_REGISTER_MAP;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED;

public class RegisterHandler {

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
        if (PRE_REGISTER_MAP.containsKey(key)) {
            JSONObject playerParms = PRE_REGISTER_MAP.get(key);
            SQLRow sqlRow = new SQLRow();
            sqlRow.set(SQLKey.NAME, playerParms.getString(JsonKey.NAME));
            sqlRow.set(SQLKey.PASSWORD, playerParms.getString(JsonKey.PASSWORD));
            sqlRow.set(SQLKey.MONEY, 0);
            sqlRow.set(SQLKey.EMAIL, playerParms.getString(JsonKey.EMAIL));
            sqlRow.set(SQLKey.REG_ADDRESS, playerParms.getString(JsonKey.REG_ADDRESS));
            sqlRow.set(SQLKey.LAST_ADDRESS, playerParms.getString(JsonKey.REG_ADDRESS));
            sqlRow.set(SQLKey.LAST_LOGIN, System.currentTimeMillis());
            Main.getData().insert(SQLKey.PLAYER, sqlRow);
            PRE_REGISTER_MAP.remove(key);
            json.put(CODE, SC_OK);
            json.put(TOKEN, JwtTokenUtil.generatorToken(playerParms.getString(JsonKey.EMAIL)));
            json.put(MSG, "成功");
            Main.getLogger().info("注册成功 玩家名：{}", playerParms.get(JsonKey.NAME));
        } else {
            json.put(CODE, SC_PAYMENT_REQUIRED);
            json.put(MSG, "验证过期或不存在");
        }
        context.result(json.toString());
    }
}
