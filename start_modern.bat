@echo off
setlocal enabledelayedexpansion

:: Check if Java version was provided as argument
if "%~1" neq "" (
    set "jver=%~1"
    echo Using specified Java version: %jver%
) else (
    :: Auto-detect Java version random windows hack
    for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -version 2^>^&1') do (
        set "jver=%%j"
        goto :breakloop
    )
    :breakloop
    echo Auto-detected Java version: %jver%
)

:: Set Java options
set "JAVA_OPTS=-Xms256m -Xmx14240m -XX:-UseGCOverheadLimit"
set "JADE_OPTS=-jade_domain_df_maxresult 1500 -jade_core_messaging_MessageManager_poolsize 10 -jade_core_messaging_MessageManager_maxqueuesize 2000000000 -jade_core_messaging_MessageManager_deliverytimethreshold 10000 -jade_domain_df_autocleanup true -local-port 35240"

:: Add Java 9+ compatibility options if needed
if %jver% GTR 9 (
    set "JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED"
)

echo Checking Python environment...

:: Try python3 first, then python
set "PYTHON_CMD=python3"
%PYTHON_CMD% --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    set "PYTHON_CMD=python"
    %PYTHON_CMD% --version >nul 2>&1
    if %ERRORLEVEL% NEQ 0 (
        echo Python 3 is not installed!
        exit /b 1
    )
    :: windows hack to check python3 version
    for /f "tokens=1,2 delims= " %%a in ('%PYTHON_CMD% --version') do (
        if not "%%a"=="Python" (
            echo Python 3 is not installed!
            exit /b 1
        )
        if not "%%b:~0,1"=="3" (
            echo Python 3 is not installed!
            exit /b 1
        )
    )
)

echo Using Python command: %PYTHON_CMD%

:: Check Python version using python modules
for /f "tokens=*" %%a in ('%PYTHON_CMD% -c "import sys; print(sys.version_info[0])"') do set major_version=%%a
for /f "tokens=*" %%a in ('%PYTHON_CMD% -c "import sys; print(sys.version_info[1])"') do set minor_version=%%a

if %major_version% LSS 3 (
    echo Python version must be 3.9 or higher. Current version: %major_version%.%minor_version%
    exit /b 1
)
if %major_version% EQU 3 if %minor_version% LSS 9 (
    echo Python version must be 3.9 or higher. Current version: %major_version%.%minor_version%
    exit /b 1
)

:: Check and install numpy if needed
%PYTHON_CMD% -c "import numpy" 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Installing numpy...
    %PYTHON_CMD% -m pip install numpy
)

:: Compile Java code
javac -nowarn -cp "lib/*" -d classes TwitterGatherDataFollowers/userRyersonU/*.java

:: Run Java application
java %JAVA_OPTS% -cp "lib/*;classes" jade.Boot %JADE_OPTS% controller:TwitterGatherDataFollowers.userRyersonU.ControllerAgent