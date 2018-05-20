
import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;


public class timeSR extends Thread{
    Packet p;
    public timeSR(Packet p){
      this.p =p;
    }

    public void run(){
      long startTime = System.nanoTime();
      long elapsedTime = System.nanoTime() - startTime;
      while(elapsedTime<500000000 && p.acknowledged == false){
        //echo("Waiting");
        elapsedTime = System.nanoTime() - startTime;
        if(p.acknowledged == true){
          return;
        }

      }
      if(p.acknowledged == false)
      {
        p.timed = true;
        p.sent = false;
      }
      else{
        return;
      }


    }
  }
