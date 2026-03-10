<template>
  <div class="auth-letter-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button link @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <span class="title">{{ authLetter.name }}</span>
            <el-tag :type="getStatusType(authLetter.status)">{{ getStatusText(authLetter.status) }}</el-tag>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="goToEdit" v-if="authLetter.status === 'DRAFT'">编辑</el-button>
            <el-button type="success" @click="handlePublish" v-if="authLetter.status === 'DRAFT'">发布</el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="授权书编号">{{ authLetter.code }}</el-descriptions-item>
        <el-descriptions-item label="授权书名称">{{ authLetter.name }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(authLetter.status)">{{ getStatusText(authLetter.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布层级">{{ getLevelText(authLetter.publishLevel) }}</el-descriptions-item>
        <el-descriptions-item label="被授权层级">{{ getLevelText(authLetter.authorizedLevel) }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatDate(authLetter.publishedAt) }}</el-descriptions-item>
        <el-descriptions-item label="有效期起始">{{ formatDate(authLetter.validFrom) }}</el-descriptions-item>
        <el-descriptions-item label="有效期截止">{{ formatDate(authLetter.validTo) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(authLetter.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="3">{{ authLetter.description || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- Scenes -->
      <el-divider content-position="left">场景列表</el-divider>

      <el-button type="primary" @click="showSceneDialog" v-if="authLetter.status === 'DRAFT'" style="margin-bottom: 16px">
        添加场景
      </el-button>

      <el-collapse v-model="activeSceneIds">
        <el-collapse-item v-for="scene in authLetter.scenes" :key="scene.id" :name="scene.id">
          <template #title>
            <div class="scene-title">
              <span>{{ scene.name }}</span>
              <el-tag size="small" style="margin-left: 8px">决策层级: {{ scene.decisionLevel || '-' }}</el-tag>
              <el-tag size="small" type="info" style="margin-left: 8px">排序: {{ scene.orderIndex }}</el-tag>
            </div>
          </template>

          <div class="scene-content">
            <p><strong>描述:</strong> {{ scene.description || '-' }}</p>

            <!-- Rules -->
            <el-divider content-position="left">规则列表</el-divider>

            <el-button type="primary" size="small" @click="showRuleDialog(scene.id)" v-if="authLetter.status === 'DRAFT'" style="margin-bottom: 16px">
              添加规则
            </el-button>

            <div v-for="rule in scene.rules" :key="rule.id" class="rule-item">
              <div class="rule-header">
                <span>{{ rule.name }}</span>
                <el-tag size="small" type="info">排序: {{ rule.orderIndex }}</el-tag>
                <div class="rule-actions" v-if="authLetter.status === 'DRAFT'">
                  <el-button link type="danger" @click="deleteRule(rule.id)">删除</el-button>
                </div>
              </div>
              <div class="rule-conditions">
                <condition-viewer :conditions="rule.conditions" />
              </div>
            </div>

            <div class="scene-actions" v-if="authLetter.status === 'DRAFT'">
              <el-button link type="primary" @click="editScene(scene)">编辑场景</el-button>
              <el-button link type="danger" @click="deleteScene(scene.id)">删除场景</el-button>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <!-- Scene Dialog -->
    <el-dialog v-model="sceneDialogVisible" :title="editingScene ? '编辑场景' : '添加场景'" width="500px">
      <el-form :model="sceneForm" label-width="100px">
        <el-form-item label="场景名称" required>
          <el-input v-model="sceneForm.name" placeholder="请输入场景名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="sceneForm.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="决策层级">
          <el-input-number v-model="sceneForm.decisionLevel" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="sceneForm.orderIndex" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sceneDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveScene">保存</el-button>
      </template>
    </el-dialog>

    <!-- Rule Dialog -->
    <el-dialog v-model="ruleDialogVisible" title="添加规则" width="800px">
      <el-form :model="ruleForm" label-width="100px">
        <el-form-item label="规则名称" required>
          <el-input v-model="ruleForm.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="ruleForm.description" type="textarea" rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="ruleForm.orderIndex" :min="0" />
        </el-form-item>
        <el-form-item label="条件">
          <condition-builder v-model="ruleForm.conditions" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ruleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRule">保存</el-button>
      </template>
    </el-dialog>

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
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authLetterApi, sceneApi, ruleApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import ConditionViewer from '../components/ConditionViewer.vue'
import ConditionBuilder from '../components/ConditionBuilder.vue'

export default {
  name: 'AuthLetterDetail',
  components: {
    ArrowLeft,
    ConditionViewer,
    ConditionBuilder
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const loading = ref(false)
    const authLetter = ref({})
    const activeSceneIds = ref([])

    const sceneDialogVisible = ref(false)
    const editingScene = ref(null)
    const sceneForm = reactive({
      name: '',
      description: '',
      decisionLevel: null,
      orderIndex: 0
    })

    const ruleDialogVisible = ref(false)
    const currentSceneId = ref(null)
    const ruleForm = reactive({
      name: '',
      description: '',
      orderIndex: 0,
      conditions: []
    })

    const publishDialogVisible = ref(false)
    const publishForm = reactive({
      publishLevel: null,
      authorizedLevel: null,
      validFrom: null,
      validTo: null
    })

    const loadAuthLetter = async () => {
      loading.value = true
      try {
        const res = await authLetterApi.get(route.params.id)
        authLetter.value = res.data
        if (authLetter.value.scenes && authLetter.value.scenes.length > 0) {
          activeSceneIds.value = [authLetter.value.scenes[0].id]
        }
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const goBack = () => {
      router.push('/auth-letters')
    }

    const goToEdit = () => {
      router.push(`/auth-letters/${route.params.id}/edit`)
    }

    const showSceneDialog = () => {
      editingScene.value = null
      Object.assign(sceneForm, {
        name: '',
        description: '',
        decisionLevel: null,
        orderIndex: 0
      })
      sceneDialogVisible.value = true
    }

    const editScene = (scene) => {
      editingScene.value = scene
      Object.assign(sceneForm, {
        name: scene.name,
        description: scene.description,
        decisionLevel: scene.decisionLevel,
        orderIndex: scene.orderIndex
      })
      sceneDialogVisible.value = true
    }

    const saveScene = async () => {
      try {
        if (editingScene.value) {
          await sceneApi.update(editingScene.value.id, sceneForm)
          ElMessage.success('更新成功')
        } else {
          await sceneApi.create(authLetter.value.id, sceneForm)
          ElMessage.success('添加成功')
        }
        sceneDialogVisible.value = false
        loadAuthLetter()
      } catch (error) {
        console.error(error)
      }
    }

    const deleteScene = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除该场景吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await sceneApi.delete(id)
        ElMessage.success('删除成功')
        loadAuthLetter()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
        }
      }
    }

    const showRuleDialog = (sceneId) => {
      currentSceneId.value = sceneId
      Object.assign(ruleForm, {
        name: '',
        description: '',
        orderIndex: 0,
        conditions: []
      })
      ruleDialogVisible.value = true
    }

    const saveRule = async () => {
      try {
        await ruleApi.create(currentSceneId.value, ruleForm)
        ElMessage.success('添加成功')
        ruleDialogVisible.value = false
        loadAuthLetter()
      } catch (error) {
        console.error(error)
      }
    }

    const deleteRule = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除该规则吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await ruleApi.delete(id)
        ElMessage.success('删除成功')
        loadAuthLetter()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
        }
      }
    }

    const handlePublish = () => {
      Object.assign(publishForm, {
        publishLevel: authLetter.value.publishLevel || null,
        authorizedLevel: authLetter.value.authorizedLevel || null,
        validFrom: authLetter.value.validFrom ? new Date(authLetter.value.validFrom) : null,
        validTo: authLetter.value.validTo ? new Date(authLetter.value.validTo) : null
      })
      publishDialogVisible.value = true
    }

    const confirmPublish = async () => {
      try {
        const params = {
          publishLevel: publishForm.publishLevel,
          authorizedLevel: publishForm.authorizedLevel,
          validFrom: publishForm.validFrom?.toISOString(),
          validTo: publishForm.validTo?.toISOString()
        }
        await authLetterApi.publish(authLetter.value.id, params)
        ElMessage.success('发布成功')
        publishDialogVisible.value = false
        loadAuthLetter()
      } catch (error) {
        console.error(error)
      }
    }

    const getStatusType = (status) => {
      const types = { DRAFT: 'info', PUBLISHED: 'success', EXPIRED: 'danger' }
      return types[status] || 'info'
    }

    const getStatusText = (status) => {
      const texts = { DRAFT: '草稿', PUBLISHED: '已发布', EXPIRED: '已失效' }
      return texts[status] || status
    }

    const getLevelText = (level) => {
      const texts = { ORGANIZATION: '机关', REGIONAL_DEPT: '地区部', REPRESENTATIVE_OFFICE: '代表处', OFFICE: '办事处' }
      return texts[level] || '-'
    }

    const formatDate = (date) => {
      if (!date) return '-'
      return new Date(date).toLocaleString('zh-CN')
    }

    onMounted(() => {
      loadAuthLetter()
    })

    return {
      loading,
      authLetter,
      activeSceneIds,
      sceneDialogVisible,
      editingScene,
      sceneForm,
      ruleDialogVisible,
      ruleForm,
      publishDialogVisible,
      publishForm,
      loadAuthLetter,
      goBack,
      goToEdit,
      showSceneDialog,
      editScene,
      saveScene,
      deleteScene,
      showRuleDialog,
      saveRule,
      deleteRule,
      handlePublish,
      confirmPublish,
      getStatusType,
      getStatusText,
      getLevelText,
      formatDate
    }
  }
}
</script>

<style scoped>
.auth-letter-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.scene-title {
  display: flex;
  align-items: center;
}

.scene-content {
  padding: 16px;
  background: #fafafa;
  border-radius: 4px;
}

.rule-item {
  padding: 12px;
  margin-bottom: 12px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 4px;
}

.rule-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.rule-conditions {
  padding: 8px;
  background: #f9f9f9;
  border-radius: 4px;
}

.scene-actions {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}
</style>