<template>
  <div class="auth-letter-detail-page">
    <!-- 基本信息区 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <span class="card-title">基本信息</span>
      </template>
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px" class="info-form">
        <div class="form-row">
          <el-form-item label="授权书名称" prop="name" required>
            <el-input v-model="formData.name" placeholder="请输入授权书名称" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item label="授权发布层级" prop="authPublishLevel" required>
            <el-select v-model="formData.authPublishLevel" multiple collapse-tags collapse-tags-tooltip placeholder="请选择">
              <el-option v-for="item in authPublishLevelOptions" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="授权发布组织" prop="authPublishOrg" required>
            <el-tree-select
              v-model="formData.authPublishOrg"
              multiple
              collapse-tags
              collapse-tags-tooltip
              placeholder="请选择"
              check-strictly
              :data="orgTreeData"
              :props="{ label: 'name', value: 'code', children: 'children' }"
            />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="授权对象层级" prop="authTargetLevel" required>
            <el-select v-model="formData.authTargetLevel" multiple collapse-tags collapse-tags-tooltip placeholder="请选择">
              <el-option v-for="item in authTargetLevelOptions" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="适用区域" prop="applicableRegion" required>
            <el-select v-model="formData.applicableRegion" multiple collapse-tags collapse-tags-tooltip placeholder="请选择">
              <el-option v-for="item in applicableRegionOptions" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="授权书发布年份" prop="publishYear" required>
            <el-date-picker v-model="formData.publishYear" type="year" placeholder="请选择年份" value-format="YYYY" />
          </el-form-item>
        </div>
        <div class="form-row single">
          <el-form-item label="授权书内容摘要" prop="contentSummary" required>
            <el-input
              v-model="formData.contentSummary"
              type="textarea"
              :rows="4"
              placeholder="请输入授权书内容摘要"
              maxlength="4000"
              show-word-limit
            />
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 附件区块 -->
    <el-card class="attachment-card" shadow="never">
      <div class="section-header">
        <span class="section-label">附件</span>
        <div class="section-content">
          <div class="action-buttons">
            <el-button type="primary" @click="handleUpload">
              <el-icon><Upload /></el-icon>
              上传
            </el-button>
            <el-button @click="handleDownloadAttachment">
              <el-icon><Download /></el-icon>
              下载
            </el-button>
            <el-button type="danger" @click="handleDeleteAttachment">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
          <el-table
            ref="attachmentTableRef"
            :data="attachmentData"
            @selection-change="handleAttachmentSelectionChange"
            stripe
            border
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column type="index" label="序号" width="60" align="center">
              <template #default="{ $index }">
                {{ (attachmentPagination.pageNum - 1) * attachmentPagination.pageSize + $index + 1 }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleDeleteAttachmentRow(row)">删除</el-button>
                <el-button link type="primary" @click="handleDownloadAttachmentRow(row)">下载</el-button>
                <el-button link type="warning" @click="handleEncryptAttachmentRow(row)">加密</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="fileName" label="文档名称" min-width="180">
              <template #default="{ row }">
                <el-link type="primary" @click="handleDownloadAttachmentRow(row)">{{ row.fileName }}</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="docType" label="文档类型" width="120">
              <template #default="{ row }">
                {{ getDocTypeName(row.docType) }}
              </template>
            </el-table-column>
            <el-table-column prop="createdBy" label="创建人" width="100" align="center" />
            <el-table-column prop="createdAt" label="创建时间" width="160" align="center" />
            <el-table-column prop="updatedBy" label="更新人" width="100" align="center" />
            <el-table-column prop="updatedAt" label="更新时间" width="160" align="center" />
          </el-table>
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="attachmentPagination.pageNum"
              v-model:page-size="attachmentPagination.pageSize"
              :page-sizes="[10, 20, 50]"
              :total="attachmentPagination.total"
              layout="total, sizes, prev, pager, next"
              @size-change="handleAttachmentSizeChange"
              @current-change="handleAttachmentCurrentChange"
            />
          </div>
        </div>
      </div>
    </el-card>

    <!-- 授权规则区块 -->
    <el-card class="rule-card" shadow="never">
      <div class="section-header">
        <span class="section-label">授权规则</span>
        <div class="section-content">
          <div class="action-buttons">
            <el-button type="primary" @click="handleAddScene">
              <el-icon><Plus /></el-icon>
              添加场景
            </el-button>
            <el-button type="danger" @click="handleDeleteScene">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
          <el-table
            ref="sceneTableRef"
            :data="sceneData"
            @selection-change="handleSceneSelectionChange"
            stripe
            border
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column type="index" label="序号" width="60" align="center">
              <template #default="{ $index }">
                {{ (scenePagination.pageNum - 1) * scenePagination.pageSize + $index + 1 }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleEditScene(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteSceneRow(row)">删除</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="sceneName" label="场景" min-width="120" />
            <el-table-column prop="industry" label="产业" min-width="100" />
            <el-table-column prop="businessScenario" label="业务场景" min-width="120" />
            <el-table-column prop="ruleDetail" label="具体规则" min-width="150" show-overflow-tooltip />
            <el-table-column prop="decisionLevel" label="决策层级" width="100" />
            <el-table-column prop="createdBy" label="创建人" width="100" align="center" />
            <el-table-column prop="createdAt" label="创建时间" width="160" align="center" />
            <el-table-column prop="updatedBy" label="更新人" width="100" align="center" />
            <el-table-column prop="updatedAt" label="更新时间" width="160" align="center" />
          </el-table>
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="scenePagination.pageNum"
              v-model:page-size="scenePagination.pageSize"
              :page-sizes="[10, 20, 50]"
              :total="scenePagination.total"
              layout="total, sizes, prev, pager, next"
              @size-change="handleSceneSizeChange"
              @current-change="handleSceneCurrentChange"
            />
          </div>
        </div>
      </div>
    </el-card>

    <!-- 底部悬浮按钮 -->
    <div class="bottom-actions">
      <el-button @click="handleSave" :disabled="!canSave">
        <el-icon><DocumentChecked /></el-icon>
        保存
      </el-button>
      <el-button type="primary" @click="handleSaveAndPublish" :disabled="!canSaveAndPublish">
        <el-icon><Promotion /></el-icon>
        保存并发布
      </el-button>
      <el-button type="success" @click="handlePublish" :disabled="!canPublish">
        <el-icon><Promotion /></el-icon>
        发布
      </el-button>
      <el-button @click="handleCancel">
        <el-icon><Close /></el-icon>
        取消
      </el-button>
      <el-button type="danger" @click="handleDeleteAuthLetter" :disabled="!canDelete">
        <el-icon><Delete /></el-icon>
        删除
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Upload,
  Download,
  Delete,
  Plus,
  DocumentChecked,
  Promotion,
  Close
} from '@element-plus/icons-vue'
import axios from 'axios'

// ========== API配置 ==========
const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

apiClient.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

const authLetterApi = {
  getDetail(id) {
    return apiClient.get(`/auth-letters/${id}`)
  },
  save(data) {
    return apiClient.post('/auth-letters', data)
  },
  update(id, data) {
    return apiClient.put(`/auth-letters/${id}`, data)
  },
  publish(id) {
    return apiClient.put(`/auth-letters/${id}/publish`)
  },
  delete(id) {
    return apiClient.delete(`/auth-letters/${id}`)
  }
}

const lookupApi = {
  getValues(code) {
    return apiClient.get(`/lookup/${code}`)
  },
  getOrgTree() {
    return apiClient.get('/lookup/org/tree')
  }
}

// ========== 页面逻辑 ==========
const router = useRouter()
const route = useRoute()

// 表单引用
const formRef = ref(null)
const attachmentTableRef = ref(null)
const sceneTableRef = ref(null)

// 页面状态
const pageStatus = ref('DRAFT') // DRAFT, PUBLISHED, EXPIRED
const isNew = ref(true)

// 表单数据
const formData = reactive({
  name: '',
  authPublishLevel: [],
  authPublishOrg: [],
  authTargetLevel: [],
  applicableRegion: [],
  publishYear: null,
  contentSummary: ''
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入授权书名称', trigger: 'blur' }],
  authPublishLevel: [{ required: true, message: '请选择授权发布层级', trigger: 'change' }],
  authPublishOrg: [{ required: true, message: '请选择授权发布组织', trigger: 'change' }],
  authTargetLevel: [{ required: true, message: '请选择授权对象层级', trigger: 'change' }],
  applicableRegion: [{ required: true, message: '请选择适用区域', trigger: 'change' }],
  publishYear: [{ required: true, message: '请选择授权书发布年份', trigger: 'change' }],
  contentSummary: [{ required: true, message: '请输入授权书内容摘要', trigger: 'blur' }]
}

// 下拉选项数据
const authPublishLevelOptions = ref([])
const authTargetLevelOptions = ref([])
const applicableRegionOptions = ref([])
const orgTreeData = ref([])
const docTypeOptions = ref([])

// 附件相关
const selectedAttachments = ref([])
const attachmentData = ref([
  {
    id: 1,
    fileName: '授权书原件.pdf',
    docType: 'ORIGINAL',
    createdBy: 'admin',
    createdAt: '2024-03-10 10:30:00',
    updatedBy: 'admin',
    updatedAt: '2024-03-10 10:30:00'
  },
  {
    id: 2,
    fileName: '授权书附件.docx',
    docType: 'ATTACHMENT',
    createdBy: 'admin',
    createdAt: '2024-03-10 11:00:00',
    updatedBy: '',
    updatedAt: ''
  }
])
const attachmentPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 2
})

// 授权规则相关
const selectedScenes = ref([])
const sceneData = ref([
  {
    id: 1,
    sceneName: '销售场景',
    industry: 'ICT',
    businessScenario: '设备销售',
    ruleDetail: '单笔金额不超过500万的销售订单可自主决策',
    decisionLevel: '地区部',
    createdBy: 'admin',
    createdAt: '2024-03-10 10:30:00',
    updatedBy: '',
    updatedAt: ''
  },
  {
    id: 2,
    sceneName: '采购场景',
    industry: '消费者',
    businessScenario: '物料采购',
    ruleDetail: '单笔金额不超过200万的采购订单可自主决策',
    decisionLevel: '代表处',
    createdBy: 'admin',
    createdAt: '2024-03-10 11:00:00',
    updatedBy: 'admin',
    updatedAt: '2024-03-10 14:00:00'
  }
])
const scenePagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 2
})

