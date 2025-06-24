package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList; // Ainda necessário para o construtor gerenciador

public class Carros implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y, width, height; // Coordenadas e dimensões de UM carro individual
    public boolean remove; // Usado para marcar o carro para remoção/reaparecimento

    // Esta lista pertence APENAS à instância "gerenciadora" de Carros,
    // que é a 'carrosManager' em ProjectRun.
    public ArrayList<Carros> activeCars;

    // Construtor para ITENS INDIVIDUAIS (cada carro na tela)
    public Carros(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.remove = false; // Inicializa como não removível
    }

    // Construtor para a INSTÂNCIA GERENCIADORA (o que 'ProjectRun.carrosManager' é)
    public Carros() {
        this.activeCars = new ArrayList<>(); // Inicializa a lista de carros ativos aqui
        // Atributos de jogo globais (pontos, acelerar, contador, slow) foram movidos para ProjectRun
    }

    // Método verificaColisao para verificar colisão de UM carro individual com o Player
    public boolean verificaColisao(Player player) {
        if (this.x < player.x + player.width &&
            this.x + this.width > player.x &&
            this.y < player.y + player.height &&
            this.y + this.height > player.y) {
            return true;
        }
        return false;
    }

    // MÉTODO RENDER PARA ITENS INDIVIDUAIS
    public void render(Graphics g) {
        g.setColor(Color.RED); // Cor dos carros
        g.fillRect(x, y, width, height);
        // Opcional: Desenhar um contorno ou imagem para o carro
    }

    // MÉTODO TICK PARA ITENS INDIVIDUAIS, agora recebe a velocidade do jogo
    public void tick(int gameSpeed) {
        this.y += gameSpeed; // Carros se movem para baixo com a velocidade do jogo
    }
}
