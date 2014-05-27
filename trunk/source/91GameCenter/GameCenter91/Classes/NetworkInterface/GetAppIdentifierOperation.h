//
//  GetAppIdentifierOperation.h
//  GameCenter91
//
//  Created by  hiyo on 13-11-6.
//
//

#import "GameCenterOperation.h"

typedef void (^IdentifierCallback)(NSString *identifier, NSString *strategyUrl, NSString *forumUrl);

@interface GetAppIdentifierOperation : GameCenterOperation

@property (nonatomic, assign) int appid;
@property (nonatomic, retain) NSArray *savedArr; //保存回调的参数(类型,参数...)

@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) NSString *strategyUrl;
@property (nonatomic, retain) NSString *forumUrl;

@property (nonatomic, copy) IdentifierCallback completionHandler;

@end
