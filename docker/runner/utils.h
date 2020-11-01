#ifndef _UTILS_H_
#define _UTILS_H_
#endif

#include <vector>
#include <string>

std::vector<std::string> get_files(std::string, std::string);
bool diff(std::string, std::string);
void write_result(std::vector<std::string>);