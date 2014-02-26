#!/bin/bash

CLASSPATH=cfg/acceptor
CLASSPATH=$CLASSPATH:lib/*

java -cp "$CLASSPATH" com.jramoyo.qfixmessenger.QFixMessenger "cfg/acceptor/messenger.cfg" "cfg/acceptor/quickfix.cfg"