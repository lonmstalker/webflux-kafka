-- changeset nkochnev:1 logicalFilePath:db/migrations/v1/changelog_1.sql
CREATE EXTENSION "uuid-ossp";

CREATE TABLE test_models
(
    id           UUID DEFAULT uuid_generate_v1() PRIMARY KEY,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    version      INT       NOT NULL
);
-- rollback DROP TABLE test_models; DROP EXTENSION "uuid-ossp";