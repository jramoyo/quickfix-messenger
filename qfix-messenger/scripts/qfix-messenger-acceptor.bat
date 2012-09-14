@echo off

echo ************************************
echo * It is safe to close this console *
echo ************************************

set CLASSPATH=cfg\acceptor
set CLASSPATH=%CLASSPATH%;lib\qfix-messenger.jar
set CLASSPATH=%CLASSPATH%;lib\jdom.jar
set CLASSPATH=%CLASSPATH%;lib\lipstikLF-1.1.jar
set CLASSPATH=%CLASSPATH%;lib\log4j-1.2.16.jar
set CLASSPATH=%CLASSPATH%;lib\mina-core-1.1.7.jar
set CLASSPATH=%CLASSPATH%;lib\quickfixj-all-1.5.2.jar
set CLASSPATH=%CLASSPATH%;lib\slf4j-api-1.6.1.jar
set CLASSPATH=%CLASSPATH%;lib\slf4j-log4j12-1.6.1.jar

javaw -cp %CLASSPATH% com.jramoyo.qfixmessenger.QFixMessenger "cfg\acceptor\messenger.cfg" "cfg\acceptor\quickfix.cfg"