//
//  GcEGORefreshTableHeaderView.m
//  Demo
//
//  Created by Devin Doty on 10/14/09October14.
//  Copyright 2009 enormego. All rights reserved.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//

#import "GcEGORefreshTableHeaderView.h"


//#define TEXT_COLOR	 [UIColor colorWithRed:87.0/255.0 green:108.0/255.0 blue:137.0/255.0 alpha:1.0]
#define TEXT_COLOR			[UIColor darkGrayColor]
#define	TEXT_SHADOW_COLOR	[UIColor colorWithWhite:0.5f alpha:1.0f]

#define FLIP_ANIMATION_DURATION 0.18f


@interface GcEGORefreshTableHeaderView ()
- (void)setState:(GcEGOPullRefreshState)aState;
@property (nonatomic, retain) NSDate*		dateLastUpdate;
@end

@implementation GcEGORefreshTableHeaderView

@synthesize delegate=_delegate;
@synthesize dateLastUpdate;

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
		
		self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
		self.backgroundColor = [UIColor clearColor];

		UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0.0f, frame.size.height - 30.0f, self.frame.size.width, 30.0f)];
		label.autoresizingMask = UIViewAutoresizingFlexibleWidth;
		label.font = [UIFont systemFontOfSize:12.0f];
		label.textColor = TEXT_COLOR;
		//label.shadowColor = TEXT_SHADOW_COLOR;
		//label.shadowOffset = CGSizeMake(0.0f, 1.0f);
		label.backgroundColor = [UIColor clearColor];
		label.textAlignment = UITextAlignmentCenter;
		[self addSubview:label];
		_lastUpdatedLabel=label;
		[label release];
		
		label = [[UILabel alloc] initWithFrame:CGRectMake(0.0f, frame.size.height - 48.0f, self.frame.size.width, 30.0f)];
		label.autoresizingMask = UIViewAutoresizingFlexibleWidth;
		label.font = [UIFont boldSystemFontOfSize:13.0f];
		label.textColor = TEXT_COLOR;
		//label.shadowColor = TEXT_SHADOW_COLOR;
		//label.shadowOffset = CGSizeMake(0.0f, 1.0f);
		label.backgroundColor = [UIColor clearColor];
		label.textAlignment = UITextAlignmentCenter;
		[self addSubview:label];
		_statusLabel=label;
		[label release];
		
		CALayer *layer = [CALayer layer];
		layer.frame = CGRectMake(25.0f, frame.size.height - 65.0f, 30.0f, 55.0f);
		layer.contentsGravity = kCAGravityResizeAspect;
		NSString* strImgFile = [[NSBundle mainBundle] pathForResource:@"blackArrow" ofType:@"png"];
		UIImage* img = [UIImage imageWithContentsOfFile:strImgFile];
		layer.contents = (id)img.CGImage;
		
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 40000
		if ([[UIScreen mainScreen] respondsToSelector:@selector(scale)]) {
			layer.contentsScale = [[UIScreen mainScreen] scale];
		}
#endif
		
		[[self layer] addSublayer:layer];
		_arrowImage=layer;
		
		UIActivityIndicatorView *view = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
		view.frame = CGRectMake(25.0f, frame.size.height - 38.0f, 20.0f, 20.0f);
		[self addSubview:view];
		_activityView = view;
		[view release];
		
		
		[self setState:NdEGOOPullRefreshNormal];
		
    }
	
    return self;
	
}


#pragma mark -
#pragma mark Setters

#define TT_MINUTE 60
#define TT_HOUR   (60 * TT_MINUTE)
#define TT_DAY    (24 * TT_HOUR)
#define TT_5_DAYS (5 * TT_DAY)
#define TT_WEEK   (7 * TT_DAY)
#define TT_MONTH  (30.5 * TT_DAY)
#define TT_YEAR   (365 * TT_DAY)

- (NSLocale *)currentLocale
{
	NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
	NSArray* languages = [defaults objectForKey:@"AppleLanguages"];
	if (languages.count > 0) {
		NSString* currentLanguage = [languages objectAtIndex:0];
		return [[[NSLocale alloc] initWithLocaleIdentifier:currentLanguage] autorelease];
	} else {
		return [NSLocale currentLocale];
	}
}

- (NSString *)formatRelativeTime:(NSDate *)date {
	NSTimeInterval elapsed = abs([date timeIntervalSinceNow]);	
	if(elapsed < TT_MINUTE) {
		return [NSString stringWithFormat:@"刚刚"];
	}else if(elapsed < TT_HOUR) {
		int mins = (int)(elapsed/TT_MINUTE);
		return [NSString stringWithFormat:@"%d分钟前", mins];
	}else if (elapsed < TT_DAY) {
		int hours = (int)((elapsed+TT_HOUR/2)/TT_HOUR);
		return [NSString stringWithFormat:@"%d小时前", hours];
	}
	else if (elapsed < TT_MONTH) {
		int days = (int)((elapsed+TT_DAY/2)/TT_DAY);
		return [NSString stringWithFormat:@"%d天前",days];
	}
	else {
		static NSDateFormatter* formatter = nil;
		if (!formatter) {
			formatter = [[NSDateFormatter alloc] init];
			formatter.dateFormat = @"yyyy-MM-dd";
			formatter.locale = [self currentLocale];
		}
		return [formatter stringFromDate:date];
	}
}

- (void)updateLabelTip
{
	NSString* str = @"";
	if (dateLastUpdate) {
		str = [self formatRelativeTime:dateLastUpdate];
	}
	_lastUpdatedLabel.text = ([str length] > 0) ? [NSString stringWithFormat:@"最后更新:  %@", str] : nil;	
}

