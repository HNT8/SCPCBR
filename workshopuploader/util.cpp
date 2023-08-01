#include <iostream>
#include <fstream>
#include <string>

#include "SteamAPI/steam_api.h"

#include "util.h"

bool yes_no_option(std::string prompt)
{
    std::cout << prompt << ": ";
    char result;
    std::cin.get(result);
    switch (result) {
        case 'y':
        case 'Y':
            return true;
        default:
            return false;
    }
}

std::string readline()
{
    std::string line;
    std::getline(std::cin, line);
    return line;
}

std::string readline_limit(int limit)
{
    for (;;) {
        std::string line = readline();

        if (!(line.length() > limit)) {
            return line;
        } else {
            std::cerr << "The input exceeds the limit of " << limit
                << " by " << line.length() - limit << " characters"
                << ".\n";
        }
    }
}

