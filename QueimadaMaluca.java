import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.net.*;

class Queimada extends JFrame {
  private static final long serialVersionUID = 1L;
  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  double t = screenSize.getWidth();
  double x = screenSize.getHeight();
  int k = (int)x;
  int z = (int)t;
  BufferedImage fundo,player1,player2,bola1,bola2;
  Desenho des = new Desenho();
  int m,n;
  int hor = player1.getWidth();
  int vert =  k/2-(player1.getHeight());
  int hor2 = z-(player1.getWidth()*2);
  int vert2 = k/2-(player1.getHeight());
  int pp1=0;
  int pp2=0;
  int bh=-1000;
  int bv=-1000;
  int bh2 =-1000;
  int bv2 = -1000;
  int cd=0;
  int cd2=0;
  JLabel placar1 = new JLabel("0");
  JLabel placar2 = new JLabel("0");
  private Socket socket;
  private int playerID;
  private ReadFromServer rfsRunnable;
  private WriteToServer wtsRunnable;
  private Timer movimentoTimer;
  private Boolean up= false, up2= false,down= false, down2 = false,left = false,left2=false, right = false , right2 = false;
  int speed;

  class Desenho extends JPanel {
    private static final long serialVersionUID = 1L;

    Desenho() {
      try {
        //Tenta carregar as imagens
        setPreferredSize(new Dimension(800,600));
        fundo = ImageIO.read(new File("newcampv2.png"));
        player1 = ImageIO.read(new File("playerjavav5.png"));
        bola1= ImageIO.read(new File("bolav3.png"));
       
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    }
    
    //Desenha imagens da pasta do jogo
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.drawImage(fundo, 0, 0, z, k, this);
      g.drawImage(player1, hor , vert , player1.getWidth(), player1.getHeight(), this);
      g.drawImage(player1, hor2 , vert2 , player1.getWidth(), player1.getHeight(), this);
      g.drawImage(bola1, bh , bv , bola1.getWidth(), bola1.getHeight(), this);
      g.drawImage(bola1, bh2 , bv2 , bola1.getWidth(), bola1.getHeight(), this);
      Toolkit.getDefaultToolkit().sync();
    }
  }

