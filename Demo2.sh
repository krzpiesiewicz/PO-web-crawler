#!/bin/bash
#@author Krzysztof Piesiewicz

if [ $# -ne 2 ]; then
  echo "Zła liczba argumentów. Program przyjmuje 2 argumenty - ";
  echo "ściężkę pliku oraz liczbę naturalną.";
  exit;
fi

PATH=$1
MAX_LEVEL=$2

java -cp lib/jsoup-1.10.2.jar:bin  pl.edu.mimuw.crawler.kp385996.Demo2 $PATH $MAX_LEVEL
