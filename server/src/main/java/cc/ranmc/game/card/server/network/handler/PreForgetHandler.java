package cc.ranmc.game.card.server.network.handler;

import cc.ranmc.game.card.common.constant.GameInfo;
import cc.ranmc.game.card.server.Main;
import cc.ranmc.game.card.server.constant.EmailContext;
import cc.ranmc.game.card.common.constant.JsonKey;
import cc.ranmc.game.card.server.constant.SQLKey;
import cc.ranmc.game.card.server.sql.SQLFilter;
import cc.ranmc.game.card.server.sql.SQLRow;
import cc.ranmc.game.card.server.util.KeyGenerator;
import com.alibaba.fastjson2.JSONObject;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;
import io.javalin.http.ContentType;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static cc.ranmc.game.card.common.constant.JsonKey.CODE;
import static cc.ranmc.game.card.common.constant.JsonKey.MSG;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class PreForgetHandler {
    public static final Map<String, JSONObject> PRE_FORGET_MAP = new HashMap<>();

    public static void handle(Context context) {
        context.contentType(ContentType.APPLICATION_JSON);
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

        if (!parms.containsKey(JsonKey.EMAIL) ||
                !Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                        .matcher(parms.getString(JsonKey.EMAIL)).matches()) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "错误邮箱格式");
            context.result(json.toString());
            return;
        }
        parms.put(JsonKey.EMAIL, parms.getString(JsonKey.EMAIL).toLowerCase());

        if (!parms.containsKey(JsonKey.PASSWORD) ||
                !Pattern.compile("^[a-z0-9]{40}$")
                        .matcher(parms.getString(JsonKey.PASSWORD)).matches()) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "错误密码格式");
            context.result(json.toString());
            return;
        }

        SQLRow sqlRow = Main.getData().selectRow(SQLKey.PLAYER,
                new SQLFilter().where(SQLKey.EMAIL, parms.getString(JsonKey.EMAIL)));
        if (sqlRow.isEmpty()) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "邮箱未被注册过");
            context.result(json.toString());
            return;
        }

        if (!sqlRow.getString(SQLKey.NAME).equals(parms.getString(JsonKey.NAME))) {
            json.put(CODE, SC_BAD_REQUEST);
            json.put(MSG, "玩家名与邮箱不匹配");
            context.result(json.toString());
            return;
        }

        for (String key : PRE_FORGET_MAP.keySet()) {
            if (PRE_FORGET_MAP.get(key).getString(JsonKey.EMAIL)
                    .equals(parms.getString(JsonKey.EMAIL))) {
                json.put(CODE, SC_UNAUTHORIZED);
                json.put(MSG, "等待验证中，请检查邮件");
                context.result(json.toString());
                return;
            }
        }

        preForget(parms);
        json.put(CODE, SC_OK);
        json.put(MSG, "成功");
        context.result(json.toString());
    }

    private static void preForget(JSONObject parms) {
        String key = KeyGenerator.get();
        // 防止生成 key 重复
        if (PRE_FORGET_MAP.containsKey(key)) {
            preForget(parms);
            return;
        }
        PRE_FORGET_MAP.put(key, parms);
        Main.getLogger().info("预忘记密码 玩家名：{} 验证码：{}", parms.getString(JsonKey.NAME), key);
        try {
            OhMyEmail.subject("重置密码")
                    .from(GameInfo.NAME)
                    .to(parms.getString(JsonKey.EMAIL))
                    .html(EmailContext.VERIFY
                            .replace("%name%", parms.getString(JsonKey.NAME))
                            .replace("%key%", key))
                    .send();
            Main.getLogger().info("发送邮件成功：{}", parms.getString(JsonKey.EMAIL));
        } catch (SendMailException e) {
            Main.getLogger().info("发送邮件失败：{}", e.getMessage());
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                PRE_FORGET_MAP.remove(key);
            }
        }, 1000 * 60 * 10);
    }
}
