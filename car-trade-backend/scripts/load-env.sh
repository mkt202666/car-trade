# ============================================================
# 5D 好车 · Git Bash / Linux 环境变量加载脚本
# ============================================================
# 用法（在仓库根目录执行）：
#   source car-trade-backend/scripts/load-env.sh
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/../.env.local"

if [ ! -f "$ENV_FILE" ]; then
    echo "[load-env] .env.local not found at $ENV_FILE"
    echo "[load-env] Please copy .env.local.example to .env.local and fill in values."
    return 1 2>/dev/null || exit 1
fi

echo "[load-env] loading $ENV_FILE ..."
count=0
# 兼容 'set -a' 让后续赋值自动 export
set -a
while IFS='=' read -r key value; do
    # 跳过空行与注释
    [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]] && continue
    # 去掉值两端的引号
    value="${value%\"}"; value="${value#\"}"
    value="${value%\'}"; value="${value#\'}"
    export "$key"="$value"
    count=$((count+1))
    if [[ "$key" =~ SECRET|PASSWORD|KEY ]]; then
        echo "  $key = ***"
    else
        echo "  $key = $value"
    fi
done < "$ENV_FILE"
set +a
echo "[load-env] $count env var(s) loaded."
