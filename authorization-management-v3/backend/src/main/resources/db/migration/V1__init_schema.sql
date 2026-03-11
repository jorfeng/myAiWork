-- V1__init_schema.sql
-- Authorization Letter Management System V3 - Initial Schema

-- Organization Level Enum
CREATE TYPE org_level AS ENUM ('ORGANIZATION', 'REGIONAL_DEPT', 'REPRESENTATIVE_OFFICE', 'OFFICE');

-- Authorization Letter Status Enum
CREATE TYPE auth_letter_status AS ENUM ('DRAFT', 'PUBLISHED', 'EXPIRED');

-- Logic Operator Enum
CREATE TYPE logic_operator AS ENUM ('AND', 'OR');

-- Authorization Letter Table
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
    created_by VARCHAR(100),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_letter IS 'Authorization Letter Table';
COMMENT ON COLUMN auth_letter.code IS 'Unique authorization letter code';
COMMENT ON COLUMN auth_letter.name IS 'Authorization letter name';
COMMENT ON COLUMN auth_letter.status IS 'Status: DRAFT, PUBLISHED, EXPIRED';
COMMENT ON COLUMN auth_letter.publish_level IS 'Publish level: ORGANIZATION, REGIONAL_DEPT, REPRESENTATIVE_OFFICE, OFFICE';
COMMENT ON COLUMN auth_letter.authorized_level IS 'Authorized level';
COMMENT ON COLUMN auth_letter.auth_target_level IS 'Authorization target level codes (JSON array)';
COMMENT ON COLUMN auth_letter.applicable_region IS 'Applicable region codes (JSON array)';
COMMENT ON COLUMN auth_letter.auth_publish_level IS 'Authorization publish level codes (JSON array)';
COMMENT ON COLUMN auth_letter.auth_publish_org IS 'Authorization publish organization codes (JSON array)';
COMMENT ON COLUMN auth_letter.publish_year IS 'Authorization letter publish year';
COMMENT ON COLUMN auth_letter.created_by IS 'Creator username';
COMMENT ON COLUMN auth_letter.valid_from IS 'Valid from date';
COMMENT ON COLUMN auth_letter.valid_to IS 'Valid to date';

-- Create indexes for query performance
CREATE INDEX idx_auth_letter_status ON auth_letter(status);
CREATE INDEX idx_auth_letter_publish_year ON auth_letter(publish_year);
CREATE INDEX idx_auth_letter_created_by ON auth_letter(created_by);

-- Scene Table
CREATE TABLE scene (
    id BIGSERIAL PRIMARY KEY,
    auth_letter_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    decision_level INTEGER,
    order_index INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scene_auth_letter FOREIGN KEY (auth_letter_id) REFERENCES auth_letter(id) ON DELETE CASCADE
);

COMMENT ON TABLE scene IS 'Scene Table - A scenario under an authorization letter';
COMMENT ON COLUMN scene.decision_level IS 'Decision level for this scene';
COMMENT ON COLUMN scene.order_index IS 'Order index for matching priority';

CREATE INDEX idx_scene_auth_letter_id ON scene(auth_letter_id);

-- Rule Table
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

COMMENT ON TABLE rule IS 'Rule Table - A rule under a scene';

CREATE INDEX idx_rule_scene_id ON rule(scene_id);

-- Condition Table
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

COMMENT ON TABLE condition IS 'Condition Table - Supports nested conditions';
COMMENT ON COLUMN condition.parent_id IS 'Parent condition ID for nested conditions';
COMMENT ON COLUMN condition.logic_operator IS 'Logic operator: AND, OR';
COMMENT ON COLUMN condition.field_name IS 'Field name for simple condition';
COMMENT ON COLUMN condition.operator IS 'Operator: EQ, NE, GT, GE, LT, LE, IN, NOT_IN, LIKE';
COMMENT ON COLUMN condition.value IS 'Value for simple condition';

CREATE INDEX idx_condition_rule_id ON condition(rule_id);
CREATE INDEX idx_condition_parent_id ON condition(parent_id);

-- Updated_at trigger function
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