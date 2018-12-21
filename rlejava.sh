#!/bin/bash

# rle_java - A Script to run rle java program on csv file
if [ "$1" == "" ]; then
  echo "Parameter 1 needed"
  echo "usage: ./rlejava.sh <csv-folder-path>"
else
  DIR=$1
  filenames=()
  # TODO: Performance matrix
  for i in $(find $DIR -maxdepth 1 -type f -iname "*.csv"); do
    ts=$(date +%s%N)
    java pkg/Main "$i"
    tt=$((($(date +%s%N) - $ts)/1000000))
    echo "Time taken: $tt ms" 
    echo ""
  done
fi

