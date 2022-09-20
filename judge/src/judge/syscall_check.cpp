#include <sys/syscall.h>
#include <algorithm>
#include "syscall_check.h"

/// C, C++, Python, Bash
static std::set<int> *blacklist0() {
    static std::set<int> blacklist{
            SYS_clone, SYS_fork, SYS_vfork, SYS_kill, SYS_socket,
            SYS_bind, SYS_accept, SYS_listen, SYS_chroot, SYS_chdir,
            SYS_mount, SYS_mknod, SYS_alarm, SYS_ptrace, SYS_pipe,
            SYS_dup2, SYS_pause, SYS_reboot, SYS_shutdown, SYS_setuid,
            SYS_unlink, SYS_nanosleep, SYS_clock_nanosleep
    };

    return &blacklist;
}

/// C#, Go, JS
static std::set<int> *blacklist1() {
    static std::set<int> blacklist{
            SYS_fork, SYS_vfork, SYS_kill, SYS_socket, SYS_bind,
            SYS_accept, SYS_listen, SYS_chroot, SYS_chdir, SYS_mount,
            SYS_mknod, SYS_alarm, SYS_ptrace, SYS_pipe, SYS_dup2,
            SYS_pause, SYS_reboot, SYS_shutdown, SYS_setuid, SYS_unlink
    };

    return &blacklist;
}

/// Java, Kotlin
static std::set<int> *blacklist_jvm() {
    static std::set<int> blacklist{
            SYS_fork, SYS_vfork, SYS_kill, SYS_bind, SYS_accept,
            SYS_listen, SYS_sendto, SYS_chroot, SYS_chdir, SYS_mount,
            SYS_mknod, SYS_alarm, SYS_ptrace, SYS_pipe, SYS_dup2,
            SYS_pause, SYS_reboot, SYS_shutdown, SYS_setuid, SYS_unlink
    };

    return &blacklist;
}

SyscallChecker::SyscallChecker(int language) {
    if (language == C || language == CPP || language == PY || language == BASH) {
        this->blacklist = blacklist0();
    } else if (language == CSHARP || language == JS || language == GO) {
        this->blacklist = blacklist1();
    } else if (language == JAVA || language == KT) {
        this->blacklist = blacklist_jvm();
    }
}

int SyscallChecker::check(struct user_regs_struct *regs) {
    int syscall_number = (int) regs->orig_rax;
    // 不允许执行外部程序
    if (syscall_number == SYS_execve && regs->rdi != 0) {
        return syscall_number;
    }
    // 无规则
    if (blacklist == nullptr) {
        return 0;
    }

    if (blacklist->find(syscall_number) != blacklist->end()) {
        return syscall_number;
    }

    return 0;
}
