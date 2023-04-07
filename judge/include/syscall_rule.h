/// 系统调用规则过滤
/// @author Cloud
#ifndef SYSCALL_RULE_H
#define SYSCALL_RULE_H 1

#include <sys/user.h>

#define C 0
#define CPP 1
#define JAVA 2
#define PY 3
#define BASH 4
#define CSHARP 5
#define JS 6
#define KT 7
#define GO 8

class SyscallRule {
private:
    // 系统调用白名单
    int rules[335] = {0};
public:
    explicit SyscallRule(int language);

    /**
     * 检查系统调用是否允许
     * @param regs 寄存器
     * @return @c 0 - OK, @c 非 0 - NO，返回系统调用号
     */
    int check(struct user_regs_struct *regs);
};

#endif // SYSCALL_RULE_H
