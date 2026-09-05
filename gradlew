#!/usr/bin/env bash
# Gradle Wrapper (generated)

# Resolve script directory
APP_HOME=$(cd "$(dirname \"$0\")" && pwd)

# Find java executable
if [ -n "$JAVA_HOME" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD=java
fi

# If not executable, try to locate in PATH
if ! command -v "$JAVACMD" >/dev/null 2>&1; then
  JAVACMD=$(command -v java)
fi

if [ -z "$JAVACMD" ]; then
  echo "Error: Java not found. Set JAVA_HOME or ensure java is on PATH." >&2
  exit 1
fi

# Execute Gradle Wrapper jar
exec "$JAVACMD" -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
