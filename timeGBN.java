import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;

public class timeGBN extends Thread{
  Packet p;
  ArrayList<Packet> packets;
  int window;
  public timeGBN(Packet p, ArrayList<Packet> packets, int window){
    this.p =p;
    this.packets = packets;
    this.window = window;
  }

  public void run(){
    //System.out.println("start timer "+ p.getSeq());
    long startTime = System.nanoTime();
    long elapsedTime = System.nanoTime() - startTime;
    while(elapsedTime<500000000 && p.acknowledged == false){
      //echo("Waiting");
      elapsedTime = System.nanoTime() - startTime;
      if(p.acknowledged == true){
        return;
      }
    }
    if(elapsedTime>= 500000000)
    {
      p.timed = true;
      //System.out.println("thread timed out at packet "+ p.getSeq());
      for(int i=p.getSeq(); i<p.getSeq()+window; i++){
        if(i<packets.size()){
          packets.get(i).sent = false;
        }
      }
      //p.sent = false;
    }


  }
}
