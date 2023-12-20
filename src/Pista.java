import java.awt.Graphics;
import java.awt.Color;
public class Pista {
    public void render(Graphics g) {
        for(int i = 0; i < 500; i+=4) {
            for(int j = 0; j < 500; j+=4) {
                g.setColor(Color.GREEN);
                g.fillRect(j,i,32,32);
            }
            
        }
        
        g.setColor(Color.gray);
        g.fillRect(150,0,200,500);
        
    }
    
}
