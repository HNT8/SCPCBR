using System;
using System.CodeDom.Compiler;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Globalization;
using System.Resources;
using System.Runtime.CompilerServices;

namespace SAM.Game
{
	// Token: 0x02000007 RID: 7
	[GeneratedCode("System.Resources.Tools.StronglyTypedResourceBuilder", "16.0.0.0")]
	[DebuggerNonUserCode]
	[CompilerGenerated]
	internal class Resources
	{
		// Token: 0x0600002B RID: 43 RVA: 0x00004954 File Offset: 0x00002B54
		internal Resources()
		{
		}

		// Token: 0x17000002 RID: 2
		// (get) Token: 0x0600002C RID: 44 RVA: 0x0000495C File Offset: 0x00002B5C
		[EditorBrowsable(EditorBrowsableState.Advanced)]
		internal static ResourceManager ResourceManager
		{
			get
			{
				if (Resources.resourceMan == null)
				{
					Resources.resourceMan = new ResourceManager("SAM.Game.Resources", typeof(Resources).Assembly);
				}
				return Resources.resourceMan;
			}
		}

		// Token: 0x17000003 RID: 3
		// (get) Token: 0x0600002D RID: 45 RVA: 0x00004988 File Offset: 0x00002B88
		// (set) Token: 0x0600002E RID: 46 RVA: 0x0000498F File Offset: 0x00002B8F
		[EditorBrowsable(EditorBrowsableState.Advanced)]
		internal static CultureInfo Culture
		{
			get
			{
				return Resources.resourceCulture;
			}
			set
			{
				Resources.resourceCulture = value;
			}
		}

		// Token: 0x17000004 RID: 4
		// (get) Token: 0x0600002F RID: 47 RVA: 0x00004997 File Offset: 0x00002B97
		internal static Bitmap Download
		{
			get
			{
				return (Bitmap)Resources.ResourceManager.GetObject("Download", Resources.resourceCulture);
			}
		}

		// Token: 0x17000005 RID: 5
		// (get) Token: 0x06000030 RID: 48 RVA: 0x000049B2 File Offset: 0x00002BB2
		internal static Bitmap Invert
		{
			get
			{
				return (Bitmap)Resources.ResourceManager.GetObject("Invert", Resources.resourceCulture);
			}
		}

		// Token: 0x17000006 RID: 6
		// (get) Token: 0x06000031 RID: 49 RVA: 0x000049CD File Offset: 0x00002BCD
		internal static Bitmap Lock
		{
			get
			{
				return (Bitmap)Resources.ResourceManager.GetObject("Lock", Resources.resourceCulture);
			}
		}

		// Token: 0x17000007 RID: 7
		// (get) Token: 0x06000032 RID: 50 RVA: 0x000049E8 File Offset: 0x00002BE8
		internal static Bitmap Refresh
		{
			get
			{
				return (Bitmap)Resources.ResourceManager.GetObject("Refresh", Resources.resourceCulture);
			}
		}

		// Token: 0x17000008 RID: 8
		// (get) Token: 0x06000033 RID: 51 RVA: 0x00004A03 File Offset: 0x00002C03
		internal static Bitmap Reset
		{
			get
			{
				return (Bitmap)Resources.ResourceManager.GetObject("Reset", Resources.resourceCulture);
			}
		}

		// Token: 0x17000009 RID: 9
		// (get) Token: 0x06000034 RID: 52 RVA: 0x00004A1E File Offset: 0x00002C1E
		internal static Bitmap Sad
		{
			get
			{
				return (Bitmap)Resources.ResourceManager.GetObject("Sad", Resources.resourceCulture);
			}
		}

		// Token: 0x1700000A RID: 10
		// (get) Token: 0x06000035 RID: 53 RVA: 0x00004A39 File Offset: 0x00002C39
		internal static Bitmap Save
		{
			get
			{
				return (Bitmap)Resources.ResourceManager.GetObject("Save", Resources.resourceCulture);
			}
		}

		// Token: 0x1700000B RID: 11
		// (get) Token: 0x06000036 RID: 54 RVA: 0x00004A54 File Offset: 0x00002C54
		internal static Bitmap Unlock
		{
			get
			{
				return (Bitmap)Resources.ResourceManager.GetObject("Unlock", Resources.resourceCulture);
			}
		}

		// Token: 0x04000031 RID: 49
		private static ResourceManager resourceMan;

		// Token: 0x04000032 RID: 50
		private static CultureInfo resourceCulture;
	}
}
