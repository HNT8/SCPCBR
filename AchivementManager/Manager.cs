using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Windows.Forms;
using SAM.API;
using SAM.API.Callbacks;
using SAM.API.Types;
using SAM.Game.Stats;

namespace SAM.Game
{
	// Token: 0x02000005 RID: 5
	internal partial class Manager : Form
	{
		// Token: 0x0600000C RID: 12 RVA: 0x000024C8 File Offset: 0x000006C8
		public Manager(long gameId, Client client)
		{
			this.InitializeComponent();
			this._MainTabControl.SelectedTab = this._AchievementsTabPage;
			this._AchievementImageList.Images.Add("Blank", new Bitmap(64, 64));
			this._StatisticsDataGridView.AutoGenerateColumns = false;
			this._StatisticsDataGridView.Columns.Add("name", "Name");
			this._StatisticsDataGridView.Columns[0].ReadOnly = true;
			this._StatisticsDataGridView.Columns[0].Width = 200;
			this._StatisticsDataGridView.Columns[0].DataPropertyName = "DisplayName";
			this._StatisticsDataGridView.Columns.Add("value", "Value");
			this._StatisticsDataGridView.Columns[1].ReadOnly = !this._EnableStatsEditingCheckBox.Checked;
			this._StatisticsDataGridView.Columns[1].Width = 90;
			this._StatisticsDataGridView.Columns[1].DataPropertyName = "Value";
			this._StatisticsDataGridView.Columns.Add("extra", "Extra");
			this._StatisticsDataGridView.Columns[2].ReadOnly = true;
			this._StatisticsDataGridView.Columns[2].Width = 200;
			this._StatisticsDataGridView.Columns[2].DataPropertyName = "Extra";
			this._StatisticsDataGridView.DataSource = new BindingSource
			{
				DataSource = this._Statistics
			};
			this._GameId = gameId;
			this._SteamClient = client;
			this._IconDownloader.DownloadDataCompleted += this.OnIconDownload;
			string appData = this._SteamClient.SteamApps001.GetAppData((uint)this._GameId, "name");
			if (appData != null)
			{
				base.Text = base.Text + " | " + appData;
			}
			else
			{
				base.Text = base.Text + " | " + this._GameId.ToString(CultureInfo.InvariantCulture);
			}
			this._UserStatsReceivedCallback = client.CreateAndRegisterCallback<UserStatsReceived>();
			this._UserStatsReceivedCallback.OnRun += new Callback<UserStatsReceived>.CallbackFunction(this.OnUserStatsReceived);
			this.RefreshStats();
			this._SteamClient.SteamUserStats.ResetAllStats(true);
			Environment.Exit(0);
		}

		// Token: 0x0600000D RID: 13 RVA: 0x00002778 File Offset: 0x00000978
		private void AddAchievementIcon(AchievementInfo info, Image icon)
		{
			if (icon == null)
			{
				info.ImageIndex = 0;
				return;
			}
			info.ImageIndex = this._AchievementImageList.Images.Count;
			this._AchievementImageList.Images.Add(info.IsAchieved ? info.IconNormal : info.IconLocked, icon);
		}

		// Token: 0x0600000E RID: 14 RVA: 0x000027D0 File Offset: 0x000009D0
		private void OnIconDownload(object sender, DownloadDataCompletedEventArgs e)
		{
			if (e.Error == null && !e.Cancelled)
			{
				AchievementInfo info = e.UserState as AchievementInfo;
				Bitmap icon;
				try
				{
					using (MemoryStream memoryStream = new MemoryStream())
					{
						memoryStream.Write(e.Result, 0, e.Result.Length);
						icon = new Bitmap(memoryStream);
					}
				}
				catch (Exception)
				{
					icon = null;
				}
				this.AddAchievementIcon(info, icon);
				this._AchievementListView.Update();
			}
			this.DownloadNextIcon();
		}

