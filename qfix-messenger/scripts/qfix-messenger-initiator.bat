@echo off

echo ************************************
echo * It is safe to close this console *
echo ************************************

set CLASSPATH=cfg\initiator
set CLASSPATH=%CLASSPATH%;lib\qfix-messenger.jar
set CLASSPATH=%CLASSPATH%;lib\jdom.jar
set CLASSPATH=%CLASSPATH%;lib\lipstikLF-1.1.jar
set CLASSPATH=%CLASSPATH%;lib\log4j-1.2.16.jar
set CLASSPATH=%CLASSPATH%;lib\mina-core-1.1.7.jar
set CLASSPATH=%CLASSPATH%;lib\quickfixj-core-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;lib\quickfixj-msg-fix40-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;lib\quickfixj-msg-fix41-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;lib\quickfixj-msg-fix42-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;lib\quickfixj-msg-fix43-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;lib\quickfixj-msg-fix44-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;lib\quickfixj-msg-fix50-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;lib\quickfixj-msg-fixt11-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;lib\slf4j-api-1.6.1.jar
set CLASSPATH=%CLASSPATH%;lib\slf4j-log4j12-1.6.1.jar

javaw -cp %CLASSPATH% com.jramoyo.qfixmessenger.QFixMessenger "cfg\initiator\messenger.cfg" "cfg\initiator\quickfix.cfg"