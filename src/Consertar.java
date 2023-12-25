import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;

   

public class Consertar {
   int x = 200, y = 0, width = 16, height = 16;
    public boolean remove;
    public ArrayList<Consertar> consertar = new ArrayList<>();
    public int spd = 2;

    
    public Consertar(){  
     
          consertar.add(this);
      
          
    }
    public boolean verificaColisao(Player player){
        if( this.x < player.x + player.width &&
               this.x + this.width > player.x &&
               this.y < player.y + player.height &&
               this.y + this.height > player.y){
            return true;
        }else{
            return false;
        }
    }

    public void tick(){  
        y+= spd;
        if(remove){
           y = 0;
           consertar.remove(this);
           consertar.add(new Consertar());
           x = (int)(Math.random()*(310 - 150 + 1) + 150); 
       }
       if(y > 500 ){ 
           y = 0;
           consertar.remove(this);
           consertar.add(new Consertar());      
           x = (int)(Math.random()*(310 - 150 + 1) + 150); 
}   
}
    
    public void render( Graphics g){
        
            g.setColor(Color.cyan);
            g.fillOval(x, y, width, height);
        
       
    }
    
}
