import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
public class Carros {
    int x = 0, y = 0, width = 32, height = 32;
    public ArrayList<Carros> carros = new ArrayList<>();
   
    
        
    public Carros(){
         x = (int) (Math.random() * 500);
       if(x > 180 && x < 300){
        carros.add(this);
       }
    }

    public void tick(){
        y+=2;
        if(y > 500){
          carros.remove(this);
        }
    }
    public void render( Graphics g){
        if(y < 500 && x > 180 && x < 300){
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
       
    }
    
    
}
