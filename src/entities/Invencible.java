package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.awt.Font;
public class Invencible implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean active;
    private long startTime;
    private long duration = 7000; // Duração da invencibilidade em milissegundos (7 segundos)

    public Invencible() {
        this.active = false;
        this.startTime = 0;
    }

    public void activateWithTime(long savedStartTime) {
        this.active = true;
        this.startTime = savedStartTime;
    }

    public boolean isActive() {
        return active;
    }

    public long getStartTime() {
        return startTime;
    }

    public void activate() {
        this.active = true;
        this.startTime = System.currentTimeMillis();
    }

    public void deactivate() {
        this.active = false;
        this.startTime = 0;
    }

    public void tick() {
        if (active) {
            if (System.currentTimeMillis() - startTime >= duration) {
                active = false;
                System.out.println("Invencibilidade desativada.");
            }
        }
    }

    // MÉTODO RENDER: Adiciona parâmetros para posição e tamanho do player
    public void render(Graphics g, int playerX, int playerY, int playerWidth, int playerHeight) {
        if (active) {
            // Efeito visual de invencibilidade ao redor do player
            g.setColor(Color.YELLOW); // Amarelo transparente (RGBA)

            // Desenha um círculo maior que o player para simular um "escudo"
            int padding = 5; // Adiciona um pequeno espaço ao redor do player
            g.fillOval(playerX - padding, playerY - padding, playerWidth + (padding * 2), playerHeight + (padding * 2));
            g.setFont(new Font("Negrito", Font.BOLD, 12)); // Ajusta o tamanho da fonte
            g.setColor(Color.WHITE); // Cor do texto
            int textWidth = g.getFontMetrics().stringWidth("Invencível!");
            g.drawString("Invencível!", playerX + (playerWidth / 2) - (textWidth / 2), playerY + playerHeight + 20);
        }
    }
}
