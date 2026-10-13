@echo off
setlocal
if not exist "gradle\wrapper\gradle-wrapper.jar" (
  echo Gradle Wrapper JAR is not present yet; bootstrapping the official wrapper...
  powershell -NoProfile -ExecutionPolicy Bypass -File "scripts\setup-gradle-wrapper.ps1"
  if errorlevel 1 exit /b %errorlevel%
)
java -classpath "gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
endlocal
