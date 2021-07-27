CREATE TABLE IF NOT EXISTS vets (
  id         SMALLSERIAL PRIMARY KEY,
  first_name TEXT        NOT NULL,
  last_name  TEXT        NOT NULL
);

CREATE INDEX IF NOT EXISTS vets_last_name ON vets (last_name);

CREATE TABLE IF NOT EXISTS specialties (
  id SMALLSERIAL PRIMARY KEY,
  name           TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS specialties_name ON specialties (name);

CREATE TABLE IF NOT EXISTS vet_specialties (
  vet_id       SMALLINT NOT NULL,
  specialty_id SMALLINT NOT NULL,
  FOREIGN KEY (vet_id) REFERENCES vets(id),
  FOREIGN KEY (specialty_id) REFERENCES specialties(id),
  CONSTRAINT vet_specialties_unique UNIQUE (vet_id, specialty_id)
);

CREATE TABLE IF NOT EXISTS types (
  id SMALLSERIAL PRIMARY KEY,
  name           TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS types_name ON specialties (name);

CREATE TABLE IF NOT EXISTS owners (
  id SMALLSERIAL PRIMARY KEY,
  first_name     TEXT NOT NULL,
  last_name      TEXT NOT NULL,
  address        TEXT,
  city           TEXT,
  telephone      TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS owners_last_name ON owners (last_name);

CREATE TABLE IF NOT EXISTS pets (
  id SMALLSERIAL PRIMARY KEY,
  name           TEXT     NOT NULL,
  birth_date     DATE     NOT NULL,
  type_id        SMALLINT NOT NULL,
  owner_id       SMALLINT NOT NULL,
  FOREIGN KEY (owner_id) REFERENCES owners(id),
  FOREIGN KEY (type_id)  REFERENCES types(id)
);

CREATE INDEX IF NOT EXISTS pets_name ON specialties (name);

CREATE TABLE IF NOT EXISTS visits (
  id          SMALLSERIAL PRIMARY KEY,
  pet_id      SMALLINT    NOT NULL,
  visit_date  DATE        NOT NULL,
  description TEXT        NOT NULL,
  FOREIGN KEY (pet_id) REFERENCES pets(id)
);