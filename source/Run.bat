copy ..\sdk\Release\SCPSDK.dll SCPSDK.dll
copy ..\sdk\Release\SCPSDK.dll "C:\Program Files (x86)\Blitz3D TSS\bin\SCPSDK.dll"
copy ..\sdk\SCPSDK.decls "Source Code\SCPSDK.decls"
copy ..\sdk\SCPSDK.decls "C:\Program Files (x86)\Blitz3D TSS\userlibs\SCPSDK.decls"
cd Source Code
4gb_patch.exe ..\SCPCBR.exe
cd ..\
start SCPCBR.exe