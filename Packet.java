import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;



public class Packet implements Serializable, Comparable<Packet>{
//header
short chsum ;
boolean terminated = false;
boolean acknowledged = false;
boolean sent = false;
boolean timed = false;
boolean written = false;
private int seq ;
public int getSeq(){
  return seq;
}
byte[] data; //data
Packet(byte[] data){
  this.data = data;
}
public void setSeqno(int n){
  this.seq = n;
}
public String getData(){
  return new String(data);
}

Packet(){}

String print(){
  return  "seq no "+seq + "Sent "+ Boolean.toString(sent) +"acknowledged "+ Boolean.toString(acknowledged)+"timeout "+ Boolean.toString(timed) ;
}

@Override
public int compareTo( Packet t) {
    if(this.seq < t.seq){
        return -1;
    }else if(this.seq > t.seq){
        return 1;
    }else
        return 0;
}

byte[] trim(byte[] bytes){
    int i = bytes.length - 1;
    while (i >= 0 && bytes[i] == 0)
    {
        --i;
    }

    return Arrays.copyOf(bytes, i + 1);
  }

}
