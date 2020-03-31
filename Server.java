import java.io.*;
import java.net.*;

public class Server {

    private ServerSocket ss;
    private int numPlayers;
    private int maxPlayers;

    private Socket p1Socket;
    private Socket p2Socket;
    private ReadFromClient p1ReadRunnable;
    private ReadFromClient p2ReadRunnable;
    private WriteToClient p1WriteRunnable;
    private WriteToClient p2WriteRunnable;

    private int p1x, p1y, p2x, p2y;
    private int b1x, b1y, b2x, b2y;
    private int ponto1, ponto2;

    public Server(){
        System.out.println("===== Servidor =====");
        numPlayers = 0;
        maxPlayers = 2;
        //Define X,Y Inicial Bola
        b1x = -100;
        b1y = -100;
        b2x = -100;
        b2y = -100;
        //Zera Placar
        ponto1 = 0;
        ponto2 = 0;

        //Try catch do construtor do servidor
        try{
            ss = new ServerSocket(45371);
        } catch(IOException ex){
            System.out.println("IOException from Server Contructor");
        }
    }
    //Função para aceitar conexões
    public void acceptConnections(){
        try{
            System.out.println("Aguardando Conexões...");
            //While para limitar jogadores
            while(numPlayers < maxPlayers){
                Socket s = ss.accept();
                DataInputStream in = new  DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                numPlayers++;
                out.writeInt(numPlayers);
                System.out.println("Player #" + numPlayers + "Entrou");

                ReadFromClient rfc = new ReadFromClient(numPlayers, in);
                WriteToClient wtc = new WriteToClient(numPlayers, out);
                //Atribui sockets para jogadores e as variaveis que leem e escrevem 
                if(numPlayers == 1){
                    p1Socket = s;
                    p1ReadRunnable = rfc;
                    p1WriteRunnable = wtc;
                }else{
                    p2Socket = s;
                    p2ReadRunnable = rfc;
                    p2WriteRunnable = wtc;
                    p1WriteRunnable.sendStartMsg();
                    p2WriteRunnable.sendStartMsg();
                    Thread readThread1 = new Thread(p1ReadRunnable);
                    Thread readThread2 = new Thread(p2ReadRunnable);
                    readThread1.start();
                    readThread2.start();
                    Thread writeThread1 = new Thread(p1WriteRunnable);
                    Thread writeThread2 = new Thread(p2WriteRunnable);
                    writeThread1.start();
                    writeThread2.start();
                }

            }
            
            System.out.println("Entradas encerradas! Sala lotada!");

        } catch(IOException ex) {
            System.out.println("IOException from acceptConnections()");
        }
    }
    //Classe que le do cliente
    private class ReadFromClient implements Runnable {
        private int playerID;
        private DataInputStream dataIn;

        public ReadFromClient(int pid, DataInputStream in){
            playerID = pid;
            dataIn = in;
            System.out.println("RFC" + playerID + "Runnable Criado");
        }
        //Thread que le do cliente
        public void run(){
            try{
                while(true){
                    if(playerID ==1){
                        p1x=dataIn.readInt();
                        p1y=dataIn.readInt();
                        b1x=dataIn.readInt();
                        b1y=dataIn.readInt();
                        ponto1=dataIn.readInt();
                    }else{
                        p2x=dataIn.readInt();
                        p2y=dataIn.readInt();
                        b2x=dataIn.readInt();
                        b2y=dataIn.readInt();
                        ponto2=dataIn.readInt();
                    }
                }
            }catch(IOException ex){
                System.out.println("IOException from RFC run()");
              }
        }

    }
    //Classe que escreve no Cliente
    private class WriteToClient implements Runnable {
        private int playerID;
        private DataOutputStream dataOut;

        public WriteToClient(int pid, DataOutputStream out){
            playerID = pid;
            dataOut = out;
            System.out.println("WTC" + playerID + "Runnable Criado");
        }
        //Thread que escreve no cliente
        public void run(){
            try{
                while(true){
                    if(playerID==1){
                        dataOut.writeInt(p2x);
                        dataOut.writeInt(p2y);
                        dataOut.writeInt(b2x);
                        dataOut.writeInt(b2y);
                        dataOut.writeInt(ponto2);
                        dataOut.flush();
                    }else{
                        dataOut.writeInt(p1x);
                        dataOut.writeInt(p1y);
                        dataOut.writeInt(b1x);
                        dataOut.writeInt(b1y);
                        dataOut.writeInt(ponto1);
                        dataOut.flush();
                    }
                    try{
                        Thread.sleep(25);
                    }catch(InterruptedException ex){
                        System.out.println("InterruptedException from WTC run()");
                    }
                }
            }catch(IOException ex){
                System.out.println("IOException from WTC run()");
              }
        }
        //Função criada para identificar quando os dois jogadores entraram e comecar a ler e escrever
        public void sendStartMsg(){
            try{
                dataOut.writeUTF("Agora temos 2 Jogadores, joguem!");
            }catch(IOException ex){
                System.out.println("IOException from sendStartMsg()");
            }
        }

    }

    public static void main(String[] args) {
        Server gs = new Server();
        gs.acceptConnections();
    }

}