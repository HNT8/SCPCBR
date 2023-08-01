Type INIFile
	Field name$
	Field bank%
	Field bankOffset% = 0
	Field size%
End Type

Function ReadINILine$(file.INIFile)
	Local rdbyte%
	Local firstbyte% = True
	Local offset% = file\bankOffset
	Local bank% = file\bank
	Local retStr$ = ""
	rdbyte = PeekByte(bank,offset)
	While ((firstbyte) Or ((rdbyte<>13) And (rdbyte<>10))) And (offset<file\size)
		rdbyte = PeekByte(bank,offset)
		If ((rdbyte<>13) And (rdbyte<>10)) Then
			firstbyte = False
			retStr=retStr+Chr(rdbyte)
		EndIf
		offset=offset+1
	Wend
	file\bankOffset = offset
	Return retStr
End Function

Function UpdateINIFile$(filename$)
	Local file.INIFile = Null
	For k.INIFile = Each INIFile
		If k\name = Lower(filename) Then
			file = k
		EndIf
	Next
	
	If file=Null Then Return
	
	If file\bank<>0 Then FreeBank file\bank
	Local f% = ReadFile(file\name)
	Local fleSize% = 1
	While fleSize<FileSize(file\name)
		fleSize=fleSize*2
	Wend
	file\bank = CreateBank(fleSize)
	file\size = 0
	While Not Eof(f)
		PokeByte(file\bank,file\size,ReadByte(f))
		file\size=file\size+1
	Wend
	CloseFile(f)
End Function

