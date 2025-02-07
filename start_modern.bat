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

where /q python3 2>nul && (python3 -c "print('ok')" 2>nul | findstr "ok" >nul) 
if %ERRORLEVEL% EQU 0 (
    set "PYTHON_CMD=python3"
    goto :python_found
)

where /q python 2>nul && (python -c "print('ok')" 2>nul | findstr "ok" >nul)
if %ERRORLEVEL% EQU 0 (
    set "PYTHON_CMD=python"
    goto :python_found
)

:python_found
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
%PYTHON_CMD% -c "import gensim" 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Installing gensim...
    %PYTHON_CMD% -m pip install gensim
)
%PYTHON_CMD% -c "import sklearn" 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Installing sklearn...
    %PYTHON_CMD% -m pip install scikit-learn
)
%PYTHON_CMD% -c "import nltk" 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Installing nltk...
    %PYTHON_CMD% -m pip install nltk
    %PYTHON_CMD% -c "import nltk;nltk.download('punkt');nltk.download('punkt_tab')"
)
%PYTHON_CMD% -c "import seaborn" 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Installing seaborn...
    %PYTHON_CMD% -m pip install seaborn
)
%PYTHON_CMD% -c "import matplotlib" 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Installing matplotlib...
    %PYTHON_CMD% -m pip install matplotlib
)
:: Compile Java code
javac -nowarn -cp "lib/*" -d classes TwitterGatherDataFollowers/userRyersonU/*.java

:: Run Java application
java %JAVA_OPTS% -cp "lib/*;classes" jade.Boot %JADE_OPTS% controller:TwitterGatherDataFollowers.userRyersonU.ControllerAgent
