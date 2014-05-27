//
//  ActivitySearchCtrl.h
//  GameCenter91
//
//  Created by hiyo on 12-12-12.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GcPageTable.h"
#import "RequestorAssistant.h"

typedef enum _SEARCH_TYPE {
	SEARCH_ALL_ACTIVITYS = -1,       //在所有活动中搜索
	SEARCH_GAME_GIFTS = 1,			//在游戏礼包中搜索	
	SEARCH_ACTIVITY_NOTICE = 2,     //在活动公告中搜索
	SEARCH_NEW_SERVERS_NOTICE = 4,  //在开服预告中搜索
}SEARCH_TYPE;

@interface ActivitySearchCtrl : UIViewController<GcPageTableDelegate, GetActivityListProtocol, UITextFieldDelegate, UITableViewDataSource, UITableViewDelegate>
{
    GcPageTable *table_result;
    SEARCH_TYPE act_type;

    UIView *chooseView;
    UILabel *chooseLabel;
    UITextField *keywordTextField;
}
@property(nonatomic, retain) GcPageTable *table_result;
@property(nonatomic, assign) SEARCH_TYPE act_type;

@property(nonatomic, readonly) IBOutlet UIView *chooseView;
@property(nonatomic, readonly) IBOutlet UILabel *chooseLabel;
@property(nonatomic, readonly) IBOutlet UITextField *keywordTextField;

- (IBAction)doChoose:(id)sender;
- (IBAction)doSearch:(id)sender;
- (IBAction)doDelete:(id)sender;

@end
