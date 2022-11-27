;3D Main Menu for SCP - Containment Breach Ultimate Edition
;Made by Box Of Horrors mod

Type Menu3DInstance
    Field Mesh%
    Field Cam%
    Field Pivot%
    Field Room$
    Field Scale#
    Field Misc%[MaxMiscAmount-1]
    Field Sprite%[MaxSpriteAmount]
    Field SpriteAlpha#
    Field Dark# = 1.0
    Field State#
    Field State2#
    Field SoundChn%
    Field SoundChn2%
    Field Rendering% = 0
    Field RenderingObj%[MaxRenderingObjAmount]
End Type

Global m3d.Menu3DInstance = New Menu3DInstance

Type Menu3DLights
    Field MenuLights%[MaxMenuLightsAmount-1]
    Field MenuLightSprites%[MaxMenuLightSpritesAmount-1]
    Field MenuLightSprites2%[MaxMenuLightSprites2Amount-1]
End Type
	
AlarmSFX(0) = LoadSound_Strict(SFXPath$+"Alarm\Alarm.ogg")
AlarmSFX(1) = LoadSound_Strict(SFXPath$+"Alarm\Alarm2.ogg") ; For 3D Menu only.
TeslaIdleSFX = LoadSound_Strict(SFXPath$+"Room\Tesla\Idle.ogg")

For i = 7 To 9
	IntroSFX(i) = LoadSound_Strict(SFXPath$+"Room\Intro\Bang" + (i - 6) + ".ogg")
Next

For i = 0 To 6
	RustleSFX(i) = LoadSound_Strict(SFXPath$+"SCP\372\Rustle" + i + ".ogg")
Next

