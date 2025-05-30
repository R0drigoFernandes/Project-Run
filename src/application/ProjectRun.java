package application;

import javax.swing.*;
import entities.Carros;
import entities.Consertar;
import entities.Pista;
import entities.Player;
import entities.Menu;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy; // Adicione este import para verificar o arquivo de save
import java.util.Iterator; // Importe para iterar e remover itens de listas

public class ProjectRun extends Canvas implements Runnable, KeyListener {

    private static final long serialVersionUID = 1L;
    public Pista pista;
    public Player player;
    public Carros carros; // Esta instância agora será o "gerenciador" de carros
    public Consertar consertar; // Esta instância agora será o "gerenciador" de consertos
    public static int width = 500, height = 500;
    private boolean running = false;

    private int carsPassedCount = 0; // Novo contador para controlar a aparição do item de conserto

    public ProjectRun(){
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(width, height));

        // Instancia os componentes do jogo.
        // As instâncias de Carros e Consertar aqui serão "gerenciadores" das listas.
        pista = new Pista();
        carros = new Carros(); // Instância que gerenciará a lista de carros
        consertar = new Consertar(); // Instância que gerenciará a lista de itens de conserto (agora vazia, será preenchida)
        player = new Player(width/2,400,3, 32, 32, carros, consertar); // Passa os gerenciadores
        player.setGameReference(this); // Passa a referência da instância de ProjectRun para o Player

