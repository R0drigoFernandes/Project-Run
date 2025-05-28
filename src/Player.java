import java.awt.*;




public class Player{
   
    public Carros carros ;
    public Consertar consertar;
    public int x,y, width, height, vida , spd = 4;
    public boolean right, left, acelerar, freiar;
    
    public Player(int x, int y, int vida,int width,int height,Carros carros,Consertar consertar) {
       this.x = x;
       this.y = y;
       this.vida = vida;
       this.width = width;
       this.height = height;
       this.carros = carros;
       this.consertar = consertar;
    }
    public void perderVida(){
            vida--;
        if (vida <= 0){
            ProjectRun game = new ProjectRun();
            game.GameOver();
        }
        
       
    }

    public void tick(){
       boolean pegar = consertar.verificaColisao(this);
        boolean colisao = carros.verificaColisao(this);
        if(pegar){
            vida = 3;
            consertar.remove = true;
        }else{
            consertar.remove = false;
        }
        if(colisao){
            perderVida();
            carros.remove = true;
        }else{
            carros.remove = false;
        }
        if(right){
            if(x < 315){
                x+= spd;
            }
          
        }
        if(left){
           if(x > 150){
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
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString("Vida: " + vida, 10, 20);
        g.drawString("Pontos: " + carros.pontos, 10, 40);
        g.setColor(Color.BLUE);
        g.fill3DRect(x,y,width,height,true);
    }

}
