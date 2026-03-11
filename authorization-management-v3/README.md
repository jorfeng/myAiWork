# 授权书管理系统 V3 (Authorization Management System V3)

基于 Spring Boot + Vue 3 构建的授权书管理系统，本次开发内容为授权书列表页。

## 项目结构

```
authorization-management-v3/
├── backend/                    # 后端 - Java Spring Boot
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/auth/letter/
│       │   │   ├── entity/           # 实体类
│       │   │   ├── dto/              # 数据传输对象
│       │   │   ├── repository/       # 数据访问层
│       │   │   ├── service/          # 服务接口
│       │   │   │   └── impl/         # 服务实现
│       │   │   ├── controller/       # REST API控制器 (使用@Path)
│       │   │   ├── enums/            # 枚举类
│       │   │   └── config/           # 配置类
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/     # Flyway迁移脚本
│       └── test/                     # 测试代码
│
├── frontend/                   # 前端 - Vue.js
│   ├── preview.html           # 静态预览页面
│   └── src/
│       ├── api/                # API接口
│       └── views/              # 页面组件
│           └── AuthLetterList.vue
│
└── README.md
```

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- JAX-RS (Jersey) - 使用 @Path 定义URL
- PostgreSQL
- Flyway (数据库迁移)
- Lombok

### 前端
- Vue 3 + Composition API
- Element Plus
- Axios

## 功能特性 - 授权书列表页

### 查询条件
| 字段名 | 控件类型 | 说明 |
|--------|----------|------|
| 授权书名称 | 文本输入框 | 模糊查询 |
| 授权对象层级 | 多选下拉框 | lookup服务获取 |
| 适用区域 | 多选下拉框 | lookup服务获取 |
| 授权发布层级 | 多选下拉框 | lookup服务获取 |
| 授权发布组织 | 树形多选下拉框 | lookup服务获取 |
| 授权书发布年份 | 年份选择器 | |
| 状态 | 下拉框 | 草稿/已发布/已失效 |

### 功能按钮
- **新建授权书**: 新建授权书
- **更新**: 批量更新选中数据 (TODO)
- **生效**: 将草稿状态的授权书发布
- **失效**: 将已发布状态的授权书设为失效
- **删除**: 删除草稿状态的授权书

### 表格字段
| 字段名 | 说明 |
|--------|------|
| 复选框 | 多选用 |
| 序号 | 根据分页自动计算 |
| 操作 | 草稿态显示编辑图标 |
| 授权书名称 | 超链接，跳转详情页 |
| 状态 | 草稿(灰)/已发布(绿)/已失效(红) |
| 授权对象层级 | |
| 适用区域 | |
| 授权发布层级 | |
| 授权发布组织 | |
| 授权书发布年份 | |
| 创建人 | |
| 创建时间 | |

## 快速开始

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端预览

直接在浏览器中打开 `frontend/preview.html` 文件即可预览页面效果。

## API接口

### 授权书管理
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/auth-letters | 分页查询授权书列表 |
| PUT | /api/auth-letters/batch/publish | 批量生效 |
| PUT | /api/auth-letters/batch/expire | 批量失效 |
| DELETE | /api/auth-letters/batch | 批量删除 |

### Lookup服务 (TODO)
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/lookup/{code} | 获取lookup值列表 |
| GET | /api/lookup/org/tree | 获取组织树 |

## 数据库

使用Flyway管理数据库迁移，迁移脚本位于 `src/main/resources/db/migration/`

### 迁移文件
- `V1__init_schema.sql` - 初始化表结构

## 注意事项

1. **Lookup服务**: 当前为模拟数据，待对接真实服务
2. **组织树**: 当前为模拟数据，待对接真实服务
3. **新建/更新功能**: 待后续开发

## License

MIT