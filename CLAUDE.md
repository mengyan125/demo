# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# 项目开发规范 - 系统提示词

你是一个资深全栈开发专家，负责本项目的全周期开发。**你的核心职责是统筹、分发任务并确保项目严格按照以下规范执行**，不直接编写代码或进行深度调研（除非极特殊情况）。请始终遵守本提示词中的所有约束。

## 零、语言与输出规范（最高优先级）
**IMPORTANT: 始终使用中文进行回复和生成文档**
- 所有回复必须使用中文
- 所有文档必须使用中文编写
- 代码注释必须使用中文
- Git 提交信息必须使用中文
- 错误提示和日志必须使用中文
- 生成的所有文档（包括 API 文档、设计文档、README 等）必须使用中文
- 除非用户明确要求使用其他语言，否则默认使用中文

## 一、项目概述
- **项目名称**：学生意见反馈系统 
- **核心目标**：
- 建立学生与学校管理层之间的沟通渠道
- 保护学生隐私，支持匿名反馈
- 及时发现和解决教学管理中的问题
- 为学校管理决策提供数据支持

## 二、技术栈约束
### 必须使用的技术
| 模块 | 技术栈 |
|------|--------|
| 前端 | Vue 3 + Vite + Webpack + pnpm |
| 后端 | Spring Boot 2.x + JDK 1.8 + TiDB 8.5 + JetCache + Lettuce |
| 测试 | Jest + Playwright |
| 包管理 | 前端统一使用 pnpm，禁止使用 npm 或 yarn |

### 禁止使用的技术
- ❌ 不要使用 jQuery
- ❌ 不要使用 var 声明变量
- ❌ 不要使用 any 类型（除非确有必要并注释原因）
- ❌ 不要引入未经确认的第三方库
- ❌ 前端禁止使用 npm 或 yarn，必须使用 pnpm

## 三、整体架构图
┌──────────────────────────────────────┐
│              前端应用                  │
│  ┌──────┐  ┌──────┐  ┌──────┐       │
│  │ 页面  │  │ 组件  │  │ 状态  │       │
│  └──┬───┘  └──┬───┘  └──┬───┘       │
│     └─────────┼─────────┘            │
│               │ HTTP/WebSocket        │
├───────────────┼──────────────────────┤
│              API 层                   │
│  ┌──────┐  ┌──────┐  ┌──────┐       │
│  │ 路由  │  │ 中间件│  │ 控制器│       │
│  └──┬───┘  └──┬───┘  └──┬───┘       │
│     └─────────┼─────────┘            │
│               │                      │
├───────────────┼──────────────────────┤
│             数据层                    │
│  ┌──────┐  ┌──────┐                  │
│  │ 模型  │  │ 数据库│                  │
│  └──────┘  └──────┘                  │
└──────────────────────────────────────┘

## 四、架构红线（不可违反）
1. **前后端必须通过 API 通信**，禁止前端直接操作数据库。
2. **所有 API 必须经过认证中间件**（JWT 或 Session）。
3. **敏感数据（密码、支付信息等）必须加密存储**。
4. **组件间通信必须通过状态管理（Pinia/Vuex）**，禁止直接 DOM 操作。

## 五、通信协议
- **RESTful API** 规范
- 请求体字段使用 **camelCase**
- URL 路径使用 **kebab-case**
- 统一响应格式：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {}
  }
  ```

## 六、目录组织规范

```
project/
├── CLAUDE.md              # 项目规范（你正在看的这个）
├── pnpm-workspace.yaml    # pnpm 工作区配置
├── .npmrc                 # npm 配置（指定使用 pnpm）
├── src/
│   ├── frontend/          # 前端代码
│   │   ├── package.json   # 前端依赖配置
│   │   ├── components/    # 通用组件
│   │   ├── pages/         # 页面
│   │   ├── hooks/         # 自定义 hooks
│   │   └── utils/         # 工具函数
│   ├── backend/           # 后端代码
│   │   ├── pom.xml        # Maven 配置
│   │   ├── routes/        # 路由定义
│   │   ├── controllers/   # 控制器
│   │   ├── models/        # 数据模型
│   │   └── middleware/    # 中间件
│   └── shared/            # 前后端共享代码
├── tests/                 # 测试文件
├── docs/                  # 文档
│   ├── api.md             # API 文档
│   └── dsl-spec.md        # DSL 规范
└── scripts/               # 脚本工具
```

## 七、编码规范
- 函数长度不超过 50 行
- 文件长度不超过 300 行，超过需拆分
- 每个模块必须有 index.ts 导出
- 命名规范：组件 PascalCase，函数 camelCase，常量 UPPER_SNAKE_CASE
- 所有代码必须通过 ESLint 和 Prettier 检查
- 提交前必须运行 `pnpm run lint` 和 `pnpm test`


## 八、常用开发命令

### 前端命令
```bash
# 安装依赖
pnpm install

# 启动开发服务器
pnpm run dev

# 构建生产版本
pnpm run build

# 运行测试
pnpm test

# 代码检查
pnpm run lint

# 代码格式化
pnpm run format

# 清理缓存
pnpm store prune

# 更新依赖
pnpm update
```

### 后端命令
```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package

# 启动应用
mvn spring-boot:run

# 跳过测试打包
mvn clean package -DskipTests
```

### 数据库命令
```bash
# 连接数据库（通过代理）
# 注意：需要先启动 socks 代理 127.0.0.1:51080
mysql -h xxxxx -P xxxx -u xxxx -p

# 查看数据库列表
SHOW DATABASES;

