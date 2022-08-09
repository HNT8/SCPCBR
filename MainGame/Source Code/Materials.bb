Type Materials
	Field name$
	Field Diff%
	Field Bump%
	Field StepSound%
End Type

Function ApplyBumpMap(texture%)
	
	TextureBlend texture%,6
	TextureBumpEnvMat texture%,0,0,-0.012
	TextureBumpEnvMat texture%,0,1,-0.012
	TextureBumpEnvMat texture%,1,0,0.012
	TextureBumpEnvMat texture%,1,1,0.012
	TextureBumpEnvOffset texture%,0.5
	TextureBumpEnvScale texture%,1.0
	
End Function

Function LoadMaterials(file$)
	Local TemporaryString$
	Local mat.Materials = Null
	Local StrTemp$ = ""
	
	Local f = OpenFile(file)
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString,1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			mat.Materials = New Materials
			
			mat\name = Lower(TemporaryString)
			
			If BumpEnabled Then
				StrTemp = GetINIString(file, TemporaryString, "bump")
				If StrTemp <> "" Then 
					mat\Bump =  LoadTexture_Strict(StrTemp)
					ApplyBumpMap(mat\Bump)			
				EndIf
			EndIf
			
			mat\StepSound = (GetINIInt(file, TemporaryString, "stepsound")+1)
		EndIf
	Wend
	
	CloseFile f
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D
