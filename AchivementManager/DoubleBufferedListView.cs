using System;
using System.Windows.Forms;

namespace SAM.Game
{
	// Token: 0x02000002 RID: 2
	internal class DoubleBufferedListView : ListView
	{
		// Token: 0x06000001 RID: 1 RVA: 0x00002050 File Offset: 0x00000250
		public DoubleBufferedListView()
		{
			base.DoubleBuffered = true;
		}
	}
}
