# Housekeeping 系统接口文档

## 文档概述

本文档详细描述了 Housekeeping 管理系统的所有后端接口规范，包括请求参数、返回格式、错误码、权限控制等内容。

### 更新日志

| 版本 | 日期 | 说明 | 作者 |
|------|------|------|------|
| 1.0.0 | 2026-01-05 | 初始版本，定义基础接口 | AI |
| 1.1.0 | 2026-01-13 | 新增员工管理、服务管理、订单管理、会员管理、评价管理等模块 | AI |
| 1.2.0 | 2026-01-13 | 更新员工管理接口，支持用户与员工信息分离存储，新增员工专业信息字段（工龄、身份、团队信息、履历） | AI |

---

## 基础信息

### 服务器配置

- **基础URL**: `http://localhost:3000/api`
- **协议**: HTTP/1.1, HTTPS (生产环境)
- **字符编码**: UTF-8
- **数据格式**: JSON (application/json)
- **时区**: Asia/Shanghai (UTC+8)

### 认证方式

- **认证方式**: Bearer Token (JWT)
- **Token获取**: 通过登录接口获取
- **Token有效期**: 24小时
- **Token刷新**: 支持Token刷新，过期前1小时内可自动刷新
- **Token存放位置**: HTTP请求头 `Authorization: Bearer {token}`

### 跨域配置

- **允许的域名**: 根据配置动态设置
- **允许的请求方法**: GET, POST, PUT, DELETE, PATCH, OPTIONS
- **允许的请求头**: Content-Type, Authorization, X-Requested-With
- **允许携带凭证**: 是 (withCredentials: true)

---

## 通用响应格式

### 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2026-01-05T10:00:00Z",
  "traceId": "uuid-v4-string"
}
```

### 错误响应

```json
{
  "code": 400,
  "message": "请求参数错误",
  "error": "详细错误信息",
  "details": {
    "field": "username",
    "reason": "用户名长度必须在3-20位之间"
  },
  "timestamp": "2026-01-05T10:00:00Z",
  "traceId": "uuid-v4-string"
}
```

### 分页响应

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 100,
      "totalPages": 10,
      "hasMore": true
    }
  }
}
```

---

## 错误码说明

### HTTP状态码

| 状态码 | 说明 | 示例场景 |
|--------|------|----------|
| 200 | 请求成功 | 数据获取、创建、更新、删除成功 |
| 201 | 创建成功 | 新资源创建成功 |
| 204 | 请求成功无内容 | 删除成功不返回数据 |
| 400 | 请求参数错误 | 参数缺失、格式不正确、验证失败 |
| 401 | 未授权/登录失效 | Token无效、Token过期、未登录 |
| 403 | 权限不足 | 无权访问该资源、角色权限不足 |
| 404 | 资源不存在 | 请求的用户/任务/其他资源不存在 |
| 409 | 资源冲突 | 用户名已存在、数据版本冲突 |
| 429 | 请求过于频繁 | 触发频率限制 |
| 500 | 服务器内部错误 | 服务器异常、数据库错误 |
| 503 | 服务不可用 | 系统维护中 |

### 业务错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 10001 | 用户名或密码错误 | 检查用户名和密码是否正确 |
| 10002 | 用户不存在 | 确认用户名或注册新用户 |
| 10003 | 用户已存在 | 更换用户名进行注册 |
| 10004 | Token无效 | 重新登录获取新Token |
| 10005 | Token过期 | 刷新Token或重新登录 |
| 10006 | 密码错误次数过多 | 等待30分钟后重试 |
| 10007 | 验证码错误 | 重新输入验证码 |
| 10008 | 验证码已过期 | 重新获取验证码 |
| 10009 | 手机号格式错误 | 输入正确的11位手机号 |
| 10010 | 邮箱格式错误 | 输入正确的邮箱地址 |
| 20001 | 任务不存在 | 确认任务ID是否正确 |
| 20002 | 任务已被删除 | 任务已被移除，无法操作 |
| 20003 | 任务状态不允许该操作 | 检查任务当前状态 |
| 30001 | 文件上传失败 | 检查文件大小和格式 |
| 30002 | 文件类型不支持 | 使用允许的文件格式 |
| 30003 | 文件大小超限 | 压缩文件或分批上传 |
| 40001 | 权限不足 | 联系管理员获取权限 |
| 50001 | 系统维护中 | 稍后重试或联系技术支持 |

---

## 1. 用户认证相关

### 1.1 用户名密码登录

- **接口路径**: `POST /auth/login`
- **接口描述**: 通过用户名和密码进行身份验证，获取访问令牌
- **权限要求**: 无需认证（公开接口）
- **请求频率限制**: 每分钟5次
- **请求头**:
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| username | string | 是 | 用户名 | 3-20位字母、数字、下划线 |
| password | string | 是 | 密码 | 6-20位任意字符，建议包含字母和数字 |

- **请求示例**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

- **注意事项**:
  - 连续登录失败5次将锁定账户30分钟
  - 登录成功会记录登录IP、时间、设备信息
  - 返回的token应妥善保存在本地存储中
  - 密码在传输过程中建议使用HTTPS加密

- **成功响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiYWRtaW4iLCJleHAiOjE3MDQ0Njg4MDB9.signature",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "超级管理员",
      "role": "admin",
      "avatar": "",
      "email": "admin@example.com",
      "phone": "13800138000",
      "joinDate": "2024-01-01",
      "lastLogin": "2026-01-05 10:00:00"
    }
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

