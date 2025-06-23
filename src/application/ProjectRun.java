package application;

import javax.swing.*;
import entities.Carros;
import entities.Consertar;
import entities.Invencible;
import entities.InvencibilityItem;
import entities.FicarLentoItem;
import entities.Pista;
import entities.Player;
import entities.pauseMenu;
import entities.Menu;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;

public class ProjectRun extends Canvas implements Runnable, KeyListener {

    private static final long serialVersionUID = 1L;
    public pauseMenu menu;
    public Pista pista;
    public Player player;
    public Carros carros; // Instância principal de Carros
    public Consertar consertar;
    public Invencible invencible;
    public ArrayList<InvencibilityItem> invencibilityItems;
    public ArrayList<FicarLentoItem> slowItems;
    public static int width = 500, height = 500;
    private boolean running = false;
    private boolean paused = false;

    private int carsPassedCount = 0;
    private int invencibilitySpawnThreshold = 10;
    private int slowItemSpawnThreshold = 15;
    private boolean slowEffectActive = false;
    private long slowEffectStartTime;
    private final long slowEffectDuration = 5000;

    public ProjectRun(){
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);

        pista = new Pista();
        carros = new Carros();
        player = new Player(width / 2, 400, 3, 32, 32, carros, new Consertar(), new Invencible());
        player.setGameReference(this);

        carros.setGame(this); // Passa a referência de ProjectRun para a instância principal de Carros

        // Adiciona 5 carros iniciais em posições espaçadas para que apareçam gradualmente
        for(int i = 0; i < 5; i++) {
            carros.activeCars.add(new Carros(
                (int)(Math.random()*(310 - 150 + 1) + 150), // x aleatório
                -i * 100 - 50 // y para que eles apareçam acima da tela e espaçados
            ));
        }

        consertar = new Consertar();
        invencible = new Invencible();
        invencibilityItems = new ArrayList<>();
        slowItems = new ArrayList<>();

