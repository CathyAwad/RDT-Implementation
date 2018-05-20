import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;


public class Ack implements Serializable{
  private int seq ;
  public int getSeq(){
    return seq;
  }
  public void setSeqno(int n){
    seq = n;

  }


}
