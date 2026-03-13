<template>
  <div class="page-container">
    <div class="page-header">
      <h2>授权书列表</h2>
    </div>

    <!-- 查询条件 -->
    <div class="search-panel">
      <div class="search-row">
        <div class="search-item">
          <label>授权书名称</label>
          <input type="text" v-model="searchForm.name" placeholder="请输入授权书名称" />
        </div>
        <div class="search-item">
          <label>授权对象层级</label>
          <select v-model="searchForm.authTargetLevel" multiple>
            <option v-for="item in authTargetLevelList" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>
        </div>
        <div class="search-item">
          <label>适用区域</label>
          <div class="tree-select">
            <div class="tree-select-trigger" @click="showRegionTree = !showRegionTree">
              <span>{{ regionSelectedText || '请选择' }}</span>
              <span class="arrow">▼</span>
            </div>
            <div class="tree-select-dropdown" v-if="showRegionTree">
              <tree-node
                :nodes="applicableRegionTree"
                :selected="searchForm.applicableRegion"
                @select="handleRegionSelect"
              ></tree-node>
            </div>
          </div>
        </div>
      </div>
      <div class="search-row">
        <div class="search-item">
          <label>授权发布层级</label>
          <select v-model="searchForm.authPublishLevel" multiple>
            <option v-for="item in authPublishLevelList" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>
        </div>
        <div class="search-item">
          <label>授权发布组织</label>
          <div class="tree-select">
            <div class="tree-select-trigger" @click="showOrgTree = !showOrgTree">
              <span>{{ orgSelectedText || '请选择' }}</span>
              <span class="arrow">▼</span>
            </div>
            <div class="tree-select-dropdown" v-if="showOrgTree">
              <tree-node
                :nodes="authPublishOrgTree"
                :selected="searchForm.authPublishOrg"
                @select="handleOrgSelect"
              ></tree-node>
            </div>
          </div>
        </div>
        <div class="search-item">
          <label>授权书发布年份</label>
          <input type="number" v-model="searchForm.publishYear" placeholder="请选择年份" min="2000" max="2100" />
        </div>
      </div>
      <div class="search-row">
        <div class="search-item">
          <label>状态</label>
          <select v-model="searchForm.status">
            <option value="">全部</option>
            <option value="DRAFT">草稿</option>
            <option value="PUBLISHED">已发布</option>
            <option value="INVALID">已失效</option>
          </select>
        </div>
      </div>
      <div class="search-buttons">
        <button class="btn btn-primary" @click="handleSearch">查询</button>
        <button class="btn btn-default" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 功能按钮 -->
    <div class="toolbar">
      <button class="btn btn-primary" @click="handleCreate">新建授权书</button>
      <button class="btn btn-default" @click="handleBatchUpdate">更新</button>
      <button class="btn btn-default" @click="handleBatchActivate">生效</button>
      <button class="btn btn-default" @click="handleBatchDeactivate">失效</button>
      <button class="btn btn-danger" @click="handleBatchDelete">删除</button>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th><input type="checkbox" v-model="selectAll" @change="handleSelectAll" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>授权书名称</th>
            <th>状态</th>
            <th>授权对象层级</th>
            <th>适用区域</th>
            <th>授权发布层级</th>
            <th>授权发布组织</th>
            <th>授权书发布年份</th>
            <th>创建人</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in tableData" :key="item.id">
            <td><input type="checkbox" v-model="selectedRows" :value="item.id" /></td>
            <td>{{ (pageNum - 1) * pageSize + index + 1 }}</td>
            <td>
              <span v-if="item.status === 'DRAFT'" class="icon-btn" @click="handleEdit(item)">✏️</span>
              <span v-else>-</span>
            </td>
            <td>
              <a href="javascript:;" class="link" @click="handleViewDetail(item)">{{ item.name }}</a>
            </td>
            <td>{{ getStatusText(item.status) }}</td>
            <td>{{ item.authTargetLevelLabel }}</td>
            <td>{{ item.applicableRegionLabel }}</td>
            <td>{{ item.authPublishLevelLabel }}</td>
            <td>{{ item.authPublishOrgLabel }}</td>
            <td>{{ item.publishYear }}</td>
            <td>{{ item.createdBy }}</td>
            <td>{{ formatTime(item.createdTime) }}</td>
          </tr>
          <tr v-if="tableData.length === 0">
            <td colspan="12" class="empty-row">暂无数据</td>
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
  </div>
