<#
.SYNOPSIS
  Simple Java project helper for Windows PowerShell - counterpart to the Unix `jmake` script.

.DESCRIPTION
  Supports commands: compile (default), run, jar, all, clean, help.
  Mirrors behavior of the provided bash `jmake` script. Handles library jars in `lib/` and
  also compiles Java sources under `lib/*` if there is no corresponding module jar.

.NOTES
  Run with: .\jmake.ps1 <command>
  Requires Java (javac/java/jar) available on PATH.
#>

[CmdletBinding()]
param(
    [Parameter(Mandatory=$false, Position=0)]
    [string]$Command = ''
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$RootDir = $PSScriptRoot
if (-not $RootDir) { $RootDir = Get-Location }
$JavaOutDir = Join-Path $RootDir 'java'
$JarName = 'axiom.jar'
$MainClass = 'Axiom'
$LibDir = Join-Path $RootDir 'lib'

function Get-LibJars {
    param([string]$libDir)
    if (-not (Test-Path $libDir)) { return @() }
    return @(Get-ChildItem -Path $libDir -Filter '*.jar' -File -ErrorAction SilentlyContinue | Sort-Object -Property Name)
}

function Collect-Sources {
    # helper: directories to ignore (virtualenvs, node modules, build outputs)
    $ignoredDirs = @('.venv','venv','.git','node_modules','target','build')

    function Is-IgnoredDir {
        param([string]$path)
        foreach ($id in $ignoredDirs) {
            if ($path -like "*$id*") { return $true }
        }
        return $false
    }

    # Collect project java files while skipping ignored or inaccessible directories.
    $projSrcs = @()
    # canonical root path for starts-with checks
    try { $rootFull = (Get-Item -LiteralPath $RootDir).FullName } catch { $rootFull = $RootDir }
    try {
        Get-ChildItem -Path $RootDir -Recurse -Include '*.java' -File -ErrorAction SilentlyContinue | ForEach-Object {
            try {
                if (-not ($_.FullName -like "$JavaOutDir*") -and -not (Is-IgnoredDir -path $_.FullName) -and ($_.FullName.StartsWith($rootFull, [System.StringComparison]::InvariantCultureIgnoreCase))) {
                    $projSrcs += $_.FullName
                }
            } catch { }
        }
    } catch {
        # if top-level recurse failed, try a safer one-level scan
        Get-ChildItem -Path $RootDir -Include '*.java' -File -ErrorAction SilentlyContinue | ForEach-Object { if (-not (Is-IgnoredDir -path $_.FullName) -and ($_.FullName.StartsWith($rootFull, [System.StringComparison]::InvariantCultureIgnoreCase))) { $projSrcs += $_.FullName } }
    }

    $libSrcs = @()
    if (Test-Path $LibDir) {
        Get-ChildItem -Path $LibDir -Directory -ErrorAction SilentlyContinue | ForEach-Object {
            $m = $_
            $moduleBase = $m.Name
            # If lib/<module>.jar exists, skip compiling that module's sources
            if (Test-Path (Join-Path $LibDir "$moduleBase.jar")) { return }
            # Prefer m/src/main/java, otherwise any .java under the module
            $preferred = Join-Path $m.FullName 'src\main\java'
            try {
                if (Test-Path $preferred) {
                    Get-ChildItem -Path $preferred -Recurse -Include '*.java' -File -ErrorAction SilentlyContinue | ForEach-Object {
                        if (-not (Is-IgnoredDir -path $_.FullName)) { $libSrcs += $_.FullName }
                    }
                } else {
                    Get-ChildItem -Path $m.FullName -Recurse -Include '*.java' -File -ErrorAction SilentlyContinue | ForEach-Object {
                        if (-not (Is-IgnoredDir -path $_.FullName)) { $libSrcs += $_.FullName }
                    }
                }
            } catch {
                # ignore inaccessible module directories
            }
        }
    }
    return ,($projSrcs + $libSrcs) | Where-Object { $_ -ne $null }
}

function Compile {
    Write-Host 'Compiling Java sources...'
    if (-not (Test-Path $JavaOutDir)) { New-Item -ItemType Directory -Path $JavaOutDir | Out-Null }
    # Ensure Collect-Sources always yields an array so .Count is safe
    $srcs = @() + (Collect-Sources)
    if (-not $srcs -or $srcs.Count -eq 0) {
        Write-Host 'No Java sources found.'
        return
    }

    $libJars = Get-LibJars -libDir $LibDir
    $libJars = @() + $libJars
    if ($env:JMAKE_DEBUG -eq '1') {
        Write-Host "Collected sources ($($srcs.Count)):" -ForegroundColor Yellow
        $srcs | ForEach-Object { Write-Host " - $_" }
    }

    if ($libJars.Count -gt 0) {
        $cp = ($libJars | ForEach-Object { $_.FullName }) -join ';'
        & javac -cp $cp -d $JavaOutDir @($srcs) 2>&1
    } else {
        & javac -d $JavaOutDir @($srcs) 2>&1
    }
    Write-Host "Compiled to $JavaOutDir"
}

function Build-Jar {
    Compile
    Write-Host "Building $JarName (Main-Class: $MainClass)..."
    $libJars = Get-LibJars -libDir $LibDir
    $libJars = @() + $libJars
    $manifestFile = Join-Path $JavaOutDir 'manifest.mf'
    if ($libJars.Count -gt 0) {
        # Build Class-Path relative entries: lib/<basename>
        $entries = $libJars | ForEach-Object { 'lib/' + $_.Name }
        $classpathEntry = $entries -join ' '
        $mfContent = "Class-Path: $classpathEntry`nMain-Class: $MainClass`n"
        $mfContent | Out-File -FilePath $manifestFile -Encoding ASCII
        Push-Location $JavaOutDir
        & jar --create --file (Join-Path $RootDir $JarName) --manifest $manifestFile -C $JavaOutDir .
        Pop-Location
    } else {
        # create a simple manifest with Main-Class
        $mfContent = "Main-Class: $MainClass`n"
        $mfContent | Out-File -FilePath $manifestFile -Encoding ASCII
        Push-Location $JavaOutDir
        & jar --create --file (Join-Path $RootDir $JarName) --manifest $manifestFile -C $JavaOutDir .
        Pop-Location
    }
    Write-Host "Created $JarName"
}

function Run-Main {
    Write-Host "Running $MainClass..."
    $libJars = Get-LibJars -libDir $LibDir
    $libJars = @() + $libJars
    if ($libJars.Count -gt 0) {
        $cpParts = @($JavaOutDir) + ($libJars | ForEach-Object { $_.FullName })
        $cp = $cpParts -join ';'
        & java -cp $cp $MainClass
    } else {
        & java -cp $JavaOutDir $MainClass
    }
}

function Clean {
    Write-Host "Cleaning compiled classes and $JarName..."
    if (Test-Path $JavaOutDir) { Remove-Item -Recurse -Force -Path $JavaOutDir }
    $jarPath = Join-Path $RootDir $JarName
    if (Test-Path $jarPath) { Remove-Item -Force $jarPath }
    Write-Host 'Clean complete.'
}

function Show-Usage {
    Write-Host @"
jmake.ps1 - simple Java project helper

Usage: .\jmake.ps1 [command]

Commands:
  run       Compile and run the main class ($MainClass)
  jar       Build executable jar ($JarName)
  all       Compile and run (alias for run)
  clean     Remove compiled classes and the jar
  help      Show this help message
  (no arg)  Compile sources only
"@
}

switch ($Command.ToLower()) {
    'help' { Show-Usage; break }
    '-h' { Show-Usage; break }
    '--help' { Show-Usage; break }
    'run' { Run-Main; break }
    'jar' { Build-Jar; break }
    'all' { Compile; Run-Main; break }
    'clean' { Clean; break }
    '' { Compile; break }
    Default { Write-Host "Usage: .\jmake.ps1 [run|jar|all|clean|help]"; exit 1 }
}
