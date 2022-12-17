#pragma once

#include "DiscordAPI/discord_register.h"
#include "DiscordAPI/discord_rpc.h"
#include <Windows.h>

class Discord {
public:
	void Initialize(const char* steamid);
	void Update(const char* state, bool updateTime);
};