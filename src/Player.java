import java.awt.*;


public class Player extends Rectangle{
    
    public int x,y, width, height, vida = 5, spd = 4;
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
                x+= spd;
            }
          
        }
        if(left){
           if(x > 0){
               x-= spd;
           }
        }
        if(acelerar){
            
            if(y > 0){
                y-= spd;
            }

            
        }
        if(freiar){
            if(y < 500 - height){
                y+= spd;
            }
        }


    }

    public void render(Graphics g){
        g.setColor(Color.BLUE);
        g.fill3DRect(x,y,width,height,true);
    }

}