Function GetINIString$(file$, section$, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	
	Local lfile.INIFile = Null
	For k.INIFile = Each INIFile
		If k\name = Lower(file) Then
			lfile = k
		EndIf
	Next
	
	If lfile = Null Then
		lfile = New INIFile
		lfile\name = Lower(file)
		lfile\bank = 0
		UpdateINIFile(lfile\name)
	EndIf
	
	lfile\bankOffset = 0
	
	section = Lower(section)
	
	;While Not Eof(f)
	While lfile\bankOffset<lfile\size
		Local strtemp$ = ReadINILine(lfile)
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			If Mid(strtemp, 2, Len(strtemp)-2)=section Then
				Repeat
					TemporaryString = ReadINILine(lfile)
					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
						;CloseFile f
						Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
					EndIf
				Until (Left(TemporaryString, 1) = "[") Or (lfile\bankOffset>=lfile\size)
				
				;CloseFile f
				Return defaultvalue
			EndIf
		EndIf
	Wend
	
	Return defaultvalue
End Function

Function GetINIInt%(file$, section$, parameter$, defaultvalue% = 0)
	Local txt$ = GetINIString(file$, section$, parameter$, defaultvalue)
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function

Function GetINIFloat#(file$, section$, parameter$, defaultvalue# = 0.0)
	Return Float(GetINIString(file$, section$, parameter$, defaultvalue))
End Function


Function GetINIString2$(file$, start%, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	Local f% = ReadFile(file)
	
	Local n%=0
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		n=n+1
		If n=start Then 
			Repeat
				TemporaryString = ReadLine(f)
				If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
					CloseFile f
					Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
				EndIf
			Until Left(TemporaryString, 1) = "[" Or Eof(f)
			CloseFile f
			Return defaultvalue
		EndIf
	Wend
	
	CloseFile f	
	
	Return defaultvalue
End Function

Function GetINIInt2%(file$, start%, parameter$, defaultvalue$="")
	Local txt$ = GetINIString2(file$, start%, parameter$, defaultvalue$)
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function

Function GetINISectionLocation%(file$, section$)
	Local Temp%
	Local f% = ReadFile(file)
	
	section = Lower(section)
	
	Local n%=0
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		n=n+1
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			Temp = Instr(strtemp, section)
			If Temp>0 Then
				If Mid(strtemp, Temp-1, 1)="[" Or Mid(strtemp, Temp-1, 1)="|" Then
					CloseFile f
					Return n
				EndIf
			EndIf
		EndIf
	Wend
	
	CloseFile f
End Function

Function PutINIValue%(file$, INI_sSection$, INI_sKey$, INI_sValue$)
	
	; Returns: True (Success) Or False (Failed)
	
	INI_sSection = "[" + Trim$(INI_sSection) + "]"
	Local INI_sUpperSection$ = Upper$(INI_sSection)
	INI_sKey = Trim$(INI_sKey)
	INI_sValue = Trim$(INI_sValue)
	Local INI_sFilename$ = file$
	
	; Retrieve the INI Data (If it exists)
	
	Local INI_sContents$ = INI_FileToString(INI_sFilename)
	
		; (Re)Create the INI file updating/adding the SECTION, KEY And VALUE
	
	Local INI_bWrittenKey% = False
	Local INI_bSectionFound% = False
	Local INI_sCurrentSection$ = ""
	
	Local INI_lFileHandle% = WriteFile(INI_sFilename)
	If INI_lFileHandle = 0 Then Return False ; Create file failed!
	
	Local INI_lOldPos% = 1
	Local INI_lPos% = Instr(INI_sContents, Chr$(0))
	
	While (INI_lPos <> 0)
		
		Local INI_sTemp$ = Mid$(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos))
		
		If (INI_sTemp <> "") Then
			
			If Left$(INI_sTemp, 1) = "[" And Right$(INI_sTemp, 1) = "]" Then
				
					; Process SECTION
				
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				End If
				INI_sCurrentSection = Upper$(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
				
			Else
				If Left(INI_sTemp, 1) = ":" Then
					WriteLine INI_lFileHandle, INI_sTemp
				Else
						; KEY=VALUE				
					Local lEqualsPos% = Instr(INI_sTemp, "=")
					If (lEqualsPos <> 0) Then
						If (INI_sCurrentSection = INI_sUpperSection) And (Upper$(Trim$(Left$(INI_sTemp, (lEqualsPos - 1)))) = Upper$(INI_sKey)) Then
							If (INI_sValue <> "") Then INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
							INI_bWrittenKey = True
						Else
							WriteLine INI_lFileHandle, INI_sTemp
						End If
					End If
				EndIf
				
			End If
			
		End If
		
			; Move through the INI file...
		
		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr$(0), INI_lOldPos)
		
	Wend
	
		; KEY wasn;t found in the INI file - Append a New SECTION If required And create our KEY=VALUE Line
	
	If (INI_bWrittenKey = False) Then
		If (INI_bSectionFound = False) Then INI_CreateSection INI_lFileHandle, INI_sSection
		INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
	End If
	
	CloseFile INI_lFileHandle
	
	Return True ; Success
	
End Function

Function INI_FileToString$(INI_sFilename$)
	
	Local INI_sString$ = ""
	Local INI_lFileHandle%= ReadFile(INI_sFilename)
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine$(INI_lFileHandle) + Chr$(0)
		Wend
		CloseFile INI_lFileHandle
	End If
	Return INI_sString
	
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine INI_lFileHandle, "" ; Blank Line between sections
	WriteLine INI_lFileHandle, INI_sNewSection
	Return INI_sNewSection
	
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	
	WriteLine INI_lFileHandle, INI_sKey + " = " + INI_sValue
	Return True
	
End Function

;[LAUNCHER]

Global LauncherWidth%= Min(GetINIInt(OptionFile, "launcher", "launcher width"), 1024)
Global LauncherHeight% = Min(GetINIInt(OptionFile, "launcher", "launcher height"), 768)
Global LauncherEnabled% = GetINIInt(OptionFile, "launcher", "launcher enabled")

;[GRAPHICS]

Global BumpEnabled% = GetINIInt(OptionFile, "graphics", "bump mapping enabled")

Global Vsync% = GetINIInt(OptionFile, "graphics", "vsync")

Global Opt_AntiAlias = GetINIInt(OptionFile, "graphics", "antialias")

Global EnableRoomLights% = GetINIInt(OptionFile, "graphics", "room lights enabled")

Global ScreenGamma# = GetINIFloat(OptionFile, "graphics", "screengamma")

Global ParticleAmount% = GetINIInt(OptionFile,"graphics","particle amount")

Global TextureDetails% = GetINIInt(OptionFile, "graphics", "texture details")

Global SaveTexturesInVRam% = GetINIInt(OptionFile, "graphics", "enable vram")

;{~--<MOD>--~}

Global FOV# = GetINIFloat(OptionFile, "graphics", "fov")

;{~--<END>--~}

;[AUDIO]

Global MusicVolume# = GetINIFloat(OptionFile, "audio", "music volume")
Global SFXVolume# = GetINIFloat(OptionFile, "audio", "sound volume")

Global EnableSFXRelease% = GetINIInt(OptionFile, "audio", "sfx release")

Global EnableUserTracks% = GetINIInt(OptionFile, "audio", "enable user tracks")
Global UserTrackMode% = GetINIInt(OptionFile, "audio", "user track setting")

;[CONTROLS]

Global MouseSensitivity# = GetINIFloat(OptionFile, "controls", "mouse sensitivity")

Global InvertMouse% = GetINIInt(OptionFile, "controls", "invert mouse y")

Global MouseSmooth# = GetINIFloat(OptionFile, "controls", "mouse smoothing")

Global KEY_RIGHT = GetINIInt(OptionFile, "controls", "Right key")
Global KEY_LEFT = GetINIInt(OptionFile, "controls", "Left key")
Global KEY_UP = GetINIInt(OptionFile, "controls", "Up key")
Global KEY_DOWN = GetINIInt(OptionFile, "controls", "Down key")

Global KEY_BLINK = GetINIInt(OptionFile, "controls", "Blink key")
Global KEY_SPRINT = GetINIInt(OptionFile, "controls", "Sprint key")
Global KEY_INV = GetINIInt(OptionFile, "controls", "Inventory key")
Global KEY_CROUCH = GetINIInt(OptionFile, "controls", "Crouch key")
Global KEY_SAVE = GetINIInt(OptionFile, "controls", "Save key")
Global KEY_CONSOLE = GetINIInt(OptionFile, "controls", "Console key")

;{~--<MOD>--~}

Global KEY_SCREENSHOT = GetINIInt(OptionFile, "controls", "Screenshot key")

;{~--<END>--~}

;[ADVANCED]

Global HUDenabled% = GetINIInt(OptionFile, "advanced", "HUD enabled")

Global CanOpenConsole% = GetINIInt(OptionFile, "advanced", "console enabled")
Global ConsoleOpening% = GetINIInt(OptionFile, "advanced", "console auto opening")
Global ConsoleVersion% = GetINIInt(OptionFile, "advanced", "console version")

Global AchvMSGenabled% = GetINIInt(OptionFile, "advanced", "achievement popup enabled")

Global ShowFPS = GetINIInt(OptionFile, "advanced", "show FPS")
Global Framelimit% = GetINIInt(OptionFile, "advanced", "framelimit")

Global AATextEnable% = GetINIInt(OptionFile, "advanced", "antialiased text")

;[GAME]

Global IntroEnabled% = GetINIInt(OptionFile, "game", "intro enabled")

Global Brightness% = GetINIFloat(OptionFile, "game", "brightness")

Global CameraFogNear# = GetINIFloat(OptionFile, "game", "camera fog near")
Global CameraFogFar# = GetINIFloat(OptionFile, "game", "camera fog far")

;{~--<MOD>--~}

Global UnlockThaumiel% = GetINIInt(OptionFile, "game", "th") ;th = Thaumiel Difficulty

;{~--<END>--~}

Global MapWidth% = GetINIInt(OptionFile, "game", "map size")
Global MapHeight% = GetINIInt(OptionFile, "game", "map size")

Global FogR = GetINIInt(OptionFile, "game", "fog r")
Global FogG = GetINIInt(OptionFile, "game", "fog g")
Global FogB = GetINIInt(OptionFile, "game", "fog b")

;[GLOBAL OPTIONS]

Global GraphicWidth% = GetINIInt(OptionFile, "options", "width")
Global GraphicHeight% = GetINIInt(OptionFile, "options", "height")

Global Fullscreen% = GetINIInt(OptionFile, "options", "fullscreen")
Global BorderlessWindowed% = GetINIInt(OptionFile, "options", "borderless windowed")

Global SelectedGFXDriver% = Max(GetINIInt(OptionFile, "options", "gfx driver"), 1)

Global Bit16Mode = GetINIInt(OptionFile, "options", "16bit")

Global PlayStartUp% = GetINIInt(OptionFile,"options","play startup video")

;Save options to .ini.
Function SaveOptionsINI()
	
	PutINIValue(OptionFile, "controls", "mouse sensitivity", MouseSensitivity)
	PutINIValue(OptionFile, "controls", "invert mouse y", InvertMouse)
	PutINIValue(OptionFile, "graphics", "bump mapping enabled", BumpEnabled)			
	PutINIValue(OptionFile, "advanced", "HUD enabled", HUDenabled)
	PutINIValue(OptionFile, "graphics", "screengamma", ScreenGamma)
	PutINIValue(OptionFile, "graphics", "antialias", Opt_AntiAlias)
	PutINIValue(OptionFile, "graphics", "vsync", Vsync)
	PutINIValue(OptionFile, "advanced", "show FPS", ShowFPS)
	PutINIValue(OptionFile, "advanced", "framelimit", Framelimit%)
	PutINIValue(OptionFile, "advanced", "achievement popup enabled", AchvMSGenabled%)
	PutINIValue(OptionFile, "graphics", "room lights enabled", EnableRoomLights%)
	PutINIValue(OptionFile, "graphics", "texture details", TextureDetails%)
	PutINIValue(OptionFile, "advanced", "console enabled", CanOpenConsole%)
	PutINIValue(OptionFile, "advanced", "console auto opening", ConsoleOpening%)
	PutINIValue(OptionFile, "advanced", "antialiased text", AATextEnable)
	PutINIValue(OptionFile, "graphics", "particle amount", ParticleAmount)
	PutINIValue(OptionFile, "graphics", "enable vram", SaveTexturesInVRam)
	PutINIValue(OptionFile, "controls", "mouse smoothing", mouse_smooth)
	
	;{~--<MOD>--~}
	
	PutINIValue(OptionFile, "graphics", "fov", Int(FOV))
	PutINIValue(OptionFile, "game", "th", UnlockThaumiel%)
	PutINIValue(OptionFile, "advanced", "console version", ConsoleVersion%)
	
	;{~--<END>--~}

	PutINIValue(OptionFile, "audio", "music volume", MusicVolume)
	PutINIValue(OptionFile, "audio", "sound volume", PrevSFXVolume)
	PutINIValue(OptionFile, "audio", "sfx release", EnableSFXRelease)
	PutINIValue(OptionFile, "audio", "enable user tracks", EnableUserTracks%)
	PutINIValue(OptionFile, "audio", "user track setting", UserTrackMode%)
	
	PutINIValue(OptionFile, "controls", "Right key", KEY_RIGHT)
	PutINIValue(OptionFile, "controls", "Left key", KEY_LEFT)
	PutINIValue(OptionFile, "controls", "Up key", KEY_UP)
	PutINIValue(OptionFile, "controls", "Down key", KEY_DOWN)
	PutINIValue(OptionFile, "controls", "Blink key", KEY_BLINK)
	PutINIValue(OptionFile, "controls", "Sprint key", KEY_SPRINT)
	PutINIValue(OptionFile, "controls", "Inventory key", KEY_INV)
	PutINIValue(OptionFile, "controls", "Crouch key", KEY_CROUCH)
	PutINIValue(OptionFile, "controls", "Save key", KEY_SAVE)
	PutINIValue(OptionFile, "controls", "Console key", KEY_CONSOLE)
	
	;{~--<MOD>--~}
	
	PutINIValue(OptionFile, "controls", "Screenshot key", KEY_SCREENSHOT)
	
	;{~--<END>--~}
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D