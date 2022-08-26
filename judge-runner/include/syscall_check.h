#ifndef _SYSCALL_CHECKER_H
#define _SYSCALL_CHECKER_H 1

#include <sys/user.h>
#include <string>
#include <set>

const int C = 0;
const int CPP = 1;
const int JAVA = 2;
const int PY = 3;
const int BASH = 4;
const int CSHARP = 5;
const int JS = 6;
const int KT = 7;
const int GO = 8;

extern int syscall_number;

/**
 * @brief 系统调用过滤，黑名单方式
 */
class SyscallChecker {
private:
    std::set<int> *blacklist = nullptr;
public:
    explicit SyscallChecker(int language);

    /**
     * 检查系统调用是否允许
     * @param regs 寄存器
     * @return @c true - ok, @c false - no
     */
    bool check(struct user_regs_struct *regs);
};

#endif //_SYSCALL_CHECKER_H
