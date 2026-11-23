variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "state_bucket_name" {
  description = "S3 bucket for Terraform state"
  type        = string
  default     = "ms-franchises-tfstate-dev-reto-nequi-46156616"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "ms-franchises"
}