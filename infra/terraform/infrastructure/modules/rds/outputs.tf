
output "db_instance_identifier" {
  description = "RDS instance identifier"
  value       = aws_db_instance.main.identifier
}

output "db_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value       = aws_db_instance.main.endpoint
}

output "db_address" {
  description = "RDS hostname"
  value       = aws_db_instance.main.address
}

output "db_port" {
  description = "PostgreSQL port"
  value       = aws_db_instance.main.port
}

output "db_name" {
  description = "Initial database name"
  value       = aws_db_instance.main.db_name
}

output "db_security_group_id" {
  description = "RDS security group ID"
  value       = aws_security_group.database.id
}

# Master password is managed by Terraform and stored in the app-aligned secret.
