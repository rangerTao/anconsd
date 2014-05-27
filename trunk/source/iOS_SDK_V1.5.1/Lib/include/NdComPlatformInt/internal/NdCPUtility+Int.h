//
//  NdCPUtility+Int.h
//  NdComPlatformInt
//
//  Created by xujianye on 12-8-28.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


int isValidUserName(NSString *userName);
int isAnEmailUserName(NSString *userName);
int isValidPassword(NSString *password);
int	isValidNickName(NSString *nickName);
BOOL isValidEmailUser(NSString *userName);
BOOL isValidPhoneNum(NSString *phone);


void postLoginNotification(BOOL success, int error, BOOL isGuestAccount);
//isGuestAccount = NO
void sendLoginNotification(BOOL success, int error);
//isGuestAccount = YES
void sendGuestLoginNotification(BOOL success, int error);


NSString* stringCovertToPinyinInitial(NSString* strSrc);

#pragma mark Error Code
NSError *errorWithCodeAndDesc(int errorCode, NSString *errorDesc);
