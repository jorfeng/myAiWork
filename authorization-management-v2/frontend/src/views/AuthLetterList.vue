<template>
  <div class="auth-letter-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>授权书列表</span>
          <el-button type="primary" @click="goToCreate">新建授权书</el-button>
        </div>
      </template>

      <el-table :data="authLetters" v-loading="loading" style="width: 100%">
        <el-table-column prop="code" label="授权书编号" width="150" />
        <el-table-column prop="name" label="授权书名称" width="200" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishLevel" label="发布层级" width="100">
          <template #default="{ row }">
            {{ getLevelText(row.publishLevel) }}
          </template>
        </el-table-column>
        <el-table-column prop="authorizedLevel" label="被授权层级" width="100">
          <template #default="{ row }">
            {{ getLevelText(row.authorizedLevel) }}
          </template>
        </el-table-column>
        <el-table-column prop="validTo" label="有效期至" width="180">
          <template #default="{ row }">
            {{ row.validTo ? formatDate(row.validTo) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goToDetail(row.id)">查看</el-button>
            <el-button link type="primary" @click="goToEdit(row.id)" v-if="row.status === 'DRAFT'">编辑</el-button>
            <el-button link type="success" @click="handlePublish(row)" v-if="row.status === 'DRAFT'">发布</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 'DRAFT'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Publish Dialog -->
    <el-dialog v-model="publishDialogVisible" title="发布授权书" width="500px">
      <el-form :model="publishForm" label-width="120px">
        <el-form-item label="发布层级">
          <el-select v-model="publishForm.publishLevel" placeholder="请选择发布层级">
            <el-option label="机关" value="ORGANIZATION" />
            <el-option label="地区部" value="REGIONAL_DEPT" />
            <el-option label="代表处" value="REPRESENTATIVE_OFFICE" />
            <el-option label="办事处" value="OFFICE" />
          </el-select>
        </el-form-item>
        <el-form-item label="被授权层级">
          <el-select v-model="publishForm.authorizedLevel" placeholder="请选择被授权层级">
            <el-option label="机关" value="ORGANIZATION" />
            <el-option label="地区部" value="REGIONAL_DEPT" />
            <el-option label="代表处" value="REPRESENTATIVE_OFFICE" />
            <el-option label="办事处" value="OFFICE" />
          </el-select>
        </el-form-item>
        <el-form-item label="有效期起始">
          <el-date-picker v-model="publishForm.validFrom" type="datetime" placeholder="选择日期时间" />
        </el-form-item>
        <el-form-item label="有效期截止">
          <el-date-picker v-model="publishForm.validTo" type="datetime" placeholder="选择日期时间" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPublish">确认发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authLetterApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AuthLetterList',
  setup() {
    const router = useRouter()
    const authLetters = ref([])
    const loading = ref(false)
    const publishDialogVisible = ref(false)
    const publishForm = ref({
      id: null,
      publishLevel: null,
      authorizedLevel: null,
      validFrom: null,
      validTo: null
    })

    const loadAuthLetters = async () => {
      loading.value = true
      try {
        const res = await authLetterApi.list()
        authLetters.value = res.data
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const goToCreate = () => {
      router.push('/auth-letters/create')
    }

    const goToDetail = (id) => {
      router.push(`/auth-letters/${id}`)
    }

    const goToEdit = (id) => {
      router.push(`/auth-letters/${id}/edit`)
    }

    const handlePublish = (row) => {
      publishForm.value = {
        id: row.id,
        publishLevel: row.publishLevel || null,
        authorizedLevel: row.authorizedLevel || null,
        validFrom: row.validFrom ? new Date(row.validFrom) : null,
        validTo: row.validTo ? new Date(row.validTo) : null
      }
      publishDialogVisible.value = true
    }

    const confirmPublish = async () => {
      try {
        const params = {
          publishLevel: publishForm.value.publishLevel,
          authorizedLevel: publishForm.value.authorizedLevel,
          validFrom: publishForm.value.validFrom?.toISOString(),
          validTo: publishForm.value.validTo?.toISOString()
        }
        await authLetterApi.publish(publishForm.value.id, params)
        ElMessage.success('发布成功')
        publishDialogVisible.value = false
        loadAuthLetters()
      } catch (error) {
        console.error(error)
      }
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该授权书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await authLetterApi.delete(row.id)
        ElMessage.success('删除成功')
        loadAuthLetters()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
        }
      }
    }

    const getStatusType = (status) => {
      const types = {
        DRAFT: 'info',
        PUBLISHED: 'success',
        EXPIRED: 'danger'
      }
      return types[status] || 'info'
    }

    const getStatusText = (status) => {
      const texts = {
        DRAFT: '草稿',
        PUBLISHED: '已发布',
        EXPIRED: '已失效'
      }
      return texts[status] || status
    }

    const getLevelText = (level) => {
      const texts = {
        ORGANIZATION: '机关',
        REGIONAL_DEPT: '地区部',
        REPRESENTATIVE_OFFICE: '代表处',
        OFFICE: '办事处'
      }
      return texts[level] || '-'
    }

    const formatDate = (date) => {
      if (!date) return '-'
      return new Date(date).toLocaleString('zh-CN')
    }

    onMounted(() => {
      loadAuthLetters()
    })

    return {
      authLetters,
      loading,
      publishDialogVisible,
      publishForm,
      loadAuthLetters,
      goToCreate,
      goToDetail,
      goToEdit,
      handlePublish,
      confirmPublish,
      handleDelete,
      getStatusType,
      getStatusText,
      getLevelText,
      formatDate
    }
  }
}
</script>

<style scoped>
.auth-letter-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>