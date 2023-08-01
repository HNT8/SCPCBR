#pragma execution_character_set("utf-8")

#define _CRT_SECURE_NO_WARNINGS
#include "Game.hpp"

#include <vector>
#include <Windows.h>
#include <thread>
#include <iostream>
#include <cstdio>
#include <filesystem>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <csignal>
#include <cstdlib>
#include <shtypes.h>
#include <fstream>
#include <locale>
#include <windef.h>

#include "Lua/LuaCpp.hpp"

#include "SteamAPI/steam_api.h"
#include "Discord.h"
#include "Network.h"

#include "Modding/Print.h"
#include "Modding/Mod.h"

#include "Toml/toml.hpp"

#include "Modding/Libraries/game.h"
#include "Modding/Libraries/console.h"

#include "Localization/Language.h"

#include "Console/Console.hpp"

double OriginalWidth;
double OriginalHeight;

#define B3DDLL(type) extern "C" __declspec(dllexport) type __stdcall

Discord* discord;

void SteamLoop() {
	while (true) {
		SteamAPI_RunCallbacks();
		Sleep(500);
	}
}

int MessageBoxC(const char* Msg, const char* Title, UINT type = 0) {
	return MessageBoxA(0, Msg, Title, type);
}

B3DDLL(void) scpSDK_TakeScreenshot() {
	SteamScreenshots()->TriggerScreenshot();
	Trace("Took screenshot.");
}

B3DDLL(const char*) scpModding_Version() {
	return VERSIONSTRING;
}

B3DDLL(void) scpDiscord_Initialize() {
	discord->Initialize("");
	discord->Update("Loading...", true);
	Trace("Initialized discord rich presence.");
}

B3DDLL(void) scpDiscord_Update(const char* State, bool UpdateTime) {
	discord->Update(State, UpdateTime);
	Trace(std::string("Updated discord rich presense to \"") + State + "\"");
}

B3DDLL(void) scpSteam_Shutdown() {
	SteamAPI_Shutdown();
	Trace("Terminated the steam api.");
}

B3DDLL(void) scpSteam_SetAchievement(const char* AchievementString) {
	SteamUserStats()->SetAchievement(AchievementString);
	SteamUserStats()->StoreStats();

	Trace(std::string("Successfully issued achievement \"") + AchievementString + "\"");
	return;
}

B3DDLL(void) scpSteam_ResetStats(bool AchievementsToo) {
	if (MessageBoxA(0, "Are you sure you want to reset your stats?", "SCP:CB Remastered", MB_YESNO) == IDYES) {
		SteamUserStats()->ResetAllStats(AchievementsToo);
		SteamUserStats()->StoreStats();
	}
	Trace("Reset user stats and achievements.");
	return;
}

B3DDLL(void) scpSDK_ResetOptions() {
	if (MessageBoxA(0, "Are you sure you want to reset your options?", "SCP:CB Remastered", MB_YESNO) == IDYES) {
		std::filesystem::remove("options.ini");
		std::filesystem::copy_file("defaultoptions.ini", "options.ini");
	}
	Trace("Reset user settings.");
}

std::vector<const char*> CommandList = {};
std::vector<const char*> ConsoleText = {};

B3DDLL(const char*) scpSDK_GetCommand() {
	if (CommandList.size() > 0) {
		const char* Val = CommandList.at(0);
		CommandList.erase(CommandList.begin());
		return Val;
	}
	else {
		return "";
	}
}

std::vector<std::pair<std::string, std::string>> redirections;

std::string GameTitle = "SCP:CB REMASTERED";
B3DDLL(const char*) scpModding_GetGameTitle() {
	return GameTitle.c_str();
}
void setGameTitle(std::string title) {
	Trace("Set game title to \"" + title + "\"");
	GameTitle = title;
}

void addRedirection(const char* gameFilePath, const char* replacePath) {
	std::string newGameFilePath = gameFilePath;
	std::string newReplacePath = replacePath;
	std::transform(newGameFilePath.begin(), newGameFilePath.end(), newGameFilePath.begin(), [](auto c) { return std::tolower(c, std::locale()); });
	std::transform(newReplacePath.begin(), newReplacePath.end(), newReplacePath.begin(), [](auto c) { return std::tolower(c, std::locale()); });

	redirections.push_back(std::make_pair(newGameFilePath, newReplacePath));

	Trace("Added file redirection from \"" + newGameFilePath + "\" to \"" + newReplacePath + "\"");
}

