variable "project_name" { type = string }
variable "environment" { type = string }
variable "aws_region" { type = string }

variable "vpc_id" { type = string }
variable "subnet_ids" { type = list(string) }

variable "alb_target_group_arn" { type = string }
variable "alb_security_group_id" { type = string }

variable "ecr_repository_url" { type = string }
variable "image_tag" {
  type    = string
  default = "latest"
}

variable "execution_role_arn" { type = string }
variable "task_role_arn" { type = string }

variable "app_db_secret_name" {
  type    = string
  default = "franchises/db-credentials"
}

variable "container_port" {
  type    = number
  default = 8080
}

variable "task_cpu" {
  type    = string
  default = "256"
}

variable "task_memory" {
  type    = string
  default = "512"
}

variable "desired_count" {
  type    = number
  default = 1
}

variable "assign_public_ip" {
  type    = bool
  default = true
}

variable "cors_allowed_origins" {
  type    = string
  default = "http://localhost:4200,http://localhost:8080"
}
