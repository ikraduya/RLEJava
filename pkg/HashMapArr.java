package pkg;
import java.util.HashMap;

public class HashMapArr {
  public HashMap<String, Integer> hm;

  public Integer get(String i) {
    return hm.get(i);
  }

  public void put(String s, Integer i) {
    hm.put(s, i);
  }
}
