//
//  UserBisicInfomation.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import <Foundation/Foundation.h>

@interface UserBasicInfomation : NSObject

@property (nonatomic, assign) int uin;
@property (nonatomic, retain) NSString *userName;
@property (nonatomic, retain) NSString *nickName;
@property (nonatomic, retain) NSString *checkSum;

+ (UserBasicInfomation *)itemFromDictionary:(NSDictionary *)dict;

@end
