import requests
import json

# 获取验证码
captcha_response = requests.get('http://localhost:8080/user/captcha/debug')
captcha_data = captcha_response.json()
print(f"获取到的验证码信息:")
print(f"Key: {captcha_data['key']}")
print(f"Code: {captcha_data['code']}")

# 注册（使用唯一用户名和手机号）
import uuid
import random
unique_suffix = str(uuid.uuid4())[:8]
# 生成有效的11位手机号（138开头）
phone_suffix = ''.join(random.choice('0123456789') for _ in range(8))
register_data = {
    "username": f"test_{unique_suffix}",
    "password": "123456",
    "confirmPassword": "123456",
    "phone": f"138{phone_suffix}",
    "email": f"test_{unique_suffix}@qq.com",
    "nickname": f"测试用户_{unique_suffix}",
    "captcha": captcha_data['code']
}

headers = {
    'Content-Type': 'application/json',
    'Captcha-Key': captcha_data['key']
}

print("\n正在注册...")
register_response = requests.post('http://localhost:8080/user/register', 
                                 data=json.dumps(register_data), 
                                 headers=headers)
print(f"注册结果: {register_response.text}")