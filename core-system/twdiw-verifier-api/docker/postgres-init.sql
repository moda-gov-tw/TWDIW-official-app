-- PostgreSQL 初始化腳本
-- 此腳本會在第一次啟動容器時執行

-- 建立 schema（如果不存在）
CREATE SCHEMA IF NOT EXISTS vp_manager;

-- 設定搜尋路徑
ALTER DATABASE twdiw_verifier SET search_path TO vp_manager, public;

-- 賦予使用者權限
GRANT ALL PRIVILEGES ON SCHEMA vp_manager TO twdiw_verifier;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA vp_manager TO twdiw_verifier;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA vp_manager TO twdiw_verifier;

-- 設定預設權限（未來建立的物件也會有權限）
ALTER DEFAULT PRIVILEGES IN SCHEMA vp_manager GRANT ALL ON TABLES TO twdiw_verifier;
ALTER DEFAULT PRIVILEGES IN SCHEMA vp_manager GRANT ALL ON SEQUENCES TO twdiw_verifier;
