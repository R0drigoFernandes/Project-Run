package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;

import application.ProjectRun; // Importe ProjectRun para acessar a instância de consertar global.


public class Carros implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y, width, height; // Tornando x, y públicos para fácil acesso
    public int acelerar = 3, pontos = 0, contador = 0;
    public boolean remove;

    // Esta ArrayList de Carros é para gerenciar as instâncias de carros em JOGO.
    // Ela não deve ser instanciada por cada 'new Carros()'.
    // A instância principal de 'Carros' em ProjectRun é que deve ter e gerenciar esta lista.
    // Vamos ajustar o construtor de Carros e ProjectRun para isso.
    public ArrayList<Carros> activeCars = new ArrayList<>(); // Renomeado para clareza

    // Remove a referência a Consertar aqui, pois a criação será gerenciada em ProjectRun.

    public Carros(int x, int y) { // Construtor para carros individuais
        this.x = x;
        this.y = y;
        this.width = 32;
        this.height = 32;
        this.remove = false;
        // Não adicionar 'this' a 'carros' aqui. A lista 'activeCars' será gerenciada externamente.
    }

    public Carros() { // Construtor padrão para o carro inicial (se ProjectRun ainda o instanciar assim)
        this.x = (int)(Math.random()*(310 - 150 + 1) + 150);
        this.y = 0;
        this.width = 32;
        this.height = 32;
        this.remove = false;
    }


    public boolean verificaColisao(Player player) {
        if( this.x < player.x + player.width &&
               this.x + this.width > player.x &&
               this.y < player.y + player.height &&
               this.y + this.height > player.y){
            return true;
        }else{
            return false;
        }
    }

    public void tick(ProjectRun game) { // Passa a referência do jogo para Carros
        y += acelerar;
        
        if(remove){
            y = 0;
            // A lógica de remoção e adição deve ser na lista que gerencia os carros ativos
            // Esta instância de carro se marca para remoção, mas a remoção real da lista 'activeCars'
            // e a adição de um novo carro deve ser gerenciada em ProjectRun.
            // Para manter a lógica de que um carro removido é imediatamente substituído:
            this.x = (int)(Math.random()*(310 - 150 + 1) + 150);
            this.y = 0;
            this.remove = false; // Resetar o estado de remoção
        }
        
        if(y > ProjectRun.height){ // Use ProjectRun.height para o limite da tela
            y = 0;
            this.x = (int)(Math.random()*(310 - 150 + 1) + 150);
            
            contador++;
            pontos++;
            
            // Incrementa o contador de consertar, mas a criação é em ProjectRun
            // Consertar.count++; // Não use um contador estático aqui.
            
            // Notifica o ProjectRun que um carro passou e que pode ser hora de criar um Consertar
            if (game != null) {
                game.carPassou(); // Método que será criado em ProjectRun
            }

            if(contador == 3){
                if(acelerar < 15) { // Limita a velocidade máxima
                    // Aumenta a velocidade do carro a cada 3 carros passados
                    acelerar++;
                }
                
                contador = 0;
            }
        }
    }



    public void render(Graphics g){
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
         g.setColor(Color.WHITE);
        g.drawString("Pontos: " + pontos, 10, 40);
    }
}