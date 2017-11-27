#include "main_window.h"
#include "image_display_widget.h"
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>


MainWindow::MainWindow (QWidget *parent)
    : QMainWindow(parent),
      urlUniversalSensor("https://d2.srm.bajtahack.si:16200")
{
    QTabWidget *tabWidget = new QTabWidget();
    setCentralWidget(tabWidget);

    QWidget *pageWidget;
    QDoubleSpinBox *doubleSpinBox;

    // UI
    pageWidget = new QWidget();
    QFormLayout *pageLayout = new QFormLayout(pageWidget);
    pageLayout->setFieldGrowthPolicy(QFormLayout::AllNonFixedFieldsGrow);

    tabWidget->addTab(pageWidget, "Conditions");

    // Temperature
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-500, 500);
    doubleSpinBox->setSuffix("°C");
    doubleSpinBox->setReadOnly(true);

    pageLayout->addRow("Temperature", doubleSpinBox);

    spinBoxTemperature = doubleSpinBox;

    // Humidity
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(0, 100);
    doubleSpinBox->setSuffix("%");
    doubleSpinBox->setReadOnly(true);

    pageLayout->addRow("Humidity", doubleSpinBox);

    spinBoxHumidity = doubleSpinBox;


    // Pir counter
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("Pir Counter", doubleSpinBox);

    spinBoxPirCounter = doubleSpinBox;

    // Pir counter
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("Pir Change", doubleSpinBox);

    spinBoxPirChange = doubleSpinBox;

    // Vibration counter
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("Vibration Counter", doubleSpinBox);

    spinBoxVibrationCounter = doubleSpinBox;

    // Vibration change
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("Vibration Change", doubleSpinBox);

    spinBoxVibrationChange = doubleSpinBox;

    // DHT Humidity
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("DHT Humidity", doubleSpinBox);

    spinBoxDhtHumidity = doubleSpinBox;

    // DHT Tempeprature
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("DHT Temperature", doubleSpinBox);

    spinBoxDhtTemperature = doubleSpinBox;

    // CO
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("CO", doubleSpinBox);

    spinBoxCo = doubleSpinBox;

    // Illumination
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("Illumination", doubleSpinBox);

    spinBoxIllumination = doubleSpinBox;

    // Sound
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("Sound", doubleSpinBox);

    spinBoxSound = doubleSpinBox;

    // Flame
    doubleSpinBox = new QDoubleSpinBox();
    doubleSpinBox->setRange(-9999, 9999);
    doubleSpinBox->setReadOnly(true);
    pageLayout->addRow("Flame", doubleSpinBox);

    spinBoxFlame = doubleSpinBox;

    //
    pageWidget = new QWidget();
    tabWidget->addTab(pageWidget, "Last Alarm Image");

    QVBoxLayout *pageLayout2 = new QVBoxLayout(pageWidget);

    imageDisplayWidget = new ImageDisplayWidget();
    pageLayout2->addWidget(imageDisplayWidget);


    // Update
    QTimer *timer = new QTimer(this);
    timer->setInterval(1000);
    connect(timer, &QTimer::timeout, this, &MainWindow::refreshData);

    networkManager = new QNetworkAccessManager(this);
    connect(networkManager, &QNetworkAccessManager::sslErrors, this, [this] (QNetworkReply * reply, const QList<QSslError> &errors) {
        Q_UNUSED(errors)
        reply->ignoreSslErrors();
    });

    timer->start();
}

MainWindow::~MainWindow ()
{
}


float MainWindow::readFloatValue (const QString &valueUrl)
{
    QString reply = QString::fromLatin1(readValue(valueUrl));
    reply = reply.remove(QChar('"'));
    qInfo() << valueUrl << reply;
    return reply.toFloat();
}

QByteArray MainWindow::readValue (const QString &valueUrl)
{
    QNetworkRequest request;
    request.setUrl(valueUrl);
    QNetworkReply *networkReply = networkManager->get(request);

    QEventLoop loop;
    connect(networkReply, &QNetworkReply::finished, &loop, &QEventLoop::quit);
    loop.exec();

    if (networkReply->error() !=  QNetworkReply::NoError) {
        throw QString("GET error: %1").arg(networkReply->error());
    }

    QByteArray data = networkReply->readAll();
    return data;
}

