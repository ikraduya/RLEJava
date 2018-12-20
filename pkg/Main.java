package pkg;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.*;

import pkg.HashMapArr;

public class Main {
  public static void main(String[] args) {
    System.out.println("RLE-ing...");

    BufferedReader reader = null;
    List<String> attrs = new ArrayList<String>();
    HashMapArr[] hmArr = null;

    try {
      // Input file which need to be parsed
      String fileToParse = args[0];
      // Create file reader
      reader = new BufferedReader(new FileReader(fileToParse));
      
      String line;
      // Read column attributes
      line = reader.readLine();
      if (line != null) {
        // Assumption: there are no newline inside attributes line
        attrs = Arrays.asList(line.split(","));

        // create hashmap array with length of attributes
        hmArr = new HashMapArr[attrs.size()];
      }

      // long startTime = System.nanoTime();
      // hashmap array to store the frequency per attribute
      hmArr = parseCSV(attrs.size(), reader);
      // long stopTime = System.nanoTime();
      // System.out.println( "Parse time: " + ((float)(stopTime - startTime) / 1000000) + " ms" );


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

      // long startTime = System.nanoTime();
      writeOutFiles(args[0], attrs, hmArr);
      // long stopTime = System.nanoTime();
      // System.out.println( "Write time: " + ((float)(stopTime - startTime) / 1000000) + " ms" );
      System.out.println("done");
    }
  }

  private static void writeOutFiles(String filePathStr, List<String> attrs, HashMapArr[] hmArr) {
    try {
      Path filePath = Paths.get(filePathStr);
      String dirPath = filePath.getParent().toString();
      String fileName = (filePath.getFileName().toString()).replace(".csv", "");

      int colNum = 0;
      for (HashMapArr hmObj : hmArr) {
        String newFilePath = dirPath + "/" + fileName + "-" + attrs.get(colNum) + "-" + (colNum+1) + ".csv";
        
        // delete if file exist
        File file = new File(newFilePath);
        file.delete();

        FileWriter writer = new FileWriter(newFilePath, true);
        for (Map.Entry<String, Integer> val : hmObj.hm.entrySet()) {
          writer.write(val.getKey() + "," + val.getValue() + "\n");
        }
        writer.close();
        colNum++;
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error while writing out files");
    }
  }

  private static HashMapArr[] parseCSV(int attrsLen, BufferedReader reader) {
    HashMapArr[] hmArr = new HashMapArr[attrsLen];
    
    try {
      // Read the file line by line
      String line = reader.readLine();
      while (line != null) { 
        String s = line;
        List<String> cols = new ArrayList<String>();

        // split by outer comma
        // using sliding window technique
        int r = 0, l = 0, ct = 0, n = line.length();
        while (ct < attrsLen && r < n) {
          if (r == n-1 && ct == attrsLen - 1) {
            r++;
            break;
          } else if (r == n && ct < attrsLen) {
            line = reader.readLine();
            s = s + line;
            n = n + line.length(); 
          }
          if (s.charAt(r) == ',') {
            cols.add(s.substring(l, r));
            ct++;
            r++;
            l = r;

            if (r == n-1 && s.charAt(r) == ',') {
              r++;
              break;
            }       
            // handle newline not in the end of line
            while (r == n-1 && ct < attrsLen-1) {
              line = reader.readLine();
              s = s + line;
              n = n + line.length();
            }
          } else if (s.charAt(r) == '"') {
            r++;
            while (r == n-1 && ct < attrsLen-1) {
              line = reader.readLine();
              s = s + line;
              n = n + line.length();
            }
            while(s.charAt(r) != '"') {
              r++;
              // handle newline inside quotes
              while (r == n-1 && ct < attrsLen-1) {
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
        // handle last column
        if (ct < attrsLen) {
          // last column is not empty
          if (l != r) {
            cols.add(s.substring(l, r));
          } else {
            cols.add("");
          }
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
