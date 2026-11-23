
module "network" {
  source = "./modules/network"

  project_name = var.project_name
  environment  = var.environment
  vpc_cidr     = var.vpc_cidr
}

module "ecr" {
  source = "./modules/ecr"

  project_name = var.project_name
  environment  = var.environment
}

module "secrets" {
  source = "./modules/secrets"

  project_name = var.project_name
  environment  = var.environment
  secret_name  = var.app_db_secret_name
}

module "iam" {
  source = "./modules/iam"

  project_name        = var.project_name
  environment         = var.environment
  database_secret_arn = module.secrets.database_secret_arn
}

module "alb" {
  source = "./modules/alb"

  project_name       = var.project_name
  environment        = var.environment
  vpc_id             = module.network.vpc_id
  vpc_cidr           = module.network.vpc_cidr
  private_subnet_ids = module.network.private_subnet_ids
  container_port     = 8080
}

module "ecs" {
  source = "./modules/ecs"

  project_name = var.project_name
  environment  = var.environment
  aws_region   = var.aws_region

  vpc_id     = module.network.vpc_id
  subnet_ids = module.network.public_subnet_ids

  alb_target_group_arn  = module.alb.target_group_arn
  alb_security_group_id = module.alb.alb_security_group_id

  ecr_repository_url = module.ecr.repository_url
  execution_role_arn = module.iam.ecs_execution_role_arn
  task_role_arn      = module.iam.ecs_task_role_arn

  app_db_secret_name   = var.app_db_secret_name
  cors_allowed_origins = var.cors_allowed_origins
}

resource "random_password" "db_master" {
  length           = 20
  special          = true
  override_special = "!#$&*()-_=+[]{}<>:?"
}

module "rds" {
  source = "./modules/rds"

  project_name = var.project_name
  environment  = var.environment

  vpc_id                = module.network.vpc_id
  subnet_ids            = module.network.public_subnet_ids
  ecs_security_group_id = module.ecs.ecs_tasks_security_group_id

  database_name   = var.database_name
  master_username = var.database_master_username
  master_password = random_password.db_master.result

  instance_class    = var.database_instance_class
  allocated_storage = var.database_allocated_storage
}

# App-aligned secret version (format expected by PostgresqlConnectionProperties)
resource "aws_secretsmanager_secret_version" "app_database" {
  secret_id = module.secrets.database_secret_arn

  secret_string = jsonencode({
    host     = module.rds.db_address
    port     = module.rds.db_port
    database = module.rds.db_name
    schema   = "public"
    username = var.database_master_username
    password = random_password.db_master.result
  })
}

module "api_gateway" {
  source = "./modules/api_gateway"

  project_name = var.project_name
  environment  = var.environment

  vpc_id                = module.network.vpc_id
  private_subnet_ids    = module.network.private_subnet_ids
  alb_listener_arn      = module.alb.alb_listener_arn
  alb_security_group_id = module.alb.alb_security_group_id
}
