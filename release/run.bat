@echo off
echo Starting LSF Server...

REM Create logs directory if it doesn't exist
if not exist logs mkdir logs

REM Set Java options
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC

REM Set the config location
set SPRING_CONFIG_LOCATION=file:./config/

REM Run the application
java %JAVA_OPTS% -jar lsf-server.jar --spring.config.location=%SPRING_CONFIG_LOCATION% --logging.file.path=./logs --logging.file.name=logs/lsf-server.log

REM If the application exits, pause to see any error messages
pause 