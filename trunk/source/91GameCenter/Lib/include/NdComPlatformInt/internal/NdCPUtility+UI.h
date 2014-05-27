//
//  NdCPUtility+UI.h
//  NdComPlatformInt
//
//  Created by xujianye on 12-8-27.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NDLOGGER.h"

#ifdef NO_LOG
#define NDCAST(classType,x) ([(x) isKindOfClass:[classType class]] ? (classType *)(x) : (nil))
#else
#define NDCAST(classType,x) ([(x) isKindOfClass:[classType class]] ? (classType *)(x) : (NDLOG(@"CAST FAIL") ,nil))
#endif


void transformContentView(UIView *view, UIInterfaceOrientation orientation, CGRect rect);
void transformContentViewWithOption(UIView *view, UIInterfaceOrientation orientation, CGRect rect, BOOL onTextWindow);

void transformNavCtr(UINavigationController *navCtr, UIInterfaceOrientation orientation);
void transformNavCtrView(UINavigationController *navCtr, UIInterfaceOrientation orientation, CGRect rect);

CGRect getWindowFrame();
CGRect  inflateRect(CGRect rcSrc, CGFloat left, CGFloat top, CGFloat right, CGFloat bottom);
CGRect  inflateRectWidthScale(CGRect rcSrc, CGFloat fScale);
CGRect  inflateRectWidthMax(CGRect rcSrc, CGFloat maxBorder);

void NdMessageBox(NSString * title, NSString * message, NSString * buttonName);

CGFloat getTextHeightWhenExpand(UILabel* label, CGFloat labelWidth);
CGFloat getLabelSingleLineHeight(UILabel* label);
CGSize  getTextSizeSingleLine(UILabel* label);
CGSize  getProperSize(UILabel* label, CGFloat limitWidth);
void    setOneLineMode(UILabel* label);
void    setMultiLineMode(UILabel* label);


UIImage *getImageFromBase64(NSString *base64);
NSString *getPhotoDataAfterBase64(UIImage *image);
NSString *getPhotoDataAfterBase64_PNG(UIImage *image);
UIImage* stretchImageToSize(UIImage* imgSrc, int newWidth, int newHeight);

UIImage* convertImageToGrayscale(UIImage* img);

UIImage *NdGetScreenShot();
UIImage *NdGetViewCapture(UIView *view, CGRect captureRect);

BOOL setCustomLocalNotification(int interval, NSString* alertBody);
void cancelAllCustomLocalNotification();


