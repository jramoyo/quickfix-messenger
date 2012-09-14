#!/bin/bash

CLASSPATH=cfg/initiator
CLASSPATH=$CLASSPATH:lib/qfix-messenger.jar
CLASSPATH=$CLASSPATH:lib/jdom.jar
CLASSPATH=$CLASSPATH:lib/lipstikLF-1.1.jar
CLASSPATH=$CLASSPATH:lib/log4j-1.2.16.jar
CLASSPATH=$CLASSPATH:lib/mina-core-1.1.7.jar
CLASSPATH=$CLASSPATH:lib/quickfixj-all-1.5.2.jar
CLASSPATH=$CLASSPATH:lib/slf4j-api-1.6.1.jar
CLASSPATH=$CLASSPATH:lib/slf4j-log4j12-1.6.1.jar

java -cp "$CLASSPATH" com.jramoyo.qfixmessenger.QFixMessenger "cfg/initiator/messenger.cfg" "cfg/initiator/quickfix.cfg"