
variable "project_name" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Deployment environment"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID where RDS will be deployed"
  type        = string
}

variable "subnet_ids" {
  description = "Subnet IDs for the RDS subnet group"
  type        = list(string)
}

variable "ecs_security_group_id" {
  description = "ECS security group allowed to connect to PostgreSQL"
  type        = string
}

variable "database_name" {
  description = "Initial PostgreSQL database name"
  type        = string
  default     = "franchises"
}

variable "master_username" {
  description = "Master username for PostgreSQL"
  type        = string
  default     = "franchises_admin"
}

variable "master_password" {
  description = "Master password for PostgreSQL"
  type        = string
  sensitive   = true
}

variable "instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "allocated_storage" {
  description = "Allocated storage in GB"
  type        = number
  default     = 20
}
