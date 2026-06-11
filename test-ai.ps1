# 测试 AI 接口脚本
$ErrorActionPreference = "Continue"

Write-Host "=== Step 1: 登录获取 Token ===" -ForegroundColor Green
$loginBody = '{"phone":"13800138000","code":"123456"}'
try {
    $resp = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/users/login" -Method Post -Body $loginBody -ContentType "application/json"
    Write-Host "登录成功，code=$($resp.code)" -ForegroundColor Green
    $token = $resp.data.token
    Write-Host "Token 前20位: $($token.Substring(0,20))..." -ForegroundColor Gray
} catch {
    Write-Host "登录失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails) { Write-Host "  详情: $($_.ErrorDetails.Message)" -ForegroundColor Red }
    exit 1
}

Write-Host ""
Write-Host "=== Step 2: 调用 AI 接口 ===" -ForegroundColor Green
$aiBody = '{"message":"你好，请用50字以内介绍你自己"}'
$headers = New-Object "System.Collections.Generic.Dictionary[[String],[String]]"
$headers.Add("Content-Type", "application/json")
$headers.Add("Authorization", "Bearer $token")

try {
    $aiResp = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/ai/chat" -Method Post -Body $aiBody -Headers $headers
    Write-Host ""
    Write-Host "=== AI 接口返回结果 ===" -ForegroundColor Cyan
    Write-Host "HTTP 状态: 200 OK" -ForegroundColor Green
    Write-Host "API code: $($aiResp.code)" -ForegroundColor Green
    if ($aiResp.message) { Write-Host "API message: $($aiResp.message)" -ForegroundColor Gray }
    if ($aiResp.data) {
        if ($aiResp.data.content) {
            Write-Host ""
            Write-Host "AI 回复内容:" -ForegroundColor Yellow
            Write-Host "$($aiResp.data.content)" -ForegroundColor White
        }
        if ($aiResp.data.type) { Write-Host "回复类型: $($aiResp.data.type)" -ForegroundColor Gray }
    }
    Write-Host ""
    Write-Host "=== 测试完成 ===" -ForegroundColor Green
} catch {
    Write-Host "AI 调用失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails) {
        Write-Host "错误详情:" -ForegroundColor Yellow
        Write-Host $_.ErrorDetails.Message -ForegroundColor Red
    }
    Write-Host ""
    Write-Host "=== 请在后端控制台查看 [AI] 开头的详细日志 ===" -ForegroundColor Yellow
}
