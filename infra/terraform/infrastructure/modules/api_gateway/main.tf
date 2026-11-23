# Security group for API Gateway VPC Link
resource "aws_security_group" "vpc_link" {
  name        = "${var.project_name}-${var.environment}-apigw-vpclink-sg"
  description = "Security group for API Gateway VPC Link"
  vpc_id      = var.vpc_id

  egress {
    description = "Allow outbound traffic to ALB"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-apigw-vpclink-sg"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_apigatewayv2_vpc_link" "this" {
  name               = "${var.project_name}-${var.environment}-vpclink"
  security_group_ids = [aws_security_group.vpc_link.id]
  subnet_ids         = var.private_subnet_ids

  tags = {
    Name        = "${var.project_name}-${var.environment}-vpclink"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_apigatewayv2_api" "this" {
  name          = "${var.project_name}-${var.environment}-api"
  protocol_type = "HTTP"

  tags = {
    Name        = "${var.project_name}-${var.environment}-api"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_apigatewayv2_integration" "alb" {
  api_id                 = aws_apigatewayv2_api.this.id
  integration_type       = "HTTP_PROXY"
  integration_uri        = var.alb_listener_arn
  integration_method     = "ANY"
  connection_type        = "VPC_LINK"
  connection_id          = aws_apigatewayv2_vpc_link.this.id
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_route" "default" {
  api_id    = aws_apigatewayv2_api.this.id
  route_key = "$default"
  target    = "integrations/${aws_apigatewayv2_integration.alb.id}"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.this.id
  name        = "$default"
  auto_deploy = true

  tags = {
    Name        = "${var.project_name}-${var.environment}-api-stage"
    Project     = var.project_name
    Environment = var.environment
  }
}
