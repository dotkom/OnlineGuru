#!/bin/bash
HERE=`pwd`
cd irclib/trunk
mvn clean install
cd $HERE
cd onlineguru/trunk
mvn clean install
cd $HERE
