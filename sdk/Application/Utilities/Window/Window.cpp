#include "Window.hpp"
#include "../UI.h"

#include <exception>
#include <vector>

extern IMGUI_IMPL_API LRESULT ImGui_ImplWin32_WndProcHandler(HWND window, UINT message, WPARAM wideParameter, LPARAM longParameter);

Window* windowptr;

// Handles UI resizing.
LRESULT HandleNonclientHitTest(HWND handle, LPARAM longParameter, int title_bar_height, int resizing_border_width) {
	if (Configuration::Initial::Resizable) {
		RECT wnd_rect;
		GetWindowRect(handle, &wnd_rect);

		int wd = wnd_rect.right - wnd_rect.left;
		int hgt = wnd_rect.bottom - wnd_rect.top;

		RECT title_bar = { 0,0, wd, title_bar_height };
		RECT left = { 0, title_bar_height , resizing_border_width , hgt - title_bar_height - resizing_border_width };
		RECT right = { wd - resizing_border_width , title_bar_height , wd, hgt - title_bar_height - resizing_border_width };
		RECT bottom = { 0, hgt - resizing_border_width, wd, hgt };

		std::tuple<RECT, LRESULT> rects[] = {
			{title_bar, HTCAPTION},
			{left, HTLEFT},
			{right, HTRIGHT},
			{bottom, HTBOTTOM}
		};

		POINT pt = { LOWORD(longParameter) - wnd_rect.left, HIWORD(longParameter) - wnd_rect.top };
		for (const auto& [r, code] : rects) {
			if (PtInRect(&r, pt))
				return code;
		}
	}
	return HTCLIENT;
}

LRESULT WindowProcess(HWND window, UINT message, WPARAM wideParameter, LPARAM longParameter) {
	if (ImGui_ImplWin32_WndProcHandler(window, message, wideParameter, longParameter))
		return true;

	switch (message) {
		case WM_SIZE: {
			if (windowptr->directXDevice && wideParameter != SIZE_MINIMIZED) {
				windowptr->directXParameters.BackBufferWidth = LOWORD(longParameter);
				windowptr->directXParameters.BackBufferHeight = HIWORD(longParameter);
				windowptr->ResetDevice();
			}
		} return 0;
		case WM_SYSCOMMAND: {
			if ((wideParameter & 0xfff0) == SC_KEYMENU)
				return 0;
		} break;
		case WM_DESTROY: {
			PostQuitMessage(0);
		} return 0;
		case WM_LBUTTONDOWN: {
			windowptr->position = MAKEPOINTS(longParameter);
		} return 0;
		case WM_MOUSEMOVE: {
			if (wideParameter == MK_LBUTTON) {
				const auto points = MAKEPOINTS(longParameter);
				auto rect = ::RECT{};

				GetWindowRect(windowptr->windowHandle, &rect);

				rect.left += points.x - windowptr->position.x;
				rect.top += points.y - windowptr->position.y;

				if (windowptr->position.x >= 0 && windowptr->position.x <= windowptr->windowSize.Width && windowptr->position.y >= 0 && windowptr->position.y <= 19)
					SetWindowPos(windowptr->windowHandle, HWND_TOPMOST, rect.left, rect.top, 0, 0, SWP_SHOWWINDOW | SWP_NOSIZE | SWP_NOZORDER);
			}
		} return 0;
		case WM_NCHITTEST: {
			return HandleNonclientHitTest(windowptr->windowHandle, longParameter, 0, 5);
		}
	}

	return DefWindowProcA(window, message, wideParameter, longParameter);
}

// Initializes the WindowSize & WindowCoordinates structures.
void Window::Initialize() noexcept {
	windowSize.Width = Configuration::Initial::StartingWidth;
	windowSize.Height = Configuration::Initial::StartingHeight;

	windowCoordinates.Left = 0;
	windowCoordinates.Right = 0;
	windowCoordinates.Bottom = 0;
	windowCoordinates.Top = 0;

	windowptr = this;
}

// Creates window class.
void Window::MakeWindow() noexcept 
{
	windowClass.cbSize = sizeof(WNDCLASSEXA);
	windowClass.style = CS_CLASSDC;
	windowClass.lpfnWndProc = (WNDPROC)WindowProcess;
	windowClass.cbClsExtra = 0;
	windowClass.cbWndExtra = 0;
	windowClass.hInstance = GetModuleHandleA(0);
	windowClass.hIcon = 0;
	windowClass.hCursor = 0;
	windowClass.hbrBackground = 0;
	windowClass.lpszMenuName = 0;
	windowClass.lpszClassName = Configuration::Initial::ClassName;
	windowClass.hIconSm = 0;

	RegisterClassExA(&windowClass);

	windowHandle = CreateWindowA(Configuration::Initial::ClassName, Configuration::Initial::WindowTitle, WS_POPUP, 100, 100, windowSize.Width, windowSize.Height, 0, 0, windowClass.hInstance, 0);

	ShowWindow(windowHandle, SW_SHOWDEFAULT);
	UpdateWindow(windowHandle);
}

