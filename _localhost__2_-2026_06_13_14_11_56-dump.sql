-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: termshop
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `mall_user`
--

DROP TABLE IF EXISTS `mall_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mall_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(30) NOT NULL COMMENT '登录账号',
  `password` varchar(100) NOT NULL COMMENT '加密密码',
  `nickname` varchar(50) DEFAULT '' COMMENT '用户昵称',
  `phone` varchar(11) DEFAULT '' COMMENT '手机号',
  `email` varchar(50) DEFAULT '' COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT '' COMMENT '头像',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '账号状态 0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商城用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mall_user`
--

LOCK TABLES `mall_user` WRITE;
/*!40000 ALTER TABLE `mall_user` DISABLE KEYS */;
INSERT INTO `mall_user` VALUES (1,'test002','$2a$10$OumuPUPzb.cGMrFduHAj2ODvtUiPHCPmK0y3j6fBSavRRl.PzL/6K','测试用户2','13800138002','test2@qq.com','',1,NULL,NULL,0),(4,'user0001','$2a$10$MbLJUkgfSrDzETCWHO.YH.aq9YzWDzfmwmGrWu7ATdzDPFOYrluhu','用户1','13800000001','user1@example.com','',1,NULL,NULL,0),(5,'test_b275e5a5','$2a$10$k1oBQcymDPNBXwSs3cBRSOiqxfRPe5L/eWSIRbA6qXHD3vjSVcKbO','测试用户_b275e5a5','13885245700','test_b275e5a5@qq.com','',1,NULL,NULL,0);
/*!40000 ALTER TABLE `mall_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `term_merchants`
--

DROP TABLE IF EXISTS `term_merchants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `term_merchants` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '商家ID(主键)',
  `merchant_name` varchar(128) NOT NULL COMMENT '店铺名称',
  `merchant_logo` varchar(512) DEFAULT NULL COMMENT '店铺Logo URL',
  `merchant_brief` varchar(512) DEFAULT NULL COMMENT '店铺简介(一句话介绍)',
  `username` varchar(32) NOT NULL COMMENT '登录账号(默认用手机号)',
  `password` varchar(255) NOT NULL COMMENT '登录密码(BCrypt加密存储)',
  `contact_name` varchar(32) NOT NULL COMMENT '店主姓名',
  `contact_phone` varchar(16) NOT NULL COMMENT '联系电话',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `main_category_id` bigint unsigned DEFAULT NULL COMMENT '主营分类ID(关联term_product_categories.id)',
  `merchant_level` tinyint NOT NULL DEFAULT '1' COMMENT '商家等级: 1-普通, 2-优质, 3-旗舰',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '店铺状态: 0-关闭, 1-营业中',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序权重',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_merchant_name` (`merchant_name`),
  KEY `idx_main_category_id` (`main_category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted_at` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家主表(含登录功能)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `term_merchants`
--

LOCK TABLES `term_merchants` WRITE;
/*!40000 ALTER TABLE `term_merchants` DISABLE KEYS */;
INSERT INTO `term_merchants` VALUES (22,'????',NULL,NULL,'testuser','$2a$10$KUiXv7vv.pktJrd.P3DgAudCfd.TjC9MYNmqUaKyRDDqS9z8.5cMK','????','13800138000','test@example.com',NULL,1,1,0,'2026-06-12 00:26:03','2026-06-12 00:26:03',NULL),(23,'测试店铺_1781197336411',NULL,'这是一家测试店铺','testuser_1781197336411','$2a$10$oz2zMDSFcdXFt5LGCOg7qeBpQPJVInEKYQsJzVDKM75dCd6hgKRyW','测试店主','13866767832','testuser_1781197336411@example.com',NULL,1,1,0,'2026-06-12 01:02:17','2026-06-12 01:02:17',NULL),(24,'我的小店',NULL,'专业销售各类优质商品','test_merchant001','$2a$10$G.xj.i9B3vVISvnT4jT8J.jl85n0gLb5W2VjRU1nmtyX2zp/kr59C','张三','13800138001','test2@example.com',1,1,1,0,'2026-06-12 17:14:50','2026-06-12 17:14:50',NULL),(25,'QualityMarket1',NULL,'QualityMarket1 - Professional Market','merchant_1','$2a$10$v/WC7W4FjRjfuVhBhUHBBO8PnkVoX6V90w8rLT8elCJfvJBAkKBi2','Zhao','13808614295','merchant_1@example.com',NULL,1,1,0,'2026-06-12 17:16:06','2026-06-12 17:16:06',NULL),(26,'PremiumMart2',NULL,'PremiumMart2 - Professional Mart','merchant_2','$2a$10$VvYDoA9FOJ.2TByoNaStw.rwD6iYS5M4TClozO1nvFyI/Yy.O5cIi','Wu','15287506142','merchant_2@example.com',NULL,1,1,0,'2026-06-12 17:16:06','2026-06-12 17:16:06',NULL),(27,'QualityEmporium3',NULL,'QualityEmporium3 - Professional Emporium','merchant_3','$2a$10$JHKgHRVDHJyMLifAUG98MOtAaWezxkcv1Y/qCSHO.hHJMH0vczyFe','Huang','18623567918','merchant_3@example.com',NULL,1,1,0,'2026-06-12 17:16:06','2026-06-12 17:16:06',NULL),(28,'PremiumShop4',NULL,'PremiumShop4 - Professional Shop','merchant_4','$2a$10$nrUUAIEjVnrrEejKfpn7WOakTI0Q43DnSgNbAgBXAz62MM3lMJelu','Lin','18990362417','merchant_4@example.com',NULL,1,1,0,'2026-06-12 17:16:06','2026-06-12 17:16:06',NULL),(29,'SuperGallery5',NULL,'SuperGallery5 - Professional Gallery','merchant_5','$2a$10$8NR6wCax0rhsCjRqIxV4Xu5XLpCxzFSUsunS8jebQonjJct9cTuJq','Wang','15181476035','merchant_5@example.com',NULL,1,1,0,'2026-06-12 17:16:06','2026-06-12 17:16:06',NULL),(30,'EliteShop6',NULL,'EliteShop6 - Professional Shop','merchant_6','$2a$10$ht88v/s1L1F75hx9qW5FauePD1jo6TE3ufN2LtFPWlTNmZfDiWSqi','Guo','18604578319','merchant_6@example.com',NULL,1,1,0,'2026-06-12 17:16:06','2026-06-12 17:16:06',NULL),(31,'SelectShop7',NULL,'SelectShop7 - Professional Shop','merchant_7','$2a$10$3quTBGyn/Y2WvESZU5C41eJwt6tMYETMhOgZoPKIvZVzzwo4T/gC.','Zhao','17863174285','merchant_7@example.com',NULL,1,1,0,'2026-06-12 17:16:06','2026-06-12 17:16:06',NULL),(32,'EliteCenter8',NULL,'EliteCenter8 - Professional Center','merchant_8','$2a$10$hzFX6REuomlNa0k46Z0/guKch6KMlmrpxAst/HOjdawQYLrSSglRy','Yang','15045031687','merchant_8@example.com',NULL,1,1,0,'2026-06-12 17:16:06','2026-06-12 17:16:06',NULL),(33,'SelectMart9',NULL,'SelectMart9 - Professional Mart','merchant_9','$2a$10$L3bOzfNZXsfjofXJKz0xSOXXCIiMNsQY1p64I6UWIz/djfK6hqQd2','Sun','15092584306','merchant_9@example.com',NULL,1,1,0,'2026-06-12 17:16:06','2026-06-12 17:16:06',NULL),(34,'QualityEmporium10',NULL,'QualityEmporium10 - Professional Emporium','merchant_10','$2a$10$v.TTJY6q/rcHso.79lhPkuCgj.BQD3O3O2WDo4kXm6TIZqy0FFpga','Huang','18512708936','merchant_10@example.com',NULL,1,1,0,'2026-06-12 17:16:07','2026-06-12 17:16:07',NULL),(35,'QualityHub11',NULL,'QualityHub11 - Professional Hub','merchant_11','$2a$10$/zZdkeGfGUxSroOE7TopU.UlQ7YDN3kZEIovOzZvtBS.WAZgcmv46','Wu','15812638790','merchant_11@example.com',NULL,1,1,0,'2026-06-12 17:16:07','2026-06-12 17:16:07',NULL),(36,'PrimeOutlet12',NULL,'PrimeOutlet12 - Professional Outlet','merchant_12','$2a$10$afZK7B/W4hNysNIz9WiRmOV1vHZ/u4IZM4CQ166tpbomugEoCQ5me','He','15117609532','merchant_12@example.com',NULL,1,1,0,'2026-06-12 17:16:07','2026-06-12 17:16:07',NULL),(37,'SuperMarket13',NULL,'SuperMarket13 - Professional Market','merchant_13','$2a$10$huFS.ZhAttqaz2D8Ngy.3OEYt2LHEtzjW8cRuO1d8BlOxB1MzMILe','Guo','18539048217','merchant_13@example.com',NULL,1,1,0,'2026-06-12 17:16:07','2026-06-12 17:16:07',NULL),(38,'ValueMart14',NULL,'ValueMart14 - Professional Mart','merchant_14','$2a$10$nbt1peAxpRaqBXZ.nATZyuXTq4QpNi.8fLDJPj/XJItoiI1/XMyB.','Li','15082054693','merchant_14@example.com',NULL,1,1,0,'2026-06-12 17:16:07','2026-06-12 17:16:07',NULL),(39,'ValueShop15',NULL,'ValueShop15 - Professional Shop','merchant_15','$2a$10$bZ3HIwr.0J/uCFY6V6hVXODycSOoO46qa3Ek.Z9fUw1KgN6r2AmC6','Chen','18898640321','merchant_15@example.com',NULL,1,1,0,'2026-06-12 17:16:07','2026-06-12 17:16:07',NULL),(40,'QualityStore16',NULL,'QualityStore16 - Professional Store','merchant_16','$2a$10$w8clXe8PzqmTWbARwC99xuRbA1UB6u5xMbkEiULEDS27W3V8.et8O','Yang','15227069358','merchant_16@example.com',NULL,1,1,0,'2026-06-12 17:16:08','2026-06-12 17:16:08',NULL),(41,'SelectGallery17',NULL,'SelectGallery17 - Professional Gallery','merchant_17','$2a$10$n8M/7Yyofnzz3y2Q0YKGBOnqIL/WMQwdSHRiIwqHr9WiFvfHUCEWe','Zhang','15192573106','merchant_17@example.com',NULL,1,1,0,'2026-06-12 17:16:08','2026-06-12 17:16:08',NULL),(42,'EliteCenter18',NULL,'EliteCenter18 - Professional Center','merchant_18','$2a$10$oOAZTT/e1HKJT0TASSaqtuZfaFbEGR8JPvAzsNLJWuMbatBcKLoXy','Qian','15291603475','merchant_18@example.com',NULL,1,1,0,'2026-06-12 17:16:08','2026-06-12 17:16:08',NULL),(43,'ValueStore19',NULL,'ValueStore19 - Professional Store','merchant_19','$2a$10$yR8olCwyYG/HjW9ZwulyYOggpYodQ3rtOjKARvgOK8DqnqaYmqIt2','Zhang','15135897620','merchant_19@example.com',NULL,1,1,0,'2026-06-12 17:16:08','2026-06-12 17:16:08',NULL),(44,'EliteEmporium20',NULL,'EliteEmporium20 - Professional Emporium','merchant_20','$2a$10$OO8MblACm4.mHlW1J3mS4Ou1GPT8yQP7By/Liu/nQXgPlBqD8bGau','Guo','18996341805','merchant_20@example.com',NULL,1,1,0,'2026-06-12 17:16:08','2026-06-12 17:16:08',NULL),(45,'EliteShop21',NULL,'EliteShop21 - Professional Shop','merchant_21','$2a$10$mofuTQOpDVpBmhi.mPp/ceHv4tsRCt04.bziAKl8HjkufzVqJwvrC','Lin','18625146097','merchant_21@example.com',NULL,1,1,0,'2026-06-12 17:16:08','2026-06-12 17:16:08',NULL),(46,'SelectEmporium22',NULL,'SelectEmporium22 - Professional Emporium','merchant_22','$2a$10$wngLPyIZ6XLOpmdJlYa05OiV.UU3OK5teuLCuhQwfepZJwZp4cW5S','Ma','15204528716','merchant_22@example.com',NULL,1,1,0,'2026-06-12 17:16:09','2026-06-12 17:16:09',NULL),(47,'PremiumCenter23',NULL,'PremiumCenter23 - Professional Center','merchant_23','$2a$10$WYD6VRv9f6QwtW/FSAGnJO5OsdGdxe2Rz603cxl0IKM2/1s7ydCzS','Wu','17819347205','merchant_23@example.com',NULL,1,1,0,'2026-06-12 17:16:09','2026-06-12 17:16:09',NULL),(48,'BestCenter24',NULL,'BestCenter24 - Professional Center','merchant_24','$2a$10$79r4qilk1MvTil57cNT.v.YqCWU0NrPPGbUD31I19kcxkddA0gAv2','Wang','15839702154','merchant_24@example.com',NULL,1,1,0,'2026-06-12 17:16:09','2026-06-12 17:16:09',NULL),(49,'TopOutlet25',NULL,'TopOutlet25 - Professional Outlet','merchant_25','$2a$10$yQV6KYcDEvIp/jNqJFBh0ebzdbDtFlCPfj1XZ6W9tF3cu5GDMrDdK','Wang','15293467802','merchant_25@example.com',NULL,1,1,0,'2026-06-12 17:16:09','2026-06-12 17:16:09',NULL),(50,'EliteShop26',NULL,'EliteShop26 - Professional Shop','merchant_26','$2a$10$Q/.Em6saofZX9jIqcpXJO.O7/o86RCcFopjxvTSJUwGTi6QkwpWCW','Wu','18381204967','merchant_26@example.com',NULL,1,1,0,'2026-06-12 17:16:09','2026-06-12 17:16:09',NULL),(51,'SuperCenter27',NULL,'SuperCenter27 - Professional Center','merchant_27','$2a$10$9tM0j3HfAAlDEeySkgqTves31h2OKsosxqW.yCWiF1qAbbe.BeUdO','Wang','15956029781','merchant_27@example.com',NULL,1,1,0,'2026-06-12 17:16:09','2026-06-12 17:16:09',NULL),(52,'PrimeGallery28',NULL,'PrimeGallery28 - Professional Gallery','merchant_28','$2a$10$J8ZuZ9kex/93DwUKSQ4ce.EwseVEtGkow8SXA/hWDdQjR5vePAZY2','Li','15887254360','merchant_28@example.com',NULL,1,1,0,'2026-06-12 17:16:09','2026-06-12 17:16:09',NULL),(53,'BestOutlet29',NULL,'BestOutlet29 - Professional Outlet','merchant_29','$2a$10$xej7Kpjb03lmkG5qOoWp4eWdJcpLGBKxP5aOIHM4mroMBnmP/5DvW','Yang','18637615084','merchant_29@example.com',NULL,1,1,0,'2026-06-12 17:16:09','2026-06-12 17:16:09',NULL),(54,'ValueShop30',NULL,'ValueShop30 - Professional Shop','merchant_30','$2a$10$XV3t.VFV/Q3iZccOGpJcNuUfPp/F3XDq4MmDBqkK5DiC5xAo2h5IS','Zhou','18538925670','merchant_30@example.com',NULL,1,1,0,'2026-06-12 17:16:09','2026-06-12 17:16:09',NULL),(55,'EliteHub31',NULL,'EliteHub31 - Professional Hub','merchant_31','$2a$10$uYvxMHdxwcdOEV.XVXmN0eD9IESmP.IUk7zPgv1n.UDQn76viapny','Ma','15073206495','merchant_31@example.com',NULL,1,1,0,'2026-06-12 17:16:10','2026-06-12 17:16:10',NULL),(56,'SelectEmporium32',NULL,'SelectEmporium32 - Professional Emporium','merchant_32','$2a$10$w/UCXUc2KK7.UCVvnRxQe.F84LIOsy8bAfcNPLEgiY7SaWs.22hj2','Guo','18950826137','merchant_32@example.com',NULL,1,1,0,'2026-06-12 17:16:10','2026-06-12 17:16:10',NULL),(57,'BestStore33',NULL,'BestStore33 - Professional Store','merchant_33','$2a$10$4fOXFntIxnoJWnoO8pSyYOPRwyEFXQ4Aj1R2ZBjtCfvw2X4aPgiEO','Chen','15284705923','merchant_33@example.com',NULL,1,1,0,'2026-06-12 17:16:10','2026-06-12 17:16:10',NULL),(58,'ChoiceOutlet34',NULL,'ChoiceOutlet34 - Professional Outlet','merchant_34','$2a$10$9ozk4JtldrwgCfaSmcCtCuvCnxJtAM0.rkUlQLA1kdygmm9xCOfhW','Huang','15874065219','merchant_34@example.com',NULL,1,1,0,'2026-06-12 17:16:10','2026-06-12 17:16:10',NULL),(59,'PrimeHub35',NULL,'PrimeHub35 - Professional Hub','merchant_35','$2a$10$q/0Lt6pEog6U4uXiWZ05bOTr8Po5NA2f0qg1dGTZR7K0MP6mgP.d6','Zhu','13875146839','merchant_35@example.com',NULL,1,1,0,'2026-06-12 17:16:10','2026-06-12 17:16:10',NULL),(60,'SelectOutlet36',NULL,'SelectOutlet36 - Professional Outlet','merchant_36','$2a$10$ksiHlRitEty2sHxSL9oGZ.rM68xWHlCRbYVjWcBRFuAZ1wHre6Tc6','Qian','15985417236','merchant_36@example.com',NULL,1,1,0,'2026-06-12 17:16:10','2026-06-12 17:16:10',NULL),(61,'ValueHub37',NULL,'ValueHub37 - Professional Hub','merchant_37','$2a$10$2wMYs85gj9nEoxy3JKI5euMhw/pLrMPRE0IAvFcl900yWl677/ZM2','He','15990365812','merchant_37@example.com',NULL,1,1,0,'2026-06-12 17:16:11','2026-06-12 17:16:11',NULL),(62,'TopOutlet38',NULL,'TopOutlet38 - Professional Outlet','merchant_38','$2a$10$.kyDe.wkVHNV3UrrdNcEGedPCz9X/K3xdRSvyEMu.rB24n5kPh.0.','Liu','15273962804','merchant_38@example.com',NULL,1,1,0,'2026-06-12 17:16:11','2026-06-12 17:16:11',NULL),(63,'ChoiceCenter39',NULL,'ChoiceCenter39 - Professional Center','merchant_39','$2a$10$Reqrtm/dYEX7ufgR2sA/UegUYhoJRazGQUWI8p7Kb8qVe8tWy6pWy','Guo','15916740258','merchant_39@example.com',NULL,1,1,0,'2026-06-12 17:16:11','2026-06-12 17:16:11',NULL),(64,'EliteMart40',NULL,'EliteMart40 - Professional Mart','merchant_40','$2a$10$NuTLkRcq1SNAxmo70eT.ouv17H4MEo0kiaZWlntjgVmzEOolXhcHm','Zhou','18908394761','merchant_40@example.com',NULL,1,1,0,'2026-06-12 17:16:11','2026-06-12 17:16:11',NULL),(65,'PremiumEmporium41',NULL,'PremiumEmporium41 - Professional Emporium','merchant_41','$2a$10$36i2A7bhXbnR7VuM5x62BuXfOPnxA1ij43KF5MY4GZ7adCMzH3Ojy','Wu','13821049763','merchant_41@example.com',NULL,1,1,0,'2026-06-12 17:16:11','2026-06-12 17:16:11',NULL),(66,'ValueShop42',NULL,'ValueShop42 - Professional Shop','merchant_42','$2a$10$wtP6rXOstw3vsDKncmPy7OPzeJPlvpjiM6eHGCdO/.AIQrZNnxuuK','Zheng','13881925674','merchant_42@example.com',NULL,1,1,0,'2026-06-12 17:16:11','2026-06-12 17:16:11',NULL),(67,'TopStore43',NULL,'TopStore43 - Professional Store','merchant_43','$2a$10$lL3HwJk./czTK9jXbkbrLuHviA5du4WROc4HzfGljKI8wG2Va8x/m','Zhu','18324931785','merchant_43@example.com',NULL,1,1,0,'2026-06-12 17:16:12','2026-06-12 17:16:12',NULL),(68,'QualityMart44',NULL,'QualityMart44 - Professional Mart','merchant_44','$2a$10$erRbfxULpR0bQUS84Sjab.dwbhYoHGwfWTCiXlWTnPBLvDSIZdtHy','Huang','18896748523','merchant_44@example.com',NULL,1,1,0,'2026-06-12 17:16:12','2026-06-12 17:16:12',NULL),(69,'PremiumStore45',NULL,'PremiumStore45 - Professional Store','merchant_45','$2a$10$ovmSBZ8Zj5Eb68HmaRpAC.vNA0XraeldsdvULWmj0KWRcXsAojsK2','Qian','13948702319','merchant_45@example.com',NULL,1,1,0,'2026-06-12 17:16:12','2026-06-12 17:16:12',NULL),(70,'QualityHub46',NULL,'QualityHub46 - Professional Hub','merchant_46','$2a$10$UtjGnV0nd4Xmz0.PyEu1yechIF.EEExeuba7H2XVL1gXmPWWzvz52','Ma','18669072145','merchant_46@example.com',NULL,1,1,0,'2026-06-12 17:16:12','2026-06-12 17:16:12',NULL),(71,'ChoicePlace47',NULL,'ChoicePlace47 - Professional Place','merchant_47','$2a$10$c7v0idpqgsCYkGuQvcl4qu.JCTvlAXiO5UTWTAqvDsnE3C2Sj7yly','Chen','15837145968','merchant_47@example.com',NULL,1,1,0,'2026-06-12 17:16:12','2026-06-12 17:16:12',NULL),(72,'BestHub48',NULL,'BestHub48 - Professional Hub','merchant_48','$2a$10$/1IZzaI62Gx/GGfpwP7yzO4HnFvnrPI6Ei46dweXs4jVB.WAK76mq','Chen','18812740569','merchant_48@example.com',NULL,1,1,0,'2026-06-12 17:16:12','2026-06-12 17:16:12',NULL),(73,'ChoiceMarket49',NULL,'ChoiceMarket49 - Professional Market','merchant_49','$2a$10$CEU23BvYAXOk/.XB8cj/h.BdxWthDcZwFJn3D1uMpz8Eh7T1xL7qW','He','13865418307','merchant_49@example.com',NULL,1,1,0,'2026-06-12 17:16:12','2026-06-12 17:16:12',NULL),(74,'BestGallery50',NULL,'BestGallery50 - Professional Gallery','merchant_50','$2a$10$6LejbpoA5FG16P.GVnrS/.0DVkpd6gxwykaoaNdGH.am5ujL74Edi','Wang','15265187390','merchant_50@example.com',NULL,1,1,0,'2026-06-12 17:16:12','2026-06-12 17:16:12',NULL),(75,'PremiumPlace51',NULL,'PremiumPlace51 - Professional Place','merchant_51','$2a$10$0RlrnQ5zo8odgD7TwpbqneLf8.6AX2Ff5oNofZ84A.tpMF2LHkbwW','Xu','18613498705','merchant_51@example.com',NULL,1,1,0,'2026-06-12 17:16:13','2026-06-12 17:16:13',NULL),(76,'TopMart52',NULL,'TopMart52 - Professional Mart','merchant_52','$2a$10$VYvk1VfWrmajI6VweW9MKuE9j.TRqHZ0tZ9iqdAOiiA3We0DOhvde','He','18651234809','merchant_52@example.com',NULL,1,1,0,'2026-06-12 17:16:13','2026-06-12 17:16:13',NULL),(77,'SuperStore53',NULL,'SuperStore53 - Professional Store','merchant_53','$2a$10$4UTqEFGwd9B0TyDLrGeenOpojbKwUNB6Cfklh/1O8bHy8hkfX5mbu','Gao','18953914208','merchant_53@example.com',NULL,1,1,0,'2026-06-12 17:16:13','2026-06-12 17:16:13',NULL),(78,'SelectStore54',NULL,'SelectStore54 - Professional Store','merchant_54','$2a$10$c9BRevP7sTFarjXmLMOaPu5q2Q6sRVK2thhI/tHikcLThBW6E4CIC','He','15295213478','merchant_54@example.com',NULL,1,1,0,'2026-06-12 17:16:13','2026-06-12 17:16:13',NULL),(79,'EliteHub55',NULL,'EliteHub55 - Professional Hub','merchant_55','$2a$10$u8yXku7Az4j6Ci3oGMdVouuvF5S7NLnXo.c2G3XJ9I/1SflYg0DMu','Chen','18630964172','merchant_55@example.com',NULL,1,1,0,'2026-06-12 17:16:13','2026-06-12 17:16:13',NULL),(80,'SuperGallery56',NULL,'SuperGallery56 - Professional Gallery','merchant_56','$2a$10$9GILIbv.1Dy8Db2AQ8SR6u1TszJKUPFTEzYxB1afDW2KvuxweEQ9.','Zhou','15027568304','merchant_56@example.com',NULL,1,1,0,'2026-06-12 17:16:13','2026-06-12 17:16:13',NULL),(81,'SuperHub57',NULL,'SuperHub57 - Professional Hub','merchant_57','$2a$10$EAdqHUMLPykr0QmYcOWvQ.Uq7yiC4V00l2xe87PbWYnQgDUj1mkdW','Chen','15276285190','merchant_57@example.com',NULL,1,1,0,'2026-06-12 17:16:13','2026-06-12 17:16:13',NULL),(82,'EliteShop58',NULL,'EliteShop58 - Professional Shop','merchant_58','$2a$10$zimWOYjzMfN.IXi94g6ryO1OewPgwt0z1qbeCAK5YAUdk5NJKb97u','Zheng','18827165084','merchant_58@example.com',NULL,1,1,0,'2026-06-12 17:16:14','2026-06-12 17:16:14',NULL),(83,'TopCenter59',NULL,'TopCenter59 - Professional Center','merchant_59','$2a$10$NJAu0zMGufXqVJNxMhRCL.LD3Q.eofHZIZvRchXulg4o/WpA75qp2','Zhang','15227904618','merchant_59@example.com',NULL,1,1,0,'2026-06-12 17:16:14','2026-06-12 17:16:14',NULL),(84,'ChoiceHub60',NULL,'ChoiceHub60 - Professional Hub','merchant_60','$2a$10$2Wz9qmVPIBu46GuEZafBV.dlWRPa2LJqaiUgMJ2Xh.LIPTmN7rjVS','Li','17829347185','merchant_60@example.com',NULL,1,1,0,'2026-06-12 17:16:14','2026-06-12 17:16:14',NULL),(85,'TopCenter61',NULL,'TopCenter61 - Professional Center','merchant_61','$2a$10$r.qOPL/eL9yyG61yxBXzROXRVzDomxJ7picXgulM07iwlj.ngHxKe','Qian','18698640127','merchant_61@example.com',NULL,1,1,0,'2026-06-12 17:16:14','2026-06-12 17:16:14',NULL),(86,'BestShop62',NULL,'BestShop62 - Professional Shop','merchant_62','$2a$10$NB2IQy/IPR98hGdPs.MdY.nTxtxJqEMZ/.YrUs30bDBdOvr3dAXV2','Chen','18263450197','merchant_62@example.com',NULL,1,1,0,'2026-06-12 17:16:14','2026-06-12 17:16:14',NULL),(87,'ChoiceGallery63',NULL,'ChoiceGallery63 - Professional Gallery','merchant_63','$2a$10$zcTKJnIZOhNe3n2/sIJuner2dIAtWtgqQp/St.LeqsjAmiEouSJHW','Xu','15276031928','merchant_63@example.com',NULL,1,1,0,'2026-06-12 17:16:14','2026-06-12 17:16:14',NULL),(88,'PrimeMarket64',NULL,'PrimeMarket64 - Professional Market','merchant_64','$2a$10$gyuP.035fuWUEnG7kCYpQu/kTAibEy1AbmejMqJiUVGtIvrAWrPY6','He','18347905213','merchant_64@example.com',NULL,1,1,0,'2026-06-12 17:16:14','2026-06-12 17:16:14',NULL),(89,'TopCenter65',NULL,'TopCenter65 - Professional Center','merchant_65','$2a$10$iZEUT41w1ygvSohOX23V4OdYbFipZ0ciwg6bZ5LXYD0rdKoh2RKCS','Guo','18219057264','merchant_65@example.com',NULL,1,1,0,'2026-06-12 17:16:15','2026-06-12 17:16:15',NULL),(90,'BestMart66',NULL,'BestMart66 - Professional Mart','merchant_66','$2a$10$lKokljFhd6yv47yfBsfnt.iQ4R.NeXymUAB1rjy0ajqr8vw/d.TLC','Xu','13971834065','merchant_66@example.com',NULL,1,1,0,'2026-06-12 17:16:15','2026-06-12 17:16:15',NULL),(91,'PrimeEmporium67',NULL,'PrimeEmporium67 - Professional Emporium','merchant_67','$2a$10$6LbVeDRtfQ4HMKR7OzsTg.dkldE6tLcrjr1tXMldS.baNNKVLERMC','Chen','18838105297','merchant_67@example.com',NULL,1,1,0,'2026-06-12 17:16:15','2026-06-12 17:16:15',NULL),(92,'SuperOutlet68',NULL,'SuperOutlet68 - Professional Outlet','merchant_68','$2a$10$Kx1skMkprc2lTBCrFcBdoe6QeRgQ2PYTdS29tTUr0UHhHo5TTD1Ku','Chen','15164978253','merchant_68@example.com',NULL,1,1,0,'2026-06-12 17:16:15','2026-06-12 17:16:15',NULL),(93,'SuperGallery69',NULL,'SuperGallery69 - Professional Gallery','merchant_69','$2a$10$NYUrow2ZlhPW8XQkwgKaYuAd7aqHyBTBzFfo5IwJGNfaaoJdmenyK','Chen','13856791840','merchant_69@example.com',NULL,1,1,0,'2026-06-12 17:16:15','2026-06-12 17:16:15',NULL),(94,'PrimeEmporium70',NULL,'PrimeEmporium70 - Professional Emporium','merchant_70','$2a$10$dFYU9NyT5kKMQzsLzOlyAeN0Z.jmj4FlDLyt3AikY0Y3S53gVF3cq','Wu','13914062398','merchant_70@example.com',NULL,1,1,0,'2026-06-12 17:16:15','2026-06-12 17:16:15',NULL),(95,'ChoicePlace71',NULL,'ChoicePlace71 - Professional Place','merchant_71','$2a$10$q.Sis3q223bjYcNNng.Rae6UBF8K2c6s2xaVx2Wd5.u2w1HuvhkjK','Guo','18234865092','merchant_71@example.com',NULL,1,1,0,'2026-06-12 17:16:16','2026-06-12 17:16:16',NULL),(96,'BestOutlet72',NULL,'BestOutlet72 - Professional Outlet','merchant_72','$2a$10$yW1B32/3Z2jI9hfiA/nVUu0PaoKOG6YLaKfilqpjlzUdMd3vP0YPG','Li','18863248019','merchant_72@example.com',NULL,1,1,0,'2026-06-12 17:16:16','2026-06-12 17:16:16',NULL),(97,'BestStore73',NULL,'BestStore73 - Professional Store','merchant_73','$2a$10$ozHxQi7kCORISCcKrhMwA.98VYxiI85pDU5n9JVxmDopRbsAyf9Ta','Qian','18536725904','merchant_73@example.com',NULL,1,1,0,'2026-06-12 17:16:16','2026-06-12 17:16:16',NULL),(98,'QualityStore74',NULL,'QualityStore74 - Professional Store','merchant_74','$2a$10$agQFwnfxu60ry.bWLjD4RuI0i6KAa/hH.lmagXP4AxzQAhKIUkU6O','Ma','18632491068','merchant_74@example.com',NULL,1,1,0,'2026-06-12 17:16:16','2026-06-12 17:16:16',NULL),(99,'QualityStore75',NULL,'QualityStore75 - Professional Store','merchant_75','$2a$10$bSFYIiSfi5U08eV6uEhSJuJef7K6s9Z47llVsxbpUEE3TOuiwU3By','Zheng','18307126584','merchant_75@example.com',NULL,1,1,0,'2026-06-12 17:16:16','2026-06-12 17:16:16',NULL),(100,'QualityEmporium76',NULL,'QualityEmporium76 - Professional Emporium','merchant_76','$2a$10$gxfurNACrpP.H0I0.C8b6u/0EbdBNiiEEli1iiofk1qwHC5OvWv4i','Zhu','18831485920','merchant_76@example.com',NULL,1,1,0,'2026-06-12 17:16:16','2026-06-12 17:16:16',NULL),(101,'SuperOutlet77',NULL,'SuperOutlet77 - Professional Outlet','merchant_77','$2a$10$D.Fc/SV41zTvywv/wKB9V.4EoSHxkVS2X1lLVf4bHojfca.vPYeLK','Lin','13902683174','merchant_77@example.com',NULL,1,1,0,'2026-06-12 17:16:16','2026-06-12 17:16:16',NULL),(102,'TopEmporium78',NULL,'TopEmporium78 - Professional Emporium','merchant_78','$2a$10$5Aymz/kV5Q4S07PO.OY4iOLRSaaJ7cU4yor8kLtOh5C746VeZB5We','Wu','18889251607','merchant_78@example.com',NULL,1,1,0,'2026-06-12 17:16:16','2026-06-12 17:16:16',NULL),(103,'PremiumGallery79',NULL,'PremiumGallery79 - Professional Gallery','merchant_79','$2a$10$u6gzfiN3YE5WlEvDDa2CpOvhkeL8xV82RyMHnNbqJPlgl1DSzwy5y','Liu','15221983054','merchant_79@example.com',NULL,1,1,0,'2026-06-12 17:16:16','2026-06-12 17:16:16',NULL),(104,'SuperMarket80',NULL,'SuperMarket80 - Professional Market','merchant_80','$2a$10$qsqvv3roOf.OaUP3bx3W1eK6QvtIYK5oJ6xNWwde1e18S4DRN5GkW','Zheng','15238294750','merchant_80@example.com',NULL,1,1,0,'2026-06-12 17:16:17','2026-06-12 17:16:17',NULL),(105,'QualityOutlet81',NULL,'QualityOutlet81 - Professional Outlet','merchant_81','$2a$10$QcJehYn00wB7D7ZuVjecLOWdUrVpcfCSseg2rh59M2DPKKkCSjR4W','He','17849316872','merchant_81@example.com',NULL,1,1,0,'2026-06-12 17:16:17','2026-06-12 17:16:17',NULL),(106,'PrimePlace82',NULL,'PrimePlace82 - Professional Place','merchant_82','$2a$10$FZRp/g0VLMheIUpK6iZEp.ZKzF8oywjGB1WIjoqrBn3Lg9ca01azy','Wang','15005684973','merchant_82@example.com',NULL,1,1,0,'2026-06-12 17:16:17','2026-06-12 17:16:17',NULL),(107,'TopPlace83',NULL,'TopPlace83 - Professional Place','merchant_83','$2a$10$MohTppDxvexvA1TD2IHcPOvdXXUybx5NkBvRpPx1HoIHNDzJhbsae','Wang','18375196382','merchant_83@example.com',NULL,1,1,0,'2026-06-12 17:16:17','2026-06-12 17:16:17',NULL),(108,'ChoiceCenter84',NULL,'ChoiceCenter84 - Professional Center','merchant_84','$2a$10$WjQHjSQmgqwXbXIrzUXCcu92.kZY619SJA4TMWt6CsC5J4EpuKRCW','Zhou','15975261038','merchant_84@example.com',NULL,1,1,0,'2026-06-12 17:16:17','2026-06-12 17:16:17',NULL),(109,'PrimeStore85',NULL,'PrimeStore85 - Professional Store','merchant_85','$2a$10$SGzk2e1TEy6ClqqVHlMzoepRbToKeCdSQ/FwA1oLCgZZcUqG6MrJ2','Sun','18367095328','merchant_85@example.com',NULL,1,1,0,'2026-06-12 17:16:17','2026-06-12 17:16:17',NULL),(110,'ElitePlace86',NULL,'ElitePlace86 - Professional Place','merchant_86','$2a$10$oiy1fXgHYY/qn8MjzOIcgu3UUu8psse864.qAgDJ4O3MxUzyBtn.e','Qian','18893470251','merchant_86@example.com',NULL,1,1,0,'2026-06-12 17:16:18','2026-06-12 17:16:18',NULL),(111,'TopMarket87',NULL,'TopMarket87 - Professional Market','merchant_87','$2a$10$Y258q8RdWqUSuneXTYPNiOap3720Gg4aPWaZZkDwlSI466D3MY4aG','Li','15963250748','merchant_87@example.com',NULL,1,1,0,'2026-06-12 17:16:18','2026-06-12 17:16:18',NULL),(112,'BestOutlet88',NULL,'BestOutlet88 - Professional Outlet','merchant_88','$2a$10$GROMbrIfRSo/ditk22zKEuyUIebRXN1rMI.L7NuCPxyB3fok9KRNO','Zheng','18695427806','merchant_88@example.com',NULL,1,1,0,'2026-06-12 17:16:18','2026-06-12 17:16:18',NULL),(113,'PremiumEmporium89',NULL,'PremiumEmporium89 - Professional Emporium','merchant_89','$2a$10$d1zctqa2ePXr3HBGwtXDVu6rdAqQJg4MgtxBwU6SHscnHaYFiQemm','Zhao','15868140573','merchant_89@example.com',NULL,1,1,0,'2026-06-12 17:16:18','2026-06-12 17:16:18',NULL),(114,'QualityCenter90',NULL,'QualityCenter90 - Professional Center','merchant_90','$2a$10$aZ1/2gXjwQ.2J8.hTO2h4.TKWYoyfPajuL0QmQnKQhKcD24TAoLTa','Zhang','15109185376','merchant_90@example.com',NULL,1,1,0,'2026-06-12 17:16:18','2026-06-12 17:16:18',NULL),(115,'TopMart91',NULL,'TopMart91 - Professional Mart','merchant_91','$2a$10$eV3AwErQ2W0ssrGPGoKNzu90ksYzxgFGvtU15U1ZH/TXIkbtMxW7q','Wu','15218725396','merchant_91@example.com',NULL,1,1,0,'2026-06-12 17:16:18','2026-06-12 17:16:18',NULL),(116,'PrimeShop92',NULL,'PrimeShop92 - Professional Shop','merchant_92','$2a$10$dpSWB8VRKOxcs0JZJkRIZ.dmwcNO64RDeMkLl8GTP7/GPscAFQrwe','He','15284137562','merchant_92@example.com',NULL,1,1,0,'2026-06-12 17:16:19','2026-06-12 17:16:19',NULL),(117,'BestEmporium93',NULL,'BestEmporium93 - Professional Emporium','merchant_93','$2a$10$QY4dYk0ah66PzEEvrbXMleoI8PXM5TvOPmet/RPOCYLEzzZY9fqTy','Xu','17826071534','merchant_93@example.com',NULL,1,1,0,'2026-06-12 17:16:19','2026-06-12 17:16:19',NULL),(118,'PrimePlace94',NULL,'PrimePlace94 - Professional Place','merchant_94','$2a$10$XWKfm/yipc674qIzIDtS1eQ9YX5rW3vza73Bdjgi7E.hZhFNPzgPm','Gao','18634069287','merchant_94@example.com',NULL,1,1,0,'2026-06-12 17:16:19','2026-06-12 17:16:19',NULL),(119,'BestMart95',NULL,'BestMart95 - Professional Mart','merchant_95','$2a$10$bwhpXyql2YSqDRGKnk.1PuFWNTLCTTklZZLJq36cgzQIfXqtDMi4G','Ma','18583246901','merchant_95@example.com',NULL,1,1,0,'2026-06-12 17:16:19','2026-06-12 17:16:19',NULL),(120,'BestGallery96',NULL,'BestGallery96 - Professional Gallery','merchant_96','$2a$10$vzwmhn2XX1IH.WMizEtb5e/RLLg7/iSzLLiUbk.HZiIWXII31YSuS','Ma','18346518032','merchant_96@example.com',NULL,1,1,0,'2026-06-12 17:16:19','2026-06-12 17:16:19',NULL),(121,'SelectGallery97',NULL,'SelectGallery97 - Professional Gallery','merchant_97','$2a$10$TjDXReh6WLIVIBT4hc5wFuKv590r6Oc2r.EUaX1qLkWZNPez0nRiq','Lin','18614327869','merchant_97@example.com',NULL,1,1,0,'2026-06-12 17:16:19','2026-06-12 17:16:19',NULL),(122,'ValuePlace98',NULL,'ValuePlace98 - Professional Place','merchant_98','$2a$10$vaWPiOr3JP.CidLqjs9gSe.vv7rJrfU8UJOPzkcB62v3cAvvR1Mmm','Zhou','18251403796','merchant_98@example.com',NULL,1,1,0,'2026-06-12 17:16:19','2026-06-12 17:16:19',NULL),(123,'BestShop99',NULL,'BestShop99 - Professional Shop','merchant_99','$2a$10$q3yUgZgX9azhBfeBDT5aluo8sM/LcepqeIbOBr4lQSTmUg3ZdZBci','Zhang','15256307824','merchant_99@example.com',NULL,1,1,0,'2026-06-12 17:16:19','2026-06-12 17:16:19',NULL),(124,'TopEmporium100',NULL,'TopEmporium100 - Professional Emporium','merchant_100','$2a$10$FU6CKZmuLoROpoXeGLFDb.pV1laxm95pvgvdwEBtIaed18z.K7d3G','Wang','17896178320','merchant_100@example.com',NULL,1,1,0,'2026-06-12 17:16:19','2026-06-12 17:16:19',NULL);
/*!40000 ALTER TABLE `term_merchants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `term_product_categories`
--

DROP TABLE IF EXISTS `term_product_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `term_product_categories` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID(主键)',
  `parent_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '父分类ID(0=顶级分类)',
  `category_name` varchar(64) NOT NULL COMMENT '分类名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标URL(前端展示专用)',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序权重(数字越大越靠前)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '分类状态:0-禁用,1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted_at` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品独立分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `term_product_categories`
--

LOCK TABLES `term_product_categories` WRITE;
/*!40000 ALTER TABLE `term_product_categories` DISABLE KEYS */;
INSERT INTO `term_product_categories` VALUES (1,0,'手机','https://xxx.com/icon/phone.png',1,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(2,0,'电脑','https://xxx.com/icon/computer.png',2,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(3,0,'电器','https://xxx.com/icon/electric.png',3,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(4,0,'运动','https://xxx.com/icon/sports.png',4,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(5,1,'智能手机','https://xxx.com/icon/smart_phone.png',1,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(6,1,'老人机','https://xxx.com/icon/elder_phone.png',2,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(7,1,'折叠屏手机','https://xxx.com/icon/fold_phone.png',3,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(8,1,'游戏手机','https://xxx.com/icon/game_phone.png',4,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(9,1,'儿童手表','https://xxx.com/icon/kid_watch.png',5,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(10,2,'笔记本电脑','https://xxx.com/icon/laptop.png',1,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(11,2,'台式机','https://xxx.com/icon/desktop.png',2,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(12,2,'平板电脑','https://xxx.com/icon/tablet.png',3,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(13,2,'一体机','https://xxx.com/icon/aio.png',4,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(14,2,'迷你主机','https://xxx.com/icon/mini_pc.png',5,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(15,3,'冰箱','https://xxx.com/icon/fridge.png',1,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(16,3,'洗衣机','https://xxx.com/icon/washing.png',2,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(17,3,'空调','https://xxx.com/icon/air.png',3,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(18,3,'电视','https://xxx.com/icon/tv.png',4,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(19,3,'小家电','https://xxx.com/icon/small_electric.png',5,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(20,4,'运动鞋','https://xxx.com/icon/sport_shoes.png',1,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(21,4,'运动服','https://xxx.com/icon/sport_clothes.png',2,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(22,4,'健身器材','https://xxx.com/icon/fitness.png',3,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(23,4,'户外装备','https://xxx.com/icon/outdoor.png',4,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL),(24,4,'球类用品','https://xxx.com/icon/ball.png',5,1,'2026-06-11 23:28:17','2026-06-11 23:28:17',NULL);
/*!40000 ALTER TABLE `term_product_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `term_product_tags`
--

DROP TABLE IF EXISTS `term_product_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `term_product_tags` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `tag_name` varchar(64) NOT NULL COMMENT '标签名称',
  `tag_color` varchar(16) DEFAULT '#333333' COMMENT '标签颜色',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `term_product_tags`
--

LOCK TABLES `term_product_tags` WRITE;
/*!40000 ALTER TABLE `term_product_tags` DISABLE KEYS */;
INSERT INTO `term_product_tags` VALUES (67,60,'数码配件','#3498DB','2026-06-12 20:09:01'),(68,60,'新品','#3498DB','2026-06-12 20:09:01'),(69,61,'外设','#3498DB','2026-06-12 20:09:01'),(70,61,'游戏','#3498DB','2026-06-12 20:09:01'),(71,61,'热销','#3498DB','2026-06-12 20:09:01'),(72,62,'数码配件','#3498DB','2026-06-12 20:09:01'),(73,62,'热销','#3498DB','2026-06-12 20:09:01'),(74,63,'显示器','#3498DB','2026-06-12 20:09:02'),(75,63,'新品','#3498DB','2026-06-12 20:09:02'),(76,64,'外设','#3498DB','2026-06-12 20:09:02'),(77,64,'游戏','#3498DB','2026-06-12 20:09:02'),(82,68,'数码配件','#3498DB','2026-06-12 20:09:03'),(83,68,'新品','#3498DB','2026-06-12 20:09:03'),(84,59,'智能穿戴','#409eff','2026-06-12 20:13:12'),(85,59,'热销','#f56c6c','2026-06-12 20:13:12'),(86,59,'新品','#67c23a','2026-06-12 20:13:12'),(87,59,'升级版','#909399','2026-06-12 20:13:12'),(88,65,'音频设备','#3498DB','2026-06-12 20:14:38'),(89,65,'热销','#3498DB','2026-06-12 20:14:38');
/*!40000 ALTER TABLE `term_product_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `term_products`
--

DROP TABLE IF EXISTS `term_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `term_products` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '商品ID(主键)',
  `merchant_id` bigint unsigned NOT NULL COMMENT '商家ID(关联商家表)',
  `product_name` varchar(255) NOT NULL COMMENT '商品名称',
  `product_code` varchar(64) DEFAULT NULL COMMENT '商品编码(SKU编码)',
  `category_id` bigint unsigned NOT NULL COMMENT '商品分类ID',
  `price` decimal(10,2) NOT NULL COMMENT '商品售价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '商品原价(划线价)',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `sales` int NOT NULL DEFAULT '0' COMMENT '销量',
  `main_image` varchar(512) NOT NULL COMMENT '主图URL(列表页展示)',
  `brief` varchar(512) DEFAULT NULL COMMENT '商品简介(列表页副标题)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '商品状态: 0-下架, 1-上架, 2-售罄',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序权重(数字越大越靠前)',
  `is_hot` tinyint NOT NULL DEFAULT '0' COMMENT '是否热门: 0-否, 1-是',
  `is_new` tinyint NOT NULL DEFAULT '0' COMMENT '是否新品: 0-否, 1-是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `product_code` (`product_code`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`),
  KEY `idx_deleted_at` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `term_products`
--

LOCK TABLES `term_products` WRITE;
/*!40000 ALTER TABLE `term_products` DISABLE KEYS */;
INSERT INTO `term_products` VALUES (4,1,'华为Mate60 Pro','SKUTEST001',1,6999.00,NULL,100,0,'https://example.com/huawei.jpg','国产旗舰手机',1,0,0,0,'2026-06-12 17:25:10','2026-06-12 17:25:10',NULL),(5,1,'??????15 Pro','SKU202606121725596427',1,8999.00,NULL,50,0,'https://example.com/iphone.jpg','?????????????17???',1,0,1,1,'2026-06-12 17:26:00','2026-06-12 17:26:00',NULL),(6,1,'??????15 Pro','SKU202606121726584145',1,8999.00,NULL,50,0,'https://example.com/iphone.jpg','?????????????17???',1,0,1,1,'2026-06-12 17:26:58','2026-06-12 17:26:58',NULL),(7,1,'荣耀Magic6 Pro','SKU202606121730279991',1,5499.00,NULL,70,0,'https://example.com/honor.jpg','荣耀旗舰手机',1,0,1,1,'2026-06-12 17:30:28','2026-06-12 17:30:28',NULL),(37,1,'????','SKU202606121758231916',1,99.99,NULL,100,0,'https://example.com/test.jpg',NULL,1,0,0,0,'2026-06-12 17:58:24','2026-06-12 17:58:24',NULL),(59,24,'智能手表 Pro Max 升级版','SKU202606122009014575',1,1499.00,NULL,200,0,'https://example.com/watch-updated.jpg','最新款智能手表，升级配置',1,0,0,0,'2026-06-12 20:09:01','2026-06-12 20:09:01',NULL),(60,24,'无线蓝牙耳机','SKU202606122009015842',1,299.00,NULL,100,0,'https://example.com/product2.jpg',NULL,1,0,0,0,'2026-06-12 20:09:01','2026-06-12 20:09:01',NULL),(61,24,'机械键盘 RGB','SKU202606122009016123',1,599.00,NULL,100,0,'https://example.com/product3.jpg',NULL,1,0,0,0,'2026-06-12 20:09:02','2026-06-12 20:09:02',NULL),(62,24,'便携充电宝 20000mAh','SKU202606122009015288',1,159.00,NULL,100,0,'https://example.com/product4.jpg',NULL,1,0,0,0,'2026-06-12 20:09:02','2026-06-12 20:09:02',NULL),(63,24,'4K显示器 27英寸','SKU202606122009023087',1,1999.00,NULL,100,0,'https://example.com/product5.jpg',NULL,1,0,0,0,'2026-06-12 20:09:02','2026-06-12 20:09:02',NULL),(64,24,'游戏鼠标 无线','SKU202606122009021768',1,399.00,NULL,100,0,'https://example.com/product6.jpg',NULL,1,0,0,0,'2026-06-12 20:09:02','2026-06-12 20:09:02',NULL),(65,24,'降噪耳机1','SKU202606122009026703',1,799.00,NULL,100,0,'https://example.com/product7.jpg',NULL,0,0,0,0,'2026-06-12 20:09:03','2026-06-12 20:09:03',NULL),(66,24,'USB-C扩展坞','SKU202606122009027329',1,259.00,NULL,100,0,'https://example.com/product8.jpg',NULL,1,0,0,0,'2026-06-12 20:09:03','2026-06-12 20:21:12','2026-06-12 20:21:12'),(67,24,'机械硬盘 2TB','SKU202606122009030212',1,499.00,NULL,100,0,'https://example.com/product9.jpg',NULL,1,0,0,0,'2026-06-12 20:09:03','2026-06-12 20:16:47','2026-06-12 20:16:47'),(68,24,'无线充电器','SKU202606122009035464',1,89.00,NULL,100,0,'https://example.com/product10.jpg',NULL,0,0,0,0,'2026-06-12 20:09:03','2026-06-12 20:09:03',NULL);
/*!40000 ALTER TABLE `term_products` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-13 14:11:56
