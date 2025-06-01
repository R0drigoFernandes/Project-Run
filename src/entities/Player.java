package entities;

import java.awt.*;
import application.ProjectRun;


public class Player {

    public Carros carros;
    public Consertar consertar;
    public Invencible invencible; // Adicione a referência para Invencible
    public int x, y, width, height, vida, spd = 4;
    public boolean right, left, acelerar, freiar;
    private ProjectRun gameRef;

    // Atualize o construtor para receber a instância de Invencible
    public Player(int x, int y, int vida, int width, int height, Carros carros, Consertar consertar, Invencible invencible) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.width = width;
        this.height = height;
        this.carros = carros;
        this.consertar = consertar;
        this.invencible = invencible; // Inicialize a instância de Invencible
    }

    public void setGameReference(ProjectRun game) {
        this.gameRef = game;
    }

    public void perderVida() {
        // Verifica se o jogador NÃO está invencível antes de perder vida
        if (!invencible.isActive()) {
            vida--;
            if (vida <= 0) {
                if (gameRef != null) {
                    gameRef.GameOver();
                } else {
                    System.err.println("Erro: Referência do jogo não definida no Player. Não é possível chamar GameOver.");
                }
            }
        }
    }

    public void tick() {
        invencible.tick(); // Atualiza o estado da invencibilidade a cada tick

        // Colisão com itens de conserto
        for (int i = 0; i < gameRef.consertar.consertarList.size(); i++) {
            Consertar itemConsertar = gameRef.consertar.consertarList.get(i);
            if (itemConsertar.verificaColisao(this)) {
                if (vida < 3) {
                    vida = 3;
                }
                itemConsertar.remove = true;
                break;
            }
        }

        // Colisão com itens de invencibilidade (NOVO)
        // Você precisará de uma lista de InvencibilityItem em ProjectRun
        for (int i = 0; i < gameRef.invencibilityItems.size(); i++) {
            InvencibilityItem invItem = gameRef.invencibilityItems.get(i);
            if (invItem.verificaColisao(this)) {
                invencible.activate(); // Ativa a invencibilidade ao colidir
                invItem.remove = true; // Marca o item para remoção
                break;
            }
        }

        // Colisão com carros
        boolean colisaoCarro = false;
        for (int i = 0; i < gameRef.carros.activeCars.size(); i++) {
            Carros carro = gameRef.carros.activeCars.get(i);
            if (carro.verificaColisao(this)) {
                colisaoCarro = true;
                carro.remove = true;
                break;
            }
        }

        if (colisaoCarro) {
            perderVida(); // Chama perderVida, que agora verifica a invencibilidade
        }

        // Movimento do jogador
        if (right) {
            if (x < ProjectRun.width - width - 150) {
                x += spd;
            }
        }
        if (left) {
            if (x > 150) {
                x -= spd;
            }
        }
        if (acelerar) {
            if (y > 0) {
                y -= spd;
            }
        }
        if (freiar) {
            if (y < ProjectRun.height - height) {
                y += spd;
            }
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Vida: " + vida, 10, 20);
        invencible.render(g); // Renderiza o efeito visual da invencibilidade
    }
}