package ui.workout.notifications;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.awt.*;
import java.awt.TrayIcon.MessageType;


/**
 * Singleton manager for app notifications.
 *
 * @author Nanorta Amwar
 * @version 0.1.0
 * @since 4/21/2026
 */

public class NotificationManager {
    private static NotificationManager instance;
    private TrayIcon trayIcon;
    private boolean traySupported;

    private NotificationManager() {
        traySupported = SystemTray.isSupported();
        if (traySupported){
            try {
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage("");
                trayIcon = new TrayIcon(image, "OtterFit");
                tray.add(trayIcon);
            } catch (AWTException e) {
                traySupported = false;
            }
        }
    }
    public static synchronized  NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();

            return instance;
        }
    }
}