</template>

<script>
import axios from 'axios';

// 内联request配置
const BASE_URL = '/api';
const axiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});
axiosInstance.interceptors.response.use(
  response => response.data,
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);
const request = {
  get: (url, params) => axiosInstance.get(url, { params }),
  post: (url, data) => axiosInstance.post(url, data),
  put: (url, data) => axiosInstance.put(url, data),
  delete: (url, params) => axiosInstance.delete(url, { params })
};

export default {
  name: 'AuthLetterList',
  data() {
    return {
      searchForm: {
        name: '',
        authTargetLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: null,
        status: ''
      },
      tableData: [],
      selectedRows: [],
      selectAll: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,

      // 下拉列表数据
      authTargetLevelList: [],
      applicableRegionTree: [],
      authPublishLevelList: [],
      authPublishOrgTree: [],

      // 树形选择器状态
      showRegionTree: false,
      showOrgTree: false,
      treeExpanded: {}
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize) || 1;
    },
    regionSelectedText() {
      return this.searchForm.applicableRegion.length > 0
        ? `已选择 ${this.searchForm.applicableRegion.length} 项`
        : '';
    },
    orgSelectedText() {
      return this.searchForm.authPublishOrg.length > 0
        ? `已选择 ${this.searchForm.authPublishOrg.length} 项`
        : '';
    }
  },
  created() {
    this.loadLookupData();
    this.loadTableData();
  },
  methods: {
    async loadLookupData() {
      try {
        const [targetLevel, region, publishLevel, publishOrg] = await Promise.all([
          request.get('/lookup/AUTH_TARGET_LEVEL'),
          request.get('/lookup/APPLICABLE_REGION'),
          request.get('/lookup/AUTH_PUBLISH_LEVEL'),
          request.get('/lookup/AUTH_PUBLISH_ORG')
        ]);
        this.authTargetLevelList = targetLevel.data || [];
        this.applicableRegionTree = region.data || [];
        this.authPublishLevelList = publishLevel.data || [];
        this.authPublishOrgTree = publishOrg.data || [];
      } catch (error) {
        console.error('加载下拉数据失败:', error);
      }
    },
    async loadTableData() {
      try {
        const params = {
          ...this.searchForm,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        };
        const res = await request.post('/authorization/list', params);
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
    handleReset() {
      this.searchForm = {
        name: '',
        authTargetLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: null,
        status: ''
      };
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
      this.$router.push('/#/AuthLetterDetail');
    },
    handleEdit(item) {
      this.$router.push(`/#/AuthLetterDetail?id=${item.id}`);
    },
    handleViewDetail(item) {
      this.$router.push(`/#/AuthLetterDetail?id=${item.id}`);
    },
    async handleBatchUpdate() {
      if (this.selectedRows.length === 0) {
        alert('请选择要更新的数据');
        return;
      }
      if (this.selectedRows.length > 1) {
        alert('只能选择一条数据进行更新');
        return;
      }
      this.$router.push(`/#/AuthLetterDetail?id=${this.selectedRows[0]}`);
    },
    async handleBatchActivate() {
      if (this.selectedRows.length === 0) {
        alert('请选择要生效的数据');
        return;
      }
      try {
        for (const id of this.selectedRows) {
          await request.post(`/authorization/activate?id=${id}`);
        }
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
        for (const id of this.selectedRows) {
          await request.post(`/authorization/deactivate?id=${id}`);
        }
        alert('操作成功');
        this.loadTableData();
      } catch (error) {
        alert('操作失败');
      }
    },
    async handleBatchDelete() {
      if (this.selectedRows.length === 0) {
        alert('请选择要删除的数据');
        return;
      }
      if (!confirm('确定要删除选中的数据吗？')) {
        return;
      }
      try {
        for (const id of this.selectedRows) {
          await request.delete(`/authorization/delete?id=${id}`);
        }
        alert('删除成功');
        this.loadTableData();
      } catch (error) {
        alert('删除失败');
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
    handleRegionSelect(values) {
      this.searchForm.applicableRegion = values;
    },
    handleOrgSelect(values) {
      this.searchForm.authPublishOrg = values;
    },
    getStatusText(status) {
      const map = {
        'DRAFT': '草稿',
        'PUBLISHED': '已发布',
        'INVALID': '已失效'
      };
      return map[status] || status;
    },
    formatTime(time) {
      if (!time) return '-';
      return time.replace('T', ' ').substring(0, 19);
    }
  },
  components: {
    'tree-node': {
      props: ['nodes', 'selected'],
      data() {
        return {
          expanded: {}
        };
      },
      methods: {
        isSelected(value) {
          return this.selected && this.selected.includes(value);
        },
        toggleSelect(node) {
          let newSelected = [...(this.selected || [])];
          const index = newSelected.indexOf(node.value);
          if (index > -1) {
            newSelected.splice(index, 1);
          } else {
            newSelected.push(node.value);
          }
          this.$emit('select', newSelected);
        },
        toggleExpand(node) {
          this.$set(this.expanded, node.id, !this.expanded[node.id]);
        }
      },
      render(h) {
        const self = this;
        if (!this.nodes || this.nodes.length === 0) {
          return h('div', { class: 'tree-node' });
        }
        const children = this.nodes.map(function(node) {
          const nodeChildren = [];

          // 节点内容
          const contentChildren = [
            h('input', {
              attrs: { type: 'checkbox' },
              domProps: { checked: self.isSelected(node.value) },
              on: { change: function() { self.toggleSelect(node); } }
            }),
            h('span', {
              class: 'tree-node-label',
              on: { click: function() { self.toggleExpand(node); } }
            }, node.label)
          ];

          // 展开图标
          if (node.children && node.children.length) {
            contentChildren.push(
              h('span', { class: 'expand-icon' }, self.expanded[node.id] ? '▼' : '▶')
            );
          }

          nodeChildren.push(
            h('div', { class: 'tree-node-content' }, contentChildren)
          );

          // 子节点
          if (node.children && node.children.length && self.expanded[node.id]) {
            nodeChildren.push(
              h('tree-node', {
                props: { nodes: node.children, selected: self.selected },
                on: { select: function(val) { self.$emit('select', val); } }
              })
            );
          }

          return h('div', { class: 'tree-node-item', key: node.id }, nodeChildren);
        });

        return h('div', { class: 'tree-node' }, children);
      }
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
  margin-bottom: 15px;
}

.search-row:last-child {
  margin-bottom: 0;
}

.search-item {
  display: flex;
  align-items: center;
  margin-right: 20px;
  margin-bottom: 10px;
}

.search-item label {
  width: 100px;
  text-align: right;
  margin-right: 10px;
  color: #666;
}

.search-item input,
.search-item select {
  width: 200px;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-item select[multiple] {
  height: 80px;
}

.tree-select {
  position: relative;
  width: 200px;
}

.tree-select-trigger {
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  background: #fff;
}

.tree-select-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  width: 300px;
  max-height: 300px;
  overflow-y: auto;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  z-index: 1000;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.tree-node {
  padding: 5px 0;
}

.tree-node-item {
  padding: 0 10px;
}

.tree-node-content {
  display: flex;
  align-items: center;
  padding: 5px 0;
}

.tree-node-content input {
  margin-right: 8px;
}

.tree-node-label {
  cursor: pointer;
}

.expand-icon {
  margin-left: 8px;
  font-size: 12px;
  color: #999;
}

.tree-node-children {
  padding-left: 20px;
}

.search-buttons {
  text-align: center;
  margin-top: 15px;
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

.btn-danger {
  background: #ff4d4f;
  color: #fff;
  border-color: #ff4d4f;
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

.link {
  color: #1890ff;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

.icon-btn {
  cursor: pointer;
  font-size: 16px;
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
</style>