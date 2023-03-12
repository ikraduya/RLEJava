# Run-length Encoding in Java

#### Compile
javac pkg/*.java

### Run
format: ./rlejava.sh \<filepath>  
example:
- ./rlejava.sh data/sampleCSV/

#### Running program directly
format: java pkg/Main \<filepath>  
example:
- java pkg/Main data/sampleCSV/FoodInspectionsSample.csv
- java pkg/Main data/sampleCSV/Lobbyist_Data_Clients.csv

### Test
./rlejava.sh -test

#### Testing program directly
format java pkg/Main \<filepath> -test  
example:
- java pkg/Main data/test/test1/test1.csv -test
- java pkg/Main data/test/test2/test2.csv -test

### Note
- There is false data in FoodInspectionsSample.csv line 1301 with Inspection ID 1547197