  //Timer para movimentar jogadores
  private void setUpMovimentoTimer(){
      int intervalo = 10;
      ActionListener al = new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          int speed2 = 8;
          if(up){
           speed = -1;
           MoveVert(-speed2);
        }
          if(up2){
            speed = -1;
           MoveVert2(-speed2);
        }
          if(down){
           speed = 1;
           MoveVert(speed2);
        }
          if(down2){
            speed = 1;
           MoveVert2(speed2);
        }
           if(left){
            speed = -1;
            MoveHor(-speed2);
        }
           if(left2){
            speed = -1;
            MoveHor2(-speed2);
        }
           if(right){
            speed = 1;
            MoveHor(speed2);
        }
           if(right2){
            speed = 1;
            MoveHor2(speed2);
        }    
          repaint();
        }
      };
      movimentoTimer = new Timer(intervalo, al);
      movimentoTimer.start();
  }
  
  //Key Listener para movimentação
  private void setUpKeyListener(){
    KeyListener kl = new KeyListener(){
      public void keyTyped(KeyEvent ke) {}
    
      public void keyPressed(KeyEvent ke) {
        int KeyCode = ke.getKeyCode();
        switch(KeyCode){
          case KeyEvent.VK_UP:
          if(playerID==1){
            up = true;
          }
          if(playerID==2){
            up2 = true;
          }
            break;

          case KeyEvent.VK_DOWN:
          if(playerID==1){
            down = true;
          }
          if(playerID==2){
            down2 = true;
          }
            break;

          case KeyEvent.VK_LEFT:
          if(playerID==1){
            left = true;
          }
          if(playerID==2){
            left2 = true;
          }
            break;

          case KeyEvent.VK_RIGHT:
          if(playerID==1){
            right = true;
          }
          if(playerID==2){
            right2 = true;
          }
            break;
        }
      }
      //KeyEvent para quando soltar tecla jogador parar.
      public void keyReleased(KeyEvent ke) {
        int KeyCode = ke.getKeyCode();
        switch(KeyCode){
          case KeyEvent.VK_UP:
          if(playerID==1){
            up = false;
          }
          if(playerID==2){
            up2 = false;
          }
            break;

          case KeyEvent.VK_DOWN:
          if(playerID==1){
            down = false;
          }
          if(playerID==2){
            down2 = false;
          }
            break;

          case KeyEvent.VK_LEFT:
          if(playerID==1){
            left = false;
          }
          if(playerID==2){
            left2 = false;
          }
            break;

          case KeyEvent.VK_RIGHT:
          if(playerID==1){
            right = false;
          }
          if(playerID==2){
            right2 = false;
          }
            break;
        }
      }
    };
    addKeyListener(kl);
    setFocusable(true);
  }
  
  //Set X,Y Players
  public void setHor(int n){
    hor = n;
  }
  public void setVert(int n){
    vert = n;
  }
  public void setHor2(int n){
    hor2 = n;
  }
  public void setVert2(int n){
    vert2 = n;
  }

  //Condições para mover jogadores apenas dentro de sua área 
  public void MoveHor(int n){
    int es = z-z+15;
    int di =z/2-(player1.getWidth());
    if(hor>es && speed<0)
    hor+=n;
    if(hor<=di && speed>0) 
    hor+=n; 
  }
  public void MoveVert(int n){
    int ci= k-k+15;
    int ba=k-15-(player1.getHeight());
    if(vert>ci && speed<0) 
    vert += n;
    if(vert<ba && speed>0)
    vert += n;
  }
  public void MoveHor2(int n){
    int di =z-player1.getWidth()-15;
    int es = z/2;
    if(hor2>es && speed<0)
    hor2+=n;
    if(hor2<=di && speed>0) 
    hor2+=n; 
  }
  public void MoveVert2(int n){
    int ci= k-k+15;
    int ba=k-15-(player1.getHeight());
    if(vert2>ci && speed<0) 
    vert2 += n;
    if(vert2<ba && speed>0)
    vert2 += n;
  }

  //Set X,Y Bolas 
  public void setBh(int n){
    bh = n;
  }
  public void setBv(int n){
    bv = n;
  }
  public void setBh2(int n){
    bh2 = n;
  }
  public void setBv2(int n){
    bv2 = n;
  }

  //Retorna X,Y Players
  public int getHor(){
    return hor;
  }
  public int getVert(){
    return vert;
  }
  public int getHor2(){
    return hor2;
  }
  public int getVert2(){
    return vert2;
  }

  //Retorna X,Y Bolas
  public int getBh(){
    return bh;
  }
  public int getBv(){
    return bv;
  }
  public int getBh2(){
    return bh2;
  }
  public int getBv2(){
    return bv2;
  }

  //Set e Get Contadores Placar
  public void setPlacar1(int n){
    pp1 = n;
  }
  public void setPlacar2(int n){
    pp2 = n;
  }

  //Retorna os placares 1 e 2
  public int getPlacar1(){
    return pp1;
  }
  public int getPlacar2(){
    return pp2;
  }

  //Função para se conectar no servidor, le PlayerID instancia ReadFromServer e WriteToServer
  private void connectToServer(){
    try{
        socket = new Socket("localhost", 45371);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        playerID = in.readInt();
        System.out.println("Você é o jogador #"+ playerID);
        if(playerID ==1){
          System.out.println("Esperando seu oponente conectar...");
        }
        rfsRunnable = new ReadFromServer(in);
        wtsRunnable = new WriteToServer(out);
        rfsRunnable.waitForStartMsg();
    }catch(IOException ex){
      System.out.println("IOException from connectToServer()");
    }
}

