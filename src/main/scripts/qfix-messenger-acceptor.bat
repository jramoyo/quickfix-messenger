@echo off

set JAVA_EXE=%JAVA_HOME%\bin\javaw.exe
start bin\quickfix-messenger "cfg\acceptor\messenger.cfg" "cfg\acceptor\quickfix.cfg"
