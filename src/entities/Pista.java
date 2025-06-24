package entities;

import java.awt.BasicStroke; // Importar BasicStroke
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D; // Importar Graphics2D
import java.awt.Stroke;     // Importar Stroke
import java.io.Serializable;

public class Pista implements Serializable {
    private static final long serialVersionUID = 1L;

    public int y = 0; // Posição Y da pista para rolagem
    private int speed = 3; // Velocidade de rolagem da pista (pode ser a mesma dos carros)

    public static final int LANE_WIDTH = 80; // Largura de cada faixa (ajuste conforme necessário)
    public static final int NUM_LANES = 3; // Número de faixas
    public static final int ROAD_WIDTH = LANE_WIDTH * NUM_LANES; // Largura total da pista
    // Largura da grama em cada lado, calculada dinamicamente
    public static final int GRASS_WIDTH = (application.ProjectRun.width - ROAD_WIDTH) / 2;
    // Limite X esquerdo da pista (onde a grama termina e a pista começa)
    public static final int ROAD_LEFT_X = GRASS_WIDTH;
    // Limite X direito da pista (onde a pista termina e a grama direita começa)
    public static final int ROAD_RIGHT_X = GRASS_WIDTH + ROAD_WIDTH;


    public void tick() {
        y += speed;
        if (y >= application.ProjectRun.height) {
            y = 0; // Reinicia a posição Y para simular rolagem contínua
        }
    }

    // NOVO: Setter para ajustar a velocidade da pista dinamicamente
    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }

    public void render(Graphics g) {
        // Desenha a grama lateral esquerda
        g.setColor(new Color(34, 139, 34)); // Verde floresta
        g.fillRect(0, 0, GRASS_WIDTH, application.ProjectRun.height);

        // Desenha a grama lateral direita
        g.fillRect(application.ProjectRun.width - GRASS_WIDTH, 0, GRASS_WIDTH, application.ProjectRun.height);

        // Desenha a pista
        g.setColor(Color.DARK_GRAY);
        g.fillRect(GRASS_WIDTH, 0, ROAD_WIDTH, application.ProjectRun.height);

        // Desenha as linhas tracejadas das faixas e o efeito de rolagem
        g.setColor(Color.YELLOW);
        Graphics2D g2d = (Graphics2D) g;
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0);
        g2d.setStroke(dashed);

        // As linhas centrais da pista
        for (int i = 0; i < NUM_LANES; i++) {
            // Ajusta o Y para dar o efeito de rolagem para as linhas da pista
            int lineYOffset = (int) ((y + i * (application.ProjectRun.height / NUM_LANES)) % (application.ProjectRun.height + 20));
            if (lineYOffset < -20) lineYOffset += application.ProjectRun.height + 20; // Lida com o wrap-around

            // Linhas tracejadas entre as faixas
            if (i > 0) { // Não desenha uma linha antes da primeira faixa
                g2d.drawLine(GRASS_WIDTH + (LANE_WIDTH * i), lineYOffset - application.ProjectRun.height, GRASS_WIDTH + (LANE_WIDTH * i), lineYOffset);
                g2d.drawLine(GRASS_WIDTH + (LANE_WIDTH * i), lineYOffset, GRASS_WIDTH + (LANE_WIDTH * i), lineYOffset + application.ProjectRun.height);
            }
        }
        g2d.setStroke(new BasicStroke(1)); // Volta ao stroke padrão
    }
}
