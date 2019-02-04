package projet;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

  //Serveur de chat
public class MultiThreadChatServer {
  //Initialiser la socket serveur
  private static ServerSocket serverSocket = null;
  //Initialiser la socket client
  private static Socket clientSocket = null;
  // Ce serveur de tchat peut accepter maxClientsCount clients(10)
  private static final int maxClientsCount = 10;
  private static final clientThread[] threads = new clientThread[maxClientsCount];

  public static void main(String args[]) throws IOException {
    //Le numéro du port
    int portNumber = 6001;
    System.out.println("Serveur de le salon de discussion numéro : " + portNumber);
    serverSocket = new ServerSocket(portNumber);
    //Creation d'un socket client pour chaque connexion et on la passe pour un nouveau thread client 
    while (true) {
        clientSocket = serverSocket.accept();
        int i = 0;
        for (i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null) 
          {
            (threads[i] = new clientThread(clientSocket, threads)).start();
             break;
          }
        }
        if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Serveur plein. Veuillez rééssayer plus tard.");
          os.close();
          clientSocket.close();
        }
    }
  }
}
 //a chaque fois qu'un client se connecte sur le salon de chat on lui demande le nom
 //les clients connectés sur le serveur sont avertis qu'un nouveau client s'est connecté
 //un message envoyé par un client est lus par tous les autres clients
 //les clients connectés sur le serveur sont avertis si un client s'est déconnecté

class clientThread extends Thread {
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
      try {
          int maxClientsCount = this.maxClientsCount;
          clientThread[] threads = this.threads;
          
          //Creation de l'input et de l'output streams pour le client
          is = new DataInputStream(clientSocket.getInputStream());
          os = new PrintStream(clientSocket.getOutputStream());
          //demande du nom de client
          os.println("Entrer votre nom.");
          String name = is.readLine().trim(); //trim(); supprimer les espaces
          //message de bienvnue pour le client
          os.println("Bienvenue " + name+ ".\n Pour quitter entrer /quitter ");
          for (int i = 0; i < maxClientsCount; i++)
          {
              if (threads[i] != null && threads[i] != this)
              {  //message d'avertissement pour les autres clients:un client ayant le nom () s'est connecté
                  threads[i].os.println("*** Le nouveau utilisateur " + name+ "  a entrer dans le salon de discussion !!! ***");
              }
          }

          while (true) {
              String line = is.readLine();
              if (line.contains("/quitter")) { // si la ligne ecrite et envoyé du client contient la commande /quitter
                  break;                       // il se deconnectera
              }
              
              for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null) {
                      threads[i].os.println("<" + name + "> : " + line);
                  }
              }
          }
          
          for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i] != this) {
                  //message d'avertissement pour les autres clients:un client ayant le nom () s'est déconnecté
                  threads[i].os.println("*** L'utilisateur " + name + " a quitté le salon de discussion !!! ***");
              }
          }
          os.println("*** Bye " + name + " ***");
          //si le thread courant a quitté, on l'affecte la valeur null
          //afin qu'un nouveau client puisse etre accepté sur le serveur
          for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] == this) {
                  threads[i] = null;
              }
          }
          
          //Fermer l'output stream, et l'input stream, et la socket.
          is.close();
          os.close();
          clientSocket.close();
      } catch (IOException ex) {
          Logger.getLogger(clientThread.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
}
