#define _CRT_SECURE_NO_WARNINGS

#include "Application/Utilities/Window/Window.hpp"
#include "Application/Globals.hpp"

#include <vector>
#include <Windows.h>
#include <thread>
#include <iostream>
#include <cstdio>
#include <filesystem>
#include <string>
#include <stdio.h>
#include <stdlib.h>

#include "SteamAPI/steam_api.h"
#include "Discord.h"
#include "Network.h"

#define B3DDLL(type) extern "C" __declspec(dllexport) type __stdcall

Discord* discord;

void SteamLoop() {
	while (true) {
		SteamAPI_RunCallbacks();
		Sleep(500);
	}
}

int MessageBoxC(const char* Msg, const char* Title, UINT type = 0) {
	return MessageBoxA(0, Msg, Title, 0);
}

B3DDLL(void) scpSDK_UploadLeaderboard_RefinedItems() {
	// REMOVED
}

B3DDLL(void) scpSDK_UploadLeaderboard_Deaths() {
	// REMOVED
}

B3DDLL(void) scpDiscord_Initialize() {
	discord->Initialize("");
	//std::cout << "Initialized Discord Rich Presence." << std::endl;
	discord->Update("Loading...", true);
	//std::cout << "Set Discord Rich Presence status to \"Loading...\" & reset time counter." << std::endl;
}

B3DDLL(void) scpDiscord_Update(const char* State, bool UpdateTime) {
	discord->Update(State, UpdateTime);
	//std::cout << "Set Discord Rich Presence status to \"" << State << "\"";
	if (UpdateTime) {
		//std::cout << " & reset time counter.";
	}
	//std::cout << std::endl;
}

B3DDLL(void) scpSteam_Initialize() {

	if (!SteamAPI_IsSteamRunning()) {
		//std::cout << "Game failed to launch. Please launch steam before attempting to launch the game!" << std::endl;
		MessageBoxA(0, "Please launch steam before attempting to launch the game!", "SCP: Containment Breach Remastered", 0);
		exit(-1);
	}
	if (!SteamAPI_Init()) {
		//std::cout << "Game failed to launch. Steam API failed to initialize." << std::endl;
		MessageBoxA(0, "Failed to initialize the Steam API.", "SCP: Containment Breach Remastered", 0);
		exit(-1);
	}

	std::thread(SteamLoop).detach();

	//std::cout << "Successfully initialized the Steam API" << std::endl;
}

B3DDLL(void) scpSteam_Shutdown() {
	SteamAPI_Shutdown();
	//std::cout << "Successfully shutdown the Steam API." << std::endl;
}

B3DDLL(void) scpSteam_SetAchievement(const char* AchievementString) {
	SteamUserStats()->SetAchievement(AchievementString);
	SteamUserStats()->StoreStats();
	//std::cout << "Successfully unlocked achievement \"" << AchievementString << "\"" << std::endl;
	return;
}

B3DDLL(void) scpSteam_ResetStats(bool AchievementsToo) {
	if (MessageBoxA(0, "Are you sure you want to reset your stats?", "SCP:CB Remastered", MB_YESNO) == IDYES) {
		SteamUserStats()->ResetAllStats(AchievementsToo);
		SteamUserStats()->StoreStats();
	}
	//std::cout << "Successfully reset stats & achievements." << std::endl;
	return;
}

B3DDLL(void) scpSDK_ResetOptions() {
	if (MessageBoxA(0, "Are you sure you want to reset your options?", "SCP:CB Remastered", MB_YESNO) == IDYES) {
		std::filesystem::remove("options.ini");
		std::filesystem::copy_file("defaultoptions.ini", "options.ini");
	}
}

std::vector<const char*> CommandList = {};
std::vector<const char*> ConsoleText = {};

void DeveloperUI() {
	Window window;
	window.Start(&CommandList, &ConsoleText);

	window.Initialize();
	window.MakeWindow();
	window.MakeDevice();
	window.MakeImGui();

	window.PostInit();
	while (window.exiting) {
		window.BeginRender();
		window.Render();
		window.UpdateSizeStructs();
		window.EndRender();

		std::this_thread::sleep_for(std::chrono::milliseconds(10));
	}
	window.PreDeinit();

	window.DestroyImGui();
	window.DestroyDevice();
	window.DeleteWindow();

	window.Exit();

	exit(EXIT_SUCCESS);
}

B3DDLL(void) scpSDK_DeveloperMenu() {
	if (std::filesystem::exists("developer")) {
		std::thread(DeveloperUI).detach();
	}
}

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

B3DDLL(void) scpSDK_SendConsoleMsg(const char* msg) {
	ConsoleText.push_back(msg);
}