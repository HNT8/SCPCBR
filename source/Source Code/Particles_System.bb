Type Particles
	Field obj%, pvt%
	Field image%
	
	Field R#, G#, B#, A#, size#
	Field speed#, yspeed#, gravity#
	Field Rchange#, Gchange#, Bchange#, Achange#
	Field SizeChange#
	
	Field lifetime#
End Type 
	
Function CreateParticle.Particles(x#, y#, z#, image%, size#, gravity# = 1.0, lifetime% = 200)
	Local p.Particles = New Particles
	p\lifetime = lifetime
	
	p\obj = CreateSprite()
	PositionEntity(p\obj, x, y, z, True)
	EntityTexture(p\obj, at\ParticleTextureID[image])
	RotateEntity(p\obj, 0, 0, Rnd(360))
	EntityFX(p\obj, 1 + 8)
	
	SpriteViewMode (p\obj, 3)
	
	Select image
		Case 0,5,6
			EntityBlend(p\obj, 1)
		Case 1,2,3,4,7
			EntityBlend(p\obj, BLEND_ADD)
	End Select
	
	p\pvt = CreatePivot()
	PositionEntity(p\pvt, x, y, z, True)
	
	p\image = image
	p\gravity = gravity * 0.004
	p\R = 255 : p\G = 255 : p\B = 255 : p\A = 1.0
	p\size = size
	ScaleSprite(p\obj, p\size, p\size)
	Return p
End Function
	
Function UpdateParticles()
	Local p.Particles
	Local fs.FPS_Settings = First FPS_Settings
	For p.Particles = Each Particles
		MoveEntity(p\pvt, 0, 0, p\speed * fs\FPSfactor[0])
		If p\gravity <> 0 Then p\yspeed = p\yspeed - p\gravity * fs\FPSfactor[0]
		TranslateEntity(p\pvt, 0, p\yspeed * fs\FPSfactor[0], 0, True)
		
		PositionEntity(p\obj, EntityX(p\pvt,True), EntityY(p\pvt,True), EntityZ(p\pvt,True), True)
		
		;TurnEntity(p\obj, 0, 0, fs\FPSfactor[0])
		
		If p\Achange <> 0 Then
			p\A=Min(Max(p\A+p\Achange * fs\FPSfactor[0],0.0),1.0)
			EntityAlpha(p\obj, p\A)		
		EndIf
		
		If p\SizeChange <> 0 Then 
			p\size= p\size+p\SizeChange * fs\FPSfactor[0]
			ScaleSprite p\obj, p\size, p\size
		EndIf
		
		p\lifetime=p\lifetime-fs\FPSfactor[0]
		If p\lifetime <= 0 Or p\size < 0.00001 Or p\A =< 0 Then
			RemoveParticle(p)
		End If
	Next
End Function
	
Function RemoveParticle(p.Particles)
	FreeEntity(p\obj)
	FreeEntity(p\pvt)	
	Delete p
End Function

Global InSmoke%
Global HissSFX% = LoadSound_Strict(scpModding_ProcessFilePath$("SFX\General\Hiss.ogg"))
Global SmokeDelay# = 0.0

Type Emitters
	Field Obj%
	
	Field Size#
	Field MinImage%, MaxImage%
	Field Gravity#
	Field LifeTime%
	
	Field Disable%
	
	Field Room.Rooms
	
	Field SoundCHN%
	
	Field Speed#, RandAngle#
	Field SizeChange#, Achange#
End Type 

Function UpdateEmitters()
    Local fs.FPS_Settings = First FPS_Settings
	InSmoke = False
	For e.emitters = Each Emitters
		If fs\FPSfactor[0] > 0 And (PlayerRoom = e\room Or e\room\dist < 8) Then
			Local p.Particles = CreateParticle(EntityX(e\obj, True), EntityY(e\obj, True), EntityZ(e\obj, True), Rand(e\minimage, e\maximage), e\size, e\gravity, e\lifetime)
			p\speed = e\speed
			RotateEntity(p\pvt, EntityPitch(e\Obj, True), EntityYaw(e\Obj, True), EntityRoll(e\Obj, True), True)
			TurnEntity(p\pvt, Rnd(-e\RandAngle, e\RandAngle), Rnd(-e\RandAngle, e\RandAngle), 0)
			
			TurnEntity p\obj, 0,0,Rnd(360)
			
			p\SizeChange = e\SizeChange
			
			p\Achange = e\achange
			e\SoundCHN = LoopSound2(HissSFX, e\SoundCHN, Camera, e\Obj)
			
			If InSmoke = False Then
				If WearingGasMask=0 And WearingHazmat=0 Then
					Local dist# = Distance(EntityX(Camera, True), EntityZ(Camera, True), EntityX(e\obj, True), EntityZ(e\obj, True))
					If dist < 0.8 Then
						If Abs(EntityY(Camera, True)-EntityY(e\obj,True))<5.0 Then InSmoke = True
					EndIf
				EndIf					
			EndIf
		EndIf
	Next
	
	If InSmoke Then
		If EyeIrritation > (70 * 6) Then BlurVolume = Max(BlurVolume, (EyeIrritation - (70 * 6)) / (70.0 * 24.0))
		If EyeIrritation > (70 * 24) Then 
			DeathMSG = scpLang_GetPhrase("events.smokedeath")
			Kill()
		EndIf
		
		If KillTimer => 0 Then 
			If Rand(150) = 1 Then
				If CoughCHN = 0 Then
					CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
				Else
					If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
				End If
			EndIf
		EndIf
		
		EyeIrritation = EyeIrritation+fs\FPSfactor[0] * 4
	EndIf	
End Function
	
Function CreateEmitter.Emitters(x#, y#, z#, emittertype%) 
	Local e.Emitters = New Emitters
		
	e\Obj = CreatePivot()
	NameEntity e\Obj,"Emitter1"
	PositionEntity(e\Obj, x, y, z, True)
		
	Select emittertype
		Case 0 ;savu
			e\Size = 0.03
			e\Gravity = -0.2
			e\LifeTime = 200
			e\SizeChange = 0.005
			e\Speed = 0.004
			e\RandAngle = 20
			e\Achange = -0.008
		Case 1
			e\Size = 0.03
			e\Gravity = -0.2
			e\LifeTime = 200
			e\SizeChange = 0.008
			e\Speed = 0.004
			e\RandAngle = 40
			e\Achange = -0.01
			
			e\MinImage = 6 : e\MaxImage = 6
	End Select
	
	For r.Rooms = Each Rooms
		If Abs(EntityX(e\Obj) - EntityX(r\obj)) < 4.0 And Abs(EntityZ(e\Obj) - EntityZ(r\obj)) < 4.0 Then
			e\Room = r
		EndIf
	Next
	
	Return e
		
End Function

Type DevilEmitters
	Field obj%
	Field x#,y#,z#
	Field particleID%
	Field room.Rooms
	Field timer#=0.0
	Field maxtimer#
	Field SoundCHN%
	Field isDeconGas%=False
End Type

Function CreateDevilEmitter.DevilEmitters(x#, y#, z#, room.Rooms, particleID%, maxTime#=2.0)
	Local dem.DevilEmitters = New DevilEmitters
	
	dem\obj = CreatePivot()
	NameEntity dem\obj,"Emitter2"
	PositionEntity dem\obj,x#,y#,z#,True
	EntityParent dem\obj,room\obj
	dem\room = room
	dem\x = x#
	dem\y = y#
	dem\z = z#
	dem\particleID = particleID
	dem\maxtimer = maxTime#
	
	Return dem
End Function

Function UpdateDevilEmitters()
	Local dem.DevilEmitters
	Local InSmoke = False
	Local fs.FPS_Settings = First FPS_Settings
	
	For dem = Each DevilEmitters
		If fs\FPSfactor[0] > 0 And (PlayerRoom = dem\room Or dem\room\dist < 8)
			If dem\timer = 0
				SetEmitter(dem\obj,ParticleEffect[dem\particleID])
				dem\timer = fs\FPSfactor[0]
			ElseIf dem\timer < dem\maxtimer
				dem\timer = Min(dem\timer+fs\FPSfactor[0],dem\maxtimer)
			Else
				dem\timer = 0.0
			EndIf
			If dem\isDeconGas
				dem\SoundCHN = LoopSound2(HissSFX, dem\SoundCHN, Camera, dem\obj)
				If InSmoke = False Then
					If WearingGasMask=0 And WearingHazmat=0 And WearingNightVision=0 Then
						Local dist# = Distance(EntityX(Camera, True), EntityZ(Camera, True), EntityX(dem\obj, True), EntityZ(dem\obj, True))
						If dist < 0.8 Then
							If Abs(EntityY(Camera, True)-EntityY(dem\obj,True))<5.0 Then InSmoke = True
						EndIf
					EndIf					
				EndIf
			EndIf
		EndIf
	Next
	
	If InSmoke Then
		If EyeIrritation > (70 * 6) Then BlurVolume = Max(BlurVolume, (EyeIrritation - (70 * 6)) / (70.0 * 24.0))
		If EyeIrritation > (70 * 24) Then 
			DeathMSG = scpLang_GetPhrase("events.smokedeath")
			Kill()
		EndIf
		
		If KillTimer => 0 Then 
			If Rand(150) = 1 Then
				If CoughCHN = 0 Then
					CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
				Else
					If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
				End If
			EndIf
		EndIf
		
		EyeIrritation = EyeIrritation+fs\FPSfactor[0] * 4
	EndIf
	
End Function

Function DeleteDevilEmitters()
	
	Delete Each DevilEmitters
	
End Function






;~IDEal Editor Parameters:
;~F#4#10#2E#4A#54#66#A0#C5#D0#E0#112
;~C#Blitz3D