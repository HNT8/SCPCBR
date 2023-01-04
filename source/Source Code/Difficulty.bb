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
difficulties(SAFE)\name = "Safe"
difficulties(SAFE)\description ="The game can be saved any time. However, as in the case of SCP Objects, a Safe classification does not mean that handling it does not pose a threat."
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
difficulties(EUCLID)\name = "Euclid"
difficulties(EUCLID)\description = "In Euclid difficulty, saving is only allowed at specific locations marked by lit up computer screens. "
difficulties(EUCLID)\description = difficulties(EUCLID)\description +"Euclid-class objects are inherently unpredictable, so that reliable containment is not always possible."
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
difficulties(KETER)\name = "Keter"
difficulties(KETER)\description = "Keter-class objects are considered the most dangerous ones in Foundation containment. "
difficulties(KETER)\description = difficulties(KETER)\description +"The same can be said for this difficulty level: the SCPs are more aggressive, and you have only one life - when you die, the game is over. "
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
difficulties(THAUMIEL)\name = "Apollyon"
difficulties(THAUMIEL)\description = "Apollyon-class objects are considered impossible to contain and/or are expected to breach containment very often. They are usually associated with world-ending threats or K-Class Scenarios. The SCPs are extremely aggressive, and you only have one life. Take your precautions."
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
difficulties(CUSTOM)\name = "Custom"
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