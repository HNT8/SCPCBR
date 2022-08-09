using System;
using System.Globalization;

namespace SAM.Game.Stats
{
	// Token: 0x0200000C RID: 12
	internal class FloatStatInfo : StatInfo
	{
		// Token: 0x1700000D RID: 13
		// (get) Token: 0x06000045 RID: 69 RVA: 0x00004C30 File Offset: 0x00002E30
		// (set) Token: 0x06000046 RID: 70 RVA: 0x00004C40 File Offset: 0x00002E40
		public override object Value
		{
			get
			{
				return this.FloatValue;
			}
			set
			{
				float num = float.Parse((string)value, CultureInfo.CurrentCulture);
				if ((base.Permission & 2) != 0 && !this.FloatValue.Equals(num))
				{
					throw new StatIsProtectedException();
				}
				this.FloatValue = num;
			}
		}

		// Token: 0x1700000E RID: 14
		// (get) Token: 0x06000047 RID: 71 RVA: 0x00004C83 File Offset: 0x00002E83
		public override bool IsModified
		{
			get
			{
				return !this.FloatValue.Equals(this.OriginalValue);
			}
		}

		// Token: 0x04000047 RID: 71
		public float OriginalValue;

		// Token: 0x04000048 RID: 72
		public float FloatValue;
	}
}
