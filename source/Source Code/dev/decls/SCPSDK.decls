.lib "SCPSDK.dll"

scpSteam_Initialize()                               :"_scpSteam_Initialize@0"
scpSteam_Shutdown()                                 :"_scpSteam_Shutdown@0"
scpSteam_SetAchievement(AchievementString$)         :"_scpSteam_SetAchievement@4"
scpSteam_ResetStats(AchievementsToo$)               :"_scpSteam_ResetStats@4"

scpDiscord_Initialize()                             :"_scpDiscord_Initialize@0"
scpDiscord_Update(State$, UpdateTime$)              :"_scpDiscord_Update@8"

scpSDK_ResetOptions()                               :"_scpSDK_ResetOptions@0"
scpSDK_UploadLeaderboard_Deaths()                   :"_scpSDK_UploadLeaderboard_Deaths@0"
scpSDK_UploadLeaderboard_RefinedItems()             :"_scpSDK_UploadLeaderboard_RefinedItems@0"
scpSDK_DeveloperMenu()                              :"_scpSDK_DeveloperMenu@0"
scpSDK_GetCommand$()                                 :"_scpSDK_GetCommand@0"
scpSDK_SendConsoleMsg(msg$)                         :"_scpSDK_SendConsoleMsg@4"