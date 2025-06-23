package application;

import javax.swing.*;
import entities.Carros;
import entities.Consertar;
import entities.Invencible;
import entities.InvencibilityItem;
import entities.FicarLentoItem; // Importe FicarLentoItem
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
    public Carros carros;
    public Consertar consertar;
    public Invencible invencible;
    public ArrayList<InvencibilityItem> invencibilityItems;
    public ArrayList<FicarLentoItem> slowItems; // Lista para itens de lentidão
    public static int width = 500, height = 500;
    private boolean running = false;
    private boolean paused = false;

    private int carsPassedCount = 0;
    private int invencibilitySpawnThreshold = 10; // A cada 10 carros, um item de invencibilidade
    private int slowItemSpawnThreshold = 15; // A cada 15 carros, um item de lentidão
    private boolean slowEffectActive = false; // Flag para controlar o efeito de lentidão
    private long slowEffectStartTime; // Tempo de início do efeito de lentidão
    private final long slowEffectDuration = 5000; // Duração do efeito de lentidão em milissegundos (5 segundos)


    public ProjectRun(){
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK); // Definindo a cor de fundo como preto

        pista = new Pista(); // Corrigido: Construtor Pista()
        player = new Player(width / 2, 400, 3, 32, 32, new Carros(), new Consertar(), new Invencible()); // Corrigido: Construtor Player() e instâncias de dependência
        player.setGameReference(this); // Importante para que o Player possa acessar o ProjectRun

        carros = new Carros(); // Instância principal para gerenciar carros
        carros.setGame(this); // Passa a referência de ProjectRun para Carros
        carros.activeCars.add(new Carros()); // Adiciona o primeiro carro

        consertar = new Consertar(); // Instância principal para gerenciar itens de conserto
        invencible = new Invencible(); // Instância de invencibilidade
        invencibilityItems = new ArrayList<>();
        slowItems = new ArrayList<>(); // Inicializa a lista de itens de lentidão

        menu = new pauseMenu(this); // Passa a referência de ProjectRun para pauseMenu
        menu.setVisible(false); // Inicia o menu de pausa invisível
    }

    public synchronized void startGame(){
        running = true;
        new Thread(this).start();
    }

    public synchronized void stopGame(){
        running = false;
        try {
            Thread.sleep(1); // Pequena pausa para a thread terminar
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void tick(){
        if(paused) return; // Se estiver pausado, não atualiza a lógica do jogo

        // Atualiza a lógica de invencibilidade
        invencible.tick();

        // Lógica para o efeito de lentidão
        if (slowEffectActive) {
            if (System.currentTimeMillis() - slowEffectStartTime >= slowEffectDuration) {
                slowEffectActive = false;
                carros.slow = false; // Desativa o efeito de lentidão nos carros
            }
        }

        // Atualiza os carros
        carros.tick(); // Certifique-se de que Carros tem um método tick() que itera sobre activeCars e chama o tick de cada carro.

        // Atualiza itens de conserto (Consertar)
        Iterator<Consertar> consertarIterator = consertar.consertarList.iterator();
        while (consertarIterator.hasNext()) {
            Consertar item = consertarIterator.next();
            item.tick();
            if (item.verificaColisao(player)) {
                player.vida++;
                consertarIterator.remove(); // Remove o item após a colisão
            } else if (item.remove) {
                consertarIterator.remove(); // Remove se saiu da tela
            }
        }

        // Atualiza itens de invencibilidade
        Iterator<InvencibilityItem> invencibilityIterator = invencibilityItems.iterator();
        while (invencibilityIterator.hasNext()) {
            InvencibilityItem item = invencibilityIterator.next();
            item.tick();
            if (item.verificaColisao(player)) {
                invencible.activate(); // Ativa a invencibilidade
                invencibilityIterator.remove();
            } else if (item.remove) {
                invencibilityIterator.remove();
            }
        }

        // Atualiza itens de lentidão
        Iterator<FicarLentoItem> slowItemIterator = slowItems.iterator();
        while (slowItemIterator.hasNext()) {
            FicarLentoItem item = slowItemIterator.next();
            item.tick();
            if (item.verificaColisao(player)) {
                slowEffectActive = true;
                slowEffectStartTime = System.currentTimeMillis();
                carros.slow = true; // Ativa o efeito de lentidão nos carros
                slowItemIterator.remove();
            } else if (item.remove) { // Corrigido: FicarLentoItem.remove agora é public
                slowItemIterator.remove();
            }
        }

        player.tick();
        pista.tick(); // Adicionado para animar a pista
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        pista.render(g); // Renderiza a pista

        // Renderiza itens de conserto
        for (Consertar item : consertar.consertarList) {
            item.render(g);
        }

        // Renderiza itens de invencibilidade
        for (InvencibilityItem item : invencibilityItems) {
            item.render(g);
        }

        // Renderiza itens de lentidão
        for (FicarLentoItem item : slowItems) {
            item.render(g);
        }

        player.render(g); // Renderiza o player

        // Renderiza os carros (activeCars)
        for(Carros car : carros.activeCars){ // Itera sobre a lista activeCars da instância de Carros
            car.render(g);
        }

        invencible.render(g); // Renderiza o efeito de invencibilidade, se ativo

        // Pontuação
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Pontos: " + carros.pontos, 20, 30);
        g.drawString("Vida: " + player.vida, 20, 60);

        if(paused){
            // Não chame menu.render(g); diretamente. O JFrame se renderiza sozinho.
            // A visibilidade do menu já é controlada por togglePause()
        }

        g.dispose();
        bs.show();
    }

    public void carPassou(){
        carsPassedCount++; // Incrementa o contador de carros passados

        // Lógica para criar itens de conserto
        if (carsPassedCount % 3 == 0) { // A cada 3 carros, cria um item de conserto
            int randomX = (int)(Math.random()*(310 - 150 + 1) + 150);
            consertar.consertarList.add(new Consertar(randomX, 0));
        }

        // Lógica para criar itens de invencibilidade
        if (carsPassedCount % invencibilitySpawnThreshold == 0) {
            int randomX = (int)(Math.random()*(310 - 150 + 1) + 150);
            invencibilityItems.add(new InvencibilityItem(randomX, 0));
        }

        // Lógica para criar itens de lentidão
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
            menu.keyPressed(e); // Passa o evento para o menu de pausa
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
            menu.keyReleased(e); // Passa o evento para o menu de pausa
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
        carsPassedCount = data.getCarsPassedCount(); // Corrigido: getCarsPassedCount()
        this.slowEffectActive = data.isSlowEffectActive(); // Carrega o estado do efeito de lentidão
        this.slowEffectStartTime = data.getSlowEffectStartTime(); // Carrega o tempo de início

        // Clear existing cars and add new ones based on loaded data (if you want to save/load individual car positions)
        // For simplicity, we are not saving individual car positions here.
        // You might need to adjust Carros to be Serializable and iterate through activeCars.
        carros.activeCars.clear();
        carros.activeCars.add(new Carros()); // Add at least one car

        consertar.consertarList.clear(); // Clear existing repair items

        invencibilityItems.clear(); // Limpa itens de invencibilidade ao carregar
        slowItems.clear(); // Limpa itens de lentidão ao carregar

        if (slowEffectActive) { // If slow effect was active, reapply it to cars
            carros.slow = true;
        }

        // Se o jogo estava pausado ao salvar, ele deve começar pausado
        // A lógica de pause está no menu de pausa, então não precisamos setar aqui
    }

    public GameData getGameData() {
        return new GameData(player.x, player.y, player.vida,
                            carros.pontos, carros.acelerar, carros.contador,
                            carsPassedCount, slowEffectActive, slowEffectStartTime); // Inclui os novos dados
    }

    public void gameOver() {
        // Pausa o jogo
        stopGame();
        // Exibe um JOptionPane
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
        slowEffectActive = false; // Reseta o efeito de lentidão
        carros.slow = false; // Garante que os carros não estão lentos

        carros.activeCars.clear();
        carros.activeCars.add(new Carros());

        consertar.consertarList.clear();
        invencibilityItems.clear();
        slowItems.clear(); // Limpa itens de lentidão no reset

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
            // Se o menu já estiver instanciado, apenas o torna visível
            if (menu == null) { // Caso seja a primeira vez que o menu é aberto
                menu = new pauseMenu(this);
            }
            menu.setVisible(true); // Mostra o menu de pausa
            setFocusable(false); // Remove o foco do canvas para que o menu receba eventos de teclado
            menu.requestFocusInWindow(); // Solicita o foco para o menu
        } else {
            if (menu != null) {
                menu.dispose(); // Fecha o menu de pausa
            }
            setFocusable(true); // Devolve o foco para o canvas
            requestFocusInWindow(); // Solicita o foco para o canvas
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu());
    }
}