// 按钮状态计算
const canSave = computed(() => pageStatus.value === 'DRAFT')
const canPublish = computed(() => pageStatus.value === 'DRAFT' && !isNew.value)
const canSaveAndPublish = computed(() => pageStatus.value === 'DRAFT')
const canDelete = computed(() => pageStatus.value === 'DRAFT')

/**
 * 加载lookup数据
 * TODO: 对接真实的lookup服务
 */
async function loadLookupData() {
  try {
    const publishLevelRes = await lookupApi.getValues('AUTH_PUBLISH_LEVEL')
    authPublishLevelOptions.value = publishLevelRes.data || []

    const targetRes = await lookupApi.getValues('AUTH_TARGET_LEVEL')
    authTargetLevelOptions.value = targetRes.data || []

    const regionRes = await lookupApi.getValues('APPLICABLE_REGION')
    applicableRegionOptions.value = regionRes.data || []

    const orgRes = await lookupApi.getOrgTree()
    orgTreeData.value = orgRes.data || []

    const docTypeRes = await lookupApi.getValues('DOC_TYPE')
    docTypeOptions.value = docTypeRes.data || []
  } catch (error) {
    console.error('加载lookup数据失败:', error)
    // 使用模拟数据
    authPublishLevelOptions.value = [
      { code: 'ORGANIZATION', name: '机关' },
      { code: 'REGIONAL_DEPT', name: '地区部' },
      { code: 'REPRESENTATIVE_OFFICE', name: '代表处' },
      { code: 'OFFICE', name: '办事处' }
    ]
    authTargetLevelOptions.value = authPublishLevelOptions.value
    applicableRegionOptions.value = [
      { code: 'EAST', name: '华东' },
      { code: 'NORTH', name: '华北' },
      { code: 'SOUTH', name: '华南' },
      { code: 'WEST', name: '西部' },
      { code: 'CENTRAL', name: '华中' }
    ]
    orgTreeData.value = [
      {
        code: 'ORG001',
        name: '总部',
        children: [
          { code: 'ORG002', name: '华东区', children: [{ code: 'ORG003', name: '上海办事处' }] },
          { code: 'ORG005', name: '华北区', children: [{ code: 'ORG006', name: '北京办事处' }] }
        ]
      }
    ]
    docTypeOptions.value = [
      { code: 'ORIGINAL', name: '原件' },
      { code: 'ATTACHMENT', name: '附件' },
      { code: 'CERTIFICATE', name: '证书' }
    ]
  }
}

