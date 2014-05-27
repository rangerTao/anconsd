/*
 *  NdResourceUtility.h
 *  NdComPlatform
 *
 *  Created by Sie Kensou on 10-8-11.
 *  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
 *
 */
#import <UIKit/UIKit.h>

//get resource full path, according to the input
//e.g if resource name is image, ext is png, the bundle path is XXXXX/Res/
//if differByDevice, resource path will be XXXXX/Res/image_iphone.png or XXXXX/Res/image_ipad.png or so
//if differByOrientation, resource path will be XXXXX/Res/image_portrait.png or XXXXX/Res/image_landscape.png
//if both differ, then will be something like XXXXX/Res/image_ipad_landscape.png
NSString *getResourcePath(NSString *resourceName, NSString *resourceExt, BOOL differByDevice, BOOL differByOrientation);

NSString *getUniqueImageResourcePath(NSString *resourceName);
NSString *getUniquePlistResourcePath(NSString *plistName);

void	setUIOrientationForUniqueResource(UIInterfaceOrientation  ori);
UIInterfaceOrientation  getUIOrientationForUniqueResource();

//get the localizable string from localizable strings table
void loadLocalizedString();
NSString *NDSTR(NSString *input);
NSString *revealNextLineChar(NSString* strSrc);

UIImage* allocMyImage(NSString* fileName, BOOL bStretchwidthCenter);

NSDictionary* loadImgFromDicFile(NSDictionary* dicImgFile, BOOL bStretchImg);
NSDictionary* loadFontTypeFromDicFile(NSDictionary* dicFontCfg);

BOOL	isCurrentLandscape();
BOOL	isCurrentIpadProject();

