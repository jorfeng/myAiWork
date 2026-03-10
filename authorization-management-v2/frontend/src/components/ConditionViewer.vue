<template>
  <div class="condition-viewer">
    <div v-if="!conditions || conditions.length === 0" class="empty">
      无条件
    </div>
    <div v-else class="condition-group">
      <div v-for="(condition, index) in conditions" :key="condition.id || index" class="condition-item">
        <!-- Simple Condition -->
        <div v-if="!condition.children || condition.children.length === 0" class="simple-condition">
          <span class="field">{{ condition.fieldName }}</span>
          <span class="operator">{{ getOperatorText(condition.operator) }}</span>
          <span class="value">{{ condition.value }}</span>
        </div>
        <!-- Group Condition -->
        <div v-else class="group-condition">
          <el-tag size="small" type="warning">{{ condition.logicOperator || 'AND' }}</el-tag>
          <condition-viewer :conditions="condition.children" />
        </div>
        <!-- Logic Operator between conditions -->
        <span v-if="index < conditions.length - 1" class="logic-operator">
          {{ getNextLogicOperator(index) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConditionViewer',
  props: {
    conditions: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    getOperatorText(operator) {
      const texts = {
        EQ: '等于',
        NE: '不等于',
        GT: '大于',
        GE: '大于等于',
        LT: '小于',
        LE: '小于等于',
        IN: '包含',
        NOT_IN: '不包含',
        LIKE: '模糊匹配'
      }
      return texts[operator] || operator
    },
    getNextLogicOperator(index) {
      if (index + 1 < this.conditions.length) {
        const nextCondition = this.conditions[index + 1]
        return nextCondition.logicOperator === 'OR' ? '或' : '与'
      }
      return ''
    }
  }
}
</script>

<style scoped>
.condition-viewer {
  font-size: 14px;
}

.empty {
  color: #999;
}

.condition-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.condition-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.simple-condition {
  padding: 4px 8px;
  background: #f0f9eb;
  border-radius: 4px;
  border: 1px solid #e1f3d8;
}

.field {
  font-weight: bold;
  color: #67c23a;
}

.operator {
  color: #909399;
  margin: 0 4px;
}

.value {
  color: #409eff;
}

.group-condition {
  padding: 8px;
  background: #fdf6ec;
  border-radius: 4px;
  border: 1px solid #faecd8;
}

.logic-operator {
  padding: 2px 8px;
  background: #409eff;
  color: white;
  border-radius: 4px;
  font-size: 12px;
}
</style>