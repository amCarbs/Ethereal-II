@echo off
echo Compiling
"C:/Program Files/Java/jdk1.7.0_65/bin/javac.exe" -d bin -cp lib/*; -sourcepath src src/com/rs/*.java
"C:/Program Files/Java/jdk1.7.0_65/bin/javac.exe" -d bin -cp lib/*; -sourcepath src src/com/rs/game/npc/familiar/*.java
"C:/Program Files/Java/jdk1.7.0_65/bin/javac.exe" -d bin -cp lib/*; -sourcepath src src/com/rs/tools/dropEditor.java
echo Complete
pause