Function PlaySound2%(SoundHandle%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range, 1.0)
	Local SoundCHN% = 0
	
	If volume > 0 Then 
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			SoundCHN% = PlaySound_Strict (SoundHandle)
			
			ChannelVolume(SoundCHN, volume# * (1 - dist#)*SFXVolume#)
			ChannelPan(SoundCHN, panvalue)			
		EndIf
	EndIf
	
	Return SoundCHN
End Function

Function LoopSound2%(SoundHandle%, CHN%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
			
		Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
		If CHN = 0 Then
			CHN% = PlaySound_Strict (SoundHandle)
		Else
			If (Not ChannelPlaying(CHN)) Then Chn% = PlaySound_Strict (SoundHandle)
		EndIf
			
		ChannelVolume(CHN, volume# * (1 - dist#)*SFXVolume#)
		ChannelPan(CHN, panvalue)
	Else
		If CHN <> 0 Then
			ChannelVolume (CHN, 0)
		EndIf 
	EndIf
	
	Return CHN
End Function

Function LoadTempSound(file$)
	If TempSounds[TempSoundIndex]<>0 Then FreeSound_Strict(TempSounds[TempSoundIndex])
	TempSound = LoadSound_Strict(file)
	TempSounds[TempSoundIndex] = TempSound
	
	TempSoundIndex=(TempSoundIndex+1) Mod 10
	
	Return TempSound
End Function

Function LoadEventSound(e.Events,file$,num%=0)
	
	If num=0 Then
		If e\Sound<>0 Then FreeSound_Strict e\Sound : e\Sound=0
		e\Sound=LoadSound_Strict(file)
		Return e\Sound
	Else If num=1 Then
		If e\Sound2<>0 Then FreeSound_Strict e\Sound2 : e\Sound2=0
		e\Sound2=LoadSound_Strict(file)
		Return e\Sound2
	Else If num=2 Then
		If e\Sound3<>0 Then FreeSound_Strict e\Sound3 : e\Sound3=0
		e\Sound3=LoadSound_Strict(file)
		Return e\Sound3
	EndIf
End Function

Function UpdateMusic()
	Local fs.FPS_Settings = First FPS_Settings
	
	If ConsoleFlush Then
		If Not ChannelPlaying(ConsoleMusPlay) Then ConsoleMusPlay = PlaySound(ConsoleMusFlush)
	ElseIf (Not PlayCustomMusic)
		If NowPlaying <> ShouldPlay ; playing the wrong clip, fade out
			CurrMusicVolume# = Max(CurrMusicVolume - (fs\FPSfactor[0] / 250.0), 0)
			If CurrMusicVolume = 0
				If NowPlaying<66
					StopStream_Strict(MusicCHN)
				EndIf
				NowPlaying = ShouldPlay
				MusicCHN = 0
				CurrMusic=0
			EndIf
		Else ; playing the right clip
			CurrMusicVolume = CurrMusicVolume + (MusicVolume - CurrMusicVolume) * (0.1*fs\FPSfactor[0])
		EndIf
		
		If NowPlaying < 66
			If CurrMusic = 0
				MusicCHN = StreamSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Music\"+Music(NowPlaying)+".ogg"),0.0,Mode)
				CurrMusic = 1
			EndIf
			SetStreamVolume_Strict(MusicCHN,CurrMusicVolume)
		EndIf
	Else
		If fs\FPSfactor[0] > 0 Or OptionsMenu = 2 Then
			;CurrMusicVolume = 1.0
			If (Not ChannelPlaying(MusicCHN)) Then MusicCHN = PlaySound_Strict(CustomMusic)
			ChannelVolume MusicCHN,1.0*MusicVolume
		EndIf
	EndIf
	
End Function 

Function PauseSounds()
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream)
				If ChannelPlaying(e\SoundCHN) Then PauseChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN,True)
			EndIf
		EndIf
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream)
				If ChannelPlaying(e\SoundCHN2) Then PauseChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2,True)
			EndIf
		EndIf
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream)
				If ChannelPlaying(e\SoundCHN3) Then PauseChannel(e\SoundCHN3)
			Else
				SetStreamPaused_Strict(e\SoundCHN3,True)
			EndIf
		EndIf		
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) Then PauseChannel(n\SoundCHN)
			Else
				If n\SoundCHN_IsStream=True
					SetStreamPaused_Strict(n\SoundCHN,True)
				EndIf
			EndIf
		EndIf
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) Then PauseChannel(n\SoundCHN2)
			Else
				If n\SoundCHN2_IsStream=True
					SetStreamPaused_Strict(n\SoundCHN2,True)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) Then PauseChannel(d\SoundCHN)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) Then PauseChannel(dem\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then PauseChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then PauseChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		SetStreamPaused_Strict(IntercomStreamCHN,True)
	EndIf
End Function

Function ResumeSounds()
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream)
				If ChannelPlaying(e\SoundCHN) Then ResumeChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN,False)
			EndIf
		EndIf
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream)
				If ChannelPlaying(e\SoundCHN2) Then ResumeChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2,False)
			EndIf
		EndIf
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream)
				If ChannelPlaying(e\SoundCHN3) Then ResumeChannel(e\SoundCHN3)
			Else
				SetStreamPaused_Strict(e\SoundCHN3,False)
			EndIf
		EndIf	
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) Then ResumeChannel(n\SoundCHN)
			Else
				If n\SoundCHN_IsStream=True
					SetStreamPaused_Strict(n\SoundCHN,False)
				EndIf
			EndIf
		EndIf
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) Then ResumeChannel(n\SoundCHN2)
			Else
				If n\SoundCHN2_IsStream=True
					SetStreamPaused_Strict(n\SoundCHN2,False)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) Then ResumeChannel(d\SoundCHN)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) Then ResumeChannel(dem\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then ResumeChannel(AmbientSFXCHN)
	EndIf	
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then ResumeChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		SetStreamPaused_Strict(IntercomStreamCHN,False)
	EndIf
End Function

Function KillSounds()
	Local i%,e.Events,n.NPCs,d.Doors,dem.DevilEmitters,snd.Sound
	
	For i=0 To 9
		If TempSounds[i]<>0 Then FreeSound_Strict TempSounds[i] : TempSounds[i]=0
	Next
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream)
				If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN)
			Else
				StopStream_Strict(e\SoundCHN)
			EndIf
		EndIf
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream)
				If ChannelPlaying(e\SoundCHN2) Then StopChannel(e\SoundCHN2)
			Else
				StopStream_Strict(e\SoundCHN2)
			EndIf
		EndIf
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream)
				If ChannelPlaying(e\SoundCHN3) Then StopChannel(e\SoundCHN3)
			Else
				StopStream_Strict(e\SoundCHN3)
			EndIf
		EndIf			
	Next
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundChn_IsStream)
				If ChannelPlaying(n\SoundCHN) Then StopChannel(n\SoundCHN)
			Else
				StopStream_Strict(n\SoundCHN)
			EndIf
		EndIf
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) Then StopChannel(n\SoundCHN2)
			Else
				StopStream_Strict(n\SoundCHN2)
			EndIf
		EndIf
	Next	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) Then StopChannel(d\SoundCHN)
		EndIf
	Next
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) Then StopChannel(dem\SoundCHN)
		EndIf
	Next
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then StopChannel(AmbientSFXCHN)
	EndIf
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
	EndIf
	If IntercomStreamCHN <> 0
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	If EnableSFXRelease
		For snd.Sound = Each Sound
			If snd\internalHandle <> 0 Then
				FreeSound snd\internalHandle
				snd\internalHandle = 0
				snd\releaseTime = 0
			EndIf
		Next
	EndIf
	
	For snd.Sound = Each Sound
		For i = 0 To 31
			If snd\channels[i]<>0 Then
				StopChannel snd\channels[i]
			EndIf
		Next
	Next
	
