# ============================================================
# 5D 好车 · Windows PowerShell 环境变量加载脚本
# ============================================================
# 用法（在仓库根目录执行）：
#   . .\car-trade-backend\scripts\load-env.ps1
#
# 配套 .env.local 文件位于 car-trade-backend/.env.local
# ============================================================

$envFile = Join-Path $PSScriptRoot "..\\.env.local"
if (-not (Test-Path $envFile)) {
    Write-Host "[load-env] .env.local not found at $envFile" -ForegroundColor Yellow
    Write-Host "[load-env] Please copy .env.local.example to .env.local and fill in values." -ForegroundColor Yellow
    return
}

Write-Host "[load-env] loading $envFile ..." -ForegroundColor Cyan
$count = 0
Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    # 跳过空行与注释
    if ([string]::IsNullOrWhiteSpace($line)) { return }
    if ($line.StartsWith('#')) { return }
    # KEY=VALUE
    $idx = $line.IndexOf('=')
    if ($idx -lt 1) { return }
    $key = $line.Substring(0, $idx).Trim()
    $val = $line.Substring($idx + 1).Trim()
    # 去掉可选的引号
    if (($val.StartsWith('"') -and $val.EndsWith('"')) -or
        ($val.StartsWith("'") -and $val.EndsWith("'"))) {
        $val = $val.Substring(1, $val.Length - 2)
    }
    Set-Item -Path "Env:$key" -Value $val
    $count++
    Write-Host "  $key = $(if ($key -match 'SECRET|PASSWORD|KEY') { '***' } else { $val })"
}
Write-Host "[load-env] $count env var(s) loaded." -ForegroundColor Green
