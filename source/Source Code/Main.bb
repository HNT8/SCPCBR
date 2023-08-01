Local InitErrorStr$ = ""
If FileSize("fmod.dll")=0 Then InitErrorStr=InitErrorStr+ "fmod.dll"+Chr(13)+Chr(10)
If FileSize("FreeImage.dll")=0 Then InitErrorStr=InitErrorStr+ "FreeImage.dll"+Chr(13)+Chr(10)
If FileSize("dplayx.dll")=0 Then InitErrorStr=InitErrorStr+ "dplayx.dll"+Chr(13)+Chr(10)
If FileSize("BlitzMovie.dll")=0 Then InitErrorStr=InitErrorStr+ "BlitzMovie.dll"+Chr(13)+Chr(10)
If FileSize("d3dim.dll")=0 Then InitErrorStr=InitErrorStr+"d3dim.dll"+Chr(13)+Chr(10)
If FileSize("d3dim700.dll")=0 Then InitErrorStr=InitErrorStr+"d3dim700.dll"+Chr(13)+Chr(10)
If FileSize("steam_api.dll")=0 Then InitErrorStr=InitErrorStr+"steam_api.dll"+Chr(13)+Chr(10)

If Len(InitErrorStr)>0 Then
	RuntimeError "The following DLLs were not found in the game directory:"+Chr(13)+Chr(10)+Chr(13)+Chr(10)+InitErrorStr
EndIf

Include "Source Code\FMod.bb"
Include "Source Code\Strict_Loads.bb"
Include "Source Code\Fullscreen_Window_Fix.bb"
Include "Source Code\Key_Name.bb"
Include "Source Code\Devil_Particle_System.bb"
Include "Source Code\Math.bb"
Include "Source Code\INI_Details.bb"

Global ModCompatibleNumber$ = "5.5.4.1" ; UE COMPATIBLE

Type Fonts
    Field Font%[MaxFontAmount-1]
    Field ConsoleFont% 
    Field CreditsFont%[MaxCreditsFontAmount-1]
End Type

Local fo.Fonts = New Fonts

Global ButtonSFX%

Global EnableSFXRelease_Prev% = EnableSFXRelease%

Dim ArrowIMG(4)

;[Block]

Global LauncherIMG%

Global Depth% = 0

Global SelectedGFXMode%

Global fresize_image%, fresize_texture%, fresize_texture2%
Global fresize_cam%

Global WireframeState

Global TotalGFXModes% = CountGfxModes3D(), GFXModes%
Dim GfxModeWidths%(TotalGFXModes), GfxModeHeights%(TotalGFXModes)

Global RealGraphicWidth%,RealGraphicHeight%
Global AspectRatioRatio#

Global TextureFloat#
Select TextureDetails%
	Case 0
		TextureFloat# = 0.8
	Case 1
		TextureFloat# = 0.4
	Case 2
		TextureFloat# = 0.0
	Case 3
		TextureFloat# = -0.4
	Case 4
		TextureFloat# = -0.8
End Select

Include "Source Code\AAText.bb"

If LauncherEnabled Then 
	AspectRatioRatio = 1.0
	UpdateLauncher()
	
	;New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode --Love Mark,
	If BorderlessWindowed
		Graphics3DExt G_viewport_width, G_viewport_height, 0, 2
		
		; -- Change the window style to 'WS_POPUP' and then set the window position to force the style to update.
		api_SetWindowLong( G_app_handle, C_GWL_STYLE, C_WS_POPUP )
		api_SetWindowPos( G_app_handle, C_HWND_TOP, G_viewport_x, G_viewport_y, G_viewport_width, G_viewport_height, C_SWP_SHOWWINDOW )
		
		RealGraphicWidth = G_viewport_width
		RealGraphicHeight = G_viewport_height
		
		AspectRatioRatio = (Float(GraphicWidth)/Float(GraphicHeight))/(Float(RealGraphicWidth)/Float(RealGraphicHeight))
		
		Fullscreen = False
	Else
		AspectRatioRatio = 1.0
		RealGraphicWidth = GraphicWidth
		RealGraphicHeight = GraphicHeight
		If Fullscreen Then
			Graphics3DExt(GraphicWidth, GraphicHeight, (16*Bit16Mode), 1)
		Else
			Graphics3DExt(GraphicWidth, GraphicHeight, 0, 2)
		End If
	EndIf
	
Else
	For i% = 1 To TotalGFXModes
		Local samefound% = False
		For  n% = 0 To TotalGFXModes - 1
			If GfxModeWidths(n) = GfxModeWidth(i) And GfxModeHeights(n) = GfxModeHeight(i) Then samefound = True : Exit
		Next
		If samefound = False Then
			If GraphicWidth = GfxModeWidth(i) And GraphicHeight = GfxModeHeight(i) Then SelectedGFXMode = GFXModes
			GfxModeWidths(GFXModes) = GfxModeWidth(i)
			GfxModeHeights(GFXModes) = GfxModeHeight(i)
			GFXModes=GFXModes+1
		End If
	Next
	
	GraphicWidth = GfxModeWidths(SelectedGFXMode)
	GraphicHeight = GfxModeHeights(SelectedGFXMode)
	
	;New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode --Love Mark,
	If BorderlessWindowed
		Graphics3DExt G_viewport_width, G_viewport_height, 0, 2
		
		; -- Change the window style to 'WS_POPUP' and then set the window position to force the style to update.
		api_SetWindowLong( G_app_handle, C_GWL_STYLE, C_WS_POPUP )
		api_SetWindowPos( G_app_handle, C_HWND_TOP, G_viewport_x, G_viewport_y, G_viewport_width, G_viewport_height, C_SWP_SHOWWINDOW )
		
		RealGraphicWidth = G_viewport_width
		RealGraphicHeight = G_viewport_height
		
		AspectRatioRatio = (Float(GraphicWidth)/Float(GraphicHeight))/(Float(RealGraphicWidth)/Float(RealGraphicHeight))
		
		Fullscreen = False
	Else
		AspectRatioRatio = 1.0
		RealGraphicWidth = GraphicWidth
		RealGraphicHeight = GraphicHeight
		If Fullscreen Then
			Graphics3DExt(GraphicWidth, GraphicHeight, (16*Bit16Mode), 1)
		Else
			Graphics3DExt(GraphicWidth, GraphicHeight, 0, 2)
		End If
	EndIf
	
EndIf

Global MenuScale# = (GraphicHeight / 1024.0)

SetBuffer(BackBuffer())

Type FPS_Settings
    Field CurTime%
    Field PrevTime%
    Field LoopDelay%
    Field FPSfactor#[2]
    Field PrevFPSFactor#
    Field CheckFPS%
    Field ElapsedLoops%
    Field FPS%
    Field ElapsedTime#
End Type

Local fs.FPS_Settings = New FPS_Settings

Global CurrFrameLimit# = (Framelimit%-19)/100.0

SeedRnd MilliSecs()

;[End block]

Global GameSaved%

Global CanSave% = True

AppTitle "SCP: Containment Breach Remastered " + scpModding_Version()

PlayStartupVideos()

;---------------------------------------------------------------------------------------------------------------------

;[Block]

Global CursorIMG% = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"cursor.png"))

Global SelectedLoadingScreen.LoadingScreens, LoadingScreenAmount%, LoadingScreenText%
Global LoadingBack% = LoadImage_Strict(scpModding_ProcessFilePath$("Loadingscreens\loadingback.png"))
InitLoadingScreens(scpModding_ProcessFilePath$("Loadingscreens\loadingscreens.ini"))

InitAAFont()
;For some reason, Blitz3D doesn't load fonts that have filenames that
;don't match their "internal name" (i.e. their display name in applications
;like Word and such). As a workaround, I moved the files and renamed them so they
;can load without FastText.
fo\Font[0] = AALoadFont("GFX\font\cour\Courier New.ttf", Int(19 * (GraphicHeight / 1024.0)), 0,0,0)
fo\Font[1] = AALoadFont("GFX\font\courbd\Courier New.ttf", Int(58 * (GraphicHeight / 1024.0)), 0,0,0)
fo\Font[2] = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(22 * (GraphicHeight / 1024.0)), 0,0,0)
fo\Font[3] = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(58 * (GraphicHeight / 1024.0)), 0,0,0) ;60
fo\Font[4] = AALoadFont("GFX\font\Journal\Journal.ttf", Int(58 * (GraphicHeight / 1024.0)), 0,0,0)

fo\ConsoleFont% = AALoadFont("Blitz", Int(20 * (GraphicHeight / 1024.0)), 0,0,0,1)

AASetFont fo\Font[1]

Global BlinkMeterIMG% = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"blinkmeter.png"))

DrawLoading(0, True)

; - -Viewport.
Global viewport_center_x% = GraphicWidth / 2, viewport_center_y% = GraphicHeight / 2

; -- Mouselook.
Global mouselook_x_inc# = 0.3 ; This sets both the sensitivity and direction (+/-) of the mouse on the X axis.
Global mouselook_y_inc# = 0.3 ; This sets both the sensitivity and direction (+/-) of the mouse on the Y axis.
; Used to limit the mouse movement to within a certain number of pixels (250 is used here) from the center of the screen. This produces smoother mouse movement than continuously moving the mouse back to the center each loop.
Global mouse_left_limit% = 250, mouse_right_limit% = GraphicsWidth () - 250
Global mouse_top_limit% = 150, mouse_bottom_limit% = GraphicsHeight () - 150 ; As above.
Global mouse_x_speed_1#, mouse_y_speed_1#
Global mouse_smooth% = GetINIInt(OptionFile, "options", "mouse smoothing", True)
Global mouse_x_leverTurn# = 0 : Global mouse_y_leverTurn# = 0

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#
Global Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#
Global Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

;player stats -------------------------------------------------------------------------------------------------------
Global Sanity#, ForceMove#, ForceAngle#
Global RestoreSanity%

Global Playable% = True

Global KillTimer#, KillAnim%, FallTimer#, DeathTimer#

Global Stamina#, StaminaEffect#=1.0, StaminaEffectTimer#

Global SCP1025state#[6]

Global HeartBeatRate#, HeartBeatTimer#, HeartBeatVolume#

Global WearingGasMask%, WearingHazmat%, WearingVest%, WearingNightVision%
Global NVTimer#

Global SuperMan%, SuperManTimer#

Global CameraShakeTimer#, Vomit%, VomitTimer#, Regurgitate%

Global BLINKFREQ#
Global BlinkTimer#, EyeIrritation#, EyeStuck#, BlinkEffect# = 1.0, BlinkEffectTimer#

Global Injuries#, Bloodloss#, HealTimer#

Global RefinedItems%

;{~--<MOD>--~}

Global UsedMorphine%, MorphineTimer#, MorphineHealAmount# 
 
Global ChanceToSpawn005% = Rand(3)

Global GasmaskBlurTimer#

Global WearingHelmet%

;{~--<END>--~}

Include "Source Code\Achievements.bb"

;player coordinates, angle, speed, movement etc ---------------------------------------------------------------------
Global DropSpeed#, HeadDropSpeed#, CurrSpeed#
Global user_camera_pitch#, side#
Global Crouch%, CrouchState#

Global PlayerZone%, PlayerRoom.Rooms

Global GrabbedEntity%

Global MouseHit1%, MouseDown1%, MouseHit2%, DoubleClick%, LastMouseHit1%, MouseUp1%

Global NoClipSpeed# = 2.0

Type Cheats
    Field GodMode%
    Field NoClip%
    Field NoTarget%
    Field Cheats%
    Field InfiniteStamina%
    Field NoBlinking%
End Type

Global chs.Cheats = New Cheats

Global CoffinDistance# = 100.0

Global PlayerSoundVolume#

;camera/lighting effects (blur, camera shake, etc)-------------------------------------------------------------------
Global Shake#

Global ExplosionTimer#, ExplosionSFX%

Global LightsOn% = True

Global SoundTransmission%

;menus, GUI ---------------------------------------------------------------------------------------------------------

Type Menu_Settings
    Field MainMenuOpen%
    Field MenuOpen%
    Field StopHidingTimer#
    Field MainMenuTab%
    Field MenuStr$
    Field MenuStrX%
    Field MenuStrY%
    Field MenuBlinkTimer%[2]
    Field MenuBlinkDuration%[2]
End Type

Global MenuWhite%, MenuBlack%

Global ms.Menu_Settings = New Menu_Settings

Global InvOpen%
Global OtherOpen.Items = Null

Type Ending
    Field Timer#
    Field Screen%
    Field SelectedEnding$
End Type

Global I_END.Ending = New Ending

Global MsgTimer#, Msg$, DeathMSG$

Global AccessCode%, KeypadInput$, KeypadTimer#, KeypadMSG$

Global DrawHandIcon%
Dim DrawArrowIcon%(4)

;----------------------------------------------  Misc -----------------------------------------------------

Include "Source Code\Difficulty.bb"

Global MTFtimer#, MTFrooms.Rooms[10], MTFroomState%[10]

Dim RadioState#(10)
Dim RadioState3%(10)
Dim RadioState4%(9)
Dim RadioCHN%(8)

Global PlayTime%
Global ConsoleFlush%
Global ConsoleFlushSnd% = 0, ConsoleMusFlush% = 0, ConsoleMusPlay% = 0

Global IsNVGBlinking% = False

;{~--<MOD>--~}

Global MTF2timer#, MTF2rooms.Rooms[10], MTF2roomState%[10]

;{~--<END>--~}

;----------------------------------------------  Console -----------------------------------------------------

Global ConsoleOpen%, ConsoleInput$
Global ConsoleScroll#,ConsoleScrollDragging%
Global ConsoleMouseMem%
Global ConsoleReissue.ConsoleMsg = Null
Global ConsoleR% = 255,ConsoleG% = 255,ConsoleB% = 255

Type ConsoleMsg
	Field txt$
	Field isCommand%
	Field r%,g%,b%
End Type

Function CreateConsoleMsg(txt$,r%=-1,g%=-1,b%=-1,isCommand%=False)
	Local c.ConsoleMsg = New ConsoleMsg
	Insert c Before First ConsoleMsg
	
	c\txt = txt
	c\isCommand = isCommand
	
	c\r = r
	c\g = g
	c\b = b
	
	If (c\r<0) Then c\r = ConsoleR
	If (c\g<0) Then c\g = ConsoleG
	If (c\b<0) Then c\b = ConsoleB
End Function

Function UpdateConsole()
   	Local fo.Fonts = First Fonts
	Local e.Events
	
	If CanOpenConsole = False Then
		ConsoleOpen = False
		Return
	EndIf
	
	If SelectedDifficulty\menu = False Then
		HUDenabled = 0
		CanOpenConsole = 0
		ConsoleOpening = 0
		AchvMSGenabled% = 0
		ScreenGamma = 0.9
		FOV# = 74
		CameraFogNear = 0.35
		CameraFogFar = 4.5
		Return
	EndIf

	If ConsoleOpen Then
		Local cm.ConsoleMsg
		
		AASetFont fo\ConsoleFont
		
		ConsoleR = 255 : ConsoleG = 255 : ConsoleB = 255
		
		If ConsoleVersion = 1 Then
		    Local x% = 0, y% = GraphicHeight-300*MenuScale, width% = GraphicWidth, height% = 300*MenuScale-30*MenuScale
		Else
		    x% = 20
		    y% = 40
		    width% = 400
		    height% = 500
		EndIf
		
		Local StrTemp$, temp%,  i%
		Local ev.Events, r.Rooms, it.Items
		
		DrawFrame x,y,width,height+30*MenuScale
		
		Local consoleHeight% = 0
		Local scrollbarHeight% = 0
		For cm.ConsoleMsg = Each ConsoleMsg
			consoleHeight = consoleHeight + 15*MenuScale
		Next
		scrollbarHeight = (Float(height)/Float(consoleHeight))*height
		If scrollbarHeight>height Then scrollbarHeight = height
		If consoleHeight<height Then consoleHeight = height
		
		Color 50,50,50
		inBar% = MouseOn(x+width-26*MenuScale,y,26*MenuScale,height)
		If inBar Then Color 70,70,70
		Rect x+width-26*MenuScale,y,26*MenuScale,height,True
		
		
		Color 120,120,120
		inBox% = MouseOn(x+width-23*MenuScale,y+height-scrollbarHeight+(ConsoleScroll*scrollbarHeight/height),20*MenuScale,scrollbarHeight)
		If inBox Then Color 200,200,200
		If ConsoleScrollDragging Then Color 255,255,255
		Rect x+width-23*MenuScale,y+height-scrollbarHeight+(ConsoleScroll*scrollbarHeight/height),20*MenuScale,scrollbarHeight,True
		
		If Not MouseDown(1) Then
			ConsoleScrollDragging=False
		ElseIf ConsoleScrollDragging Then
			ConsoleScroll = ConsoleScroll+((ScaledMouseY()-ConsoleMouseMem)*height/scrollbarHeight)
			ConsoleMouseMem = ScaledMouseY()
		EndIf
		
		If (Not ConsoleScrollDragging) Then
			If MouseHit1 Then
				If inBox Then
					ConsoleScrollDragging=True
					ConsoleMouseMem = ScaledMouseY()
				ElseIf inBar Then
					ConsoleScroll = ConsoleScroll+((ScaledMouseY()-(y+height))*consoleHeight/height+(height/2))
					ConsoleScroll = ConsoleScroll/2
				EndIf
			EndIf
		EndIf
		
		mouseScroll = MouseZSpeed()
		If mouseScroll=1 Then
			ConsoleScroll = ConsoleScroll - 15*MenuScale
		ElseIf mouseScroll=-1 Then
			ConsoleScroll = ConsoleScroll + 15*MenuScale
		EndIf
		
		Local reissuePos%
		If KeyHit(200) Then
			reissuePos% = 0
			If (ConsoleReissue=Null) Then
				ConsoleReissue=First ConsoleMsg
				
				While (ConsoleReissue<>Null)
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos - 15*MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
				
			Else
				cm.ConsoleMsg = First ConsoleMsg
				While cm<>Null
					If cm=ConsoleReissue Then Exit
					reissuePos = reissuePos-15*MenuScale
					cm = After cm
				Wend
				ConsoleReissue = After ConsoleReissue
				reissuePos = reissuePos-15*MenuScale
				
				While True
					If (ConsoleReissue=Null) Then
						ConsoleReissue=First ConsoleMsg
						reissuePos = 0
					EndIf
				
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos - 15*MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue<>Null Then
				ConsoleInput = ConsoleReissue\txt
				ConsoleScroll = reissuePos+(height/2)
			EndIf
		EndIf
		
		If KeyHit(208) Then
			reissuePos% = -consoleHeight+15*MenuScale
			If (ConsoleReissue=Null) Then
				ConsoleReissue=Last ConsoleMsg
				
				While (ConsoleReissue<>Null)
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos + 15*MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
				
			Else
				cm.ConsoleMsg = Last ConsoleMsg
				While cm<>Null
					If cm=ConsoleReissue Then Exit
					reissuePos = reissuePos+15*MenuScale
					cm = Before cm
				Wend
				ConsoleReissue = Before ConsoleReissue
				reissuePos = reissuePos+15*MenuScale
				
				While True
					If (ConsoleReissue=Null) Then
						ConsoleReissue=Last ConsoleMsg
						reissuePos=-consoleHeight+15*MenuScale
					EndIf
				
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos + 15*MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue<>Null Then
				ConsoleInput = ConsoleReissue\txt
				ConsoleScroll = reissuePos+(height/2)
			EndIf
		EndIf
		
		If ConsoleScroll<-consoleHeight+height Then ConsoleScroll = -consoleHeight+height
		If ConsoleScroll>0 Then ConsoleScroll = 0
		
		Color 255, 255, 255
		
		SelectedInputBox = 2
		Local oldConsoleInput$ = ConsoleInput
		ConsoleInput = InputBox(x, y + height, width, 30*MenuScale, ConsoleInput, 2)
		If oldConsoleInput<>ConsoleInput Then
			ConsoleReissue = Null
		EndIf
		ConsoleInput = Left(ConsoleInput, 100)
		
		If KeyHit(28) And ConsoleInput <> "" Then
			ConsoleReissue = Null
			ConsoleScroll = 0
			CreateConsoleMsg(ConsoleInput,255,255,0,True)
			If Instr(ConsoleInput, " ") > 0 Then
				StrTemp$ = Lower(Left(ConsoleInput, Instr(ConsoleInput, " ") - 1))
			Else
				StrTemp$ = Lower(ConsoleInput)
			EndIf
			
			Select Lower(StrTemp)
				Case "help"
					;[Block]
					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
					
					Select Lower(StrTemp)
						Case "1",""
							CreateConsoleMsg("LIST OF COMMANDS - PAGE 1/3")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- asd")
							CreateConsoleMsg("- status")
							CreateConsoleMsg("- camerapick")
							CreateConsoleMsg("- ending")
							CreateConsoleMsg("- noclipspeed")
							CreateConsoleMsg("- noclip")
							CreateConsoleMsg("- injure [value]")
							CreateConsoleMsg("- infect [value]")
							CreateConsoleMsg("- heal")
							CreateConsoleMsg("- teleport [room name]")
							CreateConsoleMsg("- rooms")
							CreateConsoleMsg("- npcs")
							CreateConsoleMsg("- spawnitem [item name]")
							CreateConsoleMsg("- wireframe")
							CreateConsoleMsg("- 173speed")
							CreateConsoleMsg("- 106speed")
							CreateConsoleMsg("- 173state")
							CreateConsoleMsg("- 106state")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Use "+Chr(34)+"help 2/3"+Chr(34)+" to find more commands.")
							CreateConsoleMsg("Use "+Chr(34)+"help [command name]"+Chr(34)+" to get more information about a command.")
							CreateConsoleMsg("******************************")
						Case "2"
							CreateConsoleMsg("LIST OF COMMANDS - PAGE 2/3")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- spawn [npc type] [state]")
							CreateConsoleMsg("- reset096")
							CreateConsoleMsg("- disable173")
							CreateConsoleMsg("- enable173")
							CreateConsoleMsg("- disable106")
							CreateConsoleMsg("- enable106")
							CreateConsoleMsg("- halloween")
							CreateConsoleMsg("- sanic")
							CreateConsoleMsg("- scp-420-j")
							CreateConsoleMsg("- godmode")
							CreateConsoleMsg("- revive")
							CreateConsoleMsg("- showfps")
							CreateConsoleMsg("- 096state")
							CreateConsoleMsg("- debughud")
							CreateConsoleMsg("- camerafog [near] [far]")
							CreateConsoleMsg("- gamma [value]")
							CreateConsoleMsg("- infinitestamina")
							CreateConsoleMsg("- playmusic [clip + .wav/.ogg]")
							CreateConsoleMsg("- notarget")
							CreateConsoleMsg("- unlockexits")
							CreateConsoleMsg("- disablenuke")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Use "+Chr(34)+"help [command name]"+Chr(34)+" to get more information about a command.")
							CreateConsoleMsg("******************************")
							
						;{~--<MOD>--~}
						
						Case "3"
						    CreateConsoleMsg("LIST OF COMMANDS - PAGE 3/3")
						    CreateConsoleMsg("******************************")
						    CreateConsoleMsg("MODS COMMANDS")
						    CreateConsoleMsg("******************************")
						    CreateConsoleMsg("- newyear") 
						    CreateConsoleMsg("- cheats") 
						    CreateConsoleMsg("- fov [value]") 
						    CreateConsoleMsg("- reset372") 
							CreateConsoleMsg("- reset650") 
							CreateConsoleMsg("- reset1033ru") 
							CreateConsoleMsg("- money") 
							CreateConsoleMsg("- crystal [value]") 
							CreateConsoleMsg("- unlockachievements") 
							CreateConsoleMsg("- disable049") 
							CreateConsoleMsg("- enable049") 
							CreateConsoleMsg("- enablecontrol") 
							CreateConsoleMsg("- disablecontrol") 
							CreateConsoleMsg("- unlockcheckpoints") 
							CreateConsoleMsg("- disable966") 
							CreateConsoleMsg("- enable966") 
							CreateConsoleMsg("- noblinking")
						    CreateConsoleMsg("******************************")
						
						;{~--<END>--~}
						
						Case "asd"
							CreateConsoleMsg("HELP - asd")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Actives godmode, noclip, wireframe and")
							CreateConsoleMsg("sets fog distance to 20 near, 30 far")
							CreateConsoleMsg("******************************")
						Case "rooms"
							CreateConsoleMsg("HELP - rooms")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Sends a list of all room IDs")
							CreateConsoleMsg("******************************")
						Case "npcs"
							CreateConsoleMsg("HELP - npcs")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Sends a list of all npc IDs")
							CreateConsoleMsg("******************************")
						Case "camerafog"
							CreateConsoleMsg("HELP - camerafog")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Sets the draw distance of the fog.")
							CreateConsoleMsg("The fog begins generating at 'CameraFogNear' units")
							CreateConsoleMsg("away from the camera and becomes completely opaque")
							CreateConsoleMsg("at 'CameraFogFar' units away from the camera.")
							CreateConsoleMsg("Example: camerafog 20 40")
							CreateConsoleMsg("******************************")
						Case "gamma"
							CreateConsoleMsg("HELP - gamma")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Sets the gamma correction.")
							CreateConsoleMsg("Should be set to a value between 0.0 and 2.0.")
							CreateConsoleMsg("Default is 1.0.")
							CreateConsoleMsg("******************************")
						Case "noclip","fly"
							CreateConsoleMsg("HELP - noclip")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles noclip, unless a valid parameter")
							CreateConsoleMsg("is specified (on/off).")
							CreateConsoleMsg("Allows the camera to move in any direction while")
							CreateConsoleMsg("bypassing collision.")
							CreateConsoleMsg("******************************")
						Case "godmode","god"
							CreateConsoleMsg("HELP - godmode")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles godmode, unless a valid parameter")
							CreateConsoleMsg("is specified (on/off).")
							CreateConsoleMsg("Prevents player death under normal circumstances.")
							CreateConsoleMsg("******************************")
						Case "wireframe"
							CreateConsoleMsg("HELP - wireframe")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles wireframe, unless a valid parameter")
							CreateConsoleMsg("is specified (on/off).")
							CreateConsoleMsg("Allows only the edges of geometry to be rendered,")
							CreateConsoleMsg("making everything else transparent.")
							CreateConsoleMsg("******************************")
						Case "spawnitem"
							CreateConsoleMsg("HELP - spawnitem")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Spawns an item at the player's location.")
							CreateConsoleMsg("Any name that can appear in your inventory")
							CreateConsoleMsg("is a valid parameter.")
							CreateConsoleMsg("Example: spawnitem Key Card Omni")
							CreateConsoleMsg("******************************")
						Case "spawn"
							CreateConsoleMsg("HELP - spawn")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Spawns an NPC at the player's location.")
							CreateConsoleMsg("Valid parameters are:")
							CreateConsoleMsg("008zombie / 049 / 049-2 / 066 / 096 / 106 / 173")
							CreateConsoleMsg("/ 178-1 / 372 / 513-1 / 966 / 1499-1 / class-d")
							CreateConsoleMsg("/ guard / mtf / apache / tentacle / 939 / 008-2")
							CreateConsoleMsg("/ 049-3 / 650 / ci / mtf2 / 0081 / 457 / vehicle")
							CreateConsoleMsg("******************************")
						Case "revive","undead","resurrect"
							CreateConsoleMsg("HELP - revive")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Resets the player's death timer after the dying")
							CreateConsoleMsg("animation triggers.")
							CreateConsoleMsg("Does not affect injury, blood loss,")
							CreateConsoleMsg("008 infection values")
							CreateConsoleMsg("or 409 crystalization values and others.")
							CreateConsoleMsg("******************************")
						Case "teleport"
							CreateConsoleMsg("HELP - teleport")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Teleports the player to the first instance")
							CreateConsoleMsg("of the specified room. Any room that appears")
							CreateConsoleMsg("in rooms.ini is a valid parameter.")
							CreateConsoleMsg("******************************")
						Case "stopsound", "stfu"
							CreateConsoleMsg("HELP - stopsound")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Stops all currently playing sounds.")
							CreateConsoleMsg("Also skips the intro scene (after breach)")
							CreateConsoleMsg("******************************")
						Case "camerapick"
							CreateConsoleMsg("HELP - camerapick")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Prints the texture name and coordinates of")
							CreateConsoleMsg("the model the camera is pointing at.")
							CreateConsoleMsg("******************************")
						Case "status"
							CreateConsoleMsg("HELP - status")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Prints player, camera, and others.")
							CreateConsoleMsg("******************************")
						Case "weed","scp-420-j","420j"
							CreateConsoleMsg("HELP - 420j")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Generates dank memes.")
							CreateConsoleMsg("******************************")
						Case "playmusic"
							CreateConsoleMsg("HELP - playmusic")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Will play tracks in .ogg/.wav format")
							CreateConsoleMsg("from "+Chr(34)+"SFX\"+"Music\"+"Custom\"+Chr(34)+".")
							CreateConsoleMsg("******************************")
						Case "disable106"
						   	CreateConsoleMsg("HELP - disable106")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Removes SCP-106 from the map.")
							CreateConsoleMsg("******************************")	
						Case "enable106"
						   	CreateConsoleMsg("HELP - enable106")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-106 to the map.")
							CreateConsoleMsg("******************************")
						Case "disable173"
						   	CreateConsoleMsg("HELP - disable173")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Removes SCP-173 from the map.")
							CreateConsoleMsg("******************************")	
						Case "enable173"
						   	CreateConsoleMsg("HELP - enable173")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-173 to the map.")
							CreateConsoleMsg("******************************")
						Case "reset096" 
						    CreateConsoleMsg("HELP - reset096")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-096 to idle state.")
							CreateConsoleMsg("******************************")
							
						;{~--<MOD>--~}
						
						Case "newyear" 
						    CreateConsoleMsg("HELP - newyear")
							CreateConsoleMsg("******************************")
                            CreateConsoleMsg("Makes SCP-173 a cookie.")
                            CreateConsoleMsg("******************************")
                        Case "cheats" 
							CreateConsoleMsg("HELP - cheats")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Actives godmode, noclip, notarget")
							CreateConsoleMsg("and infinitestamina.")
							CreateConsoleMsg("Is specified (on/off).")
							CreateConsoleMsg("******************************")
						 Case "fov" 
							CreateConsoleMsg("HELP - fov")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Field of view (FOV) is the amount of game view")
							CreateConsoleMsg("that is on display during a game.")
							CreateConsoleMsg("******************************")
						Case "reset372" 
						    CreateConsoleMsg("HELP - reset372")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-372 to inactive state.")
							CreateConsoleMsg("******************************")
						Case "reset650" 
						    CreateConsoleMsg("HELP - reset650")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-650 to inactive state.")
							CreateConsoleMsg("******************************")
						Case "money" 
							CreateConsoleMsg("HELP - money")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Generates a lot of money.")
							CreateConsoleMsg("******************************")
						Case "crystal" 
							CreateConsoleMsg("HELP - crystal")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("SCP-409 crystallizes player.")
							CreateConsoleMsg("Example: crystal 52")
							CreateConsoleMsg("******************************")
						Case "reset1033ru"
						    CreateConsoleMsg("HELP - reset1033ru")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Reset states of SCP-1033-RU.")
							CreateConsoleMsg("******************************")
						Case "enablecontrol" 
							CreateConsoleMsg("HELP - enablecontrol")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Remote door control on.")
							CreateConsoleMsg("******************************")
						Case "disablecontrol" 
							CreateConsoleMsg("HELP - disablecontrol")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Remote door control off.")
							CreateConsoleMsg("******************************")
						Case "unlockcheckpoints" 
							CreateConsoleMsg("HELP - unlockcheckpoints")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Unlocks all checkpoints.")
							CreateConsoleMsg("******************************")	
						Case "disable049"
						   	CreateConsoleMsg("HELP - disable049")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Removes SCP-049 from the map.")
							CreateConsoleMsg("******************************")	
						Case "enable049"
						   	CreateConsoleMsg("HELP - enable049")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-049 to the map.")
							CreateConsoleMsg("******************************")
						Case "disable966"
						   	CreateConsoleMsg("HELP - disable966")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Removes SCP-966 from the map.")
							CreateConsoleMsg("******************************")	
						Case "enable966"
						   	CreateConsoleMsg("HELP - enable966")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-966 to the map.")
							CreateConsoleMsg("******************************")
							
                        ;{~--<END>--~}	

						Default
							CreateConsoleMsg("There is no help available for that command.",255,150,0)
					End Select
					
					;[End Block]
				Case "asd"
					;[Block]
					WireFrame 1
					WireframeState=1
					chs\GodMode = 1
					chs\NoClip = 1
					CameraFogNear = 15
					CameraFogFar = 20
					;[End Block]
				Case "status"
					;[Block]
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
					CreateConsoleMsg("*******************************")
					CreateConsoleMsg("Status: ")
					CreateConsoleMsg("Coordinates: ")
					CreateConsoleMsg("    - collider: "+EntityX(Collider)+", "+EntityY(Collider)+", "+EntityZ(Collider))
					CreateConsoleMsg("    - camera: "+EntityX(Camera)+", "+EntityY(Camera)+", "+EntityZ(Camera))
					
					CreateConsoleMsg("Rotation: ")
					CreateConsoleMsg("    - collider: "+EntityPitch(Collider)+", "+EntityYaw(Collider)+", "+EntityRoll(Collider))
					CreateConsoleMsg("    - camera: "+EntityPitch(Camera)+", "+EntityYaw(Camera)+", "+EntityRoll(Camera))
					
					CreateConsoleMsg("Room: "+PlayerRoom\RoomTemplate\Name)
					For ev.Events = Each Events
						If ev\room = PlayerRoom Then
							CreateConsoleMsg("Room event: "+ev\EventName)	
							CreateConsoleMsg("-    state: "+ev\EventState)
							CreateConsoleMsg("-    state2: "+ev\EventState2)	
							CreateConsoleMsg("-    state3: "+ev\EventState3)
							CreateConsoleMsg("-    state4: "+ev\EventState4)
							Exit
						EndIf
					Next
					
					CreateConsoleMsg("Room coordinates: "+Floor(EntityX(PlayerRoom\obj) / 8.0 + 0.5)+", "+ Floor(EntityZ(PlayerRoom\obj) / 8.0 + 0.5))
					CreateConsoleMsg("Stamina: "+Stamina)
					CreateConsoleMsg("Death timer: "+KillTimer)					
					CreateConsoleMsg("Blinktimer: "+BlinkTimer)
					CreateConsoleMsg("Injuries: "+Injuries)
					CreateConsoleMsg("Blood Loss: "+Bloodloss)
					CreateConsoleMsg("Vomit Timer: "+VomitTimer)
					CreateConsoleMsg("Blur Timer: "+BlurTimer)
					CreateConsoleMsg("Light Blink: "+LightBlink)
					CreateConsoleMsg("Light Flash: "+LightFlash)
					CreateConsoleMsg("Sanity: "+Sanity)
					CreateConsoleMsg("Blink Effect Timer: "+BlinkEffectTimer)
					CreateConsoleMsg("Stamina Effect Timer: "+StaminaEffectTimer)
					CreateConsoleMsg("MTF Timer: "+MTFTimer)
					CreateConsoleMsg("SCP-008 Infection: "+I_008\Timer)
					CreateConsoleMsg("SCP-427 State (secs): "+Int(I_427\Timer/70.0))
					For i = 0 To 5
                        CreateConsoleMsg("SCP-1025 State "+i+": "+SCP1025state[i])
                    Next

					;{~--<MOD>--~}
					
					CreateConsoleMsg("********** MOD STATS **********")
					CreateConsoleMsg("BubbleFoam: "+I_1079\Foam)
					CreateConsoleMsg("BubbleTrigger: "+I_1079\Trigger)
					CreateConsoleMsg("MTF2 Timer: "+MTF2Timer)
					CreateConsoleMsg("SCP-409 Crystallization: "+I_409\Timer)
					CreateConsoleMsg("SCP-215 Idle State: "+I_215\IdleTimer)
					CreateConsoleMsg("SCP-215 State: "+I_215\Timer)
					CreateConsoleMsg("SCP-207 State: "+I_207\Timer)
					CreateConsoleMsg("SCP-402 State: "+I_402\Timer)
					CreateConsoleMsg("SCP-357 State: "+I_357\Timer)
					If I_1033RU\Using = 1
					    CreateConsoleMsg("HP of SCP-1033-RU: "+I_1033RU\HP+"/100")
					    CreateConsoleMsg("Lost HP of SCP-1033-RU: "+I_1033RU\DHP+"/100")
			        ElseIf I_1033RU\Using = 2
			            CreateConsoleMsg("HP of SCP-1033-RU: "+I_1033RU\HP+"/200")
					    CreateConsoleMsg("Lost HP of SCP-1033-RU: "+I_1033RU\DHP+"/200")
			        Else
			            ;nothing
                    EndIf
   					CreateConsoleMsg("*******************************")
					;[End Block]
				Case "camerapick"
					;[Block]
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
					c = CameraPick(Camera,GraphicWidth/2, GraphicHeight/2)
					If c = 0 Then
						CreateConsoleMsg("******************************")
						CreateConsoleMsg("No entity  picked")
						CreateConsoleMsg("******************************")								
					Else
						CreateConsoleMsg("******************************")
						CreateConsoleMsg("Picked entity:")
						sf = GetSurface(c,1)
						b = GetSurfaceBrush( sf )
						t = GetBrushTexture(b,0)
						texname$ =  StripPath(TextureName(t))
						CreateConsoleMsg("Texture name: "+texname)
						CreateConsoleMsg("Coordinates: "+EntityX(c)+", "+EntityY(c)+", "+EntityZ(c))
						CreateConsoleMsg("******************************")							
					EndIf
					;[End Block]
				Case "hidedistance"
					;[Block]
					HideDistance = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CreateConsoleMsg("Hidedistance set to "+HideDistance)
					;[End Block]
				Case "ending"
					;[Block]
					I_END\SelectedEnding = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					KillTimer = -0.1
					;I_END\Timer = -0.1
					;[End Block]
				Case "noclipspeed"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					NoClipSpeed = Float(StrTemp)
					;[End Block]
				Case "injure"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Injuries = Float(StrTemp)
					;[End Block]
				Case "infect"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_008\Timer = Float(StrTemp)
					;[End Block]
				Case "heal"
					;[Block]
					Injuries = 0
					Bloodloss = 0
					
					I_008\Timer = 0
					LightFlash = 0
					LightBlink = 0
					Stamina = 100
					BlurTimer = 0
					I_427\Timer = 0
					I_409\Timer = 0
					I_1079\Foam = 0
	                I_1079\Trigger = 0
	                I_207\Timer = 0
	                I_402\Timer = 0
	                I_357\Timer = 0
	                I_215\Timer = 0
	                I_215\IdleTimer = 0
	                GasMaskBlurTimer = 0
	                I_447\UsingEyeDropsTimer = -1.0
	                I_447\UsingFirstAidTimer = -1.0
	
	                If StaminaEffect > 1.0 Then
	                    StaminaEffect = 1.0
	                    StaminaEffectTimer = 0.0
	                EndIf
					
					If BlinkEffect > 1.0 Then
	                    BlinkEffect = 1.0
	                    BlinkEffectTimer = 0.0
	                EndIf

					For e.Events = Each Events
						If e\EventName = "room009" Then e\EventState = 0.0 : e\EventState3 = 0.0
					Next
					
					For i = 0 To 5
						SCP1025state[i]=0
					Next

					;[End Block]
				Case "teleport"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "895", "scp-895"
							StrTemp = "room895"
						Case "scp-914"
							StrTemp = "room914"
						Case "offices", "office"
							StrTemp = "room2offices"
					End Select
					
					For r.Rooms = Each Rooms
						If r\RoomTemplate\Name = StrTemp Then
							;PositionEntity (Collider, EntityX(r\obj), 0.7, EntityZ(r\obj))
							PositionEntity (Collider, EntityX(r\obj), EntityY(r\obj)+0.7, EntityZ(r\obj))
							ResetEntity(Collider)
							UpdateDoors()
							UpdateRooms()
							For it.Items = Each Items
								it\disttimer = 0
							Next
							PlayerRoom = r
							Exit
						EndIf
					Next
					
					If PlayerRoom\RoomTemplate\Name <> StrTemp Then CreateConsoleMsg("Room not found.",255,150,0)
					;[End Block]
				Case "rooms"
					CreateConsoleMsg("ROOMS LIST")
					CreateConsoleMsg("*************")
					CreateConsoleMsg("dimension1499")
					CreateConsoleMsg("pocketdimension")
					CreateConsoleMsg("room173intro")
					CreateConsoleMsg("endroom")
					CreateConsoleMsg("room1archive")
					CreateConsoleMsg("room005")
					CreateConsoleMsg("room173")
					CreateConsoleMsg("room205")
					CreateConsoleMsg("room372")
					CreateConsoleMsg("room914")
					CreateConsoleMsg("medibay2")
					CreateConsoleMsg("room2closets")
					CreateConsoleMsg("room2")
					CreateConsoleMsg("room2_2")
					CreateConsoleMsg("room2_3")
					CreateConsoleMsg("room2_4")
					CreateConsoleMsg("room2_5")
					CreateConsoleMsg("room2doors")
					CreateConsoleMsg("room2elevator")
					CreateConsoleMsg("room2gw")
					CreateConsoleMsg("room2gw_b")
					CreateConsoleMsg("room2posters")
					CreateConsoleMsg("room2scps")
					CreateConsoleMsg("room2scps2")
					CreateConsoleMsg("room2scps3")
					CreateConsoleMsg("room2sl")
					CreateConsoleMsg("room2storage")
					CreateConsoleMsg("room2tesla_lcz")
					CreateConsoleMsg("room2testroom2")
					CreateConsoleMsg("room012")
					CreateConsoleMsg("room447")
					CreateConsoleMsg("room1123")
					CreateConsoleMsg("lockroom")
					CreateConsoleMsg("lockroom3")
					CreateConsoleMsg("room2C")
					CreateConsoleMsg("room2C2")
					CreateConsoleMsg("room066")
					CreateConsoleMsg("room1162")
					CreateConsoleMsg("room3")
					CreateConsoleMsg("room3_2")
					CreateConsoleMsg("room3_3")
					CreateConsoleMsg("room3scps")
					CreateConsoleMsg("room3storage")
					CreateConsoleMsg("room4")
					CreateConsoleMsg("room4_2")
					CreateConsoleMsg("room4info")
					CreateConsoleMsg("checkpoint1")
					CreateConsoleMsg("endroom2")
					CreateConsoleMsg("room035")
					CreateConsoleMsg("room079")
					CreateConsoleMsg("room096")
					CreateConsoleMsg("room106")
					CreateConsoleMsg("room895")
					CreateConsoleMsg("tunnel")
					CreateConsoleMsg("tunnel2")
					CreateConsoleMsg("room2catwalk")
					CreateConsoleMsg("room2nuke")
					CreateConsoleMsg("room2pipes")
					CreateConsoleMsg("room2pipes2")
					CreateConsoleMsg("room2pit")
					CreateConsoleMsg("room2servers")
					CreateConsoleMsg("room2shaft")
					CreateConsoleMsg("room2tesla_hcz")
					CreateConsoleMsg("room2testroom")
					CreateConsoleMsg("room2tunnel")
					CreateConsoleMsg("room008")
					CreateConsoleMsg("room049")
					CreateConsoleMsg("room409")
					CreateConsoleMsg("room457")
					CreateConsoleMsg("room2Cpit")
					CreateConsoleMsg("room2Ctunnel")
					CreateConsoleMsg("room650")
					CreateConsoleMsg("room3pit")
					CreateConsoleMsg("room3tunnel")
					CreateConsoleMsg("room3z2")
					CreateConsoleMsg("room009")
					CreateConsoleMsg("room513")
					CreateConsoleMsg("room966")
					CreateConsoleMsg("room4pit")
					CreateConsoleMsg("room4tunnels")
					CreateConsoleMsg("checkpoint2")
					CreateConsoleMsg("endroom3")
					CreateConsoleMsg("gatea")
					CreateConsoleMsg("gateaentrance")
					CreateConsoleMsg("gateb")
					CreateConsoleMsg("room1office")
					CreateConsoleMsg("room1lifts")
					CreateConsoleMsg("medibay")
					CreateConsoleMsg("room2bio")
					CreateConsoleMsg("room2cafeteria")
					CreateConsoleMsg("room2offices")
					CreateConsoleMsg("room2offices2")
					CreateConsoleMsg("room2offices3")
					CreateConsoleMsg("room2offices4")
					CreateConsoleMsg("room2offices5")
					CreateConsoleMsg("room2poffices")
					CreateConsoleMsg("room2poffices2")
					CreateConsoleMsg("room2servers2")
					CreateConsoleMsg("room2sroom")
					CreateConsoleMsg("room2tesla")
					CreateConsoleMsg("room2toilets")
					CreateConsoleMsg("room2z3")
					CreateConsoleMsg("room2z3_2")
					CreateConsoleMsg("room860")
					CreateConsoleMsg("lockroom2")
					CreateConsoleMsg("room2Ccont")
					CreateConsoleMsg("room2Coffices")
					CreateConsoleMsg("room2Cz3")
					CreateConsoleMsg("room3servers")
					CreateConsoleMsg("room3servers2")
					CreateConsoleMsg("room3offices")
					CreateConsoleMsg("room3offices2")
					CreateConsoleMsg("room3z3")
					CreateConsoleMsg("room4z3")
				Case "npcs"
					CreateConsoleMsg("NPCS LIST")
					CreateConsoleMsg("*************")
					CreateConsoleMsg("SCP-173")
					CreateConsoleMsg("SCP-106")
					CreateConsoleMsg("SCP-096")
					CreateConsoleMsg("SCP-049")
					CreateConsoleMsg("SCP-049-2")
					CreateConsoleMsg("Guard")
					CreateConsoleMsg("MTF")
					CreateConsoleMsg("SCP-860-2")
					CreateConsoleMsg("SCP-939")
					CreateConsoleMsg("SCP-066")
					CreateConsoleMsg("SCP-966")
					CreateConsoleMsg("SCP-1499-1")
					CreateConsoleMsg("SCP-650")
					CreateConsoleMsg("SCP-457")
					CreateConsoleMsg("MTF2")
					CreateConsoleMsg("SCP-008-2")
					CreateConsoleMsg("SCP-049-3")
				Case "spawnitem"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					temp = False 
					For itt.Itemtemplates = Each ItemTemplates
						If (Lower(itt\name) = StrTemp) Then
							temp = True
							CreateConsoleMsg(itt\name + " spawned.")
							it.Items = CreateItem(itt\name, itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
							EntityType(it\collider, HIT_ITEM)
							Exit
						Else If (Lower(itt\tempname) = StrTemp) Then
							temp = True
							CreateConsoleMsg(itt\name + " spawned.")
							it.Items = CreateItem(itt\name, itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
							EntityType(it\collider, HIT_ITEM)
							Exit
						End If
					Next
					
					If temp = False Then CreateConsoleMsg("Item not found.",255,150,0)
					;[End Block]
				Case "wireframe"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							WireframeState = True							
						Case "off", "0", "false"
							WireframeState = False
						Default
							WireframeState = Not WireframeState
					End Select
					
					If WireframeState Then
						CreateConsoleMsg("WIREFRAME ON")
					Else
						CreateConsoleMsg("WIREFRAME OFF")	
					EndIf
					
					WireFrame WireframeState
					;[End Block]
				Case "173speed"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Curr173\Speed = Float(StrTemp)
					CreateConsoleMsg("173's speed set to " + StrTemp)
					;[End Block]
				Case "106speed"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Curr106\Speed = Float(StrTemp)
					CreateConsoleMsg("106's speed set to " + StrTemp)
					;[End Block]
				Case "173state"
					;[Block]
					CreateConsoleMsg("SCP-173")
					CreateConsoleMsg("Position: " + EntityX(Curr173\obj) + ", " + EntityY(Curr173\obj) + ", " + EntityZ(Curr173\obj))
					CreateConsoleMsg("Idle: " + Curr173\Idle)
					CreateConsoleMsg("State: " + Curr173\State)
					;[End Block]
				Case "106state"
					;[Block]
					CreateConsoleMsg("SCP-106")
					CreateConsoleMsg("Position: " + EntityX(Curr106\obj) + ", " + EntityY(Curr106\obj) + ", " + EntityZ(Curr106\obj))
					CreateConsoleMsg("Idle: " + Curr106\Idle)
					CreateConsoleMsg("State: " + Curr106\State)
					;[End Block]
				Case "reset096"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype096 Then
							n\State = 0
							StopStream_Strict(n\SoundChn) : n\SoundChn=0
							If n\SoundChn2<>0
								StopStream_Strict(n\SoundChn2) : n\SoundChn2=0
							EndIf
							Exit
						EndIf
					Next
					;[End Block]
				Case "disable173"
					;[Block]
					Curr173\Idle = 3 ;This phenominal comment is brought to you by PolyFox. His absolute wisdom in this fatigue of knowledge brought about a new era of 173 state checks.
					HideEntity Curr173\obj
					HideEntity Curr173\Collider
					;[End Block]
				Case "enable173"
					;[Block]
					Curr173\Idle = False
					ShowEntity Curr173\obj
					ShowEntity Curr173\Collider
					;[End Block]
				Case "disable106"
					;[Block]
					Curr106\Idle = True
					Curr106\State = 200000
					Contained106 = True
					;[End Block]
				Case "enable106"
					;[Block]
					Curr106\Idle = False
					Contained106 = False
					ShowEntity Curr106\Collider
					ShowEntity Curr106\obj
					;[End Block]
				Case "halloween"
					;[Block]
					at\OtherTextureID[0] = Not at\OtherTextureID[0]
					If at\OtherTextureID[0] Then
						Local tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_173_h.pt"), 1)
						EntityTexture Curr173\obj, tex, 0, 0
						FreeTexture tex
						CreateConsoleMsg("173 JACK-O-LANTERN ON")
					Else
						Local tex2 = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_173.png"), 1)
						EntityTexture Curr173\obj, tex2, 0, 0
						FreeTexture tex2
						CreateConsoleMsg("173 JACK-O-LANTERN OFF")
					EndIf
					;[End Block]
				Case "sanic"
					;[Block]
					SuperMan = Not SuperMan
					If SuperMan = True Then
						CreateConsoleMsg("GOTTA GO FAST")
					Else
						CreateConsoleMsg("WHOA SLOW DOWN")
					EndIf
					;[End Block]
				Case "scp-420-j","420j","weed"
					;[Block]
					For i = 1 To 20
						If Rand(2)=1 Then
							it.Items = CreateItem("Some SCP-420-J","scp420j", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5))
						Else
							it.Items = CreateItem("Joint","scp420s", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5))
						EndIf
						EntityType (it\collider, HIT_ITEM)
					Next
					PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Music\" +"Using420J.ogg"))
					;[End Block]
				Case "godmode", "god"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							chs\GodMode = True						
						Case "off", "0", "false"
							chs\GodMode = False
						Default
							chs\GodMode = Not chs\GodMode
					End Select	
					If chs\GodMode Then
						CreateConsoleMsg("GODMODE ON")
					Else
						CreateConsoleMsg("GODMODE OFF")	
					EndIf
					;[End Block]
				Case "revive","undead","resurrect"
					;[Block]
					DropSpeed = -0.1
					HeadDropSpeed = 0.0
					Shake = 0
					CurrSpeed = 0
					
					HeartBeatVolume = 0
					
					CameraShake = 0
					Shake = 0
					LightFlash = 0
					BlurTimer = 0
					I_427\Timer = 0
					I_008\Timer = 0
					I_1079\Foam = 0
	                I_1079\Trigger = 0
                    Bloodloss = 0
                    Injuries = 0
                    I_207\Timer = 0
                    I_402\Timer = 0
                    I_357\Timer = 0
					
					FallTimer = 0
					ms\MenuOpen = False
					
					chs\GodMode = 0
					chs\NoClip = 0
					chs\Cheats = 0
					
					ResetEntity Collider
					PositionEntity Collider, EntityX(Collider, True), EntityY(Collider, True) + 1.3, EntityZ(Collider, True), True
					ShowEntity Collider
					HideEntity at\OverlayID[5]
					HideEntity at\OverlayID[8]
					
					KillTimer = 0
					KillAnim = 0
					
					For e.Events = Each Events
						If e\EventName = "room009" Then e\EventState = 0.0 : e\EventState3 = 0.0
					Next

					;[End Block]
				Case "noclip","fly"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							chs\NoClip = True
							Playable = True
						Case "off", "0", "false"
							chs\NoClip = False	
							RotateEntity Collider, 0, EntityYaw(Collider), 0
						Default
							chs\NoClip = Not chs\NoClip
							If chs\NoClip = False Then		
								RotateEntity Collider, 0, EntityYaw(Collider), 0
							Else
								Playable = True
							EndIf
					End Select
					
					If chs\NoClip Then
						CreateConsoleMsg("NOCLIP ON")
					Else
						CreateConsoleMsg("NOCLIP OFF")
					EndIf
					
					DropSpeed = 0
					;[End Block]
				Case "showfps"
					;[Block]
					ShowFPS = Not ShowFPS
					CreateConsoleMsg("ShowFPS: "+Str(ShowFPS))
					;[End Block]
				Case "096state"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype096 Then
							CreateConsoleMsg("SCP-096")
							CreateConsoleMsg("Position: " + EntityX(n\obj) + ", " + EntityY(n\obj) + ", " + EntityZ(n\obj))
							CreateConsoleMsg("Idle: " + n\Idle)
							CreateConsoleMsg("State: " + n\State)
							Exit
						EndIf
					Next
					CreateConsoleMsg("SCP-096 has not spawned.")
					;[End Block]
				Case "debughud"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Select StrTemp
						Case "on", "1", "true"
							DebugHUD = True
						Case "off", "0", "false"
							DebugHUD = False
						Default
							DebugHUD = Not DebugHUD
					End Select
					
					If DebugHUD Then
						CreateConsoleMsg("Debug Mode On")
					Else
						CreateConsoleMsg("Debug Mode Off")
					EndIf
					;[End Block]
				Case "stopsound", "stfu"
					;[Block]
					For snd.Sound = Each Sound
						For i = 0 To 31
							If snd\channels[i]<>0 Then
								StopChannel snd\channels[i]
							EndIf
						Next
					Next
					
					For e.Events = Each Events
						If e\EventName = "room173" Then 
							If e\room\NPC[0] <> Null Then RemoveNPC(e\room\NPC[0])
							If e\room\NPC[1] <> Null Then RemoveNPC(e\room\NPC[1])
							If e\room\NPC[2] <> Null Then RemoveNPC(e\room\NPC[2])
							
							FreeEntity e\room\Objects[0] : e\room\Objects[0]=0
							FreeEntity e\room\Objects[1] : e\room\Objects[1]=0
							PositionEntity Curr173\Collider, 0,0,0
							ResetEntity Curr173\Collider
							ShowEntity Curr173\obj
							RemoveEvent(e)
							Exit
						EndIf
					Next
					CreateConsoleMsg("Stopped all sounds.")
					;[End Block]
				Case "camerafog"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CameraFogNear = Float(Left(args, Len(args) - Instr(args, " ")))
					CameraFogFar = Float(Right(args, Len(args) - Instr(args, " ")))
					CreateConsoleMsg("Near set to: " + CameraFogNear + ", far set to: " + CameraFogFar)
					;[End Block]
				Case "gamma"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					ScreenGamma = Int(StrTemp)
					CreateConsoleMsg("Gamma set to " + ScreenGamma)
					;[End Block]
				Case "spawn"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp$ = Piece$(args$, 1)
					StrTemp2$ = Piece$(args$, 2)
					
					;Hacky fix for when the user doesn't input a second parameter.
					If (StrTemp <> StrTemp2) Then
						Console_SpawnNPC(StrTemp, StrTemp2)
					Else
						Console_SpawnNPC(StrTemp)
					EndIf
					;[End Block]
				Case "infinitestamina","infstam", "is"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							chs\InfiniteStamina% = True						
						Case "off", "0", "false"
							chs\InfiniteStamina% = False
						Default
							chs\InfiniteStamina% = Not chs\InfiniteStamina%
					End Select
					
					If chs\InfiniteStamina
						CreateConsoleMsg("INFINITE STAMINA ON")
					Else
						CreateConsoleMsg("INFINITE STAMINA OFF")	
					EndIf
					;[End Block]
				Case "asd2"
					;[Block]
					chs\GodMode = 1
					chs\InfiniteStamina = 1
					Curr173\Idle = 3
					Curr106\Idle = True
					Curr106\State = 200000
					Contained106 = True
					;[End Block]
				Case "disablenuke"
					;[Block]
					For e2.Events = Each Events
				        If e2\EventName = "room2nuke"
							e2\EventState = 0
							UpdateLever(e2\room\Objects[1])
							UpdateLever(e2\room\Objects[3])
							RotateEntity e2\room\Objects[1], 0, EntityYaw(e2\room\Objects[1]), 30
							RotateEntity e2\room\Objects[3], 0, EntityYaw(e2\room\Objects[3]), 30
							Exit
						EndIf
				    Next
					;[End Block]
				Case "unlockexits"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "a"
							For e.Events = Each Events
								If e\EventName = "gateaentrance" Then
									e\EventState3 = 1
									e\room\RoomDoors[1]\open = True
									Exit
								EndIf
							Next
							CreateConsoleMsg("Gate A is now unlocked.")	
						Case "b"
							For e.Events = Each Events
								If e\EventName = "gateb" Then
									e\EventState3 = 1
									e\room\RoomDoors[4]\open = True
									Exit
								EndIf
							Next	
							CreateConsoleMsg("Gate B is now unlocked.")	
						Default
							For e.Events = Each Events
								If e\EventName = "gateaentrance" Then
									e\EventState3 = 1
									e\room\RoomDoors[1]\open = True
								ElseIf e\EventName = "gateb" Then
									e\EventState3 = 1
									e\room\RoomDoors[4]\open = True
								EndIf
							Next
							CreateConsoleMsg("Gate A and B are now unlocked.")	
					End Select
					
					RemoteDoorOn = True
					;[End Block]
				Case "kill","suicide"
					;[Block]
					KillTimer = -1
					Select Rand(4)
						Case 1
							DeathMSG = "[DATA REDACTED]"
						Case 2
							DeathMSG = SubjectName$+" found dead in Sector [DATA REDACTED]. "
							DeathMSG = DeathMSG + "The subject appears to have attained no physical damage, and there is no visible indication as to what killed him. "
							DeathMSG = DeathMSG + "Body was sent for autopsy."
						Case 3
							DeathMSG = "EXCP_ACCESS_VIOLATION"
						Case 4
							DeathMSG = SubjectName$+" found dead in Sector [DATA REDACTED]. "
							DeathMSG = DeathMSG + "The subject appears to have scribbled the letters "+Chr(34)+"kys"+Chr(34)+" in his own blood beside him. "
							DeathMSG = DeathMSG + "No other signs of physical trauma or struggle can be observed. Body was sent for autopsy."
					End Select
					;[End Block]
				Case "playmusic"
					;[Block]
					; I think this might be broken since the FMod library streaming was added. -Mark
					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					
					If StrTemp$ <> ""
						PlayCustomMusic% = True
						If CustomMusic <> 0 Then FreeSound_Strict CustomMusic : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel MusicCHN
						CustomMusic = LoadSound_Strict("SFX\"+"Music\" + "Custom\"+StrTemp$)
						If CustomMusic = 0
							PlayCustomMusic% = False
						EndIf
					Else
						PlayCustomMusic% = False
						If CustomMusic <> 0 Then FreeSound_Strict CustomMusic : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel MusicCHN
					EndIf
					;[End Block]
				Case "tp"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtypeMTF
							If n\MTFLeader = Null
								PositionEntity Collider,EntityX(n\Collider),EntityY(n\Collider)+5,EntityZ(n\Collider)
								ResetEntity Collider
								Exit
							EndIf
						ElseIf n\NPCtype = NPCtypeMTF2
							If n\MTF2Leader = Null
								PositionEntity Collider,EntityX(n\Collider),EntityY(n\Collider)+5,EntityZ(n\Collider)
								ResetEntity Collider
								Exit
							EndIf
						EndIf
					Next
					
					;[End Block]
				Case "tele"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp$ = Piece$(args$,1," ")
					StrTemp2$ = Piece$(args$,2," ")
					StrTemp3$ = Piece$(args$,3," ")
					PositionEntity Collider,Float(StrTemp$),Float(StrTemp2$),Float(StrTemp3$)
					PositionEntity Camera,Float(StrTemp$),Float(StrTemp2$),Float(StrTemp3$)
					ResetEntity Collider
					ResetEntity Camera
					CreateConsoleMsg("Teleported to coordinates (X|Y|Z): "+EntityX(Collider)+"|"+EntityY(Collider)+"|"+EntityZ(Collider))
					;[End Block]
				Case "notarget", "nt"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							chs\NoTarget = True						
						Case "off", "0", "false"
							chs\NoTarget = False	
						Default
							chs\NoTarget = Not chs\NoTarget
					End Select
					
					If chs\NoTarget = False Then
						CreateConsoleMsg("NOTARGET OFF")
					Else
						CreateConsoleMsg("NOTARGET ON")	
					EndIf
					;[End Block]
				Case "spawnradio"
					;[Block]
					it.Items = CreateItem("Radio Transceiver", "fineradio", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					it\state = 101
					;[End Block]
				Case "spawnnvg"
					;[Block]
					it.Items = CreateItem("Night Vision Goggles", "nvgoggles", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					it\state = 1000
					;[End Block]
				Case "spawnpumpkin","pumpkin"
					;[Block]
					CreateConsoleMsg("What pumpkin?")
					;[End Block]
				Case "spawnnav"
					;[Block]
					it.Items = CreateItem("S-NAV Navigator Ultimate", "nav", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					it\state = 101
					;[End Block]
				Case "teleport173"
					;[Block]
					PositionEntity Curr173\Collider,EntityX(Collider),EntityY(Collider)+0.2,EntityZ(Collider)
					ResetEntity Curr173\Collider
					;[End Block]
				Case "seteventstate"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp$ = Piece$(args$,1," ")
					StrTemp2$ = Piece$(args$,2," ")
					StrTemp3$ = Piece$(args$,3," ")
					StrTemp4$ = Piece$(args$,4," ")
					Local pl_room_found% = False
					If StrTemp="" Or StrTemp2="" Or StrTemp3="" Or StrTemp4=""
						CreateConsoleMsg("Too few parameters. This command requires 4.",255,150,0)
					Else
						For e.Events = Each Events
							If e\room = PlayerRoom
								If Lower(StrTemp)<>"keep"
									e\EventState = Float(StrTemp)
								EndIf
								If Lower(StrTemp2)<>"keep"
									e\EventState2 = Float(StrTemp2)
								EndIf
								If Lower(StrTemp3)<>"keep"
									e\EventState3 = Float(StrTemp3)
								EndIf
								If Lower(StrTemp4)<>"keep"
									e\EventState4 = Float(StrTemp4)
								EndIf
								CreateConsoleMsg("Changed event states from current player room to: "+e\EventState+"|"+e\EventState2+"|"+e\EventState3+"|"+e\EventState4)
								pl_room_found = True
								Exit
							EndIf
						Next
						If (Not pl_room_found)
							CreateConsoleMsg("The current room doesn't has any event applied.",255,150,0)
						EndIf
					EndIf
					;[End Block]
				Case "spawnparticles"
					;[Block]
					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					
					If Int(StrTemp) > -1 And Int(StrTemp) <= 1 ;<--- This is the maximum ID of particles by Devil Particle system, will be increased after time - ENDSHN
						SetEmitter(Collider,ParticleEffect[Int(StrTemp)])
						CreateConsoleMsg("Spawned particle emitter with ID "+Int(StrTemp)+" at player's position.")
					Else
						CreateConsoleMsg("Particle emitter with ID "+Int(StrTemp)+" not found.",255,150,0)
					EndIf
					;[End Block]
				Case "giveachievement"
					;[Block]
					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					
					If Int(StrTemp)>=0 And Int(StrTemp)<MAXACHIEVEMENTS
						Achievements(Int(StrTemp))=True
						CreateConsoleMsg("Achievemt "+AchievementStrings(Int(StrTemp))+" unlocked.")
					Else
						CreateConsoleMsg("Achievement with ID "+Int(StrTemp)+" doesn't exist.",255,150,0)
					EndIf
					;[End Block]
				Case "427state"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_427\Timer = Float(StrTemp)*70.0
					;[End Block]
				Case "teleport106"
					;[Block]
					Curr106\State = 0
					Curr106\Idle = False
					;[End Block]
				Case "setblinkeffect"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					BlinkEffect = Float(Left(args, Len(args) - Instr(args, " ")))
					BlinkEffectTimer = Float(Right(args, Len(args) - Instr(args, " ")))
					CreateConsoleMsg("Set BlinkEffect to: " + BlinkEffect + "and BlinkEffect timer: " + BlinkEffectTimer)
					;[End Block]
				Case "jorge"
					;[Block]	
					CreateConsoleMsg(Chr(74)+Chr(79)+Chr(82)+Chr(71)+Chr(69)+Chr(32)+Chr(72)+Chr(65)+Chr(83)+Chr(32)+Chr(66)+Chr(69)+Chr(69)+Chr(78)+Chr(32)+Chr(69)+Chr(88)+Chr(80)+Chr(69)+Chr(67)+Chr(84)+Chr(73)+Chr(78)+Chr(71)+Chr(32)+Chr(89)+Chr(79)+Chr(85)+Chr(46))
					;[End Block]
					
				;{~--<MOD>--~}
				
			    Case "newyear" 
					;[Block]
					at\OtherTextureID[1] = Not at\OtherTextureID[1]
					If at\OtherTextureID[1] Then
						tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_173_ny.pt"), 1)
						EntityTexture Curr173\obj, tex, 0, 0
						FreeTexture tex
						CreateConsoleMsg("173 COOKIE ON")
					Else
						tex2 = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_173.png"), 1)
						EntityTexture Curr173\obj, tex2, 0, 0
						FreeTexture tex2
						CreateConsoleMsg("173 COOKIE OFF")
					EndIf
					;[End Block]
				Case "cheats" 
					;[Block]
	
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							chs\Cheats = True			
						Case "off", "0", "false"
	                        chs\Cheats = False
						Default
							chs\Cheats = Not chs\Cheats
						End Select	
					If chs\Cheats = True Then
                        chs\Godmode = True
                        chs\NoTarget = True
                        chs\NoClip = True
                        chs\InfiniteStamina = True
                        chs\NoBlinking = True
						CreateConsoleMsg("CHEATS ON")
					Else
					    chs\Godmode = False
                        chs\NoTarget = False
                        chs\NoClip = False
                        chs\InfiniteStamina = False
                        chs\NoBlinking = False
						CreateConsoleMsg("CHEATS OFF")	
					EndIf
					;[End Block]
				Case "fov" 
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					FOV# = Float(StrTemp)
                    ;[End Block]
                 Case "reset650" 
					;[Block]				
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype650 Then
							RemoveNPC(n)
							CreateEvent("room650", "room650", 0, 0)   
							Exit
						EndIf
					Next
					;[End Block]
				Case "reset372" 
					;[Block]				
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype372 Then
							RemoveNPC(n)
							CreateEvent("room372", "room372", 0, 0)   
							Exit
						EndIf
				    Next
					;[End Block]
				Case "crystal" 
					;[Block]				
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_409\Timer = Float(StrTemp)	
					;[End Block]
				Case "reset1033ru" 
				    ;[Block]
				    I_1033RU\HP = 200
				    I_1033RU\DHP = 0
				    ;[End Block]
				Case "unlockachievements" 
					;[Block]
					Local achievementsUnlocked = 0
					For i = 0 To MAXACHIEVEMENTS - 1
						achievementsUnlocked = achievementsUnlocked + Achievements(i)
					Next
					
					If achievementsUnlocked =< MAXACHIEVEMENTS - 1 Then 
					    For i = 0 To MAXACHIEVEMENTS - 1
				            Achievements(i)=True
				        Next
				        CreateConsoleMsg("All achievements unlocked!", 255, 255, 255)
					Else					
					    CreateConsoleMsg("You are already unlocked all achievements.", 255, 0, 0)
                    EndIf
											
				    ;[End Block]
			    Case "disable049" 
			        ;[Block]
			        For n.NPCs = Each NPCs
			            If n\NPCtype = NPCtype049
			                n\State = 0
			                HideEntity n\Collider
			                HideEntity n\obj
			            EndIf
			        Next
			        ;[End Block]
			    Case "enable049"
			        ;[Block]
			        For n.NPCs = Each NPCs
			            If n\NPCtype = NPCtype049
			                n\State = 1
			                ShowEntity n\Collider
			                ShowEntity n\obj
			            EndIf
			        Next
					;[End Block]
				Case "money", "rich"
					;[Block]
					For i = 1 To 20
					    If Rand(2) = 1 Then
						    it.Items = CreateItem("Quarter","25ct", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5))
					    Else
					        it.Items = CreateItem("Coin","coin", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5))
					    EndIf
					    EntityType (it\collider, HIT_ITEM)
					Next
					;[End Block]
				Case "disablecontrol"
				    ;[Block]
				    RemoteDoorOn = False
				    CreateConsoleMsg("Remote door control disabled.", 255, 255, 255)
				    ;[End Block]
				Case "enablecontrol"
				    ;[Block]
				    RemoteDoorOn = True
				    CreateConsoleMsg("Remote door control enabled.", 255, 255, 255)
				    ;[End Block]
			    Case "unlockcheckpoints"
			        ;[Block]
			        For e2.Events = Each Events
				        If e2\EventName = "room2sl"
							e2\EventState3 = 0
							UpdateLever(e2\room\Levers[0])
							RotateEntity e2\room\Levers[0], 0, EntityYaw(e2\room\Levers[0]), 0
							TurnCheckpointMonitorsOff(0)
						EndIf
					Next
					
					For e2.Events = Each Events
				        If e2\EventName = "room008"
							e2\EventState = 2
							UpdateLever(e2\room\Levers[0])
							RotateEntity e2\room\Levers[0], 0, EntityYaw(e2\room\Levers[0]), 30
							TurnCheckpointMonitorsOff(1)
							Exit
						EndIf
				    Next
				 
				    CreateConsoleMsg("Checkpoints unlocked.", 255, 255, 255)								
					;[End Block]
				Case "disable966"
				    ;[Block]
			        For n.NPCs = Each NPCs
			            If n\NPCtype = NPCtype966
			                n\State = -1
			                HideEntity n\Collider
                            HideEntity n\obj
			            EndIf
			        Next
			        ;[End Block]
			    Case "enable966"
			        ;[Block]
			        For n.NPCs = Each NPCs
			            If n\NPCtype = NPCtype966
			                n\State = 0
			                ShowEntity n\Collider
			                If WearinNightVision > 0 Then ShowEntity n\obj
			            EndIf
			        Next
					;[End Block]
				Case "noblinking", "nb"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							chs\NoBlinking = True						
						Case "off", "0", "false"
							chs\NoBlinking = False
						Default
							chs\NoBlinking = Not chs\NoBlinking
					End Select	
					If chs\NoBlinking Then
						CreateConsoleMsg("NO BLINKING MODE ON")
					Else
						CreateConsoleMsg("NO BLINKING MODE OFF")	
					EndIf
					;[End Block]
					
                ;{~--<END>--~}

				Default
					;[Block]
					CreateConsoleMsg("Command not found.", 255, 0, 0)
					;[End Block]
			End Select
			
			ConsoleInput = ""
		End If
		
		Local TempY% = y + height - 25*MenuScale - ConsoleScroll
		Local count% = 0
		For cm.ConsoleMsg = Each ConsoleMsg
			count = count+1
			If count>1000 Then
				Delete cm
			Else
				If TempY >= y And TempY < y + height - 20*MenuScale Then
					If cm=ConsoleReissue Then
						Color cm\r/4,cm\g/4,cm\b/4
						Rect x,TempY-2*MenuScale,width-30*MenuScale,24*MenuScale,True
					EndIf
					Color cm\r,cm\g,cm\b
					If cm\isCommand Then
						AAText(x + 20*MenuScale, TempY, "> "+cm\txt)
					Else
						AAText(x + 20*MenuScale, TempY, cm\txt)
					EndIf
				EndIf
				TempY = TempY - 15*MenuScale
			EndIf
			
		Next
		
		Color 255,255,255
		
		If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	End If
	
	AASetFont fo\Font[0]
	
End Function

;ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
;CreateConsoleMsg("Console commands: ")
;CreateConsoleMsg("  - teleport [room name]")
;CreateConsoleMsg("  - godmode [on/off]")
;CreateConsoleMsg("  - noclip [on/off]")
;CreateConsoleMsg("  - noclipspeed [x] (default = 2.0)")
;CreateConsoleMsg("  - wireframe [on/off]")
;CreateConsoleMsg("  - debughud [on/off]")
;CreateConsoleMsg("  - camerafog [near] [far]")
;CreateConsoleMsg(" ")
;CreateConsoleMsg("  - status")
;CreateConsoleMsg("  - heal")
;CreateConsoleMsg(" ")
;CreateConsoleMsg("  - spawnitem [item name]")
;CreateConsoleMsg(" ")
;CreateConsoleMsg("  - 173speed [x] (default = 35)")
;CreateConsoleMsg("  - disable173/enable173")
;CreateConsoleMsg("  - disable106/enable106")
;CreateConsoleMsg("  - 173state/106state/096state")
;CreateConsoleMsg("  - spawn [npc type]")
;CreateConsoleMsg("  - cheats [on/off]")
;CreateConsoleMsg("  - disable049/enable049")

;{~--<MOD>--~}

;CreateConsoleMsg("  - fov [x] (default = 74)")

;{~--<END>--~}

Global DebugHUD%

Global BlurVolume#, BlurTimer#

Global LightBlink#, LightFlash#

Global Camera%, CameraShake#, CurrCameraZoom#

Global StoredCameraFogFar# = CameraFogFar

Include "Source Code\dreamfilter.bb"

;---------------------------------------------- Sounds -----------------------------------------------------

;[Block]

Global SoundEmitter%
Global TempSounds%[10]
Global TempSoundCHN%
Global TempSoundIndex% = 0

;The Music now has to be pre-defined, as the new system uses streaming instead of the usual sound loading system Blitz3D has
Dim Music$(34)
Music(0) = "The Dread"
Music(1) = "HeavyContainment"
Music(2) = "EntranceZone"
Music(3) = "PD"
Music(4) = "Room079"
Music(5) = "GateB1"
Music(6) = "GateB2"
Music(7) = "Room3Storage"
Music(8) = "Room049"
Music(9) = "Forest"
Music(10) = "106Chase"
Music(11) = "Menu"
Music(12) = "860_2Chase"
Music(13) = "Intro"
Music(14) = "Using178"
Music(15) = "PDTrench"
Music(16) = "Room205"
Music(17) = "GateA"
Music(18) = "Dimension1499"
Music(19) = "Dimension1499Danger"
Music(20) = "049Chase"
Music(21) = "..\Ending\MenuBreath"
Music(22) = "Room914"
Music(23) = "Ending"
Music(24) = "Credits"
Music(25) = "SaveMeFrom"

;{~--<MOD>--~}

Music(26) = "Room2Tunnel" 
Music(27) = "650Chase" 
Music(28) = "Room457" 
Music(29) = "Room409" 
Music(30) = "Using215" 
Music(31) = "Using1033RU"
Music(32) = "Room035"
Music(33) = "Room106"

;{~--<END>--~}

Global CurrMusicStream, MusicCHN
MusicCHN = StreamSound_Strict(scpModding_ProcessFilePath$("SFX\Music"+Music(2)+".ogg"),MusicVolume,Mode)

Global CurrMusicVolume# = 1.0, NowPlaying%=2, ShouldPlay%=11
Global CurrMusic% = 1

DrawLoading(10, True)

Dim OpenDoorSFX%(3,3), CloseDoorSFX%(3,3)

Global KeyCardSFX1 
Global KeyCardSFX2 
Global ButtonSFX2 
Global ScannerSFX1
Global ScannerSFX2 

Dim OpenDoorFastSFX(1)
Global CautionSFX% 

Global NuclearSirenSFX%

Global CameraSFX  

Global StoneDragSFX% 

Global GunshotSFX% 
Global Gunshot2SFX% 
Global Gunshot3SFX%
Global BullethitSFX% 

Global TeslaIdleSFX 
Global TeslaActivateSFX 
Global TeslaPowerUpSFX 

Global MagnetUpSFX%, MagnetDownSFX
Global FemurBreakerSFX%
Global EndBreathCHN%
Global EndBreathSFX%

Dim DecaySFX%(5)

Global BurstSFX 

DrawLoading(20, True)

Dim RustleSFX%(7) ;3

Global Use914SFX%
Global Death914SFX% 

Dim DripSFX%(6)

Global LeverSFX%, LightSFX% 
Global ButtGhostSFX% 

Dim RadioSFX(5,10) 

Global RadioSquelch 
Global RadioStatic 
Global RadioBuzz 

Global ElevatorBeepSFX, ElevatorMoveSFX  

Dim PickSFX%(10)

Global AmbientSFXCHN%, CurrAmbientSFX%
Dim AmbientSFXAmount(6)
;0 = light containment, 1 = heavy containment, 2 = entrance
AmbientSFXAmount(0)=25 : AmbientSFXAmount(1)=24 : AmbientSFXAmount(2)=24
;3 = general, 4 = pre-breach
AmbientSFXAmount(3)=15 : AmbientSFXAmount(4)=5
;5 = forest
AmbientSFXAmount(5)=13

Dim AmbientSFX%(6, 92)

Dim OldManSFX%(9) ;8

Dim Scp173SFX%(3)

Dim HorrorSFX%(21)

DrawLoading(25, True)

Dim IntroSFX%(20)

Dim AlarmSFX%(6)

Dim CommotionState%(26)

Global HeartBeatSFX 

Global VomitSFX%

Dim BreathSFX(2,5)
Global BreathCHN%

Dim NeckSnapSFX(3)

Dim DamageSFX%(9)

Dim MTFSFX%(8)

Dim CoughSFX%(3)
Global CoughCHN%, VomitCHN%

Global MachineSFX% 
Global ApacheSFX
Global CurrStepSFX
Dim StepSFX%(5, 2, 8) ;(normal/metal, walk/run, id)

Dim Step2SFX(6)

;{~--<MOD>--~}

Dim NVGUse%(1)

Global Ambient1123SFX% 

Global FireSFX% 

Dim DamageSFX1033RU%(3)
Global DeathSFX1033RU%

Dim SizzSFX(1) 

Dim MTF2SFX%(8) 
Global Gunshot4SFX%

Dim ScientistRadioSFX%(1) 

Global RelaxedBreathSFX%, RelaxedBreathCHN%

Global CrouchSFX%, CrouchCHN%

Global RadioStatic895%

Dim VehicleSFX%(1)

;{~--<END>--~}

DrawLoading(30, True)

;[End block]

;New Sounds and Meshes/Other things in SCP:CB 1.3 - ENDSHN
;[Block]

Global PlayCustomMusic% = False, CustomMusic% = 0

Global MonitorTimer# = 0.0, MonitorTimer2# = 0.0, UpdateCheckpoint1%, UpdateCheckpoint2%

;This variable is for when a camera detected the player
	;False: Player is not seen (will be set after every call of the Main Loop
	;True: The Player got detected by a camera
Global PlayerDetected%
Global PrevInjuries#, PrevBloodloss#

Global NVGImages = LoadAnimImage(scpModding_ProcessFilePath$("GFX\"+"battery.png"),64,64,0,2)
MaskImage NVGImages,255,0,255

Global AmbientLightRoomTex%, AmbientLightRoomVal%

Global UserTrackCheck% = 0, UserTrackCheck2% = 0
Global UserTrackMusicAmount% = 0, CurrUserTrack%, UserTrackFlag% = False
Dim UserTrackName$(256)

Global OptionsMenu% = 0
Global QuitMSG% = 0

Global InFacility% = True

Global PrevMusicVolume# = MusicVolume#
Global PrevSFXVolume# = SFXVolume#
Global DeafPlayer% = False
Global DeafTimer# = 0.0

Global room2gw_brokendoor% = False
Global room2gw_x# = 0.0
Global room2gw_z# = 0.0

Global Menu_TestIMG
Global menuroomscale# = 8.0 / 2048.0

Global CurrMenu_TestIMG$ = ""

Dim NavImages(5)
For i = 0 To 3
	NavImages(i) = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\items\"+"navigator\room_border"+i+".png"))
	MaskImage NavImages(i),255,0,255
Next
NavImages(4) = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\items\"+"navigator\battery_meter.png"))

Global NavBG = CreateImage(GraphicWidth,GraphicHeight)

Global ParticleEffect[10]

Global DTextures[MaxDTextures]

Global IntercomStreamCHN%

Global ForestNPC, ForestNPCData#[3]

;[End Block]

;-----------------------------------------  Images ----------------------------------------------------------

Global PauseMenuIMG%

Global SprintIcon%
Global BlinkIcon%
Global CrouchIcon%
Global HandIcon%
Global HandIcon2%

Global StaminaMeterIMG%

Global KeypadHUD

Global Panel294, Using294%, Input294$

DrawLoading(35, True)

;----------------------------------------------  Items  -----------------------------------------------------

Include "Source Code\Items_System.bb"

;--------------------------------------- Particles ------------------------------------------------------------

Include "Source Code\Particles_System.bb"

;-------------------------------------  Doors --------------------------------------------------------------

Global ClosestButton%, ClosestDoor.Doors
Global SelectedDoor.Doors, UpdateDoorsTimer#
Global DoorTempID%
Type Doors
	Field obj%, obj2%, frameobj%, buttons%[2]
	Field locked%, open%, angle%, openstate#, fastopen%
	Field dir%
	Field timer%, timerstate#
	Field KeyCard%
	Field room.Rooms
	
	Field DisableWaypoint%
	
	Field dist#
	
	Field SoundCHN%
	
	Field Code$
	
	Field ID%
	
	Field Level%
	Field LevelDest%
	
	Field AutoClose%
	
	Field LinkedDoor.Doors
	
	Field IsElevatorDoor% = False
	
	Field MTFClose% = True
	Field NPCCalledElevator% = False
	
	Field DoorHitOBJ%
End Type 

Function CreateDoor.Doors(lvl, x#, y#, z#, angle#, room.Rooms, dopen% = False,  big% = False, keycard% = False, code$="", useCollisionMesh% = False)
	Local d.Doors, parent, i%
	Local o.Objects = First Objects
	If room <> Null Then parent = room\obj
	
	Local d2.Doors
	
	d.Doors = New Doors
	If big=1 Then
		d\obj = CopyEntity(o\DoorID[5])
		ScaleEntity(d\obj, 55 * RoomScale, 55 * RoomScale, 55 * RoomScale)
		d\obj2 = CopyEntity(o\DoorID[6])
		ScaleEntity(d\obj2, 55 * RoomScale, 55 * RoomScale, 55 * RoomScale)
		
		d\frameobj = CopyEntity(o\DoorID[4]) ;CopyMesh				
		ScaleEntity(d\frameobj, RoomScale, RoomScale, RoomScale)
		EntityType d\frameobj, HIT_MAP
		EntityAlpha d\frameobj, 0.0
	ElseIf big=2 Then
		d\obj = CopyEntity(o\DoorID[2])
		ScaleEntity(d\obj, RoomScale, RoomScale, RoomScale)
		d\obj2 = CopyEntity(o\DoorID[3])
		ScaleEntity(d\obj2, RoomScale, RoomScale, RoomScale)
		
		d\frameobj = CopyEntity(o\DoorID[1])
	ElseIf big=3 Then
		For d2 = Each Doors
			If d2 <> d And d2\dir = 3 Then
				d\obj = CopyEntity(d2\obj)
				d\obj2 = CopyEntity(d2\obj2)
				ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
				ScaleEntity d\obj2, RoomScale, RoomScale, RoomScale
				Exit
			EndIf
		Next
		If d\obj=0 Then
			d\obj = CopyEntity(o\DoorID[7])
			d\obj2 = CopyEntity(d\obj)
			ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
			ScaleEntity d\obj2, RoomScale, RoomScale, RoomScale
		EndIf
		d\frameobj = CopyEntity(o\DoorID[1])
	Else
		d\obj = CopyEntity(o\DoorID[0])
		ScaleEntity(d\obj, (204.0 * RoomScale) / MeshWidth(d\obj), 312.0 * RoomScale / MeshHeight(d\obj), 16.0 * RoomScale / MeshDepth(d\obj))
		
		d\frameobj = CopyEntity(o\DoorID[1])
		d\obj2 = CopyEntity(o\DoorID[0])
		
		ScaleEntity(d\obj2, (204.0 * RoomScale) / MeshWidth(d\obj), 312.0 * RoomScale / MeshHeight(d\obj), 16.0 * RoomScale / MeshDepth(d\obj))
	End If
	
	PositionEntity d\frameobj, x, y, z	
	ScaleEntity(d\frameobj, (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
	EntityPickMode d\frameobj,2
	EntityType d\obj, HIT_MAP
	EntityType d\obj2, HIT_MAP
	
	d\ID = DoorTempID
	DoorTempID=DoorTempID+1
	
	d\KeyCard = keycard
	d\Code = code
	d\Level = lvl
	d\LevelDest = 66
	
	For i% = 0 To 1
		If code <> "" Then 
			d\buttons[i] = CopyEntity(o\ButtonID[2])
			EntityFX(d\buttons[i], 1)
		Else
			If keycard > 0 Then
				d\buttons[i] = CopyEntity(o\ButtonID[1])
			ElseIf keycard < 0
				d\buttons[i] = CopyEntity(o\ButtonID[3])
			ElseIf big=3	
			    d\buttons[i] = CopyEntity(o\ButtonID[4])
			Else
				If (Not big=3) Then d\buttons[i] = CopyEntity(o\ButtonID[0])
			End If
		EndIf
		
		ScaleEntity(d\buttons[i], 0.03, 0.03, 0.03)
	Next
	
	If big=1 Then
		PositionEntity d\buttons[0], x - 432.0 * RoomScale, y + 0.7, z + 192.0 * RoomScale
		PositionEntity d\buttons[1], x + 432.0 * RoomScale, y + 0.7, z - 192.0 * RoomScale
		RotateEntity d\buttons[0], 0, 90, 0
		RotateEntity d\buttons[1], 0, 270, 0
	Else
		PositionEntity d\buttons[0], x + 0.6, y + 0.7, z - 0.1
		PositionEntity d\buttons[1], x - 0.6, y + 0.7, z + 0.1
		RotateEntity d\buttons[1], 0, 180, 0		
	End If
	EntityParent(d\buttons[0], d\frameobj)
	EntityParent(d\buttons[1], d\frameobj)
	EntityPickMode(d\buttons[0], 2)
	EntityPickMode(d\buttons[1], 2)
	
	PositionEntity d\obj, x, y, z
	
	RotateEntity d\obj, 0, angle, 0
	RotateEntity d\frameobj, 0, angle, 0
	
	If d\obj2 <> 0 Then
		PositionEntity d\obj2, x, y, z
		If big=1 Then
			RotateEntity(d\obj2, 0, angle, 0)
		Else
			RotateEntity(d\obj2, 0, angle + 180, 0)
		EndIf
		EntityParent(d\obj2, parent)
	EndIf
	
	EntityParent(d\frameobj, parent)
	EntityParent(d\obj, parent)
	
	d\angle = angle
	d\open = dopen		
	
	EntityPickMode(d\obj, 2)
	If d\obj2 <> 0 Then
		EntityPickMode(d\obj2, 2)
	EndIf
	
	EntityPickMode d\frameobj,2
	
	If d\open And big = False And Rand(8) = 1 Then d\AutoClose = True
	d\dir=big
	d\room=room
	
	d\MTFClose = True
	
	If useCollisionMesh Then
		For d2.Doors = Each Doors
			If d2 <> d Then
				If d2\DoorHitOBJ <> 0 Then
					d\DoorHitOBJ = CopyEntity(d2\DoorHitOBJ,d\frameobj)
					EntityAlpha d\DoorHitOBJ,0.0
					EntityFX d\DoorHitOBJ,1
					EntityType d\DoorHitOBJ,HIT_MAP
					EntityColor d\DoorHitOBJ,255,0,0
					HideEntity d\DoorHitOBJ
					Exit
				EndIf
			EndIf
		Next
		If d\DoorHitOBJ=0 Then
			d\DoorHitOBJ = CopyEntity(o\DoorID[10])
			EntityParent d\DoorHitOBJ, d\frameobj
			EntityAlpha d\DoorHitOBJ,0.0
			EntityFX d\DoorHitOBJ,1
			EntityType d\DoorHitOBJ,HIT_MAP
			EntityColor d\DoorHitOBJ,255,0,0
			HideEntity d\DoorHitOBJ
		EndIf
	EndIf
	
	Return d
	
End Function

Function CreateButton(x#, y#, z# ,pitch#, yaw#, roll# = 0, btype = 0)
    Local o.Objects = First Objects
	Local obj = CopyEntity(o\ButtonID[btype])	
	
	ScaleEntity(obj, 0.03, 0.03, 0.03)
	
	PositionEntity obj, x, y, z
	RotateEntity obj, pitch, yaw, roll
	
	EntityPickMode(obj, 2)	
	
	Return obj
End Function

Function UpdateDoors()
	
	Local fs.FPS_Settings = First FPS_Settings
	Local i%, d.Doors, x#, z#, dist#
	If UpdateDoorsTimer =< 0 Then
		For d.Doors = Each Doors
			Local xdist# = Abs(EntityX(Collider)-EntityX(d\obj,True))
			Local zdist# = Abs(EntityZ(Collider)-EntityZ(d\obj,True))
			
			d\dist = xdist+zdist
			
			If d\dist > HideDistance*2 Then
				If d\obj <> 0 Then HideEntity d\obj
				If d\frameobj <> 0 Then HideEntity d\frameobj
				If d\obj2 <> 0 Then HideEntity d\obj2
				If d\buttons[0] <> 0 Then HideEntity d\buttons[0]
				If d\buttons[1] <> 0 Then HideEntity d\buttons[1]				
			Else
				If d\obj <> 0 Then ShowEntity d\obj
				If d\frameobj <> 0 Then ShowEntity d\frameobj
				If d\obj2 <> 0 Then ShowEntity d\obj2
				If d\buttons[0] <> 0 Then ShowEntity d\buttons[0]
				If d\buttons[1] <> 0 Then ShowEntity d\buttons[1]
			EndIf
			
			If PlayerRoom\RoomTemplate\Name$ = "room2sl"
				If ValidRoom2slCamRoom(d\room)
					If d\obj <> 0 Then ShowEntity d\obj
					If d\frameobj <> 0 Then ShowEntity d\frameobj
					If d\obj2 <> 0 Then ShowEntity d\obj2
					If d\buttons[0] <> 0 Then ShowEntity d\buttons[0]
					If d\buttons[1] <> 0 Then ShowEntity d\buttons[1]
				EndIf
			EndIf
		Next
		
		UpdateDoorsTimer = 30
	Else
		UpdateDoorsTimer = Max(UpdateDoorsTimer-fs\FPSfactor[0],0)
	EndIf
	
	ClosestButton = 0
	ClosestDoor = Null
	
	For d.Doors = Each Doors
		If d\dist < HideDistance*2 Or d\IsElevatorDoor>0 Then ;Make elevator doors update everytime because if not, this can cause a bug where the elevators suddenly won't work, most noticeable in room2tunnel - ENDSHN
			
			If (d\openstate >= 180 Or d\openstate <= 0) And GrabbedEntity = 0 Then
				For i% = 0 To 1
					If d\buttons[i] <> 0 Then
						If Abs(EntityX(Collider)-EntityX(d\buttons[i],True)) < 1.0 Then 
							If Abs(EntityZ(Collider)-EntityZ(d\buttons[i],True)) < 1.0 Then 
								dist# = Distance(EntityX(Collider, True), EntityZ(Collider, True), EntityX(d\buttons[i], True), EntityZ(d\buttons[i], True));entityDistance(collider, d\buttons[i])
								If dist < 0.7 Then
									Local temp% = CreatePivot()
									PositionEntity temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
									PointEntity temp,d\buttons[i]
									
									If EntityPick(temp, 0.6) = d\buttons[i] Then
										If ClosestButton = 0 Then
											ClosestButton = d\buttons[i]
											ClosestDoor = d
										Else
											If dist < EntityDistance(Collider, ClosestButton) Then ClosestButton = d\buttons[i] : ClosestDoor = d
										End If							
									End If
									
									FreeEntity temp
									
								EndIf							
							EndIf
						EndIf
						
					EndIf
				Next
			EndIf
			
			If d\open Then
				If d\openstate < 180 Then
					Select d\dir
						Case 0
						    ;[Block]
							d\openstate = Min(180, d\openstate + fs\FPSfactor[0] * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen*2+1) * fs\FPSfactor[0] / 80.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen+1) * fs\FPSfactor[0] / 80.0, 0, 0)	
							;[End Block]	
						Case 1
						    ;[Block]
							d\openstate = Min(180, d\openstate + fs\FPSfactor[0] * 0.8)
							MoveEntity(d\obj, Sin(d\openstate) * fs\FPSfactor[0] / 180.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, -Sin(d\openstate) * fs\FPSfactor[0] / 180.0, 0, 0)
							;[End Block]
						Case 2
						    ;[Block]
							d\openstate = Min(180, d\openstate + fs\FPSfactor[0] * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen+1) * fs\FPSfactor[0] / 85.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen*2+1) * fs\FPSfactor[0] / 120.0, 0, 0)
							;[End Block]
						Case 3
						    ;[Block]
							d\openstate = Min(180, d\openstate + fs\FPSfactor[0] * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen*2+1) * fs\FPSfactor[0] / 162.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen*2+1) * fs\FPSfactor[0] / 162.0, 0, 0)
							;[End Block]
						Case 4 ;Used for 914 only
						    ;[Block]
							d\openstate = Min(180, d\openstate + fs\FPSfactor[0] * 1.4)
							MoveEntity(d\obj, Sin(d\openstate) * fs\FPSfactor[0] / 114.0, 0, 0)
							;[End Block]
					End Select
				Else
					d\fastopen = 0
					ResetEntity(d\obj)
					If d\obj2 <> 0 Then ResetEntity(d\obj2)
					If d\timerstate > 0 Then
						d\timerstate = Max(0, d\timerstate - fs\FPSfactor[0])
						If d\timerstate + fs\FPSfactor[0] > 110 And d\timerstate <= 110 Then d\SoundCHN = PlaySound2(CautionSFX, Camera, d\obj)
						;If d\timerstate = 0 Then d\open = (Not d\open) : PlaySound2(CloseDoorSFX(Min(d\dir,1),Rand(0, 2)), Camera, d\obj)
						Local sound%
						If d\dir = 1 Then sound% = Rand(0, 1) Else sound% = Rand(0, 2)
						If d\timerstate = 0 Then d\open = (Not d\open) : d\SoundCHN = PlaySound2(CloseDoorSFX(d\dir,sound%), Camera, d\obj)
					EndIf
					If d\AutoClose And RemoteDoorOn = True Then
						If EntityDistance(Camera, d\obj) < 2.1 Then
							If (Not I_714\Using = 1) And (Not WearingGasMask = 3) And (Not WearingHazmat = 3) Then PlaySound_Strict HorrorSFX(7)
							d\open = False : d\SoundCHN = PlaySound2(CloseDoorSFX(Min(d\dir,1), Rand(0, 2)), Camera, d\obj) : d\AutoClose = False
						EndIf
					EndIf				
				EndIf
			Else
				If d\openstate > 0 Then
					Select d\dir
						Case 0
						    ;[Block]
							d\openstate = Max(0, d\openstate - fs\FPSfactor[0] * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * -fs\FPSfactor[0] * (d\fastopen+1) / 80.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -fs\FPSfactor[0] / 80.0, 0, 0)
							;[End Block]	
						Case 1
						    ;[Block]
							d\openstate = Max(0, d\openstate - fs\FPSfactor[0]*0.8)
							MoveEntity(d\obj, Sin(d\openstate) * -fs\FPSfactor[0] / 180.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * fs\FPSfactor[0] / 180.0, 0, 0)
							If d\openstate < 15 And d\openstate+fs\FPSfactor[0] => 15
								If ParticleAmount=2
									For i = 0 To Rand(75,99)
										Local pvt% = CreatePivot()
										PositionEntity(pvt, EntityX(d\frameobj,True)+Rnd(-0.2,0.2), EntityY(d\frameobj,True)+Rnd(0.0,1.2), EntityZ(d\frameobj,True)+Rnd(-0.2,0.2))
										RotateEntity(pvt, 0, Rnd(360), 0)
										
										Local p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 2, 0.002, 0, 300)
										p\speed = 0.005
										RotateEntity(p\pvt, Rnd(-20, 20), Rnd(360), 0)
										
										p\SizeChange = -0.00001
										p\size = 0.01
										ScaleSprite p\obj,p\size,p\size
										
										p\Achange = -0.01
										
										EntityOrder p\obj,-1
										
										FreeEntity pvt
									Next
								EndIf
							EndIf
							;[End Block]
						Case 2
						    ;[Block]
							d\openstate = Max(0, d\openstate - fs\FPSfactor[0] * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * -fs\FPSfactor[0] * (d\fastopen+1) / 85.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -fs\FPSfactor[0] / 120.0, 0, 0)
							;[End Block]
						Case 3
						    ;[Block]
							d\openstate = Max(0, d\openstate - fs\FPSfactor[0] * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * -fs\FPSfactor[0] * (d\fastopen+1) / 162.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -fs\FPSfactor[0] / 162.0, 0, 0)
							;[End Block]
						Case 4 ;Used for 914 only
						    ;[Block]
							d\openstate = Min(180, d\openstate - fs\FPSfactor[0] * 1.4)
							MoveEntity(d\obj, Sin(d\openstate) * -fs\FPSfactor[0] / 114.0, 0, 0)
							;[End Block]
					End Select
					
					If d\angle = 0 Or d\angle=180 Then
						If Abs(EntityZ(d\frameobj, True)-EntityZ(Collider))<0.15 Then
							If Abs(EntityX(d\frameobj, True)-EntityX(Collider))<0.7*(d\dir*2+1) Then
								z# = CurveValue(EntityZ(d\frameobj,True)+0.15*Sgn(EntityZ(Collider)-EntityZ(d\frameobj, True)), EntityZ(Collider), 5)
								PositionEntity Collider, EntityX(Collider), EntityY(Collider), z
							EndIf
						EndIf
					Else
						If Abs(EntityX(d\frameobj, True)-EntityX(Collider))<0.15 Then	
							If Abs(EntityZ(d\frameobj, True)-EntityZ(Collider))<0.7*(d\dir*2+1) Then
								x# = CurveValue(EntityX(d\frameobj,True)+0.15*Sgn(EntityX(Collider)-EntityX(d\frameobj, True)), EntityX(Collider), 5)
								PositionEntity Collider, x, EntityY(Collider), EntityZ(Collider)
							EndIf
						EndIf
					EndIf
					
					If d\DoorHitOBJ <> 0 Then
						ShowEntity d\DoorHitOBJ
					EndIf
				Else
					d\fastopen = 0
					PositionEntity(d\obj, EntityX(d\frameobj, True), EntityY(d\frameobj, True), EntityZ(d\frameobj, True))
					If d\obj2 <> 0 Then PositionEntity(d\obj2, EntityX(d\frameobj, True), EntityY(d\frameobj, True), EntityZ(d\frameobj, True))
					If d\obj2 <> 0 And d\dir = 0 Then
						MoveEntity(d\obj, 0, 0, 8.0 * RoomScale)
						MoveEntity(d\obj2, 0, 0, 8.0 * RoomScale)
					EndIf
					If d\DoorHitOBJ <> 0 Then
						HideEntity d\DoorHitOBJ
					EndIf
				EndIf
			EndIf
			
		EndIf
		UpdateSoundOrigin(d\SoundCHN,Camera,d\frameobj)
		
		If d\DoorHitOBJ<>0 Then
			If DebugHUD Then
				EntityAlpha d\DoorHitOBJ,0.5
			Else
				EntityAlpha d\DoorHitOBJ,0.0
			EndIf
		EndIf
	Next
End Function

Function UseDoor(d.Doors, showmsg%=True, playsfx%=True)
    Local fs.FPS_Settings = First FPS_Settings
	Local temp% = 0
	If d\KeyCard > 0 Then
		If SelectedItem = Null Then
			If showmsg = True Then
				If (Instr(Msg,scpLang_GetPhrase("ingame.thekey"))=0 And Instr(Msg,scpLang_GetPhrase("ingame.withkey"))=0) Or (MsgTimer<70*3) Then
					Msg = scpLang_GetPhrase("ingame.keyrequired")
					MsgTimer = 70 * 7
				EndIf
			EndIf
			Return
		Else
			Select SelectedItem\itemtemplate\tempname
				Case "key6"
				    ;[Block]
					temp = 1
					;[End Block]
				Case "key0"
				    ;[Block]
					temp = 2
					;[End Block]	
				Case "key1"
				    ;[Block]
					temp = 3
					;[End Block]
				Case "key2"
				    ;[Block]
					temp = 4
					;[End Block]
				Case "key3"
				    ;[Block]
					temp = 5
					;[End Block]
				Case "key4"
				    ;[Block]
					temp = 6
					;[End Block]
				Case "key5"
				    ;[Block]
					temp = 7
					;[End Block]
				Case "key7"
				    ;[Block]
					temp = 8
					;[End Block]
				Case "scp005"
				    ;[Block]
					temp = 9
					;[End Block]				
				Default 
				    ;[Block]
					temp = -1
					;[End Block]
			End Select
	          
			If temp =-1 Then 
				If showmsg = True Then
					If (Instr(Msg,scpLang_GetPhrase("ingame.thekey"))=0 And Instr(Msg,scpLang_GetPhrase("ingame.withkey"))=0) Or (MsgTimer<70*3) Then
						Msg = scpLang_GetPhrase("ingame.keyrequired")
						MsgTimer = 70 * 7
					EndIf
				EndIf
				Return				
			ElseIf temp >= d\KeyCard 
				SelectedItem = Null
				If showmsg = True Then
					If d\locked Then
						PlaySound_Strict KeyCardSFX2
						If temp = 9 Then 
						    Msg = scpLang_GetPhrase("ingame.nothingh")
						ElseIf temp = 1 Then 
		                    Msg = scpLang_GetPhrase("ingame.nothingh2") + " " +Chr(34)+ scpLang_GetPhrase("ingame.l6err") +Chr(34)
						Else
						    Msg = scpLang_GetPhrase("ingame.nothingh3")
						EndIf
						MsgTimer = 70 * 7
						Return
					ElseIf temp = 9 Then 					
					    PlaySound_Strict KeyCardSFX1					
						Msg = scpLang_GetPhrase("ingame.keys")
						MsgTimer = 70 * 5				
					Else
						PlaySound_Strict KeyCardSFX1
						Msg = scpLang_GetPhrase("ingame.cards")
						MsgTimer = 70 * 7	
					EndIf
				EndIf
			Else
				SelectedItem = Null
				If showmsg = True Then 
					PlaySound_Strict KeyCardSFX2					
					If d\locked Then
					    If temp = 9 Then 
						    Msg = scpLang_GetPhrase("ingame.nothingh")
						ElseIf temp = 1 Then 
		                    Msg = scpLang_GetPhrase("ingame.nothingh2") + " " +Chr(34)+ scpLang_GetPhrase("ingame.l6err") +Chr(34)
						Else					
						    Msg = scpLang_GetPhrase("ingame.nothingh3")
						EndIf						
					Else
					    If temp = 1 Then 
					        Msg = scpLang_GetPhrase("ingame.nothingh2") + " " +Chr(34)+ scpLang_GetPhrase("ingame.l6err") +Chr(34)
                        Else
					 	    Msg = scpLang_GetPhrase("ingame.carderr1") + " "+(d\KeyCard-2)+" " + scpLang_GetPhrase("ingame.carderr2")
					    EndIf
				    EndIf
				    MsgTimer = 70 * 7
				EndIf
				Return
			End If
		EndIf	
	ElseIf d\KeyCard < 0
		;I can't find any way to produce short circuited boolean expressions so work around this by using a temporary variable - risingstar64
		If SelectedItem <> Null Then
			temp = (SelectedItem\itemtemplate\tempname = "hand" And d\KeyCard=-1) Or (SelectedItem\itemtemplate\tempname = "hand2" And d\KeyCard=-2) Or (SelectedItem\itemtemplate\tempname = "hand3" And d\KeyCard=-3)
		EndIf
	    ;SelectedItem = Null
		If temp <> 0 Then
			PlaySound_Strict ScannerSFX1
			If (Instr(Msg,scpLang_GetPhrase("ingame.placedyour"))=0) Or (MsgTimer < 70*3) Then
				Msg = scpLang_GetPhrase("ingame.scannergoodhand")
			EndIf
			MsgTimer = 70 * 7
		Else
			temp = 0
            If SelectedItem <> Null
                If SelectedItem\itemtemplate\tempname = "scp005" Then temp = 1
                If SelectedItem\itemtemplate\tempname = "key0" Or SelectedItem\itemtemplate\tempname = "key1" Or SelectedItem\itemtemplate\tempname = "key2" Or SelectedItem\itemtemplate\tempname = "key3" Or SelectedItem\itemtemplate\tempname = "key4" Or SelectedItem\itemtemplate\tempname = "key5" Or SelectedItem\itemtemplate\tempname = "key6" Or SelectedItem\itemtemplate\tempname = "key7" Then temp = 2  
            EndIf
            If temp = 0
                If showmsg = True Then 
                    PlaySound_Strict ScannerSFX2
                    Msg = scpLang_GetPhrase("ingame.scannerbad")
                    MsgTimer = 70 * 7
                    Return
                EndIf
            ElseIf temp = 1
                PlaySound_Strict ScannerSFX1
                Msg = scpLang_GetPhrase("ingame.scannerwtf")
                MsgTimer = 70 * 7 
            ElseIf temp = 2
                Msg = scpLang_GetPhrase("ingame.scannererr")
                MsgTimer = 70 * 7
                Return
            EndIf
		EndIf
	Else
		If d\locked Then
			If showmsg = True Then 
				If Not (d\IsElevatorDoor>0) Then
					PlaySound_Strict ButtonSFX2
					If PlayerRoom\RoomTemplate\Name <> "room2elevator" Then
                        If d\open Then
                            Msg = scpLang_GetPhrase("ingame.doorend1")
                        Else    
                            Msg = scpLang_GetPhrase("ingame.doorend2")
                        EndIf    
                    Else
                        Msg = scpLang_GetPhrase("ingame.elevator1")
                    EndIf
					MsgTimer = 70 * 5
				Else
					If d\IsElevatorDoor = 1 Then
						Msg = scpLang_GetPhrase("ingame.elevator2")
						MsgTimer = 70 * 5
					ElseIf d\IsElevatorDoor = 3 Then
						Msg = scpLang_GetPhrase("ingame.elevator3")
						MsgTimer = 70 * 5
					ElseIf (Msg<>scpLang_GetPhrase("ingame.elevator2"))
						If (Msg=scpLang_GetPhrase("ingame.elevator4")) Or (MsgTimer<70*3)	
							Select Rand(10)
								Case 1
								    ;[Block]
									Msg = scpLang_GetPhrase("ingame.elevator5")
									MsgTimer = 70 * 7
									;[End Block]
								Case 2
								    ;[Block]
									Msg = scpLang_GetPhrase("ingame.elevator6")
									MsgTimer = 70 * 7
									;[End Block]
								Case 3
								    ;[Block]
									Msg = scpLang_GetPhrase("ingame.elevator7")
									MsgTimer = 70 * 7
									;[End Block]
								Case 4
									Msg = scpLang_GetPhrase("ingame.elevator8")
									MsgTimer = 70 * 7
								Case 5
									Msg = scpLang_GetPhrase("ingame.elevator9")
									MsgTimer = 70 * 7
								Default
								    ;[Block]
									Msg = scpLang_GetPhrase("ingame.elevator4")
									MsgTimer = 70 * 7
									;[End Block]
							End Select
						EndIf
					Else
						Msg = scpLang_GetPhrase("ingame.elevator4")
						MsgTimer = 70 * 7
					EndIf
				EndIf
				
			EndIf
			Return
		EndIf	
	EndIf
	
	d\open = (Not d\open)
	If d\LinkedDoor <> Null Then d\LinkedDoor\open = (Not d\LinkedDoor\open)
	
	Local sound = 0
	;If d\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
	If d\dir = 1 Then sound=Rand(0, 1) Else sound=Rand(0, 2)
	
	If playsfx=True Then
		If d\open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\timerstate = d\LinkedDoor\timer
			d\timerstate = d\timer
			d\SoundCHN = PlaySound2 (OpenDoorSFX(d\dir, sound), Camera, d\obj)
		Else
			d\SoundCHN = PlaySound2 (CloseDoorSFX(d\dir, sound), Camera, d\obj)
		EndIf
		UpdateSoundOrigin(d\SoundCHN,Camera,d\obj)
	Else
		If d\open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\timerstate = d\LinkedDoor\timer
			d\timerstate = d\timer
		EndIf
	EndIf
	
End Function

Function RemoveDoor(d.Doors)
	If d\buttons[0] <> 0 Then EntityParent d\buttons[0], 0
	If d\buttons[1] <> 0 Then EntityParent d\buttons[1], 0	
	
	If d\obj <> 0 Then FreeEntity d\obj
	If d\obj2 <> 0 Then FreeEntity d\obj2
	If d\frameobj <> 0 Then FreeEntity d\frameobj
	If d\buttons[0] <> 0 Then FreeEntity d\buttons[0]
	If d\buttons[1] <> 0 Then FreeEntity d\buttons[1]
	
	Delete d
End Function

DrawLoading(40,True)

Include "Source Code\Map_System.bb"

DrawLoading(80,True)

Include "Source Code\NPCs_System.bb"

;-------------------------------------  Events --------------------------------------------------------------

Include "Source Code\Event_System.bb"

Collisions HIT_PLAYER, HIT_MAP, 2, 2
Collisions HIT_PLAYER, HIT_PLAYER, 1, 3
Collisions HIT_ITEM, HIT_MAP, 2, 2
Collisions HIT_APACHE, HIT_APACHE, 1, 2
Collisions HIT_178, HIT_MAP, 2, 2
Collisions HIT_178, HIT_178, 1, 3
Collisions HIT_DEAD, HIT_MAP, 2, 2

Function MilliSecs2()
	Local retVal% = MilliSecs()
	If retVal < 0 Then retVal = retVal + 2147483648
	Return retVal
End Function

DrawLoading(90, True)

;----------------------------------- meshes and textures ----------------------------------------------------------------

Global Collider%, Head%

Dim LightSpriteTex%(5)

Global UnableToMove% = False
Global ShouldEntitiesFall% = True
Global PlayerFallingPickDistance# = 10.0

Global Save_MSG$ = ""
Global Save_MSG_Timer# = 0.0
Global Save_MSG_Y# = 0.0

Global MTF_CameraCheckTimer# = 0.0
Global MTF_CameraCheckDetected% = False

;{~--<MOD>--~}

;Global MTF2_CameraCheckTimer# = 0.0
;Global MTF2_CameraCheckDetected% = False

;{~--<END>--~}

Type AllTextures
    Field DecalTextureID[MaxDecalTextureIDAmount-1]
    Field OtherTextureID[MaxOtherTextureIDAmount-1]
    Field ParticleTextureID[MaxParticleTextureIDAmount-1]
    Field LightSpriteTextureID[MaxLightSpriteTextureIDAmount-1]
    Field OverlayID[MaxOverlayIDAmount-1]
    Field OverlayTextureID[MaxOverlayTextureIDAmount-1]
End Type

Global at.AllTextures = New AllTextures

;---------------------------------------------------------------------------------------------------

Include "Source Code\Menu.bb"
ms\MainMenuOpen = True

;---------------------------------------------------------------------------------------------------

Type MEMORYSTATUS
    Field dwLength%
    Field dwMemoryLoad%
    Field dwTotalPhys%
    Field dwAvailPhys%
    Field dwTotalPageFile%
    Field dwAvailPageFile%
    Field dwTotalVirtual%
    Field dwAvailVirtual%
End Type

Global m.MEMORYSTATUS = New MEMORYSTATUS

FlushKeys()
FlushMouse()

DrawLoading(100, True)

fs\LoopDelay = MilliSecs()

Global UpdateParticles_Time# = 0.0

Global CurrTrisAmount%

Global Input_ResetTime# = 0

Type MapZones
	Field Transition%[1]
	Field HasCustomForest%
	Field HasCustomMT%
End Type

Global I_Zone.MapZones = New MapZones

Type SCP427
	Field Using%
	Field Timer#
	Field Sound[1]
	Field SoundCHN[1]
End Type

Global I_427.SCP427 = New SCP427

Type SCP215
    Field Using%
    Field Timer#
    Field IdleTimer#
    Field Sound%[1]
    Field Limit%
End Type

Global I_215.SCP215 = New SCP215

Type SCP207
    Field Timer#
End Type

Global I_207.SCP207 = New SCP207

Type SCP402
    Field Using%
    Field Timer#
End Type

Global I_402.SCP402 = New SCP402

Type SCP357
    Field Timer#
End Type

Global I_357.SCP357 = New SCP357

Type SCP1033RU
    Field HP%
    Field DHP% ;Lost HP
    Field Using%
End Type

Global I_1033RU.SCP1033RU = New SCP1033RU

Type SCP1079
    Field Foam#
    Field Trigger%
    Field Take%
    Field Limit%
End Type

Global I_1079.SCP1079 = New SCP1079

Type SCP008
    Field Timer#
    Field Zombie%
    Field Sound%[7]
    Field Sound2%[1]
End Type

Global I_008.SCP008 = New SCP008

Type SCP409
    Field Timer#
    Field Sound%[0]
End Type 

Global I_409.SCP409 = New SCP409

Type SCP178
    Field Using%
    Field canSpawn178%
End Type 

Global I_178.SCP178 = New SCP178

Type SCP1499
    Field Using%
    Field PrevX#
    Field PrevY#
    Field PrevZ#
    Field PrevRoom.Rooms
    Field X#
    Field Y#
    Field Z#
    Field Sky%
End Type

Global I_1499.SCP1499 = New SCP1499

Type SCP447
    Field UsingEyeDrops%
    Field UsingEyeDropsTimer#
    Field UsingFirstAid%
    Field UsingFirstAidTimer#
    Field UsingPill%
    Field UsingPillTimer#
End Type

Global I_447.SCP447 = New SCP447

Type SCP714
    Field Using%
End Type

Global I_714.SCP714 = New SCP714

Type SCP500
    Field Limit%
End Type

Global I_500.SCP500 = New SCP500

;----------------------------------------------------------------------------------------------------------------------------------------------------
;----------------------------------------------       		MAIN LOOP                 ---------------------------------------------------------------
;----------------------------------------------------------------------------------------------------------------------------------------------------

Repeat
    
    If KeyHit(KEY_SCREENSHOT) Then 
		scpSDK_TakeScreenshot()
	;	n% = 0
	;	While True
	;		If FileSize("Screenshots\screenshot_" + Str(n) + ".png") = 0 Then
	;			SaveBuffer(BackBuffer(), "Screenshots\screenshot_" + Str(n) + ".png")
	;			Msg = "Screenshot taken."
	;			MsgTimer = 70*5
	;			Exit
	;		Else
	;			n = n + 1
	;		EndIf
	;	Wend
	EndIf
		
	Cls
	
	fs\CurTime = MilliSecs2()
	fs\ElapsedTime = (fs\CurTime - fs\PrevTime) / 1000.0
	fs\PrevTime = fs\CurTime
	fs\PrevFPSFactor = fs\FPSfactor[0]
	fs\FPSfactor[0] = Max(Min(fs\ElapsedTime * 70, 5.0), 0.2)
	fs\FPSfactor[1] = fs\FPSfactor[0]
	
	If ms\MenuOpen Or InvOpen Or OtherOpen<>Null Or ConsoleOpen Or SelectedDoor <> Null Or SelectedScreen <> Null Or Using294 Then fs\FPSfactor[0] = 0
		
	If Framelimit > 0 Then
	    ;Framelimit
		Local WaitingTime% = (1000.0 / Framelimit) - (MilliSecs2() - fs\LoopDelay)
		Delay WaitingTime%
		
		fs\LoopDelay = MilliSecs2()
	EndIf
	
	;Counting the fps
	If fs\CheckFPS < MilliSecs2() Then
		fs\FPS = fs\ElapsedLoops
		fs\ElapsedLoops = 0
		fs\CheckFPS = MilliSecs2()+1000
	EndIf
	fs\ElapsedLoops = fs\ElapsedLoops + 1
		
	If Input_ResetTime<=0.0
		DoubleClick = False
		MouseHit1 = MouseHit(1)
		If MouseHit1 Then
			If MilliSecs2() - LastMouseHit1 < 800 Then DoubleClick = True
			LastMouseHit1 = MilliSecs2()
		EndIf
		
		Local prevmousedown1 = MouseDown1
		MouseDown1 = MouseDown(1)
		If prevmousedown1 = True And MouseDown1=False Then MouseUp1 = True Else MouseUp1 = False
		
		MouseHit2 = MouseHit(2)
		
		If (Not MouseDown1) And (Not MouseHit1) Then GrabbedEntity = 0
	Else
		Input_ResetTime = Max(Input_ResetTime-fs\FPSfactor[0],0.0)
	EndIf
	
	UpdateMusic()
	If EnableSFXRelease Then AutoReleaseSounds()
	
	If ms\MainMenuOpen Then
		If ShouldPlay = 21 Then
			EndBreathSFX = LoadSound(scpModding_ProcessFilePath$("SFX\"+"Ending\MenuBreath.ogg"))
			EndBreathCHN = PlaySound(EndBreathSFX)
			ShouldPlay = 66
		ElseIf ShouldPlay = 66
			If (Not ChannelPlaying(EndBreathCHN)) Then
				FreeSound(EndBreathSFX)
				ShouldPlay = 11
			EndIf
		Else
			ShouldPlay = 11
		EndIf
		UpdateMainMenu()
		FPSMainMenu()
	Else
	    MainLoop()
	End If
	
	If BorderlessWindowed Then
		If (RealGraphicWidth<>GraphicWidth) Or (RealGraphicHeight<>GraphicHeight) Then
			SetBuffer TextureBuffer(fresize_texture)
			ClsColor 0,0,0 : Cls
			CopyRect 0,0,GraphicWidth,GraphicHeight,1024-GraphicWidth/2,1024-GraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
			SetBuffer BackBuffer()
			ClsColor 0,0,0 : Cls
			ScaleRender(0,0,2050.0 / Float(GraphicWidth) * AspectRatioRatio, 2050.0 / Float(GraphicWidth) * AspectRatioRatio)
			;might want to replace Float(GraphicWidth) with Max(GraphicWidth,GraphicHeight) if portrait sizes cause issues
			;everyone uses landscape so it's probably a non-issue
		EndIf
	EndIf
	
	;not by any means a perfect solution
	;Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
	If ScreenGamma>1.0 Then
		CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,1024-RealGraphicWidth/2,1024-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
		EntityBlend fresize_image,1
		ClsColor 0,0,0 : Cls
		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		EntityFX fresize_image,1+32
		EntityBlend fresize_image,3
		EntityAlpha fresize_image,ScreenGamma-1.0
		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
	ElseIf ScreenGamma<1.0 Then ;todo: maybe optimize this if it's too slow, alternatively give players the option to disable gamma
		CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,1024-RealGraphicWidth/2,1024-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
		EntityBlend fresize_image,1
		ClsColor 0,0,0 : Cls
		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		EntityFX fresize_image,1+32
		EntityBlend fresize_image,2
		EntityAlpha fresize_image,1.0
		SetBuffer TextureBuffer(fresize_texture2)
		ClsColor 255*ScreenGamma,255*ScreenGamma,255*ScreenGamma
		Cls
		SetBuffer BackBuffer()
		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		SetBuffer(TextureBuffer(fresize_texture2))
		ClsColor 0,0,0
		Cls
		SetBuffer(BackBuffer())
	EndIf
	EntityFX fresize_image,1
	EntityBlend fresize_image,1
	EntityAlpha fresize_image,1.0

	If Vsync = 0 Then
		Flip 0
	Else 
		Flip 1
	EndIf
Forever

Function MainLoop()

    Local fs.FPS_Settings = First FPS_Settings
    Local fo.Fonts = First Fonts
	
    UpdateStreamSounds()
		
	ShouldPlay = Min(PlayerZone,2)
		
	DrawHandIcon = False
		
	RestoreSanity = True
	ShouldEntitiesFall = True
		
	If fs\FPSfactor[0] > 0 And PlayerRoom\RoomTemplate\Name <> "dimension1499" Then UpdateSecurityCams()
	
	If PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "gatea" And PlayerRoom\RoomTemplate\Name <> "gateb" And (Not ms\MenuOpen) And (Not ConsoleOpen) And (Not InvOpen) Then 
			
		If Rand(1500) = 1 Then
			For i = 0 To 5
				If AmbientSFX(i,CurrAmbientSFX)<>0 Then
					If ChannelPlaying(AmbientSFXCHN)=0 Then FreeSound_Strict AmbientSFX(i,CurrAmbientSFX) : AmbientSFX(i,CurrAmbientSFX) = 0
				EndIf			
			Next
				
			PositionEntity (SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), 0.0, EntityZ(Camera) + Rnd(-1.0, 1.0))
				
			If Rand(3)=1 Then PlayerZone = 3
				
			If PlayerRoom\RoomTemplate\Name = "room173intro" Then 
				PlayerZone = 4
			ElseIf PlayerRoom\RoomTemplate\Name = "room860"
				For e.Events = Each Events
					If e\EventName = "room860"
						If e\EventState = 1.0
							PlayerZone = 5
							PositionEntity (SoundEmitter, EntityX(SoundEmitter), 30.0, EntityZ(SoundEmitter))
						EndIf
							
						Exit
					EndIf
				Next
			EndIf
				
			CurrAmbientSFX = Rand(0,AmbientSFXAmount(PlayerZone)-1)
			
			Select PlayerZone
				Case 0
					If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Ambient\Zone"+(PlayerZone+1)+"\ambient"+(CurrAmbientSFX+1)+".ogg"))
				Case 1
					If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Ambient\Zone"+(PlayerZone+1)+"\ambient"+(CurrAmbientSFX+1)+".ogg"))
				Case 2
					If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Ambient\Zone"+(PlayerZone+1)+"\ambient"+(CurrAmbientSFX+1)+".ogg"))
				Case 3
				    ;[Block]
					If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Ambient\General\ambient"+(CurrAmbientSFX+1)+".ogg"))
					;[End Block]
				Case 4
				    ;[Block]
					If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Ambient\Pre-breach\ambient"+(CurrAmbientSFX+1)+".ogg"))
					;[End Block]
				Case 5
				    ;[Block]
					If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Ambient\Forest\ambient"+(CurrAmbientSFX+1)+".ogg"))
					;[End Block]
			End Select
				
			AmbientSFXCHN = PlaySound2(AmbientSFX(PlayerZone,CurrAmbientSFX), Camera, SoundEmitter)
		EndIf
		UpdateSoundOrigin(AmbientSFXCHN,Camera, SoundEmitter)
			
		If Rand(50000) = 3 Then
			Local RN$ = PlayerRoom\RoomTemplate\Name$
			If RN$ <> "room860" And RN$ <> "room1123" And RN$ <> "room173intro" And RN$ <> "dimension1499" Then
				If fs\FPSfactor[0] > 0 Then LightBlink = Rnd(1.0,2.0)
				PlaySound_Strict  LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\079\Broadcast"+Rand(1, 8)+".ogg"))
			EndIf 
		EndIf
	EndIf
		
	UpdateCheckpoint1 = False
	UpdateCheckpoint2 = False
		
	If (Not ms\MenuOpen) And (Not InvOpen) And (OtherOpen=Null) And (SelectedDoor = Null) And (ConsoleOpen = False) And (Using294 = False) And (SelectedScreen = Null) And I_END\Timer => 0 Then
		LightVolume = CurveValue(TempLightVolume, LightVolume, 50.0)
		CameraFogRange(Camera, CameraFogNear*LightVolume,CameraFogFar*LightVolume)
		CameraFogColor(Camera, FogR, ForG, ForB)
		CameraFogMode Camera, 1
		CameraRange(Camera, 0.01, Min(CameraFogFar*LightVolume*1.5,28))	
		If PlayerRoom\RoomTemplate\Name<>"pocketdimension" Then
			CameraClsColor(Camera, 0,0,0)
		EndIf
			
		AmbientLight Brightness, Brightness, Brightness	
		PlayerSoundVolume = CurveValue(0.0, PlayerSoundVolume, 5.0)
			
		CanSave% = True
		UpdateDeafPlayer()
		UpdateEmitters()
		MouseLook()
		If PlayerRoom\RoomTemplate\Name = "dimension1499" And QuickLoadPercent > 0 And QuickLoadPercent < 100
			ShouldEntitiesFall = False
		EndIf
		MovePlayer()
		InFacility = CheckForPlayerInFacility()
		If PlayerRoom\RoomTemplate\Name = "dimension1499"
			If QuickLoadPercent = -1 Or QuickLoadPercent = 100
				UpdateDimension1499()
			EndIf
			UpdateLeave1499()
		ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Or (PlayerRoom\RoomTemplate\Name="gateb" And EntityY(Collider)>1040.0*RoomScale)
			UpdateDoors()
			If QuickLoadPercent = -1 Or QuickLoadPercent = 100
				UpdateEndings()
			EndIf
			UpdateScreens()
			UpdateRoomLights(Camera)
		Else
			UpdateDoors()
			If QuickLoadPercent = -1 Or QuickLoadPercent = 100
			    UpdateEvents()
			EndIf
			UpdateScreens()
			TimeCheckpointMonitors()
			Update294()
			UpdateRoomLights(Camera)
		EndIf
		UpdateDecals()
		UpdateMTF()
		UpdateMTF2()
		UpdateNPCs()
		UpdateItems()
		UpdateParticles()
		Use427()
		Use215()
	    Use207()
	    Use402()
	    Use357()
	    UpdateMonitorSaving()
		;Added a simple code for updating the Particles function depending on the fs\FPSfactor[0] (still WIP, might not be the final version of it) - ENDSHN
		UpdateParticles_Time# = Min(1,UpdateParticles_Time#+fs\FPSfactor[0])
		If UpdateParticles_Time#=1
			UpdateDevilEmitters()
			UpdateParticles_Devil()
			UpdateParticles_Time#=0
		EndIf
	EndIf
		
	If chs\InfiniteStamina% Then Stamina = Min(100, Stamina + (100.0-Stamina)*0.01*fs\FPSfactor[0])
	If chs\NoBlinking% Then BlinkTimer = Min(BLINKFREQ, BlinkTimer + (BLINKFREQ-BlinkTimer)*0.01*fs\FPSfactor[0])
		
	If fs\FPSfactor[0]=0
		UpdateWorld(0)
	Else
		UpdateWorld()
		ManipulateNPCBones()
	EndIf
	RenderWorld2()
		
	BlurVolume = Min(CurveValue(0.0, BlurVolume, 20.0),0.95)
	If BlurTimer > 0.0 Then
		BlurVolume = Max(Min(0.95, BlurTimer / 1000.0), BlurVolume)
		BlurTimer = Max(BlurTimer - fs\FPSfactor[0], 0.0)
	End If
		
	UpdateBlur(BlurVolume)
		
	Local darkA# = 0.0
	If (Not ms\MenuOpen)  Then
		If Sanity < 0 Then
			If RestoreSanity Then Sanity = Min(Sanity + fs\FPSfactor[0], 0.0)
			If Sanity < (-200) Then 
				darkA = Max(Min((-Sanity - 200) / 700.0, 0.6), darkA)
				If KillTimer => 0 Then 
					HeartBeatVolume = Min(Abs(Sanity+200)/500.0,1.0)
					HeartBeatRate = Max(70 + Abs(Sanity+200)/6.0, HeartBeatRate)
				EndIf
			EndIf
		End If
			
		If EyeStuck > 0 Then 
			BlinkTimer = BLINKFREQ
			EyeStuck = Max(EyeStuck-fs\FPSfactor[0],0)
				
			If EyeStuck < 9000 Then BlurTimer = Max(BlurTimer, (9000-EyeStuck)*0.5)
			If EyeStuck < 6000 Then darkA = Min(Max(darkA, (6000-EyeStuck)/5000.0),1.0)
			If EyeStuck < 9000 And EyeStuck+fs\FPSfactor[0] =>9000 Then 
				Msg = scpLang_GetPhrase("ingame.eyedropstear")
				MsgTimer = 70*6
			EndIf
		EndIf
			
		If BlinkTimer < 0 Then
			If BlinkTimer > - 5 Then
				darkA = Max(darkA, Sin(Abs(BlinkTimer * 18.0)))
			ElseIf BlinkTimer > - 15
				darkA = 1.0
			Else
				darkA = Max(darkA, Abs(Sin(BlinkTimer * 18.0)))
			EndIf
				
			If BlinkTimer <= - 20 Then
				;Randomizes the frequency of blinking. Scales with difficulty.
				Select SelectedDifficulty\otherFactors
					Case EASY
					    ;[Block]
						BLINKFREQ = Rnd(490,700)
						;[End Block]
					Case NORMAL
					    ;[Block]
						BLINKFREQ = Rnd(455,665)
						;[End Block]
					Case HARD
					    ;[Block]
						BLINKFREQ = Rnd(420,630)
						;[End Block]
				End Select 
				BlinkTimer = BLINKFREQ
			EndIf
				
			BlinkTimer = BlinkTimer - fs\FPSfactor[0]
		Else
			BlinkTimer = BlinkTimer - fs\FPSfactor[0] * 0.6 * BlinkEffect
			If WearingNightVision = 0 Then
			    If EyeIrritation > 0 Then BlinkTimer = BlinkTimer-Min(EyeIrritation / 100.0 + 1.0, 4.0) * fs\FPSfactor[0]
			EndIf
				
			darkA = Max(darkA, 0.0)
		End If
			
		EyeIrritation = Max(0, EyeIrritation - fs\FPSfactor[0])
			
		If BlinkEffectTimer > 0 Then
			BlinkEffectTimer = BlinkEffectTimer - (fs\FPSfactor[0]/70)
		Else
			If BlinkEffect <> 1.0 Then BlinkEffect = 1.0
		EndIf
			
		LightBlink = Max(LightBlink - (fs\FPSfactor[0] / 35.0), 0) 
		If LightBlink > 0 And (Not WearingNightVision) Then darkA = Min(Max(darkA, LightBlink * Rnd(0.3, 0.8)), 1.0)
	
		If Using294 Then darkA=1.0
			
		If (Not WearingNightVision) Then darkA = Max((1.0-SecondaryLightOn)*0.9, darkA)
			
		If KillTimer < 0 Then
			InvOpen = False
			SelectedItem = Null
			SelectedScreen = Null
			SelectedMonitor = Null
			BlurTimer = Abs(KillTimer*5)
			KillTimer = KillTimer-(fs\FPSfactor[0]*0.8)
			If KillTimer < - 360 Then 
				ms\MenuOpen = True 
				If I_END\SelectedEnding <> "" Then I_END\Timer = Min(KillTimer,-0.1)
			EndIf
			darkA = Max(darkA, Min(Abs(KillTimer / 400.0), 1.0))
		EndIf
			
		If FallTimer < 0 Then
			If SelectedItem <> Null Then
				If Instr(SelectedItem\itemtemplate\tempname,"hazmatsuit") Or Instr(SelectedItem\itemtemplate\tempname,"vest") Then
					If WearingHazmat=0 And WearingVest=0 Then
						DropItem(SelectedItem)
					EndIf
				EndIf
			EndIf
			InvOpen = False
			SelectedItem = Null
			SelectedScreen = Null
			SelectedMonitor = Null
			BlurTimer = Abs(FallTimer*10)
			FallTimer = FallTimer-fs\FPSfactor[0]
			darkA = Max(darkA, Min(Abs(FallTimer / 400.0), 1.0))				
		EndIf
			
		If SelectedItem <> Null Then
			If SelectedItem\itemtemplate\tempname = "navigator" Or SelectedItem\itemtemplate\tempname = "nav" Then darkA = Max(darkA, 0.5)
		End If
		If SelectedScreen <> Null Then darkA = Max(darkA, 0.5)
			
		EntityAlpha(at\OverlayID[13], darkA)	
	EndIf
		
	If LightFlash > 0 Then
		ShowEntity at\OverlayID[14]
		EntityAlpha(at\OverlayID[14], Max(Min(LightFlash + Rnd(-0.2, 0.2), 1.0), 0.0))
		LightFlash = Max(LightFlash - (fs\FPSfactor[0] / 70.0), 0)
	Else
		HideEntity at\OverlayID[14]
	EndIf
		
	EntityColor at\OverlayID[14],255,255,255
		
	If KeyHit(KEY_INV) And VomitTimer >= 0 Then
		If (Not UnableToMove) And (Not I_008\Zombie) And (Not Using294) Then
			Local W$ = ""
			Local V# = 0
			If SelectedItem<>Null
				W$ = SelectedItem\itemtemplate\tempname
				V# = SelectedItem\state
			EndIf
			If (W<>"vest" And W<>"finevest" And W<>"hazmatsuit" And W<>"hazmatsuit2" And W<>"hazmatsuit3") Or V=0 Or V=100
				If InvOpen Then
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
					mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
					mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
				Else
					PauseSounds()
				EndIf
				InvOpen = Not InvOpen
				If OtherOpen<>Null Then OtherOpen=Null
				SelectedItem = Null
			EndIf
		EndIf
	EndIf
		
	If KeyHit(KEY_SAVE) Then
		If SelectedDifficulty\saveType = SAVEANYWHERE Then
			RN$ = PlayerRoom\RoomTemplate\Name$
			If RN$ = "room173intro" Or (RN$ = "gateb" And EntityY(Collider)>1040.0*RoomScale) Or RN$ = "gatea"
				Msg = scpLang_GetPhrase("ingame.save1")
				MsgTimer = 70 * 4
			ElseIf (Not CanSave) Or QuickLoadPercent > -1
				Msg = scpLang_GetPhrase("ingame.save2")
				MsgTimer = 70 * 4
				If QuickLoadPercent > -1
					Msg = Msg + " " + scpLang_GetPhrase("ingame.save3")
				EndIf
			Else
				SaveGame(SavePath + CurrSave + "\")
			EndIf
		ElseIf SelectedDifficulty\saveType = SAVEONSCREENS
			If SelectedScreen=Null And SelectedMonitor=Null Then
				Msg = scpLang_GetPhrase("ingame.save4")
				MsgTimer = 70 * 4
			Else
				RN$ = PlayerRoom\RoomTemplate\Name$
				If RN$ = "room173intro" Or (RN$ = "gateb" And EntityY(Collider)>1040.0*RoomScale) Or RN$ = "gatea"
					Msg = scpLang_GetPhrase("ingame.save1")
					MsgTimer = 70 * 4
				ElseIf (Not CanSave) Or QuickLoadPercent > -1
					Msg = scpLang_GetPhrase("ingame.save2")
					MsgTimer = 70 * 4
					If QuickLoadPercent > -1
						Msg = Msg + " " + scpLang_GetPhrase("ingame.save3")
					EndIf
				Else
					If SelectedScreen<>Null
						GameSaved = False
						Playable = True
						DropSpeed = 0
					EndIf
					SaveGame(SavePath + CurrSave + "\")
				EndIf
			EndIf
		Else
			Msg = scpLang_GetPhrase("ingame.save6")
			MsgTimer = 70 * 4
		EndIf
	Else If SelectedDifficulty\saveType = SAVEONSCREENS And (SelectedScreen<>Null Or SelectedMonitor<>Null)
		If (Msg<>scpLang_GetPhrase("ingame.save5") And Msg<>scpLang_GetPhrase("ingame.save1")And Msg<>scpLang_GetPhrase("ingame.save2")) Or MsgTimer<=0 Then
			Msg = scpLang_GetPhrase("events.press") + " "+KeyName(KEY_SAVE)+" " + scpLang_GetPhrase("ingame.save7")
			MsgTimer = 70*4
		EndIf
			
		If MouseHit2 Then SelectedMonitor = Null
	EndIf
		
	If KeyHit(KEY_CONSOLE) Then
		If CanOpenConsole
			If ConsoleOpen Then
				UsedConsole = True
				ResumeSounds()
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
				mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
			Else
				PauseSounds()
			EndIf
			ConsoleOpen = (Not ConsoleOpen)
			FlushKeys()
		EndIf
	EndIf
		
	DrawGUI()
		
	If I_END\Timer < 0 Then
		If I_END\SelectedEnding <> "" Then DrawEnding()
	Else
		DrawMenu()			
	EndIf
		
	UpdateConsole()
		
	If PlayerRoom <> Null Then
		If PlayerRoom\RoomTemplate\Name = "room173intro" Then
			For e.Events = Each Events
				If e\EventName = "room173intro" Then
					If e\EventState3 => 40 And e\EventState3 < 50 Then
						If InvOpen Then
							Msg = scpLang_GetPhrase("ingame.doubledoc")
							MsgTimer=70*7
							e\EventState3 = 50
						EndIf
					EndIf
				EndIf
			Next
		EndIf
	EndIf
	
	If MsgTimer > 0
	    MsgTimer = MsgTimer-fs\FPSfactor[1]
	EndIf	
	
	If MsgTimer > 0 Then
		Local temp% = False
		If (Not InvOpen%)
			If SelectedItem <> Null
				If SelectedItem\itemtemplate\tempname = "paper" Or SelectedItem\itemtemplate\tempname = "oldpaper"
					temp% = True
				EndIf
			EndIf
		EndIf
			
		If (Not temp%)
			Color 0,0,0
			AAText((GraphicWidth / 2)+1, (GraphicHeight / 2) + 201, Msg, True, False, Min(MsgTimer / 2, 255)/255.0)
			Color 255,255,255;Min(MsgTimer / 2, 255), Min(MsgTimer / 2, 255), Min(MsgTimer / 2, 255)
			If Left(Msg,14)="Blitz3D Error!" Then
				Color 255,0,0
			EndIf
			AAText((GraphicWidth / 2), (GraphicHeight / 2) + 200, Msg, True, False, Min(MsgTimer / 2, 255)/255.0)
		Else
			Color 0,0,0
			AAText((GraphicWidth / 2)+1, (GraphicHeight * 0.94) + 1, Msg, True, False, Min(MsgTimer / 2, 255)/255.0)
			Color 255,255,255;Min(MsgTimer / 2, 255), Min(MsgTimer / 2, 255), Min(MsgTimer / 2, 255)
			If Left(Msg,14)="Blitz3D Error!" Then
				Color 255,0,0
			EndIf
			AAText((GraphicWidth / 2), (GraphicHeight * 0.94), Msg, True, False, Min(MsgTimer / 2, 255)/255.0)
		EndIf
	End If
		
	Color 255, 255, 255
	If ShowFPS Then AASetFont fo\ConsoleFont : AAText 20, 20, "FPS: " + fs\FPS : AASetFont fo\Font[0]
		
	DrawQuickLoading()
		
	UpdateAchievementMsg()
	RenderAchievementMsg()
	
End Function

Function Kill(blood%=False, fire%=False)
		If UsedConsole<>True Then
			;SetSteamAchievement("s"+achvname)
			;StoreSteamStats()
			;BS_UserStats_SetAchievement(BS_UserStats(), "s"+achvname)
			;BS_UserStats_StoreStats(BS_UserStats())
			
		EndIf
	If chs\GodMode Then Return
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
	EndIf
	
	If KillTimer >= 0 Then
		KillAnim = Rand(0,1)
		PlaySound_Strict(DamageSFX(0))
		If SelectedDifficulty\permaDeath Then
			DeleteFile(CurrentDir() + SavePath + CurrSave+"\save.txt") 
			DeleteDir(SavePath + CurrSave)
			LoadSaveGames()
		End If
		
	    KillTimer = Min(-1, KillTimer)
		ShowEntity Head
		PositionEntity(Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
		ResetEntity (Head)
		RotateEntity(Head, 0, EntityYaw(Camera), 0)
		
		If blood = True Then ShowEntity(at\OverlayID[5])
		
		If fire = True Then ShowEntity(at\OverlayID[8])	
	EndIf	
End Function

Function DrawEnding()
    Local fo.Fonts = First Fonts
	Local fs.FPS_Settings = First FPS_Settings
	
	ShowPointer()
	
	fs\FPSfactor[0] = 0
	;I_END\Timer = I_END\Timer-fs\FPSfactor[1]
	If I_END\Timer > -2000
		I_END\Timer = Max(I_END\Timer-fs\FPSfactor[1],-1111)
	Else
		I_END\Timer = I_END\Timer-fs\FPSfactor[1]
	EndIf
	
	GiveAchievement(Achv055)
	
	If (Not UsedConsole) Then
	    PutINIValue(OptionFile, "game", "th", 1)
	    UnlockThaumiel = 1
	    GiveAchievement(AchvThaumiel)
	EndIf
	
	If (Not UsedConsole) Then GiveAchievement(AchvConsole)
	If SelectedDifficulty\name = "Keter" Then GiveAchievement(AchvKeter)
	Local x,y,width,height, temp
	Local itt.ItemTemplates, r.Rooms
	
	Select Lower(I_END\SelectedEnding)
		Case "b2", "a1"
		    ;[Block]
			ClsColor Max(255+(I_END\Timer)*2.8,0), Max(255+(I_END\Timer)*2.8,0), Max(255+(I_END\Timer)*2.8,0)
			;[End Block]
		Default
		    ;[Block]
			ClsColor 0,0,0
			;[End Block]
	End Select
	
	ShouldPlay = 66
	
	Cls
	
	If I_END\Timer < -200 Then
		
		If BreathCHN <> 0 Then
			If ChannelPlaying(BreathCHN) Then StopChannel BreathCHN : Stamina = 100
		EndIf

		If I_END\Screen = 0 Then
			I_END\Screen = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"endingscreen.pt"))
			
			ShouldPlay = 23
			CurrMusicVolume = MusicVolume
			
			CurrMusicVolume = MusicVolume
			StopStream_Strict(MusicCHN)
			MusicCHN = StreamSound_Strict(scpModding_ProcessFilePath$("SFX\Music"+Music(23)+".ogg"),CurrMusicVolume,0)
			NowPlaying = ShouldPlay
			
			PlaySound_Strict LightSFX
		EndIf
		
		If I_END\Timer > -700 Then 
			
			;-200 -> -700
			;Max(50 - (Abs(KillTimer)-200),0)    =    0->50
			If Rand(1,150)<Min((Abs(I_END\Timer)-200),155) Then
				DrawImage I_END\Screen, GraphicWidth/2-400, GraphicHeight/2-400
			Else
				Color 0,0,0
				Rect 100,100,GraphicWidth-200,GraphicHeight-200
				Color 255,255,255
			EndIf
			
			If I_END\Timer+fs\FPSfactor[1] > -450 And I_END\Timer <= -450 Then
				Select Lower(I_END\SelectedEnding)
					Case "a1", "a2"
					    ;[Block]
						PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Ending\GateA\Ending"+I_END\SelectedEnding+".ogg"))
						;[End Block]
					Case "b1", "b2", "b3"
					    ;[Block]
						PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Ending\GateB\Ending"+I_END\SelectedEnding+".ogg"))
						;[End Block]
				End Select
			EndIf			
			
		Else
			
			DrawImage I_END\Screen, GraphicWidth/2-400, GraphicHeight/2-400
			
			If I_END\Timer < -1000 And I_END\Timer > -2000
				
				width = ImageWidth(PauseMenuIMG)
				height = ImageHeight(PauseMenuIMG)
				x = GraphicWidth / 2 - width / 2
				y = GraphicHeight / 2 - height / 2
				
				DrawImage PauseMenuIMG, x, y
				
				Color(255, 255, 255)
				AASetFont fo\Font[1]
				AAText(x + width / 2 + 40*MenuScale, y + 20*MenuScale, scpLang_GetPhrase("ingame.theend"), True)
				AASetFont fo\Font[0]
				
				If AchievementsMenu=0 Then 
					x = x+132*MenuScale
					y = y+122*MenuScale
					
					Local roomamount = 0, roomsfound = 0
					For r.Rooms = Each Rooms
						roomamount = roomamount + 1
						roomsfound = roomsfound + r\found
					Next
					
					Local docamount=0, docsfound=0
					For itt.ItemTemplates = Each ItemTemplates
						If itt\tempname = "paper" Then
							docamount=docamount+1
							docsfound=docsfound+itt\found
						EndIf
					Next
					
					Local scpsEncountered=1
					For i = 0 To 24
						scpsEncountered = scpsEncountered+Achievements(i)
					Next
					
					Local achievementsUnlocked =0
					For i = 0 To MAXACHIEVEMENTS-1
						achievementsUnlocked = achievementsUnlocked + Achievements(i)
					Next
					
					AAText x, y, scpLang_GetPhrase("ingame.scpsencountered") + " " +scpsEncountered
					AAText x, y+20*MenuScale, scpLang_GetPhrase("ingame.achunlocked") + " " + achievementsUnlocked+"/"+(MAXACHIEVEMENTS)
					AAText x, y+40*MenuScale, scpLang_GetPhrase("ingame.roomsfound") + " " + roomsfound+"/"+roomamount
					AAText x, y+60*MenuScale, scpLang_GetPhrase("ingame.docsdiscovered") + " " +docsfound+"/"+docamount
					AAText x, y+80*MenuScale, scpLang_GetPhrase("ingame.itemsrefined") + " " +RefinedItems
					
					If UnlockThaumiel = 0 And (Not UsedConsole)
					    AAText x, y+120*MenuScale, scpLang_GetPhrase("ingame.apollyonunlock")
					EndIf							
					
					x = GraphicWidth / 2 - width / 2
					y = GraphicHeight / 2 - height / 2
					x = x+width/2
					y = y+height-100*MenuScale
					
					If DrawButton(x-170*MenuScale,y-200*MenuScale,430*MenuScale,60*MenuScale,scpLang_GetPhrase("menu.achievements"), True) Then  
						AchievementsMenu = 1
					EndIf
					
					If DrawButton(x-170*MenuScale,y-100*MenuScale,430*MenuScale,60*MenuScale,scpLang_GetPhrase("menu.mainmenu"), True)
						ShouldPlay = 24
						NowPlaying = ShouldPlay
						For i=0 To 9
							If TempSounds[i]<>0 Then FreeSound_Strict TempSounds[i] : TempSounds[i]=0
						Next
						StopStream_Strict(MusicCHN)
						MusicCHN = StreamSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Music\"+Music(NowPlaying)+".ogg"),0.0,Mode)
						SetStreamVolume_Strict(MusicCHN,1.0*MusicVolume)
						FlushKeys()
						I_END\Timer = -2000
						InitCredits()
					EndIf
				Else
					ShouldPlay = 23
					DrawMenu()
				EndIf
			;Credits
			ElseIf I_END\Timer <= -2000
				ShouldPlay = 24
				DrawCredits()
			EndIf
			
		EndIf
		
	EndIf
	
	If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	
	AASetFont fo\Font[0]
End Function

Type CreditsLine
	Field txt$
	Field id%
	Field stay%
End Type

Global CreditsTimer# = 0.0
Global CreditsScreen%

Function InitCredits()
    Local fo.Fonts = First Fonts
	Local cl.CreditsLine
	Local file% = OpenFile("Credits.txt")
	Local l$
	
	fo\CreditsFont[0] = LoadFont_Strict("GFX\font\"+"cour\Courier New.ttf", Int(21 * (GraphicHeight / 1024.0)), 0,0,0)
	fo\CreditsFont[1] = LoadFont_Strict("GFX\font\"+"courbd\Courier New.ttf", Int(35 * (GraphicHeight / 1024.0)), 0,0,0)
	
	If CreditsScreen = 0
		CreditsScreen = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"creditsscreen.pt"))
	EndIf
	
	Repeat
		l = ReadLine(file)
		cl = New CreditsLine
		cl\txt = l
	Until Eof(file)
	
	Delete First CreditsLine
	CreditsTimer = 0
	
End Function

Function DrawCredits()
    Local fo.Fonts = First Fonts
    Local credits_Y# = (I_END\Timer+2000)/2+(GraphicHeight+10)
    Local cl.CreditsLine
    Local id%
    Local endlinesamount%
	Local LastCreditLine.CreditsLine
	Local fs.FPS_Settings = First FPS_Settings
	
    Cls
	
	If Rand(1,300)>1
		DrawImage CreditsScreen, GraphicWidth/2-400, GraphicHeight/2-400
	EndIf
	
	id = 0
	endlinesamount = 0
	LastCreditLine = Null
	Color 255,255,255
	For cl = Each CreditsLine
		cl\id = id
		If Left(cl\txt,1)="*"
			SetFont fo\CreditsFont[1]
			If cl\stay=False
				Text GraphicWidth/2,credits_Y+(24*cl\id*MenuScale),Right(cl\txt,Len(cl\txt)-1),True
			EndIf
		ElseIf Left(cl\txt,1)="/"
			LastCreditLine = Before(cl)
		Else
			SetFont fo\CreditsFont[0]
			If cl\stay=False
				Text GraphicWidth/2,credits_Y+(24*cl\id*MenuScale),cl\txt,True
			EndIf
		EndIf
		If LastCreditLine<>Null
			If cl\id>LastCreditLine\id
				cl\stay = True
			EndIf
		EndIf
		If cl\stay
			endlinesamount=endlinesamount+1
		EndIf
		id=id+1
	Next
	If (credits_Y+(24*LastCreditLine\id*MenuScale))<-StringHeight(LastCreditLine\txt)
		CreditsTimer=CreditsTimer+(0.5*fs\FPSfactor[1])
		If CreditsTimer>=0.0 And CreditsTimer<255.0
			Color Max(Min(CreditsTimer,255),0),Max(Min(CreditsTimer,255),0),Max(Min(CreditsTimer,255),0)
		ElseIf CreditsTimer>=255.0
			Color 255,255,255
			If CreditsTimer>500.0
				CreditsTimer=-255.0
			EndIf
		Else
			Color Max(Min(-CreditsTimer,255),0),Max(Min(-CreditsTimer,255),0),Max(Min(-CreditsTimer,255),0)
			If CreditsTimer>=-1.0
				CreditsTimer=-1.0
			EndIf
		EndIf
	EndIf
	If CreditsTimer<>0.0
		For cl = Each CreditsLine
			If cl\stay
				SetFont fo\CreditsFont[0]
				If Left(cl\txt,1)="/"
					Text GraphicWidth/2,(GraphicHeight/2)+(endlinesamount/2)+(24*cl\id*MenuScale),Right(cl\txt,Len(cl\txt)-1),True
				Else
					Text GraphicWidth/2,(GraphicHeight/2)+(24*(cl\id-LastCreditLine\id)*MenuScale)-((endlinesamount/2)*24*MenuScale),cl\txt,True
				EndIf
			EndIf
		Next
	EndIf
	
	If GetKey() Then CreditsTimer=-1
	
	If CreditsTimer=-1
		FreeFont fo\CreditsFont[0]
		FreeFont fo\CreditsFont[1]
		FreeImage CreditsScreen
		CreditsScreen = 0
		FreeImage I_END\Screen
		I_END\Screen = 0
		Delete Each CreditsLine
        NullGame(False)
        StopStream_Strict(MusicCHN)
        ShouldPlay = 21
        ms\MenuOpen = False
        ms\MainMenuOpen = True
        ms\MainMenuTab = 0
        CurrSave = ""
        FlushKeys()
	EndIf
    
End Function

;--------------------------------------- player controls -------------------------------------------

Function MovePlayer()
	Local Sprint# = 1.0, Speed# = 0.018, i%, angle#
	Local fs.FPS_Settings = First FPS_Settings
	
	If SuperMan Then
		Speed = Speed * 3
		
		SuperManTimer=SuperManTimer+fs\FPSfactor[0]
		
		CameraShake = Sin(SuperManTimer / 5.0) * (SuperManTimer / 1500.0)
		
		If SuperManTimer > 70 * 50 Then
			DeathMSG = scpLang_GetPhrase("ingame.superdeath")
			Kill(True)
			ShowEntity at\OverlayID[0]
		Else
			BlurTimer = 500		
			HideEntity at\OverlayID[0]
		EndIf
	End If
	
	If DeathTimer > 0 Then
		DeathTimer=DeathTimer-fs\FPSfactor[0]
		If DeathTimer < 1 Then DeathTimer = -1.0
	ElseIf DeathTimer < 0 
		Kill(True)
	EndIf
	
	If CurrSpeed > 0 Then
        Stamina = Min(Stamina + 0.15 * fs\FPSfactor[0]/1.25, 100.0)
    Else
        Stamina = Min(Stamina + 0.15 * fs\FPSfactor[0]*1.25, 100.0)
    EndIf
	
	If StaminaEffectTimer > 0 Then
		StaminaEffectTimer = StaminaEffectTimer - (fs\FPSfactor[0]/70)
	Else
		If StaminaEffect <> 1.0 Then StaminaEffect = 1.0
	EndIf
	
	Local temp#
	
	If PlayerRoom\RoomTemplate\Name<>"pocketdimension" Then 
		If KeyDown(KEY_SPRINT) Then
			If Stamina < 5 Then
				temp = 0
				If WearingGasMask>0 Or I_1499\Using>0 Then temp=1
				If ChannelPlaying(BreathCHN)=False Then BreathCHN = PlaySound_Strict(BreathSFX((temp), 0))
			ElseIf Stamina < 50
				If BreathCHN=0 Then
					temp = 0
					If WearingGasMask>0 Or I_1499\Using>0 Then temp=1
					BreathCHN = PlaySound_Strict(BreathSFX((temp), Rand(1,3)))
					ChannelVolume BreathCHN, Min((70.0-Stamina)/70.0,1.0)*SFXVolume
				Else
					If ChannelPlaying(BreathCHN)=False Then
						temp = 0
						If WearingGasMask>0 Or I_1499\Using>0 Then temp=1
						BreathCHN = PlaySound_Strict(BreathSFX((temp), Rand(1,3)))
						ChannelVolume BreathCHN, Min((70.0-Stamina)/70.0,1.0)*SFXVolume			
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	For i = 0 To MaxItemAmount-1
		If Inventory(i)<>Null Then
			If Inventory(i)\itemtemplate\tempname = "finevest" Then Stamina = Min(Stamina, 60)
		EndIf
	Next
	
	If I_714\Using Then
		Stamina = Min(Stamina, 10)
		Sanity = Max(-850, Sanity)
	EndIf
	
	If I_409\Timer > 10 Then 
		temporary = I_409\Timer / 15
		Stamina = Max(Stamina, temporary)
	EndIf
	
	If I_447\UsingPillTimer > 0 Then
		I_447\UsingPillTimer = I_447\UsingPillTimer-fs\FPSfactor[0]
		If I_447\UsingPillTimer < 1 Then I_447\UsingPillTimer = -1.0
	ElseIf I_447\UsingPillTimer < 0
		I_447\UsingPill = False
	EndIf

	If I_447\UsingFirstAidTimer > 0 Then
		I_447\UsingFirstAidTimer = I_447\UsingFirstAidTimer-fs\FPSfactor[0]
		If I_447\UsingFirstAidTimer < 1 Then I_447\UsingFirstAidTimer = -1.0
	ElseIf I_447\UsingFirstAidTimer < 0
		I_447\UsingFirstAid = False
	EndIf

    If I_447\UsingEyeDropsTimer > 0 Then
		I_447\UsingEyeDropsTimer = I_447\UsingEyeDropsTimer-fs\FPSfactor[0]
		If I_447\UsingEyeDropsTimer < 1 Then I_447\UsingEyeDropsTimer = -1.0
	ElseIf I_447\UsingEyeDropsTimer < 0
		I_447\UsingEyeDrops = False
	EndIf
	
	If I_447\UsingEyeDrops = True Then
		ShowEntity(at\OverlayID[7])
	Else
		HideEntity(at\OverlayID[7])
	EndIf
	
	If I_447\UsingEyeDrops = True Or I_447\UsingFirstAid = True Or I_447\UsingPill = True
		DeathMSG = scpLang_GetPhrase("ingame.scp447death")
	EndIf

	If Injuries > (4-MorphineHealAmount) Then
	    Kill()
	EndIf
	
	If MorphineTimer > 0 Then
		MorphineTimer=MorphineTimer-fs\FPSfactor[0]
		If MorphineTimer < 1 Then MorphineTimer = -1.0
	ElseIf MorphineTimer < 0
		If Injuries > 0 Then
			Injuries = Injuries + MorphineHealAmount
		EndIf
		UsedMorphine=False
		MorphineHealAmount=0
	EndIf
		
	If Injuries > (4-MorphineHealAmount) Then
		Kill()
	EndIf

	If I_008\Zombie Then Crouch = False
	
	If Abs(CrouchState-Crouch)<0.001 Then 
		CrouchState = Crouch
	Else
		CrouchState = CurveValue(Crouch, CrouchState, 10.0)
	EndIf
	
	If (Not chs\NoClip) Then 
		If ((KeyDown(KEY_DOWN) Or KeyDown(KEY_UP)) Or (KeyDown(KEY_RIGHT) Or KeyDown(KEY_LEFT)) And Playable) Or ForceMove>0 Then
			
			If Crouch = 0 And (KeyDown(KEY_SPRINT)) And Stamina > 0.0 And (Not I_008\Zombie) Then
				Sprint = 2.5
				Stamina = Stamina - fs\FPSfactor[0] * 0.4 * StaminaEffect
				If Stamina <= 0 Then Stamina = -20.0
			End If
			
			If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then 
				If EntityY(Collider)<2000*RoomScale Or EntityY(Collider)>2608*RoomScale Then
					Stamina = 0
					Speed = 0.015
					Sprint = 1.0					
				EndIf
			EndIf	
			
			If ForceMove > 0 Then Speed=Speed*ForceMove
			
			If SelectedItem<>Null Then
				If SelectedItem\itemtemplate\tempname = "firstaid" Or SelectedItem\itemtemplate\tempname = "finefirstaid" Or SelectedItem\itemtemplate\tempname = "firstaid2" Or SelectedItem\itemtemplate\tempname = "mintfirstaid" Or SelectedItem\itemtemplate\tempname = "mintfirstaid2" Or SelectedItem\itemtemplate\tempname = "mintfinefirstaid"
					Sprint = 0
				EndIf
			EndIf
			
			temp# = (Shake Mod 360)
			Local tempchn%
			If (Not UnableToMove%) Then Shake# = (Shake + fs\FPSfactor[0] * Min(Sprint, 1.5) * 7) Mod 720
			If temp < 180 And (Shake Mod 360) >= 180 And KillTimer>=0 Then
				If CurrStepSFX=0 Then
					temp = GetStepSound(Collider)
					
					If Sprint = 1.0 Then
						PlayerSoundVolume = Max(4.0,PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(temp, 0, Rand(0, 7)))
						ChannelVolume tempchn, (1.0-(Crouch*0.6))*SFXVolume#
					Else
						PlayerSoundVolume = Max(2.5-(Crouch*0.6),PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(temp, 1, Rand(0, 7)))
						ChannelVolume tempchn, (1.0-(Crouch*0.6))*SFXVolume#
					End If
				ElseIf CurrStepSFX=1
					tempchn% = PlaySound_Strict(Step2SFX(Rand(0, 2)))
					ChannelVolume tempchn, (1.0-(Crouch*0.4))*SFXVolume#
				ElseIf CurrStepSFX=2
					tempchn% = PlaySound_Strict(Step2SFX(Rand(3,5)))
					ChannelVolume tempchn, (1.0-(Crouch*0.4))*SFXVolume#
				ElseIf CurrStepSFX=3
					If Sprint = 1.0 Then
						PlayerSoundVolume = Max(4.0,PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
						ChannelVolume tempchn, (1.0-(Crouch*0.6))*SFXVolume#
					Else
						PlayerSoundVolume = Max(2.5-(Crouch*0.6),PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(0, 1, Rand(0, 7)))
						ChannelVolume tempchn, (1.0-(Crouch*0.6))*SFXVolume#
					End If
				EndIf
				
			EndIf	
		EndIf
	Else ;NoClip = True
		If (KeyDown(KEY_SPRINT)) Then 
			Sprint = 2.5
		ElseIf KeyDown(KEY_CROUCH)
			Sprint = 0.5
		EndIf
	EndIf
	
	If KeyHit(KEY_CROUCH) And Playable Then
	    CrouchCHN = PlaySound_Strict(CrouchSFX) 
	    Crouch = (Not Crouch)
	EndIf

	Local temp2# = (Speed * Sprint) / (1.0+CrouchState)
	
	If chs\NoClip Then 
		Shake = 0
		CurrSpeed = 0
		CrouchState = 0
		Crouch = 0
		
		RotateEntity Collider, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), 0
		
		temp2 = temp2 * NoClipSpeed
		
		If KeyDown(KEY_DOWN) Then MoveEntity Collider, 0, 0, -temp2*fs\FPSfactor[0]
		If KeyDown(KEY_UP) Then MoveEntity Collider, 0, 0, temp2*fs\FPSfactor[0]
		
		If KeyDown(KEY_LEFT) Then MoveEntity Collider, -temp2*fs\FPSfactor[0], 0, 0
		If KeyDown(KEY_RIGHT) Then MoveEntity Collider, temp2*fs\FPSfactor[0], 0, 0	
		
		ResetEntity Collider
	Else
		temp2# = temp2 / Max((Injuries+3.0)/3.0,1.0)
		If Injuries > 0.5 Then 
			temp2 = temp2*Min((Sin(Shake/2)+1.2),1.0)
		EndIf
		
		temp = False
		If (Not I_008\Zombie%)
			If KeyDown(KEY_DOWN) And Playable Then
				temp = True 
				angle = 180
				If KeyDown(KEY_LEFT) Then angle = 135 
				If KeyDown(KEY_RIGHT) Then angle = -135 
			ElseIf (KeyDown(KEY_UP) And Playable) Then; Or ForceMove>0
				temp = True
				angle = 0
				If KeyDown(KEY_LEFT) Then angle = 45 
				If KeyDown(KEY_RIGHT) Then angle = -45 
			ElseIf ForceMove>0 Then
				temp=True
				angle = ForceAngle
			Else If Playable Then
				If KeyDown(KEY_LEFT) Then angle = 90 : temp = True
				If KeyDown(KEY_RIGHT) Then angle = -90 : temp = True 
			EndIf
		Else
			temp=True
			angle = ForceAngle
		EndIf
		
		angle = WrapAngle(EntityYaw(Collider,True)+angle+90.0)
		
		If temp Then 
			CurrSpeed = CurveValue(temp2, CurrSpeed, 20.0)
		Else
			CurrSpeed = Max(CurveValue(0.0, CurrSpeed-0.1, 1.0),0.0)
		EndIf
		
		If (Not UnableToMove%) Then TranslateEntity Collider, Cos(angle)*CurrSpeed * fs\FPSfactor[0], 0, Sin(angle)*CurrSpeed * fs\FPSfactor[0], True
		
		Local CollidedFloor% = False
		For i = 1 To CountCollisions(Collider)
			If CollisionY(Collider, i) < EntityY(Collider) - 0.25 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			If DropSpeed# < - 0.07 Then 
				If CurrStepSFX=0 Then
					PlaySound_Strict(StepSFX(GetStepSound(Collider), 0, Rand(0, 7)))
				ElseIf CurrStepSFX=1
					PlaySound_Strict(Step2SFX(Rand(0, 2)))
				ElseIf CurrStepSFX=2
					PlaySound_Strict(Step2SFX(Rand(3, 5)))
				ElseIf CurrStepSFX=3
					PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
				EndIf
				PlayerSoundVolume = Max(3.0,PlayerSoundVolume)
			EndIf
			DropSpeed# = 0
		Else
			;DropSpeed# = Min(Max(DropSpeed - 0.006 * FPSfactor, -2.0), 0.0)
			If PlayerFallingPickDistance#<>0.0
				Local pick = LinePick(EntityX(Collider),EntityY(Collider),EntityZ(Collider),0,-PlayerFallingPickDistance,0)
				If pick
					DropSpeed# = Min(Max(DropSpeed - 0.006 * fs\FPSfactor[0], -2.0), 0.0)
				Else
					DropSpeed# = 0
				EndIf
			Else
				DropSpeed# = Min(Max(DropSpeed - 0.006 * fs\FPSfactor[0], -2.0), 0.0)
			EndIf
		EndIf
		PlayerFallingPickDistance# = 10.0
		
		If (Not UnableToMove%) And ShouldEntitiesFall Then TranslateEntity Collider, 0, DropSpeed * fs\FPSfactor[0], 0
	EndIf

	ForceMove = False
	
	If Injuries > 1.0 And I_1079\Trigger = 0 Then
		temp2 = Bloodloss
		BlurTimer = Max(Max(Sin(MilliSecs2()/100.0)*Bloodloss*30.0,Bloodloss*2*(2.0-CrouchState)), BlurTimer)
		If (Not I_427\Using=1 And I_427\Timer < 70*360) Then
			Bloodloss = Min(Bloodloss + (Min(Injuries,3.5)/300.0)*fs\FPSfactor[0],100)
		EndIf
	ElseIf Injuries > 1.0 And I_1079\Trigger = 1 Then 
		temp2 = I_1079\Foam
		BlurTimer = Max(Max(Sin(MilliSecs2()/100.0)*I_1079\Foam*30.0,I_1079\Foam*2*(2.0-CrouchState)),BlurTimer)
		If (Not I_427\Using=1 And I_427\Timer < 70*360) Then
			I_1079\Foam = Min(I_1079\Foam + (Min(Injuries,3.5)/300.0)*fs\FPSfactor[0],100)
		EndIf

		If temp2 <= 60 And Bloodloss > 60 Then
			Msg = scpLang_GetPhrase("ingame.bloodloss")
			MsgTimer = 70*4
		EndIf
		
		If temp2 <= 60 And I_1079\Foam > 60 Then
			Msg = scpLang_GetPhrase("ingame.bloodloss")
			MsgTimer = 70*4	
		EndIf	
	EndIf
	
	Update409()
	Update008()
	
	If Bloodloss > 0 And VomitTimer >= 0 Then
		If Rnd(200)<Min(Injuries,4.0) Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
			TurnEntity pvt, 90, 0, 0
			EntityPick(pvt,0.3)
			de.decals = CreateDecal(Rand(15,16), PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
			de\size = Rnd(0.03,0.08)*Min(Injuries,3.0) : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\size, de\size
			tempchn% = PlaySound_Strict(DripSFX(Rand(0, 5)))
			ChannelVolume tempchn, Rnd(0.0,0.8)*SFXVolume
			ChannelPitch tempchn, Rand(20000,30000)
			
			FreeEntity pvt
		EndIf
		
		CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs2())/20.0)+1.0)*Bloodloss*0.2)
		
		If Bloodloss > 60 Then 
		    If Crouch = False Then CrouchCHN = PlaySound_Strict(CrouchSFX)
		    Crouch = True 
		EndIf
		If Bloodloss => 100 Then 
			Kill(True)
			HeartBeatVolume = 0.0
		ElseIf Bloodloss > 80.0
			HeartBeatRate = Max(150-(Bloodloss-80)*5, HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, 0.75+(Bloodloss-80.0)*0.0125)	
		ElseIf Bloodloss > 35.0
			HeartBeatRate = Max(70+Bloodloss, HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, (Bloodloss-35.0)/60.0)			
		EndIf
	EndIf
	
	If I_1079\Foam > 0 Then
		If Rnd(200)<Min(Injuries,4.0) Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
			TurnEntity pvt, 90, 0, 0
			EntityPick(pvt,0.3)
			de.decals = CreateDecal(Rand(22,23), PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
			de\size = Rnd(0.03,0.08)*Min(Injuries,3.0) : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\size, de\size
			tempchn% = PlaySound_Strict (SizzSFX(Rand(0,1)))
			ChannelVolume tempchn, Rnd(0.0,0.8)*SFXVolume
			ChannelPitch tempchn, Rand(20000,30000)
			
			FreeEntity pvt
		EndIf
		
		CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs2())/20.0)+1.0)*Bloodloss*0.2)
		
		If I_1079\Foam > 60 Then 
		    If Crouch = False Then CrouchCHN = PlaySound_Strict(CrouchSFX) 
		    Crouch = True
		EndIf
		If I_1079\Foam => 100 Then 
			Kill(True)
			HeartBeatVolume = 0.0
		ElseIf I_1079\Foam > 80.0
			HeartBeatRate = Max(150-(I_1079\Foam-80)*5,HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, 0.75+(I_1079\Foam-80.0)*0.0125)	
		ElseIf I_1079\Foam > 35.0
			HeartBeatRate = Max(70+I_1079\Foam,HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, (I_1079\Foam-35.0)/60.0)			
		EndIf
	EndIf
	
	If HealTimer > 0 Then
		HealTimer = HealTimer - (fs\FPSfactor[0] / 70)
		Bloodloss = Min(Bloodloss + (2 / 400.0) * fs\FPSfactor[0], 100)
		Injuries = Max(Injuries - (fs\FPSfactor[0] / 70) / 30, 0.0)
	EndIf
		
	If Playable Then
		If KeyHit(KEY_BLINK) Then BlinkTimer = 0
		If KeyDown(KEY_BLINK) And BlinkTimer < - 10 Then BlinkTimer = -10
	EndIf
	
	
	If HeartBeatVolume > 0 Then
		If HeartBeatTimer <= 0 Then
			tempchn = PlaySound_Strict (HeartBeatSFX)
			ChannelVolume tempchn, HeartBeatVolume*SFXVolume#
			
			HeartBeatTimer = 70.0*(60.0/Max(HeartBeatRate,1.0))
		Else
			HeartBeatTimer = HeartBeatTimer - fs\FPSfactor[0]
		EndIf
		
		HeartBeatVolume = Max(HeartBeatVolume - fs\FPSfactor[0]*0.05, 0)
	EndIf
	
End Function

Function MouseLook()
	Local i%, incrZoom#
	Local fs.FPS_Settings = First FPS_Settings
	
	CameraShake = Max(CameraShake - (fs\FPSfactor[0] / 10), 0)
	
	;CameraZoomTemp = CurveValue(CurrCameraZoom,CameraZoomTemp, 5.0)
	CameraZoom(Camera, Min(1.0+(CurrCameraZoom/400.0),1.1) / (Tan((2*ATan(Tan((FOV#)/2)*(Float(RealGraphicWidth)/Float(RealGraphicHeight))))/2.0)))
	CurrCameraZoom = Max(CurrCameraZoom - fs\FPSfactor[0], 0)
		 
	If KillTimer >= 0 And FallTimer >=0 Then
		
		HeadDropSpeed = 0
		
		;If 0 Then 
		;fixing the black screen bug with some bubblegum code 
		Local Zero# = 0.0
		Local Nan1# = 0.0 / Zero
		If Int(EntityX(Collider))=Int(Nan1) Then
			
			PositionEntity Collider, EntityX(Camera, True), EntityY(Camera, True) - 0.5, EntityZ(Camera, True), True
			Msg = "EntityX(Collider) = NaN, RESETTING COORDINATES    -    New coordinates: "+EntityX(Collider)
			MsgTimer = 300				
		EndIf
		;EndIf
		
		Local up# = (Sin(Shake) / (20.0+CrouchState*20.0))*0.6;, side# = Cos(Shake / 2.0) / 35.0		
		Local roll# = Max(Min(Sin(Shake/2)*2.5*Min(Injuries+0.25,3.0),8.0),-8.0)
		
		;k?�?�nnet?�?�n kameraa sivulle jos pelaaja on vammautunut
		;RotateEntity Collider, EntityPitch(Collider), EntityYaw(Collider), Max(Min(up*30*Injuries,50),-50)
		PositionEntity Camera, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
		RotateEntity Camera, 0, EntityYaw(Collider), roll*0.5
		
		MoveEntity Camera, side, up + 0.6 + CrouchState * -0.3, 0
		
		Local mouse_x_acc#=(MouseXSpeed()*(MouseSens+0.5)*mouselook_x_inc / (1.0+WearingVest))
		Local mouse_y_acc#=(MouseYSpeed()*(MouseSens+0.5)*mouselook_y_inc / (1.0+WearingVest))
		If fs\PrevFPSFactor>0 Then
            If Abs(fs\FPSfactor[0]/fs\PrevFPSFactor-1.0)>1.0 Then
                ;lag spike detected - stop all camera movement
                mouse_x_speed_1 = 0.0
                mouse_y_speed_1 = 0.0
            EndIf
        EndIf

		If mouse_smooth Then
			mouse_x_speed_1=mouse_x_speed_1-mouse_x_acc
			mouse_y_speed_1=mouse_y_speed_1+mouse_y_acc
		Else
			RotateEntity Collider,0.0,EntityYaw(Collider)-mouse_x_acc,0.0
			user_camera_pitch#=user_camera_pitch#+mouse_y_acc
		EndIf
		Local oldYaw# = EntityYaw(Collider)
		RotateEntity Collider, 0.0, CurveAngle(EntityYaw(Collider) + mouse_x_speed_1,EntityYaw(Collider),12.0), 0.0 ; Turn the user on the Y (yaw) axis.
		mouse_x_speed_1=mouse_x_speed_1-WrapAngle180(EntityYaw(Collider)-oldYaw)
		Local oldPitch# = user_camera_pitch
		user_camera_pitch# = CurveValue(user_camera_pitch + mouse_y_speed_1,user_camera_pitch,12.0)
		mouse_y_speed_1=mouse_y_speed_1-WrapAngle180(user_camera_pitch-oldPitch)
		
		mouse_x_leverTurn=CurveValue(0.0,mouse_x_leverTurn,8.0)+mouse_x_acc
		mouse_y_leverTurn=CurveValue(0.0,mouse_y_leverTurn,8.0)+mouse_y_acc
		
		If user_camera_pitch# > 70.0 Then
			user_camera_pitch# = 70.0
		EndIf
		If user_camera_pitch# < -70.0 Then
			user_camera_pitch# = -70.0
		EndIf
		
		RotateEntity Camera, WrapAngle(user_camera_pitch + Rnd(-CameraShake, CameraShake)), WrapAngle(EntityYaw(Collider) + Rnd(-CameraShake, CameraShake)), roll ; Pitch the user;s camera up And down.
		
		If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
			If EntityY(Collider)<2000*RoomScale Or EntityY(Collider)>2608*RoomScale Then
				RotateEntity Camera, WrapAngle(EntityPitch(Camera)),WrapAngle(EntityYaw(Camera)), roll+WrapAngle(Sin(MilliSecs2()/150.0)*30.0) ; Pitch the user;s camera up And down.
			EndIf
		EndIf
		
	Else
		HideEntity Collider
		PositionEntity Camera, EntityX(Head), EntityY(Head), EntityZ(Head)
		
		Local CollidedFloor% = False
		For i = 1 To CountCollisions(Head)
			If CollisionY(Head, i) < EntityY(Head) - 0.01 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			HeadDropSpeed# = 0
		Else
			
			If KillAnim = 0 Then 
				MoveEntity Head, 0, 0, HeadDropSpeed
				RotateEntity(Head, CurveAngle(-90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) - 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			Else
				MoveEntity Head, 0, 0, -HeadDropSpeed
				RotateEntity(Head, CurveAngle(90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) + 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			EndIf
			
			HeadDropSpeed# = HeadDropSpeed - 0.002 * fs\FPSfactor[0]
		EndIf
		
		If InvertMouse Then
			TurnEntity (Camera, -MouseYSpeed() * 0.05 * fs\FPSfactor[0], -MouseXSpeed() * 0.15 * fs\FPSfactor[0], 0)
		Else
			TurnEntity (Camera, MouseYSpeed() * 0.05 * fs\FPSfactor[0], -MouseXSpeed() * 0.15 * fs\FPSfactor[0], 0)
		End If
		
	EndIf
	
	;p?�lyhiukkasia
	If ParticleAmount=2
		If Rand(35) = 1 Then
			Local pvt% = CreatePivot()
			PositionEntity(pvt, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True))
			RotateEntity(pvt, 0, Rnd(360), 0)
			If Rand(2) = 1 Then
				MoveEntity(pvt, 0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			Else
				MoveEntity(pvt, 0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			End If
			
			Local p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 2, 0.002, 0, 300)
			p\speed = 0.001
			RotateEntity(p\pvt, Rnd(-20, 20), Rnd(360), 0)
			
			p\SizeChange = -0.00001
			
			FreeEntity pvt
		End If
	EndIf
	
	; -- Limit the mouse;s movement. Using this method produces smoother mouselook movement than centering the mouse Each loop.
	If (MouseX() > mouse_right_limit) Or (MouseX() < mouse_left_limit) Or (MouseY() > mouse_bottom_limit) Or (MouseY() < mouse_top_limit)
		MoveMouse viewport_center_x, viewport_center_y
	EndIf
	
	If (Not WearingNightVision=0) Then
		ShowEntity(at\OverlayID[3])
		If WearingNightVision=2 Then
			EntityColor(at\OverlayID[3], 0,100,255)
			AmbientLightRooms(15)
		ElseIf WearingNightVision=3 Then
			EntityColor(at\OverlayID[3], 255,0,0)
			AmbientLightRooms(15)
		Else
			EntityColor(at\OverlayID[3], 0,255,0)
			AmbientLightRooms(15)
		EndIf
		EntityTexture(at\OverlayID[0], at\OverlayTextureID[4])
	Else
		AmbientLightRooms(0)
		HideEntity(at\OverlayID[3])
		EntityTexture(at\OverlayID[0], at\OverlayTextureID[0])
	EndIf
	
	If WearingGasmask > 0 Or I_1499\Using > 0 Then
        If I_714\Using = False Then
			If WearingGasMask = 2 Or I_1499\Using = 2 Then
				Stamina = Min(100, Stamina + (100.0-Stamina)*0.01*fs\FPSfactor[0])
			EndIf
		EndIf
		
		ShowEntity(at\OverlayID[1])
		ShowEntity(at\OverlayID[15])
		
		If ChannelPlaying(BreathCHN) = False And ChannelPlaying(RelaxedBreathCHN) = False Then RelaxedBreathCHN = PlaySound_Strict(RelaxedBreathSFX)

		If CurrSpeed > 0 Then
		    If KeyDown(KEY_SPRINT)
		        GasmaskBlurTimer = ((Min(GasmaskBlurTimer+fs\FPSfactor[0]*0.06, 90)))
		        If Stamina < 50
		            GasmaskBlurTimer = ((Min(GasmaskBlurTimer+fs\FPSfactor[0]*0.22, 90)))
		            If ChannelPlaying(RelaxedBreathCHN) = True Then StopChannel(RelaxedBreathCHN)
		        EndIf
		    Else
		        GasmaskBlurTimer = Max(0, GasmaskBlurTimer-fs\FPSfactor[0]*0.15)
		    EndIf
		Else
		    GasmaskBlurTimer = Max(0, GasmaskBlurTimer-fs\FPSfactor[0]*0.18)
		EndIf
										
	    EntityAlpha at\OverlayID[15], Min(((GasmaskBlurTimer*0.19)^2)/1000.0,0.5)
	Else
	    If ChannelPlaying(RelaxedBreathCHN) = True Then StopChannel(RelaxedBreathCHN)
	    GasmaskBlurTimer = Max(0, GasmaskBlurTimer-fs\FPSfactor[0]*0.18)
	    HideEntity(at\OverlayID[1])
	    HideEntity(at\OverlayID[15])
	EndIf
	
    If WearingHazmat > 0 Then
        If WearingHazmat = 1 Then
            Stamina = Min(60, Stamina)
        EndIf
        If I_714\Using = False
            If WearingHazmat = 2 
                Stamina = Min(100, Stamina + (100.0-Stamina)*0.01*fs\FPSfactor[0])
            EndIf
        EndIf
        ShowEntity(at\OverlayID[16])
    Else
        HideEntity(at\OverlayID[16])
    EndIf

    If WearingHelmet > 0 Then
        ShowEntity(at\OverlayID[6])
    Else
        HideEntity(at\OverlayID[6])
    EndIf

    If I_1033RU\Using = 1 Then
        I_1033RU\HP = 100 - I_1033RU\DHP
    ElseIf I_1033RU\Using = 2
        I_1033RU\HP = 200 - I_1033RU\DHP
    Else
        I_1033RU\HP = 0
    EndIf

    If I_1033RU\Using > 0 And I_1033RU\HP > 0 Then
        ShouldPlay = 31
    EndIf
 		
	If I_178\Using > 0 Then
		ShowEntity(at\OverlayID[11])
		ShouldPlay = 14
	Else
		HideEntity(at\OverlayID[11])
	EndIf
	
	I_178\canSpawn178 = 0
		
    If I_178\Using<>1 Then
		For n.NPCs = Each NPCs
			If (n\NPCtype = NPCtype178_1) Then
				If n\State3 > 0 Then I_178\canSpawn178 = 1
				If (n\State<=0) And (n\State3=0) Then
					RemoveNPC(n)
				Else If EntityDistance(Collider,n\Collider)>HideDistance*1.5 Then
					RemoveNPC(n)
				EndIf
			EndIf
		Next
	EndIf
		
	If (I_178\canSpawn178=1) Or (I_178\Using=1) Then
		tempint%=0
		For n.NPCs = Each NPCs
			If (n\NPCtype = NPCtype178_1) Then
				tempint=tempint+1
				If EntityDistance(Collider,n\Collider)>HideDistance*1.5 Then
					RemoveNPC(n)
				EndIf
			;If n\State<=0 Then RemoveNPC(n)
			EndIf
		Next
		If tempint<10 Then ;create the npcs
			For w.WayPoints = Each WayPoints
				Local dist#
				dist=EntityDistance(Collider,w\obj)
				If (dist<HideDistance*1.5) And (dist>1.2) And (w\door = Null) And (Rand(0,1)=1) Then
					tempint2=True
					For n.NPCs = Each NPCs
						If n\NPCtype=NPCtype178_1 Then
							If EntityDistance(n\Collider,w\obj)<0.5
								tempint2=False
						    	Exit
							EndIf
						EndIf
					Next
					If tempint2 Then
						CreateNPC(NPCtype178_1, EntityX(w\obj,True),EntityY(w\obj,True)+0.15,EntityZ(w\obj,True))
					EndIf	
				EndIf
			Next
		EndIf
	EndIf
	
	For i = 0 To 5
		If SCP1025state[i]>0 Then
			Select i
				Case 0 ;common cold
				    ;[Block]
					If fs\FPSfactor[0]>0 Then 
						If Rand(1000)=1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							End If
						EndIf
					EndIf
					Stamina = Stamina - fs\FPSfactor[0] * 0.3
					;[End Block]
				Case 1 ;chicken pox
				    ;[Block]
					If Rand(9000)=1 And Msg="" Then
						Msg=scpLang_GetPhrase("ingame.itchy")
						MsgTimer =70*4
					EndIf
					;[End Block]
				Case 2 ;cancer of the lungs
				    ;[Block]
					If fs\FPSfactor[0]>0 Then 
						If Rand(800)=1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							End If
						EndIf
					EndIf
					Stamina = Stamina - fs\FPSfactor[0] * 0.1
					;[End Block]
				Case 3 ;appendicitis
				    ;[Block]
					;0.035/sec = 2.1/min
					If (Not I_427\Using And I_427\Timer < 70*360) Then
						SCP1025state[i]=SCP1025state[i]+fs\FPSfactor[0]*0.0005
					EndIf
					If SCP1025state[i]>20.0 Then
						If SCP1025state[i]-fs\FPSfactor[0]<=20.0 Then Msg=scpLang_GetPhrase("ingame.stomach1")
						Stamina = Stamina - fs\FPSfactor[0] * 0.3
					ElseIf SCP1025state[i]>10.0
						If SCP1025state[i]-fs\FPSfactor[0]<=10.0 Then Msg=scpLang_GetPhrase("ingame.stomach2")
					EndIf
					;[End Block]
				Case 4 ;asthma
				    ;[Block]
					If Stamina < 35 Then
						If Rand(Int(140+Stamina*8))=1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							End If
						EndIf
						CurrSpeed = CurveValue(0, CurrSpeed, 10+Stamina*15)
					EndIf
					;[End Block]
				Case 5;cardiac arrest
				    ;[Block]
					If (Not I_427\Using And I_427\Timer < 70*360) Then
						SCP1025state[i]=SCP1025state[i]+fs\FPSfactor[0]*0.35
					EndIf
					;35/sec
					If SCP1025state[i]>110 Then
						HeartBeatRate=0
						BlurTimer = Max(BlurTimer, 500)
						If SCP1025state[i]>140 Then 
							DeathMSG = scpLang_GetPhrase("ingame.cardiacarrest")
							Kill()
						EndIf
					Else
						HeartBeatRate=Max(HeartBeatRate, 70+SCP1025state[i])
						HeartBeatVolume = 1.0
					EndIf
				    ;[End Block]
			End Select 
		EndIf
	Next
	
	
End Function

;--------------------------------------- GUI, menu etc ------------------------------------------------

Function DrawGUI()
	Local fo.Fonts = First Fonts
	Local temp%, x%, y%, z%, i%, yawvalue#, pitchvalue#
	Local x2#,y2#,z2#
	Local n%, xtemp, ytemp, strtemp$
	Local o.Objects = First Objects
	Local fs.FPS_Settings = First FPS_Settings
	Local e.Events, it.Items
	
	If ms\MenuOpen Or ConsoleOpen Or SelectedDoor <> Null Or InvOpen Or OtherOpen<>Null Or I_END\Timer < 0 Then
		ShowPointer()
	Else
		HidePointer()
	EndIf 	
	
	If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
		For e.Events = Each Events
			If e\room = PlayerRoom Then
				If Float(e\EventStr)<1000.0 Then
					If e\EventState > 600 Then
						If BlinkTimer < -3 And BlinkTimer > -10 Then
							If e\img = 0 Then
								If BlinkTimer > -5 And Rand(30)=1 Then
									PlaySound_Strict(DripSFX(Rand(0, 5)))
									If e\img = 0 Then e\img = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_106_face.png"))
								EndIf
							Else
								DrawImage e\img, GraphicWidth/2-Rand(390,310), GraphicHeight/2-Rand(290,310)
							EndIf
						Else
							If e\img <> 0 Then FreeImage e\img : e\img = 0
						EndIf
							
						Exit
					EndIf
				Else
					If BlinkTimer < -3 And BlinkTimer > -10 Then
						If e\img = 0 Then
							If BlinkTimer > -5 Then
								If e\img = 0 Then
									e\img = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"kneelmortal.pd"))
									If (ChannelPlaying(e\SoundCHN)) Then
										StopChannel(e\SoundCHN)
									EndIf
									e\SoundCHN = PlaySound_Strict(e\Sound)
								EndIf
							EndIf
						Else
							DrawImage e\img, GraphicWidth/2-Rand(390,310), GraphicHeight/2-Rand(290,310)
						EndIf
					Else
						If e\img <> 0 Then FreeImage e\img : e\img = 0
						If BlinkTimer < -3 Then
							If (Not ChannelPlaying(e\SoundCHN)) Then
								e\SoundCHN = PlaySound_Strict(e\Sound)
							EndIf
						Else
							If (ChannelPlaying(e\SoundCHN)) Then
								StopChannel(e\SoundCHN)
							EndIf
						EndIf
					EndIf
					
					Exit
				EndIf
			EndIf
		Next
	EndIf
	
	
	If ClosestButton <> 0 And SelectedDoor = Null And InvOpen = False And ms\MenuOpen = False And OtherOpen = Null Then
		temp% = CreatePivot()
		PositionEntity temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
		PointEntity temp, ClosestButton
		yawvalue# = WrapAngle(EntityYaw(Camera) - EntityYaw(temp))
		If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
		If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
		pitchvalue# = WrapAngle(EntityPitch(Camera) - EntityPitch(temp))
		If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
		If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
		
		FreeEntity (temp)
		
		DrawImage(HandIcon, GraphicWidth / 2 + Sin(yawvalue) * (GraphicWidth / 3) - 32, GraphicHeight / 2 - Sin(pitchvalue) * (GraphicHeight / 3) - 32)
		
		If MouseUp1 Then
			MouseUp1 = False
			If ClosestDoor <> Null Then 
				If ClosestDoor\Code <> "" Then
					SelectedDoor = ClosestDoor
				ElseIf Playable Then
					PlaySound2(ButtonSFX, Camera, ClosestButton)
					UseDoor(ClosestDoor,True)				
				EndIf
			EndIf
		EndIf
	EndIf
	
	If ClosestItem <> Null Then
		yawvalue# = -DeltaYaw(Camera, ClosestItem\collider)
		If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
		If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
		pitchvalue# = -DeltaPitch(Camera, ClosestItem\collider)
		If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
		If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
		
		DrawImage(HandIcon2, GraphicWidth / 2 + Sin(yawvalue) * (GraphicWidth / 3) - 32, GraphicHeight / 2 - Sin(pitchvalue) * (GraphicHeight / 3) - 32)
	EndIf
	
	If DrawHandIcon Then DrawImage(HandIcon, GraphicWidth / 2 - 32, GraphicHeight / 2 - 32)
	For i = 0 To 3
		If DrawArrowIcon(i) Then
			x = GraphicWidth / 2 - 32
			y = GraphicHeight / 2 - 32		
			Select i
				Case 0
				    ;[Block]
					y = y - 64 - 5
					;[End Block]
				Case 1
				    ;[Block]
					x = x + 64 + 5
					;[End Block]
				Case 2
				    ;[Block]
					y = y + 64 + 5
					;[End Block]
				Case 3
			        ;[Block]
					x = x - 5 - 64
					;[End Block]
			End Select
			DrawImage(HandIcon, x, y)
			;Color 0, 0, 0
			;Rect(x + 4, y + 4, 64 - 8, 64 - 8)
			DrawImage(ArrowIMG(i), x + 21, y + 21)
			DrawArrowIcon(i) = False
		End If
	Next
	
	If Using294 Then Use294()
	
	If HUDenabled Then 
		
		Local width% = 204, height% = 20
		x% = 80
		y% = GraphicHeight - 95
		
		Color 255, 255, 255	
		Rect (x, y, width, height, False)
		For i = 1 To Int(((width - 2) * (BlinkTimer / (BLINKFREQ))) / 10)
			DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next	
		Color 0, 0, 0
		Rect(x - 50, y, 30, 30)
		
		If BlurTimer > 550 Or BlinkEffect > 1.0 Or LightFlash > 0 Then
			Color 200, 0, 0
			Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
		ElseIf LightBlink > 0 And (Not WearingNightVision)
		    Color 200, 0, 0
			Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
		ElseIf EyeIrritation > 0 And (Not WearingNightVision)
		    Color 200, 0, 0
			Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
		Else
		    If BlinkEffect < 1.0
		        Color 0, 200, 0
			    Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
            EndIf
		End If
				
		If PlayerRoom\RoomTemplate\Name = "pocketdimension" Or I_714\Using > 0 Or Injuries >= 1.5 Or StaminaEffect > 1.0 Or WearingHazmat > 0 Or WearingVest = 2
			Color 200, 0, 0
			Rect(x - 50 - 3, y + 37, 30 + 6, 30 + 6)
		Else
		    If chs\InfiniteStamina = True Or StaminaEffect < 1.0 Or WearingGasMask = 2 Or I_1499\Using = 2
                Color 0, 200, 0
			    Rect(x - 50 - 3, y + 37, 30 + 6, 30 + 6)
            EndIf 
		End If

		Color 255, 255, 255
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		
		DrawImage BlinkIcon, x - 50, y
		
		y = GraphicHeight - 55
		Color 255, 255, 255
		Rect (x, y, width, height, False)
		For i = 1 To Int(((width - 2) * (Stamina / 100.0)) / 10)
			DrawImage(StaminaMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next	
		
		Color 0, 0, 0
		Rect(x - 50, y, 30, 30)
		
		Color 255, 255, 255
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		If Crouch Then
			DrawImage CrouchIcon, x - 50, y
		Else
			DrawImage SprintIcon, x - 50, y
		EndIf
		
		If DebugHUD Then
			Color 255, 255, 255
			AASetFont fo\ConsoleFont
			
			AAText x - 60, 40, "*******************************"
            AAText x - 60, 60, "Room: " + PlayerRoom\RoomTemplate\Name
            AAText x - 60, 80, "Room coordinates: (" + Floor(EntityX(PlayerRoom\obj) / 8.0 + 0.5) + ", " + Floor(EntityZ(PlayerRoom\obj) / 8.0 + 0.5) + ", angle: "+PlayerRoom\angle + ")"
			For ev.Events = Each Events
				If ev\room = PlayerRoom Then
					AAText x - 60, 100, "Room event: " + ev\EventName   
					AAText x - 60, 120, "state: " + ev\EventState
					AAText x - 60, 140, "state2: " + ev\EventState2   
					AAText x - 60, 160, "state3: " + ev\EventState3
					AAText x - 60, 180, "state4: " + ev\EventState4
					AAText x - 60, 200, "str: "+ ev\EventStr
					Exit
				EndIf
			Next
            AAText x - 60, 220, "Player Position: (" + f2s(EntityX(Collider), 3) + ", " + f2s(EntityY(Collider), 3) + ", " + f2s(EntityZ(Collider), 3) + ")"
			AAText x - 60, 240, "Camera Position: (" + f2s(EntityX(Camera), 3)+ ", " + f2s(EntityY(Camera), 3) +", " + f2s(EntityZ(Camera), 3) + ")"
			AAText x - 60, 260, "Player Rotation: (" + f2s(EntityPitch(Collider), 3) + ", " + f2s(EntityYaw(Collider), 3) + ", " + f2s(EntityRoll(Collider), 3) + ")"
			AAText x - 60, 280, "Camera Rotation: (" + f2s(EntityPitch(Camera), 3)+ ", " + f2s(EntityYaw(Camera), 3) +", " + f2s(EntityRoll(Camera), 3) + ")"
			GlobalMemoryStatus m.MEMORYSTATUS
			AAText x - 60, 300, "Memory: "+(m\dwAvailPhys%/1024/1024)+" MB/"+(m\dwTotalPhys%/1024/1024)+" MB ("+(m\dwAvailPhys%/1024)+" KB/"+(m\dwTotalPhys%/1024)+" KB)"
			AAText x - 60, 320, "Triangles rendered: "+CurrTrisAmount
			AAText x - 60, 340, "Active textures: "+ActiveTextures()			
			If Curr173 <> Null
				AAText x - 60, 360, "SCP - 173 Position (collider): (" + f2s(EntityX(Curr173\Collider), 3) + ", " + f2s(EntityY(Curr173\Collider), 3) + ", " + f2s(EntityZ(Curr173\Collider), 3) + ")"
				AAText x - 60, 380, "SCP - 173 Position (obj): (" + f2s(EntityX(Curr173\obj), 3) + ", " + f2s(EntityY(Curr173\obj), 3) + ", " + f2s(EntityZ(Curr173\obj), 3) + ")"
				AAText x - 60, 400, "SCP - 173 State: " + Curr173\State
			EndIf
			If Curr106 <> Null
				AAText x - 60, 420, "SCP - 106 Position: (" + f2s(EntityX(Curr106\obj), 3) + ", " + f2s(EntityY(Curr106\obj), 3) + ", " + f2s(EntityZ(Curr106\obj), 3) + ")"
				AAText x - 60, 440, "SCP - 106 Idle: " + Curr106\Idle
				AAText x - 60, 460, "SCP - 106 State: " + Curr106\State
			EndIf
			offset% = 0
			For npc.NPCs = Each NPCs
				If npc\NPCtype = NPCtype096 Then
					AAText x - 60, 480, "SCP - 096 Position: (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
					AAText x - 60, 500, "SCP - 096 Idle: " + npc\Idle
					AAText x - 60, 520, "SCP - 096 State: " + npc\State
					AAText x - 60, 540, "SCP - 096 Speed: " + f2s(npc\currspeed, 5)
				EndIf
				If npc\NPCtype = NPCtype049 Then
					AAText x - 60, 560, "SCP - 049 Position: (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
					AAText x - 60, 580, "SCP - 049 Idle: " + npc\Idle
					AAText x - 60, 600, "SCP - 049 State: " + npc\State
					AAText x - 60, 620, "SCP - 049 Speed: " + f2s(npc\currspeed, 5)
				EndIf
				If npc\NPCtype = NPCtype650 Then
					AAText x - 60, 640, "SCP - 650 Position: (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
					AAText x - 60, 660, "SCP - 650 Idle: " + npc\Idle
					AAText x - 60, 680, "SCP - 650 State: " + npc\State
					AAText x - 60, 700, "SCP - 650 Speed: " + f2s(npc\currspeed, 5)
				EndIf
				If npc\NPCtype = NPCtype066 Then
					AAText x - 60, 720, "SCP - 066 Position: (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
					AAText x - 60, 740, "SCP - 066 Idle: " + npc\Idle
					AAText x - 60, 760, "SCP - 066 State: " + npc\State
					AAText x - 60, 780, "SCP - 066 Speed: " + f2s(npc\currspeed, 5)
				EndIf
			Next
			If PlayerRoom\RoomTemplate\Name$ = "dimension1499"
				AAText x - 60, 800, "Current Chunk X/Z: ("+(Int((EntityX(Collider)+20)/40))+", "+(Int((EntityZ(Collider)+20)/40))+")"
				Local CH_Amount% = 0
				For ch.Chunk = Each Chunk
					CH_Amount = CH_Amount + 1
				Next
				AAText x - 60, 820, "Current Chunk Amount: "+CH_Amount
			Else
				AAText x - 60, 840, "Current Room Position: ("+PlayerRoom\x+", "+PlayerRoom\y+", "+PlayerRoom\z+")"
			EndIf
			If SelectedMonitor <> Null Then
				AAText x - 60, 860, "Current Monitor: "+SelectedMonitor\ScrObj
			Else
				AAText x - 60, 880, "Current Monitor: NULL"
			EndIf
            AAText x - 60, 900, "*******************************"
			AAText x + 380, 40, "*******************************"
			AAText x + 380, 60, "Stamina: " + f2s(Stamina, 3)
			AAText x + 380, 80, "Death timer: " + f2s(KillTimer, 3)               
			AAText x + 380, 100, "Blink timer: " + f2s(BlinkTimer, 3)
			AAText x + 380, 120, "Injuries: " + Injuries
			AAText x + 380, 140, "Bloodloss: " + Bloodloss
			AAText x + 380, 160, "Vomit Timer: " + VomitTimer
			AAText x + 380, 180, "Blur Timer: " + BlurTimer
			AAText x + 380, 200, "Light Blink: " + LightBlink
			AAText x + 380, 220, "Light Flash: " + LightFlash
			AAText x + 380, 240, "Sanity: " + Sanity
			AAText x + 380, 260, "Blink Effect Timer: " + BlinkEffectTimer
			AAText x + 380, 280, "Stamina Effect Timer: " + StaminaEffectTimer
			AAText x + 380, 300, "MTF Timer: " + MTFTimer
			AAText x + 380, 320, "SCP-008 Infection: "+I_008\Timer
			AAText x + 380, 340, "SCP-427 State (secs): "+Int(I_427\Timer/70.0)
			For i = 0 To 5
				AAText x + 380 , 360 + (20*i), "SCP-1025 State "+i+": "+SCP1025state[i]
			Next
			AAText x + 380, 480, "*******************************"
			
			;{~--<MOD>--~}
			
			AAText x + 720, 40, "********** MOD STATS **********"
			AAText x + 720, 60, "BubbleFoam: " + I_1079\Foam
			AAText x + 720, 80, "BubbleTrigger: " + I_1079\Trigger
			AAText x + 720, 100, "Taken SCP-1079-01: "+I_1079\Take
			AAText x + 720, 120, "MTF2 Timer: " + MTF2Timer
			If I_1033RU\Using = 1
			    AAText x + 720, 140, "HP of SCP-1033-RU: "+I_1033RU\HP+"/100"
			    AAText x + 720, 160, "Lost HP of SCP-1033-RU: "+I_1033RU\DHP+"/100"
			ElseIf I_1033RU\Using = 2
			    AAText x + 720, 140, "HP of SCP-1033-RU: "+I_1033RU\HP+"/200"
			    AAText x + 720, 160, "Lost HP of SCP-1033-RU: "+I_1033RU\DHP+"/200"
			Else
			    ;nothing
            EndIf
			AAText x + 720, 180, "SCP-409 Crystallization: "+I_409\Timer
			AAText x + 720, 200, "SCP-215 Idle State: "+I_215\IdleTimer
			AAText x + 720, 220, "SCP-215 State: "+I_215\Timer
			AAText x + 720, 240, "SCP-207 State: "+I_207\Timer
			AAText x + 720, 260, "SCP-402 State: "+I_402\Timer
			AAText x + 720, 280, "SCP-357 State: "+I_357\Timer
            AAText x + 720, 300, "*******************************"

            ;{~--<END>--~}

   			AASetFont fo\Font[0]
		EndIf
	EndIf
	
	If SelectedScreen <> Null Then
		DrawImage SelectedScreen\img, GraphicWidth/2-ImageWidth(SelectedScreen\img)/2,GraphicHeight/2-ImageHeight(SelectedScreen\img)/2
		
		If MouseUp1 Or MouseHit2 Then
			FreeImage SelectedScreen\img : SelectedScreen\img = 0
			SelectedScreen = Null
			MouseUp1 = False
		EndIf
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	
	Local shouldDrawHUD%=True
	If SelectedDoor <> Null Then
		If SelectedItem <> Null Then
			If SelectedItem\itemtemplate\tempname = "scp005" Then 
				shouldDrawHUD = False
				If SelectedDoor\Code<>"ABCD" Then
					SelectedDoor\locked = 1					
					
					If SelectedDoor\Code = Str(AccessCode) Then
						GiveAchievement(AchvMaynard)
					ElseIf SelectedDoor\Code = "7816"
						GiveAchievement(AchvHarp)
				    ElseIf SelectedDoor\Code = "2411"
				        GiveAchievement(AchvO5)
				    ElseIf SelectedDoor\Code = "1311"
				        GiveAchievement(AchvGears)
					EndIf
					
					SelectedDoor\locked = 0					
					UseDoor(SelectedDoor,True)
					SelectedDoor = Null
					PlaySound_Strict ScannerSFX1
					Msg = scpLang_GetPhrase("ingame.keykeypad")
					MsgTimer = 70 * 5
				Else
					SelectedDoor = Null
					PlaySound_Strict ScannerSFX2
					Msg = scpLang_GetPhrase("ingame.keykeypaderr")
					MsgTimer = 70 * 5
				EndIf
			EndIf
		EndIf		
		SelectedItem = Null
		
		If shouldDrawHUD Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(ClosestButton,True),EntityY(ClosestButton,True),EntityZ(ClosestButton,True)
			RotateEntity pvt, 0, EntityYaw(ClosestButton,True)-180,0
			MoveEntity pvt, 0,0,0.22
			PositionEntity Camera, EntityX(pvt),EntityY(pvt),EntityZ(pvt)
			PointEntity Camera, ClosestButton
			FreeEntity pvt	
			
			CameraProject(Camera, EntityX(ClosestButton,True),EntityY(ClosestButton,True)+MeshHeight(o\ButtonID[0])*0.015,EntityZ(ClosestButton,True))
			projY# = ProjectedY()
			CameraProject(Camera, EntityX(ClosestButton,True),EntityY(ClosestButton,True)-MeshHeight(o\ButtonID[0])*0.015,EntityZ(ClosestButton,True))
			scale# = (ProjectedY()-projy)/462.0
			
			x = GraphicWidth/2-ImageWidth(KeypadHUD)*scale/2
			y = GraphicHeight/2-ImageHeight(KeypadHUD)*scale/2		
			
			AASetFont fo\Font[2]
			If KeypadMSG <> "" Then 
				KeypadTimer = KeypadTimer-fs\FPSfactor[1]
				
				If (KeypadTimer Mod 70) < 35 Then AAText GraphicWidth/2, y+124*scale, KeypadMSG, True,True
				If KeypadTimer =<0 Then
					KeypadMSG = ""
					SelectedDoor = Null
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
					mouse_x_speed_1=0.0 : mouse_y_speed_1=0.0
					mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
				EndIf
			Else
				AAText GraphicWidth/2, y+70*scale, scpLang_GetPhrase("ingame.accesscode") + " ",True,True	
				AASetFont fo\Font[3]
				AAText GraphicWidth/2, y+124*scale, KeypadInput,True,True	
			EndIf
			
			x = x+44*scale
			y = y+249*scale
			
			For n = 0 To 3
				For i = 0 To 2
					xtemp = x+Int(58.5*scale*n)
					ytemp = y+(67*scale)*i
					
					temp = False
					If MouseOn(xtemp,ytemp, 54*scale,65*scale) And KeypadMSG = "" Then
						If MouseUp1 Then 
							PlaySound_Strict ButtonSFX
							
							Select (n+1)+(i*4)
								Case 1,2,3
								    ;[Block]
									KeypadInput=KeypadInput + ((n+1)+(i*4))
									;[End Block]
								Case 4
								    ;[Block]
									KeypadInput=KeypadInput + "0"
									;[End Block]
								Case 5,6,7
								    ;[Block]
									KeypadInput=KeypadInput + ((n+1)+(i*4)-1)
									;[End Block]
								Case 8 ;enter
								    ;[Block]
									If KeypadInput = SelectedDoor\Code Then
										PlaySound_Strict ScannerSFX1
										
										If SelectedDoor\Code = Str(AccessCode) Then
											GiveAchievement(AchvMaynard)
										ElseIf SelectedDoor\Code = "7816"
											GiveAchievement(AchvHarp)
										ElseIf SelectedDoor\Code = "2411"
										    GiveAchievement(AchvO5)
										ElseIf SelectedDoor\Code = "1311"
										    GiveAchievement(AchvGears)
										EndIf									
										
										SelectedDoor\locked = 0
										UseDoor(SelectedDoor,True)
										SelectedDoor = Null
										MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
										mouse_x_speed_1=0.0 : mouse_y_speed_1=0.0
										mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
									Else
										PlaySound_Strict ScannerSFX2
										KeypadMSG = scpLang_GetPhrase("ingame.accessdenied")
										KeypadTimer = 210
										KeypadInput = ""	
									EndIf
									;[End Block]
								Case 9,10,11
								    ;[Block]
									KeypadInput=KeypadInput + ((n+1)+(i*4)-2)
									;[End Block]
								Case 12
								    ;[Block]
									KeypadInput = ""
								    ;[End Block]
							End Select 
							
							If Len(KeypadInput)> 4 Then KeypadInput = Left(KeypadInput,4)
						EndIf
						
					Else
						temp = False
					EndIf
					
				Next
			Next
			
			If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
			
			If MouseHit2 Then
				SelectedDoor = Null
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
				mouse_x_speed_1=0.0 : mouse_y_speed_1=0.0
				mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
			EndIf
		Else
			SelectedDoor = Null
		EndIf
	Else
		KeypadInput = ""
		KeypadTimer = 0
		KeypadMSG = ""
	EndIf
	
	If KeyHit(1) And I_END\Timer = 0 And (Not Using294) Then
		If ms\MenuOpen Or InvOpen Then
			ResumeSounds()
			If OptionsMenu <> 0 Then SaveOptionsINI()
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
			mouse_x_speed_1=0.0 : mouse_y_speed_1=0.0
			mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
		Else
			PauseSounds()
		EndIf
		ms\MenuOpen = (Not ms\MenuOpen)
		
		AchievementsMenu = 0
		OptionsMenu = 0
		QuitMSG = 0
		
		SelectedDoor = Null
		SelectedScreen = Null
		SelectedMonitor = Null
		If SelectedItem <> Null Then
			If Instr(SelectedItem\itemtemplate\tempname,"vest") Or Instr(SelectedItem\itemtemplate\tempname,"hazmatsuit") Then
				If (Not WearingVest) And (Not WearingHazmat) Then
					DropItem(SelectedItem)
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
	EndIf
	
	Local spacing%
	Local PrevOtherOpen.Items
	
	Local OtherSize%,OtherAmount%
	
	Local isEmpty%
	
	Local isMouseOn%
	
	Local closedInv%
	
	If OtherOpen<>Null Then
		;[Block]
		If (PlayerRoom\RoomTemplate\Name = "gatea") Then
			HideEntity at\OverlayID[0]
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 30)
		Else If (PlayerRoom\RoomTemplate\Name = "gateb") And (EntityY(Collider)>1040.0*RoomScale)
			HideEntity at\OverlayID[0]
			CameraFogRange Camera, 5,45
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 60)
		EndIf
		
		PrevOtherOpen = OtherOpen
		OtherSize=OtherOpen\invSlots;Int(OtherOpen\state2)
		
		For i%=0 To OtherSize-1
			If OtherOpen\SecondInv[i] <> Null Then
				OtherAmount = OtherAmount+1
			EndIf
		Next
		
		;If OtherAmount > 0 Then
		;	OtherOpen\state = 1.0
		;Else
		;	OtherOpen\state = 0.0
		;EndIf
		InvOpen = False
		SelectedDoor = Null
		Local tempX% = 0
		
		width = 70
		height = 70
		spacing% = 35
		
		x = GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - (height * OtherSize /5 + spacing * (OtherSize / 5 - 1)) / 2;height
		
		ItemAmount = 0
		For  n% = 0 To OtherSize - 1
			isMouseOn% = False
			If ScaledMouseX() > x And ScaledMouseX() < x + width Then
				If ScaledMouseY() > y And ScaledMouseY() < y + height Then
					isMouseOn = True
				EndIf
			EndIf
			
			If isMouseOn Then
				MouseSlot = n
				Color 255, 0, 0
				Rect(x - 1, y - 1, width + 2, height + 2)
			EndIf
			
			DrawFrame(x, y, width, height, (x Mod 64), (x Mod 64))

			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null Then
				If (SelectedItem <> OtherOpen\SecondInv[n] Or isMouseOn) Then DrawImage(OtherOpen\SecondInv[n]\invimg, x + width / 2 - 32, y + height / 2 - 32)
			EndIf
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n] Then
			;drawimage(OtherOpen\SecondInv[n].InvIMG, x + width / 2 - 32, y + height / 2 - 32)
				If isMouseOn Then
					Color 255, 255, 255	
					AAText(x + width / 2, y + height + spacing - 15, OtherOpen\SecondInv[n]\itemtemplate\name, True)				
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = OtherOpen\SecondInv[n]
							MouseHit1 = False
							
							If DoubleClick Then
								If OtherOpen\SecondInv[n]\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(OtherOpen\SecondInv[n]\itemtemplate\sound))
								OtherOpen = Null
								closedInv=True
								InvOpen = False
								DoubleClick = False
							EndIf
						EndIf
					Else
						
					EndIf
				EndIf
				
				ItemAmount=ItemAmount+1
			Else
				If isMouseOn And MouseHit1 Then
					For z% = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null
					Next
					OtherOpen\SecondInv[n] = SelectedItem
				EndIf
				
			EndIf					
			
			x=x+width + spacing
			tempX=tempX + 1
			If tempX = 5 Then 
				tempX=0
				y = y + height*2 
				x = GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				ElseIf SelectedItem <> PrevOtherOpen\SecondInv[MouseSlot]
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				EndIf
			Else
				If MouseSlot = 66 Then
					If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
					
					ShowEntity(SelectedItem\collider)
					PositionEntity(SelectedItem\collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
					RotateEntity(SelectedItem\collider, EntityPitch(Camera), EntityYaw(Camera), 0)
					MoveEntity(SelectedItem\collider, 0, -0.1, 0.1)
					RotateEntity(SelectedItem\collider, 0, Rand(360), 0)
					ResetEntity (SelectedItem\collider)
					
					SelectedItem\DropSpeed = 0.0
					
					SelectedItem\Picked = False
					For z% = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null
					Next
					
					isEmpty=True
					If OtherOpen\itemtemplate\tempname = "wallet" Then
						If (Not isEmpty) Then
							For z% = 0 To OtherSize - 1
								If OtherOpen\SecondInv[z]<>Null
									Local name$=OtherOpen\SecondInv[z]\itemtemplate\tempname
									If name$<>"25ct" And name$<>"coin" And name$<>"key" And name$<>"scp860" And name$<>"scp005" And name$<>"scp500pill" And name$<>"pill" And name$<>"mintscp500pill" And name$<>"scp500pilldeath" And name$<>"mintpill" And name$<>"mintscp500pilldeath" Then
										isEmpty=False
										Exit
									EndIf
								EndIf
							Next
						EndIf
					Else
						For z% = 0 To OtherSize - 1
							If OtherOpen\SecondInv[z]<>Null
								isEmpty = False
								Exit
							EndIf
						Next
					EndIf
					
					If isEmpty Then
						Select OtherOpen\itemtemplate\tempname
							Case "clipboard"
							    ;[Block]
								OtherOpen\invimg = OtherOpen\itemtemplate\invimg2
								SetAnimTime OtherOpen\model, 17.0
								;[End Block]
							Case "wallet"
							    ;[Block]
								SetAnimTime OtherOpen\model, 0.0
								;[End Block]
						End Select
					EndIf
					
					SelectedItem = Null
					OtherOpen = Null
					closedInv=True
					
					MoveMouse viewport_center_x, viewport_center_y
				Else
					
					If PrevOtherOpen\SecondInv[MouseSlot] = Null Then
						For z% = 0 To OtherSize - 1
							If PrevOtherOpen\SecondInv[z] = SelectedItem Then PrevOtherOpen\SecondInv[z] = Null
						Next
						PrevOtherOpen\SecondInv[MouseSlot] = SelectedItem
						SelectedItem = Null
					ElseIf PrevOtherOpen\SecondInv[MouseSlot] <> SelectedItem
						Select SelectedItem\itemtemplate\tempname
							Default
								Msg = scpLang_GetPhrase("ingame.cannotcombine")
								MsgTimer = 70 * 5
						End Select					
					EndIf
					
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If Fullscreen Then DrawImage CursorIMG,ScaledMouseX(),ScaledMouseY()
		If (closedInv) And (Not InvOpen) Then 
			ResumeSounds() 
			OtherOpen=Null
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
			mouse_x_speed_1=0.0 : mouse_y_speed_1=0.0
			mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
		EndIf
		;[End Block]
		
	Else If InvOpen Then
		
		If (PlayerRoom\RoomTemplate\Name = "gatea") Then
			HideEntity at\OverlayID[0]
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 30)
		ElseIf (PlayerRoom\RoomTemplate\Name = "gateb") And (EntityY(Collider)>1040.0*RoomScale)
			HideEntity at\OverlayID[0]
			CameraFogRange Camera, 5,45
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 60)
		EndIf
		
		SelectedDoor = Null
		
		width% = 70
		height% = 70
		spacing% = 35
		
		x = GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - height
		
		ItemAmount = 0
		For  n% = 0 To MaxItemAmount - 1
			isMouseOn% = False
			If ScaledMouseX() > x And ScaledMouseX() < x + width Then
				If ScaledMouseY() > y And ScaledMouseY() < y + height Then
					isMouseOn = True
				End If
			EndIf
			
			If Inventory(n) <> Null Then
				Color 200, 200, 200
				Select Inventory(n)\itemtemplate\tempname 
					Case "gasmask"
					    ;[Block]
						If WearingGasMask = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "supergasmask"
					    ;[Block]
						If WearingGasMask = 2 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "gasmask3"
					    ;[Block]
						If WearingGasMask = 3 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "hazmatsuit"
					    ;[Block]
						If WearingHazmat = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "hazmatsuit2"
					    ;[Block]
						If WearingHazmat = 2 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "hazmatsuit3"
					    ;[Block]
						If WearingHazmat = 3 Then Rect(x - 3, y - 3, width + 6, height + 6)	
						;[End Block]
					Case "vest"
					    ;[Block]
						If WearingVest = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "finevest"
					    ;[Block]
						If WearingVest = 2 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "scp714"
					    ;[Block]
						If I_714\Using = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "nvgoggles"
					    ;[Block]
						If WearingNightVision = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "supernv"
					    ;[Block]
						If WearingNightVision = 2 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "scp1499"
					    ;[Block]
						If I_1499\Using = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "super1499"
					    ;[Block]
						If I_1499\Using = 2 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "finenvgoggles" 
					    ;[Block]   
						If WearingNightVision = 3 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "scp427"
					    ;[Block]
						If I_427\Using = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
												
				    ;{~--<MOD>--~}
				
					Case "scp178"
					    ;[Block]
						If I_178\Using = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "scp215"
					    ;[Block]
						If I_215\Using = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "scp1033ru"
					    ;[Block]
						If I_1033RU\Using = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "super1033ru"
					    ;[Block]
						If I_1033RU\Using = 2 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
					Case "scp402"
					    ;[Block]
					    If I_402\Using = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					    ;[End Block]
					Case "helmet"
					    ;[Block]
						If WearingHelmet = 1 Then Rect(x - 3, y - 3, width + 6, height + 6)
						;[End Block]
						
                    ;{~--<END>--~}

				End Select
			EndIf
			
			If isMouseOn Then
				MouseSlot = n
				Color 255, 0, 0
				Rect(x - 1, y - 1, width + 2, height + 2)
			EndIf
			
			Color 255, 255, 255
			DrawFrame(x, y, width, height, (x Mod 64), (x Mod 64))
			
			If Inventory(n) <> Null Then
				If (SelectedItem <> Inventory(n) Or isMouseOn) Then 
					DrawImage(Inventory(n)\invimg, x + width / 2 - 32, y + height / 2 - 32)
				EndIf
			EndIf
			
			If Inventory(n) <> Null And SelectedItem <> Inventory(n) Then
				;drawimage(Inventory(n).InvIMG, x + width / 2 - 32, y + height / 2 - 32)
				If isMouseOn Then
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = Inventory(n)
							MouseHit1 = False
							
							If DoubleClick Then
								If WearingHazmat > 0 And Instr(SelectedItem\itemtemplate\tempname,"hazmatsuit")=0 Then
									Msg = scpLang_GetPhrase("items.hazmaterr9")
									MsgTimer = 70*5
									SelectedItem = Null
									Return
								EndIf
								If Inventory(n)\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(Inventory(n)\itemtemplate\sound))
								InvOpen = False
								DoubleClick = False
							EndIf
							
						EndIf
						
						AASetFont fo\Font[0]
						Color 0,0,0
						AAText(x + width / 2 + 1, y + height + spacing - 15 + 1, Inventory(n)\name, True)							
						Color 255, 255, 255	
						AAText(x + width / 2, y + height + spacing - 15, Inventory(n)\name, True)	
						
					EndIf
				EndIf
				
				ItemAmount=ItemAmount+1
			Else
				If isMouseOn And MouseHit1 Then
					For z% = 0 To MaxItemAmount - 1
						If Inventory(z) = SelectedItem Then Inventory(z) = Null
					Next
					Inventory(n) = SelectedItem
				End If
				
			EndIf					
			
			x=x+width + spacing
			If n = 4 Then 
				y = y + height*2 
				x = GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				ElseIf SelectedItem <> Inventory(MouseSlot)
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				EndIf
			Else
				If MouseSlot = 66 Then
					Select SelectedItem\itemtemplate\tempname
						Case "vest","finevest","hazmatsuit","hazmatsuit2","hazmatsuit3"
						    ;[Block]
							Msg = scpLang_GetPhrase("ingame.doubleitem")
							MsgTimer = 70*5
						    ;[End Block] 
						Case "scp1499","super1499"
						    ;[Block]
							If I_1499\Using>0 Then
								Msg = scpLang_GetPhrase("ingame.doubleitem")
								MsgTimer = 70*5
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
						    ;[End Block]
						 
						;{~--<MOD>--~}
						
						Case "scp215"
						    ;[Block]
						    For i=0 To MaxItemAmount-1
                                If Inventory(i)<>Null Then
                                    If Inventory(i)\itemtemplate\name="SCP-215" Then
                                        If I_215\Timer >= 86.0
                                            Msg = scpLang_GetPhrase("ingame.cantdropglasses")
			                                MsgTimer = 70*6
			                                I_215\Using = 1
			                            Else
			                                DropItem(SelectedItem)
								            SelectedItem = Null
								            InvOpen = False
								        EndIf
			                            Exit
                                    EndIf
                                EndIf
                            Next
                            ;[End Block] 
                        Case "gasmask", "gasmask3", "supergasmask"
                            ;[Block]
							If WearingGasMask > 0 Then
								Msg = scpLang_GetPhrase("ingame.doubleitem")
								MsgTimer = 70*5
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
						    ;[End Block] 
						Case "scp198"
						    ;[Block]
						    Msg = scpLang_GetPhrase("ingame.cant198")
						    MsgTimer = 70*5
						    ;[End Block]
					    Case "scp402"
					        ;[Block]
						    If I_402\Timer >= 40.0 Then
						        I_402\Using = 1
								Msg = scpLang_GetPhrase("ingame.cant402")
								MsgTimer = 70*5
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
						    ;[End Block] 
						Case "scp357"
						    ;[Block]
						    If I_357\Timer >= 56.0 Then
							    Msg = scpLang_GetPhrase("ingame.cant357")
								MsgTimer = 70*5
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf 
				            ;[End Block] 
				        Case "helmet"
                            ;[Block]
							If WearingHelmet > 0 Then
								Msg = scpLang_GetPhrase("ingame.doubleitem")
								MsgTimer = 70*5
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
						    ;[End Block] 
				
                        ;{~--<END>--~}

						Default
							DropItem(SelectedItem)
							SelectedItem = Null
							InvOpen = False
					End Select
					
					MoveMouse viewport_center_x, viewport_center_y
				Else
					If Inventory(MouseSlot) = Null Then
						For z% = 0 To MaxItemAmount - 1
							If Inventory(z) = SelectedItem Then Inventory(z) = Null
						Next
						Inventory(MouseSlot) = SelectedItem
						SelectedItem = Null
					ElseIf Inventory(MouseSlot) <> SelectedItem
						Select SelectedItem\itemtemplate\tempname
							Case "paper", "key0", "key1", "key2", "key3", "key4", "key5", "key6", "key7", "misc", "oldpaper", "badge", "ticket", "25ct", "coin", "key", "scp860", "scp500pill", "scp005", "pill", "paperstrips", "mintscp500pill", "scp500pilldeath", "mintpill", "mintscp500pilldeath"
								;[Block]
								If Inventory(MouseSlot)\itemtemplate\tempname = "clipboard" Then
									;Add an item to clipboard
									Local added.Items = Null
									Local b$ = SelectedItem\itemtemplate\tempname
									Local b2$ = SelectedItem\itemtemplate\name
									If (b<>"misc" And b<>"25ct" And b<>"coin" And b<>"key" And b<>"scp860" And b<>"scp005" And b<>"scp500pill" And b<>"pill" And b<>"mintscp500pill" And b<>"scp500pilldeath" And b<>"mintpill" And b<>"mintscp500pilldeath") Or (b2="Playing Card" Or b2="Mastercard") Then
										For c% = 0 To Inventory(MouseSlot)\invSlots-1
											If (Inventory(MouseSlot)\SecondInv[c] = Null)
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\state = 1.0
													SetAnimTime Inventory(MouseSlot)\model,0.0
													Inventory(MouseSlot)\invimg = Inventory(MouseSlot)\itemtemplate\invimg
													
													For ri% = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											Msg = scpLang_GetPhrase("ingame.clipboarderr")
										Else
											If added\itemtemplate\tempname = "paper" Or added\itemtemplate\tempname = "oldpaper" Then
												Msg = scpLang_GetPhrase("ingame.clipboard1")
											ElseIf added\itemtemplate\tempname = "badge"
												Msg = added\itemtemplate\name + " " + scpLang_GetPhrase("ingame.clipboard2")
											Else
												Msg = scpLang_GetPhrase("ingame.clipboard3") + " " + added\itemtemplate\name + " " + scpLang_GetPhrase("ingame.clipboard2")
											EndIf
											
										EndIf
										MsgTimer = 70 * 5
									Else
										Msg = scpLang_GetPhrase("ingame.cannotcombine")
										MsgTimer = 70 * 5
									EndIf
								ElseIf Inventory(MouseSlot)\itemtemplate\tempname = "wallet" Then
									;Add an item to clipboard
									added.Items = Null
									b$ = SelectedItem\itemtemplate\tempname
									b2$ = SelectedItem\itemtemplate\name
									If (b<>"misc" And b<>"paper" And b<>"oldpaper" And b<> "paperstrips") Or (b2="Playing Card" Or b2="Mastercard") Then
										For c% = 0 To Inventory(MouseSlot)\invSlots-1
											If (Inventory(MouseSlot)\SecondInv[c] = Null)
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\state = 1.0
													If b<>"25ct" And b<>"coin" And b<>"key" And b<>"scp860" And b<>"scp005" And b<>"mintscp500pill" And b<>"scp500pill" And b<>"pill" And b<>"scp500pilldeath" And b<>"mintpill" And b<>"mintscp500pilldeath"
														SetAnimTime Inventory(MouseSlot)\model,3.0
													EndIf
													Inventory(MouseSlot)\invimg = Inventory(MouseSlot)\itemtemplate\invimg
													
													For ri% = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											Msg = scpLang_GetPhrase("ingame.walleterr")
										Else
											Msg = scpLang_GetPhrase("ingame.wallet1") + " "+added\itemtemplate\name+" " + scpLang_GetPhrase("ingame.wallet2")
										EndIf
										
										MsgTimer = 70 * 5
									Else
										Msg = scpLang_GetPhrase("ingame.cannotcombine")
										MsgTimer = 70 * 5
									EndIf
								Else
									Msg = scpLang_GetPhrase("ingame.cannotcombine")
									MsgTimer = 70 * 5
								EndIf
								SelectedItem = Null
								
								;[End Block]							            

							Case "battery", "bat", "mintbat"
								;[Block]
								Select Inventory(MouseSlot)\itemtemplate\name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
									    ;[Block]
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem (SelectedItem)
										SelectedItem = Null
										Inventory(MouseSlot)\state = 100.0
										Msg = scpLang_GetPhrase("ingame.nav1")
										MsgTimer = 70 * 5
										;[End Block]
									Case "S-NAV Navigator Ultimate"
									    ;[Block]
										Msg = scpLang_GetPhrase("ingame.nav2")
										MsgTimer = 70 * 5
										;[End Block]
									Case "Radio Transceiver"
									    ;[Block]
										Select Inventory(MouseSlot)\itemtemplate\tempname 
											Case "fineradio", "veryfineradio"
											    ;[Block]
												Msg = scpLang_GetPhrase("ingame.radio1")
												MsgTimer = 70 * 5
												;[End Block]
											Case "18vradio"
											    ;[Block]
												Msg = scpLang_GetPhrase("ingame.radio2")
												MsgTimer = 70 * 5
												;[End Block]
											Case "radio"
											    ;[Block]
												If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
												RemoveItem (SelectedItem)
												SelectedItem = Null
												Inventory(MouseSlot)\state = 100.0
												Msg = scpLang_GetPhrase("ingame.radio3")
												MsgTimer = 70 * 5
												;[End Block]
										End Select
										;[End Block]
									Case "Night Vision Goggles"
									    ;[Block]
										Local nvname$ = Inventory(MouseSlot)\itemtemplate\tempname
										If nvname$="nvgoggles" Or nvname$="supernv" Then
											If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
											RemoveItem (SelectedItem)
											SelectedItem = Null
											Inventory(MouseSlot)\state = 1000.0
											Msg = scpLang_GetPhrase("ingame.goggles1")
											MsgTimer = 70 * 5
										Else
											Msg = scpLang_GetPhrase("ingame.goggles2")
											MsgTimer = 70 * 5
										EndIf
										;[End Block]
									Default
									    ;[Block]
										Msg = scpLang_GetPhrase("ingame.cannotcombine")
										MsgTimer = 70 * 5
										;[End Block]	
								End Select
								;[End Block]
							Case "18vbat", "mint18vbat"
								;[Block]
								Select Inventory(MouseSlot)\itemtemplate\name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
									    ;[Block]
										Msg = scpLang_GetPhrase("ingame.nav3")
										MsgTimer = 70 * 5
										;[End Block]
									Case "S-NAV Navigator Ultimate"
									    ;[Block]
										Msg = scpLang_GetPhrase("ingame.nav2")
										MsgTimer = 70 * 5
										;[End Block]
									Case "Radio Transceiver"
									    ;[Block]
										Select Inventory(MouseSlot)\itemtemplate\tempname 
											Case "fineradio", "veryfineradio"
											    ;[Block]
												Msg = scpLang_GetPhrase("ingame.radio1")
												MsgTimer = 70 * 5
												;[End Block]		
											Case "18vradio"
											    ;[Block]
												If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
												RemoveItem (SelectedItem)
												SelectedItem = Null
												Inventory(MouseSlot)\state = 100.0
												Msg = scpLang_GetPhrase("ingame.radio3")
												MsgTimer = 70 * 5
												;[End Block]
										End Select 
										;[End Block]
									Default
									    ;[Block]
										Msg = scpLang_GetPhrase("ingame.cannotcombine")
										MsgTimer = 70 * 5
										;[End Block]	
								End Select
								;[End Block]
							Default
								;[Block]
								Msg = scpLang_GetPhrase("ingame.cannotcombine")
								MsgTimer = 70 * 5
								;[End Block]
						End Select					
					End If
					
				End If
				SelectedItem = Null
			End If
		End If
		
		If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
		
		If InvOpen = False Then 
			ResumeSounds() 
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
			mouse_x_speed_1=0.0 : mouse_y_speed_1=0.0
			mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
		EndIf
	Else ;invopen = False
		
		If SelectedItem <> Null Then
			Select SelectedItem\itemtemplate\tempname
				Case "nvgoggles"
					;[Block]
					For i = 0 To MaxItemAmount - 1
                        If Inventory(i) <> Null Then
                            If Inventory(i)\itemtemplate\name = "SCP-215" And I_215\Timer >= 86.0 Then
                                Msg = scpLang_GetPhrase("ingame.nvg215")
						        MsgTimer = 70 * 5
						        SelectedItem = Null
						        Return
                                Exit
                            EndIf
                        EndIf
                    Next

					If I_215\Using > 0
						Msg = scpLang_GetPhrase("ingame.nvg2152")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
				    ElseIf WearingGasMask > 0
				        Msg = scpLang_GetPhrase("ingame.nvggas")
				        MsgTimer = 70 * 5
				        SelectedItem = Null
						Return
					ElseIf I_1499\Using > 0
                        Msg = scpLang_GetPhrase("ingame.nvg1499")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf I_178\Using > 0
                        Msg = scpLang_GetPhrase("ingame.nvg178")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf WearingHelmet > 0
                        Msg = scpLang_GetPhrase("ingame.nvghelmet")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
                    Else			
						If WearingNightVision = 1 Then
							Msg = scpLang_GetPhrase("ingame.nvgoff")
							CameraFogFar = StoredCameraFogFar
							PlaySound_Strict(NVGUse(0))
						Else
							Msg = scpLang_GetPhrase("ingame.nvgon")
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
							PlaySound_Strict(NVGUse(1))
						EndIf
						WearingNightVision = (Not WearingNightVision)
						MsgTimer = 70 * 5
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "supernv"
					;[Block]
					For i = 0 To MaxItemAmount - 1
                        If Inventory(i) <> Null Then
                            If Inventory(i)\itemtemplate\name = "SCP-215" And I_215\Timer >= 86.0 Then
                                Msg = scpLang_GetPhrase("ingame.nvg215")
						        MsgTimer = 70 * 5
						        SelectedItem = Null
						        Return
                                Exit
                            EndIf
                        EndIf
                    Next

					If I_215\Using > 0
						Msg = scpLang_GetPhrase("ingame.nvg2152")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
				    ElseIf WearingGasMask > 0
				        Msg = scpLang_GetPhrase("ingame.nvggas")
				        MsgTimer = 70 * 5
				        SelectedItem = Null
						Return
					ElseIf I_1499\Using > 0
                        Msg = scpLang_GetPhrase("ingame.nvg1499")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf I_178\Using > 0
                        Msg = scpLang_GetPhrase("ingame.nvg178")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf WearingHelmet > 0
                        Msg = scpLang_GetPhrase("ingame.nvghelmet")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
                    Else			
						If WearingNightVision = 2 Then
							Msg = scpLang_GetPhrase("ingame.nvgoff")
							CameraFogFar = StoredCameraFogFar
							PlaySound_Strict(NVGUse(0))
						Else
							Msg = scpLang_GetPhrase("ingame.nvgon")
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
							PlaySound_Strict(NVGUse(1))
						EndIf
						WearingNightVision = (Not WearingNightVision) * 2
						MsgTimer = 70 * 5
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "finenvgoggles"
					;[Block]
					For i = 0 To MaxItemAmount - 1
                        If Inventory(i) <> Null Then
                            If Inventory(i)\itemtemplate\name = "SCP-215" And I_215\Timer >= 86.0 Then
                                Msg = scpLang_GetPhrase("ingame.nvg215")
						        MsgTimer = 70 * 5
						        SelectedItem = Null
						        Return
                                Exit
                            EndIf
                        EndIf
                    Next

					If I_215\Using > 0
						Msg = scpLang_GetPhrase("ingame.nvg2152")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
				    ElseIf WearingGasMask > 0
				        Msg = scpLang_GetPhrase("ingame.nvggas")
				        MsgTimer = 70 * 5
				        SelectedItem = Null
						Return
					ElseIf I_1499\Using > 0
                        Msg = scpLang_GetPhrase("ingame.nvg1499")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf I_178\Using > 0
                        Msg = scpLang_GetPhrase("ingame.nvg178")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf WearingHelmet > 0
                        Msg = scpLang_GetPhrase("ingame.nvghelmet")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
                    Else			
						If WearingNightVision = 3 Then
							Msg = scpLang_GetPhrase("ingame.nvgoff")
							CameraFogFar = StoredCameraFogFar
							PlaySound_Strict(NVGUse(0))
						Else
							Msg = scpLang_GetPhrase("ingame.nvgon")
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
							PlaySound_Strict(NVGUse(1))
						EndIf
						WearingNightVision = (Not WearingNightVision) * 3
						MsgTimer = 70 * 5
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "scp1123"
					;[Block]
					If (Not I_714\Using = 1) And (Not WearingHazmat=3) Or (Not WearingGasMask=3) Then
						If PlayerRoom\RoomTemplate\Name <> "room1123" Then
							ShowEntity at\OverlayID[14]
							LightFlash = 7
							PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\1123\Touch.ogg")))
							If I_1033RU\HP = 0		
							    DeathMSG = scpLang_GetPhrase("items.itemscp1123d")
							    Kill()
							Else
							    Damage1033RU(50+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
							EndIf
							Return
						EndIf
						For e.Events = Each Events
							If e\EventName = "room1123" Then 
								If e\EventState = 0 Then
									ShowEntity at\OverlayID[14]
									LightFlash = 3
									PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\1123\Touch.ogg")))		
								EndIf
								e\EventState = Max(1, e\EventState)
								Exit
							EndIf
						Next
					EndIf
					;[End Block]
				Case "key0", "key1", "key2", "key3", "key4", "key5", "key6", "key7", "keyomni", "scp860", "hand", "hand2", "25ct", "scp005", "hand3"
					;[Block]
					DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					;[End Block]
				Case "scp513"
					;[Block]
					PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\513\Bell1.ogg"))
					
					If Curr5131 = Null
						Curr5131 = CreateNPC(NPCtype513_1, 0,0,0)
					EndIf	
					SelectedItem = Null
					;[End Block]
				Case "scp500pill"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.pill1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.pillgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
						GiveAchievement(Achv500)
						
						If I_008\Timer > 0 Then
							Msg = scpLang_GetPhrase("ingame.pill008")
						ElseIf I_409\Timer > 0 Then
						    Msg = scpLang_GetPhrase("ingame.pill409")
						ElseIf I_357\Timer > 0 Then
						    Msg = scpLang_GetPhrase("ingame.pill357")
						Else
							Msg = scpLang_GetPhrase("ingame.pillnone")
						EndIf
						MsgTimer = 70*7
						
						DeathTimer = 0
						I_008\Timer = 0
						Stamina = 100
						I_409\Timer = 0
						I_207\Timer = 0
						I_357\Timer = 0
						;Returns the color of the blood back to normal
						I_1079\Take = 0
					    I_1079\Foam = 0
					    I_1079\Trigger = 0	
						
						For i = 0 To 5
							SCP1025state[i]=0
						Next
						
						If StaminaEffect > 1.0 Then
							StaminaEffect = 1.0
							StaminaEffectTimer = 0.0
						EndIf
						
						If BlinkEffect > 1.0 Then
							BlinkEffect = 1.0
							BlinkEffectTimer = 0.0
						EndIf
						
						For e.Events = Each Events
							If e\EventName="room009" Then e\EventState=0.0 : e\EventState3=0.0
						Next
						
						RemoveItem(SelectedItem)
					EndIf	
					;[End Block]
				Case "veryfinefirstaid"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.aid1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.aidgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_215\Using > 0
					    Msg = scpLang_GetPhrase("ingame.aid215")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf I_178\Using > 0
					    Msg = scpLang_GetPhrase("ingame.aid178")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.aidnvg")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
					    If (Not I_402\Using)
						    Select Rand(5)
							    Case 1
								    Injuries = 3.5
								    Msg = scpLang_GetPhrase("ingame.bleedheavy")
								    MsgTimer = 70*7
							    Case 2
								    Injuries = 0
								    Bloodloss = 0
								    I_1079\Foam = 0
								    Msg = scpLang_GetPhrase("ingame.bleedheal")
								    MsgTimer = 70*7
							    Case 3
								    Injuries = Max(0, Injuries - Rnd(0.5,3.5))
								    Bloodloss = Max(0, Bloodloss - Rnd(10,100))
								    Msg = scpLang_GetPhrase("ingame.bleedbetter")
								    MsgTimer = 70*7
							    Case 4
								    BlurTimer = 10000
								    Bloodloss = 0
								    Msg = scpLang_GetPhrase("ingame.bleednaus")
								    MsgTimer = 70*7
							    Case 5
								    BlinkTimer = -10
								    Local roomname$ = PlayerRoom\RoomTemplate\Name
								    If roomname = "dimension1499" Or roomname = "gatea" Or (roomname="gateb" And EntityY(Collider)>1040.0*RoomScale)
									    Injuries = 2.5
									    Msg = scpLang_GetPhrase("ingame.bleedheavy")
									    MsgTimer = 70*7
								    Else
									    For r.Rooms = Each Rooms
										    If r\RoomTemplate\Name = "pocketdimension" Then
											    PositionEntity(Collider, EntityX(r\obj),0.8,EntityZ(r\obj))		
											    ResetEntity Collider									
											    UpdateDoors()
											    UpdateRooms()
											    PlaySound_Strict(Use914SFX)
											    DropSpeed = 0
											    Curr106\State = -2500
											    Exit
										    EndIf
									    Next
									    Msg = scpLang_GetPhrase("ingame.pocketdimensionunknown")
									    MsgTimer = 70*8
							        EndIf
						    End Select
						
						    RemoveItem(SelectedItem)
						Else
                            Msg = Chr(34)+scpLang_GetPhrase("ingame.finefirstaiderr")+Chr(34)
                            MsgTimer = 70*5
                            Return
                            SelectedItem = Null
					    EndIf
					EndIf
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If Bloodloss = 0 And Injuries = 0 Then
						Msg = scpLang_GetPhrase("ingame.aidnotnow")
						MsgTimer = 70*5
						SelectedItem = Null
					Else
						CurrSpeed = CurveValue(0, CurrSpeed, 5.0)	
						
						DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
							
						width% = 300
						height% = 20
						x% = GraphicWidth / 2 - width / 2
						y% = GraphicHeight / 2 + 80
						Rect(x, y, width+4, height, False)
						For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
							DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
						Next
							
						SelectedItem\state = Min(SelectedItem\state+(fs\FPSfactor[0]/5.0),100)	
						
						If Crouch = False Then CrouchCHN = PlaySound_Strict(CrouchSFX) 	
						
						Crouch = True
						
						If SelectedItem\state = 100 Then
							If SelectedItem\itemtemplate\tempname = "finefirstaid" Then
								Bloodloss = 0
								I_1079\Foam = 0
								Injuries = Max(0, Injuries - 2.0)
								If Injuries = 0 Then
									Msg = scpLang_GetPhrase("ingame.aidpainfine")
								ElseIf Injuries > 1.0
									Msg = scpLang_GetPhrase("ingame.aidpainbleed")
								Else
									Msg = scpLang_GetPhrase("ingame.aidpainsore")
								EndIf
								MsgTimer = 70*5
								RemoveItem(SelectedItem)
							Else
								Bloodloss = Max(0, Bloodloss - Rand(10,20))
								If Injuries >= 2.5 Then
									Msg = scpLang_GetPhrase("ingame.aidbleedsev")
									Injuries = Max(2.5, Injuries-Rnd(0.3,0.7))
								ElseIf Injuries > 1.0
									Injuries = Max(0.5, Injuries-Rnd(0.5,1.0))
									If Injuries > 1.0 Then
										Msg = scpLang_GetPhrase("ingame.aidbleedun")
									Else
										Msg = scpLang_GetPhrase("ingame.aidbleedstop")
									EndIf
								Else
									If Injuries > 0.5 Then
										Injuries = 0.5
										Msg = scpLang_GetPhrase("ingame.painease")
									Else
										Injuries = 0.5
										Msg = scpLang_GetPhrase("ingame.painhurt")
									EndIf
								EndIf
									
								If SelectedItem\itemtemplate\tempname = "firstaid2" Then 
									Select Rand(6)
										Case 1
											SuperMan = True
											Msg = scpLang_GetPhrase("ingame.adrenaline")
										Case 2
											InvertMouse = (Not InvertMouse)
											Msg = scpLang_GetPhrase("ingame.headdif")
										Case 3
											BlurTimer = 5000
											Msg = scpLang_GetPhrase("ingame.bleednaus")
										Case 4
											BlinkEffect = 0.6
											BlinkEffectTimer = Rand(20,30)
										Case 5
											Bloodloss = 0
											Injuries = 0
											Msg = scpLang_GetPhrase("ingame.bandfine")
										Case 6
											Msg = scpLang_GetPhrase("ingame.bandheavy")
											Injuries = 3.5
									End Select
								EndIf
									
								MsgTimer = 70*5
								RemoveItem(SelectedItem)
							EndIf						
						EndIf
					EndIf
					;[End Block]
				Case "eyedrops", "eyedrops2"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.dropsgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_215\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops215")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_178\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops178")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.dropsnvg")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
				        If (Not I_402\Using)
				            Msg = scpLang_GetPhrase("ingame.dropsgood")
				            MsgTimer = 70*5
					        BlinkEffect = 0.6
							BlinkEffectTimer = Rand(20, 30)
							BlurTimer = 200
						    RemoveItem(SelectedItem)
						Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            SelectedItem = Null
                            Return
						EndIf
					EndIf
					;[End Block]
				Case "fineeyedrops"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.dropsgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_215\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops215")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_178\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops178")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.dropsnvg")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
					    If (Not I_402\Using)
					        Msg = scpLang_GetPhrase("ingame.dropsgood2")
					        MsgTimer = 70*5
							BlinkEffect = 0.4
							BlinkEffectTimer = Rand(30, 40)
							Bloodloss = Max(Bloodloss-1.0, 0)
							BlurTimer = 200
						    RemoveItem(SelectedItem)
						Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            SelectedItem = Null
                            Return
						EndIf
					EndIf
					;[End Block]
				Case "supereyedrops"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.dropsgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_215\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops215")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_178\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops178")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.dropsnvg")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
					    If (Not I_402\Using)
					        Msg = scpLang_GetPhrase("ingame.dropsgood2")
					        MsgTimer = 70*5
							BlinkEffect = 0.0
							BlinkEffectTimer = 60
							EyeStuck = 10000
						    BlurTimer = 1000
						    RemoveItem(SelectedItem)
						Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            Return
                            SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "paper", "ticket"
					;[Block]
					If SelectedItem\itemtemplate\img = 0 Then
						Select SelectedItem\itemtemplate\name
							Case "Burnt Note" 
								SelectedItem\itemtemplate\img = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\items\"+"bn.it"))
								SetBuffer ImageBuffer(SelectedItem\itemtemplate\img)
								Color 0,0,0
								AAText 277, 469, AccessCode, True, True
								Color 255,255,255
								SetBuffer BackBuffer()
							Case "Document SCP-372"
								SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
								
								SetBuffer ImageBuffer(SelectedItem\itemtemplate\img)
								Color 37,45,137
								AASetFont fo\Font[4]
								temp = ((Int(AccessCode)*3) Mod 10000)
								If temp < 1000 Then temp = temp+1000
								AAText 383*MenuScale, 734*MenuScale, temp, True, True
								Color 255,255,255
								SetBuffer BackBuffer()
							Case "Movie Ticket"
								;don't resize because it messes up the masking
								SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								
								If (SelectedItem\state = 0) Then
									Msg = scpLang_GetPhrase("ingame.ticket")
									MsgTimer = 70*10
									PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg"))
									SelectedItem\state = 1
								EndIf
							Case "Document SCP-XXX"
							    SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
								If (SelectedItem\state = 0) Then
									Msg = scpLang_GetPhrase("ingame.strangedoc")
									MsgTimer = 70*10
									PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg"))
									SelectedItem\state = 1
								EndIf
							Default 
								SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						End Select
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					;[End Block]
				Case "scp1025"
					;[Block]
					GiveAchievement(Achv1025) 
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\state = Rand(0,5)
						SelectedItem\itemtemplate\img=LoadImage_Strict(scpModding_ProcessFilePath$("GFX\items\"+"1025\1025_"+Int(SelectedItem\state)+".png"))	
						SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					If (Not I_714\Using) And (Not WearingGasMask = 3) And (Not WearingHazmat = 3) Then SCP1025state[SelectedItem\state]=Max(1,SCP1025state[SelectedItem\state])
					
					DrawImage(SelectedItem\itemtemplate\img, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					;[End Block]
				Case "cup"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.cup1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.cupgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
					    If (Not I_402\Using)
						    SelectedItem\name = Trim(Lower(SelectedItem\name))
						    If Left(SelectedItem\name, Min(6,Len(SelectedItem\name))) = "cup of" Then
							    SelectedItem\name = Right(SelectedItem\name, Len(SelectedItem\name)-7)
						    ElseIf Left(SelectedItem\name, Min(8,Len(SelectedItem\name))) = "a cup of" 
							    SelectedItem\name = Right(SelectedItem\name, Len(SelectedItem\name)-9)
						    EndIf
						
						    ;the state of refined items is more than 1.0 (fine setting increases it by 1, very fine doubles it)
						    x2 = (SelectedItem\state+1.0)
						
						    Local iniStr$ = scpModding_ProcessFilePath$("Data\SCP-294.ini")
						
						    Local loc% = GetINISectionLocation(iniStr, SelectedItem\name)
						
						    ;Stop
						
						    strtemp = GetINIString2(iniStr, loc, "message")
						    If strtemp <> "" Then Msg = scpLang_GetPhrase(strtemp) : MsgTimer = 70*6
						    
						    If GetINIInt2(iniStr, loc, "lethal") Or GetINIInt2(iniStr, loc, "deathtimer") Then 
							    DeathMSG = GetINIString2(iniStr, loc, "deathmessage")
							    If GetINIInt2(iniStr, loc, "lethal") Then Kill()
						    EndIf
						    BlurTimer = GetINIInt2(iniStr, loc, "blur")*70;*temp
						    If VomitTimer = 0 Then VomitTimer = GetINIInt2(iniStr, loc, "vomit")
						    CameraShakeTimer = GetINIString2(iniStr, loc, "camerashake")
						    Injuries = Max(Injuries + GetINIInt2(iniStr, loc, "damage"),0);*temp
						    Bloodloss = Max(Bloodloss + GetINIInt2(iniStr, loc, "blood loss"),0);*temp
						
						    ;{~--<MOD>--~}
						
						    I_1079\Foam = Max(I_1079\Foam + GetINIInt2(iniStr, loc, "bubble foam"),0);*temp
						
                            ;{~--<END>--~}

						    strtemp =  GetINIString2(iniStr, loc, "sound")
						    If strtemp<>"" Then
							    PlaySound_Strict LoadTempSound(strtemp)
						    EndIf
						    If GetINIInt2(iniStr, loc, "stomachache") Then SCP1025state[3]=1
						
						    If GetINIInt2(iniStr, loc, "infection") Then I_008\Timer = 1
						
						    ;{~--<MOD>--~}
						
						    If GetINIInt2(iniStr, loc, "crystallization") Then I_409\Timer = 1
						    If GetINIInt2(iniStr, loc, "cola") Then I_207\Timer = 1
						
						    ;{~--<END>--~}
						
						    DeathTimer = GetINIInt2(iniStr, loc, "deathtimer")*70
						
						    BlinkEffect = Float(GetINIString2(iniStr, loc, "blink effect", 1.0))*x2
						    BlinkEffectTimer = Float(GetINIString2(iniStr, loc, "blink effect timer", 1.0))*x2
						
						    StaminaEffect = Float(GetINIString2(iniStr, loc, "stamina effect", 1.0))*x2
						    StaminaEffectTimer = Float(GetINIString2(iniStr, loc, "stamina effect timer", 1.0))*x2
						
						    strtemp = GetINIString2(iniStr, loc, "refusemessage")
						    If strtemp <> "" Then
							    Msg = strtemp 
							    MsgTimer = 70*6		
						    Else
							    it.Items = CreateItem("Empty Cup", "emptycup", 0,0,0)
							    it\Picked = True
							    For i = 0 To MaxItemAmount-1
								    If Inventory(i)=SelectedItem Then Inventory(i) = it : Exit
							    Next					
							    EntityType (it\collider, HIT_ITEM)
							
							    RemoveItem(SelectedItem)
							EndIf						
						    SelectedItem = Null
						Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            SelectedItem = Null
                            Return
						EndIf
					EndIf
					;[End Block]
				Case "syringe"
					;[Block]
					If (Not I_402\Using)
						HealTimer = 30
						StaminaEffect = 0.5
						StaminaEffectTimer = 20
						
						Msg = scpLang_GetPhrase("ingame.syringe1")
						MsgTimer = 70 * 8
						
						RemoveItem(SelectedItem)
					Else
                        Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                        MsgTimer = 70*5
                        Return
                        SelectedItem = Null
					EndIf
					;[End Block]
				Case "finesyringe"
					;[Block]
					If (Not I_402\Using)
						HealTimer = Rnd(20, 40)
						StaminaEffect = Rnd(0.5, 0.8)
						StaminaEffectTimer = Rnd(20, 30)
						
						Msg = scpLang_GetPhrase("ingame.syringe2")
						MsgTimer = 70 * 8
						
						RemoveItem(SelectedItem)
					Else
                        Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                        MsgTimer = 70*5
                        SelectedItem = Null
                        Return
					EndIf
					;[End Block]
				Case "veryfinesyringe"
					;[Block]
					If (Not I_402\Using)
						Select Rand(3)
							Case 1
								HealTimer = Rnd(40, 60)
								StaminaEffect = 0.1
								StaminaEffectTimer = 30
								Msg = scpLang_GetPhrase("ingame.syringe3")
							Case 2
								SuperMan = True
								Msg = scpLang_GetPhrase("ingame.syringe4")
							Case 3
								VomitTimer = 30
								Msg = scpLang_GetPhrase("ingame.syringe5")
						End Select
						
						MsgTimer = 70 * 8
						RemoveItem(SelectedItem)
					Else
                        Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                        MsgTimer = 70*5
                        SelectedItem = Null
                        Return
					EndIf
					;[End Block]
				Case "radio","18vradio","fineradio","veryfineradio"
					;[Block]
					If SelectedItem\state <= 100 Then SelectedItem\state = Max(0, SelectedItem\state - fs\FPSfactor[0] * 0.004)
					
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					;RadioState(5) = has the "use the number keys" -message been shown yet (true/false)
					;RadioState(6) = a timer for the "code channel"
					;RadioState(7) = another timer for the "code channel"
					
					If RadioState(5) = 0 Then 
						Msg = scpLang_GetPhrase("ingame.radiokeys")
						MsgTimer = 70 * 5
						RadioState(5) = 1
						RadioState(0) = -1
					EndIf
					
					strtemp$ = ""
					
					x = GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img) ;+ 120
					y = GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img) ;- 30
					
					DrawImage(SelectedItem\itemtemplate\img, x, y)
					
					If SelectedItem\state > 0 Then
						If PlayerRoom\RoomTemplate\Name = "pocketdimension"Then
							ResumeChannel(RadioCHN(5))
							If ChannelPlaying(RadioCHN(5)) = False Then RadioCHN(5) = PlaySound_Strict(RadioStatic)	
						ElseIf CoffinDistance < 4.0
						    ResumeChannel(RadioCHN(5))
							If ChannelPlaying(RadioCHN(5)) = False Then RadioCHN(5) = PlaySound_Strict(RadioStatic895)	
						Else
							Select Int(SelectedItem\state2)
								Case 0 ;randomkanava
									ResumeChannel(RadioCHN(0))
									strtemp = "        " + scpLang_GetPhrase("ingame.radiouser") + " - "
									If (Not EnableUserTracks)
										If ChannelPlaying(RadioCHN(0)) = False Then RadioCHN(0) = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + scpLang_GetPhrase("ingame.radionot") + "     "
									ElseIf UserTrackMusicAmount<1
										If ChannelPlaying(RadioCHN(0)) = False Then RadioCHN(0) = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + scpLang_GetPhrase("ingame.radionone") + "     "
									Else
										If (Not ChannelPlaying(RadioCHN(0)))
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState(0)<(UserTrackMusicAmount-1)
														RadioState(0) = RadioState(0) + 1
													Else
														RadioState(0) = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState(0) = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks"+UserTrackName$(RadioState(0)))
											RadioCHN(0) = PlaySound_Strict(CurrUserTrack%)
										Else
											strtemp = strtemp + Upper(UserTrackName$(RadioState(0))) + "          "
											UserTrackFlag = False
										EndIf
										
										If KeyHit(2) Then
											PlaySound_Strict RadioSquelch
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState(0)<(UserTrackMusicAmount-1)
														RadioState(0) = RadioState(0) + 1
													Else
														RadioState(0) = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState(0) = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks"+UserTrackName$(RadioState(0)))
											RadioCHN(0) = PlaySound_Strict(CurrUserTrack%)
										EndIf
									EndIf
								Case 1 ;h?�lytyskanava
									
									ResumeChannel(RadioCHN(1))
									strtemp = "        " + scpLang_GetPhrase("ingame.radiocon") + "          "
									If ChannelPlaying(RadioCHN(1)) = False Then
										
										If RadioState(1) => 5 Then
											RadioCHN(1) = PlaySound_Strict(RadioSFX(1,1))	
											RadioState(1) = 0
										Else
											RadioState(1)=RadioState(1)+1	
											RadioCHN(1) = PlaySound_Strict(RadioSFX(1,0))	
										EndIf
										
									EndIf
									
								Case 2 ;scp-radio
									ResumeChannel(RadioCHN(2))
									strtemp = "        " + scpLang_GetPhrase("ingame.radiosite") + "          "
									If ChannelPlaying(RadioCHN(2)) = False Then
										RadioState(2)=RadioState(2)+1
										If RadioState(2) = 17 Then RadioState(2) = 1
										If Floor(RadioState(2)/2)=Ceil(RadioState(2)/2) Then ;parillinen, soitetaan normiviesti
											RadioCHN(2) = PlaySound_Strict(RadioSFX(2,Int(RadioState(2)/2)))	
										Else ;pariton, soitetaan musiikkia
											RadioCHN(2) = PlaySound_Strict(RadioSFX(2,0))
										EndIf
									EndIf 
								Case 3
									ResumeChannel(RadioCHN(3))
									strtemp = "             " + scpLang_GetPhrase("ingame.radioem") + "         "
									If ChannelPlaying(RadioCHN(3)) = False Then RadioCHN(3) = PlaySound_Strict(RadioStatic)
									
									If MTFtimer > 0 Then 
										RadioState(3)=RadioState(3)+Max(Rand(-10,1),0)
										Select RadioState(3)
											Case 40
												If Not RadioState3(0) Then
													RadioCHN(3) = PlaySound_Strict(scpModding_ProcessFilePath$(LoadTempSound("SFX\"+"Character\MTF\Random1.ogg")))
													RadioState(3) = RadioState(3)+1	
													RadioState3(0) = True	
												EndIf											
											Case 400
												If Not RadioState3(1) Then
													RadioCHN(3) = PlaySound_Strict(scpModding_ProcessFilePath$(LoadTempSound("SFX\"+"Character\MTF\Random2.ogg")))
													RadioState(3) = RadioState(3)+1	
													RadioState3(1) = True	
												EndIf	
											Case 800
												If Not RadioState3(2) Then
													RadioCHN(3) = PlaySound_Strict(scpModding_ProcessFilePath$(LoadTempSound("SFX\"+"Character\MTF\Random3.ogg")))
													RadioState(3) = RadioState(3)+1	
													RadioState3(2) = True
												EndIf													
											Case 1200
												If Not RadioState3(3) Then
													RadioCHN(3) = PlaySound_Strict(scpModding_ProcessFilePath$(LoadTempSound("SFX\"+"Character\MTF\Random4.ogg")))	
													RadioState(3) = RadioState(3)+1	
													RadioState3(3) = True
												EndIf
											Case 1600
												If Not RadioState3(4) Then
													RadioCHN(3) = PlaySound_Strict(scpModding_ProcessFilePath$(LoadTempSound("SFX\"+"Character\MTF\Random5.ogg")))	
													RadioState(3) = RadioState(3)+1
													RadioState3(4) = True
												EndIf
											Case 2000
												If Not RadioState3(5) Then
													RadioCHN(3) = PlaySound_Strict(scpModding_ProcessFilePath$(LoadTempSound("SFX\"+"Character\MTF\Random6.ogg")))	
													RadioState(3) = RadioState(3)+1
													RadioState3(5) = True
												EndIf
											Case 2400
												If Not RadioState3(6) Then
													RadioCHN(3) = PlaySound_Strict(scpModding_ProcessFilePath$(LoadTempSound("SFX\"+"Character\MTF\Random7.ogg")))	
													RadioState(3) = RadioState(3)+1
													RadioState3(6) = True
												EndIf
										End Select
									EndIf
								Case 4
									ResumeChannel(RadioCHN(6)) ;taustalle kohinaa
									If ChannelPlaying(RadioCHN(6)) = False Then RadioCHN(6) = PlaySound_Strict(RadioStatic)									
									
									ResumeChannel(RadioCHN(4))
									If ChannelPlaying(RadioCHN(4)) = False Then 
										If RemoteDoorOn = False And RadioState(8) = False Then
											RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Radio\Chatter3.ogg")))	
											RadioState(8) = True
										Else
											RadioState(4)=RadioState(4)+Max(Rand(-10,1),0)
											
											Select RadioState(4)
												Case 10
													If (Not Contained106)
														If Not RadioState4(0) Then
															RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Radio\OhGod.ogg")))
															RadioState(4) = RadioState(4)+1
															RadioState4(0) = True
														EndIf
													EndIf
												Case 100
													If Not RadioState4(1) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Radio\Chatter2.ogg")))
														RadioState(4) = RadioState(4)+1
														RadioState4(1) = True
													EndIf		
												Case 158
													If MTFtimer = 0 And (Not RadioState4(2)) Then 
														RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Radio\franklin1.ogg")))
														RadioState(4) = RadioState(4)+1
														RadioState(2) = True
													EndIf
												Case 200
													If Not RadioState4(3) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Radio\Chatter4.ogg")))
														RadioState(4) = RadioState(4)+1
														RadioState4(3) = True
													EndIf		
												Case 260
													If Not RadioState4(4) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\035\RadioHelp1.ogg")))
														RadioState(4) = RadioState(4)+1
														RadioState4(4) = True
													EndIf		
												Case 300
													If Not RadioState4(5) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Radio\Chatter1.ogg")))	
														RadioState(4) = RadioState(4)+1	
														RadioState4(5) = True
													EndIf		
												Case 350
													If Not RadioState4(6) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Radio\franklin2.ogg")))
														RadioState(4) = RadioState(4)+1
														RadioState4(6) = True
													EndIf		
												Case 400
													If Not RadioState4(7) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\035\RadioHelp2.ogg")))
														RadioState(4) = RadioState(4)+1
														RadioState4(7) = True
													EndIf		
												Case 450
													If Not RadioState4(8) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Radio\franklin3.ogg")))	
														RadioState(4) = RadioState(4)+1		
														RadioState4(8) = True
													EndIf		
												Case 600
													If Not RadioState4(9) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Radio\franklin4.ogg")))	
														RadioState(4) = RadioState(4)+1	
														RadioState4(9) = True
													EndIf		
											End Select
										EndIf
									EndIf
									
									
								Case 5
									ResumeChannel(RadioCHN(5))
									If ChannelPlaying(RadioCHN(5)) = False Then RadioCHN(5) = PlaySound_Strict(RadioStatic)
							End Select 
							
							x=x+66
							y=y+419
							
							Color (30,30,30)
							
							If SelectedItem\state <= 100 Then
								;Text (x - 60, y - 20, "BATTERY")
								For i = 0 To 4
									Rect(x, y+8*i, 43 - i * 6, 4, Ceil(SelectedItem\state / 20.0) > 4 - i )
								Next
							EndIf	
							
							AASetFont fo\Font[2]
							AAText(x+50, y, "CHN")						
							
							If SelectedItem\itemtemplate\tempname = "veryfineradio" Then ;"KOODIKANAVA"
								ResumeChannel(RadioCHN(0))
								If ChannelPlaying(RadioCHN(0)) = False Then RadioCHN(0) = PlaySound_Strict(RadioStatic)
								
								;RadioState(7)=kuinka mones piippaus menossa
								;RadioState(8)=kuinka mones access coden numero menossa
								RadioState(6)=RadioState(6) + fs\FPSfactor[0]
								temp = Mid(Str(AccessCode),RadioState(8)+1,1)
								If RadioState(6)-fs\FPSfactor[0] =< RadioState(7)*50 And RadioState(6)>RadioState(7)*50 Then
									PlaySound_Strict(RadioBuzz)
									RadioState(7)=RadioState(7)+1
									If RadioState(7)=>temp Then
										RadioState(7)=0
										RadioState(6)=-100
										RadioState(8)=RadioState(8)+1
										If RadioState(8)=4 Then RadioState(8)=0 : RadioState(6)=-200
									EndIf
								EndIf
								
								strtemp = ""
								For i = 0 To Rand(5, 30)
									strtemp = strtemp + Chr(Rand(1,100))
								Next
								
								AASetFont fo\Font[3]
								AAText(x+97, y+16, Rand(0,9),True,True)
								
							Else
								For i = 2 To 6
									If KeyHit(i) Then
										If SelectedItem\state2 <> i-2 Then ;pausetetaan nykyinen radiokanava
											PlaySound_Strict RadioSquelch
											If RadioCHN(Int(SelectedItem\state2)) <> 0 Then PauseChannel(RadioCHN(Int(SelectedItem\state2)))
										EndIf
										SelectedItem\state2 = i-2
										;jos nykyist?� kanavaa ollaan soitettu, laitetaan jatketaan toistoa samasta kohdasta
										If RadioCHN(SelectedItem\state2)<>0 Then ResumeChannel(RadioCHN(SelectedItem\state2))
									EndIf
								Next
								
								AASetFont fo\Font[3]
								AAText(x+97, y+16, Int(SelectedItem\state2+1),True,True)
							EndIf
							
							AASetFont fo\Font[2]
							If strtemp <> "" Then
								strtemp = Right(Left(strtemp, (Int(MilliSecs2()/300) Mod Len(strtemp))),10)
								AAText(x+32, y+33, strtemp)
							EndIf
							
							AASetFont fo\Font[0]
							
						EndIf
						
					EndIf
					;[End Block]

				Case "cigarette"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.cig1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.ciggas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
						Select Rand(6)
							Case 1
								Msg = scpLang_GetPhrase("ingame.cig1")
							Case 2
								Msg = scpLang_GetPhrase("ingame.cig2")
							Case 3
								Msg = scpLang_GetPhrase("ingame.cig3")
								RemoveItem(SelectedItem)
							Case 4
								Msg = scpLang_GetPhrase("ingame.cig4")
							Case 5
								Msg = scpLang_GetPhrase("ingame.cig5")
							Case 6
								Msg = scpLang_GetPhrase("ingame.cig6")
								RemoveItem(SelectedItem)
						End Select
						MsgTimer = 70 * 5
					EndIf
					;[End Block]
				Case "scp420j"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.weed1")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.weed2")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
						If I_714\Using = 1 Or WearingHazmat = 3 Or WearingGasMask = 3 Then
							Msg = Chr(34) + scpLang_GetPhrase("ingame.weed3") + Chr(34)
						Else
							Msg = Chr(34) + scpLang_GetPhrase("ingame.weed4") + Chr(34)
							Injuries = Max(Injuries-0.5, 0)
							BlurTimer = 500
							GiveAchievement(Achv420)
							PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"Music\" + "Using420J.ogg"))
						EndIf
						MsgTimer = 70 * 5
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp420s"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.weed5")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.weed6")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
						If I_714\Using=1 Or WearingHazmat = 3 Or WearingGasMask = 3 Then
							Msg = Chr(34) + scpLang_GetPhrase("ingame.weed3") + Chr(34)
						Else
							DeathMSG = scpLang_GetPhrase("ingame.weed7")
							Msg = Chr(34) + scpLang_GetPhrase("ingame.weed8") + Chr(34)
							KillTimer = -1						
						EndIf
						MsgTimer = 70 * 6
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp714"
					;[Block]
					If I_714\Using=1 Then
						Msg = scpLang_GetPhrase("ingame.ringoff")
						I_714\Using = False
					Else
						GiveAchievement(Achv714)
						Msg = scpLang_GetPhrase("ingame.ringon")
						I_714\Using = True
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null	
					;[End Block]
				Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
					;[Block]
				    CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
						
					DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
						
					width% = 300
					height% = 20
					x% = GraphicWidth / 2 - width / 2
					y% = GraphicHeight / 2 + 80
					Rect(x, y, width+4, height, False)
					For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
						DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					Next
						
					SelectedItem\state = Min(SelectedItem\state+(fs\FPSfactor[0]/4.0),100)
						
					If SelectedItem\state=100 Then
						If WearingHazmat>0 Then
							Msg = scpLang_GetPhrase("ingame.hazmatoff")
							WearingHazmat = False
							DropItem(SelectedItem)
						Else
							If SelectedItem\itemtemplate\tempname="hazmatsuit" Then
								WearingHazmat = 1
							ElseIf SelectedItem\itemtemplate\tempname="hazmatsuit2" Then
								WearingHazmat = 2
							Else
								WearingHazmat = 3
							EndIf
							If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
							Msg = scpLang_GetPhrase("ingame.hazmaton")
							EndIf
						SelectedItem\state=0
						MsgTimer = 70 * 5
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "vest","finevest"
					;[Block]
					CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
					
					DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					
					width% = 300
					height% = 20
					x% = GraphicWidth / 2 - width / 2
					y% = GraphicHeight / 2 + 80
					Rect(x, y, width+4, height, False)
					For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
						DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					Next
					
					SelectedItem\state = Min(SelectedItem\state+(fs\FPSfactor[0]/(2.0+(0.5*(SelectedItem\itemtemplate\tempname="finevest")))),100)
					
					If SelectedItem\state=100 Then
						If WearingVest>0 Then
							Msg = scpLang_GetPhrase("ingame.vestoff")
							WearingVest = False
							DropItem(SelectedItem)
						Else
							If SelectedItem\itemtemplate\tempname="vest" Then
								Msg = scpLang_GetPhrase("ingame.veston1")
								WearingVest = 1
							Else
								Msg = scpLang_GetPhrase("ingame.veston2")
								WearingVest = 2
							EndIf
							If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
						EndIf
						SelectedItem\state=0
						MsgTimer = 70 * 5
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "gasmask", "supergasmask", "gasmask3"
					;[Block]
                    For i = 0 To MaxItemAmount - 1
                        If Inventory(i) <> Null Then
                            If Inventory(i)\itemtemplate\name = "SCP-215" And I_215\Timer >= 86.0 Then
                                Msg = scpLang_GetPhrase("ingame.gasmaskerr1")
						        MsgTimer = 70 * 5
						        SelectedItem = Null
						        Return
                                Exit
                            EndIf
                        EndIf
                    Next

					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.gasmaskerr2")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_178\Using > 0 Then
					    Msg = scpLang_GetPhrase("ingame.gasmaskerr3")
                        MsgTimer = 70 * 5
                        SelectedItem = Null
					    Return
					ElseIf I_215\Using > 0 Then
					    Msg = scpLang_GetPhrase("ingame.gasmaskerr4")
                        MsgTimer = 70 * 5
                        SelectedItem = Null
					    Return
					ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.gasmaskerr5")
                        MsgTimer = 70 * 5
                        SelectedItem = Null
					    Return
                    ElseIf WearingHelmet > 0
                        Msg = scpLang_GetPhrase("ingame.gasmaskerr6")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
                    Else		
					    CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
					
					    DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					
					    width% = 300
					    height% = 20
					    x% = GraphicWidth / 2 - width / 2
					    y% = GraphicHeight / 2 + 80
					    Rect(x, y, width + 4, height, False)
					    For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
					    	DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					    Next
					
					    SelectedItem\state = Min(SelectedItem\state+(fs\FPSfactor[0]),100)
					
					    If SelectedItem\state = 100 Then
                            If WearingGasMask > 0 Then
                                WearingGasMask = False
                                Msg = scpLang_GetPhrase("ingame.gasmaskoff")
                                If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
                            Else
						        If SelectedItem\itemtemplate\tempname = "supergasmask"
							        Msg = scpLang_GetPhrase("ingame.gasmaskon1")
							        WearingGasMask = 2
						        ElseIf SelectedItem\itemtemplate\tempname = "gasmask3"
							        Msg = scpLang_GetPhrase("ingame.gasmaskon2")
							        WearingGasMask = 3
						        ElseIf SelectedItem\itemtemplate\tempname = "gasmask"
							        Msg = scpLang_GetPhrase("ingame.gasmaskon2")
							        WearingGasMask = 1
						        EndIf
					            If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
                            EndIf
						    SelectedItem\state = 0
						    MsgTimer = 70 * 5
						    SelectedItem = Null
					    EndIf
					EndIf
					;[End Block]
				Case "navigator", "nav"
					;[Block]
					
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					If SelectedItem\state <= 100 Then SelectedItem\state = Max(0, SelectedItem\state - fs\FPSfactor[0] * 0.005)
					
					x = GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img)*0.5+20
					y = GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img)*0.4-85
					width = 287
					height = 256
					
					Local PlayerX,PlayerZ
					
					DrawImage(SelectedItem\itemtemplate\img, x - ImageWidth(SelectedItem\itemtemplate\img) / 2, y - ImageHeight(SelectedItem\itemtemplate\img) / 2 + 85)
					
					AASetFont fo\Font[2]
					
					Local NavWorks% = True
					If PlayerRoom\RoomTemplate\Name$ = "pocketdimension" Or PlayerRoom\RoomTemplate\Name$ = "dimension1499" Then
						NavWorks% = False
					ElseIf PlayerRoom\RoomTemplate\Name$ = "room860" Then
						For e.Events = Each Events
							If e\EventName = "room860" Then
								If e\EventState = 1.0 Then
									NavWorks% = False
								EndIf
								Exit
							EndIf
						Next
					EndIf
					
					If (Not NavWorks) Then
						If (MilliSecs2() Mod 1000) > 300 Then
							Color(200, 0, 0)
							AAText(x, y + height / 2 - 80, scpLang_GetPhrase("ingame.naverr1"), True)
							AAText(x, y + height / 2 - 60, scpLang_GetPhrase("ingame.naverr2"), True)						
						EndIf
					Else
						
						If SelectedItem\state > 0 And (Rnd(CoffinDistance + 15.0) > 1.0 Or PlayerRoom\RoomTemplate\Name <> "room895") Then
							
							PlayerX% = Floor((EntityX(PlayerRoom\obj)+8) / 8.0 + 0.5)
							PlayerZ% = Floor((EntityZ(PlayerRoom\obj)+8) / 8.0 + 0.5)
							
							SetBuffer ImageBuffer(NavBG)
							Local xx = x-ImageWidth(SelectedItem\itemtemplate\img)/2
							Local yy = y-ImageHeight(SelectedItem\itemtemplate\img)/2+85
							DrawImage(SelectedItem\itemtemplate\img, xx, yy)
									    
							x = x - 12 + (((EntityX(Collider)-4.0)+8.0) Mod 8.0)*3
							y = y + 12 - (((EntityZ(Collider)-4.0)+8.0) Mod 8.0)*3
							For x2 = Max(0, PlayerX - 6) To Min(MapWidth, PlayerX + 6)
								For z2 = Max(0, PlayerZ - 6) To Min(MapHeight, PlayerZ + 6)
									
									If CoffinDistance > 16.0 Or Rnd(16.0)<CoffinDistance Then 
										If MapTemp(x2, z2)>0 And (MapFound(x2, z2) > 0 Or SelectedItem\itemtemplate\name = "S-NAV 310 Navigator" Or SelectedItem\itemtemplate\name = "S-NAV Navigator Ultimate") Then
											Local drawx% = x + (PlayerX - 1 - x2) * 24 , drawy% = y - (PlayerZ - 1 - z2) * 24
											
											If x2+1<=MapWidth Then
												If MapTemp(x2+1,z2)=False
													DrawImage NavImages(3),drawx-12,drawy-12
												EndIf
											Else
												DrawImage NavImages(3),drawx-12,drawy-12
											EndIf
											If x2-1>=0 Then
												If MapTemp(x2-1,z2)=False
													DrawImage NavImages(1),drawx-12,drawy-12
												EndIf
											Else
												DrawImage NavImages(1),drawx-12,drawy-12
											EndIf
											If z2-1>=0 Then
												If MapTemp(x2,z2-1)=False
													DrawImage NavImages(0),drawx-12,drawy-12
												EndIf
											Else
												DrawImage NavImages(0),drawx-12,drawy-12
											EndIf
											If z2+1<=MapHeight Then
												If MapTemp(x2,z2+1)=False
													DrawImage NavImages(2),drawx-12,drawy-12
												EndIf
											Else
												DrawImage NavImages(2),drawx-12,drawy-12
											EndIf
										EndIf
									EndIf
									
								Next
							Next
							
							SetBuffer BackBuffer()
							DrawImageRect NavBG,xx+80,yy+70,xx+80,yy+70,270,230
							Color 30,30,30
							If SelectedItem\itemtemplate\name = "S-NAV Navigator" Then Color(100, 0, 0)
							Rect xx+80,yy+70,270,230,False
							
							x = GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img)*0.5+20
							y = GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img)*0.4-85
							
							If SelectedItem\itemtemplate\name = "S-NAV Navigator" Then 
								Color(100, 0, 0)
							Else
								Color (30,30,30)
							EndIf
							If (MilliSecs2() Mod 1000) > 300 Then
								If SelectedItem\itemtemplate\name <> "S-NAV 310 Navigator" And SelectedItem\itemtemplate\name <> "S-NAV Navigator Ultimate" Then
									AAText(x - width/2 + 10, y - height/2 + 10, scpLang_GetPhrase("ingame.naverr3"))
								EndIf
																
								yawvalue = EntityYaw(Collider)-90
								x1 = x+Cos(yawvalue)*6 : y1 = y-Sin(yawvalue)*6
								x2 = x+Cos(yawvalue-140)*5 : y2 = y-Sin(yawvalue-140)*5				
								x3 = x+Cos(yawvalue+140)*5 : y3 = y-Sin(yawvalue+140)*5
								
								Line x1,y1,x2,y2
								Line x1,y1,x3,y3
								Line x2,y2,x3,y3
							EndIf
							
							Local SCPs_found% = 0
							If SelectedItem\itemtemplate\name = "S-NAV Navigator Ultimate" And (MilliSecs2() Mod 600) < 400 Then
								If Curr173<>Null Then
									Local dist# = EntityDistance(Camera, Curr173\obj)
									dist = Ceil(dist / 8.0) * 8.0
									If dist < 8.0 * 4 Then
										Color 100, 0, 0
										Oval(x - dist * 3, y - 7 - dist * 3, dist * 3 * 2, dist * 3 * 2, False)
										AAText(x - width / 2 + 10, y - height / 2 + 30, "SCP-173")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								If Curr106<>Null Then
									dist# = EntityDistance(Camera, Curr106\obj)
									If dist < 8.0 * 4 Then
										Color 100, 0, 0
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										AAText(x - width / 2 + 10, y - height / 2 + 30 + (20 * SCPs_found), "SCP-106")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								If Curr096<>Null Then 
									dist# = EntityDistance(Camera, Curr096\obj)
									If dist < 8.0 * 4 Then
										Color 100, 0, 0
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										AAText(x - width / 2 + 10, y - height / 2 + 30 + (20 * SCPs_found), "SCP-096")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								If Curr650<>Null Then 
									dist# = EntityDistance(Camera, Curr650\obj)
									If dist < 8.0 * 4 Then
										Color 100, 0, 0
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										AAText(x - width / 2 + 10, y - height / 2 + 30 + (20 * SCPs_found), "SCP-650")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								If Curr066<>Null Then 
									dist# = EntityDistance(Camera, Curr066\obj)
									If dist < 8.0 * 4 Then
										Color 100, 0, 0
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										AAText(x - width / 2 + 10, y - height / 2 + 30 + (20 * SCPs_found), "SCP-066")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								For np.NPCs = Each NPCs
									If np\NPCtype = NPCtype049
										dist# = EntityDistance(Camera, np\obj)
										If dist < 8.0 * 4 Then
											If (Not np\HideFromNVG) Then
												Color 100, 0, 0
												Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
												AAText(x - width / 2 + 10, y - height / 2 + 30 + (20 * SCPs_found), "SCP-049")
												SCPs_found% = SCPs_found% + 1
											EndIf
										EndIf
										Exit
									EndIf						
								Next
								If PlayerRoom\RoomTemplate\Name = "room895" Then
									If CoffinDistance < 8.0 Then
										dist = Rnd(4.0, 8.0)
										Color 100, 0, 0
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										AAText(x - width / 2 + 10, y - height / 2 + 30 + (20 * SCPs_found), "SCP-895")
									EndIf
								EndIf
							End If
							
							Color (30, 30, 30)
							If SelectedItem\itemtemplate\name = "S-NAV Navigator" Then Color(100, 0, 0)
							If SelectedItem\state =< 100 Then
								xtemp = x - width / 2 + 196
								ytemp = y - height / 2 + 10
								Rect xtemp,ytemp, 80, 20, False
								
								For i = 1 To Ceil(SelectedItem\state / 10.0)
									DrawImage NavImages(4),xtemp + i * 8 - 6, ytemp + 4
								Next
								
								AASetFont fo\Font[2]
						    EndIf
						EndIf
					EndIf
					;[End Block]
				Case "scp1499", "super1499"
					;[Block]
					For i = 0 To MaxItemAmount - 1
                        If Inventory(i) <> Null Then
                            If Inventory(i)\itemtemplate\name = "SCP-215" And I_215\Timer >= 86.0 Then
                                Msg = scpLang_GetPhrase("ingame.scp1499err1")
						        MsgTimer = 70 * 5
						        SelectedItem = Null
						        Return
                                Exit
                            EndIf
                        EndIf
                    Next
					
					If WearingNightVision > 0
						Msg = scpLang_GetPhrase("ingame.scp1499err2")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
				    ElseIf WearingGasMask > 0
				        Msg = scpLang_GetPhrase("ingame.scp1499err3")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf I_178\Using > 0
                        Msg = scpLang_GetPhrase("ingame.scp1499err4")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf I_215\Using > 0
                        Msg = scpLang_GetPhrase("ingame.scp1499err5")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf WearingHelmet > 0
                        Msg = scpLang_GetPhrase("ingame.scp1499err6")
						MsgTimer = 70 * 5
						SelectedItem = Null
					    Return
                    Else				
						CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
					
						DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					
						width% = 300
						height% = 20
						x% = GraphicWidth / 2 - width / 2
						y% = GraphicHeight / 2 + 80
						Rect(x, y, width + 4, height, False)
						For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
							DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
						Next
					
						SelectedItem\state = Min(SelectedItem\state + (fs\FPSfactor[0]), 100)
					
						If SelectedItem\state = 100 Then
							If I_1499\Using > 0 Then
								I_1499\Using = False
								If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
							Else
								If SelectedItem\itemtemplate\tempname = "scp1499" Then
									I_1499\Using = 1
								Else
									I_1499\Using = 2
								EndIf
								If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
								GiveAchievement(Achv1499)
								For r.Rooms = Each Rooms
									If r\RoomTemplate\Name = "dimension1499" Then
									    BlinkTimer = -1
									    I_1499\PrevRoom = PlayerRoom
									    I_1499\PrevX# = EntityX(Collider)
									    I_1499\PrevY# = EntityY(Collider)
									    I_1499\PrevZ# = EntityZ(Collider)
									
									    If I_1499\X# = 0.0 And I_1499\Y# = 0.0 And I_1499\Z# = 0.0 Then
										    PositionEntity (Collider, r\x + 6086.0 * RoomScale, r\y + 304.0 * RoomScale, r\z + 2292.5 * RoomScale)
										    RotateEntity Collider,0, 90, 0, True
									    Else
										    PositionEntity (Collider, I_1499\X#, I_1499\Y# + 0.05, I_1499\Z#)
									    EndIf
									    ResetEntity(Collider)
									    UpdateDoors()
									    UpdateRooms()
										For it.Items = Each Items
											it\disttimer = 0
										Next
										PlayerRoom = r
										PlaySound_Strict(LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\1499\Enter.ogg")))
										I_1499\X# = 0.0
										I_1499\Y# = 0.0
										I_1499\Z# = 0.0
										If Curr096 <> Null Then
											If Curr096\SoundChn <> 0 Then
												SetStreamVolume_Strict(Curr096\SoundChn, 0.0)
											EndIf
										EndIf
										For e.Events = Each Events
											If e\EventName = "dimension1499" Then
												If EntityDistance(e\room\obj, Collider) > 8300.0 * RoomScale Then
													If e\EventState2 < 5 Then
														e\EventState2 = e\EventState2 + 1
													EndIf
												EndIf
												Exit
											EndIf
										Next
										Exit
									EndIf
								Next
							EndIf
							SelectedItem\state = 0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "badge"
					;[Block]
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						;SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					
					If SelectedItem\state = 0 Then
						PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg"))
						Select SelectedItem\itemtemplate\name
							Case "Old Badge"
								Msg = Chr(34)+scpLang_GetPhrase("ingame.badge")+Chr(34)
								MsgTimer = 70*10
						End Select
						
						SelectedItem\state = 1
					EndIf
					;[End Block]
				Case "key"
					;[Block]
					If SelectedItem\state = 0 Then
						PlaySound_Strict LoadTempSound("SFX\"+"SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
						
						Msg = Chr(34)+scpLang_GetPhrase("ingame.keyshack")+Chr(34)
						MsgTimer = 70*10						
					EndIf
					
					SelectedItem\state = 1
					SelectedItem = Null
					;[End Block]
				Case "oldpaper"
					;[Block]
					If SelectedItem\itemtemplate\img = 0 Then
						SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					
					If SelectedItem\state = 0
						Select SelectedItem\itemtemplate\name
							Case "Disciplinary Hearing DH-S-4137-17092"
								BlurTimer = 1000
								
								Msg = Chr(34)+scpLang_GetPhrase("ingame.oldpaper")+Chr(34)
								MsgTimer = 70*10
								PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg"))
								SelectedItem\state = 1
						End Select
					EndIf
					;[End Block]
				Case "coin"
					;[Block]
					If SelectedItem\state = 0
						PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg"))
					EndIf
					
					Msg = ""
					
					SelectedItem\state = 1
					DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					;[End Block]
				Case "scp427"
					;[Block]
					If I_427\Using=1 Then
						Msg = scpLang_GetPhrase("ingame.locketoff")
						I_427\Using = False
					Else
						GiveAchievement(Achv427)
						Msg = scpLang_GetPhrase("ingame.locketon")
						I_427\Using = True
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null
					;[End Block]
				Case "pill"
					;[Block]
					If I_1499\Using > 0 
					    Msg = scpLang_GetPhrase("ingame.pill1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.pillgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
						Msg = scpLang_GetPhrase("ingame.pillnone")
						MsgTimer = 70*7
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	
					;[End Block]
				Case "scp500pilldeath"
					;[Block]
					If I_1499\Using > 0 
					    Msg = scpLang_GetPhrase("ingame.pill1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.pillgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
						Msg = scpLang_GetPhrase("ingame.pillnone")
						MsgTimer = 70*7
						
						If I_427\Timer < 70*360 Then
							I_427\Timer = 70*360
						EndIf
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
					
				;{~--<MOD>--~}
				
				Case "syringeinf"
					;[Block]
				    If (Not I_402\Using)
					    StaminaEffect = 0.5
						StaminaEffectTimer = 20
						
						Msg = scpLang_GetPhrase("ingame.syringe6")
						MsgTimer = 70 * 8
	
					    I_008\Timer = I_008\Timer + (1+(1*SelectedDifficulty\aggressiveNPCs))
						RemoveItem(SelectedItem)
					Else
                        Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                        MsgTimer = 70*5
                        Return
                        SelectedItem = Null
					EndIf
					;[End Block]
				Case "mintveryfinefirstaid"
			        ;[Block]
			         If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.weed5")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.weed6")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_215\Using > 0
					    Msg = scpLang_GetPhrase("ingame.aid215")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf I_178\Using > 0
					    Msg = scpLang_GetPhrase("ingame.aid178")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.aidnvg")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
			            If (Not I_402\Using)
			                Select Rand(3)
						        Case 1
							        Injuries = 0
							        Bloodloss = 0
							        I_1079\Foam = 0	
							        Msg = scpLang_GetPhrase("ingame.aid2heal1")
							        MsgTimer = 70*7
						        Case 2
							        Injuries = Max(0, Injuries - Rnd(1.0,3.5))
							        Bloodloss = Max(0, Bloodloss - Rnd(25,100))
							        Msg = scpLang_GetPhrase("ingame.aid2heal2")
							        MsgTimer = 70*7
						        Case 3
							        Injuries = 2.5
							        Msg = scpLang_GetPhrase("ingame.aid2heal3")
							        MsgTimer = 70*7
					        End Select
					        I_447\UsingFirstAid = True
					        I_447\UsingFirstAidTimer = 30000
					
					        RemoveItem(SelectedItem)
					    Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            Return
                            SelectedItem = Null
					    EndIf
					EndIf
					;[End Block]
				Case "mintfirstaid", "mintfinefirstaid", "mintfirstaid2"
					;[Block]
					If Bloodloss = 0 And Injuries = 0 And I_1079\Foam = 0 Then
						Msg = scpLang_GetPhrase("ingame.aidnotnow")
						MsgTimer = 70*5
						SelectedItem = Null
					Else
						CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
						
						DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
						
						width% = 300
						height% = 20
						x% = GraphicWidth / 2 - width / 2
						y% = GraphicHeight / 2 + 80
						Rect(x, y, width+4, height, False)
						For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
							DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
						Next
						
						SelectedItem\state = Min(SelectedItem\state+(fs\FPSfactor[0]/5.0),100)			
						
						If Crouch = False Then CrouchCHN = PlaySound_Strict(CrouchSFX) 	
						
						Crouch = True
						
						If SelectedItem\state = 100 Then
							If SelectedItem\itemtemplate\tempname = "mintfinefirstaid" Then
								Bloodloss = 0
								I_1079\Foam = 0	
								Injuries = Max(0, Injuries - 2.5)
								If Injuries = 0 Then
									Msg = scpLang_GetPhrase("ingame.aid2heal4")
								ElseIf Injuries > 1.0
									Msg = scpLang_GetPhrase("ingame.aid2heal5")
								Else
									Msg = scpLang_GetPhrase("ingame.aid2heal6")
								EndIf
								MsgTimer = 70*5
								
								RemoveItem(SelectedItem)
							Else
								Bloodloss = Max(0, Bloodloss - Rand(15,25))
								If Injuries >= 2.5 Then
									Msg = scpLang_GetPhrase("ingame.aid2heal7")
									Injuries = Max(2.5, Injuries-Rnd(0.4,0.8))
								ElseIf Injuries > 1.0
									Injuries = Max(0.5, Injuries-Rnd(0.6,1.1))
									If Injuries > 1.0 Then
										Msg = scpLang_GetPhrase("ingame.aid2heal8")
									Else
										Msg = scpLang_GetPhrase("ingame.aid2heal9")
									EndIf
								Else
									If Injuries > 0.5 Then
										Injuries = 0.5
										Msg = scpLang_GetPhrase("ingame.aid2heal10")
									Else
										Injuries = 0.5
										Msg = scpLang_GetPhrase("ingame.aid2heal11")
									EndIf
								EndIf
								
								If SelectedItem\itemtemplate\tempname = "mintfirstaid2" Then 
									Select Rand(5)
										Case 1
											SuperMan = True
											Msg = scpLang_GetPhrase("ingame.aid2heal12")
										Case 2
											InvertMouse = (Not InvertMouse)
											Msg = scpLang_GetPhrase("ingame.aid2heal13")
										Case 3
											BlinkEffect = 0.4
											BlinkEffectTimer = Rand(30,40)
										Case 4
											Bloodloss = 0
											I_1079\Foam = 0	
											Injuries = 0
											Msg = scpLang_GetPhrase("ingame.aid2heal14")
										Case 5
											Msg = scpLang_GetPhrase("ingame.aid2heal15")
											Injuries = 2.5
									End Select
								EndIf
								I_447\UsingFirstAid = True
						        I_447\UsingFirstAidTimer = 40000
						
								MsgTimer = 70*5
								RemoveItem(SelectedItem)
							EndIf							
						EndIf
					EndIf
					;[End Block]
                Case "minteyedrops", "minteyedrops2"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops21499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.drops2gas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_215\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops2215")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf I_178\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops2178")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.drops2nvg")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
					    If (Not I_402\Using)
					        Msg = scpLang_GetPhrase("ingame.drops2good")
					        MsgTimer = 70*5
						    BlinkEffect = 0.5
						    BlinkEffectTimer = Rand(30,40)
						    BlurTimer = 200
						    I_447\UsingEyeDrops = True
						    I_447\UsingEyeDropsTimer = 20000
					        RemoveItem(SelectedItem)
				        Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            Return
                            SelectedItem = Null
					    EndIf
					EndIf
					;[End Block]
				Case "mintfineeyedrops"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops21499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.drops2gas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_215\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops2215")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf I_178\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops2178")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.drops2nvg")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
					   If (Not I_402\Using)
						    BlinkEffect = 0.3
						    BlinkEffectTimer = Rand(40,50)
						    Bloodloss = Max(Bloodloss-1.5, 0)
						    BlurTimer = 200
						    I_447\UsingEyeDrops = True
						    I_447\UsingEyeDropsTimer = 20000
					        RemoveItem(SelectedItem)
					    Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            Return
                            SelectedItem = Null
					    EndIf
					EndIf
					;[End Block]
				Case "mintsupereyedrops"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops21499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.drops2gas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf I_215\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops2215")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf I_178\Using > 0
					    Msg = scpLang_GetPhrase("ingame.drops2178")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.drops2nvg")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
					   If (Not I_402\Using)
						    BlinkEffect = 0.1
						    BlinkEffectTimer = Rand(50,60)
						    Bloodloss = Max(Bloodloss-3.5, 0)
						    BlurTimer = 200
						    I_447\UsingEyeDrops = True
						    I_447\UsingEyeDropsTimer = 20000
					        RemoveItem(SelectedItem)
					    Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            Return
                            SelectedItem = Null
					    EndIf
					EndIf
					;[End Block]
				Case "morphine"
					;[Block]
					If (Not I_402\Using)
					    If Injuries < 0.5 Then
						    Msg = scpLang_GetPhrase("ingame.morphine1")
						    MsgTimer = 70*5
						    SelectedItem = Null
					    Else
						    If Injuries > 3 Then
							    MorphineHealAmount = Min(MorphineHealAmount + 2.5, 4)
							    Injuries = Max(Injuries-2.5, 0.5)
						    Else
							    MorphineHealAmount = Min((Injuries-0.5)+MorphineHealAmount, 4)
							    Injuries=Max(Injuries-2.5,0.5)
						    EndIf
						    Msg = scpLang_GetPhrase("ingame.morphine2")
						    MsgTimer = 70*5							
						    MorphineTimer=10000
						    UsedMorphine=True
						    RemoveItem(SelectedItem)
						EndIf
					Else
                        Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                        MsgTimer = 70*5
                        SelectedItem = Null
                        Return
					EndIf
					;[End Block]
				 Case "mintscp500pill"
					;[Block]
					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.pillmint1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.pillmintgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
						GiveAchievement(Achv500)
						
						If I_008\Timer > 0 Then
							Msg = scpLang_GetPhrase("ingame.pill008")
						ElseIf I_409\Timer > 0 Then
						    Msg = scpLang_GetPhrase("ingame.pill409")
						ElseIf I_357\Timer > 0 Then
						    Msg = scpLang_GetPhrase("ingame.pill357")						
						Else
							Msg = scpLang_GetPhrase("ingame.pillnone")
						EndIf
						MsgTimer = 70*7
						
						DeathTimer = 0
						I_008\Timer = 0
				        I_409\Timer = 0
				        I_207\Timer = 0
				        I_357\Timer = 0
				        ;Returns the color of the blood back to normal
						I_1079\Take = 0
					    I_1079\Foam = 0
					    I_1079\Trigger = 0									
						Stamina = 100
						
						For i = 0 To 5
							SCP1025state[i]=0
						Next
										
						;Box Of Horrors
						For e.Events = Each Events
							If e\EventName = "room009" Then e\EventState = 0.0 : e\EventState3 = 0.0
						Next
						
						If StaminaEffect > 1.0 Then
							StaminaEffect = 1.0
							StaminaEffectTimer = 0.0
						EndIf
						
						If BlinkEffect > 1.0 Then
							BlinkEffect = 1.0
							BlinkEffectTimer = 0.0
						EndIf

						I_447\UsingPill = True
						I_447\UsingPillTimer = 40000
												
						RemoveItem(SelectedItem)
					EndIf	
					;[End Block]
			    Case "scp447"
			        ;[Block]
			        If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.aid1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.aidgas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
			            If (Not I_402\Using)
			                If Rand(10) = 1 Then
			                    Msg = Chr(34) + scpLang_GetPhrase("ingame.slime1") + Chr(34)
	                        Else
	                            Msg = Chr(34) + scpLang_GetPhrase("ingame.slime2") + Chr(34)
	                        EndIf
	                        MsgTimer = 70*5
	                        SelectedItem = Null
	                    Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            SelectedItem = Null
                            Return
	                    EndIf
	                EndIf
	                ;[End Block]
                Case "scp178"
				    ;[Block]
				    If WearingNightVision > 0
						Msg = scpLang_GetPhrase("ingame.scp1781")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
				    ElseIf WearingGasMask > 0
				        Msg = scpLang_GetPhrase("ingame.scp1782")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf I_1499\Using > 0
                        Msg = scpLang_GetPhrase("ingame.scp1783")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf I_215\Using > 0
                        Msg = scpLang_GetPhrase("ingame.scp1784")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf WearingHelmet > 0
                        Msg = scpLang_GetPhrase("ingame.scp1785")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
                    Else			
				        If I_178\Using = 1 Then
						    Msg = scpLang_GetPhrase("ingame.scp1786")
						    I_178\Using = 0
					    Else
						    GiveAchievement(Achv178)
						    Msg = scpLang_GetPhrase("ingame.scp1787")
						    I_178\Using = 1
					    EndIf
                        MsgTimer = 70 * 5
			            SelectedItem = Null
			        EndIf	
					;[End Block]
                Case "scp215"
					;[Block]
					If I_215\Timer >= 86.0
					    I_215\Using = 1
						Msg = scpLang_GetPhrase("ingame.scp2151")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					EndIf

					If WearingNightVision > 0
						Msg = scpLang_GetPhrase("ingame.scp2152")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
				    ElseIf WearingGasMask > 0
				        Msg = scpLang_GetPhrase("ingame.scp2153")
				        MsgTimer = 70 * 5
				        SelectedItem = Null
						Return
					ElseIf I_1499\Using > 0
                        Msg = scpLang_GetPhrase("ingame.scp2154")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf I_178\Using > 0
                        Msg = scpLang_GetPhrase("ingame.scp2155")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					ElseIf WearingHelmet > 0
                        Msg = scpLang_GetPhrase("ingame.scp2156")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
                    Else			
                        If I_215\Using = 1 Then
						    Msg = scpLang_GetPhrase("ingame.scp2157")
						    I_215\Using = 0
					    Else
						    GiveAchievement(Achv215)
						    Msg = scpLang_GetPhrase("ingame.scp2158")
						    I_215\Using = 1					
					    EndIf
                        MsgTimer = 70 * 5
			            SelectedItem = Null
			        EndIf						
					;[End Block]
                Case "glassescase"
					;[Block]
											
					If ItemAmount < MaxItemAmount Then ;Temporary
						For n% = 0 To ItemAmount + 0
						    If Inventory(n) = Null Then
						        If ItemAmount > MaxItemAmount Then
								    Msg = scpLang_GetPhrase("events.moreitems")
								    MsgTimer = 70*5	
						        Else
                                    Inventory(n) = CreateItem("SCP-215", "scp215", 1, 1, 1)
								    Inventory(n)\Picked = True
								    Inventory(n)\Dropped = -1
							        Inventory(n)\itemtemplate\found=True
							        I_215\Limit = I_215\Limit + 1
								    HideEntity Inventory(n)\collider
			                        EntityType (Inventory(n)\collider, HIT_ITEM)
			                        EntityParent(Inventory(n)\collider, 0)
			                    EndIf										
							EndIf	
						Next
						If I_215\Limit >= 1 Then 
						    RemoveItem(SelectedItem)
						    I_215\Limit = 0
						EndIf
				    Else
						Msg = scpLang_GetPhrase("events.moreitems")
						MsgTimer = 70*5					
					EndIf																																										
					;[End Block]
				Case "scp1033ru", "super1033ru"
					;[Block]
					If I_1033RU\Using > 0 Then
						Msg = scpLang_GetPhrase("ingame.scp10331")
						I_1033RU\Using = False
					Else
						GiveAchievement(Achv1033RU)
						Msg = scpLang_GetPhrase("ingame.scp10332")
						If SelectedItem\itemtemplate\tempname = "scp1033ru" Then
							I_1033RU\Using = 1
						Else
							I_1033RU\Using = 2
						EndIf
					EndIf
					
					MsgTimer = 70 * 5
					SelectedItem = Null		
				    ;[End Block]
			    Case "scp1079sweet"
                    ;[Block]
				     If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.scp10791")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.scp10792")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
				        Select Rand(2)
                            Case 1
						        Msg = scpLang_GetPhrase("ingame.scp10793")
				            Case 2
						        Msg = scpLang_GetPhrase("ingame.scp10794")
						End Select
						MsgTimer = 70*7
                        
                        I_1079\Take = I_1079\Take + 1
                        If I_1033RU\HP = 0
					        Injuries = Injuries + 0.05
					    Else
					        Damage1033RU(Rand(5))
					    EndIf
                        PlaySound_Strict(SizzSFX(Rand(0,1)))

					    GiveAchievement(Achv1079)
					    RemoveItem(SelectedItem)
					    pvt = CreatePivot()
			            PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
			            TurnEntity pvt, 90, 0, 0
			            EntityPick(pvt,0.3)
			            de.decals = CreateDecal(Rand(22,23), PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
			            de\size = Rnd(0.06,0.1) : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\size, de\size
			            ChannelVolume tempchn, Rnd(0.0,0.8)*SFXVolume
			            ChannelPitch tempchn, Rand(20000,30000)
			
			            FreeEntity pvt					
					EndIf
									
				    If I_1079\Take >= 4 Then
				        I_1079\Trigger = 1													
					    Injuries = Injuries+Rand(2,3)
					    DeathMSG = scpLang_GetPhrase("ingame.scp1079d")
					EndIf
					;[End Block]
				Case "scp1079"
					;[Block]
											
					If ItemAmount < MaxItemAmount Then ;Temporary
						For n% = 0 To ItemAmount
						    If Inventory(n) = Null Then
						        If ItemAmount > MaxItemAmount Then
								    Msg = scpLang_GetPhrase("events.moreitems")
								    MsgTimer = 70*5	
						        Else
                                    Inventory(n) = CreateItem("SCP-1079-01", "scp1079sweet", 1, 1, 1)
								    Inventory(n)\Picked = True
								    Inventory(n)\Dropped = -1
							        Inventory(n)\itemtemplate\found=True
							        I_1079\Limit = I_1079\Limit + 1
								    HideEntity Inventory(n)\collider
			                        EntityType (Inventory(n)\collider, HIT_ITEM)
			                        EntityParent(Inventory(n)\collider, 0)
			                    EndIf										
							EndIf	
						Next
						If I_1079\Limit >= 4 Then 
						    RemoveItem(SelectedItem)
						    I_1079\Limit = 0
						EndIf
				    Else
						Msg = scpLang_GetPhrase("events.moreitems")
						MsgTimer = 70*5					
					EndIf																																									
					;[End Block]
				Case "scp207"
				    ;[Block]
				    If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.scp2071499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.scp207gas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
				        If (Not I_402\Using)
					        CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
							
							DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
							
							width% = 300
					        height% = 20
					        x% = GraphicWidth / 2 - width / 2
					        y% = GraphicHeight / 2 + 80
					        Rect(x, y, width+4, height, False)
					        For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
						        DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					        Next
					
					        SelectedItem\state = Min(SelectedItem\state+(fs\FPSfactor[0]/1.2),100)
							
							If SelectedItem\state = 100 Then
							    If SelectedItem\itemtemplate\tempname = "scp207" Then
							        PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\109\Ahh.ogg"))
							
								    BlinkEffect = 0.6
								    BlinkEffectTimer = 400
								
								    Bloodloss = 0
							        Injuries = 0
							
							        If I_207\Timer = 0 Then Speed = Speed * 2
							
						            StaminaEffect = 0.6
						            StaminaEffectTimer = 400
						
						            DeathTimer = 0
						            I_008\Timer = 0
						            Stamina = 100
						            I_409\Timer = 0
						            I_207\Timer = 0
						            I_357\Timer = 0
						            ;Returns the color of the blood back to normal
						            I_1079\Take = 0
					                I_1079\Foam = 0
					                I_1079\Trigger = 0	
						
						            For i = 0 To 5
							            SCP1025state[i]=0
						            Next
												
						            For e.Events = Each Events
						            	If e\EventName="room009" Then e\EventState=0.0 : e\EventState3=0.0
						            Next
						
						            I_207\Timer = 1.0

							        Select Rand(1,4)
							            Case 1
							                Msg = scpLang_GetPhrase("ingame.scp2071")
							                MsgTimer = 70 * 6
							            Case 2
							                Msg = scpLang_GetPhrase("ingame.scp2072")
							                MsgTimer = 70 * 6
							            Case 3
							                Msg = scpLang_GetPhrase("ingame.scp2073")
							                MsgTimer = 70 * 6
							            Case 4
							                Msg = scpLang_GetPhrase("ingame.scp2074")
							                MsgTimer = 70 * 6
							        End Select
							        SelectedItem\state = 0
                                    RemoveItem(SelectedItem)
                                EndIf
                            EndIf
                        Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            Return
                            SelectedItem = Null
                        EndIf
                    EndIf	
					;[End Block]
				Case "scp198"
					;[Block]
				    Msg = Chr(34) + scpLang_GetPhrase("ingame.scp1981") + Chr(34)
					MsgTimer = 70 * 7
					SelectedItem = Null
					;[End Block]
				Case "scp109"
				    ;[Block]
				    If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.scp1091499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.scp109gas")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
		                If (Not I_402\Using)
					        CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
							
					        DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
							
					        width% = 300
					        height% = 20
					        x% = GraphicWidth / 2 - width / 2
					        y% = GraphicHeight / 2 + 80
					        Rect(x, y, width+4, height, False)
					        For i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
						        DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					        Next
					
					        SelectedItem\state = Min(SelectedItem\state+(fs\FPSfactor[0]/1.2),100)
			
					        If SelectedItem\state = 100 Then
						        If SelectedItem\itemtemplate\tempname = "scp109" Then
							        GiveAchievement(Achv109)
							        If Stamina >= -11.0 And Stamina < 25.0 Then
								        BlurTimer = 10000
								        VomitTimer = 10
								        Msg = scpLang_GetPhrase("ingame.scp1091")
							        ElseIf Stamina > 25.0 And Stamina < 60.0
								        PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\109\ahh.ogg"))
							            Injuries = Max(0, Injuries - Rnd(0.09, 0.1))
								        Msg = scpLang_GetPhrase("ingame.scp1092")
							        ElseIf Stamina > 60.0 And Stamina < 100.0
								        PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\109\ahh.ogg"))
							            Injuries = Max(0, Injuries - Rnd(0.1, 0.5))
								        Msg = scpLang_GetPhrase("ingame.scp1093")
							        Else
								        Injuries = Max(0, Injuries - Rnd(0.1, 0.5))
								        PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\109\ahh.ogg"))
								        Msg = scpLang_GetPhrase("ingame.scp1094")
							        EndIf
							        Stamina = Stamina + Rand(40)
                                    MsgTimer = 70 * 5
                                    SelectedItem\state = 0
                                    SelectedItem = Null
                                EndIf
                            EndIf
                        Else
                            Msg = Chr(34)+scpLang_GetPhrase("events.room10488")+Chr(34)
                            MsgTimer = 70*5
                            Return
                            SelectedItem = Null
                        EndIf
                    EndIf	
					;[End Block]
				Case "scp500"
					;[Block]					
					If ItemAmount < MaxItemAmount Then ;Temporary
						For n% = 0 To ItemAmount+0
						    If Inventory(n) = Null Then
						        If ItemAmount > MaxItemAmount Then
								Msg = scpLang_GetPhrase("events.moreitems")
								MsgTimer = 70*5	
						    Else
                                Inventory(n) = CreateItem("SCP-500-01", "scp500pill", 1, 1, 1)
								Inventory(n)\Picked = True
								Inventory(n)\Dropped = -1
							    Inventory(n)\itemtemplate\found=True
							    I_500\Limit = I_500\Limit + 1
								HideEntity Inventory(n)\collider
			                    EntityType (Inventory(n)\collider, HIT_ITEM)
			                    EntityParent(Inventory(n)\collider, 0)
			                    EndIf										
							EndIf	
						Next
						If I_500\Limit >= 3 Then 
						    RemoveItem(SelectedItem)
						    I_500\Limit = 0
						EndIf
				    Else
						Msg = scpLang_GetPhrase("events.moreitems")
						MsgTimer = 70*5					
					EndIf																																										
					;[End Block]
				Case "scp402"
				    ;[Block]
				    If I_402\Timer >= 40
					    I_402\Using = 1
						Msg = scpLang_GetPhrase("ingame.scp4021")
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					EndIf

				    If WearingGasmask = 0 And I_1499\Using = 0 Then
				        If I_402\Using = 1 Then
						    Msg = scpLang_GetPhrase("ingame.scp4022")
						    I_402\Using = 0
					    Else
						    GiveAchievement(Achv402)
						    Msg = scpLang_GetPhrase("ingame.scp4023")
						    I_402\Using = 1
					    EndIf
					
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					EndIf		
				    ;[End Block]
				Case "helmet"
					;[Block]
					For i = 0 To MaxItemAmount - 1
                        If Inventory(i) <> Null Then
                            If Inventory(i)\itemtemplate\name = "SCP-215" And I_215\Timer >= 86.0 Then
                                Msg = scpLang_GetPhrase("ingame.helmetglasses")
						        MsgTimer = 70 * 5
						        SelectedItem = Null
						        Return
                                Exit
                            EndIf
                        EndIf
                    Next

					If I_1499\Using > 0
					    Msg = scpLang_GetPhrase("ingame.helmet1499")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingNightVision > 0
					    Msg = scpLang_GetPhrase("ingame.helmetnvg")
                        MsgTimer = 70 * 5
                        SelectedItem = Null
					    Return
				    ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.helmetgas")
                        MsgTimer = 70 * 5
                        SelectedItem = Null
					    Return
					ElseIf I_178\Using > 0
					    Msg = scpLang_GetPhrase("ingame.helmet178")
                        MsgTimer = 70 * 5
                        SelectedItem = Null
					    Return
                    ElseIf I_215\Using > 0
					    Msg = scpLang_GetPhrase("ingame.helmet215")
                        MsgTimer = 70 * 5
                        SelectedItem = Null
					    Return
                    Else		
					    CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
					
					    DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					
					    width% = 300
					    height% = 20
					    x% = GraphicWidth / 2 - width / 2
					    y% = GraphicHeight / 2 + 80
					    Rect(x, y, width + 4, height, False)
					    For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
					    	DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					    Next
					
					    SelectedItem\state = Min(SelectedItem\state+(fs\FPSfactor[0]),100)
					
					    If SelectedItem\state = 100 Then
                            If WearingHelmet > 0 Then
                                WearingHelmet = False
                                Msg = scpLang_GetPhrase("ingame.helmetoff")
                                If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
                            Else
						        Msg = scpLang_GetPhrase("ingame.helmeton")
							    WearingHelmet = 1
							    If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
                            EndIf
						    SelectedItem\state = 0
						    MsgTimer = 70 * 5
						    SelectedItem = Null
					    EndIf
					EndIf
					;[End Block]
				Case "mintpill"
					;[Block]
					If I_1499\Using > 0 
					    Msg = scpLang_GetPhrase("ingame.mintpill1")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.mintpill2")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
						Msg = scpLang_GetPhrase("ingame.mintpill3")
						MsgTimer = 70*7
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	
					;[End Block]
				Case "mintscp500pilldeath"
					;[Block]
					If I_1499\Using > 0 
					    Msg = scpLang_GetPhrase("ingame.mintpill1")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
					ElseIf WearingGasMask > 0
					    Msg = scpLang_GetPhrase("ingame.mintpill2")
					    MsgTimer = 70 * 5
					    SelectedItem = Null
					    Return
                    Else
						Msg = scpLang_GetPhrase("ingame.mintpill3")
						MsgTimer = 70*7
						
						If I_427\Timer < 70*360 Then
							I_427\Timer = 70*360
						EndIf
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
					
	            ;{~--<END>--~}
	
				Default
					;[Block]
					;check if the item is an inventory-type object
					If SelectedItem\invSlots>0 Then
						DoubleClick = 0
						MouseHit1 = 0
						MouseDown1 = 0
						LastMouseHit1 = 0
						OtherOpen = SelectedItem
						SelectedItem = Null
					EndIf
					;[End Block]
			End Select
			
			If SelectedItem <> Null Then
				If SelectedItem\itemtemplate\img <> 0
					Local IN$ = SelectedItem\itemtemplate\tempname
					If IN$ = "paper" Or IN$ = "badge" Or IN$ = "oldpaper" Or IN$ = "ticket" Then
						For a_it.Items = Each Items
							If a_it <> SelectedItem
								Local IN2$ = a_it\itemtemplate\tempname
								If IN2$ = "paper" Or IN2$ = "badge" Or IN2$ = "oldpaper" Or IN2$ = "ticket" Then
									If a_it\itemtemplate\img<>0
										If a_it\itemtemplate\img <> SelectedItem\itemtemplate\img
											FreeImage(a_it\itemtemplate\img)
											a_it\itemtemplate\img = 0
										EndIf
									EndIf
								EndIf
							EndIf
						Next
					EndIf
				EndIf			
			EndIf
			
			If MouseHit2 Then
				EntityAlpha at\OverlayID[13], 0.0
				
				IN$ = SelectedItem\itemtemplate\tempname
				If IN$ = "scp1025" Then
					If SelectedItem\itemtemplate\img<>0 Then FreeImage(SelectedItem\itemtemplate\img)
					SelectedItem\itemtemplate\img=0
				ElseIf IN$ = "firstaid" Or IN$="finefirstaid" Or IN$="firstaid2" Or IN$="mintfirstaid" Or IN$="mintfirstaid2" Or IN$="mintfinefirstaid" Or IN$="scp207" Or IN$="cola" Or IN$="flask" Or IN$="scp109" Then
					SelectedItem\state = 0
				ElseIf IN$ = "vest" Or IN$="finevest"
					SelectedItem\state = 0
					If (Not WearingVest)
						DropItem(SelectedItem,False)
					EndIf
				ElseIf IN$="hazmatsuit" Or IN$="hazmatsuit2" Or IN$="hazmatsuit3"
					SelectedItem\state = 0
					If (Not WearingHazmat)
						DropItem(SelectedItem,False)
					EndIf
				ElseIf IN$="scp1499" Or IN$="super1499" Or IN$="gasmask" Or IN$="supergasmask" Or IN$="gasmask3" Or IN$="helmet"
					SelectedItem\state = 0
				EndIf
				
				If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
				SelectedItem = Null
			EndIf
		End If		
	EndIf
	
	If SelectedItem = Null Then
		For i = 0 To 5
			If RadioCHN(i) <> 0 Then 
				If ChannelPlaying(RadioCHN(i)) Then PauseChannel(RadioCHN(i))
			EndIf
		Next
	EndIf
	
	For it.Items = Each Items
		If it<>SelectedItem
			Select it\itemtemplate\tempname
				Case "firstaid","finefirstaid","firstaid2","vest","finevest","hazmatsuit","hazmatsuit2","hazmatsuit3","scp1499","super1499", "gasmask", "supergasmask", "gasmask3", "scp207", "mintfirstaid", "mintfirstaid2", "mintfinefirstaid", "scp109", "helmet"
					it\state = 0
			End Select
		EndIf
	Next
	
	If PrevInvOpen And (Not InvOpen) Then MoveMouse viewport_center_x, viewport_center_y
	
End Function

Function DrawMenu()
	Local fs.FPS_Settings = First FPS_Settings
	Local fo.Fonts = First Fonts
	Local x%, y%, width%, height%
	If api_GetFocus() = 0 Then ;Game is out of focus -> pause the game
		If (Not Using294) Then
			ms\MenuOpen = True
			PauseSounds()
		EndIf
        Delay 1000 ;Reduce the CPU take while game is not in focus
    EndIf
	If ms\MenuOpen Then
		
		If PlayerRoom\RoomTemplate\Name$ <> "gateb" And PlayerRoom\RoomTemplate\Name$ <> "gatea"
			If ms\StopHidingTimer = 0 Then
				If EntityDistance(Curr173\Collider, Collider)<4.0 Or EntityDistance(Curr106\Collider, Collider)<4.0 Then 
					ms\StopHidingTimer = 1
				EndIf	
			ElseIf ms\StopHidingTimer < 40
				If KillTimer >= 0 Then 
					ms\StopHidingTimer = ms\StopHidingTimer+fs\FPSfactor[0]
					
					If ms\StopHidingTimer => 40 Then
						PlaySound_Strict(HorrorSFX(15))
						Msg = scpLang_GetPhrase("ingame.stophiding")
						MsgTimer = 6*70
						ms\MenuOpen = False
						Return
					EndIf
				EndIf
			EndIf
		EndIf
		
		InvOpen = False
		
		width = ImageWidth(PauseMenuIMG)
		height = ImageHeight(PauseMenuIMG)
		x = GraphicWidth / 2 - width / 2
		y = GraphicHeight / 2 - height / 2
		
		DrawImage PauseMenuIMG, x, y
		
		Color(255, 255, 255)
		
		x = x+132*MenuScale
		y = y+122*MenuScale	
		
		If (Not MouseDown1)
			OnSliderID = 0
		EndIf
		
		If AchievementsMenu > 0 Then
			AASetFont fo\Font[1]
			AAText(x, y-(122-45)*MenuScale, scpLang_GetPhrase("menu.achievements"), False, True)
			AASetFont fo\Font[0]
		ElseIf OptionsMenu > 0 Then
			AASetFont fo\Font[1]
			AAText(x, y-(122-45)*MenuScale, scpLang_GetPhrase("menu.options"),False,True)
			AASetFont fo\Font[0]
		ElseIf QuitMSG > 0 Then
			AASetFont fo\Font[1]
			AAText(x, y-(122-45)*MenuScale, scpLang_GetPhrase("menu.quit2"),False,True)
			AASetFont fo\Font[0]
		ElseIf KillTimer >= 0 Then
			AASetFont fo\Font[1]
			AAText(x, y-(122-45)*MenuScale, scpLang_GetPhrase("menu.paused"),False,True)
			AASetFont fo\Font[0]
		Else
			AASetFont fo\Font[1]
			AAText(x, y-(122-45)*MenuScale, scpLang_GetPhrase("menu.youdied"),False,True)
			AASetFont fo\Font[0]
		End If		
		
		Local AchvXIMG% = (x + (22*MenuScale))
		Local scale# = GraphicHeight/768.0
		Local SeparationConst% = 76*scale
		Local imgsize% = 64
		
		If AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG <= 0
			AASetFont fo\Font[0]
			AAText x, y, scpLang_GetPhrase("menu.difficulty") + " "+SelectedDifficulty\name
			AAText x, y+20*MenuScale, scpLang_GetPhrase("menu.save") + " "+CurrSave
			AAText x, y+40*MenuScale, scpLang_GetPhrase("menu.seed")+" "+RandomSeed
		ElseIf AchievementsMenu <= 0 And OptionsMenu > 0 And QuitMSG <= 0 And KillTimer >= 0
			If DrawButton(x + 101 * MenuScale, y + 410 * MenuScale, 230 * MenuScale, 60 * MenuScale, scpLang_GetPhrase("menu.back2")) Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMSG = 0
				MouseHit1 = False
				SaveOptionsINI()
				
				AntiAlias Opt_AntiAlias
				TextureLodBias TextureFloat#
			EndIf
			
			Color 0,255,0
			If OptionsMenu = 1
				Rect(x-10*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			ElseIf OptionsMenu = 2
				Rect(x+100*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			ElseIf OptionsMenu = 3
				Rect(x+210*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			ElseIf OptionsMenu = 4
				Rect(x+320*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			EndIf
			
			If DrawButton(x-5*MenuScale,y,100*MenuScale,30*MenuScale,scpLang_GetPhrase("menu.graphics"),False) Then OptionsMenu = 1
			If DrawButton(x+105*MenuScale,y,100*MenuScale,30*MenuScale,scpLang_GetPhrase("menu.audio"),False) Then OptionsMenu = 2
			If DrawButton(x+215*MenuScale,y,100*MenuScale,30*MenuScale,scpLang_GetPhrase("menu.controls"),False) Then OptionsMenu = 3
			If DrawButton(x+325*MenuScale,y,100*MenuScale,30*MenuScale,scpLang_GetPhrase("menu.advanced"),False) Then OptionsMenu = 4
			
			Local tx# = (GraphicWidth/2)+(width/2)
			Local ty# = y
			Local tw# = 400*MenuScale
			Local th# = 150*MenuScale
			
			Color 255,255,255
			Select OptionsMenu
				Case 1 ;Graphics
					AASetFont fo\Font[0]
					;[Block]
					y=y+50*MenuScale
					
					Color 100,100,100
					AAText(x, y, scpLang_GetPhrase("menu.bumpmapping"))	
					BumpEnabled = DrawTick(x + 270 * MenuScale, y + MenuScale, BumpEnabled, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"bump")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.vsync"))
					Vsync% = DrawTick(x + 270 * MenuScale, y + MenuScale, Vsync%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"vsync")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.antialiasing"))
					Opt_AntiAlias = DrawTick(x + 270 * MenuScale, y + MenuScale, Opt_AntiAlias%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"antialias")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.roomlights"))
					EnableRoomLights = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableRoomLights)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"roomlights")
					EndIf
					
					y=y+30*MenuScale
					
					ScreenGamma = (SlideBar(x + 270*MenuScale, y+6*MenuScale, 100*MenuScale, ScreenGamma*50.0)/50.0)
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.gamma"))
					If MouseOn(x+270*MenuScale,y+6*MenuScale,100*MenuScale+14,20) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"gamma",ScreenGamma)
					EndIf
					
					;y = y + 50*MenuScale
					
					y=y+50*MenuScale
					
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.particleamount"))
					ParticleAmount = Slider3(x+270*MenuScale,y+6*MenuScale,100*MenuScale,ParticleAmount,2,scpLang_GetPhrase("menu.minimal"),scpLang_GetPhrase("menu.reduced"),scpLang_GetPhrase("menu.full"))
					If (MouseOn(x + 270 * MenuScale, y-6*MenuScale, 100*MenuScale+14, 20) And OnSliderID=0) Or OnSliderID=2
						DrawOptionsTooltip(tx,ty,tw,th,"particleamount",ParticleAmount)
					EndIf
					
					y=y+50*MenuScale
					
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.lodbias"))
					TextureDetails = Slider5(x+270*MenuScale,y+6*MenuScale,100*MenuScale,TextureDetails,3,"0.8","0.4","0.0","-0.4","-0.8")
					Select TextureDetails%
						Case 0
							TextureFloat# = 0.8
						Case 1
							TextureFloat# = 0.4
						Case 2
							TextureFloat# = 0.0
						Case 3
							TextureFloat# = -0.4
						Case 4
							TextureFloat# = -0.8
					End Select
					TextureLodBias TextureFloat
					If (MouseOn(x+270*MenuScale,y-6*MenuScale,100*MenuScale+14,20) And OnSliderID=0) Or OnSliderID=3
						DrawOptionsTooltip(tx,ty,tw,th+100*MenuScale,"texquality")
					EndIf
					
					y=y+40*MenuScale
					Color 100,100,100
					AAText(x, y, scpLang_GetPhrase("menu.vram"))	
					SaveTexturesInVRam = DrawTick(x + 270 * MenuScale, y + MenuScale, SaveTexturesInVRam, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"vram")
					EndIf
					
					;{~--<MOD>--~}
					
					;y=y+40*MenuScale
					y=y+50*MenuScale
					
					Local SlideBarFOV# = FOV#-40
					SlideBarFOV = (SlideBar(x + 270*MenuScale, y+6*MenuScale,100*MenuScale, SlideBarFOV*2.0)/2.0)
					FOV = SlideBarFOV+40
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.fov"))
					Color 255,255,0
					AAText(x + 5 * MenuScale, y + 25 * MenuScale, Int(FOV#)+" FOV")
					If MouseOn(x+270*MenuScale,y+6*MenuScale,100*MenuScale+14,20)
					;If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"fov")
					EndIf
					CameraZoom(Camera, Min(1.0+(CurrCameraZoom/400.0),1.1) / Tan((2*ATan(Tan((FOV#)/2)*RealGraphicWidth/RealGraphicHeight))/2.0))
					
					;{~--<END>--~}
					
					;[End Block]
				Case 2 ;Audio
					AASetFont fo\Font[0]
					;[Block]
					y = y + 50*MenuScale
					
					MusicVolume = (SlideBar(x + 250*MenuScale, y-4*MenuScale, 100*MenuScale, MusicVolume*100.0)/100.0)
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.musicvolume"))
					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"musicvol",MusicVolume)
					EndIf
					
					y = y + 30*MenuScale
					
					PrevSFXVolume = (SlideBar(x + 250*MenuScale, y-4*MenuScale, 100*MenuScale, SFXVolume*100.0)/100.0)
					If (Not DeafPlayer) Then SFXVolume# = PrevSFXVolume#
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.soundvolume"))
					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"soundvol",PrevSFXVolume)
					EndIf
					
					y = y + 30*MenuScale
					
					Color 100,100,100
					AAText x, y, scpLang_GetPhrase("menu.soundautorelease")
					EnableSFXRelease = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableSFXRelease,True)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th+220*MenuScale,"sfxautorelease")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 100,100,100
					AAText x, y, scpLang_GetPhrase("menu.usertracks")
					EnableUserTracks = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableUserTracks,True)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"usertrack")
					EndIf
					
					If EnableUserTracks
						y = y + 30 * MenuScale
						Color 255,255,255
						AAText x, y, scpLang_GetPhrase("menu.trackmode")
						UserTrackMode = DrawTick(x + 270 * MenuScale, y + MenuScale, UserTrackMode)
						If UserTrackMode
							AAText x, y + 20 * MenuScale, scpLang_GetPhrase("menu.repeat")
						Else
							AAText x, y + 20 * MenuScale, scpLang_GetPhrase("menu.random")
						EndIf
						If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"usertrackmode")
						EndIf
						;DrawButton(x, y + 30 * MenuScale, 190 * MenuScale, 25 * MenuScale, "Scan for User Tracks",False)
						;If MouseOn(x,y+30*MenuScale,190*MenuScale,25*MenuScale)
						;	DrawOptionsTooltip(tx,ty,tw,th,"usertrackscan")
						;EndIf
					EndIf
					;[End Block]
				Case 3 ;Controls
					AASetFont fo\Font[0]
					;[Block]
					y = y + 50*MenuScale
					
					MouseSensitivity = (SlideBar(x + 270*MenuScale, y-4*MenuScale, 100*MenuScale, (MouseSensitivity+0.5)*100.0)/100.0)-0.5
					Color(255, 255, 255)
					AAText(x, y, scpLang_GetPhrase("menu.mousesensitivity"))
					If MouseOn(x+270*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"mousesensitivity",MouseSensitivity)
					EndIf
					
					y = y + 30*MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, scpLang_GetPhrase("menu.invertmouse"))
					InvertMouse = DrawTick(x + 270 * MenuScale, y + MenuScale, InvertMouse)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"mouseinvert")
					EndIf
					
					y = y + 40*MenuScale
					
					mouse_smooth = DrawTick(x + 270*MenuScale, y+MenuScale,mouse_smooth)
					Color(255, 255, 255)
					AAText(x, y, scpLang_GetPhrase("menu.mousesmoothing"))
					If MouseOn(x+270*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"mousesmoothing")
					EndIf
					
					Color(255, 255, 255)
					
					y = y + 30*MenuScale
					AAText(x, y, scpLang_GetPhrase("menu.controlconfig"))
					y = y + 10*MenuScale
					
					AAText(x, y + 20 * MenuScale, scpLang_GetPhrase("menu.moveforward"))
					InputBox(x + 200 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_UP,210)),5)		
					AAText(x, y + 40 * MenuScale, scpLang_GetPhrase("menu.strafeleft"))
					InputBox(x + 200 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_LEFT,210)),3)	
					AAText(x, y + 60 * MenuScale, scpLang_GetPhrase("menu.movebackward"))
					InputBox(x + 200 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_DOWN,210)),6)				
					AAText(x, y + 80 * MenuScale, scpLang_GetPhrase("menu.straferight"))
					InputBox(x + 200 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_RIGHT,210)),4)
					
					AAText(x, y + 100 * MenuScale, scpLang_GetPhrase("menu.manualblink"))
					InputBox(x + 200 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_BLINK,210)),7)				
					AAText(x, y + 120 * MenuScale, scpLang_GetPhrase("menu.sprint"))
					InputBox(x + 200 * MenuScale, y + 120 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_SPRINT,210)),8)
					AAText(x, y + 140 * MenuScale, scpLang_GetPhrase("menu.inventory"))
					InputBox(x + 200 * MenuScale, y + 140 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_INV,210)),9)
					AAText(x, y + 160 * MenuScale, scpLang_GetPhrase("menu.crouch"))
					InputBox(x + 200 * MenuScale, y + 160 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_CROUCH,210)),10)
					AAText(x, y + 180 * MenuScale, scpLang_GetPhrase("menu.quicksave"))
					InputBox(x + 200 * MenuScale, y + 180 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_SAVE,210)),11)
					If CanOpenConsole	
					    AAText(x, y + 200 * MenuScale, scpLang_GetPhrase("menu.console"))
					    InputBox(x + 200 * MenuScale, y + 200 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_CONSOLE,210)),12)
					EndIf
					AAText(x, y + 220 * MenuScale, scpLang_GetPhrase("menu.screenshot"))
					InputBox(x + 200 * MenuScale, y + 220 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_SCREENSHOT,210)),13)
					
					If MouseOn(x,y,300*MenuScale,240*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"controls")
					EndIf
					
					For i = 0 To 227
						If KeyHit(i) Then key = i : Exit
					Next
					If key <> 0 Then
						Select SelectedInputBox
							Case 3
								KEY_LEFT = key
							Case 4
								KEY_RIGHT = key
							Case 5
								KEY_UP = key
							Case 6
								KEY_DOWN = key
							Case 7
								KEY_BLINK = key
							Case 8
								KEY_SPRINT = key
							Case 9
								KEY_INV = key
							Case 10
								KEY_CROUCH = key
							Case 11
								KEY_SAVE = key
							Case 12
								KEY_CONSOLE = key
							Case 13
							    KEY_SCREENSHOT = key
						End Select
						SelectedInputBox = 0
					EndIf
					;[End Block]
				Case 4 ;Advanced
					AASetFont fo\Font[0]
					;[Block]
					y = y + 50*MenuScale
					
					Color 255,255,255				
					AAText(x, y, scpLang_GetPhrase("menu.hud"))	
					HUDenabled = DrawTick(x + 270 * MenuScale, y + MenuScale, HUDenabled)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"hud")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.enableconsole"))
					CanOpenConsole = DrawTick(x +270 * MenuScale, y + MenuScale, CanOpenConsole)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"consoleenable")
					EndIf
					
					y = y + 30*MenuScale
					
					If CanOpenConsole
					    Color 255,255,255
					    AAText(x, y, scpLang_GetPhrase("menu.consoleonerror"))
					    ConsoleOpening = DrawTick(x + 270 * MenuScale, y + MenuScale, ConsoleOpening)
					    If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						    DrawOptionsTooltip(tx,ty,tw,th,"consoleerror")
					    EndIf
					Else
					    ConsoleOpening = 0
					EndIf
					
					y = y + 30*MenuScale
					
					If CanOpenConsole
					    Color 255,255,255
					    AAText(x, y, scpLang_GetPhrase("menu.consoleversion"))
					    ConsoleVersion = DrawTick(x + 270 * MenuScale, y + MenuScale, ConsoleVersion)
					    If ConsoleVersion = 1 Then
					        AAText(x + 310 * MenuScale, y, scpLang_GetPhrase("menu.consolenew"))
					    Else
					        AAText(x + 310 * MenuScale, y, scpLang_GetPhrase("menu.consoleold"))
					    EndIf    
					    If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						    DrawOptionsTooltip(tx,ty,tw,th,"consoleversion")
					    EndIf
					EndIf
					
					y = y + 50*MenuScale
					
					; Color 255,255,255
					; AAText(x, y, "Achievement popups:")
					; AchvMSGenabled% = DrawTick(x + 270 * MenuScale, y, AchvMSGenabled%)
					; If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
					; 	DrawOptionsTooltip(tx,ty,tw,th,"achpopup")
					; EndIf
					
					y = y + 50*MenuScale
					
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.showfps"))
					ShowFPS% = DrawTick(x + 270 * MenuScale, y, ShowFPS%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"showfps")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.framelimit"))
					
					Color 255,255,255
					If DrawTick(x + 270 * MenuScale, y, CurrFrameLimit > 0.0) Then
						;CurrFrameLimit# = (SlideBar(x + 150*MenuScale, y+30*MenuScale, 100*MenuScale, CurrFrameLimit#*50.0)/50.0)
						;CurrFrameLimit = Max(CurrFrameLimit, 0.1)
						;Framelimit% = CurrFrameLimit#*100.0
						CurrFrameLimit# = (SlideBar(x + 150*MenuScale, y+30*MenuScale, 100*MenuScale, CurrFrameLimit#*99.0)/99.0)
						CurrFrameLimit# = Max(CurrFrameLimit, 0.01)
						Framelimit% = 19+(CurrFrameLimit*100.0)
						Color 255,255,0
						AAText(x + 5 * MenuScale, y + 25 * MenuScale, Framelimit%+" FPS")
					Else
						CurrFrameLimit# = 0.0
						Framelimit = 0
					EndIf
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"framelimit",Framelimit)
					EndIf
					If MouseOn(x+150*MenuScale,y+30*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"framelimit",Framelimit)
					EndIf
					
					y = y + 80*MenuScale
					
					Color 255,255,255
					AAText(x, y, scpLang_GetPhrase("menu.antialiasedtext"))
					AATextEnable% = DrawTick(x + 270 * MenuScale, y + MenuScale, AATextEnable%)
					If AATextEnable_Prev% <> AATextEnable
						For font.AAFont = Each AAFont
							FreeFont font\lowResFont%
							If (Not AATextEnable)
								FreeTexture font\texture
								FreeImage font\backup
							EndIf
							Delete font
						Next
						If (Not AATextEnable) Then
							FreeEntity AATextCam
							;For i%=0 To 149
							;	FreeEntity AATextSprite[i]
							;Next
						EndIf
						InitAAFont()
						fo\Font[0] = AALoadFont("GFX\font\cour\Courier New.ttf", Int(18 * (GraphicHeight / 1024.0)), 0,0,0)
						fo\Font[1] = AALoadFont("GFX\font\courbd\Courier New.ttf", Int(58 * (GraphicHeight / 1024.0)), 0,0,0)
						fo\Font[2] = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(22 * (GraphicHeight / 1024.0)), 0,0,0)
						fo\Font[3] = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(58 * (GraphicHeight / 1024.0)), 0,0,0) ;60
						fo\Font[4] = AALoadFont("GFX\font\Journal\Journal.ttf", Int(58 * (GraphicHeight / 1024.0)), 0,0,0)
						fo\ConsoleFont% = AALoadFont("Blitz", Int(22 * (GraphicHeight / 1024.0)), 0,0,0,1)
						;ReloadAAFont()
						AATextEnable_Prev% = AATextEnable
					EndIf
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"antialiastext")
					EndIf
					;[End Block]
			End Select
		ElseIf AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG > 0 And KillTimer >= 0
			Local QuitButton% = 60 
			If SelectedDifficulty\saveType = SAVEONQUIT Or SelectedDifficulty\saveType = SAVEANYWHERE Then
				Local RN$ = PlayerRoom\RoomTemplate\Name$
				Local AbleToSave% = True
				If RN$ = "room173intro" Or RN$ = "gateb" Or RN$ = "gatea" Then AbleToSave = False
				If (Not CanSave) Then AbleToSave = False
				If AbleToSave
					QuitButton = 140
					If DrawButton(x, y + 60*MenuScale, 430*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.saveandquit")) Then
						DropSpeed = 0
						SaveGame(SavePath + CurrSave + "\")
						NullGame()
						ms\MenuOpen = False
						ms\MainMenuOpen = True
						ms\MainMenuTab = 0
						CurrSave = ""
						FlushKeys()
					EndIf
				EndIf
			EndIf
			
			If DrawButton(x, y + QuitButton*MenuScale, 430*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.quit3")) Then
				NullGame()
				ms\MenuOpen = False
				ms\MainMenuOpen = True
				ms\MainMenuTab = 0
				CurrSave = ""
				FlushKeys()
			EndIf
			
			If DrawButton(x+101*MenuScale, y + 344*MenuScale, 230*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.back2")) Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMSG = 0
				MouseHit1 = False
			EndIf
		Else
			If DrawButton(x+101*MenuScale, y + 344*MenuScale, 230*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.back2")) Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMSG = 0
				MouseHit1 = False
			EndIf
			
			If AchievementsMenu>0 Then
				If AchievementsMenu <= Floor(Float(MAXACHIEVEMENTS-1)/12.0) Then 
					If DrawButton(x+341*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, ">") Then
						AchievementsMenu = AchievementsMenu+1
					EndIf
				EndIf
				If AchievementsMenu > 1 Then
					If DrawButton(x+41*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, "<") Then
						AchievementsMenu = AchievementsMenu-1
					EndIf
				EndIf
				
				For i=0 To 11
					If i+((AchievementsMenu-1)*12)<MAXACHIEVEMENTS Then
						DrawAchvIMG(AchvXIMG,y+((i/4)*120*MenuScale),i+((AchievementsMenu-1)*12))
					Else
						Exit
					EndIf
				Next
				
				For i=0 To 11
					If i+((AchievementsMenu-1)*12)<MAXACHIEVEMENTS Then
						If MouseOn(AchvXIMG+((i Mod 4)*SeparationConst),y+((i/4)*120*MenuScale),64*scale,64*scale) Then
							AchievementTooltip(i+((AchievementsMenu-1)*12))
							Exit
						EndIf
					Else
						Exit
					EndIf
				Next
				
			EndIf
		EndIf
		
		y = y+10
		
		If AchievementsMenu<=0 And OptionsMenu<=0 And QuitMSG<=0 Then
			If KillTimer >= 0 Then	
				
				y = y+ 72*MenuScale
				
				If DrawButton(x, y, 430*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.resume"), True, True) Then
					ms\MenuOpen = False
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
					mouse_x_speed_1=0.0 : mouse_y_speed_1=0.0
					mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
				EndIf
				
				y = y + 75*MenuScale
				If (Not SelectedDifficulty\permaDeath) Then
					If GameSaved Then
						If DrawButton(x, y, 430*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.loadgame2")) Then
							DrawLoading(0)
							
							ms\MenuOpen = False
							LoadGameQuick(SavePath + CurrSave + "\")
							
							MoveMouse viewport_center_x,viewport_center_y
							AASetFont fo\Font[0]
							HidePointer ()
							
							FlushKeys()
							FlushMouse()
							Playable=True
							
							UpdateRooms()
							
							For r.Rooms = Each Rooms
								x = Abs(EntityX(Collider) - EntityX(r\obj))
								z = Abs(EntityZ(Collider) - EntityZ(r\obj))
								
								If x < 12.0 And z < 12.0 Then
									MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = Max(MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)), 1)
									If x < 4.0 And z < 4.0 Then
										If Abs(EntityY(Collider) - EntityY(r\obj)) < 1.5 Then PlayerRoom = r
										MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = 1
									EndIf
								End If
							Next
							
							DrawLoading(100)
							
							DropSpeed=0
							
							UpdateWorld 0.0
							
							fs\PrevTime = MilliSecs()
							fs\FPSfactor[0] = 0
							
							ResetInput()
						EndIf
					Else
						DrawFrame(x,y,430*MenuScale, 60*MenuScale)
						Color (100, 100, 100)
						AASetFont fo\Font[1]
						AAText(x + (430*MenuScale) / 2, y + (60*MenuScale) / 2, scpLang_GetPhrase("menu.loadgame2"), True, True)
					EndIf
					y = y + 75*MenuScale
			EndIf
				
				If DrawButton(x, y, 430*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.achievements2")) Then AchievementsMenu = 1
				y = y + 75*MenuScale

				If SelectedDifficulty\menu Then
					If DrawButton(x, y, 430*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.options2")) Then OptionsMenu = 1
					y = y + 75*MenuScale
				EndIf				
			Else							
				y = y+104*MenuScale
				If GameSaved And (Not SelectedDifficulty\permaDeath) Then
					If DrawButton(x, y, 430*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.loadgame2")) Then
						DrawLoading(0)
						
						ms\MenuOpen = False
						LoadGameQuick(SavePath + CurrSave + "\")
						
						MoveMouse viewport_center_x,viewport_center_y
						AASetFont fo\Font[0]
						HidePointer ()
						
						FlushKeys()
						FlushMouse()
						Playable=True
						
						UpdateRooms()
						
						For r.Rooms = Each Rooms
							x = Abs(EntityX(Collider) - EntityX(r\obj))
							z = Abs(EntityZ(Collider) - EntityZ(r\obj))
							
							If x < 12.0 And z < 12.0 Then
								MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = Max(MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)), 1)
								If x < 4.0 And z < 4.0 Then
									If Abs(EntityY(Collider) - EntityY(r\obj)) < 1.5 Then PlayerRoom = r
									MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = 1
								EndIf
							End If
						Next
						
						DrawLoading(100)
						
						DropSpeed=0
						
						UpdateWorld 0.0
						
						fs\PrevTime = MilliSecs()
						fs\FPSfactor[0] = 0
						
						ResetInput()
					EndIf
				Else
					DrawButton(x, y, 430*MenuScale, 60*MenuScale, "")
					Color 50,50,50
					AAText(x + 185*MenuScale, y + 30*MenuScale, scpLang_GetPhrase("menu.loadgame2"), True, True)
				EndIf
				If DrawButton(x, y + 80*MenuScale, 430*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.quit4")) Then
					NullGame()
					ms\MenuOpen = False
					ms\MainMenuOpen = True
					ms\MainMenuTab = 0
					CurrSave = ""
					FlushKeys()
				EndIf
				y= y + 80*MenuScale
			EndIf
			
			If KillTimer >= 0 And (Not ms\MainMenuOpen)
				If DrawButton(x, y, 430*MenuScale, 60*MenuScale, scpLang_GetPhrase("menu.quit3")) Then
					QuitMSG = 1
				EndIf
			EndIf
			
			AASetFont fo\Font[0]
			If KillTimer < 0 Then RowText(DeathMSG$, x, y + 80*MenuScale, 430*MenuScale, 600*MenuScale)
		EndIf
		
		If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
		
	End If
	
	AASetFont fo\Font[0]
End Function

Function MouseOn%(x%, y%, width%, height%)
	If ScaledMouseX() > x And ScaledMouseX() < x + width Then
		If ScaledMouseY() > y And ScaledMouseY() < y + height Then
			Return True
		End If
	End If
	Return False
End Function

;{~--<MOD>--~}

Type Objects
    Field NPCModelID[MaxNPCModelIDAmount-1]
    Field OBJTunnelID[MaxOBJTunnelIDAmount-1]
    Field MonitorID[MaxMonitorIDAmount-1]
    Field DoorID[MaxDoorIDAmount-1]
    Field ButtonID[MaxButtonIDAmount-1]
    Field LeverID[MaxLeverIDAmount-1]
    Field CamID[MaxCamIDAmount-1]
    Field OtherModelsID[MaxOtherModelsIDAmount-1]
End Type

;{~--<END>--~}

;--------------------------------------- music & sounds ----------------------------------------------

Include "Source Code\Sound_System.bb"

;--------------------------------------- entities ----------------------------------------------------

Function LoadEntities()
	DrawLoading(0)
	
	;DeInit3DMenu()

	Local i%
	Local o.Objects = New Objects
	
	For i = 0 To 9
		TempSounds[i] = 0
	Next
	
	PauseMenuIMG% = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\menu\pausemenu.png"))
	MaskImage PauseMenuIMG, 255,255,0
	ScaleImage PauseMenuIMG,MenuScale,MenuScale
	
	SprintIcon% = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"sprinticon.png"))
	BlinkIcon% = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"blinkicon.png"))
	CrouchIcon% = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"sneakicon.png"))
	HandIcon% = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"handsymbol.png"))
	HandIcon2% = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"handsymbol2.png"))

	StaminaMeterIMG% = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"staminameter.png"))

	KeypadHUD =  LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"keypadhud.png"))
	MaskImage(KeypadHUD, 255,0,255)

	Panel294 = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\"+"294panel.png"))
	MaskImage(Panel294, 255,0,255)
	
	Brightness% = GetINIFloat(OptionFile, "game", "brightness")
	CameraFogNear# = GetINIFloat(OptionFile, "game", "camera fog near")
	CameraFogFar# = GetINIFloat(OptionFile, "game", "camera fog far")
	StoredCameraFogFar# = CameraFogFar
	
	;TextureLodBias
	
	AmbientLightRoomTex% = CreateTexture(2,2,257)
	TextureBlend AmbientLightRoomTex,5
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
	AmbientLightRoomVal = 0
	
	SoundEmitter = CreatePivot()
	
	Camera = CreateCamera()
	CameraViewport Camera,0,0,GraphicWidth,GraphicHeight
	CameraRange(Camera, 0.01, CameraFogFar)
	CameraFogMode(Camera, 1)
	CameraFogRange(Camera, CameraFogNear, CameraFogFar)
	CameraFogColor(Camera, FogR, FogG, FogB)
	AmbientLight Brightness, Brightness, Brightness
	
	ScreenTexs[0] = CreateTexture(512, 512, 1+256)
	ScreenTexs[1] = CreateTexture(512, 512, 1+256)
	
	CreateBlurImage()
	CameraProjMode ark_blur_cam,0
	;Listener = CreateListener(Camera)
	
	;[OVERLAYS]
	
	at\OverlayTextureID[0] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"fog.png"), 1) ;FOG
	at\OverlayID[0] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[0], Max(GraphicWidth / 1240.0, 1.0), Max(GraphicHeight / 960.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[0], at\OverlayTextureID[0])
	EntityBlend (at\OverlayID[0], 2)
	EntityOrder at\OverlayID[0], -1000
	MoveEntity(at\OverlayID[0], 0, 0, 1.0)
	
	at\OverlayTextureID[1] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"GasmaskOverlay.png"), 1) ;GASMASK
	at\OverlayID[1] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[1], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[1], at\OverlayTextureID[1])
	EntityBlend (at\OverlayID[1], 2)
	EntityFX(at\OverlayID[1], 1)
	EntityOrder at\OverlayID[1], -1003
	MoveEntity(at\OverlayID[1], 0, 0, 1.0)
	HideEntity(at\OverlayID[1])
	
	at\OverlayTextureID[2] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"InfectOverlay.png"), 1) ;INFECTION
	at\OverlayID[2] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[2], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[2], at\OverlayTextureID[2])
	EntityBlend (at\OverlayID[2], 3)
	EntityFX(at\OverlayID[2], 1)
	EntityOrder at\OverlayID[2], -1003
	MoveEntity(at\OverlayID[2], 0, 0, 1.0)
	HideEntity(at\OverlayID[2])
	
	at\OverlayTextureID[3] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"NightVisionOverlay.png"), 1) ;NVG
	at\OverlayID[3] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[3], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[3], at\OverlayTextureID[3])
	EntityBlend (at\OverlayID[3], 2)
	EntityFX(at\OverlayID[3], 1)
	EntityOrder at\OverlayID[3], -1003
	MoveEntity(at\OverlayID[3], 0, 0, 1.0)
	HideEntity(at\OverlayID[3])
	
	at\OverlayTextureID[4] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"fogNV.png"), 1) ;NVG BLINK
	at\OverlayID[4] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[4], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityColor(at\OverlayID[4],0,0,0)
	EntityFX(at\OverlayID[4], 1)
	EntityOrder at\OverlayID[4], -1005
	MoveEntity(at\OverlayID[4], 0, 0, 1.0)
	HideEntity(at\OverlayID[4])
	
	at\OverlayTextureID[5] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"BloodOverlay.png"), 1) ;BLOOD
	at\OverlayID[5] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[5], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[5], at\OverlayTextureID[5])
	EntityBlend (at\OverlayID[5], 2)
	EntityFX(at\OverlayID[5], 1)
	EntityOrder at\OverlayID[5], -1003
	MoveEntity(at\OverlayID[5], 0, 0, 1.0)
	HideEntity(at\OverlayID[5])
	
	at\OverlayTextureID[6] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"HelmetOverlay.png"), 1) ;HELMET
	at\OverlayID[6] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[6], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[6], at\OverlayTextureID[6])
	EntityBlend (at\OverlayID[6], 2)
	EntityFX(at\OverlayID[6], 1)
	EntityOrder at\OverlayID[6], -1003
	MoveEntity(at\OverlayID[6], 0, 0, 1.0)
	HideEntity(at\OverlayID[6])
	
	at\OverlayTextureID[7] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"447Overlay.png"), 1) ;SCP-447
	at\OverlayID[7] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[7], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[7], at\OverlayTextureID[7])
	EntityBlend (at\OverlayID[7], 3)
	EntityFX(at\OverlayID[7], 1)
	EntityOrder at\OverlayID[7], -1003
	MoveEntity(at\OverlayID[7], 0, 0, 1.0)
	EntityAlpha (at\OverlayID[7], 255.0)
	HideEntity(at\OverlayID[7])
	
	at\OverlayTextureID[8] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"FireOverlay.png"), 1) ;FIRE
	at\OverlayID[8] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[8], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[8], at\OverlayTextureID[8])
	EntityBlend (at\OverlayID[8], 2)
	EntityFX(at\OverlayID[8], 1)
	EntityOrder at\OverlayID[8], -1003
	MoveEntity(at\OverlayID[8], 0, 0, 1.0)
	HideEntity(at\OverlayID[8])
	
	at\OverlayTextureID[9] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"map\red_ice.png"),1) ;RED ICE
	at\OverlayID[9] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[9], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[9], at\OverlayTextureID[9])
	EntityBlend (at\OverlayID[9], 3)
	EntityFX(at\OverlayID[9], 1)
	EntityOrder at\OverlayID[9], -1001
	MoveEntity(at\OverlayID[9], 0, 0, 1.0)
	HideEntity(at\OverlayID[9])
	
	at\OverlayTextureID[10] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"CrystalOverlay.png"), 1) ;CRYSTALLIZATION
	at\OverlayID[10] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[10], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[10], at\OverlayTextureID[10])
	EntityBlend (at\OverlayID[10], 3)
	EntityFX(at\OverlayID[10], 1)
	EntityOrder at\OverlayID[10], -1001
	MoveEntity(at\OverlayID[10], 0, 0, 1.0)
	HideEntity(at\OverlayID[10])
	
	at\OverlayTextureID[11] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"GlassesOverlay.png"),1) ;3-D GLASSES
	at\OverlayID[11] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[11], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[11], at\OverlayTextureID[11])
	EntityBlend (at\OverlayID[11], 2)
	EntityFX(at\OverlayID[11], 1)
	EntityOrder at\OverlayID[11], -1003
	MoveEntity(at\OverlayID[11], 0, 0, 1.0)
	HideEntity(at\OverlayID[11])
	
	at\OverlayTextureID[12] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"215Overlay.png"), 1) ;STATIC
	at\OverlayID[12] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[12], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[12], at\OverlayTextureID[12])
	EntityBlend (at\OverlayID[12], 3)
	EntityFX(at\OverlayID[12], 1)
	EntityOrder at\OverlayID[12], -1001
	MoveEntity(at\OverlayID[12], 0, 0, 1.0)
	HideEntity(at\OverlayID[12])
	
	at\OverlayTextureID[13] = CreateTexture(1024, 1024, 1 + 2) ;DARK
	SetBuffer TextureBuffer(at\OverlayTextureID[13])
	Cls
	SetBuffer BackBuffer()
	at\OverlayID[13] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[13], Max(GraphicWidth / 1240.0, 1.0), Max(GraphicHeight / 960.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[13], at\OverlayTextureID[13])
	EntityBlend (at\OverlayID[13], 1)
	EntityOrder at\OverlayID[13], -1002
	MoveEntity(at\OverlayID[13], 0, 0, 1.0)
	EntityAlpha at\OverlayID[13], 0.0
	
	at\OverlayTextureID[14] = CreateTexture(1024, 1024, 1 + 2+256) ;LIGHT
	SetBuffer TextureBuffer(at\OverlayTextureID[14])
	ClsColor 255, 255, 255
	Cls
	ClsColor 0, 0, 0
	SetBuffer BackBuffer()
	at\OverlayID[14] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[14], Max(GraphicWidth / 1240.0, 1.0), Max(GraphicHeight / 960.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[14], at\OverlayTextureID[14])
	EntityBlend (at\OverlayID[14], 1)
	EntityOrder at\OverlayID[14], -1002
	MoveEntity(at\OverlayID[14], 0, 0, 1.0)
	HideEntity at\OverlayID[14]
	
	at\OverlayTextureID[21] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"GasmaskBlurOverlay.png"), 1) ;GASMASK BLUR
	at\OverlayID[15] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[15], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[15], at\OverlayTextureID[21])
	EntityBlend (at\OverlayID[15], 3)
	EntityFX(at\OverlayID[15], 1)
	EntityOrder at\OverlayID[15], -1000
	MoveEntity(at\OverlayID[15], 0, 0, 1.0)
	HideEntity(at\OverlayID[15])
	
	at\OverlayTextureID[22] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"HazmatOverlay.png"), 1) ;HAZMAT SUIT
	at\OverlayID[16] = CreateSprite(ark_blur_cam)
	ScaleSprite(at\OverlayID[16], Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(at\OverlayID[16], at\OverlayTextureID[22])
	EntityBlend (at\OverlayID[16], 2)
	EntityFX(at\OverlayID[16], 1)
	EntityOrder at\OverlayID[16], -1003
	MoveEntity(at\OverlayID[16], 0, 0, 1.0)
	HideEntity(at\OverlayID[16])
	
	DrawLoading(5)
				
	at\OverlayTextureID[15] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"map\tesla.png"), 1+2) ;TESLA
	
	at\OverlayTextureID[16] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"monitoroverlay.png")) ;MONITOR
	at\OverlayTextureID[17] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"map\LockdownScreen2.png"));LOCKDOWN #1
	at\OverlayTextureID[18] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"map\LockdownScreen.png")) ;LOCKDOWN #2
	at\OverlayTextureID[19] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"map\LockdownScreen3.png")) ;LOCKDOWN #3
	at\OverlayTextureID[20] = CreateTexture(1,1) ;MONITOR OFF
	SetBuffer TextureBuffer(at\OverlayTextureID[20])
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
		
	Collider = CreatePivot()
	EntityRadius Collider, 0.15, 0.30
	EntityPickMode(Collider, 1)
	EntityType Collider, HIT_PLAYER
	
	Head = CreatePivot()
	EntityRadius Head, 0.15
	EntityType Head, HIT_PLAYER
	
	;[NPCs]
	
	o\NPCModelID[0] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_173.b3d")) ;SCP-173

    o\NPCModelID[1] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_106.b3d")) ;SCP-106
	
	o\NPCModelID[2] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"guard.b3d")) ;Guard

    o\NPCModelID[3] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"class_d.b3d")) ;Class-D
	
	o\NPCModelID[4] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_372.b3d")) ;SCP-372
	
	o\NPCModelID[5] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"apache.b3d")) ;Apache Helicopter
	
	o\NPCModelID[6] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"apache_rotor.b3d")) ;Helicopter's Rotor #1
	
	o\NPCModelID[7] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"MTF.b3d")) ;MTF
	
	o\NPCModelID[8] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_096.b3d")) ;SCP-096
	
	o\NPCModelID[9] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_049.b3d")) ;SCP-049
	
	o\NPCModelID[10] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_049_2.b3d")) ;SCP-049-2
	
	o\NPCModelID[11] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_513_1.b3d")) ;SCP-513-1
	
	o\NPCModelID[12] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_035_tentacle.b3d")) ;SCP-035's Tentacle
	
	o\NPCModelID[13] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_860_2.b3d")) ;SCP-860-2
	
	o\NPCModelID[14] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_939.b3d")) ;SCP-939
	
	o\NPCModelID[15] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_066.b3d")) ;SCP-066
	
	o\NPCModelID[16] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_966.b3d")) ;SCP-966
	
	o\NPCModelID[17] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_1048_a.b3d")) ;SCP-1048-A
	
	o\NPCModelID[18] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_1499_1.b3d")) ;SCP-1499-1
	
	o\NPCModelID[19] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_008_1.b3d")) ;SCP-008-1
	
	o\NPCModelID[20] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"clerk.b3d")) ;clerk
	
	o\NPCModelID[21] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_008_2.b3d")) ;SCP-008-2                     
	
	o\NPCModelID[22] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_650.b3d")) ;SCP-650                                   
              
	o\NPCModelID[23] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_049_3.b3d")) ;SCP-049-3                
	
	o\NPCModelID[24] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_178_1.b3d")) ;SCP-178-1
	
	o\NPCModelID[25] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"MTF2.b3d")) ;MTF2
	
	o\NPCModelID[26] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"apache_rotor(2).b3d")) ;Helicopter's Rotor #2
	
	o\NPCModelID[27] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"nazi_officer.b3d")) ;Nazi Officer
	
	o\NPCModelID[28] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_1048.b3d")) ;SCP-1048 #1
	
	o\NPCModelID[29] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_1048_p.b3d")) ;SCP-1048 #2
	
	o\NPCModelID[30] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"duck.b3d")) ;Anomalous Duck
	
	o\NPCModelID[31] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"CI.b3d")) ;CI
	
	o\NPCModelID[32] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_035.b3d")) ;SCP-035
	
	o\NPCModelID[33] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_682_arm.b3d")) ;SCP-682's Arm
	
	o\NPCModelID[34] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_173_box.b3d")) ;SCP-173's Box
	
	o\NPCModelID[35] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_205_demon.b3d")) ;SCP-205's Demon #1
	
	o\NPCModelID[36] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_205_demon(2).b3d")) ;SCP-205's Demon#2
	
	o\NPCModelID[37] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_205_demon(3).b3d")) ;SCP-205's Demon#3
	
	o\NPCModelID[38] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_205_woman.b3d")) ;SCP-205's Woman
	
	o\NPCModelID[39] = LoadAnimMesh_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"vehicle.b3d")) ;Vehicle
		
    For i = 0 To MaxNPCModelIDAmount-1
        HideEntity o\NPCModelID[i]
    Next
	
	DrawLoading(10)
	
	;[DOORS]
	
	o\DoorID[0] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"door01.x")) ;door
	
	o\DoorID[1] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"doorframe.x")) ;frame #1
	
	o\DoorID[2] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"heavydoor1.x")) ;heavy door #1
	
	o\DoorID[3] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"heavydoor2.x")) ;heavy door #2
	
	o\DoorID[4] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"doorcoll.x")) ;collider
		
	o\DoorID[5] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"ContDoorLeft.x")) ;big door #1

	o\DoorID[6] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"ContDoorRight.x")) ;big door #2
	
	o\DoorID[7] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"elevatordoor.b3d")) ;elevator door
	
	o\DoorID[8] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"forest\door_frame.b3d")) ;frame #2
	
	o\DoorID[9] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"forest\door.b3d")) ;wooden door
	
	o\DoorID[10] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"doorhit.b3d")) ;door's hitbox

	For i = 0 To MaxDoorIDAmount-1
	    HideEntity o\DoorID[i]
	Next
	
	;[LEVERS]
	
	o\LeverID[0] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"leverbase.x")) ;lever base

	o\LeverID[1] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"leverhandle.x")) ;lever handle

	For i = 0 To MaxLeverIDAmount-1
	    HideEntity o\LeverID[i]
	Next

    ;[BUTTONS]

	o\ButtonID[0] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"Button.b3d")) ;button
	
	o\ButtonID[1] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"ButtonKeycard.b3d")) ;keycard button
	
	o\ButtonID[2] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"ButtonCode.b3d")) ;code button
		
	o\ButtonID[3] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"ButtonScanner.b3d")) ;scanner button
	
	o\ButtonID[4] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"ButtonElevator.b3d")) ;elevator button
	
	For i=0 To MaxButtonIDAmount-1
        HideEntity o\ButtonID[i]
    Next	

    ;[OTHER MODELS]
	
	o\OtherModelsID[0] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\items\"+"cup_liquid.x")) ;cup's liquid
	
	o\OtherModelsID[1] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\"+"lightcone.b3d")) ;light cone
	
	For i=0 To MaxOtherModelsIDAmount-1
        HideEntity o\OtherModelsID[i]
    Next	
	
	DrawLoading(15)
	
	at\LightSpriteTextureID[0] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"light.png"), 1)
	at\LightSpriteTextureID[1] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"light(2).png"), 1)
	at\LightSpriteTextureID[2] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"lightsprite.png"),1)
	
	at\OtherTextureID[3] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"079Overlays\079Overlay.png"))
	For i = 4 To 9
	    at\OtherTextureID[i] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"079Overlays\079Overlay(" + (i - 2) + ").png"))
	Next
	
	at\OtherTextureID[10] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"895Overlays\895Overlay.png"))
	For i = 11 To 20
		at\OtherTextureID[i] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"895Overlays\895Overlay(" + (i - 9) + ").png"))
	Next
		
	DrawLoading(20)
	
	at\DecalTextureID[0] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decal.png"), 1 + 2)

	For i = 1 To 6
		at\DecalTextureID[i] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decal(" + (i + 1) + ").png"), 1 + 2)
	Next
	
	at\DecalTextureID[7] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decal447.png"), 1 + 2)
	
	at\DecalTextureID[8] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decalpd.png"), 1 + 2)
	For i = 9 To 12
		at\DecalTextureID[i] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decalpd("+(i-7)+").png"), 1 + 2)	
	Next
	
	at\DecalTextureID[13] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"bullethole.png"), 1 + 2)
	at\DecalTextureID[14] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"bullethole(2).png"), 1 + 2)		
	
	at\DecalTextureID[15] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"blooddrop.png"), 1 + 2)
	at\DecalTextureID[16] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"blooddrop(2).png"), 1 + 2)
		
	at\DecalTextureID[17] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decal(8).png"), 1 + 2)
		
	at\DecalTextureID[18] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decalpd(6).dc"), 1 + 2)
		
	at\DecalTextureID[19] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decal409.png"), 1 + 2)
	at\DecalTextureID[20] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decal427.png"), 1 + 2)
	at\DecalTextureID[21] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decal008.png"), 1 + 2)
	at\DecalTextureID[22] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decal1079.png"), 1 + 2)
	at\DecalTextureID[23] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"decal1079(2).png"), 1 + 2)

	DrawLoading(25)
	
	;[MONITORS]
	
	o\MonitorID[0] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"monitor.b3d")) ;monitor #1

	o\MonitorID[1] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"monitor_checkpoint.b3d")) ;monitor #2

	o\MonitorID[2] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"monitor_checkpoint.b3d")) ;monitor #3

    For i=0 To MaxMonitorIDAmount-1
        HideEntity o\MonitorID[i]
    Next
	
	;[CAMS]
	
	o\CamID[0] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"cambase.x")) ;cam base

	o\CamID[1] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"CamHead.b3d")) ;cam head

	For i=0 To MaxCamIDAmount-1
        HideEntity o\CamID[i]
    Next

	For i = 2 To CountSurfaces(o\MonitorID[1])
		sf = GetSurface(o\MonitorID[1],i)
		b = GetSurfaceBrush(sf)
		If b<>0 Then
			t1 = GetBrushTexture(b,0)
			If t1<>0 Then
				name$ = StripPath(TextureName(t1))
				If Lower(name) <> "monitoroverlay.png"
					BrushTexture b, at\OverlayTextureID[20], 0, 0
					PaintSurface sf,b
				EndIf
				If name<>"" Then FreeTexture t1
			EndIf
			FreeBrush b
		EndIf
	Next
	For i = 2 To CountSurfaces(o\MonitorID[2])
		sf = GetSurface(o\MonitorID[2],i)
		b = GetSurfaceBrush(sf)
		If b<>0 Then
			t1 = GetBrushTexture(b,0)
			If t1<>0 Then
				name$ = StripPath(TextureName(t1))
				If Lower(name) <> "monitoroverlay.png"
					BrushTexture b, at\OverlayTextureID[20], 0, 0
					PaintSurface sf,b
				EndIf
				If name<>"" Then FreeTexture t1
			EndIf
			FreeBrush b
		EndIf
	Next
	
	UserTrackMusicAmount% = 0
	If EnableUserTracks Then
		Local dirPath$ = scpModding_ProcessFilePath$("SFX\"+"Radio\UserTracks\")
		If FileType(dirPath)<>2 Then
			CreateDir(dirPath)
		EndIf
		
		Local Dir% = ReadDir(scpModding_ProcessFilePath$("SFX\Radio\UserTracks"))
		Repeat
			file$=NextFile(Dir)
			If file$="" Then Exit
			If FileType(scpModding_ProcessFilePath$("SFX\Radio\UserTracks"+file$)) = 1 Then
				test = LoadSound(scpModding_ProcessFilePath$("SFX\Radio\UserTracks"+file$))
				If test<>0
					UserTrackName$(UserTrackMusicAmount%) = file$
					UserTrackMusicAmount% = UserTrackMusicAmount% + 1
				EndIf
				FreeSound test
			EndIf
		Forever
		CloseDir Dir
	EndIf
	If EnableUserTracks Then DebugLog scpLang_GetPhrase("menu.trackfound")+": "+UserTrackMusicAmount
	
	InitItemTemplates()
	
	at\ParticleTextureID[0] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"smoke.png"), 1 + 2)
	at\ParticleTextureID[1] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"flash.png"), 1 + 2)
	at\ParticleTextureID[2] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"dust.png"), 1 + 2)
	at\ParticleTextureID[3] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"hg.pt"), 1 + 2)
	at\ParticleTextureID[4] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\map\"+"sun.png"), 1 + 2)
	at\ParticleTextureID[5] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"bloodsprite.png"), 1 + 2)
	at\ParticleTextureID[6] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"smoke2.png"), 1 + 2)
	at\ParticleTextureID[7] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"spark.png"), 1 + 2)
	at\ParticleTextureID[8] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"particle.png"), 1 + 2)
	at\ParticleTextureID[9] = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\"+"fire_particle.png"), 1 + 2)
	
	SetChunkDataValues()
	
	For i = 1 To MaxDTextures
		DTextures[i] = CopyEntity(o\NPCModelID[3])
		HideEntity DTextures[i]
	Next
	;Gonzales
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"Gonzales.png"))
	EntityTexture DTextures[1],tex
	FreeTexture tex
	;SCP-970 Sorpse
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"corpse.png"))
	EntityTexture DTextures[2],tex
	FreeTexture tex
	;1st Scientist
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scientist.png"))
	EntityTexture DTextures[3],tex
	FreeTexture tex
	;Franklin
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"Franklin.png"))
	EntityTexture DTextures[4],tex
	FreeTexture tex
	;Janitor #1
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"janitor.png"))
	EntityTexture DTextures[5],tex
	FreeTexture tex
	;2nd Class-D
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"class_d(2).png"))
	EntityTexture DTextures[6],tex
	FreeTexture tex
	;SCP-035 Victim
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_035_victim.png"))
	EntityTexture DTextures[7],tex
	FreeTexture tex
	;SCP-106's Victim
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_106_victim.png"))
	EntityTexture DTextures[8],tex
	FreeTexture tex
	;1st Body
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"body.png"))
	EntityTexture DTextures[9],tex
	FreeTexture tex
	;2nd Body
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"body(2).png"))
	EntityTexture DTextures[10],tex
	FreeTexture tex
	;D-9341
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"d_9341.png"))
	EntityTexture DTextures[11],tex
	FreeTexture tex
	
	;{~--<MOD>--~}
	
	;1st Class-D Victim
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_106_victim(2).png"))
	EntityTexture DTextures[12],tex
	FreeTexture tex
	;2nd Class-D Victim
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_106_victim(3).png"))
	EntityTexture DTextures[13],tex
	FreeTexture tex
	;Janitor Victim
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_106_victim(4).png"))
	EntityTexture DTextures[14],tex
	FreeTexture tex
	;3d Body
    tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"body(3).png"))
	EntityTexture DTextures[15],tex
	FreeTexture tex
	;4th Body
    tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"body(4).png"))
	EntityTexture DTextures[16],tex
	FreeTexture tex
	;SCP-008-1's Victim
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"scp_008_victim.png"))
	EntityTexture DTextures[17],tex
	FreeTexture tex
	;Janitor #2
	tex = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\npcs\"+"janitor(2).png"))
	EntityTexture DTextures[18],tex
	FreeTexture tex
	
	;{~--<END>--~}
	
	LoadMaterials(scpModding_ProcessFilePath$("DATA\materials.ini"))
	
	o\OBJTunnelID[0] = LoadRMesh(scpModding_ProcessFilePath$("GFX\map\"+"mt1.rmesh"), Null) ;tunnel #1
			
	o\OBJTunnelID[1] = LoadRMesh(scpModding_ProcessFilePath$("GFX\map\"+"mt2.rmesh"), Null) ;tunnel #2

	o\OBJTunnelID[2] = LoadRMesh(scpModding_ProcessFilePath$("GFX\map\"+"mt2c.rmesh"), Null) ;tunnel #2 (corner)
				
	o\OBJTunnelID[3] = LoadRMesh(scpModding_ProcessFilePath$("GFX\map\"+"mt3.rmesh"), Null) ;tunnel #3
	
	o\OBJTunnelID[4] = LoadRMesh(scpModding_ProcessFilePath$("GFX\map\"+"mt4.rmesh"), Null) ;tunnel #4
				
	o\OBJTunnelID[5] = LoadRMesh(scpModding_ProcessFilePath$("GFX\map\"+"mt_elevator.rmesh"), Null) ;elevator

	o\OBJTunnelID[6] = LoadRMesh(scpModding_ProcessFilePath$("GFX\map\"+"mt_generator.rmesh"), Null) ;generator

	For i = 0 To MaxOBJTunnelIDAmount-1
        HideEntity o\OBJTunnelID[i]
    Next

	;TextureLodBias TextureBias
	TextureLodBias TextureFloat#
	;Devil Particle System
	;ParticleEffect[] numbers:
	;0 - electric spark
	;1 - smoke effect
	
	Local t0
	
	InitParticles(Camera)
	
	;Spark Effect (short)
	ParticleEffect[0] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[0], 3)
	SetTemplateInterval(ParticleEffect[0], 1)
	SetTemplateParticlesPerInterval(ParticleEffect[0], 6)
	SetTemplateEmitterLifeTime(ParticleEffect[0], 6)
	SetTemplateParticleLifeTime(ParticleEffect[0], 20, 30)
	SetTemplateTexture(ParticleEffect[0], scpModding_ProcessFilePath$("GFX\"+"Spark.png"), 2, 3)
	SetTemplateOffset(ParticleEffect[0], -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[0], -0.0375, 0.0375, -0.0375, 0.0375, -0.0375, 0.0375)
	SetTemplateAlignToFall(ParticleEffect[0], True, 45)
	SetTemplateGravity(ParticleEffect[0], 0.001)
	SetTemplateAlphaVel(ParticleEffect[0], True)
	;SetTemplateSize(ParticleEffect[0], 0.0625, 0.125, 0.7, 1)
	SetTemplateSize(ParticleEffect[0], 0.03125, 0.0625, 0.7, 1)
	SetTemplateColors(ParticleEffect[0], $0000FF, $6565FF)
	SetTemplateFloor(ParticleEffect[0], 0.0, 0.5)
	
	;Smoke effect (for some vents)
	ParticleEffect[1] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[1], 1)
	SetTemplateInterval(ParticleEffect[1], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[1], 3)
	SetTemplateParticleLifeTime(ParticleEffect[1], 30, 45)
	SetTemplateTexture(ParticleEffect[1], scpModding_ProcessFilePath$("GFX\"+"smoke2.png"), 2, 1)
	;SetTemplateOffset(ParticleEffect[1], -.3, .3, -.3, .3, -.3, .3)
	SetTemplateOffset(ParticleEffect[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
	;SetTemplateVelocity(ParticleEffect[1], -.04, .04, .1, .2, -.04, .04)
	SetTemplateVelocity(ParticleEffect[1], 0.0, 0.0, 0.02, 0.025, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[1], True)
	;SetTemplateSize(ParticleEffect[1], 3, 3, .5, 1.5)
	SetTemplateSize(ParticleEffect[1], 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[1], .01, 1.01)
	
	;Smoke effect (for decontamination gas)
	ParticleEffect[2] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[2], 1)
	SetTemplateInterval(ParticleEffect[2], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[2], 3)
	SetTemplateParticleLifeTime(ParticleEffect[2], 30, 45)
	SetTemplateTexture(ParticleEffect[2], scpModding_ProcessFilePath$("GFX\"+"smoke.png"), 2, 1)
	SetTemplateOffset(ParticleEffect[2], -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[2], -0.005, 0.005, 0.0, -0.03, -0.005, 0.005)
	SetTemplateAlphaVel(ParticleEffect[2], True)
	SetTemplateSize(ParticleEffect[2], 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[2], .01, 1.01)
	SetTemplateGravity(ParticleEffect[2], 0.005)
	t0 = CreateTemplate()
	SetTemplateEmitterBlend(t0, 1)
	SetTemplateInterval(t0, 1)
	SetTemplateEmitterLifeTime(t0, 3)
	SetTemplateParticleLifeTime(t0, 30, 45)
	SetTemplateTexture(t0, scpModding_ProcessFilePath$("GFX\"+"smoke2.png"), 2, 1)
	SetTemplateOffset(t0, -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(t0, -0.005, 0.005, 0.0, -0.03, -0.005, 0.005)
	SetTemplateAlphaVel(t0, True)
	SetTemplateSize(t0, 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(t0, .01, 1.01)
	SetTemplateGravity(ParticleEffect[2], 0.005)
	SetTemplateSubTemplate(ParticleEffect[2], t0)
	
	Room2slCam = CreateCamera()
	CameraViewport(Room2slCam, 0, 0, 128, 128)
	CameraRange Room2slCam, 0.05, 6.0
	CameraZoom(Room2slCam, 0.8)
	HideEntity(Room2slCam)
	
	DrawLoading(30)
	
End Function

Function InitNewGame()
	Local i%, de.Decals, d.Doors, it.Items, r.Rooms, sc.SecurityCams, e.Events
	Local fo.Fonts = First Fonts
	Local fs.FPS_Settings = First FPS_Settings
	
	DrawLoading(45)
	
	HideDistance# = 15.0
	
	HeartBeatRate = 70
	
	AccessCode = 0
	For i = 0 To 3
		AccessCode = AccessCode + Rand(1,9)*(10^i)
	Next	
	
	If SelectedMap = "" Then
		CreateMap()
	Else
		LoadMap(scpModding_ProcessFilePath$("Map Creator\Maps\"+SelectedMap))
	EndIf
	InitWayPoints()
	
	DrawLoading(79)
	
	Curr173 = CreateNPC(NPCtype173, 0, -30.0, 0)
	Curr106 = CreateNPC(NPCtype106, 0, -30.0, 0)
	Curr106\State = 70 * 60 * Rand(12,17)
	
	For d.Doors = Each Doors
		EntityParent(d\obj, 0)
		If d\obj2 <> 0 Then EntityParent(d\obj2, 0)
		If d\frameobj <> 0 Then EntityParent(d\frameobj, 0)
		If d\buttons[0] <> 0 Then EntityParent(d\buttons[0], 0)
		If d\buttons[1] <> 0 Then EntityParent(d\buttons[1], 0)
		
		If d\obj2 <> 0 And d\dir = 0 Then
			MoveEntity(d\obj, 0, 0, 8.0 * RoomScale)
			MoveEntity(d\obj2, 0, 0, 8.0 * RoomScale)
		EndIf	
	Next
	
	For it.Items = Each Items
		EntityType (it\collider, HIT_ITEM)
		EntityParent(it\collider, 0)
	Next
	
	DrawLoading(80)
	For sc.SecurityCams= Each SecurityCams
		sc\angle = EntityYaw(sc\obj) + sc\angle
		EntityParent(sc\obj, 0)
	Next	
	
	For r.Rooms = Each Rooms
		For i = 0 To MaxRoomLights
			If r\Lights[i]<>0 Then EntityParent(r\Lights[i],0)
		Next
		
		If (Not r\RoomTemplate\DisableDecals) Then
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(Rand(2, 3), EntityX(r\obj)+Rnd(- 2,2), 0.003, EntityZ(r\obj)+Rnd(-2,2), 90, Rand(360), 0)
				de\Size = Rnd(0.1, 0.4) : ScaleSprite(de\obj, de\Size, de\Size)
				EntityAlpha(de\obj, Rnd(0.85, 0.95))
			EndIf
			
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(0, EntityX(r\obj)+Rnd(- 2,2), 0.003, EntityZ(r\obj)+Rnd(-2,2), 90, Rand(360), 0)
				de\Size = Rnd(0.5, 0.7) : EntityAlpha(de\obj, 0.7) : de\ID = 1 : ScaleSprite(de\obj, de\Size, de\Size)
				EntityAlpha(de\obj, Rnd(0.7, 0.85))
			EndIf
		EndIf
		
		If (r\RoomTemplate\Name = "room173" And IntroEnabled = False) Then 
			PositionEntity (Collider, EntityX(r\obj)+3584*RoomScale, 704*RoomScale, EntityZ(r\obj)+1024*RoomScale)
			PlayerRoom = r
			it = CreateItem("Class D Orientation Leaflet", "paper", 1, 1, 1)
			it\Picked = True
			it\Dropped = -1
			it\itemtemplate\found=True
			Inventory(0) = it
			HideEntity(it\collider)
			EntityType (it\collider, HIT_ITEM)
			EntityParent(it\collider, 0)
			ItemAmount = ItemAmount + 1
			it = CreateItem("Document SCP-173", "paper", 1, 1, 1)
			it\Picked = True
			it\Dropped = -1
			it\itemtemplate\found=True
			Inventory(1) = it
			HideEntity(it\collider)
			EntityType (it\collider, HIT_ITEM)
			EntityParent(it\collider, 0)
			ItemAmount = ItemAmount + 1
			If SelectedDifficulty\TwoSlots = False Then
			    MaxItemAmount% = 10
			Else
			    MaxItemAmount% = 2
			EndIf
		ElseIf (r\RoomTemplate\Name = "room173intro" And IntroEnabled) Then
			PositionEntity (Collider, EntityX(r\obj), 1.0, EntityZ(r\obj))
			PlayerRoom = r
			If SelectedDifficulty\TwoSlots = False Then
			    MaxItemAmount% = 10
			Else
			    MaxItemAmount% = 2
			EndIf
		EndIf
		If (SelectedDifficulty\menu = False And r\RoomTemplate\Name = "room173" And IntroEnabled = False) Then 
			PositionEntity (Collider, EntityX(r\obj)+3584*RoomScale, 704*RoomScale, EntityZ(r\obj)+1024*RoomScale)
			PlayerRoom = r
			ItemAmount = ItemAmount + 1
			If SelectedDifficulty\TwoSlots = False Then
			    MaxItemAmount% = 10
			Else
			    MaxItemAmount% = 2
			EndIf
		ElseIf (SelectedDifficulty\menu = False And r\RoomTemplate\Name = "room173intro" And IntroEnabled) Then
			PositionEntity (Collider, EntityX(r\obj), 1.0, EntityZ(r\obj))
			ItemAmount = ItemAmount + 1		
			PlayerRoom = r
			If SelectedDifficulty\TwoSlots = False Then
			    MaxItemAmount% = 10
			Else
			    MaxItemAmount% = 2
			EndIf	
		EndIf					
	Next
	
	Local rt.RoomTemplates
	For rt.RoomTemplates = Each RoomTemplates
		FreeEntity (rt\obj)
	Next	
	
	Local tw.TempWayPoints
	For tw.TempWayPoints = Each TempWayPoints
		Delete tw
	Next
	
	TurnEntity(Collider, 0, Rand(160, 200), 0)
	
	ResetEntity Collider
	
	If SelectedMap = "" Then InitEvents()
	
	For e.Events = Each Events
		If e\EventName = "room2nuke"
			e\EventState = 1
		EndIf
		If e\EventName = "room106"
			e\EventState2 = 1
		EndIf	
		If e\EventName = "room2sl"
			e\EventState3 = 1
		EndIf
	Next
	
	MoveMouse viewport_center_x,viewport_center_y;320, 240
	
	AASetFont fo\Font[0]
	
	HidePointer()
	
	If UnlockThaumiel = 1 Then 
		Achievements(58)=True
		;SetSteamAchievement("s58")
		;StoreSteamStats()
		;BS_UserStats_SetAchievement(BS_UserStats(), "s58")
		;BS_UserStats_StoreStats(BS_UserStats())
		scpSteam_SetAchievement("s58")
	EndIf
	
	BlinkTimer = -10
	BlurTimer = 100
	Stamina = 100
	
	For i% = 0 To 70
		fs\FPSfactor[0] = 1.0
		FlushKeys()
		MovePlayer()
		UpdateDoors()
		UpdateNPCs()
		UpdateWorld()
		;Cls
		If (Int(Float(i)*0.27)<>Int(Float(i-1)*0.27)) Then
			DrawLoading(80+Int(Float(i)*0.27))
		EndIf
	Next
	
	FreeTextureCache
	DrawLoading(100)
	
	FlushKeys
	FlushMouse
	
	DropSpeed = 0
	
	fs\PrevTime = MilliSecs()
End Function

Function InitLoadGame()
	Local d.Doors, sc.SecurityCams, rt.RoomTemplates, e.Events
	Local fo.Fonts = First Fonts
	Local fs.FPS_Settings = First FPS_Settings
	
	DrawLoading(80)
	
	For d.Doors = Each Doors
		EntityParent(d\obj, 0)
		If d\obj2 <> 0 Then EntityParent(d\obj2, 0)
		If d\frameobj <> 0 Then EntityParent(d\frameobj, 0)
		If d\buttons[0] <> 0 Then EntityParent(d\buttons[0], 0)
		If d\buttons[1] <> 0 Then EntityParent(d\buttons[1], 0)
		
	Next
	
	For sc.SecurityCams = Each SecurityCams
		sc\angle = EntityYaw(sc\obj) + sc\angle
		EntityParent(sc\obj, 0)
	Next
	
	ResetEntity Collider
	
	;InitEvents()
	
	DrawLoading(90)
	
	MoveMouse viewport_center_x,viewport_center_y
	
	AASetFont fo\Font[0]
	
	HidePointer ()
	
	BlinkTimer = BLINKFREQ
	Stamina = 100
	
	For rt.RoomTemplates = Each RoomTemplates
		If rt\obj <> 0 Then FreeEntity(rt\obj) : rt\obj = 0
	Next
	
	DropSpeed = 0.0
	
	For e.Events = Each Events
		;Loading the necessary stuff for dimension1499, but this will only be done if the player is in this dimension already
		If e\EventName = "dimension1499"
			If e\EventState = 2
				;[Block]
				DrawLoading(91)
				e\room\Objects[0] = CreatePlane()
				Local planetex% = LoadTexture_Strict(scpModding_ProcessFilePath$("GFX\map\"+"dimension1499\grit3.jpg"))
				EntityTexture e\room\Objects[0],planetex%
				FreeTexture planetex%
				PositionEntity e\room\Objects[0],0,EntityY(e\room\obj),0
				EntityType e\room\Objects[0],HIT_MAP
				;EntityParent e\room\Objects[0],e\room\obj
				DrawLoading(92)
				I_1499\Sky = sky_CreateSky(scpModding_ProcessFilePath$("GFX\map\"+"sky\1499sky"))
				DrawLoading(93)
				For i = 1 To 15
					e\room\Objects[i] = LoadMesh_Strict(scpModding_ProcessFilePath$("GFX\map\"+"dimension1499\1499object"+i+".b3d"))
					HideEntity e\room\Objects[i]
				Next
				DrawLoading(96)
				CreateChunkParts(e\room)
				DrawLoading(97)
				x# = EntityX(e\room\obj)
				z# = EntityZ(e\room\obj)
				Local ch.Chunk
				For i = -2 To 2 Step 2
					ch = CreateChunk(-1,x#*(i*2.5),EntityY(e\room\obj),z#)
				Next
				DrawLoading(98)
				UpdateChunks(e\room,15,False)
				
				Exit
				;[End Block]
			EndIf
		EndIf
	Next
	
	FreeTextureCache
	
	DrawLoading(100)
	
	fs\PrevTime = MilliSecs()
	fs\FPSfactor[0] = 0
	ResetInput()
	
End Function

Function NullGame(playbuttonsfx%=True)
	Local i%, x%, y%, lvl
	Local itt.ItemTemplates, s.Screens, lt.LightTemplates, d.Doors, m.Materials
	Local wp.WayPoints, twp.TempWayPoints, r.Rooms, it.Items
	Local o.Objects = First Objects
	
	KillSounds()
	If playbuttonsfx Then PlaySound_Strict ButtonSFX
	
	FreeParticles()
	
	ClearTextureCache
	
	DebugHUD = False
	
	UnableToMove% = False
	
	QuickLoadPercent = -1
	QuickLoadPercent_DisplayTimer# = 0
	QuickLoad_CurrEvent = Null
	
	DeathMSG$=""
	
	SelectedMap = ""
	
	UsedConsole = False
	
	DoorTempID = 0
	RoomTempID = 0
	
	GameSaved = 0
	
	HideDistance# = 15.0
	
	For lvl = 0 To 0
		For x = 0 To MapWidth+1
			For y = 0 To MapHeight+1
				MapTemp(x, y) = 0
				MapFound(x, y) = 0
			Next
		Next
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		itt\found = False
	Next
	
	DropSpeed = 0
	Shake = 0
	CurrSpeed = 0
	
	DeathTimer = 0
	
	HeartBeatVolume = 0
	
	StaminaEffect = 1.0
	StaminaEffectTimer = 0
	BlinkEffect = 1.0
	BlinkEffectTimer = 0
	
	Bloodloss = 0
	Injuries = 0
	I_008\Timer = 0
	
	For i = 0 To 5
		SCP1025state[i] = 0
	Next
	
	I_END\SelectedEnding = ""
	I_END\Timer = 0
	ExplosionTimer = 0
	
	CameraShake = 0
	Shake = 0
	LightFlash = 0
	
	chs\GodMode = 0
	chs\NoClip = 0
	WireframeState = 0
	WireFrame 0
	WearingGasMask = 0
	WearingHazmat = 0
	WearingVest = 0
	I_714\Using = 0
	If WearingNightVision Then
		CameraFogFar = StoredCameraFogFar
		WearingNightVision = 0
	EndIf
	I_427\Using = 0
	I_427\Timer = 0.0
	
	ForceMove = 0.0
	ForceAngle = 0.0	
	Playable = True
	
	CoffinDistance = 100
	
	Contained106 = False
	If Curr173 <> Null Then Curr173\Idle = False
	
	MTFtimer = 0
	For i = 0 To 9
		MTFrooms[i] = Null
		MTFroomState[i] = 0
	Next
	
	For s.Screens = Each Screens
		If s\img <> 0 Then FreeImage s\img : s\img = 0
		Delete s
	Next
	
	For i = 0 To MAXACHIEVEMENTS - 1
		Achievements(i) = 0
	Next
	RefinedItems = 0
	
	ConsoleInput = ""
	ConsoleOpen = False
	
	EyeIrritation = 0
	EyeStuck = 0
	
	ShouldPlay = 0
	
	KillTimer = 0
	FallTimer = 0
	Stamina = 100
	BlurTimer = 0
	SuperMan = False
	SuperManTimer = 0
	Sanity = 0
	RestoreSanity = True
	Crouch = False
	CrouchState = 0.0
	LightVolume = 0.0
	Vomit = False
	VomitTimer = 0.0
	SecondaryLightOn# = True
	PrevSecondaryLightOn# = True
	RemoteDoorOn = True
	SoundTransmission = False
	
	chs\InfiniteStamina% = False
	
	;{~--<MOD>--~}
	
	I_447\UsingEyeDrops = False
	I_447\UsingEyeDropsTimer = -1.0
	I_447\UsingFirstAid = False
	I_447\UsingFirstAidTimer = -1.0
	I_447\UsingPill = False
	I_447\UsingPillTimer = -1.0
    		
	UsedMorphine = False
	MorphineTimer = 0
	MorphineHealAmount = 0
	
	I_409\Timer = 0
	
	I_178\Using = 0
	
	I_215\Using = 0
    I_215\Timer = 0
	I_215\IdleTimer = 0
	I_215\Limit = 0
	
	I_1033RU\Using = 0
	I_1033RU\HP = 0
	I_1033RU\DHP = 0
	
	I_1079\Foam = 0
	I_1079\Trigger = 0
	I_1079\Limit = 0
	I_1079\Take = 0
	
	I_207\Timer = 0
	
	I_500\Limit = 0
	
	I_402\Timer = 0
	I_402\Using = 0
	
	I_357\Timer = 0

    ;Randomize the SCP-005's chance again
    ChanceToSpawn005 = Rand(3)

    MTF2timer = 0
    For i = 0 To 9
		MTF2rooms[i] = Null
		MTF2roomState[i] = 0
	Next

    Contained457 = False

    chs\Cheats = 0

    GasMaskBlurTimer = 0

	For o.Objects = Each Objects
	    Delete o
	Next
	
    Curr650 = Null
    Curr066 = Null

    For i = 0 To 6
		MTF2rooms[i] = Null
	Next
	
	chs\NoBlinking = 0
	
	WearingHelmet = 0

	;{~--<END>--~}
	
	Msg = ""
	MsgTimer = 0
	
	SelectedItem = Null
	
	For i = 0 To MaxItemAmount - 1
		Inventory(i) = Null
	Next
	SelectedItem = Null
	
	ClosestButton = 0
	
	For d.Doors = Each Doors
		Delete d
	Next
	
	;ClearWorld
	
	For lt.LightTemplates = Each LightTemplates
		Delete lt
	Next 
	
	For m.Materials = Each Materials
		Delete m
	Next
	
	For wp.WayPoints = Each WayPoints
		Delete wp
	Next
	
	For twp.TempWayPoints = Each TempWayPoints
		Delete twp
	Next	
	
	For r.Rooms = Each Rooms
		Delete r
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		Delete itt
	Next 
	
	For it.Items = Each Items
		Delete it
	Next
	
	For pr.Props = Each Props
		Delete pr
	Next
	
	For de.decals = Each Decals
		Delete de
	Next
	
	For n.NPCs = Each NPCs
		Delete n
	Next
		
	Curr173 = Null
	Curr106 = Null
	Curr096 = Null
	Curr5131 = Null
	For i = 0 To 6
		MTFrooms[i] = Null
	Next
	
	ForestNPC = 0
	ForestNPCTex = 0
	
	Local e.Events
	For e.Events = Each Events
		If e\Sound<>0 Then FreeSound_Strict e\Sound
		If e\Sound2<>0 Then FreeSound_Strict e\Sound2
		If e\Sound3<>0 Then FreeSound_Strict e\Sound3
		Delete e
	Next
	
	For sc.securitycams = Each SecurityCams
		Delete sc
	Next
	
	For em.emitters = Each Emitters
		Delete em
	Next	
	
	For p.particles = Each Particles
		Delete p
	Next
	
	For rt.RoomTemplates = Each RoomTemplates
		rt\obj = 0
	Next
	
	For i = 0 To 5
		If ChannelPlaying(RadioCHN(i)) Then StopChannel(RadioCHN(i))
	Next
	
	I_1499\PrevX# = 0.0
	I_1499\PrevY# = 0.0
	I_1499\PrevZ# = 0.0
	I_1499\PrevRoom = Null
	I_1499\X# = 0.0
	I_1499\Y# = 0.0
	I_1499\Z# = 0.0
	I_1499\Using% = False
	DeleteChunks()
	
	DeleteElevatorObjects()
	
	DeleteDevilEmitters()
	
	chs\NoTarget% = False
	
	OptionsMenu% = -1
	QuitMSG% = -1
	AchievementsMenu% = -1
	
	MusicVolume# = PrevMusicVolume
	SFXVolume# = PrevSFXVolume
	DeafPlayer% = False
	DeafTimer# = 0.0
	
	I_008\Zombie% = False
	
	Delete Each AchievementMsg
	CurrAchvMSGID = 0
	
	;DeInitExt
	
	ClearWorld
	ReloadAAFont()
	Camera = 0
	ark_blur_cam = 0
	Collider = 0
	Sky = 0
	InitFastResize()
	
	;{~--<MOD>--~}
	
	Local roomlocationentry$
    If PlayerRoomZone = 0 Then
		roomlocationentry = "lcz"		
	ElseIf PlayerRoomZone = 1 Then
		roomlocationentry = "hcz"
	;ElseIf PlayerRoomZone = 2
		;roomlocationentry = "ez
	Else
		Select Rand(2) ;3
			Case 1
				roomlocationentry = "lcz"
			Case 2
				roomlocationentry = "hcz"
			;Case 3
			    ;roomlocationentry = "ez"
	    End Select	
	EndIf			
	PutINIValue(OptionFile, "options", "game progress", roomlocationentry)
	;Init3DMenu()
	
	;{~--<END>--~}
						
End Function

Include "Source Code\Save.bb"

;--------------------------------------- random -------------------------------------------------------

Function f2s$(n#, count%)
	Return Left(n, Len(Int(n))+count+1)
End Function

Function Use914(item.Items, setting$, x#, y#, z#)
	
	RefinedItems = RefinedItems+1
		If UsedConsole<>True Then
			;SetSteamAchievement("s"+achvname)
			;StoreSteamStats()
			;BS_UserStats_SetAchievement(BS_UserStats(), "s"+achvname)
			;BS_UserStats_StoreStats(BS_UserStats())
			;scpSDK_UploadLeaderboard_RefinedItems()
		EndIf
	;If UsedConsole<>True Then
	;	scpSteam_UploadScore("914items", 1)
	;EndIf
	
	Local it2.Items
	Select item\itemtemplate\name
		Case "Gas Mask", "Heavy Gas Mask"
            ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case "1:1"
				    ;[Block]
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
					;[End Block]
				Case "fine", "very fine"
				    ;[Block]
					it2 = CreateItem("Gas Mask", "supergasmask", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "SCP-1499"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Gas Mask", "gasmask", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("SCP-1499", "super1499", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "very fine"
				    ;[Block]
					n.NPCs = CreateNPC(NPCtype1499_1,x,y,z)
					n\State = 1
					n\Sound = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\1499\Triggered.ogg"))
					n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider,20.0)
					n\State3 = 1
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "Ballistic Vest"
		    ;[Block]
			Select setting
				Case "rough"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case "coarse"
				    ;[Block]
					it2 = CreateItem("Corrosive Ballistic Vest", "corrvest", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "1:1"
				    ;[Block]
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
					;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Bulky Ballistic Vest", "veryfinevest", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "Clipboard"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					it2 = CreateItem("Paper Strips", "paperstrips", x, y, z)
					For i% = 0 To 19
						If item\SecondInv[i]<>Null Then RemoveItem(item\SecondInv[i])
						item\SecondInv[i]=Null
					Next
					RemoveItem(item)
					;[End Block]
				Case "1:1" 
				    ;[Block]   
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
					;[End Block]
				Case "fine"
				    ;[Block]
					item\invSlots = Max(item\state2,15)
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
					;[End Block]
				Case "very fine"
				    ;[Block]
					item\invSlots = Max(item\state2,20)
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
					;[End Block]
			End Select
			;[End Block]
		Case "Night Vision Goggles"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case "1:1"
				    ;[Block]
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
					;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Night Vision Goggles", "finenvgoggles", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Night Vision Goggles", "supernv", x, y, z)
					it2\state = 1000
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "Metal Panel", "SCP-148 Ingot"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					it2 = CreateItem("SCP-148 Ingot", "scp148ingot", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "1:1", "fine", "very fine"
				    ;[Block]
					it2 = Null
					For it.Items = Each Items
						If it<>item And it\collider <> 0 And it\Picked = False Then
							If Distance(EntityX(it\collider,True), EntityZ(it\collider,True), EntityX(item\collider, True), EntityZ(item\collider, True)) < (180.0 * RoomScale) Then
								it2 = it
								Exit
							ElseIf Distance(EntityX(it\collider,True), EntityZ(it\collider,True), x,z) < (180.0 * RoomScale)
								it2 = it
								Exit
							End If
						End If
					Next
					
					If it2<>Null Then
						Select it2\itemtemplate\tempname
							Case "gasmask", "supergasmask"
							    ;[Block]
								RemoveItem (it2)
								RemoveItem (item)
								
								it2 = CreateItem("Heavy Gas Mask", "gasmask3", x, y, z)
								;[End Block]
							Case "vest"
							    ;[Block]
								RemoveItem (it2)
								RemoveItem(item)
								it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
								;[End Block]
							Case "hazmatsuit","hazmatsuit2"
							    ;[Block]
								RemoveItem (it2)
								RemoveItem(item)
								it2 = CreateItem("Heavy Hazmat Suit", "hazmatsuit3", x, y, z)
								;[End Block]
						End Select
					Else 
						If item\itemtemplate\name="SCP-148 Ingot" Then
							it2 = CreateItem("Metal Panel", "scp148", x, y, z)
							RemoveItem(item)
						Else
							PositionEntity(item\collider, x, y, z)
							ResetEntity(item\collider)							
						EndIf
					EndIf
					;[End Block]					
			End Select
			;[End Block]
		Case "Severed Hand", "Black Severed Hand"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(3, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1", "fine"
				    ;[Block]
				    If (item\itemtemplate\tempname = "hand") Then
				        If Rand(2) = 1 Then
				            it2 = CreateItem("Black Severed Hand", "hand2", x, y, z)
                        Else
                            it2 = CreateItem("Severed Hand", "hand3", x, y, z)
                        EndIf
                    ElseIf (item\itemtemplate\tempname = "hand3")
                        If Rand(2) = 1 Then
				            it2 = CreateItem("Black Severed Hand", "hand2", x, y, z)
                        Else
                            it2 = CreateItem("Severed Hand", "hand", x, y, z)
                        EndIf
                    Else
                        If Rand(2) = 1 Then
				            it2 = CreateItem("Severed Hand", "hand3", x, y, z)
                        Else
                            it2 = CreateItem("Severed Hand", "hand", x, y, z)
                        EndIf
                    EndIf
                    ;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("SCP-447", "scp447", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "First Aid Kit", "Blue First Aid Kit"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
				    If Rand(2)=1 Then
					    it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
				    Else
				        it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
				    EndIf
				    ;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Strange Bottle", "veryfinefirstaid", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Level 0 Key Card", "Level 1 Key Card", "Level 2 Key Card", "Level 3 Key Card", "Level 4 Key Card", "Level 5 Key Card", "Level 6 Key Card"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Playing Card", "misc", x, y, z)
					;[End Block]
				Case "fine"
				    ;[Block]
					Select item\itemtemplate\name
					    Case "Level 0 Key Card"
					        ;[Block]
							Select SelectedDifficulty\otherFactors
								Case EASY
								    ;[Block]
									it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
									;[End Block]
								Case NORMAL
								    ;[Block]
									If Rand(5)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
									EndIf
									;[End Block]
								Case HARD
								    ;[Block]
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 1 Key Card"
						    ;[Block]
							Select SelectedDifficulty\otherFactors
								Case EASY
								    ;[Block]
									it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									;[End Block]
								Case NORMAL
								    ;[Block]
									If Rand(5)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
									;[End Block]
								Case HARD
								    ;[Block]
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 2 Key Card"
						    ;[Block]
							Select SelectedDifficulty\otherFactors
								Case EASY
								    ;[Block]
									it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									;[End Block]
								Case NORMAL
								    ;[Block]
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
									;[End Block]
								Case HARD
								    ;[Block]
									If Rand(3)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 3 Key Card"
						    ;[Block]
							Select SelectedDifficulty\otherFactors
								Case EASY
								    ;[Block]
									If Rand(10)=1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
									;[End Block]
								Case NORMAL
								    ;[Block]
									If Rand(15)=1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
									;[End Block]
								Case HARD
								    ;[Block]
									If Rand(20)=1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 4 Key Card"
						    ;[Block]
							Select SelectedDifficulty\otherFactors
								Case EASY
								    ;[Block]
									it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									;[End Block]
								Case NORMAL
								    ;[Block]
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
									;[End Block]
								Case HARD
								    ;[Block]
									If Rand(3)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 5 Key Card"	
						    ;[Block]
							Local CurrAchvAmount%=0
							For i = 0 To MAXACHIEVEMENTS-1
								If Achievements(i)=True
									CurrAchvAmount=CurrAchvAmount+1
								EndIf
							Next
							
							Select SelectedDifficulty\otherFactors
								Case EASY
								    ;[Block]
									If Rand(0,((MAXACHIEVEMENTS-1)*3)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("Key Card Omni", "key7", x, y, z)
									Else
										If Rand(4)=1 Then
					                        it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
					                    Else
				  	                        it2 = CreateItem("Mastercard", "misc", x, y, z)
					                    EndIf
									EndIf
									;[End Block]
								Case NORMAL
								    ;[Block]
									If Rand(0,((MAXACHIEVEMENTS-1)*4)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("Key Card Omni", "key7", x, y, z)
									Else
										If Rand(5)=1 Then
					                        it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
					                    Else
					                        it2 = CreateItem("Mastercard", "misc", x, y, z)
						                EndIf
									EndIf
									;[End Block]
								Case HARD
								    ;[Block]
									If Rand(0,((MAXACHIEVEMENTS-1)*5)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("Key Card Omni", "key7", x, y, z)
									Else
										If Rand(6)=1 Then
					                        it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
					                    Else
				  	                        it2 = CreateItem("Mastercard", "misc", x, y, z)
					                    EndIf
									EndIf
									;[End Block]
                            End Select
                            ;[End Block]
                        Case "Level 6 Key Card"
                            ;[Block]
							Select SelectedDifficulty\otherFactors
								Case EASY
								    ;[Block]
									it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									;[End Block]
								Case NORMAL
								    ;[Block]
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
									;[End Block]
								Case HARD
								    ;[Block]
									If Rand(3)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
									;[End Block]
							End Select	
							;[End Block]			
					End Select
					;[End Block]
				Case "very fine"
				    ;[Block]
					CurrAchvAmount%=0
					For i = 0 To MAXACHIEVEMENTS-1
						If Achievements(i)=True
							CurrAchvAmount=CurrAchvAmount+1
						EndIf
					Next

					Select SelectedDifficulty\otherFactors
						Case EASY
						    ;[Block]
							If Rand(0,((MAXACHIEVEMENTS-1)*3)-((CurrAchvAmount-1)*3))=0
								it2 = CreateItem("Key Card Omni", "key7", x, y, z)
							Else
							    If Rand(4)=1 Then
							        it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
							    Else
								    it2 = CreateItem("Mastercard", "misc", x, y, z)
								EndIf
							EndIf
							;[End Block]
						Case NORMAL
						    ;[Block]
							If Rand(0,((MAXACHIEVEMENTS-1)*4)-((CurrAchvAmount-1)*3))=0
								it2 = CreateItem("Key Card Omni", "key7", x, y, z)
							Else
								If Rand(5)=1 Then
							        it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
							    Else
								    it2 = CreateItem("Mastercard", "misc", x, y, z)
								EndIf
							EndIf
							;[End Block]
						Case HARD
						    ;[Block]
							If Rand(0,((MAXACHIEVEMENTS-1)*5)-((CurrAchvAmount-1)*3))=0
								If Rand(6)=1 Then
							        it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
							    Else
								    it2 = CreateItem("Mastercard", "misc", x, y, z)
								EndIf
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							EndIf
							;[End Block]
					End Select
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "Key Card Omni"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					If Rand(2)=1 Then
						it2 = CreateItem("Mastercard", "misc", x, y, z)
					Else
						it2 = CreateItem("Playing Card", "misc", x, y, z)			
					EndIf	
					;[End Block]
				Case "fine", "very fine"
				    ;[Block]
					it2 = CreateItem("Key Card Omni", "key7", x, y, z)
					;[End Block]
			End Select			
			
			RemoveItem(item)
			;[End Block]
		Case "Playing Card", "Coin", "Quarter"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Level 0 Key Card", "key0", x, y, z)
					;[End Block]	
			    Case "fine", "very fine"
			        ;[Block]
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Mastercard"
		    ;[Block]
			Select setting
				Case "rough"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "coarse"
				    ;[Block]
					it2 = CreateItem("Quarter", "25ct", x, y, z)
					Local it3.Items,it4.Items,it5.Items
					it3 = CreateItem("Quarter", "25ct", x, y, z)
					it4 = CreateItem("Quarter", "25ct", x, y, z)
					it5 = CreateItem("Quarter", "25ct", x, y, z)
					EntityType (it3\collider, HIT_ITEM)
					EntityType (it4\collider, HIT_ITEM)
					EntityType (it5\collider, HIT_ITEM)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Level 0 Key Card", "key0", x, y, z)
					;[End Block]	
			    Case "fine", "very fine"
			        ;[Block]
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "S-NAV 300 Navigator", "S-NAV 310 Navigator", "S-NAV Navigator", "S-NAV Navigator Ultimate"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					it2 = CreateItem("Electronical components", "misc", x, y, z)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("S-NAV Navigator", "nav", x, y, z)
					it2\state = 100
					;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("S-NAV 310 Navigator", "nav", x, y, z)
					it2\state = 100
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("S-NAV Navigator Ultimate", "nav", x, y, z)
					it2\state = 101
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "Radio Transceiver"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					it2 = CreateItem("Electronical components", "misc", x, y, z)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Radio Transceiver", "18vradio", x, y, z)
					it2\state = 100
					;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Radio Transceiver", "fineradio", x, y, z)
					it2\state = 101
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Radio Transceiver", "veryfineradio", x, y, z)
					it2\state = 101
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "SCP-513"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					PlaySound_Strict LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\513\914Refine.ogg"))
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype513_1 Then RemoveNPC(n)
					Next
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1", "fine", "very fine"
				    ;[Block]
					it2 = CreateItem("SCP-513", "scp513", x, y, z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "Some SCP-420-J", "Cigarette"
		    ;[Block]
			Select setting
				Case "rough", "coarse"	
				    ;[Block]		
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					it2 = CreateItem("Cigarette", "cigarette", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
				Case "fine"
					it2 = CreateItem("Joint", "scp420s", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
				Case "very fine"
					it2 = CreateItem("Smelly Joint", "scp420s", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "9V Battery", "18V Battery", "Strange Battery"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("18V Battery", "18vbat", x, y, z)
					;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "ReVision Eyedrops"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
				    it2 = CreateItem("RedVision Eyedrops", "eyedrops2", x,y,z)
				    ;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Eyedrops", "fineeyedrops", x,y,z)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Eyedrops", "supereyedrops", x,y,z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "Eyedrops"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
				    If Rand(2) = 1 Then
				        it2 = CreateItem("RedVision Eyedrops", "eyedrops2", x,y,z)
				    Else
				        it2 = CreateItem("ReVision Eyedrops", "eyedrops", x,y,z)
				    EndIf
				    ;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Eyedrops", "fineeyedrops", x,y,z)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Eyedrops", "supereyedrops", x,y,z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "RedVision Eyedrops"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
				    it2 = CreateItem("ReVision Eyedrops", "eyedrops", x,y,z)
				    ;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Eyedrops", "fineeyedrops", x,y,z)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Eyedrops", "supereyedrops", x,y,z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]			
		Case "Hazmat Suit"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Hazmat Suit", "hazmatsuit", x,y,z)
					;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Hazmat Suit", "hazmatsuit2", x,y,z)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Hazmat Suit", "hazmatsuit2", x,y,z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "Syringe"
		    ;[Block]
			Select item\itemtemplate\tempname
				Case "syringe"
				    ;[Block]
					Select setting
						Case "rough", "coarse"
						    ;[Block]
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
							;[End Block]
						Case "1:1"
						    ;[Block]
							it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)	
							;[End Block]
						Case "fine"
						    ;[Block]
							it2 = CreateItem("Syringe", "finesyringe", x, y, z)
							;[End Block]
						Case "very fine"
						    ;[Block]
							it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
							;[End Block]
					End Select
					;[End Block]
				Case "finesyringe"
				    ;[Block]
					Select setting
						Case "rough"
						    ;[Block]
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
							;[End Block]
						Case "coarse"
						    ;[Block]
							it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
							;[End Block]
						Case "1:1"
						    ;[Block]
							it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
							;[End Block]	
						Case "fine", "very fine"
						    ;[Block]
							it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
							;[End Block]
					End Select
					;[End Block]
				Case "veryfinesyringe"
				    ;[Block]
					Select setting
						Case "rough", "coarse", "1:1", "fine"
						    ;[Block]
							it2 = CreateItem("Electronical components", "misc", x, y, z)	
							;[End Block]
						Case "very fine"
						    ;[Block]
							n.NPCs = CreateNPC(NPCtype008_1,x,y,z)
							n\State = 2
							;[End Block]
					End Select
					;[End Block]
					
				;{~--<MOD>--~}
				
				Case "syringeinf"
				    ;[Block]
			        Select setting
					    Case "rough", "coarse"
					        ;[Block]
					        d.Decals = CreateDecal(21, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
							;[End Block]
						Case "1:1"
						    ;[Block]
						    n.NPCs = CreateNPC(NPCtype008_1,x,y,z)
							n\State = 2
							;[End Block]	
						Case "fine"
						    ;[Block]
						    it2 = CreateItem("Syringe", "syringe", x, y, z)
						    ;[End Block]
						Case "very fine"
						    ;[Block]
						    it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
						    ;[End Block]
					End Select
					
				;{~--<END>--~}
				
			End Select
			
			RemoveItem(item)
			;[End Block]
			
		Case "SCP-500-01", "Upgraded pill", "Pill"
		    ;[Block]
			Select setting
			    ;[Block]
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Pill", "pill", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "fine"
				    ;[Block]
					Local no427Spawn% = False
					For it3.Items = Each Items
						If it3\itemtemplate\tempname = "scp427" Then
							no427Spawn = True
							Exit
						EndIf
					Next
					If (Not no427Spawn) Then
						it2 = CreateItem("SCP-427", "scp427", x, y, z)
					Else
						it2 = CreateItem("Upgraded pill", "scp500pilldeath", x, y, z)
					EndIf
					RemoveItem(item)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Upgraded pill", "scp500pilldeath", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
			
		;{~--<MOD>--~}
			
		Case "Wallet"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					it2 = CreateItem("Paper Strips", "paperstrips", x, y, z)
					For i% = 0 To 19
						If item\SecondInv[i]<>Null Then RemoveItem(item\SecondInv[i])
						item\SecondInv[i]=Null
					Next
					RemoveItem(item)
					;[End Block]
				Case "1:1"
				    ;[Block]
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
					;[End Block]
				Case "fine"
				    ;[Block]
					item\invSlots = Max(item\state2,15)
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
					;[End Block]
				Case "very fine"
				    ;[Block]
					item\invSlots = Max(item\state2,20)
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
					;[End Block]
			End Select
			;[End Block]
		Case "SCP-447"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					If Rand(2)=1 Then 
						it2 = CreateItem("Black Severed Hand", "hand2", x, y, z)
					Else
					    If Rand(2) = 1 Then
						    it2 = CreateItem("Severed Hand", "hand", x, y, z)
						Else
						    it2 = CreateItem("Severed Hand", "hand3", x, y, z)
						EndIf
					EndIf
					RemoveItem(item)
					;[End Block]
				Case "1:1", "fine", "very fine"
				    ;[Block]
					it2 = Null
					For it.Items = Each Items
						If it<>item And it\collider <> 0 And it\Picked = False Then
							If Distance(EntityX(it\collider,True), EntityZ(it\collider,True), EntityX(item\collider, True), EntityZ(item\collider, True)) < (180.0 * RoomScale) Then
								it2 = it
								Exit
							ElseIf Distance(EntityX(it\collider,True), EntityZ(it\collider,True), x,z) < (180.0 * RoomScale)
								it2 = it
								Exit
							End If
						End If
					Next
					
					If it2<>Null Then
						Select it2\itemtemplate\tempname
							Case "scp500pill"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								it2 = CreateItem("Minty SCP-500-01", "mintscp500pill", x, y, z)
								
								GiveAchievement(Achv447)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
                                ;[End Block]
							Case "firstaid"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								it2 = CreateItem("Minty First Aid Kit", "mintfirstaid", x, y, z)
								
								GiveAchievement(Achv447)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
                                ;[End Block]
							Case "finefirstaid"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								it2 = CreateItem("Minty Small First Aid Kit", "mintfinefirstaid", x, y, z)
								
								GiveAchievement(Achv447)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
                                ;[End Block]
							Case "firstaid2"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								it2 = CreateItem("Minty Blue First Aid Kit", "mintfirstaid2", x, y, z)
								
								GiveAchievement(Achv447)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
                                ;[End Block]
							Case "veryfinefirstaid"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								it2 = CreateItem("Minty Strange Bottle", "mintveryfinefirstaid", x, y, z)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
                                ;[End Block]
							Case "eyedrops"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								it2 = CreateItem("Minty ReVision Eyedrops", "minteyedrops", x, y, z)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
								;[End Block]
							Case "eyedrops2"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								it2 = CreateItem("Minty RedVision Eyedrops", "minteyedrops2", x, y, z)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							    ;[End Block]
							Case "fineeyedrops"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								it2 = CreateItem("Minty Eyedrops", "mintfineeyedrops", x, y, z)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
                                ;[End Block]
							Case "supereyedrops"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								it2 = CreateItem("Minty Eyedrops", "mintsupereyedrops", x, y, z)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							    ;[End Block]
							Case "bat"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								it2 = CreateItem("Minty 9V Battery", "mintbat", x, y, z)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							    ;[End Block]
							Case "18vbat"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								it2 = CreateItem("Minty 18V Battery", "mint18vbat", x, y, z)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							    ;[End Block]
						    Case "key0"
						        ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								If Rand(4) = 1 Then
								    it2 = CreateItem("Mastercard", "misc", x, y, z)
                                Else
								    it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
								EndIf
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							    ;[End Block]
							Case "key1"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								If Rand(5) = 1 Then
								    it2 = CreateItem("Mastercard", "misc", x, y, z)
                                Else
								    it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
								EndIf
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							    ;[End Block]
							Case "key2"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								If Rand(6) = 1 Then
								    it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
                                Else
								    it2 = CreateItem("Mastercard", "misc", x, y, z)  
								EndIf
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							    ;[End Block]
							Case "key3"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								If Rand(8) = 1 Then
								    it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
                                Else
                                    it2 = CreateItem("Mastercard", "misc", x, y, z)   							
							    EndIf
								 
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							    ;[End Block]
							Case "key4"
							    ;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								If Rand(10) = 1 Then
								    it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
                                Else
								    it2 = CreateItem("Mastercard", "misc", x, y, z)
								EndIf
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							    ;[End Block]
							Case "key5"
							    ;[Block]
						        RemoveItem(it2)
								RemoveItem(item)
								
								GiveAchievement(Achv447)
								
								d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							    d\Size = 0.2 : EntityAlpha(d\obj, 0.6) : ScaleSprite(d\obj, d\Size, d\Size)
							
							    For i = 0 To MAXACHIEVEMENTS-1
								    If Achievements(i)=True
								    	CurrAchvAmount=CurrAchvAmount+1
								    EndIf
							    Next
							
							    Select SelectedDifficulty\otherFactors
								    Case EASY
								        ;[Block]
									    If Rand(0,((MAXACHIEVEMENTS-1)*3)-((CurrAchvAmount-1)*3))=0
									    	it2 = CreateItem("Key Card Omni", "key7", x, y, z)
									    Else
										    If Rand(4)=1 Then
					                            it2 = CreateItem("Playing Card", "misc", x, y, z)
					                        Else
				  	                            it2 = CreateItem("Mastercard", "misc", x, y, z)
					                        EndIf
									    EndIf
									    ;[End Block]
								    Case NORMAL
								        ;[Block]
									    If Rand(0,((MAXACHIEVEMENTS-1)*4)-((CurrAchvAmount-1)*3))=0
										    it2 = CreateItem("Key Card Omni", "key7", x, y, z)
									    Else
										    If Rand(5)=1 Then
					                            it2 = CreateItem("Playing Card", "misc", x, y, z)
					                        Else
					                            it2 = CreateItem("Mastercard", "misc", x, y, z)
						                    EndIf
									    EndIf
									    ;[End Block]
								    Case HARD
								        ;[Block]
									    If Rand(0,((MAXACHIEVEMENTS-1)*5)-((CurrAchvAmount-1)*3))=0
									    	it2 = CreateItem("Key Card Omni", "key7", x, y, z)
									    Else
									    	If Rand(6)=1 Then
					                            it2 = CreateItem("Playing Card", "misc", x, y, z)
					                        Else
				  	                            it2 = CreateItem("Mastercard", "misc", x, y, z)
					                        EndIf
									    EndIf
									    ;[End Block]
                                End Select
                                ;[End Block]
						End Select
					Else 
						If item\itemtemplate\name = "SCP-148 Ingot" Then
							it2 = CreateItem("Metal Panel", "scp148", x, y, z)
							RemoveItem(item)
						Else
							PositionEntity(item\collider, x, y, z)
							ResetEntity(item\collider)							
						EndIf
					EndIf
					;[End Block]					
			End Select
			;[End Block]
		Case "Minty SCP-500-01", "Upgraded Minty Pill", "Minty Pill"
		    ;[Block]
			Select setting
			    ;[Block]
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Minty Pill", "mintpill", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "fine"
				    ;[Block]
					no427Spawn% = False
					For it3.Items = Each Items
						If it3\itemtemplate\tempname = "scp427" Then
							no427Spawn = True
							Exit
						EndIf
					Next
					If (Not no427Spawn) Then
						it2 = CreateItem("SCP-427", "scp427", x, y, z)
					Else
						it2 = CreateItem("Upgraded Minty Pill", "mintscp500pilldeath", x, y, z)
					EndIf
					RemoveItem(item)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Upgraded Minty Pill", "mintscp500pilldeath", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]

		Case "Minty First Aid Kit", "Minty Blue First Aid Kit"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
				    If Rand(2)=1 Then
					    it2 = CreateItem("Minty Blue First Aid Kit", "mintfirstaid2", x, y, z)
				    Else
				        it2 = CreateItem("Minty First Aid Kit", "mintfirstaid", x, y, z)
				    EndIf
				    ;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Minty Small First Aid Kit", "mintfinefirstaid", x, y, z)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Minty Strange Bottle", "mintveryfinefirstaid", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Minty Strange Bottle"
		    ;[Block]
			Select setting
				Case "rough"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "coarse"
				    ;[Block]
					it2 = CreateItem("Minty Blue First Aid Kit", "mintfirstaid2", x, y, z)
					;[End Block]
				Case "1:1"
				    ;[Block]
					If Rand(2)=1 Then
						it2 = CreateItem("Minty Small First Aid Kit", "mintfinefirstaid", x, y, z)
					Else
						it2 = CreateItem("Minty Strange Bottle", "mintveryfinefirstaid", x, y, z)
					EndIf
					;[End Block]
				Case "fine"
				    ;[Block]
					If Rand(2)=1 Then
						it2 = CreateItem("Minty Small First Aid Kit", "mintfinefirstaid", x, y, z)
					Else
						it2 = CreateItem("Minty Strange Bottle", "mintveryfinefirstaid", x, y, z)
					EndIf
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Minty Strange Bottle", "mintveryfinefirstaid", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Minty Small First Aid Kit"
		    ;[Block]
			Select setting
				Case "rough"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "coarse"
				    ;[Block]
			        it2 = CreateItem("Minty First Aid Kit", "mintfirstaid", x, y, z)
					;[End Block]
				Case "1:1"
				    ;[Block]
					If Rand(2)=1 Then
					   it2 = CreateItem("Minty Small First Aid Kit", "mintfinefirstaid", x, y, z)
					Else
				  	   it2 = CreateItem("Painkiller", "morphine", x, y, z)
					EndIf
					;[End Block]
				Case "fine", "very fine"
				    ;[Block]
					it2 = CreateItem("Minty Strange Bottle", "mintveryfinefirstaid", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
            ;[End Block]
		Case "Minty ReVision Eyedrops", "Minty RedVision Eyedrops", "Minty Eyedrops"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Minty RedVision Eyedrops", "minteyedrops2", x,y,z)
					;[End Block]
				Case "fine"
				    ;[Block]
					it2 = CreateItem("Minty Eyedrops", "mintfineeyedrops", x,y,z)
					;[End Block]
				Case "very fine"
				    ;[Block]
					it2 = CreateItem("Minty Eyedrops", "mintsupereyedrops", x,y,z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "SCP-1033-RU"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					If (item\itemtemplate\tempname = "scp1033ru")
					    it2 = CreateItem("SCP-1033-RU", "scp1033ru", x,y,z)
					    I_1033RU\HP = 100
					    I_1033RU\DHP = 0
					Else
					    it2 = CreateItem("SCP-1033-RU", "super1033ru", x,y,z)
					    I_1033RU\HP = 200
					    I_1033RU\DHP = 0
                    EndIf
                    ;[End Block]
				Case "fine", "very fine"
				    ;[Block]
					it2 = CreateItem("SCP-1033-RU", "super1033ru", x,y,z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "Ballistic Helmet"
		    ;[Block]
			Select setting
				Case "rough", "coarse"
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
					;[End Block]
				Case "1:1"
				    ;[Block]
					it2 = CreateItem("Ballistic Vest", "vest", x, y, z)
					;[End Block]	
			    Case "fine", "very fine"
			        ;[Block]
					it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
			
	    ;{~--<END>--~}
	
		Default
		    ;[Block]
			Select item\itemtemplate\tempname
				Case "cup"
				    ;[Block]
					Select setting
						Case "rough", "coarse"
						    ;[Block]
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
							d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
							;[End Block]
						Case "1:1"
						    ;[Block]
							it2 = CreateItem("cup", "cup", x,y,z)
							it2\name = item\name
							it2\r = 255-item\r
							it2\g = 255-item\g
							it2\b = 255-item\b
							;[End Block]
						Case "fine"
						    ;[Block]
							it2 = CreateItem("cup", "cup", x,y,z)
							it2\name = item\name
							it2\state = 1.0
							it2\r = Min(item\r*Rnd(0.9,1.1),255)
							it2\g = Min(item\g*Rnd(0.9,1.1),255)
							it2\b = Min(item\b*Rnd(0.9,1.1),255)
							;[End Block]
						Case "very fine"
						    ;[Block]
							it2 = CreateItem("cup", "cup", x,y,z)
							it2\name = item\name
							it2\state = Max(it2\state*2.0,2.0)	
							it2\r = Min(item\r*Rnd(0.5,1.5),255)
							it2\g = Min(item\g*Rnd(0.5,1.5),255)
							it2\b = Min(item\b*Rnd(0.5,1.5),255)
							If Rand(5)=1 Then
								ExplosionTimer = 135
							EndIf
							;[End Block]
					End Select	
					
					RemoveItem(item)
					;[End Block]
				Case "paper"
				    ;[Block]
					Select setting
						Case "rough", "coarse"
						    ;[Block]
							it2 = CreateItem("Paper Strips", "paperstrips", x, y, z)
							;[End Block]
						Case "1:1"
							Select Rand(37)
								Case 1    
								    ;[Block]									
								    it2 = CreateItem("Document SCP-106", "paper", x, y, z)
									;[End Block]
								Case 2
								    ;[Block]
									it2 = CreateItem("Document SCP-079", "paper", x, y, z)
									;[End Block]
								Case 3
								    ;[Block]
									it2 = CreateItem("Document SCP-173", "paper", x, y, z)
									;[End Block]
								Case 4
								    ;[Block]
									it2 = CreateItem("Document SCP-895", "paper", x, y, z)
									;[End Block]
								Case 5
								    ;[Block]
									it2 = CreateItem("Document SCP-682", "paper", x, y, z)
									;[End Block]
								Case 6
								    ;[Block]
									it2 = CreateItem("Document SCP-860", "paper", x, y, z)
									;[End Block]
								Case 7
								    ;[Block]
									it2 = CreateItem("SCP-035 Addendum", "paper", x, y, z)
									;[End Block]
								Case 8
								    ;[Block]
									it2 = CreateItem("Document SCP-1162", "paper", x, y, z)
									;[End Block]
								Case 9
								    ;[Block]
									it2 = CreateItem("Document SCP-096", "paper", x, y, z)
									;[End Block]
								Case 10
								    ;[Block]
									it2 = CreateItem("Document SCP-035", "paper", x, y, z)
									;[End Block]
								Case 11
								    ;[Block]
									it2 = CreateItem("Document SCP-966", "paper", x, y, z)
									;[End Block]
								Case 12
								    ;[Block]
									it2 = CreateItem("Document SCP-970", "paper", x, y, z)
									;[End Block]
								Case 13
								    ;[Block]
									it2 = CreateItem("Document SCP-939", "paper", x, y, z)
									;[End Block]
								Case 14
								    ;[Block]
									it2 = CreateItem("Document SCP-012", "paper", x, y, z)
									;[End Block]
								Case 15
								    ;[Block]
									it2 = CreateItem("Document SCP-008", "paper", x, y, z)
									;[End Block]
								Case 16
								    ;[Block]
									it2 = CreateItem("Document SCP-714", "paper", x, y, z)
									;[End Block]
								Case 17
								    ;[Block]
									it2 = CreateItem("Document SCP-1499", "paper", x, y, z)
									;[End Block]
								Case 18
								    ;[Block]
									it2 = CreateItem("Document SCP-1123", "paper", x, y, z)
									;[End Block]
								Case 19
								    ;[Block]
									it2 = CreateItem("Document SCP-049", "paper", x, y, z)
									;[End Block]
								Case 20
								    ;[Block]
									it2 = CreateItem("Document SCP-513", "paper", x, y, z)
									;[End Block]
								Case 21
								    ;[Block]
									it2 = CreateItem("Document SCP-1048", "paper", x, y, z)
									;[End Block]
								Case 22
								    ;[Block]
									it2 = CreateItem("Document SCP-500", "paper", x, y, z)
									;[End Block]
									
									;{~--<MOD>--~}
									
								Case 23
								    ;[Block]
									it2 = CreateItem("SCP-457 Addendum", "paper", x, y, z)
									;[End Block]
								Case 24
								    ;[Block]
									it2 = CreateItem("Document SCP-1079", "paper", x, y, z)
									;[End Block]
								Case 25
								    ;[Block]
									it2 = CreateItem("Document SCP-457", "paper", x, y, z)
									;[End Block]
								Case 26
								    ;[Block]
									it2 = CreateItem("Document SCP-198", "paper", x, y, z)
									;[End Block]
								Case 27
								    ;[Block]
									it2 = CreateItem("Document SCP-650", "paper", x, y, z)
									;[End Block]
								Case 28
								    ;[Block]
									it2 = CreateItem("Document SCP-409", "paper", x, y, z)
									;[End Block]
								Case 29
								    ;[Block]
									it2 = CreateItem("Document SCP-005", "paper", x, y, z)
									;[End Block]
								Case 30
								    ;[Block]
									it2 = CreateItem("Document SCP-178", "paper", x, y, z)
									;[End Block]
								Case 31
								    ;[Block]
									it2 = CreateItem("Document SCP-215", "paper", x, y, z)
									;[End Block]
								Case 32    
								    ;[Block]
									it2 = CreateItem("Document SCP-447", "paper", x, y, z)
									;[End Block]
								Case 33
								    ;[Block]
									it2 = CreateItem("Document SCP-109", "paper", x, y, z)
									;[End Block]
								Case 34
								    ;[Block]
									it2 = CreateItem("Document SCP-357", "paper", x, y, z)
									;[End Block]
								Case 35
								    ;[Block]
									it2 = CreateItem("Document SCP-207", "paper", x, y, z)
									;[End Block]
								Case 36
								    ;[Block]
									it2 = CreateItem("Document SCP-402", "paper", x, y, z)
									;[End Block]
								Case 37
								    ;[Block]
									it2 = CreateItem("Document SCP-1033-RU", "paper", x, y, z)
									;[End Block]
									
									;{~--<END>--~}
									
							End Select
						Case "fine", "very fine"
						    ;[Block]
							it2 = CreateItem("Origami", "misc", x, y, z)
							;[End Block]
					End Select
					
					RemoveItem(item)
					;[End Block]
				Default
				    ;[Block]
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)	
					;[End Block]
			End Select
			;[End Block]
	End Select
	
	If it2 <> Null Then EntityType (it2\collider, HIT_ITEM)
End Function

Function Use294()
	Local x#,y#, xtemp%,ytemp%, strtemp$, temp%
	
	ShowPointer()
	
	x = GraphicWidth/2 - (ImageWidth(Panel294)/2)
	y = GraphicHeight/2 - (ImageHeight(Panel294)/2)
	DrawImage Panel294, x, y
	If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	
	temp = True
	If PlayerRoom\SoundCHN<>0 Then temp = False
	
	AAText x+907, y+185, Input294, True,True
	
	If temp Then
		If MouseHit1 Then
			xtemp = Floor((ScaledMouseX()-x-228) / 35.5)
			ytemp = Floor((ScaledMouseY()-y-342) / 36.5)
			
			If ytemp => 0 And ytemp < 5 Then
				If xtemp => 0 And xtemp < 10 Then PlaySound_Strict ButtonSFX
			EndIf
			
			strtemp = ""
			
			temp = False
			
			Select ytemp
				Case 0
					strtemp = (xtemp + 1) Mod 10
				Case 1
					Select xtemp
						Case 0
							strtemp = "Q"
						Case 1
							strtemp = "W"
						Case 2
							strtemp = "E"
						Case 3
							strtemp = "R"
						Case 4
							strtemp = "T"
						Case 5
							strtemp = "Y"
						Case 6
							strtemp = "U"
						Case 7
							strtemp = "I"
						Case 8
							strtemp = "O"
						Case 9
							strtemp = "P"
					End Select
				Case 2
					Select xtemp
						Case 0
							strtemp = "A"
						Case 1
							strtemp = "S"
						Case 2
							strtemp = "D"
						Case 3
							strtemp = "F"
						Case 4
							strtemp = "G"
						Case 5
							strtemp = "H"
						Case 6
							strtemp = "J"
						Case 7
							strtemp = "K"
						Case 8
							strtemp = "L"
						Case 9 ;dispense
							temp = True
					End Select
				Case 3
					Select xtemp
						Case 0
							strtemp = "Z"
						Case 1
							strtemp = "X"
						Case 2
							strtemp = "C"
						Case 3
							strtemp = "V"
						Case 4
							strtemp = "B"
						Case 5
							strtemp = "N"
						Case 6
							strtemp = "M"
						Case 7
							strtemp = "-"
						Case 8
							strtemp = " "
						Case 9
							Input294 = Left(Input294, Max(Len(Input294)-1,0))
					End Select
				Case 4
					strtemp = " "
			End Select
			
			Input294 = Input294 + strtemp
			
			Input294 = Left(Input294, Min(Len(Input294),15))
			
			If temp And Input294<>"" Then ;dispense
				Input294 = Trim(Lower(Input294))
				If Left(Input294, Min(7,Len(Input294))) = "cup of " Then
					Input294 = Right(Input294, Len(Input294)-7)
				ElseIf Left(Input294, Min(9,Len(Input294))) = "a cup of " 
					Input294 = Right(Input294, Len(Input294)-9)
				EndIf
				
				If Input294<>""
					Local loc% = GetINISectionLocation(scpModding_ProcessFilePath$("Data\SCP-294.ini"),Input294)
				EndIf
				
				If loc > 0 Then
					strtemp$ = GetINIString2(scpModding_ProcessFilePath$("Data\SCP-294.ini"), loc, "dispensesound")
					If strtemp="" Then
						PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\294\Dispense1.ogg")))
					Else
						PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound(strtemp))
					EndIf
					
					If GetINIInt2(scpModding_ProcessFilePath$("Data\SCP-294.ini"), loc, "explosion")=True Then 
						ExplosionTimer = 135
						DeathMSG = GetINIString2(scpModding_ProcessFilePath$("Data\SCP-294.ini"), loc, "deathmessage")
					EndIf
					
					strtemp$ = GetINIString2(scpModding_ProcessFilePath$("Data\SCP-294.ini"), loc, "color")
					
					sep1 = Instr(strtemp, ",", 1)
					sep2 = Instr(strtemp, ",", sep1+1)
					r% = Trim(Left(strtemp, sep1-1))
					g% = Trim(Mid(strtemp, sep1+1, sep2-sep1-1))
					b% = Trim(Right(strtemp, Len(strtemp)-sep2))
					
					alpha# = Float(GetINIString2(scpModding_ProcessFilePath$("Data\SCP-294.ini"), loc, "alpha",1.0))
					glow = GetINIInt2(scpModding_ProcessFilePath$("Data\SCP-294.ini"), loc, "glow")
					;If alpha = 0 Then alpha = 1.0
					If glow Then alpha = -alpha
					
					it.items = CreateItem("Cup", "cup", EntityX(PlayerRoom\Objects[1],True),EntityY(PlayerRoom\Objects[1],True),EntityZ(PlayerRoom\Objects[1],True), r,g,b,alpha)
					it\name = "Cup of "+Input294
					EntityType (it\collider, HIT_ITEM)
					
				Else
					;out of range
					Input294 = "OUT OF RANGE"
					PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\294\OutOfRange.ogg")))
				EndIf
				
			EndIf
			
		EndIf ;if mousehit1
		
		If MouseHit2 Or (Not Using294) Then 
			HidePointer()
			Using294 = False
			Input294 = ""
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
			mouse_x_speed_1=0.0 : mouse_y_speed_1=0.0
			mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
		EndIf
		
	Else ;playing a dispensing sound
		If Input294 <> scpLang_GetPhrase("achievements.s10s") Then Input294 = scpLang_GetPhrase("ingame.dispense")
		
		If Not ChannelPlaying(PlayerRoom\SoundCHN) Then
			If Input294 <> scpLang_GetPhrase("achievements.s10s") Then
				HidePointer()
				Using294 = False
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
				mouse_x_speed_1=0.0 : mouse_y_speed_1=0.0
				mouse_x_leverTurn=0.0 : mouse_y_leverTurn=0.0
				Local e.Events
				For e.Events = Each Events
					If e\room = PlayerRoom
						e\EventState2 = 0
						Exit
					EndIf
				Next
			EndIf
			Input294=""
			PlayerRoom\SoundCHN=0
		EndIf
	EndIf
	
End Function

Function WrapAngle180#(angle#)
	If angle = Infinity Then Return 0.0
	While angle < -180
		angle = angle + 360
	Wend
	While angle >= 180
		angle = angle - 360
	Wend
	Return angle
End Function

Function Use427()
	Local i%,pvt%,de.Decals,tempchn%
	Local prevI427Timer# = I_427\Timer
	Local fs.FPS_Settings = First FPS_Settings
	
	If I_427\Timer < 70*360
		If I_427\Using=1 Then
			I_427\Timer = I_427\Timer + fs\FPSfactor[0]
			If Injuries > 0.0 Then
				Injuries = Max(Injuries - 0.0005 * fs\FPSfactor[0],0.0)
			EndIf
			If Bloodloss > 0.0 And Injuries <= 1.0 Then
				Bloodloss = Max(Bloodloss - 0.001 * fs\FPSfactor[0],0.0)
			EndIf
			If I_1079\Foam > 0.0 And Injuries <= 1.0 Then
				I_1079\Foam = Max(I_1079\Foam - 0.001 * fs\FPSfactor[0],0.0)
			EndIf
			If I_008\Timer > 0.0 Then
				I_008\Timer = Max(I_008\Timer - 0.001 * fs\FPSfactor[0],0.0)
			EndIf
			If I_409\Timer > 0.0 Then
				I_409\Timer = Max(I_409\Timer - 0.002 * fs\FPSfactor[0],0.0)
			EndIf
			If I_357\Timer > 0.0 Then
				I_357\Timer = Max(I_357\Timer - 0.002 * fs\FPSfactor[0],0.0)
			EndIf
			If I_207\Timer > 0.0 Then
				I_207\Timer = Max(I_207\Timer - 0.002 * fs\FPSfactor[0],0.0)
			EndIf
			For i = 0 To 5
				If SCP1025state[i]>0.0 Then
					SCP1025state[i] = Max(SCP1025state[i] - 0.001 * fs\FPSfactor[0],0.0)
				EndIf
			Next
			If I_427\Sound[0]=0 Then
				I_427\Sound[0] = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\427\Effect.ogg"))
			EndIf
			If (Not ChannelPlaying(I_427\SoundCHN[0])) Then
				I_427\SoundCHN[0] = PlaySound_Strict(I_427\Sound[0])
			EndIf
			If I_427\Timer => 70*180 Then
				If I_427\Sound[1]=0 Then
					I_427\Sound[1] = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\427\Transform.ogg"))
				EndIf
				If (Not ChannelPlaying(I_427\SoundCHN[1])) Then
					I_427\SoundCHN[1] = PlaySound_Strict(I_427\Sound[1])
				EndIf
			EndIf
			If prevI427Timer < 70*60 And I_427\Timer => 70*60 Then
				Msg = scpLang_GetPhrase("ingame.scp2941")
				MsgTimer = 70*5
			ElseIf prevI427Timer < 70*180 And I_427\Timer => 70*180 Then
				Msg = scpLang_GetPhrase("ingame.scp2942")
				MsgTimer = 70*5
			EndIf
		Else
			For i = 0 To 1
				If I_427\SoundCHN[i]<>0 Then
					If ChannelPlaying(I_427\SoundCHN[i]) Then
						StopChannel(I_427\SoundCHN[i])
					EndIf
				EndIf
			Next
		EndIf
	Else
		If prevI427Timer-fs\FPSfactor[0] < 70*360 And I_427\Timer => 70*360 Then
			Msg = scpLang_GetPhrase("ingame.scp2943")
			MsgTimer = 70*5
		ElseIf prevI427Timer-fs\FPSfactor[0] < 70*390 And I_427\Timer => 70*390 Then
			Msg = scpLang_GetPhrase("ingame.scp2944")
			MsgTimer = 70*5
		EndIf
		I_427\Timer = I_427\Timer + fs\FPSfactor[0]
		If I_427\Sound[0]=0 Then
			I_427\Sound[0] = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\427\Effect.ogg"))
		EndIf
		If I_427\Sound[1]=0 Then
			I_427\Sound[1] = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\427\Transform.ogg"))
		EndIf
		For i = 0 To 1
			If (Not ChannelPlaying(I_427\SoundCHN[i])) Then
				I_427\SoundCHN[i] = PlaySound_Strict(I_427\Sound[i])
			EndIf
		Next
		If Rnd(200)<2.0 Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
			TurnEntity pvt, 90, 0, 0
			EntityPick(pvt,0.3)
			de.Decals = CreateDecal(20, PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
			de\Size = Rnd(0.03,0.08)*2.0 : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\Size, de\Size
			tempchn% = PlaySound_Strict(DripSFX(Rand(0, 5)))
			ChannelVolume tempchn, Rnd(0.0,0.8)*SFXVolume
			ChannelPitch tempchn, Rand(20000,30000)
			FreeEntity pvt
			BlurTimer = 800
		EndIf
		If I_427\Timer >= 70*420 Then
			Kill()
			DeathMSG = Chr(34)+scpLang_GetPhrase("ingame.scp2945")+Chr(34)
		ElseIf I_427\Timer >= 70*390 Then
		    If Crouch = False Then CrouchCHN = PlaySound_Strict(CrouchSFX) 
			Crouch = True
		EndIf
	EndIf
	
End Function

Function Use215() ;The first UE's object
	Local prevI215Timer# = I_215\Timer
	Local fs.FPS_Settings = First FPS_Settings
	
	If I_215\Using = 1 And (Not I_714\Using = 1) And (Not WearingGasMask = 3) And (Not WearingHazmat = 3) Then
        If I_215\IdleTimer >= 0 Then
            I_215\IdleTimer = Min(I_215\IdleTimer+fs\FPSfactor[0]*0.002,10)
        EndIf
        If I_215\IdleTimer >= 10
		    I_215\Timer = 1.0
		EndIf
	Else
	    I_215\IdleTimer = 0
    EndIf

    If I_215\Sound[0]=0 Then
		I_215\Sound[0] = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\215\Whisper.ogg"))
	EndIf

    If I_215\Timer > 0 Then
        ShouldPlay = 30
		ShowEntity at\OverlayID[12]
		
        If (Not I_714\Using = 1) And (Not WearingGasMask = 3) And (Not WearingHazmat = 3) Then
		    I_215\Timer = Min(I_215\Timer+fs\FPSfactor[0]*0.004,146)
        EndIf

        If I_215\Timer >= 1 Then
            I_215\IdleTimer = 0
        EndIf

		If I_215\Timer =< 100.0 Then
			EntityAlpha at\OverlayID[12], Min(((I_215\Timer*0.2)^2)/1000.0,0.5)
			BlurTimer = Max(I_215\Timer*3*(2.0-CrouchState),BlurTimer)
		EndIf
						
        If I_215\Timer > 20.0 And prevI215Timer =< 20.0 Then
            BlurTimer = 1900
        EndIf
        If I_215\Timer > 30.0 And prevI215Timer =< 30.0 Then
            BlurTimer = 3000
            Msg = scpLang_GetPhrase("ingame.scp2159")
			MsgTimer = 70*6
        EndIf
        If I_215\Timer > 35.0 And prevI215Timer =< 35.0 Then
            BlurTimer = 4000
        EndIf
		If I_215\Timer > 40.0 And prevI215Timer =< 40.0 Then
		    BlurTimer = 5000
			Msg = scpLang_GetPhrase("ingame.scp21510")
			MsgTimer = 70*6
			PlaySound_Strict I_215\Sound[0]
		EndIf
		If I_215\Timer > 45.0 And prevI215Timer =< 45.0 Then
		    Msg = Chr(34)+scpLang_GetPhrase("ingame.scp21511")+Chr(34)
		    MsgTimer = 70*6
		EndIf
		If I_215\Timer > 55.0
		    HeartBeatRate=Max(HeartBeatRate, 70+I_215\Timer)
			HeartBeatVolume = 1.0
		EndIf
	    If I_215\Timer > 60.0 And prevI215Timer =< 60.0 Then
	        Msg = Chr(34)+scpLang_GetPhrase("ingame.scp21512")+Chr(34)
	        MsgTimer = 70*6
	    EndIf
	    If I_215\Timer > 65.0 And prevI215Timer =< 65.0 Then
	        Msg = Chr(34)+scpLang_GetPhrase("ingame.scp21513")+Chr(34)
	        MsgTimer = 70*6
	    EndIf
		If I_215\Timer > 70.0 And prevI215Timer =< 70.0 Then
		    BlurTimer = 5200
		    CameraShake = 22
			Msg = scpLang_GetPhrase("ingame.scp21514")
			MsgTimer = 70*6
			PlaySound_Strict I_215\Sound[0]
		EndIf
	    If I_215\Timer > 75.0 And prevI215Timer =< 75.0 Then
	        CameraShake = 30
	        Msg = Chr(34)+scpLang_GetPhrase("ingame.scp21515")+Chr(34)
	        MsgTimer = 70*6
	    EndIf
		If I_215\Timer > 86.0 And prevI215Timer =< 86.0 Then
		    BlurTimer = 5300
		    CameraShake = 7
			PlaySound_Strict I_215\Sound[0]
		    For i=0 To MaxItemAmount-1
                If Inventory(i)<>Null Then
                    If Inventory(i)\itemtemplate\name="SCP-215" Then
                        Msg = scpLang_GetPhrase("ingame.scp21516")
			            MsgTimer = 70*6
			            If WearingGasMask Or I_1499\Using Or WearingHazmat Or WearingNightVision Or I_178\Using Then
			                I_215\Using = 1
			                WearingGasMask = 0
			                I_1499\Using = 0
			                WearingHazmat = 0
			                WearingNightVision = 0
			                I_178\Using = 0
			            EndIf
                        Exit
                    EndIf
                EndIf
            Next
		EndIf
	    If I_215\Timer > 90.0 And prevI215Timer =< 90.0 Then
	        CameraShake = 50
	        Msg = scpLang_GetPhrase("ingame.scp21517")
	        MsgTimer = 70*6
	    EndIf
	    If I_215\Timer > 94.0 And prevI215Timer =< 94.0 Then
	        CameraShake = 50
	        Msg = Chr(34)+scpLang_GetPhrase("ingame.scp21518")+Chr(34)
	        MsgTimer = 70*6
	    EndIf
	    If I_215\Timer > 96.0 And prevI215Timer =< 96.0 Then
	        BlurTimer = 7000
	        CameraShake = 50
	        Msg = Chr(34)+scpLang_GetPhrase("ingame.scp21519")+Chr(34)
	        MsgTimer = 70*6
	    EndIf
	    If I_215\Timer > 110.0 And prevI215Timer =< 110.0 Then
	        BlurTimer = 8000
	        CameraShake = 50
	        Msg = Chr(34)+scpLang_GetPhrase("ingame.scp21520")+Chr(34)
	        MsgTimer = 70*6
        EndIf
        If I_215\Timer > 135.0 And prevI215Timer =< 135.0 Then
            BlurTimer = 8000
	        CameraShake = 50
	        Msg = Chr(34)+scpLang_GetPhrase("ingame.scp21521")+Chr(34)
	        MsgTimer = 70*6
        EndIf
        If I_215\Timer > 146.0 And prevI215Timer =< 146.0 Then
            BlurTimer = 9000
            CameraShake = 100
            Msg = Chr(34)+scpLang_GetPhrase("ingame.scp21522")+Chr(34)
	        MsgTimer = 70*6
	    EndIf
	Else
		HideEntity at\OverlayID[12]	
	EndIf

End Function

Function Use207()
   
      Local prevI207Timer# = I_207\Timer
      Local fs.FPS_Settings = First FPS_Settings

      If I_207\Timer > 0.0 Then
          If (Not I_427\Using=1 And I_427\Timer < 70*360) Then
              I_207\Timer = Min(I_207\Timer+fs\FPSfactor[0]*0.002,51)
          EndIf
        
          If I_207\Timer > 20.0 Then
             HeartBeatRate=Max(HeartBeatRate, 70+I_207\Timer)
			 HeartBeatVolume = 1.0
		  EndIf
		
		  If I_207\Timer > 30.0 And prevI207Timer =< 30.0 Then
              CameraShake = 10.0
              BlurTimer = 600.0
		  EndIf
		
		  If I_207\Timer > 35.0 And prevI207Timer =< 35.0 Then
              CameraShake = 10.0
              BlurTimer = 600.0
		  EndIf

          If I_207\Timer >= 50.0 Then
              DeathMSG = scpLang_GetPhrase("items.scp2075")
              Kill()
          EndIf
      EndIf
End Function

Function Use402() 

    Local prevI402Timer# = I_402\Timer
    Local fs.FPS_Settings = First FPS_Settings

    If I_402\Using > 0 Then
        If I_402\Timer >= 0 Then
            I_402\Timer = Min(I_402\Timer+fs\FPSfactor[0]*0.004, 61)

            If I_402\Timer > 10.0 And prevI402Timer =< 10.0 Then
                PlaySound_Strict(CoughSFX(Rand(0, 2)))
                CameraShake = 5
                If I_1033RU\HP = 0
                    Msg = scpLang_GetPhrase("ingame.scp4024")
                    Injuries = Injuries + 0.1
                Else
                    Msg = scpLang_GetPhrase("ingame.scp4025")
                    Damage1033RU(10 + (Rand(5) * SelectedDifficulty\aggressiveNPCs))
                EndIf
                MsgTimer = 70*6
            EndIf

            If I_402\Timer > 15.0 And prevI402Timer =< 15.0 Then
                Msg = scpLang_GetPhrase("ingame.scp4026")
                MsgTimer = 70*6
            EndIf

            If I_1033RU\HP > 0
                If I_402\Timer > 12.0 Then
                    Msg = scpLang_GetPhrase("ingame.scp4027")
                    MsgTimer = 70*6
                    Damage1033RU(10 + (Rand(5) * SelectedDifficulty\aggressiveNPCs))
                    PlaySound_Strict(CoughSFX(Rand(0, 2)))
                    I_402\Using = 0
                EndIf
            EndIf
                 
            If I_402\Timer > 20.0 And prevI402Timer =< 20.0 Then
                Msg = scpLang_GetPhrase("ingame.scp4028")
                MsgTimer = 70*6
                CameraShake = 5
                Injuries = Injuries + 0.1
                Speed = Speed / 0.5
            EndIf
 
            If I_402\Timer > 40.0 And prevI402Timer =< 40.0 Then
                Msg = scpLang_GetPhrase("ingame.scp4029")
			    MsgTimer = 70*6
                PlaySound_Strict(CoughSFX(Rand(0, 2)))
                CameraShake = 5
                Injuries = Injuries + 0.1
   	        EndIf

            If I_402\Timer > 42.0 Then
                HeartBeatRate=Max(HeartBeatRate, 70+I_402\Timer)
			    HeartBeatVolume = 1.0
            EndIf
	
	        If I_402\Timer > 45.0 And prevI402Timer =< 45.0 Then
	            PlaySound_Strict(CoughSFX(Rand(0, 2)))
	        EndIf

	        If I_402\Timer > 50.0 And prevI402Timer =< 50.0 Then
	            Msg = Chr(34)+scpLang_GetPhrase("ingame.scp40210")+Chr(34)
	            MsgTimer = 70*6
	            PlaySound_Strict(CoughSFX(Rand(0, 2)))
	        EndIf

            If I_402\Timer > 55.0 And prevI402Timer =< 55.0 Then
	            PlaySound_Strict(CoughSFX(Rand(0, 2)))
	        EndIf
	
	        If I_402\Timer >= 60.0 And prevI402Timer =< 60.0 Then
	            PlaySound_Strict(CoughSFX(Rand(0, 2)))
                DeathMSG = scpLang_GetPhrase("ingame.scp40211")
		        Kill()
	        EndIf   
	    EndIf
	Else
	    I_402\Timer = 0
	EndIf
       
End Function

Function Use357() 
     Local prevI357Timer# = I_357\Timer
     Local i%
     Local fs.FPS_Settings = First FPS_Settings

     For i=0 To MaxItemAmount-1
         If Inventory(i)<>Null Then
             If Inventory(i)\itemtemplate\name="SCP-357" Then
                 If (Not I_427\Using=1 And I_427\Timer < 70*360) Then
                     I_357\Timer = Min(I_357\Timer+fs\FPSfactor[0]*0.004,75)
                 EndIf
             EndIf
         EndIf
     Next

     If I_357\Timer > 0 Then

         If I_357\Timer > 20.0 And prevI357Timer =< 20.0 Then
             BlurTimer = 1900
             Speed = Speed / 2.0
         EndIf
         If I_357\Timer > 30.0 And prevI357Timer =< 30.0 Then
             BlurTimer = 3000
         EndIf
         If I_357\Timer > 35.0 And prevI357Timer =< 35.0 Then
             BlurTimer = 4000
         EndIf
		 If I_357\Timer > 40.0 And prevI357Timer =< 40.0 Then
		     BlurTimer = 5000
			 Msg = scpLang_GetPhrase("ingame.scp3571")
			 MsgTimer = 70*6
		 EndIf
         If I_357\Timer > 56.0 And prevI357Timer =< 56.0 Then
             BlurTimer = 5000
             Msg = scpLang_GetPhrase("ingame.scp3572")
             MsgTimer = 70*6
         EndIf
         If I_357\Timer > 65.0 Then
	         HeartBeatRate=Max(HeartBeatRate, 70 + I_357\Timer)
			 HeartBeatVolume = 1.0
			 CameraShake = Sin(I_357\Timer / 5.0) * (I_357\Timer / 15.0)
		 EndIf 
         If I_357\Timer > 70.0 And prevI357Timer =< 70.0 Then
             BlurTimer = 5600
             If I_1033RU\HP = 0
                 Injuries = Injuries + 2.0
                 Playable = False
			     CanBreathe = False
			 Else
			     Damage1033RU(50 + (Rand(5) * SelectedDifficulty\aggressiveNPCs))
			 EndIf
		 EndIf
	     If I_357\Timer >= 75.0 Then
	         If Rand(2) = 1 Then
	             DeathMSG = scpLang_GetPhrase("ingame.scp3573")+" "
	         Else
	             DeathMSG = scpLang_GetPhrase("ingame.scp3574")+" "
             EndIf
	         DeathMSG = DeathMSG + scpLang_GetPhrase("ingame.scp3575")
	         Kill()
	     EndIf
     EndIf

End Function
 
Function UpdateMTF()
	If PlayerRoom\RoomTemplate\Name = "gateaentrance" Or PlayerRoom\RoomTemplate\Name = "gateb" Or PlayerRoom\RoomTemplate\Name = "room106" Or PlayerRoom\RoomTemplate\Name = "room457" Then Return
	
	Local r.Rooms, n.NPCs
	Local dist#, i%
	Local fs.FPS_Settings = First FPS_Settings
	
	If MTFtimer = 0 Then
		If Rand(30) = 1 And PlayerRoom\RoomTemplate\Name$ <> "dimension1499" Then
			
			Local entrance.Rooms = Null
			For r.Rooms = Each Rooms
				If Lower(r\RoomTemplate\Name) = "gateaentrance" Then entrance = r : Exit
			Next
			If entrance <> Null Then 
				If Abs(EntityZ(entrance\obj)-EntityZ(Collider))<30.0 Then
					If PlayerInReachableRoom()
						PlayAnnouncement(scpModding_ProcessFilePath$("SFX\"+"Character\MTF\Announc.ogg"))
						GiveAchievement(AchvMTF)
					EndIf
					
					MTFtimer = fs\FPSfactor[0]
					Local leader.NPCs
					For i = 0 To 2
						n.NPCs = CreateNPC(NPCtypeMTF, EntityX(entrance\obj)+0.3*(i-1), 1.0, EntityZ(entrance\obj) + 8.0)
						
						If i = 0 Then 
							leader = n
						Else
							n\MTFLeader = leader
						EndIf
						
						n\PrevX = i
					Next
				EndIf
			EndIf
		EndIf
	Else
		If MTFtimer <= 70*90
            MTFtimer = MTFtimer + fs\FPSfactor[0]
        ElseIf MTFtimer > 70*90 And MTFtimer < 70*91
            If PlayerInReachableRoom()
                PlayAnnouncement(scpModding_ProcessFilePath$("SFX\"+"Character\MTF2\Announc.ogg"))
            EndIf
            MTFtimer = 70*91
        ElseIf MTFtimer >= 70*91 And MTFtimer <= 70*131 ;70*120
			MTFtimer = MTFtimer + fs\FPSfactor[0]
		ElseIf MTFtimer > 70*131 And MTFtimer < 10000
			If PlayerInReachableRoom()
				PlayAnnouncement(scpModding_ProcessFilePath$("SFX\"+"Character\MTF\AnnouncAfter1.ogg"))
			EndIf
			MTFtimer = 10000
		ElseIf MTFtimer >= 10000 And MTFtimer <= 10000+(70*120) ;70*120
			MTFtimer = MTFtimer + fs\FPSfactor[0]
		ElseIf MTFtimer > 10000+(70*120) And MTFtimer < 20000
			If PlayerInReachableRoom()
				PlayAnnouncement(scpModding_ProcessFilePath$("SFX\"+"Character\MTF\AnnouncAfter2.ogg"))
			EndIf
			MTFtimer = 20000
		ElseIf MTFtimer >= 20000 And MTFtimer <= 20000+(70*60) ;70*120
			MTFtimer = MTFtimer + fs\FPSfactor[0]
		ElseIf MTFtimer > 20000+(70*60) And MTFtimer < 25000
			If PlayerInReachableRoom()
				;If the player has an SCP in their inventory play special voice line.
				For i = 0 To MaxItemAmount-1
					If Inventory(i) <> Null Then
						If (Left(Inventory(i)\itemtemplate\name, 4) = "SCP-") And (Left(Inventory(i)\itemtemplate\name, 7) <> "SCP-035") And (Left(Inventory(i)\itemtemplate\name, 7) <> "SCP-093") And (Left(Inventory(i)\itemtemplate\name, 7) <> "SCP-085") And (Left(Inventory(i)\itemtemplate\name, 7) <> "SCP-457") And (Left(Inventory(i)\itemtemplate\name, 7) <> "SCP-3651")
							PlayAnnouncement(scpModding_ProcessFilePath$("SFX\"+"Character\MTF\ThreatAnnouncPossession.ogg"))
							MTFtimer = 25000
							Return
							Exit
						EndIf
					EndIf
				Next
				
				PlayAnnouncement(scpModding_ProcessFilePath$("SFX\"+"Character\MTF\ThreatAnnounc"+Rand(1,3)+".ogg"))
			EndIf
			MTFtimer = 25000
			
		ElseIf MTFtimer >= 25000 And MTFtimer <= 25000+(70*60) ;70*120
			MTFtimer = MTFtimer + fs\FPSfactor[0]
		ElseIf MTFtimer > 25000+(70*60) And MTFtimer < 30000
			If PlayerInReachableRoom()
				PlayAnnouncement(scpModding_ProcessFilePath$("SFX\"+"Character\MTF\ThreatAnnouncFinal.ogg"))
			EndIf
			MTFtimer = 30000
			
		EndIf
	EndIf
	
End Function

Function UpdateMTF2()
	If PlayerRoom\RoomTemplate\Name = "gateaentrance" Or PlayerRoom\RoomTemplate\Name = "gateb" Or PlayerRoom\RoomTemplate\Name = "room106" Or PlayerRoom\RoomTemplate\Name = "room457" Then Return
	
	Local r.Rooms, n.NPCs
	Local dist#, i%
	Local fs.FPS_Settings = First FPS_Settings
	
	;mtf2 ei viel?� spawnannut, spawnataan jos pelaaja menee tarpeeksi l?�helle gate b:t?�
	If MTF2timer = 0 Then
		If Rand(30) = 1 And PlayerRoom\RoomTemplate\Name$ <> "dimension1499" Then
			
			Local entrance2.Rooms = Null
			For r.Rooms = Each Rooms
				If Lower(r\RoomTemplate\Name) = "gateb" Then entrance2 = r : Exit
			Next
			
			If entrance2 <> Null Then
			    If Abs(EntityZ(entrance2\obj)-EntityZ(Collider))<30.0 Then
					
				    MTF2timer = 1.0
				
				    Local leader2.NPCs
				    For i = 0 To 2
					    n.NPCs = CreateNPC(NPCtypeMTF2, EntityX(entrance2\obj)+0.3*(i-1), 1.0, EntityZ(entrance2\obj) + 8.0)
						
					    If i = 0 Then 
						    leader2 = n
					    Else
						    n\MTF2Leader = leader2
					    EndIf
						
					    n\PrevX = i
				    Next
				EndIf
			EndIf
		EndIf		
	EndIf
	
End Function

Function Update409()
    Local Kill%
	Local prevI409Timer# = I_409\Timer
	Local fs.FPS_Settings = First FPS_Settings
	
	If I_409\Sound[0]=0 Then
		I_409\Sound[0] = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\D9341\Damage10.ogg"))
	EndIf
	If I_409\Timer > 0 And I_409\Timer =< 100 Then
		ShowEntity at\OverlayID[10]
		
		If (Not I_427\Using=1 And I_427\Timer < 70*360) Then
			I_409\Timer = ((Min(I_409\Timer+fs\FPSfactor[0]*0.004,100)))
		EndIf	
		EntityAlpha at\OverlayID[10], Min(((I_409\Timer*0.2)^2)/1000.0,0.5)
	    BlurTimer = Max(I_409\Timer*3*(2.0-CrouchState),BlurTimer)

        If I_409\Timer > 40.0 And prevI409Timer =< 40.0 Then
			Msg = scpLang_GetPhrase("ingame.scp4091")
			MsgTimer = 70*6
		ElseIf I_409\Timer > 55.0 And prevI409Timer =< 55.0 Then
			Msg = scpLang_GetPhrase("ingame.scp4092")
			MsgTimer = 70*6
		ElseIf I_409\Timer > 70.0 And prevI409Timer =< 70.0 Then
			Msg = scpLang_GetPhrase("ingame.scp4093")
			MsgTimer = 70*6
		ElseIf I_409\Timer > 85.0 And prevI409Timer =< 85.0 Then
			Msg = scpLang_GetPhrase("ingame.scp4094")
			MsgTimer = 70*6
		ElseIf I_409\Timer > 93.0 And prevI409Timer =< 93.0 Then
			PlaySound_Strict(I_409\Sound[0])
			Injuries = Max(Injuries,2.0)
		ElseIf I_409\Timer > 94.0 Then
			I_409\Timer = Min(I_409\Timer+fs\FPSfactor[0]*0.004, 100)
			Playable = False
			CanBreathe = False
			BlurTimer = 4.0
			CameraShake = 3.0
		EndIf
		If I_409\Timer >= 96.9222
			DeathMSG = scpLang_GetPhrase("ingame.scp4095")
			Kill(True)
        EndIf
    Else
		HideEntity at\OverlayID[10]	
    EndIf

End Function

Function Update008()
	Local i%, r.Rooms
	Local teleportForInfect% = True
	Local prevI008Timer# = I_008\Timer
	Local fs.FPS_Settings = First FPS_Settings
	
	If PlayerRoom\RoomTemplate\Name = "room860"
		For e.Events = Each Events
			If e\EventName = "room860"
				If e\EventState = 1.0
					teleportForInfect = False
				EndIf
				Exit
			EndIf
		Next
	ElseIf PlayerRoom\RoomTemplate\Name = "dimension1499" Or PlayerRoom\RoomTemplate\Name = "pocketdimension" Or PlayerRoom\RoomTemplate\Name = "gatea"
		teleportForInfect = False
	ElseIf PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(Collider)>1040.0*RoomScale
		teleportForInfect = False
	EndIf

	If I_008\Timer > 0 Then
		ShowEntity at\OverlayID[2]
		
	    For i = 0 To 6
	        If I_008\Sound[i] = 0
		        I_008\Sound[i] = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\008\Voices"+i+".ogg"))
		    EndIf
		Next
	    For i = 0 To 1
	        If I_008\Sound2[i] = 0 
		        I_008\Sound2[i] = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\008\KillScientist"+i+".ogg"))
		    EndIf
		Next
		
		If I_008\Timer < 93.0 Then
			If (Not I_427\Using And I_427\Timer < 70*360) Then
				I_008\Timer = Min(I_008\Timer+fs\FPSfactor[0]*0.002,100)
			EndIf
			
			BlurTimer = Max(I_008\Timer*3*(2.0-CrouchState),BlurTimer)
			
			HeartBeatRate = Max(HeartBeatRate, 100)
			HeartBeatVolume = Max(HeartBeatVolume, I_008\Timer/120.0)
			
			EntityAlpha at\OverlayID[2], Min(((I_008\Timer*0.2)^2)/1000.0,0.5) * (Sin(MilliSecs2()/8.0)+2.0)
			
			For i = 0 To 6
				If I_008\Timer>i*15+10 And prevI008Timer =< i*15+10 Then
					PlaySound_Strict I_008\Sound[Rand(0, 6)]
				EndIf
			Next
			
			If I_008\Timer > 20 And prevI008Timer =< 20.0 Then
				Msg = scpLang_GetPhrase("ingame.scp0081")
				MsgTimer = 70*6
			ElseIf I_008\Timer > 40 And prevI008Timer =< 40.0
				Msg = scpLang_GetPhrase("ingame.scp0082")
				MsgTimer = 70*6
			ElseIf I_008\Timer > 60 And prevI008Timer =< 60.0
				Msg = scpLang_GetPhrase("ingame.scp0083")
				MsgTimer = 70*6
			ElseIf I_008\Timer > 80 And prevI008Timer =< 80.0
				Msg = scpLang_GetPhrase("ingame.scp0084")
				MsgTimer = 70*6
			ElseIf I_008\Timer => 91.5
				BlinkTimer = Max(Min(-10*(I_008\Timer-91.5), BlinkTimer),-10)
				I_008\Zombie = True
				UnableToMove = True
				If I_008\Timer >= 92.7 And prevI008Timer < 92.7 Then
					If teleportForInfect
						For r.Rooms = Each Rooms
							If r\RoomTemplate\Name="room008" Then
								PositionEntity Collider, EntityX(r\Objects[7],True),EntityY(r\Objects[7],True),EntityZ(r\Objects[7],True),True
								ResetEntity Collider
								r\NPC[0] = CreateNPC(NPCtypeD, EntityX(r\Objects[6],True),EntityY(r\Objects[6],True)+0.2,EntityZ(r\Objects[6],True))
								r\NPC[0]\SoundCHN = PlaySound_Strict(I_008\Sound2[0])
								ChangeNPCTextureID(r\NPC[0], 16)
								r\NPC[0]\State=6
								PlayerRoom = r
								UnableToMove = False
								Exit
							EndIf
						Next
					EndIf
				EndIf
			EndIf
		Else
			I_008\Timer = Min(I_008\Timer+fs\FPSfactor[0]*0.004,100)
			
			If teleportForInfect
				If I_008\Timer < 94.7 Then
					EntityAlpha at\OverlayID[2], 0.5 * (Sin(MilliSecs2()/8.0)+2.0)
					BlurTimer = 900
					
					If I_008\Timer > 94.5 Then BlinkTimer = Max(Min(-50*(I_008\Timer-94.5), BlinkTimer),-10)
					PointEntity Collider, PlayerRoom\NPC[0]\Collider
					PointEntity PlayerRoom\NPC[0]\Collider, Collider
					PointEntity Camera, PlayerRoom\NPC[0]\Collider,EntityRoll(Camera)
					ForceMove = 0.75
					Injuries = 2.5
					Bloodloss = 0
					I_1079\Foam = 0
					I_1079\Trigger = 0
					UnableToMove = False
					
					Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 357, 381, 0.3)
				ElseIf I_008\Timer < 98.5
					
					EntityAlpha at\OverlayID[2], 0.5 * (Sin(MilliSecs2()/5.0)+2.0)
					BlurTimer = 950
					
					ForceMove = 0.0
					UnableToMove = True
					PointEntity Camera, PlayerRoom\NPC[0]\Collider
					
					If prevI008Timer < 94.7 Then 
						PlayerRoom\NPC[0]\SoundCHN = PlaySound_Strict(I_008\Sound2[1])
						
						DeathMSG = scpLang_GetPhrase("ingame.scp0085")
						
						Kill()
						de.Decals = CreateDecal(3, EntityX(PlayerRoom\NPC[0]\Collider), 544*RoomScale + 0.01, EntityZ(PlayerRoom\NPC[0]\Collider),90,Rnd(360),0)
						de\Size = 0.8
						ScaleSprite(de\obj, de\Size,de\Size)
					ElseIf I_008\Timer > 96
						BlinkTimer = Max(Min(-10*(I_008\Timer-96),BlinkTimer),-10)
					Else
						KillTimer = Max(-350, KillTimer)
					EndIf
					
					If PlayerRoom\NPC[0]\State2=0 Then
						Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 13, 19, 0.3,False)
						If AnimTime(PlayerRoom\NPC[0]\obj) => 19 Then PlayerRoom\NPC[0]\State2=1
					Else
						Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 19, 13, -0.3)
						If AnimTime(PlayerRoom\NPC[0]\obj) =< 13 Then PlayerRoom\NPC[0]\State2=0
					EndIf
					
					If ParticleAmount>0
						If Rand(50)=1 Then
							p.Particles = CreateParticle(EntityX(PlayerRoom\NPC[0]\Collider),EntityY(PlayerRoom\NPC[0]\Collider),EntityZ(PlayerRoom\NPC[0]\Collider), 5, Rnd(0.05,0.1), 0.15, 200)
							p\speed = 0.01
							p\SizeChange = 0.01
							p\A = 0.5
							p\Achange = -0.01
							RotateEntity p\pvt, Rnd(360),Rnd(360),0
						EndIf
					EndIf
					
					PositionEntity Head, EntityX(PlayerRoom\NPC[0]\Collider,True), EntityY(PlayerRoom\NPC[0]\Collider,True)+0.65,EntityZ(PlayerRoom\NPC[0]\Collider,True),True
					RotateEntity Head, (1.0+Sin(MilliSecs2()/5.0))*15, PlayerRoom\angle-180, 0, True
					MoveEntity Head, 0,0,-0.4
					TurnEntity Head, 80+(Sin(MilliSecs2()/5.0))*30,(Sin(MilliSecs2()/5.0))*40,0
				EndIf
			Else
				Kill()
				BlinkTimer = Max(Min(-10*(I_008\Timer-96), BlinkTimer),-10)
				If PlayerRoom\RoomTemplate\Name = "dimension1499" Then
					DeathMSG = scpLang_GetPhrase("ingame.scp0086")
				ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Or PlayerRoom\RoomTemplate\Name = "gateb" Then
					DeathMSG = SubjectName$+" " + scpLang_GetPhrase("ingame.scp0087") + " "
					If PlayerRoom\RoomTemplate\Name = "gatea" Then
						DeathMSG = DeathMSG + "A"
					Else
						DeathMSG = DeathMSG + "B"
					EndIf
					DeathMSG = DeathMSG + scpLang_GetPhrase("ingame.scp0088")
				Else
					DeathMSG = ""
				EndIf
			EndIf
		EndIf
		
		
	Else
		HideEntity at\OverlayID[2]
	EndIf
End Function

;--------------------------------------- decals -------------------------------------------------------

Type Decals
	Field obj%
	Field SizeChange#, Size#, MaxSize#
	Field AlphaChange#, Alpha#
	Field blendmode%
	Field fx%
	Field ID%
	Field Timer#
	
	Field lifetime#
	
	Field x#, y#, z#
	Field pitch#, yaw#, roll#
End Type

Function CreateDecal.Decals(id%, x#, y#, z#, pitch#, yaw#, roll#)
	Local d.Decals = New Decals
	
	d\x = x
	d\y = y
	d\z = z
	d\pitch = pitch
	d\yaw = yaw
	d\roll = roll
	
	d\MaxSize = 1.0
	
	d\Alpha = 1.0
	d\Size = 1.0
	d\obj = CreateSprite()
	d\blendmode = 1
	
	EntityTexture(d\obj, at\DecalTextureID[id])
	EntityFX(d\obj, 0)
	SpriteViewMode(d\obj, 2)
	PositionEntity(d\obj, x, y, z)
	RotateEntity(d\obj, pitch, yaw, roll)
	
	d\ID = id
	
	If at\DecalTextureID[id] = 0 Or d\obj = 0 Then Return Null
	
	Return d
End Function

Function UpdateDecals()
	Local d.Decals
	Local fs.FPS_Settings = First FPS_Settings
	For d.Decals = Each Decals
		If d\SizeChange <> 0 Then
			d\Size=d\Size + d\SizeChange * fs\FPSfactor[0]
			ScaleSprite(d\obj, d\Size, d\Size)
			
			Select d\ID
				Case 0
					If d\Timer <= 0 Then
						Local angle# = Rand(360)
						Local temp# = Rnd(d\Size)
						Local d2.Decals = CreateDecal(1, EntityX(d\obj) + Cos(angle) * temp, EntityY(d\obj) - 0.0005, EntityZ(d\obj) + Sin(angle) * temp, EntityPitch(d\obj), Rnd(360), EntityRoll(d\obj))
						d2\Size = Rnd(0.1, 0.5) : ScaleSprite(d2\obj, d2\Size, d2\Size)
						PlaySound2(DecaySFX(Rand(1, 3)), Camera, d2\obj, 10.0, Rnd(0.1, 0.5))
						;d\Timer = d\Timer + Rand(50,150)
						d\Timer = Rand(50, 100)
					Else
						d\Timer= d\Timer-fs\FPSfactor[0]
					End If
				;Case 6
				;	EntityBlend d\obj, 2
			End Select
			
			If d\Size >= d\MaxSize Then d\SizeChange = 0 : d\Size = d\MaxSize
		End If
		
		If d\AlphaChange <> 0 Then
			d\Alpha = Min(d\Alpha + fs\FPSfactor[0] * d\AlphaChange, 1.0)
			EntityAlpha(d\obj, d\Alpha)
		End If
		
		If d\lifetime > 0 Then
			d\lifetime=Max(d\lifetime-fs\FPSfactor[0],5)
		EndIf
		
		If d\Size <= 0 Or d\Alpha <= 0 Or d\lifetime=5.0  Then
			FreeEntity(d\obj)
			Delete d
		End If
	Next
End Function

;--------------------------------------- MakeCollBox -functions -------------------------------------------------------


; Create a collision box For a mesh entity taking into account entity scale
; (will not work in non-uniform scaled space)
Function MakeCollBox(mesh%)
	Local sx# = EntityScaleX(mesh, 1)
	Local sy# = Max(EntityScaleY(mesh, 1), 0.001)
	Local sz# = EntityScaleZ(mesh, 1)
	GetMeshExtents(mesh)
	EntityBox mesh, Mesh_MinX * sx, Mesh_MinY * sy, Mesh_MinZ * sz, Mesh_MagX * sx, Mesh_MagY * sy, Mesh_MagZ * sz
End Function

; Find mesh extents
Function GetMeshExtents(Mesh%)
	Local s%, surf%, surfs%, v%, verts%, x#, y#, z#
	Local minx# = Infinity
	Local miny# = Infinity
	Local minz# = Infinity
	Local maxx# = -Infinity
	Local maxy# = -Infinity
	Local maxz# = -Infinity
	
	surfs = CountSurfaces(Mesh)
	
	For s = 1 To surfs
		surf = GetSurface(Mesh, s)
		verts = CountVertices(surf)
		
		For v = 0 To verts - 1
			x = VertexX(surf, v)
			y = VertexY(surf, v)
			z = VertexZ(surf, v)
			
			If (x < minx) Then minx = x
			If (x > maxx) Then maxx = x
			If (y < miny) Then miny = y
			If (y > maxy) Then maxy = y
			If (z < minz) Then minz = z
			If (z > maxz) Then maxz = z
		Next
	Next
	
	Mesh_MinX = minx
	Mesh_MinY = miny
	Mesh_MinZ = minz
	Mesh_MaxX = maxx
	Mesh_MaxY = maxy
	Mesh_MaxZ = maxz
	Mesh_MagX = maxx-minx
	Mesh_MagY = maxy-miny
	Mesh_MagZ = maxz-minz
	
End Function

Function EntityScaleX#(entity%, globl% = False)
	If globl Then TFormVector 1, 0, 0, entity, 0 Else TFormVector 1, 0, 0, entity, GetParent(entity)
	Return Sqr(TFormedX() * TFormedX() + TFormedY() * TFormedY() + TFormedZ() * TFormedZ())
End Function 

Function EntityScaleY#(entity%, globl% = False)
	If globl Then TFormVector 0, 1, 0, entity, 0 Else TFormVector 0, 1, 0, entity, GetParent(entity)
	Return Sqr(TFormedX() * TFormedX() + TFormedY() * TFormedY() + TFormedZ() * TFormedZ())
End Function 

Function EntityScaleZ#(entity%, globl% = False)
	If globl Then TFormVector 0, 0, 1, entity, 0 Else TFormVector 0, 0, 1, entity, GetParent(entity)
	Return Sqr(TFormedX() * TFormedX() + TFormedY() * TFormedY() + TFormedZ() * TFormedZ())
End Function 

Function Graphics3DExt%(width%,height%,depth%=32,mode%=2)
	;If FE_InitExtFlag = 1 Then DeInitExt() ;prevent FastExt from breaking itself
	Graphics3D width,height,depth,mode
	InitFastResize()
	;InitExt()
	AntiAlias GetINIInt(OptionFile,"options","antialias")
	;TextureAnisotropy% (GetINIInt(OptionFile,"options","anisotropy"),-1)
End Function

Function ResizeImage2(image%,width%,height%)
    img% = CreateImage(width,height)
	
	oldWidth% = ImageWidth(image)
	oldHeight% = ImageHeight(image)
	CopyRect 0,0,oldWidth,oldHeight,1024-oldWidth/2,1024-oldHeight/2,ImageBuffer(image),TextureBuffer(fresize_texture)
	SetBuffer BackBuffer()
	ScaleRender(0,0,2048.0 / Float(RealGraphicWidth) * Float(width) / Float(oldWidth), 2048.0 / Float(RealGraphicWidth) * Float(height) / Float(oldHeight))
	;might want to replace Float(GraphicWidth) with Max(GraphicWidth,GraphicHeight) if portrait sizes cause issues
	;everyone uses landscape so it's probably a non-issue
	CopyRect RealGraphicWidth/2-width/2,RealGraphicHeight/2-height/2,width,height,0,0,BackBuffer(),ImageBuffer(img)
	
    FreeImage image
    Return img
End Function


Function RenderWorld2()
    Local fo.Fonts = First Fonts
    Local fs.FPS_Settings = First FPS_Settings
	CameraProjMode ark_blur_cam,0
	CameraProjMode Camera,1
	
	If WearingNightVision>0 And WearingNightVision<3 Then
		AmbientLight Min(Brightness*2,255), Min(Brightness*2,255), Min(Brightness*2,255)
	ElseIf WearingNightVision=3
		AmbientLight 255,255,255
	ElseIf PlayerRoom<>Null
		If (PlayerRoom\RoomTemplate\Name<>"room173intro") And (PlayerRoom\RoomTemplate\Name<>"gateb") And (PlayerRoom\RoomTemplate\Name<>"gatea") Then
			AmbientLight Brightness, Brightness, Brightness
		EndIf
	EndIf
	
	IsNVGBlinking% = False
	HideEntity at\OverlayID[4]
	
	CameraViewport Camera,0,0,GraphicWidth,GraphicHeight
	
	Local hasBattery% = 2
	Local power% = 0
	If (WearingNightVision=1) Or (WearingNightVision=2)
		For i% = 0 To MaxItemAmount - 1
			If (Inventory(i)<>Null) Then
				If (WearingNightVision = 1 And Inventory(i)\itemtemplate\tempname = "nvgoggles") Or (WearingNightVision = 2 And Inventory(i)\itemtemplate\tempname = "supernv") Then
					Inventory(i)\state = Inventory(i)\state - (fs\FPSfactor[0] * (0.02 * WearingNightVision))
					power%=Int(Inventory(i)\state)
					If Inventory(i)\state<=0.0 Then ;this nvg can't be used
						hasBattery = 0
						Msg = scpLang_GetPhrase("ingame.nvgdead")
						BlinkTimer = -1.0
						MsgTimer = 350
						Exit
					ElseIf Inventory(i)\state<=100.0 Then
						hasBattery = 1
					EndIf
				EndIf
			EndIf
		Next
		
		If (hasBattery) Then
			RenderWorld()
		EndIf
	Else
		RenderWorld()
	EndIf
	
	CurrTrisAmount = TrisRendered()

	If hasBattery=0 And WearingNightVision<>3
		IsNVGBlinking% = True
		ShowEntity at\OverlayID[4]
	EndIf
	
	If BlinkTimer < - 16 Or BlinkTimer > - 6
		If WearingNightVision=2 And hasBattery<>0 Then ;show a HUD
			NVTimer=NVTimer-fs\FPSfactor[0]
			
			If NVTimer<=0.0 Then
				For np.NPCs = Each NPCs
					np\NVX = EntityX(np\Collider,True)
					np\NVY = EntityY(np\Collider,True)
					np\NVZ = EntityZ(np\Collider,True)
				Next
				IsNVGBlinking% = True
				ShowEntity at\OverlayID[4]
				If NVTimer<=-10
				    NVTimer = 600.0
			    EndIf
			EndIf
			
			Color 255,255,255
			
			AASetFont fo\Font[2]			
			Local plusY% = 0
			If hasBattery=1 Then plusY% = 40
			
			AAText GraphicWidth/2,(20+plusY)*MenuScale,scpLang_GetPhrase("ingame.nvgr"),True,False
			
			AAText GraphicWidth/2,(60+plusY)*MenuScale,Max(f2s(NVTimer/60.0,1),0.0),True,False
			AAText GraphicWidth/2,(100+plusY)*MenuScale,scpLang_GetPhrase("ingame.nvgr2"),True,False
			
			temp% = CreatePivot() : temp2% = CreatePivot()
			PositionEntity temp, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
			
			Color 255,255,255;*(NVTimer/600.0)
			
			For np.NPCs = Each NPCs
				If np\NVName<>"" And (Not np\HideFromNVG) Then ;don't waste your time if the string is empty
					PositionEntity temp2,np\NVX,np\NVY,np\NVZ
					dist# = EntityDistance(temp2,Collider)
					If dist<23.5 Then ;don't draw text if the NPC is too far away
						PointEntity temp, temp2
						yawvalue# = WrapAngle(EntityYaw(Camera) - EntityYaw(temp))
						xvalue# = 0.0
						If yawvalue > 90 And yawvalue <= 180 Then
							xvalue# = Sin(90)/90*yawvalue
						Else If yawvalue > 180 And yawvalue < 270 Then
							xvalue# = Sin(270)/yawvalue*270
						Else
							xvalue = Sin(yawvalue)
						EndIf
						pitchvalue# = WrapAngle(EntityPitch(Camera) - EntityPitch(temp))
						yvalue# = 0.0
						If pitchvalue > 90 And pitchvalue <= 180 Then
							yvalue# = Sin(90)/90*pitchvalue
						Else If pitchvalue > 180 And pitchvalue < 270 Then
							yvalue# = Sin(270)/pitchvalue*270
						Else
							yvalue# = Sin(pitchvalue)
						EndIf
						
						If (Not IsNVGBlinking%)
						    AAText GraphicWidth / 2 + xvalue * (GraphicWidth / 2),GraphicHeight / 2 - yvalue * (GraphicHeight / 2),np\NVName,True,True
						    AAText GraphicWidth / 2 + xvalue * (GraphicWidth / 2),GraphicHeight / 2 - yvalue * (GraphicHeight / 2) + 30.0 * MenuScale,f2s(dist,1)+" m",True,True
					    EndIf
				    EndIf
				EndIf
			Next
			
			FreeEntity (temp) : FreeEntity (temp2)
			
			Color 0,0,55
			For k=0 To 10
				Rect 45,GraphicHeight*0.5-(k*20),54,10,True
			Next
			Color 0,0,255
			For l=0 To Floor((power%+50)*0.01)
				Rect 45,GraphicHeight*0.5-(l*20),54,10,True
			Next
			DrawImage NVGImages,40,GraphicHeight*0.5+30,1
			
			Color 255,255,255
		ElseIf WearingNightVision=1 And hasBattery<>0
			Color 0,55,0
			For k=0 To 10
				Rect 45,GraphicHeight*0.5-(k*20),54,10,True
			Next
			Color 0,255,0
			For l=0 To Floor((power%+50)*0.01)
				Rect 45,GraphicHeight*0.5-(l*20),54,10,True
			Next
			DrawImage NVGImages,40,GraphicHeight*0.5+30,0
		EndIf
	EndIf
	
	;render sprites
	CameraProjMode ark_blur_cam,2
	CameraProjMode Camera,0
	RenderWorld()
	CameraProjMode ark_blur_cam,0
	
	If BlinkTimer < - 16 Or BlinkTimer > - 6
		If (WearingNightVision=1 Or WearingNightVision=2) And (hasBattery=1) And ((MilliSecs2() Mod 800) < 400) Then
			Color 255,0,0
			AASetFont fo\Font[2]
			
			AAText GraphicWidth/2,20*MenuScale,scpLang_GetPhrase("ingame.nvgr3"),True,False
			Color 255,255,255
		EndIf
	EndIf
End Function


Function ScaleRender(x#,y#,hscale#=1.0,vscale#=1.0)
	If Camera<>0 Then HideEntity Camera
	WireFrame 0
	ShowEntity fresize_image
	ScaleEntity fresize_image,hscale,vscale,1.0
	PositionEntity fresize_image, x, y, 1.0001
	ShowEntity fresize_cam
	RenderWorld()
	HideEntity fresize_cam
	HideEntity fresize_image
	WireFrame WireframeState
	If Camera<>0 Then ShowEntity Camera
End Function

Function InitFastResize()
    ;Create Camera
	Local cam% = CreateCamera()
	CameraProjMode cam, 2
	CameraZoom cam, 0.1
	CameraClsMode cam, 0, 0
	CameraRange cam, 0.1, 1.5
	MoveEntity cam, 0, 0, -10000
	
	fresize_cam = cam
	
    ;ark_sw = GraphicsWidth()
    ;ark_sh = GraphicsHeight()
	
    ;Create sprite
	Local spr% = CreateMesh(cam)
	Local sf% = CreateSurface(spr)
	AddVertex sf, -1, 1, 0, 0, 0
	AddVertex sf, 1, 1, 0, 1, 0
	AddVertex sf, -1, -1, 0, 0, 1
	AddVertex sf, 1, -1, 0, 1, 1
	AddTriangle sf, 0, 1, 2
	AddTriangle sf, 3, 2, 1
	EntityFX spr, 17
	ScaleEntity spr, 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicHeight), 1
	PositionEntity spr, 0, 0, 1.0001
	EntityOrder spr, -100001
	EntityBlend spr, 1
	fresize_image = spr
	
    ;Create texture
	fresize_texture = CreateTexture(2048, 2048, 1+256)
	fresize_texture2 = CreateTexture(2048, 2048, 1+256)
	TextureBlend fresize_texture2,3
	SetBuffer(TextureBuffer(fresize_texture2))
	ClsColor 0,0,0
	Cls
	SetBuffer(BackBuffer())
	;TextureAnisotropy(fresize_texture)
	EntityTexture spr, fresize_texture,0,0
	EntityTexture spr, fresize_texture2,0,1
	
	HideEntity fresize_cam
End Function

;--------------------------------------- Some new 1.3 -functions -------------------------------------------------------

Function UpdateLeave1499()
	Local r.Rooms, it.Items,r2.Rooms,i%
	Local r1499.Rooms
	
	If (Not I_1499\Using) And PlayerRoom\RoomTemplate\Name$ = "dimension1499"
		For r.Rooms = Each Rooms
			If r = I_1499\PrevRoom
				BlinkTimer = -1
				I_1499\X# = EntityX(Collider)
				I_1499\Y# = EntityY(Collider)
				I_1499\Z# = EntityZ(Collider)
				PositionEntity (Collider, I_1499\PrevX#, I_1499\PrevY#+0.05, I_1499\PrevZ#)
				ResetEntity(Collider)
				PlayerRoom = r
				UpdateDoors()
				UpdateRooms()
				If PlayerRoom\RoomTemplate\Name = "room3storage"
					If EntityY(Collider)<-4600*RoomScale
						For i = 0 To 3
							PlayerRoom\NPC[i]\State = 2
							PositionEntity(PlayerRoom\NPC[i]\Collider, EntityX(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True),EntityY(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True)+0.2,EntityZ(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True))
							ResetEntity PlayerRoom\NPC[i]\Collider
							PlayerRoom\NPC[i]\State2 = PlayerRoom\NPC[i]\State2 + 1
							If PlayerRoom\NPC[i]\State2 > PlayerRoom\NPC[i]\PrevState Then PlayerRoom\NPC[i]\State2 = (PlayerRoom\NPC[i]\PrevState-3)
						Next
					EndIf
				ElseIf PlayerRoom\RoomTemplate\Name = "pocketdimension"
					CameraFogColor Camera, 0,0,0
					CameraClsColor Camera, 0,0,0
				ElseIf PlayerRoom\RoomTemplate\Name = "room457"
				    If EntityY(Collider) < - 2400.0 * RoomScale
				        If (Not Contained457) Then
				            For e.Events = Each Events
				                If e\EventName = "room457"
				                    PositionEntity(PlayerRoom\NPC[0]\Collider, EntityX(PlayerRoom\Objects[Int(e\EventState-1)], True), EntityY(PlayerRoom\Objects[Int(e\EventState-1)], True) + 0.1, EntityZ(PlayerRoom\Objects[Int(e\EventState-1)], True))
				                    If PlayerRoom\NPC[0]\PathStatus = 1
				                        PlayerRoom\NPC[0]\PathStatus = 0
				                    EndIf
				                EndIf
				            Next
						Else
				            PositionEntity(PlayerRoom\NPC[0]\Collider, EntityX(PlayerRoom\Objects[4], True), EntityY(PlayerRoom\Objects[4], True) + 0.1, EntityZ(PlayerRoom\Objects[4], True))
                        EndIf 
                        PlayerRoom\NPC[0]\State = 0
                        ResetEntity PlayerRoom\NPC[0]\Collider
                    EndIf
                EndIf
				For r2.Rooms = Each Rooms
					If r2\RoomTemplate\Name = "dimension1499"
						r1499 = r2
						Exit
					EndIf
				Next
				For it.Items = Each Items
					it\disttimer = 0
					If it\itemtemplate\tempname = "scp1499" Or it\itemtemplate\tempname = "super1499"
						If EntityY(it\collider) >= EntityY(r1499\obj)-5
							PositionEntity it\collider,I_1499\PrevX#,I_1499\PrevY#+(EntityY(it\collider)-EntityY(r1499\obj)),I_1499\PrevZ#
							ResetEntity it\collider
							Exit
						EndIf
					EndIf
				Next
				r1499 = Null
				ShouldEntitiesFall = False
				PlaySound_Strict (LoadTempSound(scpModding_ProcessFilePath$("SFX\"+"SCP\1499\Exit.ogg")))
				I_1499\PrevX# = 0.0
				I_1499\PrevY# = 0.0
				I_1499\PrevZ# = 0.0
				I_1499\PrevRoom = Null
				Exit
			EndIf
		Next
	EndIf
	
End Function

Function CheckForPlayerInFacility()
	;False (=0): NPC is not in facility (mostly meant for "dimension1499")
	;True (=1): NPC is in facility
	;2: NPC is in tunnels (maintenance tunnels/049 tunnels/939 storage room, etc...)
	
	If EntityY(Collider)>100.0
		Return False
	EndIf
	If EntityY(Collider)< -10.0
		Return 2
	EndIf
	If EntityY(Collider)> 7.0 And EntityY(Collider)<=100.0
		Return 2
	EndIf
	
	Return True
End Function

Function IsItemGoodFor1162(itt.ItemTemplates)
	Local IN$ = itt\tempname$
	
	Select itt\tempname
		Case "key0", "key1", "key2", "key3"
			Return True
		Case "misc", "scp420j", "cigarette"
			Return True
		Case "vest", "finevest","gasmask","helmet"
			Return True
		Case "radio","18vradio"
			Return True
		Case "clipboard", "eyedrops", "eyedrops2", "nvgoggles", "minteyedrops", "minteyedrops2"
			Return True
		Case "scp198", "scp357"
			Return True
		Case "drawing"
			If itt\img<>0 Then FreeImage itt\img	
			itt\img = LoadImage_Strict(scpModding_ProcessFilePath$("GFX\items\"+"1048\1048_"+Rand(1,25)+".jpg")) ;Gives a random drawing.
			Return True
		Default
			If itt\tempname <> "paper" Then
				Return False
			Else If Instr(itt\name, "Leaflet")
				Return False
			Else
				;if the item is a paper, only allow spawning it if the name contains the word "note" or "log"
				;(because those are items created recently, which D-9341 has most likely never seen)
				Return ((Not Instr(itt\name, "Note")) And (Not Instr(itt\name, "Log")))
			EndIf
	End Select
End Function

Function ControlSoundVolume()
	Local snd.Sound,i
	
	For snd.Sound = Each Sound
		For i=0 To 31
			ChannelVolume snd\channels[i],SFXVolume#
		Next
	Next
	
End Function

Function UpdateDeafPlayer()
	Local fs.FPS_Settings = First FPS_Settings
	
	If DeafTimer > 0
		DeafTimer = DeafTimer-fs\FPSfactor[0]
		SFXVolume# = 0.0
		If SFXVolume# > 0.0
			ControlSoundVolume()
		EndIf
	Else
		DeafTimer = 0
		SFXVolume# = PrevSFXVolume#
		If DeafPlayer Then ControlSoundVolume()
		DeafPlayer = False
	EndIf
	
End Function

Function CheckTriggers$()
	Local i%,sx#,sy#,sz#
	Local inside% = -1
	
	If PlayerRoom\TriggerboxAmount = 0
		Return ""
	Else
		For i = 0 To PlayerRoom\TriggerboxAmount-1
			EntityAlpha PlayerRoom\Triggerbox[i],1.0
			sx# = EntityScaleX(PlayerRoom\Triggerbox[i], 1)
			sy# = Max(EntityScaleY(PlayerRoom\Triggerbox[i], 1), 0.001)
			sz# = EntityScaleZ(PlayerRoom\Triggerbox[i], 1)
			GetMeshExtents(PlayerRoom\Triggerbox[i])
			If DebugHUD
				EntityColor PlayerRoom\Triggerbox[i],255,255,0
				EntityAlpha PlayerRoom\Triggerbox[i],0.2
			Else
				EntityColor PlayerRoom\Triggerbox[i],255,255,255
				EntityAlpha PlayerRoom\Triggerbox[i],0.0
 			EndIf
			If EntityX(Collider)>((sx#*Mesh_MinX)+PlayerRoom\x) And EntityX(Collider)<((sx#*Mesh_MaxX)+PlayerRoom\x)
				If EntityY(Collider)>((sy#*Mesh_MinY)+PlayerRoom\y) And EntityY(Collider)<((sy#*Mesh_MaxY)+PlayerRoom\y)
					If EntityZ(Collider)>((sz#*Mesh_MinZ)+PlayerRoom\z) And EntityZ(Collider)<((sz#*Mesh_MaxZ)+PlayerRoom\z)
						inside% = i%
						Exit
					EndIf
				EndIf
			EndIf
		Next
		
		If inside% > -1 Then Return PlayerRoom\TriggerboxName[inside%]
	EndIf
	
End Function

Function ScaledMouseX%()
	Return Float(MouseX()-(RealGraphicWidth*0.5*(1.0-AspectRatioRatio)))*Float(GraphicWidth)/Float(RealGraphicWidth*AspectRatioRatio)
End Function

Function ScaledMouseY%()
	Return Float(MouseY())*Float(GraphicHeight)/Float(RealGraphicHeight)
End Function

Function Create3DIcon(width%,height%,modelpath$,modelX#=0,modelY#=0,modelZ#=0,modelPitch#=0,modelYaw#=0,modelRoll#=0,modelscaleX#=1,modelscaleY#=1,modelscaleZ#=1,withfog%=False)
	Local img% = CreateImage(width,height)
	Local cam% = CreateCamera()
	Local model%
	
	CameraRange cam,0.01,16
	CameraViewport cam,0,0,width,height
	If withfog
		CameraFogMode cam,1
		CameraFogRange cam,CameraFogNear,CameraFogFar
	EndIf
	
	If Right(Lower(modelpath$),6)=".rmesh"
		model = LoadRMesh(scpModding_ProcessFilePath$(modelpath$),Null)
	Else
		model = LoadMesh(scpModding_ProcessFilePath$(modelpath$))
	EndIf
	ScaleEntity model,modelscaleX,modelscaleY,modelscaleZ
	PositionEntity model,modelX#,modelY#,modelZ#
	RotateEntity model,modelPitch#,modelYaw#,modelRoll#
	
	;Cls
	RenderWorld
	CopyRect 0,0,width,height,0,0,BackBuffer(),ImageBuffer(img)
	
	FreeEntity model
	FreeEntity cam
	Return img%
End Function

Function TeleportEntity(entity%,x#,y#,z#,customradius#=0.3,isglobal%=False,pickrange#=2.0,dir%=0)
	Local pvt,pick
	;dir = 0 - towards the floor (default)
	;dir = 1 - towrads the ceiling (mostly for PD decal after leaving dimension)
	
	pvt = CreatePivot()
	PositionEntity(pvt, x,y+0.05,z,isglobal)
	If dir%=0
		RotateEntity pvt,90,0,0
	Else
		RotateEntity pvt,-90,0,0
	EndIf
	pick = EntityPick(pvt,pickrange)
	If pick<>0
		If dir%=0
			PositionEntity(entity, x,PickedY()+customradius#+0.02,z,isglobal)
		Else
			PositionEntity(entity, x,PickedY()+customradius#-0.02,z,isglobal)
		EndIf
	Else
		PositionEntity(entity,x,y,z,isglobal)
	EndIf
	FreeEntity pvt
	ResetEntity entity
	
End Function

Function PlayStartupVideos()
	
	;If PlayStartUp = 0 Then Return
	
	Local Cam = CreateCamera() 
	CameraClsMode Cam, 0, 1
	Local Quad = CreateQuad()
	Local Texture = CreateTexture(2048, 2048, 256 Or 16 Or 32)
	EntityTexture Quad, Texture
	EntityFX Quad, 1
	CameraRange Cam, 0.01, 100
	TranslateEntity Cam, 1.0 / 2048 ,-1.0 / 2048 ,-1.0
	EntityParent Quad, Cam, 1
	
	Local ScaledGraphicHeight%
	Local Ratio# = Float(RealGraphicWidth)/Float(RealGraphicHeight)
	If Ratio>1.76 And Ratio<1.78
		ScaledGraphicHeight = RealGraphicHeight
	Else
		ScaledGraphicHeight% = Float(RealGraphicWidth)/(16.0/9.0)
	EndIf
	
	Local i, moviefile$
	For i = 0 To 2
		Select i
			Case 0
				moviefile$ = scpModding_ProcessFilePath$("GFX\menu\startup_RM")
			Case 1
				moviefile$ = scpModding_ProcessFilePath$("GFX\menu\startup_TSS")
			Case 2
				moviefile$ = scpModding_ProcessFilePath$("GFX\menu\startup_Undertow")
		End Select
		BlitzMovie_Open(moviefile$+".avi") ;Get movie size
		Local moview = BlitzMovie_GetWidth()
		Local movieh = BlitzMovie_GetHeight()
		BlitzMovie_Close()
		Local image = CreateImage(moview, movieh)
		Local SplashScreenVideo = BlitzMovie_OpenDecodeToImage(moviefile$+".avi", image, False)
		If SplashScreenVideo = 0 Then
			FreeTexture Texture
			FreeEntity Quad
			FreeEntity Cam
			FreeImage image
			PutINIValue(OptionFile, "options", "play startup video", "false")
			Return
		EndIf
		SplashScreenVideo = BlitzMovie_Play()
		Local SplashScreenAudio = StreamSound_Strict(moviefile$+".ogg",SFXVolume,0)
		Repeat
			Cls
			ProjectImage(image, RealGraphicWidth, ScaledGraphicHeight, Quad, Texture)
			Color 255,255,255
	        Text GraphicWidth/2,GraphicHeight-50,scpLang_GetPhrase("loadingscreens.anykey2"),True, True
			Flip
		Until (GetKey() Or (Not IsStreamPlaying_Strict(SplashScreenAudio)))
		StopStream_Strict(SplashScreenAudio)
		BlitzMovie_Stop()
		BlitzMovie_Close()
		FreeImage image
		
		Cls
		Flip
	Next
	
	FreeTexture Texture
	FreeEntity Quad
	FreeEntity Cam

End Function

Function ProjectImage(img, w#, h#, Quad%, Texture%)
	
	Local img_w# = ImageWidth(img)
	Local img_h# = ImageHeight(img)
	If img_w > 2048 Then img_w = 2048
	If img_h > 2048 Then img_h = 2048
	If img_w < 1 Then img_w = 1
	If img_h < 1 Then img_h = 1
	
	If w > 2048 Then w = 2048
	If h > 2048 Then h = 2048
	If w < 1 Then w = 1
	If h < 1 Then h = 1
	
	Local w_rel# = w# / img_w#
	Local h_rel# = h# / img_h#
	Local g_rel# = 2048.0 / Float(RealGraphicWidth)
	Local dst_x = 1024 - (img_w / 2.0)
	Local dst_y = 1024 - (img_h / 2.0)
	CopyRect 0, 0, img_w, img_h, dst_x, dst_y, ImageBuffer(img), TextureBuffer(Texture)
	ScaleEntity Quad, w_rel * g_rel, h_rel * g_rel, 0.0001
	RenderWorld()
	
End Function

Function CreateQuad()
	
	mesh = CreateMesh()
	surf = CreateSurface(mesh)
	v0 = AddVertex(surf,-1.0, 1.0, 0, 0, 0)
	v1 = AddVertex(surf, 1.0, 1.0, 0, 1, 0)
	v2 = AddVertex(surf, 1.0,-1.0, 0, 1, 1)
	v3 = AddVertex(surf,-1.0,-1.0, 0, 0, 1)
	AddTriangle(surf, v0, v1, v2)
	AddTriangle(surf, v0, v2, v3)
	UpdateNormals mesh
	Return mesh
	
End Function

;I don't know why this function doesn't work. I'm used a very stupid method to fix it, but it works. - Jabka

;Function CanUseItem(canUseWithHazmat%=True, canUseWithGasMask%=True, canUseWithEyewear%=True)
;	If (canUseWithHazmat = False And WearingHazmat) Then
;		Msg = "You can't use that item while wearing a hazmat suit."
;		MsgTimer = 70*5
;		Return False
;	Else If (canUseWithGasMask = False And (WearingGasMask Or I_1499\Using))
;		Msg = "You can't use that item while wearing a gas mask."
;		MsgTimer = 70*5
;		Return False
;	Else If (canUseWithEyewear = False And (WearingNightVision Or I_178\Using Or I_215\Using))
;		Msg = "You can't use that item while wearing headgear."
;	EndIf
;	
;	Return True
;End Function

Function ResetInput()
	
	FlushKeys()
	FlushMouse()
	MouseHit1 = 0
	MouseHit2 = 0
	MouseDown1 = 0
	MouseUp1 = 0
	MouseHit(1)
	MouseHit(2)
	MouseDown(1)
	GrabbedEntity = 0
	Input_ResetTime# = 10.0
	
End Function

Function RotateEntity90DegreeAngles(entity%)
	Local angle = WrapAngle(entity)
	
	If angle < 45.0 Then
		Return 0
	ElseIf angle >= 45.0 And angle < 135 Then
		Return 90
	ElseIf angle >= 135 And angle < 225 Then
		Return 180
	Else
		Return 270
	EndIf
	
End Function

Function Damage1033RU(damage%)
	
	If damage% > 0 And damage% < 15 Then
	    Bloodloss = Bloodloss + Rnd(5)
	ElseIf damage% >= 15 And damage% < 30
	    Bloodloss = Bloodloss + Rnd(10)
	ElseIf damage% >= 30 And damage% < 50
	    Bloodloss = Bloodloss + Rnd(15)
	ElseIf damage% >= 50
	    Bloodloss = Bloodloss + Rnd(20)
	EndIf
	
	LightFlash = 0.2
	
	I_1033RU\HP = I_1033RU\HP - Int(damage%)
		
    If I_1033RU\HP =< 0 Then I_1033RU\HP = 0
			
	If I_1033RU\Using = 2 Then
	    If I_1033RU\HP > 200 Then I_1033RU\HP = 200
	Else
	    If I_1033RU\HP > 100 Then I_1033RU\HP = 100
    EndIf

	I_1033RU\DHP = I_1033RU\DHP + Int(damage%)
			
	If I_1033RU\DHP =< 0 Then I_1033RU\DHP = 0
			
	If I_1033RU\Using = 2 Then
	    If I_1033RU\DHP > 200 Then I_1033RU\DHP = 200
	Else
	    If I_1033RU\DHP > 100 Then I_1033RU\DHP = 100
	EndIf
	
	If I_1033RU\HP > 0 Then
	    PlaySound_Strict DamageSFX1033RU(Rand(0, 3))
	Else
	    PlaySound_Strict DeathSFX1033RU%
	EndIf
		
End Function


;~IDEal Editor Parameters:
;~F#548
;~B#11DC#1454#1BF2
;~C#Blitz3D