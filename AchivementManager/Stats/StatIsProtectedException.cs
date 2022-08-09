using System;
using System.Runtime.Serialization;

namespace SAM.Game.Stats
{
	// Token: 0x02000012 RID: 18
	[Serializable]
	internal class StatIsProtectedException : Exception
	{
		// Token: 0x0600005C RID: 92 RVA: 0x00004D90 File Offset: 0x00002F90
		public StatIsProtectedException()
		{
		}

		// Token: 0x0600005D RID: 93 RVA: 0x00004D98 File Offset: 0x00002F98
		public StatIsProtectedException(string message) : base(message)
		{
		}

		// Token: 0x0600005E RID: 94 RVA: 0x00004DA1 File Offset: 0x00002FA1
		public StatIsProtectedException(string message, Exception innerException) : base(message, innerException)
		{
		}

		// Token: 0x0600005F RID: 95 RVA: 0x00004DAB File Offset: 0x00002FAB
		protected StatIsProtectedException(SerializationInfo info, StreamingContext context) : base(info, context)
		{
		}
	}
}
