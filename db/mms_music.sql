/*
 Navicat Premium Data Transfer

 Source Server         : mms
 Source Server Type    : SQLite
 Source Server Version : 3030001
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3030001
 File Encoding         : 65001

 Date: 29/12/2023 10:02:36
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for mms_music
-- ----------------------------
DROP TABLE IF EXISTS "mms_music";
CREATE TABLE "mms_music" (
  "id" bigint NOT NULL,
  "file_id" bigint NOT NULL,
  "md5" text,
  "title" text,
  "artist" text,
  "album" text,
  "year" text,
  "language" text,
  "lyrics" text,
  "lyrics_length" integer,
  "track_number" integer,
  "cover" blob,
  "cover_info" text,
  "cover_art" blob,
  "format" text,
  "bitrate" text,
  "track_length" text,
  "organized" integer,
  PRIMARY KEY ("id", "file_id")
);

PRAGMA foreign_keys = true;
