# CeyGreen Docker Storage Cleaner (Windows PowerShell)
# Safely frees gigabytes of Docker cache, dangling images, and container logs without deleting database data.

Write-Host "=========================================" -ForegroundColor Green
Write-Host "   CeyGreen Docker Storage Cleaner       " -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green

Write-Host "`n[1/4] Pruning stopped containers..." -ForegroundColor Cyan
docker container prune -f

Write-Host "`n[2/4] Pruning dangling and unused images..." -ForegroundColor Cyan
docker image prune -af

Write-Host "`n[3/4] Pruning Docker BuildKit build cache..." -ForegroundColor Cyan
docker builder prune -af

Write-Host "`n[4/4] Pruning unused Docker networks..." -ForegroundColor Cyan
docker network prune -f

Write-Host "`n=========================================" -ForegroundColor Green
Write-Host "Docker Storage Summary:" -ForegroundColor Green
docker system df
Write-Host "=========================================" -ForegroundColor Green
Write-Host "`nTip for Docker Desktop on Windows: If you use WSL2, you can also shrink the virtual disk file with:" -ForegroundColor Yellow
Write-Host "wsl --shutdown; Optimize-VHD -Path '$env:LOCALAPPDATA\Docker\wsl\data\ext4.vhdx' -Mode Full" -ForegroundColor Yellow
