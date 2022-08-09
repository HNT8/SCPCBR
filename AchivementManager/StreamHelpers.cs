using System;
using System.Globalization;
using System.IO;
using System.Text;

namespace SAM.Game
{
	// Token: 0x02000008 RID: 8
	internal static class StreamHelpers
	{
		// Token: 0x06000037 RID: 55 RVA: 0x00004A6F File Offset: 0x00002C6F
		public static byte ReadValueU8(this Stream stream)
		{
			return (byte)stream.ReadByte();
		}

		// Token: 0x06000038 RID: 56 RVA: 0x00004A78 File Offset: 0x00002C78
		public static int ReadValueS32(this Stream stream)
		{
			byte[] array = new byte[4];
			stream.Read(array, 0, 4);
			return BitConverter.ToInt32(array, 0);
		}

		// Token: 0x06000039 RID: 57 RVA: 0x00004AA0 File Offset: 0x00002CA0
		public static uint ReadValueU32(this Stream stream)
		{
			byte[] array = new byte[4];
			stream.Read(array, 0, 4);
			return BitConverter.ToUInt32(array, 0);
		}

		// Token: 0x0600003A RID: 58 RVA: 0x00004AC8 File Offset: 0x00002CC8
		public static ulong ReadValueU64(this Stream stream)
		{
			byte[] array = new byte[8];
			stream.Read(array, 0, 8);
			return BitConverter.ToUInt64(array, 0);
		}

		// Token: 0x0600003B RID: 59 RVA: 0x00004AF0 File Offset: 0x00002CF0
		public static float ReadValueF32(this Stream stream)
		{
			byte[] array = new byte[4];
			stream.Read(array, 0, 4);
			return BitConverter.ToSingle(array, 0);
		}

		// Token: 0x0600003C RID: 60 RVA: 0x00004B18 File Offset: 0x00002D18
		internal static string ReadStringInternalDynamic(this Stream stream, Encoding encoding, char end)
		{
			int byteCount = encoding.GetByteCount("e");
			string b = end.ToString(CultureInfo.InvariantCulture);
			int num = 0;
			byte[] array = new byte[128 * byteCount];
			for (;;)
			{
				if (num + byteCount > array.Length)
				{
					Array.Resize<byte>(ref array, array.Length + 128 * byteCount);
				}
				stream.Read(array, num, byteCount);
				if (encoding.GetString(array, num, byteCount) == b)
				{
					break;
				}
				num += byteCount;
			}
			if (num == 0)
			{
				return "";
			}
			return encoding.GetString(array, 0, num);
		}

		// Token: 0x0600003D RID: 61 RVA: 0x00004B9A File Offset: 0x00002D9A
		public static string ReadStringAscii(this Stream stream)
		{
			return stream.ReadStringInternalDynamic(Encoding.ASCII, '\0');
		}

		// Token: 0x0600003E RID: 62 RVA: 0x00004BA8 File Offset: 0x00002DA8
		public static string ReadStringUnicode(this Stream stream)
		{
			return stream.ReadStringInternalDynamic(Encoding.UTF8, '\0');
		}
	}
}
