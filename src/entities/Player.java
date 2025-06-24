package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable; // Certifique-se de importar Serializable

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y, width, height;
    public int playerVida = 3;
    // Flags para movimento em 4 direções (left, right, up, down)
    public boolean right, left, up, down;
    public int spd = 3; // Velocidade de movimento horizontal e vertical

    // Para o efeito de lentidão
    public boolean slowEffectActive = false;
    public long slowEffectStartTime = 0;
    private long slowEffectDuration = 5000; // 5 segundos de lentidão

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 32;
        this.height = 32;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getVida() { return playerVida; }
    public boolean isSlowEffectActive() { return slowEffectActive; }
    public long getSlowEffectStartTime() { return slowEffectStartTime; }

    // Setters (para salvar/carregar ou ativar efeitos)
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVida(int vida) { this.playerVida = vida; }
    public void setSlowEffectActive(boolean slowEffectActive) { this.slowEffectActive = slowEffectActive; }
    public void setSlowEffectStartTime(long slowEffectStartTime) { this.slowEffectStartTime = slowEffectStartTime; }

    public void tick() {
        int currentSpd = spd;
        if (slowEffectActive) {
            currentSpd = spd / 2; // Metade da velocidade se o efeito de lentidão estiver ativo
        }

        // Lógica de movimento para 4 direções
        if (right) {
            x += currentSpd;
        }
        if (left) {
            x -= currentSpd;
        }
        if (up) {
            y -= currentSpd;
        }
        if (down) {
            y += currentSpd;
        }

        // Limites da pista e da tela
        // Importante: ProjectRun.width e Pista.GRASS_WIDTH devem ser STATIC para acesso aqui
        if (x + width > application.ProjectRun.width - Pista.GRASS_WIDTH) { // Limite direito da pista
            x = application.ProjectRun.width - Pista.GRASS_WIDTH - width;
        }
        if (x < Pista.GRASS_WIDTH) { // Limite esquerdo da pista
            x = Pista.GRASS_WIDTH;
        }
        if (y + height > application.ProjectRun.height) { // Limite inferior da tela
            y = application.ProjectRun.height - height;
        }
        if (y < 0) { // Limite superior da tela
            y = 0;
        }

        // Atualiza o efeito de lentidão
        updateSlowEffect();
    }

    // Verifica e desativa o efeito de lentidão se o tempo expirou
    public void updateSlowEffect() {
        if (slowEffectActive) {
            if (System.currentTimeMillis() - slowEffectStartTime >= slowEffectDuration) {
                slowEffectActive = false;
                System.out.println("Efeito de lentidão desativado.");
            }
        }
    }

    // Renderiza o player
    public void render(Graphics g) {
        if (slowEffectActive) {
            g.setColor(new Color(0, 255, 255, 150)); // Ciano transparente quando lento
            g.fillRect(x, y, width, height);
        }
        g.setColor(Color.BLUE); // Cor do player
        g.fillRect(x, y, width, height);
        // Opcional: Desenhar um contorno ou imagem para o player
    }

    // Método para verificar colisão com um objeto Carro
    public boolean verificaColisao(Carros carro) {
        return this.x < carro.x + carro.width &&
               this.x + this.width > carro.x &&
               this.y < carro.y + carro.height &&
               this.y + this.height > carro.y;
    }
}