/**
 * 加载授权书详情
 */
async function loadDetail(id) {
  try {
    const res = await authLetterApi.getDetail(id)
    if (res.code === 200) {
      Object.assign(formData, res.data)
      pageStatus.value = res.data.status || 'DRAFT'
      isNew.value = false
    }
  } catch (error) {
    console.error('加载详情失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// ========== 附件操作 ==========
function handleUpload() {
  ElMessage.info('上传功能待实现')
}

function handleDownloadAttachment() {
  if (selectedAttachments.value.length === 0) {
    ElMessage.warning('请先选择要下载的附件')
    return
  }
  ElMessage.info('批量下载功能待实现')
}

function handleDeleteAttachment() {
  if (selectedAttachments.value.length === 0) {
    ElMessage.warning('请先选择要删除的附件')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedAttachments.value.length} 个附件吗？`, '提示', { type: 'warning' })
    .then(() => {
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

function handleDeleteAttachmentRow(row) {
  ElMessageBox.confirm('确定要删除该附件吗？', '提示', { type: 'warning' })
    .then(() => {
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

function handleDownloadAttachmentRow(row) {
  // TODO: 文档名称超链接下载地址待提供
  ElMessage.info(`下载文件: ${row.fileName}`)
}

function handleEncryptAttachmentRow(row) {
  ElMessage.info(`加密文件: ${row.fileName}`)
}

function handleAttachmentSelectionChange(selection) {
  selectedAttachments.value = selection
}

function handleAttachmentSizeChange(size) {
  attachmentPagination.pageSize = size
}

function handleAttachmentCurrentChange(page) {
  attachmentPagination.pageNum = page
}

function getDocTypeName(code) {
  const item = docTypeOptions.value.find(opt => opt.code === code)
  return item ? item.name : code
}

// ========== 授权规则操作 ==========
function handleAddScene() {
  ElMessage.info('添加场景功能待实现')
}

function handleDeleteScene() {
  if (selectedScenes.value.length === 0) {
    ElMessage.warning('请先选择要删除的场景')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedScenes.value.length} 个场景吗？`, '提示', { type: 'warning' })
    .then(() => {
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

function handleEditScene(row) {
  ElMessage.info(`编辑场景: ${row.sceneName}`)
}

function handleDeleteSceneRow(row) {
  ElMessageBox.confirm('确定要删除该场景吗？', '提示', { type: 'warning' })
    .then(() => {
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

function handleSceneSelectionChange(selection) {
  selectedScenes.value = selection
}

function handleSceneSizeChange(size) {
  scenePagination.pageSize = size
}

function handleSceneCurrentChange(page) {
  scenePagination.pageNum = page
}

// ========== 底部按钮操作 ==========
async function handleSave() {
  try {
    await formRef.value.validate()
    const data = { ...formData }
    if (isNew.value) {
      const res = await authLetterApi.save(data)
      if (res.code === 200) {
        ElMessage.success('保存成功')
        isNew.value = false
      }
    } else {
      const res = await authLetterApi.update(route.params.id, data)
      if (res.code === 200) {
        ElMessage.success('保存成功')
      }
    }
  } catch (error) {
    console.error('保存失败:', error)
  }
}

async function handleSaveAndPublish() {
  try {
    await formRef.value.validate()
    const data = { ...formData }
    let id = route.params.id
    if (isNew.value) {
      const res = await authLetterApi.save(data)
      if (res.code === 200) {
        id = res.data.id
      }
    } else {
      await authLetterApi.update(id, data)
    }
    const res = await authLetterApi.publish(id)
    if (res.code === 200) {
      ElMessage.success('保存并发布成功')
      pageStatus.value = 'PUBLISHED'
      isNew.value = false
    }
  } catch (error) {
    console.error('保存并发布失败:', error)
  }
}

async function handlePublish() {
  try {
    await ElMessageBox.confirm('确定要发布该授权书吗？', '提示', { type: 'warning' })
    const res = await authLetterApi.publish(route.params.id)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      pageStatus.value = 'PUBLISHED'
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布失败:', error)
    }
  }
}

function handleCancel() {
  router.push('/auth-letters')
}

async function handleDeleteAuthLetter() {
  try {
    await ElMessageBox.confirm('确定要删除该授权书吗？', '提示', { type: 'warning' })
    const res = await authLetterApi.delete(route.params.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      router.push('/auth-letters')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 初始化
onMounted(() => {
  loadLookupData()
  const id = route.params.id
  if (id && id !== 'create') {
    loadDetail(id)
  }
})
</script>

<style scoped>
.auth-letter-detail-page {
  padding: 20px;
  padding-bottom: 80px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.info-card,
.attachment-card,
.rule-card {
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.info-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.form-row.single {
  grid-template-columns: 1fr;
}

.form-row.single :deep(.el-form-item__content) {
  max-width: 100%;
}

.info-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.info-form :deep(.el-input),
.info-form :deep(.el-select),
.info-form :deep(.el-date-picker),
.info-form :deep(.el-tree-select) {
  width: 100%;
}

.section-header {
  display: flex;
}

.section-label {
  width: 80px;
  font-weight: 600;
  color: #606266;
  padding-top: 8px;
  flex-shrink: 0;
}

.section-content {
  flex: 1;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

/* 底部悬浮按钮 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 16px 20px;
  display: flex;
  justify-content: center;
  gap: 16px;
  box-shadow: 0 -2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 100;
}
</style>