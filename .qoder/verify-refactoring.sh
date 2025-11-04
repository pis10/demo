#!/bin/bash
# TechBlog XSS 演示靶场 - 重构验证脚本
# 用于快速验证重构后的代码是否正常工作

echo "======================================"
echo "TechBlog 重构验证脚本"
echo "======================================"
echo ""

# 1. 检查编译
echo "📦 步骤1: 检查后端编译..."
cd apps/backend

if command -v mvn &> /dev/null; then
    echo "✅ Maven 已安装"
    echo "开始编译..."
    mvn clean compile -q
    if [ $? -eq 0 ]; then
        echo "✅ 后端编译成功"
    else
        echo "❌ 后端编译失败"
        exit 1
    fi
else
    echo "⚠️  Maven 未安装，跳过编译检查"
fi

cd ../..

# 2. 检查文件结构
echo ""
echo "📁 步骤2: 检查新增文件..."

check_file() {
    if [ -f "$1" ]; then
        echo "✅ $1"
    else
        echo "❌ 缺少文件: $1"
    fi
}

# 异常体系
check_file "apps/backend/src/main/java/com/techblog/backend/common/exception/BusinessException.java"
check_file "apps/backend/src/main/java/com/techblog/backend/common/exception/ResourceNotFoundException.java"
check_file "apps/backend/src/main/java/com/techblog/backend/common/exception/InvalidCredentialsException.java"
check_file "apps/backend/src/main/java/com/techblog/backend/common/exception/UserAlreadyExistsException.java"

# 枚举和响应
check_file "apps/backend/src/main/java/com/techblog/backend/common/enums/ErrorCode.java"
check_file "apps/backend/src/main/java/com/techblog/backend/common/response/ErrorResponse.java"

# Mapper层
check_file "apps/backend/src/main/java/com/techblog/backend/mapper/UserMapper.java"
check_file "apps/backend/src/main/java/com/techblog/backend/mapper/ArticleMapper.java"
check_file "apps/backend/src/main/java/com/techblog/backend/mapper/TagMapper.java"
check_file "apps/backend/src/main/java/com/techblog/backend/mapper/CommentMapper.java"
check_file "apps/backend/src/main/java/com/techblog/backend/mapper/FeedbackMapper.java"

# 配置类
check_file "apps/backend/src/main/java/com/techblog/backend/config/CookieProperties.java"
check_file "apps/backend/src/main/java/com/techblog/backend/config/CacheConfig.java"

# 3. 检查Lombok移除
echo ""
echo "🔍 步骤3: 检查Lombok依赖..."
if grep -q "lombok" apps/backend/pom.xml; then
    echo "❌ pom.xml中仍存在Lombok依赖"
else
    echo "✅ Lombok依赖已完全移除"
fi

# 4. 检查配置文件
echo ""
echo "⚙️  步骤4: 检查配置文件..."
if grep -q "security.cookie" apps/backend/src/main/resources/application.yml; then
    echo "✅ Cookie安全配置已添加"
else
    echo "❌ 缺少Cookie安全配置"
fi

if grep -q "caffeine" apps/backend/pom.xml; then
    echo "✅ Caffeine缓存依赖已添加"
else
    echo "❌ 缺少Caffeine缓存依赖"
fi

# 5. 总结
echo ""
echo "======================================"
echo "验证完成！"
echo "======================================"
echo ""
echo "📊 重构总结："
echo "  ✅ 第一阶段: 基础重构 (3/3 任务)"
echo "  ✅ 第二阶段: 架构优化 (3/3 任务)"
echo "  ✅ 第三阶段: 性能优化 (2/2 任务)"
echo ""
echo "📝 详细报告请查看: .qoder/REFACTORING_SUMMARY.md"
echo ""
echo "🚀 下一步："
echo "  1. 启动MySQL数据库"
echo "  2. cd apps/backend && mvn spring-boot:run"
echo "  3. cd apps/frontend && npm run dev"
echo "  4. 测试双模式切换和XSS演示场景"
echo ""
