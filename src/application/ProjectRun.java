package application;

import javax.swing.*;
import entities.Carros;
import entities.Consertar;
import entities.Invencible; // Importe Invencible
import entities.InvencibilityItem; // Importe InvencibilityItem
import entities.Pista;
import entities.Player;
import entities.pauseMenu;
import entities.Menu;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList; // Importe ArrayList
import java.util.Iterator;

public class ProjectRun extends Canvas implements Runnable, KeyListener {

    private static final long serialVersionUID = 1L;
    public pauseMenu menu;
    public Pista pista;
    public Player player;
    public Carros carros;
    public Consertar consertar;
    public Invencible invencible; // Adicione a instância de Invencible
    public ArrayList<InvencibilityItem> invencibilityItems; // Lista para itens de invencibilidade
    public static int width = 500, height = 500;
    private boolean running = false;
    private boolean paused = false;

    private int carsPassedCount = 0;
    private int invencibilitySpawnThreshold = 10; // A cada 20 carros, um item de invencibilidade

    public ProjectRun(){
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(width, height));

        pista = new Pista();
        carros = new Carros();
        consertar = new Consertar();
        invencible = new Invencible(); // Instancie Invencible
        invencibilityItems = new ArrayList<>(); // Inicialize a lista de itens de invencibilidade

        player = new Player(width/2,400,3, 32, 32, carros, consertar, invencible); // Passe invencible para o Player
        player.setGameReference(this);

        carros.activeCars.add(new Carros( (int)(Math.random()*(310 - 150 + 1) + 150), 0));
    }

    public GameData getGameData() {
        return new GameData(
            player.x,
            player.y,
            player.vida,
            carros.pontos,
            carros.acelerar,
            carros.contador,
            carsPassedCount
            // Se quiser salvar o estado da invencibilidade, adicione aqui (active, startTime)
        );
    }

    public void applyGameData(GameData data) {
        player.x = data.getPlayerX();
        player.y = data.getPlayerY();
        player.vida = data.getPlayerVida();
        carros.pontos = data.getCarrosPontos();
        carros.acelerar = data.getCarrosAcelerar();
        carros.contador = data.getCarrosContador();
        carsPassedCount = data.getConsertarCount();

        carros.activeCars.clear();
        carros.activeCars.add(new Carros( (int)(Math.random()*(310 - 150 + 1) + 150), 0));

        consertar.consertarList.clear();
        if (carsPassedCount % 10 == 0 && carsPassedCount > 0) {
             consertar.consertarList.add(new Consertar((int)(Math.random()*(310 - 150 + 1) + 150), -10));
        }
        invencibilityItems.clear(); // Limpa itens de invencibilidade ao carregar
        // Se invencibilidade for persistente, carregue seu estado aqui também
    }

    public void startGame() {
        JFrame frame = new JFrame();
        frame.add(this);
        frame.setTitle("Jogo de Corrida");
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        if (!running) {
             running = true;
             new Thread(this).start();
        }
    }

    public void tick(){
        if (paused) return;

        player.tick();
        Iterator<Carros> carIterator = carros.activeCars.iterator();
        while (carIterator.hasNext()) {
            Carros c = carIterator.next();
            c.tick(this);
            if (c.remove) {
                carIterator.remove();
                carros.activeCars.add(new Carros((int)(Math.random()*(310 - 150 + 1) + 150), 0));
            }
        }

        Iterator<Consertar> consertarIterator = consertar.consertarList.iterator();
        while (consertarIterator.hasNext()) {
            Consertar item = consertarIterator.next();
            item.tick();
            if (item.remove) {
                consertarIterator.remove();
            }
        }

        // Atualiza e remove itens de invencibilidade (NOVO)
        Iterator<InvencibilityItem> invItemIterator = invencibilityItems.iterator();
        while (invItemIterator.hasNext()) {
            InvencibilityItem item = invItemIterator.next();
            item.tick();
            if (item.remove) {
                invItemIterator.remove();
            }
        }

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
        for(Consertar item : consertar.consertarList){
            item.render(g);
        }
        for(InvencibilityItem item : invencibilityItems){ // Renderiza itens de invencibilidade
            item.render(g);
        }
        for(Carros c : carros.activeCars){
            c.render(g);
        }
        player.render(g); // Player renderiza o efeito de invencibilidade

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
        if (e.getKeyCode() == KeyEvent.VK_S) {
            SaveLoadManager.saveGame(getGameData());
            JOptionPane.showMessageDialog(this, "Jogo Salvo!");
        }
        if (e.getKeyCode() == KeyEvent.VK_L) {
            GameData loadedData = SaveLoadManager.loadGame();
            if (loadedData != null) {
                applyGameData(loadedData);
                JOptionPane.showMessageDialog(this, "Jogo Carregado!");
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum jogo salvo para carregar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            togglePause();
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
        // Not used
    }

   @Override
    public void run() {
        while(running){
            if (!paused) {
                tick();
                render();
            }

            try {
                Thread.sleep(1000/60);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void carPassou() {
        carsPassedCount++;
        if (carsPassedCount % 6 == 0) {
            consertar.consertarList.add(new Consertar((int)(Math.random()*(310 - 150 + 1) + 150), -10));
        }
        // Lógica para spawnar item de invencibilidade (NOVO)
        if (carsPassedCount % invencibilitySpawnThreshold == 0 && carsPassedCount > 0) {
            invencibilityItems.add(new InvencibilityItem((int)(Math.random()*(310 - 150 + 1) + 150), -10));
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

        carros.activeCars.clear();
        carros.activeCars.add(new Carros());

        consertar.consertarList.clear();
        invencibilityItems.clear(); // Limpa itens de invencibilidade no reset

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
            menu = new pauseMenu(this);
            menu.setVisible(true);
        } else {
            if (menu != null) {
                menu.dispose();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu());
    }
}