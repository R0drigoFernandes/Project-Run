package entities;
import application.ProjectRun;
import java.awt.Graphics;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Consertar implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y, width, height;
    public boolean remove;
    public ArrayList<Consertar> consertarList = new ArrayList<>();
    public int spd = 2;

    public Consertar(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.remove = false;
    }

    public Consertar() {
        // Construtor padrão para a instância principal de Consertar em ProjectRun
        // que gerencia a lista consertarList.
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

        if (y > ProjectRun.height) {
            remove = true;
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.GREEN); // Cor do item de conserto
        g.fillRect(x, y, width, height);
    }
}