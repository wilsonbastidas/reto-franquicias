
variable "project_name" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Deployment environment"
  type        = string
}

variable "database_secret_arn" {
  description = "ARN of the database secret in Secrets Manager"
  type        = string
}