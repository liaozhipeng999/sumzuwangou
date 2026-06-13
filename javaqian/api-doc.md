# 商城系统接口文档（前端版）

## 服务地址
```
http://localhost:8080
```

---

## 一、验证码接口

### 获取验证码
```
GET /user/captcha
```

**前端代码**：
```javascript
const res = await fetch('http://localhost:8080/user/captcha');
const captchaKey = res.headers.get('Captcha-Key');  // 保存！
const blob = await res.blob();
const imgUrl = URL.createObjectURL(blob);
```

**返回**：图片二进制流 + 响应头 `Captcha-Key`

---

## 二、注册接口

### 用户注册
```
POST /user/register
Headers:
  Content-Type: application/json
  Captcha-Key: {之前获取的key}
```

**前端代码**：
```javascript
const data = {
    username: "test001",
    password: "123456",
    confirmPassword: "123456",
    phone: "13800138000",
    email: "test@qq.com",
    nickname: "测试用户",
    captcha: "验证码"
};

const res = await fetch('http://localhost:8080/user/register', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Captcha-Key': localStorage.getItem('captchaKey')
    },
    body: JSON.stringify(data)
});
const result = await res.json();  // {code:200, message:"注册成功"}
```

**参数说明**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 账号(3-20位) |
| password | String | 是 | 密码(6-20位) |
| confirmPassword | String | 是 | 确认密码 |
| phone | String | 是 | 手机号 |
| email | String | 否 | 邮箱 |
| nickname | String | 否 | 昵称 |
| captcha | String | 是 | 验证码 |

---

## 三、商品推荐接口

### 综合推荐
```
GET /recommend/products?limit=10&userId=1
```

**前端代码**：
```javascript
const res = await fetch('http://localhost:8080/recommend/products?limit=10');
const result = await res.json();
console.log(result.data);  // 商品数组
```

### 热销商品
```
GET /recommend/hot?limit=10
```

### 新品推荐
```
GET /recommend/new?limit=10
```

### 分类推荐
```
GET /recommend/category/{categoryId}?limit=10
```

### 随机推荐
```
GET /recommend/random?limit=10
```

### 记录浏览
```
POST /recommend/record-view?productId=1&userId=1
```

---

## 四、商品数据结构

```javascript
{
    id: 4,                       // 商品ID
    productName: "华为Mate60 Pro", // 商品名称
    price: 6999.00,              // 售价
    originalPrice: 7999.00,       // 原价
    stock: 100,                  // 库存
    sales: 50,                   // 销量
    mainImage: "https://...",    // 主图
    brief: "简介",               // 简介
    isHot: 1,                   // 是否热门(1是)
    isNew: 0,                   // 是否新品(1是)
    status: 1,                  // 状态(1上架)
    categoryId: 1                // 分类ID
}
```

---

## 五、状态码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 失败 |
| 500 | 服务器错误 |

---

## 六、测试账号

| 账号 | 密码 |
|------|------|
| test001 | 123456 |
| test002 | 123456 |

**测试步骤**：
1. 调用 `GET /user/captcha` 获取验证码图片
2. 记录响应头 `Captcha-Key`
3. 填入注册信息调用注册接口

---

## 七、完整示例

```html
<!DOCTYPE html>
<html>
<head><title>接口测试</title></head>
<body>
    <h2>验证码</h2>
    <img id="captcha-img" onclick="refresh()"/>
    <input id="captcha" placeholder="输入验证码"/>
    <button onclick="refresh()">刷新</button>

    <h2>注册</h2>
    <input id="username" value="test003"/><br/>
    <input id="password" type="password" value="123456"/><br/>
    <input id="confirmPassword" type="password" value="123456"/><br/>
    <input id="phone" value="13800138003"/><br/>
    <input id="email" value="test3@qq.com"/><br/>
    <input id="nickname" value="测试"/><br/>
    <button onclick="register()">注册</button>

    <h2>推荐商品</h2>
    <button onclick="getProducts()">获取10个商品</button>
    <div id="products"></div>

    <script>
        let captchaKey = '';

        async function refresh() {
            const res = await fetch('http://localhost:8080/user/captcha');
            captchaKey = res.headers.get('Captcha-Key');
            document.getElementById('captcha-img').src = URL.createObjectURL(await res.blob());
        }

        async function register() {
            const data = {
                username: username.value,
                password: password.value,
                confirmPassword: confirmPassword.value,
                phone: phone.value,
                email: email.value,
                nickname: nickname.value,
                captcha: captcha.value
            };
            const res = await fetch('http://localhost:8080/user/register', {
                method: 'POST',
                headers: {'Content-Type':'application/json', 'Captcha-Key': captchaKey},
                body: JSON.stringify(data)
            });
            alert((await res.json()).message);
        }

        async function getProducts() {
            const res = await fetch('http://localhost:8080/recommend/products?limit=10');
            const result = await res.json();
            products.innerHTML = result.data.map(p =>
                `<div style="border:1px solid #ccc;padding:5px;margin:5px;">
                    <b>${p.productName}</b><br/>¥${p.price}
                </div>`
            ).join('');
        }

        refresh();
    </script>
</body>
</html>
```

---

## 八、跨域

后端已配置CORS，允许跨域访问。