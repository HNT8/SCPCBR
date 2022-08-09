using System;
using System.Windows.Forms;
using SAM.API;

namespace SAM.Game
{
	// Token: 0x02000006 RID: 6
	internal static class Program
	{
		// Token: 0x0600002A RID: 42 RVA: 0x00004854 File Offset: 0x00002A54
		[STAThread]
		public static void Main(string[] args)
		{
			long num = 2090230L;
			using (Client client = new Client())
			{
				try
				{
					client.Initialize(num);
				}
				catch (ClientInitializeException ex)
				{
					if (ex.Failure == 5)
					{
						MessageBox.Show("Steam is not running. Please start Steam then run this tool again.\n\nIf you have the game through Family Share, the game may be locked due to\n\nthe Family Share account actively playing a game.\n\n(" + ex.Message + ")", "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
					}
					else if (!string.IsNullOrEmpty(ex.Message))
					{
						MessageBox.Show("Steam is not running. Please start Steam then run this tool again.\n\n(" + ex.Message + ")", "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
					}
					else
					{
						MessageBox.Show("Steam is not running. Please start Steam then run this tool again.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
					}
					return;
				}
				catch (DllNotFoundException)
				{
					MessageBox.Show("You've caused an exceptional error!", "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
					return;
				}
				Application.EnableVisualStyles();
				Application.SetCompatibleTextRenderingDefault(false);
				Application.Run(new Manager(num, client));
			}
		}
	}
}
