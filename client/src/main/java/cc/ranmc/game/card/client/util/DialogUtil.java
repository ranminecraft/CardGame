package cc.ranmc.game.card.client.util;

import cc.ranmc.game.card.common.constant.GameInfo;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static cc.ranmc.game.card.common.constant.GameInfo.HEIGHT;
import static cc.ranmc.game.card.common.constant.GameInfo.WIDTH;

public class DialogUtil {

    private static StackPane dialogPane;
    private static Text text;
    private static boolean hidden = true;

    public static void changeScene() {
        if (hidden || FXGL.getGameScene().getUINodes().contains(dialogPane)) return;
        FXGL.getGameScene().addUINode(dialogPane);
    }

    public static void show(String message) {
        Platform.runLater(() -> {
            if (dialogPane == null) {
                dialogPane = new StackPane();

                Rectangle bg = new Rectangle(WIDTH, HEIGHT, Color.rgb(0, 0, 0, 0.5));

                text = new Text();
                text.setFill(Color.WHITE);
                text.setFont(Font.font(GameInfo.FONT, FontWeight.BOLD, 24));

                Texture background = FXGL.getAssetLoader().loadTexture("dialog.png");

                Button closeButton = new Button("确定");
                closeButton.getStyleClass().add("small-button");
                closeButton.setOnAction(_ -> {
                    hidden = true;
                    FXGL.getGameScene().removeUINode(dialogPane);
                });

                VBox vbox = new VBox(30, text, closeButton);
                vbox.setAlignment(Pos.CENTER);

                dialogPane.getChildren().addAll(bg, background, vbox);
            }
            text.setText(message);
            if (FXGL.getGameScene().getUINodes().contains(dialogPane)) return;
            FXGL.getGameScene().addUINode(dialogPane);
            hidden = false;
        });
    }
}
