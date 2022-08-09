using System;
using System.Windows.Forms;

namespace SAM.Game.Stats
{
	// Token: 0x0200000A RID: 10
	internal class AchievementInfo
	{
		// Token: 0x1700000C RID: 12
		// (get) Token: 0x06000041 RID: 65 RVA: 0x00004C0D File Offset: 0x00002E0D
		// (set) Token: 0x06000042 RID: 66 RVA: 0x00004C1A File Offset: 0x00002E1A
		public int ImageIndex
		{
			get
			{
				return this.Item.ImageIndex;
			}
			set
			{
				this.Item.ImageIndex = value;
			}
		}

		// Token: 0x0400003A RID: 58
		public string Id;

		// Token: 0x0400003B RID: 59
		public bool IsAchieved;

		// Token: 0x0400003C RID: 60
		public int Permission;

		// Token: 0x0400003D RID: 61
		public string IconNormal;

		// Token: 0x0400003E RID: 62
		public string IconLocked;

		// Token: 0x0400003F RID: 63
		public string Name;

		// Token: 0x04000040 RID: 64
		public string Description;

		// Token: 0x04000041 RID: 65
		public ListViewItem Item;
	}
}
