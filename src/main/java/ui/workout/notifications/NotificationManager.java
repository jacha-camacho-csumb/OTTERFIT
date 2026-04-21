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
        if (traySupported) {
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

    /**
     * Return the single instance of the NotificationManager
     */
    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    /**
     * Displays a system tray notification or falls back to an alert if not supported
     */
    public void showDesktopNotification(String title, String message) {
        if (traySupported && trayIcon != null) {
            Platform.runLater(() -> trayIcon.displayMessage(title, message, MessageType.INFO));
        } else {
            showInfoAlert(title, message);
        }
    }

    /**
     * Displays an error alert dialog to the user.
     */
    public void showErrorAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Warning alert dialog to the user.
     */
    public void showWarningAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Informational alert dialog to the user.
     */
    public void showInfoAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Confirmation dialog and returns true if the user selects OK.
     */
    public boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
