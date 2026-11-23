
# General
aws_region   = "us-east-1"
project_name = "ms-franchises"
environment  = "dev"

# Network
vpc_cidr = "10.0.0.0/16"

# RDS PostgreSQL
database_name              = "franchises"
database_master_username   = "franchises_admin"
database_instance_class    = "db.t4g.micro"
database_allocated_storage = 20
