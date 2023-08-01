#define GL_SILENCE_DEPRECATION
#define GLFW_EXPOSE_NATIVE_WIN32
#define GLEW_STATIC
#define STB_IMAGE_IMPLEMENTATION

#include "GL/glew.h"

#include "GLFW/glfw3.h"
#include "GLFW/glfw3native.h"

#include "IMGUI/imgui.h"
#include "IMGUI/imgui_impl_glfw.h"
#include "IMGUI/imgui_impl_opengl3.h"
#include "IMGUI/imgui_internal.h"

#include "STBI/stb_image.h"

std::vector<std::string> split(std::string text, char delim) {
	std::string line;
	std::vector<std::string> vec;
	std::stringstream ss(text);
	while (std::getline(ss, line, delim)) {
		vec.push_back(line);
	}
	return vec;
}

int width = 800;
int height = 400;

std::ostringstream ConsoleOutput;

bool AutoWrap = true;
bool AutoScroll = true;
bool TraceMessages = false;

void DebugConsole() {
	std::cout.rdbuf(ConsoleOutput.rdbuf());

	if (!glfwInit())
		exit(1);

	const char* glsl_version = "#version 130";
	glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

	GLFWmonitor* monitor = glfwGetPrimaryMonitor();

	if (!monitor)
		exit(1);

	glfwWindowHint(GLFW_RESIZABLE, true);

	GLFWwindow* window = glfwCreateWindow(width, height, "SCP: Containment Breach Remastered Debug Console", NULL, NULL);
	if (window == NULL)
		exit(1);

	glfwMakeContextCurrent(window);
	glfwSwapInterval(0);

	if (glewInit() != GLEW_OK)
		exit(1);

	IMGUI_CHECKVERSION();
	ImGui::CreateContext();
	ImGuiIO& io = ImGui::GetIO(); (void)io;
	io.IniFilename = nullptr;
	
	ImGui::StyleColorsDark();

	ImGuiStyle& style = ImGui::GetStyle(); (void)style;
	style.Colors[ImGuiCol_Button] = ImColor(128, 19, 65, 255);
	style.Colors[ImGuiCol_ButtonActive] = ImColor(128, 19, 65, 255);
	style.Colors[ImGuiCol_ButtonHovered] = ImColor(132, 22, 69, 255);
	style.Colors[ImGuiCol_CheckMark] = ImColor(128, 19, 65, 255);
	style.Colors[ImGuiCol_FrameBg] = ImColor(51, 56, 69, 255);
	style.Colors[ImGuiCol_FrameBgActive] = ImColor(51, 56, 69, 255);
	style.Colors[ImGuiCol_FrameBgHovered] = ImColor(51, 56, 69, 255);
	style.Colors[ImGuiCol_WindowBg] = ImColor(33, 36, 43, 255);
	style.Colors[ImGuiCol_ScrollbarBg] = ImColor(33, 36, 43, 0);

	GLFWimage images[1];
	images[0].pixels = stbi_load(std::string(std::filesystem::current_path().string() + "\\GFX\\development\\remastered.png").c_str(), &images[0].width, &images[0].height, nullptr, 4);
	glfwSetWindowIcon(window, 1, images);

	ImGui_ImplGlfw_InitForOpenGL(window, true);
	ImGui_ImplOpenGL3_Init(glsl_version);

	glViewport(0, 0, width, height);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrtho(0.0, width, height, 0, 0.0, 1.0);

	while (!glfwWindowShouldClose(window)) {
		glfwPollEvents();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		int display_w, display_h;
		glfwGetFramebufferSize(window, &display_w, &display_h);

		ImGui_ImplOpenGL3_NewFrame();
		ImGui_ImplGlfw_NewFrame();
		ImGui::NewFrame();

		ImGui::SetNextWindowPos(ImVec2(0, 0));
		ImGui::SetNextWindowSize(ImVec2(display_w, display_h));
		ImGui::Begin("Debug Console", 0, ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoCollapse | ImGuiWindowFlags_NoResize | ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoBringToFrontOnFocus);

		ImGui::Checkbox("Auto Wrap", &AutoWrap);
		ImGui::SameLine();
		ImGui::Checkbox("Auto Scroll", &AutoScroll);
		ImGui::SameLine();
		ImGui::Checkbox("Show Trace Messages", &TraceMessages);

		ImGui::PushStyleColor(ImGuiCol_WindowBg, ImVec4(51 / 255.0f, 56 / 255.0f, 69 / 255.0f, 255 / 255.0f));
		ImGui::SetNextWindowSize(ImVec2(display_w - 16, display_h - 40));
		ImGui::SetNextWindowPos(ImVec2(8, 33));
		ImGui::Begin("##consoleoutputwindow", 0, ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoResize | ImGuiWindowFlags_NoCollapse | ImGuiWindowFlags_NoMove | ImGuiWindowFlags_HorizontalScrollbar);
		{
			ImGui::SetCursorPos(ImVec2(4, 4));
			for (std::string line : split(ConsoleOutput.str(), '\n')) {
				ImGui::SetCursorPos(ImVec2(4, ImGui::GetCursorPos().y));
				
				ImVec4 textColor = ImVec4(1, 1, 1, 1);

				if (line.find("WARNING") != std::string::npos)
					textColor = ImVec4(1, 165 / 255.0f, 0, 1);

				if (line.find("ERROR") != std::string::npos)
					textColor = ImVec4(1, 0, 0, 1);

				if (!TraceMessages)
					if (line.find("TRACE") != std::string::npos)
						continue;

				ImGui::PushStyleColor(ImGuiCol_Text, textColor);
				if (AutoWrap)
					ImGui::TextWrapped(line.c_str());
				else
					ImGui::Text(line.c_str());
				ImGui::PopStyleColor();
			}

			if (AutoScroll)
				ImGui::SetScrollHereY(0.999f);
		}
		ImGui::End();
		ImGui::PopStyleColor();

		ImGui::End();

		ImGui::Render();
		glViewport(0, 0, display_w, display_h);

		ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
		glfwSwapBuffers(window);
	}

	ImGui_ImplOpenGL3_Shutdown();
	ImGui_ImplGlfw_Shutdown();
	ImGui::DestroyContext();

	glfwDestroyWindow(window);
	glfwTerminate();

	exit(0);
}