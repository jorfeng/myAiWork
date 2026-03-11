<template>
  <div class="auth-letter-list-page">
    <!-- 查询条件区 -->
    <el-card class="query-card" shadow="never">
      <el-form :model="queryParams" class="query-form">
        <div class="form-row">
          <el-form-item label="授权书名称">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入授权书名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="授权对象层级">
            <el-select
              v-model="queryParams.authTargetLevel"
              multiple
              collapse-tags
              collapse-tags-tooltip
              placeholder="请选择"
              clearable
            >
              <el-option
                v-for="item in authTargetLevelOptions"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="适用区域">
            <el-select
              v-model="queryParams.applicableRegion"
              multiple
              collapse-tags
              collapse-tags-tooltip
              placeholder="请选择"
              clearable
            >
              <el-option
                v-for="item in applicableRegionOptions"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="授权发布层级">
            <el-select
              v-model="queryParams.authPublishLevel"
              multiple
              collapse-tags
              collapse-tags-tooltip
              placeholder="请选择"
              clearable
            >
              <el-option
                v-for="item in authPublishLevelOptions"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="授权发布组织">
            <el-tree-select
              v-model="queryParams.authPublishOrg"
              multiple
              collapse-tags
              collapse-tags-tooltip
              placeholder="请选择"
              clearable
              check-strictly
              :data="orgTreeData"
              :props="{ label: 'name', value: 'code', children: 'children' }"
            />
          </el-form-item>
          <el-form-item label="授权书发布年份">
            <el-date-picker
              v-model="queryParams.publishYear"
              type="year"
              placeholder="请选择年份"
              value-format="YYYY"
            />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择"
              clearable
            >
              <el-option label="草稿" value="DRAFT" />
              <el-option label="已发布" value="PUBLISHED" />
              <el-option label="已失效" value="EXPIRED" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <div class="query-buttons">
        <el-button type="primary" @click="handleQuery">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
    </el-card>

    <!-- 功能按钮区 -->
    <div class="action-buttons">
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        新建授权书
      </el-button>
      <el-button @click="handleUpdate">
        <el-icon><Edit /></el-icon>
        更新
      </el-button>
      <el-button type="success" @click="handleActivate">
        <el-icon><Check /></el-icon>
        生效
      </el-button>
      <el-button type="warning" @click="handleDeactivate">
        <el-icon><Close /></el-icon>
        失效
      </el-button>
      <el-button type="danger" @click="handleDelete">
        <el-icon><Delete /></el-icon>
        删除
      </el-button>
    </div>

    <!-- 数据表格区 -->
    <el-card class="table-card" shadow="never">
      <el-table
        ref="tableRef"
        :data="tableData"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center">
          <template #default="{ $index }">
            {{ (pagination.pageNum - 1) * pagination.pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'DRAFT'"
              link
              type="primary"
              @click="goToDetail(row.id)"
            >
              <el-icon><Edit /></el-icon>
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="授权书名称" min-width="150">
          <template #default="{ row }">
            <el-link type="primary" @click="goToDetail(row.id)">
              {{ row.name }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="授权对象层级" width="120">
          <template #default="{ row }">
            {{ formatArrayText(row.authTargetLevelText) }}
          </template>
        </el-table-column>
        <el-table-column label="适用区域" width="100">
          <template #default="{ row }">
            {{ formatArrayText(row.applicableRegionText) }}
          </template>
        </el-table-column>
        <el-table-column label="授权发布层级" width="120">
          <template #default="{ row }">
            {{ formatArrayText(row.authPublishLevelText) }}
          </template>
        </el-table-column>
        <el-table-column label="授权发布组织" width="150">
          <template #default="{ row }">
            {{ formatArrayText(row.authPublishOrgText) }}
          </template>
        </el-table-column>
        <el-table-column prop="publishYear" label="授权书发布年份" width="120" align="center" />
        <el-table-column prop="createdBy" label="创建人" width="100" align="center" />
        <el-table-column prop="createdAt" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Edit,
  Check,
  Close,
  Delete
} from '@element-plus/icons-vue'
import axios from 'axios'

// ========== API配置 ==========
const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 响应拦截器
apiClient.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// API方法
const authLetterApi = {
  queryList(params) {
    return apiClient.get('/auth-letters', { params })
  },
  batchPublish(ids) {
    return apiClient.put('/auth-letters/batch/publish', { ids })
  },
  batchExpire(ids) {
    return apiClient.put('/auth-letters/batch/expire', { ids })
  },
  batchDelete(ids) {
    return apiClient.delete('/auth-letters/batch', { data: { ids } })
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

// 表格引用
const tableRef = ref(null)

// 加载状态
const loading = ref(false)

// 选中的行
const selectedRows = ref([])

// 查询参数
const queryParams = reactive({
  name: '',
  authTargetLevel: [],
  applicableRegion: [],
  authPublishLevel: [],
  authPublishOrg: [],
  publishYear: null,
  status: ''
})

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([])

// 下拉选项数据
const authTargetLevelOptions = ref([])
const applicableRegionOptions = ref([])
const authPublishLevelOptions = ref([])
const orgTreeData = ref([])

/**
 * 加载lookup数据
 * TODO: 对接真实的lookup服务
 */
async function loadLookupData() {
  try {
    const targetRes = await lookupApi.getValues('AUTH_TARGET_LEVEL')
    authTargetLevelOptions.value = targetRes.data || []

    const regionRes = await lookupApi.getValues('APPLICABLE_REGION')
    applicableRegionOptions.value = regionRes.data || []

    const publishLevelRes = await lookupApi.getValues('AUTH_PUBLISH_LEVEL')
    authPublishLevelOptions.value = publishLevelRes.data || []

    const orgRes = await lookupApi.getOrgTree()
    orgTreeData.value = orgRes.data || []
  } catch (error) {
    console.error('加载lookup数据失败:', error)
    // 使用模拟数据
    authTargetLevelOptions.value = [
      { code: 'ORGANIZATION', name: '机关' },
      { code: 'REGIONAL_DEPT', name: '地区部' },
      { code: 'REPRESENTATIVE_OFFICE', name: '代表处' },
      { code: 'OFFICE', name: '办事处' }
    ]
    applicableRegionOptions.value = [
      { code: 'EAST', name: '华东' },
      { code: 'NORTH', name: '华北' },
      { code: 'SOUTH', name: '华南' },
      { code: 'WEST', name: '西部' },
      { code: 'CENTRAL', name: '华中' }
    ]
    authPublishLevelOptions.value = authTargetLevelOptions.value
    orgTreeData.value = [
      {
        code: 'ORG001',
        name: '总部',
        children: [
          {
            code: 'ORG002',
            name: '华东区',
            children: [
              { code: 'ORG003', name: '上海办事处' },
              { code: 'ORG004', name: '杭州办事处' }
            ]
          },
          {
            code: 'ORG005',
            name: '华北区',
            children: [
              { code: 'ORG006', name: '北京办事处' },
              { code: 'ORG007', name: '天津办事处' }
            ]
          }
        ]
      }
    ]
  }
}

/**
 * 加载表格数据
 */
async function loadTableData() {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await authLetterApi.queryList(params)
    if (res.code === 200) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

/**
 * 查询按钮点击
 */
function handleQuery() {
  pagination.pageNum = 1
  loadTableData()
}

/**
 * 重置按钮点击
 */
function handleReset() {
  queryParams.name = ''
  queryParams.authTargetLevel = []
  queryParams.applicableRegion = []
  queryParams.authPublishLevel = []
  queryParams.authPublishOrg = []
  queryParams.publishYear = null
  queryParams.status = ''
  pagination.pageNum = 1
  loadTableData()
}

/**
 * 表格选择变化
 */
function handleSelectionChange(selection) {
  selectedRows.value = selection
}

/**
 * 分页大小变化
 */
function handleSizeChange(size) {
  pagination.pageSize = size
  loadTableData()
}

/**
 * 页码变化
 */
function handleCurrentChange(page) {
  pagination.pageNum = page
  loadTableData()
}

/**
 * 检查是否选中数据
 */
function checkSelection() {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择数据')
    return false
  }
  return true
}

/**
 * 获取选中数据的ID列表
 */
function getSelectedIds() {
  return selectedRows.value.map(row => row.id)
}

/**
 * 新建授权书
 * TODO: 跳转到新建页面
 */
function handleCreate() {
  ElMessage.info('新建授权书功能待实现')
  // router.push('/auth-letters/create')
}

/**
 * 更新
 * TODO: 实现批量更新逻辑
 */
function handleUpdate() {
  if (!checkSelection()) return
  ElMessage.info('更新功能待实现')
}

/**
 * 生效
 */
async function handleActivate() {
  if (!checkSelection()) return

  const ids = getSelectedIds()
  try {
    await ElMessageBox.confirm(
      `确定要将选中的 ${ids.length} 条数据发布生效吗？`,
      '提示',
      { type: 'warning' }
    )
    const res = await authLetterApi.batchPublish(ids)
    if (res.code === 200) {
      ElMessage.success(res.message || '操作成功')
      loadTableData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

/**
 * 失效
 */
async function handleDeactivate() {
  if (!checkSelection()) return

  const ids = getSelectedIds()
  try {
    await ElMessageBox.confirm(
      `确定要将选中的 ${ids.length} 条数据设为失效吗？`,
      '提示',
      { type: 'warning' }
    )
    const res = await authLetterApi.batchExpire(ids)
    if (res.code === 200) {
      ElMessage.success(res.message || '操作成功')
      loadTableData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

/**
 * 删除
 */
async function handleDelete() {
  if (!checkSelection()) return

  const ids = getSelectedIds()
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${ids.length} 条数据吗？`,
      '提示',
      { type: 'warning' }
    )
    const res = await authLetterApi.batchDelete(ids)
    if (res.code === 200) {
      ElMessage.success(res.message || '删除成功')
      loadTableData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 跳转到详情页
 */
function goToDetail(id) {
  router.push(`/auth-letters/${id}`)
}

/**
 * 获取状态标签类型
 */
function getStatusTagType(status) {
  const types = {
    DRAFT: 'info',
    PUBLISHED: 'success',
    EXPIRED: 'danger'
  }
  return types[status] || 'info'
}

/**
 * 格式化数组文本
 */
function formatArrayText(arr) {
  if (!arr || arr.length === 0) return '-'
  return arr.join('、')
}

/**
 * 格式化日期时间
 */
function formatDateTime(dateTime) {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  const second = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

// 初始化
onMounted(() => {
  loadLookupData()
  loadTableData()
})
</script>

<style scoped>
.auth-letter-list-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.query-card {
  margin-bottom: 16px;
}

.query-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.query-form :deep(.el-form-item) {
  margin-bottom: 0;
  display: flex;
  align-items: center;
}

.query-form :deep(.el-form-item__label) {
  width: auto;
  min-width: 100px;
  flex-shrink: 0;
}

.query-form :deep(.el-form-item__content) {
  flex: 1;
}

.query-form :deep(.el-input),
.query-form :deep(.el-select),
.query-form :deep(.el-date-picker),
.query-form :deep(.el-tree-select) {
  width: 100%;
}

.query-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.table-card {
  margin-bottom: 16px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.el-tag {
  min-width: 60px;
  text-align: center;
}
</style>