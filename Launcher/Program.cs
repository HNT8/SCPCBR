using System.Runtime.InteropServices;
using System.Threading;
using System.Diagnostics;
using System;
using System.Threading.Tasks;

using DiscordRPC;
using System.IO;
using System.Windows.Forms;

namespace scpcbr
{
    internal class Program
    {
        [DllImport("user32.dll")]
        static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);

        public static bool DiscordRP = true;
        public static bool Debug = false;
        public static DiscordRpcClient DiscordClient;
        public static Process GameProcess;

        public static string Executable = Directory.GetCurrentDirectory() + "\\remastered.exe";

        public static string[] Args;

        static void Main(string[] args)
        {
            Args = args;
            MainTask().GetAwaiter().GetResult();
        }

        static async Task MainTask()
        {
            if (Args.Length > 0)
                foreach (string arg in Args)
                {
                    if (arg == "-nodiscordrp") DiscordRP = false;
                    if (arg == "-leveleditor") Executable = Directory.GetCurrentDirectory() + "\\Map Creator\\mapcreator.exe";
                    if (arg == "-debug") Debug = true;
                    if (arg == "-resetachievements")
                    {
                        Process.Start(Directory.GetCurrentDirectory() + "\\achmgr.exe");
                        MessageBox.Show("Successfully reset achievement progress!");
                        Environment.Exit(0);
                    }
                    if (arg == "-resetsettings")
                    {
                        DialogResult msg = MessageBox.Show("WARNING\nThis will change all of your launcher & in-game settings!\nAre you sure you want to continue?", "SCP: Containment Breach Remastered", MessageBoxButtons.YesNo, MessageBoxIcon.Warning);
                        if (msg == DialogResult.No) Environment.Exit(0);
                        string defaultini = File.ReadAllText("Source Code/defaultoptions.ini");
                        File.WriteAllText("Source Code/options.ini", defaultini);
                        MessageBox.Show("Successfully reset settings.", "SCP: Containment Breach Remastered", MessageBoxButtons.OK, MessageBoxIcon.Information);
                        Environment.Exit(0);
                    }
                }

            Console.Title = "SCP: Containment Breach Remastered Bootstrapper";
            if (Debug) Console.WriteLine("You have launched with bootstrapper debugging enabled.");
            if (!Debug)
            {
                IntPtr h = Process.GetCurrentProcess().MainWindowHandle;
                ShowWindow(h, 0);
            }
            Console.WriteLine();

            if (DiscordRP)
            {
                try
                {
                    DiscordClient = new DiscordRpcClient("767487208684650536");
                    DiscordClient.Initialize();
                    DiscordClient.SetPresence(new RichPresence()
                    {
                        State = "Escaping Site-19",
                        Assets = new Assets()
                        {
                            LargeImageKey = "scpcbr",
                            LargeImageText = "v1.2.12.555"
                        }
                    });
                    if (Debug) Console.WriteLine("Successfully set Discord Rich Presence");
                } catch
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    if (Debug) Console.WriteLine("Discord Rich Presence failed to initialize.");
                    Console.ResetColor();
                    DialogResult msg = MessageBox.Show("Failed to launch Discord Rich Presence. Would you like to continue launching the game without it?", "SCP:CBR", MessageBoxButtons.YesNo, MessageBoxIcon.Error);
                    if (msg.HasFlag(DialogResult.No)) Environment.Exit(0);
                    if (Debug) Console.WriteLine("You have selected to continue launch after Discord Rich Presence failing to launch.");
                }
            }

            try
            {
                GameProcess = Process.Start(Executable);
                if (Debug) Console.WriteLine("Successfully started remastered.exe process.");
                GameProcess.EnableRaisingEvents = true;
                GameProcess.Exited += Exited;
                if (Debug) Console.WriteLine("Set process exit event.");

                if (Debug) Console.WriteLine("Successfully launched game.");
                if (Debug) Console.WriteLine("Waiting for game to exit.");

                Console.WriteLine();

                if (Debug)
                {
                    while (true)
                    {
                        Console.Write(" >> ");
                        string cmd = Console.ReadLine();
                        cmd = cmd.ToLower();
                        string[] cmdargs = cmd.Split(' ');

                        if (!Debug) break;

                        if (cmdargs[0] == "help")
                        {
                            Console.WriteLine("exit - Kills the game process and closes the launcher.");
                            Console.WriteLine("exit_debug - Exits debug mode and returns to normal mode.");
                            Console.WriteLine("restart - Closes the game & launcher, and runs the launcher again using the same command line arguments provided.");
                            Console.WriteLine("reset_settings - Resets all the user in-game options.");
                            Console.WriteLine("reset_achivements - Resets all the user achievements.");
                        }
                        if (cmdargs[0] == "exit")
                        {
                            GameProcess.Kill();
                            Environment.Exit(0);
                        }
                        if (cmdargs[0] == "exit_debug")
                        {
                            IntPtr h = Process.GetCurrentProcess().MainWindowHandle;
                            ShowWindow(h, 0);
                            Debug = false;
                            break;
                        }
                        if (cmdargs[0] == "reset_settings")
                        {
                            string defaultini = File.ReadAllText("Source Code/defaultoptions.ini");
                            File.WriteAllText("Source Code/options.ini", defaultini);
                            Console.WriteLine("Overwritten options with default settings. Please restart your game to take effect.");
                        }
                        if (cmdargs[0] == "restart")
                        {
                            GameProcess.Kill();
                            Process.Start("SCPCBR.exe", String.Join(" ", Args));
                            Environment.Exit(0);
                        }
                        if (cmdargs[0] == "reset_achievements")
                        {
                            Process.Start("achmgr.exe");
                            Console.WriteLine("Successfully reset achievement progress!");
                        }
                    }
                }
                await Task.Delay(-1);
            } catch
            {
                Console.ForegroundColor = ConsoleColor.Red;
                if (Debug) Console.WriteLine("Failed to initialize. Please verify integriy of game files.");
                if (Debug) Console.ReadKey();
                Console.ResetColor();
                if (!Debug) MessageBox.Show("Failed to launch the game. Please verify the integrity of the game files.", "SCP:CBR", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        static void Exited(object sender, EventArgs e)
        {
            if (DiscordRP) DiscordClient.ClearPresence();
            Environment.Exit(0);
        }
    }
}
