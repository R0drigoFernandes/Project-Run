package entities;
import java.awt.Graphics;
import java.awt.Color;
public class Pista {
    // Adicionar uma variável para o offset das linhas da pista
    private int lineOffsetY = 0;
    private final int LINE_SPEED = 3; // Velocidade com que as linhas se movem

    public void tick() {
        lineOffsetY += LINE_SPEED;
        if (lineOffsetY >= 50) { // Se o offset for maior ou igual ao espaçamento entre as linhas
            lineOffsetY = 0; // Reinicia o offset
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(0,0,500,500);
        g.setColor(Color.gray);
        g.fillRect(150,0,200,500); // Faixa da pista

        // Desenha as linhas da pista com o offset
        for(int i = -50 + lineOffsetY; i < 500; i += 50) { // Começa um pouco acima da tela
            g.setColor(Color.yellow);
            g.fillRect(250,i,10,20);
        }
    }
}