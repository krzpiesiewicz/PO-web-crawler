#!/bin/bash
#@author Krzysztof Piesiewicz

if [ $# -ne 1 ]; then
  echo "Zła liczba argumentów. Program przyjmuje 1 argument - adres internetowy.";
  exit;
fi

ADDRESS=$1

java -cp lib/jsoup-1.10.2.jar:bin  pl.edu.mimuw.crawler.kp385996.Demo1 $ADDRESS
