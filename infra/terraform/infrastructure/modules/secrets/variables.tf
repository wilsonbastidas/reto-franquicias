variable "project_name" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Deployment environment"
  type        = string
}

variable "secret_name" {
  description = "Name of the application database secret"
  type        = string
  default     = "franchises/db-credentials"
}
