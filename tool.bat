@echo off

echo.1:Debug
echo.2:Release
echo.3:CreateDumpImage
echo.9:CleanUp
set /P MeineVariable=
if %MeineVariable%==1 goto debug
if %MeineVariable%==2 goto release
if %MeineVariable%==3 goto dumpImage
if %MeineVariable%==9 goto cleanUp

echo %MeineVariable%
:cleanUp
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

:dumpImage
echo Pfad (z.B. boiler\BoilerBase1.png):
set /P Pfad=

echo Text:
set /P Text=

echo Color:
set /P Color=

java -jar MinecraftDumpImageCreator.jar "%Pfad%" "%Color%" "%Text%"
goto end


:end