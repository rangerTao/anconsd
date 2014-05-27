//
//  GameSearchBar.m
//  GameCenter91
//
//  Created by  hiyo on 12-9-7.
//  Copyright 2012 Nd. All rights reserved.
//

#import "GameSearchBar.h"
#import <QuartzCore/CALayer.h>
#import "UIView+Addition.h"
#import "UITableViewCell+Addition.h"
#import "GameSearchResultCell.h"
#import "RequestorAssistant.h"

#import "NdPinyinTable.h"
#import "DatabaseUtility.h"
#import "MBProgressHUD.h"
#import "OptionProtocols.h"
#import "SuggestionSoftItem.h"
#import "CommUtility.h"

#define MARGIN_LEFT_RIGHT		5.0f
#define MARGIN_TOP_BUTTOM		5.0f
#define HEIGHT_RESULT			28.0f
#define RGB(r,g,b)          [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1.0]


@interface GameSearchBar()<GetSoftSuggestionProtocol>
@property (nonatomic, assign) NSInteger resultCount;
@property (nonatomic, retain) UITableView *m_tableView;
@property (nonatomic, retain) NSMutableArray *results;
- (void)initSearchBar;
- (void)textChanged;
- (NSString *)stringCovertToPinyinInitial:(NSString* )strSrc;
@end


@implementation GameSearchBar
@synthesize searchDelgate;
@synthesize m_textField;
@synthesize reset_btn;
@synthesize search_btn;
@synthesize m_tableView;
@synthesize results;
@synthesize resultCount;

+ (GameSearchBar *)searchBar
{
    GameSearchBar *bar = [GameSearchBar loadFromNIB];
    bar.backgroundColor = RGB(0xd9, 0xd8, 0xdc);
    [bar initSearchBar];
	return bar;
}

- (void)hideKeyboard
{
	[self.m_textField resignFirstResponder];
}

- (UIScrollView *)m_tableView
{
    if (m_tableView == nil) {
        CGFloat height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:NO otherExcludeHeight:CGRectGetHeight(self.frame)];

        self.m_tableView = [[[UITableView alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(self.frame), 320, height) style:UITableViewStylePlain] autorelease];
        m_tableView.backgroundColor = [UIColor whiteColor];
        m_tableView.delegate = self;
        m_tableView.dataSource = self;
        m_tableView.hidden = YES;
        [self.superview addSubview:m_tableView];
    }
    return m_tableView;
}

- (IBAction)search:(id)sender
{
    if ([self.m_textField.text length] == 0) {
        [MBProgressHUD showHintHUD:@"搜索内容不能为空" message:@"请输入游戏名或拼音首字母" hideAfter:DEFAULT_TIP_LAST_TIME];
        return;
    }
    
	[self hideKeyboard];
	self.m_tableView.hidden = YES;
    
    if ([self.searchDelgate respondsToSelector:@selector(doSearchWithKeyword:)]) {
        [self.searchDelgate doSearchWithKeyword:self.m_textField.text];
    }

}

- (IBAction)reset:(id)sender
{
	self.m_textField.text = @"";
    self.m_tableView.hidden = YES;
    [results removeAllObjects];
    self.reset_btn.hidden = YES;
    if ([self.searchDelgate respondsToSelector:@selector(doDelete)]) {
        [self.searchDelgate doDelete];
    }
}

- (void)dealloc
{
    self.searchDelgate = nil;
    self.m_tableView = nil;
    self.results = nil;
    [super dealloc];
}

#pragma mark -
#pragma mark inner Method
- (void)initSearchBar
{
    self.searchDelgate = nil;
    self.reset_btn.hidden = YES;
	resultCount = 0;
    self.results = [NSMutableArray arrayWithCapacity:1];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textChanged) 
										name:UITextFieldTextDidChangeNotification object:nil];
}


- (void)textChanged
{
    [results removeAllObjects];
    [self.m_tableView reloadData];
    if ([m_textField.text length] <= 0) {
        self.m_tableView.hidden = YES;
        return;
    }
    self.reset_btn.hidden = NO;

//    [self oldSuggestionMethod];
//    [self.m_tableView reloadData];
//    self.m_tableView.hidden = NO;
    NSNumber *ret = [RequestorAssistant requestSoftSuggestionList:self.m_textField.text delegate:self];
    
    if (ret >= 0) {
        self.m_tableView.hidden = NO;
    }
}

#pragma mark delegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    self.m_tableView.hidden = YES;
	[self hideKeyboard];
	return YES;
}
- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    if ([textField.text length] > 0) {
        self.reset_btn.hidden = NO;
    }

}
- (void)textFieldDidEndEditing:(UITextField *)textField
{
    if ([textField.text length] <= 0) {
        self.reset_btn.hidden = YES;
    }
}
#pragma mark UITableView Delegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [results count];
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellId = @"suggestionCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellId];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellId] autorelease];
        cell.textLabel.font = [UIFont systemFontOfSize:18.0];
    }
    SuggestionSoftItem *item = (SuggestionSoftItem *)[self.results objectAtIndex:indexPath.row];
    cell.textLabel.text = item.softName;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    SuggestionSoftItem *item = (SuggestionSoftItem *)[self.results objectAtIndex:indexPath.row];
    self.m_textField.text = item.softName;
    [self search:nil];
}

#pragma mark - search pinyin method
- (NSString *)stringCovertToPinyinInitial:(NSString* )strSrc
{
#define BuffSize_unicode	8
#define	BuffSize_pinyin		8
	static char s_pinyinBuff[BuffSize_pinyin] = {0};
	static char s_unicodeBuff[BuffSize_unicode] = {0};
	
	if ([strSrc length] <= 0) {
		return strSrc;
	}
	
	NSMutableString* strResult = [NSMutableString stringWithCapacity:[strSrc length]];
	NSUInteger strLen = [strSrc length];
	NSRange range = {0,1};
	for (NSUInteger i = 0;  i < strLen; i++) {
		range.location = i;
		NSString *strTmp =[strSrc substringWithRange:range];
		memset(s_pinyinBuff, 0, BuffSize_pinyin);
		memset(s_unicodeBuff, 0, BuffSize_unicode);
		CFStringGetCString((CFStringRef)strTmp, s_unicodeBuff, BuffSize_unicode, kCFStringEncodingDOSChineseSimplif);
		convertToWordPy(s_unicodeBuff, s_pinyinBuff);
		if (s_pinyinBuff[0]) {
			[strResult appendFormat:@"%c", s_pinyinBuff[0]];
		}
		else {
			[strResult appendString:strTmp];
		}
	}
	return strResult;
}
#pragma mark - GetSoftSuggestionProtocol
- (void)operation:(CommonOperation *)operation getSoftSuggestionDidFinish:(NSError *)error suggestionList:(NSArray *)suggestionList
{
    self.results = [NSMutableArray arrayWithArray:suggestionList];
    [self.m_tableView reloadData];
}
@end
