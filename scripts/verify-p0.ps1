param(
    [switch]$RunE2E,
    [switch]$Headless
)

$ErrorActionPreference = "Stop"
$Utf8 = New-Object System.Text.UTF8Encoding($false)
[Console]::InputEncoding = $Utf8
[Console]::OutputEncoding = $Utf8
$OutputEncoding = $Utf8
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8"

function Fail([string]$Message) {
    Write-Host "P0 FAIL: $Message" -ForegroundColor Red
    exit 1
}

Write-Host "Validando P0..." -ForegroundColor Cyan

$trackedGenerated = git ls-files build target .gradle .gradle-bootstrap 2>$null
if ($LASTEXITCODE -eq 0 -and $trackedGenerated) {
    Fail "Hay artefactos generados versionados:`n$trackedGenerated"
}

$trackedSensitive = git ls-files | Where-Object {
    $_ -match '(^|/)(\.env($|\.)|.*\.(pem|key|p12|pfx)$|.*cookies.*\.json$|.*token.*\.json$|.*credentials.*\.json$)'
}
if ($trackedSensitive) {
    Fail "Hay archivos potencialmente sensibles versionados:`n$($trackedSensitive -join "`n")"
}

& .\gradlew.bat p0StaticCheck
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

if ($RunE2E) {
    $arguments = @("clean", "test", "aggregate", "--warning-mode", "all")
    if ($Headless) {
        $arguments += "-Dheadless.mode=true"
    }
    & .\gradlew.bat @arguments
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}

Write-Host "P0 QUALITY GATE: PASS" -ForegroundColor Green