- (void)refreshLastUpdatedDate {
	
	if ([_delegate respondsToSelector:@selector(egoRefreshTableHeaderDataSourceLastUpdated:)]) {
		self.dateLastUpdate = [_delegate egoRefreshTableHeaderDataSourceLastUpdated:self];
	} 
	
	[self updateLabelTip];	
}

- (void)setState:(GcEGOPullRefreshState)aState{
	
	switch (aState) {
		case NdEGOOPullRefreshPulling:
			
			_statusLabel.text = @"松开即可刷新...";///NSLocalizedString(@"Release to refresh...", @"Release to refresh status");
			[CATransaction begin];
			[CATransaction setAnimationDuration:FLIP_ANIMATION_DURATION];
			_arrowImage.transform = CATransform3DMakeRotation((M_PI / 180.0) * 180.0f, 0.0f, 0.0f, 1.0f);
			[CATransaction commit];
			
			break;
		case NdEGOOPullRefreshNormal:
			
			if (_state == NdEGOOPullRefreshPulling) {
				[CATransaction begin];
				[CATransaction setAnimationDuration:FLIP_ANIMATION_DURATION];
				_arrowImage.transform = CATransform3DIdentity;
				[CATransaction commit];
			}
			
			_statusLabel.text = @"下拉可以刷新...";//NSLocalizedString(@"Pull down to refresh...", @"Pull down to refresh status");
			[_activityView stopAnimating];
			[CATransaction begin];
			[CATransaction setValue:(id)kCFBooleanTrue forKey:kCATransactionDisableActions]; 
			_arrowImage.hidden = NO;
			_arrowImage.transform = CATransform3DIdentity;
			[CATransaction commit];
			
			//[self refreshLastUpdatedDate];
			
			break;
		case NdEGOOPullRefreshLoading:
			
			_statusLabel.text = @"加载中⋯";//NSLocalizedString(@"Loading...", @"Loading Status");
			[_activityView startAnimating];
			[CATransaction begin];
			[CATransaction setValue:(id)kCFBooleanTrue forKey:kCATransactionDisableActions]; 
			_arrowImage.hidden = YES;
			[CATransaction commit];
			
			break;
		default:
			break;
	}
	
	_state = aState;
}


#pragma mark -
#pragma mark ScrollView Methods

- (void)egoRefreshScrollViewDidScroll:(UIScrollView *)scrollView {	
	
	if (_state == NdEGOOPullRefreshLoading) {
		
		CGFloat offset = MAX(scrollView.contentOffset.y * -1, 0);
		offset = MIN(offset, 60);
		scrollView.contentInset = UIEdgeInsetsMake(offset, 0.0f, 0.0f, 0.0f);
		
	} else if (scrollView.isDragging) {
		
		BOOL _loading = NO;
		if ([_delegate respondsToSelector:@selector(egoRefreshTableHeaderDataSourceIsLoading:)]) {
			_loading = [_delegate egoRefreshTableHeaderDataSourceIsLoading:self];
		}
		
		if (_state == NdEGOOPullRefreshPulling && scrollView.contentOffset.y > -65.0f && scrollView.contentOffset.y < 0.0f && !_loading) {
			[self setState:NdEGOOPullRefreshNormal];
		} else if (_state == NdEGOOPullRefreshNormal && scrollView.contentOffset.y < -65.0f && !_loading) {
			[self setState:NdEGOOPullRefreshPulling];
		}
		
		if (scrollView.contentInset.top != 0) {
			scrollView.contentInset = UIEdgeInsetsZero;
		}
	}
	
	if (scrollView.contentOffset.y < 0.0f) {
		if (!bHasUpdateLabelTip) {
			bHasUpdateLabelTip = YES;
			[self updateLabelTip];
		}
	}
	else {
		bHasUpdateLabelTip = NO;
	}
}

- (void)egoRefreshScrollViewDidEndDragging:(UIScrollView *)scrollView {
	
	BOOL _loading = NO;
	if ([_delegate respondsToSelector:@selector(egoRefreshTableHeaderDataSourceIsLoading:)]) {
		_loading = [_delegate egoRefreshTableHeaderDataSourceIsLoading:self];
	}
	
	if (scrollView.contentOffset.y <= - 65.0f && !_loading) {
		
		if ([_delegate respondsToSelector:@selector(egoRefreshTableHeaderDidTriggerRefresh:)]) {
			[_delegate egoRefreshTableHeaderDidTriggerRefresh:self];
		}
		
		[self setState:NdEGOOPullRefreshLoading];
		[UIView beginAnimations:nil context:NULL];
		[UIView setAnimationDuration:0.2];
		scrollView.contentInset = UIEdgeInsetsMake(60.0f, 0.0f, 0.0f, 0.0f);
		[UIView commitAnimations];
		
	}
	
}

- (void)egoRefreshScrollViewDataSourceDidFinishedLoading:(UIScrollView *)scrollView {	
	
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationDuration:.3];
	[scrollView setContentInset:UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f)];
	[UIView commitAnimations];
	
	[self setState:NdEGOOPullRefreshNormal];
	[self refreshLastUpdatedDate];

}


#pragma mark -
#pragma mark Dealloc

- (void)dealloc {
	
	_delegate=nil;
	_activityView = nil;
	_statusLabel = nil;
	_arrowImage = nil;
	_lastUpdatedLabel = nil;
	self.dateLastUpdate = nil;
    [super dealloc];
}


@end
