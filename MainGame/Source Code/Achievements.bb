;achievement menu & messages by InnocentSam

Dim Achievements%(MAXACHIEVEMENTS)

Global UsedConsole

Global AchievementsMenu%
Dim AchievementStrings$(MAXACHIEVEMENTS)
Dim AchievementDescs$(MAXACHIEVEMENTS)
Dim AchvIMG%(MAXACHIEVEMENTS)
For i = 0 To MAXACHIEVEMENTS-1
	Local loc2% = GetINISectionLocation("Data\achievementstrings.ini", "s"+Str(i))
	AchievementStrings(i) = GetINIString2("Data\achievementstrings.ini", loc2, "string1")
	AchievementDescs(i) = GetINIString2("Data\achievementstrings.ini", loc2, "AchvDesc")
	
	Local image$ = GetINIString2("Data\achievementstrings.ini", loc2, "image") 
	
	AchvIMG(i) = LoadImage_Strict("GFX\menu\achievements\"+image+".png")
	AchvIMG(i) = ResizeImage2(AchvIMG(i),ImageWidth(AchvIMG(i))*GraphicHeight/768.0,ImageHeight(AchvIMG(i))*GraphicHeight/768.0)
	
	BufferDirty ImageBuffer(AchvIMG(i))
Next

Global AchvLocked = LoadImage_Strict("GFX\menu\achievements\AchvLocked.png")
AchvLocked = ResizeImage2(AchvLocked,ImageWidth(AchvLocked)*GraphicHeight/768.0,ImageHeight(AchvLocked)*GraphicHeight/768.0)

BufferDirty ImageBuffer(AchvLocked)

Function GiveAchievement(achvname%, showMessage%=True)
	If Achievements(achvname)<>True Then
		Achievements(achvname)=True
		If UsedConsole<>True Then
			SetSteamAchievement("s"+achvname)
			StoreSteamStats()
		EndIf
		If AchvMSGenabled And showMessage Then
			Local loc2% = GetINISectionLocation("Data\achievementstrings.ini", "s"+achvname)
			Local AchievementName$ = GetINIString2("Data\achievementstrings.ini", loc2, "string1")
			;Msg = "Achievement Unlocked - "+AchievementName
			;MsgTimer=70*7
			CreateAchievementMsg(achvname,AchievementName)
		EndIf
	EndIf
End Function

Function AchievementTooltip(achvno%)
    Local scale# = GraphicHeight/768.0
    Local fo.Fonts = First Fonts
    
    AASetFont fo\Font[2]
    Local width = AAStringWidth(AchievementStrings(achvno))
    AASetFont fo\Font[0]
    If (AAStringWidth(AchievementDescs(achvno))>width) Then
        width = AAStringWidth(AchievementDescs(achvno))
    EndIf
    width = width+20*MenuScale
    
    Local height = 38*scale
    
    Color 25,25,25
    Rect(ScaledMouseX()+(20*MenuScale),ScaledMouseY()+(20*MenuScale),width,height,True)
    Color 150,150,150
    Rect(ScaledMouseX()+(20*MenuScale),ScaledMouseY()+(20*MenuScale),width,height,False)
    AASetFont fo\Font[2]
    AAText(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(35*MenuScale), AchievementStrings(achvno), True, True)
    AASetFont fo\Font[0]
    AAText(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(55*MenuScale), AchievementDescs(achvno), True, True)
End Function

Function DrawAchvIMG(x%, y%, achvno%)
	Local row%
	Local scale# = GraphicHeight/768.0
	Local SeparationConst2 = 76 * scale
;	If achvno >= 0 And achvno < 4 Then 
;		row = achvno
;	ElseIf achvno >= 3 And achvno <= 6 Then
;		row = achvno-3
;	ElseIf achvno >= 7 And achvno <= 10 Then
;		row = achvno-7
;	ElseIf achvno >= 11 And achvno <= 14 Then
;		row = achvno-11
;	ElseIf achvno >= 15 And achvno <= 18 Then
;		row = achvno-15
;	ElseIf achvno >= 19 And achvno <= 22 Then
;		row = achvno-19
;	ElseIf achvno >= 24 And achvno <= 26 Then
;		row = achvno-24
;	EndIf
	row = achvno Mod 4
	Color 0,0,0
	Rect((x+((row)*SeparationConst2)), y, 64*scale, 64*scale, True)
	If Achievements(achvno) = True Then
		DrawImage(AchvIMG(achvno),(x+(row*SeparationConst2)),y)
	Else
		DrawImage(AchvLocked,(x+(row*SeparationConst2)),y)
	EndIf
	Color 50,50,50
	
	Rect((x+(row*SeparationConst2)), y, 64*scale, 64*scale, False)
End Function

Global CurrAchvMSGID% = 0

Type AchievementMsg
	Field achvID%
	Field txt$
	Field msgx#
	Field msgtime#
	Field msgID%
End Type

Function CreateAchievementMsg.AchievementMsg(id%,txt$)
    Local fs.FPS_Settings = First FPS_Settings
	Local amsg.AchievementMsg = New AchievementMsg
	
	amsg\achvID = id
	amsg\txt = txt
	amsg\msgx = 0.0
	amsg\msgtime = fs\FPSfactor[1]
	amsg\msgID = CurrAchvMSGID
	CurrAchvMSGID = CurrAchvMSGID + 1
	
	Return amsg
End Function

Function UpdateAchievementMsg()
    Local fs.FPS_Settings = First FPS_Settings
	Local amsg.AchievementMsg,amsg2.AchievementMsg
	Local scale# = GraphicHeight/768.0
	Local width% = 264*scale
	Local height% = 84*scale
	Local x%,y%
	
	For amsg = Each AchievementMsg
		If amsg\msgtime <> 0
			If amsg\msgtime > 0.0 And amsg\msgtime < 70*7
				amsg\msgtime = amsg\msgtime + fs\FPSfactor[1]
				If amsg\msgx > -width%
					amsg\msgx = Max(amsg\msgx-4*fs\FPSfactor[1],-width%)
				EndIf
			ElseIf amsg\msgtime >= 70*7
				amsg\msgtime = -1
			ElseIf amsg\msgtime = -1
				If amsg\msgx < 0.0
					amsg\msgx = Min(amsg\msgx+4*fs\FPSfactor[1],0.0)
				Else
					amsg\msgtime = 0.0
				EndIf
			EndIf
		Else
			Delete amsg
		EndIf
	Next
	
End Function

Function RenderAchievementMsg()
     Local fo.Fonts = First Fonts
	 Local amsg.AchievementMsg,amsg2.AchievementMsg
	 Local scale# = GraphicHeight/768.0
	 Local width% = 264*scale
	 Local height% = 84*scale
	 Local x%,y%

     For amsg = Each AchievementMsg
		If amsg\msgtime <> 0
            x=GraphicWidth+amsg\msgx
			y=0
			For amsg2 = Each AchievementMsg
				If amsg2 <> amsg
					If amsg2\msgID > amsg\msgID
						y=y+height 
					EndIf
				EndIf
			Next
			DrawFrame(x,y,width,height)
			Color 0,0,0
			Rect(x+10*scale,y+10*scale,64*scale,64*scale,True)
			DrawImage(AchvIMG(amsg\achvID),x+10*scale,y+10*scale)
			Color 50,50,50
			Rect(x+10*scale,y+10*scale,64*scale,64*scale,False)
			Color 255,255,255
			AASetFont fo\Font[0]
			RowText("Achievement Unlocked -    "+amsg\txt,x+84*scale,y+10*scale,width-94*scale,y-20*scale)
		EndIf
	Next
End Function

;~IDEal Editor Parameters:
;~F#31#48
;~C#Blitz3D