package cc.ranmc.game.card.client.scene;

import cc.ranmc.game.card.client.Main;
import cc.ranmc.game.card.client.util.ApiUtil;
import cc.ranmc.game.card.client.util.HashUtil;
import cc.ranmc.game.card.client.util.HttpUtil;
import cc.ranmc.game.card.common.constant.BundleKey;
import cc.ranmc.game.card.common.constant.HttpResponse;
import cc.ranmc.game.card.common.constant.JsonKey;
import com.alibaba.fastjson2.JSONObject;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static cc.ranmc.game.card.common.constant.GameInfo.NAME;
import static cc.ranmc.game.card.common.constant.GameInfo.SAVE_FILE_NAME;
import static cc.ranmc.game.card.common.constant.HttpPath.FORGET_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.LOGIN_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.PRE_FORGET_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.PRE_REGISTER_PATH;
import static cc.ranmc.game.card.common.constant.HttpPath.REGISTER_PATH;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

public class LoginScene extends Scene {

    @Override
    public void onCreate() {
        getGameScene().setCursor(Cursor.DEFAULT);
        Texture background = FXGL.getAssetLoader().loadTexture("login.png");
        background.setFitWidth(960);
        background.setFitHeight(540);
        background.setTranslateX(0);
        background.setTranslateY(0);
        FXGL.getGameScene().addUINode(background);

        TextField playerNameField = new TextField();
        playerNameField.setPromptText("玩家名");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码");

        // 按钮
        Button loginButton = new Button("登录");
        Button registerButton = new Button("注册");
        Button forgotButton = new Button("忘记密码");

        Text helpText = new Text("急招美术！！");
        helpText.setFont(Font.font(40));

        String name = Main.getSave().get(NAME);
        if (name != null && !name.isEmpty()) playerNameField.setText(name);

        VBox box = new VBox(10, helpText, playerNameField, passwordField, loginButton, registerButton, forgotButton);
        box.setAlignment(Pos.CENTER);
        box.setTranslateX(380);
        box.setTranslateY(180);

        FXGL.getGameScene().addUINode(box);

        loginButton.setOnAction(_ -> {
            String playerName = playerNameField.getText();
            if (passwordField.getText() == null ||
                    passwordField.getText().isEmpty() ||
                    passwordField.getText().length() < 6) {
                getDialogService().showMessageBox("密码长度必须大于6位");
                return;
            }
            String password = HashUtil.sha1(passwordField.getText());
            JSONObject json = new JSONObject();
            json.put(JsonKey.NAME, playerName);
            json.put(JsonKey.PASSWORD, password);

            HttpUtil.post(ApiUtil.get(LOGIN_PATH), json.toString(), body -> {
                if (body.isEmpty()) {
                    getDialogService().showMessageBox("连接服务器失败");
                    return;
                }
                JSONObject bodyJson = JSONObject.parseObject(body);
                int code = bodyJson.getIntValue(JsonKey.CODE, 0);
                if (code == HttpResponse.SC_OK) {
                    Main.getSave().put(BundleKey.TOKEN, bodyJson.getString(JsonKey.TOKEN));
                    Main.getSave().put(BundleKey.NAME, playerName);
                    FXGL.getSaveLoadService().saveAndWriteTask(SAVE_FILE_NAME).run();
                    Main.changeScene(new MainMenuScene());
                } else {
                    getDialogService().showMessageBox(bodyJson.getString(JsonKey.MSG));
                }
            });
        });

        registerButton.setOnAction(_ -> {
            if (passwordField.getText() == null ||
                    passwordField.getText().isEmpty() ||
                    passwordField.getText().length() < 6) {
                getDialogService().showMessageBox("密码长度必须大于6位");
                return;
            }
            getDialogService().showInputBox("输入您的邮箱", email -> {
                String playerName = playerNameField.getText();
                String password = HashUtil.sha1(passwordField.getText());
                JSONObject json = new JSONObject();
                json.put(JsonKey.NAME, playerName);
                json.put(JsonKey.EMAIL, email);
                json.put(JsonKey.PASSWORD, password);
                HttpUtil.post(ApiUtil.get(PRE_REGISTER_PATH), json.toString(), body -> {
                    if (body.isEmpty()) {
                        getDialogService().showMessageBox("连接服务器失败");
                        return;
                    }
                    JSONObject bodyJson = JSONObject.parseObject(body);
                    int code = bodyJson.getIntValue(JsonKey.CODE, 0);
                    if (code != HttpResponse.SC_OK && code != HttpResponse.SC_UNAUTHORIZED) {
                        getDialogService().showMessageBox(bodyJson.getString(JsonKey.MSG));
                        return;
                    }
                    getDialogService().showInputBox("请检查邮箱并输入验证码", key -> {
                        JSONObject keyJson = new JSONObject();
                        keyJson.put(JsonKey.KEY, key);
                        HttpUtil.post(ApiUtil.get(REGISTER_PATH), keyJson.toString(), regRepBody -> {
                            if (regRepBody.isEmpty()) {
                                getDialogService().showMessageBox("连接服务器失败");
                                return;
                            }
                            JSONObject regRepJson = JSONObject.parseObject(regRepBody);
                            int regRepCode = regRepJson.getIntValue(JsonKey.CODE, 0);;
                            if (regRepCode != HttpResponse.SC_OK) {
                                getDialogService().showMessageBox(regRepJson.getString(JsonKey.MSG));
                                return;
                            }
                            Main.getSave().put(BundleKey.TOKEN, regRepJson.getString(JsonKey.TOKEN));
                            Main.getSave().put(BundleKey.NAME, playerName);
                            FXGL.getSaveLoadService().saveAndWriteTask(SAVE_FILE_NAME).run();
                            Main.changeScene(new MainMenuScene());
                        });
                    });
                });
            });
        });

        forgotButton.setOnAction(_ -> {
            if (passwordField.getText() == null ||
                    passwordField.getText().isEmpty() ||
                    passwordField.getText().length() < 6) {
                getDialogService().showMessageBox("密码长度必须大于6位");
                return;
            }
            getDialogService().showInputBox("输入您的邮箱", email -> {
                String playerName = playerNameField.getText();
                String password = HashUtil.sha1(passwordField.getText());
                JSONObject json = new JSONObject();
                json.put(JsonKey.NAME, playerName);
                json.put(JsonKey.EMAIL, email);
                json.put(JsonKey.PASSWORD, password);

                HttpUtil.post(ApiUtil.get(PRE_FORGET_PATH), json.toString(), body -> {
                    JSONObject bodyJson = JSONObject.parseObject(body);
                    int code = bodyJson.getIntValue(JsonKey.CODE, 0);
                    if (code != HttpResponse.SC_OK && code != HttpResponse.SC_UNAUTHORIZED) {
                        getDialogService().showMessageBox(bodyJson.getString(JsonKey.MSG));
                        return;
                    }
                    getDialogService().showInputBox("请检查邮箱并输入验证码", key -> {
                        JSONObject keyJson = new JSONObject();
                        keyJson.put(JsonKey.KEY, key);
                        HttpUtil.post(ApiUtil.get(FORGET_PATH), keyJson.toString(), regRepBody -> {
                            JSONObject regRepJson = JSONObject.parseObject(regRepBody);
                            int regRepCode = regRepJson.getIntValue(JsonKey.CODE, 0);;
                            if (regRepCode != HttpResponse.SC_OK) {
                                getDialogService().showMessageBox(regRepJson.getString(JsonKey.MSG));
                            }
                            Main.getSave().put(BundleKey.TOKEN, regRepJson.getString(JsonKey.TOKEN));
                            Main.getSave().put(BundleKey.NAME, playerName);
                            FXGL.getSaveLoadService().saveAndWriteTask(SAVE_FILE_NAME).run();
                            Main.changeScene(new MainMenuScene());
                        });
                    });
                });
            });
        });
    }
}
