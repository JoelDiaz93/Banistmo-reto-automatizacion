param(
    [switch]$Headless,
    [switch]$SkipClean
)

$ErrorActionPreference = "Stop"
$Utf8 = New-Object System.Text.UTF8Encoding($false)
[Console]::InputEncoding = $Utf8
[Console]::OutputEncoding = $Utf8
$OutputEncoding = $Utf8
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8"

$arguments = @()
if (-not $SkipClean) {
    $arguments += "clean"
}
$arguments += @("test", "aggregate")
if ($Headless) {
    $arguments += "-Dheadless.mode=true"
}

Write-Host "Ejecutando R010 con salida UTF-8..." -ForegroundColor Cyan
$watch = [System.Diagnostics.Stopwatch]::StartNew()
& .\gradlew.bat @arguments
$exitCode = $LASTEXITCODE
$watch.Stop()

Write-Host ("Duración total del comando: {0:N2}s" -f $watch.Elapsed.TotalSeconds)
if ($exitCode -ne 0) {
    exit $exitCode
}

$report = Join-Path $PSScriptRoot "..\target\site\serenity\index.html"
if (Test-Path $report) {
    Write-Host "Reporte Serenity: $report" -ForegroundColor Green
}
