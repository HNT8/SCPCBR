using DiscordRPC;
using Steamworks;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;

namespace workshopuploader {
    public class Program {
        [STAThread]
        public static void Main(string[] args) {
            try {
                SteamClient.Init(2090230);
            } catch (Exception e) {
                MessageBox.Show($"An error occured while initializing Steam!\nError: {e.Message}", "Application Error", MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown(1);
            }

            DiscordRpcClient dsc = new DiscordRpcClient("767487208684650536");
            dsc.Initialize();
            Timestamps startTimestamp = new Timestamps();
            startTimestamp.Start = DateTime.UtcNow;
            dsc.SetPresence(new RichPresence() {
                State = "Workshop Uploader",
                Timestamps = startTimestamp,
                Buttons = new DiscordRPC.Button[]{
                        new DiscordRPC.Button {
                            Label = "SCP:CB Remastered",
                            Url = "https://store.steampowered.com/app/2090230/SCP_Containment_Breach_Remastered/"
                        }
                    }
            });

            App application = new App();
            application.InitializeComponent();
            application.Run();
        }
    }
}
