import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;



public class Client{
    DatagramPacket reply;
    ArrayList<Packet> packets = new ArrayList<>();
    int max = Integer.MAX_VALUE;
    PriorityQueue<Packet> q = new PriorityQueue<>();
    Serialization ser = new Serialization();
    boolean terminal = false;
    int i =0;
    String name = new String();


  public void write(String filename, byte[] data){
    try (FileOutputStream output = new FileOutputStream(filename, true)) {
    output.write(data);
    output.close();
    }catch(IOException e)
    {
        System.err.println("IOException " + e);
    }


  }

   byte[] trim(byte[] bytes){
    int i = bytes.length - 1;
    while (i >= 0 && bytes[i] == 0)
    {
        --i;
    }
    byte []b2 = new byte[i+1];
    b2 = Arrays.copyOf(bytes, i + 1);

    return b2 ;
    }

public  Packet receiveSW(DatagramSocket sock){
  try{
    byte[] buffer = new byte[1000];
    reply = new DatagramPacket(buffer, buffer.length);

    sock.receive(reply);
    byte[] data = new byte[1000];
    data = reply.getData();
    try{
      Packet p = (Packet)ser.deserialize(data);
      //sendAck(p.getSeq(),sock);
      return p;
    }catch(ClassNotFoundException e){
        System.err.println("IOException clsl" + e);
    }

  }catch(IOException e){
      System.err.println("IOException ana hna " + e);
  }

  return null;
}

public  Packet receiveSR(DatagramSocket sock){
  try{
    byte[] buffer = new byte[1000];
    reply = new DatagramPacket(buffer, buffer.length);

    sock.receive(reply);
    byte[] data = new byte[1000];
    data = reply.getData();
    try{
      Packet p = (Packet)ser.deserialize(data);
      sendAck(p.getSeq(),sock);
      return p;
    }catch(ClassNotFoundException e){
        System.err.println("IOException clsl" + e);
    }

  }catch(IOException e){
      System.err.println("IOException ana hna " + e);
  }

  return null;
}


  public  void sendAck(int i, DatagramSocket sock){
      try{
        InetAddress host = InetAddress.getByName("localhost");
        Ack k = new Ack();
        k.setSeqno(i);
        byte[] b = ser.serialize(k);
        DatagramPacket  ack = new DatagramPacket(b , b.length , host , reply.getPort());
        sock.send(ack);
        echo("sent ack "+i);

      }catch(IOException e){
          System.err.println("IOException " + e);
      }


    }


  public void SW( DatagramSocket sock){
    i=0;
    while(terminal == false){

      i = i%max;

      Packet p = receiveSW(sock);
      if(p.getData().equals("File Not Found") ){
        echo("Error 404 File Not Found");
        terminal = true;
        break;
      }
      if(p.getSeq() == i){
        packets.add(p);
        if(p.terminated == true){
          terminal = true;

        }
        write(name, p.data);
        i++;
      }
      sendAck(i-1,sock);



    }

  }


  public void SRnew(DatagramSocket sock, int window){
    i=0;
    int last = 0;
    Packet arr[] = new Packet[window];
    for(int x=0; x<window; x++){
      arr[x] = null;
    }
    while(terminal == false){
      //i = i%max;
      Packet p = receiveSR(sock);
      i = p.getSeq();
      if(p.getData().equals("EOF") ){
        echo("file terminated");
        terminal = true;
        break;
      }
      if(p.getData().equals("File Not Found") ){
        echo("Error 404 File Not Found");
        terminal = true;
        break;
      }

      if(arr[i%window] == null){

        arr[i%window] = p;
      }else{
        int x;
        for( x=last; x<window; x++){
          if(arr[x] != null){
            echo("write "+arr[x].getSeq());
            write(name, arr[x].data);
            arr[x] = null;
          }else{
            last = x;
            break;
          }

        }
        if(x == window){
          last =0;
        }
        arr[i%window] = p;
      }
    }
    for(int x=last; x<window; x++){
      if(arr[x] != null){
        echo("write "+arr[x].getSeq());
        write(name, arr[x].data);
        arr[x] = null;
      }

    }
  }

