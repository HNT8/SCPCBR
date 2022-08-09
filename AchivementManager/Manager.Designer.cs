namespace SAM.Game
{
	// Token: 0x02000005 RID: 5
	internal partial class Manager : global::System.Windows.Forms.Form
	{
		// Token: 0x06000028 RID: 40 RVA: 0x00003B27 File Offset: 0x00001D27
		protected override void Dispose(bool disposing)
		{
			if (disposing && this.components != null)
			{
				this.components.Dispose();
			}
			base.Dispose(disposing);
		}

		// Token: 0x06000029 RID: 41 RVA: 0x00003B48 File Offset: 0x00001D48
		private void InitializeComponent()
		{
			this.components = new global::System.ComponentModel.Container();
			global::System.ComponentModel.ComponentResourceManager componentResourceManager = new global::System.ComponentModel.ComponentResourceManager(typeof(global::SAM.Game.Manager));
			this._MainToolStrip = new global::System.Windows.Forms.ToolStrip();
			this._StoreButton = new global::System.Windows.Forms.ToolStripButton();
			this._ReloadButton = new global::System.Windows.Forms.ToolStripButton();
			this._ResetButton = new global::System.Windows.Forms.ToolStripButton();
			this._AchievementImageList = new global::System.Windows.Forms.ImageList(this.components);
			this._MainStatusStrip = new global::System.Windows.Forms.StatusStrip();
			this._CountryStatusLabel = new global::System.Windows.Forms.ToolStripStatusLabel();
			this._GameStatusLabel = new global::System.Windows.Forms.ToolStripStatusLabel();
			this._DownloadStatusLabel = new global::System.Windows.Forms.ToolStripStatusLabel();
			this._CallbackTimer = new global::System.Windows.Forms.Timer(this.components);
			this._MainTabControl = new global::System.Windows.Forms.TabControl();
			this._AchievementsTabPage = new global::System.Windows.Forms.TabPage();
			this._AchievementListView = new global::SAM.Game.DoubleBufferedListView();
			this._AchievementNameColumnHeader = new global::System.Windows.Forms.ColumnHeader();
			this._AchievementDescriptionColumnHeader = new global::System.Windows.Forms.ColumnHeader();
			this._AchievementsToolStrip = new global::System.Windows.Forms.ToolStrip();
			this._LockAllButton = new global::System.Windows.Forms.ToolStripButton();
			this._InvertAllButton = new global::System.Windows.Forms.ToolStripButton();
			this._UnlockAllButton = new global::System.Windows.Forms.ToolStripButton();
			this._StatisticsTabPage = new global::System.Windows.Forms.TabPage();
			this._EnableStatsEditingCheckBox = new global::System.Windows.Forms.CheckBox();
			this._StatisticsDataGridView = new global::System.Windows.Forms.DataGridView();
			global::System.Windows.Forms.ToolStripSeparator toolStripSeparator = new global::System.Windows.Forms.ToolStripSeparator();
			this._MainToolStrip.SuspendLayout();
			this._MainStatusStrip.SuspendLayout();
			this._MainTabControl.SuspendLayout();
			this._AchievementsTabPage.SuspendLayout();
			this._AchievementsToolStrip.SuspendLayout();
			this._StatisticsTabPage.SuspendLayout();
			((global::System.ComponentModel.ISupportInitialize)this._StatisticsDataGridView).BeginInit();
			base.SuspendLayout();
			toolStripSeparator.Name = "_ToolStripSeparator1";
			toolStripSeparator.Size = new global::System.Drawing.Size(6, 25);
			this._MainToolStrip.Items.AddRange(new global::System.Windows.Forms.ToolStripItem[]
			{
				this._StoreButton,
				this._ReloadButton,
				toolStripSeparator,
				this._ResetButton
			});
			this._MainToolStrip.Location = new global::System.Drawing.Point(0, 0);
			this._MainToolStrip.Name = "_MainToolStrip";
			this._MainToolStrip.Size = new global::System.Drawing.Size(632, 25);
			this._MainToolStrip.TabIndex = 1;
			this._StoreButton.Alignment = global::System.Windows.Forms.ToolStripItemAlignment.Right;
			this._StoreButton.Enabled = false;
			this._StoreButton.Image = global::SAM.Game.Resources.Save;
			this._StoreButton.ImageTransparentColor = global::System.Drawing.Color.Magenta;
			this._StoreButton.Name = "_StoreButton";
			this._StoreButton.Size = new global::System.Drawing.Size(120, 22);
			this._StoreButton.Text = "Commit Changes";
			this._StoreButton.ToolTipText = "Store achievements and statistics for active game.";
			this._StoreButton.Click += new global::System.EventHandler(this.OnStore);
			this._ReloadButton.Enabled = false;
			this._ReloadButton.Image = global::SAM.Game.Resources.Refresh;
			this._ReloadButton.ImageTransparentColor = global::System.Drawing.Color.Magenta;
			this._ReloadButton.Name = "_ReloadButton";
			this._ReloadButton.Size = new global::System.Drawing.Size(66, 22);
			this._ReloadButton.Text = "Refresh";
			this._ReloadButton.ToolTipText = "Refresh achievements and statistics for active game.";
			this._ReloadButton.Click += new global::System.EventHandler(this.OnRefresh);
			this._ResetButton.Image = global::SAM.Game.Resources.Reset;
			this._ResetButton.ImageTransparentColor = global::System.Drawing.Color.Magenta;
			this._ResetButton.Name = "_ResetButton";
			this._ResetButton.Size = new global::System.Drawing.Size(55, 22);
			this._ResetButton.Text = "Reset";
			this._ResetButton.ToolTipText = "Reset achievements and/or statistics for active game.";
			this._ResetButton.Click += new global::System.EventHandler(this.OnResetAllStats);
			this._AchievementImageList.ColorDepth = global::System.Windows.Forms.ColorDepth.Depth8Bit;
			this._AchievementImageList.ImageSize = new global::System.Drawing.Size(64, 64);
			this._AchievementImageList.TransparentColor = global::System.Drawing.Color.Transparent;
			this._MainStatusStrip.Items.AddRange(new global::System.Windows.Forms.ToolStripItem[]
			{
				this._CountryStatusLabel,
				this._GameStatusLabel,
				this._DownloadStatusLabel
			});
			this._MainStatusStrip.Location = new global::System.Drawing.Point(0, 370);
			this._MainStatusStrip.Name = "_MainStatusStrip";
			this._MainStatusStrip.Size = new global::System.Drawing.Size(632, 22);
			this._MainStatusStrip.TabIndex = 4;
			this._MainStatusStrip.Text = "statusStrip1";
			this._CountryStatusLabel.Name = "_CountryStatusLabel";
			this._CountryStatusLabel.Size = new global::System.Drawing.Size(0, 17);
			this._GameStatusLabel.Name = "_GameStatusLabel";
			this._GameStatusLabel.Size = new global::System.Drawing.Size(617, 17);
			this._GameStatusLabel.Spring = true;
			this._GameStatusLabel.TextAlign = global::System.Drawing.ContentAlignment.MiddleLeft;
			this._DownloadStatusLabel.Image = global::SAM.Game.Resources.Download;
			this._DownloadStatusLabel.Name = "_DownloadStatusLabel";
			this._DownloadStatusLabel.Size = new global::System.Drawing.Size(111, 17);
			this._DownloadStatusLabel.Text = "Download status";
			this._DownloadStatusLabel.Visible = false;
			this._CallbackTimer.Enabled = true;
			this._CallbackTimer.Tick += new global::System.EventHandler(this.OnTimer);
			this._MainTabControl.Anchor = (global::System.Windows.Forms.AnchorStyles.Top | global::System.Windows.Forms.AnchorStyles.Bottom | global::System.Windows.Forms.AnchorStyles.Left | global::System.Windows.Forms.AnchorStyles.Right);
			this._MainTabControl.Controls.Add(this._AchievementsTabPage);
			this._MainTabControl.Controls.Add(this._StatisticsTabPage);
			this._MainTabControl.Location = new global::System.Drawing.Point(8, 33);
			this._MainTabControl.Name = "_MainTabControl";
			this._MainTabControl.SelectedIndex = 0;
			this._MainTabControl.Size = new global::System.Drawing.Size(616, 334);
			this._MainTabControl.TabIndex = 5;
			this._AchievementsTabPage.Controls.Add(this._AchievementListView);
			this._AchievementsTabPage.Controls.Add(this._AchievementsToolStrip);
			this._AchievementsTabPage.Location = new global::System.Drawing.Point(4, 22);
			this._AchievementsTabPage.Name = "_AchievementsTabPage";
			this._AchievementsTabPage.Padding = new global::System.Windows.Forms.Padding(3);
			this._AchievementsTabPage.Size = new global::System.Drawing.Size(608, 308);
			this._AchievementsTabPage.TabIndex = 0;
			this._AchievementsTabPage.Text = "Achievements";
			this._AchievementsTabPage.UseVisualStyleBackColor = true;
			this._AchievementListView.Activation = global::System.Windows.Forms.ItemActivation.OneClick;
			this._AchievementListView.BackColor = global::System.Drawing.Color.Black;
			this._AchievementListView.BackgroundImageTiled = true;
			this._AchievementListView.CheckBoxes = true;
			this._AchievementListView.Columns.AddRange(new global::System.Windows.Forms.ColumnHeader[]
			{
				this._AchievementNameColumnHeader,
				this._AchievementDescriptionColumnHeader
			});
			this._AchievementListView.Dock = global::System.Windows.Forms.DockStyle.Fill;
			this._AchievementListView.ForeColor = global::System.Drawing.Color.White;
			this._AchievementListView.FullRowSelect = true;
			this._AchievementListView.GridLines = true;
			this._AchievementListView.HideSelection = false;
			this._AchievementListView.LargeImageList = this._AchievementImageList;
			this._AchievementListView.Location = new global::System.Drawing.Point(3, 28);
			this._AchievementListView.Name = "_AchievementListView";
			this._AchievementListView.Size = new global::System.Drawing.Size(602, 277);
			this._AchievementListView.SmallImageList = this._AchievementImageList;
			this._AchievementListView.Sorting = global::System.Windows.Forms.SortOrder.Ascending;
			this._AchievementListView.TabIndex = 4;
			this._AchievementListView.UseCompatibleStateImageBehavior = false;
			this._AchievementListView.View = global::System.Windows.Forms.View.Details;
			this._AchievementListView.ItemCheck += new global::System.Windows.Forms.ItemCheckEventHandler(this.OnCheckAchievement);
			this._AchievementNameColumnHeader.Text = "Name";
			this._AchievementNameColumnHeader.Width = 200;
			this._AchievementDescriptionColumnHeader.Text = "Description";
			this._AchievementDescriptionColumnHeader.Width = 380;
			this._AchievementsToolStrip.Items.AddRange(new global::System.Windows.Forms.ToolStripItem[]
			{
				this._LockAllButton,
				this._InvertAllButton,
				this._UnlockAllButton
			});
			this._AchievementsToolStrip.Location = new global::System.Drawing.Point(3, 3);
			this._AchievementsToolStrip.Name = "_AchievementsToolStrip";
			this._AchievementsToolStrip.Size = new global::System.Drawing.Size(602, 25);
			this._AchievementsToolStrip.TabIndex = 5;
			this._LockAllButton.DisplayStyle = global::System.Windows.Forms.ToolStripItemDisplayStyle.Image;
			this._LockAllButton.Image = global::SAM.Game.Resources.Lock;
			this._LockAllButton.ImageTransparentColor = global::System.Drawing.Color.Magenta;
			this._LockAllButton.Name = "_LockAllButton";
			this._LockAllButton.Size = new global::System.Drawing.Size(23, 22);
			this._LockAllButton.Text = "Lock All";
			this._LockAllButton.ToolTipText = "Lock all achievements.";
			this._LockAllButton.Click += new global::System.EventHandler(this.OnLockAll);
			this._InvertAllButton.DisplayStyle = global::System.Windows.Forms.ToolStripItemDisplayStyle.Image;
			this._InvertAllButton.Image = global::SAM.Game.Resources.Invert;
			this._InvertAllButton.ImageTransparentColor = global::System.Drawing.Color.Magenta;
			this._InvertAllButton.Name = "_InvertAllButton";
			this._InvertAllButton.Size = new global::System.Drawing.Size(23, 22);
			this._InvertAllButton.Text = "Invert All";
			this._InvertAllButton.ToolTipText = "Invert all achievements.";
			this._InvertAllButton.Click += new global::System.EventHandler(this.OnInvertAll);
			this._UnlockAllButton.DisplayStyle = global::System.Windows.Forms.ToolStripItemDisplayStyle.Image;
			this._UnlockAllButton.Image = global::SAM.Game.Resources.Unlock;
			this._UnlockAllButton.ImageTransparentColor = global::System.Drawing.Color.Magenta;
			this._UnlockAllButton.Name = "_UnlockAllButton";
			this._UnlockAllButton.Size = new global::System.Drawing.Size(23, 22);
			this._UnlockAllButton.Text = "Unlock All";
			this._UnlockAllButton.ToolTipText = "Unlock all achievements.";
			this._UnlockAllButton.Click += new global::System.EventHandler(this.OnUnlockAll);
			this._StatisticsTabPage.Controls.Add(this._EnableStatsEditingCheckBox);
			this._StatisticsTabPage.Controls.Add(this._StatisticsDataGridView);
			this._StatisticsTabPage.Location = new global::System.Drawing.Point(4, 22);
			this._StatisticsTabPage.Name = "_StatisticsTabPage";
			this._StatisticsTabPage.Padding = new global::System.Windows.Forms.Padding(3);
			this._StatisticsTabPage.Size = new global::System.Drawing.Size(608, 308);
			this._StatisticsTabPage.TabIndex = 1;
			this._StatisticsTabPage.Text = "Statistics";
			this._StatisticsTabPage.UseVisualStyleBackColor = true;
			this._EnableStatsEditingCheckBox.Anchor = (global::System.Windows.Forms.AnchorStyles.Bottom | global::System.Windows.Forms.AnchorStyles.Left | global::System.Windows.Forms.AnchorStyles.Right);
			this._EnableStatsEditingCheckBox.AutoSize = true;
			this._EnableStatsEditingCheckBox.Location = new global::System.Drawing.Point(6, 285);
			this._EnableStatsEditingCheckBox.Name = "_EnableStatsEditingCheckBox";
			this._EnableStatsEditingCheckBox.Size = new global::System.Drawing.Size(512, 17);
			this._EnableStatsEditingCheckBox.TabIndex = 1;
			this._EnableStatsEditingCheckBox.Text = "I understand by modifying the values of stats, I may screw things up and can't blame anyone but myself.";
			this._EnableStatsEditingCheckBox.UseVisualStyleBackColor = true;
			this._EnableStatsEditingCheckBox.CheckedChanged += new global::System.EventHandler(this.OnStatAgreementChecked);
			this._StatisticsDataGridView.AllowUserToAddRows = false;
			this._StatisticsDataGridView.AllowUserToDeleteRows = false;
			this._StatisticsDataGridView.Anchor = (global::System.Windows.Forms.AnchorStyles.Top | global::System.Windows.Forms.AnchorStyles.Bottom | global::System.Windows.Forms.AnchorStyles.Left | global::System.Windows.Forms.AnchorStyles.Right);
			this._StatisticsDataGridView.ColumnHeadersHeightSizeMode = global::System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
			this._StatisticsDataGridView.Location = new global::System.Drawing.Point(6, 6);
			this._StatisticsDataGridView.Name = "_StatisticsDataGridView";
			this._StatisticsDataGridView.Size = new global::System.Drawing.Size(596, 273);
			this._StatisticsDataGridView.TabIndex = 0;
			this._StatisticsDataGridView.CellEndEdit += new global::System.Windows.Forms.DataGridViewCellEventHandler(this.OnStatCellEndEdit);
			this._StatisticsDataGridView.DataError += new global::System.Windows.Forms.DataGridViewDataErrorEventHandler(this.OnStatDataError);
			base.AutoScaleDimensions = new global::System.Drawing.SizeF(6f, 13f);
			base.AutoScaleMode = global::System.Windows.Forms.AutoScaleMode.Font;
			base.ClientSize = new global::System.Drawing.Size(632, 392);
			base.Controls.Add(this._MainToolStrip);
			base.Controls.Add(this._MainTabControl);
			base.Controls.Add(this._MainStatusStrip);
			base.Icon = (global::System.Drawing.Icon)componentResourceManager.GetObject("$this.Icon");
			this.MinimumSize = new global::System.Drawing.Size(640, 50);
			base.Name = "Manager";
			this.Text = "Steam Achievement Manager 7.0";
			this._MainToolStrip.ResumeLayout(false);
			this._MainToolStrip.PerformLayout();
			this._MainStatusStrip.ResumeLayout(false);
			this._MainStatusStrip.PerformLayout();
			this._MainTabControl.ResumeLayout(false);
			this._AchievementsTabPage.ResumeLayout(false);
			this._AchievementsTabPage.PerformLayout();
			this._AchievementsToolStrip.ResumeLayout(false);
			this._AchievementsToolStrip.PerformLayout();
			this._StatisticsTabPage.ResumeLayout(false);
			this._StatisticsTabPage.PerformLayout();
			((global::System.ComponentModel.ISupportInitialize)this._StatisticsDataGridView).EndInit();
			base.ResumeLayout(false);
			base.PerformLayout();
		}

		// Token: 0x0400001A RID: 26
		private global::System.ComponentModel.IContainer components;

		// Token: 0x0400001B RID: 27
		private global::System.Windows.Forms.ToolStrip _MainToolStrip;

		// Token: 0x0400001C RID: 28
		private global::System.Windows.Forms.ToolStripButton _StoreButton;

		// Token: 0x0400001D RID: 29
		private global::System.Windows.Forms.ToolStripButton _ReloadButton;

		// Token: 0x0400001E RID: 30
		private global::System.Windows.Forms.StatusStrip _MainStatusStrip;

		// Token: 0x0400001F RID: 31
		private global::System.Windows.Forms.ToolStripStatusLabel _CountryStatusLabel;

		// Token: 0x04000020 RID: 32
		private global::System.Windows.Forms.ToolStripStatusLabel _GameStatusLabel;

		// Token: 0x04000021 RID: 33
		private global::System.Windows.Forms.ImageList _AchievementImageList;

		// Token: 0x04000022 RID: 34
		private global::System.Windows.Forms.Timer _CallbackTimer;

		// Token: 0x04000023 RID: 35
		private global::System.Windows.Forms.TabControl _MainTabControl;

		// Token: 0x04000024 RID: 36
		private global::System.Windows.Forms.TabPage _AchievementsTabPage;

		// Token: 0x04000025 RID: 37
		private global::System.Windows.Forms.TabPage _StatisticsTabPage;

		// Token: 0x04000026 RID: 38
		private global::SAM.Game.DoubleBufferedListView _AchievementListView;

		// Token: 0x04000027 RID: 39
		private global::System.Windows.Forms.ColumnHeader _AchievementNameColumnHeader;

		// Token: 0x04000028 RID: 40
		private global::System.Windows.Forms.ColumnHeader _AchievementDescriptionColumnHeader;

		// Token: 0x04000029 RID: 41
		private global::System.Windows.Forms.ToolStrip _AchievementsToolStrip;

		// Token: 0x0400002A RID: 42
		private global::System.Windows.Forms.ToolStripButton _LockAllButton;

		// Token: 0x0400002B RID: 43
		private global::System.Windows.Forms.ToolStripButton _InvertAllButton;

		// Token: 0x0400002C RID: 44
		private global::System.Windows.Forms.ToolStripButton _UnlockAllButton;

		// Token: 0x0400002D RID: 45
		private global::System.Windows.Forms.DataGridView _StatisticsDataGridView;

		// Token: 0x0400002E RID: 46
		public global::System.Windows.Forms.CheckBox _EnableStatsEditingCheckBox;

		// Token: 0x0400002F RID: 47
		private global::System.Windows.Forms.ToolStripButton _ResetButton;

		// Token: 0x04000030 RID: 48
		private global::System.Windows.Forms.ToolStripStatusLabel _DownloadStatusLabel;
	}
}
