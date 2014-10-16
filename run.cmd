@echo off
title runserver
"C:/Program Files (x86)/Java/jre7/bin/java.exe" -Xmx815m -cp bin;lib/*; com.rs.Launcher true true false
pause