  public void SR( DatagramSocket sock){
    i=0;

    while(terminal == false){
      i = i%max;
      Packet p = receiveSR(sock);
      //sendAck(p.getSeq(), sock);
    //  echo("received "+p.data  + Boolean.toString(p.terminated));

      if(p.getData().equals("EOF") ){
        echo("file terminated");
        terminal = true;
        break;
      }
      q.add(p);

      Packet min = q.poll();
     //echo ("i " + i+" min " + min.getSeq());
      while(min.getSeq()<i && q.size()>0){ //fady elduplicates el2odam

        if(min.written == true){
          min  = q.poll();
        }else{
          q.add(min);
          break;
        }

      }
      if(min.getSeq() == i && min.written == false){ //write kol elpackets elly 3ndy mtrtbeen
        min.written = true;
        echo("write "+min.getSeq());
        write(name, min.data);
          i++;
          while(q.size()>0){
            min = q.poll();
            if(min.getSeq() == i && min.written == false){
              min.written = true;

              echo("write "+min.getSeq());
              write(name, min.data);
                i++;
            }else{
              q.add(min);
              break;
            }
          }
      }
      if(min.getSeq() >i){ //push back out of order packet
        q.add(min);
      }



    }

    while(q.size()>0){
      Packet min = q.poll();
      if(min.written == false){
        min.written = true;
        echo("write "+ min.getSeq());

        write(name, min.data);
      }

    }

  }

  public void GBN( DatagramSocket sock){
    i=0;
    while(terminal == false){
      i = i%max;

      Packet p = receiveSW(sock);
      if(p.getData().equals("File Not Found") ){
        echo("Error 404 File Not Found");
        terminal = true;
        break;
      }
      if(p.getSeq() == i){
        packets.add(p);
        //if(i!=5){
          sendAck(i, sock);
        //}

        if(p.terminated == true){
          terminal = true;

        }
        write(name, p.data);
        i++;
      }
      else if(i !=0){
        sendAck(i-1,sock);
        System.out.println("sent ack "+ (i-1));

      }


    }

  }

  public Request input(BufferedReader cin){
    try{
      echo("Enter message to send : ");
      String s = (String)cin.readLine();
      if(s.equals("bye")){
        return null;
      }
      echo("Enter the algorithm to use ( 1- SW. 		2- SR.		3-GBN) ");
      int alg = Integer.parseInt(cin.readLine());
      echo("Enter PLP to use from 0 to 100: ");
      int prob = Integer.parseInt(cin.readLine());
      //echo("Enter Unique client ID: ");
      //int id = Integer.parseInt(cin.readLine());
      int window = 1;
      if(alg == 2 || alg == 3){
        echo("Enter window size to use: ");
       window = Integer.parseInt(cin.readLine());

      }
      return new Request(s, window, prob, alg);

    }catch(IOException e)
    {
        System.err.println("IOException " + e);
    }
    return null;

  }

  public void run(String args[]) throws Exception{

        DatagramSocket sock = null;
        int port = 7777;
        String s;

        BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));

        try
        {

            InetAddress host = InetAddress.getByName("localhost");

            while(true)
            {
                terminal = false;
                //take input and send the packet
                sock = new DatagramSocket();
                Request r = input(cin);
                r.clientID = sock.getLocalPort();
                if(r== null){
                  break;
                }
                r.generate();
                //echo("Enter message to send : ");
                //s = (String)cin.readLine();
                /*
                byte[] b = s.getBytes();
                byte[] b2 = trim(b);
                String filename = new String(b2);
                if(filename.equals("bye"))
                  break;

                String extensionRemoved = filename.split("\\.")[0];
                String extension = filename.split("\\.")[1];
                 name = extensionRemoved + "out." + extension;
                 */
                 byte[] b = ser.serialize(r);


                DatagramPacket  dp = new DatagramPacket(b , b.length , host , port);
                sock.send(dp);
                name = r.out;
                //now receive reply
                long startTime = System.nanoTime();
                  long elapsedTime;
                switch(r.algorithm){
                  case 1:
                  //Stop and wait
                  echo("sw");

                        SW(sock);
                        elapsedTime = (System.nanoTime() - startTime) / 1000000000;
                        echo("local port "+ sock.getLocalPort());
                        echo("elapsed time SW" + elapsedTime);
                        break;
                  case 2:
                  //Selective Repeat
                  echo("sr");
                        SRnew(sock, r.window);
                        elapsedTime = (System.nanoTime() - startTime) / 1000000000;
                        echo("local port "+ sock.getLocalPort());
                        echo("elapsed time SR" + elapsedTime);

                        break;
                  case 3:
                  //Go Back N
                  echo("GBN");
                        GBN(sock);
                        elapsedTime = (System.nanoTime() - startTime) / 1000000000;
                        echo("local port "+ sock.getLocalPort());
                        echo("elapsed time GBN" + elapsedTime);

                        break;


                }




            }
        }

        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
        sock.close();
    }



    public static void main(String [] args){
      try{
          Client obj = new Client ();
          obj.run (args);
      }
      catch (Exception e){
          e.printStackTrace ();
      }
    }


    //simple function to echo data to terminal

  public static void echo(String msg){
        System.out.println(msg);
    }

}
