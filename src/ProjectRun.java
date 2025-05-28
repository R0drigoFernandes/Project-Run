import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class ProjectRun extends Canvas implements Runnable, KeyListener {
   
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Pista pista;
    public Player player;
    public Carros carros;
    public Consertar consertar;
    public static int width = 500, height = 500;
    private boolean running = true;
    
    
    public ProjectRun(){
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(width, height));
        pista = new Pista();
        carros = new Carros();
        consertar = new Consertar();
        player = new Player(width/2,400,3, 32, 32, carros, consertar);
    }
    public static void main(String[] args) {
        ProjectRun projectRun = new ProjectRun();
        JFrame frame = new JFrame();
        frame.add(projectRun);
        frame.setTitle("Project Run");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        new Thread(projectRun).start();
    }
    public void tick(){
    player.tick();
    carros.tick(); 
    if(carros.pontos % 10 == 0){
        consertar.tick();
    }
    
    }
    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,width,height);
        pista.render(g);
        carros.render(g);
        
        consertar.render(g);
        player.render(g);
        bs.show();

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_RIGHT){
            player.right = true;
        }
        if(keyCode == KeyEvent.VK_LEFT){
            player.left = true;
        }
        if(keyCode == KeyEvent.VK_UP){
            player.acelerar = true;
        }
        if (keyCode == KeyEvent.VK_DOWN){
            player.freiar = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_RIGHT){
            player.right = false;
        }
        if(keyCode == KeyEvent.VK_LEFT){
            player.left = false;
        }
        if(keyCode == KeyEvent.VK_UP){
            player.acelerar = false;
        }
        if (keyCode == KeyEvent.VK_DOWN){
            player.freiar = false;
        }
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
        else{
            System.exit(0);
        }
    }
    public void gamereset(){
        running = true;
        main(null);
    }
}

