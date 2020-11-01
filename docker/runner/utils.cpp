#include "utils.h"
#include <string>
#include <vector>
#include <iostream>
#include <fstream>
#include <sstream>
#include <dirent.h>
#include <cstring>
#include <algorithm>

/**
 * Get files by path and extension.
 */
std::vector<std::string> get_files(std::string path, std::string ext)
{
    std::vector<std::string> files;
    DIR *dir = opendir(path.c_str());

    if (dir == NULL)
    {
        std::cerr << "[ERROR] " << path << " not exist!\n";
        exit(-1);
    }

    struct dirent *d_ent = NULL;

    char self[3] = ".";
    char parent[6] = "..";

    while ((d_ent = readdir(dir)) != NULL)
    {
        // exclude "." and ".."
        if (strcmp(d_ent->d_name, self) != 0 && strcmp(d_ent->d_name, parent) != 0)
        {
            if (d_ent->d_type != DT_DIR)
            {
                std::string file_name(d_ent->d_name);

                if (strcmp(file_name.c_str() + file_name.length() - ext.length(), ext.c_str()) == 0)
                {
                    std::string abs_path;
                    if (path[path.length() - 1] == '/')
                        abs_path = path + file_name;
                    else
                        abs_path = path + std::string("/") + file_name;
                    files.push_back(abs_path);
                }
            }
        }
    }

    std::sort(files.begin(), files.end());

    return files;
}

inline std::string &rtrim(std::string &str)
{
    if (str.empty())
        return str;
    str.erase(str.find_last_not_of("\r") + 1);
    str.erase(str.find_last_not_of("\n") + 1);
    str.erase(str.find_last_not_of(" ") + 1);
    return str;
}

/**
 * Compare files.
 * true: not same, false: same.
 */
bool diff(std::string path1, std::string path2)
{
    std::ifstream file1(path1);
    std::ifstream file2(path2);

    std::ostringstream oss1, oss2;
    oss1 << file1.rdbuf();
    oss2 << file2.rdbuf();

    std::string s1 = oss1.str(), s2 = oss2.str();

    rtrim(s1);
    rtrim(s2);

    return s1.length() != s2.length() ? true : s1 != s2;
}

/**
 * Write results to json file.
 */
void write_result(std::vector<std::string> results)
{
    std::string res;

    if (results.size() == 1)
    {
        res = "[" + results[0] + "]";
    }
    else
    {
        res.append("[");

        for (auto i = 0; i < results.size(); i++)
        {
            res.append(results[i]);
            if (i < results.size() - 1)
                res.append(",");
        }

        res.append("]");
    }

    std::ofstream of;
    of.open("./result.json", std::ios_base::out | std::ios_base::trunc);
    of << res;
    of.close();
}