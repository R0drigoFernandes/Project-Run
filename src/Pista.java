import java.awt.Graphics;
import java.awt.Color;
public class Pista {
    public void render(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(0,0,500,500);
        g.setColor(Color.gray);
        g.fillRect(150,0,200,500);
        
    }
    
}
