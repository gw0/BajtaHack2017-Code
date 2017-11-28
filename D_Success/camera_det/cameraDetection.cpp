#include <opencv2/opencv.hpp>
//#include "opencv2/imgcodecs.hpp"
//#include "opencv2/imgproc.hpp"
//#include "opencv2/videoio.hpp"
#include <opencv2/highgui/highgui.hpp>
//#include <opencv2/video.hpp>
//#include <opencv2/cvconfig.h>

#include <iostream>
#include <sstream>
#include <unistd.h>
#include <stdlib.h>

#include <stdio.h>

using namespace std;
using namespace cv;

Mat frame;
Mat frame2;
Mat fg_mask;
Mat frm_keypoints;

BackgroundSubtractorMOG pMOG;
SimpleBlobDetector::Params params;

int main(int argc, char** argv)
{
	//background subtractor object
	//pMOG = BackgroundSubtractorMOG2();
	pMOG = BackgroundSubtractorMOG(4, 5, 0.7, 10);

	//video capture object
	VideoCapture cap = VideoCapture(0);

	if (!cap.isOpened()){
		cout << "error - couldnt open video" << endl;
		return -1;
	}

	//cap.set(CV_CAP_PROP_FRAME_WIDTH, 1280);
	//cap.set(CV_CAP_PROP_FRAME_HEIGHT, 720);
	cap.read(frame);
	//imwrite("abc.png", frame);
	//return 0;

	params.minThreshold = 10;
	params.maxThreshold = 200;
	params.filterByArea = true;
	params.minArea = 20;
	params.filterByInertia = true;
	params.minInertiaRatio = 0.01;
	params.filterByCircularity = false;
	params.filterByConvexity = false;


	SimpleBlobDetector detector(params);
//	SimpleBlobDetector detector;

	while (1){
		cap.retrieve(frame);
		bool successRead = cap.read(frame);
		if(!successRead){
			cout << "couldnt read image from camera" << endl;
			return -1;
		}
		
		//resizeimage
		resize(frame, frame, Size(frame.size().width/2, frame.size().height/2));
		//cvtColor(frame, frame, COLOR_BGR2RGB);
		cvtColor(frame, frame2, COLOR_RGB2GRAY); //convert to grayscale

		//img quality compress
		std::vector<int> quality_type;
		quality_type.push_back(CV_IMWRITE_JPEG_QUALITY);
		quality_type.push_back(45);

		//write image on disk
		imwrite("/home/pi/webcam.jpg", frame2, quality_type);
		cout << "image captured" << endl;
		
                int status = system("/home/pi/srm_upload_image.sh");
                cout << "srm_upload_image_status: " << status << endl;

		//perform foreground extraction
		pMOG.operator()(frame2, fg_mask);
		imwrite("../../foreground.jpg", fg_mask, quality_type);
		cout << "foreground extracted" << endl;

		//blobs centers
		vector<KeyPoint> blob_points;
		//vector<Point2f> blob_centers;
		//convert(blob_points, blob_centers);
		detector.detect(fg_mask, blob_points);
		
		FileStorage fs1("../../keypoints.yml", FileStorage::WRITE);
		write(fs1, "blob_keypoints", blob_points);
		fs1.release();
		//FileStorage fs2("../../centers.yml", FileStorage::WRITE);
		//write(fs2, "blob_centers", blob_centers);
		//fs2.release();
		cout << "blob centers saved" << endl;

		drawKeypoints(frame, blob_points, frm_keypoints, Scalar(0,0,255), DrawMatchesFlags::DRAW_RICH_KEYPOINTS);
		imwrite("../../foregroundkeys.jpg", frm_keypoints, quality_type);
		cout << "keypoints of blobs drawn" << endl;

		//wait few seconds
		usleep(3000000);
	}

	return 0;

}