public void abreJanela(){
  this.setTitle("Cliente");
  this.setExtendedState(MAXIMIZED_BOTH);
  this.setUndecorated(true);
  setDefaultCloseOperation(EXIT_ON_CLOSE);
  add(placar1);
  add(placar2);
  add(des);
  setVisible(true);
  setUpMovimentoTimer();
  setUpKeyListener();
}

  Queimada() {
    //Define JLabel do Placar
    placar1.setFont(new Font("Arial", Font.BOLD, 70));
    placar1.setForeground(Color.black);
    placar1.setBounds(player1.getWidth()-bola1.getWidth(), player1.getHeight(), 90, 55);
    placar2.setFont(new Font("Arial", Font.BOLD, 70));
    placar2.setForeground(Color.black);
    placar2.setBounds(z-(player1.getWidth()), player1.getHeight(), 90, 55);
    

 
    //Thread para arremessar bola p1
    Thread atira=new Thread(){
      public void run(){
        bh=hor+player1.getWidth();
        bv=vert+player1.getHeight()/4;
        repaint();
        while(bh<1920){
          try {
            bh=bh+10;
            repaint();
            //Colissão
            if((bh+bola1.getWidth()>=hor2) && (bh+bola1.getWidth())<(hor2+(player1.getWidth()+bola1.getWidth()))){
              if((bv+bola1.getHeight()>=vert2) && (bv+bola1.getHeight())<(vert2+(player1.getHeight()+bola1.getHeight()))){
              pp1++;
              bh=1920;
              }
            }
            sleep(5);
          }catch (InterruptedException ex){}
        }
        cd=0;
        repaint();
      }
    };

    //Thread para arremessar bola p2
    Thread atira2=new Thread(){
      public void run(){
        bh2=hor2-player1.getWidth();
        bv2=vert2+player1.getHeight()/4;
        repaint();
        while(bh2>(0-bola1.getWidth())){
          try {
            bh2=bh2-10;
            repaint();
            //Colissão
            if((bh2-bola1.getWidth()<=hor) && (bh2-bola1.getWidth())>(hor-(player1.getWidth()-bola1.getWidth()))){
              if((bv2+bola1.getHeight()>=vert) && (bv2+bola1.getHeight())<(vert+(player1.getHeight()+bola1.getHeight()))){
              pp2++;
              if(pp2==5){
                JOptionPane.showMessageDialog(null, "JOGAR 2 GANHOU!!");
                System.exit(0);
              }
              bh2=(0-bola1.getWidth());
              }
            }
            sleep(5);
          }catch (InterruptedException ex){}
        }
        cd2=0;
        repaint();
      }
    };

    //KeyAdapter para lanca bola
    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e){
        switch (e.getKeyCode()){
     
        case KeyEvent.VK_SPACE:
        if(playerID==1){
          if(cd==0){
            cd=100;
            new Thread(atira).start();
          }
        }
        if(playerID==2){
          if(cd2==0){
            cd2=100;
            new Thread(atira2).start();
          }
        }
          break;   
        }
        repaint();
      }
    });

  }

  //Classe para ler dados do servidor
  private class ReadFromServer implements Runnable{
      private DataInputStream dataIn;
      public ReadFromServer(DataInputStream in){
        dataIn = in;
        System.out.println("RFS Runnable Criado");
      } 
      //Thread que le dados do servidor
      public void run(){
        try{
          while(true){
            if(playerID==1){ 
              if(pp1==5){
                JOptionPane.showMessageDialog(null, "JOGAR 1 GANHOU!!");
                System.exit(0);
              }  
              if(pp2==5){
                JOptionPane.showMessageDialog(null, "JOGAR 2 GANHOU!!");
                System.exit(0);
              }
            setHor2(dataIn.readInt());
            setVert2(dataIn.readInt());
            setBh2(dataIn.readInt());
            setBv2(dataIn.readInt());
            setPlacar2(dataIn.readInt());
            placar2.setText(String.valueOf(pp2));
            placar1.setText(String.valueOf(pp1));
            repaint();
            }
            if(playerID==2){
              if(pp2==5){
                JOptionPane.showMessageDialog(null, "JOGAR 2 GANHOU!!");
                System.exit(0);
              }
              if(pp1==5){
                JOptionPane.showMessageDialog(null, "JOGAR 1 GANHOU!!");
                System.exit(0);
              }  
            setHor(dataIn.readInt());
            setVert(dataIn.readInt());
            setBh(dataIn.readInt());
            setBv(dataIn.readInt());
            setPlacar1(dataIn.readInt());
            placar1.setText(String.valueOf(pp1));
            placar2.setText(String.valueOf(pp2));
            repaint();
            }
        }
        }catch(IOException ex){
          System.out.println("IOException from RFS run()");
        }
      }

      //Funcao para comeca a ler e escrever no servidor apenas quando 2 conexões estão estabelecidas
      public void waitForStartMsg(){
        try{
          String startMsg = dataIn.readUTF();
          System.out.println("Mensagem do Servido: " + startMsg);
          Thread readThread = new Thread(rfsRunnable);
          Thread writThread = new Thread(wtsRunnable);
          readThread.start();
          writThread.start();
        }catch(IOException ex){
          System.out.println("IOException from waitForStartMsg()");
        }
      }

  }

    //Classe para escrever no servidor
  private class WriteToServer implements Runnable{
    private DataOutputStream dataOut;
    public WriteToServer(DataOutputStream out){
      dataOut = out;
      System.out.println("WTS Runnable Criado");
    } 
    //Thread que escreve no servidor
    public void run(){
      try{
                
        while(true){
            if(playerID ==1){
            dataOut.writeInt(getHor());
            dataOut.writeInt(getVert());
            dataOut.writeInt(getBh());
            dataOut.writeInt(getBv());
            dataOut.writeInt(getPlacar1());
            dataOut.flush();
            }
            if(playerID==2){
              dataOut.writeInt(getHor2());
              dataOut.writeInt(getVert2());
              dataOut.writeInt(getBh2());
              dataOut.writeInt(getBv2());
              dataOut.writeInt(getPlacar2());
              dataOut.flush();
            }
            try{
              Thread.sleep(25);
            }catch(InterruptedException ex){
              System.out.println("InterruptedException from WTS run()");
            }
        }

    }catch(IOException ex){
        System.out.println("IOException WTS run()");
    }
    }

}

  static public void main(String[] args) {
    Queimada f = new Queimada();
    f.connectToServer();
    f.abreJanela();
    
  }
}