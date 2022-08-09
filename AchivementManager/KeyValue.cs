using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;

namespace SAM.Game
{
	// Token: 0x02000003 RID: 3
	internal class KeyValue
	{
		// Token: 0x17000001 RID: 1
		public KeyValue this[string key]
		{
			get
			{
				if (this.Children == null)
				{
					return KeyValue._Invalid;
				}
				KeyValue keyValue = this.Children.SingleOrDefault((KeyValue c) => string.Compare(c.Name, key, StringComparison.InvariantCultureIgnoreCase) == 0);
				if (keyValue == null)
				{
					return KeyValue._Invalid;
				}
				return keyValue;
			}
		}

		// Token: 0x06000003 RID: 3 RVA: 0x000020AA File Offset: 0x000002AA
		public string AsString(string defaultValue)
		{
			if (!this.Valid)
			{
				return defaultValue;
			}
			if (this.Value == null)
			{
				return defaultValue;
			}
			return this.Value.ToString();
		}

		// Token: 0x06000004 RID: 4 RVA: 0x000020CC File Offset: 0x000002CC
		public int AsInteger(int defaultValue)
		{
			if (!this.Valid)
			{
				return defaultValue;
			}
			switch (this.Type)
			{
			case KeyValueType.String:
			case KeyValueType.WideString:
			{
				int result;
				if (!int.TryParse((string)this.Value, out result))
				{
					return defaultValue;
				}
				return result;
			}
			case KeyValueType.Int32:
				return (int)this.Value;
			case KeyValueType.Float32:
				return (int)((float)this.Value);
			case KeyValueType.UInt64:
				return (int)((ulong)this.Value & ulong.MaxValue);
			}
			return defaultValue;
		}

		// Token: 0x06000005 RID: 5 RVA: 0x00002154 File Offset: 0x00000354
		public float AsFloat(float defaultValue)
		{
			if (!this.Valid)
			{
				return defaultValue;
			}
			switch (this.Type)
			{
			case KeyValueType.String:
			case KeyValueType.WideString:
			{
				float result;
				if (!float.TryParse((string)this.Value, out result))
				{
					return defaultValue;
				}
				return result;
			}
			case KeyValueType.Int32:
				return (float)((int)this.Value);
			case KeyValueType.Float32:
				return (float)this.Value;
			case KeyValueType.UInt64:
				return (ulong)this.Value & ulong.MaxValue;
			}
			return defaultValue;
		}

		// Token: 0x06000006 RID: 6 RVA: 0x000021DC File Offset: 0x000003DC
		public bool AsBoolean(bool defaultValue)
		{
			if (!this.Valid)
			{
				return defaultValue;
			}
			switch (this.Type)
			{
			case KeyValueType.String:
			case KeyValueType.WideString:
			{
				int num;
				if (!int.TryParse((string)this.Value, out num))
				{
					return defaultValue;
				}
				return num != 0;
			}
			case KeyValueType.Int32:
				return (int)this.Value != 0;
			case KeyValueType.Float32:
				return (int)((float)this.Value) != 0;
			case KeyValueType.UInt64:
				return (ulong)this.Value > 0UL;
			}
			return defaultValue;
		}

		// Token: 0x06000007 RID: 7 RVA: 0x0000226C File Offset: 0x0000046C
		public override string ToString()
		{
			if (!this.Valid)
			{
				return "<invalid>";
			}
			if (this.Type == KeyValueType.None)
			{
				return this.Name;
			}
			return string.Format(CultureInfo.CurrentCulture, "{0} = {1}", new object[]
			{
				this.Name,
				this.Value
			});
		}

		// Token: 0x06000008 RID: 8 RVA: 0x000022C0 File Offset: 0x000004C0
		public static KeyValue LoadAsBinary(string path)
		{
			if (!File.Exists(path))
			{
				return null;
			}
			KeyValue result;
			try
			{
				using (FileStream fileStream = File.Open(path, FileMode.Open, FileAccess.Read, FileShare.ReadWrite))
				{
					KeyValue keyValue = new KeyValue();
					if (!keyValue.ReadAsBinary(fileStream))
					{
						result = null;
					}
					else
					{
						result = keyValue;
					}
				}
			}
			catch (Exception)
			{
				result = null;
			}
			return result;
		}

		// Token: 0x06000009 RID: 9 RVA: 0x00002328 File Offset: 0x00000528
		public bool ReadAsBinary(Stream input)
		{
			this.Children = new List<KeyValue>();
			bool result;
			try
			{
				for (;;)
				{
					KeyValueType keyValueType = (KeyValueType)input.ReadValueU8();
					if (keyValueType == KeyValueType.End)
					{
						goto IL_138;
					}
					KeyValue keyValue = new KeyValue
					{
						Type = keyValueType,
						Name = input.ReadStringUnicode()
					};
					switch (keyValueType)
					{
					case KeyValueType.None:
						keyValue.ReadAsBinary(input);
						goto IL_102;
					case KeyValueType.String:
						keyValue.Valid = true;
						keyValue.Value = input.ReadStringUnicode();
						goto IL_102;
					case KeyValueType.Int32:
						keyValue.Valid = true;
						keyValue.Value = input.ReadValueS32();
						goto IL_102;
					case KeyValueType.Float32:
						keyValue.Valid = true;
						keyValue.Value = input.ReadValueF32();
						goto IL_102;
					case KeyValueType.Pointer:
						keyValue.Valid = true;
						keyValue.Value = input.ReadValueU32();
						goto IL_102;
					case KeyValueType.WideString:
						goto IL_127;
					case KeyValueType.Color:
						keyValue.Valid = true;
						keyValue.Value = input.ReadValueU32();
						goto IL_102;
					case KeyValueType.UInt64:
						keyValue.Valid = true;
						keyValue.Value = input.ReadValueU64();
						goto IL_102;
					}
					break;
					IL_102:
					if (input.Position >= input.Length)
					{
						goto IL_132;
					}
					this.Children.Add(keyValue);
				}
				throw new FormatException();
				IL_127:
				throw new FormatException("wstring is unsupported");
				IL_132:
				throw new FormatException();
				IL_138:
				this.Valid = true;
				result = (input.Position == input.Length);
			}
			catch (Exception)
			{
				result = false;
			}
			return result;
		}

		// Token: 0x04000001 RID: 1
		private static readonly KeyValue _Invalid = new KeyValue();

		// Token: 0x04000002 RID: 2
		public string Name = "<root>";

		// Token: 0x04000003 RID: 3
		public KeyValueType Type;

		// Token: 0x04000004 RID: 4
		public object Value;

		// Token: 0x04000005 RID: 5
		public bool Valid;

		// Token: 0x04000006 RID: 6
		public List<KeyValue> Children;
	}
}
