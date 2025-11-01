package cc.ranmc.game.card.client.util;

import cc.ranmc.game.card.common.constant.GameInfo;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.function.Consumer;

import static cc.ranmc.game.card.common.constant.GameInfo.HEIGHT;
import static cc.ranmc.game.card.common.constant.GameInfo.WIDTH;

public class DialogUtil {

    public static void input(String message, Consumer<String> callback) {
        Platform.runLater(() -> {
            StackPane inputDialog = new StackPane();

            Rectangle bg = new Rectangle(WIDTH, HEIGHT, Color.rgb(0, 0, 0, 0.5));

            Text text = new Text(message);
            text.setFill(Color.WHITE);
            text.setFont(Font.font(GameInfo.FONT, FontWeight.BOLD, 24));
            text.maxWidth(WIDTH);

            TextField textField = new TextField();
            textField.setFont(Font.font(20));
            textField.setMaxWidth(200);

            Texture background = FXGL.getAssetLoader().loadTexture("dialog.png");

            Button closeButton = new Button("确定");
            closeButton.getStyleClass().add("small-button");
            closeButton.setOnAction(_ -> {
                FXGL.getGameScene().removeUINode(inputDialog);
                callback.accept(textField.getText());
                textField.setText("");
            });

            VBox vbox = new VBox(30, text, textField, closeButton);
            vbox.setAlignment(Pos.CENTER);

            inputDialog.getChildren().addAll(bg, background, vbox);
            if (FXGL.getGameScene().getUINodes().contains(inputDialog)) return;
            FXGL.getGameScene().addUINode(inputDialog);
        });
    }

    public static void show(String message) {
        Platform.runLater(() -> {
            StackPane dialogPane = new StackPane();

            Rectangle bg = new Rectangle(WIDTH, HEIGHT, Color.rgb(0, 0, 0, 0.5));

            Text text = new Text(message);
            text.setFill(Color.WHITE);
            text.setFont(Font.font(GameInfo.FONT, FontWeight.BOLD, 24));
            text.setWrappingWidth(WIDTH - 100);
            text.setTextAlignment(TextAlignment.CENTER);

            Texture background = FXGL.getAssetLoader().loadTexture("dialog.png");

            Button closeButton = new Button("确定");
            closeButton.getStyleClass().add("small-button");
            closeButton.setOnAction(_ -> {
                FXGL.getGameScene().removeUINode(dialogPane);
            });

            VBox vbox = new VBox(30, text, closeButton);
            vbox.setAlignment(Pos.CENTER);

            dialogPane.getChildren().addAll(bg, background, vbox);
            if (FXGL.getGameScene().getUINodes().contains(dialogPane)) return;
            FXGL.getGameScene().addUINode(dialogPane);
        });
    }
}
