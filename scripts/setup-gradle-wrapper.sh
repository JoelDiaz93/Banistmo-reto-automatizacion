#!/usr/bin/env bash
set -euo pipefail

GRADLE_VERSION="9.2.1"
BOOTSTRAP_DIR=".gradle-bootstrap"
ZIP_FILE="${BOOTSTRAP_DIR}/gradle-${GRADLE_VERSION}-bin.zip"
DIST_DIR="${BOOTSTRAP_DIR}/gradle-${GRADLE_VERSION}"

if [[ -f gradle/wrapper/gradle-wrapper.jar ]]; then
  echo "Official Gradle Wrapper already exists."
  exit 0
fi

mkdir -p "${BOOTSTRAP_DIR}"

if [[ ! -f "${ZIP_FILE}" ]]; then
  echo "Downloading Gradle ${GRADLE_VERSION}..."
  curl --fail --location --retry 3 \
    "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    --output "${ZIP_FILE}"
fi

if [[ ! -x "${DIST_DIR}/bin/gradle" ]]; then
  unzip -q -o "${ZIP_FILE}" -d "${BOOTSTRAP_DIR}"
fi

"${DIST_DIR}/bin/gradle" wrapper --gradle-version "${GRADLE_VERSION}" --distribution-type bin
chmod +x gradlew

echo "Official Gradle Wrapper generated successfully."
