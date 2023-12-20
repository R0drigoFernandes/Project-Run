import java.awt.*;
import javax.swing.Timer;

public class Player {
    Timer timer = new Timer(10000, null);
    int x,y;
    public boolean right, left, acelerar, freiar;
    public Player(int x, int y){
       this.x = x;
       this.y = y;


    }

    public void tick(){
        if(right){
            x++;
        }
        if(left){
            x--;
        }
        if(acelerar){
            


            
        }
        if(freiar){
            
        }


    }

    public void render(Graphics g){
        g.setColor(Color.GREEN);
        g.fill3DRect(x,y,32,32,true);
    }

}
