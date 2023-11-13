using ModernWpf.Controls;
using Steamworks.Ugc;
using System;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;
using System.Xml.Serialization;

namespace workshopuploader.Compiler
{
    class ManifestSaver
    {
        public static async Task<bool> CompileMod(Mod mod, MainWindow window) {
            try {

                if (!Directory.Exists(mod.ModPath)) {
                    ContentDialog dialog = new ContentDialog();
                    dialog.Content = "A valid mod path must be provided for your mod to upload to the steam workshop.";
                    dialog.DefaultButton = ContentDialogButton.Close;
                    dialog.CloseButtonText = "Close";
                    await dialog.ShowAsync();
                    return false;
                }

                if (!File.Exists(mod.ModPath + "\\mod.toml")) {
                    ContentDialog dialog = new ContentDialog();
                    dialog.Content = "A valid mod must contain a mod.toml file.";
                    dialog.DefaultButton = ContentDialogButton.Close;
                    dialog.CloseButtonText = "Close";
                    await dialog.ShowAsync();
                    return false;
                }

                if (mod.Delimiter.Length == 0 || mod.Delimiter.Any(Char.IsWhiteSpace) || mod.Delimiter.Contains('\\') || mod.Delimiter.Contains('/')) {
                    ContentDialog dialog = new ContentDialog();
                    dialog.Content = "A valid mod delimiter must be provided for your mod to function properly. It must contain characters, cannot contain whitespace, and cannot contain slashes or backslashes.";
                    dialog.DefaultButton = ContentDialogButton.Close;
                    dialog.CloseButtonText = "Close";
                    await dialog.ShowAsync();
                    return false;
                }

                ProgressUpdater.Report(33);

                if (mod.Name.Length == 0 || String.IsNullOrWhiteSpace(mod.Name) || Char.IsWhiteSpace(mod.Name.First()) || Char.IsWhiteSpace(mod.Name.Last())) {
                    ContentDialog dialog = new ContentDialog();
                    dialog.Content = "A valid mod name must be provided for your mod to be uploaded to the steam workshop. It must contain characters, cannot only contain whitespace, and cannot start or end with whitespace.";
                    dialog.DefaultButton = ContentDialogButton.Close;
                    dialog.CloseButtonText = "Close";
                    await dialog.ShowAsync();
                    return false;
                }

                ProgressUpdater.Report(66);

                if (mod.Version.Length == 0 || mod.Version.Any(Char.IsWhiteSpace)) {
                    ContentDialog dialog = new ContentDialog();
                    dialog.Content = "A valid mod version must be provided for your mod to be uploaded to the steam workshop. It cannot contain any whitespace.";
                    dialog.DefaultButton = ContentDialogButton.Close;
                    dialog.CloseButtonText = "Close";
                    await dialog.ShowAsync();
                    return false;
                }

                ProgressUpdater.Report(100);

                if (mod.WorkshopId <= 0) {
                    PublishResult newPublish = await Editor.NewCommunityFile.SubmitAsync(new ProgressUpdater());
                    mod.WorkshopId = newPublish.FileId;
                }

                Editor workshopItem = new Editor(mod.WorkshopId);
                workshopItem.WithTitle(mod.Name);
                workshopItem.WithMetaData($"Delimiter: {mod.Delimiter}\nVersion: {mod.Version}");

                ProgressUpdater.Report(10);

                if (mod.Description.Length > 0 && !String.IsNullOrWhiteSpace(mod.Description))
                    workshopItem.WithDescription(mod.Description);

                ProgressUpdater.Report(20);

                if (mod.ModelTag)
                    workshopItem.WithTag("Model");

                ProgressUpdater.Report(30);

                if (mod.TextureTag)
                    workshopItem.WithTag("Texture");

                ProgressUpdater.Report(40);

                if (mod.AudioTag)
                    workshopItem.WithTag("Audio");

                ProgressUpdater.Report(50);

                if (mod.MapTag)
                    workshopItem.WithTag("Map");

                ProgressUpdater.Report(60);

                if (mod.LanguageTag)
                    workshopItem.WithTag("Language");

                ProgressUpdater.Report(70);

                if (mod.DataTag)
                    workshopItem.WithTag("Data");

                ProgressUpdater.Report(80);

                if (mod.Changelog.Length > 0 && !String.IsNullOrWhiteSpace(mod.Changelog))
                    workshopItem.WithChangeLog(mod.Changelog);

                ProgressUpdater.Report(90);

                if (File.Exists(mod.WorkshopImage))
                    workshopItem.WithPreviewFile(mod.WorkshopImage);

                ProgressUpdater.Report(100);

                switch (mod.Visibility) {
                    case 0:
                        workshopItem.WithPublicVisibility();
                        break;
                    case 1:
                        workshopItem.WithFriendsOnlyVisibility();
                        break;
                    case 2:
                        workshopItem.WithPrivateVisibility();
                        break;
                }

                workshopItem.WithContent(mod.ModPath);
                await workshopItem.SubmitAsync(new ProgressUpdater());

                ContentDialog successDialog = new ContentDialog();
                successDialog.Content = "The mod was successfully uploaded!";
                successDialog.DefaultButton = ContentDialogButton.Close;
                successDialog.CloseButtonText = "Close";
                await successDialog.ShowAsync();
                ProgressUpdater.Report(100);
                return true;

            } catch (Exception e) {
                MessageBox.Show("An error occured while attempting to upload your mod.\nPlease report this error in our discord server:\nError: " + e.Message, "SCP:CBR Workshop Uploader", MessageBoxButton.OK, MessageBoxImage.Error);
                return false;
            }
        }

        public static bool SaveMod(Mod mod, string path) {
            TextWriter writer;
            try {
                XmlSerializer serializer = new XmlSerializer(typeof(SerializableMod));
                writer = new StreamWriter(path);
                serializer.Serialize(writer, new SerializableMod(mod));
            } catch (Exception e) {
                MessageBox.Show("An error occured while saving the upload configuration.\nPlease report this error in our discord server:\nError: " + e.Message, "SCP:CBR Workshop Uploader", MessageBoxButton.OK, MessageBoxImage.Error);
                return false;
            }
            writer.Close();
            return true;
        }

        public static Mod LoadMod(string path) {
            TextReader reader;
            try {
                XmlSerializer serializer = new XmlSerializer(typeof(SerializableMod));
                reader = new StreamReader(path);
                return new Mod(serializer.Deserialize(reader) as SerializableMod);
            } catch (Exception e) {
                MessageBox.Show("An error occured while loading the upload configuration.\nPlease report this error in our discord server:\nError: " + e.Message, "SCP:CBR Workshop Uploader", MessageBoxButton.OK, MessageBoxImage.Error);
                return null;
            }
        }
    }
}
