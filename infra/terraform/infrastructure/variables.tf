variable "aws_region" {
  description = "AWS region where infrastructure will be deployed"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "ms-franchises"
}

variable "environment" {
  description = "Deployment environment"
  type        = string
  default     = "dev"
}

variable "vpc_cidr" {
  description = "CIDR block for the main VPC"
  type        = string
  default     = "10.0.0.0/16"
}


variable "database_name" {
  description = "Name of the PostgreSQL database"
  type        = string
  default     = "franchises"
}

variable "database_master_username" {
  description = "Master username for PostgreSQL"
  type        = string
  default     = "franchises_admin"
}

variable "database_instance_class" {
  description = "RDS PostgreSQL instance class"
  type        = string
  default     = "db.t4g.micro"
}

variable "database_allocated_storage" {
  description = "Allocated RDS storage in GB"
  type        = number
  default     = 20
}

variable "app_db_secret_name" {
  description = "Secret name used by the application to read DB credentials"
  type        = string
  default     = "franchises/db-credentials"
}

variable "cors_allowed_origins" {
  description = "Allowed CORS origins for the application"
  type        = string
  default     = "http://localhost:4200,http://localhost:8080"
}
