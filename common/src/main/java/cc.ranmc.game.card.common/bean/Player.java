package cc.ranmc.game.card.common.bean;

import lombok.Data;

@Data
public class Player {
    String playerName;
    int id;

    public Player(int id) {
        this.id = id;
    }
}
