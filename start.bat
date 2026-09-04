@echo off
if not defined SERVER_PORT set SERVER_PORT=26787

call gradlew.bat bootJar -q

for %%f in (build\libs\*.jar) do set JAR_FILE=%%f

java -jar "%JAR_FILE%" --server.port=%SERVER_PORT%
