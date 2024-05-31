#include <sys/syscall.h>
#include "syscall_rule.h"

/// C, C++
static void c(int *r) {
    r[SYS_read] = 1;
    r[SYS_write] = 1;
    r[SYS_close] = 1;
    r[SYS_stat] = 1;
    r[SYS_fstat] = 1;
    r[SYS_lseek] = 1;
    r[SYS_mmap] = 1;
    r[SYS_mprotect] = 1;
    r[SYS_munmap] = 1;
    r[SYS_brk] = 1;
    r[SYS_pread64] = 1;
    r[SYS_access] = 1;
    r[SYS_execve] = 1;
    r[SYS_arch_prctl] = 1;
    r[SYS_futex] = 1;
    r[SYS_set_tid_address] = 1;
    r[SYS_openat] = 1;
    r[SYS_newfstatat] = 1;
    r[SYS_set_robust_list] = 1;
    r[SYS_prlimit64] = 1;
    r[SYS_getrandom] = 1;
    r[SYS_rseq] = 1;
}

/// Java
static void java(int *r) {
    r[SYS_read] = 1;
    r[SYS_write] = 1;
    r[SYS_close] = 1;
    r[SYS_stat] = 1;
    r[SYS_fstat] = 1;
    r[SYS_lseek] = 1;
    r[SYS_mmap] = 1;
    r[SYS_mprotect] = 1;
    r[SYS_munmap] = 1;
    r[SYS_brk] = 1;
    r[SYS_rt_sigaction] = 1;
    r[SYS_rt_sigprocmask] = 1;
    r[SYS_pread64] = 1;
    r[SYS_access] = 1;
    r[SYS_getpid] = 1;
    r[SYS_clone] = 1;
    r[SYS_execve] = 1;
    r[SYS_readlink] = 1;
    r[SYS_arch_prctl] = 1;
    r[SYS_futex] = 1;
    r[SYS_set_tid_address] = 1;
    r[SYS_exit_group] = 1;
    r[SYS_openat] = 1;
    r[SYS_newfstatat] = 1;
    r[SYS_set_robust_list] = 1;
    r[SYS_prlimit64] = 1;
    r[SYS_getrandom] = 1;
    r[SYS_rseq] = 1;
    r[SYS_clone3] = 1;
}

/// Python
static void py(int *r) {
    r[SYS_read] = 1;
    r[SYS_write] = 1;
    r[SYS_close] = 1;
    r[SYS_stat] = 1;
    r[SYS_fstat] = 1;
    r[SYS_lstat] = 1;
    r[SYS_lseek] = 1;
    r[SYS_mmap] = 1;
    r[SYS_mprotect] = 1;
    r[SYS_munmap] = 1;
    r[SYS_brk] = 1;
    r[SYS_rt_sigaction] = 1;
    r[SYS_rt_sigprocmask] = 1;
    r[SYS_ioctl] = 1;
    r[SYS_pread64] = 1;
    r[SYS_access] = 1;
    r[SYS_dup] = 1;
    r[SYS_execve] = 1;
    r[SYS_fcntl] = 1;
    r[SYS_getcwd] = 1;
    r[SYS_readlink] = 1;
    r[SYS_sysinfo] = 1;
    r[SYS_getuid] = 1;
    r[SYS_getgid] = 1;
    r[SYS_geteuid] = 1;
    r[SYS_getegid] = 1;
    r[SYS_arch_prctl] = 1;
    r[SYS_gettid] = 1;
    r[SYS_futex] = 1;
    r[SYS_getdents64] = 1;
    r[SYS_set_tid_address] = 1;
    r[SYS_exit_group] = 1;
    r[SYS_openat] = 1;
    r[SYS_newfstatat] = 1;
    r[SYS_set_robust_list] = 1;
    r[SYS_prlimit64] = 1;
    r[SYS_getrandom] = 1;
    r[SYS_rseq] = 1;
}

