
# Subnet Group: subredes privadas disponibles para RDS.
resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-${var.environment}-db-subnet-group-public"
  subnet_ids = var.subnet_ids

  tags = {
    Name        = "${var.project_name}-${var.environment}-db-subnet-group"
    Project     = var.project_name
    Environment = var.environment
  }
}

# Security Group de PostgreSQL.
resource "aws_security_group" "database" {
  name        = "${var.project_name}-${var.environment}-rds-sg"
  description = "Security group for PostgreSQL RDS"
  vpc_id      = var.vpc_id

  # Sin reglas de entrada inicialmente.
  # Posteriormente permitiremos acceso desde ECS.

  egress {
    description = "Allow outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-rds-sg"
    Project     = var.project_name
    Environment = var.environment
  }
}

# Instancia PostgreSQL.
resource "aws_db_instance" "main" {
  identifier = "${var.project_name}-${var.environment}-postgres"

  engine         = "postgres"
  engine_version = "17"
  instance_class = var.instance_class

  allocated_storage     = var.allocated_storage
  max_allocated_storage = var.allocated_storage
  storage_type          = "gp3"
  storage_encrypted     = true

  db_name  = var.database_name
  username = var.master_username
  password = var.master_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.database.id]

  publicly_accessible = true
  multi_az            = false

  backup_retention_period = 1
  copy_tags_to_snapshot   = true

  auto_minor_version_upgrade = true

  deletion_protection = false
  skip_final_snapshot = true

  tags = {
    Name        = "${var.project_name}-${var.environment}-postgres"
    Project     = var.project_name
    Environment = var.environment
  }
}

# Allow PostgreSQL access from ECS tasks
resource "aws_security_group_rule" "ecs_to_rds" {
  type                     = "ingress"
  description              = "Postgres from ECS tasks"
  from_port                = 5432
  to_port                  = 5432
  protocol                 = "tcp"
  source_security_group_id = var.ecs_security_group_id
  security_group_id        = aws_security_group.database.id
}

# Allow PostgreSQL public access for testing (not recommended for production)
resource "aws_security_group_rule" "public_to_rds" {
  type              = "ingress"
  description       = "Postgres public access for testing"
  from_port         = 5432
  to_port           = 5432
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.database.id
}
