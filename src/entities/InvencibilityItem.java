package entities;

import application.ProjectRun;
import java.awt.Graphics;
import java.awt.Color;
import java.io.Serializable;

public class InvencibilityItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y, width, height;
    public boolean remove; // Flag para indicar se o item deve ser removido da lista
    public int spd = 2; // Velocidade com que o item se move para baixo

    public InvencibilityItem(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 20; // Tamanho do item (ajuste conforme necessário)
        this.height = 20;
        this.remove = false;
    }

    // Verifica colisão entre o item de invencibilidade e o jogador
    public boolean verificaColisao(Player player) {
        if (this.x < player.x + player.width &&
            this.x + this.width > player.x &&
            this.y < player.y + player.height &&
            this.y + this.height > player.y) {
            return true;
        }
        return false;
    }

    // Atualiza a posição do item
    public void tick() {
        y += spd;
        // Se o item saiu da tela, marque para remoção
        if (y > ProjectRun.height) {
            remove = true;
        }
    }

    // Renderiza o item na tela
    public void render(Graphics g) {
        g.setColor(Color.MAGENTA); // Cor para o item de invencibilidade (ex: uma estrela roxa)
        g.fillOval(x, y, width, height); // Exemplo de forma (um círculo/oval)
    }
}