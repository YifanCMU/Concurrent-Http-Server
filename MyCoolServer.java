
package mycoolserver;

/**
 * a concurrent HTTP server
 * @author Yifan Wang
 */
import java.net.*;
import java.io.*;
import java.util.Scanner;
public class MyCoolServer {
    public static void main(String[] args) {
        while(true){
        Socket clientSocket = null;
        PrintWriter out = null;
        ServerSocket listenSocket = null;
        Scanner filescanner = null;
        try {
            int serverPort = 7777; //port number
            
            listenSocket = new ServerSocket(serverPort);
            clientSocket = listenSocket.accept();
            Scanner in;
            
            in = new Scanner(clientSocket.getInputStream());
            out = new PrintWriter(new BufferedWriter(new
                    OutputStreamWriter(clientSocket.getOutputStream())));
            String line = in.nextLine();
            String filename = line.split("/")[1];            
            File file = new File(filename);
            filescanner = new Scanner(file);
            String output = "HTTP/1.1 200 OK\n";
            while (filescanner.hasNextLine()) {
                String data = filescanner.nextLine();
                //System.out.println(data);
                //content = content+data+"\n";
                out.write(data+"\n");   
            }  
            out.flush();
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
            out.println("HTTP/1.1 404 not found\n");
            out.flush();
        } finally {
            try {if (clientSocket != null) {
                filescanner.close();
                
                listenSocket.close();
                clientSocket.close();
                    
                }
            } catch (IOException e) {
                System.out.println("Exception:" + e.getMessage());
            }

        }
    }
    }
    
    
}
