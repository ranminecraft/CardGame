package cc.ranmc.game.card.client.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

public class PlayerComponent extends Component {

    @Getter
    private AnimatedTexture texture;

    private AnimationChannel downAnim;
    private AnimationChannel leftAnim;
    private AnimationChannel rightAnim;
    private AnimationChannel upAnim;

    private boolean moving = false;
    @Getter
    @Setter
    private int direction = 0; // 0=down,1=left,2=right,3=up

    private Double targetX;
    private Double targetY;

    private String pic;

    public PlayerComponent(String pic) {
        this.pic = pic;
    }

    @Override
    public void onAdded() {

        // 每行 4 帧，每帧宽32 高48
        downAnim = new AnimationChannel(FXGL.image(pic), 4, 32, 48, Duration.seconds(0.5), 0, 3);
        leftAnim = new AnimationChannel(FXGL.image(pic), 4, 32, 48, Duration.seconds(0.5), 4, 7);
        rightAnim = new AnimationChannel(FXGL.image(pic), 4, 32, 48, Duration.seconds(0.5), 8, 11);
        upAnim = new AnimationChannel(FXGL.image(pic), 4, 32, 48, Duration.seconds(0.5), 12, 15);

        // 默认朝下静止
        texture = new AnimatedTexture(downAnim);
        entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(downAnim);
    }

    @Override
    public void onUpdate(double tpf) {
        if (moving) {

            AnimationChannel currentAnim = switch (direction) {
                case 1 -> leftAnim;
                case 2 -> rightAnim;
                case 3 -> upAnim;
                default -> downAnim;
            };

            if (texture.getAnimationChannel() != currentAnim) {
                texture.loopAnimationChannel(currentAnim);
            }

            if (targetX != null && targetY != null) {
                double dx = targetX - entity.getX();
                double dy = targetY - entity.getY();
                double distance = Math.sqrt(dx * dx + dy*dy);

                if (distance < 1 || distance > 100) {
                    entity.setX(targetX);
                    entity.setY(targetY);
                    moving = false;
                } else {
                    entity.translateX(dx / distance * 300 * tpf);
                    entity.translateY(dy / distance * 300 * tpf);
                }
            }
        } else {
            // 停止时显示当前方向第一帧（静止）
            switch (direction) {
                case 0 -> texture.loopAnimationChannel(downAnim);
                case 1 -> texture.loopAnimationChannel(leftAnim);
                case 2 -> texture.loopAnimationChannel(rightAnim);
                case 3 -> texture.loopAnimationChannel(upAnim);
            }
        }
    }

    public void setDestination(double x, double y) {
        this.targetX = x;
        this.targetY = y;
        this.moving = true;
    }

    public void moveDown() {
        moving = true;
        direction = 0;
    }

    public void moveLeft() {
        moving = true;
        direction = 1;
    }

    public void moveRight() {
        moving = true;
        direction = 2;
    }

    public void moveUp() {
        moving = true;
        direction = 3;
    }

    public void stop() {
        moving = false;
    }
}