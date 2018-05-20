import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;

public class SR{

  Serialization ser = new Serialization();
  ArrayList<Packet> packets;

  private  DatagramSocket conn;
  //  private byte[] buffer;
  private DatagramPacket incomin;
  int window;
  Boolean finishSR = false;
  int plp;

  public SR(DatagramSocket conn, ArrayList<Packet> packets, int window, DatagramPacket incomin, int plp){
    this.conn = conn;
    this.packets = packets;
    this.window = window;
    this.incomin = incomin;
    this.plp = plp;
  }

  public void start(){
    if(packets.size()<window){
      window = packets.size();
    }
    new receiveSR(conn, packets, incomin, window, finishSR).start();
    for(int i=0; i<packets.size(); i++){
      //int n = 5;
      for(int n=0; n<window; n++){
      if(i+n < packets.size()){
        //echo(packets.get(i+n).print());

      if(packets.get(i+n).sent==false){
        //echo("sending "+ packets.get(i+n).getSeq());
        packets.get(i+n).timed = false;
        sendSR(packets.get(i+n), conn, incomin);
      }
    }
    }


      while((packets.get(i).acknowledged==false) && (packets.get(i).timed == false)){
        System.out.print("");
      }
    if(packets.get(i).acknowledged==true){

        while(i >= 0 && i<packets.size() && packets.get(i).acknowledged == true ){
          i++;

        }
      }
      if(i< packets.size()){
        if(packets.get(i).sent == false){
          i--;
        }
        if(i == packets.size()-window && finishSR == false){
          i--;
        }
        if(finishSR == true){
          i++;
        }
      }




      //echo("tele3 ya basharrr lololololollllllyyyyyyyyyy");



    }

  }





  public void sendSR(Packet p, DatagramSocket conn, DatagramPacket incomin){
      try{
      byte[] yourBytes = ser.serialize((Object)p);
      DatagramPacket dp = new DatagramPacket(yourBytes, yourBytes.length , incomin.getAddress() , incomin.getPort());

      //generate random probability for dropping files
      Random ran = new Random();
      int x = ran.nextInt(100);
      echo("sending "+p.getSeq());
      new timeSR(p).start();

      if(x>=plp){
        conn.send(dp);
        p.sent = true;
      }
      else{
        echo("wa2a3 "+ p.getSeq());
      }
      //new receiveSR(conn).start();

      //new receiveAck(p, conn, incomin).start();
      return;
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
