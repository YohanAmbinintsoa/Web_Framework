$currentDir = Get-Location
cd E:\ITU\L3\Projet_Framework
javac -d . *.java
java test.Main $currentDir
cd $currentDir