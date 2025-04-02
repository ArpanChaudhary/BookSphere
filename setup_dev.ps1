# Function to check if a command exists
function Test-Command($command) {
    return [bool](Get-Command -Name $command -ErrorAction SilentlyContinue)
}

# Function to download a file
function Download-File {
    param (
        [string]$url,
        [string]$outputFile
    )
    try {
        Write-Host "Downloading $url to $outputFile"
        Invoke-WebRequest -Uri $url -OutFile $outputFile
        return $true
    }
    catch {
        Write-Host "Error downloading file: $_"
        return $false
    }
}

# Create a directory for development tools
$devToolsDir = "C:\DevTools"
if (-not (Test-Path $devToolsDir)) {
    New-Item -ItemType Directory -Path $devToolsDir
}

# Check if Java 21 is installed
$javaVersion = java -version 2>&1
if ($javaVersion -notlike "*21*") {
    Write-Host "Java 21 is not installed. Installing..."
    
    # Download OpenJDK 21
    $jdkUrl = "https://download.java.net/java/GA/jdk21.0.2/13d6b82950b5bfbcd902e805eac7d7/13/GPL/openjdk-21.0.2_windows-x64_bin.zip"
    $jdkZip = "$devToolsDir\jdk21.zip"
    $jdkDir = "$devToolsDir\jdk-21.0.2"
    
    if (Download-File -url $jdkUrl -outputFile $jdkZip) {
        Write-Host "Extracting JDK..."
        Expand-Archive -Path $jdkZip -DestinationPath $devToolsDir -Force
        
        # Set JAVA_HOME environment variable
        [Environment]::SetEnvironmentVariable("JAVA_HOME", $jdkDir, "User")
        $env:JAVA_HOME = $jdkDir
        
        # Add Java to PATH
        $javaPath = "$jdkDir\bin"
        $currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
        if ($currentPath -notlike "*$javaPath*") {
            [Environment]::SetEnvironmentVariable("Path", $currentPath + ";$javaPath", "User")
        }
        
        Remove-Item $jdkZip
        Write-Host "Java 21 installed successfully!"
    }
    else {
        Write-Host "Failed to download Java 21. Please install it manually from: https://www.oracle.com/java/technologies/downloads/#java21"
        exit 1
    }
}

# Check if Maven is installed
if (-not (Test-Command "mvn")) {
    Write-Host "Maven is not installed. Installing..."
    
    # Download Maven
    $mavenUrl = "https://dlcdn.apache.org//maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
    $mavenZip = "$devToolsDir\maven.zip"
    $mavenDir = "$devToolsDir\apache-maven-3.9.6"
    
    if (Download-File -url $mavenUrl -outputFile $mavenZip) {
        Write-Host "Extracting Maven..."
        Expand-Archive -Path $mavenZip -DestinationPath $devToolsDir -Force
        
        # Set MAVEN_HOME environment variable
        [Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenDir, "User")
        $env:MAVEN_HOME = $mavenDir
        
        # Add Maven to PATH
        $mavenPath = "$mavenDir\bin"
        $currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
        if ($currentPath -notlike "*$mavenPath*") {
            [Environment]::SetEnvironmentVariable("Path", $currentPath + ";$mavenPath", "User")
        }
        
        Remove-Item $mavenZip
        Write-Host "Maven installed successfully!"
    }
    else {
        Write-Host "Failed to download Maven. Please install it manually from: https://maven.apache.org/download.cgi"
        exit 1
    }
}

# Create Maven wrapper for the project
Write-Host "Creating Maven wrapper..."
if (Test-Command "mvn") {
    mvn -N wrapper:wrapper
}
else {
    Write-Host "Maven wrapper creation failed. Please run 'mvn -N wrapper:wrapper' manually after installing Maven."
}

Write-Host "`nEnvironment setup completed!"
Write-Host "Please restart your terminal for the changes to take effect."
Write-Host "`nYou can verify the installation by running:"
Write-Host "java -version"
Write-Host "mvn -version" 