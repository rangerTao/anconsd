//
//  GetAppIdentifierOperation.m
//  GameCenter91
//
//  Created by  hiyo on 13-11-6.
//
//

#import "GetAppIdentifierOperation.h"
#import "OptionProtocols.h"

@implementation GetAppIdentifierOperation
@synthesize completionHandler;

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 110;
        self.protocolMethod = @selector(operation:getAppIdentifierDidFinish:identifier:strategy:forum:savedArr:);
        self.beWithSessionId = NO;
        self.completionHandler = nil;
    }
    return self;
}

- (int)operation
{
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                            [NSNumber numberWithInt:self.appid], @"AppId",
                            [NSNumber numberWithInt:1], @"Platform",
                          nil];
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    self.identifier = [paramDict objectForKey:@"Identifier"];
    self.strategyUrl = [paramDict objectForKey:@"StrategyUrl"];
    self.forumUrl = [paramDict objectForKey:@"ForumUrl"];
    
    if (self.completionHandler != nil) {
        completionHandler(self.identifier, self.strategyUrl, self.forumUrl);
    }
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM_4(self.identifier, self.strategyUrl, self.forumUrl, self.savedArr), nil];
}

@end
