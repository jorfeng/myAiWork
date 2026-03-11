-- V1__init_schema.sql
-- Authorization Letter Management System V3 - Initial Schema

-- =============================================
-- Authorization Letter Table
-- =============================================
CREATE TABLE auth_letter (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    publish_level VARCHAR(30),
    authorized_level VARCHAR(30),
    auth_target_level VARCHAR(500),
    applicable_region VARCHAR(500),
    auth_publish_level VARCHAR(500),
    auth_publish_org VARCHAR(1000),
    publish_year INTEGER,
    content_summary TEXT,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_letter IS '授权书表';
COMMENT ON COLUMN auth_letter.code IS '授权书编码';
COMMENT ON COLUMN auth_letter.name IS '授权书名称';
COMMENT ON COLUMN auth_letter.status IS '状态: DRAFT-草稿, PUBLISHED-已发布, EXPIRED-已失效';
COMMENT ON COLUMN auth_letter.publish_level IS '发布层级';
COMMENT ON COLUMN auth_letter.authorized_level IS '授权层级';
COMMENT ON COLUMN auth_letter.auth_target_level IS '授权对象层级编码(JSON数组)';
COMMENT ON COLUMN auth_letter.applicable_region IS '适用区域编码(JSON数组)';
COMMENT ON COLUMN auth_letter.auth_publish_level IS '授权发布层级编码(JSON数组)';
COMMENT ON COLUMN auth_letter.auth_publish_org IS '授权发布组织编码(JSON数组)';
COMMENT ON COLUMN auth_letter.publish_year IS '发布年份';
COMMENT ON COLUMN auth_letter.content_summary IS '内容摘要';
COMMENT ON COLUMN auth_letter.created_by IS '创建人';
COMMENT ON COLUMN auth_letter.updated_by IS '更新人';
COMMENT ON COLUMN auth_letter.valid_from IS '生效时间';
COMMENT ON COLUMN auth_letter.valid_to IS '失效时间';
COMMENT ON COLUMN auth_letter.published_at IS '发布时间';

CREATE INDEX idx_auth_letter_status ON auth_letter(status);
CREATE INDEX idx_auth_letter_publish_year ON auth_letter(publish_year);
CREATE INDEX idx_auth_letter_created_by ON auth_letter(created_by);

-- =============================================
-- Scene Table (场景表)
-- =============================================
CREATE TABLE scene (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    decision_level INTEGER,
    order_index INTEGER DEFAULT 0,
    industry VARCHAR(500),
    business_scenario VARCHAR(100),
    decision_level_code VARCHAR(50),
    rule_detail TEXT,
    conditions JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scene_auth_letter FOREIGN KEY (auth_letter_id) REFERENCES auth_letter(id) ON DELETE CASCADE
);

COMMENT ON TABLE scene IS '场景表 - 授权书下的业务场景';
COMMENT ON COLUMN scene.auth_letter_id IS '关联授权书ID';
COMMENT ON COLUMN scene.name IS '场景名称';
COMMENT ON COLUMN scene.description IS '场景描述';
COMMENT ON COLUMN scene.decision_level IS '决策层级(旧字段)';
COMMENT ON COLUMN scene.order_index IS '排序索引';
COMMENT ON COLUMN scene.industry IS '产业代码(JSON数组)';
COMMENT ON COLUMN scene.business_scenario IS '业务场景代码';
COMMENT ON COLUMN scene.decision_level_code IS '决策层级代码';
COMMENT ON COLUMN scene.rule_detail IS '具体规则描述';
COMMENT ON COLUMN scene.conditions IS '条件配置(JSON格式)';

CREATE INDEX idx_scene_auth_letter_id ON scene(auth_letter_id);

-- =============================================
-- Rule Table (规则表)
-- =============================================
CREATE TABLE rule (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    order_index INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rule_scene FOREIGN KEY (scene_id) REFERENCES scene(id) ON DELETE CASCADE
);

COMMENT ON TABLE rule IS '规则表 - 场景下的规则';
COMMENT ON COLUMN rule.scene_id IS '关联场景ID';
COMMENT ON COLUMN rule.name IS '规则名称';
COMMENT ON COLUMN rule.description IS '规则描述';
COMMENT ON COLUMN rule.order_index IS '排序索引';

CREATE INDEX idx_rule_scene_id ON rule(scene_id);

-- =============================================
-- Condition Table (条件表)
-- =============================================
CREATE TABLE condition (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT,
    parent_id BIGINT,
    logic_operator VARCHAR(10),
    field_name VARCHAR(100),
    operator VARCHAR(20),
    value VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_condition_rule FOREIGN KEY (rule_id) REFERENCES rule(id) ON DELETE CASCADE,
    CONSTRAINT fk_condition_parent FOREIGN KEY (parent_id) REFERENCES condition(id) ON DELETE CASCADE
);

COMMENT ON TABLE condition IS '条件表 - 支持嵌套条件';
COMMENT ON COLUMN condition.rule_id IS '关联规则ID';
COMMENT ON COLUMN condition.parent_id IS '父条件ID(用于嵌套)';
COMMENT ON COLUMN condition.logic_operator IS '逻辑运算符: AND, OR';
COMMENT ON COLUMN condition.field_name IS '字段名称';
COMMENT ON COLUMN condition.operator IS '操作符: EQ, NE, GT, GE, LT, LE, IN, NOT_IN, LIKE';
COMMENT ON COLUMN condition.value IS '条件值';

CREATE INDEX idx_condition_rule_id ON condition(rule_id);
CREATE INDEX idx_condition_parent_id ON condition(parent_id);

-- =============================================
-- Rule Parameter Table (规则参数表)
-- =============================================
CREATE TABLE rule_param (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    data_type VARCHAR(30) NOT NULL,
    business_mappings TEXT,
    created_by VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE rule_param IS '规则参数表 - 用于规则字段配置';
COMMENT ON COLUMN rule_param.name IS '名称';
COMMENT ON COLUMN rule_param.name_en IS '名称英文(唯一)';
COMMENT ON COLUMN rule_param.status IS '状态: ACTIVE-生效, INACTIVE-失效';
COMMENT ON COLUMN rule_param.data_type IS '数据类型: TEXT-文本, NUMBER-数值, COMPARE_FIELD-比对字段, RATIO-比率';
COMMENT ON COLUMN rule_param.business_mappings IS '业务对象与取值逻辑映射(JSON格式)';
COMMENT ON COLUMN rule_param.created_by IS '创建人';
COMMENT ON COLUMN rule_param.updated_by IS '更新人';

CREATE INDEX idx_rule_param_status ON rule_param(status);
CREATE INDEX idx_rule_param_name ON rule_param(name);
CREATE INDEX idx_rule_param_name_en ON rule_param(name_en);

-- =============================================
-- Trigger Function for updated_at
-- =============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers for updated_at
CREATE TRIGGER update_auth_letter_updated_at
    BEFORE UPDATE ON auth_letter
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_scene_updated_at
    BEFORE UPDATE ON scene
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_rule_updated_at
    BEFORE UPDATE ON rule
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_rule_param_updated_at
    BEFORE UPDATE ON rule_param
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();