std::vector<std::filesystem::path> moddedMaps;
void addMap(std::filesystem::path path) {
	moddedMaps.push_back(path);

	Trace("Added custom map \"" + path.string() + "\"");
}
void removeMaps() {
	for (std::filesystem::path map : moddedMaps) {
		try {
			std::filesystem::remove(map);
		}
		catch (...) {}
	}
}

B3DDLL(const char*) scpModding_ProcessFilePath(const char* givenPath) {
	if (redirections.size() > 0) {
		std::string newGivenPath = std::string(givenPath);
		std::transform(newGivenPath.begin(), newGivenPath.end(), newGivenPath.begin(), [](auto c) { return std::tolower(c, std::locale()); });

		for (std::pair<std::string, std::string> redirection : redirections) {
			if (redirection.first == newGivenPath) {
				return redirection.second.c_str();
			}
		}
	}
	
	return givenPath;
}

std::vector<Mod> Mods;

std::vector<Mod> GetAllLoadedMods() {
	return Mods;
}

LuaCpp::LuaContext Lua;

void StartModLibraries() {
	Info("Searching for mods...");

	if (!std::filesystem::exists(MODPATH))
		return;

	for (const auto& entry : std::filesystem::directory_iterator(MODPATH)) {
		if (!std::filesystem::exists(entry.path().string() + "\\mod.toml")) {
			Error("The mod " + entry.path().filename().string() + " does not contain mod.toml!");
			continue;
		}
		
		Mod mod;

		mod.ID = entry.path().filename().string();

		try {
			auto modconfig = toml::parse_file(entry.path().string() + "\\mod.toml");

			mod.Name = modconfig["mod"]["name"].value_or(entry.path().filename().string());
			mod.Delimeter = modconfig["mod"]["delimeter"].value_or(entry.path().filename().string());
			mod.Version = modconfig["mod"]["version"].value_or("1.0");
			mod.Description = modconfig["mod"]["description"].value_or("");

			if (modconfig["mod"]["scripts"].is_array()) {
				toml::array* scriptArr = modconfig["mod"]["scripts"].as_array();
				for (int i = 0; i < scriptArr->size(); i++) {
					mod.Scripts.push_back(scriptArr->get(i)->as_string()->get());
				}
			}
		}
		catch (toml::parse_error e) {
			Error("An error occured while parsing " + entry.path().filename().string() + "'s mod.toml");
			Error(e.what());
			continue;
		}

		Mods.push_back(mod);

		Info("Successfully loaded mod: " + mod.Name + " v" + mod.Version);
	}

	LuaCpp::LuaContext* lua = &Lua;

	Info("Loaded " + std::to_string(Mods.size()) + " mods.");

	std::shared_ptr<LuaCpp::Registry::LuaLibrary> gameLib = std::make_shared<LuaCpp::Registry::LuaLibrary>("Game");
	gameLib->AddCFunction("RedirectFile", redirectFile);
	gameLib->AddCFunction("SetGameTitle", setGameTitle);
	gameLib->AddCFunction("ImplementMap", implementMap);
	gameLib->AddCFunction("Wait", wait);

	std::shared_ptr<LuaCpp::Registry::LuaLibrary> consoleLib = std::make_shared<LuaCpp::Registry::LuaLibrary>("Console");
	consoleLib->AddCFunction("Info", info);
	consoleLib->AddCFunction("Warn", warn);
	consoleLib->AddCFunction("Error", error);
	consoleLib->AddCFunction("Trace", trace);

	lua->AddLibrary(gameLib);
	lua->AddLibrary(consoleLib);

	for (Mod mod : Mods) {
		std::shared_ptr <LuaCpp::Engine::LuaTString> consoleText = std::make_shared<LuaCpp::Engine::LuaTString>(mod.Delimeter);
		mod.Environment["ModDelimeter"] = consoleText;

		for (std::string script : mod.Scripts) {
			try {
				lua->CompileFile(MODPATH + mod.ID + "\\" + script, MODPATH + mod.ID + "\\" + script);
				lua->RunWithEnvironment(MODPATH + mod.ID + "\\" + script, mod.Environment);
			}
			catch (std::runtime_error& e) {
				Error("An error occured while executing the script \"" + script + "\"", mod.Delimeter);
				Error(e.what(), mod.Delimeter);
			}
		}
	}
}