Function Init3DMenu()
    SeedRnd MilliSecs()

    ;Rooms selection	
	Select Lower(GetINIString$(OptionFile,"options","game progress"))
		;Case "intro" ;'s chamber before breach (WIP)
		    ;[Block]
			;m3d\Room = "room173intro"
			;[End Block]
		Case "lcz" ;Light Containment Zone rooms
		    ;[Block]
		    Select Rand(5) ;6
				Case 1
				    m3d\Room = "room173"
				Case 2
					m3d\Room = "lockroom"
				Case 3
					m3d\Room = "room372"
				Case 4
					m3d\Room = "room914"
			    Case 5
			        m3d\Room = "room2tesla_lcz"
		        ;Case 6
		            ;m3d\Room = "room4info"
		    ;[End Block]
			End Select
		Case "hcz" ; Heavy Containment Zone rooms
		    ;[End Block]
			Select Rand(2)
				Case 1
					m3d\Room = "room009"
				Case 2
					m3d\Room = "room3pit"
			End Select	
			;[End Block]
		;Case "ez" ;Entrance Zone rooms
		    ;[Block]
		    
		    ;[End Block]							
		Default ;a specific room
		    ;[Block]
			m3d\Room = Lower(GetINIString$(OptionFile,"options","game progress"))
			;[End Block]
	End Select
	
	If m3d\Room<>"" Then
		strMesh$ = GetINIString("Data\rooms.ini",m3d\Room,"mesh path")
		If strMesh = "" Then strMesh$ = GetINIString("Data\rooms.ini",m3d\Room,"mesh path")
		If strMesh <> "" Then
		    m3d\Mesh = LoadRMesh_3DMenu(strMesh)
			ScaleEntity (m3d\Mesh, RoomScale, RoomScale, RoomScale)
			If m3d\Mesh = 0 Then RuntimeError("Error loading "+m3d\Room+" (error 2)")
		Else
			RuntimeError("Error loading "+m3d\Room+" (error 1)")
		EndIf
	EndIf

	;Creates a camera
	m3d\Cam = CreateCamera()
	CameraRange m3d\Cam,0.1,40.0
	
	m3d\Sprite[0] = CreateSprite(m3d\Cam)	
	EntityOrder m3d\Sprite[0], -100
	ScaleSprite m3d\Sprite[0], 0.4, 0.4/650.0*228.0
	m3d\Scale = Min(1.0, Float(RealGraphicHeight)/Float(RealGraphicWidth))	
	MoveEntity m3d\Sprite[0], 0.0, m3d\Scale*0.8, 1.0 
			
	Local temptex% = LoadTexture_Strict("GFX\menu\back3d.png", 1+2) ;,3

	Cls		
				
	EntityTexture m3d\Sprite[0],temptex
	
	m3d\Sprite[1] = CopyEntity(m3d\Sprite[0])
	EntityParent m3d\Sprite[1], m3d\Cam
	EntityColor m3d\Sprite[1], 0, 0, 0
	EntityAlpha m3d\Sprite[1], 0.3
	EntityFX m3d\Sprite[1],1
	EntityOrder m3d\Sprite[1], -99
	
    FreeTexture temptex

	ClsColor 0,0,0
	
	m3d\Sprite[2] = CreateSprite()
	ScaleSprite m3d\Sprite[2], 2.0, 2.0
	MoveEntity m3d\Sprite[2], 0, 0, 1
	EntityOrder m3d\Sprite[2], -98
	EntityColor m3d\Sprite[2], 0, 0, 0
	EntityParent m3d\Sprite[2], m3d\Cam
	
	HideEntity m3d\Sprite[0]
	HideEntity m3d\Sprite[1]
	HideEntity m3d\Sprite[2]
	HideEntity m3d\Cam
				
	Select m3d\Room
			;Intro
		;Case "room173intro"
		;	;[Block]
		;	AmbientLight 200,200,200
		;	TranslateEntity m3d\Cam, -16.0, -1.0, -8.0
		;	CameraFogMode m3d\Cam, 0
		;	RotateEntity m3d\Cam, -8.5, -65.0,0
		;	PointEntity m3d\Cam, m3d\Mesh
		;	;[End Block]
			;LCZ (Light Containment Zone)
		Case "room173"
			;[Block]
			AmbientLight Brightness,Brightness,Brightness
			TranslateEntity m3d\Cam,-1.6,2.5,-3.6
			CameraFogRange m3d\Cam,0.1,6.0
			CameraFogMode m3d\Cam,1
			PointEntity m3d\Cam,m3d\Mesh
			m3d\Misc[0] = LoadMesh_Strict(MapPath$+"DoorFrame.x")
			m3d\Misc[1] = LoadMesh_Strict(MapPath$+"Door01.x")
			;m3d\Misc[2] = LoadMesh_Strict(MapPath$+"Button.b3d")
			PositionEntity m3d\Misc[0], -2.45, 1.5, 0.0,True
			PositionEntity m3d\Misc[1], -2.45, 1.5, 0.0,True
			;PositionEntity m3d\Misc[2], -0.6, 0.7, -3.9,True
			ScaleEntity m3d\Misc[0], RoomScale,RoomScale,RoomScale
			ScaleEntity m3d\Misc[1], (204.0 * RoomScale) / MeshWidth(m3d\Misc[1]), 312.0 * RoomScale / MeshHeight(m3d\Misc[1]), 16.0 * RoomScale / MeshDepth(m3d\Misc[1])
			;ScaleEntity m3d\Misc[2], 0.03, 0.03, 0.03
			;RotateEntity m3d\Misc[2], 0, 0, 0,True
			RotateEntity m3d\Misc[0],0, 270, 0, True
			RotateEntity m3d\Misc[1], 0, 270, 0, True
			EntityParent m3d\Misc[0], m3d\Mesh
			EntityParent m3d\Misc[1], m3d\Mesh
			;EntityParent m3d\Misc[2], m3d\Mesh
			;[End Block]
		Case "lockroom"
			;[Block]
			AmbientLight Brightness,Brightness,Brightness
			at\ParticleTextureID[0] = LoadTexture_Strict(GFXPath$+"smoke.png",1+2)
			TranslateEntity m3d\Cam,-112.0 * RoomScale,1.4,112.0 * RoomScale
			PointEntity m3d\Cam,m3d\Mesh
			CameraFogRange m3d\Cam,0.1,6.0
			CameraFogMode m3d\Cam,1
		Case "room372"
			;[Block]
			AmbientLight Brightness,Brightness,Brightness
			CameraFogRange m3d\Cam,0.1,6.0
			CameraFogMode m3d\Cam,1
		    MoveEntity m3d\Cam,0.0,1.0,-2.0			
			;[End Block]
		Case "room914"
			;[Block]
			AmbientLight Brightness,Brightness,Brightness
			CameraFogRange m3d\Cam,0.1,6.0
			CameraFogMode m3d\Cam,1
			TranslateEntity m3d\Cam,-1.61,0.2,0.1
				
			m3d\Misc[0] = LoadMesh_Strict(MapPath$+"914key.x")
			m3d\Misc[1] = LoadMesh_Strict(MapPath$+"914knob.x")
			
			For i% = 0 To 1
				ScaleEntity(m3d\Misc[i], RoomScale, RoomScale, RoomScale)
				EntityFX m3d\Misc[i],0
			Next
			
			PositionEntity (m3d\Misc[0], - 416.0 * RoomScale, 190.0 * RoomScale, 374.0 * RoomScale)
			PositionEntity (m3d\Misc[1], - 416.0 * RoomScale, 230.0 * RoomScale, 374.0 * RoomScale)
			EntityParent(m3d\Misc[0], m3d\Mesh)
			EntityParent(m3d\Misc[1], m3d\Mesh)
			
			m3d\Misc[2] = LoadMesh_Strict(MapPath$+"DoorFrame.x")
			m3d\Misc[3] = CopyEntity(m3d\Misc[2])
			PositionEntity m3d\Misc[2], - 1038.0 * RoomScale, 0, 528.0 * RoomScale, True
			PositionEntity m3d\Misc[3], 401.0 * RoomScale, 0, 528.0 * RoomScale, True
			ScaleEntity m3d\Misc[2], RoomScale, RoomScale, RoomScale
			ScaleEntity m3d\Misc[3], RoomScale, RoomScale, RoomScale
			EntityParent(m3d\Misc[2], m3d\Mesh)
			EntityParent(m3d\Misc[3], m3d\Mesh)
			
			;[End Block]
		Case "room2tesla_lcz"
		    ;[Block]
		    AmbientLight Brightness,Brightness,Brightness
		    CameraFogRange m3d\Cam, -5.0, 8.7
		    CameraFogMode m3d\Cam, 1
		    MoveEntity m3d\Cam, 0.0, 1.0, -2.0
		    TranslateEntity m3d\Cam, 0.4, 0.2, -1.8
		    
		    m3d\Misc[0] = LoadAnimMesh_Strict(NPCsPath$+"clerk.b3d")
		    ftemp# = 0.5 / MeshWidth(m3d\Misc[0])
		    ScaleEntity m3d\Misc[0], ftemp, ftemp, ftemp
		    PositionEntity m3d\Misc[0], 0.0 * RoomScale, 10.0 * RoomScale, 0.0 * RoomScale, True
		    RotateEntity m3d\Misc[0], 0, 0, 0, True
		    SetAnimTime m3d\Misc[0], 60.0
		    EntityParent(m3d\Misc[0], m3d\Mesh)
		    
		    m3d\Misc[1] = LoadAnimMesh_Strict(MapPath$+"room2tesla_caution.b3d")
		    ftemp# = 1.8 / MeshWidth(m3d\Misc[1])
		    ScaleEntity m3d\Misc[1], ftemp, ftemp, ftemp
		    PositionEntity m3d\Misc[1], 0.0 * RoomScale, 0.0 * RoomScale, 0.0 * RoomScale
		    RotateEntity m3d\Misc[1], 0, 0, 0, True
		    SetAnimTime m3d\Misc[1], 0.0
		    EntityParent(m3d\Misc[1], m3d\Mesh)
		
		    m3d\Misc[2] = LoadMesh_Strict(MapPath$+"DoorFrame.x")
		    m3d\Misc[3] = LoadMesh_Strict(MapPath$+"Door01.x")
		    PositionEntity m3d\Misc[2], 0.0 * RoomScale, 0.0 * RoomScale, 1000.0 * RoomScale, True
		    PositionEntity m3d\Misc[3], 0.0 * RoomScale, 0.0 * RoomScale, 1000.0 * RoomScale, True
		    RotateEntity m3d\Misc[3], 0, 180, 0, True
		    ScaleEntity m3d\Misc[2], RoomScale, RoomScale, RoomScale
		    ScaleEntity m3d\Misc[3], (204.0 * RoomScale) / MeshWidth(m3d\Misc[3]), 312.0 * RoomScale / MeshHeight(m3d\Misc[3]), 16.0 * RoomScale / MeshDepth(m3d\Misc[3])
		    EntityParent(m3d\Misc[2], m3d\Mesh)
		    EntityParent(m3d\Misc[3], m3d\Mesh)
		    ;[End Block]
		;Case "room4info"
		;    ;[Block]
		;    AmbientLight Brightness,Brightness,Brightness
		;    CameraFogRange m3d\Cam, -5.0, 7.7
		;    CameraFogMode m3d\Cam, 1
		;    MoveEntity m3d\Cam, -0.5, 1.0, -3.5
		;    TranslateEntity m3d\Cam, 0.4 * RoomScale, 0.2 * RoomScale, -1.8 * RoomScale
		;    RotateEntity m3d\Cam, 0, 315, 0
		;    
		;    m3d\Misc[0] = LoadAnimMesh_Strict(NPCsPath$+"clerk.b3d")
        ;    ftemp# = 0.5 / MeshWidth(m3d\Misc[0])
        ;    PositionEntity m3d\Misc[0], 710.0 * RoomScale, 10.0 * RoomScale, -650.0 * RoomScale, True
        ;    RotateEntity m3d\Misc[0], 0, 90, 0, True
        ;    SetAnimTime m3d\Misc[0], 35.0
        ;    EntityParent(m3d\Misc[0], m3d\Mesh)
        ;    tex1 = LoadTexture_Strict(NPCsPath$+"bodyc1.png")
        ;    EntityTexture m3d\Misc[0], tex1
        ;    FreeTexture tex1
        ;
        ;    m3d\Misc[1] = LoadAnimMesh_Strict("GFX\items\radio.x")
        ;    ftemp# = 0.1 / MeshWidth(m3d\Misc[1])
        ;    ScaleEntity m3d\Misc[1], ftemp, ftemp, ftemp
        ;    PositionEntity m3d\Misc[1], 800.0 * RoomScale, 0.0 * RoomScale, -585.0 * RoomScale, True
        ;    RotateEntity m3d\Misc[1], 0, 90, 0, True
        ;    EntityParent(m3d\Misc[1], m3d\Mesh)
        ;    
        ;    m3d\Misc[2] = LoadMesh_Strict(MapPath$+"DoorFrame.x")
		;    m3d\Misc[3] = LoadMesh_Strict(MapPath$+"Door01.x")
		;    PositionEntity m3d\Misc[2], 0.0 * RoomScale, 0.0 * RoomScale, 1000.0 * RoomScale, True
		;    PositionEntity m3d\Misc[3], 0.0 * RoomScale, 0.0 * RoomScale, 1000.0 * RoomScale, True
        ;    ScaleEntity m3d\Misc[2], RoomScale, RoomScale, RoomScale
		;    ScaleEntity m3d\Misc[3], (204.0 * RoomScale) / MeshWidth(m3d\Misc[3]), 312.0 * RoomScale / MeshHeight(m3d\Misc[3]), 16.0 * RoomScale / MeshDepth(m3d\Misc[3])
        ;    RotateEntity m3d\Misc[2], 0, 180, 0, True
        ;    RotateEntity m3d\Misc[3], 0, 180, 0, True
        ;    EntityParent(m3d\Misc[2], m3d\Mesh)
        ;    EntityParent(m3d\Misc[3], m3d\Mesh)
        ;    ;[End Block]
	        ;HCZ (Heavy Containment Zone)
		Case "room009"
			;[Block]
			AmbientLight Brightness,Brightness,Brightness
			CameraFogRange m3d\Cam,0.1,6.0
			CameraFogMode m3d\Cam, 1	
            MoveEntity m3d\Cam, 0.0164874,2.0,0.8512 ;0.0164874 0.0291088
	        PointEntity m3d\Cam,m3d\Mesh
		
			m3d\Misc[0] = LoadAnimMesh_Strict(NPCsPath$+"class_d.b3d")
			ftemp# = 0.5 / MeshWidth(m3d\Misc[0])	
			ScaleEntity m3d\Misc[0], ftemp, ftemp, ftemp	
			PositionEntity m3d\Misc[0], + 490.0 * RoomScale, - 500.0 * RoomScale, 0.0, True		
			RotateEntity m3d\Misc[0],0,0,0,True		
			SetAnimTime m3d\Misc[0],19.0
			
			EntityParent(m3d\Misc[0], m3d\Mesh)
						
			m3d\Misc[0] = GetChild(m3d\Mesh,2)
			m3d\Misc[1] = 0		
			
			m3d\Misc[2] = LoadMesh_Strict(MapPath$+"DoorFrame.x")
			m3d\Misc[3] = LoadMesh_Strict(MapPath$+"heavydoor1.x")
			m3d\Misc[4] = LoadMesh_Strict(MapPath$+"heavydoor2.x")
			
			PositionEntity m3d\Misc[2],0,0, -640.0 * RoomScale,True
			PositionEntity m3d\Misc[3],0,0, -640.0 * RoomScale,True
			PositionEntity m3d\Misc[4],0,0, -640.0 * RoomScale,True

			ScaleEntity m3d\Misc[2],RoomScale, RoomScale, RoomScale
			ScaleEntity m3d\Misc[3],RoomScale, RoomScale, RoomScale			
			ScaleEntity m3d\Misc[4],RoomScale, RoomScale, RoomScale
			
			RotateEntity m3d\Misc[3],0,0,0
			RotateEntity m3d\Misc[4],0,180,0

			EntityParent m3d\Misc[2], m3d\Mesh
			EntityParent m3d\Misc[3], m3d\Mesh
			EntityParent m3d\Misc[4], m3d\Mesh
																													
			;[End Block]
		Case "room3pit"
			;[Block]
			at\ParticleTextureID[0] = LoadTexture_Strict(GFXPath$+"smoke.png",1+2)
										
			AmbientLight Brightness,Brightness,Brightness
			CameraFogRange m3d\Cam,0.1,6.0
			CameraFogMode m3d\Cam,1
		    ;MoveEntity m3d\Cam,0.0,1.0,-2.0	
			;PointEntity m3d\Cam,m3d\Mesh
            MoveEntity m3d\Cam,-0.00339339,1.0,-3.66559

			;m3d\Misc[0]= CreatePivot(m3d\Mesh)
			;PositionEntity(m3d\Misc[0], + 704.0 * RoomScale, 112.0 * RoomScale, -416.0 * RoomScale, True)

			m3d\Misc[0] = LoadMesh_Strict(MapPath$+"DoorFrame.x")
			m3d\Misc[1]=CopyEntity(m3d\Misc[0])

			PositionEntity m3d\Misc[0],-1024.0 * RoomScale,0,0,True
			PositionEntity m3d\Misc[1],1024.0 * RoomScale,0,0,True
						
			RotateEntity m3d\Misc[0],0,90,0
			RotateEntity m3d\Misc[1],0,90,0	 		
						
			ScaleEntity m3d\Misc[0],RoomScale,RoomScale,RoomScale
			ScaleEntity m3d\Misc[1],RoomScale,RoomScale,RoomScale
						
			EntityParent(m3d\Misc[0], m3d\Mesh)
			EntityParent(m3d\Misc[1], m3d\Mesh)
			
			m3d\Misc[2] = LoadMesh_Strict(MapPath$+"heavydoor1.x")
			m3d\Misc[3] = LoadMesh_Strict(MapPath$+"heavydoor2.x")
			m3d\Misc[4] = CopyEntity(m3d\Misc[2])
			m3d\Misc[5] = CopyEntity(m3d\Misc[3])

			ScaleEntity m3d\Misc[2],RoomScale, RoomScale, RoomScale
			ScaleEntity m3d\Misc[3],RoomScale, RoomScale, RoomScale			
			ScaleEntity m3d\Misc[4],RoomScale, RoomScale, RoomScale
			ScaleEntity m3d\Misc[5],RoomScale, RoomScale, RoomScale

			PositionEntity m3d\Misc[2],-1024.0 * RoomScale,0,0,True
			PositionEntity m3d\Misc[3],-1024.0 * RoomScale,0,0,True
			PositionEntity m3d\Misc[4],1024.0 * RoomScale,0,0,True
			PositionEntity m3d\Misc[5],1024.0 * RoomScale,0,0,True
			
			RotateEntity m3d\Misc[2],0,90,0
			RotateEntity m3d\Misc[3],0,270,0
			RotateEntity m3d\Misc[4],0,90,0
			RotateEntity m3d\Misc[5],0,270,0

			EntityParent m3d\Misc[2], m3d\Mesh
			EntityParent m3d\Misc[3], m3d\Mesh
			EntityParent m3d\Misc[4], m3d\Mesh
			EntityParent m3d\Misc[5], m3d\Mesh

			m3d\Misc[6] = LoadMesh_Strict(MapPath$+"Button.b3d")
			m3d\Misc[7] = CopyEntity(m3d\Misc[6])

	        ScaleEntity(m3d\Misc[6], 0.03, 0.03, 0.03)
	        ScaleEntity(m3d\Misc[7], 0.03, 0.03, 0.03)

			PositionEntity m3d\Misc[6],-1000.0 * RoomScale, 183.0 * RoomScale, 168.0 * RoomScale,True
			PositionEntity m3d\Misc[7],1000.0 * RoomScale,183.0 * RoomScale,-168.0 * RoomScale,True

			RotateEntity m3d\Misc[6],0,90,0
			RotateEntity m3d\Misc[7],0,-90,0
						
			EntityParent m3d\Misc[6],m3d\Mesh
			EntityParent m3d\Misc[7],m3d\Mesh
																																																				
			;[End Block]
		    ;EZ (Entrance Zone)						
	End Select
			
	ClearTextureCache
