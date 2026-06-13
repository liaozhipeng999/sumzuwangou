@echo off
setlocal

:: 获取验证码
powershell -Command "Invoke-WebRequest -Uri 'http://localhost:8080/user/captcha/debug' -Method Get -UseBasicParsing | Select-Object -ExpandProperty Content"

echo.
echo "请复制上面的 key 和 code，然后在 Postman 中测试注册接口"
pause