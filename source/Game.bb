; SCPCBR v1.6
scpSDK_ProcessCmdArgs(CommandLine$())

scpSteam_Initialize()
scpDiscord_Initialize()
scpDiscord_Update("", True)

scpLang_SetLang(GetINIString(OptionFile, "game", "lang", True))

Include "Source Code\Constants.bb"

Include "Source Code\Main.bb"

;~IDEal Editor Parameters:
;~C#Blitz3D TSS