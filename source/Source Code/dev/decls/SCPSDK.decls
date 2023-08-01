.lib "SCPSDK.dll"

scpSteam_Initialize()                               :"_scpSteam_Initialize@0"
scpSteam_Shutdown()                                 :"_scpSteam_Shutdown@0"
scpSteam_SetAchievement(AchievementString$)         :"_scpSteam_SetAchievement@4"
scpSteam_ResetStats(AchievementsToo$)               :"_scpSteam_ResetStats@4"

scpDiscord_Initialize()                             :"_scpDiscord_Initialize@0"
scpDiscord_Update(State$, UpdateTime$)              :"_scpDiscord_Update@8"

scpSDK_ResetOptions()                               :"_scpSDK_ResetOptions@0"
scpSDK_GetCommand$()                                :"_scpSDK_GetCommand@0"
scpSDK_ProcessCmdArgs(args$)                        :"_scpSDK_ProcessCmdArgs@4"
scpSDK_TakeScreenshot()                             :"_scpSDK_TakeScreenshot@0"
scpSDK_MessageBox(msg$, title$)                     :"_scpSDK_MessageBox@8"

scpModding_ProcessFilePath$(givenPath$)             :"_scpModding_ProcessFilePath@4"
scpModding_Version$()                               :"_scpModding_Version@0"
scpModding_GetGameTitle$()                          :"_scpModding_GetGameTitle@0"

scpLang_GetLang$()                                  :"_scpLang_GetLang@0"
scpLang_SetLang(lang$)                              :"_scpLang_SetLang@4"
scpLang_BackLang()                                  :"_scpLang_BackLang@0"
scpLang_NextLang()                                  :"_scpLang_NextLang@0"
scpLang_GetPhrase$(key$)                            :"_scpLang_GetPhrase@4"

scpFont_GetFile$(font$)                             :"_scpFont_GetFile@4"
scpFont_GetSize%(font$)                              :"_scpFont_GetSize@4"