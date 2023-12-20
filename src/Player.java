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
            if(x < 500 - width){
                x+= 4;
            }
          
        }
        if(left){
           if(x > 0){
               x-= 4;
           }
        }
        if(acelerar){
            
            if(y > 0){
                y-= 4;
            }

            
        }
        if(freiar){
            if(y < 500 - height){
                y+= 4;
            }
        }


    }

    public void render(Graphics g){
        g.setColor(Color.GREEN);
        g.fill3DRect(x,y,width,height,true);
    }

}
