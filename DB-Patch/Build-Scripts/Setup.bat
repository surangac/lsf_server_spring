@echo off & setlocal

set str1=
set str2=
set str3=
set str=
For /F "tokens=1*" %%i in (..\Parameters.dat) do call :doSomething "%%i"
echo %str1% %str2% %str3%
call execute %str1%  %str2% %str3%

goto :eof

:doSomething
echo Now working with "%~1" string...
Set "Str=%~1"

echo %str%

echo %str:~0,7% %str:~8%

 If %str:~0,7%==CON_STR  set "str1=%str:~8%"
 If %str:~0,7%==SYS_PWD  set "str2=%str:~8%"
 If %str:~0,7%==DB_USER  set "str3=%str:~8%"

echo %str1% %str2% %str3%
goto :eof