- **失败响应**:
```json
{
  "code": 10001,
  "message": "用户名或密码错误",
  "error": "Invalid credentials",
  "details": {
    "remainingAttempts": 4,
    "lockTime": null
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 1.2 发送短信验证码

- **接口路径**: `POST /auth/send-sms`
- **接口描述**: 向指定手机号发送短信验证码，用于登录、注册或密码重置
- **权限要求**: 无需认证（公开接口）
- **请求频率限制**: 同一手机号每分钟1次，每天10次
- **请求头**:
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| phone | string | 是 | 手机号 | 11位数字，中国大陆手机号 |
| type | string | 是 | 验证码类型 | login(登录)/register(注册)/reset(密码重置)/bind(绑定) |

- **请求示例**:
```json
{
  "phone": "13800138001",
  "type": "login"
}
```

- **注意事项**:
  - 手机号必须是合法的中国大陆手机号
  - 验证码有效期为5分钟
  - 验证码为6位随机数字
  - 同一手机号在60秒内不能重复发送
  - 验证码以短信形式发送，可能存在网络延迟
  - 生产环境需要配置短信服务商

- **成功响应**:
```json
{
  "code": 200,
  "message": "发送成功",
  "data": {
    "sessionId": "sms_session_20260105_123456",
    "expireTime": 300,
    "nextSendTime": 60
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

- **失败响应**:
```json
{
  "code": 429,
  "message": "发送过于频繁",
  "error": "Too many requests",
  "details": {
    "remainingTime": 45
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 1.3 手机号验证码登录

- **接口路径**: `POST /auth/login-sms`
- **接口描述**: 使用手机号和短信验证码进行快速登录
- **权限要求**: 无需认证（公开接口）
- **请求频率限制**: 每分钟3次
- **请求头**:
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| phone | string | 是 | 手机号 | 11位数字 |
| code | string | 是 | 验证码 | 6位数字 |
| sessionId | string | 是 | 会话ID | 发送验证码时返回的ID |

- **请求示例**:
```json
{
  "phone": "13800138001",
  "code": "123456",
  "sessionId": "sms_session_20260105_123456"
}
```

- **注意事项**:
  - 验证码错误次数超过3次会话失效
  - sessionId过期需要重新获取验证码
  - 登录成功后sessionId立即失效
  - 首次使用手机号登录会自动创建账户，默认角色为member
  - 新用户首次登录需要完善个人信息

- **成功响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 10,
      "username": "user_13800138001",
      "nickname": "微信用户",
      "role": "member",
      "phone": "13800138001",
      "avatar": "",
      "joinDate": "2026-01-05",
      "lastLogin": "2026-01-05 10:00:00"
    },
    "isNewUser": true
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

- **失败响应**:
```json
{
  "code": 10007,
  "message": "验证码错误",
  "error": "Invalid verification code",
  "details": {
    "remainingAttempts": 2
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 1.4 微信授权登录

- **接口路径**: `POST /auth/wechat-login`
- **接口描述**: 通过微信小程序或公众号进行授权登录
- **权限要求**: 无需认证（公开接口）
- **请求头**:
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| code | string | 是 | 微信授权码 | 通过wx.login()获取，有效期5分钟 |
| userInfo | object | 否 | 微信用户信息 | 用户可选授权 |

- **userInfo子参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nickname | string | 否 | 微信昵称，最多20个字符 |
| avatar | string | 否 | 头像URL |
| gender | number | 否 | 性别：1(男)/2(女)/0(未知) |
| city | string | 否 | 城市 |
| province | string | 否 | 省份 |
| country | string | 否 | 国家 |

- **请求示例**:
```json
{
  "code": "0612a3b6",
  "userInfo": {
    "nickname": "微信用户",
    "avatar": "https://thirdwx.qlogo.cn/...",
    "gender": 1,
    "city": "深圳",
    "province": "广东",
    "country": "中国"
  }
}
```

- **注意事项**:
  - code只能使用一次，使用后立即失效
  - 需要在微信小程序或公众号环境中使用
  - 用户信息需要用户主动授权才能获取
  - openId用于唯一标识微信用户
  - unionId用于关联同一用户的不同应用
  - 首次登录会自动创建账户
  - 生产环境需要配置微信AppID和AppSecret

- **成功响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 11,
      "username": "wx_user_123456",
      "nickname": "微信用户",
      "role": "member",
      "wechatOpenId": "wx_openid_123456",
      "wechatUnionId": "wx_unionid_123456",
      "avatar": "https://thirdwx.qlogo.cn/...",
      "joinDate": "2026-01-05",
      "lastLogin": "2026-01-05 10:00:00"
    },
    "isNewUser": true
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 1.5 微信绑定手机号

- **接口路径**: `POST /auth/bind-phone`
- **接口描述**: 微信登录用户绑定手机号
- **权限要求**: 需要认证
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| phone | string | 是 | 手机号 | 11位数字 |
| code | string | 是 | 验证码 | 6位数字 |
| sessionId | string | 是 | 会话ID | 发送验证码时返回的ID |

- **请求示例**:
```json
{
  "phone": "13800138001",
  "code": "123456",
  "sessionId": "sms_session_20260105_123456"
}
```

- **注意事项**:
  - 绑定后手机号将成为用户的唯一标识之一
  - 一个手机号只能绑定一个账户
  - 绑定成功后会发送通知

- **成功响应**:
```json
{
  "code": 200,
  "message": "绑定成功",
  "data": {
    "phone": "13800138001",
    "bindTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 1.6 用户登出

- **接口路径**: `POST /auth/logout`
- **接口描述**: 用户退出登录，使当前Token失效
- **权限要求**: 需要认证
- **请求头**:
  - `Authorization: Bearer {token}`

- **请求示例**:
```json
{}
```

- **注意事项**:
  - 登出后Token将被加入黑名单
  - 客户端应清除本地存储的Token
  - Token在服务端也会被标记为失效

- **成功响应**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": {
    "logoutTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 1.7 刷新Token

- **接口路径**: `POST /auth/refresh`
- **接口描述**: 使用当前Token获取新的访问令牌
- **权限要求**: 需要认证
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| refreshToken | string | 是 | 刷新令牌 |

- **请求示例**:
```json
{
  "refreshToken": "refresh_token_string"
}
```

- **注意事项**:
  - 只能在Token过期前1小时内刷新
  - 刷新后旧Token立即失效
  - RefreshToken只能使用一次
  - 建议在Token即将过期前自动刷新

- **成功响应**:
```json
{
  "code": 200,
  "message": "刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "refreshToken": "new_refresh_token_string"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 1.8 获取当前用户信息

- **接口路径**: `GET /auth/me`
- **接口描述**: 获取当前登录用户的详细信息
- **权限要求**: 需要认证
- **请求头**:
  - `Authorization: Bearer {token}`

- **注意事项**:
  - 返回的信息不包含敏感数据（如密码）
  - 包含用户的完整权限列表

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "超级管理员",
    "role": "admin",
    "email": "admin@example.com",
    "phone": "13800138000",
    "avatar": "",
    "status": "active",
    "isVerified": true,
    "joinDate": "2024-01-01",
    "lastLogin": "2026-01-05 10:00:00",
    "permissions": [
      "user:view",
      "user:create",
      "user:update",
      "user:delete",
      "task:view",
      "task:create",
      "task:update",
      "task:delete",
      "system:manage"
    ]
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

## 2. 用户管理相关

### 2.1 获取用户列表

- **接口路径**: `GET /users`
- **接口描述**: 分页获取系统用户列表，支持多条件搜索和筛选
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | number | 否 | 页码，最小值为1 | 1 |
| pageSize | number | 否 | 每页数量，范围1-100 | 10 |
| keyword | string | 否 | 搜索关键词，可搜索用户名、昵称、手机号、邮箱 | - |
| status | string | 否 | 状态筛选 | all |
| role | string | 否 | 角色筛选 | - |
| sortBy | string | 否 | 排序字段 | createdAt |
| sortOrder | string | 否 | 排序方向 | desc |

- **status可选值**:
  - `all`: 全部用户
  - `active`: 启用状态
  - `disabled`: 禁用状态

- **role可选值**:
  - `member`: 普通会员
  - `manager`: 管理者
  - `admin`: 管理员

- **sortBy可选值**:
  - `id`: 按ID排序
  - `username`: 按用户名排序
  - `createdAt`: 按创建时间排序
  - `lastLogin`: 按最后登录时间排序

- **sortOrder可选值**:
  - `asc`: 升序
  - `desc`: 降序

- **请求示例**:
```
GET /users?page=1&pageSize=10&keyword=admin&status=active&role=admin&sortBy=createdAt&sortOrder=desc
```

- **注意事项**:
  - 支持按用户名、昵称、手机号、邮箱进行模糊搜索
  - 支持按用户状态和角色进行精确筛选
  - 敏感信息（如密码）不会返回
  - 只返回用户有权限查看的数据

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "超级管理员",
        "role": "admin",
        "email": "admin@example.com",
        "phone": "13800138000",
        "avatar": "",
        "status": "active",
        "isVerified": true,
        "joinDate": "2024-01-01",
        "lastLogin": "2026-01-05 10:00:00",
        "createdAt": "2024-01-01 00:00:00",
        "updatedAt": "2026-01-05 09:00:00"
      },
      {
        "id": 2,
        "username": "manager",
        "nickname": "管理员",
        "role": "manager",
        "email": "manager@example.com",
        "phone": "13800138001",
        "avatar": "",
        "status": "active",
        "isVerified": true,
        "joinDate": "2024-01-15",
        "lastLogin": "2026-01-05 09:00:00",
        "createdAt": "2024-01-15 10:00:00",
        "updatedAt": "2026-01-04 15:00:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 100,
      "totalPages": 10,
      "hasMore": true
    }
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 2.2 获取用户详情

- **接口路径**: `GET /users/{id}`
- **接口描述**: 获取指定用户的详细信息
- **权限要求**: 
  - 管理员及以上：可以查看所有用户
  - 普通用户：只能查看自己的信息
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 用户ID |

- **请求示例**:
```
GET /users/1
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "超级管理员",
    "role": "admin",
    "email": "admin@example.com",
    "phone": "13800138000",
    "avatar": "",
    "status": "active",
    "isVerified": true,
    "idCard": "110***********1234",
    "address": "北京市朝阳区",
    "joinDate": "2024-01-01",
    "lastLogin": "2026-01-05 10:00:00",
    "createdAt": "2024-01-01 00:00:00",
    "updatedAt": "2026-01-05 09:00:00",
    "loginCount": 125,
    "loginHistory": [
      {
        "time": "2026-01-05 10:00:00",
        "ip": "192.168.1.1",
        "device": "Chrome/Windows"
      },
      {
        "time": "2026-01-04 09:30:00",
        "ip": "192.168.1.1",
        "device": "Chrome/Windows"
      }
    ]
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 2.3 新增用户

- **接口路径**: `POST /users`
- **接口描述**: 创建新用户
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| username | string | 是 | 用户名，唯一 | 3-20位字母、数字、下划线 |
| password | string | 是 | 密码 | 6-20位任意字符 |
| nickname | string | 否 | 昵称 | 最多20个字符 |
| role | string | 否 | 角色 | member/manager/admin，默认member |
| email | string | 否 | 邮箱 | 有效的邮箱格式 |
| phone | string | 否 | 手机号 | 11位数字 |
| idCard | string | 否 | 身份证号 | 18位身份证号 |
| address | string | 否 | 地址 | 最多200个字符 |

- **请求示例**:
```json
{
  "username": "newuser",
  "password": "123456",
  "nickname": "新用户",
  "role": "member",
  "email": "newuser@example.com",
  "phone": "13800138002",
  "idCard": "110101199001011234",
  "address": "北京市朝阳区建国路88号"
}
```

- **注意事项**:
  - username必须唯一，重复会返回错误
  - password应使用HTTPS传输
  - 创建成功后会向邮箱或手机发送通知（如果提供）
  - 默认状态为active（启用）

- **成功响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 100,
    "username": "newuser",
    "nickname": "新用户",
    "role": "member",
    "status": "active",
    "joinDate": "2026-01-05",
    "createdAt": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 2.4 更新用户

- **接口路径**: `PUT /users/{id}`
- **接口描述**: 更新用户信息
- **权限要求**: 
  - 管理员及以上：可以更新所有用户
  - 普通用户：只能更新自己的信息（不包括角色和状态）
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 用户ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| nickname | string | 否 | 昵称 | 最多20个字符 |
| password | string | 否 | 新密码 | 6-20位任意字符，为空则不修改 |
| role | string | 否 | 角色 | member/manager/admin，仅管理员可修改 |
| email | string | 否 | 邮箱 | 有效的邮箱格式 |
| phone | string | 否 | 手机号 | 11位数字 |
| idCard | string | 否 | 身份证号 | 18位身份证号 |
| address | string | 否 | 地址 | 最多200个字符 |

- **请求示例**:
```json
{
  "nickname": "更新后的昵称",
  "email": "updated@example.com",
  "phone": "13800138003",
  "address": "北京市海淀区中关村"
}
```

- **注意事项**:
  - 至少需要提供一个要更新的字段
  - password为空则不修改密码
  - role和status只有管理员可以修改
  - 邮箱和手机号需要验证唯一性

- **成功响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 100,
    "updateTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 2.5 删除用户

- **接口路径**: `DELETE /users/{id}`
- **接口描述**: 删除指定用户（软删除）
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 用户ID |

- **请求示例**:
```
DELETE /users/100
```

- **注意事项**:
  - 使用软删除，数据不会立即从数据库中物理删除
  - 不能删除自己
  - 不能删除超级管理员账户
  - 删除后用户无法登录，但数据可以恢复

- **成功响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": {
    "id": 100,
    "deleteTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 2.6 切换用户状态

- **接口路径**: `PUT /users/{id}/status`
- **接口描述**: 启用/禁用用户
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 用户ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态：active(启用) 或 disabled(禁用) |

- **请求示例**:
```json
{
  "status": "disabled"
}
```

- **注意事项**:
  - 禁用后用户无法登录
  - 不能禁用自己
  - 不能禁用超级管理员账户

- **成功响应**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": {
    "id": 100,
    "status": "disabled",
    "updateTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 2.7 更新个人资料

- **接口路径**: `PUT /profile`
- **接口描述**: 当前登录用户更新自己的个人资料
- **权限要求**: 需要认证
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| nickname | string | 否 | 昵称 | 最多20个字符 |
| email | string | 否 | 邮箱 | 有效的邮箱格式 |
| phone | string | 否 | 手机号 | 11位数字 |
| idCard | string | 否 | 身份证号 | 18位身份证号 |
| address | string | 否 | 地址 | 最多200个字符 |

- **请求示例**:
```json
{
  "nickname": "我的昵称",
  "email": "myemail@example.com",
  "phone": "13800138004"
}
```

- **注意事项**:
  - 至少需要提供一个要更新的字段
  - 邮箱和手机号需要验证唯一性

- **成功响应**:
```json
{
  "code": 200,
  "message": "资料更新成功",
  "data": {
    "updateTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 2.8 修改密码

- **接口路径**: `PUT /profile/password`
- **接口描述**: 当前登录用户修改自己的密码
- **权限要求**: 需要认证
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| currentPassword | string | 是 | 当前密码 | 6-20位任意字符 |
| newPassword | string | 是 | 新密码 | 6-20位任意字符 |
| confirmPassword | string | 是 | 确认新密码 | 必须与新密码相同 |

- **请求示例**:
```json
{
  "currentPassword": "123456",
  "newPassword": "newpass123",
  "confirmPassword": "newpass123"
}
```

- **注意事项**:
  - 当前密码必须正确
  - 新密码不能与当前密码相同
  - 密码修改成功后，所有已登录设备都会被登出（除当前设备）
  - 建议使用强密码，包含字母、数字和特殊字符

- **成功响应**:
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": {
    "updateTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 2.9 上传头像

- **接口路径**: `POST /profile/avatar`
- **接口描述**: 上传当前登录用户的头像
- **权限要求**: 需要认证
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: multipart/form-data`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 头像文件，支持jpg、jpeg、png、gif，最大5MB |

- **注意事项**:
  - 上传成功后旧头像会被删除
  - 图片会自动裁剪为正方形
  - 图片质量会被适当压缩
  - 建议上传尺寸为200x200像素的图片

- **成功响应**:
```json
{
  "code": 200,
  "message": "头像上传成功",
  "data": {
    "url": "http://localhost:3000/uploads/avatar/100.jpg",
    "filename": "100.jpg",
    "size": 102400,
    "type": "image/jpeg",
    "uploadTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 2.10 实名认证

- **接口路径**: `POST /profile/verify`
- **接口描述**: 当前登录用户进行实名认证
- **权限要求**: 需要认证
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| realName | string | 是 | 真实姓名 | 2-10个汉字 |
| idCard | string | 是 | 身份证号 | 18位身份证号 |
| phone | string | 是 | 手机号 | 11位数字，必须与账户绑定的手机号一致 |

- **请求示例**:
```json
{
  "realName": "张三",
  "idCard": "110101199001011234",
  "phone": "13800138001"
}
```

- **注意事项**:
  - 实名认证需要调用第三方认证接口
  - 认证信息不可更改
  - 认证失败会返回具体原因
  - 认证通过后会提升账户权限

- **成功响应**:
```json
{
  "code": 200,
  "message": "实名认证成功",
  "data": {
    "isVerified": true,
    "verifiedAt": "2026-01-05 10:00:00",
    "verifiedName": "张*"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

## 3. 员工管理相关

### 3.1 获取员工列表

- **接口路径**: `GET /employees`
- **接口描述**: 分页获取员工列表,支持多条件搜索和筛选
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | number | 否 | 页码,最小值为1 | 1 |
| pageSize | number | 否 | 每页数量,范围1-100 | 10 |
| keyword | string | 否 | 搜索关键词,可搜索用户名、昵称、手机号、邮箱 | - |
| status | string | 否 | 状态筛选 | - |
| role | string | 否 | 角色筛选 | employee |
| sortBy | string | 否 | 排序字段 | createdAt |
| sortOrder | string | 否 | 排序方向 | desc |

- **status可选值**:
  - `active`: 启用状态
  - `disabled`: 禁用状态

- **role可选值**:
  - `employee`: 普通员工
  - `manager`: 管理者
  - `admin`: 管理员

- **sortBy可选值**:
  - `id`: 按ID排序
  - `username`: 按用户名排序
  - `createdAt`: 按创建时间排序

- **sortOrder可选值**:
  - `asc`: 升序
  - `desc`: 降序

- **请求示例**:
```
GET /employees?page=1&pageSize=10&keyword=test&status=active&sortBy=createdAt&sortOrder=desc
```

- **注意事项**:
  - 支持按用户名、昵称、手机号、邮箱进行模糊搜索
  - 支持按用户状态和角色进行精确筛选
  - 敏感信息(如密码)不会返回
  - 员工信息与用户信息分开存储在两个表中

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 10,
        "username": "employee1",
        "nickname": "张阿姨",
        "role": "employee",
        "email": "zhang@example.com",
        "phone": "13800138001",
        "avatar": "",
        "status": "active",
        "isVerified": true,
        "joinDate": "2024-01-01",
        "workYears": "5",
        "identity": "domestic_worker",
        "isTeamLeader": false,
        "teamSize": "",
        "resume": "5年家政服务经验,擅长日常保洁和烹饪",
        "createdAt": "2024-01-01 00:00:00",
        "updatedAt": "2026-01-05 09:00:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 50,
      "totalPages": 5,
      "hasMore": true
    }
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 3.2 获取员工详情

- **接口路径**: `GET /employees/{id}`
- **接口描述**: 获取指定员工的详细信息
- **权限要求**:
  - 管理员及以上:可以查看所有员工
  - 普通员工:只能查看自己的信息
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 员工ID |

- **请求示例**:
```
GET /employees/1
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "userId": 10,
    "username": "employee1",
    "nickname": "张阿姨",
    "role": "employee",
    "email": "zhang@example.com",
    "phone": "13800138001",
    "avatar": "",
    "status": "active",
    "isVerified": true,
    "idCard": "110***********1234",
    "address": "北京市朝阳区",
    "joinDate": "2024-01-01",
    "workYears": "5",
    "identity": "domestic_worker",
    "isTeamLeader": false,
    "teamSize": "",
    "resume": "5年家政服务经验,擅长日常保洁和烹饪",
    "createdAt": "2024-01-01 00:00:00",
    "updatedAt": "2026-01-05 09:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 3.3 根据用户ID获取员工信息

- **接口路径**: `GET /employees/by-user/{userId}`
- **接口描述**: 根据用户ID获取员工信息
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | number | 是 | 用户ID |

- **请求示例**:
```
GET /employees/by-user/10
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "userId": 10,
    "username": "employee1",
    "nickname": "张阿姨",
    "role": "employee",
    "workYears": "5",
    "identity": "domestic_worker",
    "isTeamLeader": false,
    "teamSize": "",
    "resume": "5年家政服务经验"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 3.4 新增员工

- **接口路径**: `POST /employees`
- **接口描述**: 创建新员工,支持选择现有用户或创建新用户
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| mode | string | 否 | 创建模式 | create(创建新用户)或select(选择现有用户) |
| userId | number | 否 | 现有用户ID | mode=select时必填 |
| username | string | 否 | 用户名,唯一 | mode=create时必填,3-20位字母、数字、下划线 |
| password | string | 否 | 密码 | mode=create时必填,6-20位任意字符 |
| nickname | string | 否 | 昵称 | 最多20个字符 |
| role | string | 否 | 角色 | 默认employee |
| email | string | 否 | 邮箱 | 有效的邮箱格式 |
| phone | string | 否 | 手机号 | 11位数字 |
| idCard | string | 否 | 身份证号 | 18位身份证号 |
| address | string | 否 | 地址 | 最多200个字符 |
| realName | string | 否 | 真实姓名 | 2-10个汉字 |
| workYears | string | 否 | 工龄 | 数字或描述 |
| identity | string | 否 | 身份 | domestic_worker/cleaner/nanny/confinement_nanny/caregiver/hourly_worker/other |
| isTeamLeader | boolean | 否 | 是否团队负责人 | 默认false |
| teamSize | string | 否 | 团队人数 | isTeamLeader=true时填写 |
| resume | string | 否 | 履历 | 最多1000字符 |

- **identity可选值**:
  - `domestic_worker`: 保姆
  - `cleaner`: 清洁工
  - `nanny`: 育儿嫂
  - `confinement_nanny`: 月嫂
  - `caregiver`: 养老护理员
  - `hourly_worker`: 钟点工
  - `other`: 其他

- **请求示例(创建新用户)**:
```json
{
  "mode": "create",
  "username": "newemployee",
  "password": "123456",
  "nickname": "新员工",
  "role": "employee",
  "email": "newemployee@example.com",
  "phone": "13800138002",
  "workYears": "3",
  "identity": "cleaner",
  "isTeamLeader": false,
  "resume": "3年清洁服务经验"
}
```

- **请求示例(选择现有用户)**:
```json
{
  "mode": "select",
  "userId": 15,
  "workYears": "5",
  "identity": "nanny",
  "isTeamLeader": true,
  "teamSize": "3",
  "resume": "5年育儿经验,带领3人团队"
}
```

- **注意事项**:
  - mode=create时会先创建用户,再创建员工信息
  - mode=select时用户已存在,只需创建员工信息
  - mode=select时不需要提供username和password
  - 员工信息存储在employees表,用户信息存储在users表
  - 两个表通过userId关联

- **成功响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 50,
    "userId": 20,
    "username": "newemployee",
    "nickname": "新员工",
    "role": "employee",
    "joinDate": "2026-01-13",
    "createdAt": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 3.5 更新员工信息

- **接口路径**: `PUT /employees/{id}`
- **接口描述**: 更新员工信息
- **权限要求**:
  - 管理员及以上:可以更新所有员工
  - 普通员工:只能更新自己的员工信息(不包括角色和状态)
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 员工ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| nickname | string | 否 | 昵称 | 最多20个字符 |
| email | string | 否 | 邮箱 | 有效的邮箱格式 |
| phone | string | 否 | 手机号 | 11位数字 |
| idCard | string | 否 | 身份证号 | 18位身份证号 |
| address | string | 否 | 地址 | 最多200个字符 |
| workYears | string | 否 | 工龄 | 数字或描述 |
| identity | string | 否 | 身份 | domestic_worker/cleaner/nanny等 |
| isTeamLeader | boolean | 否 | 是否团队负责人 | true/false |
| teamSize | string | 否 | 团队人数 | 数字 |
| resume | string | 否 | 履历 | 最多1000字符 |

- **请求示例**:
```json
{
  "nickname": "更新后的昵称",
  "email": "updated@example.com",
  "phone": "13800138003",
  "workYears": "6",
  "identity": "confinement_nanny",
  "isTeamLeader": true,
  "teamSize": "5",
  "resume": "6年月嫂经验,带领5人专业团队"
}
```

- **注意事项**:
  - 至少需要提供一个要更新的字段
  - role和status只有管理员可以修改
  - 邮箱和手机号需要验证唯一性
  - 员工信息更新不影响用户表的基础信息

- **成功响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 50,
    "updateTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 3.6 删除员工

- **接口路径**: `DELETE /employees/{id}`
- **接口描述**: 删除指定员工(软删除)
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 员工ID |

- **请求示例**:
```
DELETE /employees/50
```

- **注意事项**:
  - 使用软删除,数据不会立即从数据库中物理删除
  - 不能删除自己
  - 删除员工信息不会删除对应的用户账户
  - 用户账户需要单独处理

- **成功响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": {
    "id": 50,
    "deleteTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 3.7 切换员工状态

- **接口路径**: `PUT /employees/{id}/status`
- **接口描述**: 启用/禁用员工
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 员工ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态:active(启用) 或 disabled(禁用) |

- **请求示例**:
```json
{
  "status": "disabled"
}
```

- **注意事项**:
  - 禁用后员工无法登录
  - 不能禁用自己
  - 状态会同步到用户表

- **成功响应**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": {
    "id": 50,
    "status": "disabled",
    "updateTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

## 4. 服务管理相关

### 4.1 获取服务列表

- **接口路径**: `POST /services/query`
- **接口描述**: 分页获取服务列表,支持多种筛选条件
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| current | number | 否 | 页码,最小值为1 | 1 |
| size | number | 否 | 每页数量,范围1-50 | 10 |
| keyword | string | 否 | 搜索关键词 | - |
| categoryId | string | 否 | 分类ID | - |
| status | string | 否 | 状态筛选 | - |

- **请求示例**:
```json
{
  "current": 1,
  "size": 10,
  "keyword": "保洁"
}
```

- **注意事项**:
  - 支持按服务名称、描述进行搜索
  - 支持按分类筛选

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "日常保洁",
        "description": "提供家庭日常清洁服务",
        "categoryId": 1,
        "categoryName": "清洁服务",
        "price": 100,
        "unit": "小时",
        "images": ["http://example.com/image1.jpg"],
        "status": "active",
        "createdAt": "2026-01-01 10:00:00"
      }
    ],
    "pagination": {
      "current": 1,
      "size": 10,
      "total": 50
    }
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 4.2 获取热门服务列表

- **接口路径**: `POST /services/query/hot`
- **接口描述**: 获取热门服务列表
- **权限要求**: 无需认证(公开接口)
- **请求头**:
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| current | number | 否 | 页码 | 1 |
| size | number | 否 | 每页数量 | 4 |

- **请求示例**:
```json
{
  "current": 1,
  "size": 4
}
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "日常保洁",
        "price": 100,
        "unit": "小时",
        "images": ["http://example.com/image1.jpg"],
        "orderCount": 120
      }
    ]
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 4.3 获取服务详情

- **接口路径**: `GET /services/info/{id}`
- **接口描述**: 获取指定服务的详细信息
- **权限要求**: 无需认证(公开接口)
- **请求头**:
  - `Authorization: Bearer {token}` (可选)
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 服务ID |

- **请求示例**:
```
GET /services/info/1
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "name": "日常保洁",
    "description": "提供家庭日常清洁服务,包括客厅、卧室、厨房、卫生间等全方位清洁",
    "categoryId": 1,
    "categoryName": "清洁服务",
    "price": 100,
    "unit": "小时",
    "duration": 2,
    "images": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
    "features": ["专业清洁", "环保材料", "快速高效"],
    "status": "active",
    "createdAt": "2026-01-01 10:00:00",
    "updatedAt": "2026-01-10 15:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 4.4 新增服务

- **接口路径**: `POST /services/add`
- **接口描述**: 创建新服务
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| name | string | 是 | 服务名称 | 1-50字符 |
| description | string | 是 | 服务描述 | 1-500字符 |
| categoryId | number | 是 | 分类ID | 有效分类ID |
| price | number | 是 | 价格 | 大于0 |
| unit | string | 否 | 计价单位 | 如:小时/次/平米 |
| duration | number | 否 | 服务时长(小时) | 大于0 |
| images | array | 否 | 服务图片 | 最多5张 |
| features | array | 否 | 服务特点 | 最多10个 |

- **请求示例**:
```json
{
  "name": "深度保洁",
  "description": "全方位深度清洁服务",
  "categoryId": 1,
  "price": 200,
  "unit": "次",
  "duration": 3,
  "images": ["http://example.com/image.jpg"],
  "features": ["专业清洁", "高温消毒", "快速干燥"]
}
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 10,
    "name": "深度保洁",
    "createdAt": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 4.5 更新服务

- **接口路径**: `PUT /services/update/{id}`
- **接口描述**: 更新服务信息
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 服务ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| name | string | 否 | 服务名称 | 1-50字符 |
| description | string | 否 | 服务描述 | 1-500字符 |
| categoryId | number | 否 | 分类ID | 有效分类ID |
| price | number | 否 | 价格 | 大于0 |
| unit | string | 否 | 计价单位 | 如:小时/次/平米 |
| duration | number | 否 | 服务时长(小时) | 大于0 |
| images | array | 否 | 服务图片 | 最多5张 |
| features | array | 否 | 服务特点 | 最多10个 |
| status | string | 否 | 状态 | active/disabled |

- **请求示例**:
```json
{
  "name": "更新后的服务名称",
  "price": 250,
  "status": "active"
}
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 10,
    "updateTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 4.6 删除服务

- **接口路径**: `DELETE /services/delete/{id}`
- **接口描述**: 删除指定服务(软删除)
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 服务ID |

- **请求示例**:
```
DELETE /services/delete/10
```

- **注意事项**:
  - 使用软删除,数据不会立即从数据库中物理删除
  - 已关联订单的服务不能删除

- **成功响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": {
    "id": 10,
    "deleteTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

## 5. 订单管理相关

### 5.1 获取订单列表

- **接口路径**: `GET /order/query`
- **接口描述**: 分页获取订单列表,支持多种筛选条件
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| current | number | 否 | 页码,最小值为1 | 1 |
| size | number | 否 | 每页数量,范围1-50 | 10 |
| keyword | string | 否 | 搜索关键词 | - |
| status | string | 否 | 订单状态 | - |
| serviceId | number | 否 | 服务ID | - |
| startDate | string | 否 | 开始日期 | - |
| endDate | string | 否 | 结束日期 | - |

- **status可选值**:
  - `pending`: 待确认
  - `confirmed`: 已确认
  - `processing`: 进行中
  - `completed`: 已完成
  - `cancelled`: 已取消

- **请求示例**:
```
GET /order/query?current=1&size=10&status=confirmed
```

- **权限说明**:
  - 普通用户:只能查看自己的订单
  - 员工:可以查看指派给自己的订单
  - 管理员:可以查看所有订单

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "orderNo": "ORDER202601130001",
        "serviceId": 1,
        "serviceName": "日常保洁",
        "customerId": 5,
        "customerName": "王先生",
        "customerPhone": "13800138000",
        "employeeId": 10,
        "employeeName": "张阿姨",
        "address": "北京市朝阳区XX路XX号",
        "serviceTime": "2026-01-15 14:00:00",
        "status": "confirmed",
        "price": 200,
        "createdAt": "2026-01-13 10:00:00"
      }
    ],
    "pagination": {
      "current": 1,
      "size": 10,
      "total": 30
    }
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 5.2 获取订单详情

- **接口路径**: `GET /order/info/{id}`
- **接口描述**: 获取指定订单的详细信息
- **权限要求**:
  - 管理员及以上:可以查看所有订单
  - 普通用户:只能查看自己的订单
  - 员工:可以查看指派给自己的订单
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

- **请求示例**:
```
GET /order/info/1
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "orderNo": "ORDER202601130001",
    "serviceId": 1,
    "serviceName": "日常保洁",
    "serviceDescription": "提供家庭日常清洁服务",
    "customerId": 5,
    "customerName": "王先生",
    "customerPhone": "13800138000",
    "customerAddress": "北京市朝阳区XX路XX号",
    "employeeId": 10,
    "employeeName": "张阿姨",
    "employeePhone": "13800138001",
    "address": "北京市朝阳区XX路XX号",
    "serviceTime": "2026-01-15 14:00:00",
    "serviceDuration": 2,
    "status": "confirmed",
    "price": 200,
    "paymentStatus": "unpaid",
    "remark": "需要带清洁工具",
    "createdAt": "2026-01-13 10:00:00",
    "updatedAt": "2026-01-13 11:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 5.3 新增订单

- **接口路径**: `POST /order/add`
- **接口描述**: 创建新订单
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| serviceId | number | 是 | 服务ID | 有效服务ID |
| address | string | 是 | 服务地址 | 最多200字符 |
| serviceTime | string | 是 | 服务时间 | 格式YYYY-MM-DD HH:mm:ss |
| serviceDuration | number | 否 | 服务时长(小时) | 大于0 |
| remark | string | 否 | 备注 | 最多500字符 |

- **请求示例**:
```json
{
  "serviceId": 1,
  "address": "北京市朝阳区XX路XX号XX小区XX号楼XX室",
  "serviceTime": "2026-01-15 14:00:00",
  "serviceDuration": 2,
  "remark": "需要带清洁工具,重点清洁厨房和卫生间"
}
```

- **注意事项**:
  - 服务时间不能早于当前时间
  - 订单号自动生成,格式为ORDER+年月日+序号
  - 订单初始状态为pending(待确认)
  - 创建成功后会发送通知给相关员工和管理员

- **成功响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 100,
    "orderNo": "ORDER202601130001",
    "status": "pending",
    "createdAt": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 5.4 更新订单

- **接口路径**: `PUT /order/update/{id}`
- **接口描述**: 更新订单信息
- **权限要求**:
  - 管理员及以上:可以更新所有字段
  - 普通用户:只能更新自己的订单(服务时间和备注)
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| address | string | 否 | 服务地址 | 最多200字符 |
| serviceTime | string | 否 | 服务时间 | 格式YYYY-MM-DD HH:mm:ss |
| serviceDuration | number | 否 | 服务时长(小时) | 大于0 |
| employeeId | number | 否 | 指派员工ID | 仅管理员 |
| status | string | 否 | 订单状态 | pending/confirmed/processing/completed/cancelled |
| remark | string | 否 | 备注 | 最多500字符 |

- **请求示例**:
```json
{
  "serviceTime": "2026-01-16 10:00:00",
  "employeeId": 15,
  "status": "confirmed",
  "remark": "更新后的备注"
}
```

- **注意事项**:
  - 至少需要提供一个要更新的字段
  - employeeId必须是有员工信息的用户ID
  - 状态更新会记录操作日志
  - 更新指派员工后会向新员工发送通知

- **成功响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 100,
    "updateTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 5.5 删除订单

- **接口路径**: `DELETE /order/delete/{id}`
- **接口描述**: 删除指定订单(软删除)
- **权限要求**:
  - 超级管理员、管理员、订单创建者:可以删除订单
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 订单ID |

- **请求示例**:
```
DELETE /order/delete/100
```

- **注意事项**:
  - 使用软删除,数据不会立即从数据库中物理删除
  - 已完成或进行中的订单不能删除
  - 删除后会发送通知给相关人员

- **成功响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": {
    "id": 100,
    "deleteTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

## 6. 会员管理相关

### 6.1 获取会员列表

- **接口路径**: `GET /member/query`
- **接口描述**: 分页获取会员列表,支持多种筛选条件
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| current | number | 否 | 页码,最小值为1 | 1 |
| size | number | 否 | 每页数量,范围1-50 | 10 |
| keyword | string | 否 | 搜索关键词 | - |
| level | string | 否 | 会员等级 | - |
| status | string | 否 | 会员状态 | - |

- **level可选值**:
  - `normal`: 普通会员
  - `silver`: 银卡会员
  - `gold`: 金卡会员
  - `platinum`: 白金会员

- **status可选值**:
  - `active`: 正常
  - `frozen`: 冻结
  - `expired`: 过期

- **请求示例**:
```
GET /member/query?current=1&size=10&level=gold&status=active
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 20,
        "username": "member1",
        "nickname": "王先生",
        "phone": "13800138000",
        "level": "gold",
        "points": 5000,
        "balance": 1000,
        "totalOrders": 20,
        "totalAmount": 5000,
        "status": "active",
        "expireDate": "2027-01-13",
        "createdAt": "2025-01-01 10:00:00"
      }
    ],
    "pagination": {
      "current": 1,
      "size": 10,
      "total": 100
    }
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 6.2 获取会员详情

- **接口路径**: `GET /member/info/{id}`
- **接口描述**: 获取指定会员的详细信息
- **权限要求**: 
  - 管理员及以上:可以查看所有会员
  - 普通会员:只能查看自己的信息
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 会员ID |

- **请求示例**:
```
GET /member/info/1
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "userId": 20,
    "username": "member1",
    "nickname": "王先生",
    "phone": "13800138000",
    "email": "wang@example.com",
    "avatar": "",
    "level": "gold",
    "points": 5000,
    "balance": 1000,
    "totalOrders": 20,
    "totalAmount": 5000,
    "discount": 0.9,
    "status": "active",
    "expireDate": "2027-01-13",
    "joinDate": "2025-01-01",
    "lastOrderTime": "2026-01-10 15:00:00",
    "createdAt": "2025-01-01 10:00:00",
    "updatedAt": "2026-01-13 09:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 6.3 新增会员

- **接口路径**: `POST /member/add`
- **接口描述**: 创建新会员
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| userId | number | 是 | 用户ID | 必须是有效用户 |
| level | string | 否 | 会员等级 | normal/silver/gold/platinum,默认normal |
| points | number | 否 | 初始积分 | 默认0 |
| balance | number | 否 | 初始余额 | 默认0 |

- **请求示例**:
```json
{
  "userId": 25,
  "level": "silver",
  "points": 100,
  "balance": 0
}
```

- **注意事项**:
  - userId对应的用户必须存在
  - 一个用户只能有一个会员记录
  - 默认会员等级为normal
  - 创建成功后会向用户发送通知

- **成功响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 50,
    "userId": 25,
    "level": "silver",
    "createdAt": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 6.4 更新会员

- **接口路径**: `PUT /member/update/{id}`
- **接口描述**: 更新会员信息
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 会员ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| level | string | 否 | 会员等级 | normal/silver/gold/platinum |
| points | number | 否 | 积分 | 大于等于0 |
| balance | number | 否 | 余额 | 大于等于0 |
| status | string | 否 | 会员状态 | active/frozen/expired |
| expireDate | string | 否 | 到期日期 | 格式YYYY-MM-DD |

- **请求示例**:
```json
{
  "level": "gold",
  "points": 1000,
  "status": "active",
  "expireDate": "2027-12-31"
}
```

- **注意事项**:
  - 至少需要提供一个要更新的字段
  - 提升会员等级会自动调整折扣率
  - 冻结会员后无法进行订单操作

- **成功响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 50,
    "updateTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 6.5 删除会员

- **接口路径**: `DELETE /member/delete/{id}`
- **接口描述**: 删除指定会员(软删除)
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 会员ID |

- **请求示例**:
```
DELETE /member/delete/50
```

- **注意事项**:
  - 使用软删除,数据不会立即从数据库中物理删除
  - 删除会员不影响对应的用户账户
  - 删除后会员的订单记录保留

- **成功响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": {
    "id": 50,
    "deleteTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

## 7. 评价管理相关

### 7.1 获取评价列表

- **接口路径**: `GET /evaluate/query`
- **接口描述**: 分页获取评价列表,支持多种筛选条件
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| current | number | 否 | 页码,最小值为1 | 1 |
| size | number | 否 | 每页数量,范围1-50 | 10 |
| orderId | number | 否 | 订单ID | - |
| rating | number | 否 | 评分 | 1-5 |
| keyword | string | 否 | 搜索关键词 | - |

- **请求示例**:
```
GET /evaluate/query?current=1&size=10&rating=5
```

- **权限说明**:
  - 普通用户:只能查看自己的评价
  - 员工:可以查看对自己的评价
  - 管理员:可以查看所有评价

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "orderId": 100,
        "orderNo": "ORDER202601130001",
        "customerId": 5,
        "customerName": "王先生",
        "customerAvatar": "",
        "employeeId": 10,
        "employeeName": "张阿姨",
        "employeeAvatar": "",
        "rating": 5,
        "content": "服务非常好,非常满意!",
        "images": ["http://example.com/image1.jpg"],
        "reply": "感谢您的评价,我们会继续努力!",
        "createdAt": "2026-01-13 10:00:00"
      }
    ],
    "pagination": {
      "current": 1,
      "size": 10,
      "total": 50
    }
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 7.2 获取评价详情

- **接口路径**: `GET /evaluate/info/{id}`
- **接口描述**: 获取指定评价的详细信息
- **权限要求**: 
  - 管理员及以上:可以查看所有评价
  - 普通用户:只能查看自己的评价
  - 员工:可以查看对自己的评价
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

- **请求示例**:
```
GET /evaluate/info/1
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "orderId": 100,
    "orderNo": "ORDER202601130001",
    "serviceName": "日常保洁",
    "customerId": 5,
    "customerName": "王先生",
    "customerAvatar": "",
    "employeeId": 10,
    "employeeName": "张阿姨",
    "employeeAvatar": "",
    "rating": 5,
    "content": "服务非常好,非常满意!张阿姨非常专业,做事细致,打扫得很干净,而且态度也很好,会继续选择!",
    "images": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
    "tags": ["专业", "细致", "态度好"],
    "reply": "感谢您的评价,我们会继续努力提供更好的服务!",
    "replyTime": "2026-01-13 12:00:00",
    "createdAt": "2026-01-13 10:00:00",
    "updatedAt": "2026-01-13 12:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 7.3 新增评价

- **接口路径**: `POST /evaluate/add`
- **接口描述**: 创建新评价
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| orderId | number | 是 | 订单ID | 有效订单ID,且订单已完成 |
| rating | number | 是 | 评分 | 1-5星 |
| content | string | 否 | 评价内容 | 最多500字符 |
| images | array | 否 | 评价图片 | 最多5张 |
| tags | array | 否 | 评价标签 | 最多10个 |

- **请求示例**:
```json
{
  "orderId": 100,
  "rating": 5,
  "content": "服务非常好,非常满意!",
  "images": ["http://example.com/image1.jpg"],
  "tags": ["专业", "细致", "态度好"]
}
```

- **注意事项**:
  - 订单必须已完成才能评价
  - 每个订单只能评价一次
  - 评分必须在1-5之间
  - 评价成功后会为员工增加积分
  - 评价成功后会为用户增加积分

- **成功响应**:
```json
{
  "code": 200,
  "message": "评价成功",
  "data": {
    "id": 50,
    "orderId": 100,
    "createdAt": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 7.4 更新评价

- **接口路径**: `PUT /evaluate/update/{id}`
- **接口描述**: 更新评价信息
- **权限要求**:
  - 管理员及以上:可以更新所有字段
  - 普通用户:只能更新自己的评价内容和标签
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| rating | number | 否 | 评分 | 1-5星 |
| content | string | 否 | 评价内容 | 最多500字符 |
| images | array | 否 | 评价图片 | 最多5张 |
| tags | array | 否 | 评价标签 | 最多10个 |
| reply | string | 否 | 回复内容 | 最多500字符,仅管理员 |

- **请求示例**:
```json
{
  "rating": 5,
  "content": "更新后的评价内容",
  "reply": "感谢您的反馈!"
}
```

- **注意事项**:
  - 至少需要提供一个要更新的字段
  - 普通用户不能修改评分
  - 管理员可以添加回复
  - 回复后评价将标记为已处理

- **成功响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 50,
    "updateTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

### 7.5 删除评价

- **接口路径**: `DELETE /evaluate/delete/{id}`
- **接口描述**: 删除指定评价(软删除)
- **权限要求**:
  - 超级管理员、管理员、评价创建者:可以删除评价
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 评价ID |

- **请求示例**:
```
DELETE /evaluate/delete/50
```

- **注意事项**:
  - 使用软删除,数据不会立即从数据库中物理删除
  - 删除后相关的积分会扣除
  - 删除后订单可以重新评价

- **成功响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": {
    "id": 50,
    "deleteTime": "2026-01-13 10:00:00"
  },
  "timestamp": "2026-01-13T10:00:00Z"
}
```

---

## 8. 任务管理相关

### 3.1 获取任务列表

- **接口路径**: `GET /tasks`
- **接口描述**: 分页获取任务列表，支持多种筛选和搜索条件
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| page | number | 否 | 页码，最小值为1 | 1 |
| pageSize | number | 否 | 每页数量，范围1-50 | 10 |
| keyword | string | 否 | 搜索关键词，可搜索标题、描述 | - |
| type | string | 否 | 任务类型筛选 | - |
| status | string | 否 | 状态筛选 | - |
| priority | string | 否 | 优先级筛选 | - |
| assigneeId | number | 否 | 指派人ID筛选 | - |
| startDate | string | 否 | 开始日期，格式YYYY-MM-DD | - |
| endDate | string | 否 | 结束日期，格式YYYY-MM-DD | - |
| sortBy | string | 否 | 排序字段 | createdAt |
| sortOrder | string | 否 | 排序方向 | desc |

- **type可选值**:
  - `daily_task`: 日常任务
  - `special_task`: 特殊任务
  - `maintenance`: 维护任务

- **status可选值**:
  - `pending`: 待处理
  - `processing`: 进行中
  - `completed`: 已完成
  - `cancelled`: 已取消

- **priority可选值**:
  - `low`: 低优先级
  - `medium`: 中优先级（默认）
  - `high`: 高优先级
  - `urgent`: 紧急

- **sortBy可选值**:
  - `id`: 按ID排序
  - `title`: 按标题排序
  - `priority`: 按优先级排序
  - `status`: 按状态排序
  - `dueDate`: 按截止日期排序
  - `createdAt`: 按创建时间排序

- **请求示例**:
```
GET /tasks?page=1&pageSize=10&status=pending&priority=high&sortBy=dueDate&sortOrder=asc
```

- **权限说明**:
  - 普通用户：只能查看自己创建或被指派的任务
  - 管理员及以上：可以查看所有任务

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "系统维护任务",
        "description": "对系统进行例行维护",
        "type": "maintenance",
        "status": "pending",
        "priority": "high",
        "assignee": {
          "id": 1,
          "username": "admin",
          "nickname": "超级管理员",
          "avatar": ""
        },
        "creator": {
          "id": 1,
          "username": "admin",
          "nickname": "超级管理员"
        },
        "category": "系统维护",
        "tags": ["重要", "定期"],
        "dueDate": "2026-01-10",
        "createdAt": "2026-01-05 10:00:00",
        "updatedAt": "2026-01-05 10:00:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 200,
      "totalPages": 20,
      "hasMore": true
    }
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 3.2 获取任务详情

- **接口路径**: `GET /tasks/{id}`
- **接口描述**: 获取指定任务的详细信息
- **权限要求**: 
  - 管理员及以上：可以查看所有任务
  - 普通用户：只能查看自己创建或被指派的任务
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 任务ID |

- **请求示例**:
```
GET /tasks/1
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "title": "系统维护任务",
    "description": "对系统进行例行维护，包括数据库备份、日志清理等",
    "type": "maintenance",
    "status": "pending",
    "priority": "high",
    "category": "系统维护",
    "tags": ["重要", "定期"],
    "assignee": {
      "id": 1,
      "username": "admin",
      "nickname": "超级管理员",
      "avatar": ""
    },
    "creator": {
      "id": 1,
      "username": "admin",
      "nickname": "超级管理员"
    },
    "dueDate": "2026-01-10",
    "startDate": "2026-01-06",
    "completedAt": null,
    "createdAt": "2026-01-05 10:00:00",
    "updatedAt": "2026-01-05 10:00:00",
    "comments": [],
    "attachments": []
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 3.3 创建任务

- **接口路径**: `POST /tasks`
- **接口描述**: 创建新的任务，支持设置多种属性和指派
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| title | string | 是 | 任务标题 | 1-100字符 |
| description | string | 是 | 任务描述 | 1-1000字符 |
| type | string | 否 | 任务类型 | daily_task/special_task/maintenance，默认daily_task |
| priority | string | 否 | 优先级 | low/medium/high/urgent，默认medium |
| assigneeId | number | 否 | 指派人ID | 必须是系统有效用户ID |
| dueDate | string | 否 | 截止日期 | 格式YYYY-MM-DD或YYYY-MM-DD HH:mm:ss |
| category | string | 否 | 任务分类 | 最多20个字符 |
| tags | array | 否 | 标签数组 | 最多5个标签，每个标签最大20字符 |

- **请求示例**:
```json
{
  "title": "更新项目文档",
  "description": "根据最新要求更新项目相关文档，包括接口文档、用户手册等",
  "type": "daily_task",
  "priority": "high",
  "assigneeId": 2,
  "dueDate": "2026-01-10",
  "category": "文档管理",
  "tags": ["重要", "项目"]
}
```

- **注意事项**:
  - 标题和描述不能为空
  - 截止日期不能早于当前时间
  - 指派人ID必须是有效的用户ID
  - 创建成功后会向指派人发送通知
  - 任务状态默认为pending（待处理）
  - 创建者自动成为任务的所有者

- **成功响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 101,
    "title": "更新项目文档",
    "status": "pending",
    "createdAt": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 3.4 更新任务

- **接口路径**: `PUT /tasks/{id}`
- **接口描述**: 更新任务信息
- **权限要求**: 
  - 超级管理员、管理员、任务创建者：可以更新所有字段
  - 普通用户：只能更新自己创建的任务
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 任务ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 验证规则 |
|--------|------|------|------|----------|
| title | string | 否 | 任务标题 | 1-100字符 |
| description | string | 否 | 任务描述 | 1-1000字符 |
| type | string | 否 | 任务类型 | daily_task/special_task/maintenance |
| priority | string | 否 | 优先级 | low/medium/high/urgent |
| assigneeId | number | 否 | 指派人ID | 必须是系统有效用户ID |
| status | string | 否 | 任务状态 | pending/processing/completed/cancelled |
| dueDate | string | 否 | 截止日期 | 格式YYYY-MM-DD或YYYY-MM-DD HH:mm:ss |
| category | string | 否 | 任务分类 | 最多20个字符 |
| tags | array | 否 | 标签数组 | 最多5个标签，每个标签最大20字符 |

- **请求示例**:
```json
{
  "title": "更新后的任务标题",
  "description": "更新后的任务描述",
  "priority": "urgent",
  "status": "processing",
  "assigneeId": 3
}
```

- **注意事项**:
  - 至少需要提供一个要更新的字段
  - 指派人ID必须是有效的用户ID
  - 更新指派人后会向新指派人发送通知
  - 状态更新会记录操作日志

- **成功响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 101,
    "updateTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 3.5 删除任务

- **接口路径**: `DELETE /tasks/{id}`
- **接口描述**: 删除指定任务（软删除）
- **权限要求**: 
  - 超级管理员、管理员、任务创建者：可以删除任务
- **请求头**:
  - `Authorization: Bearer {token}`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 任务ID |

- **请求示例**:
```
DELETE /tasks/101
```

- **注意事项**:
  - 使用软删除，数据不会立即从数据库中物理删除
  - 已完成的任务不建议删除
  - 删除后会发送通知给相关人员

- **成功响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": {
    "id": 101,
    "deleteTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 3.6 更新任务状态

- **接口路径**: `PUT /tasks/{id}/status`
- **接口描述**: 更新任务状态
- **权限要求**: 
  - 任务创建者、指派人、管理员及以上：可以更新状态
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | number | 是 | 任务ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 任务状态 |

- **status可选值**:
  - `pending`: 待处理
  - `processing`: 进行中
  - `completed`: 已完成
  - `cancelled`: 已取消

- **请求示例**:
```json
{
  "status": "processing"
}
```

- **注意事项**:
  - 状态更新会记录操作日志
  - 状态为completed的任务不能修改为其他状态
  - 状态为cancelled的任务不能修改为其他状态

- **成功响应**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": {
    "id": 101,
    "status": "processing",
    "updateTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 3.7 获取任务统计

- **接口路径**: `GET /tasks/stats`
- **接口描述**: 获取任务相关的统计数据和趋势信息
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| period | string | 否 | 统计周期 | week |
| startDate | string | 否 | 开始日期，格式YYYY-MM-DD | - |
| endDate | string | 否 | 结束日期，格式YYYY-MM-DD | - |

- **period可选值**:
  - `week`: 最近7天
  - `month`: 最近30天
  - `quarter`: 最近90天
  - `year`: 最近365天

- **请求示例**:
```
GET /tasks/stats?period=week
```

- **权限说明**:
  - 普通用户：只能看到自己相关任务的统计
  - 管理员：可以看到所有任务的统计

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 3456,
    "pending": 566,
    "processing": 2890,
    "completed": 2890,
    "cancelled": 0,
    "completionRate": 83.6,
    "trend": {
      "weekly": [
        {
          "date": "2025-12-30",
          "count": 45,
          "completed": 38
        },
        {
          "date": "2025-12-31",
          "count": 52,
          "completed": 45
        },
        {
          "date": "2026-01-01",
          "count": 35,
          "completed": 30
        },
        {
          "date": "2026-01-02",
          "count": 48,
          "completed": 42
        },
        {
          "date": "2026-01-03",
          "count": 60,
          "completed": 55
        },
        {
          "date": "2026-01-04",
          "count": 40,
          "completed": 35
        },
        {
          "date": "2026-01-05",
          "count": 55,
          "completed": 48
        }
      ],
      "typeDistribution": [
        {
          "type": "daily_task",
          "label": "日常任务",
          "count": 1234,
          "percentage": 35.7
        },
        {
          "type": "special_task",
          "label": "特殊任务",
          "count": 890,
          "percentage": 25.8
        },
        {
          "type": "maintenance",
          "label": "维护任务",
          "count": 1332,
          "percentage": 38.5
        }
      ],
      "priorityDistribution": [
        {
          "priority": "urgent",
          "label": "紧急",
          "count": 120,
          "percentage": 3.5
        },
        {
          "priority": "high",
          "label": "高",
          "count": 566,
          "percentage": 16.4
        },
        {
          "priority": "medium",
          "label": "中",
          "count": 2034,
          "percentage": 58.9
        },
        {
          "priority": "low",
          "label": "低",
          "count": 736,
          "percentage": 21.2
        }
      ]
    },
    "stats": {
      "avgCompletionTime": 2.5,
      "overdueCount": 45,
      "overdueRate": 1.3,
      "myTasks": 125,
      "myCompleted": 98
    }
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

## 4. 文件上传相关

### 4.1 上传文件

- **接口路径**: `POST /upload`
- **接口描述**: 上传单个文件到服务器，支持多种文件类型
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: multipart/form-data`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 文件 |
| type | string | 否 | 文件类型：image/document/archive/other |
| category | string | 否 | 文件分类：avatar/document/task/system |

- **文件大小限制**:
  - 图片文件：jpg, jpeg, png, gif, webp (最大5MB)
  - 文档文件：pdf, doc, docx, xls, xlsx, txt (最大10MB)
  - 压缩包：zip, rar, 7z (最大50MB)
  - 其他文件：最大20MB

- **注意事项**:
  - 文件会自动进行安全扫描
  - 图片文件会自动生成缩略图
  - 文件名会自动重命名避免重复
  - 上传成功后返回可访问的URL
  - 支持断点续传（大文件）

- **成功响应**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://localhost:3000/uploads/2026/01/05/abc123.jpg",
    "filename": "abc123.jpg",
    "originalName": "image.jpg",
    "size": 1024000,
    "type": "image/jpeg",
    "thumbnailUrl": "http://localhost:3000/uploads/2026/01/05/thumb_abc123.jpg",
    "uploadTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 4.2 批量上传文件

- **接口路径**: `POST /upload/batch`
- **接口描述**: 一次性上传多个文件
- **权限要求**: 所有登录用户
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: multipart/form-data`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| files[] | File[] | 是 | 文件数组 |
| category | string | 否 | 文件分类 |

- **注意事项**:
  - 最多支持同时上传20个文件
  - 总大小限制为100MB
  - 失败的文件不会影响其他文件的上传
  - 支持并行上传处理

- **成功响应**:
```json
{
  "code": 200,
  "message": "上传完成",
  "data": {
    "success": 2,
    "failed": 0,
    "total": 2,
    "results": [
      {
        "filename": "image1.jpg",
        "success": true,
        "url": "http://localhost:3000/uploads/image1.jpg",
        "size": 1024000
      },
      {
        "filename": "image2.jpg",
        "success": true,
        "url": "http://localhost:3000/uploads/image2.jpg",
        "size": 2048000
      }
    ]
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

## 5. 系统设置相关

### 5.1 获取系统配置

- **接口路径**: `GET /settings`
- **接口描述**: 获取系统运行配置和参数设置
- **权限要求**: 所有登录用户（部分敏感信息仅管理员可见）
- **请求头**:
  - `Authorization: Bearer {token}`

- **成功响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "siteName": "Housekeeping管理系统",
    "version": "1.0.0",
    "maintenance": false,
    "registrationEnabled": true,
    "maxFileSize": 10485760,
    "allowedFileTypes": ["jpg", "png", "gif", "pdf", "doc", "docx"],
    "features": {
      "smsLogin": true,
      "wechatLogin": true,
      "emailNotification": true
    }
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

### 5.2 更新系统配置

- **接口路径**: `PUT /settings`
- **接口描述**: 更新系统配置
- **权限要求**: 管理员及以上权限
- **请求头**:
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| siteName | string | 否 | 站点名称 |
| maintenance | boolean | 否 | 维护模式 |
| registrationEnabled | boolean | 否 | 是否允许注册 |
| maxFileSize | number | 否 | 最大文件大小（字节） |
| allowedFileTypes | array | 否 | 允许的文件类型列表 |

- **请求示例**:
```json
{
  "siteName": "新的系统名称",
  "maintenance": false,
  "registrationEnabled": true
}
```

- **成功响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "updateTime": "2026-01-05 10:00:00"
  },
  "timestamp": "2026-01-05T10:00:00Z"
}
```

---

## 接口权限说明

### 角色定义

| 角色 | 英文标识 | 说明 |
|------|----------|------|
| 超级管理员 | admin | 拥有所有权限 |
| 管理员 | manager | 拥有除系统设置外的所有权限 |
| 普通会员 | member | 基础用户权限 |
| 游客 | guest | 未登录用户 |

### 权限矩阵

| 功能模块 | 游客 | 普通会员 | 管理员 | 超级管理员 |
|----------|------|----------|--------|------------|
| 用户登录 | ✅ | ✅ | ✅ | ✅ |
| 用户注册 | ✅ | ❌ | ❌ | ❌ |
| 查看个人资料 | ❌ | ✅(自己) | ✅ | ✅ |
| 编辑个人资料 | ❌ | ✅(自己) | ✅ | ✅ |
| 查看用户列表 | ❌ | ❌ | ✅ | ✅ |
| 创建用户 | ❌ | ❌ | ✅ | ✅ |
| 编辑用户 | ❌ | ❌ | ✅ | ✅ |
| 删除用户 | ❌ | ❌ | ✅ | ✅ |
| 禁用用户 | ❌ | ❌ | ✅ | ✅ |
| 查看任务列表 | ❌ | ✅(自己的) | ✅ | ✅ |
| 创建任务 | ❌ | ✅ | ✅ | ✅ |
| 编辑任务 | ❌ | ✅(自己的) | ✅ | ✅ |
| 删除任务 | ❌ | ✅(自己的) | ✅ | ✅ |
| 查看任务统计 | ❌ | ✅(自己的) | ✅ | ✅ |
| 上传文件 | ❌ | ✅ | ✅ | ✅ |
| 查看系统设置 | ❌ | ✅(部分) | ✅ | ✅ |
| 修改系统设置 | ❌ | ❌ | ❌ | ✅ |

**说明**:
- ✅ 完全权限
- ✅(自己) 只能操作自己的数据
- ✅(部分) 只能看到部分公开配置
- ❌ 无权限

---

## 数据验证规则

### 用户相关

| 字段 | 验证规则 | 错误提示 |
|------|----------|----------|
| username | 3-20位字母、数字、下划线，唯一 | 用户名长度必须在3-20位之间，且只允许字母、数字、下划线 |
| password | 6-20位任意字符 | 密码长度必须在6-20位之间 |
| nickname | 1-20个字符 | 昵称长度必须在1-20个字符之间 |
| email | 有效的邮箱格式 | 请输入正确的邮箱格式 |
| phone | 11位数字，以1开头 | 请输入正确的手机号 |
| idCard | 18位身份证号，符合格式 | 请输入正确的身份证号码 |
| realName | 2-10个汉字 | 姓名长度必须在2-10个汉字之间 |
| address | 最多200个字符 | 地址不能超过200个字符 |

### 任务相关

| 字段 | 验证规则 | 错误提示 |
|------|----------|----------|
| title | 1-100字符 | 任务标题长度必须在1-100个字符之间 |
| description | 1-1000字符 | 任务描述长度必须在1-1000个字符之间 |
| type | daily_task/special_task/maintenance | 任务类型不正确 |
| status | pending/processing/completed/cancelled | 任务状态不正确 |
| priority | low/medium/high/urgent | 优先级不正确 |
| dueDate | 日期格式YYYY-MM-DD，不能早于当前时间 | 截止日期格式不正确或已过期 |
| category | 最多20个字符 | 任务分类不能超过20个字符 |
| tags | 最多5个标签，每个标签最多20字符 | 标签数量或长度超过限制 |

### 文件上传

| 类型 | 允许格式 | 最大大小 | 错误提示 |
|------|----------|----------|----------|
| 图片 | jpg, jpeg, png, gif, webp | 5MB | 图片格式不支持或文件过大 |
| 文档 | pdf, doc, docx, xls, xlsx, txt | 10MB | 文档格式不支持或文件过大 |
| 压缩包 | zip, rar, 7z | 50MB | 压缩包格式不支持或文件过大 |
| 其他 | 任意 | 20MB | 文件大小超过限制 |

---

## 安全说明

### 1. Token认证
- 所有接口（除登录外）都需要在请求头中携带Token
- Token有效期24小时，过期需重新登录或刷新
- Token应妥善保存，避免泄露

### 2. 权限验证
- 每个接口都会验证用户权限
- 权限不足时会返回403错误
- 敏感操作会记录操作日志

### 3. 参数验证
- 所有参数都会进行格式和业务验证
- 验证失败会返回400错误
- 敏感字段会进行脱敏处理

### 4. 频率限制
- 防止恶意请求，限制每分钟最大请求数
- 超过限制会返回429错误
- 登录失败5次会锁定账户30分钟

### 5. 数据安全
- 敏感数据在日志中进行脱敏处理
- 密码等敏感信息使用加密存储
- 支持HTTPS加密传输

### 6. SQL注入防护
- 使用参数化查询防止SQL注入
- 所有用户输入都进行转义处理
- 定期进行安全审计

### 7. XSS防护
- 输出数据进行HTML转义处理
- Content-Type设置为application/json
- 禁止使用innerHTML

### 8. CSRF防护
- 使用CSRF Token验证
- 检查Referer和Origin头
- 重要操作需要二次确认

---

## 开发注意事项

### 1. 环境配置

**开发环境**: `http://localhost:3000/api`
**测试环境**: `https://test-api.example.com/api`
**生产环境**: `https://api.example.com/api`

### 2. 错误处理

所有接口错误都应该在前端统一处理，建议的错误处理流程：
1. 检查HTTP状态码
2. 检查业务错误码
3. 显示友好的错误提示
4. 记录错误日志（生产环境）

### 3. Token管理

建议在前端实现以下功能：
- Token自动刷新
- Token过期自动重新登录
- 多Tab页Token同步
- 退出登录清除Token

### 4. 请求拦截

建议在前端实现请求拦截器：
- 统一添加Token
- 统一处理错误
- 统一显示加载状态
- 统一记录请求日志

### 5. 数据缓存

对于不常变化的数据可以缓存：
- 用户权限信息
- 系统配置信息
- 用户基础信息

缓存时间建议：5-30分钟

---

## 附录

### A. HTTP状态速查

| 状态码 | 说明 |
|--------|------|
| 2xx | 成功 |
| 3xx | 重定向 |
| 4xx | 客户端错误 |
| 5xx | 服务端错误 |

### B. 常见错误码速查

| 错误码 | 说明 |
|--------|------|
| 1xxxx | 认证相关错误 |
| 2xxxx | 任务相关错误 |
| 3xxxx | 文件相关错误 |
| 4xxxx | 权限相关错误 |
| 5xxxx | 系统相关错误 |

### C. 联系方式

如有疑问或问题，请联系：
- 技术支持: tech@example.com
- Bug反馈: bug@example.com
- 功能建议: feedback@example.com

---

**文档版本**: 1.2.0
**最后更新**: 2026-01-13
**维护者**: 开发团队
