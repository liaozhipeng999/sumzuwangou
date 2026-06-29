<template>
  <div class="product-add">
    <el-card>
      <template #header>
        <span>上架商品</span>
      </template>
      
      <el-form :model="form" :rules="rules" ref="formRef" label-width="140px" style="max-width: 900px;">
        <!-- 必填字段 -->
        <el-form-item label="商品名称" prop="productName">
          <el-input 
            v-model="form.productName" 
            placeholder="请输入商品名称" 
            maxlength="255"
            @blur="validateField('productName')"
          />
        </el-form-item>
        
        <el-form-item label="商品分类" prop="categoryId">
          <el-select 
            v-model="form.categoryId" 
            placeholder="请选择商品分类" 
            style="width: 100%;"
            @change="validateField('categoryId')"
          >
            <el-option label="手机数码" :value="1" />
            <el-option label="电脑办公" :value="2" />
            <el-option label="服装鞋帽" :value="3" />
            <el-option label="家用电器" :value="4" />
            <el-option label="食品生鲜" :value="5" />
            <el-option label="美妆护肤" :value="6" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="商品售价" prop="price">
          <el-input 
            v-model.number="form.price" 
            type="number" 
            placeholder="请输入商品售价" 
            style="width: 200px;"
            @blur="validateField('price')"
          />
          <span class="price-unit">元</span>
        </el-form-item>
        
        <el-form-item label="原价(划线价)" prop="originalPrice">
          <el-input 
            v-model.number="form.originalPrice" 
            type="number" 
            placeholder="请输入原价（可选）" 
            style="width: 200px;"
            @blur="validateField('originalPrice')"
          />
          <span class="price-unit">元</span>
        </el-form-item>
        
        <el-form-item label="库存数量" prop="stock">
          <el-input-number 
            v-model="form.stock" 
            :min="0" 
            style="width: 200px;"
            @change="validateField('stock')"
          />
        </el-form-item>
        
        <el-form-item label="主图URL" prop="mainImage">
          <el-input 
            v-model="form.mainImage" 
            placeholder="请输入商品主图URL" 
            maxlength="512"
            @blur="validateField('mainImage')"
          />
          <div class="form-tip">支持 JPG/PNG 格式，建议尺寸 800x800</div>
        </el-form-item>
        
        <!-- 可选字段 -->
        <el-form-item label="商品编码">
          <el-input 
            v-model="form.productCode" 
            placeholder="系统自动生成（可选）" 
            maxlength="100"
            disabled
          />
          <div class="form-tip">留空则自动生成SKU编码</div>
        </el-form-item>
        
        <el-form-item label="商品简介">
          <el-input
            v-model="form.brief"
            type="textarea"
            :rows="3"
            placeholder="请输入商品简介（可选）"
            maxlength="500"
          />
        </el-form-item>
        
        <el-form-item label="商品状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="商品标签">
          <div class="tag-container">
            <div class="tag-list">
              <el-tag 
                v-for="(tag, index) in form.tags" 
                :key="index" 
                :style="{ backgroundColor: tag.tagColor, color: getContrastColor(tag.tagColor) }"
                closable
                @close="removeTag(index)"
              >
                {{ tag.tagName }}
              </el-tag>
            </div>
            <div class="tag-input-row">
              <el-input 
                v-model="newTagName" 
                placeholder="标签名称" 
                maxlength="20"
                style="width: 150px; margin-right: 10px;"
              />
              <el-color-picker 
                v-model="newTagColor" 
                show-alpha
                style="width: 100px; margin-right: 10px;"
              />
              <el-button type="primary" size="small" @click="addTag">添加标签</el-button>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">确认上架</el-button>
          <el-button @click="handleReset">重置表单</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useMerchantStore } from '@/stores/merchant'
import { addProduct, type ProductTag } from '@/api/product'

const merchantStore = useMerchantStore()
const formRef = ref()
const loading = ref(false)

const newTagName = ref('')
const newTagColor = ref('#FF5733')

const form = reactive({
  merchantId: 0,
  productName: '',
  categoryId: 0,
  price: 0,
  originalPrice: null as number | null,
  stock: 0,
  mainImage: '',
  productCode: '',
  brief: '',
  status: 1,
  tags: [] as ProductTag[]
})

