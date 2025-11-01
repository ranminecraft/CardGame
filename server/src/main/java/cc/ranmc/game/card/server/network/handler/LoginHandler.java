package cc.ranmc.game.card.server.network.handler;

import cc.ranmc.game.card.server.Main;
import cc.ranmc.game.card.common.constant.JsonKey;
import cc.ranmc.game.card.server.constant.SQLKey;
import cc.ranmc.game.card.server.sql.SQLFilter;
import cc.ranmc.game.card.server.sql.SQLRow;
import cc.ranmc.game.card.server.util.JwtTokenUtil;
import com.alibaba.fastjson2.JSONObject;
import io.javalin.http.Context;

import java.util.regex.Pattern;

import static cc.ranmc.game.card.common.constant.JsonKey.CODE;
import static cc.ranmc.game.card.common.constant.JsonKey.MSG;
import static cc.ranmc.game.card.common.constant.JsonKey.TOKEN;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

public class LoginHandler {
    public static void handle(Context context) {
        JSONObject json = new JSONObject();
        JSONObject parms = JSONObject.parseObject(context.body());
        if (!parms.containsKey(JsonKey.NAME) ||
                !Pattern.compile("^[a-zA-Z0-9一-龥]{2,8}$")
                        .matcher(parms.getString(JsonKey.NAME)).matches()) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "昵称由2~8位中英文或数字组成");
            context.result(json.toString());
            return;
        }

        if (!parms.containsKey(JsonKey.PASSWORD) ||
                !Pattern.compile("^[a-z0-9]{40}$")
                        .matcher(parms.getString(JsonKey.PASSWORD)).matches()) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "错误密码格式");
            context.result(json.toString());
            return;
        }

        SQLRow sqlRow = Main.getData().selectRow(SQLKey.PLAYER,
                new SQLFilter().where(SQLKey.NAME, parms.getString(JsonKey.NAME)));
        if (sqlRow.isEmpty()) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "玩家名未注册");
            context.result(json.toString());
            return;
        }

        if (!parms.getString(JsonKey.PASSWORD)
                .equals(sqlRow.getString(SQLKey.PASSWORD))) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "密码错误");
            context.result(json.toString());
            return;
        }

        try {
            Thread.sleep(1500);
        } catch (Exception ignored) {}

        Main.getData().update(SQLKey.PLAYER, new SQLFilter()
                .set(SQLKey.LAST_LOGIN, System.currentTimeMillis())
                .andSet(SQLKey.LAST_ADDRESS, context.ip())
                .where(SQLKey.NAME, parms.getString(JsonKey.NAME)));

        json.put(CODE, SC_OK);
        json.put(TOKEN, JwtTokenUtil.generatorToken(sqlRow.getString(SQLKey.EMAIL)));
        json.put(MSG, "成功");
        Main.getLogger().info("登录成功 玩家名：{}", sqlRow.getString(SQLKey.NAME));
        context.result(json.toString());
    }
}
