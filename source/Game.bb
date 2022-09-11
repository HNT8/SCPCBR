;steamstate=OpenSteam(2090230)

;If steamstate=-1
;	RuntimeError "Steam failed to initialize!"
;EndIf

scpSteam_Initialize()
scpDiscord_Initialize()
scpDiscord_Update("", True)

Include "Source Code\Constants.bb"

Include "Source Code\Main.bb"

;~IDEal Editor Parameters:
;~C#Blitz3D