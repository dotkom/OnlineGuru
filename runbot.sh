#!/bin/bash
cd ..
java -Dlog4j.debug -Dlog4j.configuration=file:///local/home/sys/onlinebot/log4j.properties -jar bot/onlineguru/target/onlineguru-1.2-SNAPSHOT-jar-with-dependencies.jar &
cd bot/