quint32 MainWindow::readHDC1080Register (const QString &baseUrl, int reg)
{
    // Write register address
    if (true) {
        QByteArray payload = QString("\"%1\"").arg(reg, 2, 10, QLatin1Char('0')).toLatin1();

        QNetworkRequest request;
        request.setUrl(baseUrl + "/phy/i2c/1/slaves/64/value");
        QNetworkReply *networkReply = networkManager->put(request, payload);

        QEventLoop loop;
        connect(networkReply, &QNetworkReply::finished, &loop, &QEventLoop::quit);
        loop.exec();

        if (networkReply->error() !=  QNetworkReply::NoError) {
            throw QString("PUT error: %1").arg(networkReply->error());
        }
    }

    // Get register contents
    if (true) {
        QNetworkRequest request;
        request.setUrl(baseUrl + "/phy/i2c/1/slaves/64/value");
        QNetworkReply *networkReply = networkManager->get(request);

        QEventLoop loop;
        connect(networkReply, &QNetworkReply::finished, &loop, &QEventLoop::quit);
        loop.exec();

        if (networkReply->error() !=  QNetworkReply::NoError) {
            throw QString("GET error: %1").arg(networkReply->error());
        }

        QByteArray data = networkReply->readAll();
        QString reply = QString::fromLatin1(data).remove(QChar('"'));

        return reply.toInt(Q_NULLPTR, 16);
    }
}


void MainWindow::refreshData ()
{
    // *** Universal sensor ***
    // Retrieve temperature
    try {
        quint32 val = readHDC1080Register (urlUniversalSensor, 0);
        temperature = (val / 65536.0f)*165 - 40;
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }

    // Retrieve humidity
    try {
        quint32 val = readHDC1080Register (urlUniversalSensor, 1);
        humidity = (val / 65536.0f)*100;
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }

    spinBoxTemperature->setValue(temperature);
    spinBoxHumidity->setValue(humidity);

    // *** Virtual sensor (forwarded values from Arduino) ****
    // pir_counter
    try {
        pirCounter = readFloatValue(urlUniversalSensor + "/log/string/pir_counter/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxPirCounter->setValue(pirCounter);

    // pir_change
    try {
        pirChange = readFloatValue(urlUniversalSensor + "/log/string/pir_change/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxPirChange->setValue(pirChange);

    // vibration_counter
    try {
        vibrationCounter = readFloatValue(urlUniversalSensor + "/log/string/vibration_counter/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxVibrationCounter->setValue(vibrationCounter);

    // vibration_change
    try {
        vibrationChange = readFloatValue(urlUniversalSensor + "/log/string/vibration_change/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxVibrationChange->setValue(vibrationChange);

    // dht_huminity
    try {
        dhtHumidity = readFloatValue(urlUniversalSensor + "/log/string/dht_humidity/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxDhtHumidity->setValue(dhtHumidity);

    // dht_temperature
    try {
        dhtTemperature = readFloatValue(urlUniversalSensor + "/log/string/dht_temperature/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxDhtTemperature->setValue(dhtTemperature);

    // co
    try {
        co = readFloatValue(urlUniversalSensor + "/log/string/co/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxCo->setValue(co);

    // illumination
    try {
        illumination = readFloatValue(urlUniversalSensor + "/log/string/illumination/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxIllumination->setValue(illumination);

    // sound
    try {
        sound = readFloatValue(urlUniversalSensor + "/log/string/sound/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxSound->setValue(sound);

    // flame
    try {
        flame = readFloatValue(urlUniversalSensor + "/log/string/flame/value");
    } catch (const QString &message) {
        qInfo() << "ERROR:" << message;
    }
    spinBoxFlame->setValue(flame);

    // Image
    QByteArray payload;
    try {
        payload = MainWindow::readValue(urlUniversalSensor + "/sys/file/1/value");
    } catch (const QString &message) {
        qInfo() << "Error retrieving file #1:" << message;
    }

    if (payload.size()) {
        payload = QByteArray::fromBase64(payload); // base64 encoding

        QTemporaryFile file(QDir::temp().absoluteFilePath("retrieved-image-XXXXXX"));
        if (file.open()) {
            QString temporaryFilename = file.fileName();

            file.write(payload);
            file.close();

            cv::Mat image = cv::imread(temporaryFilename.toStdString());

            imageDisplayWidget->setImage(image);
        } else {
            qWarning() << "Failed to save image to temporary file!";
        }
    }
}