/// Bash
static void bash(int *r) {
    r[SYS_read] = 1;
    r[SYS_write] = 1;
    r[SYS_close] = 1;
    r[SYS_stat] = 1;
    r[SYS_fstat] = 1;
    r[SYS_lseek] = 1;
    r[SYS_mmap] = 1;
    r[SYS_mprotect] = 1;
    r[SYS_munmap] = 1;
    r[SYS_brk] = 1;
    r[SYS_rt_sigaction] = 1;
    r[SYS_rt_sigprocmask] = 1;
    r[SYS_ioctl] = 1;
    r[SYS_pread64] = 1;
    r[SYS_access] = 1;
    r[SYS_dup2] = 1;
    r[SYS_getpid] = 1;
    r[SYS_execve] = 1;
    r[SYS_uname] = 1;
    r[SYS_fcntl] = 1;
    r[SYS_getcwd] = 1;
    r[SYS_sysinfo] = 1;
    r[SYS_getuid] = 1;
    r[SYS_getgid] = 1;
    r[SYS_geteuid] = 1;
    r[SYS_getegid] = 1;
    r[SYS_getppid] = 1;
    r[SYS_getpgrp] = 1;
    r[SYS_arch_prctl] = 1;
    r[SYS_futex] = 1;
    r[SYS_set_tid_address] = 1;
    r[SYS_exit_group] = 1;
    r[SYS_openat] = 1;
    r[SYS_newfstatat] = 1;
    r[SYS_set_robust_list] = 1;
    r[SYS_prlimit64] = 1;
    r[SYS_getrandom] = 1;
    r[SYS_rseq] = 1;
}

/// C#
static void cs(int *r) {
    r[SYS_read] = 1;
    r[SYS_write] = 1;
    r[SYS_close] = 1;
    r[SYS_stat] = 1;
    r[SYS_fstat] = 1;
    r[SYS_lseek] = 1;
    r[SYS_mmap] = 1;
    r[SYS_mprotect] = 1;
    r[SYS_munmap] = 1;
    r[SYS_brk] = 1;
    r[SYS_rt_sigaction] = 1;
    r[SYS_rt_sigprocmask] = 1;
    r[SYS_ioctl] = 1;
    r[SYS_pread64] = 1;
    r[SYS_access] = 1;
    r[SYS_sched_yield] = 1;
    r[SYS_getpid] = 1;
    r[SYS_clone] = 1;
    r[SYS_execve] = 1;
    r[SYS_fcntl] = 1;
    r[SYS_getcwd] = 1;
    r[SYS_readlink] = 1;
    r[SYS_sigaltstack] = 1;
    r[SYS_statfs] = 1;
    r[SYS_arch_prctl] = 1;
    r[SYS_futex] = 1;
    r[SYS_sched_getaffinity] = 1;
    r[SYS_getdents64] = 1;
    r[SYS_set_tid_address] = 1;
    r[SYS_exit_group] = 1;
    r[SYS_openat] = 1;
    r[SYS_newfstatat] = 1;
    r[SYS_set_robust_list] = 1;
    r[SYS_prlimit64] = 1;
    r[SYS_getrandom] = 1;
    r[SYS_rseq] = 1;
    r[SYS_clone3] = 1;
}

/// JavaScript(Node)
static void js(int *r) {
    r[SYS_read] = 1;
    r[SYS_write] = 1;
    r[SYS_close] = 1;
    r[SYS_stat] = 1;
    r[SYS_fstat] = 1;
    r[SYS_lseek] = 1;
    r[SYS_mmap] = 1;
    r[SYS_mprotect] = 1;
    r[SYS_munmap] = 1;
    r[SYS_brk] = 1;
    r[SYS_rt_sigaction] = 1;
    r[SYS_rt_sigprocmask] = 1;
    r[SYS_ioctl] = 1;
    r[SYS_pread64] = 1;
    r[SYS_access] = 1;
    r[SYS_sched_yield] = 1;
    r[SYS_madvise] = 1;
    r[SYS_getpid] = 1;
    r[SYS_clone] = 1;
    r[SYS_execve] = 1;
    r[SYS_uname] = 1;
    r[SYS_fcntl] = 1;
    r[SYS_getcwd] = 1;
    r[SYS_readlink] = 1;
    r[SYS_sysinfo] = 1;
    r[SYS_getuid] = 1;
    r[SYS_getgid] = 1;
    r[SYS_geteuid] = 1;
    r[SYS_getegid] = 1;
    r[SYS_capget] = 1;
    r[SYS_arch_prctl] = 1;
    r[SYS_futex] = 1;
    r[SYS_set_tid_address] = 1;
    r[SYS_exit_group] = 1;
    r[SYS_epoll_wait] = 1;
    r[SYS_epoll_ctl] = 1;
    r[SYS_openat] = 1;
    r[SYS_newfstatat] = 1;
    r[SYS_set_robust_list] = 1;
    r[SYS_eventfd2] = 1;
    r[SYS_epoll_create1] = 1;
    r[SYS_pipe2] = 1;
    r[SYS_prlimit64] = 1;
    r[SYS_getrandom] = 1;
    r[SYS_pkey_alloc] = 1;
    r[SYS_statx] = 1;
    r[SYS_rseq] = 1;
    r[SYS_clone3] = 1;
}

