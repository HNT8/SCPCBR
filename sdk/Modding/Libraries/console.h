#pragma once

extern "C" {
	static int info(lua_State* state) {
		std::vector<std::string> strings;
		for (int i = 1; i <= lua_gettop(state); i++) {
			if (!lua_isstring(state, i)) {
				lua_pushliteral(state, "All arguments must be a string type! Error at index " + i);
				lua_error(state);
				return 0;
			}

			strings.push_back(lua_tostring(state, i));
		}

		std::string msg = "";
		for (std::string str : strings)
			msg += str;

		lua_getglobal(state, "ModDelimeter");
		Info(msg, lua_tostring(state, -1));

		return 0;
	}

	static int warn(lua_State* state) {
		std::vector<std::string> strings;
		for (int i = 1; i <= lua_gettop(state); i++) {
			if (!lua_isstring(state, i)) {
				lua_pushliteral(state, "All arguments must be a string type! Error at index " + i);
				lua_error(state);
				return 0;
			}

			strings.push_back(lua_tostring(state, i));
		}

		std::string msg = "";
		for (std::string str : strings)
			msg += str;

		lua_getglobal(state, "ModDelimeter");
		Warn(msg, lua_tostring(state, -1));

		return 0;
	}

	static int error(lua_State* state) {
		std::vector<std::string> strings;
		for (int i = 1; i <= lua_gettop(state); i++) {
			if (!lua_isstring(state, i)) {
				lua_pushliteral(state, "All arguments must be a string type! Error at index " + i);
				lua_error(state);
				return 0;
			}

			strings.push_back(lua_tostring(state, i));
		}

		std::string msg = "";
		for (std::string str : strings)
			msg += str;

		lua_getglobal(state, "ModDelimeter");
		Error(msg, lua_tostring(state, -1));

		return 0;
	}

	static int trace(lua_State* state) {
		std::vector<std::string> strings;
		for (int i = 1; i <= lua_gettop(state); i++) {
			if (!lua_isstring(state, i)) {
				lua_pushliteral(state, "All arguments must be a string type! Error at index " + i);
				lua_error(state);
				return 0;
			}

			strings.push_back(lua_tostring(state, i));
		}

		std::string msg = "";
		for (std::string str : strings)
			msg += str;

		lua_getglobal(state, "ModDelimeter");
		Trace(msg, lua_tostring(state, -1));

		return 0;
	}
}