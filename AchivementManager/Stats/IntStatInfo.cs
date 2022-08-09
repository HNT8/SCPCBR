using System;
using System.Globalization;

namespace SAM.Game.Stats
{
	// Token: 0x0200000E RID: 14
	internal class IntStatInfo : StatInfo
	{
		// Token: 0x1700000F RID: 15
		// (get) Token: 0x0600004A RID: 74 RVA: 0x00004CA1 File Offset: 0x00002EA1
		// (set) Token: 0x0600004B RID: 75 RVA: 0x00004CB0 File Offset: 0x00002EB0
		public override object Value
		{
			get
			{
				return this.IntValue;
			}
			set
			{
				int num = int.Parse((string)value, CultureInfo.CurrentCulture);
				if ((base.Permission & 2) != 0 && this.IntValue != num)
				{
					throw new StatIsProtectedException();
				}
				this.IntValue = num;
			}
		}

		// Token: 0x17000010 RID: 16
		// (get) Token: 0x0600004C RID: 76 RVA: 0x00004CEE File Offset: 0x00002EEE
		public override bool IsModified
		{
			get
			{
				return this.IntValue != this.OriginalValue;
			}
		}

		// Token: 0x0400004E RID: 78
		public int OriginalValue;

		// Token: 0x0400004F RID: 79
		public int IntValue;
	}
}
