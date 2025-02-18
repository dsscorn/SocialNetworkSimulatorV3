#!/bin/bash

# java version as arg
if [ -n "$1" ]; then
    java_version=$1
    echo "Using specified Java version: $java_version"
else
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F. '{print $1}')
    echo "Auto-detected Java version: $java_version"
fi

JAVA_OPTS="-Xms256m -Xmx1424m -XX:-UseGCOverheadLimit"
JADE_OPTS="-jade_domain_df_maxresult 1500 -jade_core_messaging_MessageManager_poolsize 10 -jade_core_messaging_MessageManager_maxqueuesize 2000000000 -jade_core_messaging_MessageManager_deliverytimethreshold 10000 -jade_domain_df_autocleanup true -local-port 35240"

# Check if Java version is greater than 9, add options
if [ "$java_version" -gt 9 ]; then
    echo "Adding Java 9+ Compatibility Options"
    JAVA_OPTS="$JAVA_OPTS --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.io=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED \
    --add-opens java.base/java.util.concurrent=ALL-UNNAMED \
    --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
    --add-opens java.base/java.net=ALL-UNNAMED"
fi

echo "Checking Python environment..."
# Try to find Python command (python3 for Unix, python for Windows)
if command -v python3 &> /dev/null; then
    PYTHON_CMD="python3"
elif command -v python &> /dev/null; then
    # Verify it's Python 3
    if python --version 2>&1 | grep -q "Python 3"; then
        PYTHON_CMD="python"
    else
        echo "Python 3 is not installed!"
        exit 1
    fi
else
    echo "Python 3 is not installed!"
    exit 1
fi

echo "Using Python command: $PYTHON_CMD"

# Check Python version by calling it
major_version=$($PYTHON_CMD -c 'import sys; print(sys.version_info[0])')
minor_version=$($PYTHON_CMD -c 'import sys; print(sys.version_info[1])')
if [ "$major_version" -lt 3 ] || ([ "$major_version" -eq 3 ] && [ "$minor_version" -lt 9 ]); then
    echo "Python version must be 3.9 or higher. Current version: $major_version.$minor_version"
    exit 1
fi

# Check and install numpy if needed
if ! $PYTHON_CMD -c "import numpy" &> /dev/null; then
    echo "Installing numpy..."
    $PYTHON_CMD -m pip install numpy
fi

if ! $PYTHON_CMD -c "import gensim" &> /dev/null; then
    echo "Installing gensim..."
    $PYTHON_CMD -m pip install gensim
fi

if ! $PYTHON_CMD -c "import sklearn" &> /dev/null; then
    echo "Installing sklearn..."
    $PYTHON_CMD -m pip install scikit-learn
fi

if ! $PYTHON_CMD -c "import nltk" &> /dev/null; then
    echo "Installing nltk..."
    $PYTHON_CMD -m pip install nltk
    $PYTHON_CMD -c "import nltk;nltk.download('punkt');nltk.download('punkt_tab')"
fi

if ! $PYTHON_CMD -c "import seaborn" &> /dev/null; then
    echo "Installing seaborn..."
    $PYTHON_CMD -m pip install seaborn
fi

if ! $PYTHON_CMD -c "import matplotlib" &> /dev/null; then
    echo "Installing matplotlib..."
    $PYTHON_CMD -m pip install matplotlib
fi

javac -nowarn -cp "lib/*" -d classes TwitterGatherDataFollowers/userRyersonU/*.java

java $JAVA_OPTS -cp "lib/*:classes" jade.Boot $JADE_OPTS controller:TwitterGatherDataFollowers.userRyersonU.ControllerAgent
