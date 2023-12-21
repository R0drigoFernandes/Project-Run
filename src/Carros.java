import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
public class Carros{
    int x = 200, y = 0, width = 32, height = 32;
    public int acelerar = 3, pontos = 0, contador = 0;
    public boolean remove;
    public ArrayList<Carros> carros = new ArrayList<>();
    
    
        
    public Carros(){  
        
            carros.add(this);
       
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
        
        y += acelerar;
        
        if(y > 500 || remove){
            y = 0;
            carros.remove(this);
            carros.add(new Carros());
            x = (int)(Math.random()*(310 - 150 + 1) + 150);
            contador++;  
          pontos++;
          if(contador == 10){
            if(acelerar <= 15){
              acelerar++;
              contador = 0;
            }
          }
          
        }
    }
   
    public void render( Graphics g){
        
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        
       
    }
    
    
}
