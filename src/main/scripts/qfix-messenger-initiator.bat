@echo off

set JAVA_EXE=%JAVA_HOME%\bin\javaw.exe
start bin\quickfix-messenger "cfg\initiator\messenger.cfg" "cfg\initiator\quickfix.cfg"
