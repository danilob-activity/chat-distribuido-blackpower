import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Servidor extends Thread {

private static ArrayList<BufferedWriter>clientes;           
private static ServerSocket server; 
private String nome;
private Socket con;
private InputStream in;  
private InputStreamReader inr;  
private BufferedReader bfr;

/**
  * Construtor 
  * @param com do tipo Socket
  */
  public Servidor(Socket con){
    this.con = con;
    try {
          in  = con.getInputStream();
          inr = new InputStreamReader(in);
          bfr = new BufferedReader(inr);
    } catch (IOException e) {
           e.printStackTrace();
    }                          
 }

 /**
  * Método run
  */
public void run(){
                       
    try{
                                        
      String msg;
      OutputStream ou =  this.con.getOutputStream();
      Writer ouw = new OutputStreamWriter(ou);
      BufferedWriter bfw = new BufferedWriter(ouw); 
      clientes.add(bfw);
      nome = msg = bfr.readLine();
      //System.out.println(nome);
      getCurrentTime();
      sendToAll(null, getCurrentTime()+"... "+ msg+" entrou no chat!");
                 
      while(!"Sair".equalsIgnoreCase(msg) && msg != null)
        {           
         msg = bfr.readLine();
         sendToAll(bfw, msg);
         System.out.println(msg);                                              
         }
         sendToAll(null, getCurrentTime()+"... "+ nome+" saiu do chat!");
         clientes.remove(bfw);
                                        
     }catch (Exception e) {
       e.printStackTrace();
      
     }                       
  }

  /***
 * Método usado para enviar mensagem para todos os clients
 * @param bwSaida do tipo BufferedWriter
 * @param msg do tipo String
 * @throws IOException
 */
public void sendToAll(BufferedWriter bwSaida, String msg) throws  IOException 
{
  BufferedWriter bwS;
    
  for(BufferedWriter bw : clientes){
   bwS = (BufferedWriter)bw;
   if(!("Sair".equalsIgnoreCase(msg)&&(bwSaida == bwS))){
    bw.write("("+getCurrentTime()+") "+ nome + "\n   -> " + msg+"\r\n");
     bw.flush(); 
   }
  }          
}
public void sendToAllServer(BufferedWriter bwSaida, String msg) throws IOException {

  for (BufferedWriter bw : clientes) {
          bw.write(msg + "\n");
          bw.flush();   
  }
}


public String getCurrentTime(){
  Calendar calendar = Calendar.getInstance();
  SimpleDateFormat fomatter = new SimpleDateFormat("HH:mm:ss");
  System.out.println(fomatter.format(calendar.getTime()));
  return ""+fomatter.format(calendar.getTime());
}

/***
   * Método main
   * @param args
   */
  public static void main(String []args) {
    
    try{
      //Cria os objetos necessário para instânciar o servidor
      JLabel lblMessage = new JLabel("Porta do Servidor:");
      JTextField txtPorta = new JTextField("3535");
      Object[] texts = {lblMessage, txtPorta };  
      JOptionPane.showMessageDialog(null, texts);
      server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
      clientes = new ArrayList<BufferedWriter>();
      JOptionPane.showMessageDialog(null,"Servidor ativo na porta: "+         
      txtPorta.getText());
      
       while(true){
         System.out.println("Aguardando conexão...");
         Socket con = server.accept();
         System.out.println(" conectado...");
         Thread t = new Servidor(con);
          t.start();   
      }
                                
    }catch (Exception e) {
      
      e.printStackTrace();
    }                       
   }// Fim do método main                      
  } //Fim da classe
