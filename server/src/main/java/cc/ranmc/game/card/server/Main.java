package cc.ranmc.game.card.server;

import cc.ranmc.game.card.server.constant.JsonKey;
import cc.ranmc.game.card.server.network.GameServer;
import cc.ranmc.game.card.server.network.HttpServer;
import cc.ranmc.game.card.server.sql.DataSQL;
import cc.ranmc.game.card.server.util.ConfigUtil;
import io.github.biezhi.ome.OhMyEmail;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static cc.ranmc.game.card.common.constant.GameInfo.NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.AUTHOR;
import static cc.ranmc.game.card.common.constant.GameInfo.VERSION;
import static cc.ranmc.game.card.server.util.ConfigUtil.CONFIG;
import static io.github.biezhi.ome.OhMyEmail.defaultConfig;

public class Main {

    @Getter
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    @Getter
    private static DataSQL data = new DataSQL(System.getProperty("user.dir") + "/data.db");

    static void main() {
        System.out.println("-----------------------");
        System.out.println(NAME + " By " + AUTHOR);
        System.out.println("Version: " + VERSION);
        System.out.println("-----------------------");

        ConfigUtil.load();

        // 初始化邮件
        Properties props = defaultConfig(false);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.qcloudmail.com");
        props.put("mail.smtp.port", "465");
        OhMyEmail.config(props, "bot@ranmc.cc", CONFIG.getString(JsonKey.EMAIL));

        GameServer.start();
        HttpServer.start();
    }
}
