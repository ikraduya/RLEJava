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
  static boolean isTesting;
  static String dirPath;
  public static void main(String[] args) {
    System.out.println("RLE-ing...");

    BufferedReader reader = null;
    List<String> attrs = new ArrayList<String>();
    HashMapArr[] hmArr = null;
    if (args.length == 2 && args[1] != null) {
      isTesting = (args[1].equals("-test"));
    }

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
    }
  }

  private static void writeOutFiles(String filePathStr, List<String> attrs, HashMapArr[] hmArr) {
    List<String> newfileNames = new ArrayList<String>();
    String fileName = null;
    
    try {
      Path filePath = Paths.get(filePathStr);
      dirPath = filePath.getParent().toString();
      fileName = (filePath.getFileName().toString()).replace(".csv", "");

      int colNum = 0;
      for (HashMapArr hmObj : hmArr) {
        String newFilePath = dirPath + "/" + fileName + "-" + attrs.get(colNum) + "-" + (colNum+1) + ".csv";
        if (isTesting) {
          newfileNames.add(fileName + "-" + attrs.get(colNum) + "-" + (colNum+1) + ".csv");
        }
        
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
    } finally {
      if (isTesting) {
        if (checkAnswer(newfileNames)) {
          System.out.println("Testing... " + fileName + " PASS");
        } else {
          System.out.println("Testing... " + fileName + " NOT PASS");
        }
      }
      System.out.println("done");
    }
  }

  private static boolean checkAnswer(List<String> fileNames) {
    BufferedReader reader = null, readerAns = null;
    boolean pass = false;

    try {
      String line, lineAns;
      pass = true;
      for (String fileName : fileNames) {
        if (!pass) {
          break;
        }
        reader = new BufferedReader(new FileReader(dirPath + "/" + fileName));
        readerAns = new BufferedReader(new FileReader(dirPath + "/answer/" + fileName));

        line = reader.readLine();
        lineAns = readerAns.readLine();
        while (line != null && lineAns != null) {
          if (!(line.equals(lineAns))) {
            pass = false;
            break;
          }
          line = reader.readLine();
          lineAns = readerAns.readLine();
        }
        if ((line == null && lineAns != null) || (line != null && lineAns == null)) {
          pass = false;
        }
        reader.close();
        readerAns.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error while testing");
    } finally {
      return pass;
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
        int l = 0, r = 0, ct = 0, n = line.length();
        while (ct < attrsLen && r < n) {
          // if last column empty
          if (r == n && ct == attrsLen - 1) {
            l = r;
            break;
          }
          if (s.charAt(r) == ',') {
            cols.add(s.substring(l, r));
            ct++;
            r++;
            l = r;       
          } else if (s.charAt(r) == '"') {
            r++;
            while (r >= n-1 && ct < attrsLen-1) {
              line = reader.readLine();
              s = s + line;
              n = n + line.length();
            }
            while(s.charAt(r) != '"') {
              r++;
              // handle newline inside quotes
              while (r >= n-1 && ct < attrsLen-1) {
                line = reader.readLine();
                s = s + line;
                n = n + line.length();
              }
            }
            while (r < n && s.charAt(r) != ',') {
              r++;
            }
            cols.add(s.substring(l, r));
            System.out.println("khusu: " + s.substring(l, r));
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
