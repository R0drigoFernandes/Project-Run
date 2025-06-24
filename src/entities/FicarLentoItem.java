package entities;
import java.awt.Color;
import java.awt.Graphics;
import application.ProjectRun;
import java.io.Serializable;
import java.awt.Font;

public class FicarLentoItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public boolean slow; // Este atributo 'slow' aqui não é o mesmo de Carros, é para este item.
    public boolean remove;
    public int x, y, width, height;
    public int spd = 2; // Velocidade com que o item se move para baixo

    public FicarLentoItem(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 20; // Tamanho do item (ajuste conforme necessário)
        this.height = 20;
        this.slow = false; // Estado inicial do efeito, ativado na colisão
        this.remove = false; // Estado inicial para remoção
    }

    public void tick() {
        y += spd;
        // Se o item saiu da tela, marque para remoção
        if (y > ProjectRun.height) {
            remove = true;
        }
    }

    public boolean verificaColisao(Player player) {
        if (this.x < player.x + player.width &&
            this.x + this.width > player.x &&
            this.y < player.y + player.height &&
            this.y + this.height > player.y) {
            slow = true; // Ativa o efeito de ficar lento (no item)
            return true;
        }
        return false;
    }

    public void render(Graphics g) {
        g.setColor(Color.RED); // Azul escuro para o item de lentidão
        g.fillOval(x, y, width, height); // Desenha um círculo
        g.setFont(new Font("Negrito", Font.BOLD, 12)); // Ajusta o tamanho da fonte
        g.setColor(Color.WHITE); // Cor do texto
        int textWidth = g.getFontMetrics().stringWidth("Lentidão!");
        g.drawString("Lentidão!", x + (width / 2) - (textWidth / 2), y + height + 20);
        // Opcional: Desenhar um ícone de lentidão
    }
}
