package cc.ranmc.game.card.client.util;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static cc.ranmc.game.card.common.constant.GameInfo.HEIGHT;
import static cc.ranmc.game.card.common.constant.GameInfo.WIDTH;

public class LoadingUtil {

    private static StackPane loadingPane;

    public static void start() {
        if (loadingPane == null) {
            loadingPane = new StackPane();
            Rectangle bg = new Rectangle(WIDTH, HEIGHT, Color.rgb(0, 0, 0, 0.5));
            Text text = new Text("加载中...");
            text.setFill(Color.WHITE);
            text.setFont(Font.font(24));
            loadingPane.getChildren().addAll(bg, text);
        }
        FXGL.runOnce(() -> FXGL.getGameScene().addUINode(loadingPane), Duration.millis(1));

    }

    public static void end() {
        FXGL.runOnce(() -> FXGL.getGameScene().removeUINode(loadingPane), Duration.millis(1));
    }

}
