import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;

   

public class Consertar extends Carros {
   int x = 200, y = 0, width = 32, height = 32;
    public boolean remove;
    public ArrayList<Consertar> consertar = new ArrayList<>();
    

    
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
       y++;
       
       if(y > 500 || remove){
           y = 0;
           consertar.remove(this);
           
              if(contador == 100){
                  consertar.add(new Consertar());
                  contador = 0;
              }
           
           
           
           x = (int)(Math.random()*(310 - 150 + 1) + 150);  

       }
    }
    public void render( Graphics g){
        
            g.setColor(Color.cyan);
            g.fillRect(x, y, width, height);
        
       
    }
    
}