End Function

Function GetStepSound(entity%)
    Local picker%,brush%,texture%,name$
    Local mat.Materials
    
    picker = LinePick(EntityX(entity),EntityY(entity),EntityZ(entity),0,-1,0)
    If picker <> 0 Then
        If GetEntityType(picker) <> HIT_MAP Then Return 0
        brush = GetSurfaceBrush(GetSurface(picker,CountSurfaces(picker)))
        If brush <> 0 Then
            texture = GetBrushTexture(brush,3)
            If texture <> 0 Then
                name = StripPath(TextureName(texture))
                If (name <> "") Then FreeTexture(texture)
				For mat.Materials = Each Materials
					If mat\name = name Then
						If mat\StepSound > 0 Then
							FreeBrush(brush)
							Return mat\StepSound-1
						EndIf
						Exit
					EndIf
				Next                
			EndIf
			texture = GetBrushTexture(brush,2)
			If texture <> 0 Then
				name = StripPath(TextureName(texture))
				If (name <> "") Then FreeTexture(texture)
				For mat.Materials = Each Materials
					If mat\name = name Then
						If mat\StepSound > 0 Then
							FreeBrush(brush)
							Return mat\StepSound-1
						EndIf
						Exit
					EndIf
				Next                
			EndIf
			texture = GetBrushTexture(brush,1)
			If texture <> 0 Then
				name = StripPath(TextureName(texture))
				If (name <> "") Then FreeTexture(texture)
				FreeBrush(brush)
				For mat.Materials = Each Materials
					If mat\name = name Then
						If mat\StepSound > 0 Then
							Return mat\StepSound-1
						EndIf
						Exit
					EndIf
				Next                
			EndIf
		EndIf
	EndIf
    
    Return 0
