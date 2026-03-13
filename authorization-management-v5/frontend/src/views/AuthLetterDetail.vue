<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isNew ? '新建授权书' : '授权书详情' }}</h2>
    </div>

    <!-- 基本信息 -->
    <div class="section">
      <div class="section-title">基本信息</div>
      <div class="form-container">
        <div class="form-row">
          <div class="form-item required">
            <label>授权书名称</label>
            <input type="text" v-model="formData.name" :disabled="isReadonly" placeholder="请输入授权书名称" />
          </div>
          <div class="form-item required">
            <label>授权发布层级</label>
            <select v-model="formData.authPublishLevel" multiple :disabled="isReadonly">
              <option v-for="item in authPublishLevelList" :key="item.value" :value="item.value">
                {{ item.label }}
              </option>
            </select>
          </div>
          <div class="form-item required">
            <label>授权发布组织</label>
            <div class="tree-select">
              <div class="tree-select-trigger" @click="showOrgTree = !showOrgTree">
                <span>{{ orgSelectedText || '请选择' }}</span>
                <span class="arrow">▼</span>
              </div>
              <div class="tree-select-dropdown" v-if="showOrgTree && !isReadonly">
                <tree-node
                  :nodes="authPublishOrgTree"
                  :selected="formData.authPublishOrg"
                  @select="handleOrgSelect"
                ></tree-node>
              </div>
            </div>
          </div>
        </div>
        <div class="form-row">
          <div class="form-item required">
            <label>授权对象层级</label>
            <select v-model="formData.authTargetLevel" multiple :disabled="isReadonly">
              <option v-for="item in authTargetLevelList" :key="item.value" :value="item.value">
                {{ item.label }}
              </option>
            </select>
          </div>
          <div class="form-item required">
            <label>适用区域</label>
            <div class="tree-select">
              <div class="tree-select-trigger" @click="showRegionTree = !showRegionTree">
                <span>{{ regionSelectedText || '请选择' }}</span>
                <span class="arrow">▼</span>
              </div>
              <div class="tree-select-dropdown" v-if="showRegionTree && !isReadonly">
                <tree-node
                  :nodes="applicableRegionTree"
                  :selected="formData.applicableRegion"
                  @select="handleRegionSelect"
                ></tree-node>
              </div>
            </div>
          </div>
          <div class="form-item required">
            <label>授权书发布年份</label>
            <input type="number" v-model="formData.publishYear" :disabled="isReadonly" min="2000" max="2100" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-item full-width required">
            <label>授权书内容摘要</label>
            <textarea v-model="formData.summary" :disabled="isReadonly" maxlength="4000" placeholder="请输入授权书内容摘要"></textarea>
          </div>
        </div>
      </div>
    </div>

    <!-- 附件区块 -->
    <div class="section">
      <div class="section-title">附件</div>
      <div class="section-toolbar">
        <button class="btn btn-primary" @click="handleUpload" :disabled="isReadonly">上传</button>
        <button class="btn btn-default" @click="handleDownloadAttachment">下载</button>
        <button class="btn btn-danger" @click="handleDeleteAttachment" :disabled="isReadonly">删除</button>
      </div>
      <table class="data-table">
        <thead>
          <tr>
            <th><input type="checkbox" v-model="attachmentSelectAll" @change="handleAttachmentSelectAll" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>文档名称</th>
            <th>文档类型</th>
            <th>创建人</th>
            <th>创建时间</th>
            <th>更新人</th>
            <th>更新时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in attachmentList" :key="item.id">
            <td><input type="checkbox" v-model="selectedAttachments" :value="item.id" /></td>
            <td>{{ (attachmentPageNum - 1) * attachmentPageSize + index + 1 }}</td>
            <td>
              <span class="icon-btn" @click="handleDownloadSingle(item)">下载</span>
              <span class="icon-btn disabled">加密</span>
            </td>
            <td><a href="javascript:;" class="link" @click="handleDownloadSingle(item)">{{ item.docName }}</a></td>
            <td>{{ getDocTypeText(item.docType) }}</td>
            <td>{{ item.createdBy }}</td>
            <td>{{ formatTime(item.createdTime) }}</td>
            <td>{{ item.updatedBy }}</td>
            <td>{{ formatTime(item.updatedTime) }}</td>
          </tr>
          <tr v-if="attachmentList.length === 0">
            <td colspan="9" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <span>共 {{ attachmentTotal }} 条</span>
        <select v-model="attachmentPageSize" @change="loadAttachmentList">
          <option :value="10">10条/页</option>
          <option :value="30">30条/页</option>
          <option :value="50">50条/页</option>
        </select>
        <button :disabled="attachmentPageNum === 1" @click="attachmentPageNum = 1; loadAttachmentList()">首页</button>
        <button :disabled="attachmentPageNum === 1" @click="attachmentPageNum--; loadAttachmentList()">上一页</button>
        <span>第 {{ attachmentPageNum }} 页</span>
        <button :disabled="attachmentPageNum >= attachmentTotalPages" @click="attachmentPageNum++; loadAttachmentList()">下一页</button>
      </div>
    </div>

    <!-- 授权规则区块 -->
    <div class="section">
      <div class="section-title">授权规则</div>
      <div class="section-toolbar">
        <button class="btn btn-primary" @click="handleAddScene" :disabled="isReadonly">添加场景</button>
        <button class="btn btn-danger" @click="handleDeleteScene" :disabled="isReadonly">删除</button>
      </div>
      <table class="data-table">
        <thead>
          <tr>
            <th><input type="checkbox" v-model="sceneSelectAll" @change="handleSceneSelectAll" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>场景</th>
            <th>产业</th>
            <th>业务场景</th>
            <th>具体规则</th>
            <th>决策层级</th>
            <th>创建人</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in sceneList" :key="item.id">
            <td><input type="checkbox" v-model="selectedScenes" :value="item.id" /></td>
            <td>{{ (scenePageNum - 1) * scenePageSize + index + 1 }}</td>
            <td>
              <span class="icon-btn" @click="handleEditScene(item)" v-if="!isReadonly">编辑</span>
              <span class="icon-btn" @click="handleDeleteSingleScene(item.id)" v-if="!isReadonly">删除</span>
            </td>
            <td>{{ item.sceneName }}</td>
            <td>{{ item.industry ? item.industry.join(',') : '' }}</td>
            <td>{{ getBusinessSceneText(item.businessScene) }}</td>
            <td>{{ item.specificRule }}</td>
            <td>{{ getDecisionLevelText(item.decisionLevel) }}</td>
            <td>{{ item.createdBy }}</td>
            <td>{{ formatTime(item.createdTime) }}</td>
          </tr>
          <tr v-if="sceneList.length === 0">
            <td colspan="10" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <span>共 {{ sceneTotal }} 条</span>
        <select v-model="scenePageSize" @change="loadSceneList">
          <option :value="10">10条/页</option>
          <option :value="30">30条/页</option>
          <option :value="50">50条/页</option>
        </select>
        <button :disabled="scenePageNum === 1" @click="scenePageNum = 1; loadSceneList()">首页</button>
        <button :disabled="scenePageNum === 1" @click="scenePageNum--; loadSceneList()">上一页</button>
        <span>第 {{ scenePageNum }} 页</span>
        <button :disabled="scenePageNum >= sceneTotalPages" @click="scenePageNum++; loadSceneList()">下一页</button>
      </div>
    </div>

    <!-- 操作日志区块（已发布/已失效状态显示） -->
    <div class="section" v-if="showLogSection">
      <div class="section-title collapsible" @click="logCollapsed = !logCollapsed">
        日志
        <span class="collapse-icon">{{ logCollapsed ? '▶' : '▼' }}</span>
      </div>
      <div class="section-content" v-if="!logCollapsed">
        <table class="data-table">
          <thead>
            <tr>
              <th>序号</th>
              <th>操作</th>
              <th>操作人</th>
              <th>操作时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in logList" :key="item.id">
              <td>{{ (logPageNum - 1) * logPageSize + index + 1 }}</td>
              <td>{{ item.operation }}</td>
              <td>{{ item.operator }}</td>
              <td>{{ formatTime(item.operationTime) }}</td>
            </tr>
            <tr v-if="logList.length === 0">
              <td colspan="4" class="empty-row">暂无数据</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination">
          <span>共 {{ logTotal }} 条</span>
          <select v-model="logPageSize" @change="loadLogList">
            <option :value="10">10条/页</option>
            <option :value="30">30条/页</option>
            <option :value="50">50条/页</option>
          </select>
          <button :disabled="logPageNum === 1" @click="logPageNum = 1; loadLogList()">首页</button>
          <button :disabled="logPageNum === 1" @click="logPageNum--; loadLogList()">上一页</button>
          <span>第 {{ logPageNum }} 页</span>
          <button :disabled="logPageNum >= logTotalPages" @click="logPageNum++; loadLogList()">下一页</button>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="footer-buttons">
      <template v-if="formData.status === 'DRAFT' || isNew">
        <button class="btn btn-primary" @click="handleSave">保存</button>
        <button class="btn btn-primary" @click="handleSaveAndPublish">保存并发布</button>
        <button class="btn btn-primary" @click="handlePublish" v-if="!isNew">发布</button>
        <button class="btn btn-default" @click="handleCancel">取消</button>
        <button class="btn btn-danger" @click="handleDelete" v-if="!isNew">删除</button>
      </template>
      <template v-else>
        <button class="btn btn-default" @click="handleBack">返回</button>
        <button class="btn btn-primary" @click="handleDeactivate" v-if="formData.status === 'PUBLISHED'">失效</button>
        <button class="btn btn-danger" @click="handleDelete">删除</button>
      </template>
    </div>

    <!-- 场景配置弹窗 -->
    <div class="modal" v-if="showSceneModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingSceneId ? '编辑场景' : '添加场景' }}</h3>
          <span class="close" @click="closeSceneModal">&times;</span>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-item required">
              <label>场景名称</label>
              <input type="text" v-model="sceneForm.sceneName" placeholder="请输入场景名称" />
            </div>
            <div class="form-item required">
              <label>产业</label>
              <select v-model="sceneForm.industry" multiple>
                <option v-for="item in industryTree" :key="item.value" :value="item.value">
                  {{ item.label }}
                </option>
              </select>
            </div>
            <div class="form-item required">
              <label>业务场景</label>
              <select v-model="sceneForm.businessScene">
                <option value="">请选择</option>
                <option v-for="item in businessSceneList" :key="item.value" :value="item.value">
                  {{ item.label }}
                </option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>决策层级</label>
              <select v-model="sceneForm.decisionLevel">
                <option value="">请选择</option>
                <option v-for="item in decisionLevelList" :key="item.value" :value="item.value">
                  {{ item.label }}
                </option>
              </select>
            </div>
            <div class="form-item full-width">
              <label>具体规则</label>
              <textarea v-model="sceneForm.specificRule" maxlength="1000" placeholder="请输入具体规则"></textarea>
            </div>
          </div>

          <!-- 规则配置 -->
          <div class="rule-config">
            <div class="rule-config-header">
              <span>规则配置</span>
            </div>
            <div class="rule-config-body">
              <div class="rule-condition" v-for="(condition, index) in sceneForm.ruleConditions" :key="index">
                <div class="condition-row">
                  <select v-model="condition.fieldId" @change="onFieldChange(condition)">
                    <option value="">请选择规则字段</option>
                    <option v-for="field in ruleParamList" :key="field.id" :value="field.id">
                      {{ field.name }}
                    </option>
                  </select>
                  <template v-if="condition.fieldId">
                    <select v-model="condition.operator">
                      <option value=">">></option>
                      <option value="<"><</option>
                      <option value="=">=</option>
                      <option value=">=">>=</option>
                      <option value="<="><=</option>
                      <option value="!=">!=</option>
                    </select>
                    <select v-model="condition.compareType">
                      <option value="FIELD">对比字段</option>
                      <option value="NUMBER">数值</option>
                      <option value="TEXT">文本</option>
                    </select>
                    <template v-if="condition.compareType === 'FIELD'">
                      <select v-model="condition.compareFieldId">
                        <option value="">请选择字段</option>
                        <option v-for="field in ruleParamList" :key="field.id" :value="field.id">
                          {{ field.name }}
                        </option>
                      </select>
                    </template>
                    <template v-else-if="condition.compareType === 'NUMBER'">
                      <input type="number" v-model="condition.compareValue" placeholder="请输入数值" />
                      <select v-model="condition.unit">
                        <option value="">单位</option>
                        <option v-for="item in measureUnitList" :key="item.value" :value="item.value">
                          {{ item.label }}
                        </option>
                      </select>
                    </template>
                    <template v-else>
                      <input type="text" v-model="condition.compareValue" placeholder="请输入文本" />
                    </template>
                  </template>
                  <span class="remove-btn" @click="removeCondition(index)">✕</span>
                </div>
                <div class="condition-logic" v-if="index < sceneForm.ruleConditions.length - 1">
                  <span class="logic-btn" @click="toggleLogic(index)">
                    {{ condition.logic || '且' }}
                  </span>
                </div>
              </div>
              <div class="rule-config-actions">
                <span class="text-btn" @click="addCondition">+ 新增条件</span>
                <span class="text-btn" @click="addConditionGroup">+ 新增子条件组</span>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="saveScene">确定</button>
          <button class="btn btn-default" @click="closeSceneModal">取消</button>
        </div>
      </div>
    </div>

    <!-- 树形组件模板 -->
    <script type="text/x-template" id="tree-node-template-detail">
      <div class="tree-node">
        <div class="tree-node-item" v-for="node in nodes" :key="node.id">
          <div class="tree-node-content">
            <input type="checkbox" :checked="isSelected(node.value)" @change="toggleSelect(node)" />
            <span class="tree-node-label" @click="toggleExpand(node)">{{ node.label }}</span>
            <span v-if="node.children && node.children.length" class="expand-icon">{{ expanded[node.id] ? '▼' : '▶' }}</span>
          </div>
          <div class="tree-node-children" v-if="node.children && node.children.length && expanded[node.id]">
            <tree-node :nodes="node.children" :selected="selected" @select="$emit('select', $event)"></tree-node>
          </div>
        </div>
      </div>
    </script>
  </div>
