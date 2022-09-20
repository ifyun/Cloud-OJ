#include <cstring>
#include <boost/program_options.hpp>
#include <iostream>
#include "options.h"
#include "common.h"

namespace po = boost::program_options;

int get_args(int argc, char *argv[], char *cmd, int &lang, char workdir[], char datadir[], Config &config) {
    std::string _cmd, _workdir, _datadir;
    po::options_description desc("选项");
    desc.add_options()
            ("help,h", "显示此信息")
            ("cmd,c", po::value<std::string>(&_cmd), "命令(空格用 '@' 表示)")
            ("lang,l", po::value<int>(&lang), "语言: 0.C 1.C++ 2.Java 3.Python 4.Bash 5.C# 6.JS 7.Kotlin 8.Go")
            ("time,t", po::value<long>(&config.timeout), "时间限制(ms)")
            ("ram,m", po::value<long>(&config.memory), "内存限制(MB)")
            ("output,o", po::value<long>(&config.output_size), "输出限制(MB)")
            ("work-dir,w", po::value<std::string>(&_workdir), "工作路径")
            ("data,d", po::value<std::string>(&_datadir), "测试数据路径")
            ("cpu,u", po::value<int>(&config.cpu), "CPU 核心");
    po::variables_map vm;
    po::store(po::command_line_parser(argc, argv).options(desc).run(), vm);
    po::notify(vm);

    if (vm.count("help")) {
        std::cerr << desc;
        return 1;
    }

    if (vm.count("cmd") && vm.count("lang") && vm.count("time") && vm.count("ram") &&
        vm.count("output") && vm.count("work-dir") && vm.count("data") && vm.count("cpu")) {
        strcpy(cmd, _cmd.c_str());
        strcpy(workdir, _workdir.c_str());
        strcpy(datadir, _datadir.c_str());
        config.memory <<= 10;
        config.output_size <<= 10;
        return 0;
    } else {
        return JUDGE_ERROR;
    }
}
