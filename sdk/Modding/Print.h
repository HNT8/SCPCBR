#pragma once
#include <string>
#include <iostream>

#include "../Color.h"

void Info(std::string text, std::string mod = "INTERNAL") {
	std::time_t time = std::time(0);
	std::tm* now = std::localtime(&time);

	std::cout << dye::white("[") << dye::white(now->tm_hour) << dye::white(":") << dye::white(now->tm_min) << dye::white(":") << dye::white(now->tm_sec) << dye::white(" INFO][" + mod + "] ") << dye::white(text) << std::endl;
}

void Warn(std::string text, std::string mod = "INTERNAL") {
	std::time_t time = std::time(0);
	std::tm* now = std::localtime(&time);

	std::cout << dye::yellow("[") << dye::yellow(now->tm_hour) << dye::yellow(":") << dye::yellow(now->tm_min) << dye::yellow(":") << dye::yellow(now->tm_sec) << dye::yellow(" WARNING][" + mod + "] ") << dye::yellow(text) << std::endl;
}

void Error(std::string text, std::string mod = "INTERNAL") {
	std::time_t time = std::time(0);
	std::tm* now = std::localtime(&time);

	std::cout << dye::red("[") << dye::red(now->tm_hour) << dye::red(":") << dye::red(now->tm_min) << dye::red(":") << dye::red(now->tm_sec) << dye::red(" ERROR][" + mod + "] ") << dye::red(text) << std::endl;
}

void Trace(std::string text, std::string mod = "INTERNAL") {
	std::time_t time = std::time(0);
	std::tm* now = std::localtime(&time);

	std::cout << dye::white("[") << dye::white(now->tm_hour) << dye::white(":") << dye::white(now->tm_min) << dye::white(":") << dye::white(now->tm_sec) << dye::white(" TRACE][" + mod + "] ") << dye::white(text) << std::endl;
}