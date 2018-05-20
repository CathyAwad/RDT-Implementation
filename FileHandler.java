import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;

public class FileHandler {
  ArrayList<Packet> packets;
  byte[] fname;
  String filename = new String();
  byte[] data;
  int max = Integer.MAX_VALUE;

  public FileHandler(ArrayList<Packet> packets, byte[]fname){
    this.packets = packets;
    this.fname = fname;
  }

  public void start(){
    getFname();
    readBinaryFile();
    divideFile(packets);
  }

  public void getFname(){
    String file = new String(fname);
    echo(file);
    int i = 0;
    //int hmda = 0;
    for( i =0; i<file.length(); i++){
      if(file.charAt(i) == '\0'){

        break;
      }
    }
    //System.out.println("hamaadsaaaaaa"+hmda);
    filename = file.substring(0,i);

  }

  public void readBinaryFile(){
    try{

      File file = new File(filename);
      InputStream insputStream = new FileInputStream(file);
      long length = file.length();
      data = new byte[(int) length];
      insputStream.read(data);
      insputStream.close();
      }catch(Exception e){
        System.out.println("Error is:" + e.getMessage());
      }
  }

  public byte[] trim(byte[] bytes){
      int i = bytes.length - 1;
      while (i >= 0 && bytes[i] == 0)
      {
          --i;
      }

      return Arrays.copyOf(bytes, i + 1);
    }

  public void divideFile(ArrayList<Packet> packets){
    int l = data.length;
    int i=0;
    for(i=0; i< l/100; i++){
      byte[] b = new byte[100];
      int j=0;
      for(j=0; j< 100; j++){
        b[j] = data[i*100 +j];

      }

      Packet p = new Packet(b);
        echo("adding sequence");
      p.setSeqno(i%max);
      packets.add(p);
    }
    if(l%100 !=0){
      byte[] b = new byte[100];
      int j=0;
      for(j=0; j< l%100; j++){
        b[j] = data[i*100 +j];

      }
      byte[] b2 = trim(b);
      Packet p = new Packet(b2);
      echo("terminated file");
      p.setSeqno(i%max);
      p.terminated = true;
      echo("terminated file");
      packets.add(p);
    }
    packets.get(packets.size()-1).terminated = true;
  }


  //simple function to echo data to terminal
  public static void echo(String msg)
  {
      System.out.println(msg);
  }
}
