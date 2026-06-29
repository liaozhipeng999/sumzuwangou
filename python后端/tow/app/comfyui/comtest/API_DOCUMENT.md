# 图像生成 API 接口文档

## 服务信息

| 项目 | 值 |
|------|-----|
| 服务名称 | 图像生成 API |
| 版本 | v2.0.0 |
| 基础地址 | http://localhost:8088 |
| API 文档 | http://localhost:8088/docs |
| 交互文档 | http://localhost:8088/redoc |

---

## 接口列表

| 接口 | 方法 | 说明 |
|------|------|------|
| `/health` | GET | 健康检查 |
| `/api/generate` | POST | 生成图像 |
| `/api/queue` | GET | 查看任务队列 |
| `/images/{path}` | GET | 访问生成的图片 |

---

## 1. 健康检查

### 请求

```
GET /health
```

### 响应

```json
{
  "status": "healthy",
  "comfyui_connected": true,
  "default_output_dir": "F:\\temp\\image"
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| status | string | 服务状态：`healthy` 正常，`degraded` 降级 |
| comfyui_connected | boolean | ComfyUI 服务是否连接 |
| default_output_dir | string | 默认输出目录 |

---

## 2. 生成图像

### 请求

```
POST /api/generate
Content-Type: application/json
```

### 请求参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| prompt | string | 是 | - | 正提示词，描述想要生成的图像内容 |
| negative_prompt | string | 否 | "" | 负提示词，描述不想要的内容 |
| width | integer | 否 | 1024 | 图片宽度（64-4096） |
| height | integer | 否 | 1024 | 图片高度（64-4096） |
| output_dir | string | 否 | null | 输出目录，不存在会自动创建 |

### 请求示例

```json
{
  "prompt": "精美电商产品展示图，高端护肤品套装，白色背景，专业摄影风格，光线明亮",
  "negative_prompt": "模糊，低质量，水印，文字，杂乱背景",
  "width": 1024,
  "height": 1024,
  "output_dir": "F:\\temp\\image\\banner"
}
```

### 响应示例

**成功响应：**

```json
{
  "success": true,
  "message": "图像生成成功",
  "image_path": "F:\\temp\\image\\banner\\z-image_20260616_185944.png",
  "image_url": "/images/z-image_20260616_185944.png",
  "file_size": 857.73
}
```

**失败响应：**

```json
{
  "success": false,
  "message": "图像生成失败，请检查 ComfyUI 服务日志",
  "image_path": null,
  "image_url": null,
  "file_size": null
}
```

### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| success | boolean | 是否成功 |
| message | string | 结果消息 |
| image_path | string | 图片绝对路径（成功时返回） |
| image_url | string | 图片访问 URL（成功时返回） |
| file_size | float | 文件大小（KB） |

---

## 3. 查看任务队列

### 请求

```
GET /api/queue
```

### 响应

```json
{
  "queue_running": [],
  "queue_pending": []
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| queue_running | array | 正在运行的任务 |
| queue_pending | array | 等待中的任务 |

---

## 4. 访问图片

### 请求

```
GET /images/{path}
```

### 参数

| 参数 | 类型 | 说明 |
|------|------|------|
| path | string | 图片路径（使用生成接口返回的 image_url） |

### 响应

返回图片文件（PNG 格式）

---

## 调用示例

### cURL

```bash
# 健康检查
curl http://localhost:8088/health

# 生成图像
curl -X POST "http://localhost:8088/api/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "精美电商产品展示图，高端护肤品套装，白色背景",
    "negative_prompt": "模糊，低质量，水印",
    "width": 1024,
    "height": 1024,
    "output_dir": "F:\\temp\\image\\banner"
  }'

# 访问图片
curl http://localhost:8088/images/z-image_20260616_185944.png --output image.png
```

### JavaScript (Fetch)

```javascript
// 生成图像
async function generateImage() {
  const response = await fetch('http://localhost:8088/api/generate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      prompt: '精美电商产品展示图，高端护肤品套装，白色背景',
      negative_prompt: '模糊，低质量，水印',
      width: 1024,
      height: 1024,
      output_dir: 'F:\\temp\\image\\banner'
    })
  });
  
  const data = await response.json();
  console.log('生成结果:', data);
  
  if (data.success) {
    // 获取图片 URL
    const imageUrl = `http://localhost:8088${data.image_url}`;
    console.log('图片地址:', imageUrl);
  }
}

