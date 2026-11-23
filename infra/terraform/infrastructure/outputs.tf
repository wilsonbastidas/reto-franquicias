
output "vpc_id" {
  description = "ID of the main VPC"
  value       = module.network.vpc_id
}

output "public_subnet_ids" {
  description = "IDs of public subnets"
  value       = module.network.public_subnet_ids
}

output "private_subnet_ids" {
  description = "IDs of private subnets"
  value       = module.network.private_subnet_ids
}

output "vpc_cidr" {
  description = "CIDR block of the main VPC"
  value       = module.network.vpc_cidr
}

output "ecr_repository_name" {
  description = "Name of the ECR repository"
  value       = module.ecr.repository_name
}

output "ecr_repository_arn" {
  description = "ARN of the ECR repository"
  value       = module.ecr.repository_arn
}

output "ecr_repository_url" {
  description = "URL of the ECR repository"
  value       = module.ecr.repository_url
}

output "ecs_execution_role_name" {
  description = "Name of the ECS task execution role"
  value       = module.iam.ecs_execution_role_name
}

output "ecs_execution_role_arn" {
  description = "ARN of the ECS task execution role"
  value       = module.iam.ecs_execution_role_arn
}

output "ecs_task_role_name" {
  description = "Name of the ECS task role"
  value       = module.iam.ecs_task_role_name
}

output "ecs_task_role_arn" {
  description = "ARN of the ECS task role"
  value       = module.iam.ecs_task_role_arn
}

output "database_secret_arn" {
  description = "ARN of the database secret"
  value       = module.secrets.database_secret_arn
}

output "database_secret_name" {
  description = "Name of the database secret"
  value       = module.secrets.database_secret_name
}

output "alb_arn" {
  description = "ARN of the Application Load Balancer"
  value       = module.alb.alb_arn
}

output "alb_dns_name" {
  description = "DNS name of the Application Load Balancer"
  value       = module.alb.alb_dns_name
}

output "alb_zone_id" {
  description = "Hosted zone ID of the Application Load Balancer"
  value       = module.alb.alb_zone_id
}

output "alb_security_group_id" {
  description = "Security Group ID of the Application Load Balancer"
  value       = module.alb.alb_security_group_id
}

output "target_group_arn" {
  description = "ARN of the Target Group"
  value       = module.alb.target_group_arn
}


output "db_instance_identifier" {
  description = "RDS instance identifier"
  value       = module.rds.db_instance_identifier
}

output "db_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value       = module.rds.db_endpoint
}

output "db_port" {
  description = "PostgreSQL port"
  value       = module.rds.db_port
}

output "db_name" {
  description = "PostgreSQL database name"
  value       = module.rds.db_name
}

output "db_security_group_id" {
  description = "RDS security group ID"
  value       = module.rds.db_security_group_id
}

# RDS password is stored in the app-aligned secret (database_secret_arn).

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = module.ecs.cluster_name
}

output "ecs_service_name" {
  description = "ECS service name"
  value       = module.ecs.service_name
}

output "ecs_tasks_security_group_id" {
  description = "Security group ID for ECS tasks"
  value       = module.ecs.ecs_tasks_security_group_id
}

output "ecs_log_group_name" {
  description = "CloudWatch log group for ECS"
  value       = module.ecs.log_group_name
}

output "api_gateway_id" {
  description = "API Gateway HTTP API ID"
  value       = module.api_gateway.api_id
}

output "api_gateway_endpoint" {
  description = "Base URL of the API Gateway"
  value       = module.api_gateway.api_endpoint
}
