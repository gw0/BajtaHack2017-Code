#ifndef MAIN_WINDOW_H
#define MAIN_WINDOW_H

#include <QtWidgets>
#include <QtNetwork>

class ImageDisplayWidget;

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow (QWidget *parent = Q_NULLPTR);
    virtual ~MainWindow ();

protected:
    void refreshData ();

    quint32 readHDC1080Register (const QString &baseUrl, int reg);
    QByteArray readValue (const QString &valueUrl);
    float readFloatValue (const QString &valueUrl);


protected:
    QNetworkAccessManager *networkManager;

    QString urlUniversalSensor;

    float temperature;
    float humidity;

    int pirCounter;
    int pirChange;
    int vibrationCounter;
    int vibrationChange;
    int dhtHumidity;
    int dhtTemperature;
    int co;
    int illumination;
    int sound;
    int flame;

    // Widgets
    QDoubleSpinBox *spinBoxTemperature;
    QDoubleSpinBox *spinBoxHumidity;

    QDoubleSpinBox *spinBoxPirCounter;
    QDoubleSpinBox *spinBoxPirChange;
    QDoubleSpinBox *spinBoxVibrationCounter;
    QDoubleSpinBox *spinBoxVibrationChange;
    QDoubleSpinBox *spinBoxDhtHumidity;
    QDoubleSpinBox *spinBoxDhtTemperature;
    QDoubleSpinBox *spinBoxCo;
    QDoubleSpinBox *spinBoxIllumination;
    QDoubleSpinBox *spinBoxSound;
    QDoubleSpinBox *spinBoxFlame;

    ImageDisplayWidget *imageDisplayWidget;
};


#endif
