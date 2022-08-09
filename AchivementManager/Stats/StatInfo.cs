using System;

namespace SAM.Game.Stats
{
	// Token: 0x02000011 RID: 17
	internal abstract class StatInfo
	{
		// Token: 0x17000011 RID: 17
		// (get) Token: 0x0600004F RID: 79
		public abstract bool IsModified { get; }

		// Token: 0x17000012 RID: 18
		// (get) Token: 0x06000050 RID: 80 RVA: 0x00004D01 File Offset: 0x00002F01
		// (set) Token: 0x06000051 RID: 81 RVA: 0x00004D09 File Offset: 0x00002F09
		public string Id { get; set; }

		// Token: 0x17000013 RID: 19
		// (get) Token: 0x06000052 RID: 82 RVA: 0x00004D12 File Offset: 0x00002F12
		// (set) Token: 0x06000053 RID: 83 RVA: 0x00004D1A File Offset: 0x00002F1A
		public string DisplayName { get; set; }

		// Token: 0x17000014 RID: 20
		// (get) Token: 0x06000054 RID: 84
		// (set) Token: 0x06000055 RID: 85
		public abstract object Value { get; set; }

		// Token: 0x17000015 RID: 21
		// (get) Token: 0x06000056 RID: 86 RVA: 0x00004D23 File Offset: 0x00002F23
		// (set) Token: 0x06000057 RID: 87 RVA: 0x00004D2B File Offset: 0x00002F2B
		public bool IsIncrementOnly { get; set; }

		// Token: 0x17000016 RID: 22
		// (get) Token: 0x06000058 RID: 88 RVA: 0x00004D34 File Offset: 0x00002F34
		// (set) Token: 0x06000059 RID: 89 RVA: 0x00004D3C File Offset: 0x00002F3C
		public int Permission { get; set; }

		// Token: 0x17000017 RID: 23
		// (get) Token: 0x0600005A RID: 90 RVA: 0x00004D48 File Offset: 0x00002F48
		public string Extra
		{
			get
			{
				return (StatFlags.None | ((!this.IsIncrementOnly) ? StatFlags.None : StatFlags.IncrementOnly) | (((this.Permission & 2) == 0) ? StatFlags.None : StatFlags.Protected) | (((this.Permission & -3) == 0) ? StatFlags.None : StatFlags.UnknownPermission)).ToString();
			}
		}
	}
}
