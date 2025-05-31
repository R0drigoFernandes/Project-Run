package entities;

import java.awt.*;
import application.ProjectRun;

public class Player {

    public Carros carros; // Esta referência será para a instância que gerencia a lista de carros
    public Consertar consertar; // Esta referência será para a instância que gerencia a lista de consertos
    public int x, y, width, height, vida, spd = 4;
    public boolean right, left, acelerar, freiar;

    private ProjectRun gameRef;

    public Player(int x, int y, int vida, int width, int height, Carros carros, Consertar consertar) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.width = width;
        this.height = height;
        this.carros = carros;
        this.consertar = consertar;
    }

    public void setGameReference(ProjectRun game) {
        this.gameRef = game;
    }

    public void perderVida() {
        vida--;
        if (vida <= 0) {
            if (gameRef != null) {
                gameRef.GameOver();
            } else {
                System.err.println("Erro: Referência do jogo não definida no Player. Não é possível chamar GameOver.");
            }
        }
    }

    public void tick() {
        // Colisão com itens de conserto
        // Itera sobre a lista de consertar no gameRef
        for (int i = 0; i < gameRef.consertar.consertarList.size(); i++) {
            Consertar itemConsertar = gameRef.consertar.consertarList.get(i);
            if (itemConsertar.verificaColisao(this)) {
                if (vida < 3) {
                    vida = 3; // Restaura a vida
                    itemConsertar.remove = true; // Marca este item específico para ser removido
                }
                // Se a vida já está cheia, o item de conserto não é pego e continua na tela
                break; // Apenas um item de conserto pode ser pego por vez
            }
        }

        // Colisão com carros
        boolean colisaoCarro = false;
        for (int i = 0; i < gameRef.carros.activeCars.size(); i++) {
            Carros carro = gameRef.carros.activeCars.get(i);
            if (carro.verificaColisao(this)) {
                colisaoCarro = true;
                carro.remove = true; // Marca o carro para ser removido/resetado
                break; // Colidiu com um carro, pode sair do loop
            }
        }

        if (colisaoCarro) {
            perderVida();
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
        // Renderiza a vida do jogador
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Vida: " + vida, 10, 20);
        // Renderiza os pontos do jogador
       

    }
}