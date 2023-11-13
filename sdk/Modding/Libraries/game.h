#pragma once

std::vector<Mod> GetAllLoadedMods();
void addRedirection(const char* gameFilePath, const char* replacePath);
void setGameTitle(std::string title);
void addMap(std::filesystem::path path);
std::vector<Language>* GetLanguagesVector();

extern "C" {
	static int wait(lua_State* state) {
		if (lua_gettop(state) != 1) {
			lua_pushliteral(state, "Incorrect amount of arguments. Game.Wait(seconds) calls for 1 argument.");
			lua_error(state);
			return 0;
		}

		if (!lua_isinteger(state, 1)) {
			lua_pushliteral(state, "Incorrect argument type for argument 1. Argument type must be an integer!");
			lua_error(state);
			return 0;
		}

		int length = lua_tointeger(state, 1);
		Sleep(length * 1000);
		return 0;
	}

	static int redirectFile(lua_State* state) {
		if (lua_gettop(state) != 2) {
			lua_pushliteral(state, "Incorrect amount of arguments. Game.RedirectFile(gameFile, fileToRedirectTo) calls for 2 arguments.");
			lua_error(state);
			return 0;
		}
		if (!lua_isstring(state, 1)) {
			lua_pushliteral(state, "Incorrect argument type for argument 1. Argument type must be a string!");
			lua_error(state);
			return 0;
		}
		if (!lua_isstring(state, 2)) {
			lua_pushliteral(state, "Incorrect argument type for argument 2. Argument type must be a string!");
			lua_error(state);
			return 0;
		}

		std::string gameFile = lua_tostring(state, 1);
		std::string modFile = lua_tostring(state, 2);

		if (gameFile.find('/') != std::string::npos || modFile.find('/') != std::string::npos) {
			lua_pushliteral(state, "The paths entered cannot contain '/'! They must use '\\'!");
			lua_error(state);
			return 0;
		}

		std::string modRelation = modFile.substr(1, modFile.find("\\") - 1);
		if (modRelation == "@") {
			lua_getglobal(state, "ModDelimeter");
			std::string modDelimiter = lua_tostring(state, -1);
			
			for (Mod mod : GetAllLoadedMods()) {
				if (mod.Delimeter == modDelimiter) {
					modFile = MODPATH + mod.ID + "\\" + modFile.substr(modFile.find("\\") + 1, modFile.size() - 1);
				}
			}
		} else {
			for (Mod mod : GetAllLoadedMods()) {
				if (mod.Delimeter == modRelation) {
					modFile = MODPATH + mod.ID + "\\" + modFile.substr(modFile.find("\\") + 1, modFile.size() - 1);
				}
			}
		}
		

		if (modFile == lua_tostring(state, 2)) {
			lua_pushliteral(state, "The mod specified in the relational path does not exist or is not downloaded.");
			lua_error(state);
			return 0;
		}

		addRedirection(gameFile.c_str(), modFile.c_str());
		return 0;
	}

	static int implementMap(lua_State* state) {
		if (lua_gettop(state) != 1) {
			lua_pushliteral(state, "Incorrect amount of arguments. Game.ImplementMap(mapFile) calls for 1 argument.");
			lua_error(state);
			return 0;
		}
		if (!lua_isstring(state, 1)) {
			lua_pushliteral(state, "Incorrect argument type for argument 1. Argument type must be a string!");
			lua_error(state);
			return 0;
		}

		std::string modFile = lua_tostring(state, 1);
		std::string modRelation = modFile.substr(1, modFile.find("\\") - 1);
		if (modRelation == "@") {
			lua_getglobal(state, "ModDelimeter");
			std::string modDelimiter = lua_tostring(state, -1);

			for (Mod mod : GetAllLoadedMods()) {
				if (mod.Delimeter == modDelimiter) {
					modFile = MODPATH + mod.ID + "\\" + modFile.substr(modFile.find("\\") + 1, modFile.size() - 1);
				}
			}
		}
		else {
			for (Mod mod : GetAllLoadedMods()) {
				if (mod.Delimeter == modRelation) {
					modFile = MODPATH + mod.ID + "\\" + modFile.substr(modFile.find("\\") + 1, modFile.size() - 1);
				}
			}
		}

		if (modFile == lua_tostring(state, 1)) {
			lua_pushliteral(state, "The mod specified in the relational path does not exist or is not downloaded.");
			lua_error(state);
			return 0;
		}

		std::filesystem::path mapPath(modFile);
		if (mapPath.extension().string() != ".cbmap" && mapPath.extension().string() != ".cbmap2") {
			lua_pushliteral(state, "The map file specified must be a \"cbmap\" or \"cbmap2\" file.");
			lua_error(state);
			return 0;
		}

		std::filesystem::copy(mapPath, std::filesystem::current_path().string() + "\\Map Creator\\Maps\\" + mapPath.filename().string());

		addMap(std::filesystem::current_path().string() + "\\Map Creator\\Maps\\" + mapPath.filename().string());
		return 0;
	}

	static int implementLang(lua_State* state) {
		if (lua_gettop(state) != 1) {
			lua_pushliteral(state, "Incorrect amount of arguments. Game.ImplementLang(langFile) calls for 1 argument.");
			lua_error(state);
			return 0;
		}
		if (!lua_isstring(state, 1)) {
			lua_pushliteral(state, "Incorrect argument type for argument 1. Argument type must be a string!");
			lua_error(state);
			return 0;
		}

		std::string modFile = lua_tostring(state, 1);
		std::string modRelation = modFile.substr(1, modFile.find("\\") - 1);
		if (modRelation == "@") {
			lua_getglobal(state, "ModDelimeter");
			std::string modDelimiter = lua_tostring(state, -1);

			for (Mod mod : GetAllLoadedMods()) {
				if (mod.Delimeter == modDelimiter) {
					modFile = MODPATH + mod.ID + "\\" + modFile.substr(modFile.find("\\") + 1, modFile.size() - 1);
				}
			}
		}
		else {
			for (Mod mod : GetAllLoadedMods()) {
				if (mod.Delimeter == modRelation) {
					modFile = MODPATH + mod.ID + "\\" + modFile.substr(modFile.find("\\") + 1, modFile.size() - 1);
				}
			}
		}

		if (modFile == lua_tostring(state, 1)) {
			lua_pushliteral(state, "The mod specified in the relational path does not exist or is not downloaded.");
			lua_error(state);
			return 0;
		}

		std::filesystem::path langPath(modFile);
		if (langPath.extension().string() != ".toml") {
			lua_pushliteral(state, "The language file specified must be a \"toml\" file.");
			lua_error(state);
			return 0;
		}

		toml::parse_result langConfig = toml::parse_file(modFile);
		Language lang;
		if (langConfig["language"]["name"].is_value())
			lang.Name = langConfig["language"]["name"].value_exact<const char*>().value();
		else if (langConfig["language.name"].is_value())
			lang.Name = langConfig["language.name"].value_exact<const char*>().value();
		else
			lang.Name = modRelation;
		lang.Toml = langConfig;

		GetLanguagesVector()->push_back(lang);
		return 0;
	}

	static int setGameTitle(lua_State* state) {
		if (lua_gettop(state) != 1) {
			lua_pushliteral(state, "Incorrect amount of arguments. Game.SetGameTitle(title) calls for 1 argument.");
			lua_error(state);
			return 0;
		}

		if (!lua_isstring(state, 1)) {
			lua_pushliteral(state, "Incorrect argument type for argument 1. Argument type must be a string!");
			lua_error(state);
			return 0;
		}

		setGameTitle(lua_tostring(state, 1));

		return 0;
	}
}