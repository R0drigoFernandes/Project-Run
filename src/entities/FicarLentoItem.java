package entities;
import java.awt.Color;
import java.awt.Graphics;
import application.ProjectRun;
import java.io.Serializable;

public class FicarLentoItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public boolean slow; // Mudado para public para visibilidade
    public boolean remove; // Mudado para public para visibilidade
    public int x, y, width, height;
    public int spd = 2; // Velocidade com que o item se move para baixo

    public FicarLentoItem(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 20; // Tamanho do item (ajuste conforme necessário)
        this.height = 20;
        this.slow = false;
        this.remove = false; // Inicialmente não está ativo
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
            slow = true; // Ativa o efeito de ficar lento
            return true;
        }
        return false;
    }

    public boolean isSlow() {
        return slow;
    }

    public void render(Graphics g) {
        g.setColor(Color.BLUE); // Cor do item FicarLento (exemplo, você pode mudar)
        g.fillRect(x, y, width, height);
    }
}