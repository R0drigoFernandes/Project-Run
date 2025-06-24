package entities;
import application.ProjectRun;
import java.awt.Graphics;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.awt.Font;

public class Consertar implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y, width, height;
    public boolean remove;
    public ArrayList<Consertar> consertarList; // Esta lista pertence à instância "gerente"

    public int spd = 2; // Velocidade com que o item se move para baixo

    // Construtor para ITENS INDIVIDUAIS
    public Consertar(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.remove = false;
    }

    // Construtor para a INSTÂNCIA GERENCIADORA (se ProjectRun.consertarManager for ela)
    public Consertar() {
        this.consertarList = new ArrayList<>(); // Inicializa a lista aqui
    }

    public boolean verificaColisao(Player player) {
        if (this.x < player.x + player.width &&
            this.x + this.width > player.x &&
            this.y < player.y + player.height &&
            this.y + this.height > player.y) {
            return true;
        }
        return false;
    }

    public void tick() {
        y += spd;
        // Se o item saiu da tela, marque para remoção
        if (y > ProjectRun.height) {
            remove = true;
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.GREEN); // Cor do item de consertar (coração)
        g.fillRect(x, y, width, height);
        g.setFont(new Font("Negrito", Font.BOLD, 12)); // Ajusta o tamanho da fonte
        g.setColor(Color.WHITE); // Cor do texto
        int textWidth = g.getFontMetrics().stringWidth("Consertar!");
        g.drawString("Consertar!", x + (width / 2) - (textWidth / 2), y + height + 20);
        // Opcional: Desenhar um "coração" ou outra imagem
    }
}
