/*
 Navicat Premium Data Transfer

 Source Server         : mms
 Source Server Type    : SQLite
 Source Server Version : 3030001
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3030001
 File Encoding         : 65001

 Date: 29/12/2023 10:02:25
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for mms_files
-- ----------------------------
DROP TABLE IF EXISTS "mms_files";
CREATE TABLE "mms_files" (
  "id" bigint NOT NULL,
  "pid" bigint,
  "md5" text,
  "category" text,
  "name" text,
  "path" text,
  "root_path" text,
  "absolute_path" text,
  "type" text,
  "type_sort" integer,
  "size" bigint,
  "level" integer,
  "last_modified" text,
  PRIMARY KEY ("id")
);

PRAGMA foreign_keys = true;
