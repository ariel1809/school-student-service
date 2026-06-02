-- Student service schema

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE classrooms (
    id                  uuid PRIMARY KEY,
    code                VARCHAR(32) NOT NULL,
    name                VARCHAR(100) NOT NULL,
    academic_year_id    uuid NOT NULL,
    level_id            uuid NOT NULL,
    track_id            uuid,
    main_teacher_id     uuid,
    capacity            INT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by          uuid,
    updated_at          TIMESTAMPTZ,
    updated_by          uuid,
    version             BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_classrooms_code_year UNIQUE (code, academic_year_id)
);
CREATE INDEX idx_classrooms_year ON classrooms (academic_year_id);

CREATE TABLE students (
    id                      uuid PRIMARY KEY,
    matricule               VARCHAR(32) NOT NULL,
    photo_url               VARCHAR(500),
    first_name              VARCHAR(100) NOT NULL,
    last_name               VARCHAR(100) NOT NULL,
    gender                  VARCHAR(10) NOT NULL,
    date_of_birth           DATE NOT NULL,
    place_of_birth          VARCHAR(200),
    nationality             VARCHAR(100),
    address                 VARCHAR(500),
    city                    VARCHAR(100),
    phone                   VARCHAR(50),
    email                   VARCHAR(255),
    current_classroom_id    uuid REFERENCES classrooms(id) ON DELETE SET NULL,
    status                  VARCHAR(20) NOT NULL,
    notes                   TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by              uuid,
    updated_at              TIMESTAMPTZ,
    updated_by              uuid,
    version                 BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_students_matricule UNIQUE (matricule)
);
CREATE INDEX idx_students_classroom ON students (current_classroom_id);
CREATE INDEX idx_students_status ON students (status);
CREATE INDEX idx_students_name ON students (lower(last_name), lower(first_name));

CREATE TABLE guardians (
    id              uuid PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(50),
    email           VARCHAR(255),
    address         VARCHAR(500),
    profession      VARCHAR(200),
    employer        VARCHAR(200),
    national_id     VARCHAR(50),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      uuid,
    updated_at      TIMESTAMPTZ,
    updated_by      uuid,
    version         BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_guardians_email ON guardians (lower(email));
CREATE INDEX idx_guardians_phone ON guardians (phone);

CREATE TABLE student_guardians (
    id                  uuid PRIMARY KEY,
    student_id          uuid NOT NULL REFERENCES students(id)  ON DELETE CASCADE,
    guardian_id         uuid NOT NULL REFERENCES guardians(id) ON DELETE RESTRICT,
    relationship        VARCHAR(20) NOT NULL,
    is_primary          BOOLEAN NOT NULL DEFAULT FALSE,
    emergency_contact   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by          uuid,
    updated_at          TIMESTAMPTZ,
    updated_by          uuid,
    version             BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_student_guardian_pair UNIQUE (student_id, guardian_id, relationship)
);
CREATE INDEX idx_student_guardians_student ON student_guardians (student_id);
CREATE INDEX idx_student_guardians_guardian ON student_guardians (guardian_id);

CREATE TABLE student_documents (
    id              uuid PRIMARY KEY,
    student_id      uuid NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    type            VARCHAR(30) NOT NULL,
    label           VARCHAR(200),
    file_url        VARCHAR(1000) NOT NULL,
    content_type    VARCHAR(100),
    size_bytes      BIGINT,
    verified        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      uuid,
    updated_at      TIMESTAMPTZ,
    updated_by      uuid,
    version         BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_student_documents_student ON student_documents (student_id);

CREATE TABLE matricule_sequences (
    year_code   VARCHAR(16) PRIMARY KEY,
    last_value  BIGINT NOT NULL DEFAULT 0
);
