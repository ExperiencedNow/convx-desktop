# Convx Desktop - FINAL .exe build. Refuses to run without the user's approval file.
# Usage: powershell -ExecutionPolicy Bypass -File scripts\package-exe.ps1
# NOTE: written without being able to execute it. Verify task names with:  .\gradlew.bat :desktopApp:tasks

$ErrorActionPreference = "Stop"
Set-Location (Split-Path $PSScriptRoot -Parent)

# ---- Approval gate (rule F7) -------------------------------------------------
$approval = ".\APPROVED.txt"
if (-not (Test-Path $approval)) {
    Write-Error "BLOCKED: APPROVED.txt not found. The .exe is only built after the user approves the preview (docs\04_PREVIEW_AND_RELEASE.md)."
    exit 1
}
$firstLine = (Get-Content $approval -TotalCount 1).Trim()
if ($firstLine -ne "APPROVED FOR EXE") {
    Write-Error "BLOCKED: first line of APPROVED.txt must be exactly: APPROVED FOR EXE"
    exit 1
}

# ---- Tooling checks ----------------------------------------------------------
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error "Java not found on PATH. Install JDK 21 (with jpackage)."; exit 1
}
if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    Write-Warning "jpackage not found on PATH; Gradle may still locate it from JAVA_HOME. Continuing."
}
$wix = Get-Command candle.exe -ErrorAction SilentlyContinue
if (-not $wix) {
    Write-Error "WiX Toolset 3.x (candle.exe / light.exe) not found on PATH. It is required to build an .exe installer. Install it, reopen the terminal, and retry."
    exit 1
}

# ---- Release test gate -------------------------------------------------------
Write-Host "Running full test suite before packaging..." -ForegroundColor Cyan
& .\gradlew.bat :desktopApp:check :desktop-core:check --console=plain
if ($LASTEXITCODE -ne 0) { Write-Error "Tests failed. Not packaging."; exit $LASTEXITCODE }

# ---- Package -----------------------------------------------------------------
Write-Host "Building installer (.exe)..." -ForegroundColor Cyan
& .\gradlew.bat :desktopApp:packageExe --console=plain
if ($LASTEXITCODE -ne 0) { Write-Error "packageExe failed."; exit $LASTEXITCODE }

$outDir = "desktopApp\build\compose\binaries\main\exe"
$exe = Get-ChildItem -Path $outDir -Filter "*.exe" -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $exe) {
    Write-Warning "Build reported success but no .exe found in $outDir. Check the Compose plugin output path for your version."
    exit 2
}

New-Item -ItemType Directory -Force -Path "docs\evidence\release" | Out-Null
$hash = (Get-FileHash $exe.FullName -Algorithm SHA256).Hash
"$hash  $($exe.Name)" | Out-File -FilePath "docs\evidence\release\SHA256.txt" -Encoding ascii
Write-Host "DONE: $($exe.FullName)" -ForegroundColor Green
Write-Host "SHA-256: $hash"
Write-Host "Next: run the release checklist in docs\04_PREVIEW_AND_RELEASE.md (clean-VM install test)."
