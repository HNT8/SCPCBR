; steam blitz interface

.lib "steamstub.dll"

OpenSteam%(gameid):OpenSteam
ReadSteam$():ReadSteam
GetSteamStat%(id$):GetSteamStat
SetSteamStat(id$,value):SetSteamStat
GetSteamAchievement%(id$):GetSteamAchievement
SetSteamAchievement(id$):SetSteamAchievement
StoreSteamStats():StoreSteamStats
