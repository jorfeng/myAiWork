<template>
  <div class="auth-letter-edit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑授权书' : '新建授权书' }}</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 600px">
        <el-form-item label="授权书编号" prop="code">
          <el-input v-model="form.code" placeholder="请输入授权书编号" />
        </el-form-item>
        <el-form-item label="授权书名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入授权书名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authLetterApi } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'AuthLetterEdit',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const formRef = ref(null)
    const isEdit = computed(() => !!route.params.id)

    const form = reactive({
      code: '',
      name: '',
      description: ''
    })

    const rules = {
      code: [
        { required: true, message: '请输入授权书编号', trigger: 'blur' }
      ],
      name: [
        { required: true, message: '请输入授权书名称', trigger: 'blur' }
      ]
    }

    const loadAuthLetter = async () => {
      if (isEdit.value) {
        try {
          const res = await authLetterApi.get(route.params.id)
          form.code = res.data.code
          form.name = res.data.name
          form.description = res.data.description
        } catch (error) {
          console.error(error)
        }
      }
    }

    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        if (isEdit.value) {
          await authLetterApi.update(route.params.id, form)
          ElMessage.success('更新成功')
        } else {
          const res = await authLetterApi.create(form)
          ElMessage.success('创建成功')
          router.push(`/auth-letters/${res.data.id}`)
          return
        }
        router.push(`/auth-letters/${route.params.id}`)
      } catch (error) {
        console.error(error)
      }
    }

    const goBack = () => {
      router.back()
    }

    onMounted(() => {
      loadAuthLetter()
    })

    return {
      formRef,
      form,
      rules,
      isEdit,
      handleSubmit,
      goBack
    }
  }
}
</script>

<style scoped>
.auth-letter-edit {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>