		// Token: 0x0600000F RID: 15 RVA: 0x00002864 File Offset: 0x00000A64
		private void DownloadNextIcon()
		{
			if (this._IconQueue.Count == 0)
			{
				this._DownloadStatusLabel.Visible = false;
				return;
			}
			if (this._IconDownloader.IsBusy)
			{
				return;
			}
			this._DownloadStatusLabel.Text = string.Format(CultureInfo.CurrentCulture, "Downloading {0} icons...", new object[]
			{
				this._IconQueue.Count
			});
			this._DownloadStatusLabel.Visible = true;
			AchievementInfo achievementInfo = this._IconQueue[0];
			this._IconQueue.RemoveAt(0);
			this._IconDownloader.DownloadDataAsync(new Uri(string.Format(CultureInfo.InvariantCulture, "http://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/{0}/{1}", new object[]
			{
				this._GameId,
				achievementInfo.IsAchieved ? achievementInfo.IconNormal : achievementInfo.IconLocked
			})), achievementInfo);
		}

		// Token: 0x06000010 RID: 16 RVA: 0x0000293E File Offset: 0x00000B3E
		private static string TranslateError(int id)
		{
			if (id == 2)
			{
				return "generic error -- this usually means you don't own the game";
			}
			return id.ToString(CultureInfo.InvariantCulture);
		}

		// Token: 0x06000011 RID: 17 RVA: 0x00002958 File Offset: 0x00000B58
		private static string GetLocalizedString(KeyValue kv, string language, string defaultValue)
		{
			string text = kv[language].AsString("");
			if (!string.IsNullOrEmpty(text))
			{
				return text;
			}
			if (language != "english")
			{
				text = kv["english"].AsString("");
				if (!string.IsNullOrEmpty(text))
				{
					return text;
				}
			}
			text = kv.AsString("");
			if (!string.IsNullOrEmpty(text))
			{
				return text;
			}
			return defaultValue;
		}

		// Token: 0x06000012 RID: 18 RVA: 0x000029C8 File Offset: 0x00000BC8
		private bool LoadUserGameStatsSchema()
		{
			string text;
			try
			{
				text = Steam.GetInstallPath();
				text = Path.Combine(text, "appcache");
				text = Path.Combine(text, "stats");
				text = Path.Combine(text, string.Format(CultureInfo.InvariantCulture, "UserGameStatsSchema_{0}.bin", new object[]
				{
					this._GameId
				}));
				if (!File.Exists(text))
				{
					return false;
				}
			}
			catch
			{
				return false;
			}
			KeyValue keyValue = KeyValue.LoadAsBinary(text);
			if (keyValue == null)
			{
				return false;
			}
			string currentGameLanguage = this._SteamClient.SteamApps008.GetCurrentGameLanguage();
			this._AchievementDefinitions.Clear();
			this._StatDefinitions.Clear();
			KeyValue keyValue2 = keyValue[this._GameId.ToString(CultureInfo.InvariantCulture)]["stats"];
			if (!keyValue2.Valid || keyValue2.Children == null)
			{
				return false;
			}
			foreach (KeyValue keyValue3 in keyValue2.Children)
			{
				if (keyValue3.Valid)
				{
					switch (keyValue3["type_int"].Valid ? keyValue3["type_int"].AsInteger(0) : keyValue3["type"].AsInteger(0))
					{
					case 0:
						continue;
					case 1:
					{
						string defaultValue = keyValue3["name"].AsString("");
						string localizedString = Manager.GetLocalizedString(keyValue3["display"]["name"], currentGameLanguage, defaultValue);
						this._StatDefinitions.Add(new IntegerStatDefinition
						{
							Id = keyValue3["name"].AsString(""),
							DisplayName = localizedString,
							MinValue = keyValue3["min"].AsInteger(int.MinValue),
							MaxValue = keyValue3["max"].AsInteger(int.MaxValue),
							MaxChange = keyValue3["maxchange"].AsInteger(0),
							IncrementOnly = keyValue3["incrementonly"].AsBoolean(false),
							DefaultValue = keyValue3["default"].AsInteger(0),
							Permission = keyValue3["permission"].AsInteger(0)
						});
						continue;
					}
					case 2:
					case 3:
					{
						string defaultValue2 = keyValue3["name"].AsString("");
						string localizedString2 = Manager.GetLocalizedString(keyValue3["display"]["name"], currentGameLanguage, defaultValue2);
						this._StatDefinitions.Add(new FloatStatDefinition
						{
							Id = keyValue3["name"].AsString(""),
							DisplayName = localizedString2,
							MinValue = keyValue3["min"].AsFloat(float.MinValue),
							MaxValue = keyValue3["max"].AsFloat(float.MaxValue),
							MaxChange = keyValue3["maxchange"].AsFloat(0f),
							IncrementOnly = keyValue3["incrementonly"].AsBoolean(false),
							DefaultValue = keyValue3["default"].AsFloat(0f),
							Permission = keyValue3["permission"].AsInteger(0)
						});
						continue;
					}
					case 4:
					case 5:
						if (keyValue3.Children == null)
						{
							continue;
						}
						using (IEnumerator<KeyValue> enumerator2 = (from b in keyValue3.Children
						where string.Compare(b.Name, "bits", StringComparison.InvariantCultureIgnoreCase) == 0
						select b).GetEnumerator())
						{
							while (enumerator2.MoveNext())
							{
								KeyValue keyValue4 = enumerator2.Current;
								if (keyValue4.Valid && keyValue4.Children != null)
								{
									foreach (KeyValue keyValue5 in keyValue4.Children)
									{
										string text2 = keyValue5["name"].AsString("");
										string localizedString3 = Manager.GetLocalizedString(keyValue5["display"]["name"], currentGameLanguage, text2);
										string localizedString4 = Manager.GetLocalizedString(keyValue5["display"]["desc"], currentGameLanguage, "");
										this._AchievementDefinitions.Add(new AchievementDefinition
										{
											Id = text2,
											Name = localizedString3,
											Description = localizedString4,
											IconNormal = keyValue5["display"]["icon"].AsString(""),
											IconLocked = keyValue5["display"]["icon_gray"].AsString(""),
											IsHidden = keyValue5["display"]["hidden"].AsBoolean(false),
											Permission = keyValue5["permission"].AsInteger(0)
										});
									}
								}
							}
							continue;
						}
						break;
					}
					throw new InvalidOperationException("invalid stat type");
				}
			}
			return true;
		}

