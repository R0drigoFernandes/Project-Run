package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;


import application.ProjectRun;


public class Carros implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y, width, height; // Estas são as coordenadas e dimensões para UMA instância de carro individual
    public int acelerar = 3, pontos = 0, contador = 0; // Estas deveriam ser controladas pela instância principal de Carros em ProjectRun
    public boolean remove;
    public boolean slow = false; // Flag para controlar o efeito de lentidão (na instância principal)

    // Esta lista pertence à ÚNICA instância de 'Carros' gerenciada por ProjectRun.
    // Ela não deveria ser criada para cada Carro individual que é adicionado a ela.
    // A instância de Carros que é criada em ProjectRun terá esta lista.
    public ArrayList<Carros> activeCars = new ArrayList<>(); // Lista de carros que estão na tela

    public ProjectRun game; // Referência para ProjectRun

    // Construtor para CARROS INDIVIDUAIS que são adicionados à lista activeCars
    public Carros(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 32;
        this.height = 32;
        this.remove = false;
    }

    // Construtor padrão. Esta é a instância que estará em ProjectRun (public Carros carros;).
    // Ela não representa um carro na tela, mas sim o "gerenciador" de carros.
    public Carros() {
        // Nada a inicializar para um carro individual aqui.
        // As variáveis de jogo (acelerar, pontos, contador) são desta instância.
    }

    public void setGame(ProjectRun game) {
        this.game = game;
    }

    // Este método tick() e render() só devem ser chamados para a INSTÂNCIA PRINCIPAL de Carros em ProjectRun.
    // Ele itera sobre activeCars e chama o tick/render de cada carro individual.
    public void tick() {
        // A lógica de movimento e remoção dos carros individuais foi movida para ProjectRun.tick()
        // para que ProjectRun possa gerenciar a adição/remoção da lista activeCars.
        // Este método 'tick' aqui é para a INSTÂNCIA PRINCIPAL de Carros
        // Se você quiser que cada carro individual tenha seu próprio 'tick', você precisa ter:
        // public void tickIndividual() { /* lógica de movimento de um carro */ }
        // E chamá-lo em ProjectRun.tick() para cada 'car' em 'carros.activeCars'.
        // No entanto, a lógica de respawn e pontuação é melhor no ProjectRun.
    }

    // Método verificaColisao para verificar colisão com o Player
    public boolean verificaColisao(Player player) {
        // Este método é chamado para cada *carro individual* na lista `activeCars`.
        // Portanto, ele usa as coordenadas (this.x, this.y) do carro individual.
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
        // Renderiza cada carro ativo na lista 'activeCars' desta instância principal de Carros
        for(int i = 0; i < activeCars.size(); i++) {
            Carros car = activeCars.get(i); // Obtém o carro individual
            g.fillRect(car.x, car.y, car.width, car.height); // Renderiza as coordenadas do carro individual
        }
    }
}