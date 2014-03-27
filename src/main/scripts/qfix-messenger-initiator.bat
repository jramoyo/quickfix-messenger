@echo off

echo ************************************
echo * It is safe to close this console *
echo ************************************

set CLASSPATH=cfg\acceptor
set CLASSPATH=%CLASSPATH%;lib\*

javaw -cp %CLASSPATH% com.jramoyo.qfixmessenger.QFixMessenger "cfg\initiator\messenger.cfg" "cfg\initiator\quickfix.cfg"