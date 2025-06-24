package application;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;     // Importar Stroke
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities; // Importar SwingUtilities

import entities.Carros;
import entities.Consertar;
import entities.FicarLentoItem;
import entities.InvencibilityItem;
import entities.Invencible;
import entities.Menu;
import entities.Pista;
import entities.Player;
import entities.pauseMenu;

public class ProjectRun extends Canvas implements Runnable, KeyListener {
    private static final long serialVersionUID = 1L;

    // Dimensões da janela do jogo (static final para acesso global)
    public static final int width = 480;
    public static final int height = 640;

    // Atributos do jogo
    public Player player;
    public Pista pista;
    public Carros carrosManager; // Gerencia a lista de carros inimigos
    public Consertar consertarManager; // Gerencia a lista de itens de conserto
    public Invencible invencible; // Gerencia o estado de invencibilidade do player
    public ArrayList<FicarLentoItem> slowItems; // Lista de itens de lentidão
    public ArrayList<InvencibilityItem> invencibilityItems; // Lista de itens de invencibilidade

    private Random random; // Para geração de números aleatórios (spawn de itens/carros)

    private Thread thread;
    private boolean isRunning = false;
    private boolean isPaused = false; // Estado de pausa do jogo
    private pauseMenu pauseMenuInstance; // Instância do menu de pausa

    public int gamePoints = 0;
    public int gameCarSpeed = 3; // Velocidade base dos carros e da pista
    public int carsPassedCount = 0; // Contador para spawn de itens e aceleração
    private boolean gameOverDisplayed = false; // Controla se a mensagem de Game Over já foi exibida

    // Atributos para controle de spawn de carros e itens
    private long lastCarSpawnTime = 0;
    private final long CAR_SPAWN_DELAY = 1200; // Atraso em milissegundos entre os spawns de carros
    private long lastConsertarSpawnTime = 0;
    private final long CONSERTAR_SPAWN_DELAY = 10000; // Atraso para item de consertar
    private long lastInvencibilitySpawnTime = 0;
    private final long INVENCIBILITY_SPAWN_DELAY = 15000; // Atraso para item de invencibilidade
    private long lastSlowItemSpawnTime = 0;
    private final long SLOW_ITEM_SPAWN_DELAY = 20000; // Atraso para item de lentidão


    public ProjectRun() {
        setPreferredSize(new java.awt.Dimension(width, height));
        addKeyListener(this);
        setFocusable(true); // Garante que o canvas pode receber eventos de teclado
        requestFocusInWindow(); // Solicita o foco imediatamente ao ser criado

        // Inicializações de objetos do jogo
        player = new Player(width / 2 - 16, height - 64);
        pista = new Pista();
        carrosManager = new Carros(); // Objeto gerenciador de carros
        consertarManager = new Consertar(); // Objeto gerenciador de itens de conserto
        invencible = new Invencible(); // Objeto para controlar a invencibilidade
        slowItems = new ArrayList<>();
        invencibilityItems = new ArrayList<>();
        random = new Random();

        // Inicializa o menu de pausa, passando a referência deste jogo
        this.pauseMenuInstance = new pauseMenu(this);
    }

    // Método para iniciar o loop do jogo
    public synchronized void startGame() {
        if (isRunning) return; // Evita iniciar o jogo múltiplas vezes
        isRunning = true;
        thread = new Thread(this);
        thread.start();
        requestFocusInWindow(); // Garante foco ao iniciar/retomar o jogo
    }