// Destroys the window class.
void Window::DeleteWindow() noexcept
{
	DestroyWindow(windowHandle);
	UnregisterClassA(windowClass.lpszClassName, windowClass.hInstance);
}

// Creates DirectX 9 device.
void Window::MakeDevice() noexcept
{
	directX = Direct3DCreate9(D3D_SDK_VERSION);

	if (!directX) throw std::exception("Failed to create directX 9 device!");

	ZeroMemory(&directXParameters, sizeof(directXParameters));

	directXParameters.Windowed = TRUE;
	directXParameters.SwapEffect = D3DSWAPEFFECT_DISCARD;
	directXParameters.BackBufferFormat = D3DFMT_UNKNOWN;
	directXParameters.EnableAutoDepthStencil = TRUE;
	directXParameters.AutoDepthStencilFormat = D3DFMT_D16;
	directXParameters.PresentationInterval = D3DPRESENT_INTERVAL_ONE;

	if (directX->CreateDevice(D3DADAPTER_DEFAULT, D3DDEVTYPE_HAL, windowHandle, D3DCREATE_HARDWARE_VERTEXPROCESSING, &directXParameters, &directXDevice) < 0)
		throw std::exception("Failed to create directX 9 device!");
}

// Re-creates DirectX 9 device.
void Window::ResetDevice() noexcept
{
	ImGui_ImplDX9_InvalidateDeviceObjects();

	const auto result = directXDevice->Reset(&directXParameters);

	if (result == D3DERR_INVALIDCALL)
		IM_ASSERT(0);

	ImGui_ImplDX9_CreateDeviceObjects();
}

// Destroys DirectX 9 device.
void Window::DestroyDevice() noexcept
{
	if (directXDevice) {
		directXDevice->Release();
		directXDevice = nullptr;
	}

	if (directX) {
		directX->Release();
		directX = nullptr;
	}
}

// Initializes ImGui.
void Window::MakeImGui() noexcept
{
	IMGUI_CHECKVERSION();
	imGuiContext = ImGui::CreateContext();
	imGuiIO = &ImGui::GetIO();
	imGuiIO->IniFilename = NULL;

	ImGui::StyleColorsDark();

	ImGui_ImplWin32_Init(windowHandle);
	ImGui_ImplDX9_Init(directXDevice);
}

// Destroys ImGui.
void Window::DestroyImGui() noexcept
{
	ImGui_ImplDX9_Shutdown();
	ImGui_ImplWin32_Shutdown();
	ImGui::DestroyContext();
	imGuiContext = nullptr;
}

// Initializes frame before rendering ImGui.
void Window::BeginRender() noexcept
{
	MSG message;
	while (PeekMessage(&message, 0, 0, 0, PM_REMOVE)) {
		TranslateMessage(&message);
		DispatchMessage(&message);
	}

	ImGui_ImplDX9_NewFrame();
	ImGui_ImplWin32_NewFrame();
	ImGui::NewFrame();
}

// Renders ImGui.
void Window::Render() noexcept
{
	RunUI(this);
}

// Ends the frame and clears buffers to DirectX 9 device. Re-creates DirectX 9 device if required.
void Window::EndRender() noexcept
{
	ImGui::EndFrame();

	directXDevice->SetRenderState(D3DRS_ZENABLE, FALSE);
	directXDevice->SetRenderState(D3DRS_ALPHABLENDENABLE, FALSE);
	directXDevice->SetRenderState(D3DRS_SCISSORTESTENABLE, FALSE);

	directXDevice->Clear(0, 0, D3DCLEAR_TARGET | D3DCLEAR_ZBUFFER, D3DCOLOR_RGBA(0, 0, 0, 255), 1.0f, 0);

	if (directXDevice->BeginScene() >= 0) {
		ImGui::Render();
		ImGui_ImplDX9_RenderDrawData(ImGui::GetDrawData());
		directXDevice->EndScene();
	}

	const auto result = directXDevice->Present(0, 0, 0, 0);

	if (result == D3DERR_DEVICELOST && directXDevice->TestCooperativeLevel() == D3DERR_DEVICENOTRESET)
		ResetDevice();
}

// Updates WindowSize & Coordinates structures.
void Window::UpdateSizeStructs() noexcept
{
	RECT rect;
	GetWindowRect(windowHandle, &rect);

	windowCoordinates.Left = rect.left;
	windowCoordinates.Right = rect.right;
	windowCoordinates.Top = rect.top;
	windowCoordinates.Bottom = rect.bottom;
	windowCoordinates.Center.x = rect.left + (rect.right - rect.left) / 2;
	windowCoordinates.Center.y = rect.top + -(rect.top - rect.bottom) / 2;

	windowSize.Width = rect.right - rect.left;
	windowSize.Height = -(rect.top - rect.bottom);
}

// Runs starting & exiting functions.
void Window::Start(std::vector<const char*>* commandList, std::vector<const char*>* consoleText) noexcept { Starting(); CommandList = commandList;  ConsoleText = consoleText; }
void Window::Exit() noexcept { Exiting(); }
void Window::PostInit() noexcept { PostInitialization(this); }
void Window::PreDeinit() noexcept { PreDeinitialization(this); }