        menu = new pauseMenu(this);
        menu.setVisible(false);
    }

    public synchronized void startGame(){
        running = true;
        new Thread(this).start();
    }

    public synchronized void stopGame(){
        running = false;
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void tick(){
        if(paused) return;

        invencible.tick();

        if (slowEffectActive) {
            if (System.currentTimeMillis() - slowEffectStartTime >= slowEffectDuration) {
                slowEffectActive = false;
                carros.slow = false;
            }
        }

        // Atualiza todos os carros ativos
        // Usamos um loop for tradicional ou um iterador que não modifica a estrutura da lista
        // para evitar ConcurrentModificationException.
        // A melhor abordagem é reutilizar o objeto Carros que saiu da tela.
        for (int i = 0; i < carros.activeCars.size(); i++) {
            Carros car = carros.activeCars.get(i);
            // Ajusta a velocidade com base na flag 'slow' da instância principal de Carros
            car.y += (carros.slow ? carros.acelerar / 2 : carros.acelerar);

            if (car.y > ProjectRun.height) {
                // Um carro saiu da tela, REUTILIZE-O e reposicione-o no topo
                car.y = 0;
                car.x = (int)(Math.random()*(310 - 150 + 1) + 150);

                // Atualiza pontos e aceleração somente quando um carro é efetivamente "passado"
                carros.pontos++;
                carros.contador++;
                if (carros.contador == 3) {
                    if (!carros.slow && carros.acelerar < 13) {
                        carros.acelerar++;
                    }
                    carros.contador = 0;
                }
                carPassou(); // Chama a lógica de spawn de itens
            }
        }


        Iterator<Consertar> consertarIterator = consertar.consertarList.iterator();
        while (consertarIterator.hasNext()) {
            Consertar item = consertarIterator.next();
            item.tick();
            if (item.verificaColisao(player)) {
                player.vida++;
                consertarIterator.remove();
            } else if (item.remove) {
                consertarIterator.remove();
            }
        }

        Iterator<InvencibilityItem> invencibilityIterator = invencibilityItems.iterator();
        while (invencibilityIterator.hasNext()) {
            InvencibilityItem item = invencibilityIterator.next();
            item.tick();
            if (item.verificaColisao(player)) {
                invencible.activate();
                invencibilityIterator.remove();
            } else if (item.remove) {
                invencibilityIterator.remove();
            }
        }

        Iterator<FicarLentoItem> slowItemIterator = slowItems.iterator();
        while (slowItemIterator.hasNext()) {
            FicarLentoItem item = slowItemIterator.next();
            item.tick();
            if (item.verificaColisao(player)) {
                slowEffectActive = true;
                slowEffectStartTime = System.currentTimeMillis();
                carros.slow = true;
                slowItemIterator.remove();
            } else if (item.remove) {
                slowItemIterator.remove();
            }
        }

        player.tick();
        pista.tick();
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        pista.render(g);

        for (Consertar item : consertar.consertarList) {
            item.render(g);
        }

        for (InvencibilityItem item : invencibilityItems) {
            item.render(g);
        }

        for (FicarLentoItem item : slowItems) {
            item.render(g);
        }

        player.render(g);

        // Renderiza os carros (activeCars)
        carros.render(g); // Chama o método render da instância principal de Carros

        invencible.render(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Pontos: " + carros.pontos, 20, 30);
        g.drawString("Vida: " + player.vida, 20, 60);

        g.dispose();
        bs.show();
    }

    public void carPassou(){
        carsPassedCount++;

        if (carsPassedCount % 3 == 0) {
            int randomX = (int)(Math.random()*(310 - 150 + 1) + 150);
            consertar.consertarList.add(new Consertar(randomX, 0));
        }

        if (carsPassedCount % invencibilitySpawnThreshold == 0) {
            int randomX = (int)(Math.random()*(310 - 150 + 1) + 150);
            invencibilityItems.add(new InvencibilityItem(randomX, 0));
        }

        if (carsPassedCount % slowItemSpawnThreshold == 0) {
            int randomX = (int)(Math.random()*(310 - 150 + 1) + 150);
            slowItems.add(new FicarLentoItem(randomX, 0));
        }
    }


    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                tick();
                delta--;
            }
            if(running){
                render();
            }
            frames++;

            if(System.currentTimeMillis() - timer >= 1000){
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        stopGame();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(paused) {
            menu.keyPressed(e);
            return;
        }

        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            player.right = true;
        }else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            player.left = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_UP){
            player.acelerar = true;
        }else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            player.freiar = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            togglePause();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(paused) {
            menu.keyReleased(e);
            return;
        }

        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            player.right = false;
        }else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            player.left = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_UP){
            player.acelerar = false;
        }else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            player.freiar = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Não utilizado neste jogo
    }

    public void applyGameData(GameData data) {
        player.x = data.getPlayerX();
        player.y = data.getPlayerY();
        player.vida = data.getPlayerVida();
        carros.pontos = data.getCarrosPontos();
        carros.acelerar = data.getCarrosAcelerar();
        carros.contador = data.getCarrosContador();
        carsPassedCount = data.getCarsPassedCount();
        this.slowEffectActive = data.isSlowEffectActive();
        this.slowEffectStartTime = data.getSlowEffectStartTime();

        // Clear existing cars and add new ones based on loaded data (if you want to save/load individual car positions)
        // For simplicity, we are not saving individual car positions here.
        carros.activeCars.clear();
        // Adiciona 5 novos carros para que o jogo continue, em vez de carregar os antigos
        for(int i = 0; i < 5; i++) {
            carros.activeCars.add(new Carros(
                (int)(Math.random()*(310 - 150 + 1) + 150),
                -i * 100 - 50
            ));
        }

        consertar.consertarList.clear();
        invencibilityItems.clear();
        slowItems.clear();

        if (slowEffectActive) {
            carros.slow = true;
        }
    }

    public GameData getGameData() {
        return new GameData(player.x, player.y, player.vida,
                            carros.pontos, carros.acelerar, carros.contador,
                            carsPassedCount, slowEffectActive, slowEffectStartTime);
    }

    public void gameOver() {
        stopGame();
        int choice = JOptionPane.showConfirmDialog(this, "Game Over! Pontos: " + carros.pontos + "\nDeseja jogar novamente?", "Game Over", JOptionPane.YES_NO_OPTION);

        if(choice == JOptionPane.YES_OPTION){
            gamereset();
        } else if (JOptionPane.showConfirmDialog(this, "Deseja voltar ao Menu?")==0){
            SwingUtilities.invokeLater(() -> new Menu());
        }else{
            System.exit(0);
        }
    }

    public void gamereset(){
        player.x = width / 2;
        player.y = 400;
        player.vida = 3;
        player.acelerar = false;
        player.freiar = false;
        player.right = false;
        player.left = false;
        carros.pontos = 0;
        carros.acelerar = 3;
        carros.contador = 0;
        carsPassedCount = 0;
        slowEffectActive = false;
        carros.slow = false;

        carros.activeCars.clear();
        for(int i = 0; i < 5; i++) { // Adiciona 5 carros iniciais novamente
            carros.activeCars.add(new Carros(
                (int)(Math.random()*(310 - 150 + 1) + 150),
                -i * 100 - 50
            ));
        }

        consertar.consertarList.clear();
        invencibilityItems.clear();
        slowItems.clear();

        paused = false;
        if (!running) {
             running = true;
             new Thread(this).start();
        }
    }

    public void exitGame(){
        int confirm = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja sair?", "Sair do Jogo", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void togglePause() {
        paused = !paused;
        if (paused) {
            if (menu == null) {
                menu = new pauseMenu(this);
            }
            menu.setVisible(true);
            setFocusable(false);
            menu.requestFocusInWindow();
        } else {
            if (menu != null) {
                menu.dispose();
            }
            setFocusable(true);
            requestFocusInWindow();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu());
    }
}