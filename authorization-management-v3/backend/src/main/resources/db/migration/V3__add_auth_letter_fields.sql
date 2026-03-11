-- V3__add_auth_letter_fields.sql
-- Add additional fields to auth_letter and scene tables

-- Add updated_by column to auth_letter
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'auth_letter') THEN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'auth_letter' AND column_name = 'updated_by') THEN
            ALTER TABLE auth_letter ADD COLUMN updated_by VARCHAR(100);
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'auth_letter' AND column_name = 'content_summary') THEN
            ALTER TABLE auth_letter ADD COLUMN content_summary TEXT;
        END IF;
    END IF;
END $$;

COMMENT ON COLUMN auth_letter.updated_by IS '更新人';
COMMENT ON COLUMN auth_letter.content_summary IS '授权书内容摘要';

-- Add additional fields to scene table
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'scene') THEN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'scene' AND column_name = 'industry') THEN
            ALTER TABLE scene ADD COLUMN industry VARCHAR(500);
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'scene' AND column_name = 'business_scenario') THEN
            ALTER TABLE scene ADD COLUMN business_scenario VARCHAR(100);
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'scene' AND column_name = 'decision_level_code') THEN
            ALTER TABLE scene ADD COLUMN decision_level_code VARCHAR(50);
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'scene' AND column_name = 'rule_detail') THEN
            ALTER TABLE scene ADD COLUMN rule_detail TEXT;
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'scene' AND column_name = 'conditions') THEN
            ALTER TABLE scene ADD COLUMN conditions JSONB;
        END IF;
    END IF;
END $$;

COMMENT ON COLUMN scene.industry IS '产业代码（JSON数组）';
COMMENT ON COLUMN scene.business_scenario IS '业务场景代码';
COMMENT ON COLUMN scene.decision_level_code IS '决策层级代码';
COMMENT ON COLUMN scene.rule_detail IS '具体规则描述';
COMMENT ON COLUMN scene.conditions IS '条件配置（JSON格式）';

-- Create trigger function for updated_at if not exists
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add trigger for rule_param table
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'rule_param') THEN
        IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'update_rule_param_updated_at') THEN
            CREATE TRIGGER update_rule_param_updated_at
                BEFORE UPDATE ON rule_param
                FOR EACH ROW
                EXECUTE FUNCTION update_updated_at_column();
        END IF;
    END IF;
END $$;