End Function

Function UpdateSoundOrigin2(Chn%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
			ChannelVolume(Chn, volume# * (1 - dist#))
			ChannelPan(Chn, panvalue)
		EndIf
	Else
		If Chn <> 0 Then
			ChannelVolume (Chn, 0)
		EndIf 
	EndIf
End Function

Function UpdateSoundOrigin(Chn%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
			ChannelVolume(Chn, volume# * (1 - dist#)*SFXVolume#)
			ChannelPan(Chn, panvalue)
		EndIf
	Else
		If Chn <> 0 Then
			ChannelVolume (Chn, 0)
		EndIf 
	EndIf
End Function

Function PlayAnnouncement(file$) ;This function streams the announcement currently playing
	
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	
	IntercomStreamCHN = StreamSound_Strict(file$,SFXVolume,0)
	
End Function

Function UpdateStreamSounds()
	Local e.Events
	Local fs.FPS_Settings = First FPS_Settings
	If fs\FPSfactor[0] > 0 Then
		If IntercomStreamCHN <> 0 Then
			SetStreamVolume_Strict(IntercomStreamCHN,SFXVolume)
		EndIf
		For e = Each Events
			If e\SoundCHN<>0 Then
				If e\SoundCHN_IsStream
					SetStreamVolume_Strict(e\SoundCHN,SFXVolume)
				EndIf
			EndIf
			If e\SoundCHN2<>0 Then
				If e\SoundCHN2_IsStream
					SetStreamVolume_Strict(e\SoundCHN2,SFXVolume)
				EndIf
			EndIf
			If e\SoundCHN3<>0 Then
				If e\SoundCHN3_IsStream
					SetStreamVolume_Strict(e\SoundCHN3,SFXVolume)
				EndIf
			EndIf
		Next
	EndIf
	
	If (Not PlayerInReachableRoom()) Then
		If PlayerRoom\RoomTemplate\Name <> "gateb" And PlayerRoom\RoomTemplate\Name <> "gatea" Then
			If IntercomStreamCHN <> 0 Then
				StopStream_Strict(IntercomStreamCHN)
				IntercomStreamCHN = 0
			EndIf
			If PlayerRoom\RoomTemplate\Name$ <> "dimension1499" Then
				For e = Each Events
					If e\SoundCHN<>0 And e\SoundCHN_IsStream Then
						StopStream_Strict(e\SoundCHN)
						e\SoundCHN = 0
						e\SoundCHN_IsStream = 0
					EndIf
					If e\SoundCHN2<>0 And e\SoundCHN2_IsStream Then
						StopStream_Strict(e\SoundCHN2)
						e\SoundCHN = 0
						e\SoundCHN_IsStream = 0
					EndIf
					If e\SoundCHN3<>0 And e\SoundCHN3_IsStream Then
						StopStream_Strict(e\SoundCHN3)
						e\SoundCHN = 0
						e\SoundCHN_IsStream = 0
					EndIf
				Next
			EndIf
		EndIf
	EndIf
	
End Function

Function LoadAllSounds()
	;Dim OpenDoorSFX%(3,3), CloseDoorSFX%(3,3)
	For i = 0 To 2
		OpenDoorSFX(0,i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Door\DoorOpen" + (i + 1) + ".ogg"))
		CloseDoorSFX(0,i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Door\DoorClose" + (i + 1) + ".ogg"))
		OpenDoorSFX(2,i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Door\Door2Open" + (i + 1) + ".ogg"))
		CloseDoorSFX(2,i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Door\Door2Close" + (i + 1) + ".ogg"))
		OpenDoorSFX(3,i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Door\ElevatorOpen" + (i + 1) + ".ogg"))
		CloseDoorSFX(3,i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Door\ElevatorClose" + (i + 1) + ".ogg"))
	Next
	For i = 0 To 1
		OpenDoorSFX(1,i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Door\BigDoorOpen" + (i + 1) + ".ogg"))
		CloseDoorSFX(1,i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Door\BigDoorClose" + (i + 1) + ".ogg"))
	Next

	KeyCardSFX1 = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Interact\KeyCardUse1.ogg"))
	KeyCardSFX2 = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Interact\KeyCardUse2.ogg"))
	ButtonSFX2 = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Interact\Button2.ogg"))
	ScannerSFX1 = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Interact\ScannerUse1.ogg"))
	ScannerSFX2 = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Interact\ScannerUse2.ogg"))

	For i = 0 To 1
  	    OpenDoorFastSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Door\DoorOpenFast" + (i + 1) + ".ogg"))
	Next
	CautionSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\LockroomSiren.ogg"))

	;NuclearSirenSFX%

	CameraSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"General\Camera.ogg"))

	StoneDragSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\173\StoneDrag.ogg"))

	GunshotSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"General\Gunshot.ogg"))
	Gunshot2SFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"General\Gunshot2.ogg"))
	Gunshot3SFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"General\BulletMiss.ogg"))
	Gunshot4SFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"General\Gunshot3.ogg"))
	BullethitSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"General\BulletHit.ogg"))

	TeslaIdleSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\Tesla\Idle.ogg"))
	TeslaActivateSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\Tesla\WindUp.ogg"))
	TeslaPowerUpSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\Tesla\PowerUp.ogg"))

	MagnetUpSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\106Chamber\MagnetUp.ogg"))
	MagnetDownSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\106Chamber\MagnetDown.ogg"))
	;FemurBreakerSFX%
	;EndBreathCHN%
	;EndBreathSFX%

	;Dim DecaySFX%(5)
	For i = 0 To 3
		DecaySFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\106\Decay" + i + ".ogg"))
	Next

	BurstSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\TunnelBurst.ogg"))

	;DrawLoading(20, True)

	;Dim RustleSFX%(7)
	For i = 0 To 6
		RustleSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\372\Rustle" + i + ".ogg"))
	Next

	Death914SFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\914\PlayerDeath.ogg"))
	Use914SFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\914\PlayerUse.ogg"))

	;Dim DripSFX%(6)
	For i = 0 To 5
		DripSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\D9341\BloodDrip" + i + ".ogg"))
	Next

	LeverSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Interact\LeverFlip.ogg"))
	LightSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"General\LightSwitch.ogg"))

	ButtGhostSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\Joke\789J.ogg"))

	;Dim RadioSFX(5,10)
	RadioSFX(1,0) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Radio\RadioAlarm.ogg"))
	RadioSFX(1,1) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Radio\RadioAlarm2.ogg"))
	For i = 0 To 8
		RadioSFX(2,i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Radio\SCPRadio"+i+".ogg"))
	Next
	RadioSquelch = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Radio\Squelch.ogg"))
	RadioStatic = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Radio\Static.ogg"))
	RadioBuzz = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Radio\Buzz.ogg"))

	ElevatorBeepSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"General\Elevator\Beep.ogg"))
	ElevatorMoveSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"General\Elevator\Moving.ogg"))

	;Dim PickSFX%(10)
	For i = 0 To 5 ;3
		PickSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Interact\PickItem" + i + ".ogg"))
	Next

	;AmbientSFXCHN% 
	;CurrAmbientSFX%
	;Dim AmbientSFXAmount(6)
	;0 = light containment, 1 = heavy containment, 2 = entrance
	AmbientSFXAmount(0)=25 : AmbientSFXAmount(1)=24 : AmbientSFXAmount(2)=24
	;3 = general, 4 = pre-breach
	AmbientSFXAmount(3)=15 : AmbientSFXAmount(4)=5
	;5 = forest
	AmbientSFXAmount(5)=13

	;Dim AmbientSFX%(6, 92)

	;Dim OldManSFX%(9)
	For i = 0 To 2
		OldManSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\106\Corrosion" + (i + 1) + ".ogg"))
	Next
	OldManSFX(3) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\106\Laugh.ogg"))
	OldManSFX(4) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\106\Breathing.ogg"))
	OldManSFX(5) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\PocketDimension\Enter.ogg"))
	For i = 0 To 2
		OldManSFX(6+i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\106\WallDecay"+(i+1)+".ogg"))
	Next
	OldManSFX(9) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\106\Laugh2.ogg"))

	;Dim Scp173SFX%(3)
	For i = 0 To 2
		Scp173SFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\173\Rattle" + (i + 1) + ".ogg"))
	Next

	;Dim HorrorSFX%(20)
	For i = 0 To 11
		HorrorSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Horror\Horror" + i + ".ogg"))
	Next
	For i = 14 To 15
		HorrorSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Horror\Horror" + i + ".ogg"))
	Next

	;Dim IntroSFX%(20)

	For i = 7 To 9
		IntroSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\Intro\Bang" + (i - 6) + ".ogg"))
	Next
	For i = 10 To 12
		IntroSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\Intro\Light" + (i - 9) + ".ogg"))
	Next
	;IntroSFX(13) = LoadSound_Strict("SFX\"+"Intro\Shoot1.ogg")
	;IntroSFX(14) = LoadSound_Strict("SFX\"+"Intro\Shoot2.ogg")
	IntroSFX(15) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\Intro\173Vent.ogg"))

	;Dim AlarmSFX%(6)
	AlarmSFX(0) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Alarm\Alarm.ogg"))
	AlarmSFX(1) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Alarm\Alarm2.ogg"))
	AlarmSFX(2) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Alarm\Alarm3.ogg"))

	;room_gw alarms
	AlarmSFX(3) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Alarm\Alarm4.ogg"))
	AlarmSFX(4) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Alarm\Alarm5.ogg"))
	AlarmSFX(5) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Alarm\Alarm6.ogg"))

	;Dim CommotionState%(27)

	HeartBeatSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\D9341\Heartbeat.ogg"))

	;VomitSFX%

	;Dim BreathSFX(2,5)
 	;BreathCHN%
	For i = 0 To 4
		BreathSFX(0,i)=LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\D9341\breath"+i+".ogg"))
		BreathSFX(1,i)=LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\D9341\breath"+i+"gas.ogg"))
	Next

	;Dim NeckSnapSFX(3)
	For i = 0 To 2
		NeckSnapSFX(i) =  LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\173\NeckSnap"+(i+1)+".ogg"))
	Next

	;Dim DamageSFX%(9)
	For i = 0 To 8
		DamageSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\D9341\Damage"+(i+1)+".ogg"))
	Next

	;Dim MTFSFX%(8)

	;Dim CoughSFX%(3)
	;CoughCHN% 
	;VomitCHN%
	For i = 0 To 2
		CoughSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\D9341\Cough" + (i + 1) + ".ogg"))
	Next

 	MachineSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\914\Refining.ogg"))

 	ApacheSFX = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\Apache\Propeller.ogg"))

	;CurrStepSFX
	;Dim StepSFX%(4, 2, 8) ;(normal/metal, walk/run, id)
	For i = 0 To 7
		StepSFX(0, 0, i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Step\Step" + (i + 1) + ".ogg"))
		StepSFX(1, 0, i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Step\StepMetal" + (i + 1) + ".ogg"))
		StepSFX(0, 1, i)= LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Step\Run" + (i + 1) + ".ogg"))
		StepSFX(1, 1, i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Step\RunMetal" + (i + 1) + ".ogg"))
		If i < 3
			StepSFX(2, 0, i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\MTF\Step" + (i + 1) + ".ogg"))
			StepSFX(3, 0, i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\049\Step"+ (i + 1) + ".ogg"))
		EndIf
		If i < 4
    	    StepSFX(4, 0, i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Step\SCP\StepSCP" + (i + 1) + ".ogg"))
    	EndIf
	Next

	;Dim Step2SFX(6)
	For i = 0 To 2
		Step2SFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Step\StepPD" + (i + 1) + ".ogg"))
		Step2SFX(i+3) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Step\StepForest" + (i + 1) + ".ogg"))
	Next

	;{~--<MOD>--~}

	;Dim NVGUse%(1) 
	For i = 0 To 1
  	    NVGUse(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Interact\NVGuse" + i + ".ogg"))
	Next

	;Global Ambient1123SFX%
	Ambient1123SFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\1123\Ambient.ogg"))

	;Global FireSFX% 
	FireSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\457\FireLoop.ogg"))

	;Dim DamageSFX1033RU%(1) 
	For i = 0 To 3
		DamageSFX1033RU(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\1033RU\Damage"+ i +".ogg"))
	Next

	;Global DeathSFX1033RU%
	DeathSFX1033RU% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\1033RU\SCPDeath.ogg"))

	;Dim SizzSFX(1) 
	For i = 0 To 1
  	    SizzSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"SCP\1079\BubbleSizz" + i + ".ogg"))
	Next

	;Dim MTF2SFX%(8) 
	
	;Dim ScientistRadioSFX%(1) 
	For i = 0 To 1
  	    ScientistRadioSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\Scientist\Radio" + i + ".ogg"))
	Next

	;Dim HorrorSFX%(21)
	For i = 17 To 21
  	    HorrorSFX(i) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Horror\Horror" + i + ".ogg"))
	Next

    ;Global RelaxedBreathSFX%
    ;Global RelaxedBreathCHN%
	RelaxedBreathSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\D9341\RelaxedBreathGas.ogg"))
	
	;Global CrouchSFX%
	;Global CrouchCHN%
	CrouchSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Character\D9341\Crouch.ogg"))
	
	;Global RadioStatic895%
	RadioStatic895% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Radio\Static895.ogg"))
	
	;Dim VehicleSFX%(1)
	VehicleSFX(0) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\Intro\Vehicle\VehicleMove.ogg"))
	VehicleSFX(1) = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\"+"Room\Intro\Vehicle\VehicleIdle.ogg"))

	;{~--<END>--~}
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D