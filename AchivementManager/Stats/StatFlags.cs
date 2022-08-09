using System;

namespace SAM.Game.Stats
{
	// Token: 0x02000010 RID: 16
	[Flags]
	internal enum StatFlags
	{
		// Token: 0x04000054 RID: 84
		None = 0,
		// Token: 0x04000055 RID: 85
		IncrementOnly = 1,
		// Token: 0x04000056 RID: 86
		Protected = 2,
		// Token: 0x04000057 RID: 87
		UnknownPermission = 4
	}
}
