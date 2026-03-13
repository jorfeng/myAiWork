<template>
  <div class="page-container">
    <div class="page-header">
      <h2>规则参数配置</h2>
    </div>

    <!-- 查询条件 -->
    <div class="search-panel">
      <div class="search-row">
        <div class="search-item">
          <label>名称</label>
          <input type="text" v-model="searchForm.name" placeholder="请输入名称" />
        </div>
        <div class="search-item">
          <label>名称英文</label>
          <input type="text" v-model="searchForm.nameEn" placeholder="请输入名称英文" />
        </div>
        <div class="search-item">
          <label>状态</label>
          <select v-model="searchForm.status">
            <option value="">全部</option>
            <option value="ACTIVE">生效</option>
            <option value="INACTIVE">失效</option>
          </select>
        </div>
        <button class="btn btn-primary" @click="handleSearch">查询</button>
      </div>
    </div>

    <!-- 功能按钮 -->
    <div class="toolbar">
      <button class="btn btn-primary" @click="handleCreate">新建</button>
      <button class="btn btn-default" @click="handleBatchActivate">生效</button>
      <button class="btn btn-default" @click="handleBatchDeactivate">失效</button>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th><input type="checkbox" v-model="selectAll" @change="handleSelectAll" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>名称</th>
            <th>名称英文</th>
            <th>状态</th>
            <th>创建人</th>
            <th>创建时间</th>
            <th>更新人</th>
            <th>更新时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in tableData" :key="item.id">
            <td><input type="checkbox" v-model="selectedRows" :value="item.id" /></td>
            <td>{{ (pageNum - 1) * pageSize + index + 1 }}</td>
            <td>
              <span class="icon-btn" @click="handleEdit(item)">✏️</span>
            </td>
            <td>{{ item.name }}</td>
            <td>{{ item.nameEn }}</td>
            <td>
              <span :class="['status-tag', item.status === 'ACTIVE' ? 'active' : 'inactive']">
                {{ item.status === 'ACTIVE' ? '生效' : '失效' }}
              </span>
            </td>
            <td>{{ item.createdBy }}</td>
            <td>{{ formatTime(item.createdTime) }}</td>
            <td>{{ item.updatedBy }}</td>
            <td>{{ formatTime(item.updatedTime) }}</td>
          </tr>
          <tr v-if="tableData.length === 0">
            <td colspan="10" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <span>共 {{ total }} 条</span>
      <select v-model="pageSize" @change="handlePageSizeChange">
        <option :value="10">10条/页</option>
        <option :value="30">30条/页</option>
        <option :value="50">50条/页</option>
      </select>
      <button :disabled="pageNum === 1" @click="handlePageChange(1)">首页</button>
      <button :disabled="pageNum === 1" @click="handlePageChange(pageNum - 1)">上一页</button>
      <span>第 {{ pageNum }} 页</span>
      <button :disabled="pageNum >= totalPages" @click="handlePageChange(pageNum + 1)">下一页</button>
      <button :disabled="pageNum >= totalPages" @click="handlePageChange(totalPages)">末页</button>
    </div>

    <!-- 新建/编辑弹窗 -->
    <div class="modal" v-if="showModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingId ? '编辑规则参数' : '新建规则参数' }}</h3>
          <span class="close" @click="closeModal">&times;</span>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-item required">
              <label>名称</label>
              <input type="text" v-model="formData.name" placeholder="请输入名称" />
            </div>
            <div class="form-item required">
              <label>名称英文</label>
              <input type="text" v-model="formData.nameEn" placeholder="请输入名称英文" />
            </div>
          </div>
          <div class="form-row" v-for="(item, index) in formData.businessObjects" :key="index">
            <div class="form-item">
              <label>业务对象</label>
              <input type="text" v-model="item.businessObject" placeholder="请输入业务对象" />
            </div>
            <div class="form-item">
              <label>取值逻辑</label>
              <input type="text" v-model="item.valueLogic" placeholder="请输入取值逻辑" />
            </div>
            <span class="remove-row" @click="removeBusinessObject(index)">✕</span>
          </div>
          <div class="form-row">
            <span class="text-btn" @click="addBusinessObject">+ 添加业务对象</span>
          </div>
          <div class="form-row">
            <div class="form-item required">
              <label>是否生效</label>
              <select v-model="formData.isActive">
                <option :value="true">是</option>
                <option :value="false">否</option>
              </select>
            </div>
            <div class="form-item required">
              <label>数据类型</label>
              <select v-model="formData.dataType">
                <option value="TEXT">文本</option>
                <option value="NUMBER">数值</option>
                <option value="COMPARE_FIELD">比对字段</option>
                <option value="RATIO">比率</option>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="handleSave">确定</button>
          <button class="btn btn-default" @click="closeModal">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '../utils/request';

