package pkg;
import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.*;
import java.io.IOException;

import pkg.HashMapArr;

public class Main {
  public static void main(String[] args) {
    BufferedReader reader = null;
    // hashmap array to store the frequency per attribute
    HashMapArr[] hmArr = null;
    
    try {
      // Input file which need to be parsed
      String fileToParse = "sampleCSV/" + args[0];
      // Create file reader
      reader = new BufferedReader(new FileReader(fileToParse));
      
      String line;
      List<String> attrs = new ArrayList<String>();

      // Read column attributes
      line = reader.readLine();
      if (line != null) {
        // TODO: need custom parser
        attrs = splitByOuterComma(line);

        // create hashmap array with length
        hmArr = new HashMapArr[attrs.size()];
      }

      // Read the file line by line
      line = reader.readLine();
      int i;
      while (line != null) { 
        // TODO: need custom parser
        List<String> cols = splitByOuterComma(line);

        i = 0;
        for (String col : cols) {
          if (hmArr[i].hm == null) {
            hmArr[i].hm = new HashMap<String, Integer>();
            hmArr[i].hm.put(col, 1);  
          } else {
            Integer ct = hmArr[i].hm.get(col);
            hmArr[i].hm.put(col, (ct == null) ? 1 : ct + 1);
          }
          i++;
        }

        // read next line
        line = reader.readLine();
      }

      // display the occurent of elements per attribute
      int colNum;
      for (HashMapArr hmObj : hmArr) {
        colNum = 1;
        System.out.println("\nAttribute " + (colNum + 1));

        for (Map.Entry<String, Integer> val : hmObj.hm.entrySet()) {
          System.out.println("Element " + val.getKey() + " occurs: " + val.getValue() + " times");
        }

        colNum++;
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("File not found");
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Failed closing the file");
      }
    }
  }

  private static List<String> splitByOuterComma(String s) {
    // TODO: need custom parser, will be replaced
    List<String> temp = new ArrayList<String>();
    int r = 0, l = 0;
    for (int n = s.length(); r < n;) {
      if (s.charAt(r) == ',') {
        temp.add(s.substring(l, r));
        r++;
        l = r;
        
        if (s.charAt(r) == '"') {
          r++;
          while(s.charAt(r) != '"') {
            r++;
          }
          r++;
          temp.add(s.substring(l, r));
          l = r;
        }
      } else {
        r++;
      }
    }
    temp.add(s.substring(l, r-2));
    return temp;
  }
}
