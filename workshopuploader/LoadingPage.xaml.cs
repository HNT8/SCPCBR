using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace workshopuploader
{
    /// <summary>
    /// Interaction logic for LoadingPage.xaml
    /// </summary>
    public partial class LoadingPage : Window
    {
        public LoadingPage()
        {
            InitializeComponent();
        }

        private async void Window_Loaded(object sender, RoutedEventArgs e) {
            await SteamOverlayDelay();
        }

        public async Task SteamOverlayDelay() {
            progressBar.Value = 0;
            for (int i = 0; i < 5000; i++) {
                await Task.Delay(1);
                Dispatcher.Invoke(() => {
                    progressBar.Value += 0.02;
                });
            }

            MainWindow mainWindow = new MainWindow();
            mainWindow.Show();

            Close();
        }
    }
}
