package cc.ranmc.game.card.client.util;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class LoadingUtil {

    private static StackPane loadingPane;

    public static void start() {
        loadingPane = new StackPane();
        Rectangle bg = new Rectangle(960, 540, Color.rgb(0, 0, 0, 0.5));
        Text text = new Text("加载中...");
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-size: 24px;");
        loadingPane.getChildren().addAll(bg, text);
        FXGL.getGameScene().addUINode(loadingPane);
    }

    public static void end() {
        FXGL.getGameScene().removeUINode(loadingPane);
    }

}
