$ErrorActionPreference = "Stop"
$GradleVersion = "9.2.1"
$BootstrapDir = ".gradle-bootstrap"
$ZipFile = Join-Path $BootstrapDir "gradle-$GradleVersion-bin.zip"
$DistDir = Join-Path $BootstrapDir "gradle-$GradleVersion"
$GradleExe = Join-Path $DistDir "bin\gradle.bat"

if (Test-Path "gradle\wrapper\gradle-wrapper.jar") {
    Write-Host "Official Gradle Wrapper already exists."
    exit 0
}

New-Item -ItemType Directory -Force -Path $BootstrapDir | Out-Null

if (-not (Test-Path $ZipFile)) {
    Write-Host "Downloading Gradle $GradleVersion..."
    Invoke-WebRequest "https://services.gradle.org/distributions/gradle-$GradleVersion-bin.zip" -OutFile $ZipFile
}

if (-not (Test-Path $GradleExe)) {
    Expand-Archive -Path $ZipFile -DestinationPath $BootstrapDir -Force
}

& $GradleExe wrapper --gradle-version $GradleVersion --distribution-type bin
if ($LASTEXITCODE -ne 0) {
    throw "Gradle wrapper generation failed with exit code $LASTEXITCODE"
}

Write-Host "Official Gradle Wrapper generated successfully."
