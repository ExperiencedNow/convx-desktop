# Convx Desktop - PREVIEW launcher (NOT the final .exe)
# Usage:
#   powershell -ExecutionPolicy Bypass -File scripts\preview.ps1             # Preview A: dev run
#   powershell -ExecutionPolicy Bypass -File scripts\preview.ps1 -Portable   # Preview B: portable folder, no installer
#   add -Clean to wipe previous build output first
# NOTE: written without being able to execute it. Gradle task names follow the Compose Multiplatform
#       Gradle plugin conventions; verify with:  .\gradlew.bat :desktopApp:tasks

param(
    [switch]$Portable,
    [switch]$Clean
)

$ErrorActionPreference = "Stop"
Set-Location (Split-Path $PSScriptRoot -Parent)

if (-not (Test-Path ".\gradlew.bat")) {
    Write-Error "gradlew.bat not found. Run this script from inside the convx-desktop repo (scripts\ folder)."
    exit 1
}

New-Item -ItemType Directory -Force -Path "preview-report" | Out-Null
$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$log = "preview-report\run-$stamp.log"

if ($Clean) {
    Write-Host "Cleaning desktopApp build output..."
    & .\gradlew.bat :desktopApp:clean --console=plain
}

# The app should read this env var to show a visible PREVIEW badge and verbose logging.
$env:CONVX_PREVIEW = "1"

if ($Portable) {
    Write-Host "Building Preview B (portable folder, no installer)..." -ForegroundColor Cyan
    & .\gradlew.bat :desktopApp:createDistributable --console=plain 2>&1 | Tee-Object -FilePath $log
    if ($LASTEXITCODE -ne 0) { Write-Error "Build failed. See $log"; exit $LASTEXITCODE }

    $root = "desktopApp\build\compose\binaries\main\app"
    if (Test-Path $root) {
        $exe = Get-ChildItem -Path $root -Filter "*.exe" -Recurse | Select-Object -First 1
        if ($exe) {
            Write-Host "Portable preview built: $($exe.FullName)" -ForegroundColor Green
            Write-Host "Launching it now. Nothing is installed on your PC."
            Start-Process -FilePath $exe.FullName
            exit 0
        }
    }
    Write-Warning "Build finished but no .exe found under $root. Check the Compose plugin output path for your version."
    exit 2
}
else {
    Write-Host "Starting Preview A (dev run)..." -ForegroundColor Cyan
    Write-Host "Log: $log"
    & .\gradlew.bat :desktopApp:run --console=plain 2>&1 | Tee-Object -FilePath $log
}
