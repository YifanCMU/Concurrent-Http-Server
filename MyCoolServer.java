package mycoolserver;
/**
 * a Concurrent HTTP server
 *
 * @author Yifan Wang Please test the server by using web browser to visit
 * "localhost:7777/filename" e.g. "localhost:/7777/MadaData.txt" If the file you
 * want is under the same directory as this piece of code, then the content of
 * the file will be returned to your web browser
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;
public class MyCoolServer implements Runnable{
    Scanner filescanner = null;
    static ServerSocket listenSocket=null;
    static Socket clientSocket = null;
    public static void main(String[] args) {
        new MyCoolServer();
        
    }
    public MyCoolServer() {
        try {
            int serverPort = 7777; //port number
            listenSocket = new ServerSocket(serverPort);
            new Thread(this).start();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    public void run () {
        try{          
            while (true) {

                clientSocket = listenSocket.accept();        
                Scanner in;
                in = new Scanner(clientSocket.getInputStream()); 
                String line = in.nextLine();              
                String filename = line.split("/")[1];
                filename = filename.substring(0, filename.length() - 5); //get file name   
                System.out.println("file name:"+filename);
                if(filename.endsWith(".jpg")||filename.endsWith(".png")){
                    sendPic(filename,clientSocket);
                }
                else{
                    sendText(filename,clientSocket);
                }
                close(clientSocket);
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    //send jpg or png file through socket to browser
    private void sendPic(String filename, Socket client) {
    File file = new File(filename);
    if (file.exists() && !file.isDirectory()) {
      try {
        PrintStream writer = new PrintStream(client.getOutputStream());
        writer.println("HTTP/1.0 200 OK");
        writer.println("Content-Type:application/binary");
        writer.println("Content-Length:" + file.length());
        writer.println();
        FileInputStream fis = new FileInputStream(file);
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
  //send txt, html, css, javacript file over socket to browser
    public void sendText(String filename, Socket client) throws Exception{
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));          
        try{
            File file = new File(filename);
            System.out.println("opened");
            filescanner = new Scanner(file);  
            while (filescanner.hasNextLine()) {
                String data = filescanner.nextLine();
                writer.write(data+"\n");  
            }  
            writer.flush();
        }
        catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());               
            writer.write("HTTP/1.1 404 not found\n");
            writer.write("File does not exist");
            writer.flush();
        } 
        finally{
             if (filescanner != null) {
                    filescanner.close();
            }
        }
    }
    public static void close(Socket clientSoekct){    
        try{       
            clientSocket.close();
        } 
        catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());
        }           
    }
}
