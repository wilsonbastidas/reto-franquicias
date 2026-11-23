terraform {
  backend "s3" {
    bucket       = "ms-franchises-tfstate-dev-reto-nequi-46156616"
    key          = "dev/terraform.tfstate"
    region       = "us-east-1"
    encrypt      = true
    use_lockfile = true
  }
}