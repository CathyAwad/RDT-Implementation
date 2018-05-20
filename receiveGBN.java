import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;

public class receiveGBN extends Thread{
  DatagramSocket conn;
  ArrayList<Packet> packets;
  Serialization ser = new Serialization();

  public receiveGBN( DatagramSocket conn, ArrayList<Packet> packets){
    this.packets = packets;
    this.conn = conn;
  }
  public void run(){
    byte[] buf = new byte[500];
    DatagramPacket incom = new DatagramPacket(buf, buf.length);
    int count = 0;
    try{
      try{
          while(packets.get(packets.size()-1).acknowledged == false){
            conn.receive(incom);
            Ack a = (Ack)ser.deserialize(incom.getData());
            int d = a.getSeq();
            System.out.println("received ack "+ d);
            if(count == d){
              count++;
              packets.get(d).acknowledged = true;
            }
            if(count < d){
              for(int n = count; n<=d; n++){

                packets.get(n).acknowledged = true;
              }
              count++;
            }
          }



          }catch(IOException e){
            System.out.println("IOFoundException " +e);
          }



      }catch(ClassNotFoundException e){
        System.out.println("ClassNotFoundException " +e);
      }
  }
}

