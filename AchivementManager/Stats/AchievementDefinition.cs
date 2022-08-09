using System;
using System.Globalization;

namespace SAM.Game.Stats
{
	// Token: 0x02000009 RID: 9
	internal class AchievementDefinition
	{
		// Token: 0x0600003F RID: 63 RVA: 0x00004BB8 File Offset: 0x00002DB8
		public override string ToString()
		{
			IFormatProvider currentCulture = CultureInfo.CurrentCulture;
			string format = "{0}: {1}";
			object[] array = new object[2];
			int num = 0;
			string text;
			if ((text = this.Name) == null)
			{
				text = (this.Id ?? base.ToString());
			}
			array[num] = text;
			array[1] = this.Permission;
			return string.Format(currentCulture, format, array);
		}

		// Token: 0x04000033 RID: 51
		public string Id;

		// Token: 0x04000034 RID: 52
		public string Name;

		// Token: 0x04000035 RID: 53
		public string Description;

		// Token: 0x04000036 RID: 54
		public string IconNormal;

		// Token: 0x04000037 RID: 55
		public string IconLocked;

		// Token: 0x04000038 RID: 56
		public bool IsHidden;

		// Token: 0x04000039 RID: 57
		public int Permission;
	}
}
