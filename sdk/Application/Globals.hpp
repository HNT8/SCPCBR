#pragma once
#include "Utilities/Window/Window.hpp"

namespace Configuration {
	namespace Initial {
		static int StartingWidth = 488; // The width of the window when first launched.
		static int StartingHeight = 552; // The height of the window when first launched.
		static bool Resizable = false; // Whether the window is resizable or not.

		static const char* WindowTitle = "SCP:CBR Developer Menu"; // The title of the window.
		static const char* ClassName = "ImGuiBase"; // The class name of the window.
	}
}