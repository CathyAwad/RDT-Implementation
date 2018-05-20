import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;

public class GBN{

  Serialization ser = new Serialization();
  ArrayList<Packet> packets;
  int plp;

  private  DatagramSocket conn;
  //  private byte[] buffer;
  private DatagramPacket incomin;
  int window;
  public GBN(DatagramSocket conn, ArrayList<Packet> packets, int window, DatagramPacket incomin, int plp){
    this.conn = conn;
    this.packets = packets;
    this.window = window;
    this.incomin = incomin;
    this.plp = plp;
  }
  public void start(){
    new receiveGBN(conn, packets).start();
    for(int i=0; i<packets.size(); i++){

      for(int n=0; n<window; n++){
        if(i+n<packets.size()){
          echo(packets.get(i+n).print());

          if(packets.get(i+n).sent==false){
          //  echo("sending "+ packets.get(i+n).getSeq());
            packets.get(i+n).timed = false;
            sendGBN(packets.get(i+n), conn, incomin, packets);
          }
        }

      }
      new timeGBN(packets.get(i), packets, window).start();

      while((packets.get(i).acknowledged==false) && (packets.get(i).timed == false)){
        System.out.print("");
      }

      if(packets.get(i).acknowledged==false && packets.get(i).timed == true){
        //echo("hna "+ Integer.toString(i));
        i--;
      }
      int flag = 0;
      while(i >= 0 && packets.get(i).acknowledged == true && i<packets.size()){
        flag = 1;
        i++;
      }
      if(flag == 1){
        i--;
      }



    }
  }

  public void sendGBN(Packet p, DatagramSocket conn, DatagramPacket incomin, ArrayList<Packet> packets){
    try{
      byte[] yourBytes = ser.serialize((Object)p);
      DatagramPacket dp = new DatagramPacket(yourBytes, yourBytes.length , incomin.getAddress() , incomin.getPort());

      //generate random probability for dropping files
      Random ran = new Random();
      int x = ran.nextInt(100);
      if(x>=plp){
        conn.send(dp);
        echo("sending "+p.getSeq());
        p.sent = true;
      }
      else{
        echo("wa2a3 "+ p.getSeq());
      }
      //new timeGBN(p, packets, window).start();


    }catch(IOException e)
    {
        System.err.println("IOException " + e);
    }
  }

  public static void echo(String msg){
      System.out.println(msg);
  }



}
