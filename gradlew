#!/usr/bin/env bash
# Gradle Wrapper (fixed for Windows Bash)

# Resolve script directory robustly
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

# Find java executable
if [ -n "$JAVA_HOME" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD=java
fi

# Fallback to PATH
if ! command -v "$JAVACMD" > /dev/null 2>&1; then
  JAVACMD=$(command -v java)
fi

if [ -z "$JAVACMD" ]; then
  echo "Error: Java not found. Set JAVA_HOME or ensure java is on PATH." >&2
  exit 1
fi

# Execute the Gradle wrapper JAR
exec "$JAVACMD" -jar "$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
