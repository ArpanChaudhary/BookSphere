# Create a directory for development tools
$devToolsDir = "C:\DevTools"
if (-not (Test-Path $devToolsDir)) {
    New-Item -ItemType Directory -Path $devToolsDir
}

# Download and install JDK 21
$jdkUrl = "https://download.java.net/java/GA/jdk21.0.2/13d6b82950b5bfbcd902e805eac7d7/13/GPL/openjdk-21.0.2_windows-x64_bin.zip"
$jdkZip = "$devToolsDir\jdk21.zip"
$jdkDir = "$devToolsDir\jdk-21.0.2"

Write-Host "Downloading JDK 21..."
Invoke-WebRequest -Uri $jdkUrl -OutFile $jdkZip

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

# Download and install Maven
$mavenUrl = "https://dlcdn.apache.org//maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
$mavenZip = "$devToolsDir\maven.zip"
$mavenDir = "$devToolsDir\apache-maven-3.9.6"

Write-Host "Downloading Maven..."
Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip

Write-Host "Extracting Maven..."
Expand-Archive -Path $mavenZip -DestinationPath $devToolsDir -Force

# Set MAVEN_HOME environment variable
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenDir, "User")
$env:MAVEN_HOME = $mavenDir

# Add Maven to PATH
$mavenPath = "$mavenDir\bin"
if ($currentPath -notlike "*$mavenPath*") {
    [Environment]::SetEnvironmentVariable("Path", $currentPath + ";$mavenPath", "User")
}

# Create Maven wrapper for the project
Write-Host "Creating Maven wrapper..."
mvn -N wrapper:wrapper

# Clean up downloaded files
Remove-Item $jdkZip
Remove-Item $mavenZip

Write-Host "Environment setup completed!"
Write-Host "Please restart your terminal for the changes to take effect."
Write-Host "You can verify the installation by running:"
Write-Host "java -version"
Write-Host "mvn -version" 