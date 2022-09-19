/// 系统调用检查工具类
/// @author Cloud
#ifndef _SYSCALL_CHECKER_H
#define _SYSCALL_CHECKER_H 1

#include <sys/user.h>
#include <set>

#define C 0
#define CPP 1
#define JAVA 2
#define PY 3
#define BASH 4
#define CSHARP 5
#define JS 6
#define KT 7
#define GO 8

class SyscallChecker {
private:
    std::set<int> *blacklist = nullptr;
public:
    explicit SyscallChecker(int language);

    /**
     * 检查系统调用是否允许
     * @param regs 寄存器
     * @return @c 0 - OK, @c 非 0 - NO，返回系统调用号
     */
    int check(struct user_regs_struct *regs);
};

#endif //_SYSCALL_CHECKER_H
