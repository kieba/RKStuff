@echo off

echo.1:Debug
echo.2:Release
set /P MeineVariable=
if %MeineVariable%==1 goto debug
if %MeineVariable%==2 goto release
if %MeineVariable%==9 goto init

echo %MeineVariable%
:init
gradlew cleanCache
goto end

:release
echo release
gradlew clean setupCIWorkSpace build
goto end

:debug
echo debug
gradlew clean setupDecompWorkspace installWaila installTranslocator idea
goto end


:end