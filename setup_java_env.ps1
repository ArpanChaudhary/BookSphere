# Create virtual environment directory
$envDir = ".\venv"
if (-not (Test-Path $envDir)) {
    New-Item -ItemType Directory -Path $envDir
}

# Clean up existing files and directories
if (Test-Path "$envDir\jdk21.zip") { Remove-Item "$envDir\jdk21.zip" }
if (Test-Path "$envDir\maven.zip") { Remove-Item "$envDir\maven.zip" }
if (Test-Path "$envDir\jdk21") { Remove-Item "$envDir\jdk21" -Recurse }
if (Test-Path "$envDir\maven") { Remove-Item "$envDir\maven" -Recurse }

# Function to download with retry
function Download-FileWithRetry {
    param (
        [string]$Url,
        [string]$OutputFile,
        [int]$MaxRetries = 3
    )
    
    for ($i = 0; $i -lt $MaxRetries; $i++) {
        try {
            Write-Host "Download attempt $($i + 1) of $MaxRetries..."
            $ProgressPreference = 'SilentlyContinue'
            $webClient = New-Object System.Net.WebClient
            $webClient.DownloadFile($Url, $OutputFile)
            return $true
        } catch {
            Write-Host "Attempt $($i + 1) failed: $_"
            if ($i -lt $MaxRetries - 1) {
                Write-Host "Retrying in 5 seconds..."
                Start-Sleep -Seconds 5
            }
        }
    }
    return $false
}

# Download JDK 21
Write-Host "Downloading JDK 21..."
$jdkUrl = "https://download.java.net/java/GA/jdk21.0.2/13d6b10a0d40e08180c3f5a73eb4cacd/13/GPL/openjdk-21.0.2_windows-x64_bin.zip"
$jdkZip = "$envDir\jdk21.zip"

# Try alternative JDK URLs if the first one fails
$jdkUrls = @(
    "https://download.java.net/java/GA/jdk21.0.2/13d6b10a0d40e08180c3f5a73eb4cacd/13/GPL/openjdk-21.0.2_windows-x64_bin.zip",
    "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jdk_x64_windows_hotspot_21.0.2_13.zip",
    "https://download.java.net/java/GA/jdk21.0.2/13d6b10a0d40e08180c3f5a73eb4cacd/13/GPL/openjdk-21.0.2_windows-x64_bin.zip"
)

$jdkDownloaded = $false
foreach ($url in $jdkUrls) {
    Write-Host "Trying JDK URL: $url"
    if (Download-FileWithRetry -Url $url -OutputFile $jdkZip) {
        $jdkDownloaded = $true
        break
    }
}

if (-not $jdkDownloaded) {
    Write-Host "Failed to download JDK from all sources"
    exit 1
}

Write-Host "JDK download completed successfully"

# Download Maven
Write-Host "Downloading Maven..."
$mavenUrl = "https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
$mavenZip = "$envDir\maven.zip"
if (-not (Download-FileWithRetry -Url $mavenUrl -OutputFile $mavenZip)) {
    Write-Host "Failed to download Maven after multiple attempts"
    exit 1
}
Write-Host "Maven download completed successfully"

# Extract JDK
Write-Host "Extracting JDK..."
try {
    Expand-Archive -Path $jdkZip -DestinationPath "$envDir\jdk21" -Force
    Write-Host "JDK extraction completed successfully"
} catch {
    Write-Host "Error extracting JDK: $_"
    exit 1
}

# Extract Maven
Write-Host "Extracting Maven..."
try {
    Expand-Archive -Path $mavenZip -DestinationPath "$envDir\maven" -Force
    Write-Host "Maven extraction completed successfully"
} catch {
    Write-Host "Error extracting Maven: $_"
    exit 1
}

# Create environment setup script
$envScript = @"
# Set up environment variables
`$env:JAVA_HOME = "`$PSScriptRoot\jdk21\jdk-21.0.2"
`$env:MAVEN_HOME = "`$PSScriptRoot\maven\apache-maven-3.9.6"
`$env:Path = "`$env:JAVA_HOME\bin;`$env:MAVEN_HOME\bin;`$env:Path"

# Verify installations
Write-Host "Verifying Java installation..."
java -version
Write-Host "Verifying Maven installation..."
mvn -version
"@

# Save environment setup script
$envScript | Out-File -FilePath "$envDir\activate.ps1" -Encoding UTF8

# Clean up zip files
Remove-Item $jdkZip
Remove-Item $mavenZip

Write-Host "Setup completed! To activate the environment, run: .\venv\activate.ps1" 