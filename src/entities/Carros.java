package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;


import application.ProjectRun;


public class Carros implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y, width, height;
    public int acelerar = 3, pontos = 0, contador = 0;
    public boolean remove;
    public boolean slow = false; // Nova flag para controlar o efeito de lentidão

    public ArrayList<Carros> activeCars = new ArrayList<>();
    private ProjectRun game; // Referência para ProjectRun

    public Carros(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 32;
        this.height = 32;
        this.remove = false;
    }

    // Construtor padrão (adicionado para inicialização em ProjectRun)
    public Carros() {
        this.x = (int)(Math.random()*(310 - 150 + 1) + 150);
        this.y = 0;
        this.width = 32;
        this.height = 32;
        this.remove = false;
    }

    public void setGame(ProjectRun game) {
        this.game = game;
    }

    public void tick() {
        // Itera sobre a lista de carros ativos para mover e verificar a remoção de itens com um Iterator se necessário
        for(int i = 0; i < activeCars.size(); i++) {
            Carros car = activeCars.get(i);
            // Ajusta a velocidade com base na flag 'slow'
            car.y += (slow ? acelerar / 2 : acelerar); // Se slow for true, a velocidade é metade

            if(car.y > ProjectRun.height){
                // Um carro saiu da tela, "remove" ele e cria um novo na parte superior
                car.y = 0;
                car.x = (int)(Math.random()*(310 - 150 + 1) + 150);

                // Apenas para a instância principal de Carros que está em ProjectRun
                // (onde `pontos`, `contador` e `acelerar` são realmente usados para o jogo)
                if (game != null) { // Verifica se a referência ao jogo está disponível
                    pontos++; // Atualiza a pontuação na instância principal
                    game.carPassou(); // Notifica ProjectRun que um carro passou
                    contador++; // Incrementa o contador para aceleração
                    if(contador == 3){
                        if(!slow && acelerar < 13) { // Acelera apenas se não estiver lento e abaixo do limite
                            acelerar++;
                        }
                        contador = 0;
                    }
                }
            }
        }
    }

    // Método verificaColisao para verificar colisão com o Player
    public boolean verificaColisao(Player player) {
        // Verifica se a caixa delimitadora do carro colide com a caixa delimitadora do player
        if (this.x < player.x + player.width &&
            this.x + this.width > player.x &&
            this.y < player.y + player.height &&
            this.y + this.height > player.y) {
            return true;
        }
        return false;
    }

    public void render(Graphics g){
        g.setColor(Color.RED);
        // Renderiza cada carro ativo na lista
        for(int i = 0; i < activeCars.size(); i++) {
            Carros car = activeCars.get(i);
            g.fillRect(car.x, car.y, car.width, car.height);
        }
    }
}