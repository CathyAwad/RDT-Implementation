import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import java.math.*;

public class Serialization{

  public static byte[] serialize(Object obj) throws IOException {
          ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
          ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
          objectStream.writeObject(obj);
          objectStream.flush();
          return byteStream.toByteArray();
      }
  public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
              ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
              ObjectInputStream objectStream = new ObjectInputStream(byteStream);
              return objectStream.readObject();
          }

}
