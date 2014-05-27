//
//  CustomSegment.h
//  GameCenter91
//
//  Created by  hiyo on 12-8-22.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>


@protocol CustomSegmentProtocol;
@interface CustomSegment : UIView {
	NSArray *items;
	int selectedSegmentIndex;
	id<CustomSegmentProtocol> customDelegate;
}
@property (nonatomic, retain) NSArray *items;
@property (nonatomic, assign) int selectedSegmentIndex;
@property (nonatomic, assign) id<CustomSegmentProtocol> customDelegate;

- (id)initWithItems:(NSArray *)arr delegate:(id)del;
- (void)setBadgeNum:(int)num atIndex:(int)index bAutoHideWhenZero:(BOOL)bHide;

@end

@protocol CustomSegmentProtocol

- (void)segmentIndexChangedFromOld:(int)oldIndex ToNew:(int)newIndex;

@end