/// Kotlin
static void kt(int *r) {
    r[SYS_read] = 1;
    r[SYS_write] = 1;
    r[SYS_close] = 1;
    r[SYS_stat] = 1;
    r[SYS_fstat] = 1;
    r[SYS_lseek] = 1;
    r[SYS_mmap] = 1;
    r[SYS_mprotect] = 1;
    r[SYS_munmap] = 1;
    r[SYS_brk] = 1;
    r[SYS_rt_sigaction] = 1;
    r[SYS_rt_sigprocmask] = 1;
    r[SYS_ioctl] = 1;
    r[SYS_pread64] = 1;
    r[SYS_access] = 1;
    r[SYS_dup2] = 1;
    r[SYS_getpid] = 1;
    r[SYS_clone] = 1;
    r[SYS_execve] = 1;
    r[SYS_wait4] = 1;
    r[SYS_uname] = 1;
    r[SYS_semget] = 1;
    r[SYS_fcntl] = 1;
    r[SYS_getcwd] = 1;
    r[SYS_readlink] = 1;
    r[SYS_sysinfo] = 1;
    r[SYS_getuid] = 1;
    r[SYS_getgid] = 1;
    r[SYS_geteuid] = 1;
    r[SYS_getegid] = 1;
    r[SYS_getppid] = 1;
    r[SYS_getpgrp] = 1;
    r[SYS_arch_prctl] = 1;
    r[SYS_gettid] = 1;
    r[SYS_futex] = 1;
    r[SYS_set_tid_address] = 1;
    r[SYS_exit_group] = 1;
    r[SYS_openat] = 1;
    r[SYS_newfstatat] = 1;
    r[SYS_set_robust_list] = 1;
    r[SYS_prlimit64] = 1;
    r[SYS_getrandom] = 1;
    r[SYS_rseq] = 1;
    r[SYS_clone3] = 1;
}

/// Go
static void go(int *r) {
    r[SYS_read] = 1;
    r[SYS_write] = 1;
    r[SYS_close] = 1;
    r[SYS_stat] = 1;
    r[SYS_fstat] = 1;
    r[SYS_lseek] = 1;
    r[SYS_mmap] = 1;
    r[SYS_mprotect] = 1;
    r[SYS_brk] = 1;
    r[SYS_rt_sigaction] = 1;
    r[SYS_rt_sigprocmask] = 1;
    r[SYS_access] = 1;
    r[SYS_clone] = 1;
    r[SYS_execve] = 1;
    r[SYS_fcntl] = 1;
    r[SYS_getrlimit] = 1;
    r[SYS_sigaltstack] = 1;
    r[SYS_arch_prctl] = 1;
    r[SYS_setrlimit] = 1;
    r[SYS_gettid] = 1;
    r[SYS_futex] = 1;
    r[SYS_sched_getaffinity] = 1;
    r[SYS_openat] = 1;
}

SyscallRule::SyscallRule(int language) {
    if (language == C || language == CPP) {
        c(this->rules);
    } else if (language == JAVA) {
        java(this->rules);
    } else if (language == PY) {
        py(this->rules);
    } else if (language == BASH) {
        bash(this->rules);
    } else if (language == CSHARP) {
        cs(this->rules);
    } else if (language == JS) {
        js(this->rules);
    } else if (language == KT) {
        kt(this->rules);
    } else if (language == GO) {
        go(this->rules);
    }
}

int SyscallRule::check(struct user_regs_struct *regs) {
    int syscall_number = (int) regs->orig_rax;

    if (syscall_number < 0) {
        return 0;
    }

    if (rules[syscall_number] != 1) {
        return syscall_number;
    }

    return 0;
}
