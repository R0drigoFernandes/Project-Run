package entities;
import application.ProjectRun;
import java.awt.Graphics;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Consertar implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y, width, height; // Tornando x, y públicos para fácil acesso, se necessário, ou mantenha privados com getters
    public boolean remove;
    public ArrayList<Consertar> consertarList = new ArrayList<>(); // Renomeado para evitar conflito com a classe
    public int spd = 2;

    // Removemos 'public static int count = 0;' daqui e a lógica do construtor
    // para que a criação seja gerenciada externamente, por exemplo, na classe Carros.

    public Consertar(int x, int y) { // Construtor com posição inicial
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.remove = false; // Começa como não removido
    }

    // Construtor padrão (pode ser usado se o item for adicionado em outro lugar)
    public Consertar() {
        // Este construtor pode ser usado se você for gerar as posições aleatoriamente em outro lugar
        // ou se você quiser que cada instância de Consertar seja única e não gerencie uma lista global.
        // Para a lógica atual, é melhor usar o construtor com x,y.
        // Se você ainda quiser que Consertar.count controle a adição inicial, você precisaria de uma lógica mais robusta.
        this.width = 16;
        this.height = 16;
        this.remove = false;
        this.x = (int) (Math.random() * (310 - 150 + 1) + 150); // Posição aleatória na pista
        this.y = -10; // Começa acima da tela
    }


    public boolean verificaColisao(Player player) {
        // Simplifica a verificação de colisão.
        // A condição 'count % 3 == 0' não deve estar aqui, pois ela é para a _criação_ do objeto,
        // não para a _interação_ com ele uma vez que ele existe.
        if (this.x < player.x + player.width &&
            this.x + this.width > player.x &&
            this.y < player.y + player.height &&
            this.y + this.height > player.y) {
            return true;
        }
        return false;
    }

    public void tick() {
        y += spd; // Move o item para baixo

        // A lógica de remoção e recriação deve ser gerenciada onde os itens de conserto são controlados
        // (provavelmente em ProjectRun ou Carros, onde eles são adicionados à lista de consertar).
        // Se este Consertar é um item individual em uma lista, ele não deve "recriar" outro Consertar.
        // Ele deve apenas marcar a si mesmo para remoção.

        // Se o item saiu da tela, marque para remoção
        if (y > ProjectRun.height) { // Use ProjectRun.height para o limite da tela
            remove = true;
        }
    }

    public void render(Graphics g) {
        // Renderiza apenas se não estiver marcado para remoção (se você quiser que ele desapareça imediatamente ao ser pego)
        // Ou, se você quiser um efeito visual de coleta antes de desaparecer, a lógica de renderização pode ser diferente.
        g.setColor(Color.CYAN); // Cor para o item de consertar (ex: kit de ferramentas)
        g.fillOval(x, y, width, height);
    }
}