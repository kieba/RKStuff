@echo off

echo.1:Debug
echo.2:Release
set /P MeineVariable=
if %MeineVariable%==1 goto debug
if %MeineVariable%==2 goto release

echo %MeineVariable%

:release
echo release
gradlew clean setupCIWorkSpace build
goto end

:debug
echo debug
gradlew clean setupDecompWorkspace idea
goto end


:end