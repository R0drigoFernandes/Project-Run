import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.Timer;

public class Player {
    Timer timer = new Timer(10000, null);
    AffineTransform t = new AffineTransform();
    int x,y, width, height;
    public boolean right, left, acelerar, freiar;
    public Player(int x, int y,int width,int height) {
       this.x = x;
       this.y = y;
       this.width = width;
       this.height = height;


    }

    public void tick(){
        if(right){
            x+= 4;
        }
        if(left){
            x-= 4;
        }
        if(acelerar){
            
        y-= 2;

            
        }
        if(freiar){
            y+= 2;
        }


    }

    public void render(Graphics g){
        g.setColor(Color.GREEN);
        g.fill3DRect(x,y,width,height,true);
    }

}
