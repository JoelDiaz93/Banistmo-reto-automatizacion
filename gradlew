#!/usr/bin/env bash
set -euo pipefail
if [[ ! -f "gradle/wrapper/gradle-wrapper.jar" ]]; then
  echo "Gradle Wrapper JAR is not present yet; bootstrapping the official wrapper..."
  bash scripts/setup-gradle-wrapper.sh
fi
exec java -classpath "gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
