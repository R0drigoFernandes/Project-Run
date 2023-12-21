import java.awt.Color;
import java.awt.Graphics;
public class Carros extends ProjectRun {
    int x, y, width, height;
    public int car = 0;
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
        if(y < 500){
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }else if(y == 500){
            g.clearRect(x, y, width, height);
            
        }
       
    }
    
}
