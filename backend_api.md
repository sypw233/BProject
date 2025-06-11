---
title: Demo
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"

---

# Demo

Base URLs:

# Authentication

# 用户认证

## POST 用户登录

POST /auth/login

> Body 请求参数

```json
{
  "username": "string",
  "password": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[UserLoginDTO](#schemauserlogindto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## POST 用户注册

POST /auth/register

> Body 请求参数

```json
{
  "username": "string",
  "password": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[UserRegisterDTO](#schemauserregisterdto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## GET 获取当前登录用户信息

GET /auth/me

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

# 文件上传

## POST 上传文件

POST /files/upload

> Body 请求参数

```yaml
file: string

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|object| 否 |none|
|» file|body|string(binary)| 是 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

# 班级管理

## POST 创建班级

POST /classes

> Body 请求参数

```json
{
  "name": "string",
  "grade": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[ClassCreateDTO](#schemaclasscreatedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## PUT 更新班级

PUT /classes

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "grade": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[ClassUpdateDTO](#schemaclassupdatedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## DELETE 删除班级

DELETE /classes/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## GET 获取班级详情

GET /classes/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## GET 获取班级分页列表

GET /classes/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|current|query|integer| 是 |none|
|size|query|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

# 学生管理

## POST 创建学生

POST /students

> Body 请求参数

```json
{
  "name": "string",
  "gender": 0,
  "birthDate": "string",
  "classId": 0,
  "joinDate": "string",
  "status": 1
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[StudentCreateDTO](#schemastudentcreatedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## PUT 更新学生

PUT /students

> Body 请求参数

```json
{
  "id": 0,
  "name": "string",
  "gender": 0,
  "birthDate": "string",
  "classId": 0,
  "joinDate": "string",
  "status": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[StudentUpdateDTO](#schemastudentupdatedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## DELETE 删除学生

DELETE /students/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## GET 获取学生详情

GET /students/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## GET 获取学生分页列表

GET /students/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|current|query|integer| 是 |none|
|size|query|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

# 员工管理

## POST 创建员工

POST /employees

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "realName": "string",
  "gender": 0,
  "avatar": "string",
  "job": 0,
  "departmentId": 0,
  "entryDate": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[EmployeeCreateDTO](#schemaemployeecreatedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## PUT 更新员工

PUT /employees

> Body 请求参数

```json
{
  "id": 0,
  "username": "string",
  "password": "string",
  "realName": "string",
  "gender": 0,
  "avatar": "string",
  "job": 0,
  "departmentId": 0,
  "entryDate": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[EmployeeUpdateDTO](#schemaemployeeupdatedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## DELETE 删除员工

DELETE /employees/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## GET 获取员工详情

GET /employees/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## GET 获取员工分页列表

GET /employees/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|current|query|integer| 是 |none|
|size|query|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

# 部门管理

## POST 创建部门

POST /departments

> Body 请求参数

```json
{
  "name": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[DepartmentCreateDTO](#schemadepartmentcreatedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## PUT 更新部门

PUT /departments

> Body 请求参数

```json
{
  "id": 0,
  "name": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[DepartmentUpdateDTO](#schemadepartmentupdatedto)| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## DELETE 删除部门

DELETE /departments/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## GET 获取部门详情

GET /departments/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

## GET 获取部门分页列表

GET /departments/page

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|current|query|integer| 是 |none|
|size|query|integer| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[SaResult](#schemasaresult)|

# 数据模型

<h2 id="tocS_SaResult">SaResult</h2>

<a id="schemasaresult"></a>
<a id="schema_SaResult"></a>
<a id="tocSsaresult"></a>
<a id="tocssaresult"></a>

```json
{
  "key": {}
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|key|object|false|none||none|

<h2 id="tocS_LoginRequest">LoginRequest</h2>

<a id="schemaloginrequest"></a>
<a id="schema_LoginRequest"></a>
<a id="tocSloginrequest"></a>
<a id="tocsloginrequest"></a>

```json
{
  "username": "string",
  "password": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|password|string|true|none||none|

<h2 id="tocS_ClassCreateDTO">ClassCreateDTO</h2>

<a id="schemaclasscreatedto"></a>
<a id="schema_ClassCreateDTO"></a>
<a id="tocSclasscreatedto"></a>
<a id="tocsclasscreatedto"></a>

```json
{
  "name": "string",
  "grade": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|name|string|true|none||none|
|grade|string|true|none||none|

<h2 id="tocS_UserLoginDTO">UserLoginDTO</h2>

<a id="schemauserlogindto"></a>
<a id="schema_UserLoginDTO"></a>
<a id="tocSuserlogindto"></a>
<a id="tocsuserlogindto"></a>

```json
{
  "username": "string",
  "password": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|password|string|true|none||none|

<h2 id="tocS_ClassUpdateDTO">ClassUpdateDTO</h2>

<a id="schemaclassupdatedto"></a>
<a id="schema_ClassUpdateDTO"></a>
<a id="tocSclassupdatedto"></a>
<a id="tocsclassupdatedto"></a>

```json
{
  "id": 0,
  "name": "string",
  "grade": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||none|
|name|string|true|none||none|
|grade|string|true|none||none|

<h2 id="tocS_UserRegisterDTO">UserRegisterDTO</h2>

<a id="schemauserregisterdto"></a>
<a id="schema_UserRegisterDTO"></a>
<a id="tocSuserregisterdto"></a>
<a id="tocsuserregisterdto"></a>

```json
{
  "username": "string",
  "password": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|password|string|true|none||none|

<h2 id="tocS_DepartmentCreateDTO">DepartmentCreateDTO</h2>

<a id="schemadepartmentcreatedto"></a>
<a id="schema_DepartmentCreateDTO"></a>
<a id="tocSdepartmentcreatedto"></a>
<a id="tocsdepartmentcreatedto"></a>

```json
{
  "name": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|name|string|true|none||none|

<h2 id="tocS_DepartmentUpdateDTO">DepartmentUpdateDTO</h2>

<a id="schemadepartmentupdatedto"></a>
<a id="schema_DepartmentUpdateDTO"></a>
<a id="tocSdepartmentupdatedto"></a>
<a id="tocsdepartmentupdatedto"></a>

```json
{
  "id": 0,
  "name": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||none|
|name|string|true|none||none|

<h2 id="tocS_EmployeeCreateDTO">EmployeeCreateDTO</h2>

<a id="schemaemployeecreatedto"></a>
<a id="schema_EmployeeCreateDTO"></a>
<a id="tocSemployeecreatedto"></a>
<a id="tocsemployeecreatedto"></a>

```json
{
  "username": "string",
  "password": "string",
  "realName": "string",
  "gender": 0,
  "avatar": "string",
  "job": 0,
  "departmentId": 0,
  "entryDate": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|password|string|true|none||none|
|realName|string|true|none||none|
|gender|integer|true|none||none|
|avatar|string|false|none||none|
|job|integer|true|none||none|
|departmentId|integer|true|none||none|
|entryDate|string|true|none||none|

<h2 id="tocS_EmployeeUpdateDTO">EmployeeUpdateDTO</h2>

<a id="schemaemployeeupdatedto"></a>
<a id="schema_EmployeeUpdateDTO"></a>
<a id="tocSemployeeupdatedto"></a>
<a id="tocsemployeeupdatedto"></a>

```json
{
  "id": 0,
  "username": "string",
  "password": "string",
  "realName": "string",
  "gender": 0,
  "avatar": "string",
  "job": 0,
  "departmentId": 0,
  "entryDate": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||none|
|username|string|true|none||none|
|password|string|false|none||none|
|realName|string|true|none||none|
|gender|integer|true|none||none|
|avatar|string|false|none||none|
|job|integer|true|none||none|
|departmentId|integer|true|none||none|
|entryDate|string|true|none||none|

<h2 id="tocS_StudentCreateDTO">StudentCreateDTO</h2>

<a id="schemastudentcreatedto"></a>
<a id="schema_StudentCreateDTO"></a>
<a id="tocSstudentcreatedto"></a>
<a id="tocsstudentcreatedto"></a>

```json
{
  "name": "string",
  "gender": 0,
  "birthDate": "string",
  "classId": 0,
  "joinDate": "string",
  "status": 1
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|name|string|true|none||none|
|gender|integer|true|none||none|
|birthDate|string|true|none||none|
|classId|integer|true|none||none|
|joinDate|string|true|none||none|
|status|integer|false|none||none|

<h2 id="tocS_StudentUpdateDTO">StudentUpdateDTO</h2>

<a id="schemastudentupdatedto"></a>
<a id="schema_StudentUpdateDTO"></a>
<a id="tocSstudentupdatedto"></a>
<a id="tocsstudentupdatedto"></a>

```json
{
  "id": 0,
  "name": "string",
  "gender": 0,
  "birthDate": "string",
  "classId": 0,
  "joinDate": "string",
  "status": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||none|
|name|string|true|none||none|
|gender|integer|true|none||none|
|birthDate|string|true|none||none|
|classId|integer|true|none||none|
|joinDate|string|true|none||none|
|status|integer|true|none||none|

