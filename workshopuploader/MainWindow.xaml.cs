using System;
using System.Windows;
using System.ComponentModel;
using Windows.System;
using Steamworks;
using Microsoft.Win32;
using System.IO;
using System.Diagnostics;
using ModernWpf;
using System.Windows.Media;
using ModernWpf.Controls;
using ModernWpf.Controls.Primitives;
using System.Windows.Controls;
using System.Linq;
using System.Threading.Tasks;
using System.Text.RegularExpressions;
using workshopuploader.Compiler;
using Windows.Storage.Pickers;
using Windows.Storage;
using System.Windows.Forms;

namespace workshopuploader {
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window {
        private Mod CurrentMod { get; set; } = null;
        private bool CurrentSaved { get; set; } = true;

        public MainWindow() {
            InitializeComponent();

            ProgressUpdater.MainWindow = this;

            ThemeManager.Current.ApplicationTheme = ApplicationTheme.Dark;
            ThemeManager.Current.AccentColor = Colors.Red;

            Title = "SCP:CBR Workshop Uploader";
        }

        private async void Window_Closing(object sender, CancelEventArgs e) {
            if (!CurrentSaved) {
                e.Cancel = true;

                ContentDialog dialog = new ContentDialog();
                dialog.Content = "Are you sure you want to exit without saving?";
                dialog.DefaultButton = ContentDialogButton.Primary;
                dialog.PrimaryButtonText = "Yes";
                dialog.CloseButtonText = "No";
                dialog.PrimaryButtonClick += CloseDialog_Yes;
                dialog.CloseButtonClick += CloseDialog_No;
                await dialog.ShowAsync();
            } else {
                if (Directory.Exists(Directory.GetCurrentDirectory() + "\\Temp\\"))
                    Directory.Delete(Directory.GetCurrentDirectory() + "\\Temp\\", true);
            }
        }

        private void CloseDialog_Yes(ContentDialog dialog, ContentDialogButtonClickEventArgs e) {
            if (Directory.Exists(Directory.GetCurrentDirectory() + "\\Temp\\"))
                Directory.Delete(Directory.GetCurrentDirectory() + "\\Temp\\", true);

            Environment.Exit(0);
        }

        private void CloseDialog_No(ContentDialog dialog, ContentDialogButtonClickEventArgs e) {
            dialog.Hide();
        }

        private async void Documentation_Click(object sender, RoutedEventArgs e) {
            await Launcher.LaunchUriAsync(new Uri("https://steamcommunity.com/sharedfiles/filedetails/?id=3013429910"));
        }

        private void Application_Click(object sender, RoutedEventArgs e) {
            System.Windows.MessageBox.Show("SCP:CBR Workshop Uploader\nv1.0 © HNT8 Organization\nProgrammed in C# with WPF for UI.", "Application Information", MessageBoxButton.OK, MessageBoxImage.Information);
        }

        private async void Discord_Click(object sender, RoutedEventArgs e) {
            await Launcher.LaunchUriAsync(new Uri("https://discord.gg/SCPCBR"));
        }

        private async void Steam_Click(object sender, RoutedEventArgs e) {
            await Launcher.LaunchUriAsync(new Uri("https://steamcommunity.com/app/2090230/"));
        }

        private void Exit_Click(object sender, RoutedEventArgs e) {
            Close();
        }

        private async void newMod_Click(object sender, RoutedEventArgs e) {
            if (!CurrentSaved) {
                ContentDialog dialog = new ContentDialog();
                dialog.Content = "Are you sure you want to create a new upload configuration without saving the upload configuration you are currently editing?";
                dialog.DefaultButton = ContentDialogButton.Primary;
                dialog.PrimaryButtonText = "Yes";
                dialog.CloseButtonText = "No";
                dialog.PrimaryButtonClick += NewDialog_Yes;
                dialog.CloseButtonClick += NewDialog_No;
                await dialog.ShowAsync();
            } else {
                CurrentMod = new Mod();
                topWarning.Visibility = Visibility.Hidden;
                topWarning.Height = 0;
                tabs.IsEnabled = true;
                CurrentSaved = false;
                Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";

                uploaderTab_modName.Text = CurrentMod.Name;
                uploaderTab_modDelimiter.Text = CurrentMod.Delimiter;
                uploaderTab_modVersion.Text = CurrentMod.Version;
                uploaderTab_modDescription.Text = CurrentMod.Description;
                bbPreview.BBCode = CurrentMod.Description;
                uploaderTab_modTagModel.IsChecked = CurrentMod.ModelTag;
                uploaderTab_modTagTexture.IsChecked = CurrentMod.TextureTag;
                uploaderTab_modTagAudio.IsChecked = CurrentMod.AudioTag;
                uploaderTab_modTagMap.IsChecked = CurrentMod.MapTag;
                uploaderTab_modTagLanguage.IsChecked = CurrentMod.LanguageTag;
                uploaderTab_modTagData.IsChecked = CurrentMod.DataTag;
                uploaderTab_modVisibility.SelectedIndex = CurrentMod.Visibility;
                uploaderTab_workshopIcon.Text = CurrentMod.WorkshopImage;
                uploaderTab_modChangelog.Text = CurrentMod.Changelog;
                uploaderTab_modFolder.Text = CurrentMod.ModPath;
            }
        }

        private async void NewDialog_Yes(ContentDialog dialog, ContentDialogButtonClickEventArgs e) {
            CurrentMod = new Mod();
            topWarning.Visibility = Visibility.Hidden;
            topWarning.Height = 0;
            tabs.IsEnabled = true;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";

            uploaderTab_modName.Text = CurrentMod.Name;
            uploaderTab_modDelimiter.Text = CurrentMod.Delimiter;
            uploaderTab_modVersion.Text = CurrentMod.Version;
            uploaderTab_modDescription.Text = CurrentMod.Description;
            bbPreview.BBCode = CurrentMod.Description;
            uploaderTab_modTagModel.IsChecked = CurrentMod.ModelTag;
            uploaderTab_modTagTexture.IsChecked = CurrentMod.TextureTag;
            uploaderTab_modTagAudio.IsChecked = CurrentMod.AudioTag;
            uploaderTab_modTagMap.IsChecked = CurrentMod.MapTag;
            uploaderTab_modTagLanguage.IsChecked = CurrentMod.LanguageTag;
            uploaderTab_modTagData.IsChecked = CurrentMod.DataTag;
            uploaderTab_modVisibility.SelectedIndex = CurrentMod.Visibility;
            uploaderTab_workshopIcon.Text = CurrentMod.WorkshopImage;
            uploaderTab_modChangelog.Text = CurrentMod.Changelog;
            uploaderTab_modFolder.Text = CurrentMod.ModPath;
        }

        private void NewDialog_No(ContentDialog dialog, ContentDialogButtonClickEventArgs e) {
            dialog.Hide();
        }

        private async void openMod_Click(object sender, RoutedEventArgs e) {
            if (!CurrentSaved) {
                ContentDialog dialog = new ContentDialog();
                dialog.Content = "Are you sure you want to open another upload configuration without saving the upload configuration you are currently editing?";
                dialog.DefaultButton = ContentDialogButton.Primary;
                dialog.PrimaryButtonText = "Yes";
                dialog.CloseButtonText = "No";
                dialog.PrimaryButtonClick += OpenDialog_Yes;
                dialog.CloseButtonClick += OpenDialog_No;
                await dialog.ShowAsync();
            } else {
                Microsoft.Win32.OpenFileDialog dialog = new();
                dialog.DefaultExt = ".cbruc";
                dialog.Filter = "SCPCBR Upload Configuration File (.cbruc)|*.cbruc";
                dialog.Title = "Open Existing Upload Configuration";
                dialog.CheckFileExists = true;
                dialog.CheckPathExists = true;
                dialog.Multiselect = false;
                dialog.InitialDirectory = Directory.GetCurrentDirectory() + "\\Mods\\";

                if (dialog.ShowDialog() == true) {
                    CurrentMod = ManifestSaver.LoadMod(dialog.FileName);
                    topWarning.Visibility = Visibility.Hidden;
                    topWarning.Height = 0;
                    tabs.IsEnabled = true;

                    uploaderTab_modName.Text = CurrentMod.Name;
                    uploaderTab_modDelimiter.Text = CurrentMod.Delimiter;
                    uploaderTab_modVersion.Text = CurrentMod.Version;
                    uploaderTab_modDescription.Text = CurrentMod.Description;
                    bbPreview.BBCode = CurrentMod.Description;
                    uploaderTab_modTagModel.IsChecked = CurrentMod.ModelTag;
                    uploaderTab_modTagTexture.IsChecked = CurrentMod.TextureTag;
                    uploaderTab_modTagAudio.IsChecked = CurrentMod.AudioTag;
                    uploaderTab_modTagMap.IsChecked = CurrentMod.MapTag;
                    uploaderTab_modTagLanguage.IsChecked = CurrentMod.LanguageTag;
                    uploaderTab_modTagData.IsChecked = CurrentMod.DataTag;
                    uploaderTab_modVisibility.SelectedIndex = CurrentMod.Visibility;
                    uploaderTab_workshopIcon.Text = CurrentMod.WorkshopImage;
                    uploaderTab_modChangelog.Text = CurrentMod.Changelog;
                    uploaderTab_modFolder.Text = CurrentMod.ModPath;

                    CurrentSaved = true;
                    Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name;
                }
            }
        }

        private async void OpenDialog_Yes(ContentDialog o, ContentDialogButtonClickEventArgs e) {
            o.Hide();

            Microsoft.Win32.OpenFileDialog dialog = new();
            dialog.DefaultExt = ".cbruc";
            dialog.Filter = "SCPCBR Upload Configuration File (.cbruc)|*.cbruc";
            dialog.Title = "Open Existing Upload Configuration";
            dialog.CheckFileExists = true;
            dialog.CheckPathExists = true;
            dialog.Multiselect = false;
            dialog.InitialDirectory = Directory.GetCurrentDirectory() + "\\Mods\\";

            if (dialog.ShowDialog() == true) {
                CurrentMod = ManifestSaver.LoadMod(dialog.FileName);
                topWarning.Visibility = Visibility.Hidden;
                topWarning.Height = 0;
                tabs.IsEnabled = true;

                uploaderTab_modName.Text = CurrentMod.Name;
                uploaderTab_modDelimiter.Text = CurrentMod.Delimiter;
                uploaderTab_modVersion.Text = CurrentMod.Version;
                uploaderTab_modDescription.Text = CurrentMod.Description;
                bbPreview.BBCode = CurrentMod.Description;
                uploaderTab_modTagModel.IsChecked = CurrentMod.ModelTag;
                uploaderTab_modTagTexture.IsChecked = CurrentMod.TextureTag;
                uploaderTab_modTagAudio.IsChecked = CurrentMod.AudioTag;
                uploaderTab_modTagMap.IsChecked = CurrentMod.MapTag;
                uploaderTab_modTagLanguage.IsChecked = CurrentMod.LanguageTag;
                uploaderTab_modTagData.IsChecked = CurrentMod.DataTag;
                uploaderTab_modVisibility.SelectedIndex = CurrentMod.Visibility;
                uploaderTab_workshopIcon.Text = CurrentMod.WorkshopImage;
                uploaderTab_modChangelog.Text = CurrentMod.Changelog;
                uploaderTab_modFolder.Text = CurrentMod.ModPath;

                CurrentSaved = true;
                Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name;
            }
        }

        private void OpenDialog_No(ContentDialog dialog, ContentDialogButtonClickEventArgs e) {
            dialog.Hide();
        }

        private void saveMod_Click(object sender, RoutedEventArgs e) {
            Microsoft.Win32.SaveFileDialog dialog = new();
            dialog.FileName = CurrentMod.Delimiter;
            dialog.DefaultExt = ".cbruc";
            dialog.Filter = "SCPCBR Upload Configuration File (.cbruc)|*.cbruc";
            dialog.Title = "Save Upload Configuration";
            dialog.CheckPathExists = true;

            if (dialog.ShowDialog() == true) {
                ManifestSaver.SaveMod(CurrentMod, dialog.FileName);
                CurrentSaved = true;
                Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name;
            }
        }

        private void uploaderTab_modDescription_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e) {
            bbPreview.BBCode = uploaderTab_modDescription.Text;
            CurrentMod.Description = uploaderTab_modDescription.Text;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modName_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e) {
            CurrentMod.Name = uploaderTab_modName.Text;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modDelimiter_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e) {
            CurrentMod.Delimiter = uploaderTab_modDelimiter.Text;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modVersion_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e) {
            CurrentMod.Version = uploaderTab_modVersion.Text;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modTagModel_Checked(object sender, RoutedEventArgs e) {
            CurrentMod.ModelTag = uploaderTab_modTagModel.IsChecked == true;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modTagTexture_Checked(object sender, RoutedEventArgs e) {
            CurrentMod.TextureTag = uploaderTab_modTagTexture.IsChecked == true;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modTagAudio_Checked(object sender, RoutedEventArgs e) {
            CurrentMod.AudioTag = uploaderTab_modTagAudio.IsChecked == true;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modTagMap_Checked(object sender, RoutedEventArgs e) {
            CurrentMod.MapTag = uploaderTab_modTagMap.IsChecked == true;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modTagLanguage_Checked(object sender, RoutedEventArgs e) {
            CurrentMod.LanguageTag = uploaderTab_modTagLanguage.IsChecked == true;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modTagData_Checked(object sender, RoutedEventArgs e) {
            CurrentMod.DataTag = uploaderTab_modTagData.IsChecked == true;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_workshopIcon_Upload_Click(object sender, RoutedEventArgs e) {
            Microsoft.Win32.OpenFileDialog dialog = new();
            dialog.Filter = "Png (.png)|*.png|Jpg (.jpg)|*.jpg|Jpeg (.jpeg)|*.jpeg";
            dialog.Title = "Choose Workshop Image";
            dialog.CheckFileExists = true;
            dialog.CheckPathExists = true;
            dialog.RestoreDirectory = true;
            dialog.Multiselect = false;

            if (dialog.ShowDialog() == true) {
                uploaderTab_workshopIcon.Text = dialog.FileName;
                CurrentMod.WorkshopImage = dialog.FileName;
                CurrentSaved = false;
                Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
            }
        }

        private void uploaderTab_modChangelog_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e) {
            CurrentMod.Changelog = uploaderTab_modChangelog.Text;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploaderTab_modVisibility_SelectionChanged(object sender, RoutedEventArgs e) {
            CurrentMod.Visibility = uploaderTab_modVisibility.SelectedIndex;
            CurrentSaved = false;
            Title = "SCP:CBR Workshop Uploader | " + CurrentMod.Name + " | Unsaved";
        }

        private void uploadSubmit_Click(object sender, RoutedEventArgs e) {
            uploadConfirmFlyout.Placement = FlyoutPlacementMode.Top;
            uploadConfirmFlyout.ShowAt(uploadSubmit);
        }

        private async void uploadConfirm_Click(object sender, RoutedEventArgs e) {
            uploadConfirmFlyout.Hide();
            await ManifestSaver.CompileMod(CurrentMod, this);
        }

        private void uploadReset_Click(object sender, RoutedEventArgs e) {
            resetConfirmFlyout.Placement = FlyoutPlacementMode.Top;
            resetConfirmFlyout.ShowAt(uploadSubmit);
        }

        private void resetConfirm_Click(object sender, RoutedEventArgs e) {
            CurrentMod.WorkshopId = 0;
            resetConfirmFlyout.Hide();
            CurrentSaved = false;
        }

        private async void uploaderTab_modFolder_Upload_Click(object sender, RoutedEventArgs e) {
            FolderBrowserDialog dialog = new FolderBrowserDialog();
            dialog.UseDescriptionForTitle = true;
            dialog.Description = "Select Mod Folder to Upload";

            if (dialog.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
                CurrentMod.ModPath = dialog.SelectedPath;
                uploaderTab_modFolder.Text = dialog.SelectedPath;
            }
        }
    }
}
