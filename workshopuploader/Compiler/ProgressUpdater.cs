using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace workshopuploader.Compiler
{
    class ProgressUpdater : IProgress<float> {
        public static MainWindow MainWindow = null;

        public void Report(float value) {
            int newValue = Math.Clamp((int)Math.Round(value * 100), 0, 100);

            if (MainWindow is not null) {
                MainWindow.Dispatcher.Invoke(new Action(() => {
                    MainWindow.uploadProgress.Value = newValue;
                }));
            }
        }

        public static void Report(int value) {
            int newValue = Math.Clamp(value, 0, 100);

            if (MainWindow is not null) {
                MainWindow.Dispatcher.Invoke(new Action(() => {
                    MainWindow.uploadProgress.Value = newValue;
                }));
            }
        }
    }
}