const rules = {
  productName: [
    { required: true, message: '商品名称不能为空', trigger: 'blur' },
    { max: 255, message: '商品名称不能超过255个字符', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9\s_.\-]+$/, message: '商品名称只能包含中文、英文、数字、空格、下划线和连字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' },
    { type: 'number', min: 1, max: 6, message: '请选择有效的商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '商品售价不能为空', trigger: 'blur' },
    { type: 'number', min: 0, message: '商品售价不能为负数', trigger: 'blur' },
    { type: 'number', max: 99999999.99, message: '商品售价不能超过99999999.99', trigger: 'blur' },
    { validator: validatePricePrecision, trigger: 'blur' }
  ],
  originalPrice: [
    { type: 'number', min: 0, message: '原价不能为负数', trigger: 'blur' },
    { type: 'number', max: 99999999.99, message: '原价不能超过99999999.99', trigger: 'blur' },
    { validator: validatePricePrecision, trigger: 'blur' },
    { validator: validateOriginalPrice, trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '库存数量不能为空', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存数量不能为负数', trigger: 'blur' },
    { type: 'number', max: 999999, message: '库存数量不能超过999999', trigger: 'blur' }
  ],
  mainImage: [
    { required: true, message: '主图URL不能为空', trigger: 'blur' },
    { max: 512, message: '主图URL不能超过512个字符', trigger: 'blur' },
    { pattern: /^https?:\/\/[\w\-._~:/?#[\]@!$&'()*+,;=%]+$/, message: '请输入有效的URL地址', trigger: 'blur' }
  ]
}

// 校验价格精度（最多两位小数）
function validatePricePrecision(_rule: any, value: number, callback: any) {
  if (value === null || value === undefined) {
    callback()
    return
  }
  const priceStr = String(value)
  const decimalPart = priceStr.split('.')[1]
  if (decimalPart && decimalPart.length > 2) {
    callback(new Error('价格最多保留两位小数'))
  } else {
    callback()
  }
}

// 校验原价不能低于售价
function validateOriginalPrice(_rule: any, value: number, callback: any) {
  if (value === null || value === undefined) {
    callback()
    return
  }
  if (form.price > 0 && value < form.price) {
    callback(new Error('原价不能低于售价'))
  } else {
    callback()
  }
}

// 实时校验字段
const validateField = (field: string) => {
  formRef.value?.validateField(field)
}

// 设置默认测试数据
const setDefaultData = () => {
  form.productName = '智能手表 Pro Max'
  form.categoryId = 1
  form.price = 1299.00
  form.originalPrice = 1599.00
  form.stock = 100
  form.mainImage = 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=smart%20watch%20product%20photo%20white%20background%20modern%20design&image_size=square_hd'
  form.brief = '最新款智能手表'
  form.status = 1
  form.tags = [
    { tagName: '智能穿戴', tagColor: '#FF5733' },
    { tagName: '热销', tagColor: '#3366FF' }
  ]
}

// 获取对比度颜色（确保文字可读）
const getContrastColor = (hexColor: string): string => {
  const r = parseInt(hexColor.slice(1, 3), 16)
  const g = parseInt(hexColor.slice(3, 5), 16)
  const b = parseInt(hexColor.slice(5, 7), 16)
  const brightness = (r * 299 + g * 587 + b * 114) / 1000
  return brightness > 128 ? '#000000' : '#ffffff'
}

// 添加标签
const addTag = () => {
  if (!newTagName.value.trim()) {
    ElMessage.warning('请输入标签名称')
    return
  }
  if (form.tags.length >= 5) {
    ElMessage.warning('最多添加5个标签')
    return
  }
  form.tags.push({
    tagName: newTagName.value.trim(),
    tagColor: newTagColor.value
  })
  newTagName.value = ''
  newTagColor.value = '#FF5733'
}

// 删除标签
const removeTag = (index: number) => {
  form.tags.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  // 设置商家ID
  form.merchantId = merchantStore.merchantInfo?.id || 1
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const data = {
          merchantId: form.merchantId,
          productName: form.productName,
          categoryId: form.categoryId,
          price: form.price,
          stock: form.stock,
          mainImage: form.mainImage,
          productCode: form.productCode || undefined,
          originalPrice: form.originalPrice || undefined,
          brief: form.brief || undefined,
          status: form.status,
          tags: form.tags.length > 0 ? form.tags : undefined
        }
        
        const res = await addProduct(data)
        
        if (res.code === 200) {
          ElMessage.success('商品上架成功！')
          handleReset()
        } else {
          ElMessage.error(res.message || '商品上架失败')
        }
      } catch (error: any) {
        ElMessage.error(error.message || '商品上架失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  formRef.value?.resetFields()
  form.originalPrice = null
  form.productCode = ''
  form.brief = ''
  form.status = 1
  form.tags = []
  newTagName.value = ''
  newTagColor.value = '#FF5733'
}

// 页面加载时设置默认测试数据
onMounted(() => {
  setDefaultData()
})
</script>

<style scoped>
.product-add {
  padding: 0;
}

.price-unit {
  margin-left: 10px;
  color: #909399;
}

.form-tip {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}

.tag-container {
  margin-top: 10px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.tag-input-row {
  display: flex;
  align-items: center;
}
</style>
