import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;

public class Request implements Serializable{
  String fname;
  int window;
  int plp;
  int algorithm;
  int clientID = 0;
  String out = new String();
  public Request(String fname, int window, int plp, int algorithm){
    this.fname = fname;
    this.window = window;
    this.plp = plp;
    this.algorithm = algorithm;
    //this.clientID = clientID;

  }

  public Request(){}

  public void generate(){
    String extensionRemoved = fname.split("\\.")[0];
    String extension = fname.split("\\.")[1];
     this.out = extensionRemoved + "out"+ clientID + "." + extension;
  }

}
