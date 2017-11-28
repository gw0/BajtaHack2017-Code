#ifndef IMAGE_DISPLAY_WIDGET_H
#define IMAGE_DISPLAY_WIDGET_H

#include <QtWidgets>
#include <opencv2/core.hpp>


class ImageDisplayWidget : public QFrame
{
    Q_OBJECT

public:
    ImageDisplayWidget (const QString &text = QString(), QWidget *parent = Q_NULLPTR);
    virtual ~ImageDisplayWidget ();

    virtual void setImage (const cv::Mat &image);
    void setText (const QString &text);

protected:
    virtual void paintEvent (QPaintEvent *event) override;

protected:
    QString text;
    cv::Mat image;
};


#endif
