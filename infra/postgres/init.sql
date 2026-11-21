-- 1. Database creation
-- La base de datos "franchises_db" la crea el contenedor de PostgreSQL con la
-- variable POSTGRES_DB (ver docker-compose.yaml), por eso no se usa CREATE DATABASE aquí.

-- 2. Franchises table
CREATE TABLE franchises (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Branches table
CREATE TABLE branches (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      name VARCHAR(150) NOT NULL,
      franchise_id UUID NOT NULL,
      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
      CONSTRAINT fk_branch_franchise
          FOREIGN KEY (franchise_id)
              REFERENCES franchises(id)
              ON DELETE CASCADE,
      CONSTRAINT uq_branch_name_franchise UNIQUE (franchise_id, name)
);

-- 4. Products per branch table
CREATE TABLE products (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      name VARCHAR(150) NOT NULL,
      stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
      branch_id UUID NOT NULL,
      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
      CONSTRAINT fk_product_branch
          FOREIGN KEY (branch_id)
              REFERENCES branches(id)
              ON DELETE CASCADE,
      CONSTRAINT uq_product_name_branch UNIQUE (branch_id, name)
);

-- 5. Indexes
CREATE INDEX idx_branches_franchise_id ON branches(franchise_id);
CREATE INDEX idx_products_branch_stock ON products(branch_id, stock DESC);