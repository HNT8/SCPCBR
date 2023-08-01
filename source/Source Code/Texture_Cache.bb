Function GetTextureFromCache%(name$)
	For tc.Materials = Each Materials
		If tc\name = name Then Return tc\Diff
	Next
	Return 0
End Function

Function GetBumpFromCache%(name$)
	For tc.Materials = Each Materials
		If tc\name = name Then Return tc\Bump
	Next
	Return 0
End Function

Function GetCache.Materials(name$)
	For tc.Materials = Each Materials
		If tc\name = name Then Return tc
	Next
	Return Null
End Function

Function AddTextureToCache(texture%)
	Local tc.Materials = GetCache(StripPath(TextureName(texture)))
	If tc.Materials = Null Then
		tc.Materials = New Materials
		tc\name = StripPath(TextureName(texture))
		If BumpEnabled Then
			Local temp$ = GetINIString(scpModding_ProcessFilePath$("Data\materials.ini"),tc\name,"bump")
			If temp<>"" Then
				tc\Bump = LoadTexture_Strict(temp)
				TextureBlend tc\Bump,6
				TextureBumpEnvMat tc\Bump,0,0,-0.012
				TextureBumpEnvMat tc\Bump,0,1,-0.012
				TextureBumpEnvMat tc\Bump,1,0,0.012
				TextureBumpEnvMat tc\Bump,1,1,0.012
				TextureBumpEnvOffset tc\Bump,0.5
				TextureBumpEnvScale tc\Bump,1.0
			Else
				tc\Bump = 0
			EndIf
		EndIf
		tc\Diff = 0
	EndIf
	If tc\Diff = 0 Then tc\Diff = texture
End Function

Function ClearTextureCache()
	For tc.Materials = Each Materials
		If tc\Diff<>0 Then FreeTexture tc\Diff
		If tc\Bump<>0 Then FreeTexture tc\Bump
		Delete tc
	Next
End Function

Function FreeTextureCache()
	For tc.Materials = Each Materials
		If tc\Diff<>0 Then FreeTexture tc\Diff
		If tc\Bump<>0 Then FreeTexture tc\Bump
		tc\Diff = 0 : tc\Bump = 0
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D