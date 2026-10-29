const { app, BrowserWindow, Menu, shell } = require('electron');
const path = require('path');

function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1280,
    height: 840,
    minWidth: 900,
    minHeight: 650,
    title: 'AI TEACH — Your Interactive 3D Tutor',
    icon: path.join(__dirname, 'icon.png'),
    backgroundColor: '#090D16',
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      sandbox: true
    },
    autoHideMenuBar: false
  });

  // Load the web app index
  mainWindow.loadFile(path.join(__dirname, 'index.html'));

  // Open external links (such as Instamojo payment gateway) in system browser
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url);
    return { action: 'deny' };
  });

  // Native Windows Desktop Menu (हिंदी एवं अंग्रेजी में)
  const template = [
    {
      label: 'AI TEACH',
      submenu: [
        { role: 'reload', label: 'रीलोड करें (Reload)' },
        { role: 'forceReload', label: 'फ़ोर्स रीलोड (Force Reload)' },
        { type: 'separator' },
        { role: 'quit', label: 'बाहर निकलें (Exit)' }
      ]
    },
    {
      label: 'देखें (View)',
      submenu: [
        { role: 'resetZoom', label: 'रीसेट ज़ूम' },
        { role: 'zoomIn', label: 'ज़ूम इन (+)' },
        { role: 'zoomOut', label: 'ज़ूम आउट (-)' },
        { type: 'separator' },
        { role: 'togglefullscreen', label: 'फ़ुलस्क्रीन (Toggle Fullscreen)' }
      ]
    },
    {
      label: 'सहायता एवं प्लान्स (Help)',
      submenu: [
        {
          label: 'Instamojo Payments (प्लान खरीदें)',
          click: async () => {
            await shell.openExternal('https://imjo.in/WhRtvz');
          }
        },
        {
          label: 'डेवलपर टूल्स (DevTools)',
          accelerator: 'F12',
          click: () => {
            mainWindow.webContents.toggleDevTools();
          }
        }
      ]
    }
  ];

  const menu = Menu.buildFromTemplate(template);
  Menu.setApplicationMenu(menu);
}

app.whenReady().then(() => {
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});
