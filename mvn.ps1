# Script PowerShell pour lancer Maven de manière portable sous Windows
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$MavenArgs
)

function Resolve-MavenCmd {
    $wrapper = Join-Path $PSScriptRoot 'mvnw.cmd'
    if (Test-Path $wrapper) {
        return $wrapper
    }

    $mavenCmd = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if ($mavenCmd) {
        return $mavenCmd.Source
    }

    if ($env:M2_HOME) {
        $candidate = Join-Path $env:M2_HOME 'bin\mvn.cmd'
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    $searchRoots = @(
        'C:\Apache',
        'C:\Program Files',
        'C:\Program Files (x86)',
        'C:\Tools'
    )

    $localMavenHomes = foreach ($root in $searchRoots) {
        if (Test-Path $root) {
            Get-ChildItem $root -Directory -ErrorAction SilentlyContinue |
                Where-Object { $_.Name -match 'maven' } |
                ForEach-Object { $_.FullName }
        }
    }

    foreach ($mavenHome in $localMavenHomes | Select-Object -Unique) {
        $candidate = Join-Path $mavenHome 'bin\mvn.cmd'
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    return $null
}

function Resolve-JavaHome {
    $candidates = @()

    if ($env:JAVA_HOME) {
        $candidates += $env:JAVA_HOME
    }

    $searchRoots = @(
        'C:\Program Files\Java',
        'C:\Program Files\Eclipse Adoptium',
        'C:\Program Files\Microsoft\jdk',
        'C:\Program Files\Zulu',
        'C:\Program Files\Amazon Corretto',
        'C:\Java'
    )

    foreach ($root in $searchRoots) {
        if (Test-Path $root) {
            Get-ChildItem $root -Directory | ForEach-Object { $candidates += $_.FullName }
        }
    }

    foreach ($candidate in $candidates | Select-Object -Unique) {
        $javaPath = Join-Path $candidate 'bin\java.exe'
        if (Test-Path $javaPath) {
            return $candidate
        }
    }

    return $null
}

$mavenExecutable = Resolve-MavenCmd
if (-not $mavenExecutable) {
    throw "Maven introuvable. Installez Maven et ajoutez `mvn.cmd` au PATH, ou définissez M2_HOME vers votre installation Maven."
}

$detectedJavaHome = Resolve-JavaHome
if ($detectedJavaHome) {
    $env:JAVA_HOME = $detectedJavaHome
    if ($env:Path -notlike "$detectedJavaHome\bin*") {
        $env:Path = "$detectedJavaHome\bin;" + $env:Path
    }
}

& $mavenExecutable @MavenArgs

