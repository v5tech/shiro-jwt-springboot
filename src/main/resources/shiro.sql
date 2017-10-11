
-- ----------------------------
-- Table structure for sys_resource
-- ----------------------------
DROP TABLE IF EXISTS `sys_resource`;
CREATE TABLE `sys_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT '' COMMENT '权限类型：menu、button、url',
  `name` varchar(255) NOT NULL COMMENT '权限名称',
  `permission` varchar(255) NOT NULL COMMENT '权限字符串',
  `icon` varchar(255) DEFAULT NULL,
  `sort` int(11) DEFAULT '0',
  `url` varchar(255) DEFAULT '',
  `description` varchar(255) DEFAULT '' COMMENT '资源描述',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态值',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父ID',
  `create_by` bigint(20) DEFAULT NULL,
  `create_at` datetime NOT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_resource_type` (`type`,`permission`) USING BTREE,
  KEY `create_by` (`create_by`),
  KEY `update_by` (`update_by`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='资源（权限）表';


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `remark` varchar(255) DEFAULT '',
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `parent_id` bigint(20) DEFAULT NULL,
  `create_by` bigint(20) DEFAULT NULL,
  `create_at` datetime NOT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `create_by` (`create_by`),
  KEY `update_by` (`update_by`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for sys_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_resource`;
CREATE TABLE `sys_role_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL,
  `resource_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `role_id` (`role_id`),
  KEY `resource_id` (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile_phone` varchar(255) NOT NULL DEFAULT '' COMMENT '手机号码',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `salt` varchar(255) DEFAULT '' COMMENT '加密混淆字符',
  `signature` varchar(255) DEFAULT NULL COMMENT '个性签名',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别',
  `qq` bigint(20) DEFAULT NULL COMMENT 'QQ号码',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱地址',
  `avatar` varchar(500) DEFAULT '' COMMENT '头像图片路径',
  `province` varchar(50) DEFAULT '' COMMENT '省',
  `city` varchar(50) DEFAULT '' COMMENT '市',
  `reg_ip` varchar(50) DEFAULT NULL COMMENT '注册时IP地址',
  `score` int(10) DEFAULT '0' COMMENT '积分值',
  `status` int(10) DEFAULT '1' COMMENT '状态：0禁用 1正常',
  `create_by` bigint(20) DEFAULT NULL,
  `create_at` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_roles` (`user_id`),
  KEY `fk_role_users` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;



-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', '18966668888', 'super', '超级管理员', 'e10adc3949ba59abbe56e057f20f883e', null, null, null, null, '', null, null, null, null, null, '1', null, '2015-09-28 17:47:18', null, '2015-09-30 17:36:16');
INSERT INTO `sys_user` VALUES ('2', '13988886666', 'admin', '系统管理员A', 'e10adc3949ba59abbe56e057f20f883e', null, null, null, '1234567', 'super@millinch.com', null, null, null, null, null, '1', null, '2015-09-29 17:47:22', null, '2015-09-30 17:32:07');

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '超级管理员', 'superManager', '拥有所有权限', '1', null, '0', '2015-09-01 14:36:16', null, '2016-01-03 22:29:58');
INSERT INTO `sys_role` VALUES ('2', '系统管理员', 'systemManager', '拥有部分权限', '1', null, '0', '2015-08-30 18:03:47', null, '2015-08-30 18:03:47');
INSERT INTO `sys_role` VALUES ('3', '角色1', 'role1', 'nothing 34', '1', null, null, '2015-10-05 18:20:35', null, '2015-10-05 18:35:57');

-- ----------------------------
-- Records of sys_resource
-- ----------------------------
INSERT INTO `sys_resource` VALUES ('1', '菜单', '系统管理', 'system:*', 'fa fa-dashboard', '1', '', '', '1', '0', '0', '2015-07-01 19:33:21', null, '2015-10-09 10:34:05');
INSERT INTO `sys_resource` VALUES ('2', '菜单', '角色管理', 'system:role:*', 'fa fa-male', '12', '/role/config', '', '1', '1', '0', '2015-07-01 19:38:38', null, '2015-07-01 19:38:38');
INSERT INTO `sys_resource` VALUES ('3', '菜单', '密码修改', 'system:password', null, '14', '/user/password/edition', '', '1', '1', '0', '2015-07-01 19:38:51', null, '2015-07-01 19:39:51');
INSERT INTO `sys_resource` VALUES ('4', '菜单', '操作日志', 'system:log:*', null, '15', '/handle/operation/log', '', '1', '1', '0', '2015-07-01 19:40:37', null, '2015-07-01 19:40:37');
INSERT INTO `sys_resource` VALUES ('5', 'URL', '新增角色', 'system:role:create', null, '13', '/role/addition', '', '1', '1', '0', '2015-07-01 19:41:21', null, '2015-10-08 16:45:43');
INSERT INTO `sys_resource` VALUES ('6', '菜单', '用户管理', 'system:admin:*', 'fa fa-users', '11', '/user/config', '', '1', '1', '0', '2015-07-01 19:34:38', null, '2015-07-01 19:34:38');
INSERT INTO `sys_resource` VALUES ('7', 'URL', '新增用户', 'system:admin:create', '', null, '/user/addition', 'bbb', '1', '0', '0', '2015-08-30 18:29:56', null, '2015-10-09 11:33:03');

-- ----------------------------
-- Records of sys_role_resource
-- ----------------------------
INSERT INTO `sys_role_resource` VALUES ('1', '1', '1');
INSERT INTO `sys_role_resource` VALUES ('2', '1', '2');
INSERT INTO `sys_role_resource` VALUES ('3', '1', '3');
INSERT INTO `sys_role_resource` VALUES ('4', '1', '4');
INSERT INTO `sys_role_resource` VALUES ('5', '1', '5');
INSERT INTO `sys_role_resource` VALUES ('6', '1', '6');
INSERT INTO `sys_role_resource` VALUES ('7', '1', '7');
INSERT INTO `sys_role_resource` VALUES ('8', '2', '2');

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1', '1');
INSERT INTO `sys_user_role` VALUES ('2', '2', '2');
