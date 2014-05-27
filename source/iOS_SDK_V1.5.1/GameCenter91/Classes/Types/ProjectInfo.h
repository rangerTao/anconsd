//
//  ProjectInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import <Foundation/Foundation.h>

@interface ProjectInfo : NSObject

@property (nonatomic, assign) int position;
@property (nonatomic, retain) NSString *imageUrl;
@property (nonatomic, retain) NSString *mainTitle;
@property (nonatomic, retain) NSString *subTitle;
@property (nonatomic, assign) int projectType;
@property (nonatomic, assign) int targetType;
@property (nonatomic, retain) NSString *targetAction;
@property (nonatomic, retain) NSString *labelList;
@property (nonatomic, retain) NSString *targetActionUrl;
@property (nonatomic, retain) NSString *bgColor;

+ (ProjectInfo *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)listFromDictionary:(NSDictionary *)dict;

@end
