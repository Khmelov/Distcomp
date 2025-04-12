$solutionName = Read-Host "Enter the solution name"
$basePath = "$(Get-Location)\$solutionName"

New-Item -ItemType Directory -Path $basePath -Force
Set-Location -Path $basePath

dotnet new sln -n $solutionName

### CORE
New-Item -ItemType Directory -Path "$basePath\Core" -Force
New-Item -ItemType Directory -Path "$basePath\Core\Domain" -Force
New-Item -ItemType Directory -Path "$basePath\Core\Application" -Force

dotnet new classlib -o "$basePath\Core\Domain" -n Domain
Remove-Item -Path "$basePath\Core\Domain\Class1.cs" -Force
dotnet sln add "$basePath\Core\Domain\Domain.csproj"

dotnet new classlib -o "$basePath\Core\Application" -n Application
Remove-Item -Path "$basePath\Core\Application\Class1.cs" -Force
dotnet sln add "$basePath\Core\Application\Application.csproj"

### INFRASTRUCTURE
New-Item -ItemType Directory -Path "$basePath\Infrastructure" -Force
New-Item -ItemType Directory -Path "$basePath\Infrastructure\Persistence" -Force

dotnet new classlib -o "$basePath\Infrastructure\Persistence" -n Persistence
Remove-Item -Path "$basePath\Infrastructure\Persistence\Class1.cs" -Force
dotnet sln add "$basePath\Infrastructure\Persistence\Persistence.csproj"

### PRESENTATION
New-Item -ItemType Directory -Path "$basePath\Presentation" -Force
New-Item -ItemType Directory -Path "$basePath\Presentation\API" -Force

dotnet new webapi -o "$basePath\Presentation\API" -n API
dotnet sln add "$basePath\Presentation\API\API.csproj"

Write-Host "Solution has been created successfully" -ForegroundColor Green
Set-Location ..