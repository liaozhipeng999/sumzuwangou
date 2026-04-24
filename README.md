后台管理系统・软件工程课程设计
项目简介
本项目为软件工程课程设计作业，基于 renren-security 开源脚手架快速搭建开发，是一套轻量化、前后端分离的权限管理后台系统。脚手架已封装完善的权限控制、数据校验、代码生成、定时任务、文件存储等基础能力，本项目在此基础上完成二次开发与功能调试，满足课程设计开发规范与基础业务需求。
系统采用主流技术栈拆分前后端架构，结构清晰、注释完善，便于后期维护、二次开发与课程设计文档编写。
核心功能特点
前后端完全分离架构，基于 Token 实现接口安全交互；
精细化权限管理，支持菜单、按钮级别的权限控制；
数据权限隔离，支持部门、用户层级数据访问控制；
内置代码生成器，自动生成后端实体类、DAO、Service 及前端页面代码；
集成定时任务模块，支持任务动态配置与管理；
统一后端参数校验、XSS 安全防护，保障系统安全性；
集成在线 API 文档，方便接口调试与开发学习；
支持多数据库适配，兼容性强。
技术选型
后端技术
核心框架：SpringBoot 3.x
安全框架：Shiro 权限框架
持久层：MyBatis-Plus、Druid 连接池
定时任务：Quartz
数据校验：Hibernate Validator
接口文档：Swagger
项目构建：Maven
前端技术
核心框架：Vue3 + TypeScript
组件库：Element Plus
状态管理：Pinia
路由管理：Vue Router
网络请求：Axios
构建工具：Vite
项目整体结构
plaintext
renren-security
├─renren-common     公共依赖、工具类、全局常量
├─renren-admin      后台核心服务（系统管理、权限、定时任务等）
│  ├─db             数据库初始化 SQL 脚本
│  ├─modules        业务功能模块
│  └─resources      配置文件、映射文件、静态资源
├─renren-api        对外接口服务模块
├─renren-generator  代码生成器模块，快速生成业务代码
└─renren-ui         Vue3 前端独立工程
运行环境要求
JDK 17 及以上
Maven 3.6 及以上
MySQL 8.0 及以上
开发工具：IDEA / VS Code
环境：Windows / MacOS 通用
本地部署运行教程
克隆项目到本地，导入 IDEA 开发工具；
创建本地数据库 renren_security，编码格式设置为 UTF-8；
执行 renren-admin/db/mysql.sql 脚本，完成数据库表结构与初始数据初始化；
修改配置文件 application-dev.yml，配置本地 MySQL 账号、密码；
项目根目录执行 mvn clean install 完成依赖打包；
运行后端启动类 AdminApplication.java，后端启动成功；
进入 renren-ui 前端目录，安装依赖并启动前端项目；
浏览器访问：后端地址 http://localhost:8080/renren-admin
默认测试账号：admin / 密码：admin