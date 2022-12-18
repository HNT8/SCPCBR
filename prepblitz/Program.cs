using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Security.Permissions;
using System.Windows.Forms;
using System.Net;
using System.IO.Compression;

namespace prepblitz
{
    internal class Program
    {
        static void Main(string[] args)
        {
            if (!File.Exists("C:\\Program Files (x86)\\Blitz3D TSS\\Blitz3D.exe"))
            {
                DialogResult result = MessageBox.Show("Blitz3D TSS was not found. Would you like to install Blitz3D TSS? This is required to continue.", "SCPCBR Blitz3D Prepper", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
                if (result == DialogResult.Yes)
                {
                    WebClient web = new WebClient();
                    Directory.CreateDirectory("C:\\Program Files (x86)\\Blitz3D TSS\\");
                    web.DownloadFile("https://github.com/HNT8/Blitz3D/releases/download/v1.125/Blitz3D.TSS.zip", "C:\\Program Files (x86)\\Blitz3D TSS\\Blitz3D.zip");
                    web.Dispose();
                    ZipFile.ExtractToDirectory("C:\\Program Files (x86)\\Blitz3D TSS\\Blitz3D.zip", "C:\\Program Files (x86)\\Blitz3D TSS\\");
                } else
                {
                    return;
                }
            }

            try
            {
                foreach (string file in Directory.GetFiles(Directory.GetCurrentDirectory() + "\\bin"))
                {
                    string filename = Path.GetFileName(file);
                    if (File.Exists("C:\\Program Files (x86)\\Blitz3D TSS\\bin\\" + filename)) File.Delete("C:\\Program Files (x86)\\Blitz3D TSS\\bin\\" + filename);
                    File.Copy(file, "C:\\Program Files (x86)\\Blitz3D TSS\\bin\\" + filename);
                }
                foreach (string file in Directory.GetFiles(Directory.GetCurrentDirectory() + "\\decls"))
                {
                    string filename = Path.GetFileName(file);
                    if (File.Exists("C:\\Program Files (x86)\\Blitz3D TSS\\userlibs\\" + filename)) File.Delete("C:\\Program Files (x86)\\Blitz3D TSS\\userlibs\\" + filename);
                    File.Copy(file, "C:\\Program Files (x86)\\Blitz3D TSS\\userlibs\\" + filename);
                }
            }
            catch
            {
                MessageBox.Show("Your Blitz3D TSS installation is broken. Please delete Blitz3D TSS and relaunch this program.", "SCPCBR Blitz3D Prepper");
                return;
            }

            MessageBox.Show("Successfully prepped Blitz3D TSS", "SCPCBR Blitz3D Prepper");
        }
    }
}
