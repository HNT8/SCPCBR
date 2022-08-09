Global Curr173.NPCs, Curr106.NPCs, Curr096.NPCs, Curr5131.NPCs

;{~--<MOD>--~}

Global Curr650.NPCs, Curr066.NPCs

;{~--<END>--~}

Type NPCs
	Field obj%, obj2%, obj3%, obj4%, Collider%
	Field NPCtype%, ID%
	Field DropSpeed#, Gravity%
	Field State#, State2#, State3#, PrevState%
	Field MakingNoise%
	
	Field Frame#
	
	Field Angle#
	Field Sound%, SoundCHN%, SoundTimer#
	Field Sound2%, SoundCHN2%
	
	Field Speed#, CurrSpeed#
	
	Field texture$
	
	Field Idle#
	
	Field Reload#
	
	Field LastSeen%, LastDist#
	
	Field PrevX#, PrevY#, PrevZ#
	
	Field Target.NPCs, TargetID%
	Field EnemyX#, EnemyY#, EnemyZ#
	
	Field Path.WayPoints[20], PathStatus%, PathTimer#, PathLocation%
	
	Field NVX#,NVY#,NVZ#,NVName$
	
	Field GravityMult# = 1.0
	Field MaxGravity# = 0.2
	Field MTFVariant%
	Field MTFLeader.NPCs
	Field IsDead%
	Field BlinkTimer# = 1.0
	Field IgnorePlayer%
	
	Field ManipulateBone%
	Field ManipulationType%
	Field BoneToManipulate$
	Field BonePitch#
	Field BoneYaw#
	Field BoneRoll#
	Field NPCNameInSection$
	Field InFacility% = True
	Field CanUseElevator% = False
	Field CurrElevator.ElevatorObj
	Field HP%
	Field PathX#,PathZ#
	Field Model$
	Field ModelScaleX#,ModelScaleY#,ModelScaleZ#
	Field HideFromNVG
	Field TextureID%=-1
	Field CollRadius#
	Field IdleTimer#
	Field SoundCHN_IsStream%, SoundCHN2_IsStream%
	Field FallingPickDistance#
	
	;{~--<MOD>--~}
	
    Field UseHeadPhones% = False
	Field TargetEnt%
	Field MTF2Leader.NPCs
	
	;{~--<END>--~}
	
End Type

Function CreateNPC.NPCs(NPCtype%, x#, y#, z#)
	Local n.NPCs = New NPCs, n2.NPCs
	Local temp#, i%, diff1, bump1, spec1
	Local sf, b, t1
	Local o.Objects = First Objects
	
	n\NPCtype = NPCtype
	n\GravityMult = 1.0
	n\MaxGravity = 0.2
	n\CollRadius = 0.2
	n\FallingPickDistance = 10
	Select NPCtype
		Case NPCtype173
			;[Block]
			n\NVName = "SCP-173"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.23, 0.32
			EntityType n\Collider, HIT_PLAYER
			n\Gravity = True
			
			n\obj = CopyEntity(o\NPCModelID[0])
			
			;On Halloween set jack-o-latern texture.
			If (Left(CurrentDate(), 7) = "31 Oct ") Then
				at\OtherTextureID[0] = True
				Local texFestive = LoadTexture_Strict(NPCsPath$+"scp_173_h.pt", 1)
				EntityTexture n\obj, texFestive, 0, 0
				FreeTexture texFestive
			EndIf
			
			;On Halloween set jack-o-latern texture.
			If (Left(CurrentDate(), 7) = "1 Jan ") Then
				at\OtherTextureID[1] = True
				Local texFestive2 = LoadTexture_Strict(NPCsPath$+"scp_173_ny.pt", 1)
				EntityTexture n\obj, texFestive2, 0, 0
				FreeTexture texFestive2
			EndIf
			
			temp# = (GetINIFloat("Data\NPCs.ini", "SCP-173", "scale") / MeshDepth(n\obj))			
			ScaleEntity n\obj, temp,temp,temp
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-173", "speed") / 100.0)
			
			n\obj2 = CopyEntity(o\NPCModelID[34])
			ScaleEntity n\obj2, RoomScale, RoomScale, RoomScale
			HideEntity n\obj2
			
			n\CollRadius = 0.32
			;[End Block]
		Case NPCtype106
			;[Block]
			n\NVName = "SCP-106"
			n\Collider = CreatePivot()
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			n\obj = CopyEntity(o\NPCModelID[1])
			
			temp# = (GetINIFloat("Data\NPCs.ini", "SCP-106", "scale") / 2.2)		
			ScaleEntity n\obj, temp, temp, temp
			
			Local OldManEyes% = LoadTexture_Strict(NPCsPath$+"scp_106_eyes.png")
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-106", "speed") / 100.0)
			
			n\obj2 = CreateSprite()
			ScaleSprite(n\obj2, 0.03, 0.03)
			EntityTexture(n\obj2, OldManEyes)
			EntityBlend (n\obj2, 3)
			EntityFX(n\obj2, 1 + 8)
			SpriteViewMode(n\obj2, 2)
			
			FreeTexture OldManEyes%
			;[End Block]
		Case NPCtypeGuard
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			n\obj = CopyEntity(o\NPCModelID[2]) 
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "Guard", "speed") / 100.0)
			temp# = (GetINIFloat("Data\NPCs.ini", "Guard", "scale") / 2.5)
			
			ScaleEntity n\obj, temp, temp, temp
			
			MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			;[End Block]
		Case NPCtypeD
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.32
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = CopyEntity(o\NPCModelID[3])
			
			temp# = 0.5 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			
			n\Speed = 2.0 / 100
			
			MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			
			n\CollRadius = 0.32
			;[End Block]
		Case NPCtype372
			;[Block]
			n\NVName = "SCP-372"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			n\obj = CopyEntity(o\NPCModelID[4])
			
			temp# = 0.3 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			;[End Block]

		Case NPCtypeApache
			;[Block]
			n\NVName = "Apache Helicopter"
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			n\obj = CopyEntity(o\NPCModelID[5])
			
			n\obj2 = CopyEntity(o\NPCModelID[6])
			EntityParent n\obj2, n\obj
			
			For i = -1 To 1 Step 2
				Local rotor2 = CopyEntity(n\obj2,n\obj2)
				RotateEntity rotor2,0,4.0*i,0
				EntityAlpha rotor2, 0.5
			Next
			
			n\obj3 = CopyEntity(o\NPCModelID[26])
			EntityParent n\obj3, n\obj
			PositionEntity n\obj3, 0.0, 2.15, -5.48
			
			EntityType n\Collider, HIT_APACHE
			EntityRadius n\Collider, 3.0
			
			For i = -1 To 1 Step 2
				Local Light1 = CreateLight(2,n\obj)
				LightRange(Light1,2.0)
				LightColor(Light1,255,255,255)
				PositionEntity(Light1, 1.65*i, 1.17, -0.25)
				
				Local lightsprite = CreateSprite(n\obj)
				PositionEntity(lightsprite, 1.65*i, 1.17, 0, -0.25)
				ScaleSprite(lightsprite, 0.13, 0.13)
				EntityTexture(lightsprite, at\LightSpriteTextureID[0])
				EntityBlend (lightsprite, 3)
				EntityFX lightsprite, 1+8				
			Next
			
			temp# = 0.7
			ScaleEntity n\obj, temp, temp, temp
			;[End Block]
			
		Case NPCtypeMTF
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2) Then
					n\obj = CopyEntity(n2\obj)
					Exit
				EndIf
			Next

            If n\obj = 0 Then
			    n\obj = CopyEntity(o\NPCModelID[7]) 
			
			    temp# = (GetINIFloat("Data\NPCs.ini", "MTF", "scale") / 2.5)
			
			    ScaleEntity n\obj, temp, temp, temp
			
			    MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2) 
			EndIf
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "MTF", "speed") / 100.0)
			
			If MTFSFX(0) = 0 Then
				MTFSFX(0) = LoadSound_Strict(SFXPath$+"Character\MTF\ClassD1.ogg")
				MTFSFX(1) = LoadSound_Strict(SFXPath$+"Character\MTF\ClassD2.ogg")
				MTFSFX(2) = LoadSound_Strict(SFXPath$+"Character\MTF\ClassD3.ogg")			
				MTFSFX(3) = LoadSound_Strict(SFXPath$+"Character\MTF\ClassD4.ogg")
				;MTFSFX(4) = LoadSound_Strict(SFXPath$+"Character\MTF\Tesla0.ogg")
				MTFSFX(5) = LoadSound_Strict(SFXPath$+"Character\MTF\Beep.ogg")
				MTFSFX(6) = LoadSound_Strict(SFXPath$+"Character\MTF\Breath.ogg")
			EndIf
			If MTFrooms[6] = Null Then 
				For r.Rooms = Each Rooms
					Select Lower(r\RoomTemplate\Name)
						Case "room106"
							MTFrooms[0] = r
						Case "room372"
							MTFrooms[1] = r	
						Case "room079"
							MTFrooms[2] = r	
						Case "room2poffices"
							MTFrooms[3] = r	
						Case "room914"
							MTFrooms[4] = r	
						Case "room895"
							MTFrooms[5] = r	
						Case "room173"
							MTFrooms[6] = r
					End Select
				Next			
			EndIf
			;[End Block]
		Case NPCtype096
			;[Block]
			n\NVName = "SCP-096"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.26
			EntityType n\Collider, HIT_PLAYER
			n\obj = CopyEntity(o\NPCModelID[8])
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-096", "speed") / 100.0)
			
			temp# = (GetINIFloat("Data\NPCs.ini", "SCP-096", "scale") / 3.0)
			ScaleEntity n\obj, temp, temp, temp	
			
			MeshCullBox (n\obj, -MeshWidth(n\obj)*2, -MeshHeight(n\obj)*2, -MeshDepth(n\obj)*2, MeshWidth(n\obj)*2, MeshHeight(n\obj)*4, MeshDepth(n\obj)*4)
			
			n\CollRadius = 0.26
			;[End Block]

		Case NPCtype049
			;[Block]
			n\NVName = "SCP-049"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			n\obj = CopyEntity(o\NPCModelID[9])
			
			n\Speed = (GetINIFloat("DATA\NPCs.ini", "SCP-049", "speed") / 100.0)
			
			temp# = GetINIFloat("DATA\NPCs.ini", "SCP-049", "scale")
			ScaleEntity n\obj, temp, temp, temp	
			
			n\Sound = LoadSound_Strict(SFXPath$+"Horror\Horror12.ogg")
			
			If HorrorSFX(13)=0 Then HorrorSFX(13)=LoadSound_Strict(SFXPath$+"Horror\Horror13.ogg")
			
			n\CanUseElevator = True
			;[End Block]
		Case NPCtype049_2
			;[Block]
			n\NVName = "SCP-049-2"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2) Then
					n\obj = CopyEntity (n2\obj)
					Exit
				EndIf
			Next

			If n\obj = 0 Then 
				n\obj = CopyEntity(o\NPCModelID[10])
				
				temp# = (GetINIFloat("Data\NPCs.ini", "SCP-049-2", "scale") / 2.5)
				ScaleEntity n\obj, temp, temp, temp
				
				MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			EndIf
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-049-2", "speed") / 100.0)
			
			SetAnimTime(n\obj, 107)
			
			n\Sound = LoadSound_Strict(SFXPath$+"SCP\049_2\Breath.ogg")
			
			n\HP = 100
			;[End Block]
		Case NPCtype513_1
			;[Block]
			n\NVName = "SCP-513-1"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
		    n\obj = CopyEntity(o\NPCModelID[11])
			
			n\obj2 = CopyEntity(n\obj)
			EntityAlpha n\obj2, 0.6
			
			temp# = 1.8 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			ScaleEntity n\obj2, temp, temp, temp
			;[End Block]

		Case NPCtype035_Tentacle
			;[Block]
			n\NVName = "Unidentified"
			
			n\Collider = CreatePivot()
			
			For n2.NPCs = Each NPCs
				If n\NPCtype = n2\NPCtype And n<>n2 Then
					n\obj = CopyEntity (n2\obj)
					Exit
				EndIf
			Next
			
			If n\obj = 0 Then
			    n\obj = CopyEntity(o\NPCModelID[12])
				ScaleEntity n\obj, 0.065,0.065,0.065
			EndIf
			
			SetAnimTime n\obj, 283
			
			n\HP = 150
			;[End Block]
		Case NPCtype860_2
			;[Block]
			n\NVName = "Unidentified"
			
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.25
			EntityType n\Collider, HIT_PLAYER
			n\obj = CopyEntity(o\NPCModelID[13])
			
			EntityFX(n\obj, 1)
			
			tex = LoadTexture_Strict(NPCsPath$+"scp_860_2_eyes.png",1+2)
			
			n\obj2 = CreateSprite()
			ScaleSprite(n\obj2, 0.1, 0.1)
			EntityTexture(n\obj2, tex)
			FreeTexture tex
			
			EntityFX(n\obj2, 1 + 8)
			EntityBlend(n\obj2, BLEND_ADD)
			SpriteViewMode(n\obj2, 2)
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-860-2", "speed") / 100.0)
			
			temp# = (GetINIFloat("Data\NPCs.ini", "SCP-860-2", "scale") / 20.0)
			ScaleEntity n\obj, temp, temp, temp	
			
			MeshCullBox (n\obj, -MeshWidth(n\obj)*2, -MeshHeight(n\obj)*2, -MeshDepth(n\obj)*2, MeshWidth(n\obj)*2, MeshHeight(n\obj)*4, MeshDepth(n\obj)*4)
			
			n\CollRadius = 0.25
			;[End Block]
		Case NPCtype939
			;[Block]
			Local amount939% = 0
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2)
					amount939% = amount939% + 1
				EndIf
			Next
			If amount939% = 0 Then i = 53
			If amount939% = 1 Then i = 89
			If amount939% = 2 Then i = 96
			If amount939% = 3 Then i = 24
			n\NVName = "SCP-939-"+i
			
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.3
			EntityType n\Collider, HIT_PLAYER
			For n2.NPCs = Each NPCs
				If n\NPCtype = n2\NPCtype And n<>n2 Then
					n\obj = CopyEntity (n2\obj)
					Exit
				EndIf
			Next
			
			If n\obj = 0 Then 
			    n\obj = CopyEntity(o\NPCModelID[14])
							
				temp# = GetINIFloat("Data\NPCs.ini", "SCP-939", "scale") / 2.5
				ScaleEntity n\obj, temp, temp, temp		
			EndIf
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-939", "speed") / 100.0)
			
			n\CollRadius = 0.3
			;[End Block]
		Case NPCtype066
			;[Block]
			n\NVName = "SCP-066"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = CopyEntity(o\NPCModelID[15])
			temp# = GetINIFloat("Data\NPCs.ini", "SCP-066", "scale") / 2.5
			ScaleEntity n\obj, temp, temp, temp		
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-066", "speed") / 100.0)
			;[End Block]
		Case NPCtype966
			;[Block]
			i = 1
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2) Then i=i+1
			Next
			n\NVName = "SCP-966-"+i
			
			n\Collider = CreatePivot()
			EntityRadius n\Collider,0.2
			
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2) Then
					n\obj = CopyEntity (n2\obj)
					Exit
				EndIf
			Next
			
			If n\obj = 0 Then 
			    n\obj = CopyEntity(o\NPCModelID[16])
			
			    EntityFX n\obj,1
			
			    temp# = GetINIFloat("Data\NPCs.ini", "SCP-966", "scale") / 40.0
			    ScaleEntity n\obj, temp, temp, temp		
			EndIf
						
			SetAnimTime n\obj,15.0
			
			EntityType n\Collider,HIT_PLAYER
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-966", "speed") / 100.0)
			;[End Block]
		Case NPCtype1048_A
			;[Block]
			n\NVName = "SCP-1048-A"
			n\obj = CopyEntity(o\NPCModelID[17])
			ScaleEntity n\obj, 0.05,0.05,0.05
			SetAnimTime(n\obj, 2)
			
			n\Sound = LoadSound_Strict(SFXPath$+"SCP\1048A\Shriek.ogg")
			n\Sound2 = LoadSound_Strict(SFXPath$+"SCP\1048A\Growth.ogg")
			;[End Block]

		Case NPCtype1499_1
			;[Block]
			n\NVName = "Unidentified"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2) Then
					n\obj = CopyEntity (n2\obj)
					Exit
				EndIf
			Next
			
			If n\obj = 0 Then n\obj = CopyEntity(o\NPCModelID[18])

			n\Speed = (GetINIFloat("DATA\NPCs.ini", "SCP-1499-1", "speed") / 100.0) * Rnd(0.9, 1.1)
			temp# = (GetINIFloat("DATA\NPCs.ini", "SCP-1499-1", "scale") / 4.0) * Rnd(0.8,1.0)
			
			ScaleEntity n\obj, temp, temp, temp
			
			EntityFX n\obj,1
			
			EntityAutoFade n\obj,HideDistance*2.5,HideDistance*2.95
			;[End Block]

		Case NPCtype008_1
			;[Block]
			n\NVName = "SCP-008-1"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = CopyEntity(o\NPCModelID[19])
			
			temp# = 0.5 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			
			n\Speed = 2.0 / 100
			
			MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			
			SetNPCFrame n, 11
			
			n\Sound = LoadSound_Strict(SFXPath$+"SCP\008_1\Breath.ogg")
			
			n\HP = 120
			;[End Block]
		Case NPCtypeClerk
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.32
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = CopyEntity(o\NPCModelID[20])
			
			temp# = 0.5 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			
			n\Speed = 2.0 / 100
			
			MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			
			n\CollRadius = 0.32
			;[End Block]
			
		;{~--<MOD>--~}
		
		Case NPCtype008_2
			;[Block]
			n\NVName = "SCP-008-2"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			
			If n\obj = 0 Then 
				n\obj = CopyEntity(o\NPCModelID[21]) 
				
				temp# = (GetINIFloat("Data\NPCs.ini", "SCP-008-2", "scale") / 2.5)
				ScaleEntity n\obj, temp, temp, temp
				
				MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			EndIf
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-008-2", "speed") / 100.0)

			SetAnimTime(n\obj, 107)
						
			n\Sound = LoadSound_Strict(SFXPath$+"SCP\008_1\Breath.ogg")
			
			n\HP = 120
			;[End Block]
		Case NPCtype650
			;[Block]
			
			;Placeholder
			n\NVName = "SCP-650"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.23, 0.32
			EntityType n\Collider, HIT_PLAYER
			n\Gravity = True
			
			n\obj = CopyEntity(o\NPCModelID[22])
			
			temp# = (GetINIFloat("Data\NPCs.ini", "SCP-650", "scale") / MeshDepth(n\obj))			
			ScaleEntity n\obj, temp,temp,temp
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-650", "speed") / 100.0)
			
			SetNPCFrame n, 1
			
			n\CollRadius = 0.5
			;[End Block]
		Case NPCtype457
		    ;[Block]
			n\NVName = "SCP-457"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider,HIT_PLAYER
			
            If n\obj = 0 Then
			    n\obj = CopyEntity(o\NPCModelID[1])
			
			    temp# = (GetINIFloat("Data\NPCs.ini", "SCP-457", "scale") / 2.2)		
			    ScaleEntity n\obj, temp, temp, temp
			EndIf
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-457", "speed") / 150.0)
			
			tex = LoadTexture_Strict(NPCsPath$+"scp_457.png")
			EntityAlpha n\obj, 0.25
			EntityTexture n\obj, tex
			FreeTexture tex
						
			n\Sound = LoadSound_Strict(SFXPath$+"SCP\457\Sighting.ogg")
            n\CollRadius = 0.32
	        ;[End Block]
	    Case NPCtype049_3
            ;[Block]
            n\NVName = "SCP-049-3"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			
			If n\obj = 0 Then 
				n\obj = CopyEntity(o\NPCModelID[23]) 
				
				MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
				
				temp# = (GetINIFloat("Data\NPCs.ini", "SCP-049-3", "scale") / 20.6)
				ScaleEntity n\obj, temp, temp, temp	
			EndIf
						
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-049-3", "speed") / 100.0)
			
			SetAnimTime(n\obj, 3.0)
			
			n\Sound = LoadSound_Strict(SFXPath$+"SCP\049_2\Breath.ogg")
			
			n\HP = 100
			;[End Block]
		Case NPCtype178_1
			;[Block]
			n\NVName = "Unidentified"
			
			n\Collider = CreatePivot()
			EntityRadius n\Collider,0.2
			
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2) Then
					n\obj = CopyEntity (n2\obj)
					Exit
				EndIf
			Next
			
			If n\obj = 0 Then 
			    n\obj = CopyEntity(o\NPCModelID[24])
			
			    temp# = Rnd(0.09,0.125)
			
			    EntityFX n\obj,1
			
			    temp# = Rnd(0.47, 0.67) / MeshWidth(n\obj)
			    ScaleEntity n\obj, temp, temp, temp
			EndIf
			
			SetAnimTime n\obj, 15.0
			
			EntityType n\Collider, HIT_178
			
			n\Speed = 0.02
			;[End Block]
		Case NPCtypeMTF2
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2) Then
					n\obj = CopyEntity (n2\obj)
					Exit
				EndIf
			Next

			If n\obj = 0 Then
			    n\obj = CopyEntity(o\NPCModelID[25])

			    temp# = (GetINIFloat("Data\NPCs.ini", "MTF2", "scale") / 2.5)
			
			    ScaleEntity n\obj, temp, temp, temp
			
			    MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2) 
			EndIf
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "MTF2", "speed") / 100.0)
			
			If MTF2SFX(0) = 0
			    MTF2SFX(0) = LoadSound_Strict(SFXPath$+"Character\MTF2\Stop1.ogg")
				MTF2SFX(1) = LoadSound_Strict(SFXPath$+"Character\MTF2\Stop2.ogg")
				MTF2SFX(2) = LoadSound_Strict(SFXPath$+"Character\MTF2\Stop3.ogg")			
				MTF2SFX(3) = LoadSound_Strict(SFXPath$+"Character\MTF2\ClassD2.ogg")
				MTF2SFX(4) = LoadSound_Strict(SFXPath$+"Character\MTF2\ClassD1.ogg")
				MTF2SFX(5) = LoadSound_Strict(SFXPath$+"Character\MTF2\Beep.ogg")
			EndIf

			If MTF2rooms[6] = Null Then 
				For r.Rooms = Each Rooms
					Select Lower(r\RoomTemplate\Name)
						Case "room895"
							MTF2rooms[0] = r
						Case "room005"
							MTF2rooms[1] = r	
						Case "room1office"
							MTF2rooms[2] = r	
						Case "room2poffices"
							MTF2rooms[3] = r	
						Case "room914"
							MTF2rooms[4] = r	
						Case "room650"
							MTF2rooms[5] = r	
						Case "room173"
							MTF2rooms[6] = r
					End Select
				Next			
			EndIf
			
			;[End Block]
		Case NPCtypeCI
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.32
			EntityType n\Collider, HIT_PLAYER
			
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2) Then
					n\obj = CopyEntity (n2\obj)
					Exit
				EndIf
			Next

            If n\obj = 0 Then
			    n\obj = CopyEntity(o\NPCModelID[31])
			
			    temp# = 0.55 / MeshWidth(n\obj)
			    ScaleEntity n\obj, temp, temp, temp
			
			    MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			EndIf

		    n\Speed = 2.0 / 100
		
		    n\CollRadius = 0.32
			;[End Block]
		Case NPCtypeVehicle
			;[Block]
			n\NVName = "Vehicle"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.32
			EntityType n\Collider, HIT_PLAYER
		
            If n\obj = 0 Then
			    n\obj = CopyEntity(o\NPCModelID[39])
			
			    temp# = 1.86 / MeshWidth(n\obj)
			    ScaleEntity n\obj, temp, temp, temp
			
			    MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			EndIf

		    n\Speed = 2.0 / 100
		
		    n\CollRadius = 0.72
			;[End Block]
			
        ;{~--<END>--~}

	End Select
	
	PositionEntity(n\Collider, x, y, z, True)
	PositionEntity(n\obj, x, y, z, True)
	
	ResetEntity(n\Collider)
	
	n\ID = 0
	n\ID = FindFreeNPCID()
	
	NPCSpeedChange(n)
	
	Return n
End Function

Function RemoveNPC(n.NPCs)
	
	If n=Null Then Return
	
	If n\obj2 <> 0 Then 
		FreeEntity n\obj2
		n\obj2 = 0
	EndIf
	If n\obj3 <> 0 Then 
		FreeEntity n\obj3
		n\obj3 = 0
	EndIf
	If n\obj4 <> 0 Then 
		FreeEntity n\obj4
		n\obj4 = 0
	EndIf
	
	If (Not n\SoundCHN_IsStream)
		If (n\SoundCHN <> 0 And ChannelPlaying(n\SoundCHN)) Then
			StopChannel(n\SoundCHN)
		EndIf
	Else
		If (n\SoundCHN <> 0)
			StopStream_Strict(n\SoundCHN)
		EndIf
	EndIf
	
	If (Not n\SoundCHN2_IsStream)
		If (n\SoundCHN2 <> 0 And ChannelPlaying(n\SoundCHN2)) Then
			StopChannel(n\SoundCHN2)
		EndIf
	Else
		If (n\SoundCHN2 <> 0)
			StopStream_Strict(n\SoundCHN2)
		EndIf
	EndIf
	
	If n\Sound<>0 Then FreeSound_Strict n\Sound
	If n\Sound2<>0 Then FreeSound_Strict n\Sound2
	
	FreeEntity(n\obj) : n\obj = 0
	FreeEntity(n\Collider) : n\Collider = 0	
	
	Delete n
End Function

Function UpdateNPCs()
	Local n.NPCs, n2.NPCs, d.Doors, de.Decals, r.Rooms, eo.ElevatorObj, eo2.ElevatorObj
	Local i%, dist#, dist2#, angle#, x#, y#, z#, prevFrame#, PlayerSeeAble%, RN$
	Local fs.FPS_Settings = First FPS_Settings
	Local target%
	Local dist_to_player#
	
	For n.NPCs = Each NPCs
		;A variable to determine if the NPC is in the facility or not
		n\InFacility = CheckForNPCInFacility(n)
		
		Select n\NPCtype
			Case NPCtype173
				;[Block]
				
				If Curr173\Idle <> 3 Then
					dist# = EntityDistance(n\Collider, Collider)		
					
					n\State3 = 1
					
					If n\Idle < 2 Then
						If n\IdleTimer > 0.1
							n\Idle = 1
							n\IdleTimer = Max(n\IdleTimer-fs\FPSfactor[0],0.1)
						ElseIf n\IdleTimer = 0.1
							n\Idle = 0
							n\IdleTimer = 0
						EndIf
						
						PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
						RotateEntity (n\obj, 0, EntityYaw(n\Collider)-180, 0)
						
						If n\Idle = False Then  
							Local temp% = False
							Local move% = True
							If dist < 15 Then
								If dist < 10.0 Then 
									If EntityVisible(n\Collider, Collider) Then
										temp = True
										n\EnemyX = EntityX(Collider, True)
										n\EnemyY = EntityY(Collider, True)
										n\EnemyZ = EntityZ(Collider, True)
									EndIf
								EndIf										
								
								Local SoundVol# = Max(Min((Distance(EntityX(n\Collider), EntityZ(n\Collider), n\PrevX, n\PrevZ) * 2.5), 1.0), 0.0)
								n\SoundCHN = LoopSound2(StoneDragSFX, n\SoundCHN, Camera, n\Collider, 10.0, n\State)
								
								n\PrevX = EntityX(n\Collider)
								n\PrevZ = EntityZ(n\Collider)				
								
								If (BlinkTimer < - 16 Or BlinkTimer > - 6) And (IsNVGBlinking=False) Then
									If EntityInView(n\obj, Camera) Then move = False
								EndIf
							EndIf
							
							If chs\NoTarget Then move = True
							
							;player is looking at it -> doesn't move
							If move = False Then
								BlurVolume = Max(Max(Min((4.0 - dist) / 6.0, 0.9), 0.1), BlurVolume)
								CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs2())/20.0)+1.0)*15.0*Max((3.5-dist)/3.5,0.0))								
								
								If dist < 3.5 And MilliSecs2() - n\LastSeen > 60000 And temp Then
									PlaySound_Strict(HorrorSFX(Rand(3,4)))
									
									n\LastSeen = MilliSecs2()
								EndIf
								
								If dist < 1.5 And Rand(700) = 1 Then PlaySound2(Scp173SFX(Rand(0, 2)), Camera, n\obj)
								
								If dist < 1.5 And n\LastDist > 2.0 And temp Then
									CurrCameraZoom = 40.0
									HeartBeatRate = Max(HeartBeatRate, 140)
									HeartBeatVolume = 0.5
									
									Select Rand(9)
										Case 1
											PlaySound_Strict(HorrorSFX(1))
										Case 2
											PlaySound_Strict(HorrorSFX(2))
										Case 3
											PlaySound_Strict(HorrorSFX(9))
										Case 4
											PlaySound_Strict(HorrorSFX(10))
										Case 5
											PlaySound_Strict(HorrorSFX(14))
										Case 6
										    PlaySound_Strict(HorrorSFX(17))
										Case 7
										    PlaySound_Strict(HorrorSFX(18))
										Case 8
										    PlaySound_Strict(HorrorSFX(19))
										Case 9
										    PlaySound_Strict(HorrorSFX(20))
									End Select
								EndIf									
									
								n\LastDist = dist
								
								n\State = Max(0, n\State - fs\FPSfactor[0] / 20)
							Else 
								;more than 6 room lengths away from the player -> teleport to a room closer to the player
								If dist > 50 Then
									If Rand(70)=1 Then
										If PlayerRoom\RoomTemplate\Name <> "gateb" And PlayerRoom\RoomTemplate\Name <> "gatea" And PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then
											For w.waypoints = Each WayPoints
												If w\door=Null And Rand(5)=1 Then
													x = Abs(EntityX(Collider)-EntityX(w\obj,True))
													If x < 25.0 And x > 15.0 Then
														z = Abs(EntityZ(Collider)-EntityZ(w\obj,True))
														If z < 25 And z > 15.0 Then
															PositionEntity n\Collider, EntityX(w\obj,True), EntityY(w\obj,True)+0.25,EntityZ(w\obj,True)
															ResetEntity n\Collider
															Exit
														EndIf
													EndIf
														
												EndIf
											Next
										EndIf
									EndIf
								ElseIf dist > HideDistance*0.8 ;3-6 rooms away from the player -> move randomly from waypoint to another
									If Rand(70)=1 Then TeleportCloser(n)
								Else ;less than 3 rooms away -> actively move towards the player
									n\State = CurveValue(SoundVol, n\State, 3)
									
									;try to open doors
									If Rand(20) = 1 Then
										For d.Doors = Each Doors
											If (Not d\locked) And d\open = False And d\Code = "" And d\KeyCard=0 Then
												For i% = 0 To 1
													If d\buttons[i] <> 0 Then
														If Abs(EntityX(n\Collider) - EntityX(d\buttons[i])) < 0.5 Then
															If Abs(EntityZ(n\Collider) - EntityZ(d\buttons[i])) < 0.5 Then
																If (d\openstate >= 180 Or d\openstate <= 0) Then
																	pvt = CreatePivot()
																	PositionEntity pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.5, EntityZ(n\Collider)
																	PointEntity pvt, d\buttons[i]
																	MoveEntity pvt, 0, 0, n\Speed * 0.6
																	
																	If EntityPick(pvt, 0.5) = d\buttons[i] Then 
																		PlaySound_Strict (LoadTempSound(SFXPath$+"Door\DoorOpen173.ogg"))
																		UseDoor(d, False)
																	EndIf
																	
																	FreeEntity pvt
																EndIf
															EndIf
														EndIf
													EndIf
												Next
											EndIf
										Next
									EndIf
									
									If chs\NoTarget
										temp = False
										n\EnemyX = 0
										n\EnemyY = 0
										n\EnemyZ = 0
									EndIf
									
									;player is not looking and is visible from 173's position -> attack
									If temp Then 				
										If dist < 0.65 Then
											If KillTimer >= 0 And (Not chs\GodMode) Then
												
												Select PlayerRoom\RoomTemplate\Name
													Case "lockroom", "room2closets", "room895"
														DeathMSG = SubjectName$+". Cause of death: Fatal cervical fracture. The surveillance tapes confirm that the subject was killed by SCP-173."	
													Case "room173intro"
														DeathMSG = SubjectName$+". Cause of death: Fatal cervical fracture. According to Security Chief Franklin who was present at SCP-173's containment "
														DeathMSG = DeathMSG + "chamber during the breach, the subject was killed by SCP-173 as soon as the disruptions in the electrical network started."
													Case "room2doors"
														DeathMSG = Chr(34)+"If I'm not mistaken, one of the main purposes of these rooms was to stop SCP-173 from moving further in the event of a containment breach. "
														DeathMSG = DeathMSG + "So, who's brilliant idea was it to put A GODDAMN MAN-SIZED VENTILATION DUCT in there?"+Chr(34)
													Default 
														DeathMSG = SubjectName$+". Cause of death: Fatal cervical fracture. Assumed to be attacked by SCP-173."
												End Select
												
												If (Not chs\GodMode) Then n\Idle = True
												PlaySound_Strict(NeckSnapSFX(Rand(0,2)))
												If Rand(2) = 1 Then 
													TurnEntity(Camera, 0, Rand(80,100), 0)
												Else
													TurnEntity(Camera, 0, Rand(-100,-80), 0)
												EndIf
												Kill()
											EndIf
										Else
											PointEntity(n\Collider, Collider)
											RotateEntity n\Collider, 0, EntityYaw(n\Collider), EntityRoll(n\Collider)
											;MoveEntity(n\Collider, 0, 0, n\Speed * fs\FPSfactor[0])
											TranslateEntity n\Collider,Cos(EntityYaw(n\Collider)+90.0)*n\Speed*fs\FPSfactor[0],0.0,Sin(EntityYaw(n\Collider)+90.0)*n\Speed*fs\FPSfactor[0]
										EndIf
									Else ;player is not visible -> move to the location where he was last seen							
										If n\EnemyX <> 0 Then						
											If Distance(EntityX(n\Collider), EntityZ(n\Collider), n\EnemyX, n\EnemyZ) > 0.5 Then
												AlignToVector(n\Collider, n\EnemyX-EntityX(n\Collider), 0, n\EnemyZ-EntityZ(n\Collider), 3)
												MoveEntity(n\Collider, 0, 0, n\Speed * fs\FPSfactor[0])
												If Rand(500) = 1 Then n\EnemyX = 0 : n\EnemyY = 0 : n\EnemyZ = 0
											Else
												n\EnemyX = 0 : n\EnemyY = 0 : n\EnemyZ = 0
											End If
										Else
											If Rand(400)=1 Then RotateEntity (n\Collider, 0, Rnd(360), 10)
											TranslateEntity n\Collider,Cos(EntityYaw(n\Collider)+90.0)*n\Speed*fs\FPSfactor[0],0.0,Sin(EntityYaw(n\Collider)+90.0)*n\Speed*fs\FPSfactor[0]
											
										End If
									EndIf
									
								EndIf ; less than 2 rooms away from the player
								
							EndIf
							
						EndIf ;idle = false
						
						PositionEntity(n\Collider, EntityX(n\Collider), Min(EntityY(n\Collider),0.35), EntityZ(n\Collider))
						
					Else ;idle = 2
						
						If n\Target <> Null Then
							Local tmp = False
							If dist > HideDistance*0.7
								If EntityVisible(n\obj,Collider)=False
									tmp = True
								EndIf
							EndIf
							If (Not tmp)
								PointEntity n\obj, n\Target\Collider
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),10.0), 0, True								
								dist = EntityDistance(n\Collider, n\Target\Collider)
								;MoveEntity n\Collider, 0, 0, 0.008*fs\FPSfactor[0]*Max(Min((dist*2-1.0)*0.5,1.0),-0.5)
								MoveEntity n\Collider, 0, 0, 0.016*fs\FPSfactor[0]*Max(Min((dist*2-1.0)*0.5,1.0),-0.5)
								n\GravityMult = 1.0
							Else
								PositionEntity n\Collider,EntityX(n\Target\Collider),EntityY(n\Target\Collider)+0.3,EntityZ(n\Target\Collider)
								ResetEntity n\Collider
								n\DropSpeed = 0
								n\GravityMult = 0.0
							EndIf
						EndIf
						
						PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) + 0.05 + Sin(MilliSecs2()*0.08)*0.02, EntityZ(n\Collider))
						RotateEntity (n\obj, 0, EntityYaw(n\Collider)-180, 0)
						
						ShowEntity n\obj2
						
						PositionEntity(n\obj2, EntityX(n\Collider), EntityY(n\Collider) - 0.05 + Sin(MilliSecs2()*0.08)*0.02, EntityZ(n\Collider))
						RotateEntity (n\obj2, 0, EntityYaw(n\Collider)-180, 0)
					EndIf
				EndIf
				
				;[End block]
			Case NPCtype106
				;[Block]
				If Contained106 Then
					n\Idle = True
					HideEntity n\obj
					HideEntity n\obj2
					PositionEntity n\obj, 0,500.0,0, True
				Else
					
					dist = EntityDistance(n\Collider, Collider)
					
					Local spawn106% = True
					;checking if 106 is allowed to spawn
					If PlayerRoom\RoomTemplate\Name$ = "dimension1499" Then spawn106% = False
					For e.Events = Each Events
						If e\EventName = "room860"
							If e\EventState = 1
								spawn106% = False
							EndIf
							Exit
						EndIf
					Next
					If PlayerRoom\RoomTemplate\Name$ = "room049" And EntityY(Collider) <= -2848*RoomScale Then
						spawn106% = False
					EndIf
					;GateA event has been triggered - don't make 106 disapper!
					;The reason why this is a seperate For loop is because we need to make sure that room860 would not be able to overwrite the "spawn106%" variable
					For e.events = Each Events
						If e\EventName = "gatea"
							If e\EventState <> 0
								spawn106% = True
								If PlayerRoom\RoomTemplate\Name$ = "dimension1499" Then
									n\Idle = True
								Else
									n\Idle = False
								EndIf
							EndIf
							Exit
						EndIf
					Next
					If (Not spawn106%) And n\State <= 0 Then
						n\State = Rand(22000, 27000)
						PositionEntity n\Collider,0,500,0
					EndIf
					
					If (Not n\Idle) And spawn106%
						If n\State <= 0 Then	;attacking	
							If EntityY(n\Collider) < EntityY(Collider) - 20.0 - 0.55 Then
								If Not PlayerRoom\RoomTemplate\DisableDecals Then
									de.Decals = CreateDecal(0, EntityX(Collider), 0.01, EntityZ(Collider), 90, Rand(360), 0)
									de\Size = 0.05 : de\SizeChange = 0.001 : EntityAlpha(de\obj, 0.8) : UpdateDecals
								EndIf
								
								n\PrevY = EntityY(Collider)
								
								SetAnimTime n\obj, 110
								
								If PlayerRoom\RoomTemplate\Name <> "room895"
									PositionEntity(n\Collider, EntityX(Collider), EntityY(Collider) - 15, EntityZ(Collider))
								EndIf
								
								PlaySound_Strict(DecaySFX(0))
							End If
							
							If Rand(500) = 1 Then PlaySound2(OldManSFX(Rand(0, 2)), Camera, n\Collider)
							n\SoundCHN = LoopSound2(OldManSFX(4), n\SoundCHN, Camera, n\Collider, 8.0, 0.8)
							
							If n\State > - 10 Then
								ShouldPlay = 66
								If n\Frame < 259 Then
									PositionEntity n\Collider, EntityX(n\Collider), n\PrevY-0.15, EntityZ(n\Collider)
									PointEntity n\obj, Collider
									RotateEntity (n\Collider, 0, CurveValue(EntityYaw(n\obj),EntityYaw(n\Collider),100.0), 0, True)
									
									AnimateNPC(n, 110, 259, 0.15, False)
								Else
									n\State = -10
								EndIf
							Else
								If PlayerRoom\RoomTemplate\Name <> "gatea" Then ShouldPlay = 10
								
								Local Visible% = False
								If dist < 8.0 Then
									Visible% = EntityVisible(n\Collider, Collider)
								EndIf
								
								If chs\NoTarget Then Visible = False
								
								If Visible Then
									If PlayerRoom\RoomTemplate\Name <> "gatea" Then n\PathTimer = 0
									If EntityInView(n\Collider, Camera) Then
										GiveAchievement(Achv106)
										
										;Achievements(Achv106) = True
										
										BlurVolume = Max(Max(Min((4.0 - dist) / 6.0, 0.9), 0.1), BlurVolume)
										CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs2())/20.0)+1.0) * 20.0 * Max((4.0-dist)/4.0,0))
										
										If MilliSecs2() - n\LastSeen > 60000 Then 
											CurrCameraZoom = 40
											PlaySound_Strict(HorrorSFX(6))
											n\LastSeen = MilliSecs2()
										EndIf
									EndIf
								Else
									n\State=n\State-fs\FPSfactor[0]
								End If
								
								If dist > 0.8 Then
									If (dist > 25.0 Or PlayerRoom\RoomTemplate\Name = "pocketdimension" Or Visible Or n\PathStatus <> 1) And PlayerRoom\RoomTemplate\Name <> "gatea" Then 
										
										If (dist > 40 Or PlayerRoom\RoomTemplate\Name = "pocketdimension") Then
											TranslateEntity n\Collider, 0, ((EntityY(Collider) - 0.14) - EntityY(n\Collider)) / 50.0, 0
										EndIf
										
										n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
										
										PointEntity n\obj, Collider
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0
										
										If KillTimer >= 0 Then
											prevFrame# = n\Frame
											AnimateNPC(n, 284, 333, n\CurrSpeed*43)
											;Animate2(n\obj, AnimTime(n\obj), 284, 333, n\CurrSpeed*43)
											
											If prevFrame =< 286 And n\Frame>286 Then
												PlaySound2(Step2SFX(Rand(0,2)),Camera, n\Collider, 6.0, Rnd(0.8,1.0))
											ElseIf prevFrame=<311 And n\Frame>311.0 
												PlaySound2(Step2SFX(Rand(0,2)),Camera, n\Collider, 6.0, Rnd(0.8,1.0))
											EndIf
										Else 
											n\CurrSpeed = 0
										EndIf
										
										n\PathTimer = Max(n\PathTimer-fs\FPSfactor[0],0)
										If n\PathTimer =< 0 Then
											n\PathStatus = FindPath (n, EntityX(Collider,True), EntityY(Collider,True), EntityZ(Collider,True))
											n\PathTimer = 70*10
										EndIf
									Else 
										If n\PathTimer <= 0 Then
											n\PathStatus = FindPath (n, EntityX(Collider,True), EntityY(Collider,True), EntityZ(Collider,True))
											n\PathTimer = 70*10
											n\CurrSpeed = 0
										Else
											n\PathTimer = Max(n\PathTimer-fs\FPSfactor[0],0)
											
											If n\PathStatus = 2 Then
												n\CurrSpeed = 0
											ElseIf n\PathStatus = 1
												While n\Path[n\PathLocation]=Null
													If n\PathLocation > 19 Then 
														n\PathLocation = 0 : n\PathStatus = 0
														Exit
													Else
														n\PathLocation = n\PathLocation + 1
													EndIf
												Wend
												
												If n\Path[n\PathLocation]<>Null Then 
													TranslateEntity n\Collider, 0, ((EntityY(n\Path[n\PathLocation]\obj,True) - 0.11) - EntityY(n\Collider)) / 50.0, 0
													
													PointEntity n\obj, n\Path[n\PathLocation]\obj
													
													dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
													
													RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), Min(20.0,dist2*10.0)), 0
													n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
													
													prevFrame# = AnimTime(n\obj)
													AnimateNPC(n, 284, 333, n\CurrSpeed*43)
													;Animate2(n\obj, AnimTime(n\obj), 284, 333, n\CurrSpeed*43)
													If prevFrame =< 286 And n\Frame>286 Then
														PlaySound2(Step2SFX(Rand(0,2)),Camera, n\Collider, 6.0, Rnd(0.8,1.0))	
													ElseIf prevFrame=<311 And n\Frame>311.0 
														PlaySound2(Step2SFX(Rand(0,2)),Camera, n\Collider, 6.0, Rnd(0.8,1.0))
													EndIf
													
													If dist2 < 0.2 Then n\PathLocation = n\PathLocation + 1
												EndIf
											ElseIf n\PathStatus = 0
												If n\State3=0 Then AnimateNPC(n, 334, 494, 0.3)
												;Animate2(n\obj, AnimTime(n\obj), 334, 494, 0.3)
												n\CurrSpeed = CurveValue(0,n\CurrSpeed,10.0)
											EndIf
										EndIf
										
									EndIf
									
								ElseIf PlayerRoom\RoomTemplate\Name <> "gatea" And (Not chs\NoTarget) ;dist < 0.8
									
									If dist > 0.5 Then 
										n\CurrSpeed = CurveValue(n\Speed * 2.5,n\CurrSpeed,10.0)
									Else
										n\CurrSpeed = 0
									EndIf
									AnimateNPC(n, 105, 110, 0.15, False)
									;Animate2(n\obj, AnimTime(n\obj), 105, 110, 0.15, False)
									;If Floor(AnimTime(n\obj)) = 43 Then SetAnimTime(n\obj, 43)
									
									If KillTimer >= 0 And FallTimer >= 0 Then
										PointEntity n\obj, Collider
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0										
										
										If Ceil(n\Frame) = 110 And (Not chs\GodMode) Then
											PlaySound_Strict(DamageSFX(1))
											PlaySound_Strict(HorrorSFX(5))											
											If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
												DeathMSG = SubjectName$+". Body partially decomposed by what is assumed to be SCP-106's "+Chr(34)+"corrosion"+Chr(34)+" effect. Body disposed of via incineration."
												Kill()
											Else
											    If Rand(2) = 1 Then
												    PlaySound_Strict(OldManSFX(3))
												Else
                                                    PlaySound_Strict(OldManSFX(9))
                                                EndIf
												FallTimer = Min(-1, FallTimer)
												PositionEntity(Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
												ResetEntity (Head)
												RotateEntity(Head, 0, EntityYaw(Camera) + Rand(-45, 45), 0)
											EndIf
										EndIf
									EndIf
									
								EndIf
								
							EndIf 
							
							MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
							
							If n\State <= Rand(-3500, -3000) Then 
								If Not EntityInView(n\obj,Camera) And dist > 5 Then
									n\State = Rand(22000, 27000)
									PositionEntity n\Collider,0,500,0
								EndIf
							EndIf
							
							If FallTimer < -250.0 Then
								MoveToPocketDimension()
								n\State = 250 ;make 106 idle for a while
							EndIf
							
							If n\Reload = 0 Then
                                If dist > 10 And PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "gatea" And n\State <-5 Then ;timer idea by Juanjpro
                                    If (Not EntityInView(n\obj,Camera))
                                        TurnEntity Collider,0,180,0
                                        pick = EntityPick(Collider,5)
                                        TurnEntity Collider,0,180,0
                                        If pick<>0
											TeleportEntity(n\Collider,PickedX(),PickedY(),PickedZ(),n\CollRadius)
                                            PointEntity(n\Collider,Collider)
                                            RotateEntity(n\Collider, 0, EntityYaw(n\Collider),0)
                                            MoveEntity(n\Collider,0,0,-2)
                                            If Rand(2) = 1 Then
                                                PlaySound2(OldManSFX(3),Camera,n\Collider)
                                            Else
                                                PlaySound2(OldManSFX(9),Camera,n\Collider)
                                            EndIf
											n\SoundCHN2 = PlaySound2(OldManSFX(6+Rand(0,2)),Camera,n\Collider)
                                            n\PathTimer = 0
                                            n\Reload = (70*10.0)/(SelectedDifficulty\otherFactors+1)
                                        EndIf
                                    EndIf
                                EndIf
                            EndIf
                            n\Reload = Max(0, n\Reload - fs\FPSfactor[0])
							
							UpdateSoundOrigin(n\SoundCHN2,Camera,n\Collider)
						Else ;idling outside the map
							n\CurrSpeed = 0
							MoveEntity n\Collider, 0, ((EntityY(Collider) - 30) - EntityY(n\Collider)) / 200.0, 0
							n\DropSpeed = 0
							n\Frame = 110
							;SetAnimTime n\obj, 110.0
							
							If (Not PlayerRoom\RoomTemplate\DisableDecals) Then
								If PlayerRoom\RoomTemplate\Name <> "gatea"
									If (SelectedDifficulty\aggressiveNPCs) Then
										n\State=n\State-fs\FPSfactor[0]*2
									Else
										n\State=n\State-fs\FPSfactor[0]
									EndIf
								EndIf
							EndIf
						End If
						
						ResetEntity(n\Collider)
						n\DropSpeed = 0
						PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.15, EntityZ(n\Collider))
						
						RotateEntity n\obj, 0, EntityYaw(n\Collider), 0
						
						PositionEntity(n\obj2, EntityX(n\obj), EntityY(n\obj) , EntityZ(n\obj))
						RotateEntity(n\obj2, 0, EntityYaw(n\Collider) - 180, 0)
						MoveEntity(n\obj2, 0, 8.6 * 0.11, -1.5 * 0.11)
						
						If PlayerRoom\RoomTemplate\Name = "pocketdimension" Or PlayerRoom\RoomTemplate\Name = "gatea" Then
							HideEntity n\obj2
						Else
							If dist < CameraFogFar*LightVolume*0.6 Then
								HideEntity n\obj2
							Else
								ShowEntity n\obj2
								EntityAlpha (n\obj2, Min(dist-CameraFogFar*LightVolume*0.6,1.0))
							EndIf
						EndIf						
					Else
						HideEntity n\obj2
					EndIf
					
				EndIf
				
				;[End Block]
			Case NPCtype096
				;[Block]
				dist = EntityDistance(Collider, n\Collider)
				
				Select n\State
					Case 0
						;[Block]
						If dist<8.0 Then
							GiveAchievement(Achv096)
							;If n\Sound = 0 Then
							;	n\Sound = LoadSound_Strict(MusicPath$+"096.ogg")
							;Else
							;	n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider, 8.0, 1.0)
							;EndIf
							If n\SoundCHN = 0
								n\SoundCHN = StreamSound_Strict(MusicPath$+"096.ogg",0)
								n\SoundCHN_IsStream = True
							Else
								UpdateStreamSoundOrigin(n\SoundCHN,Camera,n\Collider,8.0,1.0)
							EndIf
							
							If n\State3 = -1
								AnimateNPC(n,936,1263,0.1,False)
								If n\Frame=>1262.9
									n\State = 5
									n\State3 = 0
									n\Frame = 312
								EndIf
							Else
								AnimateNPC(n,936,1263,0.1)
								If n\State3 < 70*6
									n\State3=n\State3+fs\FPSfactor[0]
								Else
									If Rand(1,5)=1
										n\State3 = -1
									Else
										n\State3=70*(Rand(0,3))
									EndIf
								EndIf
							EndIf
							;AnimateNPC(n, 1085,1412, 0.1) ;sitting
							
							angle = WrapAngle(DeltaYaw(n\Collider, Collider));-EntityYaw(n\Collider,True))
							
							If (Not chs\NoTarget)
								If angle<90 Or angle>270 Then
									CameraProject Camera,EntityX(n\Collider), EntityY(n\Collider)+0.25, EntityZ(n\Collider)
									
									If ProjectedX()>0 And ProjectedX()<GraphicWidth Then
										If ProjectedY()>0 And ProjectedY()<GraphicHeight Then
											If EntityVisible(Collider, n\Collider) Then
												If (BlinkTimer < - 16 Or BlinkTimer > - 6)
													PlaySound_Strict LoadTempSound(SFXPath$+"SCP\096\Triggered.ogg")
													
													CurrCameraZoom = 10
													
													n\Frame = 194
													;n\Frame = 307
													StopStream_Strict(n\SoundCHN) : n\SoundCHN=0
													n\Sound = 0
													n\State = 1
													n\State3 = 0
												EndIf
											EndIf									
										EndIf
									EndIf								
									
								EndIf
							EndIf
						EndIf
						;[End Block]
					Case 4
						;[Block]
						CurrCameraZoom = CurveValue(Max(CurrCameraZoom, (Sin(Float(MilliSecs2())/20.0)+1.0) * 10.0),CurrCameraZoom,8.0)
						
						If n\Target = Null Then 
							If n\SoundCHN = 0
								n\SoundCHN = StreamSound_Strict(SFXPath$+"SCP\096\Scream.ogg",0)
								n\SoundCHN_IsStream = True
							Else
								UpdateStreamSoundOrigin(n\SoundCHN,Camera,n\Collider,7.5,1.0)
							EndIf
							
							If n\SoundCHN2 = 0
								n\SoundCHN2 = StreamSound_Strict(MusicPath$+"096Chase.ogg",0)
								n\SoundCHN2_IsStream = 2
							Else
								SetStreamVolume_Strict(n\SoundCHN2,Min(Max(8.0-dist,0.6),1.0)*SFXVolume#)
							EndIf
						EndIf
						
						If chs\NoTarget And n\Target = Null Then n\State = 5
						
						If KillTimer => 0 Then
							
							If MilliSecs2() > n\State3 Then
								n\LastSeen = 0
								If n\Target=Null Then
									If EntityVisible(Collider, n\Collider) Then n\LastSeen=1
								Else
									If EntityVisible(n\Target\Collider, n\Collider) Then n\LastSeen=1
								EndIf
								n\State3=MilliSecs2()+3000
							EndIf
							
							If n\LastSeen=1 Then
								n\PathTimer=Max(70*3, n\PathTimer)
								n\PathStatus=0
								
								If n\Target<> Null Then dist = EntityDistance(n\Target\Collider, n\Collider)
								
								If dist < 2.8 Or n\Frame<150 Then 
									If n\Frame>193 Then n\Frame = 2.0 ;go to the start of the jump animation
									
									AnimateNPC(n, 2, 193, 0.7)
									
									If dist > 1.0 Then 
										n\CurrSpeed = CurveValue(n\Speed*2.0,n\CurrSpeed,15.0)
									Else
										n\CurrSpeed = 0
										
										If n\Target=Null Then
											If (Not chs\GodMode) Then 
												PlaySound_Strict DamageSFX(4)
												
												pvt = CreatePivot()
												CameraShake = 30
												BlurTimer = 2000
												DeathMSG = "A large amount of blood found in [DATA REDACTED]. DNA indentified as "+SubjectName$+". Most likely [DATA REDACTED] by SCP-096."
												Kill(True)
												KillAnim = 1
												For i = 0 To 6
													PositionEntity pvt, EntityX(Collider)+Rnd(-0.1,0.1),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.1,0.1)
													TurnEntity pvt, 90, 0, 0
													EntityPick(pvt,0.3)
													
													de.Decals = CreateDecal(Rand(15,16), PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
													de\Size = Rnd(0.2,0.6) : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\Size, de\Size
												Next
												FreeEntity pvt
											EndIf
										EndIf				
									EndIf
									
									If n\Target=Null Then
										PointEntity n\Collider, Collider
									Else
										PointEntity n\Collider, n\Target\Collider
									EndIf
									
								Else
									If n\Target=Null Then 
										PointEntity n\obj, Collider
									Else
										PointEntity n\obj, n\Target\Collider
									EndIf
									
									RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 5.0), 0
									
									If n\Frame>847 Then n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
									
									If n\Frame<906 Then
										AnimateNPC(n,737,906,n\Speed*8,False)
									Else
										AnimateNPC(n,907,935,n\CurrSpeed*8)	
									EndIf
								EndIf
								
								RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0, True
								MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]
								
							Else
								If n\PathStatus = 1 Then
									
									If n\Path[n\PathLocation]=Null Then 
										If n\PathLocation > 19 Then 
											n\PathLocation = 0 : n\PathStatus = 0
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										PointEntity n\obj, n\Path[n\PathLocation]\obj
										
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 5.0), 0
										
										;1000
										If n\Frame>847 Then n\CurrSpeed = CurveValue(n\Speed*1.5,n\CurrSpeed,15.0)
										MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]
										
										If n\Frame<906 Then ;1058
											AnimateNPC(n,737,906,n\Speed*8,False)
											;AnimateNPC(n, 892,1058, n\Speed*8, False)
										Else
											AnimateNPC(n,907,935,n\CurrSpeed*8)
											;AnimateNPC(n, 1059,1084, n\CurrSpeed*8)	
										EndIf

										dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
										If dist2 < 0.8 Then ;0.4
											If n\Path[n\PathLocation]\door <> Null Then
												If n\Path[n\PathLocation]\door\open = False Then
													n\Path[n\PathLocation]\door\open = True
													n\Path[n\PathLocation]\door\fastopen = 1
													PlaySound2(OpenDoorFastSFX(Rnd(0, 1)), Camera, n\Path[n\PathLocation]\door\obj)
												EndIf
											EndIf							
											If dist2 < 0.7 Then n\PathLocation = n\PathLocation + 1 ;0.2
										EndIf 
									EndIf
									
								Else
									;AnimateNPC(n, 892,972, 0.2)
									AnimateNPC(n,1471,1556,0.1)
									
									n\PathTimer = Max(0, n\PathTimer-fs\FPSfactor[0])
									If n\PathTimer=<0 Then
										If n\Target<>Null Then
											n\PathStatus = FindPath(n, EntityX(n\Target\Collider),EntityY(n\Target\Collider)+0.2,EntityZ(n\Target\Collider))	
										Else
											n\PathStatus = FindPath(n, EntityX(Collider),EntityY(Collider)+0.2,EntityZ(Collider))	
										EndIf
										n\PathTimer = 70*5
									EndIf
								EndIf
							EndIf
							
							If dist > 32.0 Or EntityY(n\Collider)<-50 Then
								If Rand(50)=1 Then TeleportCloser(n)
							EndIf
						Else ;play the eating animation if killtimer < 0 
							AnimateNPC(n, Min(27,AnimTime(n\obj)), 193, 0.5)
							
							;Animate2(n\obj, AnimTime(n\obj), Min(27,AnimTime(n\obj)), 193, 0.5)
						EndIf
						
						If dist > 24.0 
							If Rand(50)=1 Then
								For w.waypoints = Each WayPoints
									If w\door=Null And Rand(5)=1 Then
										x = Abs(EntityX(n\Collider)-EntityX(w\obj,True))
										If x < 28.0 And x > 20.0 Then
											z = Abs(EntityZ(n\Collider)-EntityZ(w\obj,True))
											If z < 28 And z > 20.0 Then
												n\PathStatus = 0 : n\PathTimer = 0 : n\PathLocation = 0
												PositionEntity n\Collider, EntityX(w\obj,True), EntityY(w\obj,True)+0.25,EntityZ(w\obj,True)
												ResetEntity n\Collider
												Exit
											EndIf
										EndIf
									EndIf
								Next
							EndIf
						EndIf
						
						;[End Block]
					Case 1,2,3
						;[Block]
						If n\SoundCHN = 0
							n\SoundCHN = StreamSound_Strict(MusicPath$+"096Angered.ogg",0)
							n\SoundCHN_IsStream = True
						Else
							UpdateStreamSoundOrigin(n\SoundCHN,Camera,n\Collider,10.0,1.0)
						EndIf
						
						If n\State=1 Then ; get up
							If n\Frame<312
								AnimateNPC(n,193,311,0.3,False)
								If n\Frame > 310.9 Then n\State = 2 : n\Frame = 737
							ElseIf n\Frame>=312 And n\Frame<=422
								AnimateNPC(n,312,422,0.3,False)
								If n\Frame > 421.9 Then n\Frame = 677
							Else
								AnimateNPC(n,677,736,0.3,False)
								If n\Frame > 735.9 Then n\State = 2 : n\Frame = 737
							EndIf
						ElseIf n\State=2
							AnimateNPC(n,677,737,0.3,False)
							If n\Frame=>737 Then n\State=3 : n\State2=0
							;AnimateNPC(n, 833, 972, 0.3, False)
							;If n\Frame=>972 Then n\State = 3 : n\State2=0
						ElseIf n\State=3
							n\State2 = n\State2+fs\FPSfactor[0]
							If n\State2 > 70*26 Then
								AnimateNPC(n,823,847,n\Speed*5,False)
								;AnimateNPC(n, 973, 1001, 0.5, False)
								If n\Frame>846.9 Then ;1000.9 
									n\State = 4
									StopStream_Strict(n\SoundCHN) : n\SoundCHN=0
								EndIf
							Else
								AnimateNPC(n,1471,1556,0.4)
								;AnimateNPC(n, 892,978, 0.3)
							EndIf
						EndIf
						;[End Block]
					Case 5
						;[Block]
						If dist < 16.0 Then 
							
							If dist < 4.0 Then
								GiveAchievement(Achv096)
							EndIf
							
							If n\SoundCHN = 0
								n\SoundCHN = StreamSound_Strict(MusicPath$+"096.ogg",0)
								n\SoundCHN_IsStream = True
							Else
								UpdateStreamSoundOrigin(n\SoundCHN,Camera,n\Collider,14.0,1.0)
							EndIf
							
							If n\Frame>=422
								n\State2=n\State2+fs\FPSfactor[0]
								If n\State2>1000 Then ;walking around
									If n\State2>1600 Then n\State2=Rand(0,500) ;: n\Frame = 1457 ;1652
									
									;1652
									If n\Frame<1382 Then ;idle to walk
										n\CurrSpeed = CurveValue(n\Speed*0.1,n\CurrSpeed,5.0)
										AnimateNPC(n,1369,1382,n\CurrSpeed*45,False)
										;AnimateNPC(n, 1638,1652, n\CurrSpeed*45,False)
									Else
										n\CurrSpeed = CurveValue(n\Speed*0.1,n\CurrSpeed,5.0)
										AnimateNPC(n,1383,1456,n\CurrSpeed*45)
										;AnimateNPC(n, 1653,1724, n\CurrSpeed*45) ;walk
									EndIf
									
									If MilliSecs2() > n\State3 Then
										n\LastSeen=0
										If EntityVisible(Collider, n\Collider) Then 
											n\LastSeen=1
										Else
											HideEntity n\Collider
											EntityPick(n\Collider, 1.5)
											If PickedEntity() <> 0 Then
												n\Angle = EntityYaw(n\Collider)+Rnd(80,110)
											EndIf
											ShowEntity n\Collider
										EndIf
										n\State3=MilliSecs2()+3000
									EndIf
									
									If n\LastSeen Then 
										PointEntity n\obj, Collider
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),130.0),0
										If dist < 1.5 Then n\State2=0
									Else
										RotateEntity n\Collider, 0, CurveAngle(n\Angle,EntityYaw(n\Collider),50.0),0
									EndIf
								Else
									;1638
									If n\Frame>472 Then ;walk to idle
										n\CurrSpeed = CurveValue(n\Speed*0.05,n\CurrSpeed,8.0)
										AnimateNPC(n,1383,1469,n\CurrSpeed*45,False)
										If n\Frame=>1468.9 Then n\Frame=423
										;AnimateNPC(n, 1652, 1638, -n\CurrSpeed*45,False)
									Else ;idle
										n\CurrSpeed = CurveValue(0,n\CurrSpeed,4.0)	
										AnimateNPC(n,423,471,0.2)
										;AnimateNPC(n, 585, 633, 0.2) ;idle
									EndIf
								EndIf
								
								MoveEntity n\Collider,0,0,n\CurrSpeed*fs\FPSfactor[0]
							Else
								AnimateNPC(n,312,422,0.3,False)
							EndIf
							
							angle = WrapAngle(DeltaYaw(n\Collider, Camera));-EntityYaw(n\Collider))
							If (Not chs\NoTarget)
								If angle<55 Or angle>360-55 Then
									CameraProject Camera,EntityX(n\Collider), EntityY(Collider)+5.8*0.2-0.25, EntityZ(n\Collider)
									
									If ProjectedX()>0 And ProjectedX()<GraphicWidth Then
										If ProjectedY()>0 And ProjectedY()<GraphicHeight Then
											If EntityVisible(Collider, n\Collider) Then
												If (BlinkTimer < - 16 Or BlinkTimer > - 6)
													PlaySound_Strict LoadTempSound(SFXPath$+"SCP\096\Triggered.ogg")
													
													CurrCameraZoom = 10
													
													If n\Frame >= 422
														n\Frame = 677 ;833
													EndIf
													StopStream_Strict(n\SoundCHN) : n\SoundCHN=0
													n\Sound = 0
													n\State = 2
												EndIf
											EndIf									
										EndIf
									EndIf
									
								EndIf
							EndIf
						EndIf
						;[End Block]	    
				End Select
				
				;ResetEntity(n\Collider)
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider)-0.03, EntityZ(n\Collider)) ;-0.07
				
				RotateEntity n\obj, EntityPitch(n\Collider), EntityYaw(n\Collider), 0
				;[End Block]
			Case NPCtype049
				;[Block]
				;n\state = the "main state" of the NPC
				;n\state2 = attacks the player when the value is above 0.0
				;n\state3 = timer for updating the path again
				
				prevFrame# = n\Frame
				
				dist = EntityDistance(Collider, n\Collider)
				
				n\BlinkTimer# = 1.0
				
				If n\Idle > 0.1
					If PlayerRoom\RoomTemplate\Name$ <> "room049"
						n\Idle = Max(n\Idle-(1+SelectedDifficulty\aggressiveNPCs)*fs\FPSfactor[0],0.1)
					EndIf
					n\DropSpeed = 0
					If ChannelPlaying(n\SoundCHN) Then StopChannel(n\SoundCHN)
					If ChannelPlaying(n\SoundCHN2) Then StopChannel(n\SoundCHN2)
					PositionEntity n\Collider,0,-500,0
					PositionEntity n\obj,0,-500,0
				Else
					If n\Idle = 0.1 Then
						If PlayerInReachableRoom() Then
							For i = 0 To 3
								If PlayerRoom\Adjacent[i]<>Null Then
									For j = 0 To 3
										If PlayerRoom\Adjacent[i]\Adjacent[j]<>Null Then
											TeleportEntity(n\Collider,PlayerRoom\Adjacent[i]\Adjacent[j]\x,0.5,PlayerRoom\Adjacent[i]\Adjacent[j]\z,n\CollRadius,True)
											Exit
										EndIf
									Next
									Exit
								EndIf
							Next
							n\Idle = 0.0
						EndIf
					EndIf
					
					Select n\State
						Case 0 ;nothing (used for events)
						Case 1 ;looking around before getting active
							;[Block]
							If n\Frame=>538 Then
								AnimateNPC(n, 659, 538, -0.45, False)
								If n\Frame > 537.9 Then n\Frame = 37
								
								;Animate2(n\obj, AnimTime(n\obj), 659, 538, -0.45, False)
								;If AnimTime(n\obj)=538 Then SetAnimTime(n\obj, 37)
							Else
								AnimateNPC(n, 37, 269, 0.7, False)
								If n\Frame>268.9 Then n\State = 2
								
								;Animate2(n\obj, AnimTime(n\obj), 37, 269, 0.7, False)
								;If AnimTime(n\obj)=269 Then n\State = 2
							EndIf
							;[End Block]
						Case 2 ;being active
							;[Block]
							If (dist < HideDistance*2) And (Not n\Idle) And PlayerInReachableRoom(True) Then
								n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider)
								PlayerSeeAble% = MeNPCSeesPlayer(n)
								If PlayerSeeAble%=True Or n\State2>0 Then ;Player is visible for 049's sight - attacking
									GiveAchievement(Achv049)
									
									;Playing a sound after detecting the player
									If n\PrevState <= 1 And ChannelPlaying(n\SoundCHN2)=False
										If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2)
										n\Sound2 = LoadSound_Strict(SFXPath$+"SCP\049\Spotted"+Rand(1,7)+".ogg")
										n\SoundCHN2 = LoopSound2(n\Sound2,n\SoundCHN2,Camera,n\obj)
										n\PrevState = 2
									EndIf
									n\PathStatus = 0
									n\PathTimer# = 0.0
									n\PathLocation = 0
									If PlayerSeeAble%=True Then n\State2 = 70*2
									
									PointEntity n\obj,Collider
									RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),10.0),0
									
									If dist < 0.5 Then
										If WearingHazmat>0 Then
											BlurTimer = BlurTimer+fs\FPSfactor[0]*2.5
											If BlurTimer>250 And BlurTimer-fs\FPSfactor[0]*2.5 <= 250 And n\PrevState<>3 Then
												If n\SoundCHN2 <> 0 Then StopChannel(n\SoundCHN2)
												n\SoundCHN2 = PlaySound_Strict(LoadTempSound(SFXPath$+"SCP\049\TakeOffHazmat.ogg"))
												n\PrevState=3
											ElseIf BlurTimer => 500
												For i = 0 To MaxItemAmount-1
													If Inventory(i)<>Null Then
														If Instr(Inventory(i)\itemtemplate\tempname,"hazmatsuit") And WearingHazmat<3 Then
															If Inventory(i)\state2 < 3 Then
																Inventory(i)\state2 = Inventory(i)\state2 + 1
																BlurTimer = 260.0
																CameraShake = 2.0
															Else
																RemoveItem(Inventory(i))
																WearingHazmat = False
															EndIf
															Exit
														EndIf
													EndIf
												Next
											EndIf
										ElseIf I_714\Using Then
											BlurTimer = BlurTimer+fs\FPSfactor[0]*2.5
											If BlurTimer>250 And BlurTimer-fs\FPSfactor[0]*2.5 <= 250 And n\PrevState<>3 Then
												If n\SoundCHN2 <> 0 Then StopChannel(n\SoundCHN2)
												n\SoundCHN2 = PlaySound_Strict(LoadTempSound(SFXPath$+"SCP\049\714Equipped.ogg"))
												n\PrevState=3
											ElseIf BlurTimer => 500
												I_714\Using=False
											EndIf
										ElseIf I_1033RU\Using > 0 And I_1033RU\HP > 0 Then
										    BlurTimer = BlurTimer+fs\FPSfactor[0]*2.5
									        If BlurTimer > 500 And BlurTimer-fs\FPSfactor[0]*2.5 =< 500 Then
												If n\PrevState<>3 Then n\PrevState=3
												PlaySound_Strict DamageSFX1033RU(Rand(0, 3))
												BlurTimer = 1.0
												LightFlash = 0.2
											EndIf
										Else
											CurrCameraZoom = 20.0
											BlurTimer = 500.0
											
											If (Not chs\GodMode) Then
												If PlayerRoom\RoomTemplate\Name$ = "room049"
													DeathMSG = "Two (2) active instances of SCP-049-2 and one (1) instances of SCP-049-3 discovered in the tunnel outside SCP-049's containment chamber. Terminated by Nine-Tailed Fox."
													For e.events = Each Events
														If e\EventName = "room049" Then e\EventState=-1 : Exit
													Next
												Else
													DeathMSG = "An active instance of SCP-049-2 was discovered in [DATA REDACTED]. Terminated by Nine-Tailed Fox."
													Kill()
												EndIf
												PlaySound_Strict HorrorSFX(13)
												If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2)
												n\Sound2 = LoadSound_Strict(SFXPath$+"SCP\049\Kidnap"+Rand(1,2)+".ogg")
												n\SoundCHN2 = LoopSound2(n\Sound2,n\SoundCHN2,Camera,n\obj)
												n\State = 3
											EndIf										
										EndIf
									Else
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]	
										
										If n\PrevState = 3 Then n\PrevState = 2
										
										If dist < 3.0 Then
											AnimateNPC(n, Max(Min(AnimTime(n\obj),428.0),387), 463.0, n\CurrSpeed*38)
										Else
											If n\Frame>428.0 Then
												AnimateNPC(n, Min(AnimTime(n\obj),463.0), 498.0, n\CurrSpeed*38,False)
												If n\Frame>497.9 Then n\Frame = 358
											Else
												AnimateNPC(n, Max(Min(AnimTime(n\obj),358.0),346), 393.0, n\CurrSpeed*38)
											EndIf
										EndIf
									EndIf
								Else ;Finding a path to the player
									If n\PathStatus = 1 ;Path to player found
										While n\Path[n\PathLocation]=Null
											If n\PathLocation > 19
												n\PathLocation = 0 : n\PathStatus = 0 : Exit
											Else
												n\PathLocation = n\PathLocation + 1
											EndIf
										Wend
										If n\Path[n\PathLocation]<>Null Then
											;closes doors behind him
											If n\PathLocation>0 Then
												If n\Path[n\PathLocation-1] <> Null Then
													If n\Path[n\PathLocation-1]\door <> Null Then
														If (Not n\Path[n\PathLocation-1]\door\IsElevatorDoor) Then
															If EntityDistance(n\Path[n\PathLocation-1]\obj,n\Collider)>0.3 Then
																If (n\Path[n\PathLocation-1]\door\MTFClose) And (n\Path[n\PathLocation-1]\door\open) And (n\Path[n\PathLocation-1]\door\buttons[0]<>0 Or n\Path[n\PathLocation-1]\door\buttons[1]<>0) Then
																	UseDoor(n\Path[n\PathLocation-1]\door, False)
																EndIf
															EndIf
														EndIf
													EndIf
												EndIf
											EndIf
											
											n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
											PointEntity n\obj,n\Path[n\PathLocation]\obj
											RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),10.0),0
											MoveEntity n\Collider,0,0,n\CurrSpeed*fs\FPSfactor[0]
											
											;opens doors in front of him
											dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
											If dist2 < 0.6 Then
												temp = True
												If n\Path[n\PathLocation]\door <> Null Then
													If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
														If (n\Path[n\PathLocation]\door\locked Or n\Path[n\PathLocation]\door\KeyCard<>0 Or n\Path[n\PathLocation]\door\Code<>"") And (Not n\Path[n\PathLocation]\door\open) Then
															temp = False
														Else
															If n\Path[n\PathLocation]\door\open = False And (n\Path[n\PathLocation]\door\buttons[0]<>0 Or n\Path[n\PathLocation]\door\buttons[1]<>0) Then
																UseDoor(n\Path[n\PathLocation]\door, False)
															EndIf
														EndIf
													EndIf
												EndIf
												If dist2#<0.2 And temp
													n\PathLocation = n\PathLocation + 1
												ElseIf dist2#<0.5 And (Not temp)
													;Breaking up the path when the door in front of 049 cannot be operated by himself
													n\PathStatus = 0
													n\PathTimer# = 0.0
												EndIf
											EndIf
											
											AnimateNPC(n, Max(Min(AnimTime(n\obj),358.0),346), 393.0, n\CurrSpeed*38)
											
											;Playing a sound if he hears the player
											If n\PrevState = 0 And ChannelPlaying(n\SoundCHN2)=False
												If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2)
												If Rand(30)=1
													n\Sound2 = LoadSound_Strict(SFXPath$+"SCP\049\Searching7.ogg")
												Else
													n\Sound2 = LoadSound_Strict(SFXPath$+"SCP\049\Searching"+Rand(1,6)+".ogg")
												EndIf
												n\SoundCHN2 = LoopSound2(n\Sound2,n\SoundCHN2,Camera,n\obj)
												n\PrevState = 1
											EndIf
											
											;Resetting the "PrevState" value randomly, to make 049 talking randomly 
											If Rand(600)=1 And ChannelPlaying(n\SoundCHN2)=False Then n\PrevState = 0
											
											If n\PrevState > 1 Then n\PrevState = 1
										EndIf
									Else ;No Path to the player found - stands still and tries to find a path
										;[Block]
										n\PathTimer# = n\PathTimer# + fs\FPSfactor[0]
										If n\PathTimer# > 70*(5-(2*SelectedDifficulty\aggressiveNPCs)) Then
											n\PathStatus = FindPath(n, EntityX(Collider),EntityY(Collider),EntityZ(Collider))
											n\PathTimer# = 0.0
											n\State3 = 0
											
											;Attempt to find a room (the Playerroom or one of it's adjacent rooms) for 049 to go to but select the one closest to him
											If n\PathStatus <> 1 Then
												Local closestdist# = EntityDistance(PlayerRoom\obj,n\Collider)
												Local closestRoom.Rooms = PlayerRoom
												Local currdist# = 0.0
												For i = 0 To 3
													If PlayerRoom\Adjacent[i]<>Null Then
														currdist = EntityDistance(PlayerRoom\Adjacent[i]\obj,n\Collider)
														If currdist < closestdist Then
															closestdist = currdist
															closestRoom = PlayerRoom\Adjacent[i]
														EndIf
													EndIf
												Next
												n\PathStatus = FindPath(n,EntityX(closestRoom\obj),0.5,EntityZ(closestRoom\obj))
											EndIf
											
											;Making 3 attempts at finding a path
											While Int(n\State3) < 3
												;Breaking up the path if no "real" path has been found (only 1 waypoint and it is too close)
												If n\PathStatus = 1 Then
													If n\Path[1]<>Null Then
														If n\Path[2]=Null And EntityDistance(n\Path[1]\obj,n\Collider)<0.4 Then
															n\PathLocation = 0
															n\PathStatus = 0
														EndIf
													EndIf
													If n\Path[0]<>Null And n\Path[1]=Null Then
														n\PathLocation = 0
														n\PathStatus = 0
													EndIf
												EndIf
												
												;No path could still be found, just make 049 go to a room (further away than the very first attempt)
												If n\PathStatus <> 1 Then
													closestdist# = 100.0 ;Prevent the PlayerRoom to be considered the closest, so 049 wouldn't try to find a path there
													closestRoom.Rooms = PlayerRoom
													currdist# = 0.0
													For i = 0 To 3
														If PlayerRoom\Adjacent[i]<>Null Then
															currdist = EntityDistance(PlayerRoom\Adjacent[i]\obj,n\Collider)
															If currdist < closestdist Then
																closestdist = currdist
																For j = 0 To 3
																	If PlayerRoom\Adjacent[i]\Adjacent[j]<>Null Then
																		If PlayerRoom\Adjacent[i]\Adjacent[j]<>PlayerRoom Then
																			closestRoom = PlayerRoom\Adjacent[i]\Adjacent[j]
																			Exit
																		EndIf
																	EndIf
																Next
															EndIf
														EndIf
													Next
													n\PathStatus = FindPath(n,EntityX(closestRoom\obj),0.5,EntityZ(closestRoom\obj))
												EndIf
												
												;Making 049 skip waypoints for doors he can't interact with, but only if the actual path is behind him
												If n\PathStatus = 1 Then
													If n\Path[1]<>Null Then
														If n\Path[1]\door<>Null Then
															If (n\Path[1]\door\locked Or n\Path[1]\door\KeyCard<>0 Or n\Path[1]\door\Code<>"") And (Not n\Path[1]\door\open) Then
																Repeat
																	If n\PathLocation > 19
																		n\PathLocation = 0 : n\PathStatus = 0 : Exit
																	Else
																		n\PathLocation = n\PathLocation + 1
																	EndIf
																	If n\Path[n\PathLocation]<>Null Then
																		If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>(45.0-Abs(DeltaYaw(n\Collider,n\Path[1]\obj))) Then
																			n\State3 = 3
																			Exit
																		EndIf
																	EndIf
																Forever
															Else
																n\State3 = 3
															EndIf
														Else
															n\State3 = 3
														EndIf
													EndIf
												EndIf
												n\State3 = n\State3 + 1
											Wend
										EndIf
										AnimateNPC(n, 269, 345, 0.2)
										;[End Block]
									EndIf
								EndIf
								
								If n\CurrSpeed > 0.005 Then
									If (prevFrame < 361 And n\Frame=>361) Or (prevFrame < 377 And n\Frame=>377) Then
										PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))						
									ElseIf (prevFrame < 431 And n\Frame=>431) Or (prevFrame < 447 And n\Frame=>447) Then
										PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))
									EndIf
								EndIf
								
								If ChannelPlaying(n\SoundCHN2)
									UpdateSoundOrigin(n\SoundCHN2,Camera,n\obj)
								EndIf
							ElseIf (Not n\Idle)
								If ChannelPlaying(n\SoundCHN) Then
									StopChannel(n\SoundCHN)
								EndIf
								If PlayerInReachableRoom(True) And InFacility=1 Then ;Player is in a room where SCP-049 can teleport to
									If Rand(1,3-SelectedDifficulty\otherFactors)=1 Then
										TeleportCloser(n)
									Else
										n\Idle = 60*70
									EndIf
								EndIf
							EndIf
							;[End Block]
						Case 3 ;The player was killed by SCP-049
							;[Block]
							AnimateNPC(n, 537, 660, 0.7, False)
							
							;Animate2(n\obj, AnimTime(n\obj), 537, 660, 0.7, False)
							PositionEntity n\Collider, CurveValue(EntityX(Collider),EntityX(n\Collider),20.0),EntityY(n\Collider),CurveValue(EntityZ(Collider),EntityZ(n\Collider),20.0)
							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(Collider)-180.0,EntityYaw(n\Collider),40), 0
							;[End Block]
						Case 4 ;Standing on catwalk in room4
							;[Block]
							If dist < 8.0 Then
								AnimateNPC(n, 18, 19, 0.05)
								
								;Animate2(n\obj, AnimTime(n\obj), 18, 19, 0.05)
								PointEntity n\obj, Collider	
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 45.0), 0
								
								n\State3 = 1
							ElseIf dist > HideDistance*0.8 And n\State3 > 0 Then
								n\State = 2
								n\State3 = 0
								For r.Rooms = Each Rooms
									If EntityDistance(r\obj,n\Collider)<4.0 Then
										TeleportEntity(n\Collider,EntityX(r\obj),0.1,EntityZ(r\obj),n\CollRadius,True)
										Exit
									EndIf
								Next
							EndIf
							;[End Block]
						Case 5 ;used for "room2sl"
							;[Block]
							n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider)
							PlayerSeeAble% = MeNPCSeesPlayer(n,True)
							If PlayerSeeAble% = True
								n\State = 2
								n\PathStatus = 0
								n\PathLocation = 0
								n\PathTimer = 0
								n\State3 = 0
								n\State2 = 70*2
								n\PrevState = 0
								PlaySound_Strict LoadTempSound(SFXPath$+"Room\Room2SL049Spawn.ogg")
							ElseIf PlayerSeeAble% = 2 And n\State3 > 0.0
								n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
							Else
								If n\State3 = 6.0
									If EntityDistance(n\Collider,Collider)>HideDistance
										n\State = 2
										n\PathStatus = 0
										n\PathLocation = 0
										n\PathTimer = 0
										n\State3 = 0
										n\PrevState = 0
									Else
										If n\PathStatus <> 1 Then n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
									EndIf
								EndIf
								
								If n\PathStatus = 1
									If n\Path[n\PathLocation]=Null
										If n\PathLocation > 19 Then
											n\PathLocation = 0 : n\PathStatus = 0
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										PointEntity n\obj,n\Path[n\PathLocation]\obj
										RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),10.0),0
										MoveEntity n\Collider,0,0,n\CurrSpeed*fs\FPSfactor[0]
										
										;closes doors behind him
										If n\PathLocation>0 Then
											If n\Path[n\PathLocation-1] <> Null
												If n\Path[n\PathLocation-1]\door <> Null Then
													If n\Path[n\PathLocation-1]\door\KeyCard=0
														If EntityDistance(n\Path[n\PathLocation-1]\obj,n\Collider)>0.3
															If n\Path[n\PathLocation-1]\door\open Then UseDoor(n\Path[n\PathLocation-1]\door, False)
														EndIf
													EndIf
												EndIf
											EndIf
										EndIf
										
										;opens doors in front of him
										dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
										If dist2 < 0.6 Then
											If n\Path[n\PathLocation]\door <> Null Then
												If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
											EndIf
										EndIf
										
										If dist2#<0.2
											n\PathLocation = n\PathLocation + 1
										EndIf
										
										AnimateNPC(n, Max(Min(AnimTime(n\obj),358.0),346), 393.0, n\CurrSpeed*38)
									EndIf
								Else
									Select n\PrevState
										Case 0
											AnimateNPC(n, 269, 345, 0.2)
										Case 1
											AnimateNPC(n, 661, 891, 0.4, False)
										Case 2
											AnimateNPC(n, 892, 1119, 0.4, False)
									End Select
								EndIf
							EndIf
							
							If PlayerRoom\RoomTemplate\Name = "room2sl" Then
								ShouldPlay = 20
							EndIf
							
							If n\CurrSpeed > 0.005 Then
								If (prevFrame < 361 And n\Frame=>361) Or (prevFrame < 377 And n\Frame=>377) Then
									PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))						
								ElseIf (prevFrame < 431 And n\Frame=>431) Or (prevFrame < 447 And n\Frame=>447)
									PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))
								EndIf
							EndIf
							
							If ChannelPlaying(n\SoundCHN2)
								UpdateSoundOrigin(n\SoundCHN2,Camera,n\obj)
							EndIf
							;[End Block]
					End Select
				EndIf
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider)-0.22, EntityZ(n\Collider))
				
				RotateEntity n\obj, 0, EntityYaw(n\Collider), 0
				
				n\LastSeen = Max(n\LastSeen-fs\FPSfactor[0],0)
				
				n\State2 = Max(n\State2-fs\FPSfactor[0],0)
				
				;[End Block]

			Case NPCtype049_2
				;[Block]
				
				If Abs(EntityY(Collider)-EntityY(n\Collider))<4.0 Then
					
					prevFrame# = n\Frame
					
					If (Not n\IsDead)
						Select n\State
							Case 0 ;lying
								;[Block]
								AnimateNPC(n, 719, 777, 0.2, False)
								
								If n\Frame=777 Then
									If Rand(700)=1 Then
										If EntityDistance(Collider, n\Collider)<5.0 Then
											n\Frame = 719
										EndIf
									EndIf
								EndIf
								;[End Block]
							Case 1 ;stands up
								;[Block]
								If n\Frame=>682 Then 
									AnimateNPC(n, 926, 935, 0.3, False)
									If n\Frame = 935 Then n\State = 2
									
									;Animate2(n\obj, AnimTime(n\obj), 926, 935, 0.3, False)
									;If AnimTime(n\obj)=935 Then n\State = 2
								Else
									AnimateNPC(n, 155, 682, 1.1, False)
									;Animate2(n\obj, AnimTime(n\obj), 155, 682, 1.5, False)
								EndIf
								;[End Block]
							Case 2 ;following the player
								;[Block]
								If n\State3 < 0 Then ;check if the player is visible every three seconds
									If EntityDistance(Collider, n\Collider)<5.0 Then 
										If EntityVisible(Collider, n\Collider) Then n\State2 = 70*5
									EndIf
									n\State3=70*3
								Else
									n\State3=n\State3-fs\FPSfactor[0]
								EndIf
								
								n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider, 6.0, 0.6)

								If n\State2 > 0 And (Not chs\NoTarget) Then ;player is visible -> attack
									
									n\PathStatus = 0
									
									dist = EntityDistance(Collider, n\Collider)
									
									PointEntity n\obj, Collider
									RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
									
									If dist < 0.7 Then 
										n\State = 3
										If Rand(2)=1 Then
											n\Frame = 2
											;SetAnimTime n\obj, 2
										Else
											n\Frame = 66
											;SetAnimTime n\obj, 66
										EndIf
									Else
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
										
										AnimateNPC(n, 936, 1017, n\CurrSpeed*60)
										
										;Animate2(n\obj, AnimTime(n\obj), 936, 1017, n\CurrSpeed*60)
									EndIf
									
									n\State2=n\State2-fs\FPSfactor[0]
								Else
									If n\PathStatus = 1 Then ;path found
										If n\Path[n\PathLocation]=Null Then 
											If n\PathLocation > 19 Then 
												n\PathLocation = 0 : n\PathStatus = 0
											Else
												n\PathLocation = n\PathLocation + 1
											EndIf
										Else
											PointEntity n\obj, n\Path[n\PathLocation]\obj
											
											RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
											n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
											MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
											
											AnimateNPC(n, 936, 1017, n\CurrSpeed*60)
											;Animate2(n\obj, AnimTime(n\obj), 936, 1017, n\CurrSpeed*60)
											
											AnimateNPC(n, 936, 1017, n\CurrSpeed*60)
											;Animate2(n\obj, AnimTime(n\obj), 936, 1017, n\CurrSpeed*60)
											
											;opens doors in front of him
										    dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
										    If dist2 < 0.6 Then
											    temp = True
											    If n\Path[n\PathLocation]\door <> Null Then
												    If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
													    If n\Path[n\PathLocation]\door\locked Or n\Path[n\PathLocation]\door\KeyCard>0 Or n\Path[n\PathLocation]\door\Code<>"" Then
														    temp = False
													    Else
														    If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
													    EndIf
												    EndIf
											    EndIf
											    If dist2#<0.2 And temp
												    n\PathLocation = n\PathLocation + 1
											    ElseIf dist2#<0.5 And (Not temp)
												    n\PathStatus = 0
												    n\PathTimer# = 0.0
											    EndIf
										    EndIf	
										EndIf
									Else ;no path to the player, stands still
										n\CurrSpeed = 0
										AnimateNPC(n, 778, 926, 0.1)
										;Animate2(n\obj, AnimTime(n\obj), 778, 926, 0.1)
										
										n\PathTimer = n\PathTimer-fs\FPSfactor[0]
										If n\PathTimer < 70*5
										    n\PathTimer = n\PathTimer + Rnd(1,2+(2*SelectedDifficulty\aggressiveNPCs))*fs\FPSfactor[0]
									    Else
										    n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
										    n\PathTimer = 0
									    EndIf
									EndIf
								EndIf
								
								;65, 80, 93, 109, 123
								If n\CurrSpeed > 0.005 Then
									If (prevFrame < 977 And n\Frame=>977) Or (prevFrame > 1010 And n\Frame<940) Then
										;PlaySound2(StepSFX(0,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
										PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
									EndIf
								EndIf
								;[End Block]
							Case 3
								;[Block]
								If chs\NoTarget Then n\State = 2
								If n\Frame < 66 Then
									AnimateNPC(n, 2, 65, 0.7, False)
									
									;Animate2(n\obj, AnimTime(n\obj), 2, 65, 0.7, False)
									If prevFrame < 23 And n\Frame=>23 Then
										If EntityDistance(n\Collider,Collider)<1.1
											If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
											    PlaySound_Strict DamageSFX(Rand(5,8))
											    If I_1033RU\HP = 0
												    Injuries = Injuries+Rnd(0.4,1.0)
												Else
											        Damage1033RU(20)
											        n\HP = n\HP - 20
											        If n\HP =< 0 Then
							                            n\IsDead = True
							                        EndIf
											    EndIf
											    If Injuries > 3.0
												    DeathMSG = SubjectName$+". Cause of death: multiple lacerations and severe blunt force trauma caused by an instance of SCP-049-2."
												    Kill(True)
												EndIf
											EndIf
										EndIf
									ElseIf n\Frame=65 Then
										n\State = 2
									EndIf							
								Else
									AnimateNPC(n, 66, 132, 0.7, False)
									;Animate2(n\obj, AnimTime(n\obj), 66, 132, 0.7, False)
									If prevFrame < 90 And n\Frame=>90 Then
										If EntityDistance(n\Collider,Collider)<1.1
											If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
											    PlaySound_Strict DamageSFX(Rand(5,8))
											    If I_1033RU\HP = 0
												    Injuries = Injuries+Rnd(0.4,1.0)
												Else
											        Damage1033RU(20)
											        n\HP = n\HP - 20
											        If n\HP =< 0 Then
							                            n\IsDead = True
							                        EndIf
                                                EndIf
												If Injuries > 3.0
												    DeathMSG = SubjectName$+". Cause of death: multiple lacerations and severe blunt force trauma caused by an instance of SCP-049-2."
												    Kill(True)
											    EndIf
											EndIf
										EndIf
									ElseIf n\Frame=132 Then
										n\State = 2
									EndIf		
								EndIf
								;[End Block]
						End Select
					Else
					    If n\SoundCHN <> 0
						    StopChannel n\SoundCHN
						    n\SoundCHN = 0
						    FreeSound_Strict n\Sound
						    n\Sound = 0
					    EndIf
					    ;If Rand(2) = 1 Then
						    ;AnimateNPC(n, 133, 157, 0.5, False)
						;Else
						    AnimateNPC(n, 1035, 1072, 0.3, False)
                        ;EndIf
					EndIf
					
					PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
					
					RotateEntity n\obj, -90, EntityYaw(n\Collider), 0
				EndIf
				
				;[End Block]
			Case NPCtypeGuard
				;[Block]
				prevFrame# = n\Frame
				
				n\BoneToManipulate = ""
				;n\BoneToManipulate2 = ""
				n\ManipulateBone = False
				n\ManipulationType = 0
				n\NPCNameInSection = "Guard"
				
				Select n\State
					Case 1 ;aims and shoots at the player
						;[Block]
						If n\Frame < 39 Or (n\Frame > 76 And n\Frame < 245) Or (n\Frame > 248 And n\Frame < 302) Or n\Frame > 344
							AnimateNPC(n,345,357,0.2,False)
							If n\Frame >= 356 Then SetNPCFrame(n,302)
						EndIf
						;Animate2(n\obj, AnimTime(n\obj), 1539, 1553, 0.2, False)
						
						If KillTimer => 0 Then
							dist = EntityDistance(n\Collider,Collider)
							Local ShootAccuracy# = 0.4+0.5*SelectedDifficulty\aggressiveNPCs
							Local DetectDistance# = 11.0
							
							;If at Gate B increase his distance so that he can shoot the player from a distance after they are spotted.
							If PlayerRoom\RoomTemplate\Name = "gateb" Then
								DetectDistance = 21.0
								ShootAccuracy = 0.0
								If Rand(1,8-SelectedDifficulty\aggressiveNPCs*4)<2 Then ShootAccuracy = 0.03
								
								;increase accuracy if the player is going slow
								ShootAccuracy = ShootAccuracy + (0.5 - CurrSpeed*20)
							EndIf
							
							If dist < DetectDistance Then
								pvt% = CreatePivot()
								PositionEntity(pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
								PointEntity(pvt, Collider)
								RotateEntity(pvt, Min(EntityPitch(pvt), 20), EntityYaw(pvt), 0)
								
								RotateEntity(n\Collider, CurveAngle(EntityPitch(pvt), EntityPitch(n\Collider), 10), CurveAngle(EntityYaw(pvt), EntityYaw(n\Collider), 10), 0, True)
								
								PositionEntity(pvt, EntityX(n\Collider), EntityY(n\Collider)+0.8, EntityZ(n\Collider))
								PointEntity(pvt, Collider)
								RotateEntity(pvt, Min(EntityPitch(pvt), 40), EntityYaw(n\Collider), 0)
								
								If n\Reload = 0 ;And n\Frame>1550 Then
									EntityPick(pvt, dist)
									If PickedEntity() = Collider Or n\State3=1 Then
										Local instaKillPlayer% = False
										
										If PlayerRoom\RoomTemplate\Name = "room173" Then 
											DeathMSG = SubjectName$+". Cause of death: Gunshot wound to the head. The surveillance tapes confirm that the subject was terminated by Agent Ulgrin shortly after the site lockdown was initiated."
											instaKillPlayer = True
										ElseIf PlayerRoom\RoomTemplate\Name = "gateb" Then
											DeathMSG = Chr(34)+"Agent G. to control. Eliminated a Class D escape in Gate B's courtyard."+Chr(34)
										Else
											DeathMSG = ""
										EndIf
										
										PlaySound2(GunshotSFX, Camera, n\Collider, 35)
										
										RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
										PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
										MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
										
										PointEntity pvt, Collider
										Shoot(EntityX(pvt), EntityY(pvt), EntityZ(pvt), ShootAccuracy, True, instaKillPlayer)
										n\Reload = 7
									Else
										n\CurrSpeed = n\Speed
									End If
								EndIf
								
								If n\Reload > 0 And n\Reload <= 7
									AnimateNPC(n,245,248,0.35,True)
								Else
									If n\Frame < 302
										AnimateNPC(n,302,344,0.35,True)
									EndIf
								EndIf
								
								FreeEntity(pvt)
							Else
								AnimateNPC(n,302,344,0.35,True)
							EndIf
							
							n\ManipulateBone = True
							
							If n\State2 = 10 Then ;Hacky way of applying spine pitch to specific guards.
								n\BoneToManipulate = "Chest"
								n\ManipulationType = 3
							Else
								n\BoneToManipulate = "Chest"
								n\ManipulationType = 0
							EndIf
						Else
							n\State = 0
						EndIf
						;[End Block]
					Case 2 ;shoots
						;[Block]
						AnimateNPC(n,245,248,0.35,True)
						;Animate2(n\obj, AnimTime(n\obj), 1539, 1553, 0.35, False)
						If n\Reload = 0 ;And n\Frame > 1545 Then 
							PlaySound2(GunshotSFX, Camera, n\Collider, 20)
							p.Particles = CreateParticle(EntityX(n\obj, True), EntityY(n\obj, True), EntityZ(n\obj, True), 1, 0.2, 0.0, 5)
							PositionEntity(p\pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
							RotateEntity(p\pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
							MoveEntity (p\pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
							n\Reload = 7
						End If
						;[End Block]
					Case 3 ;follows a path
						;[Block]
						If n\PathStatus = 2 Then
							n\State = 0
							n\CurrSpeed = 0
						ElseIf n\PathStatus = 1
							If n\Path[n\PathLocation]=Null Then 
								If n\PathLocation > 19 Then 
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								PointEntity n\obj, n\Path[n\PathLocation]\obj
								
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
								
								AnimateNPC(n,1,38,n\CurrSpeed*40)
								;Animate2(n\obj, AnimTime(n\obj), 1614, 1641, n\CurrSpeed*30)
								n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
								
								MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
								
								If EntityDistance(n\Collider,n\Path[n\PathLocation]\obj) < 0.2 Then
									n\PathLocation = n\PathLocation + 1
								EndIf 
							EndIf
						Else
							n\CurrSpeed = 0
							n\State = 4
						EndIf
						;[End Block]
					Case 4
						;[Block]
						AnimateNPC(n,77,201,0.2)
						;Animate2(n\obj, AnimTime(n\obj), 923, 1354, 0.2)
						
						If Rand(400) = 1 Then n\Angle = Rnd(-180, 180)
						
						RotateEntity(n\Collider, 0, CurveAngle(n\Angle + Sin(MilliSecs2() / 50) * 2, EntityYaw(n\Collider), 150.0), 0, True)
						
						dist# = EntityDistance(n\Collider, Collider)
						If dist < 15.0 Then
							
							If WrapAngle(EntityYaw(n\Collider)-DeltaYaw(n\Collider, Collider))<90 Then
								If EntityVisible(pvt,Collider) Then n\State = 1
							EndIf
							
						EndIf
						
						;[End Block]
					Case 5 ;following a target
						;[Block]
						
						RotateEntity n\Collider, 0, CurveAngle(VectorYaw(n\EnemyX-EntityX(n\Collider), 0, n\EnemyZ-EntityZ(n\Collider))+n\Angle, EntityYaw(n\Collider), 20.0), 0
						
						dist# = Distance(EntityX(n\Collider),EntityZ(n\Collider),n\EnemyX,n\EnemyZ)
						
						AnimateNPC(n,1,38,n\CurrSpeed*40)
						
						If dist > 2.0 Or dist < 1.0  Then
							n\CurrSpeed = CurveValue(n\Speed*Sgn(dist-1.5)*0.75, n\CurrSpeed, 10.0)
						Else
							n\CurrSpeed = CurveValue(0, n\CurrSpeed, 10.0)
						EndIf
						
						MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
						;[End Block]
					Case 7
						;[Block]
						If n\UseHeadPhones = True Then
						    AnimateNPC(n, 623, 747, 0.2)
						Else
						    AnimateNPC(n, 77, 201, 0.2)
						EndIf
						;[End Block]
					Case 8
			
					Case 9
						;[Block]
						If n\UseHeadPhones = True Then
						    AnimateNPC(n, 623, 747, 0.2)
						Else
						    AnimateNPC(n,77,201,0.2)
						EndIf
						n\BoneToManipulate = "head"
						n\ManipulateBone = True
						n\ManipulationType = 0
						n\Angle = EntityYaw(n\Collider)
						;[End Block]
					Case 10
						;[Block]
						AnimateNPC(n, 1, 38, n\CurrSpeed*40)
						
						n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
						
						MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
						;[End Block]
					Case 11
						;[Block]
						dist_to_player = EntityDistance(Collider, n\Collider)
						
						If n\UseHeadPhones = True Then
						     If n\Frame < 787 Or (n\Frame > 824 And n\Frame < 867) Or (n\Frame > 870 And n\Frame < 884) Or n\Frame > 939
							    AnimateNPC(n,927,939,0.2,False)
							    If n\Frame >= 883 Then SetNPCFrame(n,884)
						    EndIf
						Else
						    If n\Frame < 39 Or (n\Frame > 76 And n\Frame < 245) Or (n\Frame > 248 And n\Frame < 302) Or n\Frame > 344
							    AnimateNPC(n,345,357,0.2,False)
							    If n\Frame >= 356 Then SetNPCFrame(n,302)
						    EndIf
						EndIf
						
						If KillTimer => 0 Then
							dist = EntityDistance(n\Collider,Collider)
							
							Local SearchPlayer% = False
							If dist < 11.0
								If EntityVisible(n\Collider,Collider)
									SearchPlayer = True
								EndIf
							EndIf
							
							If SearchPlayer
								pvt% = CreatePivot()
								PositionEntity(pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
								PointEntity(pvt, Collider)
								RotateEntity(pvt, Min(EntityPitch(pvt), 20), EntityYaw(pvt), 0)
								
								RotateEntity(n\Collider, CurveAngle(EntityPitch(pvt), EntityPitch(n\Collider), 10), CurveAngle(EntityYaw(pvt), EntityYaw(n\Collider), 10), 0, True)
								
								PositionEntity(pvt, EntityX(n\Collider), EntityY(n\Collider)+0.8, EntityZ(n\Collider))
								PointEntity(pvt, Collider)
								RotateEntity(pvt, Min(EntityPitch(pvt), 40), EntityYaw(n\Collider), 0)
								
								If n\Reload = 0
									EntityPick(pvt, dist)
									If PickedEntity() = Collider Or n\State3=1 Then
										instaKillPlayer% = False
										
										DeathMSG = ""
										
										PlaySound2(GunshotSFX, Camera, n\Collider, 35)
										
										RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
										PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
										MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
										
										PointEntity pvt, Collider
										Shoot(EntityX(pvt),EntityY(pvt), EntityZ(pvt),((25/dist_to_player)*(1/dist_to_player)), True, instaKillPlayer)
										n\Reload = 7
									Else
										n\CurrSpeed = n\Speed
									End If
								EndIf
								
								If n\Reload > 0 And n\Reload <= 7
								    If n\UseHeadPhones = True Then
								        AnimateNPC(n,867,870,0.35,True)
								    Else
									    AnimateNPC(n,245,248,0.35,True)
									EndIf
								Else
								    If n\UseHeadPhones = True Then
								        If n\Frame < 884
										    AnimateNPC(n,884,926,0.35,True)
									    EndIf
									Else
									    If n\Frame < 302
									        AnimateNPC(n,302,344,0.35,True)
									    EndIf
									EndIf
								EndIf
								
								FreeEntity(pvt)
							Else
								If n\PathStatus = 1
									If n\Path[n\PathLocation]=Null Then 
										If n\PathLocation > 19 Then 
											n\PathLocation = 0 : n\PathStatus = 0
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
									    If n\UseHeadPhones = True Then
									        AnimateNPC(n,787,823,n\CurrSpeed*40)
									    Else
										    AnimateNPC(n,39,76,n\CurrSpeed*40)
										EndIf
										n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
										
										PointEntity n\obj, n\Path[n\PathLocation]\obj
										
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
										
										If EntityDistance(n\Collider,n\Path[n\PathLocation]\obj) < 0.2 Then
											n\PathLocation = n\PathLocation + 1
										EndIf
									EndIf
								Else
									If n\PathTimer = 0 Then n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider)+0.5,EntityZ(Collider))
									
									Local wayPointCloseToPlayer.WayPoints
									wayPointCloseToPlayer = Null
									
									For wp.WayPoints = Each WayPoints
										If EntityDistance(wp\obj,Collider)<2.0
											wayPointCloseToPlayer = wp
											Exit
										EndIf
									Next
									
									If wayPointCloseToPlayer<>Null
										n\PathTimer = 1
										If EntityVisible(wayPointCloseToPlayer\obj,n\Collider)
											If Abs(DeltaYaw(n\Collider,wayPointCloseToPlayer\obj))>0
												PointEntity n\obj, wayPointCloseToPlayer\obj
												RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
											EndIf
										EndIf
									Else
										n\PathTimer = 0
									EndIf
									
									If n\PathTimer = 1
									    If n\UseHeadPhones = True Then
									        AnimateNPC(n,787,823,n\CurrSpeed*40)
									    Else
										    AnimateNPC(n,39,76,n\CurrSpeed*40)
										EndIf
										n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
									EndIf
								EndIf
								
								If prevFrame < 43 And n\Frame=>43 Then
									PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))						
								ElseIf prevFrame < 61 And n\Frame=>61
									PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
								EndIf
							EndIf
							
						Else
							n\State = 0
						EndIf
						;[End Block]
					Case 12
						;[Block]
						If n\Frame < 39 Or (n\Frame > 76 And n\Frame < 245) Or (n\Frame > 248 And n\Frame < 302) Or n\Frame > 344
							AnimateNPC(n,345,357,0.2,False)
							If n\Frame >= 356 Then SetNPCFrame(n,302)
						EndIf
						If n\Frame < 345
							AnimateNPC(n,302,344,0.35,True)
						EndIf
						
						pvt% = CreatePivot()
						PositionEntity(pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
						If n\State2 = 1.0
							PointEntity(pvt, Collider)
						Else
							RotateEntity pvt,0,n\Angle,0
						EndIf
						RotateEntity(pvt, Min(EntityPitch(pvt), 20), EntityYaw(pvt), 0)
						
						RotateEntity(n\Collider, CurveAngle(EntityPitch(pvt), EntityPitch(n\Collider), 10), CurveAngle(EntityYaw(pvt), EntityYaw(n\Collider), 10), 0, True)
						
						PositionEntity(pvt, EntityX(n\Collider), EntityY(n\Collider)+0.8, EntityZ(n\Collider))
						If n\State2 = 1.0
							PointEntity(pvt, Collider)
							n\ManipulateBone = True
							n\BoneToManipulate = "Chest"
							n\ManipulationType = 0
						Else
							RotateEntity pvt,0,n\Angle,0
						EndIf
						RotateEntity(pvt, Min(EntityPitch(pvt), 40), EntityYaw(n\Collider), 0)
						
						FreeEntity(pvt)
						
						UpdateSoundOrigin(n\SoundCHN,Camera,n\Collider,20)
						;[End Block]
					Case 13
						;[Block]
						AnimateNPC(n,202,244,0.35,True)
						;[End Block]
					Case 14
						;[Block]
						If n\PathStatus = 2 Then
							n\State = 13
							n\CurrSpeed = 0
						ElseIf n\PathStatus = 1
							If n\Path[n\PathLocation]=Null Then 
								If n\PathLocation > 19 Then 
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								PointEntity n\obj, n\Path[n\PathLocation]\obj
								
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
								
								AnimateNPC(n,39,76,n\CurrSpeed*40)
								n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
								
								MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
								
								If EntityDistance(n\Collider,n\Path[n\PathLocation]\obj) < 0.2 Then
									n\PathLocation = n\PathLocation + 1
								EndIf 
							EndIf
						Else
							n\CurrSpeed = 0
							n\State = 13
						EndIf
						
						If prevFrame < 43 And n\Frame=>43 Then
							PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))						
						ElseIf prevFrame < 61 And n\Frame=>61
							PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
						EndIf
						;[End Block]
					Default
						;[Block]
						If Rand(400) = 1 Then n\PrevState = Rnd(-30, 30)
						n\PathStatus = 0
						AnimateNPC(n, 77, 201, 0.2)
						
						RotateEntity(n\Collider, 0, CurveAngle(n\Angle + n\PrevState + Sin(MilliSecs2() / 50) * 2, EntityYaw(n\Collider), 50), 0, True)
						;[End Block]
				End Select
				
				If n\CurrSpeed > 0.01 Then
					If prevFrame < 5 And n\Frame>=5
						PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))						
					ElseIf prevFrame < 23 And n\Frame>=23
						PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))						
					EndIf
				EndIf
				
				If n\Frame > 286.5 And n\Frame < 288.5
					n\IsDead = True
				EndIf
				
				n\Reload = Max(0, n\Reload - fs\FPSfactor[0])
				;RotateEntity(n\Collider, 0, EntityYaw(n\Collider), 0, True)
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				
				;RotateEntity n\obj, EntityPitch(n\Collider)-90, EntityYaw(n\Collider), 0
				RotateEntity n\obj, 0, EntityYaw(n\Collider)+180, 0
				;[End Block]
			Case NPCtypeMTF 
				;[Block]
				UpdateMTFUnit(n)
				
				;[End Block]
			Case NPCtypeD, NPCtypeClerk 	
				;[Block]
				RotateEntity(n\Collider, 0, EntityYaw(n\Collider), EntityRoll(n\Collider), True)
				
				prevFrame = AnimTime(n\obj)
				
				Select n\State
					Case 0 ;idle
						n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
						Animate2(n\obj, AnimTime(n\obj), 210, 235, 0.1)
					Case 1 ;walking
						If n\State2 = 1.0
							n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
						Else
							n\CurrSpeed = CurveValue(0.015, n\CurrSpeed, 5.0)
						EndIf
						Animate2(n\obj, AnimTime(n\obj), 236, 260, n\CurrSpeed * 18)
					Case 2 ;running
						n\CurrSpeed = CurveValue(0.03, n\CurrSpeed, 5.0)
						Animate2(n\obj, AnimTime(n\obj), 301, 319, n\CurrSpeed * 18)
				End Select
				
				If n\State2 <> 2
					If n\State = 1
						If n\CurrSpeed > 0.01 Then
							If prevFrame < 244 And AnimTime(n\obj)=>244 Then
								PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))						
							ElseIf prevFrame < 256 And AnimTime(n\obj)=>256
								PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
							EndIf
						EndIf
					ElseIf n\State = 2
						If n\CurrSpeed > 0.01 Then
							If prevFrame < 309 And AnimTime(n\obj)=>309
								PlaySound2(StepSFX(GetStepSound(n\Collider),1,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
							ElseIf prevFrame =< 319 And AnimTime(n\obj)=<301
								PlaySound2(StepSFX(GetStepSound(n\Collider),1,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
							EndIf
						EndIf
					EndIf
				EndIf
				
				If n\Frame = 19 Or n\Frame = 60 Or n\Frame = 629 Or n\Frame = 676
					n\IsDead = True
				EndIf
				If AnimTime(n\obj)=19 Or AnimTime(n\obj)=60 Or AnimTime(n\obj)=629 Or AnimTime(n\obj)=676
					n\IsDead = True
				EndIf
				
				MoveEntity(n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0])
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
				
				RotateEntity n\obj, EntityPitch(n\Collider), EntityYaw(n\Collider)-180.0, 0
				;[End Block]
			Case NPCtype513_1
				;[Block]
				;If KeyHit(48) Then n\Idle = True : n\State2 = 0
				
				If PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then 
					If n\Idle Then
						HideEntity(n\obj)
						HideEntity(n\obj2)
						If Rand(200)=1 Then
							For w.WayPoints = Each WayPoints
								If w\room<>PlayerRoom Then
									x = Abs(EntityX(Collider)-EntityX(w\obj,True))
									If x>3 And x < 9 Then
										z = Abs(EntityZ(Collider)-EntityZ(w\obj,True))
										If z>3 And z < 9 Then
											PositionEntity(n\Collider, EntityX(w\obj,True), EntityY(w\obj,True), EntityZ(w\obj,True))
											PositionEntity(n\obj, EntityX(w\obj,True), EntityY(w\obj,True), EntityZ(w\obj,True))
											ResetEntity n\Collider
											ShowEntity(n\obj)
											ShowEntity(n\obj2)
											
											n\LastSeen = 0
											
											n\Path[0]=w
											
											n\Idle = False
											n\State2 = Rand(15,20)*70
											n\State = Max(Rand(-1,2),0)
											n\PrevState = Rand(0,1)
											Exit
										EndIf
									EndIf
								EndIf
							Next
						End If
					Else
						dist = EntityDistance(Collider, n\Collider)
						
						;use the prev-values to do a "twitching" effect
						n\PrevX = CurveValue(0.0, n\PrevX, 10.0)
						n\PrevZ = CurveValue(0.0, n\PrevZ, 10.0)
						
						If Rand(100)=1 Then
							If Rand(5)=1 Then
								n\PrevX = (EntityX(Collider)-EntityX(n\Collider))*0.9
								n\PrevZ = (EntityZ(Collider)-EntityZ(n\Collider))*0.9
							Else
								n\PrevX = Rnd(0.1,0.5)
								n\PrevZ = Rnd(0.1,0.5)						
							EndIf
						EndIf
						
						temp = Rnd(-1.0,1.0)
						PositionEntity n\obj2, EntityX(n\Collider)+n\PrevX*temp, EntityY(n\Collider) - 0.2 + Sin((MilliSecs2()/8-45) Mod 360)*0.05, EntityZ(n\Collider)+n\PrevZ*temp
						RotateEntity n\obj2, 0, EntityYaw(n\obj), 0
						If (Floor(AnimTime(n\obj2))<>Floor(n\Frame)) Then SetAnimTime n\obj2, n\Frame
						
						If n\State = 0 Then
							If n\PrevState=0
								AnimateNPC(n,2,74,0.2)
							Else
								AnimateNPC(n,75,124,0.2)
							EndIf
							;AnimateNPC(n, 229, 299, 0.2)
							
							If n\LastSeen Then 	
								PointEntity n\obj2, Collider
								RotateEntity n\obj, 0, CurveAngle(EntityYaw(n\obj2),EntityYaw(n\obj),40), 0
								If dist < 4 Then n\State = Rand(1,2)
							Else
								If dist < 6 And Rand(5)=1 Then
									If EntityInView(n\Collider,Camera) Then
										If EntityVisible(Collider, n\Collider) Then
											n\LastSeen = 1
											PlaySound_Strict LoadTempSound(SFXPath$+"SCP\513\Bell"+Rand(2, 4)+".ogg")
										EndIf
									EndIf
								EndIf								
							EndIf
							
						Else
							If n\Path[0]=Null Then
								
								;move towards a waypoint that is:
								;1. max 8 units away from 513-1
								;2. further away from the player than 513-1's current position 
								For w.WayPoints = Each WayPoints
									x = Abs(EntityX(n\Collider,True)-EntityX(w\obj,True))
									If x < 8.0 And x > 1.0 Then
										z = Abs(EntityZ(n\Collider,True)-EntityZ(w\obj,True))
										If z < 8.0 And z > 1.0 Then
											If EntityDistance(Collider, w\obj) > dist Then
												n\Path[0]=w
												Exit
											EndIf
										EndIf
									EndIf
								Next
								
								;no suitable path found -> 513-1 simply disappears
								If n\Path[0] = Null Then
									n\Idle = True
									n\State2 = 0
								EndIf
							Else
								
								If EntityDistance(n\Collider, n\Path[0]\obj) > 1.0 Then
									PointEntity n\obj, n\Path[0]\obj
									RotateEntity n\Collider, CurveAngle(EntityPitch(n\obj),EntityPitch(n\Collider),15.0), CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),15.0), 0, True
									n\CurrSpeed = CurveValue(0.05*Max((7.0-dist)/7.0,0.0),n\CurrSpeed,15.0)
									MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]
									If Rand(200)=1 Then MoveEntity n\Collider, 0, 0, 0.5
									RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0, True
								Else
									For i = 0 To 4
										If n\Path[0]\connected[i] <> Null Then
											If EntityDistance(Collider, n\Path[0]\connected[i]\obj) > dist Then
												
												If n\LastSeen = 0 Then 
													If EntityInView(n\Collider,Camera) Then
														If EntityVisible(Collider, n\Collider) Then
															n\LastSeen = 1
															PlaySound_Strict LoadTempSound(SFXPath$+"SCP\513\Bell"+Rand(2, 4)+".ogg")
														EndIf
													EndIf
												EndIf
												
												n\Path[0]=n\Path[0]\connected[i]
												Exit
											EndIf
										EndIf
									Next
									
									If n\Path[0]=Null Then n\State2 = 0
								EndIf
							EndIf
						EndIf
						
						PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2 + Sin((MilliSecs2()/8) Mod 360)*0.1, EntityZ(n\Collider))
						
						Select n\State 
							Case 1
								If n\PrevState=0
									AnimateNPC(n,125,194,n\CurrSpeed*20)
								Else
									AnimateNPC(n,195,264,n\CurrSpeed*20)
								EndIf
								;AnimateNPC(n, 458, 527, n\CurrSpeed*20)
								RotateEntity n\obj, 0, EntityYaw(n\Collider), 0 
							Case 2
								If n\PrevState=0
									AnimateNPC(n,2,74,0.2)
								Else
									AnimateNPC(n,75,124,0.2)
								EndIf
								;AnimateNPC(n, 229, 299, 0.2)
								RotateEntity n\obj, 0, EntityYaw(n\Collider), 0						
						End Select
						
						If n\State2 > 0 Then
							If dist < 4.0 Then n\State2 = n\State2-fs\FPSfactor[0]*4
							n\State2 = n\State2-fs\FPSfactor[0]
						Else
							n\Path[0]=Null
							n\Idle = True
							n\State2=0
						EndIf
						
					End If
					
				EndIf
				
				n\DropSpeed = 0
				ResetEntity(n\Collider)						
				;[End Block]
			Case NPCtype372 
				;[Block]
				RN$ = PlayerRoom\RoomTemplate\Name
				If RN$ <> "pocketdimension" And RN$ <> "dimension1499" Then 
					If n\Idle Then
						HideEntity(n\obj)
						If Rand(50) = 1 And (BlinkTimer < -5 And BlinkTimer > -15) Then
							ShowEntity(n\obj)
							angle# = EntityYaw(Collider)+Rnd(-90,90)
							
							dist = Rnd(1.5, 2.0)
							PositionEntity(n\Collider, EntityX(Collider) + Sin(angle) * dist, EntityY(Collider)+0.2, EntityZ(Collider) + Cos(angle) * dist)
							n\Idle = False
							n\State = Rand(20, 60)
							
							If Rand(300)=1 Then PlaySound2(RustleSFX(Rand(0, 6)),Camera, n\obj, 8, Rnd(0.0,0.2))
						End If
					Else
						PositionEntity(n\obj, EntityX(n\Collider) + Rnd(-0.005, 0.005), EntityY(n\Collider)+0.3+0.1*Sin(MilliSecs2()/2), EntityZ(n\Collider) + Rnd(-0.005, 0.005))
						RotateEntity n\obj, 0, EntityYaw(n\Collider), ((MilliSecs2()/5) Mod 360)
						
						AnimateNPC(n, 1, 14, 1.2, True)
						
						If EntityInView(n\obj, Camera) Then
							GiveAchievement(Achv372)
							
							If Rand(30)=1 Then 
								If (Not ChannelPlaying(n\SoundCHN)) Then
									If EntityVisible(Camera, n\obj) Then 
										n\SoundCHN = PlaySound2(RustleSFX(Rand(0, 6)),Camera, n\obj, 8, 0.3)
									EndIf
								EndIf
							EndIf
							
							temp = CreatePivot()
							PositionEntity temp, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
							PointEntity temp, n\Collider
							
							angle =  WrapAngle(EntityYaw(Collider)-EntityYaw(temp))
							If angle < 180 Then
								RotateEntity n\Collider, 0, EntityYaw(Collider)-80, 0		
							Else
								RotateEntity n\Collider, 0, EntityYaw(Collider)+80, 0
							EndIf
							FreeEntity temp
							
							MoveEntity n\Collider, 0, 0, 0.03*fs\FPSfactor[0]
							
							n\State = n\State-fs\FPSfactor[0]
						EndIf
						n\State=n\State-(fs\FPSfactor[0]/80.0)
						If n\State <= 0 Then n\Idle = True	
					End If
					
				EndIf
				
				n\DropSpeed = 0
				ResetEntity(n\Collider)						
				;[End Block]
			Case NPCtypeApache 
				;[Block]
				dist = EntityDistance(Collider, n\Collider)
				If dist<60.0 Then 
					If PlayerRoom\RoomTemplate\Name = "gateb" Then 
						dist2 = Max(Min(EntityDistance(n\Collider, PlayerRoom\Objects[3])/(8000.0*RoomScale),1.0),0.0)
					Else 
						dist2 = 1.0
					EndIf
					
					n\SoundCHN = LoopSound2(ApacheSFX, n\SoundCHN, Camera, n\Collider, 25.0, dist2)
				EndIf
				
				n\DropSpeed = 0
				
				Select n\State
					Case 0,1
						TurnEntity(n\obj2,0,20.0*fs\FPSfactor[0],0)
						TurnEntity(n\obj3,20.0*fs\FPSfactor[0],0,0)
						
						If n\State=1 And (Not chs\NoTarget) Then
							If Abs(EntityX(Collider)-EntityX(n\Collider))< 30.0 Then
								If Abs(EntityZ(Collider)-EntityZ(n\Collider))<30.0 Then
									If Abs(EntityY(Collider)-EntityY(n\Collider))<20.0 Then
										If Rand(20)=1 Then 
											If EntityVisible(Collider, n\Collider) Then
												n\State = 2
												PlaySound2(AlarmSFX(2), Camera, n\Collider, 50, 1.0)
											EndIf
										EndIf									
									EndIf
								EndIf
							EndIf							
						EndIf
					Case 2,3 ;player located -> attack
						
						If n\State = 2 Then 
							target = Collider
						ElseIf n\State = 3
							target=CreatePivot()
							PositionEntity target, n\EnemyX, n\EnemyY, n\EnemyZ, True
						EndIf
						
						If chs\NoTarget And n\State = 2 Then n\State = 1
						
						TurnEntity(n\obj2,0,20.0*fs\FPSfactor[0],0)
						TurnEntity(n\obj3,20.0*fs\FPSfactor[0],0,0)
						
						If Abs(EntityX(target)-EntityX(n\Collider)) < 55.0 Then
							If Abs(EntityZ(target)-EntityZ(n\Collider)) < 55.0 Then
								If Abs(EntityY(target)-EntityY(n\Collider))< 20.0 Then
									PointEntity n\obj, target
									RotateEntity n\Collider, CurveAngle(Min(WrapAngle(EntityPitch(n\obj)),40.0),EntityPitch(n\Collider),40.0), CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),90.0), EntityRoll(n\Collider), True
									PositionEntity(n\Collider, EntityX(n\Collider), CurveValue(EntityY(target)+8.0,EntityY(n\Collider),70.0), EntityZ(n\Collider))
									
									dist# = Distance(EntityX(target),EntityZ(target),EntityX(n\Collider),EntityZ(n\Collider))
									
									n\CurrSpeed = CurveValue(Min(dist-6.5,6.5)*0.008, n\CurrSpeed, 50.0)
									
									;If Distance(EntityX(Collider),EntityZ(Collider),EntityX(n\collider),EntityZ(n\collider)) > 6.5 Then
									;	n\currspeed = CurveValue(0.08,n\currspeed,50.0)
									;Else
									;	n\currspeed = CurveValue(0.0,n\currspeed,30.0)
									;EndIf
									MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]
									
									
									If n\PathTimer = 0 Then
										n\PathStatus = EntityVisible(n\Collider,target)
										n\PathTimer = Rand(100,200)
									Else
										n\PathTimer = Min(n\PathTimer-fs\FPSfactor[0],0.0)
									EndIf
									
									If n\PathStatus = 1 Then ;player visible
										RotateEntity n\Collider, EntityPitch(n\Collider), EntityYaw(n\Collider), CurveAngle(0, EntityRoll(n\Collider),40), True
										
										If n\Reload =< 0 Then
											If dist<20.0 Then
												pvt = CreatePivot()
												
												PositionEntity pvt, EntityX(n\Collider),EntityY(n\Collider), EntityZ(n\Collider)
												RotateEntity pvt, EntityPitch(n\Collider), EntityYaw(n\Collider),EntityRoll(n\Collider)
												MoveEntity pvt, 0, 8.87*(0.21/9.0), 8.87*(1.7/9.0) ;2.3
												PointEntity pvt, target
												
												If WrapAngle(EntityYaw(pvt)-EntityYaw(n\Collider))<10 Then
													PlaySound2(Gunshot2SFX, Camera, n\Collider, 20)
													
													If PlayerRoom\RoomTemplate\Name = "gateb" Then
														DeathMSG = Chr(34)+"CH-2 to control. Shot down a runaway Class D at Gate B."+Chr(34)
													Else
													    DeathMSG = Chr(34)+"CH-2 to control. Shot down a runaway Class D at Gate A."+Chr(34)
													EndIf
													
													Shoot(EntityX(pvt),EntityY(pvt), EntityZ(pvt),((10/dist)*(1/dist))*(n\State=2),(n\State=2))
													
													n\Reload = 5
												EndIf
												
												FreeEntity pvt
											EndIf
										EndIf
									Else 
										RotateEntity n\Collider, EntityPitch(n\Collider), EntityYaw(n\Collider), CurveAngle(-20, EntityRoll(n\Collider),40), True
									EndIf
									MoveEntity n\Collider, -EntityRoll(n\Collider)*0.002,0,0
									
									n\Reload=n\Reload-fs\FPSfactor[0]
								
								EndIf
							EndIf
						EndIf		
						
						If n\State = 3 Then FreeEntity target
					Case 4 ;crash
						If n\State2 < 300 Then
							
							TurnEntity(n\obj2,0,20.0*fs\FPSfactor[0],0)
							TurnEntity(n\obj3,20.0*fs\FPSfactor[0],0,0)
							
							TurnEntity n\Collider,0,-fs\FPSfactor[0]*7,0;Sin(MilliSecs2()/40)*fs\FPSfactor[0]
							n\State2=n\State2+fs\FPSfactor[0]*0.3
							
							target=CreatePivot()
							PositionEntity target, n\EnemyX, n\EnemyY, n\EnemyZ, True
							
							PointEntity n\obj, target
							MoveEntity n\obj, 0,0,fs\FPSfactor[0]*0.001*n\State2
							PositionEntity(n\Collider, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
							
							If EntityDistance(n\obj, target) <0.3 Then
								;If TempSound2 <> 0 Then FreeSound_Strict TempSound2 : TempSound2 = 0
								;TempSound2 = LoadSound_Strict(SFXPath$+"Character\Apache\Crash"+Rand(1,2)+".ogg")
								CameraShake = Max(CameraShake, 3.0)
								;PlaySound_Strict TempSound2
								PlaySound_Strict LoadTempSound(SFXPath$+"Character\Apache\Crash"+Rand(1,2)+".ogg")
								n\State = 5
							EndIf
							
							FreeEntity target
						EndIf
				End Select
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
				RotateEntity n\obj, EntityPitch(n\Collider), EntityYaw(n\Collider), EntityRoll(n\Collider), True
				;[End Block]
			Case NPCtype035_Tentacle
				;[Block]
				dist = EntityDistance(n\Collider,Collider)
				
				If dist < HideDistance
				
				    If (Not n\IsDead)
					
					    Select n\State 
						    Case 0 ;spawn
							
							    If n\Frame>283 Then
								    HeartBeatVolume = Max(CurveValue(1.0, HeartBeatVolume, 50),HeartBeatVolume)
								    HeartBeatRate = Max(CurveValue(130, HeartBeatRate, 100), HeartBeatRate)
								
								    PointEntity n\obj, Collider
								    RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),25.0), 0
								
								    AnimateNPC(n, 283, 389, 0.3, False)
								    ;Animate2(n\obj, AnimTime(n\obj), 283, 389, 0.3, False)
								
								    If n\Frame>388 Then
									    n\State = 1
									    FreeSound_Strict n\Sound2 : n\Sound2 = 0
								    EndIf
							    Else
								    If dist < 2.5 Then 
									    SetNPCFrame(n, 284)
									    n\Sound2 = LoadSound_Strict(SFXPath$+"Room\035Chamber\TentacleSpawn.ogg")
									    PlaySound_Strict(n\Sound2)
								    EndIf
							    EndIf
							    ;spawn 283,389
							    ;attack 2, 32
							    ;idle 33, 174
						    Case 1 ;idle
							    If n\Sound2=0 Then
								    FreeSound_Strict n\Sound2 : n\Sound2=0
								    n\Sound2 = LoadSound_Strict(SFXPath$+"Room\035Chamber\TentacleIdle.ogg")
							    EndIf
							    n\SoundCHN2 = LoopSound2(n\Sound2,n\SoundCHN2,Camera,n\Collider)
							
							    If dist < 1.8 And (Not chs\Notarget) Then
								    If Abs(DeltaYaw(n\Collider, Collider))<20 Then 
									    n\State = 2
									    If n\Sound<>0 Then FreeSound_Strict n\Sound : n\Sound = 0 
									    ;If n\Sound2<>0 Then FreeSound_Strict n\Sound2 : n\Sound2 = 0
								    EndIf
							    EndIf
							
							    PointEntity n\obj, Collider
							    RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),25.0), 0
							
							    AnimateNPC(n, 33, 174, 0.3, True)
							    ;Animate2(n\obj, AnimTime(n\obj), 33, 174, 0.3, True)
						    Case 2
							
							    ;finish the idle animation before playing the attack animation
							    If n\Frame>33 And n\Frame<174 Then
								    AnimateNPC(n, 33, 174, 2.0, False)
								    ;Animate2(n\obj, AnimTime(n\obj), 33, 174, 2.0, False)
							    Else
								    PointEntity n\obj, Collider
								    RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),10.0), 0							
								
								    If n\Frame>33 Then 
									    ;SetAnimTime(n\obj,2)
									    If n\Sound2<>0 Then FreeSound_Strict n\Sound2 : n\Sound2 = 0
									    n\Frame = 2
									    n\Sound = LoadSound_Strict(SFXPath$+"Room\035Chamber\TentacleAttack"+Rand(1,2)+".ogg")
									    PlaySound_Strict(n\Sound)
								    EndIf
								    AnimateNPC(n, 2, 32, 0.3, False)
								    ;Animate2(n\obj, AnimTime(n\obj), 2, 32, 0.3, False)
								
								    If n\Frame>=5 And n\Frame<6 Then
									    If dist < 1.8 Then
										    If Abs(DeltaYaw(n\Collider, Collider))<20 Then 
											    If WearingHazmat Then
											        PlaySound_Strict(LoadTempSound(SFXPath$+"General\BodyFall.ogg"))
											        BlurTimer = 100
											        If I_1033RU\HP = 0
												        Injuries = Injuries+Rnd(0.5)
												    Else
												        Damage1033RU(20 + (Rand(5) * SelectedDifficulty\aggressiveNPCs))
												        n\HP = n\HP - 30
												        If n\HP =< 0 Then
							                                n\IsDead = True
							                            EndIf
												    EndIf
											    Else
												    BlurTimer = 100
												    PlaySound_Strict DamageSFX(Rand(2,3))
											        If I_1033RU\HP = 0
												        Injuries = Injuries+Rnd(1.0,1.5)
												    Else
												        Damage1033RU(20)
												        n\HP = n\HP - 34
												        If n\HP =< 0 Then
							                                n\IsDead = True
							                            EndIf
												    EndIf
												    If Injuries > 3.0 Then
													    If PlayerRoom\RoomTemplate\Name = "room2offices" Then
														    DeathMSG = Chr(34)+"One large and highly active tentacle-like appendage seems "
														    DeathMSG = DeathMSG + "to have grown outside the dead body of a scientist within office area [DATA REDACTED]. It's level of aggression is "
														    DeathMSG = DeathMSG + "unlike anything we've seen before - it looks like it has "
														    DeathMSG = DeathMSG + "beaten some unfortunate Class D to death at some point during the breach."+Chr(34)
													    Else
														    DeathMSG = Chr(34)+"We will need more than the regular cleaning team to take care of this. "
														    DeathMSG = DeathMSG + "Two large and highly active tentacle-like appendages seem "
														    DeathMSG = DeathMSG + "to have formed inside the chamber. Their level of aggression is "
														    DeathMSG = DeathMSG + "unlike anything we've seen before - it looks like they have "
														    DeathMSG = DeathMSG + "beaten some unfortunate Class D to death at some point during the breach."+Chr(34)
													    EndIf
													    Kill(True)
												    EndIf
											    EndIf
											
										    EndIf
									    EndIf
									
									    n\Frame = 6
									    ;SetAnimTime(n\obj, 6)
								    ElseIf n\Frame=32
									    n\State = 1
									    n\Frame = 173
									    ;SetAnimTime(n\obj, 173)
								    EndIf
							    EndIf
							
					    End Select
					Else
					    ;AnimateNPC(n, 390, 514, 0.15, False)
					    AnimateNPC(n, 515, 551, 0.15, False)
					    If n\Frame >= 551 Then
				            HideEntity n\obj
				            HideEntity n\Collider
				        EndIf
					EndIf
				EndIf
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
				RotateEntity n\obj, EntityPitch(n\Collider)-90, EntityYaw(n\Collider)-180, EntityRoll(n\Collider), True
				
				n\DropSpeed = 0
				
				ResetEntity n\Collider
				;[End Block]
			Case NPCtype860_2
				;[Block]
				If PlayerRoom\RoomTemplate\Name = "room860" Then
					Local fr.Forest=PlayerRoom\fr;Object.Forest(e\room\Objects[1])
					
					dist = EntityDistance(Collider,n\Collider)
					
					If ForestNPC<>0
						If ForestNPCData[2]=1
							ShowEntity ForestNPC
							If n\State<>1
								If (BlinkTimer<-8 And BlinkTimer >-12) Or (Not EntityInView(ForestNPC,Camera))
									ForestNPCData[2]=0
									HideEntity ForestNPC
								EndIf
							EndIf
						Else
							HideEntity ForestNPC
						EndIf
					EndIf
					
					Select n\State
						Case 0 ;idle (hidden)
							
							HideEntity n\Collider
							HideEntity n\obj
							HideEntity n\obj2
							
							n\State2 = 0
							PositionEntity(n\Collider, 0, -100, 0)
						Case 1 ;appears briefly behind the trees
							n\DropSpeed = 0
							
							If EntityY(n\Collider)<= -100 Then
								;transform the position of the player to the local coordinates of the forest
								TFormPoint(EntityX(Collider),EntityY(Collider),EntityZ(Collider),0,fr\Forest_Pivot)
								
								;calculate the indices of the forest cell the player is in
								x = Floor((TFormedX()+6.0)/12.0)
								z = Floor((TFormedZ()+6.0)/12.0)
								
								;step through nearby cells
								For x2 = Max(x-1,1) To Min(x+1,gridsize) Step 2
									For z2 = Max(z-1,1) To Min(z+1,gridsize) Step 2
										;choose an empty cell (not on the path)
										If fr\grid[(z2*gridsize)+x2]=0 Then
											;spawn the monster between the empty cell and the cell the player is in
											TFormPoint(((x2+x)/2)*12.0,0,((z2+z)/2)*12.0,fr\Forest_Pivot,0)
											
											;in view -> nope, keep searching for a more suitable cell
											If EntityInView(n\Collider, Camera) Then
												PositionEntity n\Collider, 0, -110, 0
											Else ; not in view -> all good
												
												PositionEntity n\Collider, TFormedX(), EntityY(fr\Forest_Pivot,True)+2.3, TFormedZ()
												
												x2 = gridsize
												Exit												
											EndIf
										EndIf
									Next
								Next
								
								If EntityY(n\Collider)> -100 Then
									PlaySound2(Step2SFX(Rand(3,5)), Camera, n\Collider, 15.0, 0.5)
									
									If ForestNPCData[2]<>1 Then ForestNPCData[2]=0
									
									Select Rand(3)
										Case 1
											PointEntity n\Collider, Collider
											n\Frame = 2
											;SetAnimTime(n\obj, 2)
										Case 2
											PointEntity n\Collider, Collider
											n\Frame = 201
											;SetAnimTime(n\obj, 201)
										Case 3
											PointEntity n\Collider, Collider
											TurnEntity n\Collider, 0, 90, 0
											n\Frame = 299
											;SetAnimTime(n\obj, 299)
									End Select
									
									n\State2 = 0
								EndIf
							Else
								
								ShowEntity n\obj
								ShowEntity n\Collider
								
								PositionEntity n\Collider, EntityX(n\Collider), EntityY(fr\Forest_Pivot,True)+2.3, EntityZ(n\Collider)
								
								;[TODO]
								If ForestNPC<>0
									If ForestNPCData[2]=0
										Local docchance% = 0
										Local docamount% = 0
										For i = 0 To MaxItemAmount-1
											If Inventory(i)<>Null
												Local docname$ = Inventory(i)\itemtemplate\name
												If docname = "Log #1" Or docname = "Log #2" Or docname = "Log #3"
													;860,850,830,800
													docamount% = docamount% + 1
													docchance = docchance + 10*docamount%
												EndIf
											EndIf
										Next
										
										If Rand(1,860-docchance)=1
											ShowEntity ForestNPC
											ForestNPCData[2]=1
											If Rand(2)=1
												ForestNPCData[0]=0
											Else
												ForestNPCData[0]=2
											EndIf
											ForestNPCData[1]=0
											PositionEntity ForestNPC,EntityX(n\Collider),EntityY(n\Collider)+0.5,EntityZ(n\Collider)
											RotateEntity ForestNPC,0,EntityYaw(n\Collider),0
											MoveEntity ForestNPC,0.75,0,0
											RotateEntity ForestNPC,0,0,0
											EntityTexture ForestNPC, at\OtherTextureID[2], ForestNPCData[0]
										Else
											ForestNPCData[2]=2
										EndIf
									ElseIf ForestNPCData[2]=1
										If ForestNPCData[1]=0.0
											If Rand(200)=1
												ForestNPCData[1]=fs\FPSfactor[0]
												EntityTexture ForestNPC, at\OtherTextureID[2], ForestNPCData[0]+1
											EndIf
										ElseIf ForestNPCData[1]>0.0 And ForestNPCData[1]<5.0
											ForestNPCData[1]=Min(ForestNPCData[1]+fs\FPSfactor[0],5.0)
										Else
											ForestNPCData[1]=0
											EntityTexture ForestNPC, at\OtherTextureID[2], ForestNPCData[0]
										EndIf
									EndIf
								EndIf
								
								If n\State2 = 0 Then ;don't start moving until the player is looking
									If EntityInView(n\Collider, Camera) Then 
										n\State2 = 1
										If Rand(8)=1 Then
											PlaySound2(LoadTempSound(SFXPath$+"SCP\860\Cancer"+Rand(0,2)+".ogg"), Camera, n\Collider, 20.0)
										EndIf										
									EndIf
								Else
									If n\Frame<=199 Then
										AnimateNPC(n, 2, 199, 0.5,False)
										If n\Frame=199 Then n\Frame = 298 : PlaySound2(Step2SFX(Rand(3,5)), Camera, n\Collider, 15.0)
									ElseIf n\Frame <= 297
										PointEntity n\Collider, Collider
										
										AnimateNPC(n, 200, 297, 0.5, False)
										If n\Frame=297 Then n\Frame=298 : PlaySound2(Step2SFX(Rand(3,5)), Camera, n\Collider, 15.0)
									Else
										angle = CurveAngle(point_direction(EntityX(n\Collider),EntityZ(n\Collider),EntityX(Collider),EntityZ(Collider)),EntityYaw(n\Collider)+90,20.0)
										
										RotateEntity n\Collider, 0, angle-90, 0, True
										
										AnimateNPC(n, 298, 316, n\CurrSpeed*10)
										
										;Animate2(n\obj, AnimTime(n\obj), 298, 316, n\CurrSpeed*10)
										
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
										MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]
										
										If dist>15.0 Then
											PositionEntity n\Collider, 0,-110,0
											n\State = 0
											n\State2 = 0
										EndIf
									EndIf									
								EndIf
								
							EndIf
							
							ResetEntity n\Collider
						Case 2 ;appears on the path and starts to walk towards the player
							ShowEntity n\obj
							ShowEntity n\Collider
							
							prevFrame = n\Frame
							
							If EntityY(n\Collider)<= -100 Then
								;transform the position of the player to the local coordinates of the forest
								TFormPoint(EntityX(Collider),EntityY(Collider),EntityZ(Collider),0,fr\Forest_Pivot)
								
								;calculate the indices of the forest cell the player is in
								x = Floor((TFormedX()+6.0)/12.0)
								z = Floor((TFormedZ()+6.0)/12.0)
								
								For x2 = Max(x-1,1) To Min(x+1,gridsize)
									For z2 = Max(z-1,1) To Min(z+1,gridsize)
										;find a nearby cell that's on the path and NOT the cell the player is in
										If fr\grid[(z2*gridsize)+x2]>0 And (x2<>x Or z2<>z) And (x2=x Or z2=z) Then
											
											;transform the position of the cell back to world coordinates
											TFormPoint(x2*12.0, 0,z2*12.0, fr\Forest_Pivot,0)
											
											PositionEntity n\Collider, TFormedX(), EntityY(fr\Forest_Pivot,True)+1.0,TFormedZ()
											
											If EntityInView(n\Collider, Camera) Then
												BlinkTimer=-10
											Else
												x2 = gridsize
												Exit
											EndIf
										EndIf
									Next
								Next
							Else
								angle = CurveAngle(Find860Angle(n, fr),EntityYaw(n\Collider)+90,80.0)
								
								RotateEntity n\Collider, 0, angle-90, 0, True
								
								n\CurrSpeed = CurveValue(n\Speed*0.3, n\CurrSpeed, 50.0)
								MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]
								
								AnimateNPC(n, 494, 569, n\CurrSpeed*25)
								
								If n\State2 = 0 Then
									If dist<8.0 Then
										If EntityInView(n\Collider,Camera) Then
											PlaySound_Strict LoadTempSound(SFXPath$+"SCP\860\Chase"+Rand(1,2)+".ogg")
											
											PlaySound2(LoadTempSound(SFXPath$+"SCP\860\Cancer"+Rand(0,2)+".ogg"), Camera, n\Collider)	
											n\State2 = 1
										EndIf										
									EndIf
								EndIf
								
								If CurrSpeed > 0.03 Then ;the player is running
									n\State3 = n\State3 + fs\FPSfactor[0]
									If Rnd(5000)<n\State3 Then
										temp = True
										If n\SoundCHN <> 0 Then
											If ChannelPlaying (n\SoundCHN) Then temp = False
										EndIf
										If temp Then
											n\SoundCHN = PlaySound2(LoadTempSound(SFXPath$+"SCP\860\Cancer"+Rand(0,2)+".ogg"), Camera, n\Collider)
										EndIf
									EndIf
								Else
									n\State3 = Max(n\State3 - fs\FPSfactor[0],0)
								EndIf
								
								If dist<4.5 Or n\State3 > Rnd(200,250) Then
									n\SoundCHN = PlaySound2(LoadTempSound(SFXPath$+"SCP\860\Cancer"+Rand(3,5)+".ogg"), Camera, n\Collider)
									n\State = 3
								EndIf
								
								If dist > 20.0 Then
									n\State = 0
									n\State2 = 0
									PositionEntity n\Collider, 0,-110,0
								EndIf
							EndIf
							
							;535, 568
							If (prevFrame < 533 And n\Frame=>533) Or (prevFrame > 568 And n\Frame<2) Then
								PlaySound2(Step2SFX(Rand(3,5)), Camera, n\Collider, 15.0, 0.6)
							EndIf
							
						Case 3 ;runs towards the player and attacks
							ShowEntity n\obj
							ShowEntity n\Collider
							
							prevFrame = n\Frame
							
							angle = CurveAngle(Find860Angle(n, fr),EntityYaw(n\Collider)+90,40.0)
							
							RotateEntity n\Collider, 0, angle-90, 0, True
							
							If n\Sound = 0 Then n\Sound = LoadSound_Strict(SFXPath$+"General\Slash1.ogg")
							If n\Sound2 = 0 Then n\Sound2 = LoadSound_Strict(SFXPath$+"General\Slash2.ogg")
							
							;if close enough to attack OR already attacking, play the attack anim
							If (dist<1.1 Or (n\Frame>451 And n\Frame<493) Or KillTimer < 0) Then
								DeathMSG = ""
								
								n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
								
								AnimateNPC(n, 451,493, 0.5, False)
								
								;Animate2(n\obj, AnimTime(n\obj), 451,493, 0.5, False)
								If (prevFrame < 461 And n\Frame=>461) Then 
									If KillTimer => 0 Then Kill(True) : KillAnim = 0
									PlaySound_Strict(n\Sound)
								EndIf
								If (prevFrame < 476 And n\Frame=>476) Then PlaySound_Strict(n\Sound2)
								If (prevFrame < 486 And n\Frame=>486) Then PlaySound_Strict(n\Sound2)
							Else
								n\CurrSpeed = CurveValue(n\Speed*0.8, n\CurrSpeed, 10.0)
								
								AnimateNPC(n, 298, 316, n\CurrSpeed*10)
								;Animate2(n\obj, AnimTime(n\obj), 298, 316, n\CurrSpeed*10)
								
								If (prevFrame < 307 And n\Frame=>307) Then
									PlaySound2(Step2SFX(Rand(3,5)), Camera, n\Collider, 10.0)
								EndIf
							EndIf
							
							MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]
					End Select
					
					If n\State <> 0 Then
						RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0, True	
						
						PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider)-0.1, EntityZ(n\Collider))
						RotateEntity n\obj, EntityPitch(n\Collider)-90, EntityYaw(n\Collider), EntityRoll(n\Collider), True
						
						If dist > 8.0 Then
							ShowEntity n\obj2
							EntityAlpha n\obj2, Min(dist-8.0,1.0)
							
							PositionEntity(n\obj2, EntityX(n\obj), EntityY(n\obj) , EntityZ(n\obj))
							RotateEntity(n\obj2, 0, EntityYaw(n\Collider) - 180, 0)
							MoveEntity(n\obj2, 0, 30.0*0.025, -33.0*0.025)
							
							;render distance is set to 8.5 inside the forest,
							;so we need to cheat a bit to make the eyes visible if they're further than that
							pvt = CreatePivot()
							PositionEntity pvt, EntityX(Camera),EntityY(Camera),EntityZ(Camera)
							PointEntity pvt, n\obj2
							MoveEntity pvt, 0,0,8.0
							PositionEntity n\obj2, EntityX(pvt),EntityY(pvt),EntityZ(pvt)
							FreeEntity pvt
						Else
							HideEntity n\obj2
						EndIf
					EndIf
				EndIf
				;[End Block] 
			Case NPCtype939
				;[Block]
				
				If PlayerRoom\RoomTemplate\Name <> "room3storage"
					n\State = 66
				EndIf
				
				;state is set to 66 in the room3storage-event if player isn't inside the room
				If n\State < 66 Then 
					Select n\State
						Case 0
							AnimateNPC(n, 290,405,0.1)
							
							;Animate2(n\obj,AnimTime(n\obj),290,405,0.1)
						Case 1
							
							If n\Frame=>644 And n\Frame<683 Then ;finish the walking animation
								;n\CurrSpeed = CurveValue(n\Speed*0.2, n\CurrSpeed, 10.0)
								n\CurrSpeed = CurveValue(n\Speed*0.05, n\CurrSpeed, 10.0)
								AnimateNPC(n, 644,683,28*n\CurrSpeed*4,False)
								If n\Frame=>682 Then n\Frame =175
								
								;Animate2(n\obj,AnimTime(n\obj),644,683,28*n\CurrSpeed,False)
								;If AnimTime(n\obj)=683 Then SetAnimTime(n\obj,175)
							Else
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 5.0)
								AnimateNPC(n, 175,297,0.22,False)
								If n\Frame=>296 Then n\State = 2
								
								;Animate2(n\obj,AnimTime(n\obj),175,297,0.22,False)
								;If AnimTime(n\obj)=297 Then n\State = 2
							EndIf
							
							n\LastSeen = 0
							
							MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]						
							
						Case 2
							n\State2 = Max(n\State2, (n\PrevState-3))
							
							dist = EntityDistance(n\Collider, PlayerRoom\Objects[n\State2])
							
							n\CurrSpeed = CurveValue(n\Speed*0.3*Min(dist,1.0), n\CurrSpeed, 10.0)
							MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0] 
							
							prevFrame = n\Frame
							AnimateNPC(n, 644,683,28*n\CurrSpeed) ;walk
							
							;prevFrame = AnimTime(n\obj)
							;Animate2(n\obj,AnimTime(n\obj),644,683,28*n\CurrSpeed) ;walk
							
							If (prevFrame<664 And n\Frame=>664) Or (prevFrame>673 And n\Frame<654) Then
								PlaySound2(StepSFX(4, 0, Rand(0,3)), Camera, n\Collider, 12.0)
								If Rand(10)=1 Then
									temp = False
									If n\SoundCHN = 0 Then 
										temp = True
									ElseIf Not ChannelPlaying(n\SoundCHN)
										temp = True
									EndIf
									If temp Then
										If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
										n\Sound = LoadSound_Strict(SFXPath$+"SCP\939\"+(n\ID Mod 3)+"Lure"+Rand(1,10)+".ogg")
										n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider)
									EndIf
								EndIf
							EndIf
							
							PointEntity n\obj, PlayerRoom\Objects[n\State2]
							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0), 0
							
							If dist<0.4 Then
								n\State2 = n\State2 + 1
								If n\State2 > n\PrevState Then n\State2 = (n\PrevState-3)
								n\State = 1
							EndIf
							
						Case 3
							If EntityVisible(Collider, n\Collider) Then
								If n\Sound2 = 0 Then n\Sound2 = LoadSound_Strict(SFXPath$+"General\Slash1.ogg")
								
								n\EnemyX = EntityX(Collider)
								n\EnemyZ = EntityZ(Collider)
								n\LastSeen = 10*7
							EndIf
							
							If n\LastSeen > 0 And (Not chs\NoTarget) Then
								prevFrame = n\Frame
								
								If (n\Frame=>18.0 And n\Frame<68.0) Then
									n\CurrSpeed = CurveValue(0, n\CurrSpeed, 5.0)
									AnimateNPC(n, 18,68,0.5,True)
									;Animate2(n\obj,AnimTime(n\obj),18,68,0.5,True)
									
									;hasn't hit
									temp = False
									
									If prevFrame < 24 And n\Frame>=24 Then
										temp = True
									ElseIf prevFrame < 57 And n\Frame>=57
										temp = True
									EndIf
									
									If temp Then
										If Distance(n\EnemyX, n\EnemyZ, EntityX(n\Collider), EntityZ(n\Collider))<1.5 Then
										    PlaySound_Strict n\Sound2
										    BlurTimer = 500
										    If I_1033RU\HP = 0
											    Injuries = Injuries + Rnd(1.5, 2.5)-WearingVest*0.5
											Else
										        Damage1033RU(20+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
                                            EndIf		
										Else
											n\Frame	 = 449
											;SetAnimTime(n\obj, 449)
										EndIf
									EndIf
									
									If Injuries>4.0 Then 
										DeathMSG=Chr(34)+"All four (4) escaped SCP-939 specimens have been captured and recontained successfully. "
										DeathMSG=DeathMSG+"Four (4) of them made quite a mess at Storage Area 6. A cleaning team has been dispatched."+Chr(34)
										Kill(True)
										If (Not chs\GodMode) Then n\State = 5
									EndIf								
								Else
									If n\LastSeen = 10*7 Then 
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										
										AnimateNPC(n, 449,464,6*n\CurrSpeed) ;run
										;Animate2(n\obj,AnimTime(n\obj),449,464,6*n\CurrSpeed) ;run
										
										If (prevFrame<452 And n\Frame=>452) Or (prevFrame<459 And n\Frame=>459) Then
											PlaySound2(StepSFX(1, 1, Rand(0,7)), Camera, n\Collider, 12.0)
										EndIf										
										
										If Distance(n\EnemyX, n\EnemyZ, EntityX(n\Collider), EntityZ(n\Collider))<1.1 Then ;player is visible
											n\Frame = 18
											;SetAnimTime(n\obj, 18.0)
										EndIf
									Else
										n\CurrSpeed = CurveValue(0, n\CurrSpeed, 5.0)
										AnimateNPC(n, 175,297,5*n\CurrSpeed,True)	
										;Animate2(n\obj,AnimTime(n\obj),175,297,5*n\CurrSpeed,True)
									EndIf
									
								EndIf
								
								angle = VectorYaw(n\EnemyX-EntityX(n\Collider), 0.0, n\EnemyZ-EntityZ(n\Collider))
								RotateEntity n\Collider, 0, CurveAngle(angle,EntityYaw(n\Collider),15.0), 0									
								
								MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]							
								
								n\LastSeen = n\LastSeen - fs\FPSfactor[0]
							Else
								n\State = 2
							EndIf
							
						;Animate2(n\obj,AnimTime(n\obj),406,437,0.1) ;leap
						Case 5
							If n\Frame<68 Then
								AnimateNPC(n, 18,68,0.5,False) ;finish the attack animation
								
								;Animate2(n\obj,AnimTime(n\obj),18,68,0.5,False) ;finish the attack animation
							Else
								AnimateNPC(n, 464,473,0.5,False) ;attack to idle
								
								;Animate2(n\obj,AnimTime(n\obj),464,473,0.5,False) ;attack to idle
							EndIf
							
					End Select
					
					If n\State < 3 And (Not chs\NoTarget) And (Not n\IgnorePlayer) Then
						dist = EntityDistance(n\Collider, Collider)
						
						If dist < 4.0 Then dist = dist - EntityVisible(Collider, n\Collider)
						If PlayerSoundVolume*1.2>dist Or dist < 1.5 Then
							If n\State3 = 0 Then
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"SCP\939\"+(n\ID Mod 3)+"Attack"+Rand(1,3)+".ogg")
								n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider)										
								
								PlaySound_Strict(LoadTempSound(SFXPath$+"SCP\939\Attack.ogg"))
								n\State3 = 1
							EndIf
							
							n\State = 3
						ElseIf PlayerSoundVolume*1.6>dist
							If n\State<>1 And n\Reload <= 0 Then
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"SCP\939\"+(n\ID Mod 3)+"Alert"+Rand(1,3)+".ogg")
								n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider)	
								
								n\Frame = 175
								n\Reload = 70 * 3
								;SetAnimTime(n\obj, 175)	
							EndIf
							
							n\State = 1
							
						EndIf
						
						n\Reload = n\Reload - fs\FPSfactor[0]
						
					EndIf				
					
					RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0, True	
					
					PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider)-0.28, EntityZ(n\Collider))
					RotateEntity n\obj, EntityPitch(n\Collider)-90, EntityYaw(n\Collider), EntityRoll(n\Collider), True					
				EndIf
				;[End Block]
			Case NPCtype066
				;[Block]
				dist = Distance(EntityX(Collider),EntityZ(Collider),EntityX(n\Collider),EntityZ(n\Collider))
				
				If EntityDistance(Collider, n\Collider) < 6.0 Then GiveAchievement(Achv066)
				
				Select n\State
					Case 0 
						;idle: moves around randomly from waypoint to another if the player is far enough
						;starts staring at the player when the player is close enough
						
						If dist > 20.0 Then
							AnimateNPC(n, 451, 612, 0.2, True)
							;Animate2(n\obj, AnimTime(n\obj), 451, 612, 0.2, True)
							
							If n\State2 < MilliSecs2() Then
								For w.waypoints = Each WayPoints
									If w\door = Null Then
										If Abs(EntityX(w\obj,True)-EntityX(n\Collider))<4.0 Then
											If Abs(EntityZ(w\obj,True)-EntityZ(n\Collider))<4.0 Then
												PositionEntity n\Collider, EntityX(w\obj,True), EntityY(w\obj,True)+0.3, EntityZ(w\obj,True)
												ResetEntity n\Collider
												Exit
											EndIf
										EndIf
									EndIf
								Next
								n\State2 = MilliSecs2()+5000
							EndIf
						ElseIf dist < 8.0
							n\LastDist = Rnd(1.0, 2.5)
							n\State = 1
						EndIf
					Case 1 ;staring at the player
						
						If n\Frame<451 Then
							angle = WrapAngle(CurveAngle(DeltaYaw(n\Collider, Collider)-180, (AnimTime(n\obj)-2.0)/1.2445, 15.0))
							;0->360 = 2->450
							SetNPCFrame(n,angle*1.2445+2.0)
							
							;SetAnimTime(n\obj, angle*1.2445+2.0)							
						Else
							AnimateNPC(n, 636, 646, 0.4, False)
							If n\Frame = 646 Then SetNPCFrame(n,2)
							;Animate2(n\obj, AnimTime(n\obj), 636, 646, 0.4, False)
							;If AnimTime(n\obj)=646 Then SetAnimTime (n\obj, 2)
						EndIf
						dist = Distance(EntityX(Collider),EntityZ(Collider),EntityX(n\Collider),EntityZ(n\Collider))
						
						If Rand(700)=1 Then PlaySound2(LoadTempSound(SFXPath$+"SCP\066\Eric"+Rand(1,3)+".ogg"),Camera, n\Collider, 8.0)
						
						If dist < 1.0+n\LastDist Then n\State = Rand(2,3)
					Case 2 ;roll towards the player and make a sound, and then escape	
						If n\Frame < 647 Then 
							angle = CurveAngle(0, (AnimTime(n\obj)-2.0)/1.2445, 5.0)
							
							If angle < 5 Or angle > 355 Then 
								SetNPCFrame(n,647)
							Else
								SetNPCFrame(n,angle*1.2445+2.0)
							EndIf
							;SetAnimTime(n\obj, angle*1.2445+2.0)
							;If angle < 5 Or angle > 355 Then SetAnimTime(n\obj, 647)
						Else
							If n\Frame=683 Then 
								If n\State2 = 0 Then
									If Rand(2)=1 Then
										PlaySound2(LoadTempSound(SFXPath$+"SCP\066\Eric"+Rand(1,3)+".ogg"),Camera, n\Collider, 8.0)
									Else
										PlaySound2(LoadTempSound(SFXPath$+"SCP\066\Notes"+Rand(1,6)+".ogg"), Camera, n\Collider, 8.0)
									EndIf									
									
									Select Rand(1,6)
										Case 1
											If n\Sound2=0 Then n\Sound2=LoadSound_Strict(SFXPath$+"SCP\066\Beethoven.ogg")
											n\SoundCHN2 = PlaySound2(n\Sound2, Camera, n\Collider)
											DeafTimer# = 70*(45+(15*SelectedDifficulty\aggressiveNPCs))
											DeafPlayer = True
											CameraShake = 10.0
										Case 2
											n\State3 = Rand(700,1400)
										Case 3
											For d.Doors = Each Doors
												If d\locked = False And d\KeyCard = 0 And d\Code = "" Then
													If Abs(EntityX(d\frameobj)-EntityX(n\Collider))<16.0 Then
														If Abs(EntityZ(d\frameobj)-EntityZ(n\Collider))<16.0 Then
															UseDoor(d, False)
														EndIf
													EndIf
												EndIf
											Next
										Case 4
											If PlayerRoom\RoomTemplate\DisableDecals = False Then
												CameraShake = 5.0
												de.Decals = CreateDecal(1, EntityX(n\Collider), 0.01, EntityZ(n\Collider), 90, Rand(360), 0)
												de\Size = 0.3 : UpdateDecals
												PlaySound_Strict(LoadTempSound(SFXPath$+"General\BodyFall.ogg"))
												If Distance(EntityX(Collider),EntityZ(Collider),EntityX(n\Collider),EntityZ(n\Collider))<0.8 Then
												    If I_1033RU\HP = 0
													    Injuries = Injuries + Rnd(0.3,0.5)
													Else
													    Damage1033RU(25 + (Rand(5) * SelectedDifficulty\aggressiveNPCs))
													EndIf
												EndIf
											EndIf
									End Select
								EndIf
								
								n\State2 = n\State2+fs\FPSfactor[0]
								If n\State2>70 Then 
									n\State = 3
									n\State2 = 0
								EndIf
							Else
								n\CurrSpeed = CurveValue(n\Speed*1.5, n\CurrSpeed, 10.0)
								PointEntity n\obj, Collider
								;angle = CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10);1.0/Max(n\CurrSpeed,0.0001))
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj)-180, EntityYaw(n\Collider), 10), 0
								
								AnimateNPC(n, 647, 683, n\CurrSpeed*25, False)
								;Animate2(n\obj, AnimTime(n\obj), 647, 683, n\CurrSpeed*25, False)
								
								MoveEntity n\Collider, 0,0,-n\CurrSpeed*fs\FPSfactor[0]
								
							EndIf
						EndIf
					Case 3
						PointEntity n\obj, Collider
						angle = CurveAngle(EntityYaw(n\obj)+n\Angle-180, EntityYaw(n\Collider), 10);1.0/Max(n\CurrSpeed,0.0001))
						RotateEntity n\Collider, 0, angle, 0
						
						n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
						MoveEntity n\Collider, 0,0,n\CurrSpeed*fs\FPSfactor[0]
						
						;Animate2(n\obj, AnimTime(n\obj), 684, 647, -n\CurrSpeed*25)
						
						If Rand(100)=1 Then n\Angle = Rnd(-20,20)
						
						n\State2 = n\State2 + fs\FPSfactor[0]
						If n\State2>250 Then 
							AnimateNPC(n, 684, 647, -n\CurrSpeed*25, False)
							;Animate2(n\obj, AnimTime(n\obj), 684, 647, -n\CurrSpeed*25, False)
							If n\Frame=647 Then 
								n\State = 0
								n\State2=0
							EndIf
						Else
							AnimateNPC(n, 684, 647, -n\CurrSpeed*25)
							
							;Animate2(n\obj, AnimTime(n\obj), 684, 647, -n\CurrSpeed*25)
						EndIf
						
				End Select
				
				If n\State > 1 Then
					If n\Sound = 0 Then n\Sound = LoadSound_Strict(SFXPath$+"SCP\066\Rolling.ogg")
					If n\SoundCHN<>0 Then
						If ChannelPlaying(n\SoundCHN) Then
							n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider, 20)
						EndIf
					Else
						n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20)
					EndIf					
				EndIf

				If n\State3 > 0 Then
					n\State3 = n\State3-fs\FPSfactor[0]
					LightVolume = TempLightVolume-TempLightVolume*Min(Max(n\State3/500,0.01),0.6)
					HeartBeatRate = Max(HeartBeatRate, 130)
					HeartBeatVolume = Max(HeartBeatVolume,Min(n\State3/1000,1.0))
				EndIf
				
				If ChannelPlaying(n\SoundCHN2)
					UpdateSoundOrigin2(n\SoundCHN2,Camera,n\Collider,20)
					BlurTimer = Max((5.0-dist)*300,0)
				EndIf
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				
				RotateEntity n\obj, EntityPitch(n\Collider)-90, EntityYaw(n\Collider), 0
				;[End Block]
			Case NPCtype966
				;[Block]
				dist = EntityDistance(n\Collider,Collider)
				
				If n\State > -1 Then
					If (dist<HideDistance) Then
					
						;n\State = the "general" state (idle/wander/attack/echo etc)
						;n\State2 = timer for doing raycasts
					
						prevFrame = n\Frame
					
						If n\Sound > 0 Then
							temp = 0.5
							;the ambient sound gets louder when the npcs are attacking
							If n\State > 0 Then temp = 1.0	
						
							n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, Camera, 10.0,temp)
						EndIf
					
						temp = Rnd(-1.0,1.0)
						PositionEntity n\obj,EntityX(n\Collider,True),EntityY(n\Collider,True)-0.2,EntityZ(n\Collider,True)
						RotateEntity n\obj,-90.0,EntityYaw(n\Collider),0.0
					
						If (WearingNightVision = 0) Then
							HideEntity n\obj
							If dist<1 And n\Reload <= 0 And MsgTimer <= 0 Then
								Select Rand(6)
									Case 1
										Msg="You feel something breathing right next to you."
									Case 2
										Msg=Chr(34)+"It feels like something's in this room with me."+Chr(34)
									Case 3
										Msg="You feel like something is here with you, but you do not see anything."
									Case 4
										Msg=Chr(34)+"Is my mind playing tricks on me or is there someone else here?"+Chr(34)
									Case 5
										Msg="You feel like something is following you."
									Case 6
										Msg="You can feel something near you, but you are unable to see it. Perhaps its time is now."
								End Select
                           		n\Reload = 20*70
								MsgTimer=8*70
							EndIf
							n\Reload = n\Reload - fs\FPSfactor[0]
						Else
							ShowEntity n\obj
						EndIf
					
						If n\State3>5*70 Then
							;n\State = 1
							If n\State3<1000.0 Then
								For n2.NPCs = Each NPCs	
									If n2\NPCtype = n\NPCtype Then n2\State3=1000.0 
								Next
							EndIf
						
							n\State = Max(n\State,8)
							n\State3 = 1000.0					
						EndIf
					
						If Stamina<10 Then 
							n\State3=n\State3+fs\FPSfactor[0]
						Else If n\State3 < 900.0
							n\State3=Max(n\State3-fs\FPSfactor[0]*0.2,0.0)
						EndIf
					
						If n\State <> 10
							n\LastSeen = 0
						EndIf
					
						Select n\State
							Case 0 ;idle, standing
								;If n\Frame>2300.0 Then
								If n\Frame>556.0
									;AnimateNPC(n, 2391, 2416, 1.0, False)	
									;If n\Frame>2415.0 Then SetNPCFrame(n, 201)
									AnimateNPC(n, 628, 652, 0.25, False)
									If n\Frame>651.0 Then SetNPCFrame(n, 2)
								Else
									;AnimateNPC(n, 201, 1015, 1.0, False)
									AnimateNPC(n, 2, 214, 0.25, False)
								
									;echo/stare/walk around periodically
									;If n\Frame>1014.0 Then
									If n\Frame>213.0
										If Rand(3)=1 And dist<4 Then
											n\State = Rand(1,4)
										Else
											n\State = Rand(5,6)								
										EndIf
									EndIf
								
									;echo if player gets close
									If dist<2.0 Then 
										n\State=Rand(1,4)
									EndIf 							
								EndIf
							
								n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 10.0)
							
								MoveEntity n\Collider,0,0,n\CurrSpeed
							
							Case 1,2 ;echo
								;If n\State=1 Then
									;AnimateNPC(n, 1015, 1180, 1.0, False)
									;If n\Frame > 1179.0 Then n\State = 0
								;Else
									;AnimateNPC(n, 1180, 1379, 1.0, False)
									;If n\Frame > 1378.0 Then n\State = 0
								;EndIf
								AnimateNPC(n, 214, 257, 0.25, False)
								If n\Frame > 256.0 Then n\State = 0
							
								;If n\Frame>1029.0 And prevFrame<=1029.0 Or n\Frame>1203.0 And prevFrame<=1203.0 Then
								If n\Frame>228.0 And prevFrame<=228.0
									PlaySound2(LoadTempSound(SFXPath$+"SCP\966\Echo"+Rand(1,3)+".ogg"), Camera, n\Collider)
								EndIf
							
								angle = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
								RotateEntity n\Collider,0.0,CurveAngle(angle,EntityYaw(n\Collider),20.0),0.0
							
								If n\State3<900 Then
									BlurTimer = ((Sin(MilliSecs2()/50)+1.0)*200)/dist
								
									If (WearingNightVision>0) Then GiveAchievement(Achv966)
								
									If (Not I_714\Using) And (WearingGasMask<3) And (WearingHazmat<3) And dist<16 Then
										BlinkEffect = Max(BlinkEffect, 1.5)
										BlinkEffectTimer = 1000
									
										StaminaEffect = 2.0
										StaminaEffectTimer = 1000
									
										If MsgTimer<=0 And StaminaEffect<1.5 Then
											Select Rand(4)
												Case 1
													Msg = "You feel exhausted."
												Case 2
													Msg = Chr(34)+"Could really go for a nap now..."+Chr(34)
												Case 3
													Msg = Chr(34)+"If I wasn't in this situation I would take a nap somewhere."+Chr(34)
												Case 4
													Msg = "You feel restless."
											End Select
										
											MsgTimer = 7*70
										EndIf
									EndIf							
								EndIf
							
							Case 3,4 ;stare at player
								;If n\State=3 Then
									;AnimateNPC(n, 1379.0, 1692.0, 1.0, False)
								
									;If n\Frame>1691.0 Then n\State = 0
								;Else
									;AnimateNPC(n, 1692.0, 2156.0, 1.0, False)
								
									;If n\Frame>2155.0 Then n\State = 0
								;EndIf
								If n\State=3
									AnimateNPC(n, 257, 332, 0.25, False)
									If n\Frame > 331.0 Then n\State = 0
								Else
									AnimateNPC(n, 332, 457, 0.25, False)
									If n\Frame > 456.0 Then n\State = 0
								EndIf
							
								;If n\Frame>1393.0 And prevFrame<=1393.0 Or n\Frame>1589.0 And prevFrame<=1589.0 Or n\Frame>2000.0 And prevFrame<=2000.0 Then
								If n\Frame>271.0 And prevFrame<=271.0 Or n\Frame>354 Or n\Frame>314.0 And prevFrame<=314.0 Or n\Frame>301.0 And prevFrame<=301.0
									PlaySound2(LoadTempSound(SFXPath$+"SCP\966\Idle"+Rand(1,3)+".ogg"), Camera, n\Collider)
								EndIf
							
								angle = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
								RotateEntity n\Collider,0.0,CurveAngle(angle,EntityYaw(n\Collider),20.0),0.0
							Case 5,6,8 ;walking or chasing
								;If n\Frame<2343.0 Then
								If n\Frame < 580.0 And n\Frame > 214.0 ;start walking
									;AnimateNPC(n, 2319, 2343, 0.5, False)
									AnimateNPC(n, 556, 580, 0.25, False)
								Else
									;AnimateNPC(n, 2343, 2391, n\CurrSpeed*25.0)
									If n\CurrSpeed > 0 ;>= 0.005 Then
										AnimateNPC(n, 580, 628, n\CurrSpeed*25.0)
									Else
										AnimateNPC(n, 2, 214, 0.25)
									EndIf
								
									;chasing the player
									If n\State = 8 And dist<32 And (Not chs\Notarget) Then
										If n\PathTimer <= 0 Then
											n\PathStatus = FindPath(n, EntityX(Collider,True), EntityY(Collider,True), EntityZ(Collider,True))
											n\PathTimer = 40*10
											n\CurrSpeed = 0
										EndIf
										n\PathTimer = Max(n\PathTimer-fs\FPSfactor[0],0)
									
										If (Not EntityVisible(n\Collider,Collider)) Then
											If n\PathStatus = 2 Then
												n\CurrSpeed = 0
												;SetNPCFrame(n,201)
												SetNPCFrame(n,2)
											ElseIf n\PathStatus = 1
												If n\Path[n\PathLocation]=Null Then 
													If n\PathLocation > 19 Then 
														n\PathLocation = 0 : n\PathStatus = 0
													Else
														n\PathLocation = n\PathLocation + 1
													EndIf
												Else
													n\Angle = VectorYaw(EntityX(n\Path[n\PathLocation]\obj,True)-EntityX(n\Collider),0,EntityZ(n\Path[n\PathLocation]\obj,True)-EntityZ(n\Collider))
													;RotateEntity n\Collider,0.0,CurveAngle(angle,EntityYaw(n\Collider),10.0),0.0
												
													dist2 = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
												
													temp = True
													If dist2 < 0.8 Then 
														If n\Path[n\PathLocation]\door<>Null Then
															If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
																If (n\Path[n\PathLocation]\door\locked Or n\Path[n\PathLocation]\door\KeyCard<>0 Or n\Path[n\PathLocation]\door\Code<>"") And (Not n\Path[n\PathLocation]\door\open) Then
																	temp = False
																Else
																	If n\Path[n\PathLocation]\door\open = False And (n\Path[n\PathLocation]\door\buttons[0]<>0 Or n\Path[n\PathLocation]\door\buttons[1]<>0) Then
																		UseDoor(n\Path[n\PathLocation]\door, False)
																	EndIf
																EndIf
															EndIf
														EndIf
														If dist2 < 0.3 Then n\PathLocation = n\PathLocation + 1
													EndIf
												
													If temp = False Then
														n\PathStatus = 0
														n\PathLocation = 0
														n\PathTimer = 40*10
													EndIf
												EndIf
											
												n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
											ElseIf n\PathStatus = 0
												n\CurrSpeed = CurveValue(0,n\CurrSpeed,10.0)
											EndIf
										Else
											n\Angle = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
											n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
										
											If dist<1.0 Then n\State=10
										
										EndIf
									Else
										If MilliSecs2() > n\State2 And dist<16.0 Then
											HideEntity n\Collider
											EntityPick(n\Collider, 1.5)
											If PickedEntity() <> 0 Then
												n\Angle = EntityYaw(n\Collider)+Rnd(80,110)
											EndIf
											ShowEntity n\Collider
										
											n\State2=MilliSecs2()+1000
										
											If Rand(5)=1 Then n\State=0
										EndIf	
									
										n\CurrSpeed = CurveValue(n\Speed*0.5, n\CurrSpeed, 20.0)
										
									EndIf
								
									If (prevFrame < 604 And n\Frame=>604) Or (prevFrame < 627 And n\Frame=>627) Then
                                  	  PlaySound2(StepSFX(4,0,Rand(0,3)),Camera, n\Collider, 7.0, Rnd(0.5,0.7))
                               		EndIf
								
									RotateEntity n\Collider, 0, CurveAngle(n\Angle,EntityYaw(n\Collider),10.0),0
								
									MoveEntity n\Collider,0,0,n\CurrSpeed*fs\FPSfactor[0]
								EndIf
							Case 10 ;attack
								If n\LastSeen=0
									PlaySound2(LoadTempSound(SFXPath$+"SCP\966\Echo"+Rand(1,3)+".ogg"), Camera, n\Collider)
									n\LastSeen = 1
								EndIf
							
								;If n\Frame>2300.0 Then
								If n\Frame>557.0
									;AnimateNPC(n, 2391, 2416, 1.0, False
									AnimateNPC(n, 628, 652, 0.25, False)
									;If n\Frame>2415.0 Then
									If n\Frame>651.0
										Select Rand(3)
											Case 1
												;SetNPCFrame(n, 2160)
												SetNPCFrame(n, 458)
											Case 2
												;SetNPCFrame(n, 2192)
												SetNPCFrame(n, 488)
											Case 3
												;SetNPCFrame(n, 2221)
												SetNPCFrame(n, 518)
										End Select
									
									EndIf
								Else
									;If n\Frame <= 2191 Then
										;AnimateNPC(n, 2160, 2191, 0.3, False)
										;If n\Frame > 2190 Then n\State = 8
									;ElseIf n\Frame <= 2220
										;AnimateNPC(n, 2192, 2220, 0.3, False)
										;If n\Frame > 2219 Then n\State = 8
									;ElseIf n\Frame <= 2260
										;AnimateNPC(n, 2221, 2260, 0.3, False)
										;If n\Frame > 2259 Then n\State = 8
									;EndIf
								
									If n\Frame <= 487
										AnimateNPC(n, 458, 487, 0.3, False)
										If n\Frame > 486.0 Then n\State = 8
									ElseIf n\Frame <= 517
										AnimateNPC(n, 488, 517, 0.3, False)
										If n\Frame > 516.0 Then n\State = 8
									ElseIf n\Frame <= 557
										AnimateNPC(n, 518, 557, 0.3, False)
										If n\Frame > 556.0 Then n\State = 8
									EndIf
								EndIf
							
								If dist<1.0 Then
									;If n\Frame>2173.0 And prevFrame<=2173.0 Or n\Frame>2203.0 And prevFrame<=2203.0 Or n\Frame>2227.0 And prevFrame<=2227.0 Then
									If n\Frame>470.0 And prevFrame<=470.0 Or n\Frame>500.0 And prevFrame<=500.0 Or n\Frame>527.0 And prevFrame<=527.0
										PlaySound2(LoadTempSound(SFXPath$+"General\Slash"+Rand(1,2)+".ogg"), Camera, n\Collider)
										If I_1033RU\HP = 0
										    Injuries = Injuries + Rnd(0.5,1.0)
										Else
										    Damage1033RU(20+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
										EndIf								
									EndIf	
								EndIf
							
								n\Angle = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
								RotateEntity n\Collider, 0, CurveAngle(n\Angle,EntityYaw(n\Collider),30.0),0	
						End Select
					Else
						HideEntity n\obj
						If (Rand(600)=1) Then
							TeleportCloser(n)
						EndIf
					EndIf
				EndIf
				
				;[End Block]

			Case NPCtype1048_A
				;[Block]
				Select n\State	
						
					Case 1
						n\PathStatus = FindPath(n, n\EnemyX,n\EnemyY+0.1,n\EnemyZ)
						;649, 677
				End Select
				;[End block]
			Case NPCtype1499_1
				;[Block]
				;n\State: Current State of the NPC
				;n\State2: A second state variable (dependend on the current NPC's n\State)
				;n\State3: Determines if the NPC will always be aggressive against the player
				;n\PrevState: Determines the type/behaviour of the NPC
				;	0 = Normal / Citizen
				;	1 = Stair guard / Guard next to king
				;	2 = King
				;	3 = Front guard
				
				prevFrame# = n\Frame
				
				If (Not n\Idle) And EntityDistance(n\Collider,Collider)<HideDistance*3 Then
					If n\PrevState = 0 Then
						If n\State = 0 Or n\State = 2 Then
							For n2.NPCs = Each NPCs
								If n2\NPCtype = n\NPCtype And n2 <> n Then
									If n2\State <> 0 And n2\State <> 2 Then
										If n2\PrevState = 0 Then
											n\State = 1
											n\State2 = 0
											Exit
										EndIf
									EndIf
								EndIf
							Next
						EndIf
					EndIf
					
					Select n\State
						Case 0
							;[Block]
							If n\PrevState=0 Then
								If n\CurrSpeed = 0.0 Then
									If n\Reload=0 Then
										If n\State2 < 500.0*Rnd(1,3) Then
											n\CurrSpeed = 0.0
											n\State2 = n\State2 + fs\FPSfactor[0]
										Else
											If n\CurrSpeed = 0.0 Then n\CurrSpeed = n\CurrSpeed + 0.0001
										EndIf
									Else
										If n\State2 < 1500 Then
											n\CurrSpeed = 0.0
											n\State2 = n\State2 + fs\FPSfactor[0]
										Else
											If n\Target <> Null Then
												If n\Target\Target <> Null Then
													n\Target\Target = Null
												EndIf
												n\Target\Reload = 0
												n\Target\Angle = n\Target\Angle+Rnd(-45,45)
												n\Target = Null
												n\Reload = 0
												n\Angle = n\Angle+Rnd(-45,45)
											EndIf
											If n\CurrSpeed = 0.0 Then n\CurrSpeed = n\CurrSpeed + 0.0001
										EndIf
									EndIf
								Else
									If n\Target<>Null Then
										n\State2 = 0.0
									EndIf
									
									If n\State2 < 10000.0*Rnd(1,3)
										n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
										n\State2 = n\State2 + fs\FPSfactor[0]
									Else
										n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,50.0)
									EndIf
									
									RotateEntity n\Collider,0,CurveAngle(n\Angle,EntityYaw(n\Collider),10.0),0
									
									If n\Target=Null Then
										If Rand(200) = 1 Then n\Angle = n\Angle + Rnd(-45,45)
										
										HideEntity n\Collider
										EntityPick(n\Collider, 1.5)
										If PickedEntity() <> 0 Then
											n\Angle = EntityYaw(n\Collider)+Rnd(80,110)
										EndIf
										ShowEntity n\Collider
									Else
										n\Angle = EntityYaw(n\Collider) + DeltaYaw(n\Collider,n\Target\Collider)
										If EntityDistance(n\Collider,n\Target\Collider)<1.5 Then
											n\CurrSpeed = 0.0
											n\Target\CurrSpeed = 0.0
											n\Reload = 1
											n\Target\Reload = 1
											temp = Rand(0,2)
											If temp=0 Then
												SetNPCFrame(n,296)
											ElseIf temp=1 Then
												SetNPCFrame(n,856)
											Else
												SetNPCFrame(n,905)
											EndIf
											temp = Rand(0,2)
											If temp=0 Then
												SetNPCFrame(n\Target,296)
											ElseIf temp=1 Then
												SetNPCFrame(n\Target,856)
											Else
												SetNPCFrame(n\Target,905)
											EndIf
										EndIf
									EndIf
								EndIf
							Else
								RotateEntity n\Collider,0,CurveAngle(n\Angle,EntityYaw(n\Collider),10.0),0
							EndIf
							
							If n\CurrSpeed = 0.0
								If n\Reload = 0 And n\PrevState<>2 Then
									AnimateNPC(n,296,320,0.2)
								ElseIf n\Reload = 0 And n\PrevState=2 Then
									;509-533
									;534-601
									If n\Frame <= 532.5 Then
										AnimateNPC(n,509,533,0.2,False)
									ElseIf n\Frame > 533.5 And n\Frame <= 600.5 Then
										AnimateNPC(n,534,601,0.2,False)
									Else
										temp = Rand(0,1)
										If temp=0 Then
											SetNPCFrame(n,509)
										Else
											SetNPCFrame(n,534)
										EndIf
									EndIf
									;SetNPCFrame(n,855)
								Else
									If n\PrevState=2 Then
										AnimateNPC(n,713,855,0.2,False)
										If n\Frame > 833.5 Then
											PointEntity n\obj,Collider
											RotateEntity n\Collider,0,CurveAngle(n\Angle,EntityYaw(n\Collider),10.0),0
										EndIf
									ElseIf n\PrevState=1 Then
										AnimateNPC(n,602,712,0.2,False)
										If n\Frame > 711.5 Then
											n\Reload = 0
										EndIf
									Else
										If n\Frame <= 319.5 Then
											AnimateNPC(n,296,320,0.2,False)
										;856-904
										ElseIf n\Frame > 320.5 And  n\Frame < 903.5 Then
											AnimateNPC(n,856,904,0.2,False)
										;905-953
										ElseIf n\Frame > 904.5 And n\Frame < 952.5 Then
											AnimateNPC(n,905,953,0.2,False)
										Else
											temp = Rand(0,2)
											If temp=0 Then
												SetNPCFrame(n,296)
											ElseIf temp=1 Then
												SetNPCFrame(n,856)
											Else
												SetNPCFrame(n,905)
											EndIf
										EndIf
									EndIf
								EndIf
							Else
								If (n\ID Mod 2 = 0) Then
									AnimateNPC(n,1,62,(n\CurrSpeed*28))
								Else
									AnimateNPC(n,100,167,(n\CurrSpeed*28))
								EndIf
								If n\PrevState = 0 Then
									If n\Target = Null Then
										If Rand(1,1200)=1 Then
											For n2.NPCs = Each NPCs
												If n2<>n Then
													If n2\NPCtype=n\NPCtype Then
														If n2\Target = Null Then
															If n2\PrevState=0 Then
																If EntityDistance(n\Collider,n2\Collider)<20.0 Then
																	n\Target = n2
																	n2\Target = n
																	n\Target\CurrSpeed = n\Target\CurrSpeed + 0.0001
																	Exit
																EndIf
															EndIf
														EndIf
													EndIf
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							EndIf
							
							;randomly play the "screaming animation" and revert back to state 0
							If n\Target=Null And n\PrevState=0 Then
								If (Rand(5000)=1) Then
									n\State = 2
									n\State2 = 0
									
									If Not ChannelPlaying(n\SoundCHN) Then
										dist = EntityDistance(n\Collider,Collider)
										If (dist < 20.0) Then
											If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
											n\Sound = LoadSound_Strict(SFXPath$+"SCP\1499\Idle"+Rand(1,4)+".ogg")
											n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20.0)
										EndIf
									EndIf
								EndIf
								
								If (n\ID Mod 2 = 0) And (Not NoTarget) Then
									dist = EntityDistance(n\Collider,Collider)
									If dist < 10.0 Then
										If EntityVisible(n\Collider,Collider) Then
											;play the "screaming animation"
											n\State = 2
											If dist < 5.0 Then
												If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
												n\Sound = LoadSound_Strict(SFXPath$+"SCP\1499\Triggered.ogg")
												n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider,20.0)
												
												n\State2 = 1 ;if player is too close, switch to attack after screaming
												
												For n2.NPCs = Each NPCs
													;If n2\NPCtype = n\NPCtype And n2 <> n And (n\ID Mod 2 = 0) Then
													If n2\NPCtype = n\NPCtype And n2 <> n
														If n2\PrevState = 0 Then
															n2\State = 1
															n2\State2 = 0
														EndIf
													EndIf
												Next
											Else
												n\State2 = 0 ;otherwise keep idling
											EndIf
											
											n\Frame = 203
										EndIf
									EndIf
								EndIf
							ElseIf n\PrevState=1 Then
								dist = EntityDistance(n\Collider,Collider)
								If (Not NoTarget) Then
									If dist < 4.0 Then
										If EntityVisible(n\Collider,Collider) Then
											If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
											n\Sound = LoadSound_Strict(SFXPath$+"SCP\1499\Triggered.ogg")
											n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider,20.0)
											
											n\State = 1
											
											n\Frame = 203
										EndIf
									EndIf
								EndIf
							EndIf
							;[End Block]
						Case 1 ;attacking the player
							;[Block]
							If NoTarget Then n\State = 0
							
							If PlayerRoom\RoomTemplate\Name = "dimension1499" And n\PrevState=0 Then
								ShouldPlay = 19
							EndIf
							
							PointEntity n\obj,Collider
							RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
							
							dist = EntityDistance(n\Collider,Collider)
							
							If n\State2 = 0.0
								If n\PrevState=1 Then
									n\CurrSpeed = CurveValue(n\Speed*2.0,n\CurrSpeed,10.0)
									If n\Target<>Null Then
										n\Target\State = 1
									EndIf
								Else
									n\CurrSpeed = CurveValue(n\Speed*1.75,n\CurrSpeed,10.0)
								EndIf
								
								If (n\ID Mod 2 = 0) Then
									AnimateNPC(n,1,62,(n\CurrSpeed*28))
								Else
									AnimateNPC(n,100,167,(n\CurrSpeed*28))
								EndIf
							EndIf
							
							If dist < 0.75
								If (n\ID Mod 2 = 0) Or n\State3 = 1 Or n\PrevState=1 Or n\PrevState=3 Or n\PrevState=4 Then
									n\State2 = Rand(1,2)
									n\State = 3
									If n\State2 = 1
										SetNPCFrame(n,63)
									Else
										SetNPCFrame(n,168)
									EndIf
								Else
									n\State = 4
								EndIf
							EndIf
							;[End Block]
						Case 2 ;play the "screaming animation" and switch to n\state2 after it's finished
							;[Block]
							n\CurrSpeed = 0.0
							AnimateNPC(n,203,295,0.1,False)
							
							If n\Frame > 294.0 Then
								n\State = n\State2
							EndIf
							;[End Block]
						Case 3 ;slashing at the player
							;[Block]
							n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,5.0)
							dist = EntityDistance(n\Collider,Collider)
							If n\State2 = 1
								AnimateNPC(n,63,100,0.6,False)
								If prevFrame < 89 And n\Frame=>89
									If dist > 0.85 Or Abs(DeltaYaw(n\Collider,Collider))>60.0
										;Miss
									Else
									    If I_1033RU\HP = 0
										    Injuries = Injuries + Rnd(0.75,1.5)
										Else
										    Damage1033RU(20+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
										EndIf
										PlaySound2(LoadTempSound(SFXPath$+"General\Slash"+Rand(1,2)+".ogg"), Camera, n\Collider)
										If Injuries > 10.0
											Kill(True)
											If PlayerRoom\RoomTemplate\Name$ = "dimension1499"
												DeathMSG = "All personnel situated within Evacuation Shelter LC-2 during the breach have been administered "
												DeathMSG = DeathMSG + "Class B amnestics due to Incident 1499-E. The Class D subject involved in the event "
												DeathMSG = DeathMSG + "died shortly after being shot by Agent [DATA REDACTED]."
											Else
												DeathMSG = "An unidentified male and a deceased Class D subject were discovered in [DATA REDACTED] by the Nine-Tailed Fox. "
												DeathMSG = DeathMSG + "The man was described as highly agitated and seemed to only speak Russian. "
												DeathMSG = DeathMSG + "He's been taken into a temporary holding area at [DATA REDACTED] while waiting for a translator to arrive."
											EndIf
										EndIf
									EndIf
								ElseIf n\Frame => 99
									n\State2 = 0.0
									n\State = 1
								EndIf
							Else
								AnimateNPC(n,168,202,0.6,False)
								If prevFrame < 189 And n\Frame=>189
									If dist > 0.85 Or Abs(DeltaYaw(n\Collider,Collider))>60.0
										;Miss
									Else
									    If I_1033RU\HP = 0
										    Injuries = Injuries + Rnd(0.75,1.5)
										Else
										    Damage1033RU(20+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
										EndIf
										PlaySound2(LoadTempSound(SFXPath$+"General\Slash"+Rand(1,2)+".ogg"), Camera, n\Collider)
										If Injuries > 10.0
											Kill(True)
											If PlayerRoom\RoomTemplate\Name$ = "dimension1499"
												DeathMSG = "All personnel situated within Evacuation Shelter LC-2 during the breach have been administered "
												DeathMSG = DeathMSG + "Class-B amnestics due to Incident 1499-E. The Class D subject involved in the event "
												DeathMSG = DeathMSG + "died shortly after being shot by Agent [DATA REDACTED]."
											Else
												DeathMSG = "An unidentified male and a deceased Class D subject were discovered in [DATA REDACTED] by the See No Evil. "
												DeathMSG = DeathMSG + "The man was described as highly agitated and seemed to only speak Russian. "
												DeathMSG = DeathMSG + "He's been taken into a temporary holding area at [DATA REDACTED] while waiting for a translator to arrive."
											EndIf
										EndIf
									EndIf
								ElseIf n\Frame => 201
									n\State2 = 0.0
									n\State = 1
								EndIf
							EndIf
							;[End Block]
						Case 4 ;standing in front of the player
							;[Block]
							dist = EntityDistance(n\Collider,Collider)
							n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,5.0)
							AnimateNPC(n,296,320,0.2)
							
							PointEntity n\obj,Collider
							RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
							
							If dist > 0.85
								n\State = 1
							EndIf
							;[End Block]
					End Select
					
					If n\SoundCHN <> 0 And ChannelPlaying(n\SoundCHN) Then
						UpdateSoundOrigin(n\SoundCHN,Camera,n\Collider,20.0)
					EndIf
					
					MoveEntity n\Collider,0,0,n\CurrSpeed*fs\FPSfactor[0]
					
					RotateEntity n\obj,0,EntityYaw(n\Collider)-180,0
					PositionEntity n\obj,EntityX(n\Collider),EntityY(n\Collider)-0.2,EntityZ(n\Collider)
					
					ShowEntity n\obj
				Else
					HideEntity n\obj
				EndIf
				
				;[End Block]

			Case NPCtype008_1
				;[Block]
				;n\State: Main State
				;n\State2: A timer used for the player detection
				;n\State3: A timer for making the NPC idle (if the player escapes during that time)
				
				If (Not n\IsDead)
					If n\State = 0
						EntityType n\Collider,HIT_DEAD
					Else
						EntityType n\Collider,HIT_PLAYER
					EndIf
					
					prevFrame = n\Frame
					
					n\BlinkTimer = 1
					
					Select n\State
						Case 0 ;Lying next to the wall
							SetNPCFrame(n,11)
						Case 1 ;Standing up
							AnimateNPC(n,11,32,0.1,False)
							If n\Frame => 29
								n\State = 2
							EndIf
						Case 2 ;Being active
							PlayerSeeAble = MeNPCSeesPlayer(n)
							If PlayerSeeAble=1 Or n\State2 > 0.0
								If PlayerSeeAble=1
									n\State2 = 70*2
								Else
									n\State2 = Max(n\State2-fs\FPSfactor[0],0)
								EndIf
								PointEntity n\obj, Collider
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
								
								AnimateNPC(n, 64, 93, n\CurrSpeed*30)
								n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
								MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
								
								If EntityDistance(n\Collider,Collider)<1.0
									If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
										n\State = 3
									EndIf
								EndIf
								
								n\PathTimer = 0
								n\PathStatus = 0
								n\PathLocation = 0
								n\State3 = 0
							Else
								If n\PathStatus = 1
									If n\Path[n\PathLocation]=Null Then 
										If n\PathLocation > 19 Then 
											n\PathLocation = 0 : n\PathStatus = 0
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										PointEntity n\obj, n\Path[n\PathLocation]\obj
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
										
										AnimateNPC(n, 64, 93, n\CurrSpeed*30)
										n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
										
										;opens doors in front of him
										dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
										If dist2 < 0.6 Then
											temp = True
											If n\Path[n\PathLocation]\door <> Null Then
												If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
													If n\Path[n\PathLocation]\door\locked Or n\Path[n\PathLocation]\door\KeyCard>0 Or n\Path[n\PathLocation]\door\Code<>"" Then
														temp = False
													Else
														If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
													EndIf
												EndIf
											EndIf
											If dist2#<0.2 And temp
												n\PathLocation = n\PathLocation + 1
											ElseIf dist2#<0.5 And (Not temp)
												n\PathStatus = 0
												n\PathTimer# = 0.0
											EndIf
										EndIf
										
										;If Rand(100)=3
										;	n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
										;EndIf
									EndIf
								Else
									AnimateNPC(n, 323, 344, 0.2, True)
									n\CurrSpeed = 0
									If n\PathTimer < 70*5
										n\PathTimer = n\PathTimer + Rnd(1,2+(2*SelectedDifficulty\aggressiveNPCs))*fs\FPSfactor[0]
									Else
										n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
										n\PathTimer = 0
									EndIf
								EndIf
								
								If EntityDistance(n\Collider,Collider)>HideDistance
									If n\State3 < 70*(15+(10*SelectedDifficulty\aggressiveNPCs))
										n\State3 = n\State3+fs\FPSfactor[0]
									Else
										n\State3 = 70*(6*60)
										n\State = 4
									EndIf
								EndIf
							EndIf
							
							If n\CurrSpeed > 0.005 Then
								If (prevFrame < 80 And n\Frame=>80) Or (prevFrame > 92 And n\Frame<65)
									PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))	
								EndIf
							EndIf
							
							n\SoundCHN = LoopSound2(n\Sound,n\SoundCHN,Camera,n\Collider)
						Case 3 ;Attacking
							AnimateNPC(n, 126, 165, 0.4, False)
							If (n\Frame => 146 And prevFrame < 146)
								If EntityDistance(n\Collider,Collider)<1.1
									If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
										PlaySound_Strict DamageSFX(Rand(5,8))
										If (Not WearingHazmat) Then I_008\Timer = I_008\Timer + (1+(1*SelectedDifficulty\aggressiveNPCs))
									    If I_1033RU\HP = 0
										    Injuries = Injuries+Rnd(0.4,1.0)
										Else
											Damage1033RU(20)
											n\HP = n\HP - 24
											If n\HP =< 0 Then
							                    n\IsDead = True
							                EndIf
										EndIf
										DeathMSG = SubjectName$+". Cause of death: multiple lacerations and severe blunt force trauma caused by [DATA REDACTED], who was infected with SCP-008. "
										DeathMSG = DeathMSG + "Said subject was located by Nine-Tailed Fox and terminated."
									EndIf
								EndIf
							ElseIf n\Frame => 164
								If EntityDistance(n\Collider,Collider)<1.1
									If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
										SetNPCFrame(n,126)
									Else
										n\State = 2
									EndIf
								Else
									n\State = 2
								EndIf
							EndIf
						Case 4 ;Idling
							HideEntity n\obj
							HideEntity n\Collider
							n\DropSpeed = 0
							PositionEntity n\Collider,0,500,0
							ResetEntity n\Collider
							If n\Idle > 0
								n\Idle = Max(n\Idle-(1+(1*SelectedDifficulty\aggressiveNPCs))*fs\FPSfactor[0],0)
							Else
								If PlayerInReachableRoom() ;Player is in a room where SCP-008-1 can teleport to
									If Rand(50-(20*SelectedDifficulty\aggressiveNPCs))=1
										ShowEntity n\Collider
										ShowEntity n\obj
										For w.waypoints = Each WayPoints
											If w\door=Null And w\room\dist < HideDistance And Rand(3)=1 Then
												If EntityDistance(w\room\obj,n\Collider)<EntityDistance(Collider,n\Collider)
													x = Abs(EntityX(n\Collider)-EntityX(w\obj,True))
													If x < 12.0 And x > 4.0 Then
														z = Abs(EntityZ(n\Collider)-EntityZ(w\obj,True))
														If z < 12 And z > 4.0 Then
															If w\room\dist > 4
																PositionEntity n\Collider, EntityX(w\obj,True), EntityY(w\obj,True)+0.25,EntityZ(w\obj,True)
																ResetEntity n\Collider
																n\PathStatus = 0
																n\PathTimer# = 0.0
																n\PathLocation = 0
																Exit
															EndIf
														EndIf
													EndIf
												EndIf
											EndIf
										Next
										n\State = 2
										n\State3 = 0
									EndIf
								EndIf
							EndIf
					End Select
				Else
					If n\SoundCHN <> 0
						StopChannel n\SoundCHN
						n\SoundCHN = 0
						FreeSound_Strict n\Sound
						n\Sound = 0
					EndIf
					AnimateNPC(n, 344, 363, 0.5, False)
				EndIf
				
				RotateEntity n\obj,0,EntityYaw(n\Collider)-180,0
				PositionEntity n\obj,EntityX(n\Collider),EntityY(n\Collider)-0.2,EntityZ(n\Collider)
				;[End Block]
			Case NPCtype008_2
				;[Block]
				
				If Abs(EntityY(Collider)-EntityY(n\Collider))<4.0 Then
					
					prevFrame# = n\Frame
					
					If (Not n\IsDead)
						Select n\State
							Case 0
								;[Block]
								AnimateNPC(n, 719, 777, 0.2, False)
								
								If n\Frame=777 Then
									If Rand(700)=1 Then
										If EntityDistance(Collider, n\Collider)<5.0 Then
											n\Frame = 719
										EndIf
									EndIf
								EndIf
								;[End Block]
							Case 1 ;stands up
								;[Block]
								If n\Frame=>682 Then 
									AnimateNPC(n, 926, 935, 0.3, False)
									If n\Frame = 935 Then n\State = 2
									
									;Animate2(n\obj, AnimTime(n\obj), 926, 935, 0.3, False)
									;If AnimTime(n\obj)=935 Then n\State = 2
								Else
									AnimateNPC(n, 155, 682, 1.1, False)
									;Animate2(n\obj, AnimTime(n\obj), 155, 682, 1.5, False)
								EndIf
								;[End Block]
							Case 2 ;following the player
								;[Block]
								If n\State3 < 0 Then ;check if the player is visible every three seconds
									If EntityDistance(Collider, n\Collider)<5.0 Then 
										If EntityVisible(Collider, n\Collider) Then n\State2 = 70*5
									EndIf
									n\State3=70*3
								Else
									n\State3=n\State3-fs\FPSfactor[0]
								EndIf
								
								n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider, 6.0, 0.6)

								If n\State2 > 0 And (Not chs\NoTarget) Then ;player is visible -> attack
									
									n\PathStatus = 0
									
									dist = EntityDistance(Collider, n\Collider)
									
									PointEntity n\obj, Collider
									RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
									
									If dist < 0.7 Then 
										n\State = 3
										If Rand(2)=1 Then
											n\Frame = 2
											;SetAnimTime n\obj, 2
										Else
											n\Frame = 66
											;SetAnimTime n\obj, 66
										EndIf
									Else
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
										
										AnimateNPC(n, 936, 1017, n\CurrSpeed*60)
										
										;Animate2(n\obj, AnimTime(n\obj), 936, 1017, n\CurrSpeed*60)
									EndIf
									
									n\State2=n\State2-fs\FPSfactor[0]
								Else
									If n\PathStatus = 1 Then ;path found
										If n\Path[n\PathLocation]=Null Then 
											If n\PathLocation > 19 Then 
												n\PathLocation = 0 : n\PathStatus = 0
											Else
												n\PathLocation = n\PathLocation + 1
											EndIf
										Else
											PointEntity n\obj, n\Path[n\PathLocation]\obj
											
											RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
											n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
											MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
											
											AnimateNPC(n, 936, 1017, n\CurrSpeed*60)
											;Animate2(n\obj, AnimTime(n\obj), 936, 1017, n\CurrSpeed*60)
											
											AnimateNPC(n, 936, 1017, n\CurrSpeed*60)
											;Animate2(n\obj, AnimTime(n\obj), 936, 1017, n\CurrSpeed*60)
											
											;opens doors in front of him
										    dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
										    If dist2 < 0.6 Then
											    temp = True
											    If n\Path[n\PathLocation]\door <> Null Then
												    If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
													    If n\Path[n\PathLocation]\door\locked Or n\Path[n\PathLocation]\door\KeyCard>0 Or n\Path[n\PathLocation]\door\Code<>"" Then
														    temp = False
													    Else
														    If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
													    EndIf
												    EndIf
											    EndIf
											    If dist2#<0.2 And temp
												    n\PathLocation = n\PathLocation + 1
											    ElseIf dist2#<0.5 And (Not temp)
												    n\PathStatus = 0
												    n\PathTimer# = 0.0
											    EndIf
										    EndIf	
										EndIf
									Else ;no path to the player, stands still
										n\CurrSpeed = 0
										AnimateNPC(n, 778, 926, 0.1)
										;Animate2(n\obj, AnimTime(n\obj), 778, 926, 0.1)
										
										n\PathTimer = n\PathTimer-fs\FPSfactor[0]
										If n\PathTimer < 70*5
										    n\PathTimer = n\PathTimer + Rnd(1,2+(2*SelectedDifficulty\aggressiveNPCs))*fs\FPSfactor[0]
									    Else
										    n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
										    n\PathTimer = 0
									    EndIf
									EndIf
								EndIf
								
								If EntityDistance(n\Collider,Collider)>HideDistance
									If n\State3 < 70*(15+(10*SelectedDifficulty\aggressiveNPCs))
										n\State3 = n\State3+fs\FPSfactor[0]
									Else
										n\State3 = 70*3
										n\State = 4
									EndIf
								EndIf
								
								;65, 80, 93, 109, 123
								If n\CurrSpeed > 0.005 Then
									If (prevFrame < 977 And n\Frame=>977) Or (prevFrame > 1010 And n\Frame<940) Then
										;PlaySound2(StepSFX(0,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
										PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
									EndIf
								EndIf
								;[End Block]
							Case 3
								;[Block]
								If chs\NoTarget Then n\State = 2
								If n\Frame < 66 Then
									AnimateNPC(n, 2, 65, 0.7, False)
									
									;Animate2(n\obj, AnimTime(n\obj), 2, 65, 0.7, False)
									If prevFrame < 23 And n\Frame=>23 Then
										If EntityDistance(n\Collider,Collider)<1.1
											If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
											    PlaySound_Strict DamageSFX(Rand(5,8))
											    If I_1033RU\HP = 0
												    Injuries = Injuries+Rnd(0.4,1.0)
												Else
											        Damage1033RU(20)
											        n\HP = n\HP - 24
											        If n\HP =< 0 Then
							                            n\IsDead = True
							                        EndIf
											    EndIf
											    If Injuries > 3.0
												    DeathMSG = SubjectName$+". Cause of death: multiple lacerations and severe blunt force trauma caused by an instance of SCP-008-2."
												    Kill(True)
												EndIf
											EndIf
										EndIf
									ElseIf n\Frame=65 Then
										n\State = 2
									EndIf							
								Else
									AnimateNPC(n, 66, 132, 0.7, False)
									;Animate2(n\obj, AnimTime(n\obj), 66, 132, 0.7, False)
									If prevFrame < 90 And n\Frame=>90 Then
										If EntityDistance(n\Collider,Collider)<1.1
											If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
											    PlaySound_Strict DamageSFX(Rand(5,8))
											    If I_1033RU\HP = 0
												    Injuries = Injuries+Rnd(0.4,1.0)
												Else
											        Damage1033RU(20)
											        n\HP = n\HP - 24
											        If n\HP =< 0 Then
							                            n\IsDead = True
							                        EndIf
                                                EndIf
												If Injuries > 3.0
												    DeathMSG = SubjectName$+". Cause of death: multiple lacerations and severe blunt force trauma caused by an instance of SCP-008-2."
												    Kill(True)
											    EndIf
											EndIf
										EndIf
									ElseIf n\Frame=132 Then
										n\State = 2
									EndIf		
								EndIf
								;[End Block]
						End Select
					Else
					    If n\SoundCHN <> 0
						    StopChannel n\SoundCHN
						    n\SoundCHN = 0
						    FreeSound_Strict n\Sound
						    n\Sound = 0
					    EndIf
						AnimateNPC(n, 1035, 1072, 0.3, False)
					EndIf
					
					PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
					
					RotateEntity n\obj, -90, EntityYaw(n\Collider), 0
				EndIf
				
				;[End Block]
			Case NPCtype650
				;[Block]
              
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
				RotateEntity (n\obj, 0, EntityYaw(n\Collider), 0)

                pvt = CreatePivot()
                PositionEntity pvt,EntityX(Collider,True),EntityY(Collider,True),EntityZ(Collider,True)
                RotateEntity pvt,0,EntityYaw(Collider,True),0
                TurnEntity pvt,0,180,0
                 
                EntityPick(pvt,1.0)
                FreeEntity pvt
				dist# = EntityDistance(n\Collider, Collider)								
				
				;Nullified teleportation behind player due to issues. If someone could do a hotfix, credits given.
				If n\Idle = False Then								
					If dist < 15 Then
						If dist < 10.0 Then 
							If EntityVisible(n\Collider, Collider) Then
                                ShouldPlay = 27
				                HeartBeatRate = Max(HeartBeatRate, 140)
				                HeartBeatVolume = 1.0
								n\EnemyX = EntityX(Collider, True)
								n\EnemyY = EntityY(Collider, True)
								n\EnemyZ = EntityZ(Collider, True)
							EndIf
						EndIf												
						n\PrevX = EntityX(n\Collider)
						n\PrevZ = EntityZ(n\Collider)						
					EndIf
					;More than 6 room lengths away from the player -> teleport to a room closer to the player
		
                    ;Set 650 to disappear sometime...
                    ;Experimenting teleportation behind the player is still on progress
                    ;As a matter of fact, it is not 100% bug-proof.

                    If PickedEntity() <> 0 Then
                        ;Do nothing
                    Else
                        If ((BlinkTimer < - 16) Or (IsNVGBlinking=True)) And EntityVisible(n\obj,Camera) Then
                            ;Make 650 to not move like 173
                            ;Player blinks -> 650 teleport behind the player (Issue fixed)
					        RotateEntity(n\Collider,0,EntityYaw(Collider)-180,0)														
					        PositionEntity(n\Collider, EntityX(Collider), EntityY(Collider), EntityZ(Collider))
					        MoveEntity(n\Collider, 0, 0, +1.5)
					        ResetEntity n\Collider
					        MoveEntity(n\Collider, 0, +0.50, 0)					
					   						         						
						    Select Rand(5)
							    Case 1
								    SetNPCFrame(n, 1)
							    Case 2						    
								    SetNPCFrame(n, 5)
							    Case 3
								    SetNPCFrame(n, 7)
							    Case 4
								    SetNPCFrame(n, 9)
							    Case 5
								    SetNPCFrame(n, 11)
						    End Select											
					    ElseIf (Not EntityInView(n\obj, Camera)) And EntityVisible(n\obj,Camera) Then 
					        ;Make 650 to move like 173
					        ;Player don't look -> 650 move forward				
						    PointEntity(n\Collider, Collider)	
																
						    MoveEntity(n\Collider, 0, 0, n\Speed * fs\FPSfactor[0])
						
						    Select Rand(5)
							    Case 1
								    SetNPCFrame(n, 1)
							    Case 2
								    SetNPCFrame(n, 5)
							    Case 3
								    SetNPCFrame(n, 7)
							    Case 4
								    SetNPCFrame(n, 9)
							    Case 5
								    SetNPCFrame(n, 11)
						    End Select
					    ElseIf (Not EntityVisible(n\obj,Camera)) And EntityInView(n\obj, Camera) And dist > 8.0 Then
					        ;Not sure if this is buggy or not but I will let the testers test it.
					        ;This is triggered if: 650 is not visible, dist is more than 10, and camera sense 650 in the view
							PositionEntity(n\Collider, EntityX(Collider), EntityY(Collider)+0.25, EntityZ(Collider))
							ResetEntity n\Collider
					        RotateEntity(n\Collider,0,EntityYaw(Collider)-180,0)														
					        PositionEntity(n\Collider, EntityX(Collider), EntityY(Collider)+0.25, EntityZ(Collider))
					        MoveEntity(n\Collider, 0, 0, +1.5)
					    ElseIf (Not EntityVisible(n\obj,Camera)) And (Not EntityInView(n\obj, Camera)) And dist > 8.0 Then	
					        ;Not sure if this is buggy or not but I will let the testers test it.
					        ;This is triggered if: 650 is not visible, dist is more than 10, and camera does not sense 650 in the view
							PositionEntity(n\Collider, EntityX(Collider), EntityY(Collider)+0.25, EntityZ(Collider))
							ResetEntity n\Collider
					        RotateEntity(n\Collider,0,EntityYaw(Collider)-180,0)														
					        PositionEntity(n\Collider, EntityX(Collider), EntityY(Collider)+0.25, EntityZ(Collider))
					        MoveEntity(n\Collider, 0, 0, +0.8)																						 															  																																		
                        EndIf
					EndIf
										
					;650 to no longer appear if player is in gateb, gatea, dimension1499, room860 or pocketdimension
					For e.Events = Each Events
					    If (PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(Collider) > 1040.0 * RoomScale) Or PlayerRoom\RoomTemplate\Name = "gatea" Or PlayerRoom\RoomTemplate\Name = "pocketdimension" Or PlayerRoom\RoomTemplate\Name = "dimension1499" Or (PlayerRoom\RoomTemplate\Name = "room860" And e\EventState = 1.0) Then
					        HideEntity n\obj					
					        HideEntity n\Collider
					        n\Idle = True					
					    EndIf	
					Next																																			  
			    EndIf	
					
				;[End Block]
			Case NPCtype457
			    ;[Block]
			      
			    If PlayerRoom\RoomTemplate\Name <> "room457" Then n\State = 3 

				prevFrame# = n\Frame
				
				If n\State < 3 Then ;used if player escaped from the room457 tunnels
				    If (Not Contained457)
				        Select n\State
					        Case 0 ;normal routine (walking and idling)
					            ;[Block]
						        If n\PathStatus = 1
							        If n\Path[n\PathLocation]=Null Then 
								        If n\PathLocation > 19 Then 
									        n\PathLocation = 0 : n\PathStatus = 0
								        Else
									        n\PathLocation = n\PathLocation + 1
								        EndIf
							        Else
								        PointEntity n\obj, n\Path[n\PathLocation]\obj
								
								        RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0

								        AnimateNPC(n, 284, 333, n\CurrSpeed*43)

								        n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
								
								        MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
								
								        If EntityDistance(n\Collider,n\Path[n\PathLocation]\obj) < 0.3 Then
									        n\PathLocation = n\PathLocation + 1
								        EndIf
							        EndIf
							
							        If EntityDistance(n\Collider,Collider) < 6.0
								        If EntityVisible(n\Collider,Collider)
									        n\State = 0
								        EndIf
							        EndIf
						        Else
							        If n\Frame < 493
								        n\CurrSpeed = 0
								        AnimateNPC(n, 334, 494, 0.4)
							        Else
                                        n\PathStatus = FindPath(n, EntityX(n\TargetEnt,True), EntityY(n\TargetEnt,True), EntityZ(n\TargetEnt,True))
							        EndIf
						        EndIf
						
						        If EntityDistance(n\Collider,Collider) < 4.0 And (Not chs\Notarget)
							        If EntityVisible(n\Collider,Collider)
								        n\LastSeen = 500
								        n\State = 1
								        n\PathStatus = 0
								        n\State3 = 0.0
							        EndIf
						        EndIf
						        ;[End Block]
					        Case 1 ;chasing the player and trying to kill him
					            ;[Block]
					
						        If n\LastSeen > 0
							        If n\State3 = 0.0
								        n\SoundCHN = PlaySound_Strict(n\Sound)
								        n\State3 = 1.0
							        EndIf
							
							        n\CurrSpeed = n\Speed
							        MoveEntity n\Collider, 0, 0, (n\CurrSpeed*2)*fs\FPSfactor[0]
							
							        AnimateNPC(n, 284, 333, n\CurrSpeed*60)
							
							        angle = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
							        RotateEntity n\Collider, 0, CurveAngle(angle, EntityYaw(n\Collider), 20.0), 0
							
							        If EntityDistance(n\Collider, Collider) < 6.0
								        If EntityVisible(n\Collider, Collider)
									        n\LastSeen = 500
									        If EntityDistance(n\Collider,Collider) < 0.6
									            If EntityDistance(n\Collider,Collider) > 0.5 Then 
										            n\CurrSpeed = CurveValue(n\Speed * 2.5,n\CurrSpeed,10.0)
									            Else
										            n\CurrSpeed = 0
									            EndIf
									            AnimateNPC(n, 105, 110, 0.15, False)

										        If KillTimer >= 0 And (Not chs\GodMode) Then
										            PointEntity n\obj, Collider 
										            RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0 
										            If Ceil(n\Frame) = 110 And (Not chs\GodMode) Then
										                PlaySound_Strict(LoadTempSound(SFXPath$+"SCP\294\Burn.ogg"))
												        Kill(False, True)
												
											            DeathMsg = SubjectName$+" found dead inside the SCP-457's Storage Tunnels. Subject seems to be burned down by SCP-457 as his body is completely burnt. "
											            DeathMsg = DeathMsg + "SCP-457 seems to had damaged the whole area trying to get as many fuel as possible to grow. Luckely SCP-457 wasn't able to use the "
											            DeathMsg = DeathMsg + "elevators to get to the main facility."
	                                               EndIf
	                                            EndIf
										    EndIf
									    EndIf
							        EndIf
						        Else
							        n\State = 0
							        n\PathStatus = 0
						        EndIf
						        ;[End Block]
				        End Select
				    Else
				        ;used for contained state
				        AnimateNPC(n, 334, 494, 0.4, True)
				    EndIf		
				
				    If chs\Notarget And n\State = 1 Then n\State = 0
				
				    ;SCP-457's fire							 
			        p.Particles = CreateParticle(EntityX(n\Collider),EntityY(n\Collider),EntityZ(n\Collider),0,Rnd(0.2,0.3),-0.2,60)
                    PositionEntity p\pvt,EntityX(n\Collider,True)+Rnd(-0.5,0.5),EntityY(n\Collider,True),EntityZ(n\Collider,True)+Rnd(-0.5,0.5)
                    p\SizeChange = 0.0098
                    p\Achange = Rnd(-0.0025,-0.003)
                    p\speed = 0.004
                    RotateEntity p\pvt, 90, 0, 0  
                    EntityTexture(p\obj, at\ParticleTextureID[9])          

                    p.Particles = CreateParticle(EntityX(n\Collider),EntityY(n\Collider),EntityZ(n\Collider),0,Rnd(0.2,0.3),-0.2,65)
                    PositionEntity p\pvt,EntityX(n\Collider,True)+Rnd(-0.5,0.5),EntityY(n\Collider,True),EntityZ(n\Collider,True)+Rnd(-0.5,0.5)
                    p\SizeChange = 0.00059
                    p\Achange = Rnd(-0.0025,-0.003)
                    p\speed = 0.0007
                    RotateEntity p\pvt, 90, 0, 0   
				
				    n\LastSeen = Max(0, n\LastSeen - fs\FPSfactor[0])
				                		
				    PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				
				    RotateEntity n\obj, 0, EntityYaw(n\Collider), 0
				
                    n\SoundCHN = LoopSound2(FireSFX, n\SoundCHN, Camera, n\Collider, 10.0, 0.9)
                
                    If prevFrame =< 286 And n\Frame>286 Then
				    	PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 6.0, Rnd(0.8,1.0))	
				    ElseIf prevFrame=<311 And n\Frame>311.0 
					    PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 6.0, Rnd(0.8,1.0))
				    EndIf
				EndIf
	            ;[End Block]
	       Case NPCtype049_3
				;[Block]
				If Abs(EntityY(Collider)-EntityY(n\Collider))<4.0 Then

                    prevFrame# = n\Frame
					;prevframe# = AnimTime(n\obj)
				
					If (Not n\IsDead)

						Select n\State
							Case 0 ;Just Lying
                                ;nothing
							Case 1 ;Stands Up
								;Animate2(n\obj, AnimTime(n\obj), 3.0, 30.0, 0.1, False)
								AnimateNPC(n, 3.0, 30.0, 0.1, False)
								;If AnimTime(n\obj)=>29.5 Then n\State = 2
								If n\Frame = 30 Then n\State = 2
							Case 2 ;Following The Player
								If n\State3 < 0 Then ;check if the player is visible every three seconds
									If EntityDistance(Collider, n\Collider)<5.0 Then 
										If EntityVisible(Collider, n\Collider) Then n\State2 = 70*5
									EndIf
									n\State3=70*3
								Else
									n\State3=n\State3-fs\FPSfactor[0]
								EndIf						
						
								If n\State2 > 0 And (Not chs\NoTarget) Then ;player is visible -> attack
									dist = EntityDistance(Collider, n\Collider)
							
									PointEntity n\obj, Collider
									RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
							
									If dist < 0.7 Then 
										n\State = 3
										n\Frame = 127.0
										;SetAnimTime n\obj, 127.0
									Else
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]								
								
										If dist < 3.0 Then
										    AnimateNPC(n, Min(AnimTime(n\obj),95.0), 124.0, n\CurrSpeed*30)
											;Animate2(n\obj, AnimTime(n\obj), Min(AnimTime(n\obj),95.0), 124.0, n\CurrSpeed*30)
										Else
											AnimateNPC(n, 64.0, 93.0, n\CurrSpeed*30)
											;Animate2(n\obj, AnimTime(n\obj), 64.0, 93.0, n\CurrSpeed*30)
										EndIf
									EndIf
							
									n\State2=n\State2-fs\FPSfactor[0]
								Else
									n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider, 6.0, 0.6)
							
									If n\PathStatus = 1 Then ;path found
										If n\Path[n\PathLocation]=Null Then 
											If n\PathLocation > 19 Then 
												n\PathLocation = 0 : n\PathStatus = 0
											Else
												n\PathLocation = n\PathLocation + 1
											EndIf
										Else
											PointEntity n\obj, n\Path[n\PathLocation]\obj
									
											RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
											n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
											MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
											AnimateNPC(n, 64.0, 93.0, n\CurrSpeed*30)
											;Animate2(n\obj, AnimTime(n\obj), 64.0, 93.0, n\CurrSpeed*30)
									
											;opens doors in front of him
											dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
											If dist2 < 0.6 Then
												temp = True
												If n\Path[n\PathLocation]\door <> Null Then
													If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
														If n\Path[n\PathLocation]\door\locked Or n\Path[n\PathLocation]\door\KeyCard>0 Or n\Path[n\PathLocation]\door\Code<>"" Then
															temp = False
														Else
															If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
														EndIf
													EndIf
												EndIf
												If dist2#<0.2 And temp
													n\PathLocation = n\PathLocation + 1
												ElseIf dist2#<0.5 And (Not temp)
													n\PathStatus = 0
													n\PathTimer# = 0.0
												EndIf
											EndIf	
										EndIf
									Else ;no path to the player, stands still
										n\CurrSpeed = 0
										AnimateNPC(n, 590, 599, 0.1)
										;Animate2(n\obj, AnimTime(n\obj), 590, 599, 0.1)
										n\PathTimer = n\PathTimer-fs\FPSfactor[0]
										If n\PathTimer < 70*5
										    n\PathTimer = n\PathTimer + Rnd(1,2+(2*SelectedDifficulty\aggressiveNPCs))*fs\FPSfactor[0]
									    Else
										    n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
										    n\PathTimer = 0
									    EndIf
									EndIf
								EndIf

								;If n\CurrSpeed > 0.005 Then
								;	If (prevframe < 65 And AnimTime(n\obj)=>65) Or (prevframe < 80 And AnimTime(n\obj)=>80) Then
								;		PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))				
								;	ElseIf (prevframe < 93 And AnimTime(n\obj)=>93) Or (prevframe < 109 And AnimTime(n\obj)=>109)
								;		PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
								;	ElseIf (prevframe < 123 And AnimTime(n\obj)=>123)
								;		PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))	
								;	EndIf
								;EndIf
								
								If n\CurrSpeed > 0 Then
									If (prevframe < 65 And n\Frame >= 65) Or (prevframe < 80 And n\Frame >= 80) Then
										PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))				
									ElseIf (prevframe < 93 And n\Frame >= 93) Or (prevframe < 109 And n\Frame >= 109)
										PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
									ElseIf (prevframe < 123 And n\Frame >= 123)
										PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))	
									EndIf
								EndIf							
							Case 3 ; Attacking
					    		If chs\NoTarget Then n\State = 2
					            AnimateNPC(n, 127, 165, 0.7, False)
								;Animate2(n\obj, AnimTime(n\obj), 127, 165, 0.7, False)
								;If prevframe < 146 And AnimTime(n\obj) >= 146 Then 
								If prevframe < 146 And n\Frame >= 146 Then 
						   	 		If EntityDistance(n\Collider,Collider)<1.1
							    		If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
							        		PlaySound_Strict DamageSFX(Rand(2,3))
						            		If I_1033RU\HP = 0
							            		Injuries = Injuries+Rnd(0.4,1.0)
							        		Else
							            		Damage1033RU(20)
							                    n\HP = n\HP - 20
							                    If n\HP =< 0 Then
							                        n\IsDead = True
							                    EndIf
							        		EndIf
							                If Injuries > 3.0 Then
							                    DeathMSG = SubjectName$+". Cause of death: multiple lacerations and severe blunt force trauma caused by an instance of SCP-049-3."
							                    Kill(True)
							                EndIf
							   			EndIf
                           			EndIf
                                ElseIf n\Frame > 164.0 Then
								;ElseIf AnimTime(n\obj)>164.0 Then
						    		n\State = 2
								EndIf
						End Select
					Else
					    If n\SoundCHN <> 0
						    StopChannel n\SoundCHN
						    n\SoundCHN = 0
						    FreeSound_Strict n\Sound
						    n\Sound = 0
					    EndIf
					    ;Animate2(n\obj, AnimTime(n\obj), 600, 619, 0.5, False)
						AnimateNPC(n, 600, 619, 0.5, False)
						If n\Frame >= 618 Then n\Frame = 619
					EndIf
				
				    PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				
				    RotateEntity n\obj, 0, EntityYaw(n\Collider)-180.0, 0
				EndIf
				
				;[End Block]
			Case NPCtype178_1
				;[Block]
				dist = EntityDistance(n\Collider,Collider)
				
				If n\Sound > 0 Then
					temp = 0.5
					;the ambient sound gets louder when the npcs are attacking
					If n\State > 0 Then temp = 1.0	
					
					n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, Camera, 10.0,temp)
				EndIf
				
				temp = Rnd(-1.0,1.0)
				PositionEntity n\obj,EntityX(n\Collider,True)+n\PrevX*temp,EntityY(n\Collider,True)-0.2,EntityZ(n\Collider,True)+n\PrevZ*temp
				RotateEntity n\obj,0.0,EntityYaw(n\Collider)-180,0.0
				
				;use the prev-values to do a "twitching" effect
				n\PrevX = CurveValue(0.0, n\PrevX, 10.0)
				n\PrevZ = CurveValue(0.0, n\PrevZ, 10.0)
				
				If Rand(200)=1 Then
					If Rand(5)=1 Then
						n\PrevX = (EntityX(Collider)-EntityX(n\Collider))*0.9
						n\PrevZ = (EntityZ(Collider)-EntityZ(n\Collider))*0.9
					Else
						n\PrevX = Rnd(0.1,0.5)
						n\PrevZ = Rnd(0.1,0.5)						
					EndIf
					
				EndIf
				
				If (I_178\Using<>1) And (n\State3=0.0) Then
					If n\State<=0 Then
						;nothing happens here...
					Else
						EntityAlpha n\obj,0.0
						n\State=Max(n\State-fs\FPSfactor[0],0.0)
					EndIf
				Else
					If n\State3=0.0 Then
						EntityAlpha n\obj,0.5+(Float(Rand(0,1))/2.0)
					Else
						EntityAlpha n\obj,0.5+(Sin(n\Frame*6.0)/2.0)
					EndIf
				EndIf
				
				If n\State = 0 Then
					If n\Reload = 0 Then
						AnimateNPC(n, 206, 240, 0.1)
						If n\Frame>=240 Then n\Reload = 1
						
						;Animate2(n\obj, AnimTime(n\obj), 206, 240, 0.1)
						;If AnimTime(n\obj)>=240 Then n\Reload = 1
					Else
						AnimateNPC(n, 206, 240, -0.1)
						If n\Frame<=206 Then n\Reload = 0					
						
						;Animate2(n\obj, AnimTime(n\obj), 206, 240, -0.1)
						;If AnimTime(n\obj)<=206 Then n\Reload = 0
					EndIf
					
					;If Rand(1,15)=1 Then EntityColor n\obj,Rand(0,50),0,Rand(50,100)
					angle = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
					RotateEntity n\Collider,0.0,CurveAngle(angle,EntityYaw(n\Collider),20.0),0.0
					
					For n2.NPCs = Each NPCs
						If (n2\NPCtype=n\NPCtype) And (n<>n2) Then
							If n2\State>0.0 Then n\State=1.0
							If n2\State3>0.0 Then n\State3=1.0
							;if one of the npcs is hostile, make all of them hostile
						EndIf
						If n\State<>0 Then Exit
					Next
					If dist<1.0 And EntityInView(n\obj,Camera) Then
						n\State2=n\State2+fs\FPSfactor[0]
					ElseIf EntityCollided(n\Collider,HIT_PLAYER)=Collider Then
						n\State2=50.0
					Else
						n\State2=Max(n\State2-fs\FPSfactor[0],0.0)
					EndIf
					If n\State2>=50.0 Then
						n\State=1.0
					EndIf
				EndIf
				If n\State>0 Then
					If n\State3=0 Then
						For n2.NPCs = Each NPCs
							If (n2\NPCtype=n\NPCtype) And (n<>n2) Then
								If n2\State3>0.0 Then n\State3=1.0
							EndIf
							If n\State3<>0 Then Exit
						Next
					EndIf
					
					;If Rand(1,15)=1 Then EntityColor n\obj,Rand(50,100),0,Rand(0,50)
					If (I_178\Using=1) Or (n\State3>0) Then
						n\State=Min(n\State+fs\FPSfactor[0],300.0)
					Else
						n\State=Max(n\State-fs\FPSfactor[0],0.0)
					EndIf
					If n\PathTimer <= 0 Then
						n\PathStatus = FindPath (n, EntityX(Collider,True), EntityY(Collider,True), EntityZ(Collider,True))
						n\PathTimer = 40*10
						n\CurrSpeed = 0
					EndIf
					n\PathTimer = Max(n\PathTimer-fs\FPSfactor[0],0)
					
					If (Not EntityVisible(n\Collider,Collider)) Then
						If n\PathStatus = 2 Then
							n\CurrSpeed = 0
							n\Frame = 15
							;SetAnimTime n\obj,15
						ElseIf n\PathStatus = 1
							If n\Path[n\PathLocation]=Null Then 
								If n\PathLocation > 19 Then 
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								angle = VectorYaw(EntityX(n\Path[n\PathLocation]\obj,True)-EntityX(n\Collider),0,EntityZ(n\Path[n\PathLocation]\obj,True)-EntityZ(n\Collider))
								RotateEntity n\Collider,0.0,CurveAngle(angle,EntityYaw(n\Collider),10.0),0.0
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
								
								If EntityDistance(n\Collider,n\Path[n\PathLocation]\obj) < 0.2 Then
									n\PathLocation = n\PathLocation + 1
								EndIf 
							EndIf
						ElseIf n\PathStatus = 0
							;SetAnimTime(n\obj,64)
							n\CurrSpeed = CurveValue(0,n\CurrSpeed,10.0)
						EndIf
					Else
						angle = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
						RotateEntity n\Collider,0.0,CurveAngle(angle,EntityYaw(n\Collider),10.0),0.0
						
						n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
					EndIf
					
					If dist > 0.3 Then
						MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
						If dist < 2.0 Then
						    AnimateNPC(n, Min(AnimTime(n\obj),95.0), 124.0, n\CurrSpeed*10)
						    ;Animate2(n\obj, AnimTime(n\obj), Min(AnimTime(n\obj),95.0), 124.0, n\CurrSpeed*30)
						Else
							AnimateNPC(n, 64.0, 93.0, n\CurrSpeed*10)
							;Animate2(n\obj, AnimTime(n\obj), 64.0, 93.0, n\CurrSpeed*30)
						EndIf
						
						For d.Doors = Each Doors
							If (EntityDistance(n\Collider,d\obj)<1.25) And (d\Code<>"ABCD") And (EntityCollided(n\Collider,HIT_178)=0) Then
								n\DropSpeed=0
								ResetEntity n\Collider
							EndIf
						Next
					EndIf
					If dist<0.4 Then
						;Animate2(n\obj, AnimTime(n\obj), 122, 172, 0.2, False)		
						If (n\State2>=65.0) And (KillTimer>=0) Then
						   PlaySound_Strict DamageSFX(Rand(2,3))
						   If I_1033RU\HP = 0
							   Injuries = Injuries+Rnd(0.4,0.8)
						   Else
						       Damage1033RU(20+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
						   EndIf
							If Injuries>=4.0 Then
								If n\State3=0.0 Then
									DeathMSG = SubjectName$+" wearing SCP-178 was found dead in [DATA REDACTED]. "
									DeathMSG = DeathMSG + "Presumed to be killed by the "
									DeathMSG = DeathMSG + "effects of SCP-178."
								Else
									DeathMSG = "Several bipedal entities of unknown origin were found in Site-[DATA REDACTED]. "
									DeathMSG = DeathMSG + "Testing reveals that these entities behave similarly to those "
									DeathMSG = DeathMSG + "seen when using SCP-178. It's unknown at the current time "
									DeathMSG = DeathMSG + "whether SCP-178 and these entities are related."
								EndIf
								Kill(True)
							EndIf
							n\State2=0.0
						Else
							n\State2=n\State2+fs\FPSfactor[0]
						EndIf
					EndIf
					
				EndIf

				;[End Block]
			Case NPCtypeMTF2
				;[Block]
				UpdateMTF2Unit(n)
				;[End Block]
			Case NPCtypeCI 	
				;[Block]

				prevFrame = AnimTime(n\obj)
				
				Select n\State
					Case 0 ;idle
						n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
						Animate2(n\obj, AnimTime(n\obj), 2, 175, 0.1)
					Case 1 ;walking
						If n\State2 = 1.0
							n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
						Else
							n\CurrSpeed = CurveValue(0.015, n\CurrSpeed, 5.0)
						EndIf
						Animate2(n\obj, AnimTime(n\obj), 176, 210, n\CurrSpeed * 18)
				End Select
				
				If n\CurrSpeed > 0.01 Then
					If prevFrame < 180 And AnimTime(n\obj)=>180 Then
						PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(2,0)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))						
					ElseIf prevFrame < 195 And AnimTime(n\obj)=>195
						PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(2,0)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
					EndIf
				EndIf				
							
				MoveEntity(n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0])
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
				
				RotateEntity n\obj, 0, EntityYaw(n\Collider), 0
				;[End Block]
            Case NPCtypeVehicle 
				;[Block]
				
				Select n\State
					Case 0 ;idle
					    n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
					Case 1 ;rides
						n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
						Animate2(n\obj, AnimTime(n\obj), 1, 20, n\CurrSpeed * 18)
				End Select
				
				If n\State = 0 Then
				    If ChannelPlaying(n\SoundCHN2) = True Then StopChannel n\SoundCHN2 
				    n\SoundCHN = LoopSound2(VehicleSFX(1), n\SoundCHN, Camera, n\Collider, 13, 1.0)
				ElseIf n\State = 1
				    If ChannelPlaying(n\SoundCHN) = True Then StopChannel n\SoundCHN  
				    n\SoundCHN2 = LoopSound2(VehicleSFX(0), n\SoundCHN2, Camera, n\Collider, 13, 1.0)
				EndIf
												
				MoveEntity(n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0])
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
				
				RotateEntity n\obj, 0, EntityYaw(n\Collider), 0
				;[End Block]
				
            ;{~--<END>--~}

		End Select
		
		If n\IsDead
			EntityType n\Collider,HIT_DEAD
		EndIf
		
		Local gravityDist = Distance(EntityX(Collider),EntityZ(Collider),EntityX(n\Collider),EntityZ(n\Collider))
		
		If gravityDist<HideDistance*0.7 Or n\NPCtype = NPCtype1499_1 Then
			If n\InFacility = InFacility
				TranslateEntity n\Collider, 0, n\DropSpeed, 0
				
				Local CollidedFloor% = False
				For i% = 1 To CountCollisions(n\Collider)
					If CollisionY(n\Collider, i) < EntityY(n\Collider) - 0.01 Then CollidedFloor = True : Exit
				Next
				
				If CollidedFloor = True Then
					n\DropSpeed# = 0
				Else
					If ShouldEntitiesFall

						Local UpdateGravity% = False
						Local MaxX#,MinX#,MaxZ#,MinZ#
						If n\InFacility=1
							If PlayerRoom\RoomTemplate\Name$ <> "room173intro"
								For e.Events = Each Events
									If e\EventName = "room860"
										If e\EventState = 1.0
											UpdateGravity = True
											Exit
										EndIf
									EndIf
								Next
							Else
								UpdateGravity = True
							EndIf
							If (Not UpdateGravity)
								For r.Rooms = Each Rooms
									If r\MaxX<>0 Or r\MinX<>0 Or r\MaxZ<>0 Or r\MinZ<>0
										MaxX# = r\MaxX
										MinX# = r\MinX
										MaxZ# = r\MaxZ
										MinZ# = r\MinZ
									Else
										MaxX# = 4.0
										MinX# = 0.0
										MaxZ# = 4.0
										MinZ# = 0.0
									EndIf
									If Abs(EntityX(n\Collider)-EntityX(r\obj))<=Abs(MaxX-MinX)
										If Abs(EntityZ(n\Collider)-EntityZ(r\obj))<=Abs(MaxZ-MinZ)
											If r=PlayerRoom
												UpdateGravity = True
												Exit
											EndIf
											If IsRoomAdjacent(PlayerRoom,r)
												UpdateGravity = True
												Exit
											EndIf
											For i=0 To 3
												If (IsRoomAdjacent(PlayerRoom\Adjacent[i],r))
													UpdateGravity = True
													Exit
												EndIf
											Next
										EndIf
									EndIf
								Next
							EndIf
						Else
							UpdateGravity = True
						EndIf
						If UpdateGravity
							n\DropSpeed# = Max(n\DropSpeed - 0.005*fs\FPSfactor[0]*n\GravityMult,-n\MaxGravity)
						Else
							If n\FallingPickDistance>0
								n\DropSpeed = 0.0
							Else
								n\DropSpeed# = Max(n\DropSpeed - 0.005*fs\FPSfactor[0]*n\GravityMult,-n\MaxGravity)
							EndIf
						EndIf
					Else
						n\DropSpeed# = 0.0
					EndIf
				EndIf
			Else
				n\DropSpeed = 0
			EndIf
		Else
			n\DropSpeed = 0
		EndIf	
	Next
	
	If MTF_CameraCheckTimer>0.0 And MTF_CameraCheckTimer<70*90
		MTF_CameraCheckTimer=MTF_CameraCheckTimer+fs\FPSfactor[0]
	ElseIf MTF_CameraCheckTimer>=70*90
		MTF_CameraCheckTimer=0.0
		If (Not PlayerDetected)
			If MTF_CameraCheckDetected
				PlayAnnouncement(SFXPath$+"Character\MTF\AnnouncCameraFound"+Rand(1,2)+".ogg")
				PlayerDetected=True
				MTF_CameraCheckTimer=70*60
			Else
				PlayAnnouncement(SFXPath$+"Character\MTF\AnnouncCameraNoFound.ogg")
			EndIf
		EndIf
		MTF_CameraCheckDetected=False
		If MTF_CameraCheckTimer=0.0
			PlayerDetected=False
		EndIf
	EndIf
	
	;If MTF2_CameraCheckTimer>0.0 And MTF2_CameraCheckTimer<70*90
        ;MTF2_CameraCheckTimer=MTF2_CameraCheckTimer+fs\FPSfactor[0]
	;ElseIf MTF2_CameraCheckTimer>=70*90
		;MTF2_CameraCheckTimer=0.0
		;If (Not PlayerDetected)
			;If MTF2_CameraCheckDetected
				;PlayAnnouncement(SFXPath$+"Character\MTF2\AnnouncCameraFound"+Rand(1,2)+".ogg")
				;PlayerDetected=True
				;MTF2_CameraCheckTimer=70*60
			;Else
		        ;PlayAnnouncement(SFXPath$+"Character\MTF2\AnnouncCameraNoFound.ogg")
			;EndIf
		;EndIf
		;MTF2_CameraCheckDetected=False
		;If MTF2_CameraCheckTimer=0.0
		;	PlayerDetected=False
		;EndIf
	;EndIf

	
End Function

Function TeleportCloser(n.NPCs)
	Local closestDist# = 0
	Local closestWaypoint.WayPoints
	Local w.WayPoints
	
	Local xtemp#, ztemp#
	
	For w.WayPoints = Each WayPoints
		If w\door = Null Then
			xtemp = Abs(EntityX(w\obj,True)-EntityX(n\Collider,True))
			If xtemp < 10.0 And xtemp > 1.0 Then 
				ztemp = Abs(EntityZ(w\obj,True)-EntityZ(n\Collider,True))
				If ztemp < 10.0 And ztemp > 1.0 Then
					If (EntityDistance(Collider, w\obj)>16-(8*SelectedDifficulty\aggressiveNPCs)) Then
						;teleports to the nearby waypoint that takes it closest to the player
						Local newDist# = EntityDistance(Collider, w\obj)
						If (newDist < closestDist Or closestWaypoint = Null) Then
							closestDist = newDist	
							closestWaypoint = w
						EndIf						
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	
	Local shouldTeleport% = False
	If (closestWaypoint<>Null) Then
		If n\InFacility <> 1 Or SelectedDifficulty\aggressiveNPCs Then
			shouldTeleport = True
		ElseIf EntityY(closestWaypoint\obj,True)<=7.0 And EntityY(closestWaypoint\obj,True)>=-10.0 Then
			shouldTeleport = True
		EndIf
		
		If shouldTeleport Then
			PositionEntity n\Collider, EntityX(closestWaypoint\obj,True), EntityY(closestWaypoint\obj,True)+0.15, EntityZ(closestWaypoint\obj,True), True
			ResetEntity n\Collider
			n\PathStatus = 0
			n\PathTimer# = 0.0
			n\PathLocation = 0
		EndIf
	EndIf
	
End Function

Function OtherNPCSeesMeNPC%(me.NPCs,other.NPCs)
	If other\BlinkTimer<=0.0 Then Return False
	
	If EntityDistance(other\Collider,me\Collider)<6.0 Then
		If Abs(DeltaYaw(other\Collider,me\Collider))<60.0 Then
			Return True
		EndIf
	EndIf
	Return False
End Function

Function MeNPCSeesPlayer%(me.NPCs,disablesoundoncrouch%=False)
	;Return values:
		;False (=0): Player is not detected anyhow
		;True (=1): Player is detected by vision
		;2: Player is detected by emitting a sound
		;3: Player is detected by a camera (only for MTF Units!)
		;4: Player is detected through glass
	
	If chs\NoTarget Then Return False
	
	If (Not PlayerDetected) Or (me\NPCtype <> NPCtypeMTF Or me\NPCtype <> NPCtypeMTF2)
		If me\BlinkTimer<=0.0 Then Return False
		If EntityDistance(Collider,me\Collider)>(8.0-CrouchState+PlayerSoundVolume) Then Return False
		
		;spots the player if he's either in view or making a loud sound
		If PlayerSoundVolume>1.0
			If (Abs(DeltaYaw(me\Collider,Collider))>60.0) And EntityVisible(me\Collider,Collider)
				Return 1
			ElseIf (Not EntityVisible(me\Collider,Collider))
				If disablesoundoncrouch% And Crouch%
					Return False
				Else
					Return 2
				EndIf
			EndIf
		Else
			If (Abs(DeltaYaw(me\Collider,Collider))>60.0) Then Return False
		EndIf
		Return EntityVisible(me\Collider,Collider)
	Else
		If EntityDistance(Collider,me\Collider)>(8.0-CrouchState+PlayerSoundVolume) Then Return 3
		If EntityVisible(me\Collider, Camera) Then Return True
		
		;spots the player if he's either in view or making a loud sound
		If PlayerSoundVolume>1.0 Then Return 2
		Return 3
	EndIf
	
End Function

Function TeleportMTFGroup(n.NPCs)
	Local n2.NPCs
	
	If n\MTFLeader <> Null Or n\MTF2Leader <> Null  Then Return
	
	TeleportCloser(n)
	
	For n2 = Each NPCs
		If n2\NPCtype = NPCtypeMTF
			If n2\MTFLeader <> Null
				PositionEntity n2\Collider,EntityX(n2\MTFLeader\Collider),EntityY(n2\MTFLeader\Collider)+0.1,EntityZ(n2\MTFLeader\Collider)
			EndIf
		EndIf
	Next
	
	For n2 = Each NPCs
		If n2\NPCtype = NPCtypeMTF2
			If n2\MTF2Leader <> Null
				PositionEntity n2\Collider,EntityX(n2\MTF2Leader\Collider),EntityY(n2\MTF2Leader\Collider)+0.1,EntityZ(n2\MTF2Leader\Collider)
			EndIf
		EndIf
	Next
	
End Function

Function UpdateMTFUnit(n.NPCs)
	;[Block]
	Local fs.FPS_Settings = First FPS_Settings
	
	If n\NPCtype<>NPCtypeMTF Then
		Local realType$ = ""
		Select n\NPCtype
			Case NPCtype173
                realType = "173"
			Case NPCtype106
                realType = "106"
			Case NPCtypeGuard
                realType = "guard"
			Case NPCtypeD
                realType = "d"
			Case NPCtype372
                realType = "372"
			Case NPCtypeApache
                realType = "apache"
			Case NPCtype096
                realType = "096"
			Case NPCtype049
                realType = "049"
			Case NPCtype049_2
                realType = "049-2"
			Case NPCtype513_1
                realType = "513-1"
			Case NPCtype035_Tentacle
                realType = "035-Tentacle"
			Case NPCtype860_2
                realType = "860-2"
			Case NPCtype939
                realType = "939"
			Case NPCtype066
                realType = "066"
			Case NPCtypePdPlane
                realType = "PDPlane"
			Case NPCtype966
                realType = "966"
			Case NPCtype1048_A
                realType = "1048-A"
			Case NPCtype1499_1
				realType = "1499-1"
				
			;{~--<MOD>--~}
			
			Case NPCtype650
                realType = "650"
            Case NPCtype457
                realType = "457"
            Case NPCtype049_3
                realType = "049-3"
            Case NPCtype008_2
			    realType = "008-2"
			Case NPCtype178_1
                realType = "178-1"
            Case NPCtypeCI
                realType = "CI"
            Case NPCtypeMTF2
                realType = "MTF2"
            Case NPCtypeVehicle
                realType = "Vehicle"

            ;{~--<END>--~}

		End Select
		RuntimeError "Called UpdateMTFUnit on "+realType
	EndIf
	;[End Block]
	
	Local x#,y#,z#
	Local r.Rooms
	Local prevDist#,newDist#
	Local n2.NPCs
	
	Local p.Particles, target, dist#, dist2#
	Local dist_to_player#
	
	If n\IsDead Then
		n\BlinkTimer = -1.0
		SetNPCFrame(n, 532)
		If ChannelPlaying(n\SoundCHN2) Then
			StopChannel(n\SoundCHN2)
		EndIf
		Return
	EndIf
	
	n\MaxGravity = 0.03
	
	n\BlinkTimer = n\BlinkTimer - fs\FPSfactor[0]
	If n\BlinkTimer<=-5.0 Then 
		;only play the "blinking" sound clip if searching/containing 173
		If n\State = 2
			If OtherNPCSeesMeNPC(Curr173,n) Or EntityDistance(n\Collider,Curr173\Collider)<3.0
				PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF\173\BLINKING.ogg"),n)
			EndIf
		EndIf
		n\BlinkTimer = 70.0*Rnd(10.0,15.0)
	EndIf	
	
	n\Reload = n\Reload - fs\FPSfactor[0]
	
	Local prevFrame# = n\Frame
	
	n\BoneToManipulate = ""
	;n\BoneToManipulate2 = ""
	n\ManipulateBone = False
	n\ManipulationType = 0
	n\NPCNameInSection = "MTF"
	
	If Int(n\State) <> 1 Then n\PrevState = 0
	
	n\SoundCHN2 = LoopSound2(MTFSFX(6),n\SoundCHN2,Camera,n\Collider)
	
	If n\Idle>0.0 Then
		FinishWalking(n,488,522,0.015*26)
		n\Idle=n\Idle-fs\FPSfactor[0]
		If n\Idle<=0.0 Then n\Idle = 0.0
	Else
		Select Int(n\State) ;what is this MTF doing
			Case 0 ;wandering around
                ;[Block]
                n\Speed = 0.015
                If n\PathTimer<=0.0 Then ;update path
					If n\MTFLeader<>Null Then ;i'll follow the leader
						n\PathStatus = FindPath(n,EntityX(n\MTFLeader\Collider,True),EntityY(n\MTFLeader\Collider,True)+0.1,EntityZ(n\MTFLeader\Collider,True)) ;whatever you say boss
					Else ;i am the leader
						If Curr173\Idle<>2
							For r = Each Rooms
								If ((Abs(r\x-EntityX(n\Collider,True))>12.0) Or (Abs(r\z-EntityZ(n\Collider,True))>12.0)) And (Rand(1,Max(4-Int(Abs(r\z-EntityZ(n\Collider,True)/8.0)),2))=1) Then
									x = r\x
									y = 0.1
									z = r\z
									Exit
								EndIf
							Next
						Else
							Local tmp = False
							If EntityDistance(n\Collider,Curr173\Collider)>4.0
								If (Not EntityVisible(n\Collider,Curr173\Collider))
									tmp = True
								EndIf
							EndIf
							
							If (Not tmp)
								For r = Each Rooms
									If r\RoomTemplate\Name$ = "room173"
										Local foundChamber% = False
										Local pvt% = CreatePivot()
										PositionEntity pvt%,EntityX(r\obj,True)+4736*RoomScale,0.5,EntityZ(r\obj,True)+1692*RoomScale
										
										If Distance(EntityX(pvt%),EntityZ(pvt%),EntityX(n\Collider),EntityZ(n\Collider))<3.5
											foundChamber% = True
										EndIf
										
										If Curr173\Idle = 3 And Distance(EntityX(pvt%),EntityZ(pvt%),EntityX(n\Collider),EntityZ(n\Collider)) > 4.0
											If r\RoomDoors[1]\open = True Then UseDoor(r\RoomDoors[1],False)
										EndIf
										
										FreeEntity pvt%
										
										If Distance(EntityX(n\Collider),EntityZ(n\Collider),EntityX(r\obj,True)+4736*RoomScale,EntityZ(r\obj,True)+1692*RoomScale)>1.6 And (Not foundChamber)
											x = EntityX(r\obj,True)+4736*RoomScale
											y = 0.1
											z = EntityZ(r\obj,True)+1692*RoomScale
											Exit
										ElseIf Distance(EntityX(n\Collider),EntityZ(n\Collider),EntityX(r\obj,True)+4736*RoomScale,EntityZ(r\obj,True)+1692*RoomScale)>1.6 And foundChamber
											n\PathX = EntityX(r\obj,True)+4736*RoomScale
											n\PathZ = EntityZ(r\obj,True)+1692*RoomScale
											Exit
										Else
											Curr173\Idle = 3
											Curr173\Target = Null
											Curr173\IsDead = True
											If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
											n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\173\Cont"+Rand(1,4)+".ogg")
											PlayMTFSound(n\Sound, n)
											PlayAnnouncement(SFXPath$+"Character\MTF\Announc173Contain.ogg")
											r\RoomDoors[1]\MTFClose = True
                                         	UseDoor(r\RoomDoors[1],False)
											r\RoomDoors[1]\open = False
											Exit
										EndIf
									EndIf
								Next
							Else
								x = EntityX(Curr173\Collider)
								y = 0.1
								z = EntityZ(Curr173\Collider)
							EndIf
						EndIf
						If n\PathX=0 Then n\PathStatus = FindPath(n,x,y,z) ;we're going to this room for no particular reason
					EndIf
					If n\PathStatus = 1 Then
						While n\Path[n\PathLocation]=Null
							If n\PathLocation>19 Then Exit
							n\PathLocation=n\PathLocation+1
						Wend
						If n\PathLocation<19 Then
							If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
								If (n\Path[n\PathLocation]\door=Null) Then
									If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
										n\PathLocation=n\PathLocation+1
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
					n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6-10 seconds
                ElseIf (n\PathTimer<=70.0 * 2.5) And (n\MTFLeader=Null) Then
					n\PathTimer=n\PathTimer-fs\FPSfactor[0]
					n\CurrSpeed = 0.0
					If Rand(1,35)=1 Then
						RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
					EndIf
					FinishWalking(n,488,522,n\Speed*26)
					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
					RotateEntity n\obj,-90.0,n\Angle,0.0,True
                Else
					If n\PathStatus=2 Then
						n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
						n\CurrSpeed = 0.0
						If Rand(1,35)=1 Then
							RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
						EndIf
						FinishWalking(n,488,522,n\Speed*26)
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					ElseIf n\PathStatus=1 Then
						If n\Path[n\PathLocation]=Null Then
							If n\PathLocation > 19 Then
								n\PathLocation = 0 : n\PathStatus = 0
							Else
								n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							PointEntity n\Collider,n\Path[n\PathLocation]\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							;MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
							
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
							
							newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
								;open the door and make it automatically close after 5 seconds
								If (Not n\Path[n\PathLocation]\door\open)
									Local sound = 0
									If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
									PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
									PlayMTFSound(MTFSFX(5),n)
								EndIf
								n\Path[n\PathLocation]\door\open = True
								If n\Path[n\PathLocation]\door\MTFClose
									n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
								EndIf
							EndIf
                            
							If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
								n\PathLocation=n\PathLocation+1
							EndIf
						EndIf
						n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
					ElseIf n\PathX#<>0.0
						pvt = CreatePivot()
						PositionEntity pvt,n\PathX#,0.5,n\PathZ#
						
						PointEntity n\Collider,pvt
						RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
						
						n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
						AnimateNPC(n,488, 522, n\CurrSpeed*26)
						
						If Distance(EntityX(n\Collider),EntityZ(n\Collider),n\PathX#,n\PathZ#)<0.2
							n\PathX# = 0.0
							n\PathZ# = 0.0
							n\PathTimer = 70.0 * Rnd(6.0,10.0)
						EndIf
						
						FreeEntity pvt
					Else
						n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
						If n\MTFLeader = Null Then
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\CurrSpeed = 0.0
						ElseIf EntityDistance(n\Collider,n\MTFLeader\Collider)>1.0 Then
							PointEntity n\Collider,n\MTFLeader\Collider
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
						Else
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\CurrSpeed = 0.0
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					EndIf
                EndIf
                
				Local temp = MeNPCSeesPlayer(n)
				
				If chs\NoTarget Then temp = False
				
                If temp>False Then
					If n\LastSeen > 0 And n\LastSeen < 70*15 Then
						If temp < 2
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\ThereHeIs"+Rand(1,6)+".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
					Else
						If temp = True
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\Stop"+Rand(1,6)+".ogg")
							PlayMTFSound(n\Sound, n)
						ElseIf temp = 2
							;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							;n\Sound = MTFSFX(Rand(0,3))
							;PlayMTFSound(n\Sound, n)
							PlayMTFSound(MTFSFX(Rand(0,3)),n)
						EndIf
					EndIf
					
					n\LastSeen = (70*Rnd(30,40))
					n\LastDist = 1
					
					n\State = 1
					n\EnemyX = EntityX(Collider,True)
					n\EnemyY = EntityY(Collider,True)
					n\EnemyZ = EntityZ(Collider,True)
					n\State2 = 70.0*(15.0*temp) ;give up after 15 seconds (30 seconds if detected by loud noise, over camera: 45)
					n\PathTimer=0.0
					n\PathStatus=0
					n\Reload = 200-(100*SelectedDifficulty\aggressiveNPCs)
					
					;If EntityDistance(n\Collider,Collider)>HideDistance*0.7
					;	TeleportMTFGroup(n)
					;EndIf
                EndIf
				
				;B3D doesn't do short-circuit evaluation, so this retarded nesting is an optimization
                If Curr173\Idle<2 Then
					Local SoundVol173# = Max(Min((Distance(EntityX(Curr173\Collider), EntityZ(Curr173\Collider), Curr173\PrevX, Curr173\PrevZ) * 2.5), 1.0), 0.0)
					If OtherNPCSeesMeNPC(Curr173,n) Or (SoundVol173#>0.0 And EntityDistance(n\Collider,Curr173\Collider)<6.0) Then
						If EntityVisible(n\Collider,Curr173\Collider) Or SoundVol173#>0.0 Then							
							n\State = 2
							n\EnemyX = EntityX(Curr173\Collider,True)
							n\EnemyY = EntityY(Curr173\Collider,True)
							n\EnemyZ = EntityZ(Curr173\Collider,True)
							n\State2 = 70.0*15.0 ;give up after 15 seconds
							n\State3 = 0.0
							n\PathTimer=0.0
							n\PathStatus=0
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\173\Spotted"+Rand(1,2)+".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
					EndIf
				EndIf
				
				If Curr106\State <= 0
					If OtherNPCSeesMeNPC(Curr106,n) Or EntityDistance(n\Collider,Curr106\Collider)<3.0 Then
						If EntityVisible(n\Collider,Curr106\Collider) Then
							n\State = 4
							n\EnemyX = EntityX(Curr106\Collider,True)
							n\EnemyY = EntityY(Curr106\Collider,True)
							n\EnemyZ = EntityZ(Curr106\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							n\Target = Curr106
							;If n\MTFLeader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\106\Spotted"+Rand(1,4)+".ogg")
								PlayMTFSound(n\Sound, n)
							;EndIf
						EndIf
					EndIf
				EndIf
				
				If Curr096 <> Null
					If OtherNPCSeesMeNPC(Curr096,n) Then
						If EntityVisible(n\Collider,Curr096\Collider) Then
							n\State = 8
							n\EnemyX = EntityX(Curr096\Collider,True)
							n\EnemyY = EntityY(Curr096\Collider,True)
							n\EnemyZ = EntityZ(Curr096\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\096\Spotted"+Rand(1,2)+".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
					EndIf
				EndIf
				
				For n2.NPCs = Each NPCs
					If n2\NPCtype = NPCtype049
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 4
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\Spotted"+Rand(1,5)+".ogg")
								PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype049_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\Player0492_1.ogg")
								PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype008_1 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\008\008_1spotted.ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
						
					;{~--<MOD>--~}
					
					ElseIf n2\NPCtype = NPCtype049_3 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\049_3Spotted.ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype008_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\008\008_2spotted.ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype035_Tentacle And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\035\Tentacle_Spotted.ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					EndIf
					
					;{~--<END>--~}
					
				Next
                ;[End Block]
			Case 1 ;searching for player
                ;[Block]
                dist_to_player = EntityDistance(Collider, n\Collider)

                n\Speed = 0.015
                n\State2=n\State2-fs\FPSfactor[0]
                If MeNPCSeesPlayer(n) = True Then
					
					;if close enough, start shooting at the player
					If playerDist < 5.2 Then
						
						Local angle# = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
						
						RotateEntity(n\Collider, 0, CurveAngle(angle, EntityYaw(n\Collider), 10.0), 0, True)
						n\Angle = EntityYaw(n\Collider)
						
						If n\Reload =< 0 And KillTimer = 0 Then
							If EntityVisible(n\Collider, Camera) Then
								angle# = WrapAngle(angle - EntityYaw(n\Collider))
								If angle < 5 Or angle > 355 Then 
									prev% = KillTimer
									
									PlaySound2(GunshotSFX, Camera, n\Collider, 15)
									
									pvt% = CreatePivot()
									
									RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
									PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
									MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)

                                    ;Shoot(EntityX(pvt),EntityY(pvt),EntityZ(pvt),5.0/playerDist, True)
									Shoot(EntityX(pvt),EntityY(pvt), EntityZ(pvt),((25/dist_to_player)*(1/dist_to_player)), True)
									n\Reload = 7
									
									FreeEntity(pvt)
									
									DeathMSG=SubjectName$+". Died of blood loss after being shot by Nine-Tailed Fox."
									
									;player killed -> "target terminated"
									If prev => 0 And KillTimer < 0 Then
										DeathMSG=SubjectName$+". Terminated by Nine-Tailed Fox."
										PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF\Targetterminated"+Rand(1,4)+".ogg"),n)
									EndIf
								EndIf	
							EndIf
						EndIf
						
						For n2.NPCs = Each NPCs
							If n2\NPCtype = NPCtypeMTF And n2 <> n
								If n2\State = 0
									If EntityDistance(n\Collider,n2\Collider)<6.0
										n\PrevState = 1
										n2\LastSeen = (70*Rnd(30,40))
										n2\LastDist = 1
										
										n2\State = 1
										n2\EnemyX = EntityX(Collider,True)
										n2\EnemyY = EntityY(Collider,True)
										n2\EnemyZ = EntityZ(Collider,True)
										n2\State2 = n\State2
										n2\PathTimer=0.0
										n2\PathStatus=0
										n2\Reload = 200-(100*SelectedDifficulty\aggressiveNPCs)
										n2\PrevState = 0
									EndIf
								EndIf
							EndIf
						Next
						
						If n\PrevState = 1
							SetNPCFrame(n,423)
							n\PrevState = 2
						ElseIf n\PrevState=2
							If n\Frame>200
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
								AnimateNPC(n, 423, 463, 0.4, False)
								If n\Frame>462.9 Then n\Frame = 78
							Else
								AnimateNPC(n, 78, 193, 0.2, False)
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
							EndIf
						Else
							If n\Frame>958 Then
								AnimateNPC(n, 1374, 1383, 0.3, False)
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
								If n\Frame>1382.9 Then n\Frame = 78
							Else
								AnimateNPC(n, 78, 193, 0.2, False)
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
							EndIf
						EndIf
					Else
						PositionEntity n\obj,n\EnemyX,n\EnemyY,n\EnemyZ,True
						PointEntity n\Collider,n\obj
						RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
						
						n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
						AnimateNPC(n,488, 522, n\CurrSpeed*26)
					EndIf
                Else
					n\LastSeen = n\LastSeen - fs\FPSfactor[0]
					
					;n\Reload = 200-(100*SelectedDifficulty\aggressiveNPCs)
					If n\Reload <= 7
						n\Reload = 7
					EndIf
					
					If n\PathTimer<=0.0 Then ;update path
						n\PathStatus = FindPath(n,n\EnemyX,n\EnemyY+0.1,n\EnemyZ)
						n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6 seconds
					ElseIf n\PathTimer<=70.0 * 2.5 Then
						n\PathTimer=n\PathTimer-fs\FPSfactor[0]
						n\CurrSpeed = 0.0
						If Rand(1,35)=1 Then
							RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
						EndIf
						FinishWalking(n,488,522,n\Speed*26)
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					Else
						If n\PathStatus=2 Then
							n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
							n\CurrSpeed = 0.0
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
						ElseIf n\PathStatus=1 Then
							If n\Path[n\PathLocation]=Null Then
								If n\PathLocation > 19 Then
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								PointEntity n\Collider,n\Path[n\PathLocation]\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
								AnimateNPC(n,488, 522, n\CurrSpeed*26)
								
								newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
									;open the door and make it automatically close after 5 seconds
									If (Not n\Path[n\PathLocation]\door\open)
										sound = 0
										If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
										PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
										PlayMTFSound(MTFSFX(5),n)
									EndIf
									n\Path[n\PathLocation]\door\open = True
									If n\Path[n\PathLocation]\door\MTFClose
										n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
									EndIf
								EndIf
								
								If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
									n\PathLocation=n\PathLocation+1
								EndIf
							EndIf
							n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
						Else
							PositionEntity n\obj,n\EnemyX,n\EnemyY,n\EnemyZ,True
							If (Distance(EntityX(n\Collider,True),EntityZ(n\Collider,True),n\EnemyX,n\EnemyZ)<0.2) Or (Not EntityVisible(n\obj,n\Collider)) Then
								If Rand(1,35)=1 Then
									RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
								EndIf
								FinishWalking(n,488,522,n\Speed*26)
								If Rand(1,35)=1 Then
									For wp.Waypoints = Each WayPoints
										If (Rand(1,3)=1) Then
											If (EntityDistance(wp\obj,n\Collider)<6.0) Then
												n\EnemyX = EntityX(wp\obj,True)
												n\EnemyY = EntityY(wp\obj,True)
												n\EnemyZ = EntityZ(wp\obj,True)
												n\PathTimer = 0.0
												Exit
											EndIf											
										EndIf
									Next
								EndIf
								n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
							Else
								PointEntity n\Collider,n\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
								AnimateNPC(n,488, 522, n\CurrSpeed*26)
							EndIf
						EndIf
					EndIf
					
					If n\MTFLeader=Null And n\LastSeen<70*30 And n\LastSeen+fs\FPSfactor[0]=>70*30 Then
						If Rand(2)=1 Then 
							PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF\Searching"+Rand(1,6)+".ogg"),n)
						EndIf
					EndIf
					
					;If EntityDistance(n\Collider,Collider)>HideDistance*0.7
					;	TeleportMTFGroup(n)
					;EndIf
                EndIf
                
                If n\State2<=0.0 And n\State2+fs\FPSfactor[0] >0.0 Then
					If n\MTFLeader = Null Then
						PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF\Targetlost"+Rand(1,3)+".ogg"),n)
						If MTF_CameraCheckTimer=0.0
							If Rand(15-(7*SelectedDifficulty\aggressiveNPCs))=1 ;Maybe change this to another chance - ENDSHN
								PlayAnnouncement(SFXPath$+"Character\MTF\AnnouncCameraCheck.ogg")
								MTF_CameraCheckTimer = fs\FPSfactor[0]
							EndIf
						EndIf
					EndIf
					n\State = 0
                EndIf
                
				;B3D doesn't do short-circuit evaluation, so this retarded nesting is an optimization
                If Curr173\Idle<2 Then
					SoundVol173# = Max(Min((Distance(EntityX(Curr173\Collider), EntityZ(Curr173\Collider), Curr173\PrevX, Curr173\PrevZ) * 2.5), 1.0), 0.0)
					If OtherNPCSeesMeNPC(Curr173,n) Or (SoundVol173#>0.0 And EntityDistance(n\Collider,Curr173\Collider)<6.0) Then
						If EntityVisible(n\Collider,Curr173\Collider) Or SoundVol173#>0.0 Then	
							n\State = 2
							n\EnemyX = EntityX(Curr173\Collider,True)
							n\EnemyY = EntityY(Curr173\Collider,True)
							n\EnemyZ = EntityZ(Curr173\Collider,True)
							n\State2 = 70.0*15.0 ;give up after 15 seconds
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\173\Spotted3.ogg")
							PlayMTFSound(n\Sound, n)
							n\State3 = 0.0
							n\PathTimer=0.0
							n\PathStatus=0
						EndIf
					EndIf
				EndIf
				If Curr106\State <= 0
					If OtherNPCSeesMeNPC(Curr106,n) Or EntityDistance(n\Collider,Curr106\Collider)<3.0 Then
						If EntityVisible(n\Collider,Curr106\Collider) Then
							n\State = 4
							n\EnemyX = EntityX(Curr106\Collider,True)
							n\EnemyY = EntityY(Curr106\Collider,True)
							n\EnemyZ = EntityZ(Curr106\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							n\Target = Curr106
							If n\MTFLeader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\106\Spotted5.ogg")
								PlayMTFSound(n\Sound, n)
							EndIf
						EndIf
					EndIf
				EndIf
				If Curr096 <> Null
					If OtherNPCSeesMeNPC(Curr096,n) Then
						If EntityVisible(n\Collider,Curr096\Collider) Then
							n\State = 8
							n\EnemyX = EntityX(Curr096\Collider,True)
							n\EnemyY = EntityY(Curr096\Collider,True)
							n\EnemyZ = EntityZ(Curr096\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							If n\MTFLeader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\096\Spotted"+Rand(1,2)+".ogg")
								PlayMTFSound(n\Sound, n)
							EndIf
						EndIf
					EndIf
				EndIf
				For n2.NPCs = Each NPCs
					If n2\NPCtype = NPCtype049
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 4
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\Spotted"+Rand(1,5)+".ogg")
								PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype049_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\Player0492_1.ogg")
								PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
						
					;{~--<MOD>--~}
					
					ElseIf n2\NPCtype = NPCtype049_3 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\MTFLeader=Null
								    ;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								    ;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\049_3Spotted.ogg")
								    ;PlayMTFSound(n\Sound, n)
								;EndIf
								Exit
							EndIf
						EndIf
                    ElseIf n2\NPCtype = NPCtype008_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\MTFLeader=Null
								    ;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								    ;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\008\008_2Spotted.ogg")
								    ;PlayMTFSound(n\Sound, n)
								;EndIf
								Exit
							EndIf
						EndIf
                     ElseIf n2\NPCtype = NPCtype035_Tentacle And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\MTFLeader=Null
								    ;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								    ;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\035\Tentacle_Spotted.ogg")
								    ;PlayMTFSound(n\Sound, n)
								;EndIf
								Exit
							EndIf
						EndIf
					EndIf
					
					;{~--<END>--~}
					
				Next
                
                ;[End Block]
			Case 2 ;searching for/looking at 173
                ;[Block]
                If Curr173\Idle = 2 Then
					n\State = 0
                Else
					For n2.NPCs = Each NPCs
						If n2<>n Then
							If n2\NPCtype = NPCtypeMTF Then
								n2\State = 2
							EndIf
						EndIf
					Next
					
					Local curr173Dist# = Distance(EntityX(n\Collider,True),EntityZ(n\Collider,True),EntityX(Curr173\Collider,True),EntityZ(Curr173\Collider,True))
					
					If curr173Dist<5.0 Then
						If Curr173\Idle <> 2 Then Curr173\Idle = True
						n\State2 = 70.0*15.0
						n\PathTimer = 0.0
						Local tempDist# = 1.0
						If n\MTFLeader<>Null Then tempDist = 2.0
						If curr173Dist<tempDist Then
							If n\MTFLeader = Null Then
								n\State3=n\State3+fs\FPSfactor[0]
								;If n\State3>=70.0*10.0 Then
								If n\State3>=70.0*15.0 Then
									Curr173\Idle = 2
									If n\MTFLeader = Null Then Curr173\Target = n
									If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\173\Box"+Rand(1,3)+".ogg")
									PlayMTFSound(n\Sound, n)
								EndIf
							EndIf
							PositionEntity n\obj,EntityX(Curr173\Collider,True),EntityY(Curr173\Collider,True),EntityZ(Curr173\Collider,True),True
							PointEntity n\Collider,n\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							FinishWalking(n,488,522,n\Speed*26)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
						Else
							PositionEntity n\obj,EntityX(Curr173\Collider,True),EntityY(Curr173\Collider,True),EntityZ(Curr173\Collider,True),True
							PointEntity n\Collider,n\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
						EndIf
					Else
						If Curr173\Idle <> 2 Then Curr173\Idle = False
						If n\PathTimer<=0.0 Then ;update path
							n\PathStatus = FindPath(n,EntityX(Curr173\Collider,True),EntityY(Curr173\Collider,True)+0.1,EntityZ(Curr173\Collider,True))
							n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6 seconds
						ElseIf n\PathTimer<=70.0 * 2.5 Then
							n\PathTimer=n\PathTimer-fs\FPSfactor[0]
							n\CurrSpeed = 0.0
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
						Else
							If n\PathStatus=2 Then
								n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
								n\CurrSpeed = 0.0
								If Rand(1,35)=1 Then
									RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
								EndIf
								FinishWalking(n,488,522,n\Speed*26)
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
							ElseIf n\PathStatus=1 Then
								If n\Path[n\PathLocation]=Null Then
									If n\PathLocation > 19 Then
										n\PathLocation = 0 : n\PathStatus = 0
									Else
										n\PathLocation = n\PathLocation + 1
									EndIf
								Else
									prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
									
									PointEntity n\Collider,n\Path[n\PathLocation]\obj
									RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
									n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
									RotateEntity n\obj,-90.0,n\Angle,0.0,True
									
									n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
									
									TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
									AnimateNPC(n,488, 522, n\CurrSpeed*26)
									
									newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
									
									If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
										;open the door and make it automatically close after 5 seconds
										If (Not n\Path[n\PathLocation]\door\open)
											sound = 0
											If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
											PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
											PlayMTFSound(MTFSFX(5),n)
										EndIf
										n\Path[n\PathLocation]\door\open = True
										If n\Path[n\PathLocation]\door\MTFClose
											n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
										EndIf
									EndIf
									
									If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
										n\PathLocation=n\PathLocation+1
									EndIf
								EndIf
								n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
							Else
								n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
								n\CurrSpeed = 0.0
								If Rand(1,35)=1 Then
									RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
								EndIf
								FinishWalking(n,488,522,n\Speed*26)
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
							EndIf
						EndIf
					EndIf
                EndIf
                ;[End Block]
			Case 3 ;following a path
				;[Block]
				
				n\Angle = CurveValue(0,n\Angle,40.0)
				
				If n\PathStatus = 2 Then
					n\State = 5
					n\CurrSpeed = 0
				ElseIf n\PathStatus = 1
					If n\Path[n\PathLocation]=Null Then 
						If n\PathLocation > 19 Then 
							n\PathLocation = 0
							n\PathStatus = 0
							If n\LastSeen > 0 Then n\State = 5
						Else
							n\PathLocation = n\PathLocation + 1
						EndIf
					Else
						If n\Path[n\PathLocation]\door <> Null Then
							If n\Path[n\PathLocation]\door\open = False Then
								n\Path[n\PathLocation]\door\open = True
								n\Path[n\PathLocation]\door\timerstate = 8.0*70.0
								PlayMTFSound(MTFSFX(5),n)
							EndIf
						EndIf
						
						If dist < HideDistance*0.7 Then 
							dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj) 
							
							;If Rand(5)=1 Then 
							;	For n2.NPCs = Each NPCs
							;		If n2\NPCtype = n\NPCtype And n2<>n Then
							;			If EntityDistance(n\Collider, n2\Collider)<2 Then
							;				n\Idle = 150
							;			EndIf
							;		EndIf
							;	Next
							;EndIf
							
							PointEntity n\obj, n\Path[n\PathLocation]\obj
							
							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0
							If n\Idle = 0 Then
								n\CurrSpeed = CurveValue(n\Speed*Max(Min(dist2,1.0),0.1), n\CurrSpeed, 20.0)
								MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
								
								;If dist2 < (0.25+((n\Path[Min(n\PathLocation+1,19)]=Null)*0.3 * (n\ID Mod 3))) Then
								If EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)<0.5
									n\PathLocation = n\PathLocation + 1
								EndIf
							EndIf
						Else
							If Rand(20)=1 Then 
								PositionEntity n\Collider, EntityX(n\Path[n\PathLocation]\obj,True),EntityY(n\Path[n\PathLocation]\obj,True)+0.25,EntityZ(n\Path[n\PathLocation]\obj,True),True
								n\PathLocation = n\PathLocation + 1
								ResetEntity n\Collider
							EndIf
						EndIf
						
					EndIf
				Else
					n\CurrSpeed = 0
					n\State = 5
				EndIf
				
				
				If n\Idle = 0 And n\PathStatus = 1 Then
					If dist < HideDistance Then
						If n\Frame>959 Then
							AnimateNPC(n, 1376, 1383, 0.2, False)
							If n\Frame >1382.9 Then n\Frame = 488
						Else
							AnimateNPC(n, 488, 522, n\CurrSpeed*30)
						EndIf
					EndIf
				Else
					If dist < HideDistance Then
						If n\LastSeen > 0 Then 
							AnimateNPC(n, 78, 312, 0.2, True)
						Else
							If n\Frame<962 Then
								If n\Frame>487 Then n\Frame = 463
								AnimateNPC(n, 463, 487, 0.3, False)
								If n\Frame>486.9 Then n\Frame = 962
							Else
								AnimateNPC(n, 962, 1259, 0.3)
							EndIf
						EndIf
					EndIf
					
					n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
				EndIf
				
				n\Angle = EntityYaw(n\Collider)
				;[End Block]
			Case 4 ;SCP-106/049 detected
				;[Block]
				n\Speed = 0.03
                n\State2=n\State2-fs\FPSfactor[0]
				If n\State2 > 0.0
					If OtherNPCSeesMeNPC(n\Target,n)
						n\State2 = 70*15
					EndIf
					
					If EntityDistance(n\Target\Collider,n\Collider)>HideDistance
						If n\State2 > 70
							n\State2 = 70
						EndIf
					EndIf
					
					If EntityDistance(n\Target\Collider,n\Collider)<3.0 And n\State3 >= 0.0
						n\State3 = 70*5
					EndIf
					
					If n\State3 > 0.0
						n\PathStatus = 0
						n\PathLocation = 0
						n\Speed = 0.02
						PointEntity n\Collider,n\Target\Collider
						RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
						n\CurrSpeed = CurveValue(-n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
						AnimateNPC(n,522, 488, n\CurrSpeed*26)
						
						n\PathTimer = 1.0
						
						n\State3=Max(n\State3-fs\FPSfactor[0],0)
						
						HideEntity n\Collider
						TurnEntity n\Collider,0,180,0
						EntityPick(n\Collider, 1.0)
						If PickedEntity() <> 0 Then
							n\State3 = -70*2
						EndIf
						ShowEntity n\Collider
						TurnEntity n\Collider,0,180,0
					ElseIf n\State3 < 0.0
						n\State3 = Min(n\State3+fs\FPSfactor[0],0)
					EndIf
					
					If n\PathTimer<=0.0 Then
						If n\MTFLeader<>Null Then
							n\PathStatus = FindPath(n,EntityX(n\MTFLeader\Collider,True),EntityY(n\MTFLeader\Collider,True)+0.1,EntityZ(n\MTFLeader\Collider,True))
						Else
							For r = Each Rooms
								If ((Abs(r\x-EntityX(n\Collider,True))>12.0) Or (Abs(r\z-EntityZ(n\Collider,True))>12.0)) And (Rand(1,Max(4-Int(Abs(r\z-EntityZ(n\Collider,True)/8.0)),2))=1) Then
									If EntityDistance(r\obj,n\Target\Collider)>6.0
										x = r\x
										y = 0.1
										z = r\z
										Exit
									EndIf
								EndIf
							Next
							n\PathStatus = FindPath(n,x,y,z)
						EndIf
						If n\PathStatus = 1 Then
							While n\Path[n\PathLocation]=Null
								If n\PathLocation>19 Then Exit
								n\PathLocation=n\PathLocation+1
							Wend
							If n\PathLocation<19 Then
								If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
									If (n\Path[n\PathLocation]\door=Null) Then
										If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
											n\PathLocation=n\PathLocation+1
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						n\PathTimer = 70*10
					Else
						If n\PathStatus=1 Then
							If n\Path[n\PathLocation]=Null Then
								If n\PathLocation > 19 Then
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								PointEntity n\Collider,n\Path[n\PathLocation]\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
								AnimateNPC(n,488, 522, n\CurrSpeed*26) ;Placeholder (until running animation has been implemented)
								
								newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								If (newDist<2.0 And n\Path[n\PathLocation]\door<>Null) Then
									If (Not n\Path[n\PathLocation]\door\open)
										sound = 0
										If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
										PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
										PlayMTFSound(MTFSFX(5),n)
									EndIf
									n\Path[n\PathLocation]\door\open = True
									If n\Path[n\PathLocation]\door\MTFClose
										n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
									EndIf
								EndIf
								
								If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
									n\PathLocation=n\PathLocation+1
								EndIf
							EndIf
							n\PathTimer=n\PathTimer-fs\FPSfactor[0]
						Else
							n\PathTimer=0.0
						EndIf
					EndIf
				Else
					n\State = 0
				EndIf
				;[End Block]
			Case 5 ;looking at some other target than the player
				;[Block]
				target=CreatePivot()
				PositionEntity target, n\EnemyX, n\EnemyY, n\EnemyZ, True
				
				If dist<HideDistance Then
					AnimateNPC(n, 346, 351, 0.2, False)
				EndIf
				
				If Abs(EntityX(target)-EntityX(n\Collider)) < 55.0 And Abs(EntityZ(target)-EntityZ(n\Collider)) < 55.0 And Abs(EntityY(target)-EntityY(n\Collider))< 20.0 Then
					
					PointEntity n\obj, target
					RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),30.0), 0, True
					
					If n\PathTimer = 0 Then
						n\PathStatus = EntityVisible(n\Collider,target)
						n\PathTimer = Rand(100,200)
					Else
						n\PathTimer = Min(n\PathTimer-fs\FPSfactor[0],0.0)
					EndIf
					
					If n\PathStatus = 1 And n\Reload =< 0 Then
						dist# = Distance(EntityX(target),EntityZ(target),EntityX(n\Collider),EntityZ(n\Collider))
						
						;If dist<20.0 Then
						;	pvt = CreatePivot()
						;	
						;	PositionEntity pvt, EntityX(n\obj),EntityY(n\obj), EntityZ(n\obj)
						;	RotateEntity pvt, EntityPitch(n\Collider), EntityYaw(n\Collider),0
						;	MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
						;	
						;	If WrapAngle(EntityYaw(pvt)-EntityYaw(n\Collider))<5 Then
						;		PlaySound2(GunshotSFX, Camera, n\Collider, 20)
						;		p.Particles = CreateParticle(EntityX(n\obj, True), EntityY(n\obj, True), EntityZ(n\obj, True), 1, 0.2, 0.0, 5)
						;		PositionEntity(p\pvt, EntityX(pvt), EntityY(pvt), EntityZ(pvt))
						;		
						;		n\Reload = 7
						;	EndIf
						;	
						;	FreeEntity pvt
						;EndIf
					EndIf
				EndIf		
				
				FreeEntity target
				
				n\Angle = EntityYaw(n\Collider)
				;[End Block]
			Case 6 ;seeing the player as a 049-2 instance
				;[Block]
				
				PointEntity n\obj,Collider
				RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
				n\Angle = EntityYaw(n\Collider)
				
				AnimateNPC(n, 346, 351, 0.2, False)
				
				If n\Reload =< 0 And KillTimer = 0 Then
					If EntityVisible(n\Collider, Collider) Then
						;angle# = WrapAngle(angle - EntityYaw(n\Collider))
						;If angle < 5 Or angle > 355 Then
						If (Abs(DeltaYaw(n\Collider,Collider))<50.0)
							;prev% = KillTimer
							
							PlaySound2(GunshotSFX, Camera, n\Collider, 15)
							
							pvt% = CreatePivot()
							
							RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
							PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
							MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
							
							Shoot(EntityX(pvt),EntityY(pvt),EntityZ(pvt),0.9, True)
							n\Reload = 7
							
							FreeEntity(pvt)
							
							;If prev => 0 And KillTimer < 0 Then
								;DeathMSG=SubjectName$+". Terminated by Nine-Tailed Fox."
								;If n\MTFLeader = Null Then PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF\049\Player0492_2.ogg"),n)
							;EndIf
						EndIf	
					EndIf
				EndIf
				
				;[End Block]
			Case 7 ;just shooting
				;[Block]
				AnimateNPC(n, 346, 351, 0.2, False)
				
				RotateEntity n\Collider,0,CurveAngle(n\State2,EntityYaw(n\Collider),20),0
				n\Angle = EntityYaw(n\Collider)
				
				If n\Reload =< 0 
					LightVolume = TempLightVolume*1.2
					PlaySound2(GunshotSFX, Camera, n\Collider, 20)
					
					pvt% = CreatePivot()
					
					RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
					PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
					MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
					
					p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 1, Rnd(0.08,0.1), 0.0, 5)
					TurnEntity p\obj, 0,0,Rnd(360)
					p\Achange = -0.15
					
					FreeEntity(pvt)
					n\Reload = 7
				End If
				;[End Block]
			Case 8 ;SCP-096 spotted
				;[Block]
				n\Speed = 0.015
				n\BoneToManipulate = "head"
				n\ManipulateBone = True
				n\ManipulationType = 2
                If n\PathTimer<=0.0 Then ;update path
					If n\MTFLeader<>Null Then ;i'll follow the leader
						n\PathStatus = FindPath(n,EntityX(n\MTFLeader\Collider,True),EntityY(n\MTFLeader\Collider,True)+0.1,EntityZ(n\MTFLeader\Collider,True)) ;whatever you say boss
					Else ;i am the leader
						For r = Each Rooms
							If ((Abs(r\x-EntityX(n\Collider,True))>12.0) Or (Abs(r\z-EntityZ(n\Collider,True))>12.0)) And (Rand(1,Max(4-Int(Abs(r\z-EntityZ(n\Collider,True)/8.0)),2))=1) Then
								x = r\x
								y = 0.1
								z = r\z
								Exit
							EndIf
						Next
						n\PathStatus = FindPath(n,x,y,z) ;we're going to this room for no particular reason
					EndIf
					If n\PathStatus = 1 Then
						While n\Path[n\PathLocation]=Null
							If n\PathLocation>19 Then Exit
							n\PathLocation=n\PathLocation+1
						Wend
						If n\PathLocation<19 Then
							If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
								If (n\Path[n\PathLocation]\door=Null) Then
									If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
										n\PathLocation=n\PathLocation+1
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
					n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6-10 seconds
                ElseIf (n\PathTimer<=70.0 * 2.5) And (n\MTFLeader=Null) Then
					n\PathTimer=n\PathTimer-fs\FPSfactor[0]
					n\CurrSpeed = 0.0
					;If Rand(1,35)=1 Then
					;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
					;EndIf
					FinishWalking(n,488,522,n\Speed*26)
					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
					RotateEntity n\obj,-90.0,n\Angle,0.0,True
                Else
					If n\PathStatus=2 Then
						n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
						n\CurrSpeed = 0.0
						;If Rand(1,35)=1 Then
						;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
						;EndIf
						FinishWalking(n,488,522,n\Speed*26)
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					ElseIf n\PathStatus=1 Then
						If n\Path[n\PathLocation]=Null Then
							If n\PathLocation > 19 Then
								n\PathLocation = 0 : n\PathStatus = 0
							Else
								n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							PointEntity n\Collider,n\Path[n\PathLocation]\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							;MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
							
							newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
								;open the door and make it automatically close after 5 seconds
								If (Not n\Path[n\PathLocation]\door\open)
									sound = 0
									If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
									PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
									PlayMTFSound(MTFSFX(5),n)
								EndIf
								n\Path[n\PathLocation]\door\open = True
								If n\Path[n\PathLocation]\door\MTFClose
									n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
								EndIf
							EndIf
                            
							If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
								n\PathLocation=n\PathLocation+1
							EndIf
						EndIf
						n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
					Else
						n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
						If n\MTFLeader = Null Then
							;If Rand(1,35)=1 Then
							;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							;EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\CurrSpeed = 0.0
						ElseIf EntityDistance(n\Collider,n\MTFLeader\Collider)>1.0 Then
							PointEntity n\Collider,n\MTFLeader\Collider
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
						Else
							;If Rand(1,35)=1 Then
							;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							;EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\CurrSpeed = 0.0
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					EndIf
                EndIf
				
				If (Not EntityVisible(n\Collider,Curr096\Collider)) Or EntityDistance(n\Collider,Curr096\Collider)>6.0
					n\State = 0
				EndIf
				;[End Block]
			Case 9 ;Spotted other NPCs
				;[Block]
				If EntityVisible(n\Collider, n\Target\Collider) Then
					PointEntity n\obj,n\Target\Collider
					RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
					n\Angle = EntityYaw(n\Collider)
					
					If EntityDistance(n\Target\Collider,n\Collider)<1.3
						n\State3 = 70*2
					EndIf
					
					If n\State3 > 0.0
						n\PathStatus = 0
						n\PathLocation = 0
						n\Speed = 0.02
						n\CurrSpeed = CurveValue(-n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
						AnimateNPC(n,522, 488, n\CurrSpeed*26)
						
						n\PathTimer = 1.0
						
						n\State3=Max(n\State3-fs\FPSfactor[0],0)
					Else
						n\State3 = 0
						AnimateNPC(n, 346, 351, 0.2, False)
					EndIf
					If n\Reload =< 0 And n\Target\IsDead = False Then
						;angle# = WrapAngle(angle - EntityYaw(n\Collider))
						;If angle < 5 Or angle > 355 Then
						If (Abs(DeltaYaw(n\Collider,n\Target\Collider))<50.0)
							;prev% = KillTimer
							
							PlaySound2(GunshotSFX, Camera, n\Collider, 15)
							
							pvt% = CreatePivot()
							
							RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
							PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
							MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
							
							p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 1, Rnd(0.08,0.1), 0.0, 5)
							TurnEntity p\obj, 0,0,Rnd(360)
							p\Achange = -0.15
							If n\Target\HP > 0
								n\Target\HP = Max(n\Target\HP-Rand(5,10),0)
							Else
								If (Not n\Target\IsDead)
									If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									If n\NPCtype = NPCtype049_2
										n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\Player0492_2.ogg")
										PlayMTFSound(n\Sound, n)
									;ElseIf n\NPCtype = NPCtype049_3
									    ;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\Player049_3_2.ogg")
									    ;PlayMTFSound(n\Sound, n)
									;ElseIf n\NPCtype = NPCtype008_2
									    ;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\008\008_2.ogg")
									    ;PlayMTFSound(n\Sound, n)
									;Else;If n\NPCtype = NPCtype008_1
									    ;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\008\008_1.ogg")
									    ;PlayMTFSound(n\Sound, n)
									EndIf
								EndIf
								SetNPCFrame(n\Target,133)
								n\Target\IsDead = True
								n\Target = Null
								n\State = 0
								Return
							EndIf
							n\Reload = 7
							
							FreeEntity(pvt)
						EndIf	
					EndIf
					n\PathStatus = 0
				Else
					If n\PathTimer<=0.0 Then
						n\PathStatus = FindPath(n,EntityX(n\Target\Collider),EntityY(n\Target\Collider),EntityZ(n\Target\Collider))
						If n\PathStatus = 1 Then
							While n\Path[n\PathLocation]=Null
								If n\PathLocation>19 Then Exit
								n\PathLocation=n\PathLocation+1
							Wend
							If n\PathLocation<19 Then
								If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
									If (n\Path[n\PathLocation]\door=Null) Then
										If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
											n\PathLocation=n\PathLocation+1
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						n\PathTimer = 70*10
					Else
						If n\PathStatus=1 Then
							If n\Path[n\PathLocation]=Null Then
								If n\PathLocation > 19 Then
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								PointEntity n\Collider,n\Path[n\PathLocation]\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
								AnimateNPC(n,488, 522, n\CurrSpeed*26)
								
								newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
									If (Not n\Path[n\PathLocation]\door\open)
										sound = 0
										If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
										PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
										PlayMTFSound(MTFSFX(5),n)
									EndIf
									n\Path[n\PathLocation]\door\open = True
									If n\Path[n\PathLocation]\door\MTFClose
										n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
									EndIf
								EndIf
								
								If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
									n\PathLocation=n\PathLocation+1
								EndIf
							EndIf
							n\PathTimer=n\PathTimer-fs\FPSfactor[0]
						Else
							n\PathTimer=0.0
						EndIf
					EndIf
				EndIf
				
				If n\Target\IsDead = True Then
					n\Target = Null
					n\State = 0
				EndIf
				
				;[End Block]
		End Select
		
		If n\CurrSpeed > 0.01 Then
			If prevFrame > 500 And n\Frame<495
				PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
			ElseIf prevFrame < 505 And n\Frame=>505
				PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
			EndIf
		EndIf
		
		If chs\NoTarget And n\State = 1 Then n\State = 0
		
		If n\State <> 3 And n\State <> 5 And n\State <> 6 And n\State <> 7
			If n\MTFLeader<>Null Then
				If EntityDistance(n\Collider,n\MTFLeader\Collider)<0.7 Then
					PointEntity n\Collider,n\MTFLeader\Collider
					RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
					
					TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)-45)* 0.01 * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)-45)* 0.01 * fs\FPSfactor[0], True
				EndIf
			Else
				For n2.NPCs = Each NPCs
					If n2<>n And n2\IsDead=False Then
						If Abs(DeltaYaw(n\Collider,n2\Collider))<80.0 Then
							If EntityDistance(n\Collider,n2\Collider)<0.7 Then							
								TranslateEntity n2\Collider, Cos(EntityYaw(n\Collider,True)+90)* 0.01 * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90)* 0.01 * fs\FPSfactor[0], True
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		EndIf
		
		;teleport back to the facility if fell through the floor
		If n\State <> 6 And n\State <> 7
			If (EntityY(n\Collider) < -10.0) Then
				TeleportCloser(n)
			EndIf
		EndIf
		
		RotateEntity n\obj,-90.0,n\Angle,0.0,True
		
		PositionEntity n\obj,EntityX(n\Collider,True),EntityY(n\Collider,True)-0.15,EntityZ(n\Collider,True),True
		
	EndIf
End Function

Function UpdateMTF2Unit(n.NPCs)
	;[Block]
	Local fs.FPS_Settings = First FPS_Settings
	
	If n\NPCtype<>NPCtypeMTF2 Then
		Local realType$ = ""
		Select n\NPCtype
			Case NPCtype173
                realType = "173"
			Case NPCtype106
                realType = "106"
			Case NPCtypeGuard
                realType = "guard"
			Case NPCtypeD
                realType = "d"
			Case NPCtype372
                realType = "372"
			Case NPCtypeApache
                realType = "apache"
			Case NPCtype096
                realType = "096"
			Case NPCtype049
                realType = "049"
			Case NPCtype049_2
                realType = "049-2"
			Case NPCtype513_1
                realType = "513-1"
			Case NPCtype035_Tentacle
                realType = "035-Tentacle"
			Case NPCtype860_2
                realType = "860-2"
			Case NPCtype939
                realType = "939"
			Case NPCtype066
                realType = "066"
			Case NPCtypePdPlane
                realType = "PDPlane"
			Case NPCtype966
                realType = "966"
			Case NPCtype1048_A
                realType = "1048-A"
			Case NPCtype1499_1
				realType = "1499-1"
			Case NPCtype008_1
			    realType = "008"
			Case NPCtypeMTF
			    realType = "MTF"
			
			;{~--<MOD>--~}
			
            Case NPCtype178_1
                realType = "178-1"
            Case NPCtype008_2
			    realType = "008-2"
			Case NPCtype049_3
			    realType = "049-3"
		    Case NPCtype650
                realType = "650"
            Case NPCtype457
                realType = "457"
            Case NPCtypeCI
                realType = "CI"
            Case NPCtypeVehicle
                realType = "Vehicle"

            ;{~--<END>--~}

		End Select
		RuntimeError "Called UpdateMTF2Unit on "+realType
	EndIf
	;[End Block]
	
	Local x#,y#,z#
	Local r.Rooms
	Local prevDist#,newDist#
	Local n2.NPCs
	
	Local p.Particles, target, dist#, dist2#
	Local dist_to_player#
	
	If n\IsDead Then
		n\BlinkTimer = -1.0
		SetNPCFrame(n, 532)
		Return
	EndIf
	
	n\MaxGravity = 0.03
	
	n\BlinkTimer = n\BlinkTimer - fs\FPSfactor[0]
	If n\BlinkTimer<=-5.0 Then 
		;only play the "blinking" sound clip if searching/containing 173
		If n\State = 2 
		    If OtherNPCSeesMeNPC(Curr173,n) Or EntityDistance(n\Collider,Curr173\Collider)<3.0
			    PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF2\173\BLINKING"+Rand(1,4)+".ogg"),n)
			EndIf
		EndIf
		n\BlinkTimer = 70.0*Rnd(10.0,15.0)
	EndIf	
	
	n\Reload = n\Reload - fs\FPSfactor[0]
	
	Local prevFrame# = n\Frame
	
	n\BoneToManipulate = ""
	;n\BoneToManipulate2 = ""
	n\ManipulateBone = True
	n\ManipulationType = 0
	n\NPCNameInSection = "MTF2"
	
	If Int(n\State) <> 1 Then n\PrevState = 0
	
	If n\Idle>0.0 Then
		FinishWalking(n,577,628,0.015*26)
		n\Idle=n\Idle-fs\FPSfactor[0]
		If n\Idle<=0.0 Then n\Idle = 0.0
	Else
		Select Int(n\State) ;what is this MTF2 doing
			Case 0 ;wandering around
                ;[Block]
                n\Speed = 0.015
                If n\PathTimer<=0.0 Then ;update path
					If n\MTF2Leader<>Null Then ;i'll follow the leader
						n\PathStatus = FindPath(n,EntityX(n\MTF2Leader\Collider,True),EntityY(n\MTF2Leader\Collider,True)+0.1,EntityZ(n\MTF2Leader\Collider,True)) ;whatever you say boss
					Else ;i am the leader
						If Curr173\Idle<>2
							For r = Each Rooms
								If ((Abs(r\x-EntityX(n\Collider,True))>12.0) Or (Abs(r\z-EntityZ(n\Collider,True))>12.0)) And (Rand(1,Max(4-Int(Abs(r\z-EntityZ(n\Collider,True)/8.0)),2))=1) Then
									x = r\x
									y = 0.1
									z = r\z
									Exit
								EndIf
							Next
						Else
							Local tmp = False
							If EntityDistance(n\Collider,Curr173\Collider)>4.0
								If (Not EntityVisible(n\Collider,Curr173\Collider))
									tmp = True
								EndIf
							EndIf
							
							If (Not tmp)
								For r = Each Rooms
									If r\RoomTemplate\Name$ = "room173"
										Local foundChamber% = False
										Local pvt% = CreatePivot()
										PositionEntity pvt%,EntityX(r\obj,True)+4736*RoomScale,0.5,EntityZ(r\obj,True)+1692*RoomScale
										
										If Distance(EntityX(pvt%),EntityZ(pvt%),EntityX(n\Collider),EntityZ(n\Collider))<3.5
											foundChamber% = True
										EndIf
										
										If Curr173\Idle = 3 And Distance(EntityX(pvt%),EntityZ(pvt%),EntityX(n\Collider),EntityZ(n\Collider)) > 4.0
											If r\RoomDoors[1]\open = True Then UseDoor(r\RoomDoors[1],False)
										EndIf
										
										FreeEntity pvt%
										
										If Distance(EntityX(n\Collider),EntityZ(n\Collider),EntityX(r\obj,True)+4736*RoomScale,EntityZ(r\obj,True)+1692*RoomScale)>1.6 And (Not foundChamber)
											x = EntityX(r\obj,True)+4736*RoomScale
											y = 0.1
											z = EntityZ(r\obj,True)+1692*RoomScale
											Exit
										ElseIf Distance(EntityX(n\Collider),EntityZ(n\Collider),EntityX(r\obj,True)+4736*RoomScale,EntityZ(r\obj,True)+1692*RoomScale)>1.6 And foundChamber
											n\PathX = EntityX(r\obj,True)+4736*RoomScale
											n\PathZ = EntityZ(r\obj,True)+1692*RoomScale
											Exit
										Else
											Curr173\Idle = 3
											Curr173\Target = Null
											Curr173\IsDead = True
											If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
											n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\173\Cont"+Rand(1,6)+".ogg")
											PlayMTFSound(n\Sound, n)
											;PlayAnnouncement(SFXPath$+"Character\MTF2\Announc173Contain.ogg")
											r\RoomDoors[1]\MTFClose = True
                                         	UseDoor(r\RoomDoors[1],False)
											r\RoomDoors[1]\open = False
											Exit
										EndIf
									EndIf
								Next
							Else
								x = EntityX(Curr173\Collider)
								y = 0.1
								z = EntityZ(Curr173\Collider)
							EndIf
						EndIf
						If n\PathX=0 Then n\PathStatus = FindPath(n,x,y,z) ;we're going to this room for no particular reason
					EndIf
					If n\PathStatus = 1 Then
						While n\Path[n\PathLocation]=Null
							If n\PathLocation>19 Then Exit
							n\PathLocation=n\PathLocation+1
						Wend
						If n\PathLocation<19 Then
							If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
								If (n\Path[n\PathLocation]\door=Null) Then
									If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
										n\PathLocation=n\PathLocation+1
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
					n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6-10 seconds
                ElseIf (n\PathTimer<=70.0 * 2.5) And (n\MTF2Leader=Null) Then
					n\PathTimer=n\PathTimer-fs\FPSfactor[0]
					n\CurrSpeed = 0.0
					If Rand(1,35)=1 Then
						RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
					EndIf
					FinishWalking(n,577, 628,n\Speed*26)
					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
					RotateEntity n\obj,-90.0,n\Angle,0.0,True
                Else
					If n\PathStatus=2 Then
						n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
						n\CurrSpeed = 0.0
						If Rand(1,35)=1 Then
							RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
						EndIf
						FinishWalking(n,577, 628,n\Speed*26)
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					ElseIf n\PathStatus=1 Then
						If n\Path[n\PathLocation]=Null Then
							If n\PathLocation > 19 Then
								n\PathLocation = 0 : n\PathStatus = 0
							Else
								n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							PointEntity n\Collider,n\Path[n\PathLocation]\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							;MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
							
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,577, 628, n\CurrSpeed*30)
							
							newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
								;open the door and make it automatically close after 5 seconds
								If (Not n\Path[n\PathLocation]\door\open)
									Local sound = 0
									If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
									PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
									PlayMTFSound(MTF2SFX(5),n)
								EndIf
								n\Path[n\PathLocation]\door\open = True
								If n\Path[n\PathLocation]\door\MTFClose
									n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
								EndIf
							EndIf
                            
							If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
								n\PathLocation=n\PathLocation+1
							EndIf
						EndIf
						n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
					ElseIf n\PathX#<>0.0
						pvt = CreatePivot()
						PositionEntity pvt,n\PathX#,0.5,n\PathZ#
						
						PointEntity n\Collider,pvt
						RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
						
						n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
						AnimateNPC(n,577, 628, n\CurrSpeed*30)
						
						If Distance(EntityX(n\Collider),EntityZ(n\Collider),n\PathX#,n\PathZ#)<0.2
							n\PathX# = 0.0
							n\PathZ# = 0.0
							n\PathTimer = 70.0 * Rnd(6.0,10.0)
						EndIf
						
						FreeEntity pvt
					Else
						n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
						If n\MTF2Leader = Null Then
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,577, 628,n\Speed*26)
							n\CurrSpeed = 0.0
						ElseIf EntityDistance(n\Collider,n\MTF2Leader\Collider)>1.0 Then
							PointEntity n\Collider,n\MTF2Leader\Collider
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,577, 628, n\CurrSpeed*30)
						Else
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,577, 628,n\Speed*26)
							n\CurrSpeed = 0.0
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					EndIf
                EndIf
                
				Local temp = MeNPCSeesPlayer(n)
				
				If chs\NoTarget Then temp = False
				
                If temp>False Then
					If n\LastSeen > 0 And n\LastSeen < 70*15 Then
						If temp < 2
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\ThereHeIs"+Rand(1,6)+".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
					Else
						If temp = True
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\Stop"+Rand(1,6)+".ogg")
							PlayMTFSound(n\Sound, n)
						ElseIf temp = 2
							;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							;n\Sound = MTFSFX(Rand(0,3))
							;PlayMTFSound(n\Sound, n)
							PlayMTFSound(MTFSFX(Rand(0,3)),n)
						EndIf
					EndIf
					
					n\LastSeen = (70*Rnd(30,40))
					n\LastDist = 1
					
					n\State = 1
					n\EnemyX = EntityX(Collider,True)
					n\EnemyY = EntityY(Collider,True)
					n\EnemyZ = EntityZ(Collider,True)
					n\State2 = 70.0*(15.0*temp) ;give up after 15 seconds (30 seconds if detected by loud noise, over camera: 45)
					n\PathTimer=0.0
					n\PathStatus=0
					n\Reload = 200-(100*SelectedDifficulty\aggressiveNPCs)
					
                EndIf
				
				;B3D doesn't do short-circuit evaluation, so this retarded nesting is an optimization
                If Curr173\Idle<2 Then
					Local SoundVol173# = Max(Min((Distance(EntityX(Curr173\Collider), EntityZ(Curr173\Collider), Curr173\PrevX, Curr173\PrevZ) * 2.5), 1.0), 0.0)
					If OtherNPCSeesMeNPC(Curr173,n) Or (SoundVol173#>0.0 And EntityDistance(n\Collider,Curr173\Collider)<6.0) Then
						If EntityVisible(n\Collider,Curr173\Collider) Or SoundVol173#>0.0 Then							
							n\State = 2
							n\EnemyX = EntityX(Curr173\Collider,True)
							n\EnemyY = EntityY(Curr173\Collider,True)
							n\EnemyZ = EntityZ(Curr173\Collider,True)
							n\State2 = 70.0*15.0 ;give up after 15 seconds
							n\State3 = 0.0
							n\PathTimer=0.0
							n\PathStatus=0
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\173\Spotted"+Rand(1,4)+".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
					EndIf
				EndIf
				
				If Curr106\State <= 0
					If OtherNPCSeesMeNPC(Curr106,n) Or EntityDistance(n\Collider,Curr106\Collider)<3.0 Then
						If EntityVisible(n\Collider,Curr106\Collider) Then
							n\State = 4
							n\EnemyX = EntityX(Curr106\Collider,True)
							n\EnemyY = EntityY(Curr106\Collider,True)
							n\EnemyZ = EntityZ(Curr106\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							n\Target = Curr106
							;If n\MTF2Leader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\106\Spotted"+Rand(1,6)+".ogg")
								PlayMTFSound(n\Sound, n)
							;EndIf
						EndIf
					EndIf
				EndIf
				
				If Curr096 <> Null
					If OtherNPCSeesMeNPC(Curr096,n) Then
						If EntityVisible(n\Collider,Curr096\Collider) Then
							n\State = 8
							n\EnemyX = EntityX(Curr096\Collider,True)
							n\EnemyY = EntityY(Curr096\Collider,True)
							n\EnemyZ = EntityZ(Curr096\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\096\Spotted"+Rand(1,2)+".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
					EndIf
				EndIf
				
				For n2.NPCs = Each NPCs
					If n2\NPCtype = NPCtype049
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 4
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\049\Spotted"+Rand(1,?)+".ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype008_1 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\008\008_1spotted.ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype049_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\049\Player0492_1.ogg")
								PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
						
					;{~--<MOD>--~}
					
					ElseIf n2\NPCtype = NPCtype008_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\008\008_2Spotted.ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype049_3 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\049\049_3Spotted.ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype035_Tentacle And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\035\Tentalce_Spotted.ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					EndIf
					
					;{~--<END>--~}
					
				Next
                ;[End Block]
			Case 1 ;searching for player
                ;[Block]
                n\Speed = 0.015
                n\State2=n\State2-fs\FPSfactor[0]

                dist_to_player = EntityDistance(Collider, n\Collider)
                If MeNPCSeesPlayer(n) = True Then
					
					;if close enough, start shooting at the player
					If playerDist < 5.2 Then
						
						Local angle# = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
						
						RotateEntity(n\Collider, 0, CurveAngle(angle, EntityYaw(n\Collider), 10.0), 0, True)
						n\Angle = EntityYaw(n\Collider)
						
						If n\Reload =< 0 And KillTimer = 0 Then
							If EntityVisible(n\Collider, Camera) Then
								angle# = WrapAngle(angle - EntityYaw(n\Collider))
								If angle < 5 Or angle > 355 Then 
									prev% = KillTimer
									
									PlaySound2(Gunshot4SFX, Camera, n\Collider, 15)
									
									pvt% = CreatePivot()
									
									RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
									PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
									MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)

									Shoot(EntityX(pvt),EntityY(pvt), EntityZ(pvt),((25/dist_to_player)*(1/dist_to_player)), True)
									
									n\Reload = 7
									
									FreeEntity(pvt)
									
									DeathMSG=SubjectName$+". Died of blood loss after being shot by See No Evil."
									
									;player killed -> "target terminated"
									If prev => 0 And KillTimer < 0 Then
										DeathMSG=SubjectName$+". Terminated by See No Evil."
										PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF2\Targetterminated"+Rand(1,5)+".ogg"),n)
									EndIf
								EndIf	
							EndIf
						EndIf
						
						For n2.NPCs = Each NPCs
							If n2\NPCtype = NPCtypeMTF2 And n2 <> n
								If n2\State = 0
									If EntityDistance(n\Collider,n2\Collider)<6.0
										n\PrevState = 1
										n2\LastSeen = (70*Rnd(30,40))
										n2\LastDist = 1
										
										n2\State = 1
										n2\EnemyX = EntityX(Collider,True)
										n2\EnemyY = EntityY(Collider,True)
										n2\EnemyZ = EntityZ(Collider,True)
										n2\State2 = n\State2
										n2\PathTimer=0.0
										n2\PathStatus=0
										n2\Reload = 200-(100*SelectedDifficulty\aggressiveNPCs)
										n2\PrevState = 0
									EndIf
								EndIf
							EndIf
						Next
						
						If n\PrevState = 1
							SetNPCFrame(n,423)
							n\PrevState = 2
						ElseIf n\PrevState=2
							If n\Frame>407 ;
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
								AnimateNPC(n, 637, 746, 0.4, False) ;
								If n\Frame>636.9 Then n\Frame = 228 ;
							Else
								AnimateNPC(n, 228, 401, 0.2, False) ;
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
							EndIf
						Else
							If n\Frame>920 Then
								AnimateNPC(n, 1374, 1383, 0.3, False)
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
								If n\Frame>1382.9 Then n\Frame = 78
							Else
								AnimateNPC(n, 228, 401, 0.2, False)
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
							EndIf
						EndIf
					Else
						PositionEntity n\obj,n\EnemyX,n\EnemyY,n\EnemyZ,True
						PointEntity n\Collider,n\obj
						RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
						
						n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
						AnimateNPC(n,577, 628, n\CurrSpeed*30)
					EndIf
                Else
					n\LastSeen = n\LastSeen - fs\FPSfactor[0]
					
					;n\Reload = 200-(100*SelectedDifficulty\aggressiveNPCs)
					If n\Reload <= 7
						n\Reload = 7
					EndIf
					
					If n\PathTimer<=0.0 Then ;update path
						n\PathStatus = FindPath(n,n\EnemyX,n\EnemyY+0.1,n\EnemyZ)
						n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6 seconds
					ElseIf n\PathTimer<=70.0 * 2.5 Then
						n\PathTimer=n\PathTimer-fs\FPSfactor[0]
						n\CurrSpeed = 0.0
						If Rand(1,35)=1 Then
							RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
						EndIf
						FinishWalking(n,577, 628,n\Speed*26)
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					Else
						If n\PathStatus=2 Then
							n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
							n\CurrSpeed = 0.0
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,577, 628,n\Speed*26)
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
						ElseIf n\PathStatus=1 Then
							If n\Path[n\PathLocation]=Null Then
								If n\PathLocation > 19 Then
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								PointEntity n\Collider,n\Path[n\PathLocation]\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
								AnimateNPC(n,577, 628, n\CurrSpeed*30)
								
								newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
									;open the door and make it automatically close after 5 seconds
									If (Not n\Path[n\PathLocation]\door\open)
										sound = 0
										If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
										PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
										PlayMTFSound(MTF2SFX(5),n)
									EndIf
									n\Path[n\PathLocation]\door\open = True
									If n\Path[n\PathLocation]\door\MTFClose
										n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
									EndIf
								EndIf
								
								If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
									n\PathLocation=n\PathLocation+1
								EndIf
							EndIf
							n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
						Else
							PositionEntity n\obj,n\EnemyX,n\EnemyY,n\EnemyZ,True
							If (Distance(EntityX(n\Collider,True),EntityZ(n\Collider,True),n\EnemyX,n\EnemyZ)<0.2) Or (Not EntityVisible(n\obj,n\Collider)) Then
								If Rand(1,35)=1 Then
									RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
								EndIf
								FinishWalking(n,577, 628,n\Speed*26)
								If Rand(1,35)=1 Then
									For wp.Waypoints = Each WayPoints
										If (Rand(1,3)=1) Then
											If (EntityDistance(wp\obj,n\Collider)<6.0) Then
												n\EnemyX = EntityX(wp\obj,True)
												n\EnemyY = EntityY(wp\obj,True)
												n\EnemyZ = EntityZ(wp\obj,True)
												n\PathTimer = 0.0
												Exit
											EndIf											
										EndIf
									Next
								EndIf
								n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
							Else
								PointEntity n\Collider,n\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
								AnimateNPC(n,577, 628, n\CurrSpeed*30)
							EndIf
						EndIf
					EndIf
					
					If n\MTF2Leader=Null And n\LastSeen<70*30 And n\LastSeen+fs\FPSfactor[0]=>70*30 Then
						If Rand(2)=1 Then 
							PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF2\Searching"+Rand(1,6)+".ogg"),n)
						EndIf
					EndIf
					
					;If EntityDistance(n\Collider,Collider)>HideDistance*0.7
					;	TeleportMTFGroup(n)
					;EndIf
                EndIf
                
                If n\State2<=0.0 And n\State2+fs\FPSfactor[0] >0.0 Then
					If n\MTF2Leader = Null Then
						PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF2\Targetlost"+Rand(1,4)+".ogg"),n)
						If MTF2_CameraCheckTimer=0.0
							If Rand(15-(7*SelectedDifficulty\aggressiveNPCs))=1 ;Maybe change this to another chance - ENDSHN
								;PlayAnnouncement(SFXPath$+"Character\MTF2\AnnouncCameraCheck.ogg")
								MTF2_CameraCheckTimer = fs\FPSfactor[0]
							EndIf
						EndIf
					EndIf
					n\State = 0
                EndIf
                
				;B3D doesn't do short-circuit evaluation, so this retarded nesting is an optimization
                If Curr173\Idle<2 Then
					SoundVol173# = Max(Min((Distance(EntityX(Curr173\Collider), EntityZ(Curr173\Collider), Curr173\PrevX, Curr173\PrevZ) * 2.5), 1.0), 0.0)
					If OtherNPCSeesMeNPC(Curr173,n) Or (SoundVol173#>0.0 And EntityDistance(n\Collider,Curr173\Collider)<6.0) Then
						If EntityVisible(n\Collider,Curr173\Collider) Or SoundVol173#>0.0 Then	
							n\State = 2
							n\EnemyX = EntityX(Curr173\Collider,True)
							n\EnemyY = EntityY(Curr173\Collider,True)
							n\EnemyZ = EntityZ(Curr173\Collider,True)
							n\State2 = 70.0*15.0 ;give up after 15 seconds
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\173\Spotted3.ogg")
							PlayMTFSound(n\Sound, n)
							n\State3 = 0.0
							n\PathTimer=0.0
							n\PathStatus=0
						EndIf
					EndIf
				EndIf
				
				If Curr106\State <= 0
					If OtherNPCSeesMeNPC(Curr106,n) Or EntityDistance(n\Collider,Curr106\Collider)<3.0 Then
						If EntityVisible(n\Collider,Curr106\Collider) Then
							n\State = 4
							n\EnemyX = EntityX(Curr106\Collider,True)
							n\EnemyY = EntityY(Curr106\Collider,True)
							n\EnemyZ = EntityZ(Curr106\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							n\Target = Curr106
							If n\MTF2Leader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\106\Spotted4.ogg")
								PlayMTFSound(n\Sound, n)
							EndIf
						EndIf
					EndIf
				EndIf
				
				If Curr096 <> Null
					If OtherNPCSeesMeNPC(Curr096,n) Then
						If EntityVisible(n\Collider,Curr096\Collider) Then
							n\State = 8
							n\EnemyX = EntityX(Curr096\Collider,True)
							n\EnemyY = EntityY(Curr096\Collider,True)
							n\EnemyZ = EntityZ(Curr096\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							If n\MTF2Leader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\096\Spotted"+Rand(1,2)+".ogg")
								PlayMTFSound(n\Sound, n)
							EndIf
						EndIf
					EndIf
				EndIf
				
				For n2.NPCs = Each NPCs
					If n2\NPCtype = NPCtype049
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 4
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\Spotted"+Rand(1,5)+".ogg")
								PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype049_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\MTF2Leader=Null
									If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\049\Player0492_1.ogg")
									PlayMTFSound(n\Sound, n)
								;EndIf
								Exit
							EndIf
						EndIf
						
					;{~--<MOD>--~}
					
					ElseIf n2\NPCtype = NPCtype049_3 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\MTF2Leader=Null
									;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\049\049_3Spotted.ogg")
									;PlayMTFSound(n\Sound, n)
								;EndIf
								Exit
							EndIf
						EndIf
                    ElseIf n2\NPCtype = NPCtype008_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\MTF2Leader=Null
									;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\008\008_2Spotted.ogg")
									;PlayMTFSound(n\Sound, n)
								;EndIf
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype035_Tentacle And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								;If n\MTF2Leader=Null
									;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\035\Tentacle_Spotted.ogg")
									;PlayMTFSound(n\Sound, n)
								;EndIf
								Exit
							EndIf
						EndIf
					EndIf
					
					;{~--<END>--~}
					
				Next
                
                ;[End Block]
			Case 2 ;searching for/looking at SCP-173
                ;[Block]
                If Curr173\Idle = 2 Then
					n\State = 0
                Else
					For n2.NPCs = Each NPCs
						If n2<>n Then
							If n2\NPCtype = NPCtypeMTF2 Then
								n2\State = 2
							EndIf
						EndIf
					Next
					
					Local curr173Dist# = Distance(EntityX(n\Collider,True),EntityZ(n\Collider,True),EntityX(Curr173\Collider,True),EntityZ(Curr173\Collider,True))
					
					If curr173Dist<5.0 Then
						If Curr173\Idle <> 2 Then Curr173\Idle = True
						n\State2 = 70.0*15.0
						n\PathTimer = 0.0
						Local tempDist# = 1.0
						If n\MTF2Leader<>Null Then tempDist = 2.0
						If curr173Dist<tempDist Then
							If n\MTF2Leader = Null Then
								n\State3=n\State3+fs\FPSfactor[0]
								;If n\State3>=70.0*10.0 Then
								If n\State3>=70.0*15.0 Then
									Curr173\Idle = 2
									If n\MTF2Leader = Null Then Curr173\Target = n
									If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\173\Box"+Rand(1,4)+".ogg")
									PlayMTFSound(n\Sound, n)
								EndIf
							EndIf
							PositionEntity n\obj,EntityX(Curr173\Collider,True),EntityY(Curr173\Collider,True),EntityZ(Curr173\Collider,True),True
							PointEntity n\Collider,n\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							FinishWalking(n,577, 628,n\Speed*26)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
						Else
							PositionEntity n\obj,EntityX(Curr173\Collider,True),EntityY(Curr173\Collider,True),EntityZ(Curr173\Collider,True),True
							PointEntity n\Collider,n\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,577, 628, n\CurrSpeed*30)
						EndIf
					Else
						If Curr173\Idle <> 2 Then Curr173\Idle = False
						If n\PathTimer<=0.0 Then ;update path
							n\PathStatus = FindPath(n,EntityX(Curr173\Collider,True),EntityY(Curr173\Collider,True)+0.1,EntityZ(Curr173\Collider,True))
							n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6 seconds
						ElseIf n\PathTimer<=70.0 * 2.5 Then
							n\PathTimer=n\PathTimer-fs\FPSfactor[0]
							n\CurrSpeed = 0.0
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,577, 628,n\Speed*26)
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
						Else
							If n\PathStatus=2 Then
								n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
								n\CurrSpeed = 0.0
								If Rand(1,35)=1 Then
									RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
								EndIf
								FinishWalking(n,577, 628,n\Speed*26)
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
							ElseIf n\PathStatus=1 Then
								If n\Path[n\PathLocation]=Null Then
									If n\PathLocation > 19 Then
										n\PathLocation = 0 : n\PathStatus = 0
									Else
										n\PathLocation = n\PathLocation + 1
									EndIf
								Else
									prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
									
									PointEntity n\Collider,n\Path[n\PathLocation]\obj
									RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
									n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
									RotateEntity n\obj,-90.0,n\Angle,0.0,True
									
									n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
									
									TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
									AnimateNPC(n,577, 628, n\CurrSpeed*30)
									
									newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
									
									If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
										;open the door and make it automatically close after 5 seconds
										If (Not n\Path[n\PathLocation]\door\open)
											sound = 0
											If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
											PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
											PlayMTFSound(MTF2SFX(5),n)
										EndIf
										n\Path[n\PathLocation]\door\open = True
										If n\Path[n\PathLocation]\door\MTFClose
											n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
										EndIf
									EndIf
									
									If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
										n\PathLocation=n\PathLocation+1
									EndIf
								EndIf
								n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
							Else
								n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
								n\CurrSpeed = 0.0
								If Rand(1,35)=1 Then
									RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
								EndIf
								FinishWalking(n,577, 628,n\Speed*26)
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
							EndIf
						EndIf
					EndIf
                EndIf
                ;[End Block]
			Case 3 ;following a path
				;[Block]
				
				n\Angle = CurveValue(0,n\Angle,40.0)
				
				If n\PathStatus = 2 Then
					n\State = 5
					n\CurrSpeed = 0
				ElseIf n\PathStatus = 1
					If n\Path[n\PathLocation]=Null Then 
						If n\PathLocation > 19 Then 
							n\PathLocation = 0
							n\PathStatus = 0
							If n\LastSeen > 0 Then n\State = 5
						Else
							n\PathLocation = n\PathLocation + 1
						EndIf
					Else
						If n\Path[n\PathLocation]\door <> Null Then
							If n\Path[n\PathLocation]\door\open = False Then
								n\Path[n\PathLocation]\door\open = True
								n\Path[n\PathLocation]\door\timerstate = 8.0*70.0
								PlayMTFSound(MTF2SFX(5),n)
							EndIf
						EndIf
						
						If dist < HideDistance*0.7 Then 
							dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj) 
							
							;If Rand(5)=1 Then 
							;	For n2.NPCs = Each NPCs
							;		If n2\NPCtype = n\NPCtype And n2<>n Then
							;			If EntityDistance(n\Collider, n2\Collider)<2 Then
							;				n\Idle = 150
							;			EndIf
							;		EndIf
							;	Next
							;EndIf
							
							PointEntity n\obj, n\Path[n\PathLocation]\obj
							
							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0
							If n\Idle = 0 Then
								n\CurrSpeed = CurveValue(n\Speed*Max(Min(dist2,1.0),0.1), n\CurrSpeed, 20.0)
								MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
								
								;If dist2 < (0.25+((n\Path[Min(n\PathLocation+1,19)]=Null)*0.3 * (n\ID Mod 3))) Then
								If EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)<0.5
									n\PathLocation = n\PathLocation + 1
								EndIf
							EndIf
						Else
							If Rand(20)=1 Then 
								PositionEntity n\Collider, EntityX(n\Path[n\PathLocation]\obj,True),EntityY(n\Path[n\PathLocation]\obj,True)+0.25,EntityZ(n\Path[n\PathLocation]\obj,True),True
								n\PathLocation = n\PathLocation + 1
								ResetEntity n\Collider
							EndIf
						EndIf
						
					EndIf
				Else
					n\CurrSpeed = 0
					n\State = 5
				EndIf
				
				
				If n\Idle = 0 And n\PathStatus = 1 Then
					If dist < HideDistance Then
						If n\Frame>920 Then
							AnimateNPC(n, 1376, 1383, 0.2, False)
							If n\Frame >1382.9 Then n\Frame = 488
						Else
							AnimateNPC(n, 488, 522, n\CurrSpeed*30)
						EndIf
					EndIf
				Else
					If dist < HideDistance Then
						If n\LastSeen > 0 Then 
							AnimateNPC(n, 78, 312, 0.2, True)
						Else
							If n\Frame<962 Then
								If n\Frame>487 Then n\Frame = 463
								AnimateNPC(n, 463, 487, 0.3, False)
								If n\Frame>486.9 Then n\Frame = 962
							Else
								AnimateNPC(n, 962, 1259, 0.3)
							EndIf
						EndIf
					EndIf
					
					n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
				EndIf
				
				n\Angle = EntityYaw(n\Collider)
				;[End Block]
			Case 4 ;SCP-106/SCP-049 detected
				;[Block]
				n\Speed = 0.03
                n\State2=n\State2-fs\FPSfactor[0]
				If n\State2 > 0.0
					If OtherNPCSeesMeNPC(n\Target,n)
						n\State2 = 70*15
					EndIf
					
					If EntityDistance(n\Target\Collider,n\Collider)>HideDistance
						If n\State2 > 70
							n\State2 = 70
						EndIf
					EndIf
					
					If EntityDistance(n\Target\Collider,n\Collider)<3.0 And n\State3 >= 0.0
						n\State3 = 70*5
					EndIf
					
					If n\State3 > 0.0
						n\PathStatus = 0
						n\PathLocation = 0
						n\Speed = 0.02
						PointEntity n\Collider,n\Target\Collider
						RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
						n\CurrSpeed = CurveValue(-n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
						AnimateNPC(n,628, 577, n\CurrSpeed*30)
						
						n\PathTimer = 1.0
						
						n\State3=Max(n\State3-fs\FPSfactor[0],0)
						
						HideEntity n\Collider
						TurnEntity n\Collider,0,180,0
						EntityPick(n\Collider, 1.0)
						If PickedEntity() <> 0 Then
							n\State3 = -70*2
						EndIf
						ShowEntity n\Collider
						TurnEntity n\Collider,0,180,0
					ElseIf n\State3 < 0.0
						n\State3 = Min(n\State3+fs\FPSfactor[0],0)
					EndIf
					
					If n\PathTimer<=0.0 Then
						If n\MTF2Leader<>Null Then
							n\PathStatus = FindPath(n,EntityX(n\MTF2Leader\Collider,True),EntityY(n\MTF2Leader\Collider,True)+0.1,EntityZ(n\MTF2Leader\Collider,True))
						Else
							For r = Each Rooms
								If ((Abs(r\x-EntityX(n\Collider,True))>12.0) Or (Abs(r\z-EntityZ(n\Collider,True))>12.0)) And (Rand(1,Max(4-Int(Abs(r\z-EntityZ(n\Collider,True)/8.0)),2))=1) Then
									If EntityDistance(r\obj,n\Target\Collider)>6.0
										x = r\x
										y = 0.1
										z = r\z
										Exit
									EndIf
								EndIf
							Next
							n\PathStatus = FindPath(n,x,y,z)
						EndIf
						If n\PathStatus = 1 Then
							While n\Path[n\PathLocation]=Null
								If n\PathLocation>19 Then Exit
								n\PathLocation=n\PathLocation+1
							Wend
							If n\PathLocation<19 Then
								If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
									If (n\Path[n\PathLocation]\door=Null) Then
										If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
											n\PathLocation=n\PathLocation+1
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						n\PathTimer = 70*10
					Else
						If n\PathStatus=1 Then
							If n\Path[n\PathLocation]=Null Then
								If n\PathLocation > 19 Then
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								PointEntity n\Collider,n\Path[n\PathLocation]\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
								AnimateNPC(n,577, 628, n\CurrSpeed*26) ;Placeholder (until running animation has been implemented)
								
								newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								If (newDist<2.0 And n\Path[n\PathLocation]\door<>Null) Then
									If (Not n\Path[n\PathLocation]\door\open)
										sound = 0
										If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
										PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
										PlayMTFSound(MTF2SFX(5),n)
									EndIf
									n\Path[n\PathLocation]\door\open = True
									If n\Path[n\PathLocation]\door\MTFClose
										n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
									EndIf
								EndIf
								
								If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
									n\PathLocation=n\PathLocation+1
								EndIf
							EndIf
							n\PathTimer=n\PathTimer-fs\FPSfactor[0]
						Else
							n\PathTimer=0.0
						EndIf
					EndIf
				Else
					n\State = 0
				EndIf
				;[End Block]
			Case 5 ;looking at some other target than the player
				;[Block]
				target=CreatePivot()
				PositionEntity target, n\EnemyX, n\EnemyY, n\EnemyZ, True
				
				If dist<HideDistance Then
					AnimateNPC(n, 629, 636, 0.2, False)
				EndIf
				
				If Abs(EntityX(target)-EntityX(n\Collider)) < 55.0 And Abs(EntityZ(target)-EntityZ(n\Collider)) < 55.0 And Abs(EntityY(target)-EntityY(n\Collider))< 20.0 Then
					
					PointEntity n\obj, target
					RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),30.0), 0, True
					
					If n\PathTimer = 0 Then
						n\PathStatus = EntityVisible(n\Collider,target)
						n\PathTimer = Rand(100,200)
					Else
						n\PathTimer = Min(n\PathTimer-fs\FPSfactor[0],0.0)
					EndIf
					
					If n\PathStatus = 1 And n\Reload =< 0 Then
						dist# = Distance(EntityX(target),EntityZ(target),EntityX(n\Collider),EntityZ(n\Collider))
						
					EndIf
				EndIf		
				
				FreeEntity target
				
				n\Angle = EntityYaw(n\Collider)
				;[End Block]
			Case 6 ;seeing the player as a 049-2 instance
				;[Block]
				PointEntity n\obj,Collider
				RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
				n\Angle = EntityYaw(n\Collider)
				
				AnimateNPC(n, 629, 636, 0.2, False)
				
				If n\Reload =< 0 And KillTimer = 0 Then
					If EntityVisible(n\Collider, Collider) Then
						;angle# = WrapAngle(angle - EntityYaw(n\Collider))
						;If angle < 5 Or angle > 355 Then
						If (Abs(DeltaYaw(n\Collider,Collider))<50.0)
							;prev% = KillTimer
							
							PlaySound2(Gunshot4SFX, Camera, n\Collider, 15)
							
							pvt% = CreatePivot()
							
							RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
							PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
							MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
							
							Shoot(EntityX(pvt),EntityY(pvt), EntityZ(pvt), 0.9, True)
							n\Reload = 7
							
							FreeEntity(pvt)
							
							;If prev => 0 And KillTimer < 0 Then
								;DeathMSG=SubjectName$+". Terminated by See No Evil."
								;If n\MTF2Leader = Null Then PlayMTFSound(LoadTempSound(SFXPath$+"Character\MTF2\049\Player0492_2.ogg"),n)
							;EndIf
						EndIf	
					EndIf
				EndIf
				
				;[End Block]
			Case 7 ;just shooting
				;[Block]
				AnimateNPC(n, 629, 636, 0.2, False)
				
				RotateEntity n\Collider,0,CurveAngle(n\State2,EntityYaw(n\Collider),20),0
				n\Angle = EntityYaw(n\Collider)
				
				If n\Reload =< 0 
					LightVolume = TempLightVolume*1.2
					PlaySound2(Gunshot4SFX, Camera, n\Collider, 20)
					
					pvt% = CreatePivot()
					
					RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
					PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
					MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
					
					p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 1, Rnd(0.08,0.1), 0.0, 5)
					TurnEntity p\obj, 0,0,Rnd(360)
					p\Achange = -0.15
					
					FreeEntity(pvt)
					n\Reload = 7
				End If
				;[End Block]
			Case 8 ;SCP-096 spotted
				;[Block]
				n\Speed = 0.015
				n\BoneToManipulate = "head"
				n\ManipulateBone = True
				n\ManipulationType = 2
                If n\PathTimer<=0.0 Then ;update path
					If n\MTF2Leader<>Null Then ;i'll follow the leader
						n\PathStatus = FindPath(n,EntityX(n\MTF2Leader\Collider,True),EntityY(n\MTF2Leader\Collider,True)+0.1,EntityZ(n\MTF2Leader\Collider,True)) ;whatever you say boss
					Else ;i am the leader
						For r = Each Rooms
							If ((Abs(r\x-EntityX(n\Collider,True))>12.0) Or (Abs(r\z-EntityZ(n\Collider,True))>12.0)) And (Rand(1,Max(4-Int(Abs(r\z-EntityZ(n\Collider,True)/8.0)),2))=1) Then
								x = r\x
								y = 0.1
								z = r\z
								Exit
							EndIf
						Next
						n\PathStatus = FindPath(n,x,y,z) ;we're going to this room for no particular reason
					EndIf
					If n\PathStatus = 1 Then
						While n\Path[n\PathLocation]=Null
							If n\PathLocation>19 Then Exit
							n\PathLocation=n\PathLocation+1
						Wend
						If n\PathLocation<19 Then
							If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
								If (n\Path[n\PathLocation]\door=Null) Then
									If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
										n\PathLocation=n\PathLocation+1
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
					n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6-10 seconds
                ElseIf (n\PathTimer<=70.0 * 2.5) And (n\MTF2Leader=Null) Then
					n\PathTimer=n\PathTimer-fs\FPSfactor[0]
					n\CurrSpeed = 0.0
					;If Rand(1,35)=1 Then
					;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
					;EndIf
					FinishWalking(n,577, 628,n\Speed*26)
					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
					RotateEntity n\obj,-90.0,n\Angle,0.0,True
                Else
					If n\PathStatus=2 Then
						n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
						n\CurrSpeed = 0.0
						;If Rand(1,35)=1 Then
						;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
						;EndIf
						FinishWalking(n,577, 628,n\Speed*26)
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					ElseIf n\PathStatus=1 Then
						If n\Path[n\PathLocation]=Null Then
							If n\PathLocation > 19 Then
								n\PathLocation = 0 : n\PathStatus = 0
							Else
								n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							PointEntity n\Collider,n\Path[n\PathLocation]\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\obj,-90.0,n\Angle,0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							;MoveEntity n\Collider, 0, 0, n\CurrSpeed * fs\FPSfactor[0]
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,577, 628, n\CurrSpeed*26)
							
							newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
								;open the door and make it automatically close after 5 seconds
								If (Not n\Path[n\PathLocation]\door\open)
									sound = 0
									If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
									PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
									PlayMTFSound(MTF2SFX(5),n)
								EndIf
								n\Path[n\PathLocation]\door\open = True
								If n\Path[n\PathLocation]\door\MTFClose
									n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
								EndIf
							EndIf
                            
							If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
								n\PathLocation=n\PathLocation+1
							EndIf
						EndIf
						n\PathTimer=n\PathTimer-fs\FPSfactor[0] ;timer goes down slow
					Else
						n\PathTimer=n\PathTimer-(fs\FPSfactor[0]*2.0) ;timer goes down fast
						If n\MTF2Leader = Null Then
							;If Rand(1,35)=1 Then
							;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							;EndIf
							FinishWalking(n,577, 628,n\Speed*26)
							n\CurrSpeed = 0.0
						ElseIf EntityDistance(n\Collider,n\MTF2Leader\Collider)>1.0 Then
							PointEntity n\Collider,n\MTF2Leader\Collider
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
							AnimateNPC(n,577, 628, n\CurrSpeed*26)
						Else
							;If Rand(1,35)=1 Then
							;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							;EndIf
							FinishWalking(n,577, 628,n\Speed*26)
							n\CurrSpeed = 0.0
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\obj,-90.0,n\Angle,0.0,True
					EndIf
                EndIf
				
				If (Not EntityVisible(n\Collider,Curr096\Collider)) Or EntityDistance(n\Collider,Curr096\Collider)>6.0
					n\State = 0
				EndIf
				;[End Block]
			Case 9 ;SCP-049-2/008 spotted
				;[Block]
				If EntityVisible(n\Collider, n\Target\Collider) Then
					PointEntity n\obj,n\Target\Collider
					RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
					n\Angle = EntityYaw(n\Collider)
					
					If EntityDistance(n\Target\Collider,n\Collider)<1.3
						n\State3 = 70*2
					EndIf
					
					If n\State3 > 0.0
						n\PathStatus = 0
						n\PathLocation = 0
						n\Speed = 0.02
						n\CurrSpeed = CurveValue(-n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
						AnimateNPC(n,628, 577, n\CurrSpeed*26)
						
						n\PathTimer = 1.0
						
						n\State3=Max(n\State3-fs\FPSfactor[0],0)
					Else
						n\State3 = 0
						AnimateNPC(n, 346, 351, 0.2, False)
					EndIf
					If n\Reload =< 0 And n\Target\IsDead = False Then
						;angle# = WrapAngle(angle - EntityYaw(n\Collider))
						;If angle < 5 Or angle > 355 Then
						If (Abs(DeltaYaw(n\Collider,n\Target\Collider))<50.0)
							;prev% = KillTimer
							
							PlaySound2(Gunshot4SFX, Camera, n\Collider, 15)
							
							pvt% = CreatePivot()
							
							RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
							PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
							MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
							
							p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 1, Rnd(0.08,0.1), 0.0, 5)
							TurnEntity p\obj, 0,0,Rnd(360)
							p\Achange = -0.15
							If n\Target\HP > 0
								n\Target\HP = Max(n\Target\HP-Rand(5,10),0)
							Else
								If (Not n\Target\IsDead)
									If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									If n\NPCtype = NPCtyp049_2
										n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\Player0492_2.ogg")
										PlayMTFSound(n\Sound, n)
									;ElseIf n\NPCtype = NPCtype049_3
									    ;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF\049\Player049_3_2.ogg")
									    ;PlayMTFSound(n\Sound, n)
									;ElseIf n\NPCtype = NPCtype008_2
									    ;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\008\008_2.ogg")
									    ;PlayMTFSound(n\Sound, n)
									;Else;If n\NPCtype = NPCtype008_1
									    ;n\Sound = LoadSound_Strict(SFXPath$+"Character\MTF2\008\008_1.ogg")
									    ;PlayMTFSound(n\Sound, n)
									EndIf
								EndIf
								SetNPCFrame(n\Target,133)
								n\Target\IsDead = True
								n\Target = Null
								n\State = 0
								Return
							EndIf
							n\Reload = 7
							
							FreeEntity(pvt)
						EndIf	
					EndIf
					n\PathStatus = 0
				Else
					If n\PathTimer<=0.0 Then
						n\PathStatus = FindPath(n,EntityX(n\Target\Collider),EntityY(n\Target\Collider),EntityZ(n\Target\Collider))
						If n\PathStatus = 1 Then
							While n\Path[n\PathLocation]=Null
								If n\PathLocation>19 Then Exit
								n\PathLocation=n\PathLocation+1
							Wend
							If n\PathLocation<19 Then
								If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
									If (n\Path[n\PathLocation]\door=Null) Then
										If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
											n\PathLocation=n\PathLocation+1
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						n\PathTimer = 70*10
					Else
						If n\PathStatus=1 Then
							If n\Path[n\PathLocation]=Null Then
								If n\PathLocation > 19 Then
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								PointEntity n\Collider,n\Path[n\PathLocation]\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\obj,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * fs\FPSfactor[0], True
								AnimateNPC(n,577, 628, n\CurrSpeed*26)
								
								newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
									If (Not n\Path[n\PathLocation]\door\open)
										sound = 0
										If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
										PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
										PlayMTFSound(MTF2SFX(5),n)
									EndIf
									n\Path[n\PathLocation]\door\open = True
									If n\Path[n\PathLocation]\door\MTFClose
										n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
									EndIf
								EndIf
								
								If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
									n\PathLocation=n\PathLocation+1
								EndIf
							EndIf
							n\PathTimer=n\PathTimer-fs\FPSfactor[0]
						Else
							n\PathTimer=0.0
						EndIf
					EndIf
				EndIf
				
				If n\Target\IsDead = True Then
					n\Target = Null
					n\State = 0
				EndIf
				
				;[End Block]
		End Select
		
		If n\CurrSpeed > 0.01 Then
			If prevframe < 601 And AnimTime(n\obj)=>601 Then
				PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))						
			ElseIf prevframe < 601 And AnimTime(n\obj)=>601
				PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))						
			ElseIf prevframe < 626 And AnimTime(n\obj)=>626
				PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))						
			ElseIf prevframe < 626 And AnimTime(n\obj)=>626
				PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))
			EndIf
		EndIf
		
		If chs\NoTarget And n\State = 1 Then n\State = 0
		
		If n\State <> 3 And n\State <> 5 And n\State <> 6 And n\State <> 7
			If n\MTF2Leader<>Null Then
				If EntityDistance(n\Collider,n\MTF2Leader\Collider)<0.7 Then
					PointEntity n\Collider,n\MTF2Leader\Collider
					RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
					
					TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)-45)* 0.01 * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)-45)* 0.01 * fs\FPSfactor[0], True
				EndIf
			Else
				For n2.NPCs = Each NPCs
					If n2<>n And n2\IsDead=False Then
						If Abs(DeltaYaw(n\Collider,n2\Collider))<80.0 Then
							If EntityDistance(n\Collider,n2\Collider)<0.7 Then							
								TranslateEntity n2\Collider, Cos(EntityYaw(n\Collider,True)+90)* 0.01 * fs\FPSfactor[0], 0, Sin(EntityYaw(n\Collider,True)+90)* 0.01 * fs\FPSfactor[0], True
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		EndIf
		
		;teleport back to the facility if fell through the floor
		If n\State <> 6 And n\State <> 7
			If (EntityY(n\Collider) < -10.0) Then
				TeleportCloser(n)
			EndIf
		EndIf
		
		RotateEntity n\obj,0,n\Angle,0.0,True
		
		PositionEntity n\obj,EntityX(n\Collider,True),EntityY(n\Collider,True)-0.15,EntityZ(n\Collider,True),True
		
	EndIf
End Function


Function Shoot(x#, y#, z#, hitProb# = 1.0, particles% = True, instaKill% = False)
	
	;muzzle flash
	Local p.Particles = CreateParticle(x,y,z, 1, Rnd(0.08,0.1), 0.0, 5)
	TurnEntity p\obj, 0,0,Rnd(360)
	p\Achange = -0.15
	
	LightVolume = TempLightVolume*1.2
	
	If instaKill Then Kill(True) : PlaySound_Strict BullethitSFX : Return
		
	If Rnd(1.0) =< hitProb Then
		TurnEntity Camera, Rnd(-3,3), Rnd(-3,3), 0
			
		Local ShotMessageUpdate$
		If WearingVest > 0 And (Not WearingHelmet) Then ;If player wearing the ballistic vest only
			If WearingVest = 1 Then
				Select Rand(16)
					Case 1,2,3,4,5,6,7,8,9 ;Vest
						;[Block]
						BlurTimer = 650
						Stamina = 0
						If I_1033RU\HP = 0
							ShotMessageUpdate = "A bullet penetrated your vest, making you gasp."
							Injuries = Injuries + Rnd(0.1,0.2)
						Else
							ShotMessageUpdate = "A bullet penetrated your vest, but SCP-1033-RU protected you."
							Damage1033RU(5+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					    EndIf
						;[End Block]
					Case 10 ;Left leg
						;[Block]
						BlurTimer = 650
						Stamina = 0
						If I_1033RU\HP = 0
						    ShotMessageUpdate = "A bullet hit your left leg."
							Injuries = Injuries + Rnd(0.7,0.9)
						Else
							ShotMessageUpdate = "A bullet hit your left leg, but SCP-1033-RU protected you."
							Damage1033RU(15+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					    EndIf
					    ;[End Block]
					Case 11 ;Right leg
						;[Block]
					    BlurTimer = 650
					    Stamina = 0
						If I_1033RU\HP = 0
						    ShotMessageUpdate = "A bullet hit your right leg."
							Injuries = Injuries + Rnd(0.7,0.9)
						Else
							ShotMessageUpdate = "A bullet hit your right leg, but SCP-1033-RU protected you."
							Damage1033RU(15+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
						EndIf
						;[End Block]
					Case 12 ;Left hand
						;[Block]
						BlurTimer = 650
						If I_1033RU\HP = 0
						    ShotMessageUpdate = "A bullet hit your left hand."
							Injuries = Injuries + Rnd(0.5,0.7)
						Else
							ShotMessageUpdate = "A bullet hit your left hand, but SCP-1033-RU protected you."
							Damage1033RU(10+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					    EndIf
					    ;[End Block]
					Case 13 ;Right hand
						;[Block]
					    BlurTimer = 650
						If I_1033RU\HP = 0
						    ShotMessageUpdate = "A bullet hit your right hand."
							Injuries = Injuries + Rnd(0.5,0.7)
						Else
							ShotMessageUpdate = "A bullet hit your right hand, but SCP-1033-RU protected you."
							Damage1033RU(10+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
						EndIf
						;[End Block]
					Case 14 ;Neck
						;[Block]
					    BlurTimer = 650
						Stamina = 0
						If I_1033RU\HP = 0
							ShotMessageUpdate = "A bullet struck your neck, making you gasp."
							Injuries = Injuries + Rnd(1.1,1.3)
						Else
							Damage1033RU(35+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
							ShotMessageUpdate = "A bullet struck your neck, but SCP-1033-RU protected you."
						EndIf
						;[End Block]
					Case 15 ;Head
						;[Block]
						ShotMessageUpdate = "A bullet hit your head."
						Kill(True)
						;[End Block]
					Case 16 ;Face
					    ;[Block]
					    For n.NPCs = Each NPCs
					        If n\NPCtype = NPCtypeMTF Or n\NPCtype = NPCtypeMTF2
		                        If EntityInView(n\obj, Camera) And EntityVisible(n\obj, Camera)
					                ShotMessageUpdate = "A bullet hit your face."
					                Kill(True)
					            Else
					                ShotMessageUpdate = "A bullet hit your head."
					                Kill(True)
					            EndIf
					        EndIf
					    Next
                        ;[End Block]
			    End Select	
			Else
				If Rand(10)=1 Then ;Chest (more damage)
					;[Block]
				    BlurTimer = 650
					Stamina = Stamina - 1
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your chest. The vest absorbed some of the damage."
						Injuries = Injuries + Rnd(0.7,0.8)
					Else
					    ShotMessageUpdate = "A bullet hit your chest, but SCP-1033-RU protected you."
						Damage1033RU(30+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
				    ;[End Block]
				Else ;Chest
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your chest. The vest absorbed most of the damage."
						Injuries = Injuries + Rnd(0.1,0.2)
				    Else
						ShotMessageUpdate = "A bullet hit your chest, but SCP-1033-RU protected you."
						Damage1033RU(5+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
				    EndIf
					;[End Block]
				EndIf
			EndIf
		Else If WearingVest > 0 And WearingHelmet > 0 Then ;If player wearing the ballistic vest and the ballistic helmet
			If WearingVest = 1 Then
				Select Rand(22)
					Case 1,2,3,4,5,6,7,8,9 ;Vest
						;[Block]
						BlurTimer = 650
						Stamina = 0
						If I_1033RU\HP = 0
							ShotMessageUpdate = "A bullet penetrated your vest, making you gasp."
							Injuries = Injuries + Rnd(0.1,0.2)
						Else
							ShotMessageUpdate = "A bullet penetrated your vest, but SCP-1033-RU protected you."
							Damage1033RU(5+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					    EndIf
						;[End Block]
					Case 10 ;Left leg
						;[Block]
					    BlurTimer = 650
					    Stamina = 0
						If I_1033RU\HP = 0
							ShotMessageUpdate = "A bullet hit your left leg."
							Injuries = Injuries + Rnd(0.7,0.9)
					    Else
							ShotMessageUpdate = "A bullet hit your left leg, but SCP-1033-RU protected you."
							Damage1033RU(15+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
						EndIf
					    ;[End Block]
					Case 11 ;Right leg
						;[Block]
					    BlurTimer = 650
						Stamina = 0
						If I_1033RU\HP = 0
							ShotMessageUpdate = "A bullet hit your right leg."
							Injuries = Injuries + Rnd(0.7,0.9)
					    Else
							ShotMessageUpdate = "A bullet hit your right leg, but SCP-1033-RU protected you."
							Damage1033RU(15+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					    EndIf
						;[End Block]
					Case 12 ;Left hand
						;[Block]
						BlurTimer = 650
						If I_1033RU\HP = 0
						    ShotMessageUpdate = "A bullet hit your left hand."
							Injuries = Injuries + Rnd(0.5,0.7)
						Else
							ShotMessageUpdate = "A bullet hit your left hand, but SCP-1033-RU protected you."
							Damage1033RU(10+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					    EndIf
					    ;[End Block]
					Case 13 ;Right hand
						;[Block]
					    BlurTimer = 650
						If I_1033RU\HP = 0
						    ShotMessageUpdate = "A bullet hit your right hand."
							Injuries = Injuries + Rnd(0.5,0.7)
						Else
							ShotMessageUpdate = "A bullet hit your right hand, but SCP-1033-RU protected you."
							Damage1033RU(10+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
						EndIf
						;[End Block]
					Case 14 ;Neck
						;[Block]
						BlurTimer = 650
						Stamina = 0
					    If I_1033RU\HP = 0
							ShotMessageUpdate = "A bullet struck your neck, making you gasp."
							Injuries = Injuries + Rnd(1.1,1.3)
						Else
							Damage1033RU(35+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
							ShotMessageUpdate = "A bullet struck your neck, but SCP-1033-RU protected you."
						EndIf
						;[End Block]
					Case 15,16,17,18,19,20,21 ;Helmet
						;[Block]
						BlurTimer = 650
						ShotMessageUpdate = "A bullet penetrated your helmet."
						;[End Block]
					Case 22 ;Face
						;[Block]
						For n.NPCs = Each NPCs
					        If n\NPCtype = NPCtypeMTF Or n\NPCtype = NPCtypeMTF2
		                        If EntityInView(n\obj, Camera) And EntityVisible(n\obj, Camera)
					                ShotMessageUpdate = "A bullet hit your face."
					                Kill(True)
					            Else
					                If WearingHelmet > 0
					                    BlurTimer = 500
					                    ShotMessageUpdate = "A bullet penetrated your helmet."
                                    Else
                                        ShotMessageUpdate = "A bullet hit your head."
					                    Kill(True)
                                    EndIf
					            EndIf
					        EndIf
					    Next
						;[End Block]
				End Select	
			Else
				If Rand(10)=1 Then ;Chest (more damage)
					;[Block]
				    BlurTimer = 650
					Stamina = Stamina - 1
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your chest. The vest absorbed some of the damage."
						Injuries = Injuries + Rnd(0.7,0.9)
					Else
						ShotMessageUpdate = "A bullet hit your chest, but SCP-1033-RU protected you."
						Damage1033RU(30+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Else ;Chest
					;[Block]
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your chest. The vest absorbed most of the damage."
						Injuries = Injuries + Rnd(0.1,0.2)
				    Else
						ShotMessageUpdate = "A bullet hit your chest, but SCP-1033-RU protected you."
						Damage1033RU(5+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				EndIf
			EndIf
		Else If WearingVest = 0 And WearingHelmet > 0 ;If player wearing the ballistic helmet only
			Select Rand(17)
				Case 1,2,3,4,5,6,7 ;Helmet
					;[Block]
					BlurTimer = 650
					ShotMessageUpdate = "A bullet penetrated your helmet."
					;[End Block]
				Case 8 ;Left leg
					;[Block]
					BlurTimer = 650
					Stamina = 0
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your left leg."
						Injuries = Injuries + Rnd(0.7,0.9)
					Else
						ShotMessageUpdate = "A bullet hit your left leg, but SCP-1033-RU protected you."
						Damage1033RU(15+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
                    EndIf
					;[End Block]
				Case 9 ;Right leg
					;[Block]
					BlurTimer = 650
					Stamina = 0
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your right leg."
						Injuries = Injuries + Rnd(0.7,0.9)
				    Else
						ShotMessageUpdate = "A bullet hit your right leg, but SCP-1033-RU protected you."
						Damage1033RU(15+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
                    EndIf
					;[End Block]
				Case 10 ;Left hand
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your left hand."
						Injuries = Injuries + Rnd(0.5,0.7)
					Else
					    ShotMessageUpdate = "A bullet hit your left hand, but SCP-1033-RU protected you."
						Damage1033RU(10+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Case 11 ;Right hand
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your right hand."
					    Injuries = Injuries + Rnd(0.5,0.7)
					Else
					    ShotMessageUpdate = "A bullet hit your right hand, but SCP-1033-RU protected you."
						Damage1033RU(10+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Case 12 ;Right Shoulder
					;[Block]
				    BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your right shoulder."
						Injuries = Injuries + Rnd(0.9,1.1)
					Else
						ShotMessageUpdate = "A bullet hit your right shoulder, but SCP-1033-RU protected you."
						Damage1033RU(25+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
				    ;[End Block]	
				Case 13 ;Left shoulder
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your left shoulder."
						Injuries = Injuries + Rnd(0.9,1.1)	
					Else
						ShotMessageUpdate = "A bullet hit your left shoulder, but SCP-1033-RU protected you."
						Damage1033RU(25+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
				    EndIf
					;[End Block]
			    Case 14 ;Right shoulder (more damage)
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your right shoulder."
						Injuries = Injuries + Rnd(2.4,2.6)
				    Else
						ShotMessageUpdate = "A bullet hit your right shoulder, but SCP-1033-RU protected you."
						Damage1033RU(25+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Case 15 ;Left shoulder (more damage)
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your left shoulder."
						Injuries = Injuries + Rnd(2.4,2.6)
				    Else
						ShotMessageUpdate = "A bullet hit your left shoulder, but SCP-1033-RU protected you."
						Damage1033RU(25+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Case 16 ;Neck
					;[Block]
					BlurTimer = 650
					Stamina = 0
					If I_1033RU\HP = 0
					    ShotMessageUpdate = "A bullet struck your neck, making you gasp."
						Injuries = Injuries + Rnd(1.1,1.3)
				    Else
					    Damage1033RU(35+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
						ShotMessageUpdate = "A bullet struck your neck, but SCP-1033-RU protected you."
					EndIf
                    ;[End Block]
			    Case 17 ;Face
					;[Block]
					For n.NPCs = Each NPCs
					    If n\NPCtype = NPCtypeMTF Or n\NPCtype = NPCtypeMTF2
		                    If EntityInView(n\obj, Camera) And EntityVisible(n\obj, Camera)
					            ShotMessageUpdate = "A bullet hit your face."
					            Kill(True)
					        Else
					            If WearingHelmet > 0
					                BlurTimer = 500
					                ShotMessageUpdate = "A bullet penetrated your helmet."
                                Else
                                    ShotMessageUpdate = "A bullet hit your head."
					                Kill(True)
                                EndIf
					        EndIf
					    EndIf
					Next
					;[End Block]
			End Select
		Else If WearingVest = 0 And WearingHelmet = 0 ;If player not wearing the ballistic vest and the ballistic helmet
			Select Rand(11)
				Case 1 ;Head
				    ;[Block]
					ShotMessageUpdate = "A bullet hit your head."
				    Kill(True)
					;[End Block]
				Case 2 ;Left leg
					;[Block]
					BlurTimer = 650
					Stamina = 0
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your left leg."
						Injuries = Injuries + Rnd(0.7,0.9)
					Else
						ShotMessageUpdate = "A bullet hit your left leg, but SCP-1033-RU protected you."
						Damage1033RU(15+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
                    EndIf
					;[End Block]
			    Case 3 ;Right leg
					;[Block]
					BlurTimer = 650
					Stamina = 0
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your right leg."
						Injuries = Injuries + Rnd(0.7,0.9)
				    Else
						ShotMessageUpdate = "A bullet hit your right leg, but SCP-1033-RU protected you."
						Damage1033RU(15+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
                    EndIf
				    ;[End Block]
				Case 4 ;Right Shoulder
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your right shoulder."
						Injuries = Injuries + Rnd(0.9,1.1)
					Else
						ShotMessageUpdate = "A bullet hit your right shoulder, but SCP-1033-RU protected you."
						Damage1033RU(25+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
				    ;[End Block]	
				Case 5 ;Left shoulder
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your left shoulder."
						Injuries = Injuries + Rnd(0.9,1.1)	
					Else
						ShotMessageUpdate = "A bullet hit your left shoulder, but SCP-1033-RU protected you."
						Damage1033RU(25+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Case 6 ;Right shoulder (more damage)
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your right shoulder."
						Injuries = Injuries + Rnd(2.4,2.6)
					Else
						ShotMessageUpdate = "A bullet hit your right shoulder, but SCP-1033-RU protected you."
						Damage1033RU(25+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Case 7 ;Left shoulder (more damage)
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your left shoulder."
						Injuries = Injuries + Rnd(2.4,2.6)
					Else
						ShotMessageUpdate = "A bullet hit your left shoulder, but SCP-1033-RU protected you."
						Damage1033RU(25+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Case 8 ;Left hand
					;[Block]
					BlurTimer = 650
					If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your left hand."
						Injuries = Injuries + Rnd(0.5,0.7)
					Else
						ShotMessageUpdate = "A bullet hit your left hand, but SCP-1033-RU protected you."
						Damage1033RU(10+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Case 9 ;Right hand
				    ;[Block]
					BlurTimer = 650
				    If I_1033RU\HP = 0
						ShotMessageUpdate = "A bullet hit your right hand."
					    Injuries = Injuries + Rnd(0.5,0.7)
					Else
						ShotMessageUpdate = "A bullet hit your right hand, but SCP-1033-RU protected you."
						Damage1033RU(10+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					EndIf
					;[End Block]
				Case 10 ;Neck
					;[Block]
					BlurTimer = 650
				    Stamina = 0
					If I_1033RU\HP = 0
					    ShotMessageUpdate = "A bullet struck your neck, making you gasp."
						Injuries = Injuries + Rnd(1.1,1.3)
				    Else
						Damage1033RU(35+(Rand(5) * SelectedDifficulty\aggressiveNPCs))
					    ShotMessageUpdate = "A bullet struck your neck, but SCP-1033-RU protected you."
					EndIf
                    ;[End Block]
                Case 11 ;Face
					;[Block]
					For n.NPCs = Each NPCs
					    If n\NPCtype = NPCtypeMTF Or n\NPCtype = NPCtypeMTF2
		                    If EntityInView(n\obj, Camera) And EntityVisible(n\obj, Camera)
					            ShotMessageUpdate = "A bullet hit your face."
					            Kill(True)
					        Else
                                ShotMessageUpdate = "A bullet hit your head."
					            Kill(True)
					        EndIf
					    EndIf
					Next
					;[End Block]
		    End Select
		EndIf
			
		;Only updates the message if it's been more than two seconds.
		If (MsgTimer < 70*4) Then
			Msg = ShotMessageUpdate
			MsgTimer = 70*6
		EndIf
			
		PlaySound_Strict BullethitSFX
	ElseIf particles And ParticleAmount>0
		pvt = CreatePivot()
		PositionEntity pvt, EntityX(Collider),(EntityY(Collider)+EntityY(Camera))/2,EntityZ(Collider)
		PointEntity pvt, p\obj
		TurnEntity pvt, 0, 180, 0
			
		EntityPick(pvt, 2.5)
			
		If PickedEntity() <> 0 Then 
			PlaySound2(Gunshot3SFX, Camera, pvt, 0.4, Rnd(0.8,1.0))
				
			If particles Then 
				;dust/smoke particles
				p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.03, 0, 80)
				p\speed = 0.001
				p\SizeChange = 0.003
				p\A = 0.8
				p\Achange = -0.01
				RotateEntity p\pvt, EntityPitch(pvt)-180, EntityYaw(pvt),0
					
				For i = 0 To Rand(2,3)
					p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.006, 0.003, 80)
					p\speed = 0.02
					p\A = 0.8
					p\Achange = -0.01
					RotateEntity p\pvt, EntityPitch(pvt)+Rnd(170,190), EntityYaw(pvt)+Rnd(-10,10),0	
				Next
					
				;bullet hole decal
				Local de.Decals = CreateDecal(Rand(13,14), PickedX(), PickedY() + Rnd(-0.05, 0.05), PickedZ(), Rnd(-4.0, 4.0), Rnd(-4.0, 4.0), Rnd(-4.0, 4.0))
				AlignToVector de\obj,-PickedNX(),-PickedNY(),-PickedNZ(),3
				MoveEntity de\obj, 0,0,-0.001
				EntityFX de\obj, 1
				de\lifetime = 70*20
				EntityBlend de\obj, 2
				de\Size = Rnd(0.028,0.034)
				ScaleSprite de\obj, de\Size, de\Size
			EndIf				
		EndIf	
		FreeEntity pvt
	EndIf
			
End Function

Function PlayMTFSound(sound%, n.NPCs)
	If n <> Null Then
		n\SoundCHN = PlaySound2(sound, Camera, n\Collider, 8.0)	
	EndIf
	
	If SelectedItem <> Null Then
		If SelectedItem\state2 = 3 And SelectedItem\state > 0 Then 
			Select SelectedItem\itemtemplate\tempname 
				Case "radio","fineradio","18vradio"
					If sound<>MTFSFX(5) Or (Not ChannelPlaying(RadioCHN(3)))
						If RadioCHN(3)<> 0 Then StopChannel RadioCHN(3)
						RadioCHN(3) = PlaySound_Strict (sound)
					EndIf
					If sound<>MTF2SFX(5) Or (Not ChannelPlaying(RadioCHN(3)))
						If RadioCHN(3)<> 0 Then StopChannel RadioCHN(3)
						RadioCHN(3) = PlaySound_Strict (sound)
					EndIf
			End Select
		EndIf
	EndIf 
End Function

Function MoveToPocketDimension()
	Local r.Rooms
	
	For r.Rooms = Each Rooms
		If r\RoomTemplate\Name = "pocketdimension" Then
			FallTimer = 0
			UpdateDoors()
			UpdateRooms()
			ShowEntity Collider
			PlaySound_Strict(Use914SFX)
			PlaySound_Strict(OldManSFX(5))
			PositionEntity(Collider, EntityX(r\obj),0.8,EntityZ(r\obj))
			DropSpeed = 0
			ResetEntity Collider
			
			BlinkTimer = -10
			
			Injuries = Injuries+0.5
			
			PlayerRoom = r
			
			Return
		EndIf
	Next		
End Function

Function FindFreeNPCID%()
	Local id% = 1
	While (True)
		Local taken% = False
		For n2.NPCs = Each NPCs
			If n2\ID = id Then
				taken = True
				Exit
			EndIf
		Next
		If (Not taken) Then
			Return id
		EndIf
		id = id + 1
	Wend
End Function

Function ForceSetNPCID(n.NPCs, newID%)
	n\ID = newID
	
	For n2.NPCs = Each NPCs
		If n2 <> n And n2\ID = newID Then
			n2\id = FindFreeNPCID()
		EndIf
	Next
End Function

Function Find860Angle(n.NPCs, fr.Forest)
	TFormPoint(EntityX(Collider),EntityY(Collider),EntityZ(Collider),0,fr\Forest_Pivot)
	Local playerx = Floor((TFormedX()+6.0)/12.0)
	Local playerz = Floor((TFormedZ()+6.0)/12.0)
	
	TFormPoint(EntityX(n\Collider),EntityY(n\Collider),EntityZ(n\Collider),0,fr\Forest_Pivot)
	Local x# = (TFormedX()+6.0)/12.0
	Local z# = (TFormedZ()+6.0)/12.0
	
	Local xt = Floor(x), zt = Floor(z)
	
	Local x2,z2
	If xt<>playerx Or zt<>playerz Then ;the monster is not on the same tile as the player
		For x2 = Max(xt-1,0) To Min(xt+1,gridsize-1)
			For z2 = Max(zt-1,0) To Min(zt+1,gridsize-1)
				If fr\grid[(z2*gridsize)+x2]>0 And (x2<>xt Or z2<>zt) And (x2=xt Or z2=zt) Then
					
					;tile (x2,z2) is closer to the player than the monsters current tile
					If (Abs(playerx-x2)+Abs(playerz-z2))<(Abs(playerx-xt)+Abs(playerz-zt)) Then
						;calculate the position of the tile in world coordinates
						TFormPoint(x2*12.0,0,z2*12.0,fr\Forest_Pivot,0)
						
						Return point_direction(EntityX(n\Collider),EntityZ(n\Collider),TFormedX(),TFormedZ())+180
					EndIf
					
				EndIf
			Next
		Next
	Else
		Return point_direction(EntityX(n\Collider),EntityZ(n\Collider),EntityX(Collider),EntityZ(Collider))+180
	EndIf		
End Function

Function Console_SpawnNPC(c_input$, c_state$ = "")
	Local n.NPCs
	Local consoleMSG$
	
	Select c_input$ 
		Case "0081", "0081zombie", "scp008-1", "scp-008-1", "scp0081", "008-1"
			n.NPCs = CreateNPC(NPCtype008_1, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			n\State = 1
			consoleMSG = "SCP-008-1 spawned."
			
		Case "049", "scp049", "scp-049"
			n.NPCs = CreateNPC(NPCtype049, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			n\State = 1
			consoleMSG = "SCP-049 spawned."
			
		Case "049-2", "0492", "scp-049-2", "scp049-2", "049zombie", "zombie", "scp-0492"
			n.NPCs = CreateNPC(NPCtype049_2, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			n\State = 1
			consoleMSG = "SCP-049-2 spawned."
			
		Case "066", "scp066", "scp-066"
			n.NPCs = CreateNPC(NPCtype066, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			If (Curr066 = Null) Curr066 = n
			consoleMSG = "SCP-066 spawned."
			
		Case "096", "scp096", "scp-096"
			n.NPCs = CreateNPC(NPCtype096, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			n\State = 5
			If (Curr096 = Null) Then Curr096 = n
			consoleMSG = "SCP-096 spawned."
			
		Case "106", "scp106", "scp-106", "larry"
			n.NPCs = CreateNPC(NPCtype106, EntityX(Collider), EntityY(Collider) - 0.5, EntityZ(Collider))
			n\State = -1
			consoleMSG = "SCP-106 spawned."
			
		Case "173", "scp173", "scp-173", "statue"
			n.NPCs = CreateNPC(NPCtype173, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			Curr173 = n
			If (Curr173\Idle = 3) Then Curr173\Idle = False
			consoleMSG = "SCP-173 spawned."
			
		Case "372", "scp372", "scp-372"
			n.NPCs = CreateNPC(NPCtype372, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "SCP-372 spawned."
			
		Case "513-1", "5131", "scp513-1", "scp-513-1"
			n.NPCs = CreateNPC(NPCtype513_1, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "SCP-513-1 spawned."
			
		Case "860-2", "8602", "scp860-2", "scp-860-2"
			CreateConsoleMsg("SCP-860-2 cannot be spawned with the console. Sorry!", 255, 0, 0)
			
		Case "939", "scp939", "scp-939"
			CreateConsoleMsg("SCP-939 instances cannot be spawned with the console. Sorry!", 255, 0, 0)

		Case "966", "scp966", "scp-966"
			n.NPCs = CreateNPC(NPCtype966, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "SCP-966 instance spawned."
			
		Case "1048-a", "scp1048-a", "scp-1048-a", "scp1048a", "scp-1048a", "1048a"
			CreateConsoleMsg("SCP-1048-A cannot be spawned with the console. Sorry!", 255, 0, 0)
			
		Case "1499-1", "14991", "scp-1499-1", "scp1499-1"
			n.NPCs = CreateNPC(NPCtype1499_1, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "SCP-1499-1 instance spawned."
			
		Case "class-d", "classd", "d"
			n.NPCs = CreateNPC(NPCtypeD, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "D-Class spawned."
			
		Case "guard"
			n.NPCs = CreateNPC(NPCtypeGuard, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "Guard spawned."
			
		Case "mtf"
			n.NPCs = CreateNPC(NPCtypeMTF, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "MTF unit spawned."
			
		Case "apache", "helicopter"
			n.NPCs = CreateNPC(NPCtypeApache, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "Apache spawned."
			
		Case "tentacle", "scp035tentacle", "035tentacle", "scp-035-tentacle", "scp035-tentacle", "scp035-tentacle"
			n.NPCs = CreateNPC(NPCtype035_Tentacle, EntityX(Collider), EntityY(Collider) - 0.12, EntityZ(Collider))
			consoleMSG = "SCP-035 tentacle spawned."
			
		Case "clerk"
			n.NPCs = CreateNPC(NPCtypeClerk, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "Clerk spawned."
			
		;{~--<MOD>--~}
			
		Case "650", "scp650", "scp-650", "black statue"
			n.NPCs = CreateNPC(NPCtype650, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			If (Curr650 = Null) Curr650 = n
			n\Idle = False
			If (Curr650\Idle = 3) Then Curr650\Idle = False
			consoleMSG = "SCP-650 spawned."
			
		Case "scp-457","457","scp457"
		    CreateConsoleMsg("SCP-457 cannot be spawned with the console. Sorry!", 255, 0, 0)
			
		Case "0493", "049-3", "scp049-3", "scp-049-3", "scp-0493", "scp0493", "049zombie2", "zombie2"
			n.NPCs = CreateNPC(NPCtype049_3, EntityX(Collider),EntityY(Collider)+0.23,EntityZ(Collider))			
			n\state = 1
			consoleMSG = "SCP-049-3 spawned."
			
		Case "0082", "008-2", "scp-008-2", "scp008-2", "0082zombie"
			n.NPCs = CreateNPC(NPCtype008_2, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			n\State = 1
			consoleMSG = "SCP-008-2 spawned."
			
		Case "178-1", "1781", "scp-178-1", "scp178-1", "scp-1781"
			n.NPCs = CreateNPC(NPCtype178_1, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "SCP-178-1 instance spawned."
			CreateConsoleMsg("SCP-178-1 instances will be invisible unless you equip SCP-178.", 255, 255, 0)

        Case "mtf2"
			n.NPCs = CreateNPC(NPCtypeMTF2, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "MTF unit spawned."
			
		Case "ci"
			n.NPCs = CreateNPC(NPCtypeCI, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "CI unit spawned."
			
		Case "vehicle", "introvehicle", "intro_vehicle", "truck"
			n.NPCs = CreateNPC(NPCtypeVehicle, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			consoleMSG = "Vehicle spawned."
			
        ;{~--<END>--~}
			
		Default 
			CreateConsoleMsg("NPC type not found.", 255, 0, 0) : Return
	End Select
	
	If n <> Null
		If c_state <> "" Then n\State = Float(c_state) : consoleMSG = consoleMSG + " (State = " + n\State + ")"
	EndIf
	
	CreateConsoleMsg(consoleMSG)
	
End Function

Function ManipulateNPCBones()
	Local n.NPCs,bone%,pvt%,bonename$
	Local maxvalue#,minvalue#,offset#,smooth#
	Local i%
	Local tovalue#
	
	For n = Each NPCs
		If n\ManipulateBone
			bonename$ = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"bonename",0)
			If bonename$<>""
				pvt% = CreatePivot()
				bone% = FindChild(n\obj,bonename$)
				If bone% = 0 Then RuntimeError "ERROR: NPC bone "+Chr(34)+bonename$+Chr(34)+" does not exist."
				PositionEntity pvt%,EntityX(bone%,True),EntityY(bone%,True),EntityZ(bone%,True)
				Select n\ManipulationType
					Case 0 ;<--- looking at player
						For i = 1 To GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controller_max",1)
							If GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i,0) = "pitch"
								maxvalue# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_max",2)
								minvalue# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_min",2)
								offset# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_offset",2)
								If GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_inverse",3)
									tovalue = -DeltaPitch(bone,Camera)+offset
								Else
									tovalue = DeltaPitch(bone,Camera)+offset
								EndIf
								;n\BonePitch = CurveAngle(tovalue,n\BonePitch,20.0)
								smooth# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_smoothing",2)
								If smooth>0.0
									n\BonePitch = CurveAngle(tovalue,n\BonePitch,smooth)
								Else
									n\BonePitch = tovalue
								EndIf
								n\BonePitch = ChangeAngleValueForCorrectBoneAssigning(n\BonePitch)
								n\BonePitch = Max(Min(n\BonePitch,maxvalue),minvalue)
							ElseIf GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis1",0) = "yaw"
								maxvalue# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_max",2)
								minvalue# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_min",2)
								offset# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_offset",2)
								If GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_inverse",3)
									tovalue = -DeltaYaw(bone,Camera)+offset
								Else
									tovalue = DeltaYaw(bone,Camera)+offset
								EndIf
								;n\BoneYaw = CurveAngle(tovalue,n\BoneYaw,20.0)
								smooth# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_smoothing",2)
								If smooth>0.0
									n\BoneYaw = CurveAngle(tovalue,n\BoneYaw,smooth)
								Else
									n\BoneYaw = tovalue
								EndIf
								n\BoneYaw = ChangeAngleValueForCorrectBoneAssigning(n\BoneYaw)
								n\BoneYaw = Max(Min(n\BoneYaw,maxvalue),minvalue)
							;ElseIf --> (Roll Value)
							;	
							EndIf
						Next
						
						RotateEntity bone%,EntityPitch(bone)+n\BonePitch,EntityYaw(bone)+n\BoneYaw,EntityRoll(bone)+n\BoneRoll
				End Select
				FreeEntity pvt%
			EndIf
		Else
			
		EndIf
	Next
	
End Function

Function GetNPCManipulationValue$(NPC$,bone$,section$,valuetype%=0)
	;valuetype determines what type of variable should the Output be returned
	;0 - String
	;1 - Int
	;2 - Float
	;3 - Boolean
	
	Local value$ = GetINIString("Data\NPCBones.ini",NPC$,bone$+"_"+section$)
	Select valuetype%
		Case 0
			Return value$
		Case 1
			Return Int(value$)
		Case 2
			Return Float(value$)
		Case 3
			If value$ = "true" Or value$ = "1"
				Return True
			Else
				Return False
			EndIf
	End Select
	
End Function

Function ChangeAngleValueForCorrectBoneAssigning(value#)
	Local numb#
	
	If value# <= 180.0
		numb# = value#
	Else
		numb# = -360+value#
	EndIf
	
	Return numb#
End Function

Function NPCSpeedChange(n.NPCs)
	
	Select n\NPCtype
		Case NPCtype173, NPCtype106, NPCtype096, NPCtype049, NPCtype939, NPCtypeMTF, NPCtype457, NPCtypeMTF2
			Select SelectedDifficulty\otherFactors
				Case NORMAL
					n\Speed = n\Speed * 1.1
				Case HARD
					n\Speed = n\Speed * 1.2
			End Select
	End Select
	
End Function

Function PlayerInReachableRoom(canSpawnIn049Chamber%=False)
	Local RN$ = PlayerRoom\RoomTemplate\Name$
	Local e.Events, temp
	
	;Player is in these rooms, returning false
	If RN = "pocketdimension" Or RN = "gatea" Or RN = "dimension1499" Or RN = "room173intro" Then
		Return False
	EndIf
	;Player is at GateB and is at the surface, returning false
	If RN = "gateb" And EntityY(Collider)>1040.0*RoomScale Then
		Return False
	EndIf
	;Player is in 860's test room and inside the forest, returning false
	temp = False
	For e = Each Events
		If e\EventName$ = "room860" And e\EventState = 1.0 Then
			temp = True
			Exit
		EndIf
	Next
	If RN = "room860" And temp Then
		Return False
	EndIf
	If (Not canSpawnIn049Chamber) Then
		If SelectedDifficulty\aggressiveNPCs = False Then
			If RN = "room049" And EntityY(Collider)<=-2848*RoomScale Then
				Return False
			EndIf
		EndIf
	EndIf
	;Return true, this means player is in reachable room
	Return True
	
End Function

Function CheckForNPCInFacility(n.NPCs)
	;False (=0): NPC is not in facility (mostly meant for "dimension1499")
	;True (=1): NPC is in facility
	;2: NPC is in tunnels (maintenance tunnels/049 tunnels/939 storage room, etc...)
	
	If EntityY(n\Collider)>100.0
		Return False
	EndIf
	If EntityY(n\Collider)< -10.0
		Return 2
	EndIf
	If EntityY(n\Collider)> 7.0 And EntityY(n\Collider)<=100.0
		Return 2
	EndIf
	
	Return True
End Function

Function FindNextElevator(n.NPCs)
	Local eo.ElevatorObj, eo2.ElevatorObj
	
	For eo = Each ElevatorObj
		If eo\InFacility = n\InFacility
			If Abs(EntityY(eo\obj,True)-EntityY(n\Collider))<10.0
				For eo2 = Each ElevatorObj
					If eo2 <> eo
						If eo2\InFacility = n\InFacility
							If Abs(EntityY(eo2\obj,True)-EntityY(n\Collider))<10.0
								If EntityDistance(eo2\obj,n\Collider)<EntityDistance(eo\obj,n\Collider)
									n\PathStatus = FindPath(n, EntityX(eo2\obj,True),EntityY(eo2\obj,True),EntityZ(eo2\obj,True))
									n\CurrElevator = eo2
									Exit
								EndIf
							EndIf
						EndIf
					EndIf
				Next
				If n\CurrElevator = Null
					n\PathStatus = FindPath(n, EntityX(eo\obj,True),EntityY(eo\obj,True),EntityZ(eo\obj,True))
					n\CurrElevator = eo
				EndIf
				If n\PathStatus <> 1
					n\CurrElevator = Null
				EndIf
				Exit
			EndIf
		EndIf
	Next
	
End Function

Function GoToElevator(n.NPCs)
	Local dist#,inside%
	
	If n\PathStatus <> 1
		PointEntity n\obj,n\CurrElevator\obj
		RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
		
		inside% = False
		If Abs(EntityX(n\Collider)-EntityX(n\CurrElevator\obj,True))<280.0*RoomScale
			If Abs(EntityZ(n\Collider)-EntityZ(n\CurrElevator\obj,True))<280.0*RoomScale Then
				If Abs(EntityY(n\Collider)-EntityY(n\CurrElevator\obj,True))<280.0*RoomScale Then
					inside% = True
				EndIf
			EndIf
		EndIf
		
		dist# = EntityDistance(n\Collider,n\CurrElevator\door\frameobj)
		If n\CurrElevator\door\open
			If (dist# > 0.4 And dist# < 0.7) And inside%
				UseDoor(n\CurrElevator\door,False)
			EndIf
		Else
			If dist# < 0.7
				n\CurrSpeed = 0.0
				If n\CurrElevator\door\NPCCalledElevator=False
					n\CurrElevator\door\NPCCalledElevator = True
				EndIf
			EndIf
		EndIf
	EndIf
	
End Function

Function FinishWalking(n.NPCs,startframe#,endframe#,speed#)
	Local centerframe#
	
	If n<>Null
		centerframe# = (endframe#-startframe#)/2
		If n\Frame >= centerframe#
			AnimateNPC(n,startframe#,endframe#,speed#,False)
		Else
			AnimateNPC(n,endframe#,startframe#,-speed#,False)
		EndIf
	EndIf
	
End Function

Function ChangeNPCTextureID(n.NPCs,textureid%)
    Local o.Objects = First Objects
	If (n=Null) Then
		CreateConsoleMsg("Tried to change the texture of an invalid NPC")
		If ConsoleOpening Then
			ConsoleOpen = True
		EndIf
		Return
	EndIf
	
	n\TextureID = textureid%+1
	FreeEntity n\obj
	n\obj = CopyEntity(DTextures[textureid%+1])
	
	temp# = 0.5 / MeshWidth(n\obj)
	ScaleEntity n\obj, temp, temp, temp
	MeshCullBox (n\obj, -MeshWidth(o\NPCModelID[3]), -MeshHeight(o\NPCModelID[3]), -MeshDepth(o\NPCModelID[3]), MeshWidth(o\NPCModelID[3])*2, MeshHeight(o\NPCModelID[3])*2, MeshDepth(o\NPCModelID[3])*2)
	
	SetNPCFrame(n,n\Frame)
	
End Function

Function AnimateNPC(n.NPCs, start#, quit#, speed#, loop=True)
	Local newTime#
	Local fs.FPS_Settings = First FPS_Settings
	
	If speed > 0.0 Then 
		newTime = Max(Min(n\Frame + speed * fs\FPSfactor[0],quit),start)
		
		If loop And newTime => quit Then
			newTime = start
		EndIf
	Else
		If start < quit Then
			temp% = start
			start = quit
			quit = temp
		EndIf
		
		If loop Then
			newTime = n\Frame + speed * fs\FPSfactor[0]
			
			If newTime < quit Then 
				newTime = start
			Else If newTime > start 
				newTime = quit
			EndIf
		Else
			newTime = Max(Min(n\Frame + speed * fs\FPSfactor[0],start),quit)
		EndIf
	EndIf
	SetNPCFrame(n, newTime)
	
End Function

Function SetNPCFrame(n.NPCs, frame#)
	If (Abs(n\Frame-frame)<0.001) Then Return
	
	SetAnimTime n\obj, frame
	
	n\Frame = frame
End Function

Function Animate2#(entity%, curr#, start%, quit%, speed#, loop=True)
	
	Local newTime#
	Local fs.FPS_Settings = First FPS_Settings
	
	If speed > 0.0 Then 
		newTime = Max(Min(curr + speed * fs\FPSfactor[0],quit),start)
		
		If loop Then
			If newTime => quit Then 
				;SetAnimTime entity, start
				newTime = start
			Else
				;SetAnimTime entity, newTime
			EndIf
		Else
			;SetAnimTime entity, newTime
		EndIf
	Else
		If start < quit Then
			temp% = start
			start = quit
			quit = temp
		EndIf
		
		If loop Then
			newTime = curr + speed * fs\FPSfactor[0]
			
			If newTime < quit Then newTime = start
			If newTime > start Then newTime = quit
			
			;SetAnimTime entity, newTime
		Else
			;SetAnimTime (entity, Max(Min(curr + speed * fs\FPSfactor[0],start),quit))
			newTime = Max(Min(curr + speed * fs\FPSfactor[0],start),quit)
		EndIf
	EndIf
	
	SetAnimTime entity, newTime
	Return newTime
	
End Function 

;~IDEal Editor Parameters:
;~B#198#12AA#1344#13DD#1591#169C#185D#18B9
;~C#Blitz3D