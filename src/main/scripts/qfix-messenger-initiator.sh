#!/bin/bash

CLASSPATH=cfg/initiator/
CLASSPATH=$CLASSPATH:resources/
CLASSPATH=$CLASSPATH:lib/*

java -cp "$CLASSPATH" com.jramoyo.qfixmessenger.QFixMessenger "cfg/initiator/messenger.cfg" "cfg/initiator/quickfix.cfg"