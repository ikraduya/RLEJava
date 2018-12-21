# RLEJava

#### Compile
javac pkg/*.java

#### Run
format: java pkg/Main \<filepath>  
example:
- java pkg/Main pkg/sampleCSV/FoodInspectionsSample.csv
- java pkg/Main pkg/sampleCSV/Lobbyist_Data_Clients.csv

#### Test
format java pkg/Main \<filepath> -test
example:
- java pkg/Main pkg/test/test1/test1.csv -test
- java pkg/Main pkg/test/test2/test2.csv -test

#### Note
- There is false data in FoodInspectionsSample.csv line 1301 with Inspection ID 1547197
