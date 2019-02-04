package projet;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadChatClient implements Runnable {
  //la socket client
  private static Socket clientSocket = null;
  //initialiser l'output stream avec la valeur NULL 
  private static PrintStream os = null;
  //initialiser l'input stream avec la valeur NULL 
  private static DataInputStream is = null;
  //initialiser la buffer reader avec la valeur NULL 
  private static BufferedReader inputLine = null;
  private static boolean closed = false;
  
  public static void main(String[] args) throws IOException {
    //Le port : 6001
    int portNumber = 6001;
    String host = "localhost";
    System.out.println("Bienvenue sur le salon de discussion numéro : " + portNumber);

      //ouvrir la socket client
      clientSocket = new Socket(host, portNumber);
      inputLine = new BufferedReader(new InputStreamReader(System.in));
      //ouvrir l'output stream
      os = new PrintStream(clientSocket.getOutputStream());
      //ouvrir l'input stream
      is = new DataInputStream(clientSocket.getInputStream());
      
    if (clientSocket != null && os != null && is != null) {
        //Creation d'un thread pour lire depuis le serveur
        new Thread(new MultiThreadChatClient()).start();
        while (!closed) {
          os.println(inputLine.readLine().trim()); //trim() permet de supprimer les espaces
        }
        //Fermer l'output stream, et l'input stream, et la socket client.
        os.close();
        is.close();
        clientSocket.close();
    }
  }

  public void run() {
          
      try {
          String responseLine;
          while ((responseLine = is.readLine()) != null) {
              System.out.println(responseLine);
              if (responseLine.indexOf("*** Bye") != -1)
                  break;
          }
          closed = true;
      } catch (IOException ex) {
          Logger.getLogger(MultiThreadChatClient.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
}