package entities;

import java.awt.*;
import application.ProjectRun;

public class Invencible {
    private boolean active; // Indica se a invencibilidade está ativa
    private long startTime; // Tempo em que a invencibilidade foi ativada
    private long duration = 7000; // Duração da invencibilidade em milissegundos (7 segundos)

    public Invencible() {
        this.active = false;
    }

    // Retorna se a invencibilidade está ativa
    public boolean isActive() {
        return active;
    }

    // Ativa a invencibilidade e registra o tempo de início
    public void activate() {
        this.active = true;
        this.startTime = System.currentTimeMillis();
    }

    // Atualiza o estado da invencibilidade a cada "tick" do jogo
    public void tick() {
        if (active) {
            // Se o tempo atual menos o tempo de início for maior ou igual à duração, desativa
            if (System.currentTimeMillis() - startTime >= duration) {
                active = false;
            }
        }
    }

    // Renderiza um efeito visual para indicar a invencibilidade
    public void render(Graphics g) {
        if (active) {
            // Desenha um retângulo semi-transparente sobre a tela
            g.setColor(new Color(255, 255, 0, 128)); // Amarelo semi-transparente
            // Ajuste as coordenadas e tamanho para cobrir a tela do jogo
            g.fillRect(0, 0, ProjectRun.width, ProjectRun.height);
        }
    }
}