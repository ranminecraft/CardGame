package cc.ranmc.game.card.client.util;

import javafx.application.Platform;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.time.Duration;
import java.util.function.Consumer;

public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient();
    private static final Duration TIMEOUT = Duration.ofSeconds(8);

    public static void get(String url, Consumer<String> callback) {
        LoadingUtil.start();
        new Thread(() -> {
            OkHttpClient c = client.newBuilder().callTimeout(TIMEOUT).build();
            Request request = new Request.Builder().url(url).build();
            String result = "";
            try (Response response = c.newCall(request).execute()) {
                if (response.isSuccessful()) result = response.body().string();
            } catch (Exception ignored) {}
            String finalResult = result;
            Platform.runLater(() -> {
                callback.accept(finalResult);
                LoadingUtil.end();
            });
        }).start();
    }

    public static void post(String url, String body, Consumer<String> callback) {
        LoadingUtil.start();
        new Thread(() -> {
            OkHttpClient c = client.newBuilder().callTimeout(TIMEOUT).build();
            RequestBody requestBody = RequestBody.create(body,
                    MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("http://" + url)
                    .post(requestBody)
                    .build();
            String result = "";
            try (Response response = c.newCall(request).execute()) {
                if (response.isSuccessful()) result = response.body().string();
            } catch (Exception ignored) {}
            String finalResult = result;
            Platform.runLater(() -> {
                callback.accept(finalResult);
                LoadingUtil.end();
            });
        }).start();
    }

}
