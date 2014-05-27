//
//  appInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/24/13.
//
//

#import <Foundation/Foundation.h>

@interface AppInfo : NSObject

@property (nonatomic, assign) int f_id;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) NSString *appIconUrl;
@property (nonatomic, retain) NSString *appName;
@property (nonatomic, assign) int gameId;
@property (nonatomic, retain) NSString *labelIcons;

@property (nonatomic, assign) BOOL bNewGame;    //是否显示新游戏标签

+ (AppInfo *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)listFromDictionary:(NSDictionary *)dict;

+ (NSArray *)serializedFromArr:(NSArray *)arr;

@end
