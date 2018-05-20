import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;


public class receiveSR extends Thread{
  DatagramSocket conn;
  ArrayList<Packet> packets;
  DatagramPacket incomin;
  int window;
  boolean finish;
  Serialization ser = new Serialization();

  public receiveSR( DatagramSocket conn, ArrayList<Packet> packets, DatagramPacket incomin, int window, boolean finish){
    this.packets = packets;
    this.conn = conn;
    this.incomin = incomin;
    this.window = window;
    this.finish = finish;
  }

  public void run(){

    int x =0;
    byte[] buf = new byte[500];
    DatagramPacket incom = new DatagramPacket(buf, buf.length);
    try{
      try{
      //  int check = Math.min(window, packets.size());
    while( x < window){
      x=0;
      for(int n= packets.size()-1; n>=packets.size()- window; n--){
        if(packets.get(n).acknowledged == true ){
            x++;
          }


      }
      if(x == window){
        finish = true;
        echo("5las");
        String s = "EOF";
        Packet fin = new Packet(s.getBytes());
        byte[] yourBytes = ser.serialize((Object)fin);
        DatagramPacket dp = new DatagramPacket(yourBytes, yourBytes.length , incomin.getAddress() , incomin.getPort());
        conn.send(dp);
        conn.close();
        return;
      }

            conn.receive(incom);
            Ack a = (Ack)ser.deserialize(incom.getData());
            int d = a.getSeq();
            echo("received" + d);
            packets.get(d).acknowledged = true;



    }

  }catch(IOException e){
    echo("IOFoundException " +e);
  }



  }catch(ClassNotFoundException e){
    echo("ClassNotFoundException " +e);
  }

 }

 public static void echo(String msg)
 {
     System.out.println(msg);
 }



}
