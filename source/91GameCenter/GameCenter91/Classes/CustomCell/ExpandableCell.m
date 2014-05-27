//
//  ExpandableCell.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-5.
//  Copyright 2012 net dragon. All rights reserved.
//

#import "ExpandableCell.h"
#import <QuartzCore/QuartzCore.h>

@implementation ExpandableCell

@synthesize expandView;
@synthesize expandLeftButton;
@synthesize expandRightButton;
@synthesize appIdentifier;

@synthesize expandLeftButtonTarget, expandLeftButtonAction, expandRightButtonAction, expandRightButtonTarget;

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self reset];
    [self setExpandLeftButtonAction:nil action:nil];
    [self setExpandRightButtonAction:nil action:nil];
    
    [self.expandLeftButton addTarget:self action:@selector(expandLeftButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    [self.expandRightButton addTarget:self action:@selector(expandRightButtonPressed:) forControlEvents:UIControlEventTouchUpInside];

}

-(void)reset
{
    self.expandView.hidden = YES;
    self.clipsToBounds = YES;
    self.appIdentifier = nil;
    self.gameName = nil;
}

- (void)dealloc {
    self.appIdentifier = nil;
    self.gameName = nil;
    [super dealloc];
}

#pragma mark -
- (void)setExpand:(BOOL)expand showRoundCorner:(BOOL)showRoundCorner
{
    self.expandView.hidden = !expand;
    self.expandView.layer.cornerRadius = showRoundCorner ? 10 : 0;
}

- (void)setExpandLeftButtonAction:(id)target action:(SEL)action {
    self.expandLeftButtonTarget = target;
    self.expandLeftButtonAction = action;
}

- (void)setExpandRightButtonAction:(id)target action:(SEL)action {
    self.expandRightButtonTarget = target;
    self.expandRightButtonAction = action;
}

- (void)expandLeftButtonPressed:(UIButton *)button
{
    if (self.expandLeftButtonTarget && self.expandLeftButtonAction)
        [self.expandLeftButtonTarget performSelector:self.expandLeftButtonAction withObject:button];
}

- (void)expandRightButtonPressed:(UIButton *)button
{
    NSLog(@"expand right button pressed");
    if (self.expandRightButtonTarget && self.expandRightButtonAction)
        [self.expandRightButtonTarget performSelector:self.expandRightButtonAction withObject:button];
}

#pragma mark -
+ (CGFloat)normalCellHeight {
    return 70;
}

+ (CGFloat)expandedCellHeight {
    return 110;
}

@end
