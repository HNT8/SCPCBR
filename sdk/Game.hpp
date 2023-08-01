#pragma once

#define SOURCETEST false
#define VERSION "1.6"
#define VERSIONSTRING "v" VERSION

#if SOURCETEST == true
#define MODPATH std::filesystem::current_path().string() + "\\Mods\\"
#else
#define MODPATH std::filesystem::current_path().string() + "\\..\\..\\workshop\\content\\2090230\\"
#endif