        // Adiciona o primeiro carro e item de conserto ao iniciar o jogo
        carros.activeCars.add(new Carros( (int)(Math.random()*(310 - 150 + 1) + 150), 0));
        // Consertar consertar; // Criar um item de conserto inicial se desejar
        // consertar.consertarList.add(new Consertar((int)(Math.random()*(310 - 150 + 1) + 150), -10));
    }

    // Método para obter os dados atuais do jogo
    public GameData getGameData() {
        return new GameData(
            player.x,
            player.y,
            player.vida,
            carros.pontos,
            carros.acelerar,
            carros.contador,
            carsPassedCount // Salvar o contador de carros passados
        );
    }

    // Método para aplicar os dados carregados ao jogo
    public void applyGameData(GameData data) {
        player.x = data.getPlayerX();
        player.y = data.getPlayerY();
        player.vida = data.getPlayerVida();
        carros.pontos = data.getCarrosPontos();
        carros.acelerar = data.getCarrosAcelerar();
        carros.contador = data.getCarrosContador();
        carsPassedCount = data.getConsertarCount(); // Carrega o contador de carros passados

        // Limpa e recria as listas com um carro e um item de conserto se necessário
        carros.activeCars.clear();
        carros.activeCars.add(new Carros( (int)(Math.random()*(310 - 150 + 1) + 150), 0));

        consertar.consertarList.clear();
        // Se houver lógica de como o item de conserto deve reaparecer após carregar, adicione aqui.
        // Por exemplo, se carsPassedCount estiver no limiar para um consertar, crie um.
        if (carsPassedCount % 3 == 0 && carsPassedCount > 0) { // Se já deveria ter um consertar
             consertar.consertarList.add(new Consertar((int)(Math.random()*(310 - 150 + 1) + 150), -10));
        }
    }


    public void startGame() {
        JFrame frame = new JFrame();
        frame.add(this); // 'this' se refere à instância de ProjectRun
        frame.setTitle("Jogo de Corrida");
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Inicia a thread do jogo
        if (!running) {
             running = true;
             new Thread(this).start();
        }
    }

    public void tick(){
        player.tick();
        // Atualiza todos os carros na lista
        Iterator<Carros> carIterator = carros.activeCars.iterator();
        while (carIterator.hasNext()) {
            Carros c = carIterator.next();
            c.tick(this); // Passa a referência do jogo para o carro
            if (c.remove) {
                // Ao invés de remover e adicionar na mesma iteração,
                // vamos apenas garantir que um novo carro apareça depois que um for 'removido'
                // Esta lógica de "remove" e "adiciona um novo no mesmo lugar" em Carros.java
                // é peculiar. O mais comum é remover e adicionar um novo carro no topo da tela,
                // o que já está implícito no `if(y > 500)` do `Carros.tick()`.
                // A flag `remove` em Carros é mais para quando o carro é colidido.
                // Se um carro é removido por colisão, ele precisa ser substituído por um novo.
                carIterator.remove(); // Remove o carro que foi colidido
                carros.activeCars.add(new Carros((int)(Math.random()*(310 - 150 + 1) + 150), 0)); // Adiciona um novo
            }
        }

        // Atualiza todos os itens de conserto na lista
        Iterator<Consertar> consertarIterator = consertar.consertarList.iterator();
        while (consertarIterator.hasNext()) {
            Consertar item = consertarIterator.next();
            item.tick();
            if (item.remove) {
                consertarIterator.remove(); // Remove o item de conserto que foi pego ou saiu da tela
                // Não adicionamos um novo item de conserto aqui, pois isso é controlado por carPassou()
            }
        }
    }

    // Método chamado quando um carro passa para controlar a aparição do item de conserto
    public void carPassou() {
        carsPassedCount++;
        if (carsPassedCount % 10 == 0) { // Se 3 carros passaram
            // Adiciona um novo item de conserto à lista
            consertar.consertarList.add(new Consertar((int)(Math.random()*(310 - 150 + 1) + 150), -10));
        }
    }


    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        // Desenha os elementos do jogo
        pista.render(g);
        player.render(g);

        // Renderiza todos os itens de conserto na lista
        for(Consertar item : consertar.consertarList){
            item.render(g);
        }
        // Renderiza todos os carros na lista
        for(Carros c : carros.activeCars){
            c.render(g);
        }


        g.dispose();
        bs.show();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            player.right = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            player.left = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_UP){
            player.acelerar = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            player.freiar = true;
        }
        // Adiciona a lógica para salvar/carregar no ProjectRun
        if (e.getKeyCode() == KeyEvent.VK_S) { // Exemplo: Salvar com 'S'
            SaveLoadManager.saveGame(getGameData());
            JOptionPane.showMessageDialog(this, "Jogo Salvo!");
        }
        if (e.getKeyCode() == KeyEvent.VK_L) { // Exemplo: Carregar com 'L'
            GameData loadedData = SaveLoadManager.loadGame();
            if (loadedData != null) {
                applyGameData(loadedData);
                JOptionPane.showMessageDialog(this, "Jogo Carregado!");
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum jogo salvo para carregar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            player.right = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            player.left = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_UP){
            player.acelerar = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            player.freiar = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Add your implementation here
    }

    @Override
    public void run() {
        while(running){
            tick();
            render();
            try {
                Thread.sleep(1000/60);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void GameOver(){
        running = false;
        JOptionPane.showMessageDialog(null, "Game Over");
        if(JOptionPane.showConfirmDialog(null, "Jogar Novamente?")==0){
            gamereset();
        }
        else if(JOptionPane.showConfirmDialog(null, "Voltar ao Menu?")==0){
            SwingUtilities.invokeLater(() -> new Menu());
         // Fecha o jogo e abre o menu
        }else{
            System.exit(0);
        }
    }

    public void gamereset(){
        running = true; // Garante que a thread possa ser reiniciada ou continue
        newGame(); // Chama newGame para resetar o estado do jogo
        // main(null); // Não chame main(null) aqui, isso cria uma nova janela e thread
        if (!running) {
             running = true;
             new Thread(this).start();
        }
    }

    public void newGame(){
        // Resetar para os valores iniciais
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
        carsPassedCount = 0; // Reseta o contador para consertar

        // Limpa e recria as listas de objetos para garantir um estado limpo
        carros.activeCars.clear();
        carros.activeCars.add(new Carros()); // Adiciona o primeiro carro

        consertar.consertarList.clear(); // Limpa a lista de consertar
        // Não adiciona um Consertar() aqui. Ele será adicionado via carPassou()
    }

    public void exitGame(){
        int confirm = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja sair?", "Sair do Jogo", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu());
    }
}