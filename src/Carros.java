import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
public class Carros {
    int x = 200, y = 0, width = 32, height = 32;
    public ArrayList<Carros> carros = new ArrayList<>();
    
    
        
    public Carros(){  
            carros.add(this);
       
    }

    public void tick(){
        y+=10;
        
        if(y > 500){
          carros.remove(this);
          y = 0;
          carros.add(new Carros());
          x = (int)(Math.random()*(310 - 170 + 1) + 170);
        }
    }
    public void render( Graphics g){
        if(y < 500){
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
       
    }
    
    
}
