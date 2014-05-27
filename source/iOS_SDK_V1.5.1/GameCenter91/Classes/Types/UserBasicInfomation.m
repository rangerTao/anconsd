//
//  UserBisicInfomation.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "UserBasicInfomation.h"

@implementation UserBasicInfomation

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (UserBasicInfomation *)itemFromDictionary:(NSDictionary *)dict
{
    UserBasicInfomation *info = [[UserBasicInfomation new] autorelease];
    info.uin = [[dict objectForKey:@"Uin"] intValue];
    info.userName = [dict objectForKey:@"UserName"];
    info.nickName = [dict objectForKey:@"NickName"];
    info.checkSum = [dict objectForKey:@"CheckSum"];
    return info;
}

@end
