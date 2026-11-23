variable "project_name" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Deployment environment"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID where the ALB will be deployed"
  type        = string
}

variable "private_subnet_ids" {
  description = "Private subnet IDs where the ALB will be deployed"
  type        = list(string)
}

variable "container_port" {
  description = "Port exposed by the application container"
  type        = number
  default     = 8080
}

variable "vpc_cidr" {
  description = "CIDR block of the VPC"
  type        = string
}