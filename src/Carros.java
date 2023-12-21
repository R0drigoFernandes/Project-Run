import java.awt.Color;
import java.awt.Graphics;
public class Carros {
    int x, y, width, height;

    public Carros(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void tick(){
        y+=2;
    }
    public void render( Graphics g){
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }
    
}