export default {
  name: 'RuleParamConfig',
  data() {
    return {
      searchForm: {
        name: '',
        nameEn: '',
        status: ''
      },
      tableData: [],
      selectedRows: [],
      selectAll: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,

      // 弹窗
      showModal: false,
      editingId: null,
      formData: {
        name: '',
        nameEn: '',
        businessObjects: [{ businessObject: '', valueLogic: '' }],
        isActive: true,
        dataType: 'TEXT'
      }
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize) || 1;
    }
  },
  created() {
    this.loadTableData();
  },
  methods: {
    async loadTableData() {
      try {
        const params = {
          ...this.searchForm,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        };
        const res = await request.post('/rule-param/list', params);
        if (res.code === 200) {
          this.tableData = res.data.list || [];
          this.total = res.data.total || 0;
        }
      } catch (error) {
        console.error('加载数据失败:', error);
      }
    },
    handleSearch() {
      this.pageNum = 1;
      this.loadTableData();
    },
    handleSelectAll() {
      if (this.selectAll) {
        this.selectedRows = this.tableData.map(item => item.id);
      } else {
        this.selectedRows = [];
      }
    },
    handleCreate() {
      this.editingId = null;
      this.formData = {
        name: '',
        nameEn: '',
        businessObjects: [{ businessObject: '', valueLogic: '' }],
        isActive: true,
        dataType: 'TEXT'
      };
      this.showModal = true;
    },
    handleEdit(item) {
      this.editingId = item.id;
      this.formData = {
        name: item.name,
        nameEn: item.nameEn,
        businessObjects: item.businessObjects && item.businessObjects.length > 0
          ? item.businessObjects
          : [{ businessObject: '', valueLogic: '' }],
        isActive: item.isActive,
        dataType: item.dataType
      };
      this.showModal = true;
    },
    async handleSave() {
      if (!this.formData.name) {
        alert('请输入名称');
        return;
      }
      if (!this.formData.nameEn) {
        alert('请输入名称英文');
        return;
      }
      try {
        const data = {
          id: this.editingId,
          ...this.formData,
          businessObjects: this.formData.businessObjects.filter(
            item => item.businessObject || item.valueLogic
          )
        };
        if (this.editingId) {
          await request.put('/rule-param/update', data);
        } else {
          await request.post('/rule-param/create', data);
        }
        alert('保存成功');
        this.closeModal();
        this.loadTableData();
      } catch (error) {
        alert('保存失败');
      }
    },
    closeModal() {
      this.showModal = false;
    },
    async handleBatchActivate() {
      if (this.selectedRows.length === 0) {
        alert('请选择要生效的数据');
        return;
      }
      try {
        await request.post('/rule-param/activateBatch', this.selectedRows);
        alert('操作成功');
        this.loadTableData();
      } catch (error) {
        alert('操作失败');
      }
    },
    async handleBatchDeactivate() {
      if (this.selectedRows.length === 0) {
        alert('请选择要失效的数据');
        return;
      }
      try {
        await request.post('/rule-param/deactivateBatch', this.selectedRows);
        alert('操作成功');
        this.loadTableData();
      } catch (error) {
        alert('操作失败');
      }
    },
    handlePageSizeChange() {
      this.pageNum = 1;
      this.loadTableData();
    },
    handlePageChange(page) {
      this.pageNum = page;
      this.loadTableData();
    },
    addBusinessObject() {
      this.formData.businessObjects.push({ businessObject: '', valueLogic: '' });
    },
    removeBusinessObject(index) {
      if (this.formData.businessObjects.length > 1) {
        this.formData.businessObjects.splice(index, 1);
      }
    },
    formatTime(time) {
      if (!time) return '-';
      return time.replace('T', ' ').substring(0, 19);
    }
  }
};
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: #fff;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 18px;
  color: #333;
}

.search-panel {
  background: #f8f8f8;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.search-item {
  display: flex;
  align-items: center;
  margin-right: 20px;
  margin-bottom: 10px;
}

.search-item label {
  width: 80px;
  text-align: right;
  margin-right: 10px;
  color: #666;
}

.search-item input,
.search-item select {
  width: 180px;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.btn {
  padding: 6px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-right: 10px;
}

.btn-primary {
  background: #1890ff;
  color: #fff;
  border-color: #1890ff;
}

.btn-default {
  background: #fff;
  color: #333;
}

.btn:hover {
  opacity: 0.8;
}

.toolbar {
  margin-bottom: 15px;
}

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}

.data-table th,
.data-table td {
  padding: 12px 10px;
  border: 1px solid #e8e8e8;
  text-align: center;
}

.data-table th {
  background: #fafafa;
  font-weight: 600;
  color: #333;
}

.data-table tbody tr:hover {
  background: #f5f5f5;
}

.empty-row {
  text-align: center;
  color: #999;
  padding: 40px;
}

.icon-btn {
  cursor: pointer;
  font-size: 16px;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-tag.active {
  background: #e6f7ff;
  color: #1890ff;
}

.status-tag.inactive {
  background: #fff1f0;
  color: #ff4d4f;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 10px 0;
}

.pagination select {
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin: 0 10px;
}

.pagination button {
  padding: 5px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  margin: 0 5px;
}

.pagination button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 600px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.modal-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
}

.close {
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 20px;
  max-height: 60vh;
  overflow-y: auto;
}

.modal-footer {
  padding: 15px 20px;
  border-top: 1px solid #e8e8e8;
  text-align: right;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 15px;
}

.form-item {
  display: flex;
  align-items: center;
  margin-right: 20px;
  margin-bottom: 10px;
}

.form-item label {
  width: 80px;
  text-align: right;
  margin-right: 10px;
  color: #666;
}

.form-item.required label::before {
  content: '*';
  color: #f00;
  margin-right: 4px;
}

.form-item input,
.form-item select {
  width: 180px;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.text-btn {
  color: #1890ff;
  cursor: pointer;
  margin-right: 20px;
}

.remove-row {
  color: #999;
  cursor: pointer;
  margin-left: 10px;
}
</style>