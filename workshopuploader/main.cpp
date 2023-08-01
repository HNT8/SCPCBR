#include <cstdlib>
#include <cstdio>
#include <iostream>
#include <chrono>
#include <thread>
#include <fstream>
#include <Windows.h>

#include "SteamAPI/steam_api.h"
#include "SteamAPI/isteamugc.h"

#include "util.h"
#include "process_steam.h"

void close_steam() {
	SteamAPI_Shutdown();
}

int main() {
	SetConsoleTitleA("SCP: Containment Breach Remastered Workshop Uploader");

    if (!SteamAPI_Init() && !SteamUGC()) {
        std::cerr << "Oh no! Steam's API could not be initialized!\n";
        std::cerr << "Try the following solutions:\n";
        std::cerr << "Ensure that Steam is running\n";
        std::cerr << "Ensure that the download isn't corrupted (check if "
            << "'steam_appid.txt' exists as the same folder as this "
            << "program)\n";
        std::cerr << "Ensure that Steam is running on the same user "
            << "account\n";
        Sleep(5000);
        exit(1);
    }

    atexit(close_steam);

    WorkshopUploader* uploader = new WorkshopUploader();
    uploader->create_item();
    while (!uploader->callback_called) {
        SteamAPI_RunCallbacks();
        std::this_thread::sleep_for(std::chrono::milliseconds(100));
    }
}