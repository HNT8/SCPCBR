# scpcbr-launcher
The launcher for the game SCP: Containment Breach Remastered on steam.

# How the code works.

Command line arguements.
```
-nodiscordrp        / Launches the game without discord rich presence.
-leveleditor        / Launches the level editor instead of the game. (Currently broken)
-debug              / Launches the launcher in debug mode. (Provides commands to reset stats and options, close game, etc.)
-resetachievements  / Resets all the user achievements.
-resetsettings      / Resets all the user in-game options.
```

Basically how the launcher works, it will create a variable for the remastered.exe process and then bind the process exit event to a function which will shutdown Discord Rich Presence, and the launcher.
