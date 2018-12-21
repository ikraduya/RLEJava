#!/bin/bash

# rle_java - A Script to run rle java program on csv file
if [ "$1" == "" ]; then
  echo "Parameter 1 needed"
  echo "usage: ./rlejava.sh <csv-folder-path>"
  echo "    or ./rlejava.sh -test"
elif [ "$1" == "-test" ]; then
  # test 1
  rm -f data/test/test1/test1-age-2.csv data/test/test1/test1-name-1.csv data/test/test1/test1-state-3.csv
  java pkg/Main data/test/test1/test1.csv -test

  # test 2
  rm -f data/test/test2/test2-age-2.csv data/test/test2/test2-name-1.csv data/test/test2/test2-country-3.csv
  java pkg/Main data/test/test2/test2.csv -test

  # test 3
  rm -f data/test/test3/test3-comment-2.csv data/test/test3/test3-name-1.csv data/test/test3/test3-age-3.csv data/test/test3/test3-hobbies-4.csv
  java pkg/Main data/test/test3/test3.csv -test

  # test 4
  rm -f data/test/test4/test4-comment-2.csv data/test/test4/test4-id-1.csv data/test/test4/test4-summary-3.csv
  java pkg/Main data/test/test4/test4.csv -test
else
  DIR=$1
  for filepath in $(find $DIR -maxdepth 1 -type f -iname "*.csv"); do
    filesize=$(($(wc -c <"$filepath")/1000))
    disk_space_used=$(du -k "$filepath" | cut -f 1)

    ts=$(date +%s%N)
    java pkg/Main "$filepath"
    tt=$((($(date +%s%N) - $ts)/1000000))

    # logging performance matrix
    echo "----------------------------------------" >> performance-log.txt
    echo "File path: $filepath" >> performance-log.txt
    echo "File size: $filesize kb" >> performance-log.txt
    echo "Disk space used: $disk_space_used kb" >> performance-log.txt
    echo "Time taken: $tt ms"  >> performance-log.txt
    echo "----------------------------------------" >> performance-log.txt
    echo "" >> performance-log.txt
  done
fi

