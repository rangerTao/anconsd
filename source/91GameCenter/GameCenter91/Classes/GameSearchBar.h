//
//  GameSearchBar.h
//  GameCenter91
//
//  Created by  hiyo on 12-9-7.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>


@protocol GameSearchBarProtocol;
@interface GameSearchBar : UIView <UITextFieldDelegate, UITableViewDataSource, UITableViewDelegate> {
    id<GameSearchBarProtocol>   searchDelgate;
	UITextField		*m_textField;
	UIButton		*reset_btn;
	UIButton		*search_btn;
}
@property(nonatomic, assign) id<GameSearchBarProtocol>   searchDelgate;
@property(nonatomic, readonly) IBOutlet UITextField		*m_textField;
@property(nonatomic, readonly) IBOutlet UIButton		*reset_btn;
@property(nonatomic, readonly) IBOutlet UIButton		*search_btn;

+ (GameSearchBar *)searchBar;
- (void)hideKeyboard;
- (IBAction)search:(id)sender;
- (IBAction)reset:(id)sender;

@end


@protocol GameSearchBarProtocol <NSObject>

- (void)doSearchWithResult:(NSArray *)arr;
- (void)doSearchWithKeyword:(NSString *)aKeyword;
- (void)doDelete;

@end
