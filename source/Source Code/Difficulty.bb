Type Difficulty
	Field name$
	Field description$
	Field permaDeath%
	Field aggressiveNPCs
	Field saveType%
	Field otherFactors%
	
	Field r%
	Field g%
	Field b%
	
	Field customizable%
	
	;{~--<MOD>--~}
	
	Field menu%
	Field TwoSlots%
	
	;{~--<END>--~}
End Type

Dim difficulties.Difficulty(4)

Global SelectedDifficulty.Difficulty

difficulties(SAFE) = New Difficulty
difficulties(SAFE)\menu = True
difficulties(SAFE)\name = scpLang_GetPhrase$("difficulty.safe")
difficulties(SAFE)\description =scpLang_GetPhrase$("difficulty.safed")
difficulties(SAFE)\permaDeath = False
difficulties(SAFE)\aggressiveNPCs = False
difficulties(SAFE)\saveType = SAVEANYWHERE
difficulties(SAFE)\otherFactors = EASY
difficulties(SAFE)\TwoSlots = False
difficulties(SAFE)\r = 120
difficulties(SAFE)\g = 150
difficulties(SAFE)\b = 50

difficulties(EUCLID) = New Difficulty
difficulties(EUCLID)\menu = True
difficulties(EUCLID)\name = scpLang_GetPhrase$("difficulty.euclid")
difficulties(EUCLID)\description = scpLang_GetPhrase$("difficulty.euclidd")
difficulties(EUCLID)\permaDeath = False
difficulties(EUCLID)\aggressiveNPCs = False
difficulties(EUCLID)\saveType = SAVEONSCREENS
difficulties(EUCLID)\otherFactors = NORMAL
difficulties(EUCLID)\TwoSlots = False
difficulties(EUCLID)\r = 200
difficulties(EUCLID)\g = 200
difficulties(EUCLID)\b = 0

difficulties(KETER) = New Difficulty
difficulties(KETER)\menu = True
difficulties(KETER)\name = scpLang_GetPhrase$("difficulty.keter")
difficulties(KETER)\description = scpLang_GetPhrase$("difficulty.keterd")
difficulties(KETER)\permaDeath = True
difficulties(KETER)\aggressiveNPCs = True
difficulties(KETER)\saveType = SAVEONQUIT
difficulties(KETER)\otherFactors = HARD
difficulties(KETER)\TwoSlots = False
difficulties(KETER)\r = 200
difficulties(KETER)\g = 0
difficulties(KETER)\b = 0

;{~--<MOD>--~}

difficulties(THAUMIEL) = New Difficulty
difficulties(THAUMIEL)\menu = False
difficulties(THAUMIEL)\name = scpLang_GetPhrase$("difficulty.apollyon")
difficulties(THAUMIEL)\description = scpLang_GetPhrase$("difficulty.apollyond")
difficulties(THAUMIEL)\permaDeath = True
difficulties(THAUMIEL)\aggressiveNPCs = True
difficulties(THAUMIEL)\saveType = SAVEONQUIT
difficulties(THAUMIEL)\otherFactors = HARD
difficulties(THAUMIEL)\TwoSlots = True
difficulties(THAUMIEL)\r = 100
difficulties(THAUMIEL)\g = 100
difficulties(THAUMIEL)\b = 100

;{~--<END>--~}

difficulties(CUSTOM) = New Difficulty
difficulties(CUSTOM)\name = scpLang_GetPhrase$("difficulty.custom")
difficulties(CUSTOM)\menu = True
difficulties(CUSTOM)\permaDeath = False
difficulties(CUSTOM)\aggressiveNPCs = True
difficulties(CUSTOM)\saveType = SAVEANYWHERE
difficulties(CUSTOM)\customizable = True
difficulties(CUSTOM)\otherFactors = EASY
difficulties(CUSTOM)\TwoSlots = False
difficulties(CUSTOM)\r = 255
difficulties(CUSTOM)\g = 255
difficulties(CUSTOM)\b = 255

SelectedDifficulty = difficulties(SAFE)
		
;~IDEal Editor Parameters:
;~F#0
;~C#Blitz3D