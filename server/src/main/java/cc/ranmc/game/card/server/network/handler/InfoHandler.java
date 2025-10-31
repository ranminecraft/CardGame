package cc.ranmc.game.card.server.network.handler;

import cc.ranmc.game.card.server.Main;
import cc.ranmc.game.card.common.constant.JsonKey;
import cc.ranmc.game.card.server.constant.SQLKey;
import cc.ranmc.game.card.server.sql.SQLFilter;
import cc.ranmc.game.card.server.sql.SQLRow;
import cc.ranmc.game.card.server.util.JwtTokenUtil;
import com.alibaba.fastjson2.JSONObject;
import io.javalin.http.Context;

import static cc.ranmc.game.card.common.constant.JsonKey.CODE;
import static cc.ranmc.game.card.common.constant.JsonKey.MSG;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class InfoHandler {
    public static void handle(Context context) {
        JSONObject json = new JSONObject();
        JSONObject parms = JSONObject.parseObject(context.body());
        if (!parms.containsKey(JsonKey.TOKEN)) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "参数错误");
            context.result(json.toString());
            return;
        }
        if (!JwtTokenUtil.validate(parms.getString(JsonKey.TOKEN))) {
            json.put(JsonKey.CODE, SC_UNAUTHORIZED);
            json.put(JsonKey.MSG, "未登录");
            context.result(json.toString());
            return;
        }
        String email;
        try {
            email = JwtTokenUtil.getEmail(parms.getString(JsonKey.TOKEN));
        } catch (Exception e) {
            json.put(JsonKey.CODE, SC_UNAUTHORIZED);
            json.put(JsonKey.MSG, "未登录");
            context.result(json.toString());
            return;
        }
        SQLRow sqlRow = Main.getData().selectRow(SQLKey.PLAYER, new SQLFilter().where(SQLKey.EMAIL, email));

        json.put(CODE, SC_OK);
        json.put(JsonKey.MONEY, sqlRow.getInt(SQLKey.MONEY));
        json.put(MSG, "成功");
        context.result(json.toString());
    }
}
