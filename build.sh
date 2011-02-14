#!/bin/bash
HERE=`pwd`
mvn clean install
cd $HERE
cd onlineguru/trunk
mvn clean install
cd $HERE
