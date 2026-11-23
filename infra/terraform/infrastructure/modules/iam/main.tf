# Trust policy: permite que ECS asuma este rol.
data "aws_iam_policy_document" "ecs_task_assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

# IAM Role utilizado por ECS para ejecutar las tareas.
resource "aws_iam_role" "ecs_execution" {
  name               = "${var.project_name}-${var.environment}-ecs-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_assume_role.json

  tags = {
    Name        = "${var.project_name}-${var.environment}-ecs-execution-role"
    Project     = var.project_name
    Environment = var.environment
  }
}

# Permisos administrados por AWS para ECS.
resource "aws_iam_role_policy_attachment" "ecs_execution" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# IAM Role utilizado por la aplicaci??n que se ejecuta dentro del contenedor.
resource "aws_iam_role" "ecs_task" {
  name               = "${var.project_name}-${var.environment}-ecs-task-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_assume_role.json

  tags = {
    Name        = "${var.project_name}-${var.environment}-ecs-task-role"
    Project     = var.project_name
    Environment = var.environment
  }
}

# Pol??tica que permite leer el secreto de la base de datos.
data "aws_iam_policy_document" "secrets_access" {
  statement {
    effect = "Allow"

    actions = [
      "secretsmanager:GetSecretValue"
    ]

    resources = [
      var.database_secret_arn
    ]
  }
}

# Pol??tica IAM asociada al ECS Execution Role.
resource "aws_iam_role_policy" "secrets_access_execution" {
  name   = "${var.project_name}-${var.environment}-secrets-access-execution"
  role   = aws_iam_role.ecs_execution.id
  policy = data.aws_iam_policy_document.secrets_access.json
}

# Pol??tica IAM asociada al ECS Task Role (la app lee el secret en runtime).
resource "aws_iam_role_policy" "secrets_access_task" {
  name   = "${var.project_name}-${var.environment}-secrets-access-task"
  role   = aws_iam_role.ecs_task.id
  policy = data.aws_iam_policy_document.secrets_access.json
}