</template>

<script>
import request from '../utils/request';

export default {
  name: 'AuthLetterDetail',
  data() {
    return {
      letterId: null,
      isNew: true,
      formData: {
        name: '',
        authTargetLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: null,
        summary: '',
        status: 'DRAFT'
      },

      // 下拉列表数据
      authTargetLevelList: [],
      applicableRegionTree: [],
      authPublishLevelList: [],
      authPublishOrgTree: [],
      industryTree: [],
      businessSceneList: [],
      decisionLevelList: [],
      docTypeList: [],
      measureUnitList: [],
      ruleParamList: [],

      // 树形选择器
      showRegionTree: false,
      showOrgTree: false,

      // 附件
      attachmentList: [],
      selectedAttachments: [],
      attachmentSelectAll: false,
      attachmentPageNum: 1,
      attachmentPageSize: 10,
      attachmentTotal: 0,

      // 场景
      sceneList: [],
      selectedScenes: [],
      sceneSelectAll: false,
      scenePageNum: 1,
      scenePageSize: 10,
      sceneTotal: 0,

      // 场景弹窗
      showSceneModal: false,
      editingSceneId: null,
      sceneForm: {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        ruleConditions: []
      },

      // 日志
      logList: [],
      logCollapsed: true,
      logPageNum: 1,
      logPageSize: 10,
      logTotal: 0
    };
  },
  computed: {
    isReadonly() {
      return this.formData.status !== 'DRAFT';
    },
    showLogSection() {
      return this.formData.status === 'PUBLISHED' || this.formData.status === 'INVALID';
    },
    regionSelectedText() {
      return this.formData.applicableRegion.length > 0
        ? `已选择 ${this.formData.applicableRegion.length} 项`
        : '';
    },
    orgSelectedText() {
      return this.formData.authPublishOrg.length > 0
        ? `已选择 ${this.formData.authPublishOrg.length} 项`
        : '';
    },
    attachmentTotalPages() {
      return Math.ceil(this.attachmentTotal / this.attachmentPageSize) || 1;
    },
    sceneTotalPages() {
      return Math.ceil(this.sceneTotal / this.scenePageSize) || 1;
    },
    logTotalPages() {
      return Math.ceil(this.logTotal / this.logPageSize) || 1;
    }
  },
  created() {
    this.letterId = this.$route.query.id;
    this.isNew = !this.letterId;
    this.loadLookupData();
    if (!this.isNew) {
      this.loadDetail();
      this.loadAttachmentList();
      this.loadSceneList();
    }
  },
  methods: {
    async loadLookupData() {
      try {
        const [
          targetLevel,
          region,
          publishLevel,
          publishOrg,
          industry,
          businessScene,
          decisionLevel,
          docType,
          measureUnit,
          ruleParams
        ] = await Promise.all([
          request.get('/lookup/AUTH_TARGET_LEVEL'),
          request.get('/lookup/APPLICABLE_REGION'),
          request.get('/lookup/AUTH_PUBLISH_LEVEL'),
          request.get('/lookup/AUTH_PUBLISH_ORG'),
          request.get('/lookup/INDUSTRY'),
          request.get('/lookup/BUSINESS_SCENE'),
          request.get('/lookup/DECISION_LEVEL'),
          request.get('/lookup/DOC_TYPE'),
          request.get('/lookup/MEASURE_UNIT'),
          request.get('/rule-param/allActive')
        ]);
        this.authTargetLevelList = targetLevel.data || [];
        this.applicableRegionTree = region.data || [];
        this.authPublishLevelList = publishLevel.data || [];
        this.authPublishOrgTree = publishOrg.data || [];
        this.industryTree = industry.data || [];
        this.businessSceneList = businessScene.data || [];
        this.decisionLevelList = decisionLevel.data || [];
        this.docTypeList = docType.data || [];
        this.measureUnitList = measureUnit.data || [];
        this.ruleParamList = ruleParams.data || [];
      } catch (error) {
        console.error('加载下拉数据失败:', error);
      }
    },
    async loadDetail() {
      try {
        const res = await request.get(`/authorization/detail?id=${this.letterId}`);
        if (res.code === 200 && res.data) {
          this.formData = {
            ...res.data,
            authTargetLevel: res.data.authTargetLevel || [],
            applicableRegion: res.data.applicableRegion || [],
            authPublishLevel: res.data.authPublishLevel || [],
            authPublishOrg: res.data.authPublishOrg || []
          };
          if (this.showLogSection) {
            this.loadLogList();
          }
        }
      } catch (error) {
        console.error('加载详情失败:', error);
      }
    },
    async loadAttachmentList() {
      try {
        const res = await request.get(`/attachment/list?letterId=${this.letterId}&pageNum=${this.attachmentPageNum}&pageSize=${this.attachmentPageSize}`);
        if (res.code === 200) {
          this.attachmentList = res.data.list || [];
          this.attachmentTotal = res.data.total || 0;
        }
      } catch (error) {
        console.error('加载附件失败:', error);
      }
    },
    async loadSceneList() {
      try {
        const res = await request.get(`/scene/list?letterId=${this.letterId}&pageNum=${this.scenePageNum}&pageSize=${this.scenePageSize}`);
        if (res.code === 200) {
          this.sceneList = res.data.list || [];
          this.sceneTotal = res.data.total || 0;
        }
      } catch (error) {
        console.error('加载场景失败:', error);
      }
    },
    async loadLogList() {
      try {
        const res = await request.post('/log/list', {
          letterId: this.letterId,
          pageNum: this.logPageNum,
          pageSize: this.logPageSize
        });
        if (res.code === 200) {
          this.logList = res.data.list || [];
          this.logTotal = res.data.total || 0;
        }
      } catch (error) {
        console.error('加载日志失败:', error);
      }
    },
    handleRegionSelect(values) {
      this.formData.applicableRegion = values;
    },
    handleOrgSelect(values) {
      this.formData.authPublishOrg = values;
    },
    async handleSave() {
      if (!this.validateForm()) return;
      try {
        if (this.isNew) {
          const res = await request.post('/authorization/create', this.formData);
          if (res.code === 200) {
            this.letterId = res.data;
            this.isNew = false;
            alert('保存成功');
          }
        } else {
          await request.put('/authorization/update', { ...this.formData, id: this.letterId });
          alert('保存成功');
        }
      } catch (error) {
        alert('保存失败');
      }
    },
    async handleSaveAndPublish() {
      await this.handleSave();
      if (this.letterId) {
        await this.handlePublish();
      }
    },
    async handlePublish() {
      if (!this.letterId) {
        alert('请先保存');
        return;
      }
      try {
        await request.post(`/authorization/publish?id=${this.letterId}`);
        alert('发布成功');
        this.loadDetail();
      } catch (error) {
        alert('发布失败');
      }
    },
    handleCancel() {
      this.$router.push('/#/AuthLetterList');
    },
    handleBack() {
      this.$router.push('/#/AuthLetterList');
    },
    async handleDeactivate() {
      if (!confirm('确定要失效该授权书吗？')) return;
      try {
        await request.post(`/authorization/deactivate?id=${this.letterId}`);
        alert('操作成功');
        this.loadDetail();
      } catch (error) {
        alert('操作失败');
      }
    },
    async handleDelete() {
      if (!confirm('确定要删除该授权书吗？')) return;
      try {
        await request.delete(`/authorization/delete?id=${this.letterId}`);
        alert('删除成功');
        this.$router.push('/#/AuthLetterList');
      } catch (error) {
        alert('删除失败');
      }
    },
    validateForm() {
      if (!this.formData.name) {
        alert('请输入授权书名称');
        return false;
      }
      if (!this.formData.authPublishLevel || this.formData.authPublishLevel.length === 0) {
        alert('请选择授权发布层级');
        return false;
      }
      if (!this.formData.authPublishOrg || this.formData.authPublishOrg.length === 0) {
        alert('请选择授权发布组织');
        return false;
      }
      if (!this.formData.authTargetLevel || this.formData.authTargetLevel.length === 0) {
        alert('请选择授权对象层级');
        return false;
      }
      if (!this.formData.applicableRegion || this.formData.applicableRegion.length === 0) {
        alert('请选择适用区域');
        return false;
      }
      if (!this.formData.publishYear) {
        alert('请选择授权书发布年份');
        return false;
      }
      return true;
    },
    // 附件操作
    handleAttachmentSelectAll() {
      if (this.attachmentSelectAll) {
        this.selectedAttachments = this.attachmentList.map(item => item.id);
      } else {
        this.selectedAttachments = [];
      }
    },
    handleUpload() {
      alert('上传功能暂未实现');
    },
    handleDownloadAttachment() {
      if (this.selectedAttachments.length === 0) {
        alert('请选择要下载的附件');
        return;
      }
      alert('下载功能暂未实现');
    },
    handleDownloadSingle(item) {
      alert('下载功能暂未实现: ' + item.docName);
    },
    async handleDeleteAttachment() {
      if (this.selectedAttachments.length === 0) {
        alert('请选择要删除的附件');
        return;
      }
      if (!confirm('确定要删除选中的附件吗？')) return;
      try {
        await request.post('/attachment/deleteBatch', this.selectedAttachments);
        alert('删除成功');
        this.loadAttachmentList();
      } catch (error) {
        alert('删除失败');
      }
    },
    // 场景操作
    handleSceneSelectAll() {
      if (this.sceneSelectAll) {
        this.selectedScenes = this.sceneList.map(item => item.id);
      } else {
        this.selectedScenes = [];
      }
    },
    handleAddScene() {
      this.editingSceneId = null;
      this.sceneForm = {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        ruleConditions: []
      };
      this.showSceneModal = true;
    },
    handleEditScene(item) {
      this.editingSceneId = item.id;
      this.sceneForm = {
        sceneName: item.sceneName,
        industry: item.industry || [],
        businessScene: item.businessScene,
        decisionLevel: item.decisionLevel,
        specificRule: item.specificRule,
        ruleConditions: item.ruleConfig ? this.parseRuleConfig(item.ruleConfig) : []
      };
      this.showSceneModal = true;
    },
    parseRuleConfig(config) {
      if (!config) return [];
      try {
        const parsed = typeof config === 'string' ? JSON.parse(config) : config;
        return this.flattenConditions(parsed);
      } catch (e) {
        return [];
      }
    },
    flattenConditions(group) {
      let result = [];
      if (group.conditions) {
        group.conditions.forEach((cond, index) => {
          if (cond.type === 'condition') {
            result.push({
              fieldId: cond.fieldId,
              fieldName: cond.fieldName,
              operator: cond.operator,
              compareType: cond.compareType,
              compareValue: cond.compareValue,
              unit: cond.unit,
              compareFieldId: cond.compareFieldId,
              logic: index < group.conditions.length - 1 ? group.logic : null
            });
          }
        });
      }
      return result;
    },
    closeSceneModal() {
      this.showSceneModal = false;
    },
    async saveScene() {
      if (!this.sceneForm.sceneName) {
        alert('请输入场景名称');
        return;
      }
      try {
        const data = {
          id: this.editingSceneId,
          letterId: this.letterId,
          ...this.sceneForm,
          ruleConfig: this.buildRuleConfig()
        };
        if (this.editingSceneId) {
          await request.put('/scene/update', data);
        } else {
          await request.post('/scene/create', data);
        }
        alert('保存成功');
        this.closeSceneModal();
        this.loadSceneList();
      } catch (error) {
        alert('保存失败');
      }
    },
    buildRuleConfig() {
      if (!this.sceneForm.ruleConditions || this.sceneForm.ruleConditions.length === 0) {
        return null;
      }
      return {
        logic: 'AND',
        conditions: this.sceneForm.ruleConditions.map(cond => ({
          type: 'condition',
          fieldId: cond.fieldId,
          fieldName: this.getFieldName(cond.fieldId),
          operator: cond.operator,
          compareType: cond.compareType,
          compareValue: cond.compareValue,
          unit: cond.unit,
          compareFieldId: cond.compareFieldId
        }))
      };
    },
    getFieldName(fieldId) {
      const field = this.ruleParamList.find(f => f.id === fieldId);
      return field ? field.name : '';
    },
    async handleDeleteScene() {
      if (this.selectedScenes.length === 0) {
        alert('请选择要删除的场景');
        return;
      }
      if (!confirm('确定要删除选中的场景吗？')) return;
      try {
        await request.post('/scene/deleteBatch', this.selectedScenes);
        alert('删除成功');
        this.loadSceneList();
      } catch (error) {
        alert('删除失败');
      }
    },
    async handleDeleteSingleScene(id) {
      if (!confirm('确定要删除该场景吗？')) return;
      try {
        await request.delete(`/scene/delete?id=${id}`);
        alert('删除成功');
        this.loadSceneList();
      } catch (error) {
        alert('删除失败');
      }
    },
    // 规则配置
    addCondition() {
      this.sceneForm.ruleConditions.push({
        fieldId: '',
        operator: '>',
        compareType: 'NUMBER',
        compareValue: '',
        unit: '',
        compareFieldId: '',
        logic: 'AND'
      });
    },
    addConditionGroup() {
      // 简化实现：添加一个子条件组的条件
      this.sceneForm.ruleConditions.push({
        fieldId: '',
        operator: '>',
        compareType: 'NUMBER',
        compareValue: '',
        unit: '',
        compareFieldId: '',
        logic: 'AND',
        isGroup: true
      });
    },
    removeCondition(index) {
      this.sceneForm.ruleConditions.splice(index, 1);
    },
    onFieldChange(condition) {
      const field = this.ruleParamList.find(f => f.id === condition.fieldId);
      if (field) {
        condition.compareType = field.dataType === 'NUMBER' ? 'NUMBER' : 'TEXT';
      }
    },
    toggleLogic(index) {
      const condition = this.sceneForm.ruleConditions[index];
      condition.logic = condition.logic === 'AND' ? 'OR' : 'AND';
    },
    // 工具方法
    getStatusText(status) {
      const map = {
        'DRAFT': '草稿',
        'PUBLISHED': '已发布',
        'INVALID': '已失效'
      };
      return map[status] || status;
    },
    getDocTypeText(type) {
      const item = this.docTypeList.find(d => d.value === type);
      return item ? item.label : type;
    },
    getBusinessSceneText(value) {
      const item = this.businessSceneList.find(d => d.value === value);
      return item ? item.label : value;
    },
    getDecisionLevelText(value) {
      const item = this.decisionLevelList.find(d => d.value === value);
      return item ? item.label : value;
    },
    formatTime(time) {
      if (!time) return '-';
      return time.replace('T', ' ').substring(0, 19);
    }
  },
  components: {
    'tree-node': {
      template: '#tree-node-template-detail',
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
  padding-bottom: 80px;
}

.page-header {
  margin-bottom: 20px;
}

.section {
  margin-bottom: 20px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.section-title {
  padding: 12px 20px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
  font-weight: 600;
}

.section-title.collapsible {
  cursor: pointer;
}

.collapse-icon {
  float: right;
}

.section-content {
  padding: 20px;
}

.section-toolbar {
  padding: 10px 20px;
  border-bottom: 1px solid #e8e8e8;
}

.form-container {
  padding: 20px;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 15px;
}

.form-item {
  display: flex;
  align-items: flex-start;
  margin-right: 20px;
  margin-bottom: 10px;
}

.form-item.full-width {
  width: 100%;
}

.form-item label {
  width: 100px;
  text-align: right;
  margin-right: 10px;
  color: #666;
  line-height: 32px;
  flex-shrink: 0;
}

.form-item.required label::before {
  content: '*';
  color: #f00;
  margin-right: 4px;
}

.form-item input,
.form-item select,
.form-item textarea {
  width: 200px;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-item textarea {
  width: calc(100% - 120px);
  height: 80px;
  padding: 10px;
  resize: vertical;
}

.form-item select[multiple] {
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
  width: auto;
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

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
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

.icon-btn {
  cursor: pointer;
  color: #1890ff;
  margin-right: 10px;
}

.icon-btn.disabled {
  color: #ccc;
  cursor: not-allowed;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 15px;
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

.footer-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px 20px;
  background: #fff;
  border-top: 1px solid #e8e8e8;
  text-align: center;
  z-index: 100;
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
  width: 800px;
  max-height: 90vh;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
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
  overflow-y: auto;
  max-height: calc(90vh - 120px);
}

.modal-footer {
  padding: 15px 20px;
  border-top: 1px solid #e8e8e8;
  text-align: right;
}

.rule-config {
  margin-top: 20px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.rule-config-header {
  padding: 10px 15px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
  font-weight: 600;
}

.rule-config-body {
  padding: 15px;
}

.rule-config-actions {
  margin-top: 10px;
}

.text-btn {
  color: #1890ff;
  cursor: pointer;
  margin-right: 20px;
}

.condition-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.condition-row select,
.condition-row input {
  width: 120px;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-right: 10px;
  font-size: 13px;
}

.condition-row input {
  width: 100px;
}

.remove-btn {
  color: #999;
  cursor: pointer;
  margin-left: 10px;
}

.condition-logic {
  margin-bottom: 10px;
  padding-left: 20px;
}

.logic-btn {
  display: inline-block;
  padding: 4px 12px;
  background: #f0f0f0;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}
</style>