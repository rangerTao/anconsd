//
//  ActivityCommonCtrl.h
//  GameCenter91
//
//  Created by  hiyo on 12-8-27.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GcPageTable.h"
#import "ActivityTableViewCell.h"
#import "RequestorAssistant.h"


@interface ActivityCommonCtrl : UIViewController<GcPageTableDelegate, GetActivityListProtocol, GetMyActivityGiftListProtocol>
@property (nonatomic, retain) GcPageTable   *act_table;
@property (nonatomic, assign) int           act_type;
@property (nonatomic, assign) BOOL          bHaveHeader;
@property (nonatomic, assign) BOOL          bNeedShowIcon;
@property (nonatomic, assign) int           tableStyle;
@property (nonatomic, retain) NSString      *identifier;

- (void)updateGameActivityBannerWithUrl:(NSString *)url;

@end

