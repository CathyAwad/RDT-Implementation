import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;

public class SW{

  Serialization ser = new Serialization();
  ArrayList<Packet> packets;

  private  DatagramSocket conn;
  //  private byte[] buffer;
  private DatagramPacket incomin;
  int plp;

  public SW(DatagramSocket conn, ArrayList<Packet> packets,  DatagramPacket incomin, int plp){
    this.conn = conn;
    this.packets = packets;
    this.incomin = incomin;
    this.plp = plp;
  }

  public void start(){
    for(Packet p:packets){
      sendSW(p, conn, incomin);
            }

  }

  public void sendSW(Packet p, DatagramSocket conn, DatagramPacket incomin){
      try{
      byte[] yourBytes = ser.serialize((Object)p);
      DatagramPacket dp = new DatagramPacket(yourBytes, yourBytes.length , incomin.getAddress() , incomin.getPort());

      //generate random probability for dropping files
      Random ran = new Random();
      int x = ran.nextInt(100);
      if(x>= plp){
        conn.send(dp);
      }else{
        echo("wa2a3 "+p.getSeq());
      }
      byte[] buf = new byte[500];
      DatagramPacket incom = new DatagramPacket(buf, buf.length);
      echo(" Waiting for Acknowledgement... " +p.getSeq() );
      conn.setSoTimeout(500);
      try{
        conn.receive(incom);
        try{
          Ack a = (Ack)ser.deserialize(incom.getData());
          int n = a.getSeq();
          while(n != p.getSeq()){
          //  echo("received");
          conn.receive(incom);
          a = (Ack)ser.deserialize(incom.getData());
          n = a.getSeq();
          }

        }catch(ClassNotFoundException e){
          echo("ClassNotFoundException " +e);
        }

      }catch (SocketTimeoutException e) {
         // resend
         sendSW(p, conn, incomin);
         return;

      }

    }catch(IOException e)
    {
        System.err.println("IOException " + e);
    }


  }


  public static void echo(String msg)
  {
      System.out.println(msg);
  }




}
