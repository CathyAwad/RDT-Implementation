import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;

public class Server{



  static byte[] data ;
  static byte[] data2;
  static String filename ;
  static byte[] fname;
  static byte[] buffer ;
  static DatagramPacket incoming ;
  static int max = Integer.MAX_VALUE; //to assign packet number
  static Serialization ser = new Serialization();


//thread to handle each client

  private static class handle extends Thread{
    ArrayList<Packet> packets;

    private  DatagramSocket conn;
    //  private byte[] buffer;
    private DatagramPacket incomin;
    handle(int n, DatagramPacket incomin ){
      try{
        this.incomin = new DatagramPacket(incomin.getData(), incomin.getData().length, incomin.getAddress(), incomin.getPort());
        conn = new DatagramSocket(n);


      }catch(IOException e)
      {
          System.err.println("IOException " + e);
      }

    }
    public void run(){
      echo("wa7ed gdeed");
      packets = new ArrayList<>();
      data = new byte[1000];
      filename =  new String();
      fname  = new byte[1000];
      byte[] buff = new byte[1000];
      DatagramPacket incoming2 = new DatagramPacket(buff, buff.length);
      Request r = new Request();
      try{
        try{
          r = (Request)ser.deserialize(incomin.getData());

        }catch(IOException e){
          echo("ioexception");
        }
      }catch(ClassNotFoundException e){
        echo("class not found exception");
      }

      int window = r.window;
      int plp = r.plp;
      int algorithm = r.algorithm;

      //fname = incomin.getData();
      try{
        new FileHandler(packets, r.fname.getBytes()).start();
        switch(r.algorithm){
          case 1:
          //Stop and Wait

              new SW(conn, packets, incomin, plp).start();

              break;
          case 2:
          //Selective Repeat
              new SR(conn, packets, window, incomin, plp).start();

              break;
          case 3:
          //Go Back N
              new GBN(conn, packets, window, incomin, plp).start();
              break;



        }


      }catch(Exception e){
        echo("file not found");
        String s = "File Not Found";
        try{
          Packet fin = new Packet(s.getBytes());
          byte[] yourBytes = ser.serialize((Object)fin);
          DatagramPacket dp = new DatagramPacket(yourBytes, yourBytes.length , incomin.getAddress() , incomin.getPort());
          conn.send(dp);
          conn.close();
        }catch(IOException m){
          echo("IOException ");
        }



      }








    //  conn.close();

    }
  }


  public static void main(String args[])
    {
        DatagramSocket sock = null;
        try{
            //1. creating a server socket, parameter is local port number
            sock = new DatagramSocket(7777);

            //2. Wait for an incoming data
            echo("Server socket created. Waiting for incoming data...");

            //communication loop
            int s = 7778;
            while(true)
            {

              buffer = new byte[1000];
              incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);
                echo("Connected!");
                //create thread to handle each client connection
                new handle(s, incoming).start();
                s++;


            }
        }

        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
        sock.close();
    }

    //simple function to echo data to terminal
    public static void echo(String msg)
    {
        System.out.println(msg);
    }


}