B3DDLL(void) scpSDK_MessageBox(const char* message, const char* title) {
	MessageBoxA(0, message, title, 0);
}

B3DDLL(void) scpSDK_ProcessCmdArgs(const char* args) {
	std::vector<std::string> arglist;
	
	std::string sargs = args;
	std::transform(sargs.begin(), sargs.end(), sargs.begin(), [](unsigned char c) { return std::tolower(c); });
	arglist = split(sargs, ' ');

	for (std::string cmd : arglist) {
		if (cmd == "-console") {
			std::thread(DebugConsole).detach();
			Info("SCPCBR Debug Console Enabled");
		}
	}
}

toml::parse_result fontConfig;
void InitializeFonts() {
	fontConfig = toml::parse_file(std::filesystem::current_path().string() + "\\Data\\fonts.toml");
}
B3DDLL(const char*) scpFont_GetFile(const char* name) {
	return fontConfig[name]["file"].value_exact<const char*>().value();
}
B3DDLL(int) scpFont_GetSize(const char* name) {
	return fontConfig[name]["size"].value_exact<int64_t>().value();
}

int selectedLang = 0;
std::vector<Language> Languages;
void InitializeLanguages() {
	for (const auto& entry : std::filesystem::directory_iterator(std::filesystem::current_path().string() + "\\Localization\\")) {
		toml::parse_result langConfig = toml::parse_file(entry.path().string());
		Language lang;
		if (langConfig["language"]["name"].is_value())
			lang.Name = langConfig["language"]["name"].value_exact<const char*>().value();
		else if (langConfig["language.name"].is_value())
			lang.Name = langConfig["language.name"].value_exact<const char*>().value();
		else
			lang.Name = entry.path().string().substr(0, entry.path().string().find('.'));
		lang.Toml = langConfig;
		Languages.push_back(lang);

		Trace("Added language \"" + lang.Name + "\"");
	}

	for (int i = 0; i < Languages.size(); i++) {
		if (Languages.at(i).Name == "English") {
			selectedLang = i;
			break;
		}
	}
}
B3DDLL(const char*) scpLang_GetLang() {
	return Languages.at(selectedLang).Name.c_str();
}
B3DDLL(void) scpLang_SetLang(const char* lang) {
	for (int i = 0; i < Languages.size(); i++) {
		if (Languages.at(i).Name == lang) {
			selectedLang = i;
			break;
		}
	}

	Trace("Set language to \"" + std::string(lang) + "\"");
}
B3DDLL(void) scpLang_BackLang() {
	selectedLang -= 1;
	if (selectedLang < 0)
		selectedLang = Languages.size() - 1;
}
B3DDLL(void) scpLang_NextLang() {
	selectedLang += 1;
	if (selectedLang >= Languages.size())
		selectedLang = 0;
}
B3DDLL(const char*) scpLang_GetPhrase(const char* key) {
	std::string group = std::string(key).substr(0, std::string(key).find('.'));
	std::string id = std::string(key).substr(std::string(key).find('.') + 1, std::string(key).size());
	if (Languages.at(selectedLang).Toml[group][id].is_value())
		return Languages.at(selectedLang).Toml[group][id].value_exact<const char*>().value();
	else if (Languages.at(selectedLang).Toml[key].is_value())
		return Languages.at(selectedLang).Toml[key].value_exact<const char*>().value();
	else
		return key;
}

B3DDLL(void) scpSteam_Initialize() {
	SetProcessDPIAware();
	SetProcessDpiAwarenessContext(DPI_AWARENESS_CONTEXT_UNAWARE_GDISCALED);
	Trace("Set process DPI aware functionality to \"unaware\"");

	if (!SteamAPI_IsSteamRunning()) {
		MessageBoxA(0, "Please launch steam before attempting to launch the game!", "SCP: Containment Breach Remastered", 0);
		exit(-1);
	}
	if (!SteamAPI_Init()) {
		MessageBoxA(0, "Failed to initialize the Steam API.\nPlease make sure you are launching the game from\nsteam and not from the game's directory.", "SCP: Containment Breach Remastered", 0);
		exit(-1);
	}

	std::thread(SteamLoop).detach();

	StartModLibraries();
	InitializeLanguages();

	Trace("Initialized Steam API");

	atexit(removeMaps);
	Trace("Binded \"remove custom maps\" method to program termination.");
}