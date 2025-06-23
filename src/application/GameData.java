package application;

import java.io.Serializable;

public class GameData implements Serializable {
    private static final long serialVersionUID = 1L;

    // Dados do Player
    private int playerX;
    private int playerY;
    private int playerVida;

    // Dados dos Carros
    private int carrosPontos;
    private int carrosAcelerar; // Velocidade dos carros
    private int carrosContador; // Contador interno dos carros (de Carros)

    // Dados do Consertar
    private int carsPassedCount; // Agora salva o contador de carros passados para controlar o Consertar

    // Dados do FicarLentoItem
    private boolean slowEffectActive;
    private long slowEffectStartTime;

    public GameData(int playerX, int playerY, int playerVida,
                    int carrosPontos, int carrosAcelerar, int carrosContador,
                    int carsPassedCount, boolean slowEffectActive, long slowEffectStartTime) { // Atualizado
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerVida = playerVida;
        this.carrosPontos = carrosPontos;
        this.carrosAcelerar = carrosAcelerar;
        this.carrosContador = carrosContador;
        this.carsPassedCount = carsPassedCount;
        this.slowEffectActive = slowEffectActive;
        this.slowEffectStartTime = slowEffectStartTime;
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

    public int getCarrosPontos() {
        return carrosPontos;
    }

    public int getCarrosAcelerar() {
        return carrosAcelerar;
    }

    public int getCarrosContador() {
        return carrosContador;
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
}