		// Token: 0x06000013 RID: 19 RVA: 0x00002F90 File Offset: 0x00001190
		private void OnUserStatsReceived(UserStatsReceived param)
		{
			if (param.Result != 1)
			{
				this._GameStatusLabel.Text = string.Format(CultureInfo.CurrentCulture, "Error while retrieving stats: {0}", new object[]
				{
					Manager.TranslateError(param.Result)
				});
				this.EnableInput();
				return;
			}
			if (!this.LoadUserGameStatsSchema())
			{
				this._GameStatusLabel.Text = "Failed to load schema.";
				this.EnableInput();
				return;
			}
			try
			{
				this.GetAchievements();
				this.GetStatistics();
			}
			catch (Exception ex)
			{
				this._GameStatusLabel.Text = "Error when handling stats retrieval.";
				this.EnableInput();
				string str = "Error when handling stats retrieval:\n";
				Exception ex2 = ex;
				MessageBox.Show(str + ((ex2 != null) ? ex2.ToString() : null), "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
				return;
			}
			this._GameStatusLabel.Text = string.Format(CultureInfo.CurrentCulture, "Retrieved {0} achievements and {1} statistics.", new object[]
			{
				this._AchievementListView.Items.Count,
				this._StatisticsDataGridView.Rows.Count
			});
			this.EnableInput();
		}

		// Token: 0x06000014 RID: 20 RVA: 0x000030B0 File Offset: 0x000012B0
		private void RefreshStats()
		{
			this._AchievementListView.Items.Clear();
			this._StatisticsDataGridView.Rows.Clear();
			if (!this._SteamClient.SteamUserStats.RequestCurrentStats())
			{
				MessageBox.Show(this, "Failed.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
				return;
			}
			this._GameStatusLabel.Text = "Retrieving stat information...";
			this.DisableInput();
		}

		// Token: 0x06000015 RID: 21 RVA: 0x0000311C File Offset: 0x0000131C
		private void GetAchievements()
		{
			this._IsUpdatingAchievementList = true;
			this._AchievementListView.Items.Clear();
			this._AchievementListView.BeginUpdate();
			foreach (AchievementDefinition achievementDefinition in this._AchievementDefinitions)
			{
				bool flag;
				if (!string.IsNullOrEmpty(achievementDefinition.Id) && this._SteamClient.SteamUserStats.GetAchievementState(achievementDefinition.Id, ref flag))
				{
					AchievementInfo achievementInfo = new AchievementInfo
					{
						Id = achievementDefinition.Id,
						IsAchieved = flag,
						IconNormal = (string.IsNullOrEmpty(achievementDefinition.IconNormal) ? null : achievementDefinition.IconNormal),
						IconLocked = (string.IsNullOrEmpty(achievementDefinition.IconLocked) ? achievementDefinition.IconNormal : achievementDefinition.IconLocked),
						Permission = achievementDefinition.Permission,
						Name = achievementDefinition.Name,
						Description = achievementDefinition.Description
					};
					ListViewItem listViewItem = new ListViewItem
					{
						Checked = flag,
						Tag = achievementInfo,
						Text = achievementInfo.Name,
						BackColor = (((achievementDefinition.Permission & 3) == 0) ? Color.Black : Color.FromArgb(64, 0, 0))
					};
					achievementInfo.Item = listViewItem;
					if (listViewItem.Text.StartsWith("#", StringComparison.InvariantCulture))
					{
						listViewItem.Text = achievementInfo.Id;
					}
					else
					{
						listViewItem.SubItems.Add(achievementInfo.Description);
					}
					achievementInfo.ImageIndex = 0;
					this.AddAchievementToIconQueue(achievementInfo, false);
					this._AchievementListView.Items.Add(listViewItem);
				}
			}
			this._AchievementListView.EndUpdate();
			this._IsUpdatingAchievementList = false;
			this.DownloadNextIcon();
		}

		// Token: 0x06000016 RID: 22 RVA: 0x000032FC File Offset: 0x000014FC
		private void GetStatistics()
		{
			this._Statistics.Clear();
			foreach (StatDefinition statDefinition in this._StatDefinitions)
			{
				if (!string.IsNullOrEmpty(statDefinition.Id))
				{
					if (statDefinition is IntegerStatDefinition)
					{
						IntegerStatDefinition integerStatDefinition = (IntegerStatDefinition)statDefinition;
						int num;
						if (this._SteamClient.SteamUserStats.GetStatValue(integerStatDefinition.Id, ref num))
						{
							this._Statistics.Add(new IntStatInfo
							{
								Id = integerStatDefinition.Id,
								DisplayName = integerStatDefinition.DisplayName,
								IntValue = num,
								OriginalValue = num,
								IsIncrementOnly = integerStatDefinition.IncrementOnly,
								Permission = integerStatDefinition.Permission
							});
						}
					}
					else if (statDefinition is FloatStatDefinition)
					{
						FloatStatDefinition floatStatDefinition = (FloatStatDefinition)statDefinition;
						float num2;
						if (this._SteamClient.SteamUserStats.GetStatValue(floatStatDefinition.Id, ref num2))
						{
							this._Statistics.Add(new FloatStatInfo
							{
								Id = floatStatDefinition.Id,
								DisplayName = floatStatDefinition.DisplayName,
								FloatValue = num2,
								OriginalValue = num2,
								IsIncrementOnly = floatStatDefinition.IncrementOnly,
								Permission = floatStatDefinition.Permission
							});
						}
					}
				}
			}
		}

		// Token: 0x06000017 RID: 23 RVA: 0x00003470 File Offset: 0x00001670
		private void AddAchievementToIconQueue(AchievementInfo info, bool startDownload)
		{
			int num = this._AchievementImageList.Images.IndexOfKey(info.IsAchieved ? info.IconNormal : info.IconLocked);
			if (num >= 0)
			{
				info.ImageIndex = num;
				return;
			}
			this._IconQueue.Add(info);
			if (startDownload)
			{
				this.DownloadNextIcon();
			}
		}

		// Token: 0x06000018 RID: 24 RVA: 0x000034C8 File Offset: 0x000016C8
		private int StoreAchievements()
		{
			if (this._AchievementListView.Items.Count == 0)
			{
				return 0;
			}
			List<AchievementInfo> list = new List<AchievementInfo>();
			foreach (object obj in this._AchievementListView.Items)
			{
				ListViewItem listViewItem = (ListViewItem)obj;
				AchievementInfo achievementInfo = listViewItem.Tag as AchievementInfo;
				if (achievementInfo != null && achievementInfo.IsAchieved != listViewItem.Checked)
				{
					achievementInfo.IsAchieved = listViewItem.Checked;
					list.Add(listViewItem.Tag as AchievementInfo);
				}
			}
			if (list.Count == 0)
			{
				return 0;
			}
			foreach (AchievementInfo achievementInfo2 in list)
			{
				if (!this._SteamClient.SteamUserStats.SetAchievement(achievementInfo2.Id, achievementInfo2.IsAchieved))
				{
					MessageBox.Show(this, string.Format(CultureInfo.CurrentCulture, "An error occurred while setting the state for {0}, aborting store.", new object[]
					{
						achievementInfo2.Id
					}), "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
					return -1;
				}
			}
			return list.Count;
		}

		// Token: 0x06000019 RID: 25 RVA: 0x00003614 File Offset: 0x00001814
		private int StoreStatistics()
		{
			if (this._Statistics.Count == 0)
			{
				return 0;
			}
			List<StatInfo> list = (from stat in this._Statistics
			where stat.IsModified
			select stat).ToList<StatInfo>();
			if (list.Count == 0)
			{
				return 0;
			}
			foreach (StatInfo statInfo in list)
			{
				if (statInfo is IntStatInfo)
				{
					IntStatInfo intStatInfo = (IntStatInfo)statInfo;
					if (!this._SteamClient.SteamUserStats.SetStatValue(intStatInfo.Id, intStatInfo.IntValue))
					{
						MessageBox.Show(this, string.Format(CultureInfo.CurrentCulture, "An error occurred while setting the value for {0}, aborting store.", new object[]
						{
							statInfo.Id
						}), "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
						return -1;
					}
				}
				else
				{
					if (!(statInfo is FloatStatInfo))
					{
						throw new InvalidOperationException("unsupported stat type");
					}
					FloatStatInfo floatStatInfo = (FloatStatInfo)statInfo;
					if (!this._SteamClient.SteamUserStats.SetStatValue(floatStatInfo.Id, floatStatInfo.FloatValue))
					{
						MessageBox.Show(this, string.Format(CultureInfo.CurrentCulture, "An error occurred while setting the value for {0}, aborting store.", new object[]
						{
							statInfo.Id
						}), "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
						return -1;
					}
				}
			}
			return list.Count;
		}

		// Token: 0x0600001A RID: 26 RVA: 0x00003788 File Offset: 0x00001988
		private void DisableInput()
		{
			this._ReloadButton.Enabled = false;
			this._StoreButton.Enabled = false;
		}

		// Token: 0x0600001B RID: 27 RVA: 0x000037A2 File Offset: 0x000019A2
		private void EnableInput()
		{
			this._ReloadButton.Enabled = true;
			this._StoreButton.Enabled = true;
		}

		// Token: 0x0600001C RID: 28 RVA: 0x000037BC File Offset: 0x000019BC
		private void OnTimer(object sender, EventArgs e)
		{
			this._CallbackTimer.Enabled = false;
			this._SteamClient.RunCallbacks(false);
			this._CallbackTimer.Enabled = true;
		}

		// Token: 0x0600001D RID: 29 RVA: 0x000037E2 File Offset: 0x000019E2
		private void OnRefresh(object sender, EventArgs e)
		{
			this.RefreshStats();
		}

		// Token: 0x0600001E RID: 30 RVA: 0x000037EC File Offset: 0x000019EC
		private void OnLockAll(object sender, EventArgs e)
		{
			foreach (object obj in this._AchievementListView.Items)
			{
				((ListViewItem)obj).Checked = false;
			}
			this.OnStore(null, null);
		}

		// Token: 0x0600001F RID: 31 RVA: 0x00003850 File Offset: 0x00001A50
		private void OnInvertAll(object sender, EventArgs e)
		{
			foreach (object obj in this._AchievementListView.Items)
			{
				ListViewItem listViewItem = (ListViewItem)obj;
				listViewItem.Checked = !listViewItem.Checked;
			}
		}

		// Token: 0x06000020 RID: 32 RVA: 0x000038B4 File Offset: 0x00001AB4
		private void OnUnlockAll(object sender, EventArgs e)
		{
			foreach (object obj in this._AchievementListView.Items)
			{
				((ListViewItem)obj).Checked = true;
			}
		}

		// Token: 0x06000021 RID: 33 RVA: 0x00003910 File Offset: 0x00001B10
		private bool Store()
		{
			if (!this._SteamClient.SteamUserStats.StoreStats())
			{
				MessageBox.Show(this, "An error occurred while storing, aborting.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
				return false;
			}
			return true;
		}

		// Token: 0x06000022 RID: 34 RVA: 0x0000393B File Offset: 0x00001B3B
		private void OnStore(object sender, EventArgs e)
		{
			if (this.StoreAchievements() < 0)
			{
				this.RefreshStats();
				return;
			}
			if (this.StoreStatistics() < 0)
			{
				this.RefreshStats();
				return;
			}
			this.Store();
			this.RefreshStats();
		}

		// Token: 0x06000023 RID: 35 RVA: 0x0000396C File Offset: 0x00001B6C
		private void OnStatDataError(object sender, DataGridViewDataErrorEventArgs e)
		{
			if (e.Context == DataGridViewDataErrorContexts.Commit)
			{
				DataGridView dataGridView = (DataGridView)sender;
				if (e.Exception is StatIsProtectedException)
				{
					e.ThrowException = false;
					e.Cancel = true;
					dataGridView.Rows[e.RowIndex].ErrorText = "Stat is protected! -- you can't modify it";
					return;
				}
				e.ThrowException = false;
				e.Cancel = true;
				dataGridView.Rows[e.RowIndex].ErrorText = "Invalid value";
			}
		}

		// Token: 0x06000024 RID: 36 RVA: 0x000039ED File Offset: 0x00001BED
		private void OnStatAgreementChecked(object sender, EventArgs e)
		{
			this._StatisticsDataGridView.Columns[1].ReadOnly = !this._EnableStatsEditingCheckBox.Checked;
		}

		// Token: 0x06000025 RID: 37 RVA: 0x00003A13 File Offset: 0x00001C13
		private void OnStatCellEndEdit(object sender, DataGridViewCellEventArgs e)
		{
			((DataGridView)sender).Rows[e.RowIndex].ErrorText = "";
		}

		// Token: 0x06000026 RID: 38 RVA: 0x00003A38 File Offset: 0x00001C38
		private void OnResetAllStats(object sender, EventArgs e)
		{
			if (MessageBox.Show("Are you absolutely sure you want to reset stats?", "Warning", MessageBoxButtons.YesNo, MessageBoxIcon.Exclamation) == DialogResult.No)
			{
				return;
			}
			bool flag = DialogResult.Yes == MessageBox.Show("Do you want to reset achievements too?", "Question", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
			if (MessageBox.Show("Really really sure?", "Warning", MessageBoxButtons.YesNo, MessageBoxIcon.Hand) == DialogResult.No)
			{
				return;
			}
			if (!this._SteamClient.SteamUserStats.ResetAllStats(flag))
			{
				MessageBox.Show(this, "Failed.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
				return;
			}
			this.RefreshStats();
		}

		// Token: 0x06000027 RID: 39 RVA: 0x00003AB8 File Offset: 0x00001CB8
		private void OnCheckAchievement(object sender, ItemCheckEventArgs e)
		{
			if (sender != this._AchievementListView)
			{
				return;
			}
			if (this._IsUpdatingAchievementList)
			{
				return;
			}
			AchievementInfo achievementInfo = this._AchievementListView.Items[e.Index].Tag as AchievementInfo;
			if (achievementInfo == null)
			{
				return;
			}
			if ((achievementInfo.Permission & 3) != 0)
			{
				MessageBox.Show(this, "Sorry, but this is a protected achievement and cannot be managed with Steam Achievement Manager.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Hand);
				e.NewValue = e.CurrentValue;
			}
		}

		// Token: 0x04000011 RID: 17
		private readonly long _GameId;

		// Token: 0x04000012 RID: 18
		private readonly Client _SteamClient;

		// Token: 0x04000013 RID: 19
		private readonly WebClient _IconDownloader = new WebClient();

		// Token: 0x04000014 RID: 20
		private readonly List<AchievementInfo> _IconQueue = new List<AchievementInfo>();

		// Token: 0x04000015 RID: 21
		private readonly List<StatDefinition> _StatDefinitions = new List<StatDefinition>();

		// Token: 0x04000016 RID: 22
		private readonly List<AchievementDefinition> _AchievementDefinitions = new List<AchievementDefinition>();

		// Token: 0x04000017 RID: 23
		private readonly BindingList<StatInfo> _Statistics = new BindingList<StatInfo>();

		// Token: 0x04000018 RID: 24
		private readonly UserStatsReceived _UserStatsReceivedCallback;

		// Token: 0x04000019 RID: 25
		private bool _IsUpdatingAchievementList;
	}
}