generateImage();
```

### JavaScript (Axios)

```javascript
const axios = require('axios');

async function generateImage() {
  try {
    const response = await axios.post('http://localhost:8088/api/generate', {
      prompt: '精美电商产品展示图，高端护肤品套装，白色背景',
      negative_prompt: '模糊，低质量，水印',
      width: 1024,
      height: 1024,
      output_dir: 'F:\\temp\\image\\banner'
    });
    
    console.log('生成结果:', response.data);
    
    if (response.data.success) {
      const imageUrl = `http://localhost:8088${response.data.image_url}`;
      console.log('图片地址:', imageUrl);
    }
  } catch (error) {
    console.error('请求失败:', error.message);
  }
}

generateImage();
```

### Python (Requests)

```python
import requests

# 生成图像
def generate_image():
    url = 'http://localhost:8088/api/generate'
    data = {
        'prompt': '精美电商产品展示图，高端护肤品套装，白色背景',
        'negative_prompt': '模糊，低质量，水印',
        'width': 1024,
        'height': 1024,
        'output_dir': r'F:\temp\image\banner'
    }
    
    response = requests.post(url, json=data)
    result = response.json()
    
    print('生成结果:', result)
    
    if result['success']:
        image_url = f"http://localhost:8088{result['image_url']}"
        print('图片地址:', image_url)
        
        # 下载图片
        img_response = requests.get(image_url)
        with open('downloaded_image.png', 'wb') as f:
            f.write(img_response.content)
        print('图片已下载')

generate_image()
```

### Java (OkHttp)

```java
import okhttp3.*;
import org.json.JSONObject;

public class ImageGenerator {
    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient();
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("prompt", "精美电商产品展示图，高端护肤品套装，白色背景");
        requestBody.put("negative_prompt", "模糊，低质量，水印");
        requestBody.put("width", 1024);
        requestBody.put("height", 1024);
        requestBody.put("output_dir", "F:\\temp\\image\\banner");
        
        Request request = new Request.Builder()
            .url("http://localhost:8088/api/generate")
            .post(RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
            ))
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            JSONObject result = new JSONObject(response.body().string());
            System.out.println("生成结果: " + result);
            
            if (result.getBoolean("success")) {
                String imageUrl = "http://localhost:8088" + result.getString("image_url");
                System.out.println("图片地址: " + imageUrl);
            }
        }
    }
}
```

---

## 常见场景示例

### 场景 1：生成移动端轮播图 (1024x1024)

```json
{
  "prompt": "精美电商产品展示图，高端护肤品套装，白色背景，专业摄影风格",
  "negative_prompt": "模糊，低质量，水印，文字",
  "width": 1024,
  "height": 1024,
  "output_dir": "F:\\temp\\image\\banner"
}
```

### 场景 2：生成商品详情图 (800x800)

```json
{
  "prompt": "时尚运动鞋，黑色配色，白色背景，360度展示，细节清晰",
  "negative_prompt": "模糊，划痕，污渍",
  "width": 800,
  "height": 800,
  "output_dir": "F:\\temp\\image\\product"
}
```

### 场景 3：生成横版宣传图 (1920x1080)

```json
{
  "prompt": "电商促销活动横幅，红色主题，优惠券元素，喜庆氛围",
  "negative_prompt": "低分辨率，模糊，颜色暗淡",
  "width": 1920,
  "height": 1080,
  "output_dir": "F:\\temp\\image\\banner"
}
```

### 场景 4：生成竖版海报 (1080x1920)

```json
{
  "prompt": "新品上市海报，简约设计，高端大气，品牌展示",
  "negative_prompt": "杂乱，低质量，不专业",
  "width": 1080,
  "height": 1920,
  "output_dir": "F:\\temp\\image\\poster"
}
```

---

## 错误码说明

| HTTP 状态码 | 说明 |
|-------------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 503 | ComfyUI 服务不可用 |

---

## 注意事项

1. **ComfyUI 服务**：使用前请确保 ComfyUI 服务已启动（默认端口 8188）
2. **输出目录**：如果指定的输出目录不存在，系统会自动创建
3. **图片尺寸**：建议使用 64 的倍数，范围 64-4096
4. **生成时间**：每张图片约需 30-60 秒，请耐心等待
5. **提示词**：
   - 正提示词：描述你想要的内容
   - 负提示词：描述你不想要的内容（如模糊、水印等）

---

## 联系方式

如有问题，请联系开发团队。
