<template>
  <div class="condition-builder">
    <div v-for="(condition, index) in modelValue" :key="index" class="condition-row">
      <!-- Simple Condition -->
      <div v-if="!condition.children || condition.children.length === 0" class="simple-condition">
        <el-input v-model="condition.fieldName" placeholder="字段名" style="width: 120px" />
        <el-select v-model="condition.operator" placeholder="操作符" style="width: 100px">
          <el-option label="等于" value="EQ" />
          <el-option label="不等于" value="NE" />
          <el-option label="大于" value="GT" />
          <el-option label="大于等于" value="GE" />
          <el-option label="小于" value="LT" />
          <el-option label="小于等于" value="LE" />
          <el-option label="包含" value="IN" />
          <el-option label="不包含" value="NOT_IN" />
          <el-option label="模糊匹配" value="LIKE" />
        </el-select>
        <el-input v-model="condition.value" placeholder="值" style="width: 150px" />
      </div>
      <!-- Group Condition -->
      <div v-else class="group-condition">
        <el-select v-model="condition.logicOperator" placeholder="逻辑" style="width: 80px">
          <el-option label="与" value="AND" />
          <el-option label="或" value="OR" />
        </el-select>
        <condition-builder v-model="condition.children" />
      </div>

      <!-- Actions -->
      <div class="condition-actions">
        <el-button size="small" @click="addNestedCondition(index)" v-if="!condition.children">
          嵌套
        </el-button>
        <el-button size="small" type="danger" @click="removeCondition(index)">
          删除
        </el-button>
      </div>

      <!-- Logic Operator for next condition -->
      <div v-if="index < modelValue.length - 1" class="logic-row">
        <el-select v-model="modelValue[index + 1].logicOperator" style="width: 80px">
          <el-option label="与" value="AND" />
          <el-option label="或" value="OR" />
        </el-select>
      </div>
    </div>

    <el-button type="primary" size="small" @click="addCondition" style="margin-top: 8px">
      添加条件
    </el-button>
  </div>
</template>

<script>
export default {
  name: 'ConditionBuilder',
  props: {
    modelValue: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update:modelValue'],
  methods: {
    addCondition() {
      const newCondition = {
        fieldName: '',
        operator: 'EQ',
        value: '',
        logicOperator: 'AND',
        children: []
      }
      this.$emit('update:modelValue', [...this.modelValue, newCondition])
    },
    removeCondition(index) {
      const newConditions = [...this.modelValue]
      newConditions.splice(index, 1)
      this.$emit('update:modelValue', newConditions)
    },
    addNestedCondition(index) {
      const newConditions = [...this.modelValue]
      newConditions[index] = {
        ...newConditions[index],
        logicOperator: 'AND',
        children: [
          {
            fieldName: '',
            operator: 'EQ',
            value: '',
            logicOperator: 'AND',
            children: []
          }
        ]
      }
      this.$emit('update:modelValue', newConditions)
    }
  }
}
</script>

<style scoped>
.condition-builder {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 12px;
  background: #fafafa;
}

.condition-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.simple-condition {
  display: flex;
  gap: 8px;
  align-items: center;
}

.group-condition {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  padding: 8px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.condition-actions {
  display: flex;
  gap: 4px;
}

.logic-row {
  margin-left: 8px;
}
</style>