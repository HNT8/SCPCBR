using System;
using System.Collections.Immutable;

namespace workshopuploader {
    public class Mod {
        public string ModPath;

        public string Delimiter;
        public string Name;
        public string Description;
        public string Version;

        public bool ModelTag;
        public bool TextureTag;
        public bool AudioTag;
        public bool MapTag;
        public bool LanguageTag;
        public bool DataTag;

        public string Changelog;

        public string WorkshopImage;
        public ulong WorkshopId;

        public int Visibility;

        public Mod() {
            this.Delimiter = "unnamedmod";
            this.Name = "Unnamed Mod";
            this.Description = "No description";
            this.Version = "1.0";

            this.ModelTag = false;
            this.TextureTag = false;
            this.AudioTag = false;
            this.MapTag = false;
            this.LanguageTag = false;
            this.DataTag = false;

            this.Changelog = "";

            this.WorkshopImage = "";
            this.WorkshopId = 0;

            this.Visibility = 0;

            this.ModPath = "";
        }

        public Mod(SerializableMod mod) {
            this.Delimiter = mod.Delimiter;
            this.Name = mod.Name;
            this.Description = mod.Description;
            this.Version = mod.Version;

            this.ModelTag = mod.ModelTag;
            this.TextureTag = mod.TextureTag;
            this.AudioTag = mod.AudioTag;
            this.MapTag = mod.MapTag;
            this.LanguageTag = mod.LanguageTag;
            this.DataTag = mod.DataTag;

            this.Changelog = mod.Changelog;

            this.WorkshopImage = mod.WorkshopImage;
            this.WorkshopId = mod.WorkshopId;

            this.Visibility = mod.Visibility;

            this.ModPath = mod.ModPath;
        }
    }

    [Serializable]
    public class SerializableMod {
        public string Delimiter;
        public string Name;
        public string Description;
        public string Version;

        public bool ModelTag;
        public bool TextureTag;
        public bool AudioTag;
        public bool MapTag;
        public bool LanguageTag;
        public bool DataTag;

        public string Changelog;

        public string WorkshopImage;
        public ulong WorkshopId;

        public int Visibility;

        public string ModPath;

        public SerializableMod() { }

        public SerializableMod(Mod mod) {
            this.Delimiter = mod.Delimiter;
            this.Name = mod.Name;
            this.Description = mod.Description;
            this.Version = mod.Version;

            this.ModelTag = mod.ModelTag;
            this.TextureTag = mod.TextureTag;
            this.AudioTag = mod.AudioTag;
            this.MapTag = mod.MapTag;
            this.LanguageTag = mod.LanguageTag;
            this.DataTag = mod.DataTag;

            this.Changelog = mod.Changelog;

            this.WorkshopImage = mod.WorkshopImage;
            this.WorkshopId = mod.WorkshopId;

            this.Visibility = mod.Visibility;

            this.ModPath = mod.ModPath;
        }
    }
}