End Function

Function Update3DMenu()
	Local fs.FPS_Settings = First FPS_Settings
	
	If ms\MainMenuTab=0 Then
		m3d\Dark=Max(m3d\Dark-fs\FPSfactor[0]*0.05,0.0)
	ElseIf ms\MainMenuTab=5 Then
		m3d\Dark=Min(m3d\Dark+fs\FPSfactor[0]*0.05,0.7)
		;EntityAlpha m3d\Sprite[1],0
		;EntityAlpha m3d\Sprite[0],1.0-m3d\Dark
	Else
		m3d\Dark=Min(m3d\Dark+fs\FPSfactor[0]*0.05,0.7)
	EndIf
	EntityAlpha m3d\Sprite[2], m3d\Dark
	
    ShowEntity m3d\Cam
	ShowEntity m3d\Sprite[0]
	ShowEntity m3d\Sprite[1]
	ShowEntity m3d\Sprite[2]	
    ;This so called "3D Menu Rendering Function" is in fact "Update3DMenu()". He could have told me but I guess that's his choice of words?
    ;Special for FOV (Thanks to ENDSHN)
    If ms\MainMenuOpen
		CameraProjMode m3d\Cam,1 ;Turns on the projection mode	
		If m3d\Rendering=0
			CameraClsMode m3d\Cam,1,1
			CameraZoom m3d\Cam,1.0/(Tan((2*ATan(Tan((FOV#)/2)*(Float(RealGraphicWidth)/Float(RealGraphicHeight))))/2.0))
			CameraViewport m3d\Cam,0,0,GraphicWidth,GraphicHeight ;This ports the projector
			HideEntity m3d\Sprite[0]
			HideEntity m3d\Sprite[1]
			HideEntity m3d\Sprite[2]
			RenderWorld()
			CameraZoom(m3d\Cam, 1.0)
			ShowEntity m3d\Sprite[0]
			CameraViewport m3d\Cam,0,0,GraphicWidth,GraphicHeight
			CameraClsMode m3d\Cam,0,1
			HideEntity m3d\Mesh
			ShowEntity m3d\Sprite[0]
			ShowEntity m3d\Sprite[1]
			ShowEntity m3d\Sprite[2]
			RenderWorld
			ShowEntity m3d\Mesh
		Else
			CameraClsMode m3d\Cam,1,1
			CameraZoom m3d\Cam,1.0/(Tan((2*ATan(Tan((FOV#)/2)*(Float(RealGraphicWidth)/Float(RealGraphicHeight))))/2.0))*0.8
			CameraViewport m3d\Cam,0,0,GraphicWidth/5,GraphicHeight/5 ;This too ports the projector
			HideEntity m3d\Sprite[0]
			HideEntity m3d\Sprite[1]
			HideEntity m3d\Sprite[2]
			RenderWorld()
			
			SetBuffer TextureBuffer(fresize_texture)
			ClsColor 0,0,0 : Cls
			CopyRect 0,0,GraphicWidth/5,GraphicHeight/5,1024-(GraphicWidth/5)/2,1024-(GraphicHeight/5)/2,BackBuffer(),TextureBuffer(m3d\RenderingObj[2])
			SetBuffer BackBuffer()
			ClsColor 0,0,0 : Cls
			Local AR# = (Float(GraphicWidth/5)/Float(GraphicHeight/5))/(Float(GraphicWidth)/Float(GraphicHeight))
			
			CameraProjMode m3d\Cam,0
			ShowEntity m3d\RenderingObj[1]
			CameraProjMode m3d\RenderingObj[0],2
			RenderWorld()
			CameraProjMode m3d\RenderingObj[0],0
			HideEntity m3d\RenderingObj[1]
			CameraProjMode m3d\Cam,1
			
			CameraZoom(m3d\Cam, 1.0)
			CameraViewport m3d\Cam,0,0,GraphicWidth,GraphicHeight ;This ports the projector
			CameraClsMode m3d\Cam,0,1
			HideEntity m3d\Mesh
			ShowEntity m3d\Sprite[0]
			ShowEntity m3d\Sprite[1]
			ShowEntity m3d\Sprite[2]
			RenderWorld
			ShowEntity m3d\Mesh
		EndIf
		CameraProjMode m3d\Cam,0 ;Turns off the projection mode	
    EndIf
		
	Select m3d\Room
		;	;intro
		;Case "room173intro"
		;	;[Block]
		;	
		;	;[End Block]
			;LCZ
		Case "room173"
			;[Block]
			PointEntity m3d\Cam,m3d\Mesh
			
			RotateEntity m3d\Cam,EntityPitch(m3d\Cam,True),EntityYaw(m3d\Cam,True)+(Sin(m3d\State)*15.0),0,True
			
			m3d\State=WrapAngle(m3d\State+fs\FPSfactor[0]*0.3)
								
			m3d\State2=m3d\State2+fs\FPSfactor[0]
			If m3d\State2 > 500 Then
				
				If m3d\State2 > 520 And m3d\State2 - fs\FPSfactor[0] <= 520 Then BlinkTimer = 0
				If m3d\State2 < 2000 Then
					If m3d\SoundChn = 0 Then					
						m3d\SoundChn = PlaySound_Strict(AlarmSFX(0))							
					Else
						If Not ChannelPlaying(m3d\SoundChn) Then m3d\SoundChn = PlaySound_Strict(AlarmSFX(0))
					End If
					If Rand(600) = 1 Then tempChn%=PlaySound_Strict(IntroSFX(Rand(7, 9)))
					If Rand(400) = 1 Then tempChn%=PlaySound_Strict(IntroSFX(Rand(13, 14)))
				Else
					If Rand(1200) = 1 Then tempChn%=PlaySound_Strict(IntroSFX(Rand(7, 9)))
					If Rand(800) = 1 Then tempChn%=PlaySound_Strict(IntroSFX(Rand(13, 14)))
				EndIf
				
				If m3d\State2 > 900 And m3d\State2 - fs\FPSfactor[0] <= 900 Then m3d\SoundChn2 = PlaySound_Strict(AlarmSFX(1))
				If m3d\State2 > 2000 And m3d\State2 - fs\FPSfactor[0] <= 2000 Then tempChn%=PlaySound_Strict(IntroSFX(7))
				If m3d\State2 > 3500 And m3d\State2 - fs\FPSfactor[0] <= 3500 Then tempChn%=PlaySound_Strict(IntroSFX(7))
				
				If m3d\SoundChn<>0 Then ChannelVolume m3d\SoundChn,0.1			
				If m3d\SoundChn2<>0 Then ChannelVolume m3d\SoundChn2,0.1
				If tempChn<>0 Then ChannelVolume tempChn,0.1
			End If
			;[End Block]
		Case "lockroom"
			;[Block]
			PointEntity m3d\Cam,m3d\Mesh
			
			RotateEntity m3d\Cam,EntityPitch(m3d\Cam,True)-20.0,EntityYaw(m3d\Cam,True)+Min(Max((Sin(m3d\State)*30.0),-25.0),25.0)+180.0,0,True
			
			m3d\State=WrapAngle(m3d\State+fs\FPSfactor[0]*0.35)
			
			Local p.Particles = CreateParticle(- 175.0 * RoomScale, 370.0 * RoomScale, 656.0 * RoomScale, 0, 0.03, -0.24, 200)
			p\speed = 0.05
			RotateEntity(p\pvt, 90, 0, 0, True)
			TurnEntity(p\pvt, Rnd(-20, 20), Rnd(-20, 20), 0)
			
			TurnEntity p\obj, 0,0,Rnd(360)
			
			p\SizeChange = 0.007
			
			p\Achange = -0.006
			
			p.Particles = CreateParticle(- 655.0 * RoomScale, 370.0 * RoomScale, 240.0 * RoomScale, 0, 0.03, -0.24, 200)			
			p\speed = 0.05
			RotateEntity(p\pvt, 90, 0, 0, True)
			TurnEntity(p\pvt, Rnd(-20, 20), Rnd(-20, 20), 0)
			
			TurnEntity p\obj, 0,0,Rnd(360)
			
			p\SizeChange = 0.007
			
			p\Achange = -0.006
			
			If m3d\SoundChn=0 Then
				m3d\SoundChn = PlaySound_Strict(HissSFX)
			Else
				If Not ChannelPlaying(m3d\SoundChn) Then m3d\SoundChn = PlaySound_Strict(HissSFX)
			EndIf
			ChannelVolume m3d\SoundChn,0.2
									
			m3d\State2=Max(m3d\State2-fs\FPSfactor[0],0)
			
			UpdateParticles()
			;[End Block]
		Case "room372"
			;[Block]		
			If Rand(2000)=1 Then 
			    m3d\Dark = Max(m3d\Dark,0.6)
			    m3d\SoundChn = PlaySound_Strict(RustleSFX(Rand(0, 6)))
			EndIf						
			;[End Block]
		Case "room914"
			;[Block]
			PointEntity m3d\Cam,m3d\Misc[0]
			
			RotateEntity m3d\Cam,EntityPitch(m3d\Cam,True),EntityYaw(m3d\Cam,True)+(Sin(m3d\State)*15.0),0,True
			
			m3d\State=WrapAngle(m3d\State+fs\FPSfactor[0]*0.3)
			;[End Block]
			;HCZ
		Case "room009"
			;[Block]
			
			PointEntity m3d\Cam,m3d\Mesh
            ;RotateEntity m3d\Cam,EntityPitch(m3d\Cam,True),EntityYaw(m3d\Cam,True),0,True
            RotateEntity m3d\Cam,EntityPitch(m3d\Cam,True),EntityYaw(m3d\Cam,True)+(Sin(m3d\State)*15.0),0,True

            m3d\SoundChn = LoopSound2(AlarmSFX(0), m3d\SoundChn, m3d\Cam, m3d\Mesh, 8.0,2.0)
            ;m3d\SoundChn2 = LoopSound2(LightBlinkSFX%, m3d\SoundChn2, m3d\Cam, m3d\Mesh, 6.0,2.0)
            m3d\SoundChn2 = LightBlinkSFX%
								
			;If m3d\Misc[1]<>0 Then
			;	HideEntity m3d\Misc[1]
			;	PlaySound2(LightBlinkSFX, m3d\Cam, m3d\Misc[1], 6.0,2.0)
			;EndIf

			If m3d\State2 > 3.0 Then
				m3d\State2=Max(m3d\State2-fs\FPSfactor[0],3.0)
			Else
				m3d\State2=m3d\State2-fs\FPSfactor[0]
			EndIf
					
			If (m3d\State2=3.0) Then
				;If BumpEnabled Then
				;	RelightRoom e\room,e\room\Objects[0],e\room\Objects[1]
				;Else
				;RelightRoom e\room,e\room\Textures[0],e\room\Textures[1]
				;EndIf
				
				PlaySound2(m3d\SoundChn2, m3d\Cam, m3d\Mesh, 6.0,2.0)
				;If m3d\Misc[1]<>0 Then
				;	HideEntity m3d\Misc[1]
				;	PlaySound2(m3d\SoundChn2, m3d\Cam, m3d\Misc[1], 6.0,2.0)
				;EndIf
			Else If (m3d\State2=<0.0) Then
				;If BumpEnabled Then
				;	RelightRoom e\room,e\room\Objects[1],e\room\Objects[0]
				;Else
				;RelightRoom e\room,e\room\Textures[1],e\room\Textures[0]
				;EndIf
				;If m3d\Misc[1]<>0 Then ShowEntity m3d\Misc[1]
				m3d\State2=Float(Rand(10,30)+1)
			EndIf
					
           ;RotateEntity m3d\Cam,88.3351,180.0,0
			;[End Block]
		Case "room3pit"
			;[Block]
			PointEntity m3d\Cam,m3d\Mesh
			RotateEntity m3d\Cam,EntityPitch(m3d\Cam,True),EntityYaw(m3d\Cam,True)+(Sin(m3d\State)*20.0),0,True			
			m3d\State=WrapAngle(m3d\State+fs\FPSfactor[0]*0.3)
			
			p.Particles = CreateParticle(512.0 * RoomScale, -76.0 * RoomScale, - 688.0 * RoomScale, 0, 0.03, -0.24, 200)						
			RotateEntity(p\pvt, -90, 0, 0, True)			
			p\speed = 0.0005
			TurnEntity(p\pvt, Rnd(-55, 55), Rnd(-55, 55), 0)			
			TurnEntity p\obj, 0,0,Rnd(360)						
			p\Achange = -0.015
			p\SizeChange = 0.007
				
			p.Particles = CreateParticle(- 512.0 * RoomScale, -76.0 * RoomScale, -688.0 * RoomScale, 0, 0.03, -0.24, 200)
			RotateEntity(p\pvt, -90, 0, 0, True)			
			p\speed = 0.0005
			TurnEntity(p\pvt, Rnd(-55, 55), Rnd(-55, 55), 0)
			TurnEntity p\obj, 0,0,Rnd(360)
			p\Achange = -0.015			
			p\SizeChange = 0.007
			
			If m3d\SoundChn=0 Then
				m3d\SoundChn = PlaySound_Strict(HissSFX)
			Else
				If Not ChannelPlaying(m3d\SoundChn) Then m3d\SoundChn = PlaySound_Strict(HissSFX)
			EndIf
			ChannelVolume m3d\SoundChn,0.6
			
			UpdateParticles()
						
			;[End Block]						
			;EZ
		;MODS
		Case "room2tesla_lcz"
		    ;[Block]
		    m3d\SoundChn = LoopSound2(TeslaIdleSFX, m3d\SoundChn, m3d\Misc[0], m3d\Mesh, 8.0, 0.5)
		    ;[End Block]
		;END
	End Select
			
	;use this code to help yourself position the camera
;	If KeyDown(17) Then ;w
;		MoveEntity m3d\Cam,0,0, 0.02*fs\FPSfactor[0]
;	EndIf
;	
;	If KeyDown(31) Then ;s
;		MoveEntity m3d\Cam,0,0,-0.02*fs\FPSfactor[0]
;	EndIf
;	
;	If KeyDown(30) Then ;a
;		MoveEntity m3d\Cam,-0.02*fs\FPSfactor[0],0,0
;	EndIf
;	
;	If KeyDown(32) Then ;d
;		MoveEntity m3d\Cam,0.02*fs\FPSfactor[0],0,0
;	EndIf
;	
;	If KeyDown(200) Then ;up
;		m3d\State=m3d\State-fs\FPSfactor[0]*0.5
;	EndIf
;	
;	If KeyDown(208) Then ;down
;		m3d\State=m3d\State+fs\FPSfactor[0]*0.5
;	EndIf
;	
;	If KeyDown(203) Then ;left
;		m3d\State2=m3d\State2+fs\FPSfactor[0]*0.5
;	EndIf
;	
;	If KeyDown(205) Then ;right
;		m3d\State2=m3d\State2-fs\FPSfactor[0]*0.5
;	EndIf
;	
;	RotateEntity m3d\Cam,m3d\State,m3d\State2,0
	
	RenderWorld ;Renders the world
    CameraProjMode m3d\Cam,0 ;Disable the projector				
End Function

Function DeInit3DMenu()
    Local ml.Menu3DLights = First Menu3DLights
	
	ClearTextureCache
		
	Select m3d\Room
		Case "lockroom"
			FreeTexture at\ParticleTextureID[0]
		Case "room009"
		    m3d\SoundChn2 = 0					
	End Select
	
	FreeEntity m3d\Sprite[0] : m3d\Sprite[0] = 0
	FreeEntity m3d\Sprite[1] : m3d\Sprite[1] = 0
	FreeEntity m3d\Sprite[2] : m3d\Sprite[2] = 0
	For i%=0 To 9
		If m3d\Misc[i]<>0 Then FreeEntity m3d\Misc[i] : m3d\Misc[i]=0
		If ml\MenuLights[i]<>0 Then FreeEntity ml\MenuLights[i] : FreeEntity ml\MenuLightSprites[i] : ml\MenuLights[i]=0 : ml\MenuLightSprites[i]=0
		If EnableRoomLights Then
		    If ml\MenuLights[i]<>0 Then FreeEntity ml\MenuLightSprites2[i] : ml\MenuLightSprites2[i]=0
		EndIf  
	Next
	m3d\Room = ""
	m3d\State = 0 : m3d\State2 = 0
	If m3d\SoundChn<>0 Then
		StopChannel m3d\SoundChn
		m3d\SoundChn = 0
	EndIf
	If m3d\SoundChn2<>0 Then
		StopChannel m3d\SoundChn2
		m3d\SoundChn2 = 0
	EndIf
	
	FreeEntity m3d\Cam : m3d\Cam = 0
	FreeEntity m3d\Mesh : m3d\Mesh = 0
	
	For pr.Props = Each Props
		Delete pr
	Next
	
	For p.Particles = Each Particles
		Delete p
	Next
	
	For mat.Materials = Each Materials
		Delete mat
	Next
	
	ClearWorld
	ReloadAAFont()
	Camera = 0
	ark_blur_cam = 0
	InitFastResize()
	
	For i=0 To 9
		If TempSounds[i]<>0 Then FreeSound_Strict TempSounds[i] : TempSounds[i]=0
	Next
	
End Function

Function LoadRMesh_3DMenu(file$) ;this ignores some stuff that we don't need
	;generate a texture made of white
	Local blankTexture%
	blankTexture=CreateTexture(4,4,1,1)
	ClsColor 255,255,255
	SetBuffer TextureBuffer(blankTexture)
	Cls
	SetBuffer BackBuffer()
	ClsColor 0,0,0
	
	;read the file
	Local f%=ReadFile(file)
	Local i%,j%,k%,x#,y#,z#,yaw#
	Local vertex%
	Local temp1i%,temp2i%,temp3i%
	Local temp1#,temp2#,temp3#
	Local temp1s$,temp2s$
	
	Local collisionMeshes% = CreatePivot()
		
	Local hasTriggerBox% = False ;See if it works on room that has triggerbox
	
	Local ml.Menu3DLights = New Menu3DLights
		
	For i=0 To 3 ;reattempt up to 3 times
		If f=0 Then
			f=ReadFile(file)
		Else
			Exit
		EndIf
	Next
	If f=0 Then RuntimeError "Error reading file "+Chr(34)+file+Chr(34)
	Local isRMesh$ = ReadString(f)	
	If isRMesh$="RoomMesh"
		;Continue
	ElseIf isRMesh$="RoomMesh.HasTriggerBox"
		hasTriggerBox% = True
	Else
		RuntimeError Chr(34)+file+Chr(34)+" is Not RMESH ("+isRMesh+")"
	EndIf
		
	;If ReadString(f)<>"RoomMesh" Then RuntimeError Chr(34)+file+Chr(34)+" is not RMESH"
	
	file=StripFilename(file)
	
	Local count%,count2%
	;drawn meshes
	
	Local Opaque%,Alpha%
	
	Opaque=CreateMesh()
	Alpha=CreateMesh()
	
	count = ReadInt(f)
	Local childMesh%
	Local surf%,tex%[2],brush%
	
	Local isAlpha%
	
	Local u#,v#
	
	For i=1 To count ;drawn mesh
		childMesh=CreateMesh()
		
		surf=CreateSurface(childMesh)
		
		brush=CreateBrush()
		
		tex[0]=0 : tex[1]=0
		
		isAlpha=0
		For j=0 To 1
			temp1i=ReadByte(f)
			If temp1i<>0 Then
				temp1s=ReadString(f)
				tex[j]=GetTextureFromCache(temp1s)
				If tex[j]=0 Then ;texture is not in cache
					Select True
						Case temp1i<3
							tex[j]=LoadTexture(file+temp1s,1)
						Default
							tex[j]=LoadTexture(file+temp1s,3)
					End Select
					
					If tex[j]<>0 Then
						If temp1i=1 Then TextureBlend tex[j],5
						AddTextureToCache(tex[j])
					EndIf
					
				EndIf
				If tex[j]<>0 Then
					isAlpha=2
					If temp1i=3 Then isAlpha=1
					
					TextureCoords tex[j],1-j
				EndIf
			EndIf
		Next
		
		If isAlpha=1 Then
			If tex[1]<>0 Then
				TextureBlend tex[1],2
				BrushTexture brush,tex[1],0,0
			Else
				BrushTexture brush,blankTexture,0,0
			EndIf
		Else
			
;			If BumpEnabled And temp1s<>"" Then
;				bumptex = GetBumpFromCache(temp1s)	
;			Else
;				bumptex = 0
;			EndIf
			
;			If bumptex<>0 Then 
;			BrushTexture brush, tex[1], 0, 0	
;			BrushTexture brush, bumptex, 0, 1
;			If tex[0]<>0 Then 
;				BrushTexture brush, tex[0], 0, 2	
;			Else
;				BrushTexture brush,blankTexture,0,2
;			EndIf
;			Else
			For j=0 To 1
				If tex[j]<>0 Then
					BrushTexture brush,tex[j],0,j
				Else
					BrushTexture brush,blankTexture,0,j
				EndIf
			Next				
;			EndIf
			
		EndIf
		
		surf=CreateSurface(childMesh)
		
		If isAlpha>0 Then PaintSurface surf,brush
		
		FreeBrush brush : brush = 0
		
		count2=ReadInt(f) ;vertices
		
		For j%=1 To count2
			;world coords
			x=ReadFloat(f) : y=ReadFloat(f) : z=ReadFloat(f)
			vertex=AddVertex(surf,x,y,z)
			
			;texture coords
			For k%=0 To 1
				u=ReadFloat(f) : v=ReadFloat(f)
				VertexTexCoords surf,vertex,u,v,0.0,k
			Next
			
			;colors
			temp1i=ReadByte(f)
			temp2i=ReadByte(f)
			temp3i=ReadByte(f)
			VertexColor surf,vertex,temp1i,temp2i,temp3i,1.0
		Next
		
		count2=ReadInt(f) ;polys
		For j%=1 To count2
			temp1i = ReadInt(f) : temp2i = ReadInt(f) : temp3i = ReadInt(f)
			AddTriangle(surf,temp1i,temp2i,temp3i)
		Next
		
		If isAlpha=1 Then
			AddMesh childMesh,Alpha
		Else
			AddMesh childMesh,Opaque
		EndIf
		EntityParent childMesh,Opaque
		EntityAlpha childMesh,0.0
		EntityType childMesh,HIT_MAP
		EntityPickMode childMesh,2
		
	Next
	
	If BumpEnabled Then; And 0 Then	
		For i = 2 To CountSurfaces(Opaque)
			sf = GetSurface(Opaque,i)
			b = GetSurfaceBrush( sf )
			If b<>0 Then
				t = GetBrushTexture(b, 1)
				If t<>0 Then
					texname$ =  StripPath(TextureName(t))
					
					For mat.Materials = Each Materials
						If texname = mat\name Then
							If mat\Bump <> 0 Then 
								t1 = GetBrushTexture(b,0)
								
								If t1<>0 Then
									BrushTexture b, t1, 0, 1 ;light map
									BrushTexture b, mat\Bump, 0, 0 ;bump
									BrushTexture b, t, 0, 2 ;diff
									
									PaintSurface sf,b
									
									FreeTexture t1
								EndIf
								
								;If t1<>0 Then FreeTexture t1
								;If t2 <> 0 Then FreeTexture t2						
							EndIf
							Exit
						EndIf 
					Next
					
					FreeTexture t
				EndIf
				FreeBrush b
			EndIf
			
		Next
	EndIf
	
;	Local hiddenMesh%
;	hiddenMesh=CreateMesh()
		
	count=ReadInt(f) ;invisible collision mesh
	For i%=1 To count
		;surf=CreateSurface(hiddenMesh)
		count2=ReadInt(f) ;vertices
		For j%=1 To count2
			;world coords
			x=ReadFloat(f) : y=ReadFloat(f) : z=ReadFloat(f)
			;vertex=AddVertex(surf,x,y,z)
		Next
		
		count2=ReadInt(f) ;polys
		For j%=1 To count2
			temp1i = ReadInt(f) : temp2i = ReadInt(f) : temp3i = ReadInt(f)
			;AddTriangle(surf,temp1i,temp2i,temp3i)
		Next
	Next

	;trigger boxes imported from window3d
	If hasTriggerBox
		numb = ReadInt(f)
		For tb = 0 To numb-1
			count = ReadInt(f)
			For i%=1 To count
				count2=ReadInt(f)
				For j%=1 To count2
					ReadFloat(f) : ReadFloat(f) : ReadFloat(f)
				Next
				count2=ReadInt(f)
				For j%=1 To count2
					ReadInt(f) : ReadInt(f) : ReadInt(f)
				Next
			Next
			ReadString(f)
		Next
	EndIf
		
	count=ReadInt(f) ;point entities
	
	Local lightTex% = LoadTexture(GFXPath$+"light.png", 1)
	Local lightSprite% = LoadTexture(GFXPath$+"lightsprite.png", 1)
	
	For i%=1 To count
		temp1s=ReadString(f)
		Select temp1s
			Case "screen"
				
				temp1=ReadFloat(f);*RoomScale
				temp2=ReadFloat(f);*RoomScale
				temp3=ReadFloat(f);*RoomScale
				
				temp2s=ReadString(f)
				
;				If temp1<>0 Or temp2<>0 Or temp3<>0 Then 
;					Local ts.TempScreens = New TempScreens	
;					ts\x = temp1
;					ts\y = temp2
;					ts\z = temp3
;					ts\imgpath = temp2s
;					ts\roomtemplate = rt
;				EndIf
				
			Case "waypoint"
				
				temp1=ReadFloat(f);*RoomScale
				temp2=ReadFloat(f);*RoomScale
				temp3=ReadFloat(f);*RoomScale
				
;				Local w.TempWayPoints = New TempWayPoints
;				w\roomtemplate = rt
;				w\x = temp1
;				w\y = temp2
;				w\z = temp3
				
			Case "light"
			
				temp1=ReadFloat(f);*RoomScale
				temp2=ReadFloat(f);*RoomScale
				temp3=ReadFloat(f);*RoomScale
				
				Local canAddLight% = -1
				For i=0 To 9
					If ml\MenuLights[i]=0 Then canAddLight=i : Exit
				Next
			
				If (temp1<>0 Or temp2<>0 Or temp3<>0) And (canAddLight>-1) Then 
					range# = ReadFloat(f)/2000.0
					lcolor$=ReadString(f)
					intensity# = Min(ReadFloat(f)*0.8,1.0)
					r%=Int(Piece(lcolor,1," "))*intensity
					g%=Int(Piece(lcolor,2," "))*intensity
					b%=Int(Piece(lcolor,3," "))*intensity
					
					ml\MenuLights[canAddLight]=CreateLight(2)
					LightRange(ml\MenuLights[canAddLight], range)
					LightColor(ml\MenuLights[canAddLight],r, g, b)
					EntityParent(ml\MenuLights[canAddLight], Opaque)
					PositionEntity(ml\MenuLights[canAddLight], temp1, temp2, temp3, True)
					
					;room\LightIntensity[i] = (r+g+b)/255.0/3.0
					
					ml\MenuLightSprites[canAddLight]= CreateSprite()
					ScaleSprite(ml\MenuLightSprites[canAddLight], 0.13 , 0.13)
					EntityTexture(ml\MenuLightSprites[canAddLight], lightTex)
					EntityBlend (ml\MenuLightSprites[canAddLight], 3)
					EntityParent(ml\MenuLightSprites[canAddLight],Opaque)
					PositionEntity(ml\MenuLightSprites[canAddLight], temp1, temp2, temp3, True)
					
					If EnableRoomLights Then
					    ml\MenuLightSprites2[canAddLight]= CreateSprite()
					    ScaleSprite(ml\MenuLightSprites2[canAddLight], 0.2 , 0.25)
					    EntityTexture(ml\MenuLightSprites2[canAddLight], lightSprite)
					    EntityBlend (ml\MenuLightSprites2[canAddLight], 3)
					    EntityParent(ml\MenuLightSprites2[canAddLight], Opaque)
					    EntityColor(ml\MenuLightSprites2[canAddLight], r, g, b)
					    PositionEntity(ml\MenuLightSprites2[canAddLight], temp1, temp2, temp3, True)
					    SpriteViewMode(ml\MenuLightSprites2[canAddLight],1)
					    RotateEntity(ml\MenuLightSprites2[canAddLight],0,0,Rand(360))
					EndIf
				Else
					ReadFloat(f) : ReadString(f) : ReadFloat(f)
				EndIf
				
			Case "spotlight"
				
				temp1=ReadFloat(f);*RoomScale
				temp2=ReadFloat(f);*RoomScale
				temp3=ReadFloat(f);*RoomScale
					
				canAddLight% = -1
				For i=0 To 9
					If ml\MenuLights[i]=0 Then canAddLight=i : Exit
				Next
			
				If (temp1<>0 Or temp2<>0 Or temp3<>0) And (canAddLight>-1) Then 
					range# = ReadFloat(f)/2000.0
					lcolor$=ReadString(f)
					intensity# = Min(ReadFloat(f)*0.8,1.0)
					r%=Int(Piece(lcolor,1," "))*intensity
					g%=Int(Piece(lcolor,2," "))*intensity
					b%=Int(Piece(lcolor,3," "))*intensity
					
;					Local lt.LightTemplates = AddTempLight(rt, temp1,temp2,temp3, 2, range, r,g,b)
;					Local ml.Menu3DLights = AddLightMenu3D(temp1,temp2,temp3, 2,range,r,g,b)
					angles$=ReadString(f)
					pitch#=Piece(angles,1," ")
					yaw#=Piece(angles,2," ")					
;					lt\pitch = pitch
;					lt\yaw = yaw
;					
					innerconeangle% = ReadInt(f)
					outerconeangle% = ReadInt(f)
					
					ml\MenuLights[canAddLight]=CreateLight(3)
					LightRange(ml\MenuLights[canAddLight],range)
					LightColor(ml\MenuLights[canAddLight], r, g, b)
					EntityParent(ml\MenuLights[canAddLight],Opaque)
					LightConeAngles(ml\MenuLights[canAddLight], innerconeangle, outerconeangle)
					RotateEntity(ml\MenuLights[canAddLight], pitch, yaw, 0)					
					PositionEntity(ml\MenuLights[canAddLight], temp1, temp2, temp3, True)
					
					ml\MenuLightSprites[canAddLight]= CreateSprite()
				    ScaleSprite(ml\MenuLightSprites[canAddLight], 0.13 , 0.13)
					EntityTexture(ml\MenuLightSprites[canAddLight], lightTex)
					EntityBlend (ml\MenuLightSprites[canAddLight], 3)
					EntityParent(ml\MenuLightSprites[canAddLight],Opaque)
					PositionEntity(ml\MenuLightSprites[canAddLight], temp1, temp2, temp3, True)
					
					If EnableRoomLights Then
					    ml\MenuLightSprites2[canAddLight]= CreateSprite()
					    ScaleSprite(ml\MenuLightSprites2[canAddLight], 0.2, 0.25)
					    EntityTexture(ml\MenuLightSprites2[canAddLight], lightSprite)
					    EntityBlend (ml\MenuLightSprites2[canAddLight], 3)
					    EntityParent(ml\MenuLightSprites2[canAddLight], Opaque)
					    EntityColor(ml\MenuLightSprites2[canAddLight], r, g, b)
					    PositionEntity(ml\MenuLightSprites2[canAddLight], temp1, temp2, temp3, True)
					    SpriteViewMode(ml\MenuLightSprites2[canAddLight], 1)
					    RotateEntity(ml\MenuLightSprites2[canAddLight],0 ,0, Rand(360))
					EndIf				
				Else
					ReadFloat(f) : ReadString(f) : ReadFloat(f) : ReadString(f) : ReadInt(f) : ReadInt(f)
				EndIf
				
			Case "soundemitter"
				
				temp1i=0
				
;				For j = 0 To 3
;					If rt\TempSoundEmitter[j]=0 Then
;						rt\TempSoundEmitterX[j]=ReadFloat(f)*RoomScale
;						rt\TempSoundEmitterY[j]=ReadFloat(f)*RoomScale
;						rt\TempSoundEmitterZ[j]=ReadFloat(f)*RoomScale
;						rt\TempSoundEmitter[j]=ReadInt(f)
;						
;						rt\TempSoundEmitterRange[j]=ReadFloat(f)
;						temp1i=1
;						Exit
;					EndIf
;				Next
;				
;				If temp1i=0 Then
				ReadFloat(f)
				ReadFloat(f)
				ReadFloat(f)
				ReadInt(f)
				ReadFloat(f)
;				EndIf
				
			Case "playerstart"
				
				temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
				
				angles$=ReadString(f)
				pitch#=Piece(angles,1," ")
				yaw#=Piece(angles,2," ")
				roll#=Piece(angles,3," ")
				If cam Then
					PositionEntity cam,temp1,temp2,temp3
					RotateEntity cam,pitch,yaw,roll
				EndIf
				
			Case "model"
				file = ReadString(f)
				If file<>""
					Local model = CreatePropObj(MapPath$+"Props\"+file);LoadMesh(MapPath$+"Props\"+file)		
					
					temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
					PositionEntity model,temp1,temp2,temp3			
					
					temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
					RotateEntity model,temp1,temp2,temp3
					
					temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
					ScaleEntity model,temp1,temp2,temp3
					
					EntityParent model,Opaque
					EntityType model,HIT_MAP
					;EntityPickMode model,2
				Else
					temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
										
					;Stop
				EndIf
				
		End Select
	Next
	
	FreeTexture lightTex
	FreeTexture lightsprite
	
	Local obj%
	
	temp1i=CopyMesh(Alpha)
	FlipMesh temp1i
	AddMesh temp1i,Alpha
	FreeEntity temp1i
	
	If brush <> 0 Then FreeBrush brush
	
	AddMesh Alpha,Opaque
	FreeEntity Alpha
	
	EntityFX Opaque,3
	
	;EntityAlpha hiddenMesh,0.0
	EntityAlpha Opaque,1.0
	
	;EntityType Opaque,HIT_MAP
	;EntityType hiddenMesh,HIT_MAP
	FreeTexture blankTexture
	
;	obj=CreatePivot()
;	CreatePivot(obj) ;skip "meshes" object
;	EntityParent Opaque,obj
;	EntityParent hiddenMesh,obj
;	CreatePivot(Room) ;skip "pointentites" object
;	CreatePivot(Room) ;skip "solidentites" object
		
	CloseFile f
	
	Return Opaque
End Function

;~IDEal Editor Parameters:
;~F#4D
;~C#Blitz3D