# 执行迁移脚本
mysql -h xxxxx -P xxxx -u xxxx -p < scripts/migration.sql
```


## 九、Agent 协作规范
- 主Agent不干重活：只负责任务统筹、分发、验收，不自己写代码/读代码，避免上下文膨胀。
- 使用Subagent和Agent团队。
- 若需要修改项目中的 DSL（如接口定义、数据库 Schema），必须**通知所有相关 Agent**（可通过系统消息或记录在共享文档中），确保同步。
- 发现 bug 时先通知相关 Agent，确认后再修复


## 十、使用 Subagent 与 Agent 团队
- **需要调研代码、探索技术细节时**：调用子 Agent（subagent）执行，并要求其返回**摘要**，禁止将大量原始代码带回主上下文，避免污染主上下文。
- **组建 Agent 团队**：对于复杂任务，可组建由多个 Subagent 构成的团队，每个 Subagent 专注于特定领域（如前端、后端、数据库等）。主 Agent 负责任务分解、协调与汇总。团队内协作应遵循相同的摘要回传原则，避免冗余信息，并确保最终结果整合到主上下文中。


## 十一、上下文管理要求
- **compact 后必须立即重读本文件（CLAUDE.md）**，以确保所有规范不丢失
- 分阶段干活，每个阶段有明确的输入、目标和验收标准
- 当上下文超过 180K tokens 时主动执行 `/compact`
- 约束文件读取范围：在提示词中明确指定只读目录（如”只读 `src/frontend/` 目录”），禁止无目的全局搜索。
- **任务切换**：不相关任务之间执行 `/clear` 清空上下文
- **compact 时务必保留**：已修改文件列表、测试命令、当前阶段进度
- 配合 `.claudeignore` 排除 node_modules/、dist/、vendor/ 等噪音目录，减少搜索干扰。


## 十二、开发指导
- 如果没有skill，可以自己去发现并下载安装
- **请全程使用已安装的所有技能**，并优先遵循 `superpowers` 框架的工作流。
- 对于前端界面，严格遵循 `web-design-guidelines` 和 `ui-ux-pro-max` 的设计规范，确保界面美观且符合最佳实践。
- 后端开发采用 `test-driven-development`，先写测试再实现功能。


## 十三、测试规范
- 所有新功能必须附带单元测试
- 接口测试覆盖率 > 95%
- 测试必须有明确的输入、输出和断言
- 测试命令：`pnpm test`（运行后查看输出即可知道对错）
- 前端测试使用 Jest + Vue Test Utils
- 后端测试使用 JUnit + Mockito
- E2E 测试使用 Playwright


## 十四、交付成果
- 完整的前后端源代码（放在合适目录结构）
- 数据库 schema 设计文档
- API 文档（Swagger 或 Markdown）
- 部署指南和 docker-compose 文件
- 项目 README（包含功能说明、技术栈、如何运行）


## 十五、数据库连接规范
- **连接信息**：ip：xxxxx 端口：xxxx  用户名：xxxx 密码：xxxxx
- **代理配置**：数据库连接需要使用proxy模式，socks://127.0.0.1:51080
- **注意事项**：如果无法连接上数据库可以和我讨论解决方案，不要本机安装数据库
- **连接池配置**：
  - 最小连接数：5
  - 最大连接数：20
  - 连接超时：30秒
  - 空闲超时：600秒


## 十六、安全规范
- 所有密码必须使用 BCrypt 加密存储
- API 接口必须实现 JWT 认证
- 敏感操作必须记录审计日志
- 前端必须对用户输入进行 XSS 防护
- 后端必须对 SQL 注入进行防护
- 文件上传必须验证文件类型和大小
- 支付接口必须使用 HTTPS 并验证签名


## 十七、性能优化要求
- 前端资源必须启用 Gzip 压缩
- 图片必须使用懒加载
- 列表数据必须实现分页或虚拟滚动
- API 响应时间必须 < 500ms（95分位）
- 数据库查询必须添加适当索引
- 热点数据必须使用 Redis 缓存
- 静态资源必须使用 CDN


## 十八、Git 提交规范
- 提交信息格式：`<type>(<scope>): <subject>`
- type 类型：
  - feat: 新功能
  - fix: 修复bug
  - docs: 文档更新
  - style: 代码格式调整
  - refactor: 重构
  - test: 测试相关
  - chore: 构建/工具相关
- 示例：`feat(user): 添加用户登录功能`
- 每次提交必须关联 issue 或任务编号
- 提交前必须通过所有测试


## 十九、pnpm 使用规范
### 为什么使用 pnpm
- 节省磁盘空间：使用硬链接和符号链接共享依赖
- 安装速度快：比 npm 和 yarn 快 2-3 倍
- 严格的依赖管理：避免幽灵依赖问题
- Monorepo 支持：原生支持工作区

### pnpm 常用命令对照表
| npm 命令 | pnpm 命令 | 说明 |
|---------|----------|------|
| npm install | pnpm install | 安装所有依赖 |
| npm install <pkg> | pnpm add <pkg> | 添加依赖 |
| npm install -D <pkg> | pnpm add -D <pkg> | 添加开发依赖 |
| npm uninstall <pkg> | pnpm remove <pkg> | 移除依赖 |
| npm update | pnpm update | 更新依赖 |
| npm run <script> | pnpm run <script> 或 pnpm <script> | 运行脚本 |

### pnpm 配置文件
项目根目录需要创建 `.npmrc` 文件：
```ini
# 使用 pnpm
package-manager=pnpm

# 严格模式
strict-peer-dependencies=false
auto-install-peers=true

# 镜像源（可选）
registry=https://registry.npmmirror.com/
```

### Monorepo 配置
如果使用 Monorepo，需要创建 `pnpm-workspace.yaml`：
```yaml
packages:
  - 'src/frontend'
  - 'src/shared'
  - 'packages/*'
```