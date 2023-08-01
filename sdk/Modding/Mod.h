#pragma once
#include <string>
#include <vector>

class Mod {
public:
	std::string ID;
	std::string Name;
	std::string Delimeter;
	std::string Version;
	std::string Description;
	std::vector<std::string> Scripts;
	LuaCpp::LuaEnvironment Environment;
};