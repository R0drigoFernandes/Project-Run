package application;

import java.io.Serializable;

public class GameData implements Serializable {
    private static final long serialVersionUID = 1L;

    // Dados do Player
    private int playerX;
    private int playerY;
    private int playerVida;

    // Dados do Jogo Global (anteriormente em 'Carros' ou dispersos)
    private int gamePoints;
    private int gameCarSpeed;
    private int carsPassedCount;        // Contador para spawn de itens e aceleração

    // Dados do FicarLentoItem
    private boolean slowEffectActive;
    private long slowEffectStartTime;

    // Dados do Invencible (para persistir o estado de invencibilidade)
    private boolean invencibleActive;
    private long invencibleStartTime;

    public GameData(int playerX, int playerY, int playerVida,
                    int gamePoints, int gameCarSpeed, int carsPassedCount,
                    boolean slowEffectActive, long slowEffectStartTime,
                    boolean invencibleActive, long invencibleStartTime) {
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerVida = playerVida;
        this.gamePoints = gamePoints;
        this.gameCarSpeed = gameCarSpeed;
        this.carsPassedCount = carsPassedCount;
        this.slowEffectActive = slowEffectActive;
        this.slowEffectStartTime = slowEffectStartTime;
        this.invencibleActive = invencibleActive;
        this.invencibleStartTime = invencibleStartTime;
    }

    // Getters para acessar os dados salvos
    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public int getPlayerVida() {
        return playerVida;
    }

    public int getGamePoints() {
        return gamePoints;
    }

    public int getGameCarSpeed() {
        return gameCarSpeed;
    }

    public int getCarsPassedCount() {
        return carsPassedCount;
    }

    public boolean isSlowEffectActive() {
        return slowEffectActive;
    }

    public long getSlowEffectStartTime() {
        return slowEffectStartTime;
    }

    public boolean isInvencibleActive() {
        return invencibleActive;
    }

    public long getInvencibleStartTime() {
        return invencibleStartTime;
    }
}
