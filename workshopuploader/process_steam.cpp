#include <cstddef>
#include <cstdlib>
#include <iostream>
#include <fstream>
#include <Windows.h>

#include "SteamAPI/steam_api.h"
#include "SteamAPI/isteamugc.h"

#include "util.h"
#include "process_steam.h"

WorkshopUploader::WorkshopUploader() : callback_called(false) {}

void WorkshopUploader::create_callback(CreateItemResult_t *result, bool
        _bool)
{
    DLOG("Call result called\n");

	// Make sure that the user agrees to the Steam Workshop legally
    if (result->m_bUserNeedsToAcceptWorkshopLegalAgreement) {
        std::cout << "By submitting this item, you agree to the "
            << "workshop terms of service "
            << "<http://steamcommunity.com/sharedfiles/workshoplegalagreement>"
            << ".\n";

        if (yes_no_option("Would you like to continue (y/N)")) {
            std::cout << "Exiting...";
            Sleep(3000);
            std::exit(2);
        }
    }

	// Handle some errors
    switch (result->m_eResult) {
        case k_EResultInsufficientPrivilege:
            std::cout << "You have insufficient privilege to make "
                << "a workshop item\n";
            Sleep(3000);
            std::exit(3);
            break;
        case k_EResultTimeout:
            std::cout << "The operation took longer than "
                << "expected; please try again\n";
            Sleep(3000);
            std::exit(3);
            break;
        case k_EResultNotLoggedOn:
            std::cout << "Please log onto Steam\n";
            Sleep(3000);
            std::exit(3);
            break;
    }

	std::cout << "Your new item is id# "
		<< result->m_nPublishedFileId << "\n";

	// Continue with populating with values
    AppId_t appid = 2090230;
    UGCUpdateHandle_t handle = SteamUGC()->StartItemUpdate(appid,
            result->m_nPublishedFileId);

    std::cout << "Please enter a title for your mod (max of 128 "
        << "characters):\n";
    for (;;) {
        std::string title = readline_limit(128);

        if (SteamUGC()->SetItemTitle(handle, title.c_str())) {
            break;
        } else {
            std::cout << "There was an error setting the title. "
                << "Please try again with a different title:\n";
        }
    }

	std::cout << "Please enter a description for your mod (max of "
		<< "8000 characters):\n";
    for (;;) {
	    std::string desc = readline_limit(8000);

        if (SteamUGC()->SetItemDescription(handle, desc.c_str())) {
            break;
        } else {
            std::cout << "There was an error setting the description"
                << ". Please try again with a different description:"
                << "\n";
        }
    }
	std::cout << "Please give an absolute path to your mod folder:\n";
    for (;;) {
        std::string path = readline();
        if (SteamUGC()->SetItemContent(handle, path.c_str())) {
            break;
        } else {
            std::cout << "There was an error setting the content path. "
                << "Please try again with a different location:\n";
        }
    }

	std::cout << "Please give an absolute path to a preview image.\n";
    std::cout << "Note that the image should be under 1MB and a "
        << "supported file type (png, jpg, gif):\n";
	for (;;) {
        std::string image = readline();
        if (SteamUGC()->SetItemPreview(handle, image.c_str())) {
            break;
        } else {
            std::cout << "There was an error setting the image. "
                << "Please try again with a different image:\n";
        }
    }

    SteamAPICall_t call = SteamUGC()->SubmitItemUpdate(handle, "First change");
    submit_call.Set(call, this, &WorkshopUploader::submit_callback);
}

void WorkshopUploader::submit_callback(SubmitItemUpdateResult_t
        *result, bool _bool)
{
    this->callback_called = true;

    switch (result->m_eResult) {
        case k_EResultInsufficientPrivilege:
            std::cout << "You have insufficient privilege to upload the mod.\n";
            Sleep(3000);
            std::exit(4);
            break;
        case k_EResultTimeout:
            std::cout << "Steam has timed out.\n";
            Sleep(3000);
            std::exit(4);
            break;
        case k_EResultNotLoggedOn:
            std::cout << "Please log onto Steam.\n";
            Sleep(3000);
            std::exit(4);
            break;
    }

    std::cout << "Mod successfully uploaded!\n";
    Sleep(3000);
}

void WorkshopUploader::create_item(AppId_t game)
{
    DLOG("Creating item for " << game << "\n");
    SteamAPICall_t result = SteamUGC()->CreateItem(game,
            k_EWorkshopFileTypeCommunity);
    DLOG("Created the item and setting a call result\n");
    call_result.Set(result, this, &WorkshopUploader::create_callback);
}

void WorkshopUploader::create_item()
{
    this->create_item(2090230);
}

