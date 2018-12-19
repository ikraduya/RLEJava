package pkg;
import java.io.*;
import java.util.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.*;
import java.io.IOException;

import pkg.HashMapArr;

public class Main {
  public static void main(String[] args) {
    BufferedReader reader = null;

    try {
      // Input file which need to be parsed
      String fileToParse = "pkg/sampleCSV/" + args[0];
      // Create file reader
      reader = new BufferedReader(new FileReader(fileToParse));
      
      String line;
      List<String> attrs = new ArrayList<String>();
      HashMapArr[] hmArr = null;

      // Read column attributes
      line = reader.readLine();
      if (line != null) {
        attrs = Arrays.asList(line.split(","));

        // create hashmap array with length
        hmArr = new HashMapArr[attrs.size()];
      }

      // hashmap array to store the frequency per attribute
      hmArr = parseCSV(attrs, reader);

      // display the occurent of elements per attribute
      int colNum = 1;
      for (HashMapArr hmObj : hmArr) {
        System.out.println("\nAttribute " + (colNum + 1));
        colNum++;

        for (Map.Entry<String, Integer> val : hmObj.hm.entrySet()) {
          System.out.println("Element " + val.getKey() + " occurs: " + val.getValue() + " times");
        }
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

  private static HashMapArr[] parseCSV(List<String> attrs, BufferedReader reader) {
    int attrsCount = attrs.size();
    HashMapArr[] hmArr = new HashMapArr[attrsCount];
    
    try {
      // Read the file line by line
      String line = reader.readLine();
      while (line != null) { 
        String s = line;
        List<String> cols = new ArrayList<String>();

        // split by outer comma
        // using sliding window technique
        int r = 0, l = 0, ct = 0, n = line.length();
        while (ct < attrsCount && r < n) {
          if (r == n-1 && ct == attrsCount - 1) {
            r++;
            break;
          } else if (r > n && ct < attrsCount) {
            line = reader.readLine();
            s = s + line;
            n = n + line.length(); 
          }
          if (s.charAt(r) == ',') {
            cols.add(s.substring(l, r));
            ct++;
            r++;
            l = r;
            
            // handle newline not in the end of line
            while (r == n && ct < attrsCount) {
              line = reader.readLine();
              s = s + line;
              n = n + line.length();
            }
          } else if (s.charAt(r) == '"') {
            r++;
            while(s.charAt(r) != '"') {
              r++;
              // handle newline inside quotes
              while (r == n-1 && ct < attrsCount-1) {
                line = reader.readLine();
                s = s + line;
                n = n + line.length();
              }
            }
            while (r < n && s.charAt(r) != ',') {
              r++;
            }
            cols.add(s.substring(l, r));
            ct++;
            r++;
            l = r;
          } else {
            r++;
          }
        }
        // if last column is empty value
        if (ct < attrsCount) {
          cols.add("");
        }

        countOccurence(hmArr, cols);

        // read next line
        line = reader.readLine();
        // skipping empty line
        while (line != null && line.length() == 0) {
          line = reader.readLine();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("File not found");
    }

    return hmArr;
  }

  private static void countOccurence(HashMapArr[] hmArr, List<String> cols) {
    int colSize = cols.size();
    int i = 0;
    for (String col : cols) {
      if (hmArr[i] == null) {
        hmArr[i] = new HashMapArr();
        hmArr[i].hm = new HashMap<String, Integer>();
        hmArr[i].hm.put(col, 1);  
      } else {
        Integer ct = hmArr[i].hm.get(col);
        hmArr[i].hm.put(col, (ct == null) ? 1 : ct + 1);
      }
      i++;
      if (i == colSize) {
        i = 0;
      }
    }
  }
}
