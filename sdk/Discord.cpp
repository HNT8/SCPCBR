#include "Discord.h"

#include <chrono>

uint64_t timeSinceEpoch() {
    using namespace std::chrono;
    return duration_cast<seconds>(system_clock::now().time_since_epoch()).count();
}

void Discord::Initialize(const char* steamid) {
	DiscordEventHandlers Handle;
	memset(&Handle, 0, sizeof(Handle));
	Discord_Initialize("767487208684650536", &Handle, 1, steamid);
}

uint64_t SelectedTime = 0;

void Discord::Update(const char* state, bool updateTime) {
    DiscordRichPresence discordPresence;
    memset(&discordPresence, 0, sizeof(discordPresence));
    discordPresence.state = state;
    if (updateTime) SelectedTime = timeSinceEpoch();
    discordPresence.startTimestamp = SelectedTime;
    discordPresence.largeImageKey = "scpcbr";
    Discord_UpdatePresence(&discordPresence);
}