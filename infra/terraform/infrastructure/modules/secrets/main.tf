resource "aws_secretsmanager_secret" "database" {
  name        = var.secret_name
  description = "Database credentials for ${var.project_name} ${var.environment}"

  tags = {
    Name        = "${var.project_name}-${var.environment}-database-secret"
    Project     = var.project_name
    Environment = var.environment
  }
}
