#pragma once
#include "../../Globals.hpp"
#include "../ImGui/imgui.h"
#include "../ImGui/imgui_impl_dx9.h"
#include "../ImGui/imgui_impl_win32.h"

#include <Windows.h>
#include <d3d9.h>
#include <vector>

struct Vector {
	int x, y;
};

struct WindowSize {
	int Width, Height;
};

struct WindowCoordinates {
	int Left, Right, Bottom, Top;
	Vector Center;
};

class Window {
public:
	HWND windowHandle = nullptr; // Handle to the Win32 Window.
	WNDCLASSEXA windowClass = {}; // The window's WNDClass.
	WindowSize windowSize; // Current window size.
	WindowCoordinates windowCoordinates; // Current window coordinates.

	PDIRECT3D9 directX = nullptr; // DirectX
	LPDIRECT3DDEVICE9 directXDevice = nullptr; // DirectX Device
	D3DPRESENT_PARAMETERS directXParameters = {}; // DirectX Parameters

	ImGuiContext* imGuiContext = nullptr; // ImGui Context
	ImGuiIO* imGuiIO = nullptr; // ImGui IO

	POINTS position = {}; // Used to handle moving the window.

	bool exiting = true; // Changes to false when ImGui window is closed.

	void Initialize() noexcept;

	void MakeWindow() noexcept;
	void DeleteWindow() noexcept;

	void MakeDevice() noexcept;
	void ResetDevice() noexcept;
	void DestroyDevice() noexcept;

	void MakeImGui() noexcept;
	void DestroyImGui() noexcept;

	void BeginRender() noexcept;
	void Render() noexcept;
	void EndRender() noexcept;

	void UpdateSizeStructs() noexcept;

	std::vector<const char*>* CommandList;
	std::vector<const char*>* ConsoleText;

	void Start(std::vector<const char*>* commandList, std::vector<const char*>* consoleText) noexcept;
	void Exit() noexcept;

	void PostInit() noexcept;
	void PreDeinit() noexcept;
};