#include "image_display_widget.h"
#include <opencv2/imgproc.hpp>


ImageDisplayWidget::ImageDisplayWidget (const QString &text, QWidget *parent)
    : QFrame(parent), text(text)
{
    setFrameStyle(QFrame::Box | QFrame::Sunken);
    setLineWidth(2);
}

ImageDisplayWidget::~ImageDisplayWidget ()
{
}


void ImageDisplayWidget::setImage (const cv::Mat &image)
{
    // Make a copy, and mark for the update
    if (image.channels() == 1) {
        cv::cvtColor(image, this->image, cv::COLOR_GRAY2RGB);
    } else {
        cv::cvtColor(image, this->image, cv::COLOR_BGR2RGB);
    }

    // Refresh
    update();
}

void ImageDisplayWidget::setText (const QString &text)
{
    // Store new text
    this->text = text;

    // Refresh
    update();
}


void ImageDisplayWidget::paintEvent (QPaintEvent *event)
{
    QPainter painter(this);
    painter.setRenderHint(QPainter::Antialiasing);

    QRect area(0, 0, width(), height());

    // Fill area
    painter.fillRect(area, QBrush(QColor(0, 0, 0, 32), Qt::DiagCrossPattern));

    if (image.empty()) {
        // Display text
        painter.drawText(area, Qt::AlignCenter, text);
    } else {
        // Display image
        int w = image.cols;
        int h = image.rows;

        double scale = qMin((double)width() / w, (double)height() / h);

        w *= scale;
        h *= scale;

        painter.translate((width() - w)/2, (height() - h)/2);

        painter.drawImage(QRect(0, 0, w, h), QImage(image.data, image.cols, image.rows, image.step, QImage::Format_RGB888));
    }

    // Draw frame on top of it all
    QFrame::paintEvent(event);
}
