# DSMP Simulator Version 3.0

The DSMP Social Network Simulator can be used by researchers and users who want to benefit from the simple UI provided in this simulation tool to examine and compare different ML-based applications in a simulated social network environment. 


# Installation 


## Windows (Original Instructions)
Download and install Java8 at https://www.oracle.com/ca-en/java/technologies/javase/javase8-archive-downloads.html.

[//]: # (Downlaod mingw-get-setup.exe at https://sourceforge.net/projects/mingw/ and install C compiler.)
 
Download Python 3.9 at https://www.python.org/downloads/ and install it.  

git clone https://github.com/dsscorn/SocialNetworkSimulatorV3.git.

Extract the ZIP and Double-click start.bat

## Windows (JDK 8, 9+)
1. Download/Install/Use your preferred version of Java 9 or higher.  
2. Download Python 3.9 [here](https://www.python.org/downloads/)
3. Download Apache Maven from your preferred Windows package manager (chocolatey/scoop), or manually [here](https://maven.apache.org/download.cgi) and follow this install guide [here](https://stackoverflow.com/questions/38549614/how-to-install-maven-in-windows)
4. Run this command to install the dependencies: `mvn dependency:copy-dependencies -DoutputDirectory=lib/maven`
5. Run `start_modern.bat`

## MacOS / Linux
1. Install Java/JDK 8 or higher (JDK 8 preferred)
2. Install/Activate a Python 3.9 install from your preferred source (package manager/ manual / other tool).
3. Install Apache Maven from your preferred source (package manager or manual)
4. Run this command to install the dependencies: `mvn dependency:copy-dependencies -DoutputDirectory=lib/maven`
5. Run `build_and_start_modern.sh` to start the program for the first term
6. Run `start_modern.sh` after if you don't modify the source code

