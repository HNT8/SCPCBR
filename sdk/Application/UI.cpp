#pragma once
#include "Utilities/UI.h"

// ImGui Loop
void RunUI(Window* window) {
	ImGui::SetNextWindowPos({ 0, 0 });
	ImGui::SetNextWindowSize({ (float)window->windowSize.Width, (float)window->windowSize.Height });
	ImGui::Begin(Configuration::Initial::WindowTitle, &window->exiting, ImGuiWindowFlags_NoResize | ImGuiWindowFlags_NoSavedSettings | ImGuiWindowFlags_NoCollapse | ImGuiWindowFlags_NoMove);
	
	static bool Authenticated = false;
	if (Authenticated) {
		ImGui::BeginChild("##console", ImVec2(470, 36), true);


		ImGui::SetNextItemWidth(400);
		static char Command[1024];
		ImGui::InputText("##consoleinput", Command, IM_ARRAYSIZE(Command));

		ImGui::SameLine();

		if (ImGui::Button("Send")) {
			window->CommandList->push_back(Command);
		}

		ImGui::EndChild();


		ImGui::BeginChild("##buttons", ImVec2(470, 72), true);

		if (ImGui::Button("Clear Console")) {
			window->ConsoleText->clear();
		}

		ImGui::SameLine();

		if (ImGui::Button("Spawn Omni Card")) {
			window->CommandList->push_back("spawnitem key7");
		}

		ImGui::SameLine();

		if (ImGui::Button("Toggle God Mode")) {
			window->CommandList->push_back("god");
		}

		if (ImGui::Button("Toggle No Clip")) {
			window->CommandList->push_back("noclip");
		}

		ImGui::SameLine();

		if (ImGui::Button("Toggle No Target")) {
			window->CommandList->push_back("notarget");
		}

		ImGui::SameLine();

		if (ImGui::Button("Heal Player")) {
			window->CommandList->push_back("heal");
		}

		ImGui::SameLine();

		if (ImGui::Button("Crash Game")) {
			exit(-1);
		}

		ImGui::EndChild();


		ImGui::BeginChild("##consolechild", ImVec2(470, 400), true);

		for (size_t i = window->ConsoleText->size(); i > 0; i--) {
			ImGui::Text(window->ConsoleText->at(i - 1));
		}

		ImGui::EndChild();
	}
	else {
		static char Password[1024];
		static char AuthenticationKey[1024] = "scpcbrv1.5.1";
		ImGui::InputText("##authenticationkey", Password, ((int)(sizeof(Password) / sizeof(*(Password)))), ImGuiInputTextFlags_Password);
		ImGui::SameLine();
		if (ImGui::Button("Authenticate")) 
			if (std::string(Password)._Equal(AuthenticationKey))
				Authenticated = true;
	}

	ImGui::End();
}

// First function to run before starting ImGui.
void Starting() {

}

// Last function to run before terminating process.
void Exiting() {

}

// Runs after initializing everything, right before the UI while loop.
void PostInitialization(Window* window) {
	ImGuiStyle& style = ImGui::GetStyle();
	ImGuiIO& io = ImGui::GetIO();
	io.Fonts->AddFontFromFileTTF("GFX\\font\\courbd\\Courier New.ttf", 14);

	style.Colors[ImGuiCol_TitleBg] = ImColor(188, 1, 62);
	style.Colors[ImGuiCol_TitleBgActive] = ImColor(188, 1, 62);

	style.Colors[ImGuiCol_Button] = ImColor(188, 1, 62);
	style.Colors[ImGuiCol_ButtonActive] = ImColor(211, 1, 62);
	style.Colors[ImGuiCol_ButtonHovered] = ImColor(195, 1, 62);

	style.Colors[ImGuiCol_FrameBg] = ImColor(126, 1, 32);
	style.Colors[ImGuiCol_FrameBgActive] = ImColor(126, 1, 32);
	style.Colors[ImGuiCol_FrameBgHovered] = ImColor(126, 1, 32);

	style.Colors[ImGuiCol_ScrollbarBg] = ImColor(0, 0, 0, 0);
}

// Runs right after the while loop breaks before deinitializing everything.
void PreDeinitialization(Window* window) {

}