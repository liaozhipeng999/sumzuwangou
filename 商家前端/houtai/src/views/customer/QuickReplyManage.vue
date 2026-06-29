<template>
  <div class="quick-reply-manage">
    <div class="page-header">
      <h2>快捷回复管理</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        新增回复
      </el-button>
    </div>

    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="replyList.length === 0" class="empty-wrap">
      <el-empty description="暂无快捷回复，点击上方按钮添加">
        <el-button type="primary" @click="showAddDialog = true">新增回复</el-button>
      </el-empty>
    </div>

    <el-table v-else :data="replyList" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="content" label="回复内容" min-width="300" />
      <el-table-column prop="sort" label="排序" width="100" align="center" />
      <el-table-column label="操作" width="120" align="center">
        <template #default="{ row }">
          <el-popconfirm title="确定删除该快捷回复？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" size="small" text>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增弹窗 -->
    <el-dialog v-model="showAddDialog" title="新增快捷回复" width="480px" @closed="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="回复内容" required>
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="3"
            placeholder="请输入快捷回复内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px">数值越小越靠前</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleAdd">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getMerchantQuickReplies, addQuickReply, deleteQuickReply } from '@/api/customer'
import type { QuickReply } from '@/api/customer'
import { useMerchantStore } from '@/stores/merchant'

const merchantStore = useMerchantStore()
const shopId = computed(() => merchantStore.merchantInfo?.id || 0)

const loading = ref(false)
const submitting = ref(false)
const replyList = ref<QuickReply[]>([])
const showAddDialog = ref(false)
const form = ref({ content: '', sort: 0 })

async function loadList() {
  if (!shopId.value) return
  loading.value = true
  try {
    const res = await getMerchantQuickReplies(shopId.value)
    if (res.code === 200) {
      replyList.value = res.data || []
    }
  } catch (e) {
    console.error('加载快捷回复失败', e)
  } finally {
    loading.value = false
  }
}

async function handleAdd() {
  if (!form.value.content.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  submitting.value = true
  try {
    const res = await addQuickReply({
      shopId: shopId.value,
      content: form.value.content.trim(),
      sort: form.value.sort
    })
    if (res.code === 200) {
      ElMessage.success('添加成功')
      showAddDialog.value = false
      loadList()
    }
  } catch (e) {
    ElMessage.error('添加失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id: number) {
  try {
    const res = await deleteQuickReply(id, shopId.value)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadList()
    }
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function resetForm() {
  form.value = { content: '', sort: 0 }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.quick-reply-manage {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  min-height: calc(100vh - 140px);
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.loading-wrap {
  padding: 20px;
}

.empty-wrap {
  padding: 80px 0;
}
</style>