    // Método para parar o loop do jogo
    public synchronized void stopGame() {
        if (!isRunning) return;
        isRunning = false;
        try {
            thread.join(); // Espera a thread terminar
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Alterna o estado de pausa do jogo
    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseMenuInstance.setVisible(true); // Mostra o menu de pausa
            System.out.println("Jogo Pausado.");
        } else {
            pauseMenuInstance.setVisible(false); // Esconde o menu de pausa
            System.out.println("Jogo Despausado.");
            requestFocusInWindow(); // Recupera o foco para o jogo
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                // Apenas executa a lógica do jogo (tick) se não estiver pausado
                if (!isPaused) {
                    tick();
                    
                }
                render(); // O render sempre acontece para exibir o jogo ou o menu de pausa
                
                delta--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                // System.out.println("FPS: " + frames + " Ticks: " + ticks);
                
            }
        }
        stopGame(); // Garante que a thread seja parada corretamente ao sair do loop
    }


    public void tick() {
        // Lógica de Game Over
        if (player.playerVida <= 0) {
            if (!gameOverDisplayed) {
                gameOverDisplayed = true;
                stopGame(); // Para o loop do jogo
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (parentFrame != null) {
                    parentFrame.dispose(); // Fecha a janela do jogo
                }

                int choice = JOptionPane.showConfirmDialog(null,
                    "Game Over! Sua pontuação: " + gamePoints + "\nDeseja jogar novamente?",
                    "Fim de Jogo",
                    JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    SwingUtilities.invokeLater(() -> new Menu().setVisible(true)); // Volta para o menu principal
                } else {
                    System.exit(0); // Sai da aplicação
                }
            }
            return; // Impede que o resto do tick seja executado após o Game Over
        }

        // Atualiza a lógica dos objetos do jogo
        player.tick();
        pista.tick();
        invencible.tick(); // Atualiza o estado de invencibilidade

        // --- Lógica de Spawn de Carros (um de cada vez, em qualquer lugar da pista) ---
        if (System.currentTimeMillis() - lastCarSpawnTime > CAR_SPAWN_DELAY && carrosManager.activeCars.size() < 3) {
            // Garante que a largura do carro não ultrapasse os limites da pista
            int carWidth = 32;
            int spawnX = random.nextInt(Pista.ROAD_WIDTH - carWidth) + Pista.GRASS_WIDTH; // Posição X aleatória dentro da pista
            carrosManager.activeCars.add(new Carros(spawnX, -50, carWidth, 32)); // Adiciona um novo carro no topo
            lastCarSpawnTime = System.currentTimeMillis();
        }

        // --- Atualiza e verifica colisões dos carros ---
        Iterator<Carros> carIterator = carrosManager.activeCars.iterator();
        while (carIterator.hasNext()) {
            Carros car = carIterator.next();
            car.tick(gameCarSpeed); // Carro se move com a velocidade do jogo

            if (car.y > height) { // Se o carro saiu da tela por baixo
                carIterator.remove();
                gamePoints++; // Adiciona pontos por carro desviado
                carsPassedCount++; // Incrementa contador para itens e aceleração
            } else if (car.verificaColisao(player)) {
                if (!invencible.isActive()) { // Só perde vida se não estiver invencível
                    player.setVida(player.getVida() - 1);
                    System.out.println("Colisão! Vida: " + player.getVida());
                    invencible.activate(); // Ativa a invencibilidade após a colisão
                }
                carIterator.remove(); // Remove o carro após a colisão
            }
        }

        // --- Lógica de Spawn e Atualização de Itens de Consertar (Coração) ---
        if (System.currentTimeMillis() - lastConsertarSpawnTime > CONSERTAR_SPAWN_DELAY && consertarManager.consertarList.isEmpty()) {
            int itemWidth = 16;
            int spawnX = random.nextInt(Pista.ROAD_WIDTH - itemWidth) + Pista.GRASS_WIDTH;
            consertarManager.consertarList.add(new Consertar(spawnX, -50));
            lastConsertarSpawnTime = System.currentTimeMillis();
        }

        Iterator<Consertar> consertarIterator = consertarManager.consertarList.iterator();
        while (consertarIterator.hasNext()) {
            Consertar item = consertarIterator.next();
            item.tick();
            if (item.remove) {
                consertarIterator.remove();
            } else if (item.verificaColisao(player)) {
                if (player.getVida() < 3) { // Máximo de 3 vidas
                    player.setVida(player.getVida() + 1);
                    System.out.println("Vida restaurada! Vida atual: " + player.getVida());
                }
                consertarIterator.remove();
            }
        }

        // --- Lógica de Spawn e Atualização de Itens de Invencibilidade ---
        if (System.currentTimeMillis() - lastInvencibilitySpawnTime > INVENCIBILITY_SPAWN_DELAY && invencibilityItems.isEmpty()) {
            int itemWidth = 20;
            int spawnX = random.nextInt(Pista.ROAD_WIDTH - itemWidth) + Pista.GRASS_WIDTH;
            invencibilityItems.add(new InvencibilityItem(spawnX, -50));
            lastInvencibilitySpawnTime = System.currentTimeMillis();
        }

        Iterator<InvencibilityItem> invencibilityItemIterator = invencibilityItems.iterator();
        while (invencibilityItemIterator.hasNext()) {
            InvencibilityItem item = invencibilityItemIterator.next();
            item.tick();
            if (item.remove) {
                invencibilityItemIterator.remove();
            } else if (item.verificaColisao(player)) {
                invencible.activate(); // Ativa a invencibilidade
                System.out.println("Invencibilidade ativada!");
                invencibilityItemIterator.remove();
            }
        }

        // --- Lógica de Spawn e Atualização de Itens de Lentidão ---
        if (System.currentTimeMillis() - lastSlowItemSpawnTime > SLOW_ITEM_SPAWN_DELAY && slowItems.isEmpty()) {
            int itemWidth = 20;
            int spawnX = random.nextInt(Pista.ROAD_WIDTH - itemWidth) + Pista.GRASS_WIDTH;
            slowItems.add(new FicarLentoItem(spawnX, -50));
            lastSlowItemSpawnTime = System.currentTimeMillis();
        }

        Iterator<FicarLentoItem> slowItemIterator = slowItems.iterator();
        while (slowItemIterator.hasNext()) {
            FicarLentoItem item = slowItemIterator.next();
            item.tick();
            if (item.remove) {
                slowItemIterator.remove();
            } else if (item.verificaColisao(player)) {
                player.setSlowEffectActive(true); // Ativa o efeito de lentidão no player
                player.setSlowEffectStartTime(System.currentTimeMillis()); // Define o tempo de início
                System.out.println("Efeito de lentidão ativado!");
                slowItemIterator.remove();
            }
        }

        // --- Lógica de Aceleração do Jogo ---
        // Acelera o jogo a cada 5 carros passados (ajuste conforme a dificuldade desejada)
        if (carsPassedCount >= 5) {
            gameCarSpeed++; // Aumenta a velocidade dos carros e da pista
            pista.setSpeed(gameCarSpeed); // Atualiza a velocidade da pista
            carsPassedCount = 0; // Reseta o contador para a próxima aceleração
            System.out.println("Velocidade aumentada para: " + gameCarSpeed);
        }
    }


    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        // Limpa a tela
        g.setColor(new Color(100, 100, 100)); // Cor de fundo cinza, pode ser ajustada
        g.fillRect(0, 0, width, height);

        // Renderiza os elementos do jogo
        pista.render(g); // Renderiza a pista

        // Renderiza todos os carros ativos
        for (Carros car : carrosManager.activeCars) {
            car.render(g);
        }

        // Renderiza todos os itens de consertar
        for (Consertar item : consertarManager.consertarList) {
            item.render(g);
        }

        // Renderiza todos os itens de invencibilidade
        for (InvencibilityItem item : invencibilityItems) {
            item.render(g);
        }

        // Renderiza todos os itens de ficar lento
        for (FicarLentoItem item : slowItems) {
            item.render(g);
        }

        // Renderiza o efeito de invencibilidade (se ativo)
        if (invencible.isActive()) {
            invencible.render(g, player.x, player.y, player.width, player.height);
        }

        // Renderiza o jogador
        player.render(g);


        // Desenha a pontuação e a vida
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Pontos: " + gamePoints, 10, 25);
        g.drawString("Vidas: " + player.playerVida, 10, 50);

        // Libera os recursos gráficos e mostra o buffer
        g.dispose();
        bs.show();
    }

    // --- Tratamento de Eventos de Teclado ---
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            togglePause(); // Ativa/desativa o menu de pausa
        }

        // Se o jogo NÃO estiver pausado, processa o movimento do player
        if (!isPaused) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                player.right = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                player.left = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                player.up = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                player.down = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            player.right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            player.left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            player.up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            player.down = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Não utilizado
    }

    // --- Métodos de Salvar/Carregar Jogo ---
    public void saveGame() {
        GameData data = new GameData(
            player.getX(), player.getY(), player.getVida(),
            gamePoints, gameCarSpeed, carsPassedCount, // carsToAccelerateCounter foi integrado em carsPassedCount para simplificar
            player.isSlowEffectActive(), player.getSlowEffectStartTime(),
            invencible.isActive(), invencible.getStartTime()
        );
        SaveLoadManager.saveGame(data);
        JOptionPane.showMessageDialog(this, "Jogo salvo com sucesso!");
    }

    public void loadGame() {
        GameData loadedData = SaveLoadManager.loadGame();
        if (loadedData != null) {
            applyGameData(loadedData);
            System.out.println("Jogo carregado do save.");
        } else {
            System.out.println("Não há jogo salvo para carregar.");
        }
    }

    // Aplica os dados carregados ao estado do jogo
    public void applyGameData(GameData loadedData) {
        player.setX(loadedData.getPlayerX());
        player.setY(loadedData.getPlayerY());
        player.setVida(loadedData.getPlayerVida());
        this.gamePoints = loadedData.getGamePoints();
        this.gameCarSpeed = loadedData.getGameCarSpeed();
        // this.carsToAccelerateCounter = loadedData.getCarsToAccelerateCounter(); // Removido, usar carsPassedCount
        this.carsPassedCount = loadedData.getCarsPassedCount();

        player.setSlowEffectActive(loadedData.isSlowEffectActive());
        player.setSlowEffectStartTime(loadedData.getSlowEffectStartTime());

        invencible.activateWithTime(loadedData.getInvencibleStartTime());
        if (!loadedData.isInvencibleActive()) {
            invencible.deactivate();
        }

        // Limpar as listas de entidades para carregar um novo estado.
        // NOTA: Para salvar/carregar as POSIÇÕES e TIPOS de carros/itens, você precisaria
        // estender GameData para serializar essas listas também.
        // Atualmente, ao carregar, o jogo recomeçará com as estatísticas do jogador,
        // mas o "mundo" (carros, itens) será gerado do zero.
        carrosManager.activeCars.clear();
        consertarManager.consertarList.clear();
        slowItems.clear();
        invencibilityItems.clear();

        // Ajustar a velocidade da pista ao carregar
        pista.setSpeed(this.gameCarSpeed);

        System.out.println("Dados do jogo aplicados: PlayerX=" + player.x + ", PlayerY=" + player.y + ", Vida=" + player.playerVida + ", Pontos=" + gamePoints + ", Velocidade=" + gameCarSpeed);
    }

    // Método principal para iniciar a aplicação (o ponto de entrada)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Menu().setVisible(true); // Inicia o jogo com o menu principal
        });
    }
}
