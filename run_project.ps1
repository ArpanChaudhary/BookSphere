# Set up environment variables
$env:JAVA_HOME = Join-Path $PSScriptRoot "venv\jdk21\jdk-21.0.2+13"
$env:MAVEN_HOME = Join-Path $PSScriptRoot "venv\maven\apache-maven-3.9.6"
$env:Path = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:Path"

# Verify installations
Write-Host "Verifying Java installation..."
& "$env:JAVA_HOME\bin\java.exe" -version

Write-Host "Verifying Maven installation..."
& "$env:MAVEN_HOME\bin\mvn.cmd" -version

# Build and run the project
Write-Host "Building and running the project..."
& "$env:MAVEN_HOME\bin\mvn.cmd" clean install spring-boot:run 