/*
 *  NdCPUtility+Base.h
 *  NdComPlatform_SNS
 *
 *  Created by Sie Kensou on 10-9-14.
 *  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
 *
 */

#import <Foundation/Foundation.h>

void dictSetObject(NSMutableDictionary *dict, id object, id key);
id dictGetObject(NSDictionary *dict, id key);


NSString*  encodeBase64(NSData* data);
NSString*  encodeBase64WithString(NSString* input);
NSData*	   decodeBase64(NSString* str);


NSString *getReadableStringForSize(unsigned long size);
NSString *getFormatedFloatString(float value);
//for handling money Mutiply
NSString *getStringFromDecimalNumberMutiplyWithString(NSString *multiplierValue, NSString *multiplicandValue);


#pragma mark file
BOOL createDirectoryAtPathIfNotExist(NSString *dirPath);
BOOL createPlistAtPathIfNotExist(NSString *filePath);
BOOL createFileAtPathIfNotExist(NSString *filePath, BOOL isPlist);

NSDate*  NdCPGetLastAccessTimeOfFile(NSString* file);
void  NdCPClearDirtyFileWhenNoMoreAccess(NSString* path, NSTimeInterval timeInterval);


#pragma mark  date 
int	 dateCompareWithNow(int nBornYear, int nBornMonth, int nBornDay);
int  dateCompareWithNow_str(NSString* strDate);
NSString* dateFormatToStr(int nBornYear, int nBornMonth, int nBornDay);
NSString* dateTodayStr();
void dateTodayInt(int* pnYear, int* pnMonth, int* pnDay);

NSDate* dateWithToday() ;
NSLocale* NdCurrentLocale();
NSString* NdFormatRelativeTime(NSDate* date) ;

NSString *stringFormDate(NSDate* date);
NSDate*	  dateFormString(NSString* string);

int daysToToday(NSDate* date);
int daysToCurrentDate(NSDate* date);
