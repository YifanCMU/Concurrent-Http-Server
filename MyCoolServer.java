package mycoolserver;
/**
 * a Concurrent HTTP server
 *
 * @author Yifan Wang Please test the server by using web browser to visit
 * "localhost:7777/filename" e.g. "localhost:/7777/MadaData.txt"
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;
public class MyCoolServer {
    Scanner filescanner = null;
    static ServerSocket listenSocket=null;
    static Socket clientSocket = null;
    public static void main(String[] args) throws Exception{
        MyCoolServer myserver = new MyCoolServer();
        while (true) {
            int serverPort = 7777; //port number
            listenSocket = new ServerSocket(serverPort);   
            clientSocket = listenSocket.accept();        
            Scanner in;
            in = new Scanner(clientSocket.getInputStream()); 
            String line = in.nextLine();              
            String filename = line.split("/")[1];
            filename = filename.substring(0, filename.length() - 5); //get file name   
            System.out.println("file name:"+filename);
            if(filename.endsWith(".jpg")){
                myserver.sendPic(filename,clientSocket);
            }
            else{
                myserver.sendText(filename,clientSocket);
            }
            close();
            
            }
    }
    private void sendPic(String path, Socket client) {
    File fileToSend = new File(path);
    if (fileToSend.exists() && !fileToSend.isDirectory()) {
      try {
        PrintStream writer = new PrintStream(client.getOutputStream());
        writer.println("HTTP/1.0 200 OK");
        writer.println("Content-Type:application/binary");
        writer.println("Content-Length:" + fileToSend.length());
        writer.println();
        FileInputStream fis = new FileInputStream(fileToSend);
        byte[] buf = new byte[fis.available()];
        fis.read(buf);
        writer.write(buf);
        writer.close();
        fis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
   
    public void sendText(String filename, Socket client) throws Exception{
            PrintWriter printwriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));          
            try{
                File file = new File(filename);
                filescanner = new Scanner(file,"UTF-8");                             
                while (filescanner.hasNextLine()) {
                    String data = filescanner.nextLine();
                    printwriter.write(data+"\n");                   
                }               
                //out.flush();
                printwriter.flush();
            }
            catch (IOException e) {
                System.out.println("IO Exception:" + e.getMessage());               
                printwriter.write("HTTP/1.1 404 not found\n");
                printwriter.write("File does not exist");
                printwriter.flush();
            } 
            finally{
                 if (filescanner != null) {
                        filescanner.close();
                }
            }
    }
    public static void close(){    
        try{       
            listenSocket.close();
            clientSocket.close();
        } 
        catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());
        }           
    }
}
