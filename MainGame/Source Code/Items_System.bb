Global BurntNote%

Global MaxItemAmount% = 10
Global ItemAmount%
Dim Inventory.Items(MaxItemAmount + 1)
Global InvSelect%, SelectedItem.Items

Global ClosestItem.Items

Global LastItemID%

Type ItemTemplates
	Field name$
	Field tempname$
	
	Field sound%
	
	Field found%
	
	Field obj%, objpath$, parentobjpath$
	Field invimg%,invimg2%,invimgpath$
	Field imgpath$, img%
	
	Field isAnim%
	
	Field scale#
	Field tex%, texpath$
End Type 

Function CreateItemTemplate.ItemTemplates(name$, tempname$, objpath$, invimgpath$, imgpath$, scale#, texturepath$ = "",invimgpath2$="",Anim%=0, texflags%=9)
	Local it.ItemTemplates = New ItemTemplates, n
	
	
	;if another item shares the same object, copy it
	For it2.itemtemplates = Each ItemTemplates
		If it2\objpath = objpath And it2\obj <> 0 Then it\obj = CopyEntity(it2\obj) : it\parentobjpath=it2\objpath : Exit
	Next
	
	If it\obj = 0 Then; it\obj = LoadMesh(objpath)
		If Anim<>0 Then
			it\obj = LoadAnimMesh_Strict(objpath)
			it\isAnim=True
		Else
			it\obj = LoadMesh_Strict(objpath)
			it\isAnim=False
		EndIf
		it\objpath = objpath
	EndIf
	it\objpath = objpath
	
	Local texture%
	
	If texturepath <> "" Then
		For it2.itemtemplates = Each ItemTemplates
			If it2\texpath = texturepath And it2\tex<>0 Then
				texture = it2\tex
				Exit
			EndIf
		Next
		If texture=0 Then texture=LoadTexture_Strict(texturepath,texflags%) : it\texpath = texturepath
		EntityTexture it\obj, texture
		it\tex = texture
	EndIf  
	
	it\scale = scale
	ScaleEntity it\obj, scale, scale, scale, True
	
	;if another item shares the same object, copy it
	For it2.itemtemplates = Each ItemTemplates
		If it2\invimgpath = invimgpath And it2\invimg <> 0 Then
			it\invimg = it2\invimg ;CopyImage()
			If it2\invimg2<>0 Then
				it\invimg2=it2\invimg2 ;CopyImage()
			EndIf
			Exit
		EndIf
	Next
	If it\invimg=0 Then
		it\invimg = LoadImage_Strict(invimgpath)
		it\invimgpath = invimgpath
		MaskImage(it\invimg, 255, 0, 255)
	EndIf
	
	If (invimgpath2 <> "") Then
		If it\invimg2=0 Then
			it\invimg2 = LoadImage_Strict(invimgpath2)
			MaskImage(it\invimg2,255,0,255)
		EndIf
	Else
		it\invimg2 = 0
	EndIf
	
	it\imgpath = imgpath
	
	it\tempname = tempname
	it\name = name
	
	it\sound = 1

	HideEntity it\obj
	
	Return it
	
End Function

Function InitItemTemplates()
	Local it.ItemTemplates,it2.ItemTemplates
	
	;---SCP:CB Items---
	
	it = CreateItemTemplate("Some SCP-420-J", "scp420j", ItemsPath$+"scp_420_j.x", ItemsPath$+"INV_scp_420_j.png", "", 0.0005)
	it\sound = 2
	
	CreateItemTemplate("Level 1 Key Card", "key1", ItemsPath$+"key_card.x", ItemsPath$+"INV_key_card_lvl_1.png", "", 0.0004,ItemsPath$+"key_card_lvl_1.png")
	CreateItemTemplate("Level 2 Key Card", "key2", ItemsPath$+"key_card.x", ItemsPath$+"INV_key_card_lvl_2.png", "", 0.0004,ItemsPath$+"key_card_lvl_2.png")
	CreateItemTemplate("Level 3 Key Card", "key3", ItemsPath$+"key_card.x", ItemsPath$+"INV_key_card_lvl_3.png", "", 0.0004,ItemsPath$+"key_card_lvl_3.png")
	CreateItemTemplate("Level 4 Key Card", "key4", ItemsPath$+"key_card.x", ItemsPath$+"INV_key_card_lvl_4.png", "", 0.0004,ItemsPath$+"key_card_lvl_4.png")
	CreateItemTemplate("Level 5 Key Card", "key5", ItemsPath$+"key_card.x", ItemsPath$+"INV_key_card_lvl_5.png", "", 0.0004,ItemsPath$+"key_card_lvl_5.png")
	CreateItemTemplate("Playing Card", "misc", ItemsPath$+"key_card.x", ItemsPath$+"INV_playing_card.png", "", 0.0004,ItemsPath$+"playing_card.png")
	CreateItemTemplate("Mastercard", "misc", ItemsPath$+"key_card.x", ItemsPath$+"INV_master_card.png", "", 0.0004,ItemsPath$+"master_card.png")
	CreateItemTemplate("Key Card Omni", "key7", ItemsPath$+"key_card.x", ItemsPath$+"INV_key_card_lvl_omni.png", "", 0.0004,ItemsPath$+"key_card_lvl_omni.png")
	
	it = CreateItemTemplate("SCP-860", "scp860", ItemsPath$+"scp_860.b3d", ItemsPath$+"INV_scp_860.png", "", 0.0026)
	it\sound = 3
	
	it = CreateItemTemplate("Document SCP-079", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_079.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-895", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_895.png", 0.003) : it\sound = 0 
	it = CreateItemTemplate("Document SCP-860", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_860.png", 0.003) : it\sound = 0 	
	it = CreateItemTemplate("Document SCP-860-1", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_860_1.png", 0.003) : it\sound = 0 	
	it = CreateItemTemplate("SCP-093 Recovered Materials", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_093_rm.png", 0.003) : it\sound = 0 	
	it = CreateItemTemplate("Document SCP-106", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_106.png", 0.003) : it\sound = 0	
	it = CreateItemTemplate("Dr. Allok's Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_106(2).png", 0.0025) : it\sound = 0
	it = CreateItemTemplate("Recall Protocol RP-106-N", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_RP.png", 0.0025) : it\sound = 0
	it = CreateItemTemplate("Document SCP-682", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_682.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-173", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_173.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-372", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_372.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-049", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_049.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-096", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_096.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-008", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_008.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-012", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_012.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-500", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_500.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-714", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_714.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-513", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_513.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-035", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_035.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("SCP-035 Addendum", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_035_ad.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-939", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_939.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-966", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_966.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-970", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_970.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-1048", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_1048.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-1123", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_1123.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-1162", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_1162.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document SCP-1499", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_1499.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Incident Report SCP-1048-A", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_1048_a.png", 0.003) : it\sound = 0
	
	it = CreateItemTemplate("Drawing", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_1048.png", 0.003) : it\sound = 0
	
	it = CreateItemTemplate("Leaflet", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"leaflet.png", 0.003, ItemsPath$+"note.png") : it\sound = 0
	
	it = CreateItemTemplate("Dr. L's Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_note.png", ItemsPath$+"doc_L.png", 0.0025, ItemsPath$+"note.png") : it\sound = 0
	it = CreateItemTemplate("Dr L's Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_note.png", ItemsPath$+"doc_L(2).png", 0.0025, ItemsPath$+"note.png") : it\sound = 0
	it = CreateItemTemplate("Blood-stained Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_note_bloody.png", ItemsPath$+"doc_L(3).png", 0.0025, ItemsPath$+"note_bloody.png") : it\sound = 0
	it = CreateItemTemplate("Dr. L's Burnt Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_burnt_note.png", ItemsPath$+"doc_L(4).png", 0.0025, ItemsPath$+"burnt_note.png") : it\sound = 0
	it = CreateItemTemplate("Dr L's Burnt Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_burnt_note.png", ItemsPath$+"doc_L(5).png", 0.0025, ItemsPath$+"burnt_note.png") : it\sound = 0
	it = CreateItemTemplate("Scorched Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_burnt_note.png", ItemsPath$+"doc_L(6).png", 0.0025, ItemsPath$+"burnt_note.png") : it\sound = 0
	
	it = CreateItemTemplate("Journal Page", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_Gonzales.png", 0.0025) : it\sound = 0
	
	it = CreateItemTemplate("Log #1", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"f(4).png", 0.004, ItemsPath$+"f(4).png") : it\sound = 0
	it = CreateItemTemplate("Log #2", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"f(5).png", 0.004, ItemsPath$+"f(4).png") : it\sound = 0
	it = CreateItemTemplate("Log #3", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"f(6).png", 0.004, ItemsPath$+"f(4).png") : it\sound = 0
	
	it = CreateItemTemplate("Strange Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_note.png", ItemsPath$+"doc_Strange.png", 0.0025, ItemsPath$+"note.png") : it\sound = 0
	
	it = CreateItemTemplate("Nuclear Device Document", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_NDP.png", 0.003) : it\sound = 0	
	it = CreateItemTemplate("Class D Orientation Leaflet", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_ORI.png", 0.003) : it\sound = 0	
	
	it = CreateItemTemplate("Note from Daniel", "paper", ItemsPath$+"note.x", ItemsPath$+"INV_note(2).png", ItemsPath$+"doc_dan.png", 0.0025) : it\sound = 0
	
	it = CreateItemTemplate("Burnt Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_burnt_note.png", ItemsPath$+"bn.it", 0.003, ItemsPath$+"burnt_note.png")
	it\img = BurntNote : it\sound = 0
	
	it = CreateItemTemplate("Mysterious Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_note.png", ItemsPath$+"sn.it", 0.003, ItemsPath$+"note.png") : it\sound = 0	
	
	it = CreateItemTemplate("Mobile Task Forces", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_MTF.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Security Clearance Levels", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_SC.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Object Classes", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_OBJC.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("Document", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_RAND(3).png", 0.003) : it\sound = 0 
	it = CreateItemTemplate("Addendum: 5/14 Test Log", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_note.png", ItemsPath$+"doc_RAND(2).png", 0.003, ItemsPath$+"note.png") : it\sound = 0 
	it = CreateItemTemplate("Notification", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_note.png", ItemsPath$+"doc_RAND.png", 0.003, ItemsPath$+"note.png") :it\sound = 0 	
	it = CreateItemTemplate("Incident Report SCP-106-0204", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_IR_106.png", 0.003) : it\sound = 0 
	
	it = CreateItemTemplate("Ballistic Vest", "vest", ItemsPath$+"vest.x", ItemsPath$+"INV_vest.png", "", 0.02,ItemsPath$+"vest.png") : it\sound = 4
	it = CreateItemTemplate("Heavy Ballistic Vest", "finevest", ItemsPath$+"vest.x", ItemsPath$+"INV_vest.png", "", 0.022,ItemsPath$+"vest.png")
	it\sound = 4
	it = CreateItemTemplate("Bulky Ballistic Vest", "veryfinevest", ItemsPath$+"vest.x", ItemsPath$+"INV_vest.png", "", 0.025,ItemsPath$+"vest.png")
	it\sound = 2
	
	it = CreateItemTemplate("Hazmat Suit", "hazmatsuit", ItemsPath$+"hazmat_suit.b3d", ItemsPath$+"INV_hazmat_suit.png", "", 0.013)
	it\sound = 2
	it = CreateItemTemplate("Hazmat Suit", "hazmatsuit2", ItemsPath$+"hazmat_suit.b3d", ItemsPath$+"INV_hazmat_suit.png", "", 0.013)
	it\sound = 2
	it = CreateItemTemplate("Heavy Hazmat Suit", "hazmatsuit3", ItemsPath$+"hazmat_suit.b3d", ItemsPath$+"INV_hazmat_suit.png", "", 0.013)
	it\sound = 2
	
	it = CreateItemTemplate("cup", "cup", ItemsPath$+"cup.x", ItemsPath$+"INV_cup.png", "", 0.04) : it\sound = 2
	
	it = CreateItemTemplate("Empty Cup", "emptycup", ItemsPath$+"cup.x", ItemsPath$+"INV_cup.png", "", 0.04) : it\sound = 2	
	
	it = CreateItemTemplate("SCP-500-01", "scp500pill", ItemsPath$+"pill.b3d", ItemsPath$+"INV_scp_500_pill.png", "", 0.0001) : it\sound = 2
	EntityColor it\obj ,255, 0, 0
	
	it = CreateItemTemplate("First Aid Kit", "firstaid", ItemsPath$+"first_aid.x", ItemsPath$+"INV_first_aid.png", "", 0.05)
	it = CreateItemTemplate("Small First Aid Kit", "finefirstaid", ItemsPath$+"first_aid.x", ItemsPath$+"INV_first_aid.png", "", 0.03)
	it = CreateItemTemplate("Blue First Aid Kit", "firstaid2", ItemsPath$+"first_aid.x", ItemsPath$+"INV_first_aid(2).png", "", 0.03, ItemsPath$+"first_aid_kit(2).png")
	it = CreateItemTemplate("Strange Bottle", "veryfinefirstaid", ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_strange_bottle.png", "", 0.002, ItemsPath$+"strange_bottle.png")	
	
	it = CreateItemTemplate("Gas Mask", "gasmask", ItemsPath$+"gas_mask.b3d", ItemsPath$+"INV_gas_mask.png", "", 0.02) : it\sound = 2
	it = CreateItemTemplate("Gas Mask", "supergasmask", ItemsPath$+"gas_mask.b3d", ItemsPath$+"INV_gas_mask.png", "", 0.021) : it\sound = 2
	it = CreateItemTemplate("Heavy Gas Mask", "gasmask3", ItemsPath$+"gas_mask.b3d", ItemsPath$+"INV_gas_mask.png", "", 0.021) : it\sound = 2
	
	it = CreateItemTemplate("Origami", "misc", ItemsPath$+"origami.b3d", ItemsPath$+"INV_origami.png", "", 0.003) : it\sound = 0
	
	CreateItemTemplate("Electronical components", "misc", ItemsPath$+"electronics.x", ItemsPath$+"INV_electronics.png", "", 0.0011)
	
	it = CreateItemTemplate("Metal Panel", "scp148", ItemsPath$+"metal_panel.x", ItemsPath$+"INV_metal_panel.png", "", RoomScale) : it\sound = 2
	it = CreateItemTemplate("SCP-148 Ingot", "scp148ingot", ItemsPath$+"scp_148.x", ItemsPath$+"INV_scp_148.png", "", RoomScale) : it\sound = 2
	
	CreateItemTemplate("S-NAV 300 Navigator", "nav", ItemsPath$+"navigator.x", ItemsPath$+"INV_navigator.png", ItemsPath$+"navigator_HUD.png", 0.0008)
	CreateItemTemplate("S-NAV Navigator", "nav", ItemsPath$+"navigator.x", ItemsPath$+"INV_navigator.png", ItemsPath$+"navigator_HUD.png", 0.0008)
	CreateItemTemplate("S-NAV Navigator Ultimate", "nav", ItemsPath$+"navigator.x", ItemsPath$+"INV_navigator.png", ItemsPath$+"navigator_HUD.png", 0.0008)
	CreateItemTemplate("S-NAV 310 Navigator", "nav", ItemsPath$+"navigator.x", ItemsPath$+"INV_navigator.png", ItemsPath$+"navigator_HUD.png", 0.0008)
	
	CreateItemTemplate("Radio Transceiver", "radio", ItemsPath$+"radio.x", ItemsPath$+"INV_radio.png", ItemsPath$+"radio_HUD.png", 1.0)
	CreateItemTemplate("Radio Transceiver", "fineradio", ItemsPath$+"radio.x", ItemsPath$+"INV_radio.png", ItemsPath$+"radio_HUD.png", 1.0)
	CreateItemTemplate("Radio Transceiver", "veryfineradio", ItemsPath$+"radio.x", ItemsPath$+"INV_radio.png", ItemsPath$+"radio_HUD.png", 1.0)
	CreateItemTemplate("Radio Transceiver", "18vradio", ItemsPath$+"radio.x", ItemsPath$+"INV_radio.png", ItemsPath$+"radio_HUD.png", 1.02)
	
	it = CreateItemTemplate("Cigarette", "cigarette", ItemsPath$+"scp_420_j.x", ItemsPath$+"INV_scp_420_j.png", "", 0.0004) : it\sound = 2
	
	it = CreateItemTemplate("Joint", "scp420s", ItemsPath$+"scp_420_j.x", ItemsPath$+"INV_scp_420_j.png", "", 0.0004) : it\sound = 2
	
	it = CreateItemTemplate("Smelly Joint", "scp420s", ItemsPath$+"scp_420_j.x", ItemsPath$+"INV_scp_420_j.png", "", 0.0004) : it\sound = 2
	
	it = CreateItemTemplate("Severed Hand", "hand", ItemsPath$+"severed_hand.b3d", ItemsPath$+"INV_severed_hand.png", "", 0.04) : it\sound = 2
	it = CreateItemTemplate("Black Severed Hand", "hand2", ItemsPath$+"severed_hand.b3d", ItemsPath$+"INV_severed_hand(2).png", "", 0.04, ItemsPath$+"severed_hand(2).png") : it\sound = 2
	
	CreateItemTemplate("9V Battery", "bat", ItemsPath$+"battery.x", ItemsPath$+"INV_battery_9v.png", "", 0.008)
	CreateItemTemplate("18V Battery", "18vbat", ItemsPath$+"battery.x", ItemsPath$+"INV_battery_18v.png", "", 0.01, ItemsPath$+"battery_18V.png")
	CreateItemTemplate("Strange Battery", "killbat", ItemsPath$+"battery.x", ItemsPath$+"INV_strange_battery.png", "", 0.01,ItemsPath$+"strange_battery.png")
	
	CreateItemTemplate("Eyedrops", "fineeyedrops", ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_eye_drops.png", "", 0.0012, ItemsPath$+"eye_drops.png")
	CreateItemTemplate("Eyedrops", "supereyedrops", ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_eye_drops.png", "", 0.0012, ItemsPath$+"eye_drops.png")
	CreateItemTemplate("ReVision Eyedrops", "eyedrops",ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_eye_drops.png", "", 0.0012, ItemsPath$+"eye_drops.png")
	CreateItemTemplate("RedVision Eyedrops", "eyedrops2", ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_eye_drops_red.png", "", 0.0012,ItemsPath$+"eye_drops_red.png")
	
	it = CreateItemTemplate("SCP-714", "scp714", ItemsPath$+"scp_714.b3d", ItemsPath$+"INV_scp_714.png", "", 0.3)
	it\sound = 3
	
	it = CreateItemTemplate("SCP-1025", "scp1025", ItemsPath$+"scp_1025.b3d", ItemsPath$+"INV_scp_1025.png", "", 0.1)
	it\sound = 0
	
	it = CreateItemTemplate("SCP-513", "scp513", ItemsPath$+"scp_513.x", ItemsPath$+"INV_scp_513.png", "", 0.1)
	it\sound = 2
	
	it = CreateItemTemplate("Clipboard", "clipboard", ItemsPath$+"clipboard.b3d", ItemsPath$+"INV_clipboard.png", "", 0.003, "", ItemsPath$+"INV_clipboard2.png", 1)
	
	it = CreateItemTemplate("SCP-1123", "scp1123", ItemsPath$+"scp_1123.b3d", ItemsPath$+"INV_scp_1123.png", "", 0.015) : it\sound = 2
		
	it = CreateItemTemplate("Night Vision Goggles", "supernv", ItemsPath$+"night_vision_goggles.b3d", ItemsPath$+"INV_super_night_vision_goggles.png", "", 0.02) : it\sound = 2
	it = CreateItemTemplate("Night Vision Goggles", "nvgoggles", ItemsPath$+"night_vision_goggles.b3d", ItemsPath$+"INV_night_vision_goggles.png", "", 0.02) : it\sound = 2
	it = CreateItemTemplate("Night Vision Goggles", "finenvgoggles", ItemsPath$+"night_vision_goggles.b3d", ItemsPath$+"INV_very_fine_night_vision_goggles.png", "", 0.02) : it\sound = 2
	
	it = CreateItemTemplate("Syringe", "syringe", ItemsPath$+"syringe.b3d", ItemsPath$+"INV_syringe.png", "", 0.005) : it\sound = 2
	it = CreateItemTemplate("Syringe", "finesyringe", ItemsPath$+"syringe.b3d", ItemsPath$+"INV_syringe.png", "", 0.005) : it\sound = 2
	it = CreateItemTemplate("Syringe", "veryfinesyringe", ItemsPath$+"syringe.b3d", ItemsPath$+"INV_syringe.png", "", 0.005) : it\sound = 2
	
	it = CreateItemTemplate("SCP-1499","scp1499",ItemsPath$+"scp_1499.b3d",ItemsPath$+"INV_scp_1499.png", "", 0.023) : it\sound = 2
	it = CreateItemTemplate("SCP-1499","super1499",ItemsPath$+"scp_1499.b3d",ItemsPath$+"INV_scp_1499.png", "", 0.023) : it\sound = 2
	CreateItemTemplate("Emily Ross' Badge", "badge", ItemsPath$+"badge.x", ItemsPath$+"INV_Emily_badge.png", ItemsPath$+"Emily_badge_HUD.png", 0.0001, ItemsPath$+"Emily_badge.png")
	it = CreateItemTemplate("Lost Key", "key", ItemsPath$+"key.b3d", ItemsPath$+"INV_key.png", "", 0.0028) : it\sound = 3
	it = CreateItemTemplate("Disciplinary Hearing DH-S-4137-17092", "oldpaper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"dh.s", 0.003) : it\sound = 0
	it = CreateItemTemplate("Coin", "coin", ItemsPath$+"coin.b3d", ItemsPath$+"INV_coin.png", "", 0.0005) : it\sound = 3
	it = CreateItemTemplate("Movie Ticket", "ticket", ItemsPath$+"ticket.b3d", ItemsPath$+"INV_ticket.png", ItemsPath$+"ticket_HUD.png", 0.002, ItemsPath$+"ticket.png", "", 0, 1 + 2 + 8) : it\sound = 0
	CreateItemTemplate("Old Badge", "badge", ItemsPath$+"badge.x", ItemsPath$+"INV_d_9341_badge.png", ItemsPath$+"d_9341_badge_HUD.png", 0.0001, ItemsPath$+"d_9341_badge.png", "", 0, 1 + 2 + 8)
	
	it = CreateItemTemplate("Quarter","25ct", ItemsPath$+"coin.b3d", ItemsPath$+"INV_coin.png", "", 0.0005, ItemsPath$+"coin.png", "", 0, 1 + 2 + 8) : it\sound = 3
	it = CreateItemTemplate("Wallet","wallet", ItemsPath$+"wallet.b3d", ItemsPath$+"INV_wallet.png", "", 0.0005, "", "", 1) : it\sound = 2
	
	it = CreateItemTemplate("SCP-427","scp427",ItemsPath$+"scp_427.b3d",ItemsPath$+"INV_scp_427.png", "", 0.001) : it\sound = 3
	it = CreateItemTemplate("Upgraded pill", "scp500pilldeath", ItemsPath$+"pill.b3d", ItemsPath$+"INV_scp_500_pill.png", "", 0.0001) : it\sound = 2
	EntityColor it\obj, 255, 0, 0
	it = CreateItemTemplate("Pill", "pill", ItemsPath$+"pill.b3d", ItemsPath$+"INV_pill.png", "", 0.0001) : it\sound = 2
	EntityColor it\obj, 255, 255, 255
	
	it = CreateItemTemplate("Sticky Note", "paper", ItemsPath$+"note.x", ItemsPath$+"INV_note(2).png", ItemsPath$+"note_682.png", 0.0025) : it\sound = 0
	it = CreateItemTemplate("The Modular Site Project", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_MSP.png", 0.003) : it\sound = 0
	
	it = CreateItemTemplate("Research Sector-02 Scheme", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_map.png", 0.003) : it\sound = 0
	
	it = CreateItemTemplate("Document SCP-427", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper_bloody.png", ItemsPath$+"doc_427.png", 0.003, ItemsPath$+"paper_bloody.png") : it\sound = 0
	
	;{~--<END>--~}
	
	;----Ultimate Edition Items----
	
	it = CreateItemTemplate("Syringe", "syringeinf", ItemsPath$+"syringe.b3d", ItemsPath$+"INV_syringe_infected.png", "", 0.005, ItemsPath$+"syringe_infected.png") : it\sound = 2
    
    CreateItemTemplate("Level 0 Key Card", "key0",  ItemsPath$+"key_card.x", ItemsPath$+"INV_key_card_lvl_0.png", "", 0.0004, ItemsPath$+"key_card_lvl_0.png")
    CreateItemTemplate("Level 6 Key Card", "key6",  ItemsPath$+"key_card.x", ItemsPath$+"INV_key_card_lvl_6.png", "", 0.0004, ItemsPath$+"key_card_lvl_6.png")
    
    it = CreateItemTemplate("Paper Strips", "paperstrips", ItemsPath$+"paper_strips.x", ItemsPath$+"INV_paper_strips.png", "", 0.003) : it\sound = 0
    it = CreateItemTemplate("Field Agent Log #235-001-CO5", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_O5.png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Groups of Interest Log", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_O5(2).png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Unknown Document", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper_bloody.png", ItemsPath$+"doc_unknown.png", 0.003, ItemsPath$+"paper_bloody.png") : it\sound = 0
    it = CreateItemTemplate("Unknown Note", "paper", ItemsPath$+"note.x", ItemsPath$+"INV_note_bloody.png", ItemsPath$+"unknown_note.png", 0.003, ItemsPath$+"note_bloody.png") : it\sound = 0

    CreateItemTemplate("SCP-215", "scp215", ItemsPath$+"scp_215.b3d", ItemsPath$+"INV_scp_215.png", "", 0.022, "", "", 1)
    CreateItemTemplate("Glasses Case", "glassescase", ItemsPath$+"glasses_case.b3d", ItemsPath$+"INV_glasses_case.png","",0.022, "", "",1)

    it = CreateItemTemplate("SCP-1033-RU", "scp1033ru", ItemsPath$+"scp_1033_ru.b3d", ItemsPath$+"INV_scp_1033_ru.png", "", 0.7) : it\sound = 3
    it = CreateItemTemplate("SCP-1033-RU", "super1033ru", ItemsPath$+"scp_1033_ru.b3d", ItemsPath$+"INV_scp_1033_ru.png", "", 0.7) : it\sound = 3
    
    it = CreateItemTemplate("SCP-207", "scp207", ItemsPath$+"scp_207.b3d",ItemsPath$+"INV_scp_207.png", "", 0.14) : it\sound = 5

    it = CreateItemTemplate("Document SCP-178", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_178.png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Document SCP-215", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_215.png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Document SCP-198", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_198.png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Document SCP-447", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_447.png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Document SCP-207", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_207.png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Document SCP-402", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_402.png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Document SCP-1033-RU", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_1033_ru.png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Document SCP-357", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_357.png", 0.003) : it\sound = 0
    it = CreateItemTemplate("Document SCP-XXX", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_970_1162.png", 0.003) : it\sound = 0

    it = CreateItemTemplate("Incident O5-14", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_burnt_note.png", ItemsPath$+"doc_O5_14.png", 0.0025, ItemsPath$+"burnt_note.png") : it\sound = 0

    it = CreateItemTemplate("SCP-500", "scp500", ItemsPath$+"scp_500_bottle.b3d",ItemsPath$+"INV_scp_500_bottle.png","",0.05) : it\sound = 2

    it = CreateItemTemplate("SCP-402", "scp402", ItemsPath$+"scp_402.b3d",ItemsPath$+"INV_scp_402.png","",0.075) : it\sound = 3

    it = CreateItemTemplate("SCP-357", "scp357", ItemsPath$+"scp_357.b3d",ItemsPath$+"INV_scp_357.png","",0.04) : it\sound = 2

    CreateItemTemplate("Minty 9V Battery", "mintbat", ItemsPath$+"battery.x", ItemsPath$+"INV_scp_447_battery_9v.png", "", 0.008, ItemsPath$+"scp_447_battery_9V.png")
	CreateItemTemplate("Minty 18V Battery", "mint18vbat", ItemsPath$+"battery.x", ItemsPath$+"INV_scp_447_battery_18v.png", "", 0.01, ItemsPath$+"scp_447_battery_18V.png")
	
    it = CreateItemTemplate("Severed Hand", "hand3", ItemsPath$+"severed_hand.b3d", ItemsPath$+"INV_severed_hand(3).png", "", 0.04, ItemsPath$+"severed_hand(3).png") : it\sound = 2

    it = CreateItemTemplate("Data Report", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper_bloody.png", ItemsPath$+"doc_data.png", 0.003, ItemsPath$+"paper_bloody.png") : it\sound = 0

    it = CreateItemTemplate("SCP-085 Note", "paper", ItemsPath$+"note.x", ItemsPath$+"INV_note.png", ItemsPath$+"note_085.png", 0.003, ItemsPath$+"note3.png") : it\sound = 0

    it = CreateItemTemplate("References Document", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_references.png", 0.003) : it\sound = 0
    
    it = CreateItemTemplate("Dr. Clef's Note", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_note.png", ItemsPath$+"note_Clef.png", 0.003, ItemsPath$+"note.png") : it\sound = 0

    it = CreateItemTemplate("Ballistic Helmet", "helmet", ItemsPath$+"helmet.x", ItemsPath$+"INV_helmet.png", "", 0.02) : it\sound = 2

    it = CreateItemTemplate("Upgraded Minty Pill", "mintscp500pilldeath", ItemsPath$+"pill.b3d", ItemsPath$+"INV_scp_447_pill.png", "", 0.0001) : it\sound = 2
    EntityColor it\obj, 0, 140, 0
    it = CreateItemTemplate("Minty Pill", "mintpill", ItemsPath$+"pill.b3d", ItemsPath$+"INV_scp_447_pill.png", "", 0.0001) : it\sound = 2
    EntityColor it\obj, 0, 140, 0

	;{~--<END>--~}
	
	;----Box Of Horrors Items----
	
	CreateItemTemplate("SCP-005", "scp005", ItemsPath$+"scp_005.b3d", ItemsPath$+"INV_scp_005.png", "", 0.0004,"")
    
    it = CreateItemTemplate("Document SCP-009", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_009.png", 0.003) : it\sound = 0	
    it = CreateItemTemplate("Document SCP-409", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_409.png", 0.003) : it\sound = 0	

    CreateItemTemplate("SCP-178", "scp178", ItemsPath$+"scp_178.b3d", ItemsPath$+"INV_scp_178.png", "", 0.02, "", "", 1)

    it = CreateItemTemplate("Document SCP-005", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_005.png", 0.003) : it\sound = 0	
    it = CreateItemTemplate("Note from Maynard", "paper", ItemsPath$+"note.x", ItemsPath$+"INV_note.png", ItemsPath$+"note_Maynard.png", 0.0025, ItemsPath$+"note.png") : it\sound = 0

    ;{~--<END>--~}

    ;----SCP-914 Expansion Items----
	
	it = CreateItemTemplate("Minty SCP-500-01", "mintscp500pill", ItemsPath$+"pill.b3d", ItemsPath$+"INV_scp_447_pill.png", "", 0.0001) : it\sound = 2
	EntityColor it\obj, 0, 140, 0
	
	it = CreateItemTemplate("Minty First Aid Kit", "mintfirstaid", ItemsPath$+"first_aid.x", ItemsPath$+"INV_scp_447_first_aid.png", "", 0.05, ItemsPath$+"scp_447_first_aid_kit.png")
	it = CreateItemTemplate("Minty Small First Aid Kit", "mintfinefirstaid", ItemsPath$+"first_aid.x", ItemsPath$+"INV_scp_447_first_aid.png", "", 0.03, ItemsPath$+"scp_447_first_aid_kit.png")
	it = CreateItemTemplate("Minty Blue First Aid Kit", "mintfirstaid2", ItemsPath$+"first_aid.x", ItemsPath$+"INV_scp_447_first_aid(2).png", "", 0.03, ItemsPath$+"scp_447_first_aid_kit(2).png")
	it = CreateItemTemplate("Minty Strange Bottle", "mintveryfinefirstaid", ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_scp_447_strange_bottle.png", "", 0.002, ItemsPath$+"scp_447_strange_bottle.png")
		
	CreateItemTemplate("Minty Eyedrops", "mintfineeyedrops", ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_scp_447_eye_drops.png", "", 0.0012, ItemsPath$+"scp_447_eye_drops.png")
	CreateItemTemplate("Minty Eyedrops", "mintsupereyedrops", ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_scp_447_eye_drops.png", "", 0.0012, ItemsPath$+"scp_447_eye_drops.png")
	CreateItemTemplate("Minty ReVision Eyedrops", "minteyedrops",ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_scp_447_eye_drops.png", "", 0.0012, ItemsPath$+"scp_447_eye_drops.png")
	CreateItemTemplate("Minty RedVision Eyedrops", "minteyedrops2", ItemsPath$+"eye_drops.b3d", ItemsPath$+"INV_scp_447_eye_drops_red.png", "", 0.0012,ItemsPath$+"scp_447_eye_drops_red.png")
	
	it = CreateItemTemplate("SCP-447", "scp447", ItemsPath$+"scp_447.b3d", ItemsPath$+"INV_scp_447.png", "", 0.003) : it\sound = 2
		
	it = CreateItemTemplate("Painkiller", "morphine", ItemsPath$+"syringe.b3d", ItemsPath$+"INV_syringe.png", "", 0.005, ItemsPath$+"syringe.png") : it\sound = 2
					
	;{~--<END>--~}
	
	;----Fan Breach Items----
   
    it = CreateItemTemplate("Document SCP-1079", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_1079.png", 0.003) : it\sound = 0	
    it = CreateItemTemplate("Document SCP-650", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_650.png", 0.003) : it\sound = 0

    it = CreateItemTemplate("Corrosive Ballistic Vest", "corrvest", ItemsPath$+"vest.x", ItemsPath$+"INV_vest.png", "", 0.02,ItemsPath$+"corrosive_vest.png")
	it\sound = 2
	
	it = CreateItemTemplate("SCP-1079-01", "scp1079sweet", ItemsPath$+"scp_1079_sweet.b3d", ItemsPath$+"INV_scp_1079_sweet.png", "", 0.01, "") : it\sound = 2

    ;{~--<END>--~}

    ;----Project Resurrection Items----
  
    it = CreateItemTemplate("SCP-1079", "scp1079", ItemsPath$+"scp_1079_packet.b3d",ItemsPath$+"INV_scp_1079_packet.png","",0.18) : it\sound = 2

    ;{~--<END>--~}

    ;----Nine Tailed Fox Items----

    it = CreateItemTemplate("Document SCP-457", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_457.png", 0.003) : it\sound = 0
	it = CreateItemTemplate("SCP-457 Addendum", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_457_ad.png", 0.003) : it\sound = 0

    it = CreateItemTemplate("SCP-198","scp198", ItemsPath$+"scp_198.b3d", ItemsPath$+"INV_scp_198.png", "", 0.04) : it\sound = 2
    
    it = CreateItemTemplate("SCP-109","scp109", ItemsPath$+"scp_109.b3d", ItemsPath$+"INV_scp_109.png", "", 0.0009, "", "", 1) : it\sound = 5

    it = CreateItemTemplate("Document SCP-109", "paper", ItemsPath$+"paper.x", ItemsPath$+"INV_paper.png", ItemsPath$+"doc_109.png", 0.003) : it\sound = 0

    ;{~--<END>--~}

	For it = Each ItemTemplates
		If (it\tex<>0) Then
			If (it\texpath<>"") Then
				For it2=Each ItemTemplates
					If (it2<>it) And (it2\tex=it\tex) Then
						it2\tex = 0
					EndIf
				Next
			EndIf
			FreeTexture it\tex : it\tex = 0
		EndIf
	Next
	
End Function 

Type Items
	Field name$
	Field collider%,model%
	Field itemtemplate.ItemTemplates
	Field DropSpeed#
	
	Field r%,g%,b%,a#
	
	Field level
	
	Field SoundChn%
	
	Field dist#, disttimer#
	
	Field state#, state2#
	
	Field Picked%,Dropped%
	
	Field invimg%
	Field WontColl% = False
	Field xspeed#,zspeed#
	Field SecondInv.Items[20]
	Field ID%
	Field invSlots%
End Type 

Function CreateItem.Items(name$, tempname$, x#, y#, z#, r%=0,g%=0,b%=0,a#=1.0,invSlots%=0)
	Local i.Items = New Items
	Local it.ItemTemplates
	Local o.Objects = First Objects
	
	name = Lower(name)
	tempname = Lower (tempname)
	
	For it.ItemTemplates = Each ItemTemplates
		If Lower(it\name) = name Then
			If Lower(it\tempname) = tempname Then
				i\itemtemplate = it
				i\collider = CreatePivot()			
				EntityRadius i\collider, 0.01
				EntityPickMode i\collider, 1, False
				i\model = CopyEntity(it\obj,i\collider)
				i\name = it\name
				ShowEntity i\collider
				ShowEntity i\model
			EndIf
		EndIf
	Next 
	
	i\WontColl = False
	
	If i\itemtemplate = Null Then RuntimeError("Item template not found ("+name+", "+tempname+")")
	
	ResetEntity i\collider		
	PositionEntity(i\collider, x, y, z, True)
	RotateEntity (i\collider, 0, Rand(360), 0)
	i\dist = EntityDistance(Collider, i\collider)
	i\DropSpeed = 0.0
	
	If tempname = "cup" Then
		i\r=r
		i\g=g
		i\b=b
		i\a=a
		
		Local liquid = CopyEntity(o\OtherModelsID[0])
		ScaleEntity liquid, i\itemtemplate\scale,i\itemtemplate\scale,i\itemtemplate\scale,True
		PositionEntity liquid, EntityX(i\collider,True),EntityY(i\collider,True),EntityZ(i\collider,True)
		EntityParent liquid, i\model
		EntityColor liquid, r,g,b
		
		If a < 0 Then 
			EntityFX liquid, 1
			EntityAlpha liquid, Abs(a)
		Else
			EntityAlpha liquid, Abs(a)
		EndIf
		
		
		EntityShininess liquid, 1.0
	EndIf
	
	i\invimg = i\itemtemplate\invimg
	If (tempname="clipboard") And (invSlots=0) Then
		invSlots = 10
		SetAnimTime i\model,17.0
		i\invimg = i\itemtemplate\invimg2
	ElseIf (tempname="wallet") And (invSlots=0) Then
		invSlots = 10
		SetAnimTime i\model,0.0
	EndIf
	
	i\invSlots=invSlots
	
	i\ID=LastItemID+1
	LastItemID=i\ID
	
	Return i
End Function

Function RemoveItem(i.Items)
	Local n
	FreeEntity(i\model) : FreeEntity(i\collider) : i\collider = 0
	
	For n% = 0 To MaxItemAmount - 1
		If Inventory(n) = i
			Inventory(n) = Null
			ItemAmount = ItemAmount-1
			Exit
		EndIf
	Next
	If SelectedItem = i Then
		Select SelectedItem\itemtemplate\tempname 
			Case "nvgoggles", "supernv"
			    ;[Block]
				WearingNightVision = False
			    ;[End Block]
			Case "gasmask", "supergasmask", "gasmask2", "gasmask3"
			    ;[Block]
				WearingGasMask = False
			    ;[End Block]
			Case "vest", "finevest", "veryfinevest"
			    ;[Block]
				WearingVest = False
			    ;[End Block]
			Case "hazmatsuit","hazmatsuit2","hazmatsuit3"
			    ;[Block]
				WearingHazmat = False
			    ;[End Block]	
			Case "scp714"
			    ;[Block]
				I_714\Using = False
			    ;[End Block]
			Case "scp1499","super1499"
			    ;[Block]
				I_1499\Using = False
			    ;[End Block]
			Case "scp427"
			    ;[Block]
				I_427\Using = False
				;[End Block]
				
			;{~--<MOD>--~}
			
			Case "scp178"
			    ;[Block]
		        I_178\Using = False
		        ;[End Block]
		    Case "scp215"
		        ;[Block]
			    I_215\Using = False
			    ;[End Block]
		    Case "scp1033ru", "super1033ru"
		        ;[Block]
			    I_1033RU\Using = False
			    ;[End Block]
			Case "scp402"
			    ;[Block]
			    I_402\Using = False
			    ;[End Block]
			Case "helmet"
			    ;[Block]
				WearingHelmet = False
			    ;[End Block]
			Case "scp357"
			    ;[Block]
			    I_357\Timer = 0
			    ;[End Block]
			
            ;{~--<END>--~}

		End Select
		
		SelectedItem = Null
	EndIf
	If i\itemtemplate\img <> 0
		FreeImage i\itemtemplate\img
		i\itemtemplate\img = 0
	EndIf
	Delete i
	
End Function

Function UpdateItems()
	Local n, i.Items, i2.Items
	Local xtemp#, ytemp#, ztemp#
	Local temp%, np.NPCs
	Local pick%
	Local fs.FPS_Settings = First FPS_Settings
	Local HideDist = HideDistance*0.5
	Local deletedItem% = False
	
	ClosestItem = Null
	For i.Items = Each Items
		i\Dropped = 0
		
		If (Not i\Picked) Then
			If i\disttimer < MilliSecs2() Then
				i\dist = EntityDistance(Camera, i\collider)
				i\disttimer = MilliSecs2() + 700
				If i\dist < HideDist Then ShowEntity i\collider
			EndIf
			
			If i\dist < HideDist Then
				ShowEntity i\collider
				
				If i\dist < 1.2 Then
					If ClosestItem = Null Then
						If EntityInView(i\model, Camera) Then
							;pick = LinePick(EntityX(Camera),EntityY(i\collider),EntityZ(Camera),EntityX(i\collider)-EntityX(Camera),0,EntityZ(i\collider)-EntityZ(Camera),0.0)
							;If pick=i\collider Or pick=0 Then
							If EntityVisible(i\collider,Camera) Then							
								ClosestItem = i
							EndIf
						EndIf
					ElseIf ClosestItem = i Or i\dist < EntityDistance(Camera, ClosestItem\collider) Then 
						If EntityInView(i\model, Camera) Then
							;pick = LinePick(EntityX(Camera),EntityY(i\collider),EntityZ(Camera),EntityX(i\collider)-EntityX(Camera),0,EntityZ(i\collider)-EntityZ(Camera),0.0)
							;If pick=i\collider Or pick=0 Then
							If EntityVisible(i\collider,Camera) Then
								ClosestItem = i
							EndIf
						EndIf
					EndIf
				EndIf
				
				If EntityCollided(i\collider, HIT_MAP) Then
					i\DropSpeed = 0
					i\xspeed = 0.0
					i\zspeed = 0.0
				Else
					If ShouldEntitiesFall
						pick = LinePick(EntityX(i\collider),EntityY(i\collider),EntityZ(i\collider),0,-10,0)
						If pick
							i\DropSpeed = i\DropSpeed - 0.0004 * fs\FPSfactor[0]
							TranslateEntity i\collider, i\xspeed*fs\FPSfactor[0], i\DropSpeed * fs\FPSfactor[0], i\zspeed*fs\FPSfactor[0]
							If i\WontColl Then ResetEntity(i\collider)
						Else
							i\DropSpeed = 0
							i\xspeed = 0.0
							i\zspeed = 0.0
						EndIf
					Else
						i\DropSpeed = 0
						i\xspeed = 0.0
						i\zspeed = 0.0
					EndIf
				EndIf
				
				If i\dist<HideDist*0.2 Then
					For i2.Items = Each Items
						If i<>i2 And (Not i2\Picked) And i2\dist<HideDist*0.2 Then
							
							xtemp# = (EntityX(i2\collider,True)-EntityX(i\collider,True))
							ytemp# = (EntityY(i2\collider,True)-EntityY(i\collider,True))
							ztemp# = (EntityZ(i2\collider,True)-EntityZ(i\collider,True))
							
							ed# = (xtemp*xtemp+ztemp*ztemp)
							If ed<0.07 And Abs(ytemp)<0.25 Then
								;items are too close together, push away
								If PlayerRoom\RoomTemplate\Name	<> "room2storage" Then
									xtemp = xtemp*(0.07-ed)
									ztemp = ztemp*(0.07-ed)
									
									While Abs(xtemp)+Abs(ztemp)<0.001
										xtemp = xtemp+Rnd(-0.002,0.002)
										ztemp = ztemp+Rnd(-0.002,0.002)
									Wend
									
									TranslateEntity i2\collider,xtemp,0,ztemp
									TranslateEntity i\collider,-xtemp,0,-ztemp
								EndIf
							EndIf
						EndIf
					Next
				EndIf
			Else
				HideEntity i\collider
			EndIf
		Else
			i\DropSpeed = 0
			i\xspeed = 0.0
			i\zspeed = 0.0
		EndIf
		
		deletedItem = False
	Next
	
	If ClosestItem <> Null Then
		
		If MouseHit1 Then PickItem(ClosestItem)
	EndIf
End Function

Function PickItem(item.Items)
	Local n% = 0
	Local canpickitem = True
	Local fullINV% = True
	
	For n% = 0 To MaxItemAmount - 1
		If Inventory(n) = Null
			fullINV = False
			Exit
		EndIf
	Next
	
	If WearingHazmat > 0 Then
		Msg = "You cannot pick up any items while wearing a hazmat suit."
		MsgTimer = 70*5
		Return
	EndIf
	
	If (Not fullINV) Then
		For n% = 0 To MaxItemAmount - 1
			If Inventory(n) = Null Then
				Select item\itemtemplate\tempname
					Case "scp1123"
					    ;[Block]
						If (Not I_714\Using = 1) And (Not WearingGasMask = 3) And (Not WearingHazmat = 3) Then
							If PlayerRoom\RoomTemplate\Name <> "room1123" Then
								ShowEntity at\OverlayID[14]
								LightFlash = 7
								PlaySound_Strict(LoadTempSound(SFXPath$+"SCP\1123\Touch.ogg"))
								If I_1033RU\HP = 0		
								    DeathMSG = SubjectName$+" was shot dead after attempting to attack a member of Nine-Tailed Fox. Surveillance tapes show that the subject had been "
								    DeathMSG = DeathMSG + "wandering around the site approximately 9 (nine) minutes prior, shouting the phrase " + Chr(34) + "get rid of the four pests" + Chr(34)
								    DeathMSG = DeathMSG + " in chinese. SCP-1123 was found in [DATA REDACTED] nearby, suggesting the subject had come into physical contact with it. How "
								    DeathMSG = DeathMSG + "exactly SCP-1123 was removed from its containment chamber is still unknown."
								    Kill()
								Else
								    Damage1033RU(70+(Rand(5)*SelectedDifficulty\aggressiveNPCs))
                                EndIf
							EndIf
							For e.Events = Each Events
								If e\EventName = "room1123" Then 
									If e\eventstate = 0 Then
										ShowEntity at\OverlayID[14]
										LightFlash = 3
										PlaySound_Strict(LoadTempSound(SFXPath$+"SCP\1123\Touch.ogg"))
									EndIf
									e\eventstate = Max(1, e\eventstate)
									Exit
								EndIf
							Next
						EndIf
						
						Return
					    ;[End Block]
					Case "killbat"
					    ;[Block]
						ShowEntity at\OverlayID[14]
						LightFlash = 1.0
						PlaySound_Strict(IntroSFX(11))
						If I_1033RU\HP = 0
						    DeathMSG = SubjectName$+" found dead inside SCP-914's output booth next to what appears to be an ordinary nine-volt battery. The subject is covered in severe "
						    DeathMSG = DeathMSG + "electrical burns, and assumed to be killed via an electrical shock caused by the battery. The battery has been stored for further study."
						    Kill()
						Else
						    Damage1033RU(100 * I_1033RU\Using)
						EndIf
					    ;[End Block]
					Case "scp148"
					    ;[Block]
						GiveAchievement(Achv148)
					    ;[End Block]
					Case "scp513"
					    ;[Block]
						GiveAchievement(Achv513)
					    ;[End Block]
					Case "scp860"
					    ;[Block]
						GiveAchievement(Achv860)
					    ;[End Block]
					Case "key7"
					    ;[Block]
						GiveAchievement(AchvOmni)
					    ;[End Block]
					Case "veryfinevest"
					    ;[Block]
						Msg = "The vest is too heavy to pick up."
						MsgTimer = 70*6
						Exit
					    ;[End Block]
					Case "firstaid", "finefirstaid", "firstaid2"
					    ;[Block]
						item\state = 0
					    ;[End Block]
					Case "navigator", "nav"
					    ;[Block]
						If item\itemtemplate\name = "S-NAV Navigator Ultimate" Then GiveAchievement(AchvSNAV)
					    ;[End Block]
					Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
					    ;[Block]
					    If I_1499\Using > 0
					        Msg = "You need to take off the SCP-1499 in order to put on a hazmat suit."	
					        MsgTimer = 70 * 5
					        SelectedItem = Null
					        Return
					    ElseIf I_178\Using > 0 Then
					        Msg = "You need to take off the SCP-178 in order to put on a hazmat suit."
                            MsgTimer = 70 * 5
                            SelectedItem = Null
					        Return
					    ElseIf I_215\Using > 0 Then
					        Msg = "You need to take off the SCP-215 in order to put on a hazmat suit."
                            MsgTimer = 70 * 5
                            SelectedItem = Null
					        Return
					    ElseIf WearingNightVision > 0
					        Msg = "You need to take off the goggles in order to put on a hazmat suit."
                            MsgTimer = 70 * 5
                            SelectedItem = Null
					        Return
                        ElseIf WearingGasMask > 0
                           	Msg = "You need to take off a gas mask in order to put on a hazmat suit."
						    MsgTimer = 70 * 5
						    SelectedItem = Null
						    Return
                        Else	
						    canpickitem = True
						    For z% = 0 To MaxItemAmount - 1
							    If Inventory(z) <> Null Then
								    If Inventory(z)\itemtemplate\tempname="hazmatsuit" Or Inventory(z)\itemtemplate\tempname="hazmatsuit2" Or Inventory(z)\itemtemplate\tempname="hazmatsuit3" Then
									    canpickitem% = False
									    Exit
							    	ElseIf Inventory(z)\itemtemplate\tempname="vest" Or Inventory(z)\itemtemplate\tempname="finevest" Then
									    canpickitem% = 2
									    Exit
								    EndIf
							    EndIf
						    Next
						
						    If canpickitem=False Then
							    Msg = "You are not able to wear two hazmat suits at the same time."
							    MsgTimer = 70 * 5
							    Return
						    ElseIf canpickitem=2 Then
							    Msg = "You are not able to wear a vest and a hazmat suit at the same time."
							    MsgTimer = 70 * 5
							    Return
						    Else
							    SelectedItem = item
						    EndIf
						EndIf
						;[End Block]
					Case "vest","finevest"
					    ;[Block]
						canpickitem = True
						For z% = 0 To MaxItemAmount - 1
							If Inventory(z) <> Null Then
								If Inventory(z)\itemtemplate\tempname="vest" Or Inventory(z)\itemtemplate\tempname="finevest" Then
									canpickitem% = False
									Exit
								ElseIf Inventory(z)\itemtemplate\tempname="hazmatsuit" Or Inventory(z)\itemtemplate\tempname="hazmatsuit2" Or Inventory(z)\itemtemplate\tempname="hazmatsuit3" Then
									canpickitem% = 2
									Exit
								EndIf
							EndIf
						Next
						
						If canpickitem=False Then
							Msg = "You are not able to wear two vests at the same time."
							MsgTimer = 70 * 5
							Return
						ElseIf canpickitem=2 Then
							Msg = "You are not able to wear a vest and a hazmat suit at the same time."
							MsgTimer = 70 * 5
							Return
						Else
							SelectedItem = item
						EndIf
					    ;[End Block]
					Case "veryfinefirstaid"
					    ;[Block]
					    item\state = 0
					    If I_402\Timer > 0 Then
					        PlaySound_Strict(HorrorSFX(Rand(0, 3)))
					        Msg = Chr(34) + "I... Can't..." + Chr(34)
					        MsgTimer = 70 * 6
					        Exit
					    EndIf
					
					;{~--<MOD>--~}
					
				    Case "scp178"
				        ;[Block]
						SetAnimTime item\model, 19.0
					    ;[End Block]
					Case "glassescase"
					    ;[Block]
					    SetAnimTime item\model, 19.0
				        ;[End Block]
					Case "scp215"
					    ;[Block]
					    SetAnimTime item\model, 1.0
					    ;[End Block]
					Case "key6"
					    ;[Block]
				        GiveAchievement(AchvKeyCard6)
				        ;[End Block]
				    Case "scp005"
				        ;[Block]    
				   	    GiveAchievement(Achv005)
				        ;[End Block]
				    Case "scp207"
				        ;[Block]
				        If I_402\Timer > 0 Then
					        PlaySound_Strict(HorrorSFX(Rand(0, 3)))
					        Msg = Chr(34) + "I... Can't..." + Chr(34)
					        MsgTimer = 70 * 6
					        Exit
					    Else
					        GiveAchievement(Achv207)
					    EndIf
					    ;[End Block]
				    Case "scp198"
				        ;[Block]
					    GiveAchievement(Achv198)
						Msg = "You feel slight pain and SCP-198 is now attached at you."
						MsgTimer = 70*6
						PlaySound_Strict LoadTempSound(SFXPath$+"SCP\198\Shock.ogg")
						LightFlash = 2.5
						BlurTimer = 1000
					    StaminaEffect = Min(Stamina, 10)
				        StaminaEffectTimer = 240
		                Sanity = Max(-850, Sanity)
		                If I_1033RU\HP = 0
		                    Injuries = Injuries + 0.5
		                Else
		                    Damage1033RU(30 + (5 * SelectedDifficulty\aggressiveNPCs))
                        EndIf
                        ;[End Block]
                    Case "scp447", "minteyedrops", "mintfineeyedrops", "mintsupereyedrops", "minteyedrops2"
                        ;[Block]
                        If I_402\Timer > 0 Then
					        PlaySound_Strict(HorrorSFX(Rand(0, 3)))
					        Msg = Chr(34) + "I... Can't..." + Chr(34)
					        MsgTimer = 70 * 6
					        Exit
					    EndIf
					    ;[End Block]
					Case "mintveryfinefirstaid"
					    ;[Block]
					    item\state = 0
					    If I_402\Timer > 0 Then
					        PlaySound_Strict(HorrorSFX(Rand(0, 3)))
					        Msg = Chr(34) + "I... Can't..." + Chr(34)
					        MsgTimer = 70 * 6
					        Exit
					    EndIf
					    ;[End Block]
					Case "scp109"
					    ;[Block]
					    SetAnimTime item\model, 19.0
                        If I_402\Timer > 0 Then
					        PlaySound_Strict(HorrorSFX(Rand(0, 3)))
					        Msg = Chr(34) + "I... Can't..." + Chr(34)
					        MsgTimer = 70 * 6
					        Exit   
					    EndIf
					    ;[End Block]
                    Case "syringe", "syringeinf", "finesyringe", "veryfinesyringe", "cup", "morphine", "eyedrops", "eyedrops2", "fineeyedrops", "supereyedrops"
                        ;[Block]
					    If I_402\Timer > 0 Then
					        PlaySound_Strict(HorrorSFX(Rand(0, 3)))
					        Msg = Chr(34) + "I... Can't..." + Chr(34)
					        MsgTimer = 70 * 6
					        Exit
					    EndIf
					    ;[End Block]
					Case "scp357"
					    ;[Block]
                        GiveAchievement(Achv357)
                        Msg = "You grabbed the SCP-357 in your hand."
                        MsgTimer = 70 * 6
                        I_357\Timer = 1.0
                        ;[End Block]
                    Case "corrvest"
                        ;[Block]
						Msg = Chr(34) + "I'm not stupid enough to pick it up." + Chr(34)
						MsgTimer = 70*6
						Exit
					    ;[End Block]
					
				    ;{~--<END>--~}
				
				End Select
				
				If item\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(item\itemtemplate\sound))
				item\Picked = True
				item\Dropped = -1
				
				item\itemtemplate\found=True
				ItemAmount = ItemAmount + 1
				
				Inventory(n) = item
				HideEntity(item\collider)
				Exit
			EndIf
		Next
	Else
		Msg = "You cannot carry any more items."
		MsgTimer = 70 * 5
	EndIf
End Function

Function DropItem(item.Items,playdropsound%=True)
	If WearingHazmat > 0 Then
		Msg = "You cannot drop any items while wearing a hazmat suit."
		MsgTimer = 70*5
		Return
	EndIf
	
	If playdropsound Then
		If item\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(item\itemtemplate\sound))
	EndIf
	
	item\Dropped = 1
	
	ShowEntity(item\collider)
	PositionEntity(item\collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
	RotateEntity(item\collider, EntityPitch(Camera), EntityYaw(Camera)+Rnd(-20,20), 0)
	MoveEntity(item\collider, 0, -0.1, 0.1)
	RotateEntity(item\collider, 0, EntityYaw(Camera)+Rnd(-110,110), 0)
	
	ResetEntity (item\collider)
	
	item\Picked = False
	For z% = 0 To MaxItemAmount - 1
		If Inventory(z) = item Then Inventory(z) = Null
	Next
	Select item\itemtemplate\tempname
		Case "gasmask", "supergasmask", "gasmask3"
		    ;[Block]
			WearingGasMask = False
		    ;[End Block]
		Case "hazmatsuit",  "hazmatsuit2", "hazmatsuit3"
		    ;[Block]
			WearingHazmat = False
			;[End Block]
		Case "vest", "finevest"
		    ;[Block]
			WearingVest = False
			;[End Block]
		Case "nvgoggles"
		    ;[Block]
			If WearingNightVision = 1 Then CameraFogFar = StoredCameraFogFar : WearingNightVision = False
			;[End Block]
		Case "supernv"
		    ;[Block]
			If WearingNightVision = 2 Then CameraFogFar = StoredCameraFogFar : WearingNightVision = False
			;[End Block]
		Case "finenvgoggles"
		    ;[Block]
			If WearingNightVision = 3 Then CameraFogFar = StoredCameraFogFar : WearingNightVision = False
			;[End Block]
		Case "scp714"
		    ;[Block]
			I_714\Using = False
			;[End Block]
		Case "scp1499","super1499"
		    ;[Block]
			I_1499\Using = False
			;[End Block]
		Case "scp427"
		    ;[Block]
			I_427\Using = False
			;[End Block]
			
		;{~--<MOD>--~}
		
		Case "scp178"
		    ;[Block]
		    I_178\Using = False
		    ;[End Block]
		Case "scp215"
		    ;[Block]
			I_215\Using = False
		    ;[End Block]
		Case "scp1033ru", "super1033ru"
		    ;[Block]
			I_1033RU\Using = False
		    ;[End Block]
		Case "scp402"
		    ;[Block]
			I_402\Using = False
		    ;[End Block]
	    Case "scp357"
	        ;[Block]
			I_357\Timer = 0
		    ;[End Block]
		Case "helmet"
		    ;[Block]
			WearingHelmet = False
		    ;[End Block]
		
        ;{~--<END>--~}

	End Select
		
End Function

;Update any ailments inflicted by SCP-294 drinks.
Function Update294()
	Local fs.FPS_Settings = First FPS_Settings
	
	If CameraShakeTimer > 0 Then
		CameraShakeTimer = CameraShakeTimer - (fs\FPSfactor[0]/70)
		CameraShake = 2
	EndIf
	
	If VomitTimer > 0 Then
		VomitTimer = VomitTimer - (fs\FPSfactor[0]/70)
		
		If (MilliSecs2() Mod 1600) < Rand(200, 400) Then
			If BlurTimer = 0 Then BlurTimer = Rnd(10, 20)*70
			CameraShake = Rnd(0, 2)
		EndIf
		
		If Rand(50) = 50 And (MilliSecs2() Mod 4000) < 200 Then PlaySound_Strict(CoughSFX(Rand(0,2)))
		
		;Regurgitate when timer is below 10 seconds. (ew)
		If VomitTimer < 10 And Rnd(0, 500 * VomitTimer) < 2 Then
			If (Not ChannelPlaying(VomitCHN)) And (Not Regurgitate) Then
				VomitCHN = PlaySound_Strict(LoadTempSound(SFXPath$+"SCP\294\Retch" + Rand(1, 2) + ".ogg"))
				Regurgitate = MilliSecs2() + 50
			EndIf
		EndIf
		
		If Regurgitate > MilliSecs2() And Regurgitate <> 0 Then
			mouse_y_speed_1 = mouse_y_speed_1 + 1.0
		Else
			Regurgitate = 0
		EndIf
		
	ElseIf VomitTimer < 0 Then ;vomit
		VomitTimer = VomitTimer - (fs\FPSfactor[0]/70)
		
		If VomitTimer > -5 Then
			If (MilliSecs2() Mod 400) < 50 Then CameraShake = 4 
			mouse_x_speed_1 = 0.0
			Playable = False
		Else
			Playable = True
		EndIf
		
		If (Not Vomit) Then
			BlurTimer = 40 * 70
			VomitSFX = LoadSound_Strict(SFXPath$+"SCP\294\Vomit.ogg")
			VomitCHN = PlaySound_Strict(VomitSFX)
			PrevInjuries = Injuries
			PrevBloodloss = Bloodloss
			Injuries = 1.5
			Bloodloss = 70
			EyeIrritation = 9 * 70
			
			pvt = CreatePivot()
			PositionEntity(pvt, EntityX(Camera), EntityY(Collider) - 0.05, EntityZ(Camera))
			TurnEntity(pvt, 90, 0, 0)
			EntityPick(pvt, 0.3)
			de.decals = CreateDecal(5, PickedX(), PickedY() + 0.005, PickedZ(), 90, 180, 0)
			de\Size = 0.001 : de\SizeChange = 0.001 : de\MaxSize = 0.6 : EntityAlpha(de\obj, 1.0) : EntityColor(de\obj, 0.0, Rnd(200, 255), 0.0) : ScaleSprite de\obj, de\size, de\size
			FreeEntity pvt
			Vomit = True
		EndIf
		
		UpdateDecals()
		
		mouse_y_speed_1 = mouse_y_speed_1 + Max((1.0 + VomitTimer / 10), 0.0)
		
		If VomitTimer < -15 Then
			FreeSound_Strict(VomitSFX)
			VomitTimer = 0
			If KillTimer >= 0 Then
				PlaySound_Strict(BreathSFX(0,0))
			EndIf
			Injuries = PrevInjuries
			Bloodloss = PrevBloodloss
			Vomit = False
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#B#1E
;~